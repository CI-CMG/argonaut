package edu.colorado.cires.argonaut.core.merge.synthetic;

import com.google.common.collect.Streams;
import edu.colorado.cires.argonaut.core.merge.synthetic.PressureIndexer.QcIndexMap;
import edu.colorado.cires.argonaut.core.netcdf.metadata.v31.ArgoMetadataV31;
import edu.colorado.cires.argonaut.core.netcdf.metadata.v31.ArgoMetadataV31Reader;
import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoMultiProfileV31;
import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoProfileV31;
import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoProfileV31Level;
import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoProfileV31Parameter;
import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoProfileV31Reader;
import edu.colorado.cires.argonaut.core.netcdf.synthprofile.v13.ArgoSyntheticProfileV13Calibration;
import edu.colorado.cires.argonaut.core.netcdf.synthprofile.v13.ArgoSyntheticProfileV13Level;
import edu.colorado.cires.argonaut.core.netcdf.synthprofile.v13.ArgoSyntheticProfileV13Writer;
import edu.colorado.cires.argonaut.core.netcdf.synthprofile.v13.impl.ArgoSyntheticProfileV13Bean;
import edu.colorado.cires.argonaut.core.netcdf.synthprofile.v13.impl.ArgoSyntheticProfileV13CalibrationBean;
import edu.colorado.cires.argonaut.core.netcdf.synthprofile.v13.impl.ArgoSyntheticProfileV13LevelBean;
import edu.colorado.cires.argonaut.core.netcdf.synthprofile.v13.impl.ArgoSyntheticProfileV13ParameterBean;
import edu.colorado.cires.argonaut.core.util.SoftwareVersion;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import ucar.ma2.InvalidRangeException;

public class DefaultSyntheticProfileMerger implements SyntheticProfileMerger {

  private static final List<String> GOOD_QC = Arrays.asList("1", "2", "5");
  private static final List<String> GOOD_QC_8 = Arrays.asList("1", "2", "5", "8");
  private static final List<String> BAD_QC = Arrays.asList("3", "4");

