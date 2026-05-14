package edu.colorado.cires.argonaut.metadata.jpa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import edu.colorado.cires.argonaut.messaging.core.databind.ArgoOcean;
import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord;
import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord.Action;
import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord.FileStatus;
import edu.colorado.cires.argonaut.messaging.core.databind.NcSubmissionMessage.FileType;
import edu.colorado.cires.argonaut.messaging.core.databind.ProfileOperation;
import edu.colorado.cires.argonaut.metadata.core.DefaultIndexPageRequest;
import edu.colorado.cires.argonaut.metadata.core.ProfilePage;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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
        em.createQuery("delete from IndexEntity").executeUpdate();
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
        .withFileType(FileType.CORE_ARGO_PROFILE)
        .build();
    datastore.updateIndex(record1);
    Instant date2 = date1.plusSeconds(1);
    Instant dateUpdate2 = dateUpdate1.plusSeconds(1);
    MetadataRecord record2 = MetadataRecord.builder()
        .withFile("aoml/13857/profiles/D13857_002.nc")
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
        .withFileType(FileType.CORE_ARGO_PROFILE)
        .build();
    datastore.updateIndex(record2);
    Instant date3 = date1.plusSeconds(1);
    Instant dateUpdate3 = dateUpdate1.plusSeconds(1);
    MetadataRecord record3 = MetadataRecord.builder()
        .withFile("aoml/13857/profiles/D13857_003.nc")
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
        .withFileType(FileType.CORE_ARGO_PROFILE)
        .build();
    datastore.updateIndex(record3);
    datastore.updateIndex(MetadataRecord.builder()
        .withFileType(FileType.CORE_ARGO_PROFILE)
        .withFile("aoml/13857/profiles/D13857_003.nc")
        .withAction(Action.REMOVE)
        .build());

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("nnic/13857/profiles/D13857_003.nc")
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
        .withFileType(FileType.CORE_ARGO_PROFILE)
        .build());

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/13858/profiles/D13858_003.nc")
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
        .withFileType(FileType.CORE_ARGO_PROFILE)
        .build());

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("nnic/1000/profiles/D1000_001.nc")
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
        .withFileType(FileType.CORE_ARGO_PROFILE)
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

    //TODO
