package edu.colorado.cires.argonaut.metadata.jpa;

import edu.colorado.cires.argonaut.messaging.core.databind.ArgoFileType;
import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord;
import edu.colorado.cires.argonaut.metadata.jpa.entity.ProfileFileEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.LockModeType;
import jakarta.persistence.OptimisticLockException;
import java.time.ZoneId;

class SyntheticMerger {

  private final EntityManagerFactory entityManagerFactory;

  SyntheticMerger(EntityManagerFactory entityManagerFactory) {
    this.entityManagerFactory = entityManagerFactory;
  }

  void updateSynthMerge(MetadataRecord record) {
    if (record.getFileType() == ArgoFileType.PROFILE_CORE || record.getFileType() == ArgoFileType.PROFILE_BIOCHEMICAL) {
      while (true) {
        try (EntityManager em = entityManagerFactory.createEntityManager()) {
          EntityTransaction tx = em.getTransaction();
          tx.begin();
          try {
            ProfileFileEntity entity = em.find(ProfileFileEntity.class, record.getFile(), LockModeType.OPTIMISTIC);
            if (entity != null) {
              entity.setSyntheticMergeTime(record.getActionTimestamp().atZone(ZoneId.of("UTC")));
            }
            tx.commit();
            break;
          } catch (OptimisticLockException e) {
            tx.rollback();
          } catch (Exception e) {
            tx.rollback();
            throw e;
          }
        }
      }
    }
  }
}
