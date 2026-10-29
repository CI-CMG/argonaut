package edu.colorado.cires.argonaut.core.netcdf.synthprofile.v13.impl;

import edu.colorado.cires.argonaut.core.netcdf.synthprofile.v13.ArgoSyntheticProfileV13Calibration;
import edu.colorado.cires.argonaut.core.util.NetCdfReadUtils;
import java.time.Instant;
import java.util.Optional;

public class NetCdfTiedArgoSyntheticProfileV13Calibration implements ArgoSyntheticProfileV13Calibration {

  private final NetCdfTiedArgoSyntheticProfileV13Parameter parent;
  private final int calibrationIndex;
  private final String equation;
  private final String coefficient;
  private final String comment;
  private final Instant date;

  public NetCdfTiedArgoSyntheticProfileV13Calibration(NetCdfTiedArgoSyntheticProfileV13Parameter parent, int calibrationIndex) {
    this.parent = parent;
    this.calibrationIndex = calibrationIndex;
    equation = valueOrDefaultString(NetCdfReadUtils.getLevel3String(parent.getNetcdf(), parent.getProfileIndex(), calibrationIndex, parent.getParamIndex(),
        "SCIENTIFIC_CALIB_EQUATION"));
    coefficient = valueOrDefaultString(NetCdfReadUtils.getLevel3String(parent.getNetcdf(), parent.getProfileIndex(), calibrationIndex, parent.getParamIndex(),
        "SCIENTIFIC_CALIB_COEFFICIENT"));
    comment = valueOrDefaultString(NetCdfReadUtils.getLevel3String(parent.getNetcdf(), parent.getProfileIndex(), calibrationIndex, parent.getParamIndex(),
        "SCIENTIFIC_CALIB_COMMENT"));
    date = NetCdfReadUtils.getLevel3Instant(parent.getNetcdf(), parent.getProfileIndex(), calibrationIndex, parent.getParamIndex(),
        "SCIENTIFIC_CALIB_DATE");
  }

  private static String valueOrDefaultString(String value) {
    return value == null ? "none" : value;
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
