package edu.colorado.cires.argonaut.core.netcdf.synthprofile.v13;

import edu.colorado.cires.argonaut.core.util.CommonParameterValues;
import java.util.List;

public interface ArgoSyntheticProfileV13Parameter extends CommonParameterValues {

  String getParameterName();

  List<ArgoSyntheticProfileV13Level> getLevels();

  List<ArgoSyntheticProfileV13Calibration> getCalibrations();


}
