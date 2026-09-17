package edu.colorado.cires.argonaut.metadata.jpa;

import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord;
import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord.FileStatus;
import edu.colorado.cires.argonaut.metadata.jpa.entity.MetadataFileEntity;
import edu.colorado.cires.argonaut.metadata.jpa.entity.ProfileFileEntity;
import edu.colorado.cires.argonaut.metadata.jpa.entity.ProfileMergeFileEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.LockModeType;
import jakarta.persistence.OptimisticLockException;

class Remover {

  private final EntityManagerFactory entityManagerFactory;

  Remover(EntityManagerFactory entityManagerFactory) {
    this.entityManagerFactory = entityManagerFactory;
  }

  private static void removeProfile(EntityManager em, MetadataRecord record) {
    ProfileFileEntity existing = em.find(ProfileFileEntity.class, record.getFile(), LockModeType.OPTIMISTIC);
    if (existing != null) {
      existing.setFileStatus(FileStatus.REMOVED.name());
    }
  }

  private static void removeMetadata(EntityManager em, MetadataRecord record) {
    MetadataFileEntity existing = em.find(MetadataFileEntity.class, record.getFile(), LockModeType.OPTIMISTIC);
    if (existing != null) {
      existing.setFileStatus(FileStatus.REMOVED.name());
    }
  }

  private static void removeMultiProfileMerge(EntityManager em, MetadataRecord record) {
    ProfileMergeFileEntity existing = em.find(ProfileMergeFileEntity.class, record.getFile(), LockModeType.OPTIMISTIC);
    if (existing != null) {
      existing.setFileStatus(FileStatus.REMOVED.name());
    } else {
      Updater.createOrUpdateProfileMergeFile(record, em, FileStatus.REMOVED);
    }
  }

  private static void remove(EntityManager em, MetadataRecord record) {
    switch (record.getFileType()) {
      case PROFILE_CORE:
      case PROFILE_BIOCHEMICAL:
      case SYNTHETIC_PROFILE_SINGLE_CYCLE:
        removeProfile(em, record);
        break;
      case METADATA:
        removeMetadata(em, record);
        break;
      case PROFILE_MULTI_CYCLE:
        removeMultiProfileMerge(em, record);
        break;
      default:
        throw new UnsupportedOperationException("Unsupported file type: " + record.getFileType());
    }
  }

  void remove(MetadataRecord record) {
    while (true) {
      try (EntityManager em = entityManagerFactory.createEntityManager()) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        try {
          remove(em, record);
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
