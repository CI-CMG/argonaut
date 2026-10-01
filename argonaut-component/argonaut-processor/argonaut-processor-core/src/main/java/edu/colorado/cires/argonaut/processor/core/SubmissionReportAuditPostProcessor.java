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

  protected void doWithReport(SubmissionReportSet submissionReportSet) {

  }

  @Override
  public SubmissionReportSet generateReport(SubmissionReportSet submissionReportSet) {
    doWithReport(submissionReportSet);
    Instant now = nowSupplier.get();
    submissionReportSet.getEvents()
        .stream()
        .map(am -> AuditMessage.builder(am).withTimestamp(now).build())
        .forEach(auditStore::markSubmissionReported);
    return submissionReportSet;
  }
}
