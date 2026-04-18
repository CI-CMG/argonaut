package edu.colorado.cires.argonaut.core.netcdf.v31.impl;

import edu.colorado.cires.argonaut.core.netcdf.v31.ArgoProfileV31Calibration;
import java.io.IOException;
import java.time.Instant;

public class NetCdfTiedArgoProfileV31Calibration implements ArgoProfileV31Calibration {

  private final NetCdfTiedArgoProfileV31Parameter parent;
  private final int calibrationIndex;
  private final String equation;
  private final String coefficient;
  private final String comment;
  private final Instant date;

  public NetCdfTiedArgoProfileV31Calibration(NetCdfTiedArgoProfileV31Parameter parent, int calibrationIndex) {
    this.parent = parent;
    this.calibrationIndex = calibrationIndex;
    try {
      equation = NetCdfUtils.getParameterCalibrationString(parent.getNetcdf(), parent.getProfileIndex(), parent.getParamIndex(), calibrationIndex, "SCIENTIFIC_CALIB_EQUATION");
      coefficient = NetCdfUtils.getParameterCalibrationString(parent.getNetcdf(), parent.getProfileIndex(), parent.getParamIndex(), calibrationIndex, "SCIENTIFIC_CALIB_COEFFICIENT");
      comment = NetCdfUtils.getParameterCalibrationString(parent.getNetcdf(), parent.getProfileIndex(), parent.getParamIndex(), calibrationIndex, "SCIENTIFIC_CALIB_COMMENT");
      date = NetCdfUtils.getParameterCalibrationInstant(parent.getNetcdf(), parent.getProfileIndex(), parent.getParamIndex(), calibrationIndex, "SCIENTIFIC_CALIB_DATE");
    } catch (IOException e) {
      throw new RuntimeException("Unable to initialize profile calibration data wrapper", e);
    }
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
