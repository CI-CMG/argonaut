package edu.colorado.cires.argonaut.message;

import static edu.colorado.cires.argonaut.message.MessageUtils.emptyOrCopy;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@JsonDeserialize(builder = SubmissionCompleteMessage.Builder.class)
public final class SubmissionCompleteMessage {

  public static Builder builder() {
    return new Builder();
  }

  public static Builder builder(SubmissionCompleteMessage source) {
    return new Builder(source);
  }

  public static final class Builder {

    private List<String> completedFiles = new ArrayList<>();
    private int numberOfFiles;
    private String dac;
    private String timeStamp;

    private Builder() {

    }

    private Builder(SubmissionCompleteMessage source) {
      completedFiles = new ArrayList<>(source.completedFiles);
      numberOfFiles = source.numberOfFiles;
      dac = source.dac;
      timeStamp = source.timeStamp;
    }

    public Builder addCompletedFile(String completedFile) {
      completedFiles.add(completedFile);
      return this;
    }

    public Builder withCompletedFiles(List<String> completedFiles) {
      this.completedFiles = emptyOrCopy(completedFiles);
      return this;
    }

    public Builder withNumberOfFiles(int numberOfFiles) {
      this.numberOfFiles = numberOfFiles;
      return this;
    }

    public Builder withDac(String dac) {
      this.dac = dac;
      return this;
    }

    public Builder withTimeStamp(String timeStamp) {
      this.timeStamp = timeStamp;
      return this;
    }

    public SubmissionCompleteMessage build() {
      completedFiles.sort(String::compareTo);
      return new SubmissionCompleteMessage(Collections.unmodifiableList(completedFiles), numberOfFiles, dac, timeStamp);
    }
  }

  private final List<String> completedFiles;
  private final int numberOfFiles;
  private final String dac;
  private final String timeStamp;

  private SubmissionCompleteMessage(List<String> completedFiles, int numberOfFiles, String dac, String timeStamp) {
    this.completedFiles = completedFiles;
    this.numberOfFiles = numberOfFiles;
    this.dac = dac;
    this.timeStamp = timeStamp;
  }

  public List<String> getCompletedFiles() {
    return completedFiles;
  }

  public int getNumberOfFiles() {
    return numberOfFiles;
  }

  public String getDac() {
    return dac;
  }

  public String getTimeStamp() {
    return timeStamp;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SubmissionCompleteMessage that = (SubmissionCompleteMessage) o;
    return numberOfFiles == that.numberOfFiles && Objects.equals(completedFiles, that.completedFiles) && Objects.equals(dac,
        that.dac) && Objects.equals(timeStamp, that.timeStamp);
  }

  @Override
  public int hashCode() {
    return Objects.hash(completedFiles, numberOfFiles, dac, timeStamp);
  }

  @Override
  public String toString() {
    return "SubmissionCompleteMessage{" +
        "completedFiles=" + completedFiles +
        ", numberOfFiles=" + numberOfFiles +
        ", dac='" + dac + '\'' +
        ", timeStamp='" + timeStamp + '\'' +
        '}';
  }
}
