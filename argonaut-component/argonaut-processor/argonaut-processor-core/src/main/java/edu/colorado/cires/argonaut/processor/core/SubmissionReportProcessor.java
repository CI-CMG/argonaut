package edu.colorado.cires.argonaut.processor.core;

import edu.colorado.cires.argonaut.messaging.core.databind.SubmissionReportSet;

public interface SubmissionReportProcessor {

  SubmissionReportSet generateReport(SubmissionReportSet submissionReportSet);

}
