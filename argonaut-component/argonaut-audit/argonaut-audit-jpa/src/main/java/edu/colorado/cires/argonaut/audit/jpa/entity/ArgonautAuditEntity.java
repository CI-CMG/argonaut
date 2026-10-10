package edu.colorado.cires.argonaut.audit.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "argonaut_audit", indexes = {
    @Index(name = "argonaut_audit_trace_id_idx", columnList = "trace_id"),
    @Index(name = "argonaut_audit_dac_idx", columnList = "dac_name"),
    @Index(name = "argonaut_audit_event_type_idx", columnList = "event_type"),
    @Index(name = "argonaut_audit_processor_idx", columnList = "processor")
})
public class ArgonautAuditEntity {

  @Id
  @Column(name = "id", length = 36, nullable = false)
  private String id;

  @Column(name = "trace_id", length = 36, nullable = false)
  private String traceId;

  @Column(name = "dac_name", nullable = false, length = 10)
  private String dacName;

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

  @Column(name = "report_date")
  private ZonedDateTime reportDate;

  public UUID getId() {
    return id == null ? null : UUID.fromString(id);
  }

  public void setId(UUID id) {
    this.id = id == null ? null : id.toString();
  }

  public UUID getTraceId() {
    return traceId == null ? null : UUID.fromString(traceId);
  }

  public void setTraceId(UUID traceId) {
    this.traceId = traceId == null ? null : traceId.toString();
  }

  public String getDacName() {
    return dacName;
  }

  public void setDacName(String dac) {
    this.dacName = dac;
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

  public ZonedDateTime getReportDate() {
    return reportDate;
  }

  public void setReportDate(ZonedDateTime reportDate) {
    this.reportDate = reportDate;
  }

  @Override
  public String toString() {
    return "ArgonautAuditEntity{" +
        "id='" + id + '\'' +
        ", traceId='" + traceId + '\'' +
        ", dacName='" + dacName + '\'' +
        ", timestamp=" + timestamp +
        ", eventType='" + eventType + '\'' +
        ", processor='" + processor + '\'' +
        ", message='" + message + '\'' +
        ", stackTrace='" + stackTrace + '\'' +
        ", fileName='" + fileName + '\'' +
        ", reportDate=" + reportDate +
        '}';
  }
}
