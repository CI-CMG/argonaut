package edu.colorado.cires.argonaut.processor.core;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import edu.colorado.cires.argonaut.file.core.FileStore;
import edu.colorado.cires.argonaut.messaging.core.databind.ArgoOcean;
import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord;
import edu.colorado.cires.argonaut.metadata.core.DefaultIndexPageRequest;
import edu.colorado.cires.argonaut.metadata.core.DefaultMetadataRecordPage;
import edu.colorado.cires.argonaut.metadata.core.MetadataStore;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.GZIPInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DefaultSyntheticProfileIndexProcessorTest {

  private static final Path localTempDir = Paths.get("target/temp");
  private static final Path outputDir = Paths.get("target/output");

  @BeforeEach
  @AfterEach
  public void setup() {
    FileUtils.deleteQuietly(localTempDir.toFile());
    FileUtils.deleteQuietly(outputDir.toFile());
  }

  @Test
  public void test() throws Exception {
    MetadataStore metadataStore = mock(MetadataStore.class);
    FileStore outputFileStore = new TestFileStore(outputDir);

    Map<String, String> accessPathDocumentation = new TreeMap<>();
    accessPathDocumentation.put("HTTP root number 1", "https://example.com/argo/dac");
    accessPathDocumentation.put("HTTP root number 2", "https://foo.bar/argo/dac");

    Instant now = Instant.parse("2026-09-28T20:24:02Z");

    DefaultSyntheticProfileIndexProcessor processor = new DefaultSyntheticProfileIndexProcessor();
    processor.setGdacNode("TEST");
    processor.setAccessPathDocumentation(accessPathDocumentation);
    processor.setMetadataStore(metadataStore);
    processor.setOutputFileStore(outputFileStore);
    processor.setPageSize(2);
    processor.setLocalTempDir(localTempDir);
    processor.setNowGenerator(() -> now);

    when(metadataStore.getSyntheticProfileIndexPage(eq(DefaultIndexPageRequest.builder().withPageNumber(1).withPageSize(2).build())))
        .thenReturn(DefaultMetadataRecordPage.builder()
            .withTotalRecords(3)
            .withPage(Arrays.asList(
                MetadataRecord.builder()
                    .withFile("meds/4902688/profiles/SR4902688_162.nc")
                    .withDate(Instant.parse("2026-09-26T15:44:00Z"))
                    .withLatitude(50.299)
                    .withLongitude(-47.487)
                    .withOcean(ArgoOcean.ATLANTIC_OCEAN)
                    .withProfilerType("834")
                    .withInstitution("ME")
                    .withParameters(Arrays.asList("PRES", "TEMP", "PSAL DOXY", "DOWN_IRRADIANCE380", "DOWN_IRRADIANCE412", "DOWN_IRRADIANCE490",
                        "DOWNWELLING_PAR", "CHLA BBP700"))
                    .withParameterDataMode("RRRARRRRAA")
                    .withDateUpdate(Instant.parse("2026-09-27T00:03:51Z"))
                    .build(),
                MetadataRecord.builder()
                    .withFile("meds/4902719/profiles/SR4902719_001.nc")
                    .withDate(Instant.parse("2026-05-13T11:32:00Z"))
                    .withLatitude(59.332)
                    .withLongitude(-39.900)
                    .withOcean(ArgoOcean.ATLANTIC_OCEAN)
                    .withProfilerType("834")
                    .withInstitution("ME")
                    .withParameters(Arrays.asList("PRES", "TEMP PSAL", "DOXY", "DOWN_IRRADIANCE380", "DOWN_IRRADIANCE412", "DOWN_IRRADIANCE490",
                        "DOWNWELLING_PAR", "PH_IN_SITU_TOTAL"))
                    .withParameterDataMode("RRRARRRRR")
                    .withDateUpdate(Instant.parse("2026-07-31T00:33:20Z"))
                    .build()
            ))
            .withIndexPageRequest(DefaultIndexPageRequest.builder().withPageNumber(1).withPageSize(2).build())
            .build());

    when(metadataStore.getSyntheticProfileIndexPage(eq(DefaultIndexPageRequest.builder().withPageNumber(2).withPageSize(2).build())))
        .thenReturn(DefaultMetadataRecordPage.builder()
            .withTotalRecords(3)
            .withPage(Arrays.asList(
                MetadataRecord.builder()
                    .withFile("meds/4902719/profiles/SR4902719_002.nc")
                    .withDate(Instant.parse("2026-05-23T11:49:00Z"))
                    .withLatitude(59.390)
                    .withLongitude(-40.266)
                    .withOcean(ArgoOcean.ATLANTIC_OCEAN)
                    .withProfilerType("834")
                    .withInstitution("ME")
                    .withParameters(Arrays.asList("PRES", "TEMP PSAL", "DOXY", "DOWN_IRRADIANCE380", "DOWN_IRRADIANCE412", "DOWN_IRRADIANCE490",
                        "DOWNWELLING_PAR", "PH_IN_SITU_TOTAL"))
                    .withParameterDataMode("RRRARRRRR")
                    .withDateUpdate(Instant.parse("2026-07-31T00:33:30Z"))
                    .build()
            ))
            .withIndexPageRequest(DefaultIndexPageRequest.builder().withPageNumber(2).withPageSize(2).build())
            .build());

    processor.generateIndex();

    String expected = Files.readString(Paths.get("src/test/resources/argo_synthetic-profile_index.txt"), StandardCharsets.UTF_8);
    assertTrue(Files.exists(outputDir.resolve("argo_synthetic-profile_index.txt")));
    assertEquals(expected, Files.readString(outputDir.resolve("argo_synthetic-profile_index.txt"), StandardCharsets.UTF_8));

    assertTrue(Files.exists(outputDir.resolve("argo_synthetic-profile_index.txt.gz")));
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    try (InputStream in = new GZIPInputStream(Files.newInputStream(outputDir.resolve("argo_synthetic-profile_index.txt.gz")))) {
      IOUtils.copy(in, out);
    }

    assertEquals(expected, out.toString(StandardCharsets.UTF_8));


  }

}