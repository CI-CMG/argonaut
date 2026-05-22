package edu.colorado.cires.argonaut.core.netcdf.profile.v31.impl;

import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoProfileV31Level;

public class ArgoProfileV31LevelBean implements ArgoProfileV31Level {

  private final ArgoProfileV31ParameterBean parent;
  private final int levelIndex;
  private Float value;
  private Float adjustedValue;
  private String adjustedQc;
  private Float adjustedErrorValue;
  private String qc;

  public ArgoProfileV31LevelBean(ArgoProfileV31ParameterBean parent, int levelIndex) {
    this.parent = parent;
    this.levelIndex = levelIndex;
  }

  @Override
  public int getProfileIndex() {
    return parent.getProfileIndex();
  }

  @Override
  public int getLevelIndex() {
    return levelIndex;
  }

  @Override
  public String getParameterName() {
    return parent.getParameterName();
  }

  @Override
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
  public Float getAdjustedErrorValue() {
    return adjustedErrorValue;
  }

  public void setAdjustedErrorValue(Float adjustedErrorValue) {
    this.adjustedErrorValue = adjustedErrorValue;
  }

  @Override
  public String getQc() {
    return qc;
  }

  public void setQc(String qc) {
    this.qc = qc;
  }
}