  public void mergeProfiles(Path cProfilePath, Path bProfilePath, Path metaPath, Path outputPath) throws IOException {
    try (
        ArgoMetadataV31Reader metaReader = new ArgoMetadataV31Reader(metaPath);
        ArgoProfileV31Reader cReader = new ArgoProfileV31Reader(cProfilePath);
        ArgoProfileV31Reader bReader = new ArgoProfileV31Reader(bProfilePath)
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

      Map<Long, SynthRow> bRows = populateBProfileRows(bProfiles, validIndexes, metadata);
      Map<Long, SynthRow> cRows = populateCProfileRows(cProfiles, validIndexes);

      List<String> bParameters = stationParameters.stream().filter(BioParameterFilter::isSupportedParameter).toList();
      List<String> cParameters = stationParameters.stream().filter(CoreParameterFilter::isSupportedParameter).toList();

      Map<Integer, Map<String, List<Long>>> inPres = bProfiles.stream()
        .map(profile -> {
          List<Float> maskedPressures = Streams.zip(
            profile.getParameter("PRES").getLevels().stream(),
            cProfiles.get(profile.getProfileIndex()).getParameter("PRES").getLevels().stream(),
            (b, c) -> c.getQc() == null || Integer.parseInt(c.getQc()) > 3 ? null : b.getValue()
          ).toList();
          return Pair.of(profile.getProfileIndex(),
            bParameters.stream()
              .filter(name -> {
                try {
                  profile.getParameter(name);
                  return true;
                } catch (Exception e) {
                  return false;
                }
              })
              .map(name -> {
                List<Long> list = maskedPressures.stream()
                  .map(value -> value == null ? null
                    : BigDecimal.valueOf(value).multiply(new BigDecimal("1000")))
                  .map(value -> value == null ? value
                    : Optional.ofNullable(metadata.getPressureOffset(name))
                      .or(() -> Optional.of(0d))
                      .map(BigDecimal::valueOf)
                      .map(offset -> offset.multiply(new BigDecimal("1000")))
                      .map(
                        offset -> value.add(offset).setScale(0, RoundingMode.HALF_EVEN).longValue())
                      .get()
                  ).map(Long.class::cast)
                  .toList();

                return Pair.of(name, list);
              })
              .collect(Collectors.toMap(Pair::getLeft, Pair::getRight))
          );
        }).filter(pair -> !pair.getRight().isEmpty())
        .collect(Collectors.toMap(Pair::getLeft, Pair::getRight));

      Set<Long> uPres = inPres.values().stream()
        .flatMap(m -> m.values().stream())
        .flatMap(Collection::stream)
        .filter(Objects::nonNull)
        .collect(Collectors.toCollection(TreeSet::new));

      Map<Integer, Map<String, Map<Long, Long>>> dPres = inPres.entrySet().stream()
        .flatMap(profileEntry -> profileEntry.getValue().entrySet().stream()
          .map(sensorEntry -> {
            List<Long> pressures = sensorEntry.getValue().stream().filter(Objects::nonNull).toList();

            Map<Long, Long> pDiffs = new HashMap<>();

            int size = pressures.size();

            if (size > 1) {
              for (int i = 0; i < size; i++) {
                long current = pressures.get(i);
                long minDiff;

                if (i == 0) {
                  minDiff = Math.abs(pressures.get(1) - current);
                } else if (i == size - 1) {
                  minDiff = Math.abs(current - pressures.get(size - 2));
                } else {
                  long diffUp = Math.abs(current - pressures.get(i - 1));
                  long diffDown = Math.abs(pressures.get(i + 1) - current);
                  minDiff = Math.min(diffUp, diffDown);
                }
                pDiffs.put(current, minDiff);
              }
            }
            return Triple.of(profileEntry.getKey(), sensorEntry.getKey(), pDiffs);
          })
        ).collect(Collectors.groupingBy(
          Triple::getLeft,
          Collectors.toMap(Triple::getMiddle, Triple::getRight)
        ));

      List<Long> syntheticPressures = calculateSyntheticAxis(uPres, dPres);

      Set<Long> corePressures = new TreeSet<>();
      List<String> coreParams = Arrays.asList("TEMP", "PSAL");

      for (String paramName : coreParams) {
        Long overallMin = null;
        Long overallMax = null;

        for (int profIdx : profilePriority) {
          ArgoProfileV31 cProfile = cProfiles.get(profIdx);
          if (cProfile == null) continue;

          ArgoProfileV31Parameter cParam;
          try {
            cParam = cProfile.getParameter(paramName);
          } catch (Exception e) {
            continue;
          }

          ArgoProfileV31Parameter cPres = cProfile.getParameter("PRES");
          List<ArgoProfileV31Level> paramLevels = cParam.getLevels();
          List<ArgoProfileV31Level> presLevels = cPres.getLevels();

          Long currentProfMin = null;
          Long currentProfMax = null;
          List<Long> keptPressures = new ArrayList<>();

          for (int i = 0; i < presLevels.size(); i++) {
            Float pVal = presLevels.get(i).getValue();
            Float yVal = paramLevels.get(i).getValue();

            if (pVal == null || yVal == null) continue;

            String pQc = presLevels.get(i).getQc();
            if (pQc != null && pQc.trim().equals("4")) continue;

            String yQc = paramLevels.get(i).getQc();
            int qcVal = (yQc == null || yQc.trim().isEmpty()) ? 0 : Integer.parseInt(yQc.trim());
            if (qcVal < 0 || qcVal > 5) continue;

            long pNorm = BigDecimal.valueOf(pVal)
              .multiply(new BigDecimal("1000"))
              .setScale(0, RoundingMode.HALF_EVEN)
              .longValue();

            if (currentProfMin == null || pNorm < currentProfMin) currentProfMin = pNorm;
            if (currentProfMax == null || pNorm > currentProfMax) currentProfMax = pNorm;

            if (overallMin == null || pNorm < overallMin || pNorm > overallMax) {
              keptPressures.add(pNorm);
            }
          }

          corePressures.addAll(keptPressures);

          if (currentProfMin != null) {
            if (overallMin == null || currentProfMin < overallMin) overallMin = currentProfMin;
            if (overallMax == null || currentProfMax > overallMax) overallMax = currentProfMax;
          }
        }
      }

      Set<Long> resultPressures = new TreeSet<>(syntheticPressures);
      resultPressures.addAll(corePressures);

      Map<Long, SynthRow> singleCRows = resolveSingleProfileParameters(cRows, profilePriority, cParameters);
      Map<Long, SynthRow> singleBRows = resolveSingleProfileParameters(bRows, profilePriority, bParameters);

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

      writeSProfileFile(metadata, interpolatedValues, stationParameters, cProfiles, bProfiles, outputPath);

    }

  }

