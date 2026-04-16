package edu.colorado.cires.argonaut.messaging.core.databind;

import edu.colorado.cires.argonaut.messaging.core.databind.NcSubmissionMessage.FileType;
import java.time.Instant;
import java.util.Objects;
import tools.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = MetadataRecord.Builder.class)
public class MetadataRecord {


  public enum FileStatus {
    ACTIVE,
    REMOVED
  }

  public static Builder builder() {
    return new Builder();
  }

  public static Builder builder(MetadataRecord source) {
    return new Builder(source);
  }

  public enum Action {
    UPDATE,
    REMOVE,
    FLOAT_MERGE,
    NONE
  }

  public static final class Builder {

    private String file;
    private Instant date;
    private Double latitude;
    private Double latitudeMin;
    private Double latitudeMax;
    private Double longitude;
    private Double longitudeMin;
    private Double longitudeMax;
    private ArgoOcean ocean;
    private String profilerType;
    private String institution;
    private Instant dateUpdate;
    private String parameters;
    private String parameterDataMode;
    private Action action;
    private FileType fileType;
    private FileStatus fileStatus;
    private String dac;
    private String floatId;
    private boolean floatMerged;

    private Builder() {

    }

    private Builder(MetadataRecord source) {
      file = source.file;
      date = source.date;
      latitude = source.latitude;
      latitudeMin = source.latitudeMin;
      latitudeMax = source.latitudeMax;
      longitude = source.longitude;
      longitudeMin = source.longitudeMin;
      longitudeMax = source.longitudeMax;
      ocean = source.ocean;
      profilerType = source.profilerType;
      institution = source.institution;
      dateUpdate = source.dateUpdate;
      parameters = source.parameters;
      parameterDataMode = source.parameterDataMode;
      action = source.action;
      fileType = source.fileType;
      fileStatus = source.fileStatus;
      dac = source.dac;
      floatId = source.floatId;
      floatMerged = source.floatMerged;
    }

    public Builder withFile(String file) {
      this.file = file;
      return this;
    }

    public Builder withDate(Instant date) {
      this.date = date;
      return this;
    }

    public Builder withLatitude(Double latitude) {
      this.latitude = latitude;
      return this;
    }

    public Builder withLatitudeMin(Double latitudeMin) {
      this.latitudeMin = latitudeMin;
      return this;
    }

    public Builder withLatitudeMax(Double latitudeMax) {
      this.latitudeMax = latitudeMax;
      return this;
    }

    public Builder withLongitude(Double longitude) {
      this.longitude = longitude;
      return this;
    }

    public Builder withLongitudeMin(Double longitudeMin) {
      this.longitudeMin = longitudeMin;
      return this;
    }

    public Builder withLongitudeMax(Double longitudeMax) {
      this.longitudeMax = longitudeMax;
      return this;
    }

    public Builder withOcean(ArgoOcean ocean) {
      this.ocean = ocean;
      return this;
    }

    public Builder withProfilerType(String profilerType) {
      this.profilerType = profilerType;
      return this;
    }

    public Builder withInstitution(String institution) {
      this.institution = institution;
      return this;
    }

    public Builder withDateUpdate(Instant dateUpdate) {
      this.dateUpdate = dateUpdate;
      return this;
    }

    public Builder withParameters(String parameters) {
      this.parameters = parameters;
      return this;
    }

    public Builder withParameterDataMode(String parameterDataMode) {
      this.parameterDataMode = parameterDataMode;
      return this;
    }

    public Builder withAction(Action action) {
      this.action = action;
      return this;
    }

    public Builder withFileType(FileType fileType) {
      this.fileType = fileType;
      return this;
    }

    public Builder withFileStatus(FileStatus fileStatus) {
      this.fileStatus = fileStatus;
      return this;
    }

    public Builder withDac(String dac) {
      this.dac = dac;
      return this;
    }

    public Builder withFloatId(String floatId) {
      this.floatId = floatId;
      return this;
    }

    public Builder withFloatMerged(boolean floatMerged) {
      this.floatMerged = floatMerged;
      return this;
    }

    public MetadataRecord build() {
      return new MetadataRecord(
          file,
          date,
          latitude,
          latitudeMin,
          latitudeMax,
          longitude,
          longitudeMin,
          longitudeMax,
          ocean,
          profilerType,
          institution,
          dateUpdate,
          parameters,
          parameterDataMode,
          action,
          fileType,
          fileStatus,
          dac,
          floatId,
          floatMerged
      );
    }

  }

