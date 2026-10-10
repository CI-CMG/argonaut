package edu.colorado.cires.argonaut.metadata.jpa;

import edu.colorado.cires.argonaut.messaging.core.databind.ArgoFileType;
import edu.colorado.cires.argonaut.messaging.core.databind.ArgoOcean;
import edu.colorado.cires.argonaut.messaging.core.databind.GeoMergeInfo;
import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord;
import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord.FileStatus;
import edu.colorado.cires.argonaut.messaging.core.databind.ProfileMode;
import edu.colorado.cires.argonaut.messaging.core.databind.ProfileOperation;
import edu.colorado.cires.argonaut.metadata.core.DefaultGeoMergePage;
import edu.colorado.cires.argonaut.metadata.core.DefaultIndexPageRequest;
import edu.colorado.cires.argonaut.metadata.core.DefaultMetadataRecordPage;
import edu.colorado.cires.argonaut.metadata.core.DefaultProfilePage;
import edu.colorado.cires.argonaut.metadata.core.DefaultRemovedFilePage;
import edu.colorado.cires.argonaut.metadata.core.DefaultRemovedFileSearch;
import edu.colorado.cires.argonaut.metadata.core.GeoMergePage;
import edu.colorado.cires.argonaut.metadata.core.IndexPageRequest;
import edu.colorado.cires.argonaut.metadata.core.MetadataRecordPage;
import edu.colorado.cires.argonaut.metadata.core.ProfilePage;
import edu.colorado.cires.argonaut.metadata.core.RecentProfileSearch;
import edu.colorado.cires.argonaut.metadata.core.RemovedFilePage;
import edu.colorado.cires.argonaut.metadata.core.RemovedFileSearch;
import edu.colorado.cires.argonaut.metadata.jpa.entity.ArgoFloatCycleEntity;
import edu.colorado.cires.argonaut.metadata.jpa.entity.ArgonautFileRemovedTimeEntity;
import edu.colorado.cires.argonaut.metadata.jpa.entity.ArgoFloatEntity;
import edu.colorado.cires.argonaut.metadata.jpa.entity.ArgoFloatMetadataEntity;
import edu.colorado.cires.argonaut.metadata.jpa.entity.ArgoFloatProfileEntity;
import edu.colorado.cires.argonaut.metadata.jpa.entity.ArgoFloatProfileParameterEntity;
import edu.colorado.cires.argonaut.metadata.jpa.entity.ArgoFloatTechnicalInfoEntity;
import edu.colorado.cires.argonaut.metadata.jpa.entity.ArgoFloatTrajectoryEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

class Finder {

  private static final DateTimeFormatter LATEST_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

  private static final List<ArgoFileType> DEFAULT_REMOVABLE_PROFILE_TYPES = Arrays.asList(
      ArgoFileType.METADATA,
      ArgoFileType.PROFILE_CORE,
      ArgoFileType.PROFILE_BIOCHEMICAL,
      ArgoFileType.TRAJECTORY,
      ArgoFileType.TECHNICAL_DATA
  );

  private final EntityManagerFactory entityManagerFactory;

  Finder(EntityManagerFactory entityManagerFactory) {
    this.entityManagerFactory = entityManagerFactory;
  }

