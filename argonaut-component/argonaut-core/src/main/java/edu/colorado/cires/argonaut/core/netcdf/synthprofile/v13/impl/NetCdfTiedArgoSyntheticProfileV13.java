package edu.colorado.cires.argonaut.core.netcdf.synthprofile.v13.impl;

import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoProfileDirection;
import edu.colorado.cires.argonaut.core.netcdf.profile.v31.impl.NetCdfTiedArgoProfileV31Parameter;
import edu.colorado.cires.argonaut.core.netcdf.synthprofile.v13.ArgoSyntheticProfileV13;
import edu.colorado.cires.argonaut.core.netcdf.synthprofile.v13.ArgoSyntheticProfileV13Parameter;
import edu.colorado.cires.argonaut.core.util.NetCdfUtils;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import ucar.nc2.NetcdfFile;

public class NetCdfTiedArgoSyntheticProfileV13 implements ArgoSyntheticProfileV13 {


  private final NetcdfFile netcdf;
  private final int profileIndex;
  private String title;
  private String institution;
  private String source;
  private String history;
  private String references;
  private String id;
  private String comment;
  private String userManualVersion;
  private String conventions;
  private String featureType;
  private String dataType;
  private String formatVersion;
  private String handbookVersion;
  private Instant referenceDateTime;
  private Instant dateCreation;
  private Instant dateUpdate;
  private String platformNumber;
  private String projectName;
  private String principalInvestigatorName;
  private List<String> stationParameters;
  private int cycleNumber;
  private ArgoProfileDirection direction;
  private String dataCenter;
  private String platformType;
  private String floatSerialNumber;
  private String firmwareVersion;
  private String wmoInstrumentType;
  private Instant julianDate;
  private String julianDateQc;
  private Instant julianDateOfLocation;
  private double latitude;
  private double longitude;
  private String positionQc;
  private String positioningSystem;
  private int configMissionNumber;
  private List<ArgoSyntheticProfileV13Parameter> parameters = new ArrayList<>();

  public NetCdfTiedArgoSyntheticProfileV13(NetcdfFile netcdf, int profileIndex) {
    this.netcdf = netcdf;
    this.profileIndex = profileIndex;
    platformNumber = NetCdfUtils.getLevel1String(netcdf, profileIndex, "PLATFORM_NUMBER");
    projectName = NetCdfUtils.getLevel1String(netcdf, profileIndex, "PROJECT_NAME");
    principalInvestigatorName = NetCdfUtils.getLevel1String(netcdf, profileIndex, "PI_NAME");
    stationParameters = NetCdfUtils.getLevel1ListOfString(netcdf, profileIndex, "STATION_PARAMETERS");
//    cycleNumber = NetCdfUtils.getLevel1Integer(netcdf, profileIndex, "CYCLE_NUMBER");
//    direction = ArgoProfileDirection.valueOf(NetCdfUtils.getLevel1String(netcdf, profileIndex, "DIRECTION"));
    dataCenter = NetCdfUtils.getLevel1String(netcdf, profileIndex, "DATA_CENTRE");
//    dataCenterReference = NetCdfUtils.getLevel1String(netcdf, profileIndex, "DC_REFERENCE");
//    dataStateIndicator = NetCdfUtils.getLevel1String(netcdf, profileIndex, "DATA_STATE_INDICATOR");
//    dataMode = ArgoProfileDataMode.valueOf(NetCdfUtils.getLevel1String(netcdf, profileIndex, "DATA_MODE"));
    platformType = NetCdfUtils.getLevel1String(netcdf, profileIndex, "PLATFORM_TYPE");
    floatSerialNumber = NetCdfUtils.getLevel1String(netcdf, profileIndex, "FLOAT_SERIAL_NO");
    firmwareVersion = NetCdfUtils.getLevel1String(netcdf, profileIndex, "FIRMWARE_VERSION");
    wmoInstrumentType = NetCdfUtils.getLevel1String(netcdf, profileIndex, "WMO_INST_TYPE");
//    julianDate = calculateJulianDate(netcdf, profileIndex, parent.getReferenceDateTime(), "JULD");
    julianDateQc = NetCdfUtils.getLevel1String(netcdf, profileIndex, "JULD_QC");
//    julianDateOfLocation = calculateJulianDate(netcdf, profileIndex, parent.getReferenceDateTime(), "JULD_LOCATION");
//    latitude = NetCdfUtils.getLevel1Double(netcdf, profileIndex, "LATITUDE");
//    longitude = NetCdfUtils.getLevel1Double(netcdf, profileIndex, "LONGITUDE");
    positionQc = NetCdfUtils.getLevel1String(netcdf, profileIndex, "POSITION_QC");
    positioningSystem = NetCdfUtils.getLevel1String(netcdf, profileIndex, "POSITIONING_SYSTEM");
//    positionErrorReported = NetCdfUtils.getLevel1Float(netcdf, profileIndex, "POSITION_ERROR_REPORTED");
//    positionErrorEstimatedComment = NetCdfUtils.getLevel1String(netcdf, profileIndex, "POSITION_ERROR_ESTIMATED_COMMENT");
//    verticalSamplingScheme = NetCdfUtils.getLevel1String(netcdf, profileIndex, "VERTICAL_SAMPLING_SCHEME");
//    configMissionNumber = NetCdfUtils.getLevel1Integer(netcdf, profileIndex, "CONFIG_MISSION_NUMBER");
//    numHistory = NetCdfUtils.getDimensionSize(netcdf, "N_HISTORY");
    parameters = new ArrayList<>(stationParameters.size());
    for (int index = 0; index < stationParameters.size(); index++) {
      String parameterName = stationParameters.get(index);
      parameters.add(new NetCdfTiedArgoSyntheticProfileV13Parameter(this, parameterName, index));
    }
  }

  public NetcdfFile getNetcdf() {
    return netcdf;
  }

  public int getProfileIndex() {
    return profileIndex;
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
  public String getId() {
    return id;
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
  public String getFeatureType() {
    return featureType;
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
  public Instant getReferenceDateTime() {
    return referenceDateTime;
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
  public String getProjectName() {
    return projectName;
  }

  @Override
  public String getPrincipalInvestigatorName() {
    return principalInvestigatorName;
  }

  @Override
  public List<String> getStationParameters() {
    return stationParameters;
  }

  @Override
  public int getCycleNumber() {
    return cycleNumber;
  }

  @Override
  public ArgoProfileDirection getDirection() {
    return direction;
  }

  @Override
  public String getDataCenter() {
    return dataCenter;
  }

  @Override
  public String getPlatformType() {
    return platformType;
  }

  @Override
  public String getFloatSerialNumber() {
    return floatSerialNumber;
  }

  @Override
  public String getFirmwareVersion() {
    return firmwareVersion;
  }

  @Override
  public String getWmoInstrumentType() {
    return wmoInstrumentType;
  }

  @Override
  public Instant getJulianDate() {
    return julianDate;
  }

  @Override
  public String getJulianDateQc() {
    return julianDateQc;
  }

  @Override
  public Instant getJulianDateOfLocation() {
    return julianDateOfLocation;
  }

  @Override
  public double getLatitude() {
    return latitude;
  }

  @Override
  public double getLongitude() {
    return longitude;
  }

  @Override
  public String getPositionQc() {
    return positionQc;
  }

  @Override
  public String getPositioningSystem() {
    return positioningSystem;
  }

  @Override
  public int getConfigMissionNumber() {
    return configMissionNumber;
  }

  @Override
  public List<ArgoSyntheticProfileV13Parameter> getParameters() {
    return parameters;
  }
}
