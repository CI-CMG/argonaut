package edu.colorado.cires.argonaut.core.netcdf.synthprofile.v13.impl;

import edu.colorado.cires.argonaut.core.netcdf.synthprofile.v13.ArgoSyntheticProfileV13Calibration;
import java.time.Instant;

public class ArgoSyntheticProfileV13CalibrationBean implements ArgoSyntheticProfileV13Calibration {

  private final String parameterName;
  private final int profileIndex;
  private final int calibrationIndex;
  private final String equation;
  private final String coefficient;
  private final String comment;
  private final Instant date;

  public ArgoSyntheticProfileV13CalibrationBean(String parameterName, int profileIndex, int calibrationIndex,
    String equation, String coefficient, String comment, Instant date) {
    this.parameterName = parameterName;
    this.profileIndex = profileIndex;
    this.calibrationIndex = calibrationIndex;
    this.equation = equation;
    this.coefficient = coefficient;
    this.comment = comment;
    this.date = date;
  }

  @Override
  public String getParameterName() {
    return parameterName;
  }

  @Override
  public int getProfileIndex() {
    return profileIndex;
  }

  @Override
  public int getCalibrationIndex() {
    return calibrationIndex;
  }

  @Override
  public String getEquation() {
    return equation;
  }

  @Override
  public String getCoefficient() {
    return coefficient;
  }

  @Override
  public String getComment() {
    return comment;
  }

  @Override
  public Instant getDate() {
    return date;
  }
}