  private static List<Long> calculateSyntheticAxis(Set<Long> uPresSet, Map<Integer, Map<String, Map<Long, Long>>> dPres) {
    List<Long> upres = new ArrayList<>(uPresSet);

    if (upres.isEmpty()) {
      return new ArrayList<>();
    }

    boolean allNaN = true;
    for (Map<String, Map<Long, Long>> profileParams : dPres.values()) {
      for (Map<Long, Long> pDiffs : profileParams.values()) {
        if (!pDiffs.isEmpty()) {
          allNaN = false;
          break;
        }
      }
      if (!allNaN) break;
    }

    if (allNaN) {
      return new ArrayList<>(Collections.singletonList(upres.get(upres.size() - 1)));
    }

    List<Long> syntheticAxis = new ArrayList<>();
    int i = upres.size() - 1;
    int niter = 0;
    int nitermax = upres.size() + 1;

    while (i >= 0) {
      niter++;
      if (niter > nitermax) {
        System.err.println("S-PROF_WARNING: Exceeded maximum number of iterations.");
        return new ArrayList<>();
      }

      long currentPres = upres.get(i);
      syntheticAxis.add(currentPres);

      Long minValdp = null;
      for (Map<String, Map<Long, Long>> profileParams : dPres.values()) {
        for (Map<Long, Long> pDiffs : profileParams.values()) {
          Long dp = pDiffs.get(currentPres);
          if (dp != null) {
            if (minValdp == null || dp < minValdp) {
              minValdp = dp;
            }
          }
        }
      }

      if (minValdp != null) {
        long windowBottom = currentPres - minValdp;

        int deepestSecondObsIndex = -1;
        boolean denserSampling = false;

        for (Map<String, Map<Long, Long>> profileParams : dPres.values()) {
          for (Map<Long, Long> pDiffs : profileParams.values()) {
            int obsCount = 0;
            int colDeepestSecondIdx = -1;

            for (int j = i; j >= 0; j--) {
              long checkP = upres.get(j);
              if (checkP <= windowBottom) break;

              if (pDiffs.containsKey(checkP)) {
                obsCount++;
                if (obsCount == 2) {
                  colDeepestSecondIdx = j;
                  break;
                }
              }
            }

            if (obsCount >= 2 && colDeepestSecondIdx > deepestSecondObsIndex) {
              deepestSecondObsIndex = colDeepestSecondIdx;
              denserSampling = true;
            }
          }
        }

        if (denserSampling) {
          i = deepestSecondObsIndex;
        } else {
          boolean jumped = false;
          for (int j = i - 1; j >= 0; j--) {
            if (upres.get(j) <= windowBottom) {
              i = j;
              jumped = true;
              break;
            }
          }
          if (!jumped) break;
        }
      } else {
        boolean jumped = false;
        for (int j = i - 1; j >= 0; j--) {
          long checkP = upres.get(j);
          boolean hasValid = false;

          for (Map<String, Map<Long, Long>> profileParams : dPres.values()) {
            for (Map<Long, Long> pDiffs : profileParams.values()) {
              if (pDiffs.containsKey(checkP)) {
                hasValid = true;
                break;
              }
            }
            if (hasValid) break;
          }

          if (hasValid) {
            i = j;
            jumped = true;
            break;
          }
        }
        if (!jumped) break;
      }
    }

    Collections.reverse(syntheticAxis);
    return syntheticAxis;
  }

