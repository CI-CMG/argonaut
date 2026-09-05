package edu.colorado.cires.argonaut.messaging.core.databind;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import edu.colorado.cires.argonaut.messaging.core.databind.ProfileOperation.Builder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import tools.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = GeoMergeInfo.Builder.class)
public class GeoMergeInfo {

  public static Builder builder() {
    return new Builder();
  }

  public static Builder builder(GeoMergeInfo source) {
    return new Builder(source);
  }

  private final UUID traceId;
  private final Integer year;
  private final Integer month;
  private final Integer day;
  private final ArgoOcean ocean;
  private final List<DacFloatFilePath> files;
  private final Map<String, Object> otherFields;

  private GeoMergeInfo(UUID traceId, Integer year, Integer month, Integer day,
      ArgoOcean ocean, List<DacFloatFilePath> files, Map<String, Object> otherFields) {
    this.traceId = traceId;
    this.year = year;
    this.month = month;
    this.day = day;
    this.ocean = ocean;
    this.files = files;
    this.otherFields = Collections.unmodifiableMap(new HashMap<>(otherFields));
  }

  public UUID getTraceId() {
    return traceId;
  }

  public Integer getYear() {
    return year;
  }

  public Integer getMonth() {
    return month;
  }

  public Integer getDay() {
    return day;
  }

  public ArgoOcean getOcean() {
    return ocean;
  }

  public List<DacFloatFilePath> getFiles() {
    return files;
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
    GeoMergeInfo that = (GeoMergeInfo) o;
    return Objects.equals(traceId, that.traceId) && Objects.equals(year, that.year) && Objects.equals(month, that.month)
        && Objects.equals(day, that.day) && ocean == that.ocean && Objects.equals(files, that.files) && Objects.equals(
        otherFields, that.otherFields);
  }

  @Override
  public int hashCode() {
    return Objects.hash(traceId, year, month, day, ocean, files, otherFields);
  }

  @Override
  public String toString() {
    return "GeoMergeInfo{" +
        "traceId=" + traceId +
        ", year=" + year +
        ", month=" + month +
        ", day=" + day +
        ", ocean=" + ocean +
        ", files=" + files +
        ", otherFields=" + otherFields +
        '}';
  }

  public static final class Builder {

    private UUID traceId;
    private Integer year;
    private Integer month;
    private Integer day;
    private ArgoOcean ocean;
    private List<DacFloatFilePath> files = Collections.emptyList();
    private Map<String, Object> otherFields = new HashMap<>();

    private Builder() {

    }

    private Builder(GeoMergeInfo source) {
      traceId = source.traceId;
      year = source.year;
      month = source.month;
      day = source.day;
      ocean = source.ocean;
      files = source.files;
      otherFields.putAll(source.otherFields);
    }

    public Builder withTraceId(UUID traceId) {
      this.traceId = traceId;
      return this;
    }


    public Builder withYear(Integer year) {
      this.year = year;
      return this;
    }

    public Builder withMonth(Integer month) {
      this.month = month;
      return this;
    }

    public Builder withDay(Integer day) {
      this.day = day;
      return this;
    }

    public Builder withOcean(ArgoOcean ocean) {
      this.ocean = ocean;
      return this;
    }

    public Builder withFiles(List<DacFloatFilePath> fileNames) {
      if (fileNames == null) {
        this.files = Collections.emptyList();
      } else {
        this.files = Collections.unmodifiableList(new ArrayList<>(fileNames));
      }
      return this;
    }

    @Deprecated
    @JsonAnySetter
    private Builder withOtherField(String name, Object value) {
      this.otherFields.put(name, value);
      return this;
    }

    public GeoMergeInfo build() {
      return new GeoMergeInfo(traceId, year, month, day, ocean, files, otherFields);
    }
  }
}
