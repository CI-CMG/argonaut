package edu.colorado.cires.argonaut.core.netcdf.profile.v31.impl;

import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoProfileV31Calibration;
import java.time.Instant;

public class ArgoProfileV31CalibrationBean implements ArgoProfileV31Calibration {

  private final ArgoProfileV31ParameterBean parent;
  private final int calibrationIndex;
  private String equation;
  private String coefficient;
  private String comment;
  private Instant date;

  public ArgoProfileV31CalibrationBean(ArgoProfileV31ParameterBean parent, int calibrationIndex) {
    this.parent = parent;
    this.calibrationIndex = calibrationIndex;
  }

  @Override
  public int getProfileIndex() {
    return parent.getProfileIndex();
  }

  @Override
  public int getCalibrationIndex() {
    return calibrationIndex;
  }

  @Override
  public String getParameterName() {
    return parent.getParameterName();
  }

  @Override
  public String getEquation() {
    return equation;
  }

  public void setEquation(String equation) {
    this.equation = equation;
  }

  @Override
  public String getCoefficient() {
    return coefficient;
  }

  public void setCoefficient(String coefficient) {
    this.coefficient = coefficient;
  }

  @Override
  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  @Override
  public Instant getDate() {
    return date;
  }

  public void setDate(Instant date) {
    this.date = date;
  }
}
