package edu.colorado.cires.argonaut.core.netcdf.metadata.v31.impl;

import edu.colorado.cires.argonaut.core.netcdf.metadata.v31.ArgoMetadataV31;
import edu.colorado.cires.argonaut.core.netcdf.metadata.v31.ArgoMetadataV31ControllerBoard;
import edu.colorado.cires.argonaut.core.netcdf.metadata.v31.ArgoMetadataV31TelecommunicationSystem;
import edu.colorado.cires.argonaut.core.util.NetCdfReadUtils;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.commons.lang3.tuple.Pair;
import org.jspecify.annotations.Nullable;
import ucar.nc2.NetcdfFile;

public class NetCdfTiedArgoMetadataV31 implements ArgoMetadataV31 {

  private static final Map<String, List<String>> SHORT_NAME_TO_SENSORS = new HashMap<>();

  static {
    SHORT_NAME_TO_SENSORS.put("ctd", List.of(
      "CTD_PRES", "CTD_TEMP", "CTD_CNDC", "CTD_PSAL"));

    SHORT_NAME_TO_SENSORS.put("optode", List.of(
      "OPTODE_DOXY"));

    SHORT_NAME_TO_SENSORS.put("ocr", List.of(
      "RADIOMETER_DOWN_IRR", "RADIOMETER_PAR",
        "RADIOMETER_DOWN_IRR380", "RADIOMETER_DOWN_IRR412", "RADIOMETER_DOWN_IRR490")
      ); // Truncated <nnn> suffix

    SHORT_NAME_TO_SENSORS.put("eco", List.of(
      "FLUOROMETER_CHLA", "FLUOROMETER_CDOM", "BACKSCATTERINGMETER_BBP", "BACKSCATTERINGMETER_BBP700",
      "BACKSCATTERINGMETER_BBP532"));

    SHORT_NAME_TO_SENSORS.put("mcoms", List.of(
      "FLUOROMETER_CHLA", "FLUOROMETER_CDOM", "BACKSCATTERINGMETER_BBP"));

    SHORT_NAME_TO_SENSORS.put("flntu", List.of(
      "FLUOROMETER_CHLA", "BACKSCATTERINGMETER_TURBIDITY"));

    SHORT_NAME_TO_SENSORS.put("crover", List.of(
      "TRANSMISSOMETER_CP"));

    SHORT_NAME_TO_SENSORS.put("suna", List.of(
      "SPECTROPHOTOMETER_NITRATE", "SPECTROPHOTOMETER_BISULFIDE"));

    SHORT_NAME_TO_SENSORS.put("isus", List.of(
      "SPECTROPHOTOMETER_NITRATE"));
  }

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
  private final Double launchLatitude;
  private final Double launchLongitude;
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
  private final Map<String, Double> pressureOffset = new HashMap<>();

