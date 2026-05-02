package edu.colorado.cires.argonaut.core.netcdf.metadata.v31.impl;

import edu.colorado.cires.argonaut.core.netcdf.metadata.v31.ArgoMetadataV31;
import edu.colorado.cires.argonaut.core.netcdf.metadata.v31.ArgoMetadataV31ControllerBoard;
import edu.colorado.cires.argonaut.core.netcdf.metadata.v31.ArgoMetadataV31TelecommunicationSystem;
import edu.colorado.cires.argonaut.core.util.NetCdfUtils;
import java.time.Instant;
import java.util.List;
import ucar.nc2.NetcdfFile;

public class NetCdfTiedArgoMetadataV31 implements ArgoMetadataV31 {

  private final NetcdfFile netcdf;
  private final String title;
  private final String institution;
  private final String source;
  private final String history;
  private final String references;
  private final String comment;
  private final String userManualVersion;
  private final String conventions;
  private final String dataType;
  private final String formatVersion;
  private final String handbookVersion;
  private final Instant dateCreation;
  private final Instant dateUpdate;
  private final String platformNumber;
  private final String platformWigosId;
  private final String ptt;
//  private final List<String> positioningSystems;
  private final String platformFamily;
  private final String platformType;
  private final String platformMaker;
  private final String firmwareVersion;
  private final String manualVersion;
  private final String floatSerialNumber;
  private final String standardFormatId;
  private final String dacFormatId;
  private final String wmoInstrumentType;
  private final String projectName;
  private final String programName;
  private final String dataCenter;
  private final String principalInvestigatorName;
  private final String anomaly;
  private final String batteryType;
  private final String batteryPacks;
  private final ArgoMetadataV31ControllerBoard primaryControllerBoard;
  private final ArgoMetadataV31ControllerBoard secondaryControllerBoard;
  private final String specialFeatures;
  private final String floatOwner;
  private final String operatingInstitution;
  private final String customization;
  private final Instant launchDate;
  private final double launchLatitude;
  private final double launchLongitude;
  private final String launchQc;
  private final Instant startDate;
  private final String startDateQc;
  private final Instant startupDate;
  private final String startupDateQc;
  private final String deploymentPlatform;
  private final String deploymentCruiseId;
  private final String deploymentReferenceStationId;
  private final Instant endMissionDate;
  private final String endMissionStatus;
  private final List<String> parameterSensors;
  private final List<String> parameters;

