package edu.colorado.cires.argonaut.processor.core;

import edu.colorado.cires.argonaut.audit.core.AuditStore;
import edu.colorado.cires.argonaut.messaging.core.databind.AuditEventProcessor;
import edu.colorado.cires.argonaut.messaging.core.databind.AuditMessage;
import edu.colorado.cires.argonaut.messaging.core.databind.AuditMessage.EventType;
import edu.colorado.cires.argonaut.messaging.core.databind.SubmissionReportSet;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

public class HtmlSubmissionReportGenerator implements SubmissionReportProcessor {

  private static final String TEMPLATE =
      "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">"
          + "<html xmlns=\"http://www.w3.org/1999/xhtml\">"
          + "<head>"
          + "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />"
          + "<title>Argo GDAC Submission Results - _ENV_ - _TIME_</title>"
          + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"/>"
          + "</head>"
          + "_BODY_"
          + "</html>";

  private static final String BODY_TEMPLATE =
      "<body style=\"margin: 0; padding: 0;\">"
          + "<h2>Argo GDAC Submission Results - _ENV_ - _TIME_</h2>"
          + "<br>"
          + "_FAILURE_"
          + "<br>"
          + "_SUCCESS_"
          + "<br>"
          + "</body>";

  private static final String FAILURE_TABLE_TEMPLATE =
      " <table width=\"100%\" style=\"border: 1px solid black; border-collapse: collapse;\">"
          + " <caption style=\"color: red; font-size: large; font-weight: bold;\">Failed Files</caption>"
          + "<tr>"
          + "<th style=\"border: 1px solid black; border-collapse: collapse; padding: 5px;\">File Name</th>"
          + "<th style=\"border: 1px solid black; border-collapse: collapse; padding: 5px;\">Start Time</th>"
          + "<th style=\"border: 1px solid black; border-collapse: collapse; padding: 5px;\">End Time</th>"
          + "<th style=\"border: 1px solid black; border-collapse: collapse; padding: 5px;\">Events</th>"
          + "<th style=\"border: 1px solid black; border-collapse: collapse; padding: 5px;\">Error</th>"
          + "</tr>"
          + "_ROWS_"
          + "</table>";

  private static final String SUCCESS_TABLE_TEMPLATE =
      " <table width=\"100%\" style=\"border: 1px solid black; border-collapse: collapse;\">"
          + " <caption style=\"color: green; font-size: large; font-weight: bold;\">Successful Files</caption>"
          + "<tr>"
          + "<th style=\"border: 1px solid black; border-collapse: collapse; padding: 5px;\">File Name</th>"
          + "<th style=\"border: 1px solid black; border-collapse: collapse; padding: 5px;\">Start Time</th>"
          + "<th style=\"border: 1px solid black; border-collapse: collapse; padding: 5px;\">End Time</th>"
          + "<th style=\"border: 1px solid black; border-collapse: collapse; padding: 5px;\">Events</th>"
          + "</tr>"
          + "_ROWS_"
          + "</table>";

  private static String buildSuccessTableRow(History history) {
    return "<tr>"
        + String.format("<td style=\"border: 1px solid black; border-collapse: collapse; padding: 5px; text-align: left;\">%s</td>",
        history.getAuditMessage().getFileName())
        + String.format("<td style=\"border: 1px solid black; border-collapse: collapse; padding: 5px; text-align: left;\">%s</td>",
        history.getStartTime().toString())
        + String.format("<td style=\"border: 1px solid black; border-collapse: collapse; padding: 5px; text-align: left;\">%s</td>",
        history.getEndTime().toString())
        + String.format("<td style=\"border: 1px solid black; border-collapse: collapse; padding: 5px; text-align: left;\">%s</td>",
        String.join(" ", history.getEvents()))
        + "</tr>";
  }

  private static String buildFailureTableRow(History history) {
    return "<tr>"
        + String.format("<td style=\"border: 1px solid black; border-collapse: collapse; padding: 5px; text-align: left;\">%s</td>",
        history.getAuditMessage().getFileName())
        + String.format("<td style=\"border: 1px solid black; border-collapse: collapse; padding: 5px; text-align: left;\">%s</td>",
        history.getStartTime().toString())
        + String.format("<td style=\"border: 1px solid black; border-collapse: collapse; padding: 5px; text-align: left;\">%s</td>",
        history.getEndTime().toString())
        + String.format("<td style=\"border: 1px solid black; border-collapse: collapse; padding: 5px; text-align: left;\">%s</td>",
        String.join(" ", history.getEvents()))
        + String.format("<td style=\"border: 1px solid black; border-collapse: collapse; padding: 5px; text-align: left;\">%s</td>",
        valueOrEmpty(history.getDetails()))
        + "</tr>";
  }