  Optional<MetadataRecord> findByFile(String file, boolean includeRemoved) {
    if (file.endsWith("_meta.nc")) {
      try (EntityManager em = entityManagerFactory.createEntityManager()) {
        ArgoFloatMetadataEntity result;
        if (includeRemoved) {
          result = em.find(ArgoFloatMetadataEntity.class, file);
        } else {
          result = em.createQuery("SELECT m FROM ArgoFloatMetadataEntity m WHERE m.file = :file AND m.fileStatus = 'ACTIVE'", ArgoFloatMetadataEntity.class)
              .setParameter("file", file)
              .getSingleResultOrNull();
        }

        if (result == null) {
          return Optional.empty();
        }

        return Optional.of(MetadataRecord.builder()
            .withFileType(ArgoFileType.METADATA)
            .withDac(result.getArgoFloat().getArgoDac().getDacName())
            .withFile(result.getFile())
            .withFileName(result.getFileName())
            .withFloatId(result.getArgoFloat().getFloatId())
            .withFileStatus(FileStatus.valueOf(result.getFileStatus()))
            .withDate(result.getMetadataDate() == null ? null : result.getMetadataDate().toInstant())
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
            // TODO fix me
//            .withParameters(result.getParameters())
            .withParameterDataMode(result.getParameterDataMode())
            .withActionTimestamp(result.getLastUpdatedTime().toInstant())
            .build());
      }
      //TODO traj, etc
    } else if (file.contains("profile")) {
      try (EntityManager em = entityManagerFactory.createEntityManager()) {
        ArgoFloatProfileEntity result;
        if (includeRemoved) {
          result = em.find(ArgoFloatProfileEntity.class, file);
        } else {
          result = em.createQuery("SELECT p FROM ArgoFloatProfileEntity p WHERE p.filePath = :file AND p.fileStatus = 'ACTIVE'", ArgoFloatProfileEntity.class)
              .setParameter("file", file)
              .getSingleResultOrNull();
        }

        if (result == null) {
          return Optional.empty();
        }
        return Optional.of(MetadataRecord.builder()
            .withDirection(result.getArgoFloatCycle().getDirection().toString())
            .withCycleNumber(result.getArgoFloatCycle().getCycleNumber())
            .withFileType(ArgoFileType.valueOf(result.getFileType()))
            .withDac(result.getArgoFloatCycle().getArgoFloat().getArgoDac().getDacName())
            .withFile(result.getFilePath())
            .withFileName(result.getFileName())
            .withFloatId(result.getArgoFloatCycle().getArgoFloat().getFloatId())
            .withFileStatus(FileStatus.valueOf(result.getFileStatus()))
            .withDate(result.getProfileDate() == null ? null : result.getProfileDate().toInstant())
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
            .withParameters(result.getParameters().stream().map(ArgoFloatProfileParameterEntity::getParameterName).toList())
            .withParameterDataMode(result.getParameterDataMode())
            .withProfileMode(ProfileMode.fromCharacter(result.getDataMode()))
            .withActionTimestamp(result.getLastUpdatedTime().toInstant())
            .build());
      }
    } else if (file.endsWith("traj.nc")) {
      try (EntityManager em = entityManagerFactory.createEntityManager()) {
        ArgoFloatTrajectoryEntity result;
        if (includeRemoved) {
          result = em.find(ArgoFloatTrajectoryEntity.class, file);
        } else {
          result = em.createQuery("select t from ArgoFloatTrajectoryEntity t where t.filePath = :file and t.fileStatus = 'ACTIVE'", ArgoFloatTrajectoryEntity.class)
            .setParameter("file", file)
            .getSingleResultOrNull();

          if (result == null) {
            return Optional.empty();
          }
        }
        return Optional.of(fromTrajectoryEntity(result));
      }
    } else if (file.endsWith("tech.nc")) {
      try (EntityManager em = entityManagerFactory.createEntityManager()) {
        ArgoFloatTechnicalInfoEntity result;
        if (includeRemoved) {
          result = em.find(ArgoFloatTechnicalInfoEntity.class, file);
        } else {
          result = em.createQuery("select t from ArgoFloatTechnicalInfoEntity t where t.filePath = :file and t.fileStatus = 'ACTIVE'", ArgoFloatTechnicalInfoEntity.class)
            .setParameter("file", file)
            .getSingleResultOrNull();

          if (result == null) {
            return Optional.empty();
          }
        }

        return Optional.of(fromTechnicalEntity(result));
      }
    }
    return Optional.empty();
  }

  private static List<MetadataRecord> getGeoPaths(EntityManager em, int year, int month, int day, String ocean) {
    List<ArgoFloatProfileEntity> entities = em.createQuery(
            """
                SELECT p FROM ArgoFloatProfileEntity p
                  WHERE p.profileDateYear = :year
                    AND p.profileDateMonth = :month
                    AND p.profileDateDay = :day
                    AND ocean = :ocean
                    AND p.fileType = 'PROFILE_CORE'
                    AND (p.fileStatus = 'ACTIVE' OR p.geoMergeTime IS NOT NULL)
            """)
        .setParameter("year", year)
        .setParameter("month", month)
        .setParameter("day", day)
        .setParameter("ocean", ocean)
        .getResultList();

    return entities.stream().map(entity -> MetadataRecord.builder()
        .withFileName(entity.getFileName())
        .withDac(entity.getArgoFloatCycle().getArgoFloat().getArgoDac().getDacName())
        .withFile(entity.getFilePath())
        .withFloatId(entity.getArgoFloatCycle().getArgoFloat().getFloatId())
        .withFileStatus(FileStatus.valueOf(entity.getFileStatus()))
        .withDate(entity.getProfileDate() == null ? null : entity.getProfileDate().toInstant())
        .build()).toList();

  }

