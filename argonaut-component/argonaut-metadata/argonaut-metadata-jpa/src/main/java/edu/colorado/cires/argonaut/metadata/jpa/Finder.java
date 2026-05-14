package edu.colorado.cires.argonaut.metadata.jpa;

import edu.colorado.cires.argonaut.messaging.core.databind.ArgoOcean;
import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord;
import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord.FileStatus;
import edu.colorado.cires.argonaut.messaging.core.databind.NcSubmissionMessage.FileType;
import edu.colorado.cires.argonaut.messaging.core.databind.ProfileOperation;
import edu.colorado.cires.argonaut.metadata.core.DefaultIndexPageRequest;
import edu.colorado.cires.argonaut.metadata.core.DefaultProfilePage;
import edu.colorado.cires.argonaut.metadata.core.IndexPageRequest;
import edu.colorado.cires.argonaut.metadata.core.ProfilePage;
import edu.colorado.cires.argonaut.metadata.jpa.entity.CycleEntity;
import edu.colorado.cires.argonaut.metadata.jpa.entity.MetadataFileEntity;
import edu.colorado.cires.argonaut.metadata.jpa.entity.ProfileFileEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

class Finder {

  private final EntityManagerFactory entityManagerFactory;

  Finder(EntityManagerFactory entityManagerFactory) {
    this.entityManagerFactory = entityManagerFactory;
  }

  Optional<MetadataRecord> findByFile(String file, boolean includeRemoved) {
    if (file.endsWith("_meta.nc")) {
      try (EntityManager em = entityManagerFactory.createEntityManager()) {
        MetadataFileEntity result;
        if (includeRemoved) {
          result = em.find(MetadataFileEntity.class, file);
        } else {
          result = em.createQuery("SELECT m FROM MetadataFileEntity m WHERE m.file = :file AND m.fileStatus = 'ACTIVE'", MetadataFileEntity.class)
              .setParameter("file", file)
              .getSingleResultOrNull();
        }

        if (result == null) {
          return Optional.empty();
        }

        return Optional.of(MetadataRecord.builder()
            .withFileType(FileType.METADATA)
            .withDac(result.getFloatId().getDac().getDac())
            .withFile(result.getFile())
            .withFloatId(result.getFloatId().getFloatId())
            .withFileStatus(FileStatus.valueOf(result.getFileStatus()))
            .withDate(result.getDate() == null ? null : result.getDate().toInstant())
            .withLatitude(result.getLatitude())
            .withLatitudeMin(result.getLatitudeMin())
            .withLatitudeMax(result.getLatitudeMax())
            .withLongitude(result.getLongitude())
            .withLongitudeMin(result.getLongitudeMin())
            .withLongitudeMax(result.getLongitudeMax())
            .withOcean(result.getOcean() == null ? null : ArgoOcean.fromCode(result.getOcean()))
            .withProfilerType(result.getProfilerType())
            .withInstitution(result.getInstitution())
            .withDateUpdate(result.getDateUpdate() == null ? null : result.getDateUpdate().toInstant())
            .withParameters(result.getParameters())
            .withParameterDataMode(result.getParameterDataMode())
            .withActionTimestamp(result.getLastUpdatedTime().toInstant())
            .build());
      }
      //TODO traj, etc
    } else if (file.contains("profile")) {
      try (EntityManager em = entityManagerFactory.createEntityManager()) {
        ProfileFileEntity result;
        if (includeRemoved) {
          result = em.find(ProfileFileEntity.class, file);
        } else {
          result = em.createQuery("SELECT p FROM ProfileFileEntity p WHERE p.file = :file AND p.fileStatus = 'ACTIVE'", ProfileFileEntity.class)
              .setParameter("file", file)
              .getSingleResultOrNull();
        }

        if (result == null) {
          return Optional.empty();
        }
        return Optional.of(MetadataRecord.builder()
            .withDirection(result.getCycle().getDirection().toString())
            .withCycleNumber(result.getCycle().getCycleNumber())
            .withParameterDataMode(result.getCycle().getCycleNumber())
            .withFileType(FileType.valueOf(result.getFileType()))
            .withDac(result.getCycle().getFloatId().getDac().getDac())
            .withFile(result.getFile())
            .withFloatId(result.getCycle().getFloatId().getFloatId())
            .withFileStatus(FileStatus.valueOf(result.getFileStatus()))
            .withDate(result.getDate() == null ? null : result.getDate().toInstant())
            .withLatitude(result.getLatitude())
            .withLatitudeMin(result.getLatitudeMin())
            .withLatitudeMax(result.getLatitudeMax())
            .withLongitude(result.getLongitude())
            .withLongitudeMin(result.getLongitudeMin())
            .withLongitudeMax(result.getLongitudeMax())
            .withOcean(result.getOcean() == null ? null : ArgoOcean.fromCode(result.getOcean()))
            .withProfilerType(result.getProfilerType())
            .withInstitution(result.getInstitution())
            .withDateUpdate(result.getDateUpdate() == null ? null : result.getDateUpdate().toInstant())
            .withParameters(result.getParameters())
            .withParameterDataMode(result.getParameterDataMode())
            .withActionTimestamp(result.getLastUpdatedTime().toInstant())
            .build());
      }
    }
    return Optional.empty();
  }

  ProfilePage findUpdatedOrMissingSyntheticProfilesPage(IndexPageRequest pageRequest) {
    try (EntityManager em = entityManagerFactory.createEntityManager()) {
      long count = em.createQuery(
          """
                 SELECT COUNT(DISTINCT profile.cycle.id) FROM ProfileFileEntity profile
                 WHERE profile.cycle.floatId.metadata.fileStatus = 'ACTIVE' AND
                       profile.fileStatus = 'ACTIVE' AND
                       (profile.fileType = 'CORE_ARGO_PROFILE' OR profile.fileType = 'B_ARGO_PROFILE') AND
                       profile.syntheticMergeTime IS NULL
              """, Long.class).getSingleResult();
      List<String> cycleIds = em.createQuery(
              """
                     SELECT DISTINCT profile.cycle.id cid FROM ProfileFileEntity profile
                         WHERE profile.cycle.floatId.metadata.fileStatus = 'ACTIVE' AND 
                             profile.fileStatus = 'ACTIVE' AND 
                             (profile.fileType = 'CORE_ARGO_PROFILE' OR profile.fileType = 'B_ARGO_PROFILE') AND
                             profile.syntheticMergeTime IS NULL
                     order by cid
                  """, String.class)
          .setMaxResults(pageRequest.getPageSize())
          .setFirstResult((pageRequest.getPageNumber() - 1) * pageRequest.getPageSize())
          .getResultList();

      List<CycleEntity> pageResults = new ArrayList<>(cycleIds.size());
      for (String cycleId : cycleIds) {
        pageResults.add(em.find(CycleEntity.class, cycleId));
      }

      return DefaultProfilePage.builder()
          .withTotalRecords(count)
          .withIndexPageRequest(DefaultIndexPageRequest.builder(pageRequest).build())
          .withPage(pageResults.stream().map(cycle -> {
                List<String> files = new ArrayList<>(3);
                files.add(cycle.getFloatId().getMetadata().getFile());
                files.addAll(cycle.getProfiles().stream().map(ProfileFileEntity::getFile).sorted().toList());
                return ProfileOperation.builder()
                    .withDac(cycle.getFloatId().getDac().getDac())
                    .withFloatId(cycle.getFloatId().getFloatId())
                    .withFiles(files)
                    .build();
              }
          ).toList()).build();
    }
  }
}
