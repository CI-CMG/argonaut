package edu.colorado.cires.argonaut.metadata.jpa;

import static edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord.FileStatus.ACTIVE;

import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord;
import edu.colorado.cires.argonaut.metadata.jpa.entity.CycleEntity;
import edu.colorado.cires.argonaut.metadata.jpa.entity.DacEntity;
import edu.colorado.cires.argonaut.metadata.jpa.entity.FloatEntity;
import edu.colorado.cires.argonaut.metadata.jpa.entity.MetadataFileEntity;
import edu.colorado.cires.argonaut.metadata.jpa.entity.ProfileFileEntity;
import edu.colorado.cires.argonaut.metadata.jpa.entity.ProfileMergeFileEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import java.time.ZoneOffset;
import java.util.Objects;

class Updater {

  private final EntityManagerFactory entityManagerFactory;

  Updater(EntityManagerFactory entityManagerFactory) {
    this.entityManagerFactory = entityManagerFactory;
  }

  private void createDacIfMissing(MetadataRecord record) {
    String dac = Objects.requireNonNull(record.getDac());

    int failedAttempts = 0;
    boolean success = false;
    while (!success) {
      try (EntityManager em = entityManagerFactory.createEntityManager()) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        try {
          DacEntity entity = em.find(DacEntity.class, dac);
          if (entity != null) {
            return;
          } else {
            entity = new DacEntity();
            entity.setDac(dac);
            em.merge(entity);
            tx.commit();
          }
        } catch (Exception e) {
          tx.rollback();
          throw e;
        }
        success = true;
      } catch (Exception e) {
        failedAttempts++;
        if (failedAttempts >= 2) {
          throw e;
        }
      }
    }
  }

  private void createFloatIfMissing(MetadataRecord record) {

    String dac = Objects.requireNonNull(record.getDac());
    String floatId = Objects.requireNonNull(record.getFloatId());
    String id = getFloatId(record);

    int failedAttempts = 0;
    boolean success = false;
    while (!success) {
      try (EntityManager em = entityManagerFactory.createEntityManager()) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        try {
          DacEntity dacEntity = em.find(DacEntity.class, dac);
          FloatEntity entity = em.find(FloatEntity.class, id);
          if (entity != null) {
            return;
          } else {
            entity = new FloatEntity();
            entity.setId(id);
            entity.setFloatId(floatId);
            entity.setDac(dacEntity);
            em.merge(entity);
            tx.commit();
          }
        } catch (Exception e) {
          tx.rollback();
          throw e;
        }
        success = true;
      } catch (Exception e) {
        failedAttempts++;
        if (failedAttempts >= 2) {
          throw e;
        }
      }
    }
  }

  private static String getFloatId(MetadataRecord record) {
    String dac = Objects.requireNonNull(record.getDac());
    String floatId = Objects.requireNonNull(record.getFloatId());
    return dac + "_" + floatId;
  }

  private static String getCycleId(MetadataRecord record) {
    String dac = Objects.requireNonNull(record.getDac());
    String floatId = Objects.requireNonNull(record.getFloatId());
    String dataMode = Objects.requireNonNull(record.getParameterDataMode());
    String direction = Objects.requireNonNull(record.getDirection());
    String cycleNumber = Objects.requireNonNull(record.getCycleNumber());
    return dac + "_" + dataMode + floatId + cycleNumber + direction;
  }

  private void createCycleIfMissing(MetadataRecord record) {
    String dataMode = Objects.requireNonNull(record.getParameterDataMode());
    String direction = Objects.requireNonNull(record.getDirection());
    String cycleNumber = Objects.requireNonNull(record.getCycleNumber());
    String id = getCycleId(record);

    int failedAttempts = 0;
    boolean success = false;
    while (!success) {
      try (EntityManager em = entityManagerFactory.createEntityManager()) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        try {
          FloatEntity floatEntity = em.find(FloatEntity.class, getFloatId(record));
          CycleEntity entity = em.find(CycleEntity.class, id);
          if (entity != null) {
            return;
          } else {
            entity = new CycleEntity();
            entity.setId(id);
            entity.setDirection(direction.charAt(0));
            entity.setDataMode(dataMode.charAt(0));
            entity.setFloatId(floatEntity);
            entity.setCycleNumber(cycleNumber);
            em.merge(entity);
            tx.commit();
          }
        } catch (Exception e) {
          tx.rollback();
          throw e;
        }
        success = true;
      } catch (Exception e) {
        failedAttempts++;
        if (failedAttempts >= 2) {
          throw e;
        }
      }
    }
  }

  private void createOrUpdateProfile(MetadataRecord record) {
    String cycleId = getCycleId(record);
    String file = Objects.requireNonNull(record.getFile());
    try (EntityManager em = entityManagerFactory.createEntityManager()) {
      EntityTransaction tx = em.getTransaction();
      tx.begin();
      try {
        CycleEntity cycle = em.find(CycleEntity.class, cycleId);
        ProfileFileEntity entity = em.find(ProfileFileEntity.class, file);
        boolean add = (entity == null);

        if (add) {
          entity = new ProfileFileEntity();
          entity.setFile(file);
          entity.setCycle(cycle);
        }

        entity.setFileType(record.getFileType().toString());
        entity.setFileStatus(ACTIVE.toString());
        entity.setDate(record.getDate() == null ? null : record.getDate().atOffset(ZoneOffset.UTC).toZonedDateTime());
        entity.setLatitude(record.getLatitude());
        entity.setLatitudeMin(record.getLatitudeMin());
        entity.setLatitudeMax(record.getLatitudeMax());
        entity.setLongitude(record.getLongitude());
        entity.setLongitudeMin(record.getLongitudeMin());
        entity.setLongitudeMax(record.getLongitudeMax());
        entity.setOcean(record.getOcean() == null ? null : record.getOcean().getCode());
        entity.setProfilerType(record.getProfilerType());
        entity.setInstitution(record.getInstitution());
        entity.setDateUpdate(record.getDateUpdate() == null ? null : record.getDateUpdate().atOffset(ZoneOffset.UTC).toZonedDateTime());
        entity.setParameters(record.getParameters());
        entity.setParameterDataMode(record.getParameterDataMode());
        entity.setSyntheticMergeTime(null);
        entity.setMultiFloatMergeTime(null);
        entity.setLastUpdatedTime(record.getActionTimestamp().atOffset(ZoneOffset.UTC).toZonedDateTime());

        if (add) {
          em.persist(entity);
        }

        tx.commit();

      } catch (Exception e) {
        tx.rollback();
        throw e;
      }
    }
  }

  private void createOrUpdateProfileMergeFile(MetadataRecord record) {
    String floatId = getFloatId(record);
    String file = Objects.requireNonNull(record.getFile());
    try (EntityManager em = entityManagerFactory.createEntityManager()) {
      EntityTransaction tx = em.getTransaction();
      tx.begin();
      try {
        FloatEntity floatEntity = em.find(FloatEntity.class, floatId);
        ProfileMergeFileEntity entity = em.find(ProfileMergeFileEntity.class, file);
        boolean add = (entity == null);

        if (add) {
          entity = new ProfileMergeFileEntity();
          entity.setFile(file);
          entity.setFloatId(floatEntity);
        }

        entity.setFileStatus(ACTIVE.toString());
        entity.setDate(record.getDate() == null ? null : record.getDate().atOffset(ZoneOffset.UTC).toZonedDateTime());
        entity.setLatitude(record.getLatitude());
        entity.setLatitudeMin(record.getLatitudeMin());
        entity.setLatitudeMax(record.getLatitudeMax());
        entity.setLongitude(record.getLongitude());
        entity.setLongitudeMin(record.getLongitudeMin());
        entity.setLongitudeMax(record.getLongitudeMax());
        entity.setOcean(record.getOcean() == null ? null : record.getOcean().getCode());
        entity.setProfilerType(record.getProfilerType());
        entity.setInstitution(record.getInstitution());
        entity.setDateUpdate(record.getDateUpdate() == null ? null : record.getDateUpdate().atOffset(ZoneOffset.UTC).toZonedDateTime());
        entity.setParameters(record.getParameters());
        entity.setParameterDataMode(record.getParameterDataMode());
        entity.setLastUpdatedTime(record.getActionTimestamp().atOffset(ZoneOffset.UTC).toZonedDateTime());

        if (add) {
          em.persist(entity);
        }
        tx.commit();
      } catch (Exception e) {
        tx.rollback();
        throw e;
      }
    }
  }


  private void createOrUpdateMetadata(MetadataRecord record) {
    String floatId = getFloatId(record);
    String file = Objects.requireNonNull(record.getFile());
    try (EntityManager em = entityManagerFactory.createEntityManager()) {
      EntityTransaction tx = em.getTransaction();
      tx.begin();
      try {
        FloatEntity floatEntity = em.find(FloatEntity.class, floatId);
        MetadataFileEntity entity = em.find(MetadataFileEntity.class, file);
        boolean add = (entity == null);

        if (add) {
          entity = new MetadataFileEntity();
          entity.setFile(file);
          entity.setFloatId(floatEntity);
        }

        entity.setFileStatus(ACTIVE.toString());
        entity.setDate(record.getDate() == null ? null : record.getDate().atOffset(ZoneOffset.UTC).toZonedDateTime());
        entity.setLatitude(record.getLatitude());
        entity.setLatitudeMin(record.getLatitudeMin());
        entity.setLatitudeMax(record.getLatitudeMax());
        entity.setLongitude(record.getLongitude());
        entity.setLongitudeMin(record.getLongitudeMin());
        entity.setLongitudeMax(record.getLongitudeMax());
        entity.setOcean(record.getOcean() == null ? null : record.getOcean().getCode());
        entity.setProfilerType(record.getProfilerType());
        entity.setInstitution(record.getInstitution());
        entity.setDateUpdate(record.getDateUpdate() == null ? null : record.getDateUpdate().atOffset(ZoneOffset.UTC).toZonedDateTime());
        entity.setParameters(record.getParameters());
        entity.setParameterDataMode(record.getParameterDataMode());
        entity.setLastUpdatedTime(record.getActionTimestamp().atOffset(ZoneOffset.UTC).toZonedDateTime());

        if (add) {
          em.persist(entity);
        }
        tx.commit();
      } catch (Exception e) {
        tx.rollback();
        throw e;
      }
    }
  }

  void update(MetadataRecord record) {
    createDacIfMissing(record);
    createFloatIfMissing(record);

    switch (record.getFileType()) {
      case CORE_ARGO_PROFILE:
      case B_ARGO_PROFILE:
      case BGC_ARGO_SYNTH_PROFILE:
        createCycleIfMissing(record);
        createOrUpdateProfile(record);
        break;
      case METADATA:
        createOrUpdateMetadata(record);
        break;
      case PROFILE_MERGE:
        createOrUpdateProfileMergeFile(record);
        break;
      default:
        throw new UnsupportedOperationException("Unsupported file type: " + record.getFileType());
    }

  }
}
