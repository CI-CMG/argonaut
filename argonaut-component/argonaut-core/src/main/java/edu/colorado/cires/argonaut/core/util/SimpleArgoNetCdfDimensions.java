package edu.colorado.cires.argonaut.core.util;

public class SimpleArgoNetCdfDimensions implements ArgoNetCdfDimensions {
  private int profiles;
  private int levels;
  private int histories;
  private int parameters;
  private int calibrations;

  @Override
  public int getProfiles() {
    return profiles;
  }

  public void setProfiles(int profiles) {
    this.profiles = profiles;
  }

  @Override
  public int getLevels() {
    return levels;
  }

  public void setLevels(int levels) {
    this.levels = levels;
  }

  @Override
  public int getHistories() {
    return histories;
  }

  public void setHistories(int histories) {
    this.histories = histories;
  }

  @Override
  public int getParameters() {
    return parameters;
  }

  public void setParameters(int parameters) {
    this.parameters = parameters;
  }

  @Override
  public int getCalibrations() {
    return calibrations;
  }

  public void setCalibrations(int calibrations) {
    this.calibrations = calibrations;
  }
}
