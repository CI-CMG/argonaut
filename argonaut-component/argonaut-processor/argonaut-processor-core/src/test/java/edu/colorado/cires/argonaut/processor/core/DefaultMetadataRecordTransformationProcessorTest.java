package edu.colorado.cires.argonaut.processor.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.doubleThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import edu.colorado.cires.argonaut.file.local.LocalFileStore;
import edu.colorado.cires.argonaut.messaging.core.databind.ArgoOcean;
import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord;
import edu.colorado.cires.argonaut.messaging.core.databind.NcSubmissionMessage;
import edu.colorado.cires.argonaut.messaging.core.databind.NcSubmissionMessage.Operation;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class DefaultMetadataRecordTransformationProcessorTest {

  private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

  private Path tempDir = Paths.get("target/temp");

  @AfterEach
  public void afterEach() throws IOException {
    FileUtils.deleteDirectory(tempDir.toFile());
  }

  @Test
  public void testReadProfile() throws Exception {
    GeoFilter geoFilter = mock(GeoFilter.class);
    when(geoFilter.determineArgoOcean(
        doubleThat(d -> Math.abs(d - -16.032) <= 0.001),
        doubleThat(d -> Math.abs(d - 0.267) <= 0.001)))
        .thenReturn(ArgoOcean.ATLANTIC_OCEAN);
    LocalFileStore outputFileStore = new LocalFileStore();
    outputFileStore.setRootPath(Paths.get("src/test/resources/output"));
    DefaultMetadataRecordTransformationProcessor processor = new DefaultMetadataRecordTransformationProcessor();
    processor.setLocalTempDir(tempDir);
    processor.setOutputFileStore(outputFileStore);
    processor.setGeoFilter(geoFilter);
    Instant now = Instant.now();
    // file,date,latitude,longitude,ocean,profiler_type,institution,date_update
    /// aoml/13857/profiles/D13857_001.nc,19970729200300,0.267,-16.032,A,845,AO,20260220143529
    MetadataRecord metadataRecord = processor.transformNcSubmissionMessage(NcSubmissionMessage.builder()
        .withOperation(Operation.ADD)
        .withTimestamp(now)
        .withDac("aoml")
        .withFileName("D13857_001.nc")
        .withFloatId("13857")
        .withProfile(true)
        .withNumberOfFilesInSubmission(100)
        .build());

    assertEquals("aoml/13857/profiles/D13857_001.nc", metadataRecord.getFile());
    assertEquals("19970729200300", DATE_TIME_FORMATTER.format(metadataRecord.getDate().atZone(ZoneId.of("UTC"))));
    assertEquals(0.267, metadataRecord.getLatitude(), 0.001);
    assertEquals(-16.032, metadataRecord.getLongitude(), 0.001);
    assertEquals(ArgoOcean.ATLANTIC_OCEAN, metadataRecord.getOcean());
    assertEquals("845", metadataRecord.getProfilerType());
    assertEquals("AO", metadataRecord.getInstitution());
    assertEquals("20260220143529", DATE_TIME_FORMATTER.format(metadataRecord.getDateUpdate().atZone(ZoneId.of("UTC"))));


  }

}