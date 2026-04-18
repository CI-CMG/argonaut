package edu.colorado.cires.argonaut.core.netcdf.profile.v31;

import java.time.Instant;

public interface ArgoProfileV31History {

  String getInstitution();

  String getStep();

  ArgoProfileV31HistorySoftware getSoftware();

  Instant getDate();

  String getAction();

  String getParameter();

  Float getStartPressure();

  Float getStopPressure();

  Float getPreviousValue();

  String getQcTest();
}
