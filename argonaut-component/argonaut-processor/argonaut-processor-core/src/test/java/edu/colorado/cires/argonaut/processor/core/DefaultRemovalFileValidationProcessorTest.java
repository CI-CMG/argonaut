package edu.colorado.cires.argonaut.processor.core;


import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.colorado.cires.argonaut.file.core.FileStore;
import edu.colorado.cires.argonaut.messaging.core.databind.ArgoFileType;
import edu.colorado.cires.argonaut.messaging.core.databind.NcSubmissionMessage;
import edu.colorado.cires.argonaut.messaging.core.databind.NcSubmissionMessage.Operation;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DefaultRemovalFileValidationProcessorTest {

  private static final Path processingDir = Paths.get("target/processing");

  @AfterEach
  @BeforeEach
  public void before() throws Exception {
    FileUtils.deleteQuietly(processingDir.toFile());
  }

  @Test
  public void testValid() throws Exception {
    Instant now = Instant.now();
    UUID traceId = UUID.randomUUID();

    Path removalTxt = processingDir.resolve("dac").resolve("aoml").resolve(now.toString()).resolve("aoml_removal.txt");
    Files.createDirectories(removalTxt.getParent());
    Files.copy(Paths.get("src/test/resources/aoml_removal.txt"), removalTxt);

    FileStore processingFileStore = new TestFileStore(processingDir);
    DefaultRemovalFileValidationProcessor processor = new DefaultRemovalFileValidationProcessor();
    processor.setProcessingFileStore(processingFileStore);

    NcSubmissionMessage message = NcSubmissionMessage.builder()
        .withOperation(Operation.REMOVE)
        .withFileName("aoml_removal.txt")
        .withFileType(ArgoFileType.REMOVAL_TXT)
        .withDac("aoml")
        .withTimestamp(now)
        .withTraceId(traceId)
        .build();

    NcSubmissionMessage result = processor.validate(message);

    assertEquals(message, result);

  }

  @Test
  public void testInvalidFileName() throws Exception {
    Instant now = Instant.now();
    UUID traceId = UUID.randomUUID();

    Path removalTxt = processingDir.resolve("dac").resolve("aoml").resolve(now.toString()).resolve("fake_removal.txt");
    Files.createDirectories(removalTxt.getParent());
    Files.copy(Paths.get("src/test/resources/aoml_removal.txt"), removalTxt);

    FileStore processingFileStore = new TestFileStore(processingDir);
    DefaultRemovalFileValidationProcessor processor = new DefaultRemovalFileValidationProcessor();
    processor.setProcessingFileStore(processingFileStore);

    NcSubmissionMessage message = NcSubmissionMessage.builder()
        .withOperation(Operation.REMOVE)
        .withFileName("fake_removal.txt")
        .withFileType(ArgoFileType.REMOVAL_TXT)
        .withDac("aoml")
        .withTimestamp(now)
        .withTraceId(traceId)
        .build();

    NcSubmissionMessage result = processor.validate(message);

    assertEquals(NcSubmissionMessage.builder(message)
        .withValidationErrors(Collections.singletonList("file name does not start with DAC identifier: 'fake_removal.txt', aoml"))
        .build(), result);

  }

  @Test
  public void testInvalidData() throws Exception {
    Instant now = Instant.now();
    UUID traceId = UUID.randomUUID();

    Path removalTxt = processingDir.resolve("dac").resolve("aoml").resolve(now.toString()).resolve("aoml_removal.txt");
    Files.createDirectories(removalTxt.getParent());
    Files.copy(Paths.get("src/test/resources/bad_removal.txt"), removalTxt);

    FileStore processingFileStore = new TestFileStore(processingDir);
    DefaultRemovalFileValidationProcessor processor = new DefaultRemovalFileValidationProcessor();
    processor.setProcessingFileStore(processingFileStore);

    NcSubmissionMessage message = NcSubmissionMessage.builder()
        .withOperation(Operation.REMOVE)
        .withFileName("aoml_removal.txt")
        .withFileType(ArgoFileType.REMOVAL_TXT)
        .withDac("aoml")
        .withTimestamp(now)
        .withTraceId(traceId)
        .build();

    NcSubmissionMessage result = processor.validate(message);

    assertEquals(NcSubmissionMessage.builder(message)
        .withValidationErrors(Arrays.asList("file name on line 3 does not match a known type", "file name on line 4 does not match a known type"))
        .build(), result);

  }

}