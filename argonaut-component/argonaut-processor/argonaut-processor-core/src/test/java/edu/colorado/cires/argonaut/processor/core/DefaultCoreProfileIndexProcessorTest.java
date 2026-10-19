package edu.colorado.cires.argonaut.processor.core;


import edu.colorado.cires.argonaut.file.core.FileStore;
import edu.colorado.cires.argonaut.messaging.core.databind.ArgoOcean;
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
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.GZIPInputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DefaultCoreProfileIndexProcessorTest {

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

    DefaultCoreProfileIndexProcessor processor = new DefaultCoreProfileIndexProcessor();
    processor.setGdacNode("TEST");
    processor.setAccessPathDocumentation(accessPathDocumentation);
    processor.setMetadataStore(metadataStore);
    processor.setOutputFileStore(outputFileStore);
    processor.setPageSize(2);
    processor.setLocalTempDir(localTempDir);
    processor.setNowGenerator(() -> now);

    when(metadataStore.getCoreProfileIndexPage(eq(DefaultIndexPageRequest.builder().withPageNumber(1).withPageSize(2).build())))
        .thenReturn(DefaultMetadataRecordPage.builder()
            .withTotalRecords(3)
            .withPage(Arrays.asList(
                MetadataRecord.builder()
                    .withFile("aoml/13857/profiles/D13857_001.nc")
                    .withDate(Instant.parse("1997-07-29T20:03:00Z"))
                    .withLatitude(0.267)
                    .withLongitude(-16.032)
                    .withOcean(ArgoOcean.ATLANTIC_OCEAN)
                    .withProfilerType("845")
                    .withInstitution("AO")
                    .withDateUpdate(Instant.parse("2026-02-20T14:35:29Z"))
                    .build(),
                MetadataRecord.builder()
                    .withFile("aoml/13857/profiles/D13857_002.nc")
                    .withDate(Instant.parse("1997-08-09T19:21:12Z"))
                    .withLatitude(0.072)
                    .withLongitude(-17.659)
                    .withOcean(ArgoOcean.ATLANTIC_OCEAN)
                    .withProfilerType("845")
                    .withInstitution("AO")
                    .withDateUpdate(Instant.parse("2026-02-20T14:35:29Z"))
                    .build()
            ))
            .withIndexPageRequest(DefaultIndexPageRequest.builder().withPageNumber(1).withPageSize(2).build())
            .build());

    when(metadataStore.getCoreProfileIndexPage(eq(DefaultIndexPageRequest.builder().withPageNumber(2).withPageSize(2).build())))
        .thenReturn(DefaultMetadataRecordPage.builder()
            .withTotalRecords(3)
            .withPage(Arrays.asList(
                MetadataRecord.builder()
                    .withFile("aoml/13857/profiles/D13857_003.nc")
                    .withDate(Instant.parse("1997-08-20T18:45:45Z"))
                    .withLatitude(0.543)
                    .withLongitude(-19.622)
                    .withOcean(ArgoOcean.ATLANTIC_OCEAN)
                    .withProfilerType("845")
                    .withInstitution("AO")
                    .withDateUpdate(Instant.parse("2026-02-20T14:35:30Z"))
                    .build()
            ))
            .withIndexPageRequest(DefaultIndexPageRequest.builder().withPageNumber(2).withPageSize(2).build())
            .build());

    processor.generateIndex();

    String expected = Files.readString(Paths.get("src/test/resources/ar_index_global_prof.txt"), StandardCharsets.UTF_8);
    assertTrue(Files.exists(outputDir.resolve("ar_index_global_prof.txt")));
    assertEquals(expected, Files.readString(outputDir.resolve("ar_index_global_prof.txt"), StandardCharsets.UTF_8));

    assertTrue(Files.exists(outputDir.resolve("ar_index_global_prof.txt.gz")));
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    try(InputStream in = new GZIPInputStream(Files.newInputStream(outputDir.resolve("ar_index_global_prof.txt.gz")))) {
      IOUtils.copy(in, out);
    }

    assertEquals(expected, out.toString(StandardCharsets.UTF_8));
  }

}