  GeoMergePage findUpdatedOrMissingGeoMergeFilesPage(IndexPageRequest pageRequest) {
    try (EntityManager em = entityManagerFactory.createEntityManager()) {
      long count = em.createQuery(
          """
                 SELECT COUNT(DISTINCT CONCAT(profile.profileDateYear, '_', profile.profileDateMonth, '_', profile.profileDateDay, '_', profile.ocean)) FROM ArgoFloatProfileEntity profile
                 WHERE profile.fileType = 'PROFILE_CORE' AND 
                       (( profile.fileStatus = 'ACTIVE' AND profile.geoMergeTime IS NULL ) 
                       OR  
                       ( profile.fileStatus = 'REMOVED' AND profile.geoMergeTime IS NOT NULL ))
              """, Long.class).getSingleResult();
      List<String> geoMergeIds = em.createQuery(
              """
                     SELECT DISTINCT CONCAT(profile.profileDateYear, '_', profile.profileDateMonth, '_', profile.profileDateDay, '_', profile.ocean) id FROM ArgoFloatProfileEntity profile
                       WHERE profile.fileType = 'PROFILE_CORE' AND 
                       (( profile.fileStatus = 'ACTIVE' AND profile.geoMergeTime IS NULL ) 
                       OR  
                       ( profile.fileStatus = 'REMOVED' AND profile.geoMergeTime IS NOT NULL ))
                     order by id
                  """, String.class)
          .setMaxResults(pageRequest.getPageSize())
          .setFirstResult((pageRequest.getPageNumber() - 1) * pageRequest.getPageSize())
          .getResultList();

      return DefaultGeoMergePage.builder()
          .withTotalRecords(count)
          .withIndexPageRequest(DefaultIndexPageRequest.builder(pageRequest).build())
          .withPage(geoMergeIds.stream().map(concatId -> {
            String[] parts = concatId.split("_");
            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int day = Integer.parseInt(parts[2]);
            String ocean = parts[3];
            return GeoMergeInfo.builder()
                .withYear(year)
                .withMonth(month)
                .withDay(day)
                .withOcean(ArgoOcean.fromCode(ocean))
                .withFiles(getGeoPaths(em, year, month, day, ocean))
                .build();
          }).toList())
          .build();
    }
  }

  ProfilePage findUpdatedOrMissingMergeFilesPage(IndexPageRequest pageRequest) {
    try (EntityManager em = entityManagerFactory.createEntityManager()) {
      long count = em.createQuery(
          """
                 SELECT COUNT(DISTINCT profile.argoFloatCycle.argoFloat.id) FROM ArgoFloatProfileEntity profile
                 WHERE profile.fileType = 'PROFILE_CORE' AND 
                       (( profile.fileStatus = 'ACTIVE' AND profile.multiFloatMergeTime IS NULL ) 
                       OR  
                       ( profile.fileStatus = 'REMOVED' AND profile.multiFloatMergeTime IS NOT NULL ))
              """, Long.class).getSingleResult();
      List<String> floatIds = em.createQuery(
              """
                     SELECT DISTINCT profile.argoFloatCycle.argoFloat.id fid FROM ArgoFloatProfileEntity profile
                         WHERE profile.fileType = 'PROFILE_CORE' AND 
                         (( profile.fileStatus = 'ACTIVE' AND profile.multiFloatMergeTime IS NULL ) 
                         OR  
                         ( profile.fileStatus = 'REMOVED' AND profile.multiFloatMergeTime IS NOT NULL ))
                     order by fid
                  """, String.class)
          .setMaxResults(pageRequest.getPageSize())
          .setFirstResult((pageRequest.getPageNumber() - 1) * pageRequest.getPageSize())
          .getResultList();

      List<ArgoFloatEntity> pageResults = new ArrayList<>(floatIds.size());
      for (String floatId : floatIds) {
        pageResults.add(em.find(ArgoFloatEntity.class, floatId));
      }

      return DefaultProfilePage.builder()
          .withTotalRecords(count)
          .withIndexPageRequest(DefaultIndexPageRequest.builder(pageRequest).build())
          .withPage(pageResults.stream().map(floatEntity -> ProfileOperation.builder()
              .withDac(floatEntity.getArgoDac().getDacName())
              .withFloatId(floatEntity.getFloatId())
              .withFiles(getMergeFileInfo(floatEntity))
              .build()
          ).toList()).build();
    }
  }

