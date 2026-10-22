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

public class DefaultWeeklyMetaIndexProcessorTest {

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

    Instant now = Instant.parse("2026-10-22T03:20:01Z");

    DefaultWeeklyMetaIndexProcessor processor = new DefaultWeeklyMetaIndexProcessor();
    processor.setGdacNode("TEST");
    processor.setAccessPathDocumentation(accessPathDocumentation);
    processor.setMetadataStore(metadataStore);
    processor.setOutputFileStore(outputFileStore);
    processor.setPageSize(2);
    processor.setLocalTempDir(localTempDir);
    processor.setNowGenerator(() -> now);

    when(metadataStore.getWeeklyMetaIndexPage(eq(DefaultIndexPageRequest.builder().withPageNumber(1).withPageSize(2).build())))
        .thenReturn(DefaultMetadataRecordPage.builder()
            .withTotalRecords(3)
            .withPage(Arrays.asList(
                MetadataRecord.builder()
                        .withFile("aoml/1901514/1901514_meta.nc")
                        .withProfilerType("846")
                        .withInstitution("AO")
                        .withDateUpdate(Instant.parse("2026-10-15T20:08:48Z"))
                        .build(),
                MetadataRecord.builder()
                        .withFile("aoml/1901615/1901615_meta.nc")
                        .withProfilerType("869")
                        .withInstitution("AO")
                        .withDateUpdate(Instant.parse("2026-10-16T20:05:57Z"))
                        .build()
            ))
            .withIndexPageRequest(DefaultIndexPageRequest.builder().withPageNumber(1).withPageSize(2).build())
            .build());

    when(metadataStore.getWeeklyMetaIndexPage(eq(DefaultIndexPageRequest.builder().withPageNumber(2).withPageSize(2).build())))
        .thenReturn(DefaultMetadataRecordPage.builder()
            .withTotalRecords(3)
            .withPage(Arrays.asList(
                    MetadataRecord.builder()
                            .withFile("aoml/1902058/1902058_meta.nc")
                            .withProfilerType("869")
                            .withInstitution("AO")
                            .withDateUpdate(Instant.parse("2026-10-18T20:04:52Z"))
                            .build()
            ))
            .withIndexPageRequest(DefaultIndexPageRequest.builder().withPageNumber(2).withPageSize(2).build())
            .build());

    processor.generateIndex();

    String expected = Files.readString(Paths.get("src/test/resources/ar_index_this_week_meta.txt"), StandardCharsets.UTF_8);
    assertTrue(Files.exists(outputDir.resolve("ar_index_this_week_meta.txt")));
    assertEquals(expected, Files.readString(outputDir.resolve("ar_index_this_week_meta.txt"), StandardCharsets.UTF_8));

    assertTrue(Files.exists(outputDir.resolve("ar_index_this_week_meta.txt.gz")));
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    try(InputStream in = new GZIPInputStream(Files.newInputStream(outputDir.resolve("ar_index_this_week_meta.txt.gz")))) {
      IOUtils.copy(in, out);
    }

    assertEquals(expected, out.toString(StandardCharsets.UTF_8));
  }

}