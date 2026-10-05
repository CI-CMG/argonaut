package edu.colorado.cires.argonaut.processor.core;

import edu.colorado.cires.argonaut.audit.core.AuditStore;
import edu.colorado.cires.argonaut.messaging.core.databind.AuditMessage;
import edu.colorado.cires.argonaut.messaging.core.databind.SubmissionReportSet;
import java.time.Instant;
import java.util.function.Supplier;

public class SubmissionReportAuditPostProcessor implements SubmissionReportProcessor {


  private AuditStore auditStore;
  private Supplier<Instant> nowSupplier = Instant::now;

  public void setAuditStore(AuditStore auditStore) {
    this.auditStore = auditStore;
  }

  public void setNowSupplier(Supplier<Instant> nowSupplier) {
    this.nowSupplier = nowSupplier;
  }

  protected SubmissionReportSet addToReport(SubmissionReportSet submissionReportSet) {
    return submissionReportSet;
  }

  @Override
  public SubmissionReportSet generateReport(SubmissionReportSet submissionReportSet) {
    SubmissionReportSet filtered = addToReport(submissionReportSet);
    Instant now = nowSupplier.get();
    filtered.getEvents()
        .stream()
        .map(am -> AuditMessage.builder(am).withTimestamp(now).build())
        .forEach(auditStore::markSubmissionReported);
    return filtered;
  }
}
