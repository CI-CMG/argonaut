package edu.colorado.cires.argonaut.messaging.core.databind;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import edu.colorado.cires.argonaut.messaging.core.databind.NcSubmissionMessage.FileType;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
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
    SYNTHETIC_MERGE,
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
    private String direction;
    private String cycleNumber;
    private Action action;
    private FileType fileType;
    private FileStatus fileStatus;
    private String dac;
    private String floatId;
    private Instant actionTimestamp = Instant.now();
    private Map<String, Object> otherFields = new HashMap<>();

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
      direction = source.direction;
      cycleNumber = source.cycleNumber;
      action = source.action;
      fileType = source.fileType;
      fileStatus = source.fileStatus;
      dac = source.dac;
      floatId = source.floatId;
      actionTimestamp = source.actionTimestamp;
      otherFields.putAll(source.otherFields);
    }

    public Builder withCycleNumber(String cycleNumber) {
      this.cycleNumber = cycleNumber;
      return this;
    }

    public Builder withDirection(String direction) {
      this.direction = direction;
      return this;
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

    public Builder withActionTimestamp(Instant actionTimestamp) {
      if (actionTimestamp == null) {
        this.actionTimestamp = Instant.now();
      } else {
        this.actionTimestamp = actionTimestamp;
      }
      return this;
    }

    @Deprecated
    @JsonAnySetter
    private Builder withOtherField(String name, Object value) {
      this.otherFields.put(name, value);
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
          direction,
          cycleNumber,
          action,
          fileType,
          fileStatus,
          dac,
          floatId,
          actionTimestamp,
          otherFields
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
  private final String direction;
  private final String cycleNumber;
  private final Action action;
  private final FileType fileType;
  private final FileStatus fileStatus;
  private final String dac;
  private final String floatId;
  private final Instant actionTimestamp;
  private final Map<String, Object> otherFields;

  private MetadataRecord(String file, Instant date, Double latitude, Double latitudeMin, Double latitudeMax, Double longitude, Double longitudeMin,
      Double longitudeMax, ArgoOcean ocean, String profilerType, String institution, Instant dateUpdate, String parameters, String parameterDataMode,
      String direction, String cycleNumber, Action action, FileType fileType, FileStatus fileStatus, String dac, String floatId, Instant actionTimestamp,
      Map<String, Object> otherFields) {
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
    this.direction = direction;
    this.cycleNumber = cycleNumber;
    this.action = action;
    this.fileType = fileType;
    this.fileStatus = fileStatus;
    this.dac = dac;
    this.floatId = floatId;
    this.actionTimestamp = actionTimestamp;
    this.otherFields = Collections.unmodifiableMap(new HashMap<>(otherFields));
  }

  public String getCycleNumber() {
    return cycleNumber;
  }

  public String getDirection() {
    return direction;
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

  public Instant getActionTimestamp() {
    return actionTimestamp;
  }

  @Deprecated
  @JsonAnyGetter
  public Map<String, Object> getOtherFields() {
    return otherFields;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MetadataRecord that = (MetadataRecord) o;
    return Objects.equals(file, that.file) && instantsEquals(date, that.date) && Objects.equals(latitude, that.latitude)
        && Objects.equals(latitudeMin, that.latitudeMin) && Objects.equals(latitudeMax, that.latitudeMax)
        && Objects.equals(longitude, that.longitude) && Objects.equals(longitudeMin, that.longitudeMin) && Objects.equals(
        longitudeMax, that.longitudeMax) && ocean == that.ocean && Objects.equals(profilerType, that.profilerType)
        && Objects.equals(institution, that.institution) && instantsEquals(dateUpdate, that.dateUpdate) && Objects.equals(
        parameters, that.parameters) && Objects.equals(parameterDataMode, that.parameterDataMode) && Objects.equals(direction,
        that.direction) && Objects.equals(cycleNumber, that.cycleNumber) && action == that.action && fileType == that.fileType
        && fileStatus == that.fileStatus && Objects.equals(dac, that.dac) && Objects.equals(floatId, that.floatId)
        && instantsEquals(actionTimestamp, that.actionTimestamp) && Objects.equals(otherFields, that.otherFields);
  }

  private static boolean instantsEquals(Instant instant1, Instant instant2) {
    if (instant1 == null && instant2 == null) {
      return true;
    }
    if (instant1 == null || instant2 == null) {
      return false;
    }
    return instant1.toEpochMilli() == instant2.toEpochMilli();
  }

  @Override
  public int hashCode() {
    return Objects.hash(file, date, latitude, latitudeMin, latitudeMax, longitude, longitudeMin, longitudeMax, ocean, profilerType, institution,
        dateUpdate, parameters, parameterDataMode, direction, cycleNumber, action, fileType, fileStatus, dac, floatId, actionTimestamp, otherFields);
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
        ", direction='" + direction + '\'' +
        ", cycleNumber='" + cycleNumber + '\'' +
        ", action=" + action +
        ", fileType=" + fileType +
        ", fileStatus=" + fileStatus +
        ", dac='" + dac + '\'' +
        ", floatId='" + floatId + '\'' +
        ", actionTimestamp=" + actionTimestamp +
        ", otherFields=" + otherFields +
        '}';
  }

}