  private record FloatQc(float value, String qc, Float pressureDisplacement,
                         Float adjustedValue, Float adjustedError, String adjustedQc) {}

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
        if (profile.parameters().get(parameterName) != null) {
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
      if (row.getProfiles().stream().anyMatch(profile -> profile.parameters().get(parameterName) != null && profile.parameters().get(parameterName).value() != null)) {
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
          FloatQc floatQc = interpolated.get(pressure);
          String qc = floatQc.qc();
          String adjustedQc = floatQc.adjustedQc();
          valueToSet = new FloatQc(
            floatQc.value(),
            GOOD_QC_8.contains(qc) ? "8" : qc,
            resolvePressureDisplacement(pressure, parameterName, rows),
            floatQc.adjustedValue(),
            floatQc.adjustedError(),
            GOOD_QC_8.contains(adjustedQc) ? "8" : adjustedQc
          );
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

  private static String getFloatQcValue(FloatQc floatQc) {
    String qc = floatQc.adjustedQc;
    if (qc == null) {
      qc = floatQc.qc;
    }

    return qc;
  }

  private static String getParamQc(Map<Long, FloatQc> values) {
    int total = 0;
    int good = 0;
    for(FloatQc floatQc : values.values()) {
      if (floatQc != null) {
        String qc = getFloatQcValue(floatQc);
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
      ArgoMetadataV31 metadata, Map<String, Map<Long, FloatQc>> interpolatedValues,
      List<String> stationParameters,
      List<ArgoProfileV31> cProfiles,
      List<ArgoProfileV31> bProfiles,
      Path outputPath
      ) throws IOException {

    Instant now = Instant.now();

    ArgoSyntheticProfileV13Bean profile = new ArgoSyntheticProfileV13Bean();
    ArgoProfileV31 pressProfile = cProfiles.stream().filter(p -> p.getStationParameters().contains("PRES")).findFirst().orElseThrow();
    profile.setInstitution(metadata.getInstitution());
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

      param.setCalibrations(getCalibrations(parameterName, BioParameterFilter.isSupportedParameter(parameterName) ? bProfiles : cProfiles));

      for (Map.Entry<Long, FloatQc> entry : values.entrySet()) {
        ArgoSyntheticProfileV13LevelBean levelBean = new ArgoSyntheticProfileV13LevelBean();
        FloatQc floatQc = entry.getValue();
        if (floatQc != null) {
          levelBean.setOriginalValue(floatQc.value());
          levelBean.setQc(floatQc.qc());
          levelBean.setPressureDisplacement(floatQc.pressureDisplacement());
          levelBean.setAdjustedValue(floatQc.adjustedValue());
          levelBean.setAdjustedQc(floatQc.adjustedQc());
          levelBean.setAdjustedError(floatQc.adjustedError());
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

  private static List<ArgoSyntheticProfileV13Calibration> getCalibrations(String parameterName, List<ArgoProfileV31> profiles) {
    Map<Integer, List<ArgoSyntheticProfileV13Calibration>> collect = profiles.stream()
      .sorted(Comparator.comparingInt(ArgoProfileV31::getProfileIndex))
      .map(ArgoProfileV31::getParameters)
      .flatMap(Collection::stream)
      .filter(parameter -> parameter.getParameterName().equals(parameterName))
      .flatMap(parameter -> parameter.getCalibrations().stream()
        .map(
          source -> (ArgoSyntheticProfileV13Calibration) new ArgoSyntheticProfileV13CalibrationBean(
            parameterName, source.getProfileIndex(), source.getCalibrationIndex(),
            source.getEquation(),
            source.getCoefficient(), source.getComment(), source.getDate()
          ))
      ).collect(Collectors.groupingBy(ArgoSyntheticProfileV13Calibration::getProfileIndex));

    return collect.entrySet().stream()
      .sorted(Entry.comparingByKey())
      .map(Entry::getValue)
      .findFirst()
      .orElse(Collections.emptyList());
  }

  private static Map<Long, FloatQc> getPressureValues(Map<Long, SynthRow> singleCRows, Collection<Long> resultPressures, String pressureQc) {
    Float constantAdjustedError = null;

    for (SynthRow r : singleCRows.values()) {
      FloatQc p = getSingleRowParameterValue(r, "PRES", null);
      if (p != null && p.adjustedError() != null) {
          constantAdjustedError = p.adjustedError();
          break;
      }
    }

    Map<Long, FloatQc> result = new TreeMap<>();
    for (Long normalizedPressure : resultPressures) {
      SynthRow row = singleCRows.get(normalizedPressure);
      FloatQc value = null;
      if (row != null) {
        FloatQc temp = getSingleRowParameterValue(row, "PRES", null);

        if (temp != null) {
          Float adjustedValue = temp.adjustedValue();
          Float adjustedError = temp.adjustedError();
          String adjustedQc = temp.adjustedQc();
          if (constantAdjustedError != null && adjustedError == null) {
            adjustedValue = temp.value();
            adjustedError = constantAdjustedError;
            adjustedQc = temp.qc();
          }

          value = new FloatQc(
            temp.value(),
            pressureQc,
            null,
            adjustedValue,
            adjustedError,
            adjustedQc
          );
        }
      }
      if (value == null) {
        float pressure = (float) ((double) normalizedPressure / 1000d);

        Float adjustedValue = null;
        Float adjustedError = null;
        String adjustedQc = null;
        if (constantAdjustedError != null) {
          adjustedValue = pressure;
          adjustedError = constantAdjustedError;
          adjustedQc = pressureQc;
        }

        value = new FloatQc(pressure, pressureQc, null, adjustedValue, adjustedError, adjustedQc);
      }

      result.put(normalizedPressure, value);
    }
    return result;
  }

  private static FloatQc getSingleRowParameterValue(SynthRow row, String parameterName, Float pressureDisplacement) {
    for (SynthProfile profile : row.getProfiles()) {
      Optional<FloatQc> maybeFloatQc = profile.getParameter(parameterName)
        .flatMap(parameter -> Optional.ofNullable(parameter.value())
          .map(value -> new FloatQc(value, parameter.qc(), pressureDisplacement, parameter.adjustedValue(), parameter.adjustedError(), parameter.adjustedQc()))
        );

      if (maybeFloatQc.isPresent()) {
        return maybeFloatQc.get();
      }
    }
    return null;
  }

  private record Knot(float x, float y, String qc, Float adjustedValue, Float adjustedError, String adjustedQc) {}

  private static Map<Long, FloatQc> knownValues(String parameterName, Map<Long, SynthRow> singleRows, Collection<Long> resultPressures) {
    Map<Long, Knot> knots = new LinkedHashMap<>();

    for (SynthRow row : singleRows.values()) {
      float pressure = (float) ((double) row.getPressure() / 1000d);
      FloatQc value = getSingleRowParameterValue(row, parameterName, 0F);
      if (value != null) {
        Knot knot = new Knot(pressure, value.value(), value.qc(), value.adjustedValue(), value.adjustedError(), value.adjustedQc());
        knots.put(row.getPressure(), knot);
      }
    }

    Map<Long, FloatQc> result = new TreeMap<>();
    for (Long normalizedPressure : resultPressures) {
      Knot knot = knots.get(normalizedPressure);
      if (knot != null) {
        result.put(normalizedPressure, new FloatQc(knot.y(), knot.qc(), 0F, knot.adjustedValue(), knot.adjustedError(), knot.adjustedQc()));
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
    TreeMap<Long, Knot> knots = new TreeMap<>();
    TreeMap<Long, Knot> adjustedKnots = new TreeMap<>();

    boolean hasAdjustedError = false;

    for (SynthRow row : singleRows.values()) {
      long pressure = row.getPressure();
      FloatQc value = getSingleRowParameterValue(row, parameterName, 0F);
      if (value != null) {
        Knot knot = new Knot(
          pressure,
          value.value(),
          value.qc(),
          value.adjustedValue(),
          value.adjustedError(),
          value.adjustedQc()
        );
        knots.put(pressure, knot);

        if (value.adjustedQc() != null) {
          if (value.adjustedError() != null) {
            hasAdjustedError = true;
          }
          adjustedKnots.put(pressure, knot);
        }
      }
    }

    Map<Long, FloatQc> result = new TreeMap<>();
    for (Long normalizedPressure : resultPressures) {
      FloatQc floatQc = interpolatePressureLevel(normalizedPressure, parameterName, knots,
        adjustedKnots, qcStrategy, singleRows, hasAdjustedError);
      result.put(normalizedPressure, floatQc);
    }

    return result;
  }

  private static FloatQc interpolatePressureLevel(Long normalizedPressure, String parameterName, TreeMap<Long, Knot> knots, TreeMap<Long, Knot> adjustedKnots, QcStrategy qcStrategy,
    Map<Long, SynthRow> singleRows, boolean hasAdjustedError) {
    if (knots.containsKey(normalizedPressure)) {
      Knot knot = knots.get(normalizedPressure);
      String adjustedQc = adjustedKnots.isEmpty() ? null : (knot.adjustedQc() == null ? "4" :  knot.adjustedQc());
      Float adjustedValue = knot.adjustedValue;
      Float adjustedError = knot.adjustedError;
      if ("4".equals(adjustedQc) || adjustedQc == null) {
        Pair<Float, String> adjustedValues = interpolateValue(normalizedPressure, adjustedKnots, qcStrategy, Knot::adjustedValue, Knot::adjustedQc);
        if (adjustedValues != null) {
          adjustedQc = adjustedValues.getRight();
          adjustedValue = adjustedValues.getLeft();
        }

        if (adjustedError == null && hasAdjustedError) {
          Pair<Float, String> adjustedErrorValues = interpolateValue(normalizedPressure, adjustedKnots, qcStrategy, Knot::adjustedError, Knot::adjustedQc);
          if (adjustedErrorValues != null) {
            adjustedError = adjustedErrorValues.getLeft();
          }
        }
      }
      return new FloatQc(knot.y(), knot.qc(), 0F, adjustedValue, adjustedError, adjustedQc);
    }

    Pair<Float, String> values = interpolateValue(normalizedPressure, knots, qcStrategy, Knot::y, Knot::qc);
    if (values != null) {
      Pair<Float, String> adjustedValues = interpolateValue(normalizedPressure, adjustedKnots, qcStrategy, Knot::adjustedValue, Knot::adjustedQc);
      String adjustedQc = adjustedKnots.isEmpty() ? null : "4";
      Float adjustedValue = null;
      Float adjustedError = null;
      if (adjustedValues != null) {
        adjustedQc = adjustedValues.getRight();
        adjustedValue = adjustedValues.getLeft();

        if (hasAdjustedError) {
          Pair<Float, String> adjustedErrorValues = interpolateValue(normalizedPressure, adjustedKnots, qcStrategy, Knot::adjustedError, Knot::adjustedQc);
          if (adjustedErrorValues != null) {
            adjustedError = adjustedErrorValues.getLeft();
          }
        }
      }
      return new FloatQc(values.getLeft(), values.getRight(), resolvePressureDisplacement(normalizedPressure, parameterName, singleRows), adjustedValue, adjustedError, adjustedQc);
    }

    return null;
  }

  private static Pair<Float, String> interpolateValue(Long normalizedPressure, TreeMap<Long, Knot> knots, QcStrategy qcStrategy, Function<Knot, Float> getValue, Function<Knot, String> getQc) {
    Knot knotAtPressure = knots.get(normalizedPressure);
    if (knotAtPressure != null) {
      return Pair.of(getValue.apply(knotAtPressure), getQc.apply(knotAtPressure));
    }

    Function<Function<Long, Entry<Long, Knot>>, Entry<Long, Knot>> getClosestNonNullValue = (mapOperation) -> {
      Entry<Long, Knot> entry = mapOperation.apply(normalizedPressure);

      while (entry != null) {
        if (getValue.apply(entry.getValue()) != null) {
          return entry;
        }

        Long currentPressure = entry.getKey();
        entry = mapOperation.apply(currentPressure);
        if (currentPressure.equals(entry.getKey())) {
          return null;
        }
      }

      return null;
    };

    Entry<Long, Knot> floorEntry = getClosestNonNullValue.apply(knots::floorEntry);
    Entry<Long, Knot> ceilingEntry = getClosestNonNullValue.apply(knots::ceilingEntry);

    BiFunction<Entry<Long, Knot>, Long, Pair<Float, String>> valueConsumer = (entry, threshold) -> {
      Long pressure = entry.getKey();
      if (Math.abs(normalizedPressure - pressure) <= threshold) {
        Knot knot = entry.getValue();

        return Pair.of(getValue.apply(knot), qcStrategy.getQc(null, getQc.apply(knot)));
      }

      return null;
    };

    if (ceilingEntry == null && floorEntry != null) { // known deeper value -> extrapolate downward
      return valueConsumer.apply(floorEntry, 2000L);
    } else if (floorEntry == null && ceilingEntry != null) { // known shallower value -> extrapolate upward
      return valueConsumer.apply(ceilingEntry, 1000L);
    } else if (floorEntry != null) { // known shallower and deeper value -> linear interpolation
      Knot floor = floorEntry.getValue();
      BigDecimal x0 = BigDecimal.valueOf(floorEntry.getKey());
      BigDecimal y0 = BigDecimal.valueOf(getValue.apply(floor));
      String qc0 = getQc.apply(floor);

      Knot ceiling = ceilingEntry.getValue();
      BigDecimal x1 = BigDecimal.valueOf(ceilingEntry.getKey());
      BigDecimal y1 = BigDecimal.valueOf(getValue.apply(ceiling));
      String qc1 = getQc.apply(ceiling);

      float v = y0.add(
        BigDecimal.valueOf(normalizedPressure).subtract(x0)
          .multiply(y1.subtract(y0))
          .divide(x1.subtract(x0), RoundingMode.FLOOR)
      ).floatValue();

      return Pair.of(v, qcStrategy.getQc(qc0, qc1));
    }

    return null;
  }

  private static class Plan {

    private final List<MinMax> sections = new ArrayList<>();
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

  private record MinMax(int min, int max) {

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
            Optional<SynthParameter> maybeParameter = profile.getParameter(parameterName);
            if (maybeParameter.isPresent()) {
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
      String parameterName = plan.getParameterName();
      for (MinMax minMax : plan.getSections()) {
        for (int i = minMax.min(); i < minMax.max(); i++) {
          SynthRow row = rows.get(i);
          SynthProfile profile = row.getProfiles().get(plan.getProfileIndex());
          if (profile != null) {
            profile.getParameter(parameterName)
              .ifPresent(parameter -> {
                SynthProfile copyProfile = new SynthProfile(profile.index(), new HashMap<>(0), new ArrayList<>(0));
              copyProfile.putParameter(parameter);

              SynthRow copyRow = result.get(row.getPressure());
              if (copyRow == null) {
                copyRow = new SynthRow();
                copyRow.setPressure(row.getPressure());
                result.put(row.getPressure(), copyRow);
              }

              copyRow.getProfiles().add(copyProfile);
            });
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
            },
            (rawValue, parameterName) -> rawValue
        );
      }
    }
    return rows;
  }


  private static Map<Long, SynthRow> populateBProfileRows(List<ArgoProfileV31> bProfiles, Map<Integer, Set<Integer>> validIndexesForProfile, ArgoMetadataV31 metadata) {
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
              String parameterName = rowParameter.parameterName();
              String pressureIdentifier = "%d_%s".formatted(rowProfile.index(), parameterName);
              SynthRow lastRow = lastPressures.get(pressureIdentifier);
              if (lastRow != null) {
                long upwardPDiff = updateMinimumRowPressureDiff(lastRow, rowProfile.index(), parameterName, row.getPressure());
                rowParameter = rowParameter.withPDiff(upwardPDiff);
                rowProfile.putParameter(rowParameter);
              }
              lastPressures.put(pressureIdentifier, row);
            },
            (rawValue, parameterName) -> Optional.ofNullable(metadata.getPressureOffset(parameterName))
              .map(offset -> new BigDecimal(Double.toString(offset)))
              .map(rawValue::add)
              .orElse(rawValue)
        );

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
      ParameterCreateVisitor onParameterCreate,
      BiFunction<BigDecimal, String, BigDecimal> addOffset
  ) {
    int profileIndex = profile.getProfileIndex();
    int levelIndex = pressureLevel.getLevelIndex();
    Float actualPressure = pressureLevel.getValue();
    if (actualPressure != null) {
      for (ArgoProfileV31Parameter parameter : profile.getParameters()) {
        ArgoProfileV31Level level = parameter.getLevels().get(levelIndex);
        if (parameterFilter.doAllow(parameter, level)) {
          // need to use BigDecimal math to prevent rounding errors causing strange errors with keys
          long normalizedPressure = addOffset.apply(new BigDecimal(Float.toString(actualPressure)), parameter.getParameterName())
            .multiply(new BigDecimal("1000")).longValue();

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
            rowProfile = new SynthProfile(profileIndex, new HashMap<>(0), profile.getStationParameters());
            rowProfiles.set(profileIndex, rowProfile);
          }

          String parameterName = parameter.getParameterName();

          SynthParameter rowParameter = new SynthParameter(parameterName, level.getValue(), null, level.getQc(), level.getAdjustedValue(), level.getAdjustedErrorValue(), level.getAdjustedQc());

          onParameterCreate.onParameterCreate(row, rowProfile, rowParameter);

          rowProfile.putParameter(rowParameter);
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
    SynthProfile synthProfile = lastRow.getProfiles().get(profileIndex);

    Optional<SynthParameter> maybeLastRowParameter = synthProfile.getParameter(parameterName);
    if (maybeLastRowParameter.isPresent()) {
      SynthParameter lastRowParameter = maybeLastRowParameter.get();
      if (lastRowParameter.pDiff() == null) {
        lastRowParameter = lastRowParameter.withPDiff(upwardPDiff);
      } else {
        lastRowParameter = lastRowParameter.withPDiff(Math.min(lastRowParameter.pDiff(), upwardPDiff));
      }

      synthProfile.putParameter(lastRowParameter);
    }

    return upwardPDiff;
  }

  private static List<SynthRow> filterPopulatedRows(Map<Long, SynthRow> rows) {
    return rows.values().stream().filter(row -> {
      for (SynthProfile profile : row.getProfiles()) {
        if (profile != null) {
          for (SynthParameter parameter : profile.parameters().values()) {
            if (parameter.value() != null) {
              return true;
            }
          }
        }
      }
      return false;
    }).toList();
  }

  private static Long getMinimumPressureDifference(SynthRow row) {
    Long minDPres = null;
    for (SynthProfile profile : row.getProfiles()) {
      if (profile != null) {
        for (SynthParameter parameter : profile.parameters().values()) {
          if (parameter.pDiff() != null) {
            if (minDPres == null || parameter.pDiff() < minDPres) {
              minDPres = parameter.pDiff();
            }
          }
        }
      }
    }
    return minDPres;
  }

}
