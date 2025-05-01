package edu.colorado.cires.argonaut.message;

import java.time.Instant;
import java.util.Objects;
import org.apache.camel.Predicate;

public class NcSubmissionMessage {


  private String floatId;
  private String validationError;
  private String timestamp;
  private String dac;
  private String fileName;
  private boolean profile;

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

  public String getValidationError() {
    return validationError;
  }

  public void setValidationError(String validationError) {
    this.validationError = validationError;
  }

  public boolean isProfile() {
    return profile;
  }

  public void setProfile(boolean profile) {
    this.profile = profile;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NcSubmissionMessage that = (NcSubmissionMessage) o;
    return profile == that.profile && Objects.equals(floatId, that.floatId) && Objects.equals(validationError, that.validationError)
        && Objects.equals(timestamp, that.timestamp) && Objects.equals(dac, that.dac) && Objects.equals(fileName,
        that.fileName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(floatId, validationError, timestamp, dac, fileName, profile);
  }

  @Override
  public String toString() {
    return "NcSubmissionMessage{" +
        "floatId='" + floatId + '\'' +
        ", validationError='" + validationError + '\'' +
        ", timestamp='" + timestamp + '\'' +
        ", dac='" + dac + '\'' +
        ", fileName='" + fileName + '\'' +
        ", profile=" + profile +
        '}';
  }

  public static final Predicate IS_VALID = exchange -> exchange.getIn().getBody(NcSubmissionMessage.class).getValidationError() == null;

}
