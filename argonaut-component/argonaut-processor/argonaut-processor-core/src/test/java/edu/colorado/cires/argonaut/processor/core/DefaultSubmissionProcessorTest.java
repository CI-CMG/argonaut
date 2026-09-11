package edu.colorado.cires.argonaut.processor.core;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.colorado.cires.argonaut.file.core.FileStore;
import edu.colorado.cires.argonaut.messaging.core.databind.ArgoFileType;
import edu.colorado.cires.argonaut.messaging.core.databind.DacSubmittedFileMessage;
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

public class DefaultSubmissionProcessorTest {

  private static final Path localTempDir = Paths.get("target/temp");
  private static final Path submitDir = Paths.get("target/submit");
  private static final Path processingDir = Paths.get("target/processing");

  @AfterEach
  @BeforeEach
  public void before() throws Exception {
    FileUtils.deleteQuietly(localTempDir.toFile());
    FileUtils.deleteQuietly(submitDir.toFile());
    FileUtils.deleteQuietly(processingDir.toFile());
  }

  @Test
  public void testRemove() throws Exception {
    FileStore submissionFileStore = new TestFileStore(submitDir);
    FileStore processingFileStore = new TestFileStore(processingDir);

    DefaultSubmissionProcessor processor = new DefaultSubmissionProcessor();
    processor.setLocalTempDir(localTempDir);
    processor.setSubmissionFileStore(submissionFileStore);
    processor.setProcessingFileStore(processingFileStore);

    UUID traceId = UUID.randomUUID();
    Instant now = Instant.now();
    Files.createDirectories(submitDir);
    Path path = submitDir.resolve("aoml_removal.txt");

    Files.createFile(path);

    DacSubmittedFileMessage message = DacSubmittedFileMessage.builder()
        .withTraceId(traceId)
        .withFileName("aoml_removal.txt")
        .withDac("aoml")
        .withTimestamp(now)
        .withPath(path.toString())
        .build();

    NcSubmissionMessage result = processor.moveToProcessing(message).get();

    assertEquals(
        NcSubmissionMessage.builder()
            .withOperation(Operation.REMOVE)
            .withFileName("aoml_removal.txt")
            .withFileType(ArgoFileType.REMOVAL_TXT)
            .withDac("aoml")
            .withTimestamp(now)
            .withTraceId(traceId)
            .build(),
        result);

    assertTrue(Files.exists(processingDir.resolve("dac").resolve("aoml").resolve(now.toString()).resolve("aoml_removal.txt")));
  }

}