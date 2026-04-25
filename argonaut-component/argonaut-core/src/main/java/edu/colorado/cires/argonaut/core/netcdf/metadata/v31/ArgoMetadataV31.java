package edu.colorado.cires.argonaut.core.netcdf.metadata.v31;

import java.time.Instant;
import java.util.List;

public interface ArgoMetadataV31 {

  String getTitle();

  String getInstitution();

  String getSource();

  String getHistory();

  String getReferences();

  String getComment();

  String getUserManualVersion();

  String getConventions();

  String getDataType();

  String getFormatVersion();

  String getHandbookVersion();

  Instant getDateCreation();

  Instant getDateUpdate();

  String getPlatformNumber();

  String getPlatformWigosId();

  String getPtt();

  List<ArgoMetadataV31TelecommunicationSystem> getTelecommunicationSystems();

//  String getTransSystem();
//
//  String getTransSystemId();
//
//  String getTransFrequency();

  List<String> getPositioningSystems();

  String getPlatformFamily();

  String getPlatformType();

  String getPlatformMaker();

  String getFirmwareVersion();

  String getManualVersion();

  String getFloatSerialNumber();

  String getStandardFormatId();

  String getDacFormatId();

  String getWmoInstrumentType();

  String getProjectName();

  String getProgramName();

  String getDataCenter();

  String getPrincipalInvestigatorName();

  String getAnomaly();

  String getBatteryType();

  String getBatteryPacks();

  ArgoMetadataV31ControllerBoard getPrimaryControllerBoard();

  ArgoMetadataV31ControllerBoard getSecondaryControllerBoard();

  String getSpecialFeatures();

  String getFloatOwner();

  String getOperatingInstitution();

  String getCustomization();

  Instant getLaunchDate();

  double getLaunchLatitude();

  double getLaunchLongitude();

  String getLaunchQc();

  Instant getStartDate();

  String getStartDateQc();

  Instant getStartupDate();

  String getStartupDateQc();

  String getDeploymentPlatform();

  String getDeploymentCruiseId();

  String getDeploymentReferenceStationId();

  Instant getEndMissionDate();

  String getEndMissionStatus();

  List<String> getParameterSensors();

  List<String> getParameters();
}
