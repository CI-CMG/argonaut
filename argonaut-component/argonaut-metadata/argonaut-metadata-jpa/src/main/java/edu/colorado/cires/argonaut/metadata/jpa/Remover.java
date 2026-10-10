package edu.colorado.cires.argonaut.metadata.jpa;

import edu.colorado.cires.argonaut.messaging.core.databind.ArgoFileType;
import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord;
import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord.FileStatus;
import edu.colorado.cires.argonaut.metadata.jpa.entity.ArgonautFileRemovedTimeEntity;
import edu.colorado.cires.argonaut.metadata.jpa.entity.ArgoFloatMetadataEntity;
import edu.colorado.cires.argonaut.metadata.jpa.entity.ArgoFloatProfileEntity;
import edu.colorado.cires.argonaut.metadata.jpa.entity.ArgonautProfileMergeFileEntity;
import edu.colorado.cires.argonaut.metadata.jpa.entity.ArgoFloatTechnicalInfoEntity;
import edu.colorado.cires.argonaut.metadata.jpa.entity.ArgoFloatTrajectoryEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.LockModeType;
import jakarta.persistence.OptimisticLockException;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

class Remover {

  private final EntityManagerFactory entityManagerFactory;

  Remover(EntityManagerFactory entityManagerFactory) {
    this.entityManagerFactory = entityManagerFactory;
  }

  private static void removeProfile(EntityManager em, MetadataRecord record) {
    ArgoFloatProfileEntity existing = em.find(ArgoFloatProfileEntity.class, record.getFile(), LockModeType.OPTIMISTIC);
    if (existing != null) {
      if (!FileStatus.REMOVED.name().equals(existing.getFileStatus())) {
        List<ArgonautFileRemovedTimeEntity> frts = em.createQuery("SELECT frt FROM ArgonautFileRemovedTimeEntity frt WHERE frt.argoFloatProfile = :profile",
                ArgonautFileRemovedTimeEntity.class)
            .setParameter("profile", existing)
            .getResultList();
        if (frts.isEmpty()) {
          ArgonautFileRemovedTimeEntity frt = new ArgonautFileRemovedTimeEntity();
          frt.setRemovedTime(record.getActionTimestamp().atOffset(ZoneOffset.UTC).toZonedDateTime());
          frt.setArgoFloatProfile(existing);
          frt.setFileType(existing.getFileType());
          frt.setId(UUID.randomUUID());
          em.persist(frt);
        }
      }
      existing.setFileStatus(FileStatus.REMOVED.name());
    }
  }

  private static void removeMetadata(EntityManager em, MetadataRecord record) {
    ArgoFloatMetadataEntity existing = em.find(ArgoFloatMetadataEntity.class, record.getFile(), LockModeType.OPTIMISTIC);
    if (existing != null) {
      if (!FileStatus.REMOVED.name().equals(existing.getFileStatus())) {
        List<ArgonautFileRemovedTimeEntity> frts = em.createQuery("SELECT frt FROM ArgonautFileRemovedTimeEntity frt WHERE frt.argoFloatMetadata = :metadata",
                ArgonautFileRemovedTimeEntity.class)
            .setParameter("metadata", existing)
            .getResultList();
        if (frts.isEmpty()) {
          ArgonautFileRemovedTimeEntity frt = new ArgonautFileRemovedTimeEntity();
          frt.setRemovedTime(record.getActionTimestamp().atOffset(ZoneOffset.UTC).toZonedDateTime());
          frt.setArgoFloatMetadata(existing);
          frt.setFileType(ArgoFileType.METADATA.toString());
          frt.setId(UUID.randomUUID());
          em.persist(frt);
        }
      }
      existing.setFileStatus(FileStatus.REMOVED.name());
      existing.setLastUpdatedTime(record.getActionTimestamp().atOffset(ZoneOffset.UTC).toZonedDateTime());
    }
  }

  private static void removeTrajectory(EntityManager em, MetadataRecord record) {
    ArgoFloatTrajectoryEntity existing = em.find(ArgoFloatTrajectoryEntity.class, record.getFile(), LockModeType.OPTIMISTIC);
    if (existing != null) {
      existing.setFileStatus(FileStatus.REMOVED.name());
      existing.setLastUpdatedTime(record.getActionTimestamp().atOffset(ZoneOffset.UTC).toZonedDateTime());
    }
  }

