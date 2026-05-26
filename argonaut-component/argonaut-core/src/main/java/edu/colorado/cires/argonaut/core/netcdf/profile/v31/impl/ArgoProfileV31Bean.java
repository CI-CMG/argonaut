package edu.colorado.cires.argonaut.core.netcdf.profile.v31.impl;

import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoProfileV31;
import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoProfileV31History;
import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoProfileV31Parameter;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class ArgoProfileV31Bean implements ArgoProfileV31 {

  private final ArgoMultiProfileV31Bean parent;
  private String platformNumber;
  private final int profileIndex;
  private String projectName;
  private String principalInvestigatorName;
  private int cycleNumber;
  private String direction;
  private String dataCenter;
  private String dataCenterReference;
  private String dataStateIndicator;
  private String dataMode;
  private String platformType;
  private String floatSerialNumber;
  private String firmwareVersion;
  private String wmoInstrumentType;
  private Instant julianDate;
  private String julianDateQc;
  private Double latitude;
  private Double longitude;
  private Instant julianDateOfLocation;
  private String positionQc;
  private String positioningSystem;
  private Float positionErrorReported;
  private Float positionErrorEstimated;
  private String positionErrorEstimatedComment;
  private String verticalSamplingScheme;
  private int configMissionNumber;
  private List<ArgoProfileV31Parameter> parameters = new ArrayList<>();
  private List<ArgoProfileV31History> history = new ArrayList<>();

  public ArgoProfileV31Bean(ArgoMultiProfileV31Bean parent, int profileIndex) {
    this.profileIndex = profileIndex;
    this.parent = parent;
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

  public void setPlatformNumber(String platformNumber) {
    this.platformNumber = platformNumber;
  }

  @Override
  public int getProfileIndex() {
    return profileIndex;
  }

  @Override
  public String getProjectName() {
    return projectName;
  }

  public void setProjectName(String projectName) {
    this.projectName = projectName;
  }

  @Override
  public String getPrincipalInvestigatorName() {
    return principalInvestigatorName;
  }

  public void setPrincipalInvestigatorName(String principalInvestigatorName) {
    this.principalInvestigatorName = principalInvestigatorName;
  }

  @Override
  public int getCycleNumber() {
    return cycleNumber;
  }

  public void setCycleNumber(int cycleNumber) {
    this.cycleNumber = cycleNumber;
  }

  @Override
  public String getDirection() {
    return direction;
  }

  public void setDirection(String direction) {
    this.direction = direction;
  }

  @Override
  public String getDataCenter() {
    return dataCenter;
  }

  public void setDataCenter(String dataCenter) {
    this.dataCenter = dataCenter;
  }

  @Override
  public String getDataCenterReference() {
    return dataCenterReference;
  }

  public void setDataCenterReference(String dataCenterReference) {
    this.dataCenterReference = dataCenterReference;
  }

  @Override
  public String getDataStateIndicator() {
    return dataStateIndicator;
  }

  public void setDataStateIndicator(String dataStateIndicator) {
    this.dataStateIndicator = dataStateIndicator;
  }

  @Override
  public String getDataMode() {
    return dataMode;
  }

  public void setDataMode(String dataMode) {
    this.dataMode = dataMode;
  }

  @Override
  public String getPlatformType() {
    return platformType;
  }

  public void setPlatformType(String platformType) {
    this.platformType = platformType;
  }

  @Override
  public String getFloatSerialNumber() {
    return floatSerialNumber;
  }

  public void setFloatSerialNumber(String floatSerialNumber) {
    this.floatSerialNumber = floatSerialNumber;
  }

  @Override
  public String getFirmwareVersion() {
    return firmwareVersion;
  }

  public void setFirmwareVersion(String firmwareVersion) {
    this.firmwareVersion = firmwareVersion;
  }

  @Override
  public String getWmoInstrumentType() {
    return wmoInstrumentType;
  }

  public void setWmoInstrumentType(String wmoInstrumentType) {
    this.wmoInstrumentType = wmoInstrumentType;
  }

  @Override
  public Instant getJulianDate() {
    return julianDate;
  }

  public void setJulianDate(Instant julianDate) {
    this.julianDate = julianDate;
  }

  @Override
  public String getJulianDateQc() {
    return julianDateQc;
  }

  public void setJulianDateQc(String julianDateQc) {
    this.julianDateQc = julianDateQc;
  }

  @Override
  public Double getLatitude() {
    return latitude;
  }

  public void setLatitude(Double latitude) {
    this.latitude = latitude;
  }

  @Override
  public Double getLongitude() {
    return longitude;
  }

  public void setLongitude(Double longitude) {
    this.longitude = longitude;
  }

  @Override
  public Instant getJulianDateOfLocation() {
    return julianDateOfLocation;
  }

  public void setJulianDateOfLocation(Instant julianDateOfLocation) {
    this.julianDateOfLocation = julianDateOfLocation;
  }

  @Override
  public String getPositionQc() {
    return positionQc;
  }

  public void setPositionQc(String positionQc) {
    this.positionQc = positionQc;
  }

  @Override
  public String getPositioningSystem() {
    return positioningSystem;
  }

  public void setPositioningSystem(String positioningSystem) {
    this.positioningSystem = positioningSystem;
  }

  @Override
  public String getPositionErrorEstimatedComment() {
    return positionErrorEstimatedComment;
  }

  public void setPositionErrorEstimatedComment(String positionErrorEstimatedComment) {
    this.positionErrorEstimatedComment = positionErrorEstimatedComment;
  }

  @Override
  public String getVerticalSamplingScheme() {
    return verticalSamplingScheme;
  }

  public void setVerticalSamplingScheme(String verticalSamplingScheme) {
    this.verticalSamplingScheme = verticalSamplingScheme;
  }

  @Override
  public int getConfigMissionNumber() {
    return configMissionNumber;
  }

  @Override
  public ArgoProfileV31Parameter getParameter(String parameterName) {
    return parameters.stream().filter(p -> parameterName.equals(p.getParameterName())).findFirst().orElse(null);
  }

  public void setConfigMissionNumber(int configMissionNumber) {
    this.configMissionNumber = configMissionNumber;
  }

  @Override
  public List<ArgoProfileV31Parameter> getParameters() {
    return parameters;
  }

  public void setParameters(List<ArgoProfileV31ParameterBean> parameters) {
    if (parameters == null) {
      this.parameters = new ArrayList<>();
    } else {
      this.parameters = new ArrayList<>(parameters);
    }
  }

  @Override
  public List<ArgoProfileV31History> getProfileHistory() {
    return history;
  }

  public void setHistory(List<ArgoProfileV31HistoryBean> history) {
    if (history == null) {
      this.history = new ArrayList<>();
    } else {
      this.history = new ArrayList<>(history);
    }
  }

  @Override
  public Float getPositionErrorReported() {
    return positionErrorReported;
  }

  public void setPositionErrorReported(Float positionErrorReported) {
    this.positionErrorReported = positionErrorReported;
  }

  @Override
  public Float getPositionErrorEstimated() {
    return positionErrorEstimated;
  }

  public void setPositionErrorEstimated(Float positionErrorEstimated) {
    this.positionErrorEstimated = positionErrorEstimated;
  }

  @Override
  public List<String> getStationParameters() {
    return parameters.stream().map(ArgoProfileV31Parameter::getParameterName).toList();
  }

}
