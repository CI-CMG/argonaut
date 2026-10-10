package edu.colorado.cires.argonaut.audit.jpa;

import edu.colorado.cires.argonaut.audit.core.AuditStore;
import edu.colorado.cires.argonaut.audit.core.DefaultSubmissionPage;
import edu.colorado.cires.argonaut.audit.core.DefaultSubmissionReportSearch;
import edu.colorado.cires.argonaut.audit.core.SubmissionPage;
import edu.colorado.cires.argonaut.audit.core.SubmissionReportSearch;
import edu.colorado.cires.argonaut.audit.jpa.entity.ArgonautAuditEntity;
import edu.colorado.cires.argonaut.messaging.core.databind.AuditEventProcessor;
import edu.colorado.cires.argonaut.messaging.core.databind.AuditMessage;
import edu.colorado.cires.argonaut.messaging.core.databind.AuditMessage.EventType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

public class JpaAuditStore implements AuditStore {

  private EntityManagerFactory entityManagerFactory;

  public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
    this.entityManagerFactory = entityManagerFactory;
  }

  @Override
  public void recordEvent(AuditMessage auditMessage) {
    try (EntityManager em = entityManagerFactory.createEntityManager()) {
      EntityTransaction tx = em.getTransaction();
      tx.begin();
      try {
        ArgonautAuditEntity entity = new ArgonautAuditEntity();
        entity.setId(UUID.randomUUID());
        entity.setTraceId(auditMessage.getTraceId());
        entity.setTimestamp(auditMessage.getTimestamp().atZone(ZoneId.of("UTC")));
        entity.setDacName(auditMessage.getDac());
        entity.setEventType(auditMessage.getEventType().toString());
        entity.setProcessor(auditMessage.getProcessor().toString());
        entity.setMessage(auditMessage.getMessage());
        entity.setStackTrace(auditMessage.getStackTrace());
        entity.setFileName(auditMessage.getFileName());
        em.merge(entity);
        tx.commit();
      } catch (Exception e) {
        tx.rollback();
        throw e;
      }
    }
  }

  @Override
  public void markSubmissionReported(AuditMessage auditMessage) {
    try (EntityManager em = entityManagerFactory.createEntityManager()) {
      EntityTransaction tx = em.getTransaction();
      tx.begin();
      try {
        ArgonautAuditEntity entity = em.find(ArgonautAuditEntity.class, auditMessage.getEventId().toString());
        if (entity != null) {
          entity.setReportDate(auditMessage.getTimestamp().atZone(ZoneId.of("UTC")));
        }
        tx.commit();
      } catch (Exception e) {
        tx.rollback();
        throw e;
      }
    }
  }

  private static AuditMessage fromEntity(ArgonautAuditEntity entity) {
    return AuditMessage.builder()
        .withTraceId(entity.getTraceId())
        .withEventId(entity.getId())
        .withDac(entity.getDacName())
        .withTimestamp(entity.getTimestamp().toInstant())
        .withEventType(EventType.valueOf(entity.getEventType()))
        .withProcessor(AuditEventProcessor.valueOf(entity.getProcessor()))
        .withMessage(entity.getMessage())
        .withStackTrace(entity.getStackTrace())
        .withFileName(entity.getFileName())
        .build();
  }

  @Override
  public SubmissionPage findUnreportedSubmissions(SubmissionReportSearch search) {
    try (EntityManager em = entityManagerFactory.createEntityManager()) {
      long count = em.createQuery(
              """
                    SELECT COUNT(a.id) FROM ArgonautAuditEntity a 
                      WHERE a.dacName = :dac 
                      AND a.processor = 'FILE_RECEIVED'
                      AND (a.message = 'triggered file update' OR a.message = 'triggered file removal')
                      AND a.timestamp < :timestamp
                      AND a.reportDate IS NULL
                  """,
              Long.class)
          .setParameter("dac", search.getDac())
          .setParameter("timestamp", search.getTimestampLt().atZone(ZoneId.of("UTC")))
          .getSingleResult();

      List<ArgonautAuditEntity> auditRecords = em.createQuery(
              """
                    SELECT a FROM ArgonautAuditEntity a 
                      WHERE a.dacName = :dac 
                      AND a.processor = 'FILE_RECEIVED'
                      AND (a.message = 'triggered file update' OR a.message = 'triggered file removal')
                      AND a.timestamp < :timestamp
                      AND a.reportDate IS NULL
                      ORDER BY a.timestamp, a.id
                  """,
              ArgonautAuditEntity.class)
          .setParameter("dac", search.getDac())
          .setParameter("timestamp", search.getTimestampLt().atZone(ZoneId.of("UTC")))
          .setMaxResults(search.getPageSize())
          .setFirstResult((search.getPageNumber() - 1) * search.getPageSize())
          .getResultList();

      return DefaultSubmissionPage.builder()
          .withTotalRecords(count)
          .withSubmissionReportSearch(DefaultSubmissionReportSearch.builder(search).build())
          .withPage(auditRecords.stream().map(JpaAuditStore::fromEntity).toList())
          .build();
    }
  }

  @Override
  public List<AuditMessage> getHistoryForTraceId(UUID traceId) {
    try (EntityManager em = entityManagerFactory.createEntityManager()) {
      List<ArgonautAuditEntity> auditRecords = em.createQuery(
              "SELECT a FROM ArgonautAuditEntity a WHERE a.traceId = :traceId ORDER BY a.timestamp, a.id",
              ArgonautAuditEntity.class)
          .setParameter("traceId", traceId.toString())
          .getResultList();

      return auditRecords.stream().map(JpaAuditStore::fromEntity).toList();
    }
  }
}
