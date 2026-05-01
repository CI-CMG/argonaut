package edu.colorado.cires.argonaut.core.merge.synthetic;

import edu.colorado.cires.argonaut.core.netcdf.metadata.v31.ArgoMetadataV31;
import edu.colorado.cires.argonaut.core.netcdf.metadata.v31.ArgoMetadataV31Reader;
import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoMultiProfileV31;
import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoProfileV31;
import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoProfileV31Level;
import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoProfileV31Parameter;
import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoProfileV31Reader;
import edu.colorado.cires.argonaut.core.netcdf.synthprofile.v13.ArgoSyntheticProfileV13Level;
import edu.colorado.cires.argonaut.core.netcdf.synthprofile.v13.ArgoSyntheticProfileV13Writer;
import edu.colorado.cires.argonaut.core.netcdf.synthprofile.v13.impl.ArgoSyntheticProfileV13Bean;
import edu.colorado.cires.argonaut.core.netcdf.synthprofile.v13.impl.ArgoSyntheticProfileV13LevelBean;
import edu.colorado.cires.argonaut.core.netcdf.synthprofile.v13.impl.ArgoSyntheticProfileV13ParameterBean;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;
import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.apache.commons.math3.exception.OutOfRangeException;
import ucar.ma2.InvalidRangeException;

public class SyntheticProfileMerger {

  private static final String version;

