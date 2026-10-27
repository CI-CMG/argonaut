package edu.colorado.cires.argonaut.processor.core;


import edu.colorado.cires.argonaut.file.core.FileStore;
import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord;
import edu.colorado.cires.argonaut.metadata.core.DefaultIndexPageRequest;
import edu.colorado.cires.argonaut.metadata.core.DefaultMetadataRecordPage;
import edu.colorado.cires.argonaut.metadata.core.MetadataStore;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.GZIPInputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DefaultBioTrajIndexProcessorTest {

  private static final Path localTempDir = Paths.get("target/temp");
  private static final Path outputDir = Paths.get("target/output");

  @BeforeEach
  @AfterEach
  public void setup() {
    FileUtils.deleteQuietly(localTempDir.toFile());
    FileUtils.deleteQuietly(outputDir.toFile());
  }

  @Test
  public void test()  throws Exception {
    MetadataStore metadataStore = mock(MetadataStore.class);
    FileStore outputFileStore = new TestFileStore(outputDir);

    Map<String, String> accessPathDocumentation = new TreeMap<>();
    accessPathDocumentation.put("HTTP root number 1", "https://example.com/argo/dac");
    accessPathDocumentation.put("HTTP root number 2", "https://foo.bar/argo/dac");

    Instant now = Instant.parse("2026-09-28T20:24:55Z");

    DefaultBioTrajectoryIndexProcessor processor = new DefaultBioTrajectoryIndexProcessor();
    processor.setGdacNode("TEST");
    processor.setAccessPathDocumentation(accessPathDocumentation);
    processor.setMetadataStore(metadataStore);
    processor.setOutputFileStore(outputFileStore);
    processor.setPageSize(2);
    processor.setLocalTempDir(localTempDir);
    processor.setNowGenerator(() -> now);

    when(metadataStore.getBioTrajectoryIndexPage(eq(DefaultIndexPageRequest.builder().withPageNumber(1).withPageSize(2).build())))
        .thenReturn(DefaultMetadataRecordPage.builder()
            .withTotalRecords(3)
            .withPage(Arrays.asList(
                MetadataRecord.builder()
                    .withFile("bodc/6904189/6904189_BRtraj.nc")
                    .withProfilerType("836")
                    .withInstitution("BO")
                    .withParameters(Arrays.asList("PRES", "C1PHASE_DOXY", "C2PHASE_DOXY", "TEMP_DOXY", "DOXY", "RAW_DOWNWELLING_IRRADIANCE380", "RAW_DOWNWELLING_IRRADIANCE412", "RAW_DOWNWELLING_IRRADIANCE490", "RAW_DOWNWELLING_PAR", "DOWN_IRRADIANCE380", "DOWN_IRRADIANCE412", "DOWN_IRRADIANCE490", "DOWNWELLING_PAR", "VRS_PH", "PH_IN_SITU_FREE", "PH_IN_SITU_TOTAL", "FLUORESCENCE_CHLA", "BETA_BACKSCATTERING700", "FLUORESCENCE_CDOM", "CHLA", "BBP700", "CDOM", "TEMP_NITRATE", "TEMP_SPECTROPHOTOMETER_NITRATE", "HUMIDITY_NITRATE", "UV_INTENSITY_DARK_NITRATE", "UV_INTENSITY_DARK_NITRATE_STD", "FIT_ERROR_NITRATE", "UV_INTENSITY_NITRATE", "NITRATE", "PPOX_DOXY"))
                    .withDateUpdate(Instant.parse("2024-01-10T01:30:18Z"))
                    .build(),
                MetadataRecord.builder()
                    .withFile("csio/2901550/2901550_BRtraj.nc")
                    .withLatitudeMax(36.813)
                    .withLatitudeMin(27.921)
                    .withLongitudeMax(155.622)
                    .withLongitudeMin(134.477)
                    .withProfilerType("841")
                    .withInstitution("HZ")
                    .withParameters(Arrays.asList("PRES", "C1PHASE_DOXY", "C2PHASE_DOXY", "TEMP_DOXY", "DOXY PPOX_DOXY"))
                    .withDateUpdate(Instant.parse("2024-11-16T06:06:44Z"))
                    .build()
            ))
            .withIndexPageRequest(DefaultIndexPageRequest.builder().withPageNumber(1).withPageSize(2).build())
            .build());

    when(metadataStore.getBioTrajectoryIndexPage(eq(DefaultIndexPageRequest.builder().withPageNumber(2).withPageSize(2).build())))
        .thenReturn(DefaultMetadataRecordPage.builder()
            .withTotalRecords(3)
            .withPage(List.of(
                    MetadataRecord.builder()
                            .withFile("csio/2901551/2901551_BRtraj.nc")
                            .withLatitudeMax(29.891)
                            .withLatitudeMin(23.523)
                            .withLongitudeMax(149.631)
                            .withLongitudeMin(139.986)
                            .withProfilerType("841")
                            .withInstitution("HZ")
                            .withParameters(Arrays.asList("PRES", "C1PHASE_DOXY", "C2PHASE_DOXY", "TEMP_DOXY", "DOXY PPOX_DOXY"))
                            .withDateUpdate(Instant.parse("2024-11-16T06:06:54Z"))
                            .build()
            ))
            .withIndexPageRequest(DefaultIndexPageRequest.builder().withPageNumber(2).withPageSize(2).build())
            .build());

    processor.generateIndex();

    String expected = Files.readString(Paths.get("src/test/resources/argo_bio-traj_index.txt"), StandardCharsets.UTF_8);
    assertTrue(Files.exists(outputDir.resolve("argo_bio-traj_index.txt")));
    assertEquals(expected, Files.readString(outputDir.resolve("argo_bio-traj_index.txt"), StandardCharsets.UTF_8));

    assertTrue(Files.exists(outputDir.resolve("argo_bio-traj_index.txt.gz")));
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    try(InputStream in = new GZIPInputStream(Files.newInputStream(outputDir.resolve("argo_bio-traj_index.txt.gz")))) {
      IOUtils.copy(in, out);
    }

    assertEquals(expected, out.toString(StandardCharsets.UTF_8));
  }
}