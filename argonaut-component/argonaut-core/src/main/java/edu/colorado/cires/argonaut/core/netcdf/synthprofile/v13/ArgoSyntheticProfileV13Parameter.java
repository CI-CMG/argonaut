package edu.colorado.cires.argonaut.core.netcdf.synthprofile.v13;

import java.util.List;

public interface ArgoSyntheticProfileV13Parameter {

  String getParameterName();

  String getDataMode();

  String getQc();

  List<ArgoSyntheticProfileV13Level> getLevels();

  List<ArgoSyntheticProfileV13Calibration> getCalibrations();


}
