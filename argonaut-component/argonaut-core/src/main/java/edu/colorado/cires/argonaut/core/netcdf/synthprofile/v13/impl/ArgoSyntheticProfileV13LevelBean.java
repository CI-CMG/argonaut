package edu.colorado.cires.argonaut.core.netcdf.synthprofile.v13.impl;

import edu.colorado.cires.argonaut.core.netcdf.synthprofile.v13.ArgoSyntheticProfileV13Level;

public class ArgoSyntheticProfileV13LevelBean  implements ArgoSyntheticProfileV13Level {

  Float originalValue;
  Float pressureDisplacement;
  String qc;
  Float value;
  Float adjustedValue;
  String adjustedQc;
  Float adjustedError;

  @Override
  public Float getOriginalValue() {
    return originalValue;
  }

  public void setOriginalValue(Float originalValue) {
    this.originalValue = originalValue;
  }

  @Override
  public Float getPressureDisplacement() {
    return pressureDisplacement;
  }

  public void setPressureDisplacement(Float pressureDisplacement) {
    this.pressureDisplacement = pressureDisplacement;
  }

  @Override
  public String getQc() {
    return qc;
  }

  public void setQc(String qc) {
    this.qc = qc;
  }

  public Float getValue() {
    return value;
  }

  public void setValue(Float value) {
    this.value = value;
  }

  @Override
  public Float getAdjustedValue() {
    return adjustedValue;
  }

  public void setAdjustedValue(Float adjustedValue) {
    this.adjustedValue = adjustedValue;
  }

  @Override
  public String getAdjustedQc() {
    return adjustedQc;
  }

  public void setAdjustedQc(String adjustedQc) {
    this.adjustedQc = adjustedQc;
  }

  @Override
  public Float getAdjustedError() {
    return adjustedError;
  }

  public void setAdjustedError(Float adjustedError) {
    this.adjustedError = adjustedError;
  }
}
