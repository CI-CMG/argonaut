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

  public static Map<Integer, Set<Integer>> getQcValidatedPressureIndexes(List<ArgoProfileV31> cProfiles) {
    //TODO look up vertical offset


    Map<Integer, Set<Integer>> validIndexesForProfile = new TreeMap<>();
    // only core profiles have QC
    for (ArgoProfileV31 cProfile : cProfiles) {
      int profileIndex = cProfile.getProfileIndex();
      Set<Integer> validIndexes = new TreeSet<>();
      validIndexesForProfile.put(profileIndex, validIndexes);
      ArgoProfileV31Parameter pressure = cProfile.getParameter("PRES");
      for (int pressureIndex = 0; pressureIndex < pressure.getLevels().size(); pressureIndex++) {
        ArgoProfileV31Level level = pressure.getLevels().get(pressureIndex);
        if (VALID_QC.contains(level.getQc())) {
          validIndexes.add(pressureIndex);
        }
      }
    }
    return validIndexesForProfile;
  }

  private static final List<String> VALID_QC = Arrays.asList("0", "1", "2", "3");
}