  public NetCdfTiedArgoMetadataV31(NetcdfFile netcdf) {
    this.netcdf = netcdf;
    title = NetCdfUtils.getGlobalAttributeString(netcdf, "title");
    institution = NetCdfUtils.getGlobalAttributeString(netcdf, "institution");
    source = NetCdfUtils.getGlobalAttributeString(netcdf, "source");
    history = NetCdfUtils.getGlobalAttributeString(netcdf, "history");
    references = NetCdfUtils.getGlobalAttributeString(netcdf, "references");
    comment = NetCdfUtils.getGlobalAttributeString(netcdf, "comment");
    userManualVersion = NetCdfUtils.getGlobalAttributeString(netcdf, "user_manual_version");
    conventions = NetCdfUtils.getGlobalAttributeString(netcdf, "Conventions");
    dataType = NetCdfUtils.getString(netcdf, "DATA_TYPE");
    formatVersion = NetCdfUtils.getString(netcdf, "FORMAT_VERSION");
    handbookVersion = NetCdfUtils.getString(netcdf, "HANDBOOK_VERSION");
    dateCreation = NetCdfUtils.getInstant(netcdf, "DATE_CREATION");
    dateUpdate = NetCdfUtils.getInstant(netcdf, "DATE_UPDATE");
    platformNumber = NetCdfUtils.getString(netcdf, "PLATFORM_NUMBER");
    projectName = NetCdfUtils.getString(netcdf, "PROJECT_NAME");
    principalInvestigatorName = NetCdfUtils.getString(netcdf, "PI_NAME");
    dataCenter = NetCdfUtils.getString(netcdf, "DATA_CENTRE");
    platformType = NetCdfUtils.getString(netcdf, "PLATFORM_TYPE");
    floatSerialNumber = NetCdfUtils.getString(netcdf, "FLOAT_SERIAL_NO");
    firmwareVersion = NetCdfUtils.getString(netcdf, "FIRMWARE_VERSION");
    wmoInstrumentType = NetCdfUtils.getString(netcdf, "WMO_INST_TYPE");
//    positioningSystem = NetCdfUtils.getString(netcdf, "POSITIONING_SYSTEM");
    platformWigosId = NetCdfUtils.getString(netcdf, "PLATFORM_WIGOS_ID");
    ptt = NetCdfUtils.getString(netcdf, "PTT");
//    transSystem = NetCdfUtils.getString(netcdf, "TRANS_SYSTEM");
//    transSystemId = NetCdfUtils.getString(netcdf, "TRANS_SYSTEM_ID");
//    transFrequency = NetCdfUtils.getString(netcdf, "TRANS_FREQUENCY");
    platformFamily = NetCdfUtils.getString(netcdf, "PLATFORM_FAMILY");
    platformMaker = NetCdfUtils.getString(netcdf, "PLATFORM_MAKER");
    manualVersion = NetCdfUtils.getString(netcdf, "MANUAL_VERSION");
    standardFormatId = NetCdfUtils.getString(netcdf, "STANDARD_FORMAT_ID");
    dacFormatId = NetCdfUtils.getString(netcdf, "DAC_FORMAT_ID");
    programName = NetCdfUtils.getString(netcdf, "PROGRAM_NAME");
    anomaly = NetCdfUtils.getString(netcdf, "ANOMALY");
    batteryType = NetCdfUtils.getString(netcdf, "BATTERY_TYPE");
    batteryPacks = NetCdfUtils.getString(netcdf, "BATTERY_PACKS");
    specialFeatures = NetCdfUtils.getString(netcdf, "SPECIAL_FEATURES");
    floatOwner = NetCdfUtils.getString(netcdf, "FLOAT_OWNER");
    operatingInstitution = NetCdfUtils.getString(netcdf, "OPERATING_INSTITUTION");
    customization = NetCdfUtils.getString(netcdf, "CUSTOMISATION");
    launchDate = NetCdfUtils.getInstant(netcdf, "LAUNCH_DATE");
    launchLatitude = NetCdfUtils.getDouble(netcdf, "LAUNCH_LATITUDE");
    launchLongitude = NetCdfUtils.getDouble(netcdf, "LAUNCH_LONGITUDE");
    launchQc = NetCdfUtils.getString(netcdf, "LAUNCH_QC");
    startDate = NetCdfUtils.getInstant(netcdf, "START_DATE");
    startDateQc = NetCdfUtils.getString(netcdf, "START_DATE_QC");
    startupDate = NetCdfUtils.getInstant(netcdf, "STARTUP_DATE");
    startupDateQc = NetCdfUtils.getString(netcdf, "STARTUP_DATE_QC");
    deploymentPlatform = NetCdfUtils.getString(netcdf, "DEPLOYMENT_PLATFORM");
    deploymentCruiseId = NetCdfUtils.getString(netcdf, "DEPLOYMENT_CRUISE_ID");
    deploymentReferenceStationId = NetCdfUtils.getString(netcdf, "DEPLOYMENT_REFERENCE_STATION_ID");
    endMissionDate = NetCdfUtils.getInstant(netcdf, "END_MISSION_DATE");
    endMissionStatus = NetCdfUtils.getString(netcdf, "END_MISSION_STATUS");
    parameterSensors = NetCdfUtils.getListOfString(netcdf, "PARAMETER_SENSOR");
    parameters = NetCdfUtils.getListOfString(netcdf, "PARAMETER");

    primaryControllerBoard = null;
    secondaryControllerBoard = null;
  }

  @Override
  public String getTitle() {
    return title;
  }

  @Override
  public String getInstitution() {
    return institution;
  }

  @Override
  public String getSource() {
    return source;
  }

  @Override
  public String getHistory() {
    return history;
  }

  @Override
  public String getReferences() {
    return references;
  }

