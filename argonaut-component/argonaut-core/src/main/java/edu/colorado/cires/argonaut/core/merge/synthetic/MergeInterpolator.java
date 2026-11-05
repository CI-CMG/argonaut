package edu.colorado.cires.argonaut.core.merge.synthetic;

import edu.colorado.cires.argonaut.core.merge.synthetic.AxisCalculator.SyntheticAxis;
import edu.colorado.cires.argonaut.core.merge.synthetic.DefaultSyntheticProfileMerger.FloatQc;
import edu.colorado.cires.argonaut.core.merge.synthetic.MergeInterpolator.PressureIndexer.QcIndexMap;
import edu.colorado.cires.argonaut.core.netcdf.metadata.v31.ArgoMetadataV31;
import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoProfileV31;
import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoProfileV31Level;
import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoProfileV31Parameter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.LongFunction;
import java.util.function.Predicate;
import org.apache.commons.lang3.tuple.Pair;

final class MergeInterpolator {
  private MergeInterpolator() {}

  private static final QcStrategy C_QC_STRATEGY = new QcStrategy8();
  private static final QcStrategy B_QC_STRATEGY = new QcStrategyMax();

  public static Map<String, Map<Long, FloatQc>> interpolateValues(MergeContext context, SyntheticAxis syntheticAxis) {
    List<ArgoProfileV31> cProfiles = context.getCProfiles();
    List<ArgoProfileV31> bProfiles = context.getBProfiles();
    ArgoMetadataV31 metadata = context.getMetadata();
    List<String> stationParameters = context.getStationParameters();

    List<Integer> profilePriority = syntheticAxis.profilePriority();
    Set<Long> resultPressures = syntheticAxis.resultPressures();
    List<Long> syntheticPressures = syntheticAxis.syntheticPressures();

    QcIndexMap qcIndexMap = PressureIndexer.getQcValidatedPressureIndexes(cProfiles);
    Map<Integer, Set<Integer>> validIndexes = qcIndexMap.getValidIndexes();
    String pressureQc = qcIndexMap.getPressureQc();
    Map<Long, SynthRow> bRows = populateBProfileRows(bProfiles, validIndexes, metadata);
    Map<Long, SynthRow> cRows = populateCProfileRows(cProfiles, validIndexes);

    Map<Long, SynthRow> singleCRows = resolveSingleProfileParameters(cRows, profilePriority,
      stationParameters, MergeConstants.CoreParameterFilter::isSupportedParameter);
    Map<Long, SynthRow> singleBRows = resolveSingleProfileParameters(bRows, profilePriority,
      stationParameters, MergeConstants.BioParameterFilter::isSupportedParameter);

    Map<String, Map<Long, FloatQc>> interpolatedValues = new LinkedHashMap<>();

    for (String parameterName : stationParameters) {
      if (MergeConstants.CoreParameterFilter.isSupportedParameter(parameterName)) {
        if (parameterName.equals("PRES")) {
          interpolatedValues.put("PRES", getPressureValues(singleCRows, resultPressures, pressureQc));
        } else {
          interpolatedValues.put(parameterName, interpolate(parameterName, singleCRows, resultPressures, C_QC_STRATEGY));
        }
      } else {
        Map<Long, FloatQc> known = knownValues(parameterName, singleBRows, syntheticPressures);
        Map<Long, FloatQc> interpolated = interpolate(parameterName, singleBRows, syntheticPressures, B_QC_STRATEGY);
        Map<Long, FloatQc> nearest = keepNearestInterpolatedValues(parameterName, known, singleBRows, interpolated);
        interpolatedValues.put(parameterName, resolveSingleGapInterpolations(parameterName, singleBRows, nearest, interpolated, resultPressures));
      }
    }

    return interpolatedValues;
  }

  private record MinMax(int min, int max) {}

  private record Plan(String parameterName, int profileIndex, List<MinMax> sections) {
    public void addSection(MinMax section) {
      this.sections.add(section);
    }

    public void addSections(Collection<MinMax> sections) {
      this.sections.addAll(sections);
    }
  }

