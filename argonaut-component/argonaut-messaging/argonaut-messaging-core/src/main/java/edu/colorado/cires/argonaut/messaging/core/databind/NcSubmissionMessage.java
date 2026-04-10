package edu.colorado.cires.argonaut.messaging.core.databind;


import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import tools.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = NcSubmissionMessage.Builder.class)
public final class NcSubmissionMessage implements Comparable<NcSubmissionMessage> {

  public static Builder builder() {
    return new Builder();
  }

  public static Builder builder(NcSubmissionMessage source) {
    return new Builder(source);
  }

  public enum Operation {
    ADD, REMOVE
  }

  public enum FileType {
    PROFILE, PROFILE_MERGE,
    //TODO remove me
    UNKNOWN
  }

  public static final class Builder {

    private String floatId;
    private List<String> validationErrors = new ArrayList<>(0);
    private Instant timestamp;
    private String dac;
    private String fileName;
    private FileType fileType;
    private int numberOfFilesInSubmission;
    private Operation operation = Operation.ADD;
//    private List<String> associatedFiles = new ArrayList<>();

    private Builder() {

    }

    private Builder(NcSubmissionMessage source) {
      floatId = source.floatId;
      validationErrors = new ArrayList<>(source.validationErrors);
      timestamp = source.timestamp;
      dac = source.dac;
      fileName = source.fileName;
      fileType = source.fileType;
      numberOfFilesInSubmission = source.numberOfFilesInSubmission;
      operation = source.operation;
//      this.associatedFiles = new ArrayList<>(source.associatedFiles);
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
      this.validationErrors = MessageUtils.emptyOrCopy(validationErrors);
      return this;
    }

    public Builder withTimestamp(Instant timestamp) {
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

    public Builder withFileType(FileType fileType) {
      this.fileType = fileType;
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

//    public Builder withAssociatedFiles(List<String> associatedFiles) {
//      this.associatedFiles = MessageUtils.emptyOrCopy(associatedFiles);
//      ;
//      return this;
//    }
//
//    public Builder addAssociatedFile(String associatedFile) {
//      associatedFiles.add(associatedFile);
//      return this;
//    }

    public NcSubmissionMessage build() {
//      associatedFiles.sort(String::compareTo);
      validationErrors.sort(String::compareTo);
      return new NcSubmissionMessage(floatId, Collections.unmodifiableList(validationErrors), timestamp, dac, fileName, fileType,
          numberOfFilesInSubmission, operation);
    }
  }

  private final String floatId;
  private final List<String> validationErrors;
  private final Instant timestamp;
  private final String dac;
  private final String fileName;
  private final FileType fileType;
  private final int numberOfFilesInSubmission;
  private final Operation operation;
//  private final List<String> associatedFiles;

  private NcSubmissionMessage(String floatId, List<String> validationErrors, Instant timestamp, String dac, String fileName, FileType fileType,
      int numberOfFilesInSubmission, Operation operation) {
    this.floatId = floatId;
    this.validationErrors = validationErrors;
    this.timestamp = timestamp;
    this.dac = dac;
    this.fileName = fileName;
    this.fileType = fileType;
    this.numberOfFilesInSubmission = numberOfFilesInSubmission;
    this.operation = operation;
//    this.associatedFiles = associatedFiles;
  }

  public Operation getOperation() {
    return operation;
  }

  public Instant getTimestamp() {
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

  public FileType getFileType() {
    return fileType;
  }

  public int getNumberOfFilesInSubmission() {
    return numberOfFilesInSubmission;
  }

//  public List<String> getAssociatedFiles() {
//    return associatedFiles;
//  }

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
    return Objects.equals(fileType, that.fileType) && numberOfFilesInSubmission == that.numberOfFilesInSubmission && Objects.equals(floatId, that.floatId)
        && Objects.equals(validationErrors, that.validationErrors) && Objects.equals(timestamp, that.timestamp)
        && Objects.equals(dac, that.dac) && Objects.equals(fileName, that.fileName) && operation == that.operation;
  }

  @Override
  public int hashCode() {
    return Objects.hash(floatId, validationErrors, timestamp, dac, fileName, fileType, numberOfFilesInSubmission, operation);
  }

  @Override
  public String toString() {
    return "NcSubmissionMessage{" +
        "floatId='" + floatId + '\'' +
        ", validationErrors=" + validationErrors +
        ", timestamp='" + timestamp + '\'' +
        ", dac='" + dac + '\'' +
        ", fileName='" + fileName + '\'' +
        ", fileType=" + fileType +
        ", numberOfFilesInSubmission=" + numberOfFilesInSubmission +
        ", operation=" + operation +
        '}';
  }

}