  private static List<MetadataRecord> getMergeFileInfo(ArgoFloatEntity floatEntity) {
    List<MetadataRecord> result = new LinkedList<>();
    for (ArgoFloatCycleEntity cycle : floatEntity.getCycles()) {
      for (ArgoFloatProfileEntity profile : cycle.getProfiles()) {
        if (ArgoFileType.PROFILE_CORE.toString().equals(profile.getFileType())) {
          if (FileStatus.ACTIVE.toString().equals(profile.getFileStatus()) || profile.getMultiFloatMergeTime() != null) {
            result.add(MetadataRecord.builder()
                .withFileName(profile.getFileName())
                .withFile(profile.getFilePath())
                .withFileStatus(FileStatus.valueOf(profile.getFileStatus()))
                .withDate(profile.getProfileDate() == null ? null : profile.getProfileDate().toInstant())
                .build());
          }
        }
      }
    }
    return result;
  }

  ProfilePage findUpdatedOrMissingSyntheticProfilesPage(IndexPageRequest pageRequest) {
    try (EntityManager em = entityManagerFactory.createEntityManager()) {
      long count = em.createQuery(
          """
                 SELECT COUNT(DISTINCT profile.argoFloatCycle.id) FROM ArgoFloatProfileEntity profile
                           WHERE
                           (
                             profile.fileStatus = 'ACTIVE'
                             AND profile.argoFloatCycle.argoFloat.metadata.fileStatus = 'ACTIVE'
                             AND (
                                    (
                                      profile.fileType = 'PROFILE_BIOCHEMICAL' 
                                      AND EXISTS (SELECT core.filePath FROM ArgoFloatProfileEntity core WHERE core.fileType = 'PROFILE_CORE' AND core.fileStatus = 'ACTIVE' AND core.argoFloatCycle = profile.argoFloatCycle)
                                    ) OR (
                                      profile.fileType = 'PROFILE_CORE'
                                      AND EXISTS (SELECT bio.filePath FROM ArgoFloatProfileEntity bio WHERE bio.fileType = 'PROFILE_BIOCHEMICAL' AND bio.fileStatus = 'ACTIVE' AND bio.argoFloatCycle = profile.argoFloatCycle) 
                                    )
                                )
                             AND (
                               profile.syntheticMergeTime IS NULL 
                               OR NOT EXISTS (SELECT mds.syntheticMergeTime FROM ArgonautSyntheticMergeMetadataEntity mds WHERE mds.argoFloatProfile = profile)
                             )
                           ) OR (
                             (profile.fileType = 'PROFILE_BIOCHEMICAL' OR profile.fileType = 'PROFILE_CORE' )
                             AND profile.fileStatus = 'REMOVED'
                             AND profile.syntheticMergeTime IS NOT NULL
                           ) OR (
                             (profile.fileType = 'PROFILE_BIOCHEMICAL' OR profile.fileType = 'PROFILE_CORE' )
                             AND profile.fileStatus = 'ACTIVE'
                             AND profile.syntheticMergeTime IS NOT NULL
                             AND NOT profile.argoFloatCycle.argoFloat.metadata.fileStatus = 'ACTIVE'
                           )
              """, Long.class).getSingleResult();


      // case 1: bio, core, and md are active, but missing one or more merge times -> trigger merge
      // case 2: bio or core have been removed, but have merge times -> trigger merge removal
      // case 3: bio or core are active and have merge time, but md is missing or removed -> trigger merge removal

      List<String> cycleIds = em.createQuery(
              """
                     SELECT DISTINCT profile.argoFloatCycle.id cid FROM ArgoFloatProfileEntity profile
                         WHERE
                           (
                             profile.fileStatus = 'ACTIVE'
                             AND profile.argoFloatCycle.argoFloat.metadata.fileStatus = 'ACTIVE'
                             AND (
                                    (
                                      profile.fileType = 'PROFILE_BIOCHEMICAL' 
                                      AND EXISTS (SELECT core.filePath FROM ArgoFloatProfileEntity core WHERE core.fileType = 'PROFILE_CORE' AND core.fileStatus = 'ACTIVE' AND core.argoFloatCycle = profile.argoFloatCycle)
                                    ) OR (
                                      profile.fileType = 'PROFILE_CORE'
                                      AND EXISTS (SELECT bio.filePath FROM ArgoFloatProfileEntity bio WHERE bio.fileType = 'PROFILE_BIOCHEMICAL' AND bio.fileStatus = 'ACTIVE' AND bio.argoFloatCycle = profile.argoFloatCycle) 
                                    )
                                )
                             AND (
                               profile.syntheticMergeTime IS NULL 
                               OR NOT EXISTS (SELECT mds.syntheticMergeTime FROM ArgonautSyntheticMergeMetadataEntity mds WHERE mds.argoFloatProfile = profile)
                             )
                           ) OR (
                             (profile.fileType = 'PROFILE_BIOCHEMICAL' OR profile.fileType = 'PROFILE_CORE' )
                             AND profile.fileStatus = 'REMOVED'
                             AND profile.syntheticMergeTime IS NOT NULL
                           ) OR (
                             (profile.fileType = 'PROFILE_BIOCHEMICAL' OR profile.fileType = 'PROFILE_CORE' )
                             AND profile.fileStatus = 'ACTIVE'
                             AND profile.syntheticMergeTime IS NOT NULL
                             AND NOT profile.argoFloatCycle.argoFloat.metadata.fileStatus = 'ACTIVE'
                           )
                     order by cid
                  """, String.class)
          .setMaxResults(pageRequest.getPageSize())
          .setFirstResult((pageRequest.getPageNumber() - 1) * pageRequest.getPageSize())
          .getResultList();

      List<ArgoFloatCycleEntity> pageResults = new ArrayList<>(cycleIds.size());
      for (String cycleId : cycleIds) {
        pageResults.add(em.find(ArgoFloatCycleEntity.class, cycleId));
      }

      return DefaultProfilePage.builder()
          .withTotalRecords(count)
          .withIndexPageRequest(DefaultIndexPageRequest.builder(pageRequest).build())
          .withPage(pageResults.stream().map(cycle -> ProfileOperation.builder()
              .withDac(cycle.getArgoFloat().getArgoDac().getDacName())
              .withFloatId(cycle.getArgoFloat().getFloatId())
              .withFiles(getSyntheticMergeFiles(cycle))
              .build()
          ).toList()).build();
    }
  }