  private static String valueOrEmpty(String s) {
    if (s == null) {
      return "";
    }
    return s;
  }

  private AuditStore auditStore;
  private Supplier<Instant> nowSupplier = Instant::now;
  private String environment;

  public void setAuditStore(AuditStore auditStore) {
    this.auditStore = auditStore;
  }

  public void setNowSupplier(Supplier<Instant> nowSupplier) {
    this.nowSupplier = nowSupplier;
  }

  public void setEnvironment(String environment) {
    this.environment = environment;
  }

  private static class History {

    private final List<String> events;
    private final boolean success;
    private final Instant startTime;
    private final Instant endTime;
    private final String details;
    private final AuditMessage auditMessage;

    private History(AuditMessage auditMessage, List<String> events, boolean success, Instant startTime, Instant endTime, String details) {
      this.events = events;
      this.success = success;
      this.startTime = startTime;
      this.endTime = endTime;
      this.details = details;
      this.auditMessage = auditMessage;
    }

    public AuditMessage getAuditMessage() {
      return auditMessage;
    }

    public List<String> getEvents() {
      return events;
    }

    public boolean isSuccess() {
      return success;
    }

    public Instant getStartTime() {
      return startTime;
    }

    public Instant getEndTime() {
      return endTime;
    }

    public String getDetails() {
      return details;
    }
  }

  private Optional<History> summary(AuditMessage auditMessage) {
    List<AuditMessage> history = auditStore.getHistoryForTraceId(auditMessage.getTraceId());
    Set<EventType> eventTypes = history.stream().map(AuditMessage::getEventType).collect(Collectors.toSet());
    boolean error = eventTypes.contains(EventType.ERROR);
    Set<AuditEventProcessor> events = new LinkedHashSet<>(history.stream().map(AuditMessage::getProcessor).collect(Collectors.toList()));
    if (error || events.contains(AuditEventProcessor.SUBMISSION_COMPLETE) || events.contains(AuditEventProcessor.REMOVAL_COMPLETE)) {
      return Optional.of(new History(
          auditMessage,
          events.stream().map(AuditEventProcessor::toString).toList(),
          !error,
          history.getFirst().getTimestamp(),
          history.getLast().getTimestamp(),
          history.stream().filter(am -> am.getEventType() == EventType.ERROR).findFirst().map(AuditMessage::getStackTrace).orElse("")
      ));
    }
    return Optional.empty();

  }

  @Override
  public SubmissionReportSet generateReport(SubmissionReportSet submissionReportSet) {
    List<History> histories = submissionReportSet.getEvents().stream().map(this::summary).filter(Optional::isPresent).map(Optional::get).toList();

    String success = histories.stream()
        .filter(History::isSuccess)
        .map(HtmlSubmissionReportGenerator::buildSuccessTableRow)
        .collect(Collectors.joining(""));

    String failure = histories.stream()
        .filter(h -> !h.isSuccess())
        .map(HtmlSubmissionReportGenerator::buildFailureTableRow)
        .collect(Collectors.joining(""));

    String successTable = SUCCESS_TABLE_TEMPLATE.replaceAll("_ROWS_", Matcher.quoteReplacement(success));
    String failureTable = FAILURE_TABLE_TEMPLATE.replaceAll("_ROWS_", Matcher.quoteReplacement(failure));

    String time = nowSupplier.get().toString();

    String body = BODY_TEMPLATE
        .replaceAll("_FAILURE_", Matcher.quoteReplacement(failureTable))
        .replaceAll("_SUCCESS_", Matcher.quoteReplacement(successTable))
        .replaceAll("_TIME_", Matcher.quoteReplacement(time))
        .replaceAll("_ENV_", Matcher.quoteReplacement(environment));

    String report = TEMPLATE
        .replaceAll("_BODY_", Matcher.quoteReplacement(body))
        .replaceAll("_TIME_", Matcher.quoteReplacement(time))
        .replaceAll("_ENV_", Matcher.quoteReplacement(environment));

    return SubmissionReportSet.builder(submissionReportSet)
        .withEvents(histories.stream().map(History::getAuditMessage).toList())
        .withReport(report)
        .build();

  }
}
