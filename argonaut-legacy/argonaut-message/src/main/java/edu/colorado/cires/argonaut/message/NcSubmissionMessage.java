package edu.colorado.cires.argonaut.message;

import static edu.colorado.cires.argonaut.message.MessageUtils.emptyOrCopy;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import edu.colorado.cires.argonaut.message.FloatMergeGroup.Builder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@JsonDeserialize(builder = NcSubmissionMessage.Builder.class)
public final class NcSubmissionMessage implements Comparable<NcSubmissionMessage> {

  public static Builder builder() {
    return new Builder();
  }

  public static Builder builder(NcSubmissionMessage source) {
    return new Builder(source);
  }

  public enum Operation {
    ADD, REMOVE, FLOAT_MERGE
  }

  public static final class Builder {

    private String floatId;
    private List<String> validationErrors = new ArrayList<>(0);
    private String timestamp;
    private String dac;
    private String fileName;
    private boolean profile;
    private int numberOfFilesInSubmission;
    private Operation operation = Operation.ADD;
    private List<String> associatedFiles = new ArrayList<>();

    private Builder() {

    }

    private Builder(NcSubmissionMessage source) {
      floatId = source.floatId;
      validationErrors = new ArrayList<>(source.validationErrors);
      timestamp = source.timestamp;
      dac = source.dac;
      fileName = source.fileName;
      profile = source.profile;
      numberOfFilesInSubmission = source.numberOfFilesInSubmission;
      operation = source.operation;
      this.associatedFiles = new ArrayList<>(source.associatedFiles);
    }

    public Builder withFloatId(String floatId) {
      this.floatId = floatId;
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

    public Builder withFileName(String fileName) {
      this.fileName = fileName;
      return this;
    }

    public Builder withProfile(boolean profile) {
      this.profile = profile;
      return this;
    }

    public Builder withNumberOfFilesInSubmission(int numberOfFilesInSubmission) {
      this.numberOfFilesInSubmission = numberOfFilesInSubmission;
      return this;
    }

    public Builder withOperation(Operation operation) {
      this.operation = operation;
      return this;
    }

    public Builder withAssociatedFiles(List<String> associatedFiles) {
      this.associatedFiles = emptyOrCopy(associatedFiles);;
      return this;
    }

    public Builder addAssociatedFile(String associatedFile) {
      associatedFiles.add(associatedFile);
      return this;
    }

    public NcSubmissionMessage build() {
      associatedFiles.sort(String::compareTo);
      validationErrors.sort(String::compareTo);
      return new NcSubmissionMessage(floatId, Collections.unmodifiableList(validationErrors), timestamp, dac, fileName, profile, numberOfFilesInSubmission, operation, Collections.unmodifiableList(associatedFiles));
    }
  }

  private final String floatId;
  private final List<String> validationErrors;
  private final String timestamp;
  private final String dac;
  private final String fileName;
  private final boolean profile;
  private final int numberOfFilesInSubmission;
  private final Operation operation;
  private final List<String> associatedFiles;

  private NcSubmissionMessage(String floatId, List<String> validationErrors, String timestamp, String dac, String fileName, boolean profile,
      int numberOfFilesInSubmission, Operation operation, List<String> associatedFiles) {
    this.floatId = floatId;
    this.validationErrors = validationErrors;
    this.timestamp = timestamp;
    this.dac = dac;
    this.fileName = fileName;
    this.profile = profile;
    this.numberOfFilesInSubmission = numberOfFilesInSubmission;
    this.operation = operation;
    this.associatedFiles = associatedFiles;
  }

  public Operation getOperation() {
    return operation;
  }

  public String getTimestamp() {
    return timestamp;
  }

  public String getDac() {
    return dac;
  }

  public String getFileName() {
    return fileName;
  }

  public String getFloatId() {
    return floatId;
  }

  public List<String> getValidationErrors() {
    return validationErrors;
  }

  public boolean isProfile() {
    return profile;
  }

  public int getNumberOfFilesInSubmission() {
    return numberOfFilesInSubmission;
  }

  public List<String> getAssociatedFiles() {
    return associatedFiles;
  }

  @Override
  public int compareTo(NcSubmissionMessage o) {
    int result = Objects.compare(dac, o.dac, Comparator.nullsLast(Comparator.naturalOrder()));
    if (result != 0) {
      return result;
    }
    result = Objects.compare(floatId, o.floatId, Comparator.nullsLast(Comparator.naturalOrder()));
    if (result != 0) {
      return result;
    }
    result = Objects.compare(timestamp, o.timestamp, Comparator.nullsLast(Comparator.naturalOrder()));
    if (result != 0) {
      return result;
    }
    return Objects.compare(fileName, o.fileName, Comparator.nullsLast(Comparator.naturalOrder()));
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NcSubmissionMessage that = (NcSubmissionMessage) o;
    return profile == that.profile && numberOfFilesInSubmission == that.numberOfFilesInSubmission && Objects.equals(floatId, that.floatId)
        && Objects.equals(validationErrors, that.validationErrors) && Objects.equals(timestamp, that.timestamp)
        && Objects.equals(dac, that.dac) && Objects.equals(fileName, that.fileName) && operation == that.operation
        && Objects.equals(associatedFiles, that.associatedFiles);
  }

  @Override
  public int hashCode() {
    return Objects.hash(floatId, validationErrors, timestamp, dac, fileName, profile, numberOfFilesInSubmission, operation, associatedFiles);
  }

  @Override
  public String toString() {
    return "NcSubmissionMessage{" +
        "floatId='" + floatId + '\'' +
        ", validationErrors=" + validationErrors +
        ", timestamp='" + timestamp + '\'' +
        ", dac='" + dac + '\'' +
        ", fileName='" + fileName + '\'' +
        ", profile=" + profile +
        ", numberOfFilesInSubmission=" + numberOfFilesInSubmission +
        ", operation=" + operation +
        ", associatedFiles=" + associatedFiles +
        '}';
  }

}
