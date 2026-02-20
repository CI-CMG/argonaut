package edu.colorado.cires.argonaut.processor.core;

import edu.colorado.cires.argonaut.messaging.core.databind.NcSubmissionMessage;

public interface SubmissionReportProcessor {

  void  generateReport(NcSubmissionMessage message);
}
