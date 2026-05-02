package edu.colorado.cires.argonaut.core.netcdf.synthprofile.v13;

import java.time.Instant;
import java.util.List;

public interface ArgoSyntheticProfileV13 {

  String getTitle();

  String getInstitution();

  String getSource();

  String getHistory();

  String getReferences();

  String getId();

  String getComment();

  String getUserManualVersion();

  String getConventions();

  String getFeatureType();

  String getDataType();

  String getFormatVersion();

  String getHandbookVersion();

  Instant getReferenceDateTime();

  Instant getDateCreation();

  Instant getDateUpdate();

  String getPlatformNumber();

  String getProjectName();

  String getPrincipalInvestigatorName();

  List<String> getStationParameters();

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

  List<ArgoSyntheticProfileV13Parameter> getParameters();

  String getSoftwareVersion();
}
