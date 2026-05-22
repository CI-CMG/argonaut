package edu.colorado.cires.argonaut.core.util;

import java.util.List;

public interface CommonParameterValues {

  String getDataMode();

  String getQc();

  List<? extends CommonLevelValues> getLevels();

  List<? extends CommonCalibrationValues> getCalibrations();

}
