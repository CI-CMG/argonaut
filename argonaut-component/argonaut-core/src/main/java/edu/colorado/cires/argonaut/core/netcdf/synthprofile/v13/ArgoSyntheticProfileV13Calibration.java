package edu.colorado.cires.argonaut.core.netcdf.synthprofile.v13;

import edu.colorado.cires.argonaut.core.util.CommonCalibrationValues;

public interface ArgoSyntheticProfileV13Calibration extends CommonCalibrationValues {

  String getParameterName();

  int getProfileIndex();

  int getCalibrationIndex();

}
