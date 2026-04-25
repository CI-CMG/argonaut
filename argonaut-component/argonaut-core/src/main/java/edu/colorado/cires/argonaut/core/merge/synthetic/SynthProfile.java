package edu.colorado.cires.argonaut.core.merge.synthetic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

class SynthProfile {

  private int index;
  private Map<String, SynthParameter> parameters = new TreeMap<>();
  private List<String> supportedParameters = new ArrayList<>();

  public int getIndex() {
    return index;
  }

  public void setIndex(int index) {
    this.index = index;
  }

  public Map<String, SynthParameter> getParameters() {
    return parameters;
  }

  public List<String> getSupportedParameters() {
    return supportedParameters;
  }
}