  private static Map<Long, SynthRow> resolveSingleProfileParameters(Map<Long, SynthRow> allRows, List<Integer> profilePriority,
    Collection<String> stationParameters, Predicate<String> parameterFilter) {
    List<SynthRow> rows = new ArrayList<>(allRows.values());
    List<Plan> plans = new ArrayList<>();
    for (String parameterName : stationParameters) {
      if (!parameterFilter.test(parameterName)) {
        continue;
      }
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
          Plan plan = new Plan(parameterName, profileIndex, new ArrayList<>(0));
          if (overallMin == null) {
            overallMin = min;
            overallMax = max;

            MinMax minMax = new MinMax(min, max + 1);
            plan.addSection(minMax);
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
              plan.addSections(minMaxs);
              plans.add(plan);
            }
          }

        }

      }
    }

    Map<Long, SynthRow> result = new TreeMap<>();
    for (Plan plan : plans) {
      String parameterName = plan.parameterName;
      for (MinMax minMax : plan.sections) {
        for (int i = minMax.min(); i < minMax.max(); i++) {
          SynthRow row = rows.get(i);
          SynthProfile profile = row.getProfiles().get(plan.profileIndex);
          if (profile != null) {
            profile.getParameter(parameterName)
              .ifPresent(parameter -> {
                SynthProfile copyProfile = new SynthProfile(profile.index(), HashMap.newHashMap(0), new ArrayList<>(0));
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
            MergeConstants.GOOD_QC_8.contains(qc) ? "8" : qc,
            resolvePressureDisplacement(pressure, parameterName, rows),
            floatQc.adjustedValue(),
            floatQc.adjustedError(),
            MergeConstants.GOOD_QC_8.contains(adjustedQc) ? "8" : adjustedQc
          );
        }
        result.put(pressure, valueToSet);
      }
    }

    for (Long normalizedPressure : resultPressures) {
      result.putIfAbsent(normalizedPressure, null);
    }

    return result;

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

      if (MergeConstants.GOOD_QC.contains(maxQc)) {
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

  private static Entry<Long, Knot> getClosesNonNullKnot(Long normalizedPressure, LongFunction<Entry<Long, Knot>> mapOperation, Function<Knot, ?> getValue) {
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
  }

  private static Pair<Float, String> interpolateValue(Long normalizedPressure, TreeMap<Long, Knot> knots, QcStrategy qcStrategy, Function<Knot, Float> getValue, Function<Knot, String> getQc, boolean adjustedParameter) {
    Knot knotAtPressure = knots.get(normalizedPressure);
    if (knotAtPressure != null) {
      return Pair.of(getValue.apply(knotAtPressure), getQc.apply(knotAtPressure));
    }

    Entry<Long, Knot> floorEntry = getClosesNonNullKnot(normalizedPressure, knots::floorEntry, adjustedParameter ? getQc : getValue);
    Entry<Long, Knot> ceilingEntry = getClosesNonNullKnot(normalizedPressure, knots::ceilingEntry, adjustedParameter ? getQc : getValue);

    BiFunction<Entry<Long, Knot>, Long, Pair<Float, String>> valueConsumer = (entry, threshold) -> {
      Long pressure = entry.getKey();
      if (Math.abs(normalizedPressure - pressure) <= threshold) {
        Knot knot = entry.getValue();

        return Pair.of(getValue.apply(knot), qcStrategy.getQc(null, getQc.apply(knot)));
      }

      return null;
    };

    Pair<Float, String> result = null;

    if (ceilingEntry == null && floorEntry != null) { // known deeper value -> extrapolate downward
      result = valueConsumer.apply(floorEntry, 2000L);
    } else if (floorEntry == null && ceilingEntry != null) { // known shallower value -> extrapolate upward
      result = valueConsumer.apply(ceilingEntry, 1000L);
    } else if (floorEntry != null) { // known shallower and deeper value -> linear interpolation
      Knot floor = floorEntry.getValue();
      Knot ceiling = ceilingEntry.getValue();
      Float floorValue = getValue.apply(floor);
      Float ceilingValue = getValue.apply(ceiling);

      if (floorValue == null || ceilingValue == null) {
        if (floorValue == null && ceilingValue == null) {
          return null;
        }

        long floorDiffPressure = Math.abs(floorEntry.getKey() - normalizedPressure);
        long ceilingDiffPressure = Math.abs(ceilingEntry.getKey() - normalizedPressure);

        int compare = Long.compare(floorDiffPressure, ceilingDiffPressure);
        if (compare == 0 || compare > 0) {
          result = Pair.of(ceilingValue, qcStrategy.getQc(getQc.apply(ceiling), getQc.apply(floor)));
        } else {
          result = Pair.of(floorValue, qcStrategy.getQc(getQc.apply(floor), getQc.apply(ceiling)));
        }
      } else {
        BigDecimal x0 = BigDecimal.valueOf(floorEntry.getKey());
        BigDecimal y0 = BigDecimal.valueOf(floorValue);
        String qc0 = getQc.apply(floor);

        BigDecimal x1 = BigDecimal.valueOf(ceilingEntry.getKey());
        BigDecimal y1 = BigDecimal.valueOf(ceilingValue);
        String qc1 = getQc.apply(ceiling);

        float v = y0.add(
          BigDecimal.valueOf(normalizedPressure).subtract(x0)
            .multiply(y1.subtract(y0))
            .divide(x1.subtract(x0), RoundingMode.FLOOR)
        ).floatValue();

        result = Pair.of(v, qcStrategy.getQc(qc0, qc1));
      }
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
        Pair<Float, String> adjustedValues = interpolateValue(normalizedPressure, adjustedKnots, qcStrategy, Knot::adjustedValue, Knot::adjustedQc, true);
        if (adjustedValues != null) {
          adjustedQc = adjustedValues.getRight();
          adjustedValue = adjustedValues.getLeft();
        }

        if (adjustedError == null && hasAdjustedError) {
          Pair<Float, String> adjustedErrorValues = interpolateValue(normalizedPressure, adjustedKnots, qcStrategy, Knot::adjustedError, Knot::adjustedQc, true);
          if (adjustedErrorValues != null) {
            adjustedError = adjustedErrorValues.getLeft();
          }
        }
      }
      return new FloatQc(knot.y(), knot.qc(), 0F, adjustedValue, adjustedError, adjustedQc);
    }

    Pair<Float, String> values = interpolateValue(normalizedPressure, knots, qcStrategy, Knot::y, Knot::qc, false);
    if (values != null) {
      Pair<Float, String> adjustedValues = interpolateValue(normalizedPressure, adjustedKnots, qcStrategy, Knot::adjustedValue, Knot::adjustedQc, true);
      String adjustedQc = adjustedKnots.isEmpty() ? null : "4";
      Float adjustedValue = null;
      Float adjustedError = null;
      if (adjustedValues != null) {
        adjustedQc = adjustedValues.getRight();
        adjustedValue = adjustedValues.getLeft();

        if (hasAdjustedError) {
          Pair<Float, String> adjustedErrorValues = interpolateValue(normalizedPressure, adjustedKnots, qcStrategy, Knot::adjustedError, Knot::adjustedQc, true);
          if (adjustedErrorValues != null) {
            adjustedError = adjustedErrorValues.getLeft();
          }
        }
      }
      return new FloatQc(values.getLeft(), values.getRight(), resolvePressureDisplacement(normalizedPressure, parameterName, singleRows), adjustedValue, adjustedError, adjustedQc);
    }

    return null;
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

  private static Map<Long, SynthRow> populateCProfileRows(List<ArgoProfileV31> cProfiles, Map<Integer, Set<Integer>> validIndexesForProfile) {
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
          (parameter, level) -> level.getValue() != null && MergeConstants.CoreParameterFilter.isSupportedParameter(parameter, level),
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
          (parameter, level) -> level.getValue() != null && MergeConstants.BioParameterFilter.isSupportedParameter(parameter),
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
    BiPredicate<ArgoProfileV31Parameter, ArgoProfileV31Level> parameterFilter,
    ParameterCreateVisitor onParameterCreate,
    BiFunction<BigDecimal, String, BigDecimal> addOffset
  ) {
    Float actualPressure = pressureLevel.getValue();
    if (actualPressure == null) {
      return;
    }

    int profileIndex = profile.getProfileIndex();
    int levelIndex = pressureLevel.getLevelIndex();
    for (ArgoProfileV31Parameter parameter : profile.getParameters()) {
      ArgoProfileV31Level level = parameter.getLevels().get(levelIndex);
      if (parameterFilter.test(parameter, level)) {
        // need to use BigDecimal math to prevent rounding errors causing strange errors with keys
        long normalizedPressure = addOffset.apply(new BigDecimal(Float.toString(actualPressure)), parameter.getParameterName())
          .multiply(MergeConstants.DBAR_CONVERSION).longValue();

        SynthRow row = rows.computeIfAbsent(normalizedPressure, pres -> {
          SynthRow r = new SynthRow();
          r.setPressure(pres);
          return r;
        });

        List<SynthProfile> rowProfiles = row.getProfiles();
        if (rowProfiles.isEmpty()) {
          for (int i = 0; i < numProfiles; i++) {
            rowProfiles.add(null);
          }
        }

        SynthProfile rowProfile = rowProfiles.get(profileIndex);
        if (rowProfile == null) {
          rowProfile = new SynthProfile(profileIndex, HashMap.newHashMap(0), profile.getStationParameters());
          rowProfiles.set(profileIndex, rowProfile);
        }

        String parameterName = parameter.getParameterName();

        SynthParameter rowParameter = new SynthParameter(parameterName, level.getValue(), null, level.getQc(), level.getAdjustedValue(), level.getAdjustedErrorValue(), level.getAdjustedQc());

        onParameterCreate.onParameterCreate(row, rowProfile, rowParameter);

        rowProfile.putParameter(rowParameter);
      }
    }
  }

  @FunctionalInterface
  private interface ParameterCreateVisitor {

    void onParameterCreate(SynthRow row, SynthProfile profile, SynthParameter parameter);
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

  record SynthParameter(String parameterName, Float value, Long pDiff, String qc, Float adjustedValue, Float adjustedError, String adjustedQc) {
    public SynthParameter withPDiff(Long pDiff) {
      return new SynthParameter(parameterName, value, pDiff, qc, adjustedValue, adjustedError, adjustedQc);
    }
  }

  record SynthProfile(int index, Map<String, SynthParameter> parameters, List<String> supportedParameters) {
    public void putParameter(SynthParameter parameter) {
      parameters.put(parameter.parameterName(), parameter);
    }

    public Optional<SynthParameter> getParameter(String parameterName) {
      return Optional.ofNullable(parameters.get(parameterName));
    }
  }

  static class SynthRow {

    long pressure;
    List<SynthProfile> profiles = new ArrayList<>();

    public long getPressure() {
      return pressure;
    }

    public void setPressure(long pressure) {
      this.pressure = pressure;
    }

    public List<SynthProfile> getProfiles() {
      return profiles;
    }

  }

  static final class PressureIndexer {

    private PressureIndexer() {

    }

    public static class QcIndexMap {
      private final Map<Integer, Set<Integer>> validIndexes;
      private final String pressureQc;

      public QcIndexMap(Map<Integer, Set<Integer>> validIndexes, String pressureQc) {
        this.validIndexes = validIndexes;
        this.pressureQc = pressureQc;
      }

      public Map<Integer, Set<Integer>> getValidIndexes() {
        return validIndexes;
      }

      public String getPressureQc() {
        return pressureQc;
      }
    }

    public static QcIndexMap getQcValidatedPressureIndexes(List<ArgoProfileV31> cProfiles) {
      //TODO look up vertical offset

      String pressureQc = "1";

      Map<Integer, Set<Integer>> validIndexesForProfile = new TreeMap<>();
      // only core profiles have QC
      for (ArgoProfileV31 cProfile : cProfiles) {
        int profileIndex = cProfile.getProfileIndex();
        Set<Integer> validIndexes = new TreeSet<>();
        validIndexesForProfile.put(profileIndex, validIndexes);
        ArgoProfileV31Parameter pressure = cProfile.getParameter("PRES");
        for (int pressureIndex = 0; pressureIndex < pressure.getLevels().size(); pressureIndex++) {
          ArgoProfileV31Level level = pressure.getLevels().get(pressureIndex);
          String qc = level.getQc();
          if (VALID_QC.contains(qc)) {
            pressureQc = getHighestQcOrder(pressureQc, qc);
            validIndexes.add(pressureIndex);
          }
        }
      }
      return new QcIndexMap(validIndexesForProfile, pressureQc);
    }

    private static final List<String> VALID_QC = Arrays.asList("0", "1", "2", "3");

    private static final List<String> QC_ORDER = Arrays.asList("1", "2", "5", "3", "4", "0");

    public static String getHighestQcOrder(String qc1, String qc2) {
      int qc1Index = QC_ORDER.indexOf(qc1);
      int qc2Index = QC_ORDER.indexOf(qc2);
      if (qc1Index > qc2Index) {
        return qc1;
      }
      return qc2;
    }
  }
}
