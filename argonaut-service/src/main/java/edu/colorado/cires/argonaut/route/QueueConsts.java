package edu.colorado.cires.argonaut.route;

public final class QueueConsts {

  public static final String VALIDATION_SUCCESS = "seda:validation-success";
  public static final String VALIDATION = "seda:validation";
  public static final String SUBMIT_DATA = "seda:submit-data";
  public static final String FILE_OUTPUT = "seda:file-output";
  public static final String SUBMISSION_REPORT = "seda:submit-report";
  public static final String FILE_MOVED = "seda:file-moved";
  public static final String SUBMISSION_COMPLETE_AGG = "seda:submission-complete-agg";
  public static final String PREPARE_SUBMISSION_EMAIL = "seda:prepare-submission-email";

  private QueueConsts() {

  }
}
