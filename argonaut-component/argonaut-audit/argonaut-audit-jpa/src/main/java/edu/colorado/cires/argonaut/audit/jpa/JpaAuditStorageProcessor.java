package edu.colorado.cires.argonaut.audit.jpa;

import edu.colorado.cires.argonaut.audit.core.AuditStorageProcessor;
import edu.colorado.cires.argonaut.audit.jpa.entity.AuditEntity;
import edu.colorado.cires.argonaut.messaging.core.databind.AuditMessage;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import java.time.ZoneId;
import java.util.UUID;

public class JpaAuditStorageProcessor implements AuditStorageProcessor {

  private EntityManagerFactory entityManagerFactory;

  public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
    this.entityManagerFactory = entityManagerFactory;
  }

  @Override
  public void recordEvent(AuditMessage auditMessage) {
    try(EntityManager em = entityManagerFactory.createEntityManager()) {
      EntityTransaction tx = em.getTransaction();
      tx.begin();
      try {
        AuditEntity entity = new AuditEntity();
        entity.setId(UUID.randomUUID());
        entity.setTraceId(auditMessage.getTraceId());
        entity.setTimestamp(auditMessage.getTimestamp().atZone(ZoneId.of("UTC")));
        entity.setDac(auditMessage.getDac());
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
}
