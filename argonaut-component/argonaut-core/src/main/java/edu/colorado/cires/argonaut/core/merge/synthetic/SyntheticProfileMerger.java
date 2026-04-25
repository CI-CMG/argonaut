package edu.colorado.cires.argonaut.core.merge.synthetic;

import edu.colorado.cires.argonaut.core.netcdf.metadata.v31.ArgoMetadataV31;
import edu.colorado.cires.argonaut.core.netcdf.metadata.v31.ArgoMetadataV31Reader;
import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoMultiProfileV31;
import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoProfileV31;
import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoProfileV31Level;
import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoProfileV31Parameter;
import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoProfileV31Reader;
import java.io.Console;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.Predicate;
import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.apache.commons.math3.exception.MathIllegalNumberException;
import org.apache.commons.math3.exception.OutOfRangeException;

public class SyntheticProfileMerger {

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

      Map<Integer, Set<Integer>> validIndexes = PressureIndexer.getQcValidatedPressureIndexes(cProfiles);
      Map<Long, SynthRow> rows = populateBProfileRows(bProfiles, validIndexes);

      List<Long> syntheticPressures = calculateSyntheticPressures(filterPopulatedRows(rows));

//      removeNotSyntheticPressures(rows, syntheticPressures);
      List<Integer> profilePriority = getNProfPriority(metadata, cProfiles, bProfiles);
      Map<Long, SynthRow> cRows = populateCProfileRows(cProfiles, validIndexes);
      Map<Long, SynthRow> singleCRows = resolveSingleCoreParameters(cRows, profilePriority);

      Set<Long> levels = new TreeSet<>(syntheticPressures);
      levels.addAll(singleCRows.keySet());

      Map<Long, Float> interpolatedTemp = interpolate(singleCRows, levels);

    }

  }

  private Map<Long, Float> interpolate(Map<Long, SynthRow> singleCRows, Set<Long> levels) {
    Map<Long, Float> result = new TreeMap<>();
    List<Double> xPressure = new ArrayList<>(singleCRows.size());
    List<Double> yTemp = new ArrayList<>(singleCRows.size());
    for (SynthRow row : singleCRows.values()) {
      Float actualPressure = row.getProfiles().stream()
          .flatMap(p -> p.getParameters().values().stream())
          .filter(p -> p.getParameterName().equals("PRES"))
          .findFirst().map(SynthParameter::getValue).orElseThrow();
      row.getProfiles().stream()
          .flatMap(p -> p.getParameters().values().stream())
          .filter(p -> p.getParameterName().equals("TEMP"))
          .findFirst().map(SynthParameter::getValue)
          .ifPresent(temp -> {
            xPressure.add((double) actualPressure);
            yTemp.add((double) temp);
            result.put(row.getPressure(), temp);
          });
    }

    LinearInterpolator interp = new LinearInterpolator();
    double[] x = xPressure.stream().mapToDouble(Double::doubleValue).toArray();
    double[] y = yTemp.stream().mapToDouble(Double::doubleValue).toArray();
    PolynomialSplineFunction f = interp.interpolate(x, y);

    for (Long pressure : levels) {
      if (result.get(pressure) == null) {
        try {
          result.put(pressure, (float) f.value(pressure));
        } catch (OutOfRangeException e) {
          break;
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

  private Map<Long, SynthRow> resolveSingleCoreParameters(Map<Long, SynthRow> cRows, List<Integer> profilePriority) {
    List<SynthRow> rows = new ArrayList<>(cRows.values());
    List<Plan> plans = new ArrayList<>();
    for (String parameterName : Arrays.asList("PRES", "TEMP", "PSAL")) {
      Integer overallMin = null;
      Integer overallMax = null;
      for (int profileIndex : profilePriority) {
        Integer min = null;
        Integer max = null;
        for (int i = 0; i < rows.size(); i++) {
          SynthRow row = rows.get(i);
          SynthProfile profile = row.getProfiles().get(profileIndex);
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
          SynthProfile rowProfile = new SynthProfile();
          rowProfile.setIndex(i);
          rowProfile.getSupportedParameters().addAll(profile.getStationParameters());
          rowProfiles.add(rowProfile);
        }
      }

      SynthProfile rowProfile = rowProfiles.get(profileIndex);

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
        for (SynthParameter parameter : profile.getParameters().values()) {
          if (parameter.getValue() != null) {
            return true;
          }
        }
      }
      return false;
    }).toList();
  }

  private static long getMinimumPressureDifference(SynthRow row) {
    Long minDPres = null;
    for (SynthProfile profile : row.getProfiles()) {
      for (SynthParameter parameter : profile.getParameters().values()) {
        if (parameter.getpDiff() != null) {
          if (minDPres == null || parameter.getpDiff() < minDPres) {
            minDPres = parameter.getpDiff();
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
