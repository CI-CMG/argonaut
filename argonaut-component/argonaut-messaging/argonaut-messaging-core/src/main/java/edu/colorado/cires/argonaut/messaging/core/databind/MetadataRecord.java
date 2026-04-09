package edu.colorado.cires.argonaut.messaging.core.databind;

import java.time.Instant;
import tools.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = MetadataRecord.Builder.class)
public class MetadataRecord {

  public static Builder builder() {
    return new Builder();
  }

  public static Builder builder(MetadataRecord source) {
    return new Builder(source);
  }

  public enum Ocean {
    A
  }

  public enum Action {
    UPDATE,
    REMOVE,
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
    private Ocean ocean;
    private String profilerType;
    private String institution;
    private Instant dateUpdate;
    private String parameters;
    private String parameterDataMode;
    private Action action;

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

    public Builder withOcean(Ocean ocean) {
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
          action
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
  private final Ocean ocean;
  private final String profilerType;
  private final String institution;
  private final Instant dateUpdate;
  private final String parameters;
  private final String parameterDataMode;
  private final Action action;

  private MetadataRecord(String file, Instant date, Double latitude, Double latitudeMin, Double latitudeMax, Double longitude, Double longitudeMin,
      Double longitudeMax, Ocean ocean, String profilerType, String institution, Instant dateUpdate, String parameters, String parameterDataMode,
      Action action) {
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

  public Ocean getOcean() {
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
}
