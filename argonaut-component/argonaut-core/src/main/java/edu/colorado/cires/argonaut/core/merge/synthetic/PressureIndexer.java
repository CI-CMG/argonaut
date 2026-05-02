package edu.colorado.cires.argonaut.core.merge.synthetic;

import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoProfileV31;
import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoProfileV31Level;
import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoProfileV31Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public final class PressureIndexer {

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
