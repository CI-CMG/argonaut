package edu.colorado.cires.argonaut.core.util;

import java.time.Instant;

public interface CommonCalibrationValues {

  String getEquation();

  String getCoefficient();

  String getComment();

  Instant getDate();

}
