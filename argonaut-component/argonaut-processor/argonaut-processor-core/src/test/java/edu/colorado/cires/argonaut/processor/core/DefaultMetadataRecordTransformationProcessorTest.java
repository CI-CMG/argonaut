package edu.colorado.cires.argonaut.processor.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.doubleThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.colorado.cires.argonaut.file.core.FileStore;
import edu.colorado.cires.argonaut.file.local.LocalFileStore;
import edu.colorado.cires.argonaut.messaging.core.databind.ArgoFileType;
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
import java.util.UUID;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.function.TriFunction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class DefaultMetadataRecordTransformationProcessorTest {

  private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

  private final Path tempDir = Paths.get("target/temp");

  @AfterEach
  public void afterEach() throws IOException {
    FileUtils.deleteDirectory(tempDir.toFile());
  }

  private GeoFilter createGeoFilter() {
    GeoFilter geoFilter = mock(GeoFilter.class);
    when(geoFilter.determineArgoOcean(
      doubleThat(d -> Math.abs(d - -16.032) <= 0.001),
      doubleThat(d -> Math.abs(d - 0.267) <= 0.001)))
      .thenReturn(ArgoOcean.ATLANTIC_OCEAN);
    return geoFilter;
  }

  private FileStore createFileStore() {
    LocalFileStore localFileStore = new LocalFileStore();
    localFileStore.setRootPath(Paths.get("src/test/resources/output"));
    return localFileStore;
  }

  private MetadataRecordTransformationProcessor createProcessor(TriFunction<String, String, Path, MetadataRecord> trajectoryReader) {
    DefaultMetadataRecordTransformationProcessor processor = new DefaultMetadataRecordTransformationProcessor();
    processor.setLocalTempDir(tempDir);
    processor.setOutputFileStore(createFileStore());
    processor.setGeoFilter(createGeoFilter());
    processor.setTrajectoryReader(trajectoryReader);
    return processor;
  }

  @Test
  public void testReadProfile() {
    MetadataRecordTransformationProcessor processor = createProcessor(null);
    Instant now = Instant.now();
    // file,date,latitude,longitude,ocean,profiler_type,institution,date_update
    /// aoml/13857/profiles/D13857_001.nc,19970729200300,0.267,-16.032,A,845,AO,20260220143529
    MetadataRecord metadataRecord = processor.transformNcSubmissionMessage(NcSubmissionMessage.builder()
        .withOperation(Operation.ADD)
        .withTimestamp(now)
        .withDac("aoml")
        .withFileName("D13857_001.nc")
        .withFloatId("13857")
        .withFileType(ArgoFileType.PROFILE_CORE)
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

  @Test
  void testReadTrajectory() {
    NcSubmissionMessage message = NcSubmissionMessage.builder()
      .withTraceId(UUID.randomUUID())
      .withOperation(Operation.ADD)
      .withTimestamp(Instant.now())
      .withDac("aoml")
      .withFileName("13857_Rtraj.nc")
      .withFloatId("13857")
      .withFileType(ArgoFileType.TRAJECTORY)
      .build();

    TriFunction<String, String, Path, MetadataRecord> trajectoryReader = mock(TriFunction.class);
    when(trajectoryReader.apply(any(), anyString(), any())).thenReturn(MetadataRecord.builder().build());

    MetadataRecord actual = createProcessor(trajectoryReader).transformNcSubmissionMessage(message);
    verify(trajectoryReader, times(1)).apply(any(), anyString(), any());

    assertEquals(
      MetadataRecord.builder()
        .withTraceId(message.getTraceId())
        .withFileName(message.getFileName())
        .build(),
      actual
    );
  }
}