package edu.colorado.cires.argonaut.messaging.core.databind;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import tools.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = ProfileOperation.Builder.class)
public class ProfileOperation implements TracedMessage{

  public static Builder builder() {
    return new Builder();
  }

  public static Builder builder(ProfileOperation source) {
    return new Builder(source);
  }

  private final String dac;
  private final String floatId;
  private final List<String> files;
  private final UUID traceId;
  private final String fileName;
  private final Map<String, Object> otherFields;

  private ProfileOperation(String dac, String floatId, List<String> files, UUID traceId, String fileName, Map<String, Object> otherFields) {
    this.dac = dac;
    this.floatId = floatId;
    this.files = files;
    this.traceId = traceId;
    this.fileName = fileName;
    this.otherFields = Collections.unmodifiableMap(new HashMap<>(otherFields));
  }

  public String getDac() {
    return dac;
  }

  @Override
  public String getFileName() {
    return fileName;
  }

  @Override
  public UUID getTraceId() {
    return traceId;
  }

  public String getFloatId() {
    return floatId;
  }

  public List<String> getFiles() {
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
    ProfileOperation that = (ProfileOperation) o;
    return Objects.equals(dac, that.dac) && Objects.equals(floatId, that.floatId) && Objects.equals(files, that.files)
        && Objects.equals(traceId, that.traceId) && Objects.equals(fileName, that.fileName) && Objects.equals(otherFields,
        that.otherFields);
  }

  @Override
  public int hashCode() {
    return Objects.hash(dac, floatId, files, traceId, fileName, otherFields);
  }

  @Override
  public String toString() {
    return "ProfileOperation{" +
        "dac='" + dac + '\'' +
        ", floatId='" + floatId + '\'' +
        ", files=" + files +
        ", traceId=" + traceId +
        ", fileName='" + fileName + '\'' +
        ", otherFields=" + otherFields +
        '}';
  }

  public static final class Builder {
    private String dac;
    private String floatId;
    private List<String> files = Collections.emptyList();
    private UUID traceId;
    private String fileName;
    private Map<String, Object> otherFields = new HashMap<>();

    private Builder() {

    }

    private Builder(ProfileOperation source) {
      dac = source.dac;
      floatId = source.floatId;
      files = source.files;
      traceId = source.traceId;
      fileName = source.fileName;
      otherFields.putAll(source.otherFields);
    }

    public Builder withDac(String dac) {
      this.dac = dac;
      return this;
    }

    public Builder withFloatId(String floatId) {
      this.floatId = floatId;
      return this;
    }

    public Builder withFiles(List<String> fileNames) {
      if (fileNames == null) {
        this.files = Collections.emptyList();
      } else {
        this.files = Collections.unmodifiableList(new ArrayList<>(fileNames));
      }
      return this;
    }

    public Builder withTraceId(UUID traceId) {
      this.traceId = traceId;
      return this;
    }

    public Builder withFileName(String fileName) {
      this.fileName = fileName;
      return this;
    }

    @Deprecated
    @JsonAnySetter
    private Builder withOtherField(String name, Object value) {
      this.otherFields.put(name, value);
      return this;
    }

    public ProfileOperation build() {
      return new ProfileOperation(dac, floatId, files, traceId, fileName, otherFields);
    }
  }
}
