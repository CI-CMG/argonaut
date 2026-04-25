package edu.colorado.cires.argonaut.core.merge.synthetic;

import java.util.ArrayList;
import java.util.List;

class SynthRow {

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