  private final String file;
  private final Instant date;
  private final Double latitude;
  private final Double latitudeMin;
  private final Double latitudeMax;
  private final Double longitude;
  private final Double longitudeMin;
  private final Double longitudeMax;
  private final ArgoOcean ocean;
  private final String profilerType;
  private final String institution;
  private final Instant dateUpdate;
  private final String parameters;
  private final String parameterDataMode;
  private final Action action;
  private final FileType fileType;
  private final FileStatus fileStatus;
  private final String dac;
  private final String floatId;
  private final boolean floatMerged;

  private MetadataRecord(String file, Instant date, Double latitude, Double latitudeMin, Double latitudeMax, Double longitude, Double longitudeMin,
      Double longitudeMax, ArgoOcean ocean, String profilerType, String institution, Instant dateUpdate, String parameters, String parameterDataMode,
      Action action, FileType fileType, FileStatus fileStatus, String dac, String floatId, boolean floatMerged) {
    this.file = file;
    this.date = date;
    this.latitude = latitude;
    this.latitudeMin = latitudeMin;
    this.latitudeMax = latitudeMax;
    this.longitude = longitude;
    this.longitudeMin = longitudeMin;
    this.longitudeMax = longitudeMax;
    this.ocean = ocean;
    this.profilerType = profilerType;
    this.institution = institution;
    this.dateUpdate = dateUpdate;
    this.parameters = parameters;
    this.parameterDataMode = parameterDataMode;
    this.action = action;
    this.fileType = fileType;
    this.fileStatus = fileStatus;
    this.dac = dac;
    this.floatId = floatId;
    this.floatMerged = floatMerged;
  }

  public String getFile() {
    return file;
  }

  public Instant getDate() {
    return date;
  }

  public Double getLatitude() {
    return latitude;
  }

  public Double getLatitudeMin() {
    return latitudeMin;
  }

  public Double getLatitudeMax() {
    return latitudeMax;
  }

  public Double getLongitude() {
    return longitude;
  }

  public Double getLongitudeMin() {
    return longitudeMin;
  }

  public Double getLongitudeMax() {
    return longitudeMax;
  }

  public ArgoOcean getOcean() {
    return ocean;
  }

  public String getProfilerType() {
    return profilerType;
  }

  public String getInstitution() {
    return institution;
  }

  public Instant getDateUpdate() {
    return dateUpdate;
  }

  public String getParameters() {
    return parameters;
  }

  public String getParameterDataMode() {
    return parameterDataMode;
  }

  public Action getAction() {
    return action;
  }

  public FileType getFileType() {
    return fileType;
  }

  public FileStatus getFileStatus() {
    return fileStatus;
  }

  public String getDac() {
    return dac;
  }

  public String getFloatId() {
    return floatId;
  }

  public boolean isFloatMerged() {
    return floatMerged;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MetadataRecord that = (MetadataRecord) o;
    return floatMerged == that.floatMerged && Objects.equals(file, that.file) && Objects.equals(date, that.date)
        && Objects.equals(latitude, that.latitude) && Objects.equals(latitudeMin, that.latitudeMin) && Objects.equals(
        latitudeMax, that.latitudeMax) && Objects.equals(longitude, that.longitude) && Objects.equals(longitudeMin,
        that.longitudeMin) && Objects.equals(longitudeMax, that.longitudeMax) && ocean == that.ocean && Objects.equals(profilerType,
        that.profilerType) && Objects.equals(institution, that.institution) && Objects.equals(dateUpdate, that.dateUpdate)
        && Objects.equals(parameters, that.parameters) && Objects.equals(parameterDataMode, that.parameterDataMode)
        && action == that.action && fileType == that.fileType && fileStatus == that.fileStatus && Objects.equals(dac, that.dac)
        && Objects.equals(floatId, that.floatId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(file, date, latitude, latitudeMin, latitudeMax, longitude, longitudeMin, longitudeMax, ocean, profilerType, institution,
        dateUpdate, parameters, parameterDataMode, action, fileType, fileStatus, dac, floatId, floatMerged);
  }

  @Override
  public String toString() {
    return "MetadataRecord{" +
        "file='" + file + '\'' +
        ", date=" + date +
        ", latitude=" + latitude +
        ", latitudeMin=" + latitudeMin +
        ", latitudeMax=" + latitudeMax +
        ", longitude=" + longitude +
        ", longitudeMin=" + longitudeMin +
        ", longitudeMax=" + longitudeMax +
        ", ocean=" + ocean +
        ", profilerType='" + profilerType + '\'' +
        ", institution='" + institution + '\'' +
        ", dateUpdate=" + dateUpdate +
        ", parameters='" + parameters + '\'' +
        ", parameterDataMode='" + parameterDataMode + '\'' +
        ", action=" + action +
        ", fileType=" + fileType +
        ", fileStatus=" + fileStatus +
        ", dac='" + dac + '\'' +
        ", floatId='" + floatId + '\'' +
        ", floatMerged=" + floatMerged +
        '}';
  }

}
