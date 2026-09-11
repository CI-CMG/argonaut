package edu.colorado.cires.argonaut.processor.core;


import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.colorado.cires.argonaut.file.core.FileStore;
import edu.colorado.cires.argonaut.messaging.core.databind.ArgoFileType;
import edu.colorado.cires.argonaut.messaging.core.databind.NcSubmissionMessage;
import edu.colorado.cires.argonaut.messaging.core.databind.NcSubmissionMessage.Operation;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.UUID;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DefaultFileMoveProcessorTest {

  private static final Path submitDir = Paths.get("target/submit");
  private static final Path processingDir = Paths.get("target/processing");
  private static final Path outputDir = Paths.get("target/output");

  @AfterEach
  @BeforeEach
  public void before() throws Exception {
    FileUtils.deleteQuietly(outputDir.toFile());
    FileUtils.deleteQuietly(submitDir.toFile());
    FileUtils.deleteQuietly(processingDir.toFile());
  }

  @Test
  public void testRemove() throws Exception {
    FileStore submissionFileStore = new TestFileStore(submitDir);
    FileStore processingFileStore = new TestFileStore(processingDir);
    FileStore outputFileStore = new TestFileStore(outputDir);

    DefaultFileMoveProcessor processor = new DefaultFileMoveProcessor();
    processor.setOutputFileStore(outputFileStore);
    processor.setSubmissionFileStore(submissionFileStore);
    processor.setProcessingFileStore(processingFileStore);

    Path outputFile = outputDir.resolve("dac/aoml/1234/profiles/R1234_001.nc");
    Files.createDirectories(outputFile.getParent());
    Files.createFile(outputFile);

    NcSubmissionMessage message = NcSubmissionMessage.builder()
        .withOperation(Operation.REMOVE)
        .withFileName("R1234_001.nc")
        .withFileType(ArgoFileType.PROFILE_CORE)
        .withDac("aoml")
        .withTimestamp(Instant.now())
        .withTraceId(UUID.randomUUID())
        .withFloatId("1234")
        .build();

    processor.moveFile(message);

    assertFalse(Files.exists(outputFile));
    assertTrue(Files.exists(outputDir.resolve("etc/removed/aoml/R1234_001.nc")));


  }

}