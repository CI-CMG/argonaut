package edu.colorado.cires.argonaut.metadata.jpa;

import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord;
import edu.colorado.cires.argonaut.messaging.core.databind.NcSubmissionMessage.FileType;
import edu.colorado.cires.argonaut.metadata.jpa.entity.ProfileFileEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import java.time.ZoneId;

class MultiFloatMerger {

  private final EntityManagerFactory entityManagerFactory;

  MultiFloatMerger(EntityManagerFactory entityManagerFactory) {
    this.entityManagerFactory = entityManagerFactory;
  }

  void updateMultiFloatMerge(MetadataRecord record) {
    if (record.getFileType() == FileType.CORE_ARGO_PROFILE) {
      try (EntityManager em = entityManagerFactory.createEntityManager()) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        try {
          ProfileFileEntity entity = em.find(ProfileFileEntity.class, record.getFile());
          if (entity != null) {
            entity.setMultiFloatMergeTime(record.getActionTimestamp().atZone(ZoneId.of("UTC")));
          }
          tx.commit();
        } catch (Exception e) {
          tx.rollback();
          throw e;
        }
      }
    }
  }
}
