package edu.colorado.cires.argonaut.message;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.apache.camel.Predicate;

public class RemovalMessage {

  private String fileName;
  private List<String> validationError = new ArrayList<>();
  private String timestamp;
  private String dac;
  private List<NcSubmissionMessage> filesToRemove = new ArrayList<>();

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public List<String> getValidationError() {
    return validationError;
  }

  public void setValidationError(List<String> validationError) {
    if (validationError == null) {
      validationError = new ArrayList<>();
    }
    this.validationError = validationError;
  }

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

  public List<NcSubmissionMessage> getFilesToRemove() {
    return filesToRemove;
  }

  public void setFilesToRemove(List<NcSubmissionMessage> filesToRemove) {
    if (filesToRemove == null) {
      filesToRemove = new ArrayList<>();
    }
    this.filesToRemove = filesToRemove;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RemovalMessage that = (RemovalMessage) o;
    return Objects.equals(fileName, that.fileName) && Objects.equals(validationError, that.validationError)
        && Objects.equals(timestamp, that.timestamp) && Objects.equals(dac, that.dac) && Objects.equals(filesToRemove,
        that.filesToRemove);
  }

  @Override
  public int hashCode() {
    return Objects.hash(fileName, validationError, timestamp, dac, filesToRemove);
  }

  @Override
  public String toString() {
    return "RemovalMessage{" +
        "fileName='" + fileName + '\'' +
        ", validationError=" + validationError +
        ", timestamp='" + timestamp + '\'' +
        ", dac='" + dac + '\'' +
        ", filesToRemove=" + filesToRemove +
        '}';
  }

  public static final Predicate IS_VALID = exchange -> exchange.getIn().getBody(RemovalMessage.class).getValidationError().isEmpty();

}
