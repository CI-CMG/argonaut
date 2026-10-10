package edu.colorado.cires.argonaut.metadata.jpa;

import edu.colorado.cires.argonaut.messaging.core.databind.ArgoFileType;
import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord;
import edu.colorado.cires.argonaut.metadata.jpa.entity.ArgoFloatMetadataEntity;
import edu.colorado.cires.argonaut.metadata.jpa.entity.ArgonautSyntheticMergeMetadataEntity;
import edu.colorado.cires.argonaut.metadata.jpa.entity.ArgoFloatProfileEntity;
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
            ArgoFloatMetadataEntity metadata = em.find(ArgoFloatMetadataEntity.class, record.getFile(), LockModeType.OPTIMISTIC);
            List<ArgoFloatProfileEntity> profiles = record.getRelatedFiles().stream()
                .map(file -> em.find(ArgoFloatProfileEntity.class, file, LockModeType.OPTIMISTIC)).toList();
            if (metadata != null) {
              for (ArgoFloatProfileEntity profile : profiles) {
                if (profile != null) {
                  LOGGER.info("Updating synth merge for " + profile.getFilePath());
                  profile.setSyntheticMergeTime(remove ? null : record.getActionTimestamp().atZone(ZoneId.of("UTC")));
                }
                List<ArgonautSyntheticMergeMetadataEntity> mds = em.createQuery(
                        "SELECT m FROM ArgonautSyntheticMergeMetadataEntity m WHERE m.argoFloatProfile = :profile AND m.argoFloatMetadata = :metadata")
                    .setParameter("profile", profile)
                    .setParameter("metadata", metadata)
                    .getResultList();

                if (remove) {
                  for (ArgonautSyntheticMergeMetadataEntity md : mds) {
                    em.remove(md);
                  }
                } else if (mds.isEmpty()) {
                  ArgonautSyntheticMergeMetadataEntity md = new ArgonautSyntheticMergeMetadataEntity();
                  md.setArgoFloatMetadata(metadata);
                  md.setArgoFloatProfile(profile);
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
