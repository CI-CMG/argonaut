package edu.colorado.cires.argonaut.metadata.jpa;

import edu.colorado.cires.argonaut.messaging.core.databind.ArgoFileType;
import edu.colorado.cires.argonaut.messaging.core.databind.ArgoOcean;
import edu.colorado.cires.argonaut.messaging.core.databind.GeoMergeInfo;
import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord;
import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord.FileStatus;
import edu.colorado.cires.argonaut.messaging.core.databind.ProfileOperation;
import edu.colorado.cires.argonaut.metadata.core.DefaultGeoMergePage;
import edu.colorado.cires.argonaut.metadata.core.DefaultIndexPageRequest;
import edu.colorado.cires.argonaut.metadata.core.DefaultProfilePage;
import edu.colorado.cires.argonaut.metadata.core.DefaultRemovedFilePage;
import edu.colorado.cires.argonaut.metadata.core.DefaultRemovedFileSearch;
import edu.colorado.cires.argonaut.metadata.core.GeoMergePage;
import edu.colorado.cires.argonaut.metadata.core.IndexPageRequest;
import edu.colorado.cires.argonaut.metadata.core.ProfilePage;
import edu.colorado.cires.argonaut.metadata.core.RecentProfileSearch;
import edu.colorado.cires.argonaut.metadata.core.RemovedFilePage;
import edu.colorado.cires.argonaut.metadata.core.RemovedFileSearch;
import edu.colorado.cires.argonaut.metadata.jpa.entity.CycleEntity;
import edu.colorado.cires.argonaut.metadata.jpa.entity.FileRemovedTimeEntity;
import edu.colorado.cires.argonaut.metadata.jpa.entity.FloatEntity;
import edu.colorado.cires.argonaut.metadata.jpa.entity.MetadataFileEntity;
import edu.colorado.cires.argonaut.metadata.jpa.entity.ProfileFileEntity;
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
            .withFileType(ArgoFileType.METADATA)
            .withDac(result.getFloatId().getDac().getDac())
            .withFile(result.getFile())
            .withFileName(result.getFileName())
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
            .withFileType(ArgoFileType.valueOf(result.getFileType()))
            .withDac(result.getCycle().getFloatId().getDac().getDac())
            .withFile(result.getFile())
            .withFileName(result.getFileName())
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

  private static List<MetadataRecord> getGeoPaths(EntityManager em, int year, int month, int day, String ocean) {
    List<ProfileFileEntity> entities = em.createQuery(
            """
                SELECT p FROM ProfileFileEntity p
                  WHERE p.year = :year
                    AND p.month = :month
                    AND p.day = :day
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
        .withDac(entity.getCycle().getFloatId().getDac().getDac())
        .withFile(entity.getFile())
        .withFloatId(entity.getCycle().getFloatId().getFloatId())
        .withFileStatus(FileStatus.valueOf(entity.getFileStatus()))
        .withDate(entity.getDate() == null ? null : entity.getDate().toInstant())
        .build()).toList();

  }

  GeoMergePage findUpdatedOrMissingGeoMergeFilesPage(IndexPageRequest pageRequest) {
    try (EntityManager em = entityManagerFactory.createEntityManager()) {
      long count = em.createQuery(
          """
                 SELECT COUNT(DISTINCT CONCAT(profile.year, '_', profile.month, '_', profile.day, '_', profile.ocean)) FROM ProfileFileEntity profile
                 WHERE profile.fileType = 'PROFILE_CORE' AND 
                       (( profile.fileStatus = 'ACTIVE' AND profile.geoMergeTime IS NULL ) 
                       OR  
                       ( profile.fileStatus = 'REMOVED' AND profile.geoMergeTime IS NOT NULL ))
              """, Long.class).getSingleResult();
      List<String> geoMergeIds = em.createQuery(
              """
                     SELECT DISTINCT CONCAT(profile.year, '_', profile.month, '_', profile.day, '_', profile.ocean) id FROM ProfileFileEntity profile
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
                 SELECT COUNT(DISTINCT profile.cycle.floatId.id) FROM ProfileFileEntity profile
                 WHERE profile.fileType = 'PROFILE_CORE' AND 
                       (( profile.fileStatus = 'ACTIVE' AND profile.multiFloatMergeTime IS NULL ) 
                       OR  
                       ( profile.fileStatus = 'REMOVED' AND profile.multiFloatMergeTime IS NOT NULL ))
              """, Long.class).getSingleResult();
      List<String> floatIds = em.createQuery(
              """
                     SELECT DISTINCT profile.cycle.floatId.id fid FROM ProfileFileEntity profile
                         WHERE profile.fileType = 'PROFILE_CORE' AND 
                         (( profile.fileStatus = 'ACTIVE' AND profile.multiFloatMergeTime IS NULL ) 
                         OR  
                         ( profile.fileStatus = 'REMOVED' AND profile.multiFloatMergeTime IS NOT NULL ))
                     order by fid
                  """, String.class)
          .setMaxResults(pageRequest.getPageSize())
          .setFirstResult((pageRequest.getPageNumber() - 1) * pageRequest.getPageSize())
          .getResultList();

      List<FloatEntity> pageResults = new ArrayList<>(floatIds.size());
      for (String floatId : floatIds) {
        pageResults.add(em.find(FloatEntity.class, floatId));
      }

      return DefaultProfilePage.builder()
          .withTotalRecords(count)
          .withIndexPageRequest(DefaultIndexPageRequest.builder(pageRequest).build())
          .withPage(pageResults.stream().map(floatEntity -> ProfileOperation.builder()
              .withDac(floatEntity.getDac().getDac())
              .withFloatId(floatEntity.getFloatId())
              .withFiles(getMergeFileInfo(floatEntity))
              .build()
          ).toList()).build();
    }
  }

  private static List<MetadataRecord> getMergeFileInfo(FloatEntity floatEntity) {
    List<MetadataRecord> result = new LinkedList<>();
    for (CycleEntity cycle : floatEntity.getCycles()) {
      for (ProfileFileEntity profile : cycle.getProfiles()) {
        if (ArgoFileType.PROFILE_CORE.toString().equals(profile.getFileType())) {
          if (FileStatus.ACTIVE.toString().equals(profile.getFileStatus()) || profile.getMultiFloatMergeTime() != null) {
            result.add(MetadataRecord.builder()
                .withFileName(profile.getFileName())
                .withFile(profile.getFile())
                .withFileStatus(FileStatus.valueOf(profile.getFileStatus()))
                .withDate(profile.getDate() == null ? null : profile.getDate().toInstant())
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
                 SELECT COUNT(DISTINCT profile.cycle.id) FROM ProfileFileEntity profile
                           WHERE
                           (
                             profile.fileStatus = 'ACTIVE'
                             AND profile.cycle.floatId.metadata.fileStatus = 'ACTIVE'
                             AND (
                                    (
                                      profile.fileType = 'PROFILE_BIOCHEMICAL' 
                                      AND EXISTS (SELECT core.file FROM ProfileFileEntity core WHERE core.fileType = 'PROFILE_CORE' AND core.fileStatus = 'ACTIVE' AND core.cycle = profile.cycle)
                                    ) OR (
                                      profile.fileType = 'PROFILE_CORE'
                                      AND EXISTS (SELECT bio.file FROM ProfileFileEntity bio WHERE bio.fileType = 'PROFILE_BIOCHEMICAL' AND bio.fileStatus = 'ACTIVE' AND bio.cycle = profile.cycle) 
                                    )
                                )
                             AND (
                               profile.syntheticMergeTime IS NULL 
                               OR NOT EXISTS (SELECT mds.syntheticMergeTime FROM MetadataSyntheticMergeEntity mds WHERE mds.profile = profile)
                             )
                           ) OR (
                             (profile.fileType = 'PROFILE_BIOCHEMICAL' OR profile.fileType = 'PROFILE_CORE' )
                             AND profile.fileStatus = 'REMOVED'
                             AND profile.syntheticMergeTime IS NOT NULL
                           ) OR (
                             (profile.fileType = 'PROFILE_BIOCHEMICAL' OR profile.fileType = 'PROFILE_CORE' )
                             AND profile.fileStatus = 'ACTIVE'
                             AND profile.syntheticMergeTime IS NOT NULL
                             AND NOT profile.cycle.floatId.metadata.fileStatus = 'ACTIVE'
                           )
              """, Long.class).getSingleResult();


      // case 1: bio, core, and md are active, but missing one or more merge times -> trigger merge
      // case 2: bio or core have been removed, but have merge times -> trigger merge removal
      // case 3: bio or core are active and have merge time, but md is missing or removed -> trigger merge removal

      List<String> cycleIds = em.createQuery(
              """
                     SELECT DISTINCT profile.cycle.id cid FROM ProfileFileEntity profile
                         WHERE
                           (
                             profile.fileStatus = 'ACTIVE'
                             AND profile.cycle.floatId.metadata.fileStatus = 'ACTIVE'
                             AND (
                                    (
                                      profile.fileType = 'PROFILE_BIOCHEMICAL' 
                                      AND EXISTS (SELECT core.file FROM ProfileFileEntity core WHERE core.fileType = 'PROFILE_CORE' AND core.fileStatus = 'ACTIVE' AND core.cycle = profile.cycle)
                                    ) OR (
                                      profile.fileType = 'PROFILE_CORE'
                                      AND EXISTS (SELECT bio.file FROM ProfileFileEntity bio WHERE bio.fileType = 'PROFILE_BIOCHEMICAL' AND bio.fileStatus = 'ACTIVE' AND bio.cycle = profile.cycle) 
                                    )
                                )
                             AND (
                               profile.syntheticMergeTime IS NULL 
                               OR NOT EXISTS (SELECT mds.syntheticMergeTime FROM MetadataSyntheticMergeEntity mds WHERE mds.profile = profile)
                             )
                           ) OR (
                             (profile.fileType = 'PROFILE_BIOCHEMICAL' OR profile.fileType = 'PROFILE_CORE' )
                             AND profile.fileStatus = 'REMOVED'
                             AND profile.syntheticMergeTime IS NOT NULL
                           ) OR (
                             (profile.fileType = 'PROFILE_BIOCHEMICAL' OR profile.fileType = 'PROFILE_CORE' )
                             AND profile.fileStatus = 'ACTIVE'
                             AND profile.syntheticMergeTime IS NOT NULL
                             AND NOT profile.cycle.floatId.metadata.fileStatus = 'ACTIVE'
                           )
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
          .withPage(pageResults.stream().map(cycle -> ProfileOperation.builder()
              .withDac(cycle.getFloatId().getDac().getDac())
              .withFloatId(cycle.getFloatId().getFloatId())
              .withFiles(getSyntheticMergeFiles(cycle))
              .build()
          ).toList()).build();
    }
  }

  private static List<MetadataRecord> getSyntheticMergeFiles(CycleEntity cycle) {
    List<MetadataRecord> result = new LinkedList<>();
    MetadataFileEntity metadataFileEntity = cycle.getFloatId().getMetadata();
    if (metadataFileEntity != null) {
      result.add(MetadataRecord.builder()
          .withFileType(ArgoFileType.METADATA)
          .withFileName(metadataFileEntity.getFileName())
          .withFile(metadataFileEntity.getFile())
          .withFileStatus(FileStatus.valueOf(metadataFileEntity.getFileStatus()))
          .withDate(metadataFileEntity.getDate() == null ? null : metadataFileEntity.getDate().toInstant())
          .build());
    }
    List<MetadataRecord> profileList = new LinkedList<>();
    for (ProfileFileEntity profile : cycle.getProfiles()) {
      if (ArgoFileType.PROFILE_CORE.toString().equals(profile.getFileType()) || ArgoFileType.PROFILE_BIOCHEMICAL.toString().equals(profile.getFileType())) {
        profileList.add(MetadataRecord.builder()
            .withFileType(ArgoFileType.valueOf(profile.getFileType()))
            .withFileName(profile.getFileName())
            .withFile(profile.getFile())
            .withFileStatus(FileStatus.valueOf(profile.getFileStatus()))
            .withDate(profile.getDate() == null ? null : profile.getDate().toInstant())
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
              SELECT COUNT(frt.id) FROM FileRemovedTimeEntity frt 
              WHERE frt.removedTime < :olderThan AND frt.fileType IN (:forFileTypes)
              """, Long.class)
          .setParameter("olderThan", queryOlderThan)
          .setParameter("forFileTypes", fileTypes)
          .getSingleResult();

      List<FileRemovedTimeEntity> pageResults = em.createQuery(
              """
                     SELECT frt FROM FileRemovedTimeEntity frt 
                     WHERE frt.removedTime < :olderThan AND frt.fileType IN (:forFileTypes)
                     ORDER BY frt.metadata.file, frt.profile.file
                  """, FileRemovedTimeEntity.class)
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
            if (frt.getMetadata() != null) {
              dac = frt.getMetadata().getFloatId().getDac().getDac();
              floatId = frt.getMetadata().getFloatId().getFloatId();
              fileInfo = MetadataRecord.builder()
                  .withFileName(frt.getMetadata().getFileName())
                  .withFile(frt.getMetadata().getFile())
                  .withFileStatus(FileStatus.valueOf(frt.getMetadata().getFileStatus()))
                  .withDate(frt.getMetadata().getDate() == null ? null : frt.getMetadata().getDate().toInstant())
                  .withFileType(ArgoFileType.METADATA)
                  .withActionTimestamp(frt.getRemovedTime().toInstant())
                  .build();
            } else if(frt.getProfile() != null) {
              dac = frt.getProfile().getCycle().getFloatId().getDac().getDac();
              floatId = frt.getProfile().getCycle().getFloatId().getFloatId();
              fileInfo = MetadataRecord.builder()
                  .withFileName(frt.getProfile().getFileName())
                  .withFile(frt.getProfile().getFile())
                  .withFileStatus(FileStatus.valueOf(frt.getProfile().getFileStatus()))
                  .withDate(frt.getProfile().getDate() == null ? null : frt.getProfile().getDate().toInstant())
                  .withFileType(ArgoFileType.valueOf(frt.getProfile().getFileType()))
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

    String fileName = pageRequest.getProfileMode().getPrefix() + LATEST_FORMATTER.format(pageRequest.getLastUpdatedDateGe().atZone(ZoneId.of("UTC")).toLocalDate());

    try (EntityManager em = entityManagerFactory.createEntityManager()) {


       long count = em.createQuery(
              """
                     SELECT COUNT(profile.file) FROM ProfileFileEntity profile 
                     WHERE profile.lastUpdatedTime >= :updatedGe AND profile.lastUpdatedTime < :updatedLt AND profile.dataMode = :dataMode
                     AND profile.fileType = 'PROFILE_CORE'
                     AND ((profile.fileStatus = 'ACTIVE' AND profile.latestMergeFileName IS NULL) OR (profile.fileStatus = 'REMOVED' AND profile.latestMergeFileName IS NOT NULL))
                  """, Long.class)
          .setParameter("updatedGe", pageRequest.getLastUpdatedDateGe().atZone(ZoneId.of("UTC")))
          .setParameter("updatedLt", pageRequest.getLastUpdatedDateLt().atZone(ZoneId.of("UTC")))
          .setParameter("dataMode", pageRequest.getProfileMode().getPrefix())
          .setMaxResults(pageRequest.getLimit())
          .getSingleResult();

       List<ProfileFileEntity> results;
       if (count > 0L) {
         results = em.createQuery(
                 """
                        SELECT profile FROM ProfileFileEntity profile 
                        WHERE profile.lastUpdatedTime >= :updatedGe AND profile.lastUpdatedTime < :updatedLt AND profile.dataMode = :dataMode
                        AND profile.fileType = 'PROFILE_CORE'
                        AND ((profile.fileStatus = 'ACTIVE') OR (profile.fileStatus = 'REMOVED' AND profile.latestMergeFileName IS NOT NULL))
                        order by profile.date
                     """, ProfileFileEntity.class)
             .setParameter("updatedGe", pageRequest.getLastUpdatedDateGe().atZone(ZoneId.of("UTC")))
             .setParameter("updatedLt", pageRequest.getLastUpdatedDateLt().atZone(ZoneId.of("UTC")))
             .setParameter("dataMode", pageRequest.getProfileMode().getPrefix())
             .setMaxResults(pageRequest.getLimit())
             .getResultList();
       } else {
         results = Collections.emptyList();
       }


      List<MetadataRecord> files = new ArrayList<>(results.size());
      for (ProfileFileEntity profile : results) {
        files.add(MetadataRecord.builder()
            .withDac(profile.getCycle().getFloatId().getDac().getDac())
            .withFloatId(profile.getCycle().getFloatId().getFloatId())
            .withFile(profile.getFile())
            .withFileName(profile.getFileName())
            .withFileStatus(FileStatus.valueOf(profile.getFileStatus()))
            .withCycleNumber(profile.getCycle().getCycleNumber())
            .withActionTimestamp(profile.getLastUpdatedTime().toInstant())
            .withDate(profile.getDate() == null ? null : profile.getDate().toInstant())
            .withFileType(ArgoFileType.PROFILE_CORE)
            .build());
      }

      return ProfileOperation.builder()
          .withFileName(fileName)
          .withFiles(files)
          .build();
    }
  }
}
