package edu.colorado.cires.argonaut.metadata.jpa;

import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord;
import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord.FileStatus;
import edu.colorado.cires.argonaut.metadata.jpa.entity.MetadataFileEntity;
import edu.colorado.cires.argonaut.metadata.jpa.entity.ProfileFileEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.LockModeType;

class Remover {

  private final EntityManagerFactory entityManagerFactory;

  Remover(EntityManagerFactory entityManagerFactory) {
    this.entityManagerFactory = entityManagerFactory;
  }

  private static void removeProfile(EntityManager em, MetadataRecord record) {
    ProfileFileEntity existing = em.find(ProfileFileEntity.class, record.getFile());
    if (existing != null) {
      existing.setFileStatus(FileStatus.REMOVED.name());
    }
  }

  private static void removeMetadata(EntityManager em, MetadataRecord record) {
    MetadataFileEntity existing = em.find(MetadataFileEntity.class, record.getFile());
    if (existing != null) {
      existing.setFileStatus(FileStatus.REMOVED.name());
      em.createQuery("""
              UPDATE ProfileFileEntity profile SET profile.syntheticMergeTime = null
              WHERE profile.cycle.floatId.id = :fid
          """).setParameter("fid", existing.getFloatId().getFloatId()).executeUpdate();
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
      default:
        throw new UnsupportedOperationException("Unsupported file type: " + record.getFileType());
    }
  }

  void remove(MetadataRecord record) {
    try (EntityManager em = entityManagerFactory.createEntityManager()) {
      EntityTransaction tx = em.getTransaction();
      tx.begin();
      try {
        remove(em, record);
        tx.commit();
      } catch (Exception e) {
        tx.rollback();
        throw e;
      }
    }
  }

}
