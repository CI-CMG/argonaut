package edu.colorado.cires.argonaut.metadata.jpa.entity;

import jakarta.persistence.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "argo_float_bio_trajectory", indexes = {
    @Index(name = "argo_float_bio_trajectory_file_status_idx", columnList = "file_status")
})
public class ArgoFloatBioTrajectoryEntity {

  @Id
  @Column(name = "file_path", nullable = false, length = 100)
  private String filePath;

  @Version
  @Column(name = "version", nullable = false)
  private int version;

  @OneToOne(cascade = {CascadeType.DETACH, CascadeType.REFRESH})
  @JoinColumn(name = "argo_float", nullable = false, unique = true)
  private ArgoFloatEntity argoFloat;

  @Column(name = "profiler_type", length = 4)
  private String profilerType;

  @Column(name = "institution", length = 2)
  private String institution;

  @Column(name = "date_update")
  private ZonedDateTime dateUpdate;

  @Column(name = "latitude_min")
  private Double latitudeMin;

  @Column(name = "latitude_max")
  private Double latitudeMax;

  @Column(name = "longitude_min")
  private Double longitudeMin;

  @Column(name = "longitude_max")
  private Double longitudeMax;

  // TODO: switch from string to list?
  @Column(name = "parameters", length = 100)
  private String parameters;
//  @OneToMany(mappedBy = "argoFloatProfile", cascade = CascadeType.ALL, orphanRemoval = true)
//  private List<ArgoFloatProfileParameterEntity> parameters = new ArrayList<>();

  @Column(name = "last_updated_time", nullable = false)
  private ZonedDateTime lastUpdatedTime;

  @Column(name = "file_status", length = 50, nullable = false)
  private String fileStatus;

  public String getFilePath() {
    return filePath;
  }

  public void setFilePath(String file) {
    this.filePath = file;
  }

  public int getVersion() {
    return version;
  }

  public void setVersion(int version) {
    this.version = version;
  }

  public ArgoFloatEntity getArgoFloat() {
    return argoFloat;
  }

  public void setArgoFloat(ArgoFloatEntity floatId) {
    this.argoFloat = floatId;
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

  public String getParameters() { return parameters; }

  public void setParameters(String parameters) { this.parameters = parameters; }

  public ZonedDateTime getLastUpdatedTime() {
    return lastUpdatedTime;
  }

  public void setLastUpdatedTime(ZonedDateTime lastUpdatedTime) {
    this.lastUpdatedTime = lastUpdatedTime;
  }

  public String getFileStatus() {
    return fileStatus;
  }

  public void setFileStatus(String fileStatus) {
    this.fileStatus = fileStatus;
  }
}
