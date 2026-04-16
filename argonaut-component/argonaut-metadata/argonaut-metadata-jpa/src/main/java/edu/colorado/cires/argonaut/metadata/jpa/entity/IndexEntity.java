package edu.colorado.cires.argonaut.metadata.jpa.entity;

import edu.colorado.cires.argonaut.messaging.core.databind.ArgoOcean;
import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord;
import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord.FileStatus;
import edu.colorado.cires.argonaut.messaging.core.databind.NcSubmissionMessage.FileType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Objects;

@Entity
@Table(name = "argonaut_index")
public class IndexEntity {

  @Id
  @Column(name = "file", nullable = false, length = 100)
  private String file;
  @Column(name = "dac", nullable = false, length = 20)
  private String dac;
  @Column(name = "float_id", nullable = false, length = 30)
  private String floatId;
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
  @Column(name = "float_merged")
  private boolean floatMerged = false;


  public String getFile() {
    return file;
  }

  public void setFile(String file) {
    this.file = file;
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

  public boolean isFloatMerged() {
    return floatMerged;
  }

  public void setFloatMerged(boolean floatMerged) {
    this.floatMerged = floatMerged;
  }

  public String getDac() {
    return dac;
  }

  public void setDac(String dac) {
    this.dac = dac;
  }

  public String getFloatId() {
    return floatId;
  }

  public void setFloatId(String floatId) {
    this.floatId = floatId;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    IndexEntity entity = (IndexEntity) o;
    return floatMerged == entity.floatMerged && Objects.equals(file, entity.file) && Objects.equals(dac, entity.dac)
        && Objects.equals(floatId, entity.floatId) && Objects.equals(fileType, entity.fileType) && Objects.equals(
        fileStatus, entity.fileStatus) && Objects.equals(date, entity.date) && Objects.equals(latitude, entity.latitude)
        && Objects.equals(latitudeMin, entity.latitudeMin) && Objects.equals(latitudeMax, entity.latitudeMax)
        && Objects.equals(longitude, entity.longitude) && Objects.equals(longitudeMin, entity.longitudeMin)
        && Objects.equals(longitudeMax, entity.longitudeMax) && Objects.equals(ocean, entity.ocean) && Objects.equals(
        profilerType, entity.profilerType) && Objects.equals(institution, entity.institution) && Objects.equals(dateUpdate,
        entity.dateUpdate) && Objects.equals(parameters, entity.parameters) && Objects.equals(parameterDataMode,
        entity.parameterDataMode);
  }

  @Override
  public int hashCode() {
    return Objects.hash(file, dac, floatId, fileType, fileStatus, date, latitude, latitudeMin, latitudeMax, longitude, longitudeMin, longitudeMax,
        ocean, profilerType, institution, dateUpdate, parameters, parameterDataMode, floatMerged);
  }

  @Override
  public String toString() {
    return "IndexEntity{" +
        "file='" + file + '\'' +
        ", dac='" + dac + '\'' +
        ", floatId='" + floatId + '\'' +
        ", fileType='" + fileType + '\'' +
        ", fileStatus='" + fileStatus + '\'' +
        ", date=" + date +
        ", latitude=" + latitude +
        ", latitudeMin=" + latitudeMin +
        ", latitudeMax=" + latitudeMax +
        ", longitude=" + longitude +
        ", longitudeMin=" + longitudeMin +
        ", longitudeMax=" + longitudeMax +
        ", ocean='" + ocean + '\'' +
        ", profilerType='" + profilerType + '\'' +
        ", institution='" + institution + '\'' +
        ", dateUpdate=" + dateUpdate +
        ", parameters='" + parameters + '\'' +
        ", parameterDataMode='" + parameterDataMode + '\'' +
        ", floatMerged=" + floatMerged +
        '}';
  }

  public static IndexEntity fromMetadataRecord(MetadataRecord metadataRecord) {
    IndexEntity entity = new IndexEntity();
    entity.setFile(metadataRecord.getFile());
    entity.setDate(metadataRecord.getDate() == null ? null : metadataRecord.getDate().atOffset(ZoneOffset.UTC).toZonedDateTime());
    entity.setLatitude(metadataRecord.getLatitude());
    entity.setLatitudeMin(metadataRecord.getLatitudeMin());
    entity.setLatitudeMax(metadataRecord.getLatitudeMax());
    entity.setLongitude(metadataRecord.getLongitude());
    entity.setLongitudeMin(metadataRecord.getLongitudeMin());
    entity.setLongitudeMax(metadataRecord.getLongitudeMax());
    entity.setOcean(metadataRecord.getOcean() == null ? null : metadataRecord.getOcean().getCode());
    entity.setProfilerType(metadataRecord.getProfilerType());
    entity.setInstitution(metadataRecord.getInstitution());
    entity.setDateUpdate(metadataRecord.getDateUpdate() == null ? null : metadataRecord.getDateUpdate().atOffset(ZoneOffset.UTC).toZonedDateTime());
    entity.setParameters(metadataRecord.getParameters());
    entity.setParameterDataMode(metadataRecord.getParameterDataMode());
    entity.setFileType(metadataRecord.getFileType().name());
    entity.setDac(metadataRecord.getDac());
    entity.setFloatId(metadataRecord.getFloatId());
    // not setting floatMerged
    // not setting fileStatus
    return entity;
  }

  public MetadataRecord toMetadataRecord() {
    return MetadataRecord.builder()
        .withFile(file)
        .withDate(date == null ? null : date.toInstant())
        .withLatitude(latitude)
        .withLatitudeMin(latitudeMin)
        .withLatitudeMax(latitudeMax)
        .withLongitude(longitude)
        .withLongitudeMin(longitudeMin)
        .withLongitudeMax(longitudeMax)
        .withOcean(ocean == null ? null : ArgoOcean.fromCode(ocean))
        .withProfilerType(profilerType)
        .withInstitution(institution)
        .withDateUpdate(dateUpdate == null ? null : dateUpdate.toInstant())
        .withParameters(parameters)
        .withParameterDataMode(parameterDataMode)
        .withFileType(FileType.valueOf(fileType))
        .withFileStatus(FileStatus.valueOf(fileStatus))
        .withDac(dac)
        .withFloatId(floatId)
        .withFloatMerged(floatMerged)
        .build();
  }
}
