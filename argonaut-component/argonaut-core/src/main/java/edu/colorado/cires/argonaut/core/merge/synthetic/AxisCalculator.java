package edu.colorado.cires.argonaut.core.merge.synthetic;

import com.google.common.collect.Streams;
import edu.colorado.cires.argonaut.core.netcdf.metadata.v31.ArgoMetadataV31;
import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoProfileV31;
import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoProfileV31Level;
import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoProfileV31Parameter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

final class AxisCalculator {
  private AxisCalculator() {}

  record SyntheticAxis(List<Long> syntheticPressures, Set<Long> resultPressures,
                       List<Integer> profilePriority) {}

  public static SyntheticAxis calculate(MergeContext context) {
    ArgoMetadataV31 metadata = context.getMetadata();
    List<ArgoProfileV31> cProfiles = context.getCProfiles();
    List<ArgoProfileV31> bProfiles = context.getBProfiles();
    List<String> stationParameters = context.getStationParameters();

    List<Integer> profilePriority = getNProfPriority(metadata, cProfiles, bProfiles);

    Map<Integer, Map<String, List<Long>>> inPres = bProfiles.stream()
      .map(profile -> {
        List<Float> maskedPressures = Streams.zip(
          profile.getParameter("PRES").getLevels().stream(),
          cProfiles.get(profile.getProfileIndex()).getParameter("PRES").getLevels().stream(),
          (b, c) -> c.getQc() == null || Integer.parseInt(c.getQc()) > 3 ? null : b.getValue()
        ).toList();
        return Pair.of(profile.getProfileIndex(),
          stationParameters.stream()
            .filter(MergeConstants.BioParameterFilter::isSupportedParameter)
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
                  : BigDecimal.valueOf(value).multiply(MergeConstants.DBAR_CONVERSION))
                .map(value -> value == null ? value
                  : Optional.ofNullable(metadata.getPressureOffset(name))
                    .or(() -> Optional.of(0d))
                    .map(BigDecimal::valueOf)
                    .map(offset -> offset.multiply(MergeConstants.DBAR_CONVERSION))
                    .map(
                      offset -> value.add(offset).setScale(0, RoundingMode.HALF_EVEN).longValue())
                    .get()
                ).map(Long.class::cast)
                .toList();

              return Pair.of(name, list);
            })
            .collect(Collectors.toMap(Pair::getLeft, Pair::getRight))
        );
      })
      .filter(pair -> !pair.getRight().isEmpty())
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

    Set<Long> corePressures = getCorePressures(profilePriority, cProfiles);
    List<Long> syntheticPressures = calculateSyntheticAxis(uPres, dPres);


    Set<Long> resultPressures = new TreeSet<>(syntheticPressures);
    resultPressures.addAll(corePressures);
    return new  SyntheticAxis(syntheticPressures, resultPressures, profilePriority);
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

  private static Set<Long> getCorePressures(List<Integer> profilePriority, List<ArgoProfileV31> cProfiles) {
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
            .multiply(MergeConstants.DBAR_CONVERSION)
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

    return corePressures;
  }

  private static List<Integer> getNProfPriority(ArgoMetadataV31 metadata, List<ArgoProfileV31> cProfiles, List<ArgoProfileV31> bProfiles) {
    Map<String, String> paramToSensor = IntStream.range(0, metadata.getParameters().size()).boxed()
      .collect(Collectors.toMap(metadata.getParameters()::get, metadata.getParameterSensors()::get));

    Map<String, Integer> concatSensorNameToIndex = IntStream.range(1, cProfiles.size()).boxed()
      .map(i -> Pair.of(
        i,
        Stream.concat(cProfiles.get(i).getStationParameters().stream(), bProfiles.get(i).getStationParameters().stream())
          .map(paramToSensor::get)
          .sorted()
          .collect(Collectors.joining("_"))
      )).collect(Collectors.toMap(Pair::getRight, Pair::getLeft, (a, b) -> b, TreeMap::new));

    List<Integer> order = new ArrayList<>(cProfiles.size());
    order.add(0);
    order.addAll(concatSensorNameToIndex.values());

    return order;
  }
}
