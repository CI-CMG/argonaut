package edu.colorado.cires.argonaut.metadata.jpa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import edu.colorado.cires.argonaut.messaging.core.databind.ArgoOcean;
import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord;
import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord.Action;
import edu.colorado.cires.argonaut.metadata.core.DefaultIndexPageRequest;
import edu.colorado.cires.argonaut.metadata.core.DefaultMetadataRecordPage;
import edu.colorado.cires.argonaut.metadata.core.MetadataRecordPage;
import jakarta.persistence.EntityManagerFactory;
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
  }

  @Test
  public void testInsertUpdateQuery() throws Exception {
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
        .build();
    datastore.updateIndex(record3);
    MetadataRecord record12 = MetadataRecord.builder(record1)
        .withInstitution("A3")
        .build();
    datastore.updateIndex(record12);

    assertEquals(MetadataRecord.builder(record12).withAction(Action.NONE).build(), datastore.findByFile("aoml/13857/profiles/D13857_001.nc").get());
    assertEquals(MetadataRecord.builder(record2).withAction(Action.NONE).build(), datastore.findByFile("aoml/13857/profiles/D13857_002.nc").get());
    assertEquals(MetadataRecord.builder(record3).withAction(Action.NONE).build(), datastore.findByFile("aoml/13857/profiles/D13857_003.nc").get());
    assertFalse(datastore.findByFile("aoml/13857/profiles/D13857_004.nc").isPresent());

    MetadataRecordPage page = datastore.findPage(DefaultIndexPageRequest.builder().withPageSize(2).build());
    DefaultMetadataRecordPage expected = DefaultMetadataRecordPage.builder()
        .withIndexPageRequest(DefaultIndexPageRequest.builder().withPageSize(2).build())
        .withTotalRecords(3L)
        .withPage(Arrays.asList(
            MetadataRecord.builder(record12).withAction(Action.NONE).build(),
            MetadataRecord.builder(record2).withAction(Action.NONE).build()))
        .build();
    assertEquals(expected, page);
    assertEquals(
        DefaultMetadataRecordPage.builder()
            .withIndexPageRequest(DefaultIndexPageRequest.builder().withPageNumber(2).withPageSize(2).build())
            .withTotalRecords(3)
            .build(),
        page.getNextPage().get());

    page = datastore.findPage(page.getNextPage().get());
    expected = DefaultMetadataRecordPage.builder()
        .withIndexPageRequest(DefaultIndexPageRequest.builder().withPageNumber(2).withPageSize(2).build())
        .withTotalRecords(3L)
        .withPage(Arrays.asList(
            MetadataRecord.builder(record3).withAction(Action.NONE).build()))
        .build();
    assertEquals(expected, page);
    assertFalse(page.getNextPage().isPresent());

  }


}