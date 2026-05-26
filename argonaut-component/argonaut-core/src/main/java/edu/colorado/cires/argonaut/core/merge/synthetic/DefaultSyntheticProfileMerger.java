package edu.colorado.cires.argonaut.core.merge.synthetic;

import edu.colorado.cires.argonaut.core.merge.synthetic.PressureIndexer.QcIndexMap;
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
import edu.colorado.cires.argonaut.core.util.SoftwareVersion;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
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
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.apache.commons.math3.exception.OutOfRangeException;
import ucar.ma2.InvalidRangeException;

public class DefaultSyntheticProfileMerger implements SyntheticProfileMerger {

  private static final List<String> GOOD_QC = Arrays.asList("1", "2", "5");
  private static final List<String> GOOD_QC_8 = Arrays.asList("1", "2", "5", "8");
  private static final List<String> BAD_QC = Arrays.asList("3", "4");

  public void mergeProfiles(Path cProfilePath, Path bProfilePath, Path metaPath, Path outputPath) throws IOException {
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

      QcIndexMap qcIndexMap = PressureIndexer.getQcValidatedPressureIndexes(cProfiles);
      Map<Integer, Set<Integer>> validIndexes = qcIndexMap.getValidIndexes();
      String pressureQc = qcIndexMap.getPressureQc();

      Map<Long, SynthRow> bRows = populateBProfileRows(bProfiles, validIndexes);
      Map<Long, SynthRow> cRows = populateCProfileRows(cProfiles, validIndexes);
      List<Long> syntheticPressures = calculateSyntheticPressures(filterPopulatedRows(bRows));

      List<String> bParameters = stationParameters.stream().filter(BioParameterFilter::isSupportedParameter).toList();
      List<String> cParameters = stationParameters.stream().filter(CoreParameterFilter::isSupportedParameter).toList();

      Map<Long, SynthRow> singleCRows = resolveSingleProfileParameters(cRows, profilePriority, cParameters);
      Map<Long, SynthRow> singleBRows = resolveSingleProfileParameters(bRows, profilePriority, bParameters);

      Set<Long> resultPressures = new TreeSet<>(singleCRows.keySet());
      resultPressures.addAll(syntheticPressures);

      QcStrategy cQcStrategy = new QcStrategy8();
      QcStrategy bQcStrategy = new QcStrategyMax();

      Map<String, Map<Long, FloatQc>> interpolatedValues = new LinkedHashMap<>();
      for (String parameterName : stationParameters) {
        if (cParameters.contains(parameterName)) {
          if (parameterName.equals("PRES")) {
            interpolatedValues.put("PRES", getPressureValues(singleCRows, resultPressures, pressureQc));
          } else {
            interpolatedValues.put(parameterName, interpolate(parameterName, singleCRows, resultPressures, cQcStrategy));
          }
        } else {
          Map<Long, FloatQc> known = knownValues(parameterName, singleBRows, syntheticPressures);
          Map<Long, FloatQc> interpolated = interpolate(parameterName, singleBRows, syntheticPressures, bQcStrategy);
          Map<Long, FloatQc> nearest = keepNearestInterpolatedValues(parameterName, known, singleBRows, interpolated);
          interpolatedValues.put(parameterName, resolveSingleGapInterpolations(parameterName, singleBRows, nearest, interpolated, resultPressures));
        }
      }

      writeSProfileFile(interpolatedValues, stationParameters, cProfiles, bProfiles, outputPath);

    }

  }

  private static class FloatQc {

    private final float value;
    private final String qc;
    private final Float pressureDisplacement;

    // TODO these need to be calculated.  First example does not use this.
    private final Float adjustedValue = null;
    private final Float adjustedError = null;
    private final String adjustedQc = null;

    private FloatQc(float value, String qc, Float pressureDisplacement) {
      this.value = value;
      this.qc = qc;
      this.pressureDisplacement = pressureDisplacement;
    }

    public float getValue() {
      return value;
    }

    public String getQc() {
      return qc;
    }

    public Float getPressureDisplacement() {
      return pressureDisplacement;
    }

    public Float getAdjustedValue() {
      return adjustedValue;
    }

    public String getAdjustedQc() {
      return adjustedQc;
    }

    public Float getAdjustedError() {
      return adjustedError;
    }
  }

  private static Map<Long, FloatQc> keepNearestInterpolatedValues(String parameterName, Map<Long, FloatQc> known, Map<Long, SynthRow> singleBRows,
      Map<Long, FloatQc> interpolated) {
    if (known.size() < 2) {
      return known;
    }
    List<Long> pressures = new ArrayList<>(known.keySet());

    Set<Long> filtered = new HashSet<>();
    for (Entry<Long, SynthRow> entry : singleBRows.entrySet()) {
      Long pressure = entry.getKey();
      SynthRow row = entry.getValue();
      for (SynthProfile profile : row.getProfiles()) {
        if (profile.getParameters().get(parameterName) != null) {
          filtered.add(pressure);
        }
      }
    }

    Map<Long, FloatQc> result = new TreeMap<>(known);
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

  private static boolean isSingleGap(int i, List<Long> pressures, Map<Long, FloatQc> nearest) {
    int last = pressures.size() - 1;

    if (pressures.size() >= 3 && i == 0) {
      return nearest.get(pressures.get(1)) != null && nearest.get(pressures.get(2)) != null;
    }

    if (pressures.size() >= 3 && i == last) {
      return nearest.get(pressures.get(last - 1)) != null && nearest.get(pressures.get(last - 2)) != null;
    }

    if (pressures.size() >= 4 && i == 1) {
      return nearest.get(pressures.get(0)) != null && nearest.get(pressures.get(2)) != null && nearest.get(pressures.get(3)) != null;
    }

    if (pressures.size() >= 4 && i == last - 1) {
      return nearest.get(pressures.get(last)) != null && nearest.get(pressures.get(last - 2)) != null && nearest.get(pressures.get(last - 3)) != null;
    }

    if (pressures.size() >= 5) {
      return nearest.get(pressures.get(i - 2)) != null &&
          nearest.get(pressures.get(i - 1)) != null &&
          nearest.get(pressures.get(i + 1)) != null &&
          nearest.get(pressures.get(i + 2)) != null;
    }

    return false;
  }

  private static Float resolvePressureDisplacement(long pressure, String parameterName, Map<Long, SynthRow> rows) {
    Long shallowestPressure = null;
    Long deepestPressure = null;
    for (Entry<Long, SynthRow> entry : rows.entrySet()) {
      long checkPressure = entry.getKey();
      SynthRow row = entry.getValue();
      if (row.getProfiles().stream().anyMatch(profile -> profile.getParameters().get(parameterName) != null && profile.getParameters().get(parameterName).getValue() != null)) {
        if (checkPressure >= pressure) {
          deepestPressure = checkPressure;
          break;
        }
        shallowestPressure = checkPressure;
      }
    }
    Long diff = null;
     if(deepestPressure != null && shallowestPressure != null) {
      long deepDiff = deepestPressure - pressure;
      long shallowDiff = pressure - shallowestPressure;
      if (shallowDiff < deepDiff) {
        diff = -shallowDiff;
      } else {
        diff = deepDiff;
      }
    } else if(deepestPressure != null) {
       diff = deepestPressure - pressure;
     } else if(shallowestPressure != null) {
       diff = -(pressure - shallowestPressure);
     }
    if (diff != null) {
      return (float)((double) diff / 1000d);
    }
    return null;
  }

  private static Map<Long, FloatQc> resolveSingleGapInterpolations(String parameterName, Map<Long, SynthRow> rows, Map<Long, FloatQc> nearest, Map<Long, FloatQc> interpolated,
      Collection<Long> resultPressures) {

    Map<Long, FloatQc> result = new TreeMap<>(nearest);
    if (nearest.size() >= 3) {
      List<Long> pressures = new ArrayList<>(nearest.keySet());
      for (int i = 0; i < pressures.size(); i++) {
        Long pressure = pressures.get(i);
        FloatQc currentValue = nearest.get(pressure);
        FloatQc valueToSet = null;
        if (currentValue != null) {
          valueToSet = currentValue;
        } else if (isSingleGap(i, pressures, nearest)) {
          valueToSet = new FloatQc(interpolated.get(pressure).getValue(), "8", resolvePressureDisplacement(pressure, parameterName, rows));
        }
        result.put(pressure, valueToSet);
      }
    }

    for (Long normalizedPressure : resultPressures) {
      if (result.get(normalizedPressure) == null) {
        result.put(normalizedPressure, null);
      }
    }

    return result;

  }

  private static List<String> getStationParameters(List<ArgoProfileV31> cProfiles, List<ArgoProfileV31> bProfiles) {
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

  private static String getParamQc(Map<Long, FloatQc> values) {
    //TODO
    /*
    The computation should be taken from <PARAM_ADJUSTED>_QC if available and from
<PARAM>_QC otherwise.
     */
    int total = 0;
    int good = 0;
    for(FloatQc floatQc : values.values()) {
      if (floatQc != null) {
        String qc = floatQc.getQc();
        if (GOOD_QC_8.contains(qc)) {
          good++;
          total++;
        } else if (BAD_QC.contains(qc)) {
          total++;
        }
      }
    }

    if (total == 0) {
      return null;
    }
    if (good == 0) {
      return "F";
    }
    if (good == total) {
      return "A";
    }
    double percentGood = (double)good / (double)total;
    if (percentGood >= 0.75) {
      return "B";
    }
    if (percentGood >= 0.5) {
      return "C";
    }
    if (percentGood >= 0.25) {
      return "D";
    }
    return "E";
  }

  private static String getParameterDataMode(String parameterName, List<ArgoProfileV31> cProfiles, List<ArgoProfileV31> bProfiles) {

    for (ArgoProfileV31 profile : cProfiles) {
      for (ArgoProfileV31Parameter parameter : profile.getParameters()) {
        if (parameter.getParameterName().equals(parameterName)) {
          String parameterDataMode = parameter.getDataMode();
          if (parameterDataMode != null) {
            return parameterDataMode;
          } else {
            return profile.getDataMode();
          }
        }
      }
    }

    for (ArgoProfileV31 profile : bProfiles) {
      for (ArgoProfileV31Parameter parameter : profile.getParameters()) {
        if (parameter.getParameterName().equals(parameterName)) {
          String parameterDataMode = parameter.getDataMode();
          if (parameterDataMode != null) {
            return parameterDataMode;
          } else {
            return profile.getDataMode();
          }
        }
      }
    }
    return null;
  }

  private static void writeSProfileFile(
      Map<String, Map<Long, FloatQc>> interpolatedValues,
      List<String> stationParameters,
      List<ArgoProfileV31> cProfiles,
      List<ArgoProfileV31> bProfiles,
      Path outputPath
      ) throws IOException {

    Instant now = Instant.now();

    ArgoSyntheticProfileV13Bean profile = new ArgoSyntheticProfileV13Bean();
    ArgoProfileV31 pressProfile = cProfiles.stream().filter(p -> p.getStationParameters().contains("PRES")).findFirst().orElseThrow();
    profile.setInstitution(pressProfile.getInstitution());
    profile.setCycleNumber(pressProfile.getCycleNumber());
    profile.setDataCenter(pressProfile.getDataCenter());
    profile.setDataType("Argo synthetic profile");
    profile.setFormatVersion("1.0");
    profile.setHandbookVersion("1.2");
    profile.setDateCreation(now);
    profile.setDateUpdate(now);
    profile.setPlatformNumber(pressProfile.getPlatformNumber());
    profile.setProjectName(pressProfile.getProjectName());
    profile.setPrincipalInvestigatorName(pressProfile.getPrincipalInvestigatorName());
    profile.setCycleNumber(pressProfile.getCycleNumber());
    profile.setDirection(pressProfile.getDirection());
    profile.setDataCenter(pressProfile.getDataCenter());
    profile.setPlatformType(pressProfile.getPlatformType());
    profile.setFloatSerialNumber(pressProfile.getFloatSerialNumber());
    profile.setFirmwareVersion(pressProfile.getFirmwareVersion());
    profile.setWmoInstrumentType(pressProfile.getWmoInstrumentType());
    profile.setJulianDate(pressProfile.getJulianDate());
    profile.setJulianDateQc(pressProfile.getJulianDateQc());
    profile.setJulianDateOfLocation(pressProfile.getJulianDateOfLocation());
    profile.setLongitude(pressProfile.getLongitude());
    profile.setLatitude(pressProfile.getLatitude());
    profile.setPositionQc(pressProfile.getPositionQc());
    profile.setPositioningSystem(pressProfile.getPositioningSystem());
    profile.setConfigMissionNumber(pressProfile.getConfigMissionNumber());

    for (String parameterName : stationParameters) {
      Map<Long, FloatQc> values = interpolatedValues.get(parameterName);
      ArgoSyntheticProfileV13ParameterBean param = new ArgoSyntheticProfileV13ParameterBean();
      List<ArgoSyntheticProfileV13Level> levels = new ArrayList<>();
      param.setLevels(levels);
      param.setParameterName(parameterName);
      param.setQc(getParamQc(values));
      param.setDataMode(getParameterDataMode(parameterName, cProfiles, bProfiles));

      for (Map.Entry<Long, FloatQc> entry : values.entrySet()) {
        ArgoSyntheticProfileV13LevelBean levelBean = new ArgoSyntheticProfileV13LevelBean();
        FloatQc floatQc = entry.getValue();
        if (floatQc != null) {
          levelBean.setOriginalValue(floatQc.getValue());
          levelBean.setQc(floatQc.getQc());
          levelBean.setPressureDisplacement(floatQc.getPressureDisplacement());
          levelBean.setAdjustedValue(floatQc.getAdjustedValue());
          levelBean.setAdjustedQc(floatQc.getAdjustedQc());
          levelBean.setAdjustedError(floatQc.getAdjustedError());
        }
        levels.add(levelBean);
      }

      profile.getParameters().add(param);
    }

    Path parent = outputPath.getParent();
    if (parent != null) {
      Files.createDirectories(parent);
    }

    try {
      ArgoSyntheticProfileV13Writer.writeSingleProfile(outputPath, profile, SoftwareVersion.getVersion());
    } catch (InvalidRangeException e) {
      throw new RuntimeException(e);
    }

  }

  private static Map<Long, FloatQc> getPressureValues(Map<Long, SynthRow> singleCRows, Collection<Long> resultPressures, String pressureQc) {
    Map<Long, FloatQc> result = new TreeMap<>();
    for (Long normalizedPressure : resultPressures) {
      SynthRow row = singleCRows.get(normalizedPressure);
      FloatQc value = null;
      if (row != null) {
        FloatQc temp = getSingleRowParameterValue(row, "PRES", null);
        if (temp != null) {
          value = new FloatQc(temp.getValue(), pressureQc, null);
        }
      }
      if (value == null) {
        value = new FloatQc((float) ((double) normalizedPressure / 1000d), pressureQc, null);
      }
      result.put(normalizedPressure, value);
    }
    return result;
  }

  private static FloatQc getSingleRowParameterValue(SynthRow row, String parameterName, Float pressureDisplacement) {
    for (SynthProfile profile : row.getProfiles()) {
      SynthParameter parameter = profile.getParameters().get(parameterName);
      if (parameter != null) {
        Float value = parameter.getValue();
        String qc = parameter.getQc();
        if (value != null) {
          return new FloatQc(value, qc, pressureDisplacement);
        }
      }
    }
    return null;
  }

  private static class Knot {

    private final float x;
    private final float y;
    private final String qc;

    public Knot(float x, float y, String qc) {
      this.x = x;
      this.y = y;
      this.qc = qc;
    }

    public float getX() {
      return x;
    }

    public float getY() {
      return y;
    }

    public String getQc() {
      return qc;
    }

    @Override
    public String toString() {
      return "Knot{" +
          "x=" + x +
          ", y=" + y +
          '}';
    }
  }

  private static Map<Long, FloatQc> knownValues(String parameterName, Map<Long, SynthRow> singleRows, Collection<Long> resultPressures) {
    Map<Long, Knot> knots = new LinkedHashMap<>();

    for (SynthRow row : singleRows.values()) {
      float pressure = (float) ((double) row.getPressure() / 1000d);
      FloatQc value = getSingleRowParameterValue(row, parameterName, 0F);
      if (value != null) {
        Knot knot = new Knot(pressure, value.getValue(), value.getQc());
        knots.put(row.getPressure(), knot);
      }
    }

    Map<Long, FloatQc> result = new TreeMap<>();
    for (Long normalizedPressure : resultPressures) {
      Knot knot = knots.get(normalizedPressure);
      if (knot != null) {
        result.put(normalizedPressure, new FloatQc(knot.getY(), knot.getQc(), 0F));
      } else {
        result.put(normalizedPressure, null);
      }
    }

    return result;

  }

  private interface QcStrategy {
    String getQc(String previousKnotQc, String nextknotQc);
  }

  private static class QcStrategy8 implements QcStrategy {
    @Override
    public String getQc(String previousKnotQc, String nextknotQc) {
      String maxQc = PressureIndexer.getHighestQcOrder(previousKnotQc == null ? "1" : previousKnotQc, nextknotQc == null ? "1" : nextknotQc);

      if (GOOD_QC.contains(maxQc)) {
        return "8";
      }
      return maxQc;

    }
  }

  private static class QcStrategyMax implements QcStrategy {
    @Override
    public String getQc(String previousKnotQc, String nextknotQc) {
      return PressureIndexer.getHighestQcOrder(previousKnotQc == null ? "1" : previousKnotQc, nextknotQc == null ? "1" : nextknotQc);
    }
  }

  private static Map<Long, FloatQc> interpolate(String parameterName, Map<Long, SynthRow> singleRows, Collection<Long> resultPressures, QcStrategy qcStrategy) {
    Map<Long, Knot> knots = new LinkedHashMap<>();

    for (SynthRow row : singleRows.values()) {
      float pressure = (float) ((double) row.getPressure() / 1000d);
      FloatQc value = getSingleRowParameterValue(row, parameterName, 0F);
      if (value != null) {
        Knot knot = new Knot(pressure, value.getValue(), value.getQc());
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
    Map<Long, FloatQc> result = new TreeMap<>();
    for (Long normalizedPressure : resultPressures) {
      Knot knot = knots.get(normalizedPressure);
      if (knot != null) {
        result.put(normalizedPressure, new FloatQc(knot.getY(), knot.getQc(), 0F));
      } else {
        double pressure = (double) normalizedPressure / 1000d;
        try {
          double interpolatedValue = f.value(pressure);
          result.put(normalizedPressure, new FloatQc((float) interpolatedValue, null, resolvePressureDisplacement(normalizedPressure, parameterName, singleRows)));
        } catch (OutOfRangeException e) {
          //TODO extrapolate?
          result.put(normalizedPressure, null);
        }
      }
    }

    List<Long> pressures = new ArrayList<>(result.keySet());
    int index = 0;
    String previousKnotQc = null;
    while (index < pressures.size()) {
      Long pressure = pressures.get(index);
      FloatQc floatQc = result.get(pressure);
      if (floatQc == null) {
        index++;
      } else if (floatQc.getQc() != null) {
        previousKnotQc = floatQc.getQc();
        index++;
      } else {
        int searchIndex = index + 1;
        String nextknotQc = null;
        while (nextknotQc == null && searchIndex < pressures.size()) {
          FloatQc check = result.get(pressures.get(searchIndex));
          if (check == null) {
            searchIndex++;
          } else if (check.getQc() != null) {
            nextknotQc = check.getQc();
          } else {
            searchIndex++;
          }
        }
        String qc = qcStrategy.getQc(previousKnotQc, nextknotQc);
        for (int j = index; j < searchIndex; j++) {
          Long updatePressure = pressures.get(j);
          FloatQc update = result.get(updatePressure);
          result.put(updatePressure, new FloatQc(update.getValue(), qc, update.getPressureDisplacement()));
        }
        previousKnotQc = null;
        index++;
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
          rowParameter.setQc(level.getQc());

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
