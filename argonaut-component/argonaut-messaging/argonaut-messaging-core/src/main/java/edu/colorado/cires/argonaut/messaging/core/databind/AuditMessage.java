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

@JsonDeserialize(builder = AuditMessage.Builder.class)
public class AuditMessage {

  public static Builder builder() {
    return new Builder();
  }

  public static Builder builder(AuditMessage source) {
    return new Builder(source);
  }

  public enum EventType {
    INFO,
    WARNING,
    ERROR
  }

  public static final class Builder {

    private UUID traceId;
    private String dac;
    private Instant timestamp;
    private EventType eventType;
    private AuditEventProcessor processor;
    private String message;
    private String stackTrace;
    private String fileName;
    private final Map<String, Object> otherFields = new HashMap<>();

    private Builder() {

    }

    private Builder(AuditMessage source) {
      this.traceId = source.traceId;
      this.dac = source.dac;
      this.timestamp = source.timestamp;
      this.eventType = source.eventType;
      this.processor = source.processor;
      this.message = source.message;
      this.stackTrace = source.stackTrace;
      this.fileName = source.fileName;
      otherFields.putAll(source.otherFields);
    }

    @Deprecated
    @JsonAnySetter
    private Builder withOtherField(String name, Object value) {
      this.otherFields.put(name, value);
      return this;
    }

    public Builder withTraceId(UUID traceId) {
      this.traceId = traceId;
      return this;
    }

    public Builder withDac(String dac) {
      this.dac = dac;
      return this;
    }

    public Builder withTimestamp(Instant timestamp) {
      this.timestamp = timestamp;
      return this;
    }

    public Builder withEventType(EventType eventType) {
      this.eventType = eventType;
      return this;
    }

    public Builder withProcessor(AuditEventProcessor processor) {
      this.processor = processor;
      return this;
    }

    public Builder withMessage(String message) {
      this.message = message;
      return this;
    }

    public Builder withStackTrace(String stackTrace) {
      this.stackTrace = stackTrace;
      return this;
    }

    public Builder withFileName(String fileName) {
      this.fileName = fileName;
      return this;
    }

    public AuditMessage build() {
      return new AuditMessage(traceId, dac, timestamp, eventType, processor, message, stackTrace, fileName, otherFields);
    }
  }

  private final UUID traceId;
  private final String dac;
  private final Instant timestamp;
  private final EventType eventType;
  private final AuditEventProcessor processor;
  private final String message;
  private final String stackTrace;
  private final String fileName;
  private final Map<String, Object> otherFields;

  private AuditMessage(UUID traceId, String dac, Instant timestamp, EventType eventType, AuditEventProcessor processor, String message, String stackTrace, String fileName,
      Map<String, Object> otherFields) {
    this.traceId = traceId;
    this.dac = dac;
    this.timestamp = timestamp;
    this.eventType = eventType;
    this.processor = processor;
    this.message = message;
    this.stackTrace = stackTrace;
    this.fileName = fileName;
    this.otherFields = Collections.unmodifiableMap(new HashMap<>(otherFields));
  }

  public AuditEventProcessor getProcessor() {
    return processor;
  }

  public UUID getTraceId() {
    return traceId;
  }

  public String getDac() {
    return dac;
  }

  public Instant getTimestamp() {
    return timestamp;
  }

  public EventType getEventType() {
    return eventType;
  }

  public String getMessage() {
    return message;
  }

  public String getStackTrace() {
    return stackTrace;
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
    AuditMessage that = (AuditMessage) o;
    return Objects.equals(traceId, that.traceId) && Objects.equals(dac, that.dac) && Objects.equals(timestamp,
        that.timestamp) && eventType == that.eventType && Objects.equals(processor, that.processor) && Objects.equals(message,
        that.message) && Objects.equals(stackTrace, that.stackTrace) && Objects.equals(fileName, that.fileName)
        && Objects.equals(otherFields, that.otherFields);
  }

  @Override
  public int hashCode() {
    return Objects.hash(traceId, dac, timestamp, eventType, processor, message, stackTrace, fileName, otherFields);
  }

  @Override
  public String toString() {
    return "AuditMessage{" +
        "traceId=" + traceId +
        ", dac='" + dac + '\'' +
        ", timestamp=" + timestamp +
        ", eventType=" + eventType +
        ", processor='" + processor + '\'' +
        ", message='" + message + '\'' +
        ", stackTrace='" + stackTrace + '\'' +
        ", fileName='" + fileName + '\'' +
        ", otherFields=" + otherFields +
        '}';
  }
}
