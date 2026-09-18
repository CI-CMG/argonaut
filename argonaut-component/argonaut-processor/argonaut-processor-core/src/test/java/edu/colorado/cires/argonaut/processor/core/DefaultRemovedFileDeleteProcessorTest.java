package edu.colorado.cires.argonaut.processor.core;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.colorado.cires.argonaut.file.core.FileStore;
import edu.colorado.cires.argonaut.messaging.core.databind.ArgoFileType;
import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord;
import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord.Action;
import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord.FileStatus;
import edu.colorado.cires.argonaut.messaging.core.databind.ProfileOperation;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Collections;
import java.util.UUID;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DefaultRemovedFileDeleteProcessorTest {


  private static final Path tempDir = Paths.get("target/temp");

  @BeforeEach
  public void setup() throws Exception {
    FileUtils.deleteQuietly(tempDir.toFile());
    Files.createDirectories(tempDir);
  }

  @AfterEach
  public void cleanup() throws Exception {
    FileUtils.deleteQuietly(tempDir.toFile());
  }

  @Test
  public void test() throws Exception {
    UUID traceId = UUID.randomUUID();
    FileStore fileStore = new TestFileStore(tempDir);
    Path testFile = Files.createFile(Files.createDirectories(tempDir.resolve("etc/removed/aoml")).resolve("BD13855_001.nc"));
    assertTrue(Files.exists(testFile));

    Instant now = Instant.now();

    DefaultRemovedFileDeleteProcessor processor = new DefaultRemovedFileDeleteProcessor();
    processor.setOutputFileStore(fileStore);
    processor.setTimestampSupplier(() -> now);

    MetadataRecord record = processor.delete(ProfileOperation.builder()
        .withTraceId(traceId)
        .withDac("aoml")
        .withFloatId("13855")
        .withFiles(Collections.singletonList(
            MetadataRecord.builder()
                .withFileType(ArgoFileType.PROFILE_BIOCHEMICAL)
                .withFile("aoml/13855/profiles/BD13855_001.nc")
                .withFileName("BD13855_001.nc")
                .withFileStatus(FileStatus.REMOVED)
                .withActionTimestamp(Instant.parse("2025-10-10T14:00:00Z"))
                .build()
        ))
        .build());

    assertEquals(MetadataRecord.builder()
        .withTraceId(traceId)
        .withFileStatus(FileStatus.REMOVED)
        .withFile("aoml/13855/profiles/BD13855_001.nc")
        .withFileName("BD13855_001.nc")
        .withDac("aoml")
        .withFloatId("13855")
        .withActionTimestamp(now)
        .withAction(Action.DELETE_REMOVED_FILE)
        .withFileType(ArgoFileType.PROFILE_BIOCHEMICAL)
        .build(), record);

    assertFalse(Files.exists(testFile));

  }

}