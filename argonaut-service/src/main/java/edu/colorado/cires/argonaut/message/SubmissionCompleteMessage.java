package edu.colorado.cires.argonaut.message;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class SubmissionCompleteMessage {

  private Set<String> filesCompleted = new HashSet<>();
  private int numberOfFiles;
  private String dac;
  private String timeStamp;

  public Set<String> getFilesCompleted() {
    return filesCompleted;
  }

  public void setFilesCompleted(Set<String> filesCompleted) {
    if (filesCompleted == null) {
      filesCompleted = new HashSet<>();
    }
    this.filesCompleted = filesCompleted;
  }

  public int getNumberOfFiles() {
    return numberOfFiles;
  }

  public void setNumberOfFiles(int numberOfFiles) {
    this.numberOfFiles = numberOfFiles;
  }

  public String getDac() {
    return dac;
  }

  public void setDac(String dac) {
    this.dac = dac;
  }

  public String getTimeStamp() {
    return timeStamp;
  }

  public void setTimeStamp(String timeStamp) {
    this.timeStamp = timeStamp;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SubmissionCompleteMessage that = (SubmissionCompleteMessage) o;
    return numberOfFiles == that.numberOfFiles && Objects.equals(filesCompleted, that.filesCompleted) && Objects.equals(dac,
        that.dac) && Objects.equals(timeStamp, that.timeStamp);
  }

  @Override
  public int hashCode() {
    return Objects.hash(filesCompleted, numberOfFiles, dac, timeStamp);
  }

  @Override
  public String toString() {
    return "SubmissionCompleteMessage{" +
        "filesCompleted=" + filesCompleted +
        ", numberOfFiles=" + numberOfFiles +
        ", dac='" + dac + '\'' +
        ", timeStamp='" + timeStamp + '\'' +
        '}';
  }
}
