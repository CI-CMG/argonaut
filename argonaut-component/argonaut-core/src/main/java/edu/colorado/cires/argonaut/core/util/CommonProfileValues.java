package edu.colorado.cires.argonaut.core.util;

import java.time.Instant;

public interface CommonProfileValues {

  String getPlatformNumber();

  String getProjectName();

  String getPrincipalInvestigatorName();

  int getCycleNumber();

  String getDirection();

  String getDataCenter();

  String getDataCenterReference();

  String getPlatformType();

  String getFloatSerialNumber();

  String getFirmwareVersion();

  String getWmoInstrumentType();

  String getDataMode();

  Instant getJulianDate();

  String getJulianDateQc();

  Instant getJulianDateOfLocation();

  Double getLatitude();

  Double getLongitude();

  String getPositionQc();

  String getPositioningSystem();

  Integer getConfigMissionNumber();

  String getVerticalSamplingScheme();

  String getDataStateIndicator();

}
