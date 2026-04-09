package edu.colorado.cires.argonaut.processor.report.jpa;

import edu.colorado.cires.argonaut.messaging.core.databind.NcSubmissionMessage;
import edu.colorado.cires.argonaut.processor.core.SubmissionReportProcessor;
import edu.colorado.cires.argonaut.processor.report.jpa.entity.SubmissionRecordEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

public class JpaFileSubmissionReportProcessor implements SubmissionReportProcessor {

  private EntityManagerFactory entityManagerFactory;

  @Override
  public void appendReport(NcSubmissionMessage message) {
    try (EntityManager em = entityManagerFactory.createEntityManager()) {
      EntityTransaction tx = em.getTransaction();
      tx.begin();
      try {
        em.persist(SubmissionRecordEntity.create(message));
        tx.commit();
      } catch (Exception e) {
        tx.rollback();
        throw e;
      }
    }
  }

  public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
    this.entityManagerFactory = entityManagerFactory;
  }
}
