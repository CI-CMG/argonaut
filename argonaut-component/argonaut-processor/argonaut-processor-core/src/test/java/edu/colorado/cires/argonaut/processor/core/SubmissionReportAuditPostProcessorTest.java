package edu.colorado.cires.argonaut.processor.core;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;

public class SubmissionReportAuditPostProcessorTest {

  @Test
  public void test() throws Exception {

    Instant now = Instant.now();
    AuditStore auditStore = mock(AuditStore.class);

    SubmissionReportAuditPostProcessor postProcessor = new SubmissionReportAuditPostProcessor();
    postProcessor.setAuditStore(auditStore);
    postProcessor.setNowSupplier(() -> now);

    UUID traceId1 = UUID.randomUUID();
    UUID eventId1 = UUID.randomUUID();
    UUID traceId2 = UUID.randomUUID();
    UUID eventId2 = UUID.randomUUID();
    UUID traceId3 = UUID.randomUUID();
    UUID eventId3 = UUID.randomUUID();
    Instant timeStamp = Instant.parse("2026-10-05T12:00:00.00Z");

    SubmissionReportSet input = SubmissionReportSet.builder()
        .withDac("aoml")
        .withReport(Files.readString(Paths.get("src/test/resources/test_report.html")))
        .withEvents(Arrays.asList(
            AuditMessage.builder()
                .withTraceId(traceId1)
                .withEventId(eventId1)
                .withDac("aoml")
                .withTimestamp(timeStamp)
                .withEventType(EventType.INFO)
                .withProcessor(AuditEventProcessor.FILE_RECEIVED)
                .withMessage("file received")
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
                .withMessage("file received")
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
                .withMessage("file received")
                .withStackTrace(null)
                .withFileName("D999_001.nc")
                .build()
        ))
        .build();

    SubmissionReportSet output = postProcessor.generateReport(input);

    assertEquals(input, output);

    for (AuditMessage e : input.getEvents()) {
      verify(auditStore, times(1)).markSubmissionReported(eq(AuditMessage.builder(e).withTimestamp(now).build()));
    }


  }

}