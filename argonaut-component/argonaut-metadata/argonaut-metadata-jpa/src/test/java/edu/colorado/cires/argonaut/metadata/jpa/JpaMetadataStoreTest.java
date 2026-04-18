package edu.colorado.cires.argonaut.metadata.jpa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import edu.colorado.cires.argonaut.messaging.core.databind.ArgoOcean;
import edu.colorado.cires.argonaut.messaging.core.databind.FloatMergeGroup;
import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord;
import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord.Action;
import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord.FileStatus;
import edu.colorado.cires.argonaut.messaging.core.databind.NcSubmissionMessage.FileType;
import edu.colorado.cires.argonaut.metadata.core.DefaultIndexPageRequest;
import edu.colorado.cires.argonaut.metadata.core.DefaultMetadataRecordPage;
import edu.colorado.cires.argonaut.metadata.core.FloatMergeGroupPage;
import edu.colorado.cires.argonaut.metadata.core.MetadataRecordPage;
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
        .withParameterDataMode("mode")
        .withAction(Action.UPDATE)
        .withFileType(FileType.CORE_ARGO_PROFILE)
        .withDac("aoml")
        .withFloatId("13857")
        .build();
    datastore.updateIndex(record1);
    Instant date2 = date1.plusSeconds(1);
    Instant dateUpdate2 = dateUpdate1.plusSeconds(1);
    MetadataRecord record2 = MetadataRecord.builder()
        .withFile("aoml/13857/profiles/D13857_002.nc")
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
        .withParameterDataMode("mode2")
        .withAction(Action.UPDATE)
        .withFileType(FileType.CORE_ARGO_PROFILE)
        .withDac("aoml")
        .withFloatId("13857")
        .build();
    datastore.updateIndex(record2);
    Instant date3 = date1.plusSeconds(1);
    Instant dateUpdate3 = dateUpdate1.plusSeconds(1);
    MetadataRecord record3 = MetadataRecord.builder()
        .withFile("aoml/13857/profiles/D13857_003.nc")
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
        .withParameterDataMode("mode3")
        .withAction(Action.UPDATE)
        .withFileType(FileType.CORE_ARGO_PROFILE)
        .withDac("aoml")
        .withFloatId("13857")
        .build();
    datastore.updateIndex(record3);
    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/13857/profiles/D13857_003.nc")
        .withAction(Action.REMOVE)
        .build());

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("nnic/13857/profiles/D13857_003.nc")
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
        .withParameterDataMode("mode3")
        .withAction(Action.UPDATE)
        .withFileType(FileType.CORE_ARGO_PROFILE)
        .withDac("nnic")
        .withFloatId("13857")
        .build());

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("aoml/13858/profiles/D13858_003.nc")
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
        .withParameterDataMode("mode3")
        .withAction(Action.UPDATE)
        .withFileType(FileType.CORE_ARGO_PROFILE)
        .withDac("aoml")
        .withFloatId("13858")
        .build());

    datastore.updateIndex(MetadataRecord.builder()
        .withFile("nnic/1000/profiles/D1000_001.nc")
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
        .withParameterDataMode("mode3")
        .withAction(Action.UPDATE)
        .withFileType(FileType.CORE_ARGO_PROFILE)
        .withDac("nnic")
        .withFloatId("1000")
        .build());

    MetadataRecord record12 = MetadataRecord.builder(record1)
        .withInstitution("A3")
        .build();
    datastore.updateIndex(record12);

    assertEquals(MetadataRecord.builder(record12).withAction(null).withFileStatus(FileStatus.ACTIVE).build(), datastore.findByFile("aoml/13857/profiles/D13857_001.nc").get());
    assertEquals(MetadataRecord.builder(record2).withAction(null).withFileStatus(FileStatus.ACTIVE).build(), datastore.findByFile("aoml/13857/profiles/D13857_002.nc").get());
    assertEquals(MetadataRecord.builder(record3).withAction(null).withFileStatus(FileStatus.REMOVED).build(), datastore.findByFile("aoml/13857/profiles/D13857_003.nc").get());
    assertFalse(datastore.findByFile("aoml/13857/profiles/D13857_004.nc").isPresent());

    MetadataRecordPage page = datastore.findProfilePage("13857", "aoml", DefaultIndexPageRequest.builder().withPageSize(2).build());
    DefaultMetadataRecordPage expected = DefaultMetadataRecordPage.builder()
        .withIndexPageRequest(DefaultIndexPageRequest.builder().withPageSize(2).build())
        .withTotalRecords(3L)
        .withPage(Arrays.asList(
            MetadataRecord.builder(record12).withAction(null).withFileStatus(FileStatus.ACTIVE).build(),
            MetadataRecord.builder(record2).withAction(null).withFileStatus(FileStatus.ACTIVE).build()))
        .build();
    assertEquals(expected, page);
    assertEquals(
        DefaultMetadataRecordPage.builder()
            .withIndexPageRequest(DefaultIndexPageRequest.builder().withPageNumber(2).withPageSize(2).build())
            .withTotalRecords(3)
            .build(),
        page.getNextPage().get());

    page = datastore.findProfilePage("13857", "aoml", page.getNextPage().get());
    expected = DefaultMetadataRecordPage.builder()
        .withIndexPageRequest(DefaultIndexPageRequest.builder().withPageNumber(2).withPageSize(2).build())
        .withTotalRecords(3L)
        .withPage(Arrays.asList(
            MetadataRecord.builder(record3).withAction(null).withFileStatus(FileStatus.REMOVED).build()))
        .build();
    assertEquals(expected, page);
    assertFalse(page.getNextPage().isPresent());

  }


  @Test
  public void testFloatMergeCycle() throws Exception {
    Instant now = Instant.now();

    MetadataRecord d13859_001 = MetadataRecord.builder()
        .withFile("aoml/13859/profiles/D13859_001.nc")
        .withDate(now)
        .withLatitude(0.32)
        .withLatitudeMin(0d)
        .withLatitudeMax(1d)
        .withLongitude(-16d)
        .withLongitudeMin(-17d)
        .withLongitudeMax(-14d)
        .withOcean(ArgoOcean.INDIAN_OCEAN)
        .withProfilerType("847")
        .withInstitution("A2")
        .withDateUpdate(now)
        .withParameters("params3")
        .withParameterDataMode("mode3")
        .withAction(Action.UPDATE)
        .withFileType(FileType.CORE_ARGO_PROFILE)
        .withDac("aoml")
        .withFloatId("13858")
        .build();

    // create
    datastore.updateIndex(d13859_001);

    assertEquals(
        MetadataRecord.builder(d13859_001)
            .withFileStatus(FileStatus.ACTIVE)
            .withFloatMerged(false)
            .withAction(null)
            .build(),
        datastore.findByFile(d13859_001.getFile()).get());

    // update float merged flag
    datastore.updateIndex(MetadataRecord.builder()
        .withFileStatus(FileStatus.ACTIVE)
        .withFile(d13859_001.getFile())
        .withAction(Action.FLOAT_MERGE)
        .build()
    );

    assertEquals(
        MetadataRecord.builder(d13859_001)
            .withFileStatus(FileStatus.ACTIVE)
            .withFloatMerged(true)
            .withAction(null)
            .build(),
        datastore.findByFile(d13859_001.getFile()).get());

    // remove
    datastore.updateIndex(MetadataRecord.builder()
        .withFile(d13859_001.getFile())
        .withAction(Action.REMOVE)
        .build()
    );

    assertEquals(
        MetadataRecord.builder(d13859_001)
            .withFileStatus(FileStatus.REMOVED)
            .withFloatMerged(true)
            .withAction(null)
            .build(),
        datastore.findByFile(d13859_001.getFile()).get());

    // update float merged flag on removed file
    datastore.updateIndex(MetadataRecord.builder(d13859_001)
        .withFileStatus(FileStatus.REMOVED)
        .withAction(Action.FLOAT_MERGE)
        .build()
    );

    assertEquals(
        MetadataRecord.builder(d13859_001)
            .withFileStatus(FileStatus.REMOVED)
            .withFloatMerged(false)
            .withAction(null)
            .build(),
        datastore.findByFile(d13859_001.getFile()).get());
  }

  @Test
  public void findUpdatedOrMissingMergeFilesPage() throws Exception {

    Instant now = Instant.now();

    MetadataRecord activeNotMerged = MetadataRecord.builder()
        .withFile("aoml/13857/profiles/D13857_001.nc")
        .withDate(now)
        .withLatitude(0.32)
        .withLatitudeMin(0d)
        .withLatitudeMax(1d)
        .withLongitude(-16d)
        .withLongitudeMin(-17d)
        .withLongitudeMax(-14d)
        .withOcean(ArgoOcean.INDIAN_OCEAN)
        .withProfilerType("847")
        .withInstitution("A2")
        .withDateUpdate(now)
        .withParameters("params3")
        .withParameterDataMode("mode3")
        .withAction(Action.UPDATE)
        .withFileType(FileType.CORE_ARGO_PROFILE)
        .withDac("aoml")
        .withFloatId("13857")
        .build();

    datastore.updateIndex(activeNotMerged);

    MetadataRecord activeNotMerged2 = MetadataRecord.builder()
        .withFile("aoml/13857/profiles/D13857_003.nc")
        .withDate(now)
        .withLatitude(0.32)
        .withLatitudeMin(0d)
        .withLatitudeMax(1d)
        .withLongitude(-16d)
        .withLongitudeMin(-17d)
        .withLongitudeMax(-14d)
        .withOcean(ArgoOcean.INDIAN_OCEAN)
        .withProfilerType("847")
        .withInstitution("A2")
        .withDateUpdate(now)
        .withParameters("params3")
        .withParameterDataMode("mode3")
        .withAction(Action.UPDATE)
        .withFileType(FileType.CORE_ARGO_PROFILE)
        .withDac("aoml")
        .withFloatId("13857")
        .build();

    datastore.updateIndex(activeNotMerged2);

    MetadataRecord activeMerged = MetadataRecord.builder()
        .withFile("aoml/13857/profiles/D13857_002.nc")
        .withDate(now)
        .withLatitude(0.32)
        .withLatitudeMin(0d)
        .withLatitudeMax(1d)
        .withLongitude(-16d)
        .withLongitudeMin(-17d)
        .withLongitudeMax(-14d)
        .withOcean(ArgoOcean.INDIAN_OCEAN)
        .withProfilerType("847")
        .withInstitution("A2")
        .withDateUpdate(now)
        .withParameters("params3")
        .withParameterDataMode("mode3")
        .withAction(Action.UPDATE)
        .withFileType(FileType.CORE_ARGO_PROFILE)
        .withDac("aoml")
        .withFloatId("13857")
        .build();

    datastore.updateIndex(activeNotMerged);

    datastore.updateIndex(MetadataRecord.builder()
        .withFileStatus(FileStatus.ACTIVE)
        .withFile(activeMerged.getFile())
        .withAction(Action.FLOAT_MERGE)
        .build()
    );

    MetadataRecord removedNotMerged = MetadataRecord.builder()
        .withFile("aoml/13858/profiles/D13858_001.nc")
        .withDate(now)
        .withLatitude(0.32)
        .withLatitudeMin(0d)
        .withLatitudeMax(1d)
        .withLongitude(-16d)
        .withLongitudeMin(-17d)
        .withLongitudeMax(-14d)
        .withOcean(ArgoOcean.INDIAN_OCEAN)
        .withProfilerType("847")
        .withInstitution("A2")
        .withDateUpdate(now)
        .withParameters("params3")
        .withParameterDataMode("mode3")
        .withAction(Action.UPDATE)
        .withFileType(FileType.CORE_ARGO_PROFILE)
        .withDac("aoml")
        .withFloatId("13858")
        .build();

    datastore.updateIndex(removedNotMerged);
    datastore.updateIndex(MetadataRecord.builder()
        .withFile(removedNotMerged.getFile())
        .withAction(Action.REMOVE)
        .build()
    );

    MetadataRecord removedMerged = MetadataRecord.builder()
        .withFile("aoml/13859/profiles/D13859_001.nc")
        .withDate(now)
        .withLatitude(0.32)
        .withLatitudeMin(0d)
        .withLatitudeMax(1d)
        .withLongitude(-16d)
        .withLongitudeMin(-17d)
        .withLongitudeMax(-14d)
        .withOcean(ArgoOcean.INDIAN_OCEAN)
        .withProfilerType("847")
        .withInstitution("A2")
        .withDateUpdate(now)
        .withParameters("params3")
        .withParameterDataMode("mode3")
        .withAction(Action.UPDATE)
        .withFileType(FileType.CORE_ARGO_PROFILE)
        .withDac("aoml")
        .withFloatId("13859")
        .build();

    datastore.updateIndex(removedMerged);
    datastore.updateIndex(MetadataRecord.builder()
        .withFileStatus(FileStatus.ACTIVE)
        .withFile(removedMerged.getFile())
        .withAction(Action.FLOAT_MERGE)
        .build()
    );
    datastore.updateIndex(MetadataRecord.builder()
        .withFile(removedMerged.getFile())
        .withAction(Action.REMOVE)
        .build()
    );

    MetadataRecord activeMerged2 = MetadataRecord.builder()
        .withFile("cats/13860/profiles/D13860_001.nc")
        .withDate(now)
        .withLatitude(0.32)
        .withLatitudeMin(0d)
        .withLatitudeMax(1d)
        .withLongitude(-16d)
        .withLongitudeMin(-17d)
        .withLongitudeMax(-14d)
        .withOcean(ArgoOcean.INDIAN_OCEAN)
        .withProfilerType("847")
        .withInstitution("A2")
        .withDateUpdate(now)
        .withParameters("params3")
        .withParameterDataMode("mode3")
        .withAction(Action.UPDATE)
        .withFileType(FileType.CORE_ARGO_PROFILE)
        .withDac("cats")
        .withFloatId("13860")
        .build();

    datastore.updateIndex(activeMerged2);

    datastore.updateIndex(MetadataRecord.builder()
        .withFileStatus(FileStatus.ACTIVE)
        .withFile(activeMerged2.getFile())
        .withAction(Action.FLOAT_MERGE)
        .build()
    );

    FloatMergeGroupPage page1 = datastore.findUpdatedOrMissingMergeFilesPage(DefaultIndexPageRequest.builder().withPageSize(1).build());
    assertEquals(Collections.singletonList(FloatMergeGroup.builder().withDac("aoml").withFloatId("13857").build()), page1.getPage());

    FloatMergeGroupPage page2 = datastore.findUpdatedOrMissingMergeFilesPage(page1.getNextPage().get());
    assertEquals(Collections.singletonList(FloatMergeGroup.builder().withDac("aoml").withFloatId("13859").build()), page2.getPage());

    assertFalse(page2.getNextPage().isPresent());

  }

}