package edu.colorado.cires.argonaut.core.util;

import java.time.Instant;

public interface CommonProfileValues {

  String getPlatformNumber();

  String getProjectName();

  String getPrincipalInvestigatorName();

  int getCycleNumber();

  String getDirection();

  String getDataCenter();

  String getPlatformType();

  String getFloatSerialNumber();

  String getFirmwareVersion();

  String getWmoInstrumentType();

  Instant getJulianDate();

  String getJulianDateQc();

  Instant getJulianDateOfLocation();

  double getLatitude();

  double getLongitude();

  String getPositionQc();

  String getPositioningSystem();

  int getConfigMissionNumber();

}
