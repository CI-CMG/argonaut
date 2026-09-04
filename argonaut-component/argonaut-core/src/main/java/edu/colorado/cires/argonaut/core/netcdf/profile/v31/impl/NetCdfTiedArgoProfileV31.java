package edu.colorado.cires.argonaut.core.netcdf.profile.v31.impl;

import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoProfileV31;
import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoProfileV31History;
import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoProfileV31Parameter;
import edu.colorado.cires.argonaut.core.util.NetCdfReadUtils;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import ucar.nc2.NetcdfFile;

public class NetCdfTiedArgoProfileV31 implements ArgoProfileV31 {

  private static final int MS_DAY = 1000 * 60 * 60 * 24;

  private final NetCdfTiedArgoMultiProfileV31 parent;
  private final NetcdfFile netcdf;
  private final String platformNumber;
  private final int profileIndex;
  private final String projectName;
  private final String principalInvestigatorName;
  private final List<String> stationParameters;
  private final int cycleNumber;
  private final String direction;
  private final String dataCenter;
  private final String dataCenterReference;
  private final String dataStateIndicator;
  private final String dataMode;
  private final String platformType;
  private final String floatSerialNumber;
  private final String firmwareVersion;
  private final String wmoInstrumentType;
  private final Instant julianDate;
  private final String julianDateQc;
  private final Double latitude;
  private final Double longitude;
  private final Instant julianDateOfLocation;
  private final String positionQc;
  private final String positioningSystem;
  private final Float positionErrorReported;
  private final Float positionErrorEstimated;
  private final String positionErrorEstimatedComment;
  private final String verticalSamplingScheme;
  private final Integer configMissionNumber;
  private final int numHistory;
  private final List<ArgoProfileV31Parameter> parameters;

  NetCdfTiedArgoProfileV31(NetCdfTiedArgoMultiProfileV31 parent, int profileIndex) {
    this.profileIndex = profileIndex;
    this.parent = parent;
    this.netcdf = parent.getNetcdf();

    platformNumber = NetCdfReadUtils.getLevel1String(netcdf, profileIndex, "PLATFORM_NUMBER");
    projectName = NetCdfReadUtils.getLevel1String(netcdf, profileIndex, "PROJECT_NAME");
    principalInvestigatorName = NetCdfReadUtils.getLevel1String(netcdf, profileIndex, "PI_NAME");
    stationParameters = NetCdfReadUtils.getLevel1ListOfString(netcdf, profileIndex, "STATION_PARAMETERS");
    cycleNumber = NetCdfReadUtils.getLevel1Integer(netcdf, profileIndex, "CYCLE_NUMBER");
    direction = NetCdfReadUtils.getLevel1String(netcdf, profileIndex, "DIRECTION");
    dataCenter = NetCdfReadUtils.getLevel1String(netcdf, profileIndex, "DATA_CENTRE");
    dataCenterReference = NetCdfReadUtils.getLevel1String(netcdf, profileIndex, "DC_REFERENCE");
    dataStateIndicator = NetCdfReadUtils.getLevel1String(netcdf, profileIndex, "DATA_STATE_INDICATOR");
    dataMode = NetCdfReadUtils.getLevel1String(netcdf, profileIndex, "DATA_MODE");
    platformType = NetCdfReadUtils.getLevel1String(netcdf, profileIndex, "PLATFORM_TYPE");
    floatSerialNumber = NetCdfReadUtils.getLevel1String(netcdf, profileIndex, "FLOAT_SERIAL_NO");
    firmwareVersion = NetCdfReadUtils.getLevel1String(netcdf, profileIndex, "FIRMWARE_VERSION");
    wmoInstrumentType = NetCdfReadUtils.getLevel1String(netcdf, profileIndex, "WMO_INST_TYPE");
    julianDate = calculateJulianDate(netcdf, profileIndex, parent.getReferenceDateTime(), "JULD");
    julianDateQc = NetCdfReadUtils.getLevel1String(netcdf, profileIndex, "JULD_QC");
    julianDateOfLocation = calculateJulianDate(netcdf, profileIndex, parent.getReferenceDateTime(), "JULD_LOCATION");
    latitude = NetCdfReadUtils.getLevel1Double(netcdf, profileIndex, "LATITUDE");
    longitude = NetCdfReadUtils.getLevel1Double(netcdf, profileIndex, "LONGITUDE");
    positionQc = NetCdfReadUtils.getLevel1String(netcdf, profileIndex, "POSITION_QC");
    positioningSystem = NetCdfReadUtils.getLevel1String(netcdf, profileIndex, "POSITIONING_SYSTEM");
    positionErrorReported = NetCdfReadUtils.getLevel1Float(netcdf, profileIndex, "POSITION_ERROR_REPORTED");
    positionErrorEstimated = NetCdfReadUtils.getLevel1Float(netcdf, profileIndex, "POSITION_ERROR_ESTIMATED");
    positionErrorEstimatedComment = NetCdfReadUtils.getLevel1String(netcdf, profileIndex, "POSITION_ERROR_ESTIMATED_COMMENT");
    verticalSamplingScheme = NetCdfReadUtils.getLevel1String(netcdf, profileIndex, "VERTICAL_SAMPLING_SCHEME");
    configMissionNumber = NetCdfReadUtils.getLevel1Integer(netcdf, profileIndex, "CONFIG_MISSION_NUMBER");
    numHistory = NetCdfReadUtils.getDimensionSize(netcdf, "N_HISTORY");
    parameters = new ArrayList<>(stationParameters.size());
    for (int index = 0; index < stationParameters.size(); index++) {
      String parameterName = stationParameters.get(index);
      parameters.add(new NetCdfTiedArgoProfileV31Parameter(this, parameterName, index));
    }
  }

