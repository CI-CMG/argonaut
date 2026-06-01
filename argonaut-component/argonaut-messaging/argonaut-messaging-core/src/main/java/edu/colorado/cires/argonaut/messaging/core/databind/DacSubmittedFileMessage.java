package edu.colorado.cires.argonaut.messaging.core.databind;


import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import tools.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = DacSubmittedFileMessage.Builder.class)
public final class DacSubmittedFileMessage implements TracedMessage {

  public static Builder builder() {
    return new Builder();
  }

  public static Builder builder(DacSubmittedFileMessage source) {
    return new Builder(source);
  }


  public static final class Builder {

    private String path;
    private Instant timestamp;
    private String dac;
    private UUID traceId;
    private String fileName;
    private final Map<String, Object> otherFields = new HashMap<>();

    private Builder() {

    }

    private Builder(DacSubmittedFileMessage source) {
      path = source.path;
      timestamp = source.timestamp;
      dac = source.dac;
      traceId = source.traceId;
      fileName = source.fileName;
      otherFields.putAll(source.otherFields);
    }

    public Builder withPath(String path) {
      this.path = path;
      return this;
    }

    public Builder withTimestamp(Instant timestamp) {
      this.timestamp = timestamp;
      return this;
    }

    public Builder withDac(String dac) {
      this.dac = dac;
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

    public DacSubmittedFileMessage build() {
      return new DacSubmittedFileMessage(path, timestamp, dac, traceId, fileName, otherFields);
    }
  }

  private final String path;
  private final Instant timestamp;
  private final String dac;
  private final UUID traceId;
  private final String fileName;
  private final Map<String, Object> otherFields;

  private DacSubmittedFileMessage(String path, Instant timestamp, String dac, UUID traceId, String fileName, Map<String, Object> otherFields) {
    this.path = path;
    this.timestamp = timestamp;
    this.dac = dac;
    this.traceId = traceId;
    this.fileName = fileName;
    this.otherFields = Collections.unmodifiableMap(new HashMap<>(otherFields));
  }

  public Instant getTimestamp() {
    return timestamp;
  }

  public String getDac() {
    return dac;
  }

  public String getPath() {
    return path;
  }

  public UUID getTraceId() {
    return traceId;
  }

  public String getFileName() {
    return fileName;
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
    DacSubmittedFileMessage that = (DacSubmittedFileMessage) o;
    return Objects.equals(path, that.path) && Objects.equals(timestamp, that.timestamp) && Objects.equals(dac, that.dac)
        && Objects.equals(traceId, that.traceId) && Objects.equals(fileName, that.fileName) && Objects.equals(otherFields,
        that.otherFields);
  }

  @Override
  public int hashCode() {
    return Objects.hash(path, timestamp, dac, traceId, fileName, otherFields);
  }

  @Override
  public String toString() {
    return "DacSubmittedFileMessage{" +
        "path='" + path + '\'' +
        ", timestamp=" + timestamp +
        ", dac='" + dac + '\'' +
        ", traceId=" + traceId +
        ", fileName='" + fileName + '\'' +
        ", otherFields=" + otherFields +
        '}';
  }

}