  private static List<MetadataRecord> getSyntheticMergeFiles(ArgoFloatCycleEntity cycle) {
    List<MetadataRecord> result = new LinkedList<>();
    ArgoFloatMetadataEntity metadataFileEntity = cycle.getArgoFloat().getMetadata();
    if (metadataFileEntity != null) {
      result.add(MetadataRecord.builder()
          .withFileType(ArgoFileType.METADATA)
          .withFileName(metadataFileEntity.getFileName())
          .withFile(metadataFileEntity.getFile())
          .withFileStatus(FileStatus.valueOf(metadataFileEntity.getFileStatus()))
          .withDate(metadataFileEntity.getMetadataDate() == null ? null : metadataFileEntity.getMetadataDate().toInstant())
          .build());
    }
    List<MetadataRecord> profileList = new LinkedList<>();
    for (ArgoFloatProfileEntity profile : cycle.getProfiles()) {
      if (ArgoFileType.PROFILE_CORE.toString().equals(profile.getFileType()) || ArgoFileType.PROFILE_BIOCHEMICAL.toString().equals(profile.getFileType())) {
        profileList.add(MetadataRecord.builder()
            .withFileType(ArgoFileType.valueOf(profile.getFileType()))
            .withFileName(profile.getFileName())
            .withFile(profile.getFilePath())
            .withFileStatus(FileStatus.valueOf(profile.getFileStatus()))
            .withDate(profile.getProfileDate() == null ? null : profile.getProfileDate().toInstant())
            .build());
      }
    }
    Collections.sort(profileList, Comparator.comparing(MetadataRecord::getFile));
    result.addAll(profileList);
    return result;
  }