  static {
    Properties properties = new Properties();
    try (InputStream in = SyntheticProfileMerger.class.getClassLoader().getResourceAsStream("edu/colorado/cires/argonaut/core/build.properties")) {
      properties.load(in);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    version = properties.getProperty("version");
  }


  private final Path cProfilePath;
  private final Path bProfilePath;
  private final Path metaPath;
  private final Path outputPath;

  public SyntheticProfileMerger(Path cProfilePath, Path bProfilePath, Path metaPath, Path outputPath) {
    this.cProfilePath = cProfilePath;
    this.bProfilePath = bProfilePath;
    this.metaPath = metaPath;
    this.outputPath = outputPath;
  }

  private static class IndexInfo {

    private int index;
    private int qc;


  }

  public void mergeProfiles() throws IOException {
    try (
        ArgoMetadataV31Reader metaReader = new ArgoMetadataV31Reader(metaPath);
        ArgoProfileV31Reader cReader = new ArgoProfileV31Reader(cProfilePath);
        ArgoProfileV31Reader bReader = new ArgoProfileV31Reader(bProfilePath);
    ) {
      ArgoMultiProfileV31 multiCProfile = cReader.getMultiProfile();
      ArgoMultiProfileV31 multiBProfile = bReader.getMultiProfile();
      ArgoMetadataV31 metadata = metaReader.getMetadata();

      List<ArgoProfileV31> cProfiles = multiCProfile.getProfiles();
      List<ArgoProfileV31> bProfiles = multiBProfile.getProfiles();
      List<String> stationParameters = getStationParameters(cProfiles, bProfiles);
      List<Integer> profilePriority = getNProfPriority(metadata, cProfiles, bProfiles);

      Map<Integer, Set<Integer>> validIndexes = PressureIndexer.getQcValidatedPressureIndexes(cProfiles);

      Map<Long, SynthRow> bRows = populateBProfileRows(bProfiles, validIndexes);
      Map<Long, SynthRow> cRows = populateCProfileRows(cProfiles, validIndexes);
      List<Long> syntheticPressures = calculateSyntheticPressures(filterPopulatedRows(bRows));

      List<String> bParameters = stationParameters.stream().filter(BioParameterFilter::isSupportedParameter).toList();
      List<String> cParameters = stationParameters.stream().filter(CoreParameterFilter::isSupportedParameter).toList();

//      Map<Long, SynthRow> allRows = mergeRows(cRows, bRows);
      Map<Long, SynthRow> singleCRows = resolveSingleProfileParameters(cRows, profilePriority, cParameters);
      Map<Long, SynthRow> singleBRows = resolveSingleProfileParameters(bRows, profilePriority, bParameters);

      Set<Long> resultPressures = new TreeSet<>(singleCRows.keySet());
      resultPressures.addAll(syntheticPressures);

      Map<String, Map<Long, Float>> interpolatedValues = new LinkedHashMap<>();
      for (String parameterName : stationParameters) {
        if (cParameters.contains(parameterName)) {
          if (parameterName.equals("PRES")) {
            interpolatedValues.put("PRES", getPressureValues(singleCRows, resultPressures));
          } else {
            interpolatedValues.put(parameterName, interpolate(parameterName, singleCRows, resultPressures));
          }
        } else {
          Map<Long, Float> known = knownValues(parameterName, singleBRows, syntheticPressures);
          Map<Long, Float> interpolated = interpolate(parameterName, singleBRows, syntheticPressures);
          Map<Long, Float> nearest = keepNearestInterpolatedValues(parameterName, known, singleBRows, interpolated);
          interpolatedValues.put(parameterName, resolveSingleGapInterpolations(nearest, interpolated, resultPressures));
        }
      }

      writeSProfileFile(interpolatedValues, stationParameters);

    }

  }

  private static Map<Long, Float> keepNearestInterpolatedValues(String parameterName, Map<Long, Float> known, Map<Long, SynthRow> singleBRows,
      Map<Long, Float> interpolated) {
    if (known.size() < 2) {
      return known;
    }
    List<Long> pressures = new ArrayList<>(known.keySet());

    Set<Long> filtered = new HashSet<>();
    for (Long pressure : singleBRows.keySet()) {
      SynthRow row = singleBRows.get(pressure);
      for (SynthProfile profile : row.getProfiles()) {
        if (profile.getParameters().get(parameterName) != null) {
          filtered.add(pressure);
        }
      }
    }

    Map<Long, Float> result = new TreeMap<>(known);
    for (Long pressure : filtered) {
      if (!known.containsKey(pressure)) {
        for (int i = 0; i < pressures.size(); i++) {
          long pressureAfter = pressures.get(i);
          if (pressureAfter > pressure) {
            if (i == 0) {
              if (result.get(pressureAfter) == null) {
                result.put(pressureAfter, interpolated.get(pressureAfter));
              }
            } else {
              long pressureBefore = pressures.get(i - 1);
              long diffBefore = pressure - pressureBefore;
              long diffAfter = pressureAfter - pressure;
              int compare = Long.compare(diffBefore, diffAfter);
              if (compare == 0) {
                if (result.get(pressureBefore) == null) {
                  result.put(pressureBefore, interpolated.get(pressureBefore));
                }
                if (result.get(pressureAfter) == null) {
                  result.put(pressureAfter, interpolated.get(pressureAfter));
                }
              } else if (compare < 0) {
                if (result.get(pressureBefore) == null) {
                  result.put(pressureBefore, interpolated.get(pressureBefore));
                }
              } else {
                if (result.get(pressureAfter) == null) {
                  result.put(pressureAfter, interpolated.get(pressureAfter));
                }
              }
            }
            break;
          }

        }
      }
    }
    return result;
  }

  private static Map<Long, Float> resolveSingleGapInterpolations(Map<Long, Float> known, Map<Long, Float> interpolated,
      Collection<Long> resultPressures) {

    Map<Long, Float> result = new TreeMap<>(interpolated);

    List<Long> pressures = new ArrayList<>(known.keySet());

    if (pressures.size() < 3) {
      result = known;
    } else {
      if (known.get(pressures.get(0)) == null && (known.get(pressures.get(1)) == null || known.get(pressures.get(2)) == null)) {
        result.put(pressures.get(0), null);
      }

      if (known.get(pressures.get(pressures.size() - 1)) == null && (known.get(pressures.get(pressures.size() - 2)) == null
          || known.get(pressures.get(pressures.size() - 3)) == null)) {
        result.put(pressures.get(pressures.size() - 1), null);
      }

      if (pressures.size() >= 4) {

        if (known.get(pressures.get(1)) == null && (known.get(pressures.get(0)) == null || known.get(pressures.get(2)) == null
            || known.get(pressures.get(3)) == null)) {
          result.put(pressures.get(1), null);
        }

        if (known.get(pressures.get(pressures.size() - 2)) == null && (known.get(pressures.get(pressures.size() - 1)) == null
            || known.get(pressures.get(pressures.size() - 3)) == null || known.get(pressures.get(pressures.size() - 4)) == null)) {
          result.put(pressures.get(pressures.size() - 2), null);
        }

        if (pressures.size() >= 5) {
          for (int i = 2; i < pressures.size() - 2; i++) {
            Long pressure = pressures.get(i);
            Float knownTarget = known.get(pressure);
            if (knownTarget == null &&
                (known.get(pressures.get(i - 1)) == null ||
                    known.get(pressures.get(i - 2)) == null ||
                    known.get(pressures.get(i + 1)) == null ||
                    known.get(pressures.get(i + 2)) == null)) {
              result.put(pressure, null);
            }
          }
        }
      }
    }

    for (Long normalizedPressure : resultPressures) {
      if (result.get(normalizedPressure) == null) {
        result.put(normalizedPressure, null);
      }
    }

    return result;

  }

  private List<String> getStationParameters(List<ArgoProfileV31> cProfiles, List<ArgoProfileV31> bProfiles) {
    Set<String> parameterNames = new LinkedHashSet<>();
    for (ArgoProfileV31 profile : cProfiles) {
      for (String parameterName : profile.getStationParameters()) {
        if (CoreParameterFilter.isSupportedParameter(parameterName)) {
          parameterNames.add(parameterName);
        }
      }
    }
    for (ArgoProfileV31 profile : bProfiles) {
      for (String parameterName : profile.getStationParameters()) {
        if (BioParameterFilter.isSupportedParameter(parameterName)) {
          parameterNames.add(parameterName);
        }
      }
    }
    return new ArrayList<>(parameterNames);
  }

  private void writeSProfileFile(Map<String, Map<Long, Float>> interpolatedValues, List<String> stationParameters) throws IOException {
    ArgoSyntheticProfileV13Bean profile = new ArgoSyntheticProfileV13Bean();
    for (String parameterName : stationParameters) {
      Map<Long, Float> values = interpolatedValues.get(parameterName);
      ArgoSyntheticProfileV13ParameterBean param = new ArgoSyntheticProfileV13ParameterBean();
      List<ArgoSyntheticProfileV13Level> levels = new ArrayList<>();
      param.setLevels(levels);
      param.setParameterName(parameterName);
//      param.setQc();
//      param.setDataMode()
//      param.setLevels(levels);

      for (Map.Entry<Long, Float> entry : values.entrySet()) {
        ArgoSyntheticProfileV13LevelBean levelBean = new ArgoSyntheticProfileV13LevelBean();
        levelBean.setOriginalValue(entry.getValue());
//        levelBean.setQc();
//        levelBean.setPressureDisplacement();
//        levelBean.setAdjustedValue();
//        levelBean.setAdjustedQc();
//        levelBean.setAdjustedError();
        levels.add(levelBean);
      }

      profile.getParameters().add(param);
    }

    Path parent = outputPath.getParent();
    if (parent != null) {
      Files.createDirectories(parent);
    }

    try {
      ArgoSyntheticProfileV13Writer.writeSingleProfile(outputPath, profile, version);
    } catch (InvalidRangeException e) {
      throw new RuntimeException(e);
    }

  }

  private static Map<Long, Float> getPressureValues(Map<Long, SynthRow> singleCRows, Collection<Long> resultPressures) {
    Map<Long, Float> result = new TreeMap<>();
    for (Long normalizedPressure : resultPressures) {
      SynthRow row = singleCRows.get(normalizedPressure);
      Float value = null;
      if (row != null) {
        value = getSingleRowParameterValue(row, "PRES");
      }
      if (value == null) {
        value = (float) ((double) normalizedPressure / 1000d);
      }
      result.put(normalizedPressure, value);
    }
    return result;
  }

  private static Float getSingleRowParameterValue(SynthRow row, String parameterName) {
    for (SynthProfile profile : row.getProfiles()) {
      SynthParameter parameter = profile.getParameters().get(parameterName);
      if (parameter != null) {
        Float value = parameter.getValue();
        if (value != null) {
          return value;
        }
      }
    }
    return null;
  }

  private static class Knot {

    private final float x;
    private final float y;

    public Knot(float x, float y) {
      this.x = x;
      this.y = y;
    }

    public float getX() {
      return x;
    }

    public float getY() {
      return y;
    }

    @Override
    public String toString() {
      return "Knot{" +
          "x=" + x +
          ", y=" + y +
          '}';
    }
  }

  private static Map<Long, Float> knownValues(String parameterName, Map<Long, SynthRow> singleRows, Collection<Long> resultPressures) {
    Map<Long, Knot> knots = new LinkedHashMap<>();

    for (SynthRow row : singleRows.values()) {
      float pressure = (float) ((double) row.getPressure() / 1000d);
      Float value = getSingleRowParameterValue(row, parameterName);
      if (value != null) {
        Knot knot = new Knot(pressure, value);
        knots.put(row.getPressure(), knot);
      }
    }

    Map<Long, Float> result = new TreeMap<>();
    for (Long normalizedPressure : resultPressures) {
      Knot knot = knots.get(normalizedPressure);
      if (knot != null) {
        result.put(normalizedPressure, knot.getY());
      } else {
        result.put(normalizedPressure, null);
      }
    }

    return result;

  }

  private static Map<Long, Float> interpolate(String parameterName, Map<Long, SynthRow> singleRows, Collection<Long> resultPressures) {
    Map<Long, Knot> knots = new LinkedHashMap<>();

    for (SynthRow row : singleRows.values()) {
      float pressure = (float) ((double) row.getPressure() / 1000d);
      Float value = getSingleRowParameterValue(row, parameterName);
      if (value != null) {
        Knot knot = new Knot(pressure, value);
        knots.put(row.getPressure(), knot);
      }
    }
    double[] x = new double[knots.size()];
    double[] y = new double[knots.size()];
    int i = 0;
    for (Knot knot : knots.values()) {
      x[i] = knot.getX();
      y[i] = knot.getY();
      i++;
    }

    PolynomialSplineFunction f = new LinearInterpolator().interpolate(x, y);
    Map<Long, Float> result = new TreeMap<>();
    for (Long normalizedPressure : resultPressures) {
      Knot knot = knots.get(normalizedPressure);
      if (knot != null) {
        result.put(normalizedPressure, knot.getY());
      } else {
        double pressure = (double) normalizedPressure / 1000d;
        try {
          double interpolatedValue = f.value(pressure);
          result.put(normalizedPressure, (float) interpolatedValue);
        } catch (OutOfRangeException e) {
          //TODO extrapolate?
          result.put(normalizedPressure, null);
        }
      }
    }

    return result;

  }


  private static class Plan {

    private List<MinMax> sections = new ArrayList<>();
    private final String parameterName;
    private final int profileIndex;

    public Plan(String parameterName, int profileIndex) {
      this.parameterName = parameterName;
      this.profileIndex = profileIndex;
    }

    public List<MinMax> getSections() {
      return sections;
    }

    public String getParameterName() {
      return parameterName;
    }

    public int getProfileIndex() {
      return profileIndex;
    }
  }

  private static class MinMax {

    private final int min;
    private final int max;

    public MinMax(int min, int max) {
      this.min = min;
      this.max = max;
    }

    public int getMin() {
      return min;
    }

    public int getMax() {
      return max;
    }
  }

  private Map<Long, SynthRow> resolveSingleProfileParameters(Map<Long, SynthRow> allRows, List<Integer> profilePriority,
      List<String> stationParameters) {
    List<SynthRow> rows = new ArrayList<>(allRows.values());
    List<Plan> plans = new ArrayList<>();
    for (String parameterName : stationParameters) {
      Integer overallMin = null;
      Integer overallMax = null;
      for (int profileIndex : profilePriority) {
        Integer min = null;
        Integer max = null;
        for (int i = 0; i < rows.size(); i++) {
          SynthRow row = rows.get(i);
          SynthProfile profile = row.getProfiles().get(profileIndex);
          if (profile != null) {
            SynthParameter parameter = profile.getParameters().get(parameterName);
            if (parameter != null) {
              if (min == null || i < min) {
                min = i;
              }
              if (max == null || i > max) {
                max = i;
              }
            }
          }
        }
        if (min != null && max != null) {
          Plan plan = new Plan(parameterName, profileIndex);
          if (overallMin == null) {
            overallMin = min;
            overallMax = max;

            MinMax minMax = new MinMax(min, max + 1);
            plan.getSections().add(minMax);
            plans.add(plan);
          } else {
            List<MinMax> minMaxs = new ArrayList<>();
            if (min < overallMin) {
              MinMax minMax = new MinMax(min, overallMin);
              minMaxs.add(minMax);
              overallMin = min;
            }
            if (max > overallMax) {
              MinMax minMax = new MinMax(overallMax, max + 1);
              minMaxs.add(minMax);
              overallMax = max;
            }
            if (!minMaxs.isEmpty()) {
              plan.getSections().addAll(minMaxs);
              plans.add(plan);
            }
          }

        }

      }
    }

    Map<Long, SynthRow> result = new TreeMap<>();
    for (Plan plan : plans) {
      for (MinMax minMax : plan.getSections()) {
        for (int i = minMax.getMin(); i < minMax.getMax(); i++) {
          SynthRow row = rows.get(i);
          SynthProfile profile = row.getProfiles().get(plan.getProfileIndex());
          if (profile != null) {
            SynthParameter parameter = profile.getParameters().get(plan.getParameterName());
            if (parameter != null) {
              SynthProfile copyProfile = new SynthProfile();
              copyProfile.setIndex(profile.getIndex());
              copyProfile.getParameters().put(parameter.getParameterName(), parameter);

              SynthRow copyRow = result.get(row.getPressure());
              if (copyRow == null) {
                copyRow = new SynthRow();
                copyRow.setPressure(row.getPressure());
                result.put(row.getPressure(), copyRow);
              }

              copyRow.getProfiles().add(copyProfile);
            }
          }
        }
      }
    }
    return result;
  }

  private List<Integer> getNProfPriority(ArgoMetadataV31 metadata, List<ArgoProfileV31> cProfiles, List<ArgoProfileV31> bProfiles) {
    if (cProfiles.size() == 1) {
      return Collections.singletonList(0);
    }
    List<String> parameters = metadata.getParameters();
    List<String> parameterSensors = metadata.getParameterSensors();
    Map<String, String> paramToSensor = new HashMap<>();
    for (int i = 0; i < parameters.size(); i++) {
      paramToSensor.put(parameters.get(i), parameterSensors.get(i));
    }

    List<String> concatSensorNames = new ArrayList<>(cProfiles.size() - 1);
    Map<String, Integer> concatSensorNameToIndex = new HashMap<>();
    for (int i = 1; i < cProfiles.size(); i++) {
      List<String> sensors = new ArrayList<>();
      ArgoProfileV31 cProfile = cProfiles.get(i);
      ArgoProfileV31 bProfile = bProfiles.get(i);
      Set<String> params = new HashSet<>(cProfile.getStationParameters());
      params.addAll(bProfile.getStationParameters());
      params.forEach(param -> sensors.add(paramToSensor.get(param)));
      Collections.sort(sensors);
      String concatSensorName = String.join("_", sensors);
      concatSensorNames.add(concatSensorName);
      concatSensorNameToIndex.put(concatSensorName, i);
    }

    Collections.sort(concatSensorNames);
    List<Integer> order = new ArrayList<>(cProfiles.size());
    order.add(0);
    for (String sensorName : concatSensorNames) {
      order.add(concatSensorNameToIndex.get(sensorName));
    }

    /*
    'CTD_CNDC_CTD_PRES_CTD_TEMP_FLOATCLOCK_MTIME'
    'CTD_PRES_OPTODE_DOXY_OPTODE_DOXY_OPTODE_DOXY_OPTODE_DOXY_OPTODE_DOXY'
    'CTD_PRES_RADIOMETER_DOWN_IRR380_RADIOMETER_DOWN_IRR380_RADIOMETER_DOWN_IRR412_RADIOMETER_DOWN_IRR412_RADIOMETER_DOWN_IRR490_RADIOMETER_DOWN_IRR490_RADIOMETER_PAR_RADIOMETER_PAR'
    'CTD_CNDC_CTD_PRES_CTD_TEMP_SPECTROPHOTOMETER_NITRATE_SPECTROPHOTOMETER_NITRATE_SPECTROPHOTOMETER_NITRATE_SPECTROPHOTOMETER_NITRATE_SPECTROPHOTOMETER_NITRATE_SPECTROPHOTOMETER_NITRATE_SPECTROPHOTOMETER_NITRATE_SPECTROPHOTOMETER_NITRATE'
     */
    return order;
  }

  private Map<Long, SynthRow> populateCProfileRows(List<ArgoProfileV31> cProfiles, Map<Integer, Set<Integer>> validIndexesForProfile) {
    Map<Long, SynthRow> rows = new TreeMap<>();
    for (ArgoProfileV31 cProfile : cProfiles) {
      Set<Integer> validIndexes = validIndexesForProfile.get(cProfile.getProfileIndex());
      for (int levelIndex : validIndexes) {
        ArgoProfileV31Parameter pressureParameter = cProfile.getParameter("PRES");
        ArgoProfileV31Level pressureLevel = pressureParameter.getLevels().get(levelIndex);
        populateRows(
            rows,
            cProfiles.size(),
            cProfile,
            pressureLevel,
            (parameter, level) -> level.getValue() != null && CoreParameterFilter.isSupportedParameter(parameter, level),
            (row, rowProfile, rowParameter) -> {
            });
      }
    }
    return rows;
  }


  private static Map<Long, SynthRow> populateBProfileRows(List<ArgoProfileV31> bProfiles, Map<Integer, Set<Integer>> validIndexesForProfile) {
    Map<Long, SynthRow> rows = new TreeMap<>();
    for (ArgoProfileV31 bProfile : bProfiles) {
      Set<Integer> validIndexes = validIndexesForProfile.get(bProfile.getProfileIndex());
      Map<String, SynthRow> lastPressures = new HashMap<>();
      for (int levelIndex : validIndexes) {
        ArgoProfileV31Parameter pressureParameter = bProfile.getParameter("PRES");
        ArgoProfileV31Level pressureLevel = pressureParameter.getLevels().get(levelIndex);

        populateRows(
            rows,
            bProfiles.size(),
            bProfile,
            pressureLevel,
            (parameter, level) -> level.getValue() != null && BioParameterFilter.isSupportedParameter(parameter),
            (row, rowProfile, rowParameter) -> {
              String parameterName = rowParameter.getParameterName();
              SynthRow lastRow = lastPressures.get(parameterName);
              if (lastRow != null) {
                long upwardPDiff = updateMinimumRowPressureDiff(lastRow, rowProfile.getIndex(), parameterName, row.getPressure());
                rowParameter.setpDiff(upwardPDiff);
              }
              lastPressures.put(parameterName, row);
            });

      }
    }
    return rows;
  }

  private static void populateRows(
      Map<Long, SynthRow> rows,
      int numProfiles,
      ArgoProfileV31 profile,
      ArgoProfileV31Level pressureLevel,
      ParameterFilter parameterFilter,
      ParameterCreateVisitor onParameterCreate
  ) {
    int profileIndex = profile.getProfileIndex();
    int levelIndex = pressureLevel.getLevelIndex();
    Float actualPressure = pressureLevel.getValue();
    if (actualPressure != null) {
      // need to use BigDecimal math to prevent rounding errors causing strange errors with keys
      long normalizedPressure = new BigDecimal(Float.toString(actualPressure)).multiply(new BigDecimal("1000")).longValue();

      SynthRow row = rows.get(normalizedPressure);
      if (row == null) {
        row = new SynthRow();
        row.setPressure(normalizedPressure);
        rows.put(normalizedPressure, row);
      }

      List<SynthProfile> rowProfiles = row.getProfiles();
      if (rowProfiles.isEmpty()) {
        for (int i = 0; i < numProfiles; i++) {
          rowProfiles.add(null);
        }
      }

      SynthProfile rowProfile = rowProfiles.get(profileIndex);
      if (rowProfile == null) {
        rowProfile = new SynthProfile();
        rowProfile.setIndex(profileIndex);
        rowProfile.getSupportedParameters().addAll(profile.getStationParameters());
        rowProfiles.set(profileIndex, rowProfile);
      }

      for (ArgoProfileV31Parameter parameter : profile.getParameters()) {
        ArgoProfileV31Level level = parameter.getLevels().get(levelIndex);
        if (parameterFilter.doAllow(parameter, level)) {
          String parameterName = parameter.getParameterName();

          SynthParameter rowParameter = new SynthParameter();
          rowParameter.setParameterName(parameterName);
          rowParameter.setValue(level.getValue());

          onParameterCreate.onParameterCreate(row, rowProfile, rowParameter);

          rowProfile.getParameters().put(parameterName, rowParameter);
        }
      }

    }
  }

  @FunctionalInterface
  private interface ParameterCreateVisitor {

    void onParameterCreate(SynthRow row, SynthProfile profile, SynthParameter parameter);
  }

  @FunctionalInterface
  private interface ParameterFilter {

    boolean doAllow(ArgoProfileV31Parameter parameter, ArgoProfileV31Level level);
  }

  private static long updateMinimumRowPressureDiff(SynthRow lastRow, int profileIndex, String parameterName, long currentPressure) {
    long lastPressure = lastRow.getPressure();
    long upwardPDiff = currentPressure - lastPressure;
    SynthParameter lastRowParameter = lastRow.getProfiles().get(profileIndex).getParameters().get(parameterName);
    if (lastRowParameter.getpDiff() == null) {
      lastRowParameter.setpDiff(upwardPDiff);
    } else {
      lastRowParameter.setpDiff(Math.min(lastRowParameter.getpDiff(), upwardPDiff));
    }
    return upwardPDiff;
  }

  private static List<SynthRow> filterPopulatedRows(Map<Long, SynthRow> rows) {
    return rows.values().stream().filter(row -> {
      for (SynthProfile profile : row.getProfiles()) {
        if (profile != null) {
          for (SynthParameter parameter : profile.getParameters().values()) {
            if (parameter.getValue() != null) {
              return true;
            }
          }
        }
      }
      return false;
    }).toList();
  }

  private static long getMinimumPressureDifference(SynthRow row) {
    Long minDPres = null;
    for (SynthProfile profile : row.getProfiles()) {
      if (profile != null) {
        for (SynthParameter parameter : profile.getParameters().values()) {
          if (parameter.getpDiff() != null) {
            if (minDPres == null || parameter.getpDiff() < minDPres) {
              minDPres = parameter.getpDiff();
            }
          }
        }
      }
    }
    return minDPres;
  }

  private static List<Long> calculateSyntheticPressures(List<SynthRow> bProfileRows) {
    List<Long> syntheticPressures = new ArrayList<>();
    int rowIndex = bProfileRows.size() - 1;
    while (rowIndex >= 0) {
      SynthRow row = bProfileRows.get(rowIndex);
      syntheticPressures.add(row.getPressure());

      if (rowIndex == 0) {
        break;
      }

      long minDPres = getMinimumPressureDifference(row);

      long minPressure = row.getPressure() - minDPres;
      ProfileCounter counter = new ProfileCounter(row.getProfiles().size());
      counter.updateCounter(rowIndex, row);

      for (int checkRowIndex = rowIndex - 1; checkRowIndex >= -1; checkRowIndex--) {
        if (checkRowIndex == -1) {
          rowIndex = 0;
          break;
        }

        SynthRow checkRow = bProfileRows.get(checkRowIndex);
        long checkPressure = checkRow.getPressure();
        if (checkPressure <= minPressure) {
          // ‘Jump’ to the next (shallower) pressure level outside the range.
          rowIndex = checkRowIndex;
          break;
        }
        counter.updateCounter(checkRowIndex, checkRow);
        if (counter.getMaxCount() > 1) {
          // ‘Jump’ to the deepest 2nd observation of the same N_PROF inside this range
          rowIndex = counter.getDeepestSecondObservation();
          break;
        }
      }
    }
    Collections.reverse(syntheticPressures);
    return syntheticPressures;
  }

}
