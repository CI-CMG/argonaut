package edu.colorado.cires.argonaut.processor.core;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import edu.colorado.cires.argonaut.audit.core.AuditStore;
import edu.colorado.cires.argonaut.messaging.core.databind.AuditEventProcessor;
import edu.colorado.cires.argonaut.messaging.core.databind.AuditMessage;
import edu.colorado.cires.argonaut.messaging.core.databind.AuditMessage.EventType;
import edu.colorado.cires.argonaut.messaging.core.databind.SubmissionReportSet;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;
import org.junit.jupiter.api.Test;

public class HtmlSubmissionReportGeneratorTest {

  @Test
  public void test() throws Exception {

    Instant now = Instant.parse("2026-10-05T15:27:54.923693Z");
    AuditStore auditStore = mock(AuditStore.class);

    HtmlSubmissionReportGenerator generator = new HtmlSubmissionReportGenerator();
    generator.setAuditStore(auditStore);
    generator.setNowSupplier(() -> now);
    generator.setEnvironment("Test");

    UUID traceId1 = UUID.randomUUID();
    UUID eventId1 = UUID.randomUUID();
    UUID traceId2 = UUID.randomUUID();
    UUID eventId2 = UUID.randomUUID();
    UUID traceId3 = UUID.randomUUID();
    UUID eventId3 = UUID.randomUUID();
    UUID traceId4 = UUID.randomUUID();
    UUID eventId4 = UUID.randomUUID();
    Instant timeStamp = Instant.parse("2026-10-05T12:00:00.00Z");

    when(auditStore.getHistoryForTraceId(eq(traceId1))).thenReturn(Arrays.asList(
        AuditMessage.builder()
            .withTraceId(traceId1)
            .withEventId(eventId1)
            .withDac("aoml")
            .withTimestamp(Instant.parse("2026-10-05T12:00:00Z"))
            .withEventType(EventType.INFO)
            .withProcessor(AuditEventProcessor.FILE_RECEIVED)
            .withMessage("triggered file update")
            .withStackTrace(null)
            .withFileName("D1234_001.nc")
            .build(),
        AuditMessage.builder()
            .withTraceId(traceId1)
            .withEventId(eventId1)
            .withDac("aoml")
            .withTimestamp(Instant.parse("2026-10-05T12:00:01Z"))
            .withEventType(EventType.INFO)
            .withProcessor(AuditEventProcessor.VALIDATION)
            .withMessage("validate")
            .withStackTrace(null)
            .withFileName("D1234_001.nc")
            .build(),
        AuditMessage.builder()
            .withTraceId(traceId1)
            .withEventId(eventId1)
            .withDac("aoml")
            .withTimestamp(Instant.parse("2026-10-05T12:00:02Z"))
            .withEventType(EventType.INFO)
            .withProcessor(AuditEventProcessor.FILE_STORE)
            .withMessage("stored")
            .withStackTrace(null)
            .withFileName("D1234_001.nc")
            .build(),
        AuditMessage.builder()
            .withTraceId(traceId1)
            .withEventId(eventId1)
            .withDac("aoml")
            .withTimestamp(Instant.parse("2026-10-05T12:00:03Z"))
            .withEventType(EventType.INFO)
            .withProcessor(AuditEventProcessor.INDEXING)
            .withMessage("updated index")
            .withStackTrace(null)
            .withFileName("D1234_001.nc")
            .build()
    ));

    when(auditStore.getHistoryForTraceId(eq(traceId2))).thenReturn(Arrays.asList(
        AuditMessage.builder()
            .withTraceId(traceId2)
            .withEventId(eventId2)
            .withDac("aoml")
            .withTimestamp(Instant.parse("2026-10-05T12:00:05Z"))
            .withEventType(EventType.INFO)
            .withProcessor(AuditEventProcessor.FILE_RECEIVED)
            .withMessage("triggered file removal")
            .withStackTrace(null)
            .withFileName("D5678_001.nc")
            .build(),
        AuditMessage.builder()
            .withTraceId(traceId2)
            .withEventId(eventId2)
            .withDac("aoml")
            .withTimestamp(Instant.parse("2026-10-05T12:00:06Z"))
            .withEventType(EventType.INFO)
            .withProcessor(AuditEventProcessor.VALIDATION)
            .withMessage("validate")
            .withStackTrace(null)
            .withFileName("D5678_001.nc")
            .build(),
        AuditMessage.builder()
            .withTraceId(traceId2)
            .withEventId(eventId2)
            .withDac("aoml")
            .withTimestamp(Instant.parse("2026-10-05T12:00:07Z"))
            .withEventType(EventType.INFO)
            .withProcessor(AuditEventProcessor.FILE_STORE)
            .withMessage("stored")
            .withStackTrace(null)
            .withFileName("D5678_001.nc")
            .build(),
        AuditMessage.builder()
            .withTraceId(traceId2)
            .withEventId(eventId2)
            .withDac("aoml")
            .withTimestamp(Instant.parse("2026-10-05T12:00:08Z"))
            .withEventType(EventType.INFO)
            .withProcessor(AuditEventProcessor.INDEXING)
            .withMessage("updated index")
            .withStackTrace(null)
            .withFileName("D5678_001.nc")
            .build()
    ));

    when(auditStore.getHistoryForTraceId(eq(traceId3))).thenReturn(Arrays.asList(
        AuditMessage.builder()
            .withTraceId(traceId3)
            .withEventId(eventId3)
            .withDac("aoml")
            .withTimestamp(Instant.parse("2026-10-05T12:00:10Z"))
            .withEventType(EventType.INFO)
            .withProcessor(AuditEventProcessor.FILE_RECEIVED)
            .withMessage("triggered file update")
            .withStackTrace(null)
            .withFileName("D999_001.nc")
            .build(),
        AuditMessage.builder()
            .withTraceId(traceId3)
            .withEventId(eventId3)
            .withDac("aoml")
            .withTimestamp(Instant.parse("2026-10-05T12:00:11Z"))
            .withEventType(EventType.ERROR)
            .withProcessor(AuditEventProcessor.VALIDATION)
            .withMessage("validate")
            .withStackTrace("Validation Failure")
            .withFileName("D999_001.nc")
            .build()
    ));

    when(auditStore.getHistoryForTraceId(eq(traceId4))).thenReturn(Arrays.asList(
        AuditMessage.builder()
            .withTraceId(traceId4)
            .withEventId(eventId4)
            .withDac("aoml")
            .withTimestamp(Instant.parse("2026-10-05T12:00:10Z"))
            .withEventType(EventType.INFO)
            .withProcessor(AuditEventProcessor.FILE_RECEIVED)
            .withMessage("triggered file update")
            .withStackTrace(null)
            .withFileName("D111_001.nc")
            .build(),
        AuditMessage.builder()
            .withTraceId(traceId4)
            .withEventId(eventId4)
            .withDac("aoml")
            .withTimestamp(Instant.parse("2026-10-05T12:00:11Z"))
            .withEventType(EventType.INFO)
            .withProcessor(AuditEventProcessor.VALIDATION)
            .withMessage("validate")
            .withFileName("D111_001.nc")
            .build()
    ));

    SubmissionReportSet input = SubmissionReportSet.builder()
        .withDac("aoml")
        .withEvents(Arrays.asList(
            AuditMessage.builder()
                .withTraceId(traceId1)
                .withEventId(eventId1)
                .withDac("aoml")
                .withTimestamp(timeStamp)
                .withEventType(EventType.INFO)
                .withProcessor(AuditEventProcessor.FILE_RECEIVED)
                .withMessage("triggered file update")
                .withStackTrace(null)
                .withFileName("D1234_001.nc")
                .build(),
            AuditMessage.builder()
                .withTraceId(traceId2)
                .withEventId(eventId2)
                .withDac("aoml")
                .withTimestamp(timeStamp)
                .withEventType(EventType.INFO)
                .withProcessor(AuditEventProcessor.FILE_RECEIVED)
                .withMessage("triggered file removal")
                .withStackTrace(null)
                .withFileName("D5678_001.nc")
                .build(),
            AuditMessage.builder()
                .withTraceId(traceId3)
                .withEventId(eventId3)
                .withDac("aoml")
                .withTimestamp(timeStamp)
                .withEventType(EventType.INFO)
                .withProcessor(AuditEventProcessor.FILE_RECEIVED)
                .withMessage("triggered file update")
                .withStackTrace(null)
                .withFileName("D999_001.nc")
                .build(),
            AuditMessage.builder()
                .withTraceId(traceId4)
                .withEventId(eventId4)
                .withDac("aoml")
                .withTimestamp(timeStamp)
                .withEventType(EventType.INFO)
                .withProcessor(AuditEventProcessor.FILE_RECEIVED)
                .withMessage("triggered file update")
                .withStackTrace(null)
                .withFileName("D111_001.nc")
                .build()
        ))
        .build();

    SubmissionReportSet result = generator.generateReport(input);

    assertEquals(SubmissionReportSet.builder(input)
        .withEvents(input.getEvents().stream().filter(e -> !e.getEventId().equals(eventId4)).toList())
        .withReport(Files.readString(Paths.get("src/test/resources/test_report.html")))
        .build(), result);

  }

}