  RemovedFilePage findRemovedFilesPage(RemovedFileSearch pageRequest) {
    Instant olderThan = pageRequest.getOlderThan() == null ? Instant.now() : pageRequest.getOlderThan();
    ZonedDateTime queryOlderThan = olderThan.atOffset(ZoneOffset.UTC).toZonedDateTime();
    List<String> fileTypes = (
        pageRequest.getFileTypes() == null || pageRequest.getFileTypes().isEmpty()
        ? DEFAULT_REMOVABLE_PROFILE_TYPES
        : pageRequest.getFileTypes().stream().filter(DEFAULT_REMOVABLE_PROFILE_TYPES::contains).toList()
        ).stream().map(ArgoFileType::toString).toList();

    try (EntityManager em = entityManagerFactory.createEntityManager()) {
      long count = em.createQuery(
          """
              SELECT COUNT(frt.id) FROM ArgonautFileRemovedTimeEntity frt 
              WHERE frt.removedTime < :olderThan AND frt.fileType IN (:forFileTypes)
              """, Long.class)
          .setParameter("olderThan", queryOlderThan)
          .setParameter("forFileTypes", fileTypes)
          .getSingleResult();

      List<ArgonautFileRemovedTimeEntity> pageResults = em.createQuery(
              """
                     SELECT frt FROM ArgonautFileRemovedTimeEntity frt 
                     WHERE frt.removedTime < :olderThan AND frt.fileType IN (:forFileTypes)
                     ORDER BY frt.argoFloatMetadata.file, frt.argoFloatProfile.filePath
                  """, ArgonautFileRemovedTimeEntity.class)
          .setParameter("olderThan", queryOlderThan)
          .setParameter("forFileTypes", fileTypes)
          .setMaxResults(pageRequest.getPageSize())
          .setFirstResult((pageRequest.getPageNumber() - 1) * pageRequest.getPageSize())
          .getResultList();

      return DefaultRemovedFilePage.builder()
          .withTotalRecords(count)
          .withIndexPageRequest(DefaultRemovedFileSearch.builder(pageRequest).build())
          .withPage(pageResults.stream().map(frt -> {
            String dac = null;
            String floatId = null;
            MetadataRecord fileInfo = null;
            if (frt.getArgoFloatMetadata() != null) {
              dac = frt.getArgoFloatMetadata().getArgoFloat().getArgoDac().getDacName();
              floatId = frt.getArgoFloatMetadata().getArgoFloat().getFloatId();
              fileInfo = MetadataRecord.builder()
                  .withFileName(frt.getArgoFloatMetadata().getFileName())
                  .withFile(frt.getArgoFloatMetadata().getFile())
                  .withFileStatus(FileStatus.valueOf(frt.getArgoFloatMetadata().getFileStatus()))
                  .withDate(frt.getArgoFloatMetadata().getMetadataDate() == null ? null : frt.getArgoFloatMetadata().getMetadataDate().toInstant())
                  .withFileType(ArgoFileType.METADATA)
                  .withActionTimestamp(frt.getRemovedTime().toInstant())
                  .build();
            } else if(frt.getArgoFloatProfile() != null) {
              dac = frt.getArgoFloatProfile().getArgoFloatCycle().getArgoFloat().getArgoDac().getDacName();
              floatId = frt.getArgoFloatProfile().getArgoFloatCycle().getArgoFloat().getFloatId();
              fileInfo = MetadataRecord.builder()
                  .withFileName(frt.getArgoFloatProfile().getFileName())
                  .withFile(frt.getArgoFloatProfile().getFilePath())
                  .withFileStatus(FileStatus.valueOf(frt.getArgoFloatProfile().getFileStatus()))
                  .withDate(frt.getArgoFloatProfile().getProfileDate() == null ? null : frt.getArgoFloatProfile().getProfileDate().toInstant())
                  .withFileType(ArgoFileType.valueOf(frt.getArgoFloatProfile().getFileType()))
                  .withActionTimestamp(frt.getRemovedTime().toInstant())
                  .build();
            }
            return ProfileOperation.builder()
                .withDac(dac)
                .withFloatId(floatId)
                .withFiles(Collections.singletonList(fileInfo))
                .build();
          }).toList()).build();
    }
  }

  ProfileOperation findUpdatedOrMissingLatestMergeFiles(RecentProfileSearch pageRequest) {

    String fileName = pageRequest.getProfileMode().getFilePrefix() + LATEST_FORMATTER.format(pageRequest.getLastUpdatedDateGe().atZone(ZoneId.of("UTC")).toLocalDate());

    try (EntityManager em = entityManagerFactory.createEntityManager()) {


       long count = em.createQuery(
              """
                     SELECT COUNT(profile.filePath) FROM ArgoFloatProfileEntity profile 
                     WHERE profile.lastUpdatedTime >= :updatedGe AND profile.lastUpdatedTime < :updatedLt AND profile.dataModeFilePrefix = :dataMode
                     AND profile.fileType = 'PROFILE_CORE'
                     AND ((profile.fileStatus = 'ACTIVE' AND profile.latestMergeFileName IS NULL) OR (profile.fileStatus = 'REMOVED' AND profile.latestMergeFileName IS NOT NULL))
                  """, Long.class)
          .setParameter("updatedGe", pageRequest.getLastUpdatedDateGe().atZone(ZoneId.of("UTC")))
          .setParameter("updatedLt", pageRequest.getLastUpdatedDateLt().atZone(ZoneId.of("UTC")))
          .setParameter("dataMode", pageRequest.getProfileMode().getFilePrefix())
          .setMaxResults(pageRequest.getLimit())
          .getSingleResult();

       List<ArgoFloatProfileEntity> results;
       if (count > 0L) {
         results = em.createQuery(
                 """
                        SELECT profile FROM ArgoFloatProfileEntity profile 
                        WHERE profile.lastUpdatedTime >= :updatedGe AND profile.lastUpdatedTime < :updatedLt AND profile.dataModeFilePrefix = :dataMode
                        AND profile.fileType = 'PROFILE_CORE'
                        AND ((profile.fileStatus = 'ACTIVE') OR (profile.fileStatus = 'REMOVED' AND profile.latestMergeFileName IS NOT NULL))
                        order by profile.profileDate
                     """, ArgoFloatProfileEntity.class)
             .setParameter("updatedGe", pageRequest.getLastUpdatedDateGe().atZone(ZoneId.of("UTC")))
             .setParameter("updatedLt", pageRequest.getLastUpdatedDateLt().atZone(ZoneId.of("UTC")))
             .setParameter("dataMode", pageRequest.getProfileMode().getFilePrefix())
             .setMaxResults(pageRequest.getLimit())
             .getResultList();
       } else {
         results = Collections.emptyList();
       }


      List<MetadataRecord> files = new ArrayList<>(results.size());
      for (ArgoFloatProfileEntity profile : results) {
        files.add(MetadataRecord.builder()
            .withDac(profile.getArgoFloatCycle().getArgoFloat().getArgoDac().getDacName())
            .withFloatId(profile.getArgoFloatCycle().getArgoFloat().getFloatId())
            .withFile(profile.getFilePath())
            .withFileName(profile.getFileName())
            .withFileStatus(FileStatus.valueOf(profile.getFileStatus()))
            .withCycleNumber(profile.getArgoFloatCycle().getCycleNumber())
            .withActionTimestamp(profile.getLastUpdatedTime().toInstant())
            .withDate(profile.getProfileDate() == null ? null : profile.getProfileDate().toInstant())
            .withFileType(ArgoFileType.PROFILE_CORE)
            .build());
      }

      return ProfileOperation.builder()
          .withFileName(fileName)
          .withFiles(files)
          .build();
    }
  }

