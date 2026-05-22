package edu.colorado.cires.argonaut.core.netcdf.profile.v31;

import edu.colorado.cires.argonaut.core.util.CommonCalibrationValues;
import java.time.Instant;

public interface ArgoProfileV31Calibration extends CommonCalibrationValues {

  int getProfileIndex();

  int getCalibrationIndex();

  String getParameterName();

  String getEquation();

  String getCoefficient();

  String getComment();

  Instant getDate();

}
