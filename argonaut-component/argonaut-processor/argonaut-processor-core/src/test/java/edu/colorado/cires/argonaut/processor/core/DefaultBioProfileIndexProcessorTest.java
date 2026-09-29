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

public class DefaultBioProfileIndexProcessorTest {

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

    Instant now = Instant.parse("2026-09-28T20:24:02Z");

    DefaultBioProfileIndexProcessor processor = new DefaultBioProfileIndexProcessor();
    processor.setGdacNode("TEST");
    processor.setAccessPathDocumentation(accessPathDocumentation);
    processor.setMetadataStore(metadataStore);
    processor.setOutputFileStore(outputFileStore);
    processor.setPageSize(2);
    processor.setLocalTempDir(localTempDir);
    processor.setNowGenerator(() -> now);

    when(metadataStore.getBioProfileIndexPage(eq(DefaultIndexPageRequest.builder().withPageNumber(1).withPageSize(2).build())))
        .thenReturn(DefaultMetadataRecordPage.builder()
            .withTotalRecords(3)
            .withPage(Arrays.asList(
                MetadataRecord.builder()
                    .withFile("aoml/1901378/profiles/BD1901378_001.nc")
                    .withDate(Instant.parse("2009-10-12T13:39:09Z"))
                    .withLatitude(31.6897)
                    .withLongitude(-64.2017)
                    .withOcean(ArgoOcean.ATLANTIC_OCEAN)
                    .withProfilerType("846")
                    .withInstitution("AO")
                    .withParameters(Arrays.asList("PRES", "TEMP_DOXY", "BPHASE_DOXY", "DOXY", "UV_INTENSITY_DARK_NITRATE", "UV_INTENSITY_NITRATE", "NITRATE"))
                    .withParameterDataMode("RRRDRRD")
                    .withDateUpdate(Instant.parse("2026-06-24T06:05:15Z"))
                    .build(),
                MetadataRecord.builder()
                    .withFile("aoml/1901378/profiles/BD1901378_002.nc")
                    .withDate(Instant.parse("2009-10-13T13:05:57Z"))
                    .withLatitude(31.7562)
                    .withLongitude(-64.2821)
                    .withOcean(ArgoOcean.ATLANTIC_OCEAN)
                    .withProfilerType("846")
                    .withInstitution("AO")
                    .withParameters(Arrays.asList("PRES", "TEMP_DOXY", "BPHASE_DOXY", "DOXY", "UV_INTENSITY_DARK_NITRATE", "UV_INTENSITY_NITRATE", "NITRATE"))
                    .withParameterDataMode("RRRDRRD")
                    .withDateUpdate(Instant.parse("2026-06-24T06:05:15Z"))
                    .build()
            ))
            .withIndexPageRequest(DefaultIndexPageRequest.builder().withPageNumber(1).withPageSize(2).build())
            .build());

    when(metadataStore.getBioProfileIndexPage(eq(DefaultIndexPageRequest.builder().withPageNumber(2).withPageSize(2).build())))
        .thenReturn(DefaultMetadataRecordPage.builder()
            .withTotalRecords(3)
            .withPage(Arrays.asList(
                MetadataRecord.builder()
                    .withFile("aoml/1901378/profiles/BD1901378_003.nc")
                    .withDate(Instant.parse("2009-10-14T13:36:42Z"))
                    .withLatitude(31.741)
                    .withLongitude(-64.323)
                    .withOcean(ArgoOcean.ATLANTIC_OCEAN)
                    .withProfilerType("846")
                    .withInstitution("AO")
                    .withParameters(Arrays.asList("PRES", "TEMP_DOXY", "BPHASE_DOXY", "DOXY", "UV_INTENSITY_DARK_NITRATE", "UV_INTENSITY_NITRATE", "NITRATE"))
                    .withParameterDataMode("RRRDRRD")
                    .withDateUpdate(Instant.parse("2026-06-24T06:05:15Z"))
                    .build()
            ))
            .withIndexPageRequest(DefaultIndexPageRequest.builder().withPageNumber(2).withPageSize(2).build())
            .build());

    processor.generateIndex();

    String expected = Files.readString(Paths.get("src/test/resources/argo_bio-profile_index.txt"), StandardCharsets.UTF_8);
    assertTrue(Files.exists(outputDir.resolve("argo_bio-profile_index.txt")));
    assertEquals(expected, Files.readString(outputDir.resolve("argo_bio-profile_index.txt"), StandardCharsets.UTF_8));

    assertTrue(Files.exists(outputDir.resolve("argo_bio-profile_index.txt.gz")));
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    try(InputStream in = new GZIPInputStream(Files.newInputStream(outputDir.resolve("argo_bio-profile_index.txt.gz")))) {
      IOUtils.copy(in, out);
    }

    assertEquals(expected, out.toString(StandardCharsets.UTF_8));



  }

}