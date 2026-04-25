package edu.colorado.cires.argonaut.core.netcdf.synthprofile.v13;

import java.time.Instant;

public interface ArgoSyntheticProfileV13Calibration {

  String getParameterName();

  String getEquation();

  String getCoefficient();

  String getComment();

  Instant getDate();

  int getProfileIndex();

  int getCalibrationIndex();

}
