package edu.colorado.cires.argonaut.core.merge.synthetic;

class SynthParameter {

  String parameterName;
  Float value;
  Long pDiff;


  public String getParameterName() {
    return parameterName;
  }

  public void setParameterName(String parameterName) {
    this.parameterName = parameterName;
  }

  public Float getValue() {
    return value;
  }

  public void setValue(Float value) {
    this.value = value;
  }

  public Long getpDiff() {
    return pDiff;
  }

  public void setpDiff(Long pDiff) {
    this.pDiff = pDiff;
  }

}
