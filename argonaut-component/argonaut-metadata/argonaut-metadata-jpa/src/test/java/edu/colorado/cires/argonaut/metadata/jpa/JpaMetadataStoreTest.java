package edu.colorado.cires.argonaut.metadata.jpa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.colorado.cires.argonaut.messaging.core.databind.ArgoFileType;
import edu.colorado.cires.argonaut.messaging.core.databind.ArgoOcean;
import edu.colorado.cires.argonaut.messaging.core.databind.GeoMergeInfo;
import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord;
import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord.Action;
import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord.FileStatus;
import edu.colorado.cires.argonaut.messaging.core.databind.ProfileOperation;
import edu.colorado.cires.argonaut.metadata.core.DefaultIndexPageRequest;
import edu.colorado.cires.argonaut.metadata.core.DefaultRemovedFileSearch;
import edu.colorado.cires.argonaut.metadata.core.GeoMergePage;
import edu.colorado.cires.argonaut.metadata.core.ProfilePage;
import edu.colorado.cires.argonaut.metadata.core.RemovedFilePage;
import edu.colorado.cires.argonaut.metadata.jpa.entity.ProfileFileEntity;
import edu.colorado.cires.argonaut.metadata.jpa.entity.ProfileMergeFileEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class JpaMetadataStoreTest {

  private EntityManagerFactory emf;
  private JpaMetadataStore datastore;

  @BeforeEach
  public void setup() throws Exception {
    Map<String, String> override = new HashMap<>();
    override.put("jakarta.persistence.jdbc.driver", "org.h2.Driver");
    override.put("jakarta.persistence.jdbc.url", "jdbc:h2:mem:index;DB_CLOSE_DELAY=-1");
    override.put("jakarta.persistence.jdbc.user", "sa");
    override.put("jakarta.persistence.jdbc.password", "");
    override.put("hibernate.hbm2ddl.auto", "update");
    override.put("hibernate.show_sql", "true");
    emf = Persistence.createEntityManagerFactory("argonaut-index", override);
    datastore = new JpaMetadataStore();
    datastore.setEntityManagerFactory(emf);

    try (EntityManager em = emf.createEntityManager()) {
      EntityTransaction tx = em.getTransaction();
      tx.begin();
      try {
        em.createQuery("delete from FileRemovedTimeEntity").executeUpdate();
        em.createQuery("delete from MetadataSyntheticMergeEntity").executeUpdate();
        em.createQuery("delete from ProfileMergeFileEntity").executeUpdate();
        em.createQuery("delete from ProfileFileEntity").executeUpdate();
        em.createQuery("delete from MetadataFileEntity").executeUpdate();
        em.createQuery("delete from CycleEntity").executeUpdate();
        em.createQuery("delete from FloatEntity").executeUpdate();
        em.createQuery("delete from DacEntity").executeUpdate();
        tx.commit();
      } catch (Exception e) {
        tx.rollback();
        throw e;
      }
    }
  }


  @Test
  public void testInsertUpdateFindProfileByIdAndDac() throws Exception {
    Instant date1 = Instant.now();
    Instant dateUpdate1 = date1.plusSeconds(60);
    MetadataRecord record1 = MetadataRecord.builder()
        .withFile("aoml/13857/profiles/D13857_001.nc")
        .withFileName("D13857_001.nc")
        .withDac("aoml")
        .withFloatId("13857")
        .withDirection("A")
        .withParameterDataMode("D")
        .withCycleNumber("001")
        .withDate(date1)
        .withLatitude(0.267)
        .withLatitudeMin(0.1)
        .withLatitudeMax(0.4)
        .withLongitude(-16.032)
        .withLongitudeMin(-17.0)
        .withLongitudeMax(-14.0)
        .withOcean(ArgoOcean.ATLANTIC_OCEAN)
        .withProfilerType("845")
        .withInstitution("A0")
        .withDateUpdate(dateUpdate1)
        .withParameters("params")
        .withAction(Action.UPDATE)
        .withActionTimestamp(Instant.now())
        .withFileType(ArgoFileType.PROFILE_CORE)
        .build();
    datastore.updateIndex(record1);
    Instant date2 = date1.plusSeconds(1);
    Instant dateUpdate2 = dateUpdate1.plusSeconds(1);
    MetadataRecord record2 = MetadataRecord.builder()
        .withFile("aoml/13857/profiles/D13857_002.nc")
        .withFileName("D13857_002.nc")
        .withDac("aoml")
        .withFloatId("13857")
        .withDirection("A")
        .withParameterDataMode("D")
        .withCycleNumber("002")
        .withDate(date2)
        .withLatitude(0.27)
        .withLatitudeMin(0.12)
        .withLatitudeMax(0.42)
        .withLongitude(-16.1)
        .withLongitudeMin(-17.2)
        .withLongitudeMax(-14.2)
        .withOcean(ArgoOcean.PACIFIC_OCEAN)
        .withProfilerType("846")
        .withInstitution("A1")
        .withDateUpdate(dateUpdate2)
        .withParameters("params2")
        .withAction(Action.UPDATE)
        .withActionTimestamp(Instant.now())
        .withFileType(ArgoFileType.PROFILE_CORE)
        .build();
    datastore.updateIndex(record2);
    Instant date3 = date1.plusSeconds(1);
    Instant dateUpdate3 = dateUpdate1.plusSeconds(1);
    MetadataRecord record3 = MetadataRecord.builder()
        .withFile("aoml/13857/profiles/D13857_003.nc")
        .withFileName("D13857_003.nc")
        .withDac("aoml")
        .withFloatId("13857")
        .withDirection("A")
        .withParameterDataMode("D")
        .withCycleNumber("003")
        .withDate(date3)
        .withLatitude(0.32)
        .withLatitudeMin(0d)
        .withLatitudeMax(1d)
        .withLongitude(-16d)
        .withLongitudeMin(-17d)
        .withLongitudeMax(-14d)
        .withOcean(ArgoOcean.INDIAN_OCEAN)
        .withProfilerType("847")
        .withInstitution("A2")
        .withDateUpdate(dateUpdate3)
        .withParameters("params3")
        .withAction(Action.UPDATE)
        .withActionTimestamp(Instant.now())
        .withFileType(ArgoFileType.PROFILE_CORE)
        .build();
    datastore.updateIndex(record3);
    datastore.updateIndex(MetadataRecord.builder()
        .withFileType(ArgoFileType.PROFILE_CORE)
        .withFile("aoml/13857/profiles/D13857_003.nc")
        .withFileName("D13857_003.nc")
        .withAction(Action.REMOVE)
        .withActionTimestamp(Instant.now())
        .build());

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("nnic/13857/profiles/D13857_003.nc")
        .withFileName("D13857_003.nc")
        .withDac("nnic")
        .withFloatId("13857")
        .withDirection("A")
        .withParameterDataMode("D")
        .withCycleNumber("003")
        .withDate(date3)
        .withLatitude(0.32)
        .withLatitudeMin(0d)
        .withLatitudeMax(1d)
        .withLongitude(-16d)
        .withLongitudeMin(-17d)
        .withLongitudeMax(-14d)
        .withOcean(ArgoOcean.INDIAN_OCEAN)
        .withProfilerType("847")
        .withInstitution("A2")
        .withDateUpdate(dateUpdate3)
        .withParameters("params3")
        .withAction(Action.UPDATE)
        .withActionTimestamp(Instant.now())
        .withFileType(ArgoFileType.PROFILE_CORE)
        .build());

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/13858/profiles/D13858_003.nc")
        .withFileName("D13858_003.nc")
        .withDac("aoml")
        .withFloatId("13858")
        .withDirection("A")
        .withParameterDataMode("D")
        .withCycleNumber("003")
        .withDate(date3)
        .withLatitude(0.32)
        .withLatitudeMin(0d)
        .withLatitudeMax(1d)
        .withLongitude(-16d)
        .withLongitudeMin(-17d)
        .withLongitudeMax(-14d)
        .withOcean(ArgoOcean.INDIAN_OCEAN)
        .withProfilerType("847")
        .withInstitution("A2")
        .withDateUpdate(dateUpdate3)
        .withParameters("params3")
        .withAction(Action.UPDATE)
        .withActionTimestamp(Instant.now())
        .withFileType(ArgoFileType.PROFILE_CORE)
        .build());

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("nnic/1000/profiles/D1000_001.nc")
        .withFileName("D1000_001.nc")
        .withDac("nnic")
        .withFloatId("1000")
        .withDirection("A")
        .withParameterDataMode("D")
        .withCycleNumber("001")
        .withDate(date3)
        .withLatitude(0.32)
        .withLatitudeMin(0d)
        .withLatitudeMax(1d)
        .withLongitude(-16d)
        .withLongitudeMin(-17d)
        .withLongitudeMax(-14d)
        .withOcean(ArgoOcean.INDIAN_OCEAN)
        .withProfilerType("847")
        .withInstitution("A2")
        .withDateUpdate(dateUpdate3)
        .withParameters("params3")
        .withAction(Action.UPDATE)
        .withActionTimestamp(Instant.now())
        .withFileType(ArgoFileType.PROFILE_CORE)
        .build());

    MetadataRecord record12 = MetadataRecord.builder(record1)
        .withInstitution("A3")
        .build();
    datastore.updateIndex(record12);

    Instant now = Instant.now();

    assertEquals(
        MetadataRecord.builder(record12).withActionTimestamp(now).withAction(null).withFileStatus(FileStatus.ACTIVE).build(),
        MetadataRecord.builder(datastore.findByFile("aoml/13857/profiles/D13857_001.nc").get()).withActionTimestamp(now).build()
    );
    assertEquals(
        MetadataRecord.builder(record2).withActionTimestamp(now).withAction(null).withFileStatus(FileStatus.ACTIVE).build(),
        MetadataRecord.builder(datastore.findByFile("aoml/13857/profiles/D13857_002.nc").get()).withActionTimestamp(now).build()
    );
    assertEquals(
        MetadataRecord.builder(record3).withActionTimestamp(now).withAction(null).withFileStatus(FileStatus.REMOVED).build(),
        MetadataRecord.builder(datastore.findByFile("aoml/13857/profiles/D13857_003.nc", true).get()).withActionTimestamp(now).build()
    );
    assertFalse(datastore.findByFile("aoml/13857/profiles/D13857_004.nc").isPresent());
  }


  @Test
  public void testFindMissingSyntheticProfilesPage() throws Exception {

    Instant date = Instant.now();

    // no synth

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/13857/13857_meta.nc")
        .withFileName("13857_meta.nc")
        .withDac("aoml")
        .withFloatId("13857")
        .withActionTimestamp(date)
        .withDate(date)
        .withLatitude(0.267)
        .withLongitude(-16.032)
        .withOcean(ArgoOcean.ATLANTIC_OCEAN)
        .withProfilerType("845")
        .withInstitution("A0")
        .withDateUpdate(date)
        .withAction(Action.UPDATE)
        .withFileType(ArgoFileType.METADATA)
        .build());

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/13857/profiles/D13857_001.nc")
        .withFileName("D13857_001.nc")
        .withDac("aoml")
        .withFloatId("13857")
        .withCycleNumber("001")
        .withDirection("A")
        .withParameterDataMode("D")
        .withActionTimestamp(date)
        .withDate(date)
        .withLatitude(0.267)
        .withLatitudeMin(0.1)
        .withLatitudeMax(0.4)
        .withLongitude(-16.032)
        .withLongitudeMin(-17.0)
        .withLongitudeMax(-14.0)
        .withOcean(ArgoOcean.ATLANTIC_OCEAN)
        .withProfilerType("845")
        .withInstitution("A0")
        .withDateUpdate(date)
        .withParameters("params")
        .withAction(Action.UPDATE)
        .withFileType(ArgoFileType.PROFILE_CORE)
        .build());

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/13857/profiles/BD13857_001.nc")
        .withFileName("BD13857_001.nc")
        .withDac("aoml")
        .withFloatId("13857")
        .withCycleNumber("001")
        .withDirection("A")
        .withParameterDataMode("D")
        .withActionTimestamp(date)
        .withDate(date)
        .withLatitude(0.267)
        .withLatitudeMin(0.1)
        .withLatitudeMax(0.4)
        .withLongitude(-16.032)
        .withLongitudeMin(-17.0)
        .withLongitudeMax(-14.0)
        .withOcean(ArgoOcean.ATLANTIC_OCEAN)
        .withProfilerType("845")
        .withInstitution("A0")
        .withDateUpdate(date)
        .withParameters("params")
        .withAction(Action.UPDATE)
        .withFileType(ArgoFileType.PROFILE_BIOCHEMICAL)
        .build());

    // no synth 2

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/123/123_meta.nc")
        .withFileName("123_meta.nc")
        .withDac("aoml")
        .withFloatId("123")
        .withActionTimestamp(date)
        .withDate(date)
        .withLatitude(0.267)
        .withLongitude(-16.032)
        .withOcean(ArgoOcean.ATLANTIC_OCEAN)
        .withProfilerType("845")
        .withInstitution("A0")
        .withDateUpdate(date)
        .withAction(Action.UPDATE)
        .withFileType(ArgoFileType.METADATA)
        .build());

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/123/profiles/D123_001.nc")
        .withFileName("D123_001.nc")
        .withDac("aoml")
        .withFloatId("123")
        .withCycleNumber("001")
        .withDirection("A")
        .withParameterDataMode("D")
        .withActionTimestamp(date)
        .withDate(date)
        .withLatitude(0.267)
        .withLatitudeMin(0.1)
        .withLatitudeMax(0.4)
        .withLongitude(-16.032)
        .withLongitudeMin(-17.0)
        .withLongitudeMax(-14.0)
        .withOcean(ArgoOcean.ATLANTIC_OCEAN)
        .withProfilerType("845")
        .withInstitution("A0")
        .withDateUpdate(date)
        .withParameters("params")
        .withAction(Action.UPDATE)
        .withFileType(ArgoFileType.PROFILE_CORE)
        .build());

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/123/profiles/BD123_001.nc")
        .withFileName("BD123_001.nc")
        .withDac("aoml")
        .withFloatId("123")
        .withCycleNumber("001")
        .withDirection("A")
        .withParameterDataMode("D")
        .withActionTimestamp(date)
        .withDate(date)
        .withLatitude(0.267)
        .withLatitudeMin(0.1)
        .withLatitudeMax(0.4)
        .withLongitude(-16.032)
        .withLongitudeMin(-17.0)
        .withLongitudeMax(-14.0)
        .withOcean(ArgoOcean.ATLANTIC_OCEAN)
        .withProfilerType("845")
        .withInstitution("A0")
        .withDateUpdate(date)
        .withParameters("params")
        .withAction(Action.UPDATE)
        .withFileType(ArgoFileType.PROFILE_BIOCHEMICAL)
        .build());

    // synth

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/13857/profiles/D13857_002.nc")
        .withFileName("D13857_002.nc")
        .withDac("aoml")
        .withFloatId("13857")
        .withCycleNumber("002")
        .withDirection("A")
        .withParameterDataMode("D")
        .withActionTimestamp(date)
        .withDate(date)
        .withLatitude(0.267)
        .withLatitudeMin(0.1)
        .withLatitudeMax(0.4)
        .withLongitude(-16.032)
        .withLongitudeMin(-17.0)
        .withLongitudeMax(-14.0)
        .withOcean(ArgoOcean.ATLANTIC_OCEAN)
        .withProfilerType("845")
        .withInstitution("A0")
        .withDateUpdate(date)
        .withParameters("params")
        .withAction(Action.UPDATE)
        .withFileType(ArgoFileType.PROFILE_CORE)
        .build());

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/13857/profiles/BD13857_002.nc")
        .withFileName("BD13857_002.nc")
        .withDac("aoml")
        .withFloatId("13857")
        .withCycleNumber("002")
        .withDirection("A")
        .withParameterDataMode("D")
        .withActionTimestamp(date)
        .withDate(date)
        .withLatitude(0.267)
        .withLatitudeMin(0.1)
        .withLatitudeMax(0.4)
        .withLongitude(-16.032)
        .withLongitudeMin(-17.0)
        .withLongitudeMax(-14.0)
        .withOcean(ArgoOcean.ATLANTIC_OCEAN)
        .withProfilerType("845")
        .withInstitution("A0")
        .withDateUpdate(date)
        .withParameters("params")
        .withAction(Action.UPDATE)
        .withFileType(ArgoFileType.PROFILE_BIOCHEMICAL)
        .build());

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/13857/13857_meta.nc")
        .withFileName("13857_meta.nc")
        .withActionTimestamp(date)
        .withDac("aoml")
        .withFloatId("13857")
        .withAction(Action.SYNTHETIC_MERGE)
        .withFileType(ArgoFileType.METADATA)
        .withRelatedFiles(Arrays.asList(
            "aoml/13857/profiles/D13857_002.nc",
            "aoml/13857/profiles/BD13857_002.nc"
        ))
        .build());

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/13857/profiles/SD13857_002.nc")
        .withFileName("SD13857_002.nc")
        .withDac("aoml")
        .withFloatId("13857")
        .withCycleNumber("002")
        .withDirection("A")
        .withParameterDataMode("D")
        .withActionTimestamp(date)
        .withDate(date)
        .withLatitude(0.267)
        .withLongitude(-16.032)
        .withOcean(ArgoOcean.ATLANTIC_OCEAN)
        .withProfilerType("845")
        .withInstitution("A0")
        .withDateUpdate(date)
        .withAction(Action.UPDATE)
        .withFileType(ArgoFileType.SYNTHETIC_PROFILE_SINGLE_CYCLE)
        .build());

    ProfilePage page1 = datastore.findUpdatedOrMissingSyntheticProfilesPage(DefaultIndexPageRequest.builder().withPageSize(1).build());
    assertEquals(2, page1.getTotalRecords());
    assertEquals(2, page1.getTotalPages());
    assertEquals(1, page1.getPageSize());
    assertEquals(1, page1.getPageNumber());
    assertEquals(Collections.singletonList(ProfileOperation.builder()
            .withDac("aoml")
            .withFloatId("123")
            .withFiles(Arrays.asList(
                MetadataRecord.builder().withFileType(ArgoFileType.METADATA).withFile("aoml/123/123_meta.nc").withFileName("123_meta.nc").withFileStatus(FileStatus.ACTIVE).build(),
                MetadataRecord.builder().withFileType(ArgoFileType.PROFILE_BIOCHEMICAL).withFile("aoml/123/profiles/BD123_001.nc").withFileName("BD123_001.nc").withFileStatus(FileStatus.ACTIVE)
                    .build(),
                MetadataRecord.builder().withFileType(ArgoFileType.PROFILE_CORE).withFile("aoml/123/profiles/D123_001.nc").withFileName("D123_001.nc").withFileStatus(FileStatus.ACTIVE).build()
            ))
            .build()),
        page1.getPage());

    ProfilePage page2 = datastore.findUpdatedOrMissingSyntheticProfilesPage(page1.getNextPage().get());
    assertEquals(2, page2.getTotalRecords());
    assertEquals(2, page2.getTotalPages());
    assertEquals(1, page2.getPageSize());
    assertEquals(2, page2.getPageNumber());
    assertEquals(Collections.singletonList(ProfileOperation.builder()
            .withDac("aoml")
            .withFloatId("13857")
            .withFiles(Arrays.asList(
                MetadataRecord.builder().withFileType(ArgoFileType.METADATA).withFile("aoml/13857/13857_meta.nc").withFileName("13857_meta.nc").withFileStatus(FileStatus.ACTIVE).build(),
                MetadataRecord.builder().withFileType(ArgoFileType.PROFILE_BIOCHEMICAL).withFile("aoml/13857/profiles/BD13857_001.nc").withFileName("BD13857_001.nc").withFileStatus(FileStatus.ACTIVE)
                    .build(),
                MetadataRecord.builder().withFileType(ArgoFileType.PROFILE_CORE).withFile("aoml/13857/profiles/D13857_001.nc").withFileName("D13857_001.nc").withFileStatus(FileStatus.ACTIVE)
                    .build()
            ))
            .build()),
        page2.getPage());
  }

  @Test
  public void testFindRemovedMissingSyntheticProfilesPage() throws Exception {

    Instant date = Instant.now();

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/123/123_meta.nc")
        .withFileName("123_meta.nc")
        .withDac("aoml")
        .withFloatId("123")
        .withActionTimestamp(date)
        .withDate(date)
        .withLatitude(0.267)
        .withLongitude(-16.032)
        .withOcean(ArgoOcean.ATLANTIC_OCEAN)
        .withProfilerType("845")
        .withInstitution("A0")
        .withDateUpdate(date)
        .withAction(Action.UPDATE)
        .withFileType(ArgoFileType.METADATA)
        .build());

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/123/profiles/D123_001.nc")
        .withFileName("D123_001.nc")
        .withDac("aoml")
        .withFloatId("123")
        .withCycleNumber("001")
        .withDirection("A")
        .withParameterDataMode("D")
        .withActionTimestamp(date)
        .withDate(date)
        .withLatitude(0.267)
        .withLatitudeMin(0.1)
        .withLatitudeMax(0.4)
        .withLongitude(-16.032)
        .withLongitudeMin(-17.0)
        .withLongitudeMax(-14.0)
        .withOcean(ArgoOcean.ATLANTIC_OCEAN)
        .withProfilerType("845")
        .withInstitution("A0")
        .withDateUpdate(date)
        .withParameters("params")
        .withAction(Action.UPDATE)
        .withFileType(ArgoFileType.PROFILE_CORE)
        .build());

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/123/profiles/BD123_001.nc")
        .withFileName("BD123_001.nc")
        .withDac("aoml")
        .withFloatId("123")
        .withCycleNumber("001")
        .withDirection("A")
        .withParameterDataMode("D")
        .withActionTimestamp(date)
        .withDate(date)
        .withLatitude(0.267)
        .withLatitudeMin(0.1)
        .withLatitudeMax(0.4)
        .withLongitude(-16.032)
        .withLongitudeMin(-17.0)
        .withLongitudeMax(-14.0)
        .withOcean(ArgoOcean.ATLANTIC_OCEAN)
        .withProfilerType("845")
        .withInstitution("A0")
        .withDateUpdate(date)
        .withParameters("params")
        .withAction(Action.UPDATE)
        .withFileType(ArgoFileType.PROFILE_BIOCHEMICAL)
        .build());

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/999/999_meta.nc")
        .withFileName("999_meta.nc")
        .withDac("aoml")
        .withFloatId("999")
        .withActionTimestamp(date)
        .withDate(date)
        .withLatitude(0.267)
        .withLongitude(-16.032)
        .withOcean(ArgoOcean.ATLANTIC_OCEAN)
        .withProfilerType("845")
        .withInstitution("A0")
        .withDateUpdate(date)
        .withAction(Action.UPDATE)
        .withFileType(ArgoFileType.METADATA)
        .build());

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/999/profiles/D999_001.nc")
        .withFileName("D999_001.nc")
        .withDac("aoml")
        .withFloatId("999")
        .withCycleNumber("001")
        .withDirection("A")
        .withParameterDataMode("D")
        .withActionTimestamp(date)
        .withDate(date)
        .withLatitude(0.267)
        .withLatitudeMin(0.1)
        .withLatitudeMax(0.4)
        .withLongitude(-16.032)
        .withLongitudeMin(-17.0)
        .withLongitudeMax(-14.0)
        .withOcean(ArgoOcean.ATLANTIC_OCEAN)
        .withProfilerType("845")
        .withInstitution("A0")
        .withDateUpdate(date)
        .withParameters("params")
        .withAction(Action.UPDATE)
        .withFileType(ArgoFileType.PROFILE_CORE)
        .build());

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/999/profiles/BD999_001.nc")
        .withFileName("BD999_001.nc")
        .withDac("aoml")
        .withFloatId("999")
        .withCycleNumber("001")
        .withDirection("A")
        .withParameterDataMode("D")
        .withActionTimestamp(date)
        .withDate(date)
        .withLatitude(0.267)
        .withLatitudeMin(0.1)
        .withLatitudeMax(0.4)
        .withLongitude(-16.032)
        .withLongitudeMin(-17.0)
        .withLongitudeMax(-14.0)
        .withOcean(ArgoOcean.ATLANTIC_OCEAN)
        .withProfilerType("845")
        .withInstitution("A0")
        .withDateUpdate(date)
        .withParameters("params")
        .withAction(Action.UPDATE)
        .withFileType(ArgoFileType.PROFILE_BIOCHEMICAL)
        .build());

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/13857/13857_meta.nc")
        .withFileName("13857_meta.nc")
        .withDac("aoml")
        .withFloatId("13857")
        .withActionTimestamp(date)
        .withDate(date)
        .withLatitude(0.267)
        .withLongitude(-16.032)
        .withOcean(ArgoOcean.ATLANTIC_OCEAN)
        .withProfilerType("845")
        .withInstitution("A0")
        .withDateUpdate(date)
        .withAction(Action.UPDATE)
        .withFileType(ArgoFileType.METADATA)
        .build());

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/13857/profiles/D13857_001.nc")
        .withFileName("D13857_001.nc")
        .withDac("aoml")
        .withFloatId("13857")
        .withCycleNumber("001")
        .withDirection("A")
        .withParameterDataMode("D")
        .withActionTimestamp(date)
        .withDate(date)
        .withLatitude(0.267)
        .withLatitudeMin(0.1)
        .withLatitudeMax(0.4)
        .withLongitude(-16.032)
        .withLongitudeMin(-17.0)
        .withLongitudeMax(-14.0)
        .withOcean(ArgoOcean.ATLANTIC_OCEAN)
        .withProfilerType("845")
        .withInstitution("A0")
        .withDateUpdate(date)
        .withParameters("params")
        .withAction(Action.UPDATE)
        .withFileType(ArgoFileType.PROFILE_CORE)
        .build());

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/13857/profiles/BD13857_001.nc")
        .withFileName("BD13857_001.nc")
        .withDac("aoml")
        .withFloatId("13857")
        .withCycleNumber("001")
        .withDirection("A")
        .withParameterDataMode("D")
        .withActionTimestamp(date)
        .withDate(date)
        .withLatitude(0.267)
        .withLatitudeMin(0.1)
        .withLatitudeMax(0.4)
        .withLongitude(-16.032)
        .withLongitudeMin(-17.0)
        .withLongitudeMax(-14.0)
        .withOcean(ArgoOcean.ATLANTIC_OCEAN)
        .withProfilerType("845")
        .withInstitution("A0")
        .withDateUpdate(date)
        .withParameters("params")
        .withAction(Action.UPDATE)
        .withFileType(ArgoFileType.PROFILE_BIOCHEMICAL)
        .build());

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/13857/profiles/D13857_002.nc")
        .withFileName("D13857_002.nc")
        .withDac("aoml")
        .withFloatId("13857")
        .withCycleNumber("002")
        .withDirection("A")
        .withParameterDataMode("D")
        .withActionTimestamp(date)
        .withDate(date)
        .withLatitude(0.267)
        .withLatitudeMin(0.1)
        .withLatitudeMax(0.4)
        .withLongitude(-16.032)
        .withLongitudeMin(-17.0)
        .withLongitudeMax(-14.0)
        .withOcean(ArgoOcean.ATLANTIC_OCEAN)
        .withProfilerType("845")
        .withInstitution("A0")
        .withDateUpdate(date)
        .withParameters("params")
        .withAction(Action.UPDATE)
        .withFileType(ArgoFileType.PROFILE_CORE)
        .build());

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/13857/profiles/BD13857_002.nc")
        .withFileName("BD13857_002.nc")
        .withDac("aoml")
        .withFloatId("13857")
        .withCycleNumber("002")
        .withDirection("A")
        .withParameterDataMode("D")
        .withActionTimestamp(date)
        .withDate(date)
        .withLatitude(0.267)
        .withLatitudeMin(0.1)
        .withLatitudeMax(0.4)
        .withLongitude(-16.032)
        .withLongitudeMin(-17.0)
        .withLongitudeMax(-14.0)
        .withOcean(ArgoOcean.ATLANTIC_OCEAN)
        .withProfilerType("845")
        .withInstitution("A0")
        .withDateUpdate(date)
        .withParameters("params")
        .withAction(Action.UPDATE)
        .withFileType(ArgoFileType.PROFILE_BIOCHEMICAL)
        .build());

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/13857/profiles/D13857_003.nc")
        .withFileName("D13857_003.nc")
        .withDac("aoml")
        .withFloatId("13857")
        .withCycleNumber("003")
        .withDirection("A")
        .withParameterDataMode("D")
        .withActionTimestamp(date)
        .withDate(date)
        .withLatitude(0.267)
        .withLatitudeMin(0.1)
        .withLatitudeMax(0.4)
        .withLongitude(-16.032)
        .withLongitudeMin(-17.0)
        .withLongitudeMax(-14.0)
        .withOcean(ArgoOcean.ATLANTIC_OCEAN)
        .withProfilerType("845")
        .withInstitution("A0")
        .withDateUpdate(date)
        .withParameters("params")
        .withAction(Action.UPDATE)
        .withFileType(ArgoFileType.PROFILE_CORE)
        .build());

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/13857/profiles/BD13857_003.nc")
        .withFileName("BD13857_003.nc")
        .withDac("aoml")
        .withFloatId("13857")
        .withCycleNumber("003")
        .withDirection("A")
        .withParameterDataMode("D")
        .withActionTimestamp(date)
        .withDate(date)
        .withLatitude(0.267)
        .withLatitudeMin(0.1)
        .withLatitudeMax(0.4)
        .withLongitude(-16.032)
        .withLongitudeMin(-17.0)
        .withLongitudeMax(-14.0)
        .withOcean(ArgoOcean.ATLANTIC_OCEAN)
        .withProfilerType("845")
        .withInstitution("A0")
        .withDateUpdate(date)
        .withParameters("params")
        .withAction(Action.UPDATE)
        .withFileType(ArgoFileType.PROFILE_BIOCHEMICAL)
        .build());

    ProfilePage page = datastore.findUpdatedOrMissingSyntheticProfilesPage(DefaultIndexPageRequest.builder().withPageSize(10).build());
    assertEquals(Arrays.asList(
            ProfileOperation.builder()
                .withDac("aoml")
                .withFloatId("123")
                .withFiles(Arrays.asList(
                    MetadataRecord.builder().withFileType(ArgoFileType.METADATA).withFile("aoml/123/123_meta.nc").withFileName("123_meta.nc").withFileStatus(FileStatus.ACTIVE).build(),
                    MetadataRecord.builder().withFileType(ArgoFileType.PROFILE_BIOCHEMICAL).withFile("aoml/123/profiles/BD123_001.nc").withFileName("BD123_001.nc")
                        .withFileStatus(FileStatus.ACTIVE).build(),
                    MetadataRecord.builder().withFileType(ArgoFileType.PROFILE_CORE).withFile("aoml/123/profiles/D123_001.nc").withFileName("D123_001.nc").withFileStatus(FileStatus.ACTIVE)
                        .build()
                ))
                .build(),
            ProfileOperation.builder()
                .withDac("aoml")
                .withFloatId("13857")
                .withFiles(Arrays.asList(
                    MetadataRecord.builder().withFileType(ArgoFileType.METADATA).withFile("aoml/13857/13857_meta.nc").withFileName("13857_meta.nc").withFileStatus(FileStatus.ACTIVE).build(),
                    MetadataRecord.builder().withFileType(ArgoFileType.PROFILE_BIOCHEMICAL).withFile("aoml/13857/profiles/BD13857_001.nc").withFileName("BD13857_001.nc")
                        .withFileStatus(FileStatus.ACTIVE).build(),
                    MetadataRecord.builder().withFileType(ArgoFileType.PROFILE_CORE).withFile("aoml/13857/profiles/D13857_001.nc").withFileName("D13857_001.nc").withFileStatus(FileStatus.ACTIVE)
                        .build()
                ))
                .build(),
            ProfileOperation.builder()
                .withDac("aoml")
                .withFloatId("13857")
                .withFiles(Arrays.asList(
                    MetadataRecord.builder().withFileType(ArgoFileType.METADATA).withFile("aoml/13857/13857_meta.nc").withFileName("13857_meta.nc").withFileStatus(FileStatus.ACTIVE).build(),
                    MetadataRecord.builder().withFileType(ArgoFileType.PROFILE_BIOCHEMICAL).withFile("aoml/13857/profiles/BD13857_002.nc").withFileName("BD13857_002.nc")
                        .withFileStatus(FileStatus.ACTIVE).build(),
                    MetadataRecord.builder().withFileType(ArgoFileType.PROFILE_CORE).withFile("aoml/13857/profiles/D13857_002.nc").withFileName("D13857_002.nc").withFileStatus(FileStatus.ACTIVE)
                        .build()
                ))
                .build(),
            ProfileOperation.builder()
                .withDac("aoml")
                .withFloatId("13857")
                .withFiles(Arrays.asList(
                    MetadataRecord.builder().withFileType(ArgoFileType.METADATA).withFile("aoml/13857/13857_meta.nc").withFileName("13857_meta.nc").withFileStatus(FileStatus.ACTIVE).build(),
                    MetadataRecord.builder().withFileType(ArgoFileType.PROFILE_BIOCHEMICAL).withFile("aoml/13857/profiles/BD13857_003.nc").withFileName("BD13857_003.nc")
                        .withFileStatus(FileStatus.ACTIVE).build(),
                    MetadataRecord.builder().withFileType(ArgoFileType.PROFILE_CORE).withFile("aoml/13857/profiles/D13857_003.nc").withFileName("D13857_003.nc").withFileStatus(FileStatus.ACTIVE)
                        .build()
                ))
                .build(),
            ProfileOperation.builder()
                .withDac("aoml")
                .withFloatId("999")
                .withFiles(Arrays.asList(
                    MetadataRecord.builder().withFileType(ArgoFileType.METADATA).withFile("aoml/999/999_meta.nc").withFileName("999_meta.nc").withFileStatus(FileStatus.ACTIVE).build(),
                    MetadataRecord.builder().withFileType(ArgoFileType.PROFILE_BIOCHEMICAL).withFile("aoml/999/profiles/BD999_001.nc").withFileName("BD999_001.nc")
                        .withFileStatus(FileStatus.ACTIVE).build(),
                    MetadataRecord.builder().withFileType(ArgoFileType.PROFILE_CORE).withFile("aoml/999/profiles/D999_001.nc").withFileName("D999_001.nc").withFileStatus(FileStatus.ACTIVE)
                        .build()
                ))
                .build()
        ),
        page.getPage());

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/123/123_meta.nc")
        .withFileName("123_meta.nc")
        .withActionTimestamp(date)
        .withDac("aoml")
        .withFloatId("123")
        .withAction(Action.SYNTHETIC_MERGE)
        .withFileType(ArgoFileType.METADATA)
        .withRelatedFiles(Arrays.asList(
            "aoml/123/profiles/BD123_001.nc",
            "aoml/123/profiles/D123_001.nc"
        ))
        .build());

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/123/profiles/SD123_001.nc")
        .withFileName("SD123_001.nc")
        .withDac("aoml")
        .withFloatId("123")
        .withCycleNumber("001")
        .withDirection("A")
        .withParameterDataMode("D")
        .withActionTimestamp(date)
        .withDate(date)
        .withLatitude(0.267)
        .withLongitude(-16.032)
        .withOcean(ArgoOcean.ATLANTIC_OCEAN)
        .withProfilerType("845")
        .withInstitution("A0")
        .withDateUpdate(date)
        .withAction(Action.UPDATE)
        .withFileType(ArgoFileType.SYNTHETIC_PROFILE_SINGLE_CYCLE)
        .build());


    page = datastore.findUpdatedOrMissingSyntheticProfilesPage(DefaultIndexPageRequest.builder().withPageSize(10).build());
    assertEquals(Arrays.asList(
            ProfileOperation.builder()
                .withDac("aoml")
                .withFloatId("13857")
                .withFiles(Arrays.asList(
                    MetadataRecord.builder().withFileType(ArgoFileType.METADATA).withFile("aoml/13857/13857_meta.nc").withFileName("13857_meta.nc").withFileStatus(FileStatus.ACTIVE).build(),
                    MetadataRecord.builder().withFileType(ArgoFileType.PROFILE_BIOCHEMICAL).withFile("aoml/13857/profiles/BD13857_001.nc").withFileName("BD13857_001.nc")
                        .withFileStatus(FileStatus.ACTIVE).build(),
                    MetadataRecord.builder().withFileType(ArgoFileType.PROFILE_CORE).withFile("aoml/13857/profiles/D13857_001.nc").withFileName("D13857_001.nc").withFileStatus(FileStatus.ACTIVE)
                        .build()
                ))
                .build(),
            ProfileOperation.builder()
                .withDac("aoml")
                .withFloatId("13857")
                .withFiles(Arrays.asList(
                    MetadataRecord.builder().withFileType(ArgoFileType.METADATA).withFile("aoml/13857/13857_meta.nc").withFileName("13857_meta.nc").withFileStatus(FileStatus.ACTIVE).build(),
                    MetadataRecord.builder().withFileType(ArgoFileType.PROFILE_BIOCHEMICAL).withFile("aoml/13857/profiles/BD13857_002.nc").withFileName("BD13857_002.nc")
                        .withFileStatus(FileStatus.ACTIVE).build(),
                    MetadataRecord.builder().withFileType(ArgoFileType.PROFILE_CORE).withFile("aoml/13857/profiles/D13857_002.nc").withFileName("D13857_002.nc").withFileStatus(FileStatus.ACTIVE)
                        .build()
                ))
                .build(),
            ProfileOperation.builder()
                .withDac("aoml")
                .withFloatId("13857")
                .withFiles(Arrays.asList(
                    MetadataRecord.builder().withFileType(ArgoFileType.METADATA).withFile("aoml/13857/13857_meta.nc").withFileName("13857_meta.nc").withFileStatus(FileStatus.ACTIVE).build(),
                    MetadataRecord.builder().withFileType(ArgoFileType.PROFILE_BIOCHEMICAL).withFile("aoml/13857/profiles/BD13857_003.nc").withFileName("BD13857_003.nc")
                        .withFileStatus(FileStatus.ACTIVE).build(),
                    MetadataRecord.builder().withFileType(ArgoFileType.PROFILE_CORE).withFile("aoml/13857/profiles/D13857_003.nc").withFileName("D13857_003.nc").withFileStatus(FileStatus.ACTIVE)
                        .build()
                ))
                .build(),
            ProfileOperation.builder()
                .withDac("aoml")
                .withFloatId("999")
                .withFiles(Arrays.asList(
                    MetadataRecord.builder().withFileType(ArgoFileType.METADATA).withFile("aoml/999/999_meta.nc").withFileName("999_meta.nc").withFileStatus(FileStatus.ACTIVE).build(),
                    MetadataRecord.builder().withFileType(ArgoFileType.PROFILE_BIOCHEMICAL).withFile("aoml/999/profiles/BD999_001.nc").withFileName("BD999_001.nc")
                        .withFileStatus(FileStatus.ACTIVE).build(),
                    MetadataRecord.builder().withFileType(ArgoFileType.PROFILE_CORE).withFile("aoml/999/profiles/D999_001.nc").withFileName("D999_001.nc").withFileStatus(FileStatus.ACTIVE)
                        .build()
                ))
                .build()
        ),
        page.getPage());

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/13857/13857_meta.nc")
        .withFileName("13857_meta.nc")
        .withActionTimestamp(date)
        .withDac("aoml")
        .withFloatId("13857")
        .withAction(Action.SYNTHETIC_MERGE)
        .withFileType(ArgoFileType.METADATA)
        .withRelatedFiles(Arrays.asList(
            "aoml/13857/profiles/BD13857_003.nc",
            "aoml/13857/profiles/D13857_003.nc"
        ))
        .build());

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/13857/profiles/SD13857_003.nc")
        .withFileName("SD13857_003.nc")
        .withDac("aoml")
        .withFloatId("13857")
        .withCycleNumber("003")
        .withDirection("A")
        .withParameterDataMode("D")
        .withActionTimestamp(date)
        .withDate(date)
        .withLatitude(0.267)
        .withLongitude(-16.032)
        .withOcean(ArgoOcean.ATLANTIC_OCEAN)
        .withProfilerType("845")
        .withInstitution("A0")
        .withDateUpdate(date)
        .withAction(Action.UPDATE)
        .withFileType(ArgoFileType.SYNTHETIC_PROFILE_SINGLE_CYCLE)
        .build());

    page = datastore.findUpdatedOrMissingSyntheticProfilesPage(DefaultIndexPageRequest.builder().withPageSize(10).build());
    assertEquals(Arrays.asList(
            ProfileOperation.builder()
                .withDac("aoml")
                .withFloatId("13857")
                .withFiles(Arrays.asList(
                    MetadataRecord.builder().withFileType(ArgoFileType.METADATA).withFile("aoml/13857/13857_meta.nc").withFileName("13857_meta.nc").withFileStatus(FileStatus.ACTIVE).build(),
                    MetadataRecord.builder().withFileType(ArgoFileType.PROFILE_BIOCHEMICAL).withFile("aoml/13857/profiles/BD13857_001.nc").withFileName("BD13857_001.nc")
                        .withFileStatus(FileStatus.ACTIVE).build(),
                    MetadataRecord.builder().withFileType(ArgoFileType.PROFILE_CORE).withFile("aoml/13857/profiles/D13857_001.nc").withFileName("D13857_001.nc").withFileStatus(FileStatus.ACTIVE)
                        .build()
                ))
                .build(),
            ProfileOperation.builder()
                .withDac("aoml")
                .withFloatId("13857")
                .withFiles(Arrays.asList(
                    MetadataRecord.builder().withFileType(ArgoFileType.METADATA).withFile("aoml/13857/13857_meta.nc").withFileName("13857_meta.nc").withFileStatus(FileStatus.ACTIVE).build(),
                    MetadataRecord.builder().withFileType(ArgoFileType.PROFILE_BIOCHEMICAL).withFile("aoml/13857/profiles/BD13857_002.nc").withFileName("BD13857_002.nc")
                        .withFileStatus(FileStatus.ACTIVE).build(),
                    MetadataRecord.builder().withFileType(ArgoFileType.PROFILE_CORE).withFile("aoml/13857/profiles/D13857_002.nc").withFileName("D13857_002.nc").withFileStatus(FileStatus.ACTIVE)
                        .build()
                ))
                .build(),
            ProfileOperation.builder()
                .withDac("aoml")
                .withFloatId("999")
                .withFiles(Arrays.asList(
                    MetadataRecord.builder().withFileType(ArgoFileType.METADATA).withFile("aoml/999/999_meta.nc").withFileName("999_meta.nc").withFileStatus(FileStatus.ACTIVE).build(),
                    MetadataRecord.builder().withFileType(ArgoFileType.PROFILE_BIOCHEMICAL).withFile("aoml/999/profiles/BD999_001.nc").withFileName("BD999_001.nc")
                        .withFileStatus(FileStatus.ACTIVE).build(),
                    MetadataRecord.builder().withFileType(ArgoFileType.PROFILE_CORE).withFile("aoml/999/profiles/D999_001.nc").withFileName("D999_001.nc").withFileStatus(FileStatus.ACTIVE)
                        .build()
                ))
                .build()
        ),
        page.getPage());



    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/13857/13857_meta.nc")
        .withFileName("13857_meta.nc")
        .withActionTimestamp(date)
        .withDac("aoml")
        .withFloatId("13857")
        .withAction(Action.REMOVE)
        .withFileType(ArgoFileType.METADATA)
        .build());

    page = datastore.findUpdatedOrMissingSyntheticProfilesPage(DefaultIndexPageRequest.builder().withPageSize(10).build());
    assertEquals(Arrays.asList(
            ProfileOperation.builder()
                .withDac("aoml")
                .withFloatId("13857")
                .withFiles(Arrays.asList(
                    MetadataRecord.builder().withFileType(ArgoFileType.METADATA).withFile("aoml/13857/13857_meta.nc").withFileName("13857_meta.nc").withFileStatus(FileStatus.REMOVED).build(),
                    MetadataRecord.builder().withFileType(ArgoFileType.PROFILE_BIOCHEMICAL).withFile("aoml/13857/profiles/BD13857_003.nc").withFileName("BD13857_003.nc")
                        .withFileStatus(FileStatus.ACTIVE).build(),
                    MetadataRecord.builder().withFileType(ArgoFileType.PROFILE_CORE).withFile("aoml/13857/profiles/D13857_003.nc").withFileName("D13857_003.nc").withFileStatus(FileStatus.ACTIVE)
                        .build()
                ))
                .build(),
            ProfileOperation.builder()
                .withDac("aoml")
                .withFloatId("999")
                .withFiles(Arrays.asList(
                    MetadataRecord.builder().withFileType(ArgoFileType.METADATA).withFile("aoml/999/999_meta.nc").withFileName("999_meta.nc").withFileStatus(FileStatus.ACTIVE).build(),
                    MetadataRecord.builder().withFileType(ArgoFileType.PROFILE_BIOCHEMICAL).withFile("aoml/999/profiles/BD999_001.nc").withFileName("BD999_001.nc")
                        .withFileStatus(FileStatus.ACTIVE).build(),
                    MetadataRecord.builder().withFileType(ArgoFileType.PROFILE_CORE).withFile("aoml/999/profiles/D999_001.nc").withFileName("D999_001.nc").withFileStatus(FileStatus.ACTIVE)
                        .build()
                ))
                .build()
        ),
        page.getPage());


    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/123/profiles/BD123_001.nc")
        .withFileName("13857_meta.nc")
        .withActionTimestamp(date)
        .withDac("aoml")
        .withFloatId("123")
        .withAction(Action.REMOVE)
        .withFileType(ArgoFileType.PROFILE_BIOCHEMICAL)
        .build());


    page = datastore.findUpdatedOrMissingSyntheticProfilesPage(DefaultIndexPageRequest.builder().withPageSize(10).build());
    assertEquals(Arrays.asList(
            ProfileOperation.builder()
                .withDac("aoml")
                .withFloatId("123")
                .withFiles(Arrays.asList(
                    MetadataRecord.builder().withFileType(ArgoFileType.METADATA).withFile("aoml/123/123_meta.nc").withFileName("123_meta.nc").withFileStatus(FileStatus.ACTIVE).build(),
                    MetadataRecord.builder().withFileType(ArgoFileType.PROFILE_BIOCHEMICAL).withFile("aoml/123/profiles/BD123_001.nc").withFileName("BD123_001.nc")
                        .withFileStatus(FileStatus.REMOVED).build(),
                    MetadataRecord.builder().withFileType(ArgoFileType.PROFILE_CORE).withFile("aoml/123/profiles/D123_001.nc").withFileName("D123_001.nc").withFileStatus(FileStatus.ACTIVE)
                        .build()
                ))
                .build(),
            ProfileOperation.builder()
                .withDac("aoml")
                .withFloatId("13857")
                .withFiles(Arrays.asList(
                    MetadataRecord.builder().withFileType(ArgoFileType.METADATA).withFile("aoml/13857/13857_meta.nc").withFileName("13857_meta.nc").withFileStatus(FileStatus.REMOVED).build(),
                    MetadataRecord.builder().withFileType(ArgoFileType.PROFILE_BIOCHEMICAL).withFile("aoml/13857/profiles/BD13857_003.nc").withFileName("BD13857_003.nc")
                        .withFileStatus(FileStatus.ACTIVE).build(),
                    MetadataRecord.builder().withFileType(ArgoFileType.PROFILE_CORE).withFile("aoml/13857/profiles/D13857_003.nc").withFileName("D13857_003.nc").withFileStatus(FileStatus.ACTIVE)
                        .build()
                ))
                .build(),
            ProfileOperation.builder()
                .withDac("aoml")
                .withFloatId("999")
                .withFiles(Arrays.asList(
                    MetadataRecord.builder().withFileType(ArgoFileType.METADATA).withFile("aoml/999/999_meta.nc").withFileName("999_meta.nc").withFileStatus(FileStatus.ACTIVE).build(),
                    MetadataRecord.builder().withFileType(ArgoFileType.PROFILE_BIOCHEMICAL).withFile("aoml/999/profiles/BD999_001.nc").withFileName("BD999_001.nc")
                        .withFileStatus(FileStatus.ACTIVE).build(),
                    MetadataRecord.builder().withFileType(ArgoFileType.PROFILE_CORE).withFile("aoml/999/profiles/D999_001.nc").withFileName("D999_001.nc").withFileStatus(FileStatus.ACTIVE)
                        .build()
                ))
                .build()
        ),
        page.getPage());

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/999/999_meta.nc")
        .withFileName("999_meta.nc")
        .withActionTimestamp(date)
        .withDac("aoml")
        .withFloatId("999")
        .withAction(Action.SYNTHETIC_MERGE)
        .withFileType(ArgoFileType.METADATA)
        .withRelatedFiles(Arrays.asList(
            "aoml/999/profiles/BD999_001.nc",
            "aoml/999/profiles/D999_001.nc"
        ))
        .build());

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/999/profiles/SD999_001.nc")
        .withFileName("SD999_001.nc")
        .withDac("aoml")
        .withFloatId("999")
        .withCycleNumber("001")
        .withDirection("A")
        .withParameterDataMode("D")
        .withActionTimestamp(date)
        .withDate(date)
        .withLatitude(0.267)
        .withLongitude(-16.032)
        .withOcean(ArgoOcean.ATLANTIC_OCEAN)
        .withProfilerType("845")
        .withInstitution("A0")
        .withDateUpdate(date)
        .withAction(Action.UPDATE)
        .withFileType(ArgoFileType.SYNTHETIC_PROFILE_SINGLE_CYCLE)
        .build());

    page = datastore.findUpdatedOrMissingSyntheticProfilesPage(DefaultIndexPageRequest.builder().withPageSize(10).build());
    assertEquals(Arrays.asList(
            ProfileOperation.builder()
                .withDac("aoml")
                .withFloatId("123")
                .withFiles(Arrays.asList(
                    MetadataRecord.builder().withFileType(ArgoFileType.METADATA).withFile("aoml/123/123_meta.nc").withFileName("123_meta.nc").withFileStatus(FileStatus.ACTIVE).build(),
                    MetadataRecord.builder().withFileType(ArgoFileType.PROFILE_BIOCHEMICAL).withFile("aoml/123/profiles/BD123_001.nc").withFileName("BD123_001.nc")
                        .withFileStatus(FileStatus.REMOVED).build(),
                    MetadataRecord.builder().withFileType(ArgoFileType.PROFILE_CORE).withFile("aoml/123/profiles/D123_001.nc").withFileName("D123_001.nc").withFileStatus(FileStatus.ACTIVE)
                        .build()
                ))
                .build(),
            ProfileOperation.builder()
                .withDac("aoml")
                .withFloatId("13857")
                .withFiles(Arrays.asList(
                    MetadataRecord.builder().withFileType(ArgoFileType.METADATA).withFile("aoml/13857/13857_meta.nc").withFileName("13857_meta.nc").withFileStatus(FileStatus.REMOVED).build(),
                    MetadataRecord.builder().withFileType(ArgoFileType.PROFILE_BIOCHEMICAL).withFile("aoml/13857/profiles/BD13857_003.nc").withFileName("BD13857_003.nc")
                        .withFileStatus(FileStatus.ACTIVE).build(),
                    MetadataRecord.builder().withFileType(ArgoFileType.PROFILE_CORE).withFile("aoml/13857/profiles/D13857_003.nc").withFileName("D13857_003.nc").withFileStatus(FileStatus.ACTIVE)
                        .build()
                ))
                .build()
        ),
        page.getPage());


    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/999/999_meta.nc")
        .withFileName("999_meta.nc")
        .withDac("aoml")
        .withFloatId("999")
        .withActionTimestamp(date)
        .withDate(date)
        .withLatitude(0.267)
        .withLongitude(-16.032)
        .withOcean(ArgoOcean.ATLANTIC_OCEAN)
        .withProfilerType("845")
        .withInstitution("A0")
        .withDateUpdate(date)
        .withAction(Action.UPDATE)
        .withFileType(ArgoFileType.METADATA)
        .build());

    page = datastore.findUpdatedOrMissingSyntheticProfilesPage(DefaultIndexPageRequest.builder().withPageSize(10).build());
    assertEquals(Arrays.asList(
            ProfileOperation.builder()
                .withDac("aoml")
                .withFloatId("123")
                .withFiles(Arrays.asList(
                    MetadataRecord.builder().withFileType(ArgoFileType.METADATA).withFile("aoml/123/123_meta.nc").withFileName("123_meta.nc").withFileStatus(FileStatus.ACTIVE).build(),
                    MetadataRecord.builder().withFileType(ArgoFileType.PROFILE_BIOCHEMICAL).withFile("aoml/123/profiles/BD123_001.nc").withFileName("BD123_001.nc")
                        .withFileStatus(FileStatus.REMOVED).build(),
                    MetadataRecord.builder().withFileType(ArgoFileType.PROFILE_CORE).withFile("aoml/123/profiles/D123_001.nc").withFileName("D123_001.nc").withFileStatus(FileStatus.ACTIVE)
                        .build()
                ))
                .build(),
            ProfileOperation.builder()
                .withDac("aoml")
                .withFloatId("13857")
                .withFiles(Arrays.asList(
                    MetadataRecord.builder().withFileType(ArgoFileType.METADATA).withFile("aoml/13857/13857_meta.nc").withFileName("13857_meta.nc").withFileStatus(FileStatus.REMOVED).build(),
                    MetadataRecord.builder().withFileType(ArgoFileType.PROFILE_BIOCHEMICAL).withFile("aoml/13857/profiles/BD13857_003.nc").withFileName("BD13857_003.nc")
                        .withFileStatus(FileStatus.ACTIVE).build(),
                    MetadataRecord.builder().withFileType(ArgoFileType.PROFILE_CORE).withFile("aoml/13857/profiles/D13857_003.nc").withFileName("D13857_003.nc").withFileStatus(FileStatus.ACTIVE)
                        .build()
                ))
                .build(),
            ProfileOperation.builder()
                .withDac("aoml")
                .withFloatId("999")
                .withFiles(Arrays.asList(
                    MetadataRecord.builder().withFileType(ArgoFileType.METADATA).withFile("aoml/999/999_meta.nc").withFileName("999_meta.nc").withFileStatus(FileStatus.ACTIVE).build(),
                    MetadataRecord.builder().withFileType(ArgoFileType.PROFILE_BIOCHEMICAL).withFile("aoml/999/profiles/BD999_001.nc").withFileName("BD999_001.nc")
                        .withFileStatus(FileStatus.ACTIVE).build(),
                    MetadataRecord.builder().withFileType(ArgoFileType.PROFILE_CORE).withFile("aoml/999/profiles/D999_001.nc").withFileName("D999_001.nc").withFileStatus(FileStatus.ACTIVE)
                        .build()
                ))
                .build()
        ),
        page.getPage());


    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/123/123_meta.nc")
        .withFileName("123_meta.nc")
        .withActionTimestamp(date)
        .withDac("aoml")
        .withFloatId("123")
        .withAction(Action.SYNTHETIC_MERGE_REMOVE)
        .withFileType(ArgoFileType.METADATA)
        .withRelatedFiles(Arrays.asList(
            "aoml/123/profiles/BD123_001.nc",
            "aoml/123/profiles/D123_001.nc"
        ))
        .build());

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/123/profiles/SD123_001.nc")
        .withFileName("SD123_001.nc")
        .withDac("aoml")
        .withFloatId("123")
        .withCycleNumber("001")
        .withDirection("A")
        .withParameterDataMode("D")
        .withActionTimestamp(date)
        .withDate(date)
        .withLatitude(0.267)
        .withLongitude(-16.032)
        .withOcean(ArgoOcean.ATLANTIC_OCEAN)
        .withProfilerType("845")
        .withInstitution("A0")
        .withDateUpdate(date)
        .withAction(Action.REMOVE)
        .withFileType(ArgoFileType.SYNTHETIC_PROFILE_SINGLE_CYCLE)
        .build());

    assertFalse(datastore.findByFile("aoml/123/profiles/SD123_001.nc").isPresent());
    assertTrue(datastore.findByFile("aoml/123/profiles/SD123_001.nc", true).isPresent());


    page = datastore.findUpdatedOrMissingSyntheticProfilesPage(DefaultIndexPageRequest.builder().withPageSize(10).build());
    assertEquals(Arrays.asList(
            ProfileOperation.builder()
                .withDac("aoml")
                .withFloatId("13857")
                .withFiles(Arrays.asList(
                    MetadataRecord.builder().withFileType(ArgoFileType.METADATA).withFile("aoml/13857/13857_meta.nc").withFileName("13857_meta.nc").withFileStatus(FileStatus.REMOVED).build(),
                    MetadataRecord.builder().withFileType(ArgoFileType.PROFILE_BIOCHEMICAL).withFile("aoml/13857/profiles/BD13857_003.nc").withFileName("BD13857_003.nc")
                        .withFileStatus(FileStatus.ACTIVE).build(),
                    MetadataRecord.builder().withFileType(ArgoFileType.PROFILE_CORE).withFile("aoml/13857/profiles/D13857_003.nc").withFileName("D13857_003.nc").withFileStatus(FileStatus.ACTIVE)
                        .build()
                ))
                .build(),
            ProfileOperation.builder()
                .withDac("aoml")
                .withFloatId("999")
                .withFiles(Arrays.asList(
                    MetadataRecord.builder().withFileType(ArgoFileType.METADATA).withFile("aoml/999/999_meta.nc").withFileName("999_meta.nc").withFileStatus(FileStatus.ACTIVE).build(),
                    MetadataRecord.builder().withFileType(ArgoFileType.PROFILE_BIOCHEMICAL).withFile("aoml/999/profiles/BD999_001.nc").withFileName("BD999_001.nc")
                        .withFileStatus(FileStatus.ACTIVE).build(),
                    MetadataRecord.builder().withFileType(ArgoFileType.PROFILE_CORE).withFile("aoml/999/profiles/D999_001.nc").withFileName("D999_001.nc").withFileStatus(FileStatus.ACTIVE)
                        .build()
                ))
                .build()
        ),
        page.getPage());
  }

  @Test
  public void testRemoveMetadata() throws Exception {

    Instant date = Instant.now();

    // no synth

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/13857/13857_meta.nc")
        .withFileName("13857_meta.nc")
        .withDac("aoml")
        .withFloatId("13857")
        .withActionTimestamp(date)
        .withDate(date)
        .withLatitude(0.267)
        .withLongitude(-16.032)
        .withOcean(ArgoOcean.ATLANTIC_OCEAN)
        .withProfilerType("845")
        .withInstitution("A0")
        .withDateUpdate(date)
        .withAction(Action.UPDATE)
        .withFileType(ArgoFileType.METADATA)
        .build());

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/13857/profiles/D13857_001.nc")
        .withFileName("D13857_001.nc")
        .withDac("aoml")
        .withFloatId("13857")
        .withCycleNumber("001")
        .withDirection("A")
        .withParameterDataMode("D")
        .withActionTimestamp(date)
        .withDate(date)
        .withLatitude(0.267)
        .withLatitudeMin(0.1)
        .withLatitudeMax(0.4)
        .withLongitude(-16.032)
        .withLongitudeMin(-17.0)
        .withLongitudeMax(-14.0)
        .withOcean(ArgoOcean.ATLANTIC_OCEAN)
        .withProfilerType("845")
        .withInstitution("A0")
        .withDateUpdate(date)
        .withParameters("params")
        .withAction(Action.UPDATE)
        .withFileType(ArgoFileType.PROFILE_CORE)
        .build());

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/13857/profiles/BD13857_001.nc")
        .withFileName("BD13857_001.nc")
        .withDac("aoml")
        .withFloatId("13857")
        .withCycleNumber("001")
        .withDirection("A")
        .withParameterDataMode("D")
        .withActionTimestamp(date)
        .withDate(date)
        .withLatitude(0.267)
        .withLatitudeMin(0.1)
        .withLatitudeMax(0.4)
        .withLongitude(-16.032)
        .withLongitudeMin(-17.0)
        .withLongitudeMax(-14.0)
        .withOcean(ArgoOcean.ATLANTIC_OCEAN)
        .withProfilerType("845")
        .withInstitution("A0")
        .withDateUpdate(date)
        .withParameters("params")
        .withAction(Action.UPDATE)
        .withFileType(ArgoFileType.PROFILE_BIOCHEMICAL)
        .build());

    // no synth 2

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/123/123_meta.nc")
        .withFileName("123_meta.nc")
        .withDac("aoml")
        .withFloatId("123")
        .withActionTimestamp(date)
        .withDate(date)
        .withLatitude(0.267)
        .withLongitude(-16.032)
        .withOcean(ArgoOcean.ATLANTIC_OCEAN)
        .withProfilerType("845")
        .withInstitution("A0")
        .withDateUpdate(date)
        .withAction(Action.UPDATE)
        .withFileType(ArgoFileType.METADATA)
        .build());

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/123/profiles/D123_001.nc")
        .withFileName("D123_001.nc")
        .withDac("aoml")
        .withFloatId("123")
        .withCycleNumber("001")
        .withDirection("A")
        .withParameterDataMode("D")
        .withActionTimestamp(date)
        .withDate(date)
        .withLatitude(0.267)
        .withLatitudeMin(0.1)
        .withLatitudeMax(0.4)
        .withLongitude(-16.032)
        .withLongitudeMin(-17.0)
        .withLongitudeMax(-14.0)
        .withOcean(ArgoOcean.ATLANTIC_OCEAN)
        .withProfilerType("845")
        .withInstitution("A0")
        .withDateUpdate(date)
        .withParameters("params")
        .withAction(Action.UPDATE)
        .withFileType(ArgoFileType.PROFILE_CORE)
        .build());

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/123/profiles/BD123_001.nc")
        .withFileName("BD123_001.nc")
        .withDac("aoml")
        .withFloatId("123")
        .withCycleNumber("001")
        .withDirection("A")
        .withParameterDataMode("D")
        .withActionTimestamp(date)
        .withDate(date)
        .withLatitude(0.267)
        .withLatitudeMin(0.1)
        .withLatitudeMax(0.4)
        .withLongitude(-16.032)
        .withLongitudeMin(-17.0)
        .withLongitudeMax(-14.0)
        .withOcean(ArgoOcean.ATLANTIC_OCEAN)
        .withProfilerType("845")
        .withInstitution("A0")
        .withDateUpdate(date)
        .withParameters("params")
        .withAction(Action.UPDATE)
        .withFileType(ArgoFileType.PROFILE_BIOCHEMICAL)
        .build());

    // synth

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/13857/profiles/D13857_002.nc")
        .withFileName("D13857_002.nc")
        .withDac("aoml")
        .withFloatId("13857")
        .withCycleNumber("002")
        .withDirection("A")
        .withParameterDataMode("D")
        .withActionTimestamp(date)
        .withDate(date)
        .withLatitude(0.267)
        .withLatitudeMin(0.1)
        .withLatitudeMax(0.4)
        .withLongitude(-16.032)
        .withLongitudeMin(-17.0)
        .withLongitudeMax(-14.0)
        .withOcean(ArgoOcean.ATLANTIC_OCEAN)
        .withProfilerType("845")
        .withInstitution("A0")
        .withDateUpdate(date)
        .withParameters("params")
        .withAction(Action.UPDATE)
        .withFileType(ArgoFileType.PROFILE_CORE)
        .build());

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/13857/profiles/BD13857_002.nc")
        .withFileName("BD13857_002.nc")
        .withDac("aoml")
        .withFloatId("13857")
        .withCycleNumber("002")
        .withDirection("A")
        .withParameterDataMode("D")
        .withActionTimestamp(date)
        .withDate(date)
        .withLatitude(0.267)
        .withLatitudeMin(0.1)
        .withLatitudeMax(0.4)
        .withLongitude(-16.032)
        .withLongitudeMin(-17.0)
        .withLongitudeMax(-14.0)
        .withOcean(ArgoOcean.ATLANTIC_OCEAN)
        .withProfilerType("845")
        .withInstitution("A0")
        .withDateUpdate(date)
        .withParameters("params")
        .withAction(Action.UPDATE)
        .withFileType(ArgoFileType.PROFILE_BIOCHEMICAL)
        .build());

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/13857/13857_meta.nc")
        .withFileName("13857_meta.nc")
        .withActionTimestamp(date)
        .withDac("aoml")
        .withFloatId("13857")
        .withAction(Action.SYNTHETIC_MERGE)
        .withFileType(ArgoFileType.METADATA)
        .withRelatedFiles(Arrays.asList(
            "aoml/13857/profiles/D13857_002.nc",
            "aoml/13857/profiles/BD13857_002.nc"
        ))
        .build());


    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/13857/profiles/SD13857_002.nc")
        .withFileName("SD13857_002.nc")
        .withDac("aoml")
        .withFloatId("13857")
        .withCycleNumber("002")
        .withDirection("A")
        .withParameterDataMode("D")
        .withActionTimestamp(date)
        .withDate(date)
        .withLatitude(0.267)
        .withLongitude(-16.032)
        .withOcean(ArgoOcean.ATLANTIC_OCEAN)
        .withProfilerType("845")
        .withInstitution("A0")
        .withDateUpdate(date)
        .withAction(Action.UPDATE)
        .withFileType(ArgoFileType.SYNTHETIC_PROFILE_SINGLE_CYCLE)
        .build());

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/13855/13855_meta.nc")
        .withFileName("13855_meta.nc")
        .withDac("aoml")
        .withFloatId("13855")
        .withActionTimestamp(date)
        .withDate(date)
        .withLatitude(0.267)
        .withLongitude(-16.032)
        .withOcean(ArgoOcean.ATLANTIC_OCEAN)
        .withProfilerType("845")
        .withInstitution("A0")
        .withDateUpdate(date)
        .withAction(Action.UPDATE)
        .withFileType(ArgoFileType.METADATA)
        .build());

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/13855/profiles/D13855_001.nc")
        .withFileName("D13855_001.nc")
        .withDac("aoml")
        .withFloatId("13855")
        .withCycleNumber("001")
        .withDirection("A")
        .withParameterDataMode("D")
        .withActionTimestamp(date)
        .withDate(date)
        .withLatitude(0.267)
        .withLatitudeMin(0.1)
        .withLatitudeMax(0.4)
        .withLongitude(-16.032)
        .withLongitudeMin(-17.0)
        .withLongitudeMax(-14.0)
        .withOcean(ArgoOcean.ATLANTIC_OCEAN)
        .withProfilerType("845")
        .withInstitution("A0")
        .withDateUpdate(date)
        .withParameters("params")
        .withAction(Action.UPDATE)
        .withFileType(ArgoFileType.PROFILE_CORE)
        .build());

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/13855/profiles/BD13855_001.nc")
        .withFileName("BD13855_001.nc")
        .withDac("aoml")
        .withFloatId("13855")
        .withCycleNumber("001")
        .withDirection("A")
        .withParameterDataMode("D")
        .withActionTimestamp(date)
        .withDate(date)
        .withLatitude(0.267)
        .withLatitudeMin(0.1)
        .withLatitudeMax(0.4)
        .withLongitude(-16.032)
        .withLongitudeMin(-17.0)
        .withLongitudeMax(-14.0)
        .withOcean(ArgoOcean.ATLANTIC_OCEAN)
        .withProfilerType("845")
        .withInstitution("A0")
        .withDateUpdate(date)
        .withParameters("params")
        .withAction(Action.UPDATE)
        .withFileType(ArgoFileType.PROFILE_BIOCHEMICAL)
        .build());

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/13855/13855_meta.nc")
        .withFileName("D13855_001.nc")
        .withActionTimestamp(date)
        .withAction(Action.SYNTHETIC_MERGE)
        .withFileType(ArgoFileType.METADATA)
        .withDac("aoml")
        .withFloatId("13855")
        .withRelatedFiles(Arrays.asList(
            "aoml/13855/profiles/BD13855_001.nc",
            "aoml/13855/profiles/D13855_001.nc"
        ))
        .build());


    ProfilePage page = datastore.findUpdatedOrMissingSyntheticProfilesPage(DefaultIndexPageRequest.builder().withPageSize(100).build());
    assertEquals(Arrays.asList(ProfileOperation.builder()
                .withDac("aoml")
                .withFloatId("123")
                .withFiles(Arrays.asList(
                    MetadataRecord.builder().withFileType(ArgoFileType.METADATA).withFile("aoml/123/123_meta.nc").withFileName("123_meta.nc").withFileStatus(FileStatus.ACTIVE).build(),
                    MetadataRecord.builder().withFileType(ArgoFileType.PROFILE_BIOCHEMICAL).withFile("aoml/123/profiles/BD123_001.nc").withFileName("BD123_001.nc").withFileStatus(FileStatus.ACTIVE)
                        .build(),
                    MetadataRecord.builder().withFileType(ArgoFileType.PROFILE_CORE).withFile("aoml/123/profiles/D123_001.nc").withFileName("D123_001.nc").withFileStatus(FileStatus.ACTIVE).build()
                ))
                .build(),
            ProfileOperation.builder()
                .withDac("aoml")
                .withFloatId("13857")
                .withFiles(Arrays.asList(
                    MetadataRecord.builder().withFileType(ArgoFileType.METADATA).withFile("aoml/13857/13857_meta.nc").withFileName("13857_meta.nc").withFileStatus(FileStatus.ACTIVE).build(),
                    MetadataRecord.builder().withFileType(ArgoFileType.PROFILE_BIOCHEMICAL).withFile("aoml/13857/profiles/BD13857_001.nc").withFileName("BD13857_001.nc")
                        .withFileStatus(FileStatus.ACTIVE).build(),
                    MetadataRecord.builder().withFileType(ArgoFileType.PROFILE_CORE).withFile("aoml/13857/profiles/D13857_001.nc").withFileName("D13857_001.nc").withFileStatus(FileStatus.ACTIVE)
                        .build()
                ))
                .build()
        ),
        page.getPage());

    //MetadataRecord{file='aoml/13857/13857_meta.nc', date=2026-05-14T13:48:57.457848Z, latitude=0.267, latitudeMin=null, latitudeMax=null, longitude=-16.032, longitudeMin=null, longitudeMax=null, ocean=ATLANTIC_OCEAN, profilerType='845', institution='A0', dateUpdate=2026-05-14T13:48:57.457848Z, parameters='null', parameterDataMode='null', direction='null', cycleNumber='null', action=null, fileType=METADATA, fileStatus=ACTIVE, dac='aoml', floatId='13857', actionTimestamp=2026-05-14T13:48:57.723361Z, otherFields={}}

    assertEquals(MetadataRecord.builder()
        .withFile("aoml/13857/13857_meta.nc")
        .withFileName("13857_meta.nc")
        .withDac("aoml")
        .withFloatId("13857")
        .withActionTimestamp(date)
        .withDate(date)
        .withLatitude(0.267)
        .withLongitude(-16.032)
        .withOcean(ArgoOcean.ATLANTIC_OCEAN)
        .withProfilerType("845")
        .withInstitution("A0")
        .withDateUpdate(date)
        .withFileType(ArgoFileType.METADATA)
        .withFileStatus(FileStatus.ACTIVE)
        .build(), datastore.findByFile("aoml/13857/13857_meta.nc").get());

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/13857/13857_meta.nc")
        .withFileName("13857_meta.nc")
        .withActionTimestamp(date)
        .withDac("aoml")
        .withFloatId("13857")
        .withAction(Action.REMOVE)
        .withFileType(ArgoFileType.METADATA)
        .build());

    page = datastore.findUpdatedOrMissingSyntheticProfilesPage(DefaultIndexPageRequest.builder().withPageSize(100).build());
    assertEquals(Arrays.asList(ProfileOperation.builder()
                .withDac("aoml")
                .withFloatId("123")
                .withFiles(Arrays.asList(
                    MetadataRecord.builder().withFileType(ArgoFileType.METADATA).withFile("aoml/123/123_meta.nc").withFileName("123_meta.nc").withFileStatus(FileStatus.ACTIVE).build(),
                    MetadataRecord.builder().withFileType(ArgoFileType.PROFILE_BIOCHEMICAL).withFile("aoml/123/profiles/BD123_001.nc").withFileName("BD123_001.nc").withFileStatus(FileStatus.ACTIVE)
                        .build(),
                    MetadataRecord.builder().withFileType(ArgoFileType.PROFILE_CORE).withFile("aoml/123/profiles/D123_001.nc").withFileName("D123_001.nc").withFileStatus(FileStatus.ACTIVE).build()
                ))
                .build(),
            ProfileOperation.builder()
                .withDac("aoml")
                .withFloatId("13857")
                .withFiles(Arrays.asList(
                    MetadataRecord.builder().withFileType(ArgoFileType.METADATA).withFile("aoml/13857/13857_meta.nc").withFileName("13857_meta.nc").withFileStatus(FileStatus.REMOVED)
                        .build(),
                    MetadataRecord.builder().withFileType(ArgoFileType.PROFILE_BIOCHEMICAL).withFile("aoml/13857/profiles/BD13857_002.nc").withFileName("BD13857_002.nc")
                        .withFileStatus(FileStatus.ACTIVE).build(),
                    MetadataRecord.builder().withFileType(ArgoFileType.PROFILE_CORE).withFile("aoml/13857/profiles/D13857_002.nc").withFileName("D13857_002.nc").withFileStatus(FileStatus.ACTIVE)
                        .build()
                ))
                .build()
        ),
        page.getPage());

    assertEquals(MetadataRecord.builder()
        .withFile("aoml/13857/13857_meta.nc")
        .withFileName("13857_meta.nc")
        .withDac("aoml")
        .withFloatId("13857")
        .withActionTimestamp(date)
        .withDate(date)
        .withLatitude(0.267)
        .withLongitude(-16.032)
        .withOcean(ArgoOcean.ATLANTIC_OCEAN)
        .withProfilerType("845")
        .withInstitution("A0")
        .withDateUpdate(date)
        .withFileType(ArgoFileType.METADATA)
        .withFileStatus(FileStatus.REMOVED)
        .build(), datastore.findByFile("aoml/13857/13857_meta.nc", true).get());

    assertFalse(datastore.findByFile("aoml/13857/13857_meta.nc").isPresent());
  }

  @Test
  public void testFindUpdatedOrMissingMergeFilesPage() throws Exception {

    Instant date = Instant.now();

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/13857/profiles/D13857_001.nc")
        .withFileName("D13857_001.nc")
        .withDac("aoml")
        .withFloatId("13857")
        .withCycleNumber("001")
        .withDirection("A")
        .withParameterDataMode("D")
        .withActionTimestamp(date)
        .withDate(date)
        .withLatitude(0.267)
        .withLatitudeMin(0.1)
        .withLatitudeMax(0.4)
        .withLongitude(-16.032)
        .withLongitudeMin(-17.0)
        .withLongitudeMax(-14.0)
        .withOcean(ArgoOcean.ATLANTIC_OCEAN)
        .withProfilerType("845")
        .withInstitution("A0")
        .withDateUpdate(date)
        .withParameters("params")
        .withAction(Action.UPDATE)
        .withFileType(ArgoFileType.PROFILE_CORE)
        .build());

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/13857/profiles/D13857_002.nc")
        .withFileName("D13857_002.nc")
        .withDac("aoml")
        .withFloatId("13857")
        .withCycleNumber("002")
        .withDirection("A")
        .withParameterDataMode("D")
        .withActionTimestamp(date)
        .withDate(date)
        .withLatitude(0.267)
        .withLatitudeMin(0.1)
        .withLatitudeMax(0.4)
        .withLongitude(-16.032)
        .withLongitudeMin(-17.0)
        .withLongitudeMax(-14.0)
        .withOcean(ArgoOcean.ATLANTIC_OCEAN)
        .withProfilerType("845")
        .withInstitution("A0")
        .withDateUpdate(date)
        .withParameters("params")
        .withAction(Action.UPDATE)
        .withFileType(ArgoFileType.PROFILE_CORE)
        .build());

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/123/profiles/D123_001.nc")
        .withFileName("D123_001.nc")
        .withDac("aoml")
        .withFloatId("123")
        .withCycleNumber("001")
        .withDirection("A")
        .withParameterDataMode("D")
        .withActionTimestamp(date)
        .withDate(date)
        .withLatitude(0.267)
        .withLatitudeMin(0.1)
        .withLatitudeMax(0.4)
        .withLongitude(-16.032)
        .withLongitudeMin(-17.0)
        .withLongitudeMax(-14.0)
        .withOcean(ArgoOcean.ATLANTIC_OCEAN)
        .withProfilerType("845")
        .withInstitution("A0")
        .withDateUpdate(date)
        .withParameters("params")
        .withAction(Action.UPDATE)
        .withFileType(ArgoFileType.PROFILE_CORE)
        .build());

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/123/profiles/BD123_001.nc")
        .withFileName("BD123_001.nc")
        .withDac("aoml")
        .withFloatId("123")
        .withCycleNumber("001")
        .withDirection("A")
        .withParameterDataMode("D")
        .withActionTimestamp(date)
        .withDate(date)
        .withLatitude(0.267)
        .withLatitudeMin(0.1)
        .withLatitudeMax(0.4)
        .withLongitude(-16.032)
        .withLongitudeMin(-17.0)
        .withLongitudeMax(-14.0)
        .withOcean(ArgoOcean.ATLANTIC_OCEAN)
        .withProfilerType("845")
        .withInstitution("A0")
        .withDateUpdate(date)
        .withParameters("params")
        .withAction(Action.UPDATE)
        .withFileType(ArgoFileType.PROFILE_BIOCHEMICAL)
        .build());

    ProfilePage page1 = datastore.findUpdatedOrMissingMergeFilesPage(DefaultIndexPageRequest.builder().withPageSize(1).build());
    assertEquals(2, page1.getTotalRecords());
    assertEquals(2, page1.getTotalPages());
    assertEquals(1, page1.getPageSize());
    assertEquals(1, page1.getPageNumber());
    assertEquals(Collections.singletonList(ProfileOperation.builder()
            .withDac("aoml")
            .withFloatId("123")
            .withFiles(Arrays.asList(
                MetadataRecord.builder().withFile("aoml/123/profiles/D123_001.nc").withFileName("D123_001.nc").withFileStatus(FileStatus.ACTIVE).build()
            ))
            .build()),
        page1.getPage());

    ProfilePage page2 = datastore.findUpdatedOrMissingMergeFilesPage(page1.getNextPage().get());
    assertEquals(2, page2.getTotalRecords());
    assertEquals(2, page2.getTotalPages());
    assertEquals(1, page2.getPageSize());
    assertEquals(2, page2.getPageNumber());
    assertEquals(Collections.singletonList(ProfileOperation.builder()
            .withDac("aoml")
            .withFloatId("13857")
            .withFiles(Arrays.asList(
                MetadataRecord.builder().withFile("aoml/13857/profiles/D13857_001.nc").withFileName("D13857_001.nc").withFileStatus(FileStatus.ACTIVE)
                    .build(),
                MetadataRecord.builder().withFile("aoml/13857/profiles/D13857_002.nc").withFileName("D13857_002.nc").withFileStatus(FileStatus.ACTIVE)
                    .build()
            ))
            .build()),
        page2.getPage());

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/13857/profiles/D13857_001.nc")
        .withFileName("D13857_001.nc")
        .withActionTimestamp(date)
        .withDac("aoml")
        .withFloatId("13857")
        .withAction(Action.FLOAT_MERGE)
        .withFileType(ArgoFileType.METADATA)
        .build());

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/13857/profiles/D13857_002.nc")
        .withFileName("D13857_002.nc")
        .withActionTimestamp(date)
        .withDac("aoml")
        .withFloatId("13857")
        .withAction(Action.FLOAT_MERGE)
        .withFileType(ArgoFileType.METADATA)
        .build());

    page1 = datastore.findUpdatedOrMissingMergeFilesPage(DefaultIndexPageRequest.builder().withPageSize(1).build());
    assertEquals(2, page1.getTotalRecords());
    assertEquals(2, page1.getTotalPages());
    assertEquals(1, page1.getPageSize());
    assertEquals(1, page1.getPageNumber());
    assertEquals(Collections.singletonList(ProfileOperation.builder()
            .withDac("aoml")
            .withFloatId("123")
            .withFiles(Arrays.asList(
                MetadataRecord.builder().withFile("aoml/123/profiles/D123_001.nc").withFileName("D123_001.nc").withFileStatus(FileStatus.ACTIVE).build()
            ))
            .build()),
        page1.getPage());

  }


  @Test
  public void testFindRemovedMergeFilesPage() throws Exception {

    Instant date = Instant.now();

    // saved file, not merged - should be in results
    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/13857/profiles/D13857_001.nc")
        .withFileName("D13857_001.nc")
        .withDac("aoml")
        .withFloatId("13857")
        .withCycleNumber("001")
        .withDirection("A")
        .withParameterDataMode("D")
        .withActionTimestamp(date)
        .withDate(date)
        .withLatitude(0.267)
        .withLatitudeMin(0.1)
        .withLatitudeMax(0.4)
        .withLongitude(-16.032)
        .withLongitudeMin(-17.0)
        .withLongitudeMax(-14.0)
        .withOcean(ArgoOcean.ATLANTIC_OCEAN)
        .withProfilerType("845")
        .withInstitution("A0")
        .withDateUpdate(date)
        .withParameters("params")
        .withAction(Action.UPDATE)
        .withFileType(ArgoFileType.PROFILE_CORE)
        .build());

    // removed without merge timestamp should not be in results
    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/13857/profiles/D13857_002.nc")
        .withFileName("D13857_002.nc")
        .withDac("aoml")
        .withFloatId("13857")
        .withCycleNumber("002")
        .withDirection("A")
        .withParameterDataMode("D")
        .withActionTimestamp(date)
        .withDate(date)
        .withLatitude(0.267)
        .withLatitudeMin(0.1)
        .withLatitudeMax(0.4)
        .withLongitude(-16.032)
        .withLongitudeMin(-17.0)
        .withLongitudeMax(-14.0)
        .withOcean(ArgoOcean.ATLANTIC_OCEAN)
        .withProfilerType("845")
        .withInstitution("A0")
        .withDateUpdate(date)
        .withParameters("params")
        .withAction(Action.UPDATE)
        .withFileType(ArgoFileType.PROFILE_CORE)
        .build());
    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/13857/profiles/D13857_002.nc")
        .withFileName("D13857_002.nc")
        .withActionTimestamp(date)
        .withDac("aoml")
        .withFloatId("13857")
        .withAction(Action.REMOVE)
        .withFileType(ArgoFileType.PROFILE_CORE)
        .build());

    // saved file, merged - should not be in results
    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/123/profiles/D123_001.nc")
        .withFileName("D123_001.nc")
        .withDac("aoml")
        .withFloatId("123")
        .withCycleNumber("001")
        .withDirection("A")
        .withParameterDataMode("D")
        .withActionTimestamp(date)
        .withDate(date)
        .withLatitude(0.267)
        .withLatitudeMin(0.1)
        .withLatitudeMax(0.4)
        .withLongitude(-16.032)
        .withLongitudeMin(-17.0)
        .withLongitudeMax(-14.0)
        .withOcean(ArgoOcean.ATLANTIC_OCEAN)
        .withProfilerType("845")
        .withInstitution("A0")
        .withDateUpdate(date)
        .withParameters("params")
        .withAction(Action.UPDATE)
        .withFileType(ArgoFileType.PROFILE_CORE)
        .build());

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/123/profiles/D123_001.nc")
        .withFileName("D123_001.nc")
        .withActionTimestamp(date)
        .withDac("aoml")
        .withFloatId("123")
        .withAction(Action.FLOAT_MERGE)
        .withFileType(ArgoFileType.PROFILE_CORE)
        .build());

    // removed file, merged - should be in results
    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/1111/profiles/D1111_001.nc")
        .withFileName("D1111_001.nc")
        .withDac("aoml")
        .withFloatId("1111")
        .withCycleNumber("001")
        .withDirection("A")
        .withParameterDataMode("D")
        .withActionTimestamp(date)
        .withDate(date)
        .withLatitude(0.267)
        .withLatitudeMin(0.1)
        .withLatitudeMax(0.4)
        .withLongitude(-16.032)
        .withLongitudeMin(-17.0)
        .withLongitudeMax(-14.0)
        .withOcean(ArgoOcean.ATLANTIC_OCEAN)
        .withProfilerType("845")
        .withInstitution("A0")
        .withDateUpdate(date)
        .withParameters("params")
        .withAction(Action.UPDATE)
        .withFileType(ArgoFileType.PROFILE_CORE)
        .build());

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/1111/profiles/D1111_001.nc")
        .withFileName("D1111_001.nc")
        .withActionTimestamp(date)
        .withDac("aoml")
        .withFloatId("1111")
        .withAction(Action.FLOAT_MERGE)
        .withFileType(ArgoFileType.PROFILE_CORE)
        .build());

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/1111/profiles/D1111_001.nc")
        .withFileName("D1111_001.nc")
        .withActionTimestamp(date)
        .withDac("aoml")
        .withFloatId("1111")
        .withAction(Action.REMOVE)
        .withFileType(ArgoFileType.PROFILE_CORE)
        .build());

    // removed file, not merged - should not be in results

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/2222/profiles/D2222_001.nc")
        .withFileName("D2222_001.nc")
        .withDac("aoml")
        .withFloatId("2222")
        .withCycleNumber("001")
        .withDirection("A")
        .withParameterDataMode("D")
        .withActionTimestamp(date)
        .withDate(date)
        .withLatitude(0.267)
        .withLatitudeMin(0.1)
        .withLatitudeMax(0.4)
        .withLongitude(-16.032)
        .withLongitudeMin(-17.0)
        .withLongitudeMax(-14.0)
        .withOcean(ArgoOcean.ATLANTIC_OCEAN)
        .withProfilerType("845")
        .withInstitution("A0")
        .withDateUpdate(date)
        .withParameters("params")
        .withAction(Action.UPDATE)
        .withFileType(ArgoFileType.PROFILE_CORE)
        .build());

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/2222/profiles/D2222_001.nc")
        .withFileName("D2222_001.nc")
        .withActionTimestamp(date)
        .withDac("aoml")
        .withFloatId("2222")
        .withAction(Action.REMOVE)
        .withFileType(ArgoFileType.PROFILE_CORE)
        .build());

    ProfilePage page1 = datastore.findUpdatedOrMissingMergeFilesPage(DefaultIndexPageRequest.builder().withPageSize(100).build());
    assertEquals(
        Arrays.asList(
            ProfileOperation.builder().withDac("aoml")
                .withFloatId("1111")
                .withFiles(Arrays.asList(
                    MetadataRecord.builder().withFile("aoml/1111/profiles/D1111_001.nc").withFileName("D1111_001.nc")
                        .withFileStatus(FileStatus.REMOVED).build()
                ))
                .build(),
            ProfileOperation.builder()
                .withDac("aoml")
                .withFloatId("13857")
                .withFiles(Arrays.asList(
                    MetadataRecord.builder().withFile("aoml/13857/profiles/D13857_001.nc").withFileName("D13857_001.nc")
                        .withFileStatus(FileStatus.ACTIVE).build()
                ))
                .build()
        ),
        page1.getPage());

    // simulate callback from merge processor

    UUID traceId = UUID.randomUUID();

    datastore.updateIndex(MetadataRecord.builder()
        .withTraceId(traceId)
        .withFileName("D13857_001.nc")
        .withFile("aoml/13857/profiles/D13857_001.nc")
        .withDac("aoml")
        .withFloatId("13857")
        .withAction(Action.FLOAT_MERGE)
        .withFileType(ArgoFileType.PROFILE_CORE)
        .withActionTimestamp(Instant.now())
        .build());

    datastore.updateIndex(MetadataRecord.builder()
        .withTraceId(traceId)
        .withFileName("D1111_001.nc")
        .withFile("aoml/1111/profiles/D1111_001.nc")
        .withDac("aoml")
        .withFloatId("1111")
        .withAction(Action.FLOAT_MERGE_REMOVE)
        .withFileType(ArgoFileType.PROFILE_CORE)
        .withActionTimestamp(Instant.now())
        .build());

    datastore.updateIndex(MetadataRecord.builder()
        .withTraceId(traceId)
        .withActionTimestamp(Instant.now())
        .withFileName("13857_prof.nc")
        .withFile("aoml/13857/13857_prof.nc")
        .withDate(Instant.now())
        .withDateUpdate(Instant.now())
        .withAction(Action.UPDATE)
        .withDac("aoml")
        .withFloatId("13857")
        .withFileType(ArgoFileType.PROFILE_MULTI_CYCLE)
        .build());

    datastore.updateIndex(MetadataRecord.builder()
        .withTraceId(traceId)
        .withActionTimestamp(Instant.now())
        .withFileName("1111_prof.nc")
        .withFile("aoml/1111/1111_prof.nc")
        .withDate(Instant.now())
        .withDateUpdate(Instant.now())
        .withAction(Action.REMOVE)
        .withDac("aoml")
        .withFloatId("1111")
        .withFileType(ArgoFileType.PROFILE_MULTI_CYCLE)
        .build());

    page1 = datastore.findUpdatedOrMissingMergeFilesPage(DefaultIndexPageRequest.builder().withPageSize(100).build());
    assertEquals(0, page1.getTotalRecords());

    try (EntityManager em = emf.createEntityManager()) {
      EntityTransaction tx = em.getTransaction();
      tx.begin();
      try {
        assertEquals(FileStatus.REMOVED.toString(), em.find(ProfileMergeFileEntity.class, "aoml/1111/1111_prof.nc").getFileStatus());
        assertEquals(FileStatus.ACTIVE.toString(), em.find(ProfileMergeFileEntity.class, "aoml/13857/13857_prof.nc").getFileStatus());

        ProfileFileEntity d13857 = em.find(ProfileFileEntity.class, "aoml/13857/profiles/D13857_001.nc");
        assertEquals(FileStatus.ACTIVE.toString(), d13857.getFileStatus());
        assertNotNull(d13857.getMultiFloatMergeTime());

        ProfileFileEntity d1111 = em.find(ProfileFileEntity.class, "aoml/1111/profiles/D1111_001.nc");
        assertEquals(FileStatus.REMOVED.toString(), d1111.getFileStatus());
        assertNull(d1111.getMultiFloatMergeTime());

        tx.commit();
      } catch (Exception e) {
        tx.rollback();
        throw e;
      }
    }


  }

  @Test
  public void testFindUpdatedOrMissingGeoMergeFilesPage() throws Exception {

    Instant date = Instant.parse("2020-01-02T00:00:00.00Z");
    Instant now = Instant.now();

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/13857/profiles/D13857_001.nc")
        .withFileName("D13857_001.nc")
        .withDac("aoml")
        .withFloatId("13857")
        .withCycleNumber("001")
        .withDirection("A")
        .withParameterDataMode("D")
        .withActionTimestamp(now)
        .withDate(date)
        .withLatitude(0.267)
        .withLatitudeMin(0.1)
        .withLatitudeMax(0.4)
        .withLongitude(-16.032)
        .withLongitudeMin(-17.0)
        .withLongitudeMax(-14.0)
        .withOcean(ArgoOcean.INDIAN_OCEAN)
        .withProfilerType("845")
        .withInstitution("A0")
        .withDateUpdate(now)
        .withParameters("params")
        .withAction(Action.UPDATE)
        .withFileType(ArgoFileType.PROFILE_CORE)
        .build());

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/13857/profiles/D13857_002.nc")
        .withFileName("D13857_002.nc")
        .withDac("aoml")
        .withFloatId("13857")
        .withCycleNumber("002")
        .withDirection("A")
        .withParameterDataMode("D")
        .withActionTimestamp(now)
        .withDate(date)
        .withLatitude(0.267)
        .withLatitudeMin(0.1)
        .withLatitudeMax(0.4)
        .withLongitude(-16.032)
        .withLongitudeMin(-17.0)
        .withLongitudeMax(-14.0)
        .withOcean(ArgoOcean.INDIAN_OCEAN)
        .withProfilerType("845")
        .withInstitution("A0")
        .withDateUpdate(now)
        .withParameters("params")
        .withAction(Action.UPDATE)
        .withFileType(ArgoFileType.PROFILE_CORE)
        .build());

    // removed without geo merge timestamp should not be in results
    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/13857/profiles/D13857_003.nc")
        .withFileName("D13857_003.nc")
        .withDac("aoml")
        .withFloatId("13857")
        .withCycleNumber("003")
        .withDirection("A")
        .withParameterDataMode("D")
        .withActionTimestamp(now)
        .withDate(date)
        .withLatitude(0.267)
        .withLatitudeMin(0.1)
        .withLatitudeMax(0.4)
        .withLongitude(-16.032)
        .withLongitudeMin(-17.0)
        .withLongitudeMax(-14.0)
        .withOcean(ArgoOcean.INDIAN_OCEAN)
        .withProfilerType("845")
        .withInstitution("A0")
        .withDateUpdate(now)
        .withParameters("params")
        .withAction(Action.UPDATE)
        .withFileType(ArgoFileType.PROFILE_CORE)
        .build());
    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/13857/profiles/D13857_003.nc")
        .withFileName("D13857_003.nc")
        .withActionTimestamp(date)
        .withDac("aoml")
        .withFloatId("13857")
        .withAction(Action.REMOVE)
        .withFileType(ArgoFileType.PROFILE_CORE)
        .build());

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/123/profiles/D123_001.nc")
        .withFileName("D123_001.nc")
        .withDac("aoml")
        .withFloatId("123")
        .withCycleNumber("001")
        .withDirection("A")
        .withParameterDataMode("D")
        .withActionTimestamp(now)
        .withDate(date)
        .withLatitude(0.267)
        .withLatitudeMin(0.1)
        .withLatitudeMax(0.4)
        .withLongitude(-16.032)
        .withLongitudeMin(-17.0)
        .withLongitudeMax(-14.0)
        .withOcean(ArgoOcean.ATLANTIC_OCEAN)
        .withProfilerType("845")
        .withInstitution("A0")
        .withDateUpdate(now)
        .withParameters("params")
        .withAction(Action.UPDATE)
        .withFileType(ArgoFileType.PROFILE_CORE)
        .build());

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/123/profiles/BD123_001.nc")
        .withFileName("BD123_001.nc")
        .withDac("aoml")
        .withFloatId("123")
        .withCycleNumber("001")
        .withDirection("A")
        .withParameterDataMode("D")
        .withActionTimestamp(now)
        .withDate(date)
        .withLatitude(0.267)
        .withLatitudeMin(0.1)
        .withLatitudeMax(0.4)
        .withLongitude(-16.032)
        .withLongitudeMin(-17.0)
        .withLongitudeMax(-14.0)
        .withOcean(ArgoOcean.ATLANTIC_OCEAN)
        .withProfilerType("845")
        .withInstitution("A0")
        .withDateUpdate(now)
        .withParameters("params")
        .withAction(Action.UPDATE)
        .withFileType(ArgoFileType.PROFILE_BIOCHEMICAL)
        .build());

    GeoMergePage page1 = datastore.findUpdatedOrMissingGeoMergeFilesPage(DefaultIndexPageRequest.builder().withPageSize(1).build());
    assertEquals(2, page1.getTotalRecords());
    assertEquals(2, page1.getTotalPages());
    assertEquals(1, page1.getPageSize());
    assertEquals(1, page1.getPageNumber());
    assertEquals(Collections.singletonList(GeoMergeInfo.builder()
            .withYear(2020)
            .withMonth(1)
            .withDay(2)
            .withOcean(ArgoOcean.ATLANTIC_OCEAN)
            .withFiles(Arrays.asList(
                MetadataRecord.builder()
                    .withFileName("D123_001.nc")
                    .withFileStatus(FileStatus.ACTIVE)
                    .withFile("aoml/123/profiles/D123_001.nc")
                    .withDac("aoml")
                    .withFloatId("123")
                    .build()
            ))
            .build()),
        page1.getPage());

    GeoMergePage page2 = datastore.findUpdatedOrMissingGeoMergeFilesPage(page1.getNextPage().get());
    assertEquals(2, page2.getTotalRecords());
    assertEquals(2, page2.getTotalPages());
    assertEquals(1, page2.getPageSize());
    assertEquals(2, page2.getPageNumber());
    assertEquals(Collections.singletonList(GeoMergeInfo.builder()
            .withYear(2020)
            .withMonth(1)
            .withDay(2)
            .withOcean(ArgoOcean.INDIAN_OCEAN)
            .withFiles(Arrays.asList(
                MetadataRecord.builder()
                    .withFileName("D13857_001.nc")
                    .withFileStatus(FileStatus.ACTIVE)
                    .withFile("aoml/13857/profiles/D13857_001.nc")
                    .withDac("aoml")
                    .withFloatId("13857")
                    .build(),
                MetadataRecord.builder()
                    .withFileName("D13857_002.nc")
                    .withFileStatus(FileStatus.ACTIVE)
                    .withFile("aoml/13857/profiles/D13857_002.nc")
                    .withDac("aoml")
                    .withFloatId("13857")
                    .build()
            ))
            .build()),
        page2.getPage());

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/13857/profiles/D13857_001.nc")
        .withFileName("D13857_001.nc")
        .withActionTimestamp(date)
        .withDac("aoml")
        .withFloatId("13857")
        .withAction(Action.GEO_MERGE)
        .withFileType(ArgoFileType.METADATA)
        .build());

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/13857/profiles/D13857_002.nc")
        .withFileName("D13857_002.nc")
        .withActionTimestamp(date)
        .withDac("aoml")
        .withFloatId("13857")
        .withAction(Action.GEO_MERGE)
        .withFileType(ArgoFileType.METADATA)
        .build());

    page1 = datastore.findUpdatedOrMissingGeoMergeFilesPage(DefaultIndexPageRequest.builder().withPageSize(1).build());
    assertEquals(2, page1.getTotalRecords());
    assertEquals(2, page1.getTotalPages());
    assertEquals(1, page1.getPageSize());
    assertEquals(1, page1.getPageNumber());
    assertEquals(Collections.singletonList(GeoMergeInfo.builder()
            .withYear(2020)
            .withMonth(1)
            .withDay(2)
            .withOcean(ArgoOcean.ATLANTIC_OCEAN)
            .withFiles(Arrays.asList(
                MetadataRecord.builder()
                    .withFileName("D123_001.nc")
                    .withFileStatus(FileStatus.ACTIVE)
                    .withFile("aoml/123/profiles/D123_001.nc")
                    .withDac("aoml")
                    .withFloatId("123")
                    .build()
            ))
            .build()),
        page1.getPage());

  }

  @Test
  public void testFindRemovedGeoMergeFilesPage() throws Exception {

    // saved file, not merged - should be in results
    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/13857/profiles/D13857_001.nc")
        .withFileName("D13857_001.nc")
        .withDac("aoml")
        .withFloatId("13857")
        .withCycleNumber("001")
        .withDirection("A")
        .withParameterDataMode("D")
        .withActionTimestamp(Instant.now())
        .withDate(Instant.parse("2026-05-01T00:00:00Z"))
        .withLatitude(0.267)
        .withLatitudeMin(0.1)
        .withLatitudeMax(0.4)
        .withLongitude(-16.032)
        .withLongitudeMin(-17.0)
        .withLongitudeMax(-14.0)
        .withOcean(ArgoOcean.ATLANTIC_OCEAN)
        .withProfilerType("845")
        .withInstitution("A0")
        .withDateUpdate(Instant.now())
        .withParameters("params")
        .withAction(Action.UPDATE)
        .withFileType(ArgoFileType.PROFILE_CORE)
        .build());

    // saved file, merged - should not be in results
    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/123/profiles/D123_001.nc")
        .withFileName("D123_001.nc")
        .withDac("aoml")
        .withFloatId("123")
        .withCycleNumber("001")
        .withDirection("A")
        .withParameterDataMode("D")
        .withActionTimestamp(Instant.now())
        .withDate(Instant.parse("2026-05-02T00:00:00Z"))
        .withLatitude(0.267)
        .withLatitudeMin(0.1)
        .withLatitudeMax(0.4)
        .withLongitude(-16.032)
        .withLongitudeMin(-17.0)
        .withLongitudeMax(-14.0)
        .withOcean(ArgoOcean.ATLANTIC_OCEAN)
        .withProfilerType("845")
        .withInstitution("A0")
        .withDateUpdate(Instant.now())
        .withParameters("params")
        .withAction(Action.UPDATE)
        .withFileType(ArgoFileType.PROFILE_CORE)
        .build());

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/123/profiles/D123_001.nc")
        .withFileName("D123_001.nc")
        .withActionTimestamp(Instant.now())
        .withDac("aoml")
        .withFloatId("123")
        .withAction(Action.GEO_MERGE)
        .withFileType(ArgoFileType.PROFILE_CORE)
        .build());

    // removed file, merged - should be in results
    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/1111/profiles/D1111_001.nc")
        .withFileName("D1111_001.nc")
        .withDac("aoml")
        .withFloatId("1111")
        .withCycleNumber("001")
        .withDirection("A")
        .withParameterDataMode("D")
        .withActionTimestamp(Instant.now())
        .withDate(Instant.parse("2026-05-03T00:00:00Z"))
        .withLatitude(0.267)
        .withLatitudeMin(0.1)
        .withLatitudeMax(0.4)
        .withLongitude(-16.032)
        .withLongitudeMin(-17.0)
        .withLongitudeMax(-14.0)
        .withOcean(ArgoOcean.INDIAN_OCEAN)
        .withProfilerType("845")
        .withInstitution("A0")
        .withDateUpdate(Instant.now())
        .withParameters("params")
        .withAction(Action.UPDATE)
        .withFileType(ArgoFileType.PROFILE_CORE)
        .build());

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/1111/profiles/D1111_001.nc")
        .withFileName("D1111_001.nc")
        .withActionTimestamp(Instant.now())
        .withDac("aoml")
        .withFloatId("1111")
        .withAction(Action.GEO_MERGE)
        .withFileType(ArgoFileType.PROFILE_CORE)
        .build());

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/1111/profiles/D1111_001.nc")
        .withFileName("D1111_001.nc")
        .withActionTimestamp(Instant.now())
        .withDac("aoml")
        .withFloatId("1111")
        .withAction(Action.REMOVE)
        .withFileType(ArgoFileType.PROFILE_CORE)
        .build());

    // removed file, not merged - should not be in results

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/2222/profiles/D2222_001.nc")
        .withFileName("D2222_001.nc")
        .withDac("aoml")
        .withFloatId("2222")
        .withCycleNumber("001")
        .withDirection("A")
        .withParameterDataMode("D")
        .withActionTimestamp(Instant.now())
        .withDate(Instant.parse("2026-05-04T00:00:00Z"))
        .withLatitude(0.267)
        .withLatitudeMin(0.1)
        .withLatitudeMax(0.4)
        .withLongitude(-16.032)
        .withLongitudeMin(-17.0)
        .withLongitudeMax(-14.0)
        .withOcean(ArgoOcean.ATLANTIC_OCEAN)
        .withProfilerType("845")
        .withInstitution("A0")
        .withDateUpdate(Instant.now())
        .withParameters("params")
        .withAction(Action.UPDATE)
        .withFileType(ArgoFileType.PROFILE_CORE)
        .build());

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/2222/profiles/D2222_001.nc")
        .withFileName("D2222_001.nc")
        .withActionTimestamp(Instant.now())
        .withDac("aoml")
        .withFloatId("2222")
        .withAction(Action.REMOVE)
        .withFileType(ArgoFileType.PROFILE_CORE)
        .build());

    GeoMergePage page1 = datastore.findUpdatedOrMissingGeoMergeFilesPage(DefaultIndexPageRequest.builder().withPageSize(100).build());
    assertEquals(
        Arrays.asList(
            GeoMergeInfo.builder()
                .withYear(2026)
                .withMonth(5)
                .withDay(1)
                .withOcean(ArgoOcean.ATLANTIC_OCEAN)
                .withFiles(Arrays.asList(
                    MetadataRecord.builder()
                        .withFileName("D13857_001.nc")
                        .withFileStatus(FileStatus.ACTIVE)
                        .withDac("aoml")
                        .withFloatId("13857")
                        .withFile("aoml/13857/profiles/D13857_001.nc")
                        .build()
                ))
                .build(),
            GeoMergeInfo.builder()
                .withYear(2026)
                .withMonth(5)
                .withDay(3)
                .withOcean(ArgoOcean.INDIAN_OCEAN)
                .withFiles(Arrays.asList(
                    MetadataRecord.builder()
                        .withFileName("D1111_001.nc")
                        .withFileStatus(FileStatus.REMOVED)
                        .withDac("aoml")
                        .withFloatId("1111")
                        .withFile("aoml/1111/profiles/D1111_001.nc")
                        .build()
                ))
                .build()
        ),
        page1.getPage());

    // simulate callback from merge processor

    UUID traceId = UUID.randomUUID();

    datastore.updateIndex(MetadataRecord.builder()
        .withTraceId(traceId)
        .withFileName("D13857_001.nc")
        .withFile("aoml/13857/profiles/D13857_001.nc")
        .withDac("aoml")
        .withFloatId("13857")
        .withAction(Action.GEO_MERGE)
        .withFileType(ArgoFileType.PROFILE_CORE)
        .withActionTimestamp(Instant.now())
        .build());

    datastore.updateIndex(MetadataRecord.builder()
        .withTraceId(traceId)
        .withFileName("D1111_001.nc")
        .withFile("aoml/1111/profiles/D1111_001.nc")
        .withDac("aoml")
        .withFloatId("1111")
        .withAction(Action.GEO_MERGE_REMOVE)
        .withFileType(ArgoFileType.PROFILE_CORE)
        .withActionTimestamp(Instant.now())
        .build());

    page1 = datastore.findUpdatedOrMissingGeoMergeFilesPage(DefaultIndexPageRequest.builder().withPageSize(100).build());
    assertEquals(0, page1.getTotalRecords());

    try (EntityManager em = emf.createEntityManager()) {
      EntityTransaction tx = em.getTransaction();
      tx.begin();
      try {

        ProfileFileEntity d13857 = em.find(ProfileFileEntity.class, "aoml/13857/profiles/D13857_001.nc");
        assertEquals(FileStatus.ACTIVE.toString(), d13857.getFileStatus());
        assertNotNull(d13857.getGeoMergeTime());

        ProfileFileEntity d1111 = em.find(ProfileFileEntity.class, "aoml/1111/profiles/D1111_001.nc");
        assertEquals(FileStatus.REMOVED.toString(), d1111.getFileStatus());
        assertNull(d1111.getGeoMergeTime());

        tx.commit();
      } catch (Exception e) {
        tx.rollback();
        throw e;
      }
    }


  }


  @Test
  public void testFindRemovedFilesPage() throws Exception {
    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/13857/profiles/D13857_001.nc")
        .withFileName("D13857_001.nc")
        .withDac("aoml")
        .withFloatId("13857")
        .withCycleNumber("001")
        .withDirection("A")
        .withParameterDataMode("D")
        .withActionTimestamp(Instant.now())
        .withDate(Instant.parse("2026-05-01T00:00:00Z"))
        .withLatitude(0.267)
        .withLatitudeMin(0.1)
        .withLatitudeMax(0.4)
        .withLongitude(-16.032)
        .withLongitudeMin(-17.0)
        .withLongitudeMax(-14.0)
        .withOcean(ArgoOcean.ATLANTIC_OCEAN)
        .withProfilerType("845")
        .withInstitution("A0")
        .withDateUpdate(Instant.now())
        .withParameters("params")
        .withAction(Action.UPDATE)
        .withFileType(ArgoFileType.PROFILE_CORE)
        .build());

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/13857/profiles/D13857_002.nc")
        .withFileName("D13857_002.nc")
        .withDac("aoml")
        .withFloatId("13857")
        .withCycleNumber("002")
        .withDirection("A")
        .withParameterDataMode("D")
        .withActionTimestamp(Instant.now())
        .withDate(Instant.parse("2026-05-01T00:00:00Z"))
        .withLatitude(0.267)
        .withLatitudeMin(0.1)
        .withLatitudeMax(0.4)
        .withLongitude(-16.032)
        .withLongitudeMin(-17.0)
        .withLongitudeMax(-14.0)
        .withOcean(ArgoOcean.ATLANTIC_OCEAN)
        .withProfilerType("845")
        .withInstitution("A0")
        .withDateUpdate(Instant.now())
        .withParameters("params")
        .withAction(Action.UPDATE)
        .withFileType(ArgoFileType.PROFILE_CORE)
        .build());

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/13857/13857_meta.nc")
        .withFileName("13857_meta.nc")
        .withDac("aoml")
        .withFloatId("13857")
        .withActionTimestamp(Instant.now())
        .withDate(Instant.now())
        .withLatitude(0.267)
        .withLongitude(-16.032)
        .withOcean(ArgoOcean.ATLANTIC_OCEAN)
        .withProfilerType("845")
        .withInstitution("A0")
        .withDateUpdate(Instant.now())
        .withAction(Action.UPDATE)
        .withFileType(ArgoFileType.METADATA)
        .build());

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/13855/13855_meta.nc")
        .withFileName("13855_meta.nc")
        .withDac("aoml")
        .withFloatId("13855")
        .withActionTimestamp(Instant.now())
        .withDate(Instant.now())
        .withLatitude(0.267)
        .withLongitude(-16.032)
        .withOcean(ArgoOcean.ATLANTIC_OCEAN)
        .withProfilerType("845")
        .withInstitution("A0")
        .withDateUpdate(Instant.now())
        .withAction(Action.UPDATE)
        .withFileType(ArgoFileType.METADATA)
        .build());

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/13855/profiles/BD13855_001.nc")
        .withFileName("BD13855_001.nc")
        .withDac("aoml")
        .withFloatId("13855")
        .withCycleNumber("001")
        .withDirection("A")
        .withParameterDataMode("D")
        .withActionTimestamp(Instant.now())
        .withDate(Instant.now())
        .withLatitude(0.267)
        .withLatitudeMin(0.1)
        .withLatitudeMax(0.4)
        .withLongitude(-16.032)
        .withLongitudeMin(-17.0)
        .withLongitudeMax(-14.0)
        .withOcean(ArgoOcean.ATLANTIC_OCEAN)
        .withProfilerType("845")
        .withInstitution("A0")
        .withDateUpdate(Instant.now())
        .withParameters("params")
        .withAction(Action.UPDATE)
        .withFileType(ArgoFileType.PROFILE_BIOCHEMICAL)
        .build());

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/13855/profiles/BD13855_002.nc")
        .withFileName("BD13855_002.nc")
        .withDac("aoml")
        .withFloatId("13855")
        .withCycleNumber("002")
        .withDirection("A")
        .withParameterDataMode("D")
        .withActionTimestamp(Instant.now())
        .withDate(Instant.now())
        .withLatitude(0.267)
        .withLatitudeMin(0.1)
        .withLatitudeMax(0.4)
        .withLongitude(-16.032)
        .withLongitudeMin(-17.0)
        .withLongitudeMax(-14.0)
        .withOcean(ArgoOcean.ATLANTIC_OCEAN)
        .withProfilerType("845")
        .withInstitution("A0")
        .withDateUpdate(Instant.now())
        .withParameters("params")
        .withAction(Action.UPDATE)
        .withFileType(ArgoFileType.PROFILE_BIOCHEMICAL)
        .build());


    RemovedFilePage page = datastore.findRemovedFilesPage(DefaultRemovedFileSearch.builder().withPageSize(100).build());
    assertEquals(1, page.getPageNumber());
    assertEquals(0, page.getTotalRecords());
    assertEquals(0, page.getPage().size());



    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/13857/profiles/D13857_001.nc")
        .withFileName("D13857_001.nc")
        .withDac("aoml")
        .withFloatId("13857")
        .withCycleNumber("001")
        .withActionTimestamp(Instant.parse("2025-10-10T12:00:00Z"))
        .withAction(Action.REMOVE)
        .withFileType(ArgoFileType.PROFILE_CORE)
        .build());

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/13857/13857_meta.nc")
        .withFileName("13857_meta.nc")
        .withDac("aoml")
        .withFloatId("13857")
        .withActionTimestamp(Instant.parse("2025-10-10T13:00:00Z"))
        .withAction(Action.REMOVE)
        .withFileType(ArgoFileType.METADATA)
        .build());

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/13855/profiles/BD13855_001.nc")
        .withFileName("BD13855_001.nc")
        .withDac("aoml")
        .withFloatId("13855")
        .withCycleNumber("001")
        .withActionTimestamp(Instant.parse("2025-10-10T14:00:00Z"))
        .withAction(Action.REMOVE)
        .withFileType(ArgoFileType.PROFILE_BIOCHEMICAL)
        .build());


    page = datastore.findRemovedFilesPage(DefaultRemovedFileSearch.builder().withPageSize(1).withPageNumber(1).build());
    assertEquals(3, page.getTotalRecords());
    assertEquals(1, page.getPageNumber());
    assertEquals(1, page.getPageSize());


    assertEquals(Arrays.asList(ProfileOperation.builder()
                .withDac("aoml")
                .withFloatId("13855")
                .withFiles(Collections.singletonList(
                    MetadataRecord.builder()
                        .withFileType(ArgoFileType.PROFILE_BIOCHEMICAL)
                        .withFile("aoml/13855/profiles/BD13855_001.nc")
                        .withFileName("BD13855_001.nc")
                        .withFileStatus(FileStatus.REMOVED)
                        .withActionTimestamp(Instant.parse("2025-10-10T14:00:00Z"))
                        .build()
                ))
                .build()
        ),
        page.getPage());


    page = datastore.findRemovedFilesPage(DefaultRemovedFileSearch.builder().withPageSize(1).withPageNumber(2).build());
    assertEquals(3, page.getTotalRecords());
    assertEquals(2, page.getPageNumber());
    assertEquals(1, page.getPageSize());


    assertEquals(Arrays.asList(
            ProfileOperation.builder()
                .withDac("aoml")
                .withFloatId("13857")
                .withFiles(Collections.singletonList(
                    MetadataRecord.builder()
                        .withFileType(ArgoFileType.PROFILE_CORE)
                        .withFile("aoml/13857/profiles/D13857_001.nc")
                        .withFileName("D13857_001.nc")
                        .withFileStatus(FileStatus.REMOVED)
                        .withActionTimestamp(Instant.parse("2025-10-10T12:00:00Z"))
                        .build()
                ))
                .build()
        ),
        page.getPage());

    page = datastore.findRemovedFilesPage(DefaultRemovedFileSearch.builder().withPageSize(1).withPageNumber(3).build());
    assertEquals(3, page.getTotalRecords());
    assertEquals(3, page.getPageNumber());
    assertEquals(1, page.getPageSize());

    assertEquals(Arrays.asList(
            ProfileOperation.builder()
                .withDac("aoml")
                .withFloatId("13857")
                .withFiles(Collections.singletonList(
                    MetadataRecord.builder()
                        .withFileType(ArgoFileType.METADATA)
                        .withFile("aoml/13857/13857_meta.nc")
                        .withFileName("13857_meta.nc")
                        .withFileStatus(FileStatus.REMOVED)
                        .withActionTimestamp(Instant.parse("2025-10-10T13:00:00Z"))
                        .build()
                ))
                .build()
        ),
        page.getPage());

    page = datastore.findRemovedFilesPage(DefaultRemovedFileSearch.builder().withForFileTypes(Collections.singletonList(ArgoFileType.METADATA)).withPageSize(100).build());

    assertEquals(Arrays.asList(
            ProfileOperation.builder()
                .withDac("aoml")
                .withFloatId("13857")
                .withFiles(Collections.singletonList(
                    MetadataRecord.builder()
                        .withFileType(ArgoFileType.METADATA)
                        .withFile("aoml/13857/13857_meta.nc")
                        .withFileName("13857_meta.nc")
                        .withFileStatus(FileStatus.REMOVED)
                        .withActionTimestamp(Instant.parse("2025-10-10T13:00:00Z"))
                        .build()
                ))
                .build()
        ),
        page.getPage());

    page = datastore.findRemovedFilesPage(DefaultRemovedFileSearch.builder().withOlderThan(Instant.parse("2025-10-10T13:00:00Z")).withPageSize(100).build());

    assertEquals(Arrays.asList(
            ProfileOperation.builder()
                .withDac("aoml")
                .withFloatId("13857")
                .withFiles(Collections.singletonList(
                    MetadataRecord.builder()
                        .withFileType(ArgoFileType.PROFILE_CORE)
                        .withFile("aoml/13857/profiles/D13857_001.nc")
                        .withFileName("D13857_001.nc")
                        .withFileStatus(FileStatus.REMOVED)
                        .withActionTimestamp(Instant.parse("2025-10-10T12:00:00Z"))
                        .build()
                ))
                .build()
        ),
        page.getPage());
  }

}