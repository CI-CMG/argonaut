package edu.colorado.cires.argonaut.audit.core;

import edu.colorado.cires.argonaut.messaging.core.databind.AuditMessage;
import java.util.List;
import java.util.UUID;

public interface AuditStore {

  void recordEvent(AuditMessage auditMessage);

  void markSubmissionReported(AuditMessage auditMessage);

  SubmissionPage findUnreportedSubmissions(SubmissionReportSearch search);

  List<AuditMessage> getHistoryForTraceId(UUID traceId);

}
