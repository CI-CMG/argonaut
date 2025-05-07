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
  public static final String FLOAT_MERGE_AGG = "seda:float-merge-agg";
  public static final String GEO_MERGE_AGG = "seda:geo-merge-agg";
  public static final String LATEST_MERGE_AGG = "seda:latest-merge-agg";
  public static final String UPDATE_INDEX_AGG = "seda:update-index-agg";
  public static final String FLOAT_MERGE = "seda:float-merge";
  public static final String SUBMIT_REMOVAL = "seda:submit-removal";
  public static final String SUBMIT_UNKNOWN = "seda:submit-unknown";
  public static final String REMOVAL_SPLITTER = "seda:removal-splitter";

  private QueueConsts() {

  }
}
