package edu.colorado.cires.argonaut.metadata.jpa;

import static edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord.FileStatus.ACTIVE;

import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord;
import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord.FileStatus;
import edu.colorado.cires.argonaut.messaging.core.databind.ProfileMode;
import edu.colorado.cires.argonaut.metadata.jpa.entity.ArgoFloatCycleEntity;
import edu.colorado.cires.argonaut.metadata.jpa.entity.ArgoDacEntity;
import edu.colorado.cires.argonaut.metadata.jpa.entity.ArgoFloatEntity;
import edu.colorado.cires.argonaut.metadata.jpa.entity.ArgoFloatMetadataEntity;
import edu.colorado.cires.argonaut.metadata.jpa.entity.ArgoFloatProfileEntity;
import edu.colorado.cires.argonaut.metadata.jpa.entity.ArgonautProfileMergeFileEntity;
import edu.colorado.cires.argonaut.metadata.jpa.entity.ArgoFloatProfileParameterEntity;
import edu.colorado.cires.argonaut.metadata.jpa.entity.ArgoFloatTechnicalInfoEntity;
import edu.colorado.cires.argonaut.metadata.jpa.entity.ArgoFloatTrajectoryEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.LockModeType;
import jakarta.persistence.OptimisticLockException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

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
          ArgoDacEntity entity = em.find(ArgoDacEntity.class, dac);
          if (entity != null) {
            tx.rollback();
            return;
          } else {
            entity = new ArgoDacEntity();
            entity.setDacName(dac);
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
          ArgoDacEntity argoDacEntity = em.find(ArgoDacEntity.class, dac);
          ArgoFloatEntity entity = em.find(ArgoFloatEntity.class, id);
          if (entity != null) {
            tx.rollback();
            return;
          } else {
            entity = new ArgoFloatEntity();
            entity.setId(id);
            entity.setFloatId(floatId);
            entity.setArgoDac(argoDacEntity);
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
    String dataMode = Objects.requireNonNull(record.getProfileMode()).getFilePrefix();
    String direction = Objects.requireNonNull(record.getDirection());
    String cycleNumber = Objects.requireNonNull(record.getCycleNumber());
    return dac + "_" + dataMode + floatId + cycleNumber + direction;
  }

  private void createCycleIfMissing(MetadataRecord record) {
    ProfileMode dataMode = Objects.requireNonNull(record.getProfileMode());
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
          ArgoFloatEntity floatEntity = em.find(ArgoFloatEntity.class, getFloatId(record));
          ArgoFloatCycleEntity entity = em.find(ArgoFloatCycleEntity.class, id);
          if (entity != null) {
            tx.rollback();
            return;
          } else {
            entity = new ArgoFloatCycleEntity();
            entity.setId(id);
            entity.setDirection(direction.charAt(0));
            entity.setDataMode(dataMode.getCharacter().charAt(0));
            entity.setArgoFloat(floatEntity);
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
    while (true) {
      try (EntityManager em = entityManagerFactory.createEntityManager()) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        try {
          ArgoFloatCycleEntity cycle = em.find(ArgoFloatCycleEntity.class, cycleId);
          ArgoFloatProfileEntity entity = em.find(ArgoFloatProfileEntity.class, file, LockModeType.OPTIMISTIC);
          boolean add = (entity == null);

          if (add) {
            entity = new ArgoFloatProfileEntity();
            entity.setFilePath(file);
            entity.setArgoFloatCycle(cycle);
          }

          entity.setFileName(record.getFileName());
          entity.setFileType(record.getFileType().toString());
          entity.setFileStatus(ACTIVE.toString());
          entity.setDataModeFilePrefix(record.getProfileMode() == null ? null : record.getProfileMode().getFilePrefix());
          entity.setDataMode(record.getProfileMode() == null ? null : record.getProfileMode().getCharacter());
          entity.setLatestMergeFileName(null);
          if (record.getDate() == null) {
            entity.setProfileDate(null);
            entity.setProfileDateYear(null);
            entity.setProfileDateMonth(null);
            entity.setProfileDateDay(null);
          } else {
            ZonedDateTime date = record.getDate().atOffset(ZoneOffset.UTC).toZonedDateTime();
            entity.setProfileDate(date);
            entity.setProfileDateYear(date.getYear());
            entity.setProfileDateMonth(date.getMonthValue());
            entity.setProfileDateDay(date.getDayOfMonth());
          }
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
          entity.setParameterDataMode(record.getParameterDataMode());
          entity.setSyntheticMergeTime(null);
          entity.setMultiFloatMergeTime(null);
          entity.setGeoMergeTime(null);
          entity.setLastUpdatedTime(record.getActionTimestamp().atOffset(ZoneOffset.UTC).toZonedDateTime());
          entity.getRemovedTimes().clear();

          List<ArgoFloatProfileParameterEntity> parameters = entity.getParameters();
          parameters.clear();
          for (int i = 0; i < record.getParameters().size(); i++) {
            String parameterName =  record.getParameters().get(i);
            ArgoFloatProfileParameterEntity parameter = new ArgoFloatProfileParameterEntity();
            parameter.setId(UUID.randomUUID());
            parameter.setParameterName(parameterName);
            parameter.setArgoFloatProfile(entity);
            parameter.setParameterIndex(i);
            parameters.add(parameter);
          }

          if (add) {
            em.persist(entity);
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

  static void createOrUpdateProfileMergeFile(MetadataRecord record, EntityManager em, FileStatus fileStatus) {
    String floatId = getFloatId(record);
    String file = Objects.requireNonNull(record.getFile());

    ArgoFloatEntity floatEntity = em.find(ArgoFloatEntity.class, floatId);
    ArgonautProfileMergeFileEntity entity = em.find(ArgonautProfileMergeFileEntity.class, file);
    boolean add = (entity == null);

    if (add) {
      entity = new ArgonautProfileMergeFileEntity();
      entity.setFile(file);
      entity.setArgoFloat(floatEntity);
    }

    entity.setFileName(record.getFileName());
    entity.setFileStatus(fileStatus.toString());
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
    // TODO fix me
//    entity.setParameters(record.getParameters());
    entity.setParameterDataMode(record.getParameterDataMode());
    entity.setLastUpdatedTime(record.getActionTimestamp().atOffset(ZoneOffset.UTC).toZonedDateTime());

    if (add) {
      em.persist(entity);
    }

  }

  private void createOrUpdateProfileMergeFile(MetadataRecord record) {
    try (EntityManager em = entityManagerFactory.createEntityManager()) {
      EntityTransaction tx = em.getTransaction();
      tx.begin();
      try {
        createOrUpdateProfileMergeFile(record, em, FileStatus.ACTIVE);
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
        ArgoFloatEntity floatEntity = em.find(ArgoFloatEntity.class, floatId);
        ArgoFloatMetadataEntity entity = em.find(ArgoFloatMetadataEntity.class, file);
        boolean add = (entity == null);

        if (add) {
          entity = new ArgoFloatMetadataEntity();
          entity.setFile(file);
          entity.setArgoFloat(floatEntity);
        }

        entity.setFileName(record.getFileName());
        entity.setFileStatus(ACTIVE.toString());
        entity.setMetadataDate(record.getDate() == null ? null : record.getDate().atOffset(ZoneOffset.UTC).toZonedDateTime());
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
        // TODO fix me
//        entity.setParameters(record.getParameters());
        entity.setParameterDataMode(record.getParameterDataMode());
        entity.setLastUpdatedTime(record.getActionTimestamp().atOffset(ZoneOffset.UTC).toZonedDateTime());
        entity.getSyntheticMerges().clear();
        entity.getRemovedTimes().clear();

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
      case PROFILE_CORE:
      case PROFILE_BIOCHEMICAL:
      case SYNTHETIC_PROFILE_SINGLE_CYCLE:
        createCycleIfMissing(record);
        createOrUpdateProfile(record);
        break;
      case METADATA:
        createOrUpdateMetadata(record);
        break;
      case PROFILE_MULTI_CYCLE:
        createOrUpdateProfileMergeFile(record);
        break;
      case TRAJECTORY:
        createOrUpdateTrajectory(record);
        break;
      case TECHNICAL_DATA:
        createOrUpdateTechnical(record);
        break;
      default:
        throw new UnsupportedOperationException("Unsupported file type: " + record.getFileType());
    }

  }

  private void createOrUpdateTechnical(MetadataRecord record) {
    String floatId = getFloatId(record);
    String file = Objects.requireNonNull(record.getFile());
    try (EntityManager em = entityManagerFactory.createEntityManager()) {
      EntityTransaction tx = em.getTransaction();
      tx.begin();
      try {
        ArgoFloatEntity floatEntity = em.find(ArgoFloatEntity.class, floatId);
        ArgoFloatTechnicalInfoEntity entity = em.find(ArgoFloatTechnicalInfoEntity.class, file);
        boolean add = (entity == null);

        if (add) {
          entity = new ArgoFloatTechnicalInfoEntity();
          entity.setFilePath(file);
          entity.setArgoFloat(floatEntity);
        }

        entity.setInstitution(record.getInstitution());
        entity.setDateUpdate(record.getDateUpdate() == null ? null : record.getDateUpdate().atOffset(ZoneOffset.UTC).toZonedDateTime());

        entity.setLastUpdatedTime(record.getActionTimestamp().atOffset(ZoneOffset.UTC).toZonedDateTime());
        entity.setFileStatus(ACTIVE.toString());

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

  private void createOrUpdateTrajectory(MetadataRecord record) {
    String floatId = getFloatId(record);
    String file = Objects.requireNonNull(record.getFile());
    try (EntityManager em = entityManagerFactory.createEntityManager()) {
      EntityTransaction tx = em.getTransaction();
      tx.begin();
      try {
        ArgoFloatEntity floatEntity = em.find(ArgoFloatEntity.class, floatId);
        ArgoFloatTrajectoryEntity entity = em.find(ArgoFloatTrajectoryEntity.class, file);
        boolean add = (entity == null);

        if (add) {
          entity = new ArgoFloatTrajectoryEntity();
          entity.setFilePath(file);
          entity.setArgoFloat(floatEntity);
        }

        entity.setProfilerType(record.getProfilerType());
        entity.setInstitution(record.getInstitution());
        entity.setDateUpdate(record.getDateUpdate() == null ? null : record.getDateUpdate().atOffset(ZoneOffset.UTC).toZonedDateTime());

        entity.setLatitudeMin(record.getLatitudeMin());
        entity.setLatitudeMax(record.getLatitudeMax());

        entity.setLongitudeMin(record.getLongitudeMin());
        entity.setLongitudeMax(record.getLongitudeMax());

        entity.setLastUpdatedTime(record.getActionTimestamp().atOffset(ZoneOffset.UTC).toZonedDateTime());
        entity.setFileStatus(ACTIVE.toString());

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
}
