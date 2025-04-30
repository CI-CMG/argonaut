package edu.colorado.cires.argonaut.message;

public class ProcessingMessage {

  private String floatId;
  private SubmissionMessage submission;

  public SubmissionMessage getSubmission() {
    return submission;
  }

  public void setSubmission(SubmissionMessage submission) {
    this.submission = submission;
  }

  public String getFloatId() {
    return floatId;
  }

  public void setFloatId(String floatId) {
    this.floatId = floatId;
  }
}
