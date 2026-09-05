package edu.colorado.cires.argonaut.messaging.core.databind;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import tools.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = DacFloatFilePath.Builder.class)
public class DacFloatFilePath {

  public static Builder builder() {
    return new Builder();
  }

  public static Builder builder(DacFloatFilePath source) {
    return new Builder(source);
  }

  private final String dac;
  private final String floatId;
  private final String file;
  private final Map<String, Object> otherFields;

  private DacFloatFilePath(String dac, String floatId, String file, Map<String, Object> otherFields) {
    this.dac = dac;
    this.floatId = floatId;
    this.file = file;
    this.otherFields = Collections.unmodifiableMap(new HashMap<>(otherFields));
  }

  public String getDac() {
    return dac;
  }

  public String getFloatId() {
    return floatId;
  }

  public String getFile() {
    return file;
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
    DacFloatFilePath that = (DacFloatFilePath) o;
    return Objects.equals(dac, that.dac) && Objects.equals(floatId, that.floatId) && Objects.equals(file, that.file)
        && Objects.equals(otherFields, that.otherFields);
  }

  @Override
  public int hashCode() {
    return Objects.hash(dac, floatId, file, otherFields);
  }

  @Override
  public String toString() {
    return "DacFloatFilePath{" +
        "dac='" + dac + '\'' +
        ", floatId='" + floatId + '\'' +
        ", file='" + file + '\'' +
        ", otherFields=" + otherFields +
        '}';
  }

  public static final class Builder {

    private String dac;
    private String floatId;
    private String file;
    private Map<String, Object> otherFields = new HashMap<>();

    private Builder() {

    }

    private Builder(DacFloatFilePath source) {
      dac = source.dac;
      floatId = source.floatId;
      file = source.file;
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

    public Builder withFile(String file) {
      this.file = file;
      return this;
    }

    @Deprecated
    @JsonAnySetter
    private Builder withOtherField(String name, Object value) {
      this.otherFields.put(name, value);
      return this;
    }

    public DacFloatFilePath build() {
      return new DacFloatFilePath(dac, floatId, file, otherFields);
    }
  }
}