//    MetadataRecordPage page = datastore.findProfilePage("13857", "aoml", DefaultIndexPageRequest.builder().withPageSize(2).build());
//    DefaultMetadataRecordPage expected = DefaultMetadataRecordPage.builder()
//        .withIndexPageRequest(DefaultIndexPageRequest.builder().withPageSize(2).build())
//        .withTotalRecords(3L)
//        .withPage(Arrays.asList(
//            MetadataRecord.builder(record12).withAction(null).withFileStatus(FileStatus.ACTIVE).build(),
//            MetadataRecord.builder(record2).withAction(null).withFileStatus(FileStatus.ACTIVE).build()))
//        .build();
//    assertEquals(expected, page);
//    assertEquals(
//        DefaultMetadataRecordPage.builder()
//            .withIndexPageRequest(DefaultIndexPageRequest.builder().withPageNumber(2).withPageSize(2).build())
//            .withTotalRecords(3)
//            .build(),
//        page.getNextPage().get());
//
//    page = datastore.findProfilePage("13857", "aoml", page.getNextPage().get());
//    expected = DefaultMetadataRecordPage.builder()
//        .withIndexPageRequest(DefaultIndexPageRequest.builder().withPageNumber(2).withPageSize(2).build())
//        .withTotalRecords(3L)
//        .withPage(Arrays.asList(
//            MetadataRecord.builder(record3).withAction(null).withFileStatus(FileStatus.REMOVED).build()))
//        .build();
//    assertEquals(expected, page);
//    assertFalse(page.getNextPage().isPresent());

  }


  @Test
  @Disabled
  public void testFloatMergeCycle() throws Exception {
    throw  new UnsupportedOperationException("Float merge not yet implemented");
//    Instant now = Instant.now();
//
//    MetadataRecord d13859_001 = MetadataRecord.builder()
//        .withFile("aoml/13859/profiles/D13859_001.nc")
//        .withDate(now)
//        .withLatitude(0.32)
//        .withLatitudeMin(0d)
//        .withLatitudeMax(1d)
//        .withLongitude(-16d)
//        .withLongitudeMin(-17d)
//        .withLongitudeMax(-14d)
//        .withOcean(ArgoOcean.INDIAN_OCEAN)
//        .withProfilerType("847")
//        .withInstitution("A2")
//        .withDateUpdate(now)
//        .withParameters("params3")
//        .withParameterDataMode("mode3")
//        .withAction(Action.UPDATE)
//        .withFileType(FileType.CORE_ARGO_PROFILE)
//        .withDac("aoml")
//        .withFloatId("13858")
//        .build();
//
//    // create
//    datastore.updateIndex(d13859_001);
//
//    assertEquals(
//        MetadataRecord.builder(d13859_001)
//            .withFileStatus(FileStatus.ACTIVE)
//            .withFloatMerged(false)
//            .withAction(null)
//            .build(),
//        datastore.findByFile(d13859_001.getFile()).get());
//
//    // update float merged flag
//    datastore.updateIndex(MetadataRecord.builder()
//        .withFileStatus(FileStatus.ACTIVE)
//        .withFile(d13859_001.getFile())
//        .withAction(Action.FLOAT_MERGE)
//        .build()
//    );
//
//    assertEquals(
//        MetadataRecord.builder(d13859_001)
//            .withFileStatus(FileStatus.ACTIVE)
//            .withFloatMerged(true)
//            .withAction(null)
//            .build(),
//        datastore.findByFile(d13859_001.getFile()).get());
//
//    // remove
//    datastore.updateIndex(MetadataRecord.builder()
//        .withFile(d13859_001.getFile())
//        .withAction(Action.REMOVE)
//        .build()
//    );
//
//    assertEquals(
//        MetadataRecord.builder(d13859_001)
//            .withFileStatus(FileStatus.REMOVED)
//            .withFloatMerged(true)
//            .withAction(null)
//            .build(),
//        datastore.findByFile(d13859_001.getFile()).get());
//
//    // update float merged flag on removed file
//    datastore.updateIndex(MetadataRecord.builder(d13859_001)
//        .withFileStatus(FileStatus.REMOVED)
//        .withAction(Action.FLOAT_MERGE)
//        .build()
//    );
//
//    assertEquals(
//        MetadataRecord.builder(d13859_001)
//            .withFileStatus(FileStatus.REMOVED)
//            .withFloatMerged(false)
//            .withAction(null)
//            .build(),
//        datastore.findByFile(d13859_001.getFile()).get());
  }

  @Test
  @Disabled
  public void findUpdatedOrMissingMergeFilesPage() throws Exception {
    throw  new UnsupportedOperationException("Float merge not yet implemented");
//    Instant now = Instant.now();
//
//    MetadataRecord activeNotMerged = MetadataRecord.builder()
//        .withFile("aoml/13857/profiles/D13857_001.nc")
//        .withDate(now)
//        .withLatitude(0.32)
//        .withLatitudeMin(0d)
//        .withLatitudeMax(1d)
//        .withLongitude(-16d)
//        .withLongitudeMin(-17d)
//        .withLongitudeMax(-14d)
//        .withOcean(ArgoOcean.INDIAN_OCEAN)
//        .withProfilerType("847")
//        .withInstitution("A2")
//        .withDateUpdate(now)
//        .withParameters("params3")
//        .withAction(Action.UPDATE)
//        .withFileType(FileType.CORE_ARGO_PROFILE)
//        .withDac("aoml")
//        .withFloatId("13857")
//        .build();
//
//    datastore.updateIndex(activeNotMerged);
//
//    MetadataRecord activeNotMerged2 = MetadataRecord.builder()
//        .withFile("aoml/13857/profiles/D13857_003.nc")
//        .withDate(now)
//        .withLatitude(0.32)
//        .withLatitudeMin(0d)
//        .withLatitudeMax(1d)
//        .withLongitude(-16d)
//        .withLongitudeMin(-17d)
//        .withLongitudeMax(-14d)
//        .withOcean(ArgoOcean.INDIAN_OCEAN)
//        .withProfilerType("847")
//        .withInstitution("A2")
//        .withDateUpdate(now)
//        .withParameters("params3")
//        .withAction(Action.UPDATE)
//        .withFileType(FileType.CORE_ARGO_PROFILE)
//        .withDac("aoml")
//        .withFloatId("13857")
//        .build();
//
//    datastore.updateIndex(activeNotMerged2);
//
//    MetadataRecord activeMerged = MetadataRecord.builder()
//        .withFile("aoml/13857/profiles/D13857_002.nc")
//        .withDate(now)
//        .withLatitude(0.32)
//        .withLatitudeMin(0d)
//        .withLatitudeMax(1d)
//        .withLongitude(-16d)
//        .withLongitudeMin(-17d)
//        .withLongitudeMax(-14d)
//        .withOcean(ArgoOcean.INDIAN_OCEAN)
//        .withProfilerType("847")
//        .withInstitution("A2")
//        .withDateUpdate(now)
//        .withParameters("params3")
//        .withAction(Action.UPDATE)
//        .withFileType(FileType.CORE_ARGO_PROFILE)
//        .withDac("aoml")
//        .withFloatId("13857")
//        .build();
//
//    datastore.updateIndex(activeNotMerged);
//
//    datastore.updateIndex(MetadataRecord.builder()
//        .withFileStatus(FileStatus.ACTIVE)
//        .withFile(activeMerged.getFile())
//        .withAction(Action.FLOAT_MERGE)
//        .build()
//    );
//
//    MetadataRecord removedNotMerged = MetadataRecord.builder()
//        .withFile("aoml/13858/profiles/D13858_001.nc")
//        .withDate(now)
//        .withLatitude(0.32)
//        .withLatitudeMin(0d)
//        .withLatitudeMax(1d)
//        .withLongitude(-16d)
//        .withLongitudeMin(-17d)
//        .withLongitudeMax(-14d)
//        .withOcean(ArgoOcean.INDIAN_OCEAN)
//        .withProfilerType("847")
//        .withInstitution("A2")
//        .withDateUpdate(now)
//        .withParameters("params3")
//        .withAction(Action.UPDATE)
//        .withFileType(FileType.CORE_ARGO_PROFILE)
//        .withDac("aoml")
//        .withFloatId("13858")
//        .build();
//
//    datastore.updateIndex(removedNotMerged);
//    datastore.updateIndex(MetadataRecord.builder()
//        .withFile(removedNotMerged.getFile())
//        .withAction(Action.REMOVE)
//        .build()
//    );
//
//    MetadataRecord removedMerged = MetadataRecord.builder()
//        .withFile("aoml/13859/profiles/D13859_001.nc")
//        .withDate(now)
//        .withLatitude(0.32)
//        .withLatitudeMin(0d)
//        .withLatitudeMax(1d)
//        .withLongitude(-16d)
//        .withLongitudeMin(-17d)
//        .withLongitudeMax(-14d)
//        .withOcean(ArgoOcean.INDIAN_OCEAN)
//        .withProfilerType("847")
//        .withInstitution("A2")
//        .withDateUpdate(now)
//        .withParameters("params3")
//        .withAction(Action.UPDATE)
//        .withFileType(FileType.CORE_ARGO_PROFILE)
//        .withDac("aoml")
//        .withFloatId("13859")
//        .build();
//
//    datastore.updateIndex(removedMerged);
//    datastore.updateIndex(MetadataRecord.builder()
//        .withFileStatus(FileStatus.ACTIVE)
//        .withFile(removedMerged.getFile())
//        .withAction(Action.FLOAT_MERGE)
//        .build()
//    );
//    datastore.updateIndex(MetadataRecord.builder()
//        .withFile(removedMerged.getFile())
//        .withAction(Action.REMOVE)
//        .build()
//    );
//
//    MetadataRecord activeMerged2 = MetadataRecord.builder()
//        .withFile("cats/13860/profiles/D13860_001.nc")
//        .withDate(now)
//        .withLatitude(0.32)
//        .withLatitudeMin(0d)
//        .withLatitudeMax(1d)
//        .withLongitude(-16d)
//        .withLongitudeMin(-17d)
//        .withLongitudeMax(-14d)
//        .withOcean(ArgoOcean.INDIAN_OCEAN)
//        .withProfilerType("847")
//        .withInstitution("A2")
//        .withDateUpdate(now)
//        .withParameters("params3")
//        .withAction(Action.UPDATE)
//        .withFileType(FileType.CORE_ARGO_PROFILE)
//        .withDac("cats")
//        .withFloatId("13860")
//        .build();
//
//    datastore.updateIndex(activeMerged2);
//
//    datastore.updateIndex(MetadataRecord.builder()
//        .withFileStatus(FileStatus.ACTIVE)
//        .withFile(activeMerged2.getFile())
//        .withAction(Action.FLOAT_MERGE)
//        .build()
//    );
//
//    FloatMergeGroupPage page1 = datastore.findUpdatedOrMissingMergeFilesPage(DefaultIndexPageRequest.builder().withPageSize(1).build());
//    assertEquals(Collections.singletonList(FloatMergeGroup.builder().withDac("aoml").withFloatId("13857").build()), page1.getPage());
//
//    FloatMergeGroupPage page2 = datastore.findUpdatedOrMissingMergeFilesPage(page1.getNextPage().get());
//    assertEquals(Collections.singletonList(FloatMergeGroup.builder().withDac("aoml").withFloatId("13859").build()), page2.getPage());
//
//    assertFalse(page2.getNextPage().isPresent());

  }

  @Test
  public void testFindMissingSyntheticProfilesPage() throws Exception {

    Instant date = Instant.now();

    // no synth

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/13857/13857_meta.nc")
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
        .withFileType(FileType.METADATA)
        .build());

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/13857/profiles/D13857_001.nc")
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
        .withFileType(FileType.CORE_ARGO_PROFILE)
        .build());

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/13857/profiles/BD13857_001.nc")
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
        .withFileType(FileType.B_ARGO_PROFILE)
        .build());

    // no synth 2

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/123/123_meta.nc")
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
        .withFileType(FileType.METADATA)
        .build());

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/123/profiles/D123_001.nc")
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
        .withFileType(FileType.CORE_ARGO_PROFILE)
        .build());

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/123/profiles/BD123_001.nc")
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
        .withFileType(FileType.B_ARGO_PROFILE)
        .build());


    // synth

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/13857/profiles/D13857_002.nc")
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
        .withFileType(FileType.CORE_ARGO_PROFILE)
        .build());

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/13857/profiles/BD13857_002.nc")
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
        .withFileType(FileType.B_ARGO_PROFILE)
        .build());



    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/13857/13857_meta.nc")
        .withActionTimestamp(date)
        .withDac("aoml")
        .withFloatId("13857")
        .withAction(Action.SYNTHETIC_MERGE)
        .withFileType(FileType.METADATA)
        .build());


    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/13857/profiles/D13857_002.nc")
        .withActionTimestamp(date)
        .withAction(Action.SYNTHETIC_MERGE)
        .withFileType(FileType.CORE_ARGO_PROFILE)
        .withDac("aoml")
        .withFloatId("13857")
        .build());

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/13857/profiles/BD13857_002.nc")
        .withActionTimestamp(date)
        .withAction(Action.SYNTHETIC_MERGE)
        .withFileType(FileType.B_ARGO_PROFILE)
        .withDac("aoml")
        .withFloatId("13857")
        .build());


    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/13857/profiles/SD13857_002.nc")
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
        .withFileType(FileType.BGC_ARGO_SYNTH_PROFILE)
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
                "aoml/123/123_meta.nc",
                "aoml/123/profiles/BD123_001.nc",
                "aoml/123/profiles/D123_001.nc"
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
                "aoml/13857/13857_meta.nc",
                "aoml/13857/profiles/BD13857_001.nc",
                "aoml/13857/profiles/D13857_001.nc"
            ))
            .build()),
        page2.getPage());
  }


  @Test
  public void testRemoveMetadata() throws Exception {

    Instant date = Instant.now();

    // no synth

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/13857/13857_meta.nc")
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
        .withFileType(FileType.METADATA)
        .build());

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/13857/profiles/D13857_001.nc")
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
        .withFileType(FileType.CORE_ARGO_PROFILE)
        .build());

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/13857/profiles/BD13857_001.nc")
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
        .withFileType(FileType.B_ARGO_PROFILE)
        .build());

    // no synth 2

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/123/123_meta.nc")
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
        .withFileType(FileType.METADATA)
        .build());

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/123/profiles/D123_001.nc")
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
        .withFileType(FileType.CORE_ARGO_PROFILE)
        .build());

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/123/profiles/BD123_001.nc")
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
        .withFileType(FileType.B_ARGO_PROFILE)
        .build());


    // synth

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/13857/profiles/D13857_002.nc")
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
        .withFileType(FileType.CORE_ARGO_PROFILE)
        .build());

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/13857/profiles/BD13857_002.nc")
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
        .withFileType(FileType.B_ARGO_PROFILE)
        .build());



    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/13857/13857_meta.nc")
        .withActionTimestamp(date)
        .withDac("aoml")
        .withFloatId("13857")
        .withAction(Action.SYNTHETIC_MERGE)
        .withFileType(FileType.METADATA)
        .build());


    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/13857/profiles/D13857_002.nc")
        .withActionTimestamp(date)
        .withAction(Action.SYNTHETIC_MERGE)
        .withFileType(FileType.CORE_ARGO_PROFILE)
        .withDac("aoml")
        .withFloatId("13857")
        .build());

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/13857/profiles/BD13857_002.nc")
        .withActionTimestamp(date)
        .withAction(Action.SYNTHETIC_MERGE)
        .withFileType(FileType.B_ARGO_PROFILE)
        .withDac("aoml")
        .withFloatId("13857")
        .build());


    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/13857/profiles/SD13857_002.nc")
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
        .withFileType(FileType.BGC_ARGO_SYNTH_PROFILE)
        .build());

    ProfilePage page = datastore.findUpdatedOrMissingSyntheticProfilesPage(DefaultIndexPageRequest.builder().withPageSize(100).build());
    assertEquals(Arrays.asList(ProfileOperation.builder()
            .withDac("aoml")
            .withFloatId("123")
            .withFiles(Arrays.asList(
                "aoml/123/123_meta.nc",
                "aoml/123/profiles/BD123_001.nc",
                "aoml/123/profiles/D123_001.nc"
            ))
            .build(),
            ProfileOperation.builder()
                .withDac("aoml")
                .withFloatId("13857")
                .withFiles(Arrays.asList(
                    "aoml/13857/13857_meta.nc",
                    "aoml/13857/profiles/BD13857_001.nc",
                    "aoml/13857/profiles/D13857_001.nc"
                ))
                .build()
            ),
        page.getPage());


    //MetadataRecord{file='aoml/13857/13857_meta.nc', date=2026-05-14T13:48:57.457848Z, latitude=0.267, latitudeMin=null, latitudeMax=null, longitude=-16.032, longitudeMin=null, longitudeMax=null, ocean=ATLANTIC_OCEAN, profilerType='845', institution='A0', dateUpdate=2026-05-14T13:48:57.457848Z, parameters='null', parameterDataMode='null', direction='null', cycleNumber='null', action=null, fileType=METADATA, fileStatus=ACTIVE, dac='aoml', floatId='13857', actionTimestamp=2026-05-14T13:48:57.723361Z, otherFields={}}

    assertEquals(MetadataRecord.builder()
        .withFile("aoml/13857/13857_meta.nc")
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
        .withFileType(FileType.METADATA)
        .withFileStatus(FileStatus.ACTIVE)
        .build(), datastore.findByFile("aoml/13857/13857_meta.nc").get());

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/13857/13857_meta.nc")
        .withActionTimestamp(date)
        .withDac("aoml")
        .withFloatId("13857")
        .withAction(Action.REMOVE)
        .withFileType(FileType.METADATA)
        .build());

    page = datastore.findUpdatedOrMissingSyntheticProfilesPage(DefaultIndexPageRequest.builder().withPageSize(100).build());
    assertEquals(Arrays.asList(ProfileOperation.builder()
                .withDac("aoml")
                .withFloatId("123")
                .withFiles(Arrays.asList(
                    "aoml/123/123_meta.nc",
                    "aoml/123/profiles/BD123_001.nc",
                    "aoml/123/profiles/D123_001.nc"
                ))
                .build()
        ),
        page.getPage());


    assertEquals(MetadataRecord.builder()
        .withFile("aoml/13857/13857_meta.nc")
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
        .withFileType(FileType.METADATA)
        .withFileStatus(FileStatus.REMOVED)
        .build(), datastore.findByFile("aoml/13857/13857_meta.nc", true).get());

    assertFalse(datastore.findByFile("aoml/13857/13857_meta.nc").isPresent());
  }

}