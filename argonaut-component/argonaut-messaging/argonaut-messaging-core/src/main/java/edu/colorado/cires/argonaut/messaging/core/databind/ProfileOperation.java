package edu.colorado.cires.argonaut.messaging.core.databind;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import tools.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = ProfileOperation.Builder.class)
public class ProfileOperation {

  public static Builder builder() {
    return new Builder();
  }

  public static Builder builder(ProfileOperation source) {
    return new Builder(source);
  }

  private final String dac;
  private final String floatId;
  private final List<String> files;
  private final Map<String, Object> otherFields;

  private ProfileOperation(String dac, String floatId, List<String> files, Map<String, Object> otherFields) {
    this.dac = dac;
    this.floatId = floatId;
    this.files = files;
    this.otherFields = Collections.unmodifiableMap(new HashMap<>(otherFields));
  }

  public String getDac() {
    return dac;
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
        && Objects.equals(otherFields, that.otherFields);
  }

  @Override
  public int hashCode() {
    return Objects.hash(dac, floatId, files, otherFields);
  }

  @Override
  public String toString() {
    return "ProfileOperation{" +
        "dac='" + dac + '\'' +
        ", floatId='" + floatId + '\'' +
        ", files=" + files +
        ", otherFields=" + otherFields +
        '}';
  }

  public static final class Builder {
    private String dac;
    private String floatId;
    private List<String> files = Collections.emptyList();
    private Map<String, Object> otherFields = new HashMap<>();

    private Builder() {

    }

    private Builder(ProfileOperation source) {
      dac = source.dac;
      floatId = source.floatId;
      files = source.files;
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

    @Deprecated
    @JsonAnySetter
    private Builder withOtherField(String name, Object value) {
      this.otherFields.put(name, value);
      return this;
    }

    public ProfileOperation build() {
      return new ProfileOperation(dac, floatId, files, otherFields);
    }
  }
}
