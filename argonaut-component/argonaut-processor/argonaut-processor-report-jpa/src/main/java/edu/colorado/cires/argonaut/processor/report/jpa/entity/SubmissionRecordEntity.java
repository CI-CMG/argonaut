package edu.colorado.cires.argonaut.processor.report.jpa.entity;

import edu.colorado.cires.argonaut.messaging.core.databind.NcSubmissionMessage;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Table(name = "submission_record")
public class SubmissionRecordEntity {

  public static SubmissionRecordEntity create(NcSubmissionMessage message) {
    SubmissionRecordEntity entity = new SubmissionRecordEntity();
    entity.setId(UUID.randomUUID());
    entity.setTimestamp(message.getTimestamp().atZone(ZoneId.of("UTC")));
    entity.setDac(message.getDac());
    entity.setFloatId(message.getFloatId());
    entity.setNumberOfFilesInSubmission(message.getNumberOfFilesInSubmission());
    entity.setFileName(message.getFileName());
    entity.setOperation(message.getOperation().name());
    if (message.getValidationErrors() != null && !message.getValidationErrors().isEmpty()) {
      entity.setSuccess(false);
      entity.setValidationErrors(message.getValidationErrors().stream().map(error -> {
        SubmissionRecordValidationErrorEntity errorEntity = new SubmissionRecordValidationErrorEntity();
        errorEntity.setSubmissionRecord(entity);
        errorEntity.setId(UUID.randomUUID());
        errorEntity.setMessage(error);
        return errorEntity;
      }).collect(Collectors.toList()));
    } else  {
      entity.setSuccess(true);
    }
    return entity;
  }

  @Id
  @Column(name = "id", nullable = false)
  private UUID id;
  @Column(name = "timestamp", nullable = false)
  private ZonedDateTime timestamp;
  @Column(name = "dac", nullable = false, length = 20)
  private String dac;
  @Column(name = "float_id", nullable = false, length = 11)
  private String floatId;
  @Column(name = "submission_file_count", nullable = false)
  private Integer numberOfFilesInSubmission;
  @Column(name = "file_name", nullable = false, length = 50)
  private String fileName;
  @Column(name = "success", nullable = false)
  private boolean success;
  @Column(name = "operation", nullable = false, length = 20)
  private String operation;
  @OneToMany(mappedBy = "submissionRecord", cascade = CascadeType.ALL, orphanRemoval = true)
  @OrderBy(value = "message ASC")
  private List<SubmissionRecordValidationErrorEntity> validationErrors = new ArrayList<>();

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public ZonedDateTime getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(ZonedDateTime timestamp) {
    this.timestamp = timestamp;
  }

  public String getDac() {
    return dac;
  }

  public void setDac(String dac) {
    this.dac = dac;
  }

  public String getFloatId() {
    return floatId;
  }

  public void setFloatId(String floatId) {
    this.floatId = floatId;
  }

  public Integer getNumberOfFilesInSubmission() {
    return numberOfFilesInSubmission;
  }

  public void setNumberOfFilesInSubmission(Integer numberOfFilesInSubmission) {
    this.numberOfFilesInSubmission = numberOfFilesInSubmission;
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public boolean isSuccess() {
    return success;
  }

  public void setSuccess(boolean success) {
    this.success = success;
  }

  public String getOperation() {
    return operation;
  }

  public void setOperation(String operation) {
    this.operation = operation;
  }

  public List<SubmissionRecordValidationErrorEntity> getValidationErrors() {
    return validationErrors;
  }

  public void setValidationErrors(List<SubmissionRecordValidationErrorEntity> validationErrors) {
    this.validationErrors = validationErrors;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SubmissionRecordEntity entity = (SubmissionRecordEntity) o;
    return success == entity.success && Objects.equals(id, entity.id) && Objects.equals(timestamp, entity.timestamp)
        && Objects.equals(dac, entity.dac) && Objects.equals(floatId, entity.floatId) && Objects.equals(
        numberOfFilesInSubmission, entity.numberOfFilesInSubmission) && Objects.equals(fileName, entity.fileName) && Objects.equals(
        operation, entity.operation) && Objects.equals(validationErrors, entity.validationErrors);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public String toString() {
    return "SubmissionRecordEntity{" +
        "id=" + id +
        ", timestamp=" + timestamp +
        ", dac='" + dac + '\'' +
        ", floatId='" + floatId + '\'' +
        ", numberOfFilesInSubmission=" + numberOfFilesInSubmission +
        ", fileName='" + fileName + '\'' +
        ", success=" + success +
        ", operation='" + operation + '\'' +
        ", validationErrors=" + validationErrors +
        '}';
  }
}
