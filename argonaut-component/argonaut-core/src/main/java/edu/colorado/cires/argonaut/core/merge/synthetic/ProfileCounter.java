package edu.colorado.cires.argonaut.core.merge.synthetic;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileCounter {


  private final Map<Integer, List<Integer>> counter;

  public ProfileCounter(int numberOfProfiles) {
    counter = new HashMap<>();
    for (int i = 0; i < numberOfProfiles; i++) {
      counter.put(i, new ArrayList<>());
    }
  }

  public void updateCounter(int level, SynthRow row) {
    for (SynthProfile profile : row.getProfiles()) {
      int profileIndex = profile.getIndex();
      int count = 0;
      for(SynthParameter parameter : profile.getParameters().values()){
        if (parameter.getValue() != null) {
          count++;
        }
      }
      if (count > 0) {
        counter.get(profileIndex).add(level);
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
