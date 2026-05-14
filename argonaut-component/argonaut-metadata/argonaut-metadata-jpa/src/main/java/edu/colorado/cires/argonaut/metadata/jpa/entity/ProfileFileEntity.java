package edu.colorado.cires.argonaut.metadata.jpa.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.ZonedDateTime;

@Entity
@Table(name = "profile")
public class ProfileFileEntity {

  @Id
  @Column(name = "file", nullable = false, length = 100)
  private String file;

  @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.REFRESH})
  @JoinColumn(name = "cycle", nullable = false)
  private CycleEntity cycle;

  @Column(name = "file_type", length = 50, nullable = false)
  private String fileType;
  @Column(name = "file_status", length = 50, nullable = false)
  private String fileStatus;
  @Column(name = "date")
  private ZonedDateTime date;
  @Column(name = "latitude")
  private Double latitude;
  @Column(name = "latitude_min")
  private Double latitudeMin;
  @Column(name = "latitude_max")
  private Double latitudeMax;
  @Column(name = "longitude")
  private Double longitude;
  @Column(name = "longitude_min")
  private Double longitudeMin;
  @Column(name = "longitude_max")
  private Double longitudeMax;
  @Column(name = "ocean", length = 1)
  private String ocean;
  @Column(name = "profiler_type", length = 4)
  private String profilerType;
  @Column(name = "institution", length = 2)
  private String institution;
  @Column(name = "date_update")
  private ZonedDateTime dateUpdate;
  @Column(name = "parameters", length = 100)
  private String parameters;
  @Column(name = "parameter_data_mode", length = 100)
  private String parameterDataMode;
  @Column(name = "synthetic_merge_time")
  private ZonedDateTime syntheticMergeTime;
  @Column(name = "last_updated_time", nullable = false)
  private ZonedDateTime lastUpdatedTime;


  public String getFile() {
    return file;
  }

  public void setFile(String file) {
    this.file = file;
  }

  public CycleEntity getCycle() {
    return cycle;
  }

  public void setCycle(CycleEntity cycle) {
    this.cycle = cycle;
  }

  public String getFileType() {
    return fileType;
  }

  public void setFileType(String fileType) {
    this.fileType = fileType;
  }

  public String getFileStatus() {
    return fileStatus;
  }

  public void setFileStatus(String fileStatus) {
    this.fileStatus = fileStatus;
  }

  public ZonedDateTime getDate() {
    return date;
  }

  public void setDate(ZonedDateTime date) {
    this.date = date;
  }

  public Double getLatitude() {
    return latitude;
  }

  public void setLatitude(Double latitude) {
    this.latitude = latitude;
  }

  public Double getLatitudeMin() {
    return latitudeMin;
  }

  public void setLatitudeMin(Double latitudeMin) {
    this.latitudeMin = latitudeMin;
  }

  public Double getLatitudeMax() {
    return latitudeMax;
  }

  public void setLatitudeMax(Double latitudeMax) {
    this.latitudeMax = latitudeMax;
  }

  public Double getLongitude() {
    return longitude;
  }

  public void setLongitude(Double longitude) {
    this.longitude = longitude;
  }

  public Double getLongitudeMin() {
    return longitudeMin;
  }

  public void setLongitudeMin(Double longitudeMin) {
    this.longitudeMin = longitudeMin;
  }

  public Double getLongitudeMax() {
    return longitudeMax;
  }

  public void setLongitudeMax(Double longitudeMax) {
    this.longitudeMax = longitudeMax;
  }

  public String getOcean() {
    return ocean;
  }

  public void setOcean(String ocean) {
    this.ocean = ocean;
  }

  public String getProfilerType() {
    return profilerType;
  }

  public void setProfilerType(String profilerType) {
    this.profilerType = profilerType;
  }

  public String getInstitution() {
    return institution;
  }

  public void setInstitution(String institution) {
    this.institution = institution;
  }

  public ZonedDateTime getDateUpdate() {
    return dateUpdate;
  }

  public void setDateUpdate(ZonedDateTime dateUpdate) {
    this.dateUpdate = dateUpdate;
  }

  public String getParameters() {
    return parameters;
  }

  public void setParameters(String parameters) {
    this.parameters = parameters;
  }

  public String getParameterDataMode() {
    return parameterDataMode;
  }

  public void setParameterDataMode(String parameterDataMode) {
    this.parameterDataMode = parameterDataMode;
  }

  public ZonedDateTime getSyntheticMergeTime() {
    return syntheticMergeTime;
  }

  public void setSyntheticMergeTime(ZonedDateTime syntheticMergeTime) {
    this.syntheticMergeTime = syntheticMergeTime;
  }

  public ZonedDateTime getLastUpdatedTime() {
    return lastUpdatedTime;
  }

  public void setLastUpdatedTime(ZonedDateTime lastUpdatedTime) {
    this.lastUpdatedTime = lastUpdatedTime;
  }
}
