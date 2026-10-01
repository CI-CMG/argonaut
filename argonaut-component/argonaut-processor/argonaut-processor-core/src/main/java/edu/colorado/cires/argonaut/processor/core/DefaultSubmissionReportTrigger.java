package edu.colorado.cires.argonaut.processor.core;

import edu.colorado.cires.argonaut.audit.core.AuditStore;
import edu.colorado.cires.argonaut.audit.core.DefaultSubmissionReportSearch;
import edu.colorado.cires.argonaut.audit.core.SubmissionPage;
import edu.colorado.cires.argonaut.audit.core.SubmissionReportSearch;
import edu.colorado.cires.argonaut.messaging.core.databind.SubmissionReportSet;
import edu.colorado.cires.argonaut.messaging.core.queue.MessageSender;
import java.time.Instant;
import java.util.Optional;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.databind.json.JsonMapper;

public class DefaultSubmissionReportTrigger implements SubmissionReportTrigger {

  private static final Logger LOGGER = LoggerFactory.getLogger(DefaultSubmissionReportTrigger.class);

  private MessageSender messageSender;
  private String reportGenerationQueue;
  private AuditStore auditStore;
  private int maxRecordsPerReport;
  private int minutesBack;
  private Supplier<Instant> nowSupplier = Instant::now;
  private boolean enabled = true;
  private JsonMapper jsonMapper;

  public void setMessageSender(MessageSender messageSender) {
    this.messageSender = messageSender;
  }

  public void setReportGenerationQueue(String reportGenerationQueue) {
    this.reportGenerationQueue = reportGenerationQueue;
  }

  public void setAuditStore(AuditStore auditStore) {
    this.auditStore = auditStore;
  }

  public void setMaxRecordsPerReport(int maxRecordsPerReport) {
    this.maxRecordsPerReport = maxRecordsPerReport;
  }

  public void setMinutesBack(int minutesBack) {
    this.minutesBack = minutesBack;
  }

  public void setNowSupplier(Supplier<Instant> nowSupplier) {
    this.nowSupplier = nowSupplier;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public void setJsonMapper(JsonMapper jsonMapper) {
    this.jsonMapper = jsonMapper;
  }

  @Override
  public void trigger(String dac) {
    if (enabled) {
      LOGGER.info("Triggered submission report query for {}", dac);
      Instant now = nowSupplier.get();
      SubmissionPage page = auditStore.findUnreportedSubmissions(DefaultSubmissionReportSearch.builder()
          .withDac(dac)
          .withPageSize(maxRecordsPerReport)
          .withTimestampLt(now.minusSeconds((long) minutesBack * 60L))
          .build());
      sendMessage(page);
      Optional<SubmissionReportSearch> maybeNextPage = page.getNextPage();
      while (maybeNextPage.isPresent()) {
        page = auditStore.findUnreportedSubmissions(DefaultSubmissionReportSearch.builder(maybeNextPage.get()).build());
        sendMessage(page);
        maybeNextPage = page.getNextPage();
      }
    }
  }

  private void sendMessage(SubmissionPage page) {
    messageSender.sendJson(reportGenerationQueue, jsonMapper.writeValueAsString(SubmissionReportSet.builder()
        .withDac(page.getDac())
        .withEvents(page.getPage())
        .build()));
  }
}