  public NetCdfTiedArgoMetadataV31(NetcdfFile netcdf) {
    this.netcdf = netcdf;
    title = NetCdfReadUtils.getGlobalAttributeString(netcdf, "title");
    institution = NetCdfReadUtils.getGlobalAttributeString(netcdf, "institution");
    source = NetCdfReadUtils.getGlobalAttributeString(netcdf, "source");
    history = NetCdfReadUtils.getGlobalAttributeString(netcdf, "history");
    references = NetCdfReadUtils.getGlobalAttributeString(netcdf, "references");
    comment = NetCdfReadUtils.getGlobalAttributeString(netcdf, "comment");
    userManualVersion = NetCdfReadUtils.getGlobalAttributeString(netcdf, "user_manual_version");
    conventions = NetCdfReadUtils.getGlobalAttributeString(netcdf, "Conventions");
    dataType = NetCdfReadUtils.getString(netcdf, "DATA_TYPE");
    formatVersion = NetCdfReadUtils.getString(netcdf, "FORMAT_VERSION");
    handbookVersion = NetCdfReadUtils.getString(netcdf, "HANDBOOK_VERSION");
    dateCreation = NetCdfReadUtils.getInstant(netcdf, "DATE_CREATION");
    dateUpdate = NetCdfReadUtils.getInstant(netcdf, "DATE_UPDATE");
    platformNumber = NetCdfReadUtils.getString(netcdf, "PLATFORM_NUMBER");
    projectName = NetCdfReadUtils.getString(netcdf, "PROJECT_NAME");
    principalInvestigatorName = NetCdfReadUtils.getString(netcdf, "PI_NAME");
    dataCenter = NetCdfReadUtils.getString(netcdf, "DATA_CENTRE");
    platformType = NetCdfReadUtils.getString(netcdf, "PLATFORM_TYPE");
    floatSerialNumber = NetCdfReadUtils.getString(netcdf, "FLOAT_SERIAL_NO");
    firmwareVersion = NetCdfReadUtils.getString(netcdf, "FIRMWARE_VERSION");
    wmoInstrumentType = NetCdfReadUtils.getString(netcdf, "WMO_INST_TYPE");
//    positioningSystem = NetCdfUtils.getString(netcdf, "POSITIONING_SYSTEM");
    platformWigosId = NetCdfReadUtils.getString(netcdf, "PLATFORM_WIGOS_ID");
    ptt = NetCdfReadUtils.getString(netcdf, "PTT");
//    transSystem = NetCdfUtils.getString(netcdf, "TRANS_SYSTEM");
//    transSystemId = NetCdfUtils.getString(netcdf, "TRANS_SYSTEM_ID");
//    transFrequency = NetCdfUtils.getString(netcdf, "TRANS_FREQUENCY");
    platformFamily = NetCdfReadUtils.getString(netcdf, "PLATFORM_FAMILY");
    platformMaker = NetCdfReadUtils.getString(netcdf, "PLATFORM_MAKER");
    manualVersion = NetCdfReadUtils.getString(netcdf, "MANUAL_VERSION");
    standardFormatId = NetCdfReadUtils.getString(netcdf, "STANDARD_FORMAT_ID");
    dacFormatId = NetCdfReadUtils.getString(netcdf, "DAC_FORMAT_ID");
    programName = NetCdfReadUtils.getString(netcdf, "PROGRAM_NAME");
    anomaly = NetCdfReadUtils.getString(netcdf, "ANOMALY");
    batteryType = NetCdfReadUtils.getString(netcdf, "BATTERY_TYPE");
    batteryPacks = NetCdfReadUtils.getString(netcdf, "BATTERY_PACKS");
    specialFeatures = NetCdfReadUtils.getString(netcdf, "SPECIAL_FEATURES");
    floatOwner = NetCdfReadUtils.getString(netcdf, "FLOAT_OWNER");
    operatingInstitution = NetCdfReadUtils.getString(netcdf, "OPERATING_INSTITUTION");
    customization = NetCdfReadUtils.getString(netcdf, "CUSTOMISATION");
    launchDate = NetCdfReadUtils.getInstant(netcdf, "LAUNCH_DATE");
    launchLatitude = NetCdfReadUtils.getDouble(netcdf, "LAUNCH_LATITUDE");
    launchLongitude = NetCdfReadUtils.getDouble(netcdf, "LAUNCH_LONGITUDE");
    launchQc = NetCdfReadUtils.getString(netcdf, "LAUNCH_QC");
    startDate = NetCdfReadUtils.getInstant(netcdf, "START_DATE");
    startDateQc = NetCdfReadUtils.getString(netcdf, "START_DATE_QC");
    startupDate = NetCdfReadUtils.getInstant(netcdf, "STARTUP_DATE");
    startupDateQc = NetCdfReadUtils.getString(netcdf, "STARTUP_DATE_QC");
    deploymentPlatform = NetCdfReadUtils.getString(netcdf, "DEPLOYMENT_PLATFORM");
    deploymentCruiseId = NetCdfReadUtils.getString(netcdf, "DEPLOYMENT_CRUISE_ID");
    deploymentReferenceStationId = NetCdfReadUtils.getString(netcdf, "DEPLOYMENT_REFERENCE_STATION_ID");
    endMissionDate = NetCdfReadUtils.getInstant(netcdf, "END_MISSION_DATE");
    endMissionStatus = NetCdfReadUtils.getString(netcdf, "END_MISSION_STATUS");
    parameterSensors = NetCdfReadUtils.getListOfString(netcdf, "PARAMETER_SENSOR");
    parameters = NetCdfReadUtils.getListOfString(netcdf, "PARAMETER");
    computePressureOffsetValues(netcdf);

    primaryControllerBoard = null;
    secondaryControllerBoard = null;
  }

  private void computePressureOffsetValues(NetcdfFile netcdf) {
    Optional.ofNullable(NetCdfReadUtils.getListOfString(netcdf, "LAUNCH_CONFIG_PARAMETER_NAME"))
      .flatMap(names -> NetCdfReadUtils.read1DDoubleArray("LAUNCH_CONFIG_PARAMETER_VALUE", netcdf)
          .map(values -> Pair.of(names, values))
      ).ifPresent(pair -> populatePressureOffsets(pair.getLeft(), pair.getRight(), pressureOffset));
  }

  private void populatePressureOffsets(List<String> names, double[] parameterValues, Map<String, Double> pressureOffset) {
    Map<String, List<String>> sensorToParameter = IntStream.range(0, parameters.size()).boxed()
      .map(i -> Pair.of(parameterSensors.get(i), parameters.get(i)))
      .collect(Collectors.groupingBy(Pair::getLeft, Collectors.mapping(Pair::getRight, Collectors.toList())));

    for (int i = 0; i < names.size(); i++) {
      String parameterName = names.get(i);
      if (parameterName.contains("VerticalPressureOffset_dbar")) {
        String sensorShortName = parameterName
          .replace("CONFIG_", "")
          .replaceAll("(?:_?\\d+)?VerticalPressureOffset_dbar", "")
          .toLowerCase();

        double value = parameterValues[i];

        Optional.ofNullable(SHORT_NAME_TO_SENSORS.get(sensorShortName))
          .map(sensors -> sensors.stream()
            .map(sensorToParameter::get)
            .filter(Objects::nonNull)
            .flatMap(List::stream)
          )
          .ifPresent(parameterNames -> parameterNames.forEach(name -> pressureOffset.put(name, value)));
      }
    }
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
  public Double getLaunchLatitude() {
    return launchLatitude;
  }

  @Override
  public Double getLaunchLongitude() {
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

  @Override
  @Nullable
  public Double getPressureOffset(String parameterName) {
    return pressureOffset.get(parameterName);
  }
}