  MetadataRecordPage getBioProfileIndexPage(IndexPageRequest pageRequest) {

    try (EntityManager em = entityManagerFactory.createEntityManager()) {
      long count = em.createQuery(
              """
                  SELECT COUNT(profile.filePath) FROM ArgoFloatProfileEntity profile 
                  WHERE profile.fileStatus = 'ACTIVE' AND profile.fileType = 'PROFILE_BIOCHEMICAL'
                  """, Long.class)
          .getSingleResult();

      List<ArgoFloatProfileEntity> pageResults = em.createQuery(
              """
                    SELECT profile FROM ArgoFloatProfileEntity profile 
                    WHERE profile.fileStatus = 'ACTIVE' AND profile.fileType = 'PROFILE_BIOCHEMICAL'
                    ORDER BY profile.filePath
                  """, ArgoFloatProfileEntity.class)
          .setMaxResults(pageRequest.getPageSize())
          .setFirstResult((pageRequest.getPageNumber() - 1) * pageRequest.getPageSize())
          .getResultList();



      return createMetadataRecordPage(count, pageResults, Finder::fromProfileEntity, pageRequest);
    }
  }

  private static MetadataRecord fromProfileEntity(ArgoFloatProfileEntity profile) {
    return MetadataRecord.builder()
      .withFile(profile.getFilePath())
      .withDate(profile.getProfileDate() == null ? null : profile.getProfileDate().toInstant())
      .withLatitude(profile.getLatitude())
      .withLongitude(profile.getLongitude())
      .withOcean(profile.getOcean() == null ? null : ArgoOcean.fromCode(profile.getOcean()))
      .withProfilerType(profile.getProfilerType())
      .withInstitution(profile.getInstitution())
      .withParameters(profile.getParameters().stream().sorted(Comparator.comparingInt(ArgoFloatProfileParameterEntity::getParameterIndex)).map(
          ArgoFloatProfileParameterEntity::getParameterName).toList())
      .withParameterDataMode(profile.getParameterDataMode())
      .withDateUpdate(profile.getDateUpdate() == null ? null : profile.getDateUpdate().toInstant())
      .build();
  }

  private static <T> DefaultMetadataRecordPage createMetadataRecordPage(long count, List<T> pageResults, Function<T, MetadataRecord> transform, IndexPageRequest pageRequest) {
    return DefaultMetadataRecordPage.builder()
        .withTotalRecords(count)
        .withIndexPageRequest(DefaultIndexPageRequest.builder(pageRequest).build())
        .withPage(pageResults.stream()
            .map(transform)
            .toList())
        .build();
  }