  @Override
  public String getComment() {
    return comment;
  }

  @Override
  public String getUserManualVersion() {
    return userManualVersion;
  }

  @Override
  public String getConventions() {
    return conventions;
  }

  @Override
  public String getDataType() {
    return dataType;
  }

  @Override
  public String getFormatVersion() {
    return formatVersion;
  }

  @Override
  public String getHandbookVersion() {
    return handbookVersion;
  }

  @Override
  public Instant getDateCreation() {
    return dateCreation;
  }

  @Override
  public Instant getDateUpdate() {
    return dateUpdate;
  }

  @Override
  public String getPlatformNumber() {
    return platformNumber;
  }

  @Override
  public String getPlatformWigosId() {
    return platformWigosId;
  }

  @Override
  public String getPtt() {
    return ptt;
  }

  @Override
  public List<ArgoMetadataV31TelecommunicationSystem> getTelecommunicationSystems() {
    return List.of();
  }


  @Override
  public List<String> getPositioningSystems() {
    return null;
  }

  @Override
  public String getPlatformFamily() {
    return platformFamily;
  }

  @Override
  public String getPlatformType() {
    return platformType;
  }

  @Override
  public String getPlatformMaker() {
    return platformMaker;
  }

  @Override
  public String getFirmwareVersion() {
    return firmwareVersion;
  }

  @Override
  public String getManualVersion() {
    return manualVersion;
  }

  @Override
  public String getFloatSerialNumber() {
    return floatSerialNumber;
  }

  @Override
  public String getStandardFormatId() {
    return standardFormatId;
  }

  @Override
  public String getDacFormatId() {
    return dacFormatId;
  }

  @Override
  public String getWmoInstrumentType() {
    return wmoInstrumentType;
  }

  @Override
  public String getProjectName() {
    return projectName;
  }

  @Override
  public String getProgramName() {
    return programName;
  }

  @Override
  public String getDataCenter() {
    return dataCenter;
  }

  @Override
  public String getPrincipalInvestigatorName() {
    return principalInvestigatorName;
  }

  @Override
  public String getAnomaly() {
    return anomaly;
  }

  @Override
  public String getBatteryType() {
    return batteryType;
  }

  @Override
  public String getBatteryPacks() {
    return batteryPacks;
  }

  @Override
  public ArgoMetadataV31ControllerBoard getPrimaryControllerBoard() {
    return primaryControllerBoard;
  }

  @Override
  public ArgoMetadataV31ControllerBoard getSecondaryControllerBoard() {
    return secondaryControllerBoard;
  }

  @Override
  public String getSpecialFeatures() {
    return specialFeatures;
  }

  @Override
  public String getFloatOwner() {
    return floatOwner;
  }

  @Override
  public String getOperatingInstitution() {
    return operatingInstitution;
  }

  @Override
  public String getCustomization() {
    return customization;
  }

  @Override
  public Instant getLaunchDate() {
    return launchDate;
  }

  @Override
  public double getLaunchLatitude() {
    return launchLatitude;
  }

  @Override
  public double getLaunchLongitude() {
    return launchLongitude;
  }

  @Override
  public String getLaunchQc() {
    return launchQc;
  }

  @Override
  public Instant getStartDate() {
    return startDate;
  }

  @Override
  public String getStartDateQc() {
    return startDateQc;
  }

  @Override
  public Instant getStartupDate() {
    return startupDate;
  }

  @Override
  public String getStartupDateQc() {
    return startupDateQc;
  }

  @Override
  public String getDeploymentPlatform() {
    return deploymentPlatform;
  }

  @Override
  public String getDeploymentCruiseId() {
    return deploymentCruiseId;
  }

  @Override
  public String getDeploymentReferenceStationId() {
    return deploymentReferenceStationId;
  }

  @Override
  public Instant getEndMissionDate() {
    return endMissionDate;
  }

  @Override
  public String getEndMissionStatus() {
    return endMissionStatus;
  }

  @Override
  public List<String> getParameterSensors() {
    return parameterSensors;
  }

  @Override
  public List<String> getParameters() {
    return parameters;
  }
}
