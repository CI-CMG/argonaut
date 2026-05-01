package edu.colorado.cires.argonaut.core.netcdf.synthprofile.v13.impl;

import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoProfileDirection;
import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoProfileV31Parameter;
import edu.colorado.cires.argonaut.core.netcdf.synthprofile.v13.ArgoSyntheticProfileV13;
import edu.colorado.cires.argonaut.core.netcdf.synthprofile.v13.ArgoSyntheticProfileV13Parameter;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class ArgoSyntheticProfileV13Bean implements ArgoSyntheticProfileV13 {

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


  @Override
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  @Override
  public String getInstitution() {
    return institution;
  }

  public void setInstitution(String institution) {
    this.institution = institution;
  }

  @Override
  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  @Override
  public String getHistory() {
    return history;
  }

  public void setHistory(String history) {
    this.history = history;
  }

  @Override
  public String getReferences() {
    return references;
  }

  public void setReferences(String references) {
    this.references = references;
  }

  @Override
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  @Override
  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  @Override
  public String getUserManualVersion() {
    return userManualVersion;
  }

  public void setUserManualVersion(String userManualVersion) {
    this.userManualVersion = userManualVersion;
  }

  @Override
  public String getConventions() {
    return conventions;
  }

  public void setConventions(String conventions) {
    this.conventions = conventions;
  }

  @Override
  public String getFeatureType() {
    return featureType;
  }

  public void setFeatureType(String featureType) {
    this.featureType = featureType;
  }

  @Override
  public String getDataType() {
    return dataType;
  }

  public void setDataType(String dataType) {
    this.dataType = dataType;
  }

  @Override
  public String getFormatVersion() {
    return formatVersion;
  }

  public void setFormatVersion(String formatVersion) {
    this.formatVersion = formatVersion;
  }

  @Override
  public String getHandbookVersion() {
    return handbookVersion;
  }

  public void setHandbookVersion(String handbookVersion) {
    this.handbookVersion = handbookVersion;
  }

  @Override
  public Instant getReferenceDateTime() {
    return referenceDateTime;
  }

  public void setReferenceDateTime(Instant referenceDateTime) {
    this.referenceDateTime = referenceDateTime;
  }

  @Override
  public Instant getDateCreation() {
    return dateCreation;
  }

  public void setDateCreation(Instant dateCreation) {
    this.dateCreation = dateCreation;
  }

  @Override
  public Instant getDateUpdate() {
    return dateUpdate;
  }

  public void setDateUpdate(Instant dateUpdate) {
    this.dateUpdate = dateUpdate;
  }

  @Override
  public String getPlatformNumber() {
    return platformNumber;
  }

  public void setPlatformNumber(String platformNumber) {
    this.platformNumber = platformNumber;
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
  public List<String> getStationParameters() {
    return parameters.stream().map(ArgoSyntheticProfileV13Parameter::getParameterName).toList();
  }


  @Override
  public int getCycleNumber() {
    return cycleNumber;
  }

  public void setCycleNumber(int cycleNumber) {
    this.cycleNumber = cycleNumber;
  }

  @Override
  public ArgoProfileDirection getDirection() {
    return direction;
  }

  public void setDirection(ArgoProfileDirection direction) {
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
  public Instant getJulianDateOfLocation() {
    return julianDateOfLocation;
  }

  public void setJulianDateOfLocation(Instant julianDateOfLocation) {
    this.julianDateOfLocation = julianDateOfLocation;
  }

  @Override
  public double getLatitude() {
    return latitude;
  }

  public void setLatitude(double latitude) {
    this.latitude = latitude;
  }

  @Override
  public double getLongitude() {
    return longitude;
  }

  public void setLongitude(double longitude) {
    this.longitude = longitude;
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
  public int getConfigMissionNumber() {
    return configMissionNumber;
  }

  public void setConfigMissionNumber(int configMissionNumber) {
    this.configMissionNumber = configMissionNumber;
  }

  @Override
  public List<ArgoSyntheticProfileV13Parameter> getParameters() {
    return parameters;
  }

  public void setParameters(List<ArgoSyntheticProfileV13Parameter> parameters) {
    this.parameters = parameters;
  }
}
