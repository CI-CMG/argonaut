package edu.colorado.cires.argonaut.audit.jpa;

import static org.junit.jupiter.api.Assertions.*;

import edu.colorado.cires.argonaut.audit.core.DefaultSubmissionPage;
import edu.colorado.cires.argonaut.audit.core.DefaultSubmissionReportSearch;
import edu.colorado.cires.argonaut.audit.core.SubmissionPage;
import edu.colorado.cires.argonaut.audit.core.SubmissionReportSearch;
import edu.colorado.cires.argonaut.messaging.core.databind.AuditEventProcessor;
import edu.colorado.cires.argonaut.messaging.core.databind.AuditMessage;
import edu.colorado.cires.argonaut.messaging.core.databind.AuditMessage.EventType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class JpaAuditStoreTest {

  private EntityManagerFactory emf;
  private JpaAuditStore auditStore;

  @BeforeEach
  public void setup() throws Exception {
    Map<String, String> override = new HashMap<>();
    override.put("jakarta.persistence.jdbc.driver", "org.h2.Driver");
    override.put("jakarta.persistence.jdbc.url", "jdbc:h2:mem:index;DB_CLOSE_DELAY=-1");
    override.put("jakarta.persistence.jdbc.user", "sa");
    override.put("jakarta.persistence.jdbc.password", "");
    override.put("hibernate.hbm2ddl.auto", "update");
    override.put("hibernate.show_sql", "true");
    emf = Persistence.createEntityManagerFactory("argonaut-audit", override);
    auditStore = new JpaAuditStore();
    auditStore.setEntityManagerFactory(emf);

    try (EntityManager em = emf.createEntityManager()) {
      EntityTransaction tx = em.getTransaction();
      tx.begin();
      try {
        em.createQuery("delete from AuditEntity ").executeUpdate();
        tx.commit();
      } catch (Exception e) {
        tx.rollback();
        throw e;
      }
    }
  }

  @Test
  public void testSubmissionReportFlow() {

    UUID traceId1 = UUID.randomUUID();
    UUID traceId2 = UUID.randomUUID();
    UUID traceId3 = UUID.randomUUID();
    UUID traceId4 = UUID.randomUUID();

    auditStore.recordEvent(AuditMessage.builder()
        .withTraceId(traceId1)
        .withDac("aoml")
        .withTimestamp(Instant.parse("2026-10-05T12:00:00Z"))
        .withEventType(EventType.INFO)
        .withProcessor(AuditEventProcessor.FILE_RECEIVED)
        .withMessage("file received")
        .withStackTrace(null)
        .withFileName("D1234_001.nc")
        .build());

    auditStore.recordEvent(AuditMessage.builder()
        .withTraceId(traceId1)
        .withDac("aoml")
        .withTimestamp(Instant.parse("2026-10-05T12:00:01Z"))
        .withEventType(EventType.INFO)
        .withProcessor(AuditEventProcessor.VALIDATION)
        .withMessage("validate")
        .withStackTrace(null)
        .withFileName("D1234_001.nc")
        .build());

    auditStore.recordEvent(AuditMessage.builder()
        .withTraceId(traceId1)
        .withDac("aoml")
        .withTimestamp(Instant.parse("2026-10-05T12:00:02Z"))
        .withEventType(EventType.INFO)
        .withProcessor(AuditEventProcessor.INDEXING)
        .withMessage("index")
        .withStackTrace(null)
        .withFileName("D1234_001.nc")
        .build());

    auditStore.recordEvent(AuditMessage.builder()
        .withTraceId(traceId1)
        .withDac("aoml")
        .withTimestamp(Instant.parse("2026-10-05T12:00:03Z"))
        .withEventType(EventType.INFO)
        .withProcessor(AuditEventProcessor.FILE_STORE)
        .withMessage("stored")
        .withStackTrace(null)
        .withFileName("D1234_001.nc")
        .build());

    auditStore.recordEvent(AuditMessage.builder()
        .withTraceId(traceId1)
        .withDac("aoml")
        .withTimestamp(Instant.parse("2026-10-05T12:00:04Z"))
        .withEventType(EventType.INFO)
        .withProcessor(AuditEventProcessor.SUBMISSION_COMPLETE)
        .withMessage("complete")
        .withStackTrace(null)
        .withFileName("D1234_001.nc")
        .build());

    auditStore.recordEvent(AuditMessage.builder()
        .withTraceId(traceId2)
        .withDac("aoml")
        .withTimestamp(Instant.parse("2026-10-05T12:00:05Z"))
        .withEventType(EventType.INFO)
        .withProcessor(AuditEventProcessor.FILE_RECEIVED)
        .withMessage("file received")
        .withStackTrace(null)
        .withFileName("D5678_001.nc")
        .build());

    auditStore.recordEvent(AuditMessage.builder()
        .withTraceId(traceId2)
        .withDac("aoml")
        .withTimestamp(Instant.parse("2026-10-05T12:00:06Z"))
        .withEventType(EventType.INFO)
        .withProcessor(AuditEventProcessor.VALIDATION)
        .withMessage("validate")
        .withStackTrace(null)
        .withFileName("D5678_001.nc")
        .build());

    auditStore.recordEvent(AuditMessage.builder()
        .withTraceId(traceId2)
        .withDac("aoml")
        .withTimestamp(Instant.parse("2026-10-05T12:00:07Z"))
        .withEventType(EventType.INFO)
        .withProcessor(AuditEventProcessor.INDEXING)
        .withMessage("index")
        .withStackTrace(null)
        .withFileName("D5678_001.nc")
        .build());

    auditStore.recordEvent(AuditMessage.builder()
        .withTraceId(traceId2)
        .withDac("aoml")
        .withTimestamp(Instant.parse("2026-10-05T12:00:08Z"))
        .withEventType(EventType.INFO)
        .withProcessor(AuditEventProcessor.FILE_STORE)
        .withMessage("stored")
        .withStackTrace(null)
        .withFileName("D5678_001.nc")
        .build());

    auditStore.recordEvent(AuditMessage.builder()
        .withTraceId(traceId2)
        .withDac("aoml")
        .withTimestamp(Instant.parse("2026-10-05T12:00:09Z"))
        .withEventType(EventType.INFO)
        .withProcessor(AuditEventProcessor.REMOVAL_COMPLETE)
        .withMessage("complete")
        .withStackTrace(null)
        .withFileName("D5678_001.nc")
        .build());

    auditStore.recordEvent(AuditMessage.builder()
        .withTraceId(traceId3)
        .withDac("aoml")
        .withTimestamp(Instant.parse("2026-10-05T12:00:10Z"))
        .withEventType(EventType.INFO)
        .withProcessor(AuditEventProcessor.FILE_RECEIVED)
        .withMessage("file received")
        .withStackTrace(null)
        .withFileName("D999_001.nc")
        .build());

    auditStore.recordEvent(AuditMessage.builder()
        .withTraceId(traceId3)
        .withDac("aoml")
        .withTimestamp(Instant.parse("2026-10-05T12:00:11Z"))
        .withEventType(EventType.ERROR)
        .withProcessor(AuditEventProcessor.VALIDATION)
        .withMessage("validate")
        .withStackTrace("Validation Failure")
        .withFileName("D999_001.nc")
        .build());

    auditStore.recordEvent(AuditMessage.builder()
        .withTraceId(traceId4)
        .withDac("aoml")
        .withTimestamp(Instant.parse("2026-10-05T12:00:11Z"))
        .withEventType(EventType.INFO)
        .withProcessor(AuditEventProcessor.FILE_RECEIVED)
        .withMessage("file received")
        .withStackTrace(null)
        .withFileName("D111_001.nc")
        .build());

    auditStore.recordEvent(AuditMessage.builder()
        .withTraceId(traceId4)
        .withDac("aoml")
        .withTimestamp(Instant.parse("2026-10-05T12:00:12Z"))
        .withEventType(EventType.INFO)
        .withProcessor(AuditEventProcessor.VALIDATION)
        .withMessage("validate")
        .withStackTrace("Validation Failure")
        .withFileName("D111_001.nc")
        .build());

    auditStore.recordEvent(AuditMessage.builder()
        .withTraceId(UUID.randomUUID())
        .withDac("bodc")
        .withTimestamp(Instant.parse("2026-10-05T12:00:11Z"))
        .withEventType(EventType.INFO)
        .withProcessor(AuditEventProcessor.FILE_RECEIVED)
        .withMessage("file received")
        .withStackTrace(null)
        .withFileName("D222_001.nc")
        .build());

    auditStore.recordEvent(AuditMessage.builder()
        .withTraceId(UUID.randomUUID())
        .withDac("aoml")
        .withTimestamp(Instant.parse("2026-10-06T00:00:00Z"))
        .withEventType(EventType.INFO)
        .withProcessor(AuditEventProcessor.FILE_RECEIVED)
        .withMessage("file received")
        .withStackTrace(null)
        .withFileName("D333_001.nc")
        .build());

    Instant now = Instant.parse("2026-10-05T23:00:00Z");

    DefaultSubmissionReportSearch search1 = DefaultSubmissionReportSearch.builder()
        .withDac("aoml")
        .withPageSize(2)
        .withTimestampLt(now)
        .build();

    SubmissionPage page1 = auditStore.findUnreportedSubmissions(search1);

    assertEquals(DefaultSubmissionPage.builder()
        .withSubmissionReportSearch(search1)
        .withTotalRecords(4)
        .withPage(Arrays.asList(
            AuditMessage.builder()
                .withTraceId(traceId1)
                .withEventId(page1.getPage().get(0).getEventId())
                .withDac("aoml")
                .withTimestamp(Instant.parse("2026-10-05T12:00:00Z"))
                .withEventType(EventType.INFO)
                .withProcessor(AuditEventProcessor.FILE_RECEIVED)
                .withMessage("file received")
                .withStackTrace(null)
                .withFileName("D1234_001.nc")
                .build(),
            AuditMessage.builder()
                .withTraceId(traceId2)
                .withEventId(page1.getPage().get(1).getEventId())
                .withDac("aoml")
                .withTimestamp(Instant.parse("2026-10-05T12:00:05Z"))
                .withEventType(EventType.INFO)
                .withProcessor(AuditEventProcessor.FILE_RECEIVED)
                .withMessage("file received")
                .withStackTrace(null)
                .withFileName("D5678_001.nc")
                .build()
        ))
        .build(), page1);

    SubmissionPage page2 = auditStore.findUnreportedSubmissions(page1.getNextPage().get());

    assertEquals(DefaultSubmissionPage.builder()
        .withSubmissionReportSearch(DefaultSubmissionReportSearch.builder()
            .withDac("aoml")
            .withPageSize(2)
            .withPageNumber(2)
            .withTimestampLt(now)
            .build())
        .withTotalRecords(4)
        .withPage(Arrays.asList(
            AuditMessage.builder()
                .withTraceId(traceId3)
                .withEventId(page2.getPage().get(0).getEventId())
                .withDac("aoml")
                .withTimestamp(Instant.parse("2026-10-05T12:00:10Z"))
                .withEventType(EventType.INFO)
                .withProcessor(AuditEventProcessor.FILE_RECEIVED)
                .withMessage("file received")
                .withStackTrace(null)
                .withFileName("D999_001.nc")
                .build(),
            AuditMessage.builder()
                .withTraceId(traceId4)
                .withEventId(page2.getPage().get(1).getEventId())
                .withDac("aoml")
                .withTimestamp(Instant.parse("2026-10-05T12:00:11Z"))
                .withEventType(EventType.INFO)
                .withProcessor(AuditEventProcessor.FILE_RECEIVED)
                .withMessage("file received")
                .withStackTrace(null)
                .withFileName("D111_001.nc")
                .build()
        ))
        .build(), page2);

    List<AuditMessage> history1 = auditStore.getHistoryForTraceId(traceId1);

    assertEquals(Arrays.asList(
        AuditMessage.builder()
            .withTraceId(traceId1)
            .withEventId(history1.get(0).getEventId())
            .withDac("aoml")
            .withTimestamp(Instant.parse("2026-10-05T12:00:00Z"))
            .withEventType(EventType.INFO)
            .withProcessor(AuditEventProcessor.FILE_RECEIVED)
            .withMessage("file received")
            .withStackTrace(null)
            .withFileName("D1234_001.nc")
            .build(),
        AuditMessage.builder()
            .withTraceId(traceId1)
            .withEventId(history1.get(1).getEventId())
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
            .withEventId(history1.get(2).getEventId())
            .withDac("aoml")
            .withTimestamp(Instant.parse("2026-10-05T12:00:02Z"))
            .withEventType(EventType.INFO)
            .withProcessor(AuditEventProcessor.INDEXING)
            .withMessage("index")
            .withStackTrace(null)
            .withFileName("D1234_001.nc")
            .build(),
        AuditMessage.builder()
            .withTraceId(traceId1)
            .withEventId(history1.get(3).getEventId())
            .withDac("aoml")
            .withTimestamp(Instant.parse("2026-10-05T12:00:03Z"))
            .withEventType(EventType.INFO)
            .withProcessor(AuditEventProcessor.FILE_STORE)
            .withMessage("stored")
            .withStackTrace(null)
            .withFileName("D1234_001.nc")
            .build(),
        AuditMessage.builder()
            .withTraceId(traceId1)
            .withEventId(history1.get(4).getEventId())
            .withDac("aoml")
            .withTimestamp(Instant.parse("2026-10-05T12:00:04Z"))
            .withEventType(EventType.INFO)
            .withProcessor(AuditEventProcessor.SUBMISSION_COMPLETE)
            .withMessage("complete")
            .withStackTrace(null)
            .withFileName("D1234_001.nc")
            .build()

    ), history1);

    List<AuditMessage> history2 = auditStore.getHistoryForTraceId(traceId2);

    assertEquals(Arrays.asList(
        AuditMessage.builder()
            .withTraceId(traceId2)
            .withEventId(history2.get(0).getEventId())
            .withDac("aoml")
            .withTimestamp(Instant.parse("2026-10-05T12:00:05Z"))
            .withEventType(EventType.INFO)
            .withProcessor(AuditEventProcessor.FILE_RECEIVED)
            .withMessage("file received")
            .withStackTrace(null)
            .withFileName("D5678_001.nc")
            .build(),
        AuditMessage.builder()
            .withTraceId(traceId2)
            .withEventId(history2.get(1).getEventId())
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
            .withEventId(history2.get(2).getEventId())
            .withDac("aoml")
            .withTimestamp(Instant.parse("2026-10-05T12:00:07Z"))
            .withEventType(EventType.INFO)
            .withProcessor(AuditEventProcessor.INDEXING)
            .withMessage("index")
            .withStackTrace(null)
            .withFileName("D5678_001.nc")
            .build(),
        AuditMessage.builder()
            .withTraceId(traceId2)
            .withEventId(history2.get(3).getEventId())
            .withDac("aoml")
            .withTimestamp(Instant.parse("2026-10-05T12:00:08Z"))
            .withEventType(EventType.INFO)
            .withProcessor(AuditEventProcessor.FILE_STORE)
            .withMessage("stored")
            .withStackTrace(null)
            .withFileName("D5678_001.nc")
            .build(),
        AuditMessage.builder()
            .withTraceId(traceId2)
            .withEventId(history2.get(4).getEventId())
            .withDac("aoml")
            .withTimestamp(Instant.parse("2026-10-05T12:00:09Z"))
            .withEventType(EventType.INFO)
            .withProcessor(AuditEventProcessor.REMOVAL_COMPLETE)
            .withMessage("complete")
            .withStackTrace(null)
            .withFileName("D5678_001.nc")
            .build()
    ), history2);

    List<AuditMessage> history3 = auditStore.getHistoryForTraceId(traceId3);

    assertEquals(Arrays.asList(
        AuditMessage.builder()
            .withTraceId(traceId3)
            .withEventId(history3.get(0).getEventId())
            .withDac("aoml")
            .withTimestamp(Instant.parse("2026-10-05T12:00:10Z"))
            .withEventType(EventType.INFO)
            .withProcessor(AuditEventProcessor.FILE_RECEIVED)
            .withMessage("file received")
            .withStackTrace(null)
            .withFileName("D999_001.nc")
            .build(),
        AuditMessage.builder()
            .withTraceId(traceId3)
            .withEventId(history3.get(1).getEventId())
            .withDac("aoml")
            .withTimestamp(Instant.parse("2026-10-05T12:00:11Z"))
            .withEventType(EventType.ERROR)
            .withProcessor(AuditEventProcessor.VALIDATION)
            .withMessage("validate")
            .withStackTrace("Validation Failure")
            .withFileName("D999_001.nc")
            .build()
    ), history3);

    List<AuditMessage> history4 = auditStore.getHistoryForTraceId(traceId4);

    assertEquals(Arrays.asList(
        AuditMessage.builder()
            .withTraceId(traceId4)
            .withEventId(history4.get(0).getEventId())
            .withDac("aoml")
            .withTimestamp(Instant.parse("2026-10-05T12:00:11Z"))
            .withEventType(EventType.INFO)
            .withProcessor(AuditEventProcessor.FILE_RECEIVED)
            .withMessage("file received")
            .withStackTrace(null)
            .withFileName("D111_001.nc")
            .build(),
        AuditMessage.builder()
            .withTraceId(traceId4)
            .withEventId(history4.get(1).getEventId())
            .withDac("aoml")
            .withTimestamp(Instant.parse("2026-10-05T12:00:12Z"))
            .withEventType(EventType.INFO)
            .withProcessor(AuditEventProcessor.VALIDATION)
            .withMessage("validate")
            .withStackTrace("Validation Failure")
            .withFileName("D111_001.nc")
            .build()
    ), history4);

    auditStore.markSubmissionReported(history1.get(0));
    auditStore.markSubmissionReported(history2.get(0));
    auditStore.markSubmissionReported(history3.get(0));

    SubmissionPage recheck = auditStore.findUnreportedSubmissions(search1);

    assertEquals(DefaultSubmissionPage.builder()
        .withSubmissionReportSearch(search1)
        .withTotalRecords(1)
        .withPage(Arrays.asList(
            AuditMessage.builder()
                .withTraceId(traceId4)
                .withEventId(page2.getPage().get(1).getEventId())
                .withDac("aoml")
                .withTimestamp(Instant.parse("2026-10-05T12:00:11Z"))
                .withEventType(EventType.INFO)
                .withProcessor(AuditEventProcessor.FILE_RECEIVED)
                .withMessage("file received")
                .withStackTrace(null)
                .withFileName("D111_001.nc")
                .build()
        ))
        .build(), recheck);
  }

}