package edu.colorado.cires.argonaut.core.netcdf.v31;

import java.time.Instant;

public interface ArgoProfileV31Calibration {

  int getProfileIndex();

  int getCalibrationIndex();

  String getParameterName();

  String getEquation();

  String getCoefficient();

  String getComment();

  Instant getDate();

}
