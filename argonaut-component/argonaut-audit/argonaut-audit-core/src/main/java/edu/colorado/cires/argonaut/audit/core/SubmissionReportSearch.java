package edu.colorado.cires.argonaut.audit.core;

import java.time.Instant;

public interface SubmissionReportSearch {

  String getDac();

  Instant getTimestampLt();

  int getPageNumber();

  int getPageSize();

}
