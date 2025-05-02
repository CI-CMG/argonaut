package edu.colorado.cires.argonaut.message;

import java.util.List;
import java.util.Objects;
import org.apache.camel.Predicate;

public class NcSubmissionMessage {


  private String floatId;
  private List<String> validationError;
  private String timestamp;
  private String dac;
  private String fileName;
  private boolean profile;
  private int numberOfFilesInSubmission;

  public String getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(String timestamp) {
    this.timestamp = timestamp;
  }

  public String getDac() {
    return dac;
  }

  public void setDac(String dac) {
    this.dac = dac;
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public String getFloatId() {
    return floatId;
  }

  public void setFloatId(String floatId) {
    this.floatId = floatId;
  }

  public List<String> getValidationError() {
    return validationError;
  }

  public void setValidationError(List<String> validationError) {
    this.validationError = validationError;
  }

  public boolean isProfile() {
    return profile;
  }

  public void setProfile(boolean profile) {
    this.profile = profile;
  }

  public int getNumberOfFilesInSubmission() {
    return numberOfFilesInSubmission;
  }

  public void setNumberOfFilesInSubmission(int numberOfFilesInSubmission) {
    this.numberOfFilesInSubmission = numberOfFilesInSubmission;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NcSubmissionMessage that = (NcSubmissionMessage) o;
    return profile == that.profile && numberOfFilesInSubmission == that.numberOfFilesInSubmission && Objects.equals(floatId, that.floatId)
        && Objects.equals(validationError, that.validationError) && Objects.equals(timestamp, that.timestamp)
        && Objects.equals(dac, that.dac) && Objects.equals(fileName, that.fileName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(floatId, validationError, timestamp, dac, fileName, profile, numberOfFilesInSubmission);
  }

  @Override
  public String toString() {
    return "NcSubmissionMessage{" +
        "floatId='" + floatId + '\'' +
        ", validationError=" + validationError +
        ", timestamp='" + timestamp + '\'' +
        ", dac='" + dac + '\'' +
        ", fileName='" + fileName + '\'' +
        ", profile=" + profile +
        ", numberOfFilesInSubmission=" + numberOfFilesInSubmission +
        '}';
  }

  public static final Predicate IS_VALID = exchange -> exchange.getIn().getBody(NcSubmissionMessage.class).getValidationError() == null;

}
