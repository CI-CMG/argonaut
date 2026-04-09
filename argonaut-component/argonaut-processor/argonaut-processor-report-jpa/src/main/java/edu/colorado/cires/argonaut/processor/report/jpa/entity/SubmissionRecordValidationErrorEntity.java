package edu.colorado.cires.argonaut.processor.report.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "submission_record_validation_error")
public class SubmissionRecordValidationErrorEntity {

  @Id
  @Column(name = "id", nullable = false)
  private UUID id;
  @ManyToOne
  @JoinColumn(name = "submission_record", nullable = false)
  private SubmissionRecordEntity submissionRecord;
  @Column(name = "message", nullable = false, length = 500)
  private String message;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public SubmissionRecordEntity getSubmissionRecord() {
    return submissionRecord;
  }

  public void setSubmissionRecord(SubmissionRecordEntity submissionRecord) {
    this.submissionRecord = submissionRecord;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SubmissionRecordValidationErrorEntity that = (SubmissionRecordValidationErrorEntity) o;
    return Objects.equals(id, that.id) && Objects.equals(submissionRecord.getId(), that.submissionRecord.getId()) && Objects.equals(
        message, that.message);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public String toString() {
    return "SubmissionRecordValidationErrorEntity{" +
        "id=" + id +
//        ", submissionRecord=" + submissionRecord == null ? "null" : submissionRecord.getId() +
        ", message='" + message + '\'' +
        '}';
  }
}
