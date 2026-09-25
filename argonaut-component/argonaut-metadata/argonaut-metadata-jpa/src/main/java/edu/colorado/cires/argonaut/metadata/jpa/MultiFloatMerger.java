package edu.colorado.cires.argonaut.metadata.jpa;

import edu.colorado.cires.argonaut.messaging.core.databind.ArgoFileType;
import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord;
import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord.FileStatus;
import edu.colorado.cires.argonaut.metadata.jpa.entity.ProfileFileEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.LockModeType;
import jakarta.persistence.OptimisticLockException;
import java.time.ZoneId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class MultiFloatMerger {

  private static final Logger LOGGER = LoggerFactory.getLogger(MultiFloatMerger.class);

  private final EntityManagerFactory entityManagerFactory;

  MultiFloatMerger(EntityManagerFactory entityManagerFactory) {
    this.entityManagerFactory = entityManagerFactory;
  }

  void updateMultiFloatMerge(MetadataRecord record, boolean remove) {
    if (record.getFileType() == ArgoFileType.PROFILE_CORE) {
      while (true) {
        try (EntityManager em = entityManagerFactory.createEntityManager()) {
          EntityTransaction tx = em.getTransaction();
          tx.begin();
          try {
            ProfileFileEntity entity = em.find(ProfileFileEntity.class, record.getFile(), LockModeType.OPTIMISTIC);
            if (entity != null) {
              LOGGER.info("Updating multi-float merge for " + record.getFile());
              entity.setMultiFloatMergeTime(remove ? null : record.getActionTimestamp().atZone(ZoneId.of("UTC")));
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

  void updateGeoFloatMerge(MetadataRecord record, boolean remove) {
    if (record.getFileType() == ArgoFileType.PROFILE_CORE) {
      while (true) {
        try (EntityManager em = entityManagerFactory.createEntityManager()) {
          EntityTransaction tx = em.getTransaction();
          tx.begin();
          try {
            ProfileFileEntity entity = em.find(ProfileFileEntity.class, record.getFile(), LockModeType.OPTIMISTIC);
            if (entity != null) {
              LOGGER.info("Updating geo merge for " + record.getFile());
              entity.setGeoMergeTime(remove ? null : record.getActionTimestamp().atZone(ZoneId.of("UTC")));
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

  void updateLatestMerge(MetadataRecord record, boolean remove) {
    if (record.getFileType() == ArgoFileType.PROFILE_CORE) {
      while (true) {
        try (EntityManager em = entityManagerFactory.createEntityManager()) {
          EntityTransaction tx = em.getTransaction();
          tx.begin();
          try {
            ProfileFileEntity entity = em.find(ProfileFileEntity.class, record.getFile(), LockModeType.OPTIMISTIC);
            if (entity != null) {
              LOGGER.info("Updating latest merge for " + record.getFile());
              entity.setLatestMergeFileName(remove ? null : record.getRelatedFiles().get(0));
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
