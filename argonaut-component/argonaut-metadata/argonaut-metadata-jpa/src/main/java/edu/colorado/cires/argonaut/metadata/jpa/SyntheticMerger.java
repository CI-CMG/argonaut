package edu.colorado.cires.argonaut.metadata.jpa;

import edu.colorado.cires.argonaut.messaging.core.databind.ArgoFileType;
import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord;
import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord.FileStatus;
import edu.colorado.cires.argonaut.metadata.jpa.entity.MetadataFileEntity;
import edu.colorado.cires.argonaut.metadata.jpa.entity.MetadataSyntheticMergeEntity;
import edu.colorado.cires.argonaut.metadata.jpa.entity.ProfileFileEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.LockModeType;
import jakarta.persistence.OptimisticLockException;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class SyntheticMerger {

  private static final Logger LOGGER = LoggerFactory.getLogger(SyntheticMerger.class);

  private final EntityManagerFactory entityManagerFactory;

  SyntheticMerger(EntityManagerFactory entityManagerFactory) {
    this.entityManagerFactory = entityManagerFactory;
  }

  void updateSynthMerge(MetadataRecord record, boolean remove) {
    if (record.getFileType() == ArgoFileType.METADATA) {
      while (true) {
        try (EntityManager em = entityManagerFactory.createEntityManager()) {
          EntityTransaction tx = em.getTransaction();
          tx.begin();
          try {
            MetadataFileEntity metadata = em.find(MetadataFileEntity.class, record.getFile(), LockModeType.OPTIMISTIC);
            List<ProfileFileEntity> profiles = record.getRelatedFiles().stream()
                .map(file -> em.find(ProfileFileEntity.class, file, LockModeType.OPTIMISTIC)).toList();
            if (metadata != null) {
              for (ProfileFileEntity profile : profiles) {
                if (profile != null) {
                  LOGGER.info("Updating synth merge for " + profile.getFile());
                  profile.setSyntheticMergeTime(remove ? null : record.getActionTimestamp().atZone(ZoneId.of("UTC")));
                }
                List<MetadataSyntheticMergeEntity> mds = em.createQuery(
                        "SELECT m FROM MetadataSyntheticMergeEntity m WHERE m.profile = :profile AND m.metadata = :metadata")
                    .setParameter("profile", profile)
                    .setParameter("metadata", metadata)
                    .getResultList();

                if (remove) {
                  for (MetadataSyntheticMergeEntity md : mds) {
                    em.remove(md);
                  }
                } else if (mds.isEmpty()) {
                  MetadataSyntheticMergeEntity md = new MetadataSyntheticMergeEntity();
                  md.setMetadata(metadata);
                  md.setProfile(profile);
                  md.setSyntheticMergeTime(record.getActionTimestamp().atZone(ZoneId.of("UTC")));
                  md.setId(UUID.randomUUID());
                  em.persist(md);
                }

              }

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
