package edu.colorado.cires.argonaut.metadata.jpa.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "argo_float_profile", indexes = {
    @Index(name = "argo_float_profile_cycle_idx", columnList = "argo_float_cycle"),
    @Index(name = "argo_float_profile_file_type_idx", columnList = "file_type"),
    @Index(name = "argo_float_profile_file_status_idx", columnList = "file_status"),
    @Index(name = "argo_float_profile_date_idx", columnList = "profile_date_year,profile_date_month,profile_date_day"),
    @Index(name = "argo_float_profile_ocean_idx", columnList = "ocean"),
    @Index(name = "argo_float_profile_geo_merge_time_idx", columnList = "geo_merge_time"),
    @Index(name = "argo_float_profile_multi_float_merge_time_idx", columnList = "multi_float_merge_time"),
    @Index(name = "argo_float_profile_synthetic_merge_time_idx", columnList = "synthetic_merge_time"),
    @Index(name = "argo_float_profile_last_updated_time_idx", columnList = "last_updated_time"),
    @Index(name = "argo_float_profile_latest_merge_file_name_idx", columnList = "latest_merge_file_name")
})
public class ArgoFloatProfileEntity {

  @Id
  @Column(name = "file_path", nullable = false, length = 100)
  private String filePath;

  @Version
  @Column(name = "version", nullable = false)
  private int version;

  @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.REFRESH})
  @JoinColumn(name = "argo_float_cycle", nullable = false)
  private ArgoFloatCycleEntity argoFloatCycle;

  @Column(name = "file_type", length = 50, nullable = false)
  private String fileType;
  @Column(name = "file_status", length = 50, nullable = false)
  private String fileStatus;
  @Column(name = "profile_date")
  private ZonedDateTime profileDate;
  @Column(name = "profile_date_year")
  private Integer profileDateYear;
  @Column(name = "profile_date_month")
  private Integer profileDateMonth;
  @Column(name = "profile_date_day")
  private Integer profileDateDay;
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
  @Column(name = "data_mode_file_prefix", length = 1)
  private String dataModeFilePrefix;
  @Column(name = "data_mode", length = 1)
  private String dataMode;
  @Column(name = "profiler_type", length = 4)
  private String profilerType;
  @Column(name = "institution", length = 2)
  private String institution;
  @Column(name = "date_update")
  private ZonedDateTime dateUpdate;
  @Column(name = "parameter_data_mode", length = 100)
  private String parameterDataMode;
  @Column(name = "synthetic_merge_time")
  private ZonedDateTime syntheticMergeTime;
  @Column(name = "multi_float_merge_time")
  private ZonedDateTime multiFloatMergeTime;
  @Column(name = "geo_merge_time")
  private ZonedDateTime geoMergeTime;
  @Column(name = "last_updated_time", nullable = false)
  private ZonedDateTime lastUpdatedTime;
  @Column(name = "file_name", nullable = false, length = 20)
  private String fileName;
  @Column(name = "latest_merge_file_name", length = 9)
  private String latestMergeFileName;

  @OneToMany(mappedBy = "argoFloatProfile", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ArgonautFileRemovedTimeEntity> removedTimes = new ArrayList<>();

  @OneToMany(mappedBy = "argoFloatProfile", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ArgoFloatProfileParameterEntity> parameters = new ArrayList<>();


  public String getFilePath() {
    return filePath;
  }

  public void setFilePath(String file) {
    this.filePath = file;
  }

  public ArgoFloatCycleEntity getArgoFloatCycle() {
    return argoFloatCycle;
  }

  public void setArgoFloatCycle(ArgoFloatCycleEntity cycle) {
    this.argoFloatCycle = cycle;
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

  public ZonedDateTime getProfileDate() {
    return profileDate;
  }

  public void setProfileDate(ZonedDateTime date) {
    this.profileDate = date;
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

  public ZonedDateTime getMultiFloatMergeTime() {
    return multiFloatMergeTime;
  }

  public void setMultiFloatMergeTime(ZonedDateTime multiFloatMergeTime) {
    this.multiFloatMergeTime = multiFloatMergeTime;
  }

  public ZonedDateTime getGeoMergeTime() {
    return geoMergeTime;
  }

  public void setGeoMergeTime(ZonedDateTime geoMergeTime) {
    this.geoMergeTime = geoMergeTime;
  }

  public int getVersion() {
    return version;
  }

  public Integer getProfileDateYear() {
    return profileDateYear;
  }

  public void setProfileDateYear(Integer year) {
    this.profileDateYear = year;
  }

  public Integer getProfileDateMonth() {
    return profileDateMonth;
  }

  public void setProfileDateMonth(Integer month) {
    this.profileDateMonth = month;
  }

  public Integer getProfileDateDay() {
    return profileDateDay;
  }

  public void setProfileDateDay(Integer day) {
    this.profileDateDay = day;
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public List<ArgonautFileRemovedTimeEntity> getRemovedTimes() {
    return removedTimes;
  }

  public void setRemovedTimes(List<ArgonautFileRemovedTimeEntity> removedTimes) {
    this.removedTimes = removedTimes;
  }

  public String getDataMode() {
    return dataMode;
  }

  public void setDataMode(String dataMode) {
    this.dataMode = dataMode;
  }

  public String getLatestMergeFileName() {
    return latestMergeFileName;
  }

  public void setLatestMergeFileName(String latestMergeFileName) {
    this.latestMergeFileName = latestMergeFileName;
  }

  public String getDataModeFilePrefix() {
    return dataModeFilePrefix;
  }

  public void setDataModeFilePrefix(String dataModeFilePrefix) {
    this.dataModeFilePrefix = dataModeFilePrefix;
  }

  public List<ArgoFloatProfileParameterEntity> getParameters() {
    return parameters;
  }

  public void setParameters(List<ArgoFloatProfileParameterEntity> parameters) {
    this.parameters = parameters;
  }

  @Override
  public String toString() {
    return "ArgoFloatProfileEntity{" +
        "filePath='" + filePath + '\'' +
        ", fileStatus='" + fileStatus + '\'' +
        ", dataMode='" + dataMode + '\'' +
        ", dataModeFilePrefix='" + dataModeFilePrefix + '\'' +
        ", latestMergeFileName='" + latestMergeFileName + '\'' +
        ", fileType='" + fileType + '\'' +
        '}';
  }
}
