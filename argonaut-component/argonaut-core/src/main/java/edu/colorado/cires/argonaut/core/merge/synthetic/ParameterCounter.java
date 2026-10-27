package edu.colorado.cires.argonaut.core.merge.synthetic;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParameterCounter {


  private final Map<String, List<Integer>> counter = new HashMap<>();

  public void updateCounter(int level, SynthRow row) {
    for (SynthProfile profile : row.getProfiles()) {
      if (profile != null) {
        for (SynthParameter parameter : profile.parameters().values()) {
          if (parameter.value() != null) {
            counter.computeIfAbsent("%d_%s".formatted(profile.index(), parameter.parameterName()), k -> new ArrayList<>()).add(level);
          }
        }
      }
    }
  }

  public int getMaxCount() {
    return counter.values().stream().map(List::size).max(Integer::compareTo).orElse(0);
  }

  public int getDeepestSecondObservation() {
    int target = 0;
    for (List<Integer> levels : counter.values()) {
      if (levels.size() > 1) {
        int check = levels.get(1);
        if(check > target){
          target = check;
        }
      }
    }
    return target;
  }

}
