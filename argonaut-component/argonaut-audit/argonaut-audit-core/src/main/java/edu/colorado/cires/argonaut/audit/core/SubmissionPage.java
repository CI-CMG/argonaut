package edu.colorado.cires.argonaut.audit.core;

import edu.colorado.cires.argonaut.messaging.core.databind.AuditMessage;
import java.util.List;
import java.util.Optional;

public interface SubmissionPage extends SubmissionReportSearch {


  int getTotalPages();

  long getTotalRecords();

  List<AuditMessage> getPage();

  Optional<SubmissionReportSearch> getNextPage();

}
