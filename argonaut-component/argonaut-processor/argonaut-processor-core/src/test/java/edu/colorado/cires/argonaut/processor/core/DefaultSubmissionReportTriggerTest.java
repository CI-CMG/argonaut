package edu.colorado.cires.argonaut.processor.core;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.colorado.cires.argonaut.audit.core.AuditStore;
import edu.colorado.cires.argonaut.audit.core.DefaultSubmissionPage;
import edu.colorado.cires.argonaut.audit.core.DefaultSubmissionReportSearch;
import edu.colorado.cires.argonaut.messaging.core.databind.AuditEventProcessor;
import edu.colorado.cires.argonaut.messaging.core.databind.AuditMessage;
import edu.colorado.cires.argonaut.messaging.core.databind.AuditMessage.EventType;
import edu.colorado.cires.argonaut.messaging.core.databind.SubmissionReportSet;
import edu.colorado.cires.argonaut.messaging.core.queue.MessageSender;
import edu.colorado.cires.argonaut.messaging.core.util.ArgonautJsonMapperFactory;
import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

public class DefaultSubmissionReportTriggerTest {

  @Test
  public void test() {

    MessageSender messageSender = mock(MessageSender.class);
    AuditStore auditStore = mock(AuditStore.class);
    Instant now = Instant.now();
    JsonMapper jsonMapper = ArgonautJsonMapperFactory.getJsonMapper();

    DefaultSubmissionReportTrigger trigger = new DefaultSubmissionReportTrigger();
    trigger.setMessageSender(messageSender);
    trigger.setReportGenerationQueue("seda:report");
    trigger.setAuditStore(auditStore);
    trigger.setMaxRecordsPerReport(2);
    trigger.setMinutesBack(5);
    trigger.setNowSupplier(() -> now);
    trigger.setJsonMapper(jsonMapper);

    UUID traceId = UUID.randomUUID();
    UUID eventId = UUID.randomUUID();
    Instant timeStamp = Instant.parse("2026-10-05T12:00:00.00Z");


    DefaultSubmissionReportSearch search1 = DefaultSubmissionReportSearch.builder()
        .withDac("aoml")
        .withPageSize(2)
        .withTimestampLt(now.minusSeconds(5L * 60L))
        .withPageNumber(1)
        .build();

    when(auditStore.findUnreportedSubmissions(search1))
        .thenReturn(DefaultSubmissionPage.builder()
            .withSubmissionReportSearch(search1)
            .withTotalRecords(3)
            .withPage(Arrays.asList(
                AuditMessage.builder()
                    .withTraceId(traceId)
                    .withEventId(eventId)
                    .withDac("aoml")
                    .withTimestamp(timeStamp)
                    .withEventType(EventType.INFO)
                    .withProcessor(AuditEventProcessor.FILE_RECEIVED)
                    .withMessage("file received")
                    .withStackTrace(null)
                    .withFileName("D1234_001.nc")
                    .build(),
                AuditMessage.builder()
                    .withTraceId(traceId)
                    .withEventId(eventId)
                    .withDac("aoml")
                    .withTimestamp(timeStamp)
                    .withEventType(EventType.INFO)
                    .withProcessor(AuditEventProcessor.FILE_RECEIVED)
                    .withMessage("file received")
                    .withStackTrace(null)
                    .withFileName("D5678_001.nc")
                    .build()
            ))
            .build());

    DefaultSubmissionReportSearch search2 = DefaultSubmissionReportSearch.builder()
        .withDac("aoml")
        .withPageSize(2)
        .withTimestampLt(now.minusSeconds(5L * 60L))
        .withPageNumber(2)
        .build();

    when(auditStore.findUnreportedSubmissions(search2))
        .thenReturn(DefaultSubmissionPage.builder()
            .withSubmissionReportSearch(search2)
            .withTotalRecords(3)
            .withPage(Arrays.asList(
                AuditMessage.builder()
                    .withTraceId(traceId)
                    .withEventId(eventId)
                    .withDac("aoml")
                    .withTimestamp(timeStamp)
                    .withEventType(EventType.INFO)
                    .withProcessor(AuditEventProcessor.FILE_RECEIVED)
                    .withMessage("file received")
                    .withStackTrace(null)
                    .withFileName("D999_001.nc")
                    .build()
            ))
            .build());

    trigger.trigger("aoml");

    verify(messageSender, times(1)).sendJson(eq("seda:report"), eq(jsonMapper.writeValueAsString(SubmissionReportSet.builder()
        .withDac("aoml")
        .withEvents(Arrays.asList(
            AuditMessage.builder()
                .withTraceId(traceId)
                .withEventId(eventId)
                .withDac("aoml")
                .withTimestamp(timeStamp)
                .withEventType(EventType.INFO)
                .withProcessor(AuditEventProcessor.FILE_RECEIVED)
                .withMessage("file received")
                .withStackTrace(null)
                .withFileName("D1234_001.nc")
                .build(),
            AuditMessage.builder()
                .withTraceId(traceId)
                .withEventId(eventId)
                .withDac("aoml")
                .withTimestamp(timeStamp)
                .withEventType(EventType.INFO)
                .withProcessor(AuditEventProcessor.FILE_RECEIVED)
                .withMessage("file received")
                .withStackTrace(null)
                .withFileName("D5678_001.nc")
                .build()
        ))
        .build())));

    verify(messageSender, times(1)).sendJson(eq("seda:report"), eq(jsonMapper.writeValueAsString(SubmissionReportSet.builder()
        .withDac("aoml")
        .withEvents(Arrays.asList(
            AuditMessage.builder()
                .withTraceId(traceId)
                .withEventId(eventId)
                .withDac("aoml")
                .withTimestamp(timeStamp)
                .withEventType(EventType.INFO)
                .withProcessor(AuditEventProcessor.FILE_RECEIVED)
                .withMessage("file received")
                .withStackTrace(null)
                .withFileName("D999_001.nc")
                .build()
        ))
        .build())));


  }
}