  MetadataRecordPage getSyntheticProfileIndexPage(IndexPageRequest pageRequest) {

    try (EntityManager em = entityManagerFactory.createEntityManager()) {
      long count = em.createQuery(
              """
                  SELECT COUNT(profile.filePath) FROM ArgoFloatProfileEntity profile 
                  WHERE profile.fileStatus = 'ACTIVE' AND profile.fileType = 'SYNTHETIC_PROFILE_SINGLE_CYCLE'
                  """, Long.class)
          .getSingleResult();

      List<ArgoFloatProfileEntity> pageResults = em.createQuery(
              """
                    SELECT profile FROM ArgoFloatProfileEntity profile 
                    WHERE profile.fileStatus = 'ACTIVE' AND profile.fileType = 'SYNTHETIC_PROFILE_SINGLE_CYCLE'
                    ORDER BY profile.filePath
                  """, ArgoFloatProfileEntity.class)
          .setMaxResults(pageRequest.getPageSize())
          .setFirstResult((pageRequest.getPageNumber() - 1) * pageRequest.getPageSize())
          .getResultList();

      return createMetadataRecordPage(count, pageResults, Finder::fromProfileEntity, pageRequest);
    }
  }


  MetadataRecordPage getTechnicalIndexPage(IndexPageRequest indexPageRequest) {
    try (EntityManager em = entityManagerFactory.createEntityManager()) {
      long count = em.createQuery("""
      SELECT COUNT(t.filePath) FROM ArgoFloatTechnicalInfoEntity t
      WHERE t.fileStatus = 'ACTIVE'
      """, Long.class)
        .getSingleResult();

      List<ArgoFloatTechnicalInfoEntity> pageResults = em.createQuery("""
      SELECT t FROM ArgoFloatTechnicalInfoEntity t
      WHERE t.fileStatus = 'ACTIVE'
      ORDER BY t.filePath
      """, ArgoFloatTechnicalInfoEntity.class)
        .setMaxResults(indexPageRequest.getPageSize())
        .setFirstResult((indexPageRequest.getPageNumber() - 1) * indexPageRequest.getPageSize())
        .getResultList();

      return createMetadataRecordPage(count, pageResults, Finder::fromTechnicalEntity, indexPageRequest);
    }
  }

  private static MetadataRecord fromTechnicalEntity(ArgoFloatTechnicalInfoEntity technicalFile) {
    return MetadataRecord.builder()
      .withFileType(ArgoFileType.TECHNICAL_DATA)
      .withFile(technicalFile.getFilePath())
      .withDac(technicalFile.getArgoFloat().getArgoDac().getDacName())
      .withFloatId(technicalFile.getArgoFloat().getFloatId())
      .withInstitution(technicalFile.getInstitution())
      .withDateUpdate(technicalFile.getDateUpdate() == null ? null : technicalFile.getDateUpdate().toInstant())
      .build();
  }

  MetadataRecordPage getTrajectoryIndexPage(IndexPageRequest indexPageRequest) {
    try (EntityManager em = entityManagerFactory.createEntityManager()) {
      long count = em.createQuery("""
      SELECT COUNT(t.filePath) FROM ArgoFloatTrajectoryEntity t
      WHERE t.fileStatus = 'ACTIVE'
      """, Long.class)
        .getSingleResult();

      List<ArgoFloatTrajectoryEntity> pageResults = em.createQuery("""
      SELECT t FROM ArgoFloatTrajectoryEntity t
      WHERE t.fileStatus = 'ACTIVE'
      ORDER BY t.filePath
      """, ArgoFloatTrajectoryEntity.class)
        .setMaxResults(indexPageRequest.getPageSize())
        .setFirstResult((indexPageRequest.getPageNumber() - 1) * indexPageRequest.getPageSize())
        .getResultList();

      return createMetadataRecordPage(count, pageResults, Finder::fromTrajectoryEntity, indexPageRequest);
    }
  }

  private static MetadataRecord fromTrajectoryEntity(ArgoFloatTrajectoryEntity trajectoryFile) {
    return MetadataRecord.builder()
      .withFileType(ArgoFileType.TRAJECTORY)
      .withFile(trajectoryFile.getFilePath())
      .withDac(trajectoryFile.getArgoFloat().getArgoDac().getDacName())
      .withFloatId(trajectoryFile.getArgoFloat().getFloatId())
      .withProfilerType(trajectoryFile.getProfilerType())
      .withInstitution(trajectoryFile.getInstitution())
      .withDateUpdate(trajectoryFile.getDateUpdate() == null ? null : trajectoryFile.getDateUpdate().toInstant())
      .withLatitudeMax(trajectoryFile.getLatitudeMax())
      .withLatitudeMin(trajectoryFile.getLatitudeMin())
      .withLongitudeMax(trajectoryFile.getLongitudeMax())
      .withLongitudeMin(trajectoryFile.getLongitudeMin())
      .build();
  }
}
