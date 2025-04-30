package edu.colorado.cires.argonaut.message;

import java.time.Instant;

public class SubmissionMessage {

  private Instant timestamp;
  private String dac;
  private String fileName;


  public Instant getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Instant timestamp) {
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
}
