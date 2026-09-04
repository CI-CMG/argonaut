package edu.colorado.cires.argonaut.core.netcdf.synthprofile.v13.impl;

import edu.colorado.cires.argonaut.core.netcdf.synthprofile.v13.ArgoSyntheticProfileV13;
import edu.colorado.cires.argonaut.core.netcdf.synthprofile.v13.ArgoSyntheticProfileV13Parameter;
import edu.colorado.cires.argonaut.core.util.NetCdfReadUtils;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import ucar.nc2.NetcdfFile;

public class NetCdfTiedArgoSyntheticProfileV13 implements ArgoSyntheticProfileV13 {


  private final NetcdfFile netcdf;
  private final int profileIndex;
  private final Instant referenceDateTime;
  private final Instant dateCreation;
  private final Instant dateUpdate;
  private final String platformNumber;
  private final String projectName;
  private final String principalInvestigatorName;
  private final List<String> stationParameters;
  private final int cycleNumber;
  private final String direction;
  private final String dataCenter;
  private final String platformType;
  private final String floatSerialNumber;
  private final String firmwareVersion;
  private final String wmoInstrumentType;
  private final Instant julianDate;
  private final String julianDateQc;
  private final Instant julianDateOfLocation;
  private final Double latitude;
  private final Double longitude;
  private final String positionQc;
  private final String positioningSystem;
  private final Integer configMissionNumber;
  private final String dataMode;
  private List<ArgoSyntheticProfileV13Parameter> parameters = new ArrayList<>();
  private final NetCdfTiedArgoSyntheticMultiProfileV13 parent;

  public NetCdfTiedArgoSyntheticProfileV13(NetcdfFile netcdf, NetCdfTiedArgoSyntheticMultiProfileV13 parent, int profileIndex) {
    this.netcdf = netcdf;
    this.parent = parent;
    this.profileIndex = profileIndex;
    configMissionNumber = NetCdfReadUtils.getLevel1Integer(netcdf, profileIndex, "CONFIG_MISSION_NUMBER");
    dateCreation = NetCdfReadUtils.getInstant(netcdf, "DATE_CREATION");
    dateUpdate = NetCdfReadUtils.getInstant(netcdf, "DATE_UPDATE");
    referenceDateTime = NetCdfReadUtils.getInstant(netcdf, "REFERENCE_DATE_TIME");
    platformNumber = NetCdfReadUtils.getLevel1String(netcdf, profileIndex, "PLATFORM_NUMBER");
    projectName = NetCdfReadUtils.getLevel1String(netcdf, profileIndex, "PROJECT_NAME");
    principalInvestigatorName = NetCdfReadUtils.getLevel1String(netcdf, profileIndex, "PI_NAME");
    stationParameters = NetCdfReadUtils.getLevel1ListOfString(netcdf, profileIndex, "STATION_PARAMETERS");
    cycleNumber = NetCdfReadUtils.getLevel1Integer(netcdf, profileIndex, "CYCLE_NUMBER");
    direction = NetCdfReadUtils.getLevel1String(netcdf, profileIndex, "DIRECTION");
    dataCenter = NetCdfReadUtils.getLevel1String(netcdf, profileIndex, "DATA_CENTRE");
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
    dataMode = NetCdfReadUtils.getLevel1String(netcdf, profileIndex, "DATA_MODE");

    parameters = new ArrayList<>(stationParameters.size());
    for (int index = 0; index < stationParameters.size(); index++) {
      String parameterName = stationParameters.get(index);
      parameters.add(new NetCdfTiedArgoSyntheticProfileV13Parameter(this, parameterName, index));
    }
  }

  private static Instant calculateJulianDate(NetcdfFile netcdf, int profileIndex, Instant referenceDateTime, String variable) {
    Double daysSinceRef = NetCdfReadUtils.getLevel1Double(netcdf, profileIndex, variable);
    if (daysSinceRef == null) {
      return null;
    }
    return NetCdfReadUtils.calculateJulianDate(referenceDateTime, daysSinceRef);
  }

  public NetcdfFile getNetcdf() {
    return netcdf;
  }

  public int getProfileIndex() {
    return profileIndex;
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
  public String getDirection() {
    return direction;
  }

  @Override
  public String getDataCenter() {
    return dataCenter;
  }

  @Override
  public String getDataCenterReference() {
    return null;
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
  public Integer getConfigMissionNumber() {
    return configMissionNumber;
  }

  @Override
  public String getVerticalSamplingScheme() {
    return null;
  }

  @Override
  public String getDataStateIndicator() {
    return null;
  }

  @Override
  public List<ArgoSyntheticProfileV13Parameter> getParameters() {
    return parameters;
  }

  @Override
  public String getSoftwareVersion() {
    return parent.getSoftwareVersion();
  }

  @Override
  public String getDataMode() {
    return dataMode;
  }
}
