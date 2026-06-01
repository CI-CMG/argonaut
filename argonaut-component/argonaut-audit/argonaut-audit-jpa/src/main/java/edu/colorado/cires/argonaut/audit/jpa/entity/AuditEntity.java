package edu.colorado.cires.argonaut.audit.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "audit")
public class AuditEntity {

  @Id
  private UUID id;

  @Column(name = "trace_id", nullable = false)
  private UUID traceId;

  @Column(name = "dac", nullable = false, length = 10)
  private String dac;

  @Column(name = "timestamp", nullable = false)
  private ZonedDateTime timestamp;

  @Column(name = "event_type", nullable = false, length = 10)
  private String eventType;

  @Column(name = "processor", nullable = false, length = 30)
  private String processor;

  @Column(name = "message", nullable = false, length = 255)
  private String message;

  @Lob
  @Column(name = "stack_trace")
  private String stackTrace;

  @Column(name = "file_name", nullable = false, length = 100)
  private String fileName;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public UUID getTraceId() {
    return traceId;
  }

  public void setTraceId(UUID traceId) {
    this.traceId = traceId;
  }

  public String getDac() {
    return dac;
  }

  public void setDac(String dac) {
    this.dac = dac;
  }

  public ZonedDateTime getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(ZonedDateTime timestamp) {
    this.timestamp = timestamp;
  }

  public String getEventType() {
    return eventType;
  }

  public void setEventType(String eventType) {
    this.eventType = eventType;
  }

  public String getProcessor() {
    return processor;
  }

  public void setProcessor(String processor) {
    this.processor = processor;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getStackTrace() {
    return stackTrace;
  }

  public void setStackTrace(String stackTrace) {
    this.stackTrace = stackTrace;
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  @Override
  public String toString() {
    return "AuditEntity{" +
        "id=" + id +
        ", traceId=" + traceId +
        ", dac='" + dac + '\'' +
        ", timestamp=" + timestamp +
        ", eventType='" + eventType + '\'' +
        ", processor='" + processor + '\'' +
        ", message='" + message + '\'' +
        ", stackTrace='" + stackTrace + '\'' +
        ", fileName='" + fileName + '\'' +
        '}';
  }
}