  private static Instant calculateJulianDate(NetcdfFile netcdf, int profileIndex, Instant referenceDateTime, String variable) {
    Double daysSinceRef = NetCdfReadUtils.getLevel1Double(netcdf, profileIndex, variable);
    if (daysSinceRef == null) {
      return null;
    }
    return NetCdfReadUtils.calculateJulianDate(referenceDateTime, daysSinceRef);
  }

  NetcdfFile getNetcdf() {
    return netcdf;
  }

  @Override
  public String getTitle() {
    return parent.getTitle();
  }

  @Override
  public String getInstitution() {
    return parent.getInstitution();
  }

  @Override
  public String getSource() {
    return parent.getSource();
  }

  @Override
  public String getHistory() {
    return parent.getHistory();
  }

  @Override
  public String getReferences() {
    return parent.getReferences();
  }

  @Override
  public String getId() {
    return parent.getId();
  }

  @Override
  public String getComment() {
    return parent.getComment();
  }

  @Override
  public String getUserManualVersion() {
    return parent.getUserManualVersion();
  }

  @Override
  public String getConventions() {
    return parent.getConventions();
  }

  @Override
  public String getFeatureType() {
    return parent.getFeatureType();
  }

  @Override
  public String getCommentOnResolution() {
    return parent.getCommentOnResolution();
  }

  @Override
  public String getDataType() {
    return parent.getDataType();
  }

  @Override
  public String getFormatVersion() {
    return parent.getFormatVersion();
  }

  @Override
  public String getHandbookVersion() {
    return parent.getHandbookVersion();
  }

  @Override
  public Instant getReferenceDateTime() {
    return parent.getReferenceDateTime();
  }

  @Override
  public Instant getDateCreation() {
    return parent.getDateCreation();
  }

  @Override
  public Instant getDateUpdate() {
    return parent.getDateUpdate();
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
  public String getDirection() {
    return direction;
  }

  @Override
  public String getDataCenter() {
    return dataCenter;
  }

  @Override
  public String getDataCenterReference() {
    return dataCenterReference;
  }

  @Override
  public String getDataStateIndicator() {
    return dataStateIndicator;
  }

  @Override
  public String getDataMode() {
    return dataMode;
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
  public Double getLatitude() {
    return latitude;
  }

  @Override
  public Double getLongitude() {
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
  public Float getPositionErrorReported() {
    return positionErrorReported;
  }

  @Override
  public Float getPositionErrorEstimated() {
    return positionErrorEstimated;
  }

  @Override
  public String getPositionErrorEstimatedComment() {
    return positionErrorEstimatedComment;
  }

  @Override
  public String getVerticalSamplingScheme() {
    return verticalSamplingScheme;
  }

  @Override
  public Integer getConfigMissionNumber() {
    return configMissionNumber;
  }

  @Override
  public ArgoProfileV31Parameter getParameter(String parameterName) {
    int index = stationParameters.indexOf(parameterName);
    if (index < 0) {
      throw new IllegalArgumentException("Parameter " + parameterName + " not found");
    }
    return new NetCdfTiedArgoProfileV31Parameter(this, parameterName, index);
  }

  @Override
  public List<ArgoProfileV31Parameter> getParameters() {
    return parameters;
  }

  @Override
  public List<ArgoProfileV31History> getProfileHistory() {
    List<ArgoProfileV31History> history = new ArrayList<>(numHistory);
    for (int i = 0; i < numHistory; i++) {
      history.add(new NetCdfTiedArgoProfileV31History(this, i));
    }
    return history;
  }

  @Override
  public int getProfileIndex() {
    return profileIndex;
  }


}
