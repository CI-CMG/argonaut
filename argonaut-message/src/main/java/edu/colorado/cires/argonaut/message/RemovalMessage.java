package edu.colorado.cires.argonaut.message;

import static edu.colorado.cires.argonaut.message.MessageUtils.emptyOrCopy;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@JsonDeserialize(builder = RemovalMessage.Builder.class)
public final class RemovalMessage {


  public static Builder builder() {
    return new Builder();
  }

  public static Builder builder(RemovalMessage source) {
    return new Builder(source);
  }

  public static final class Builder {

    private String fileName;
    private List<String> validationErrors = new ArrayList<>(0);
    private String timestamp;
    private String dac;
    private List<NcSubmissionMessage> removalFiles = new ArrayList<>(0);

    private Builder() {

    }

    private Builder(RemovalMessage source) {
      fileName = source.fileName;
      validationErrors = new ArrayList<>(source.validationErrors);
      timestamp = source.timestamp;
      dac = source.dac;
      removalFiles = new ArrayList<>(source.removalFiles);
    }

    public Builder withFileName(String fileName) {
      this.fileName = fileName;
      return this;
    }

    public Builder addValidationError(String validationError) {
      validationErrors.add(validationError);
      return this;
    }

    public Builder withValidationErrors(List<String> validationErrors) {
      this.validationErrors = emptyOrCopy(validationErrors);
      return this;
    }

    public Builder withTimestamp(String timestamp) {
      this.timestamp = timestamp;
      return this;
    }

    public Builder withDac(String dac) {
      this.dac = dac;
      return this;
    }

    public Builder addRemovalFile(NcSubmissionMessage removalFile) {
      removalFiles.add(removalFile);
      return this;
    }

    public Builder withRemovalFiles(List<NcSubmissionMessage> removalFiles) {
      this.removalFiles = emptyOrCopy(removalFiles);
      return this;
    }

    public RemovalMessage build() {
      validationErrors.sort(String::compareTo);
      removalFiles.sort(NcSubmissionMessage::compareTo);
      return new RemovalMessage(
          fileName,
          Collections.unmodifiableList(validationErrors),
          timestamp,
          dac,
          Collections.unmodifiableList(removalFiles)
      );
    }
  }

  private final String fileName;
  private final List<String> validationErrors;
  private final String timestamp;
  private final String dac;
  private final List<NcSubmissionMessage> removalFiles;

  private RemovalMessage(String fileName, List<String> validationErrors, String timestamp, String dac, List<NcSubmissionMessage> removalFiles) {
    this.fileName = fileName;
    this.validationErrors = validationErrors;
    this.timestamp = timestamp;
    this.dac = dac;
    this.removalFiles = removalFiles;
  }

  public String getFileName() {
    return fileName;
  }

  public List<String> getValidationErrors() {
    return validationErrors;
  }

  public String getTimestamp() {
    return timestamp;
  }

  public String getDac() {
    return dac;
  }

  public List<NcSubmissionMessage> getRemovalFiles() {
    return removalFiles;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RemovalMessage that = (RemovalMessage) o;
    return Objects.equals(fileName, that.fileName) && Objects.equals(validationErrors, that.validationErrors)
        && Objects.equals(timestamp, that.timestamp) && Objects.equals(dac, that.dac) && Objects.equals(removalFiles,
        that.removalFiles);
  }

  @Override
  public int hashCode() {
    return Objects.hash(fileName, validationErrors, timestamp, dac, removalFiles);
  }

  @Override
  public String toString() {
    return "RemovalMessage{" +
        "fileName='" + fileName + '\'' +
        ", validationErrors=" + validationErrors +
        ", timestamp='" + timestamp + '\'' +
        ", dac='" + dac + '\'' +
        ", removalFiles=" + removalFiles +
        '}';
  }

}