  private static void removeTechnical(EntityManager em, MetadataRecord record) {
    ArgoFloatTechnicalInfoEntity existing = em.find(ArgoFloatTechnicalInfoEntity.class, record.getFile(), LockModeType.OPTIMISTIC);
    if (existing != null) {
      existing.setFileStatus(FileStatus.REMOVED.name());
      existing.setLastUpdatedTime(record.getActionTimestamp().atOffset(ZoneOffset.UTC).toZonedDateTime());
    }
  }

  private static void removeMultiProfileMerge(EntityManager em, MetadataRecord record) {
    ArgonautProfileMergeFileEntity existing = em.find(ArgonautProfileMergeFileEntity.class, record.getFile(), LockModeType.OPTIMISTIC);
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
      case TRAJECTORY:
        removeTrajectory(em, record);
        break;
      case TECHNICAL_DATA:
        removeTechnical(em, record);
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

  private static void deleteProfile(EntityManager em, MetadataRecord record) {
    ArgoFloatProfileEntity existing = em.find(ArgoFloatProfileEntity.class, record.getFile(), LockModeType.OPTIMISTIC);
    if (existing != null) {
      if (FileStatus.REMOVED.name().equals(existing.getFileStatus())) {
        List<ArgonautFileRemovedTimeEntity> frts = em.createQuery("SELECT frt FROM ArgonautFileRemovedTimeEntity frt WHERE frt.argoFloatProfile = :profile",
                ArgonautFileRemovedTimeEntity.class)
            .setParameter("profile", existing)
            .getResultList();
        for (ArgonautFileRemovedTimeEntity frt : frts) {
          em.remove(frt);
        }
      }
    }
  }

  private static void deleteMetadata(EntityManager em, MetadataRecord record) {
    ArgoFloatMetadataEntity existing = em.find(ArgoFloatMetadataEntity.class, record.getFile(), LockModeType.OPTIMISTIC);
    if (existing != null) {
      if (FileStatus.REMOVED.name().equals(existing.getFileStatus())) {
        List<ArgonautFileRemovedTimeEntity> frts = em.createQuery("SELECT frt FROM ArgonautFileRemovedTimeEntity frt WHERE frt.argoFloatMetadata = :metadata",
                ArgonautFileRemovedTimeEntity.class)
            .setParameter("metadata", existing)
            .getResultList();
        for (ArgonautFileRemovedTimeEntity frt : frts) {
          em.remove(frt);
        }
      }
      existing.setLastUpdatedTime(record.getActionTimestamp().atOffset(ZoneOffset.UTC).toZonedDateTime());
    }
  }

  private static void delete(EntityManager em, MetadataRecord record) {
    switch (record.getFileType()) {
      case PROFILE_CORE:
      case PROFILE_BIOCHEMICAL:
        deleteProfile(em, record);
        break;
      case TRAJECTORY:
        deleteTrajectory(em, record);
        break;
      case TECHNICAL_DATA:
        deleteTechnical(em, record);
        break;
      case METADATA:
        deleteMetadata(em, record);
        break;
      default:
        throw new UnsupportedOperationException("Unsupported file type: " + record.getFileType());
    }
  }

  private static void deleteTechnical(EntityManager em, MetadataRecord record) {
    ArgoFloatTechnicalInfoEntity existing = em.find(ArgoFloatTechnicalInfoEntity.class, record.getFile(), LockModeType.OPTIMISTIC);
    if (existing != null) {
      existing.setLastUpdatedTime(record.getActionTimestamp().atOffset(ZoneOffset.UTC).toZonedDateTime());
    }
  }

  private static void deleteTrajectory(EntityManager em, MetadataRecord record) {
    ArgoFloatTrajectoryEntity existing = em.find(ArgoFloatTrajectoryEntity.class, record.getFile(),
      LockModeType.OPTIMISTIC);
    if (existing != null) {
      existing.setLastUpdatedTime(record.getActionTimestamp().atOffset(ZoneOffset.UTC).toZonedDateTime());
    }
  }

  void delete(MetadataRecord record) {
    while (true) {
      try (EntityManager em = entityManagerFactory.createEntityManager()) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        try {
          delete(em, record);
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
