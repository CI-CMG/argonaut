package edu.colorado.cires.argonaut.processor.core;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import edu.colorado.cires.argonaut.file.core.FileStore;
import edu.colorado.cires.argonaut.messaging.core.databind.ArgoFileType;
import edu.colorado.cires.argonaut.messaging.core.databind.NcSubmissionMessage;
import edu.colorado.cires.argonaut.messaging.core.databind.NcSubmissionMessage.Operation;
import edu.colorado.cires.argonaut.messaging.core.queue.MessageSender;
import edu.colorado.cires.argonaut.messaging.core.util.ArgonautJsonMapperFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.UUID;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

public class DefaultRemovalFileMessageSplitterTest {
  private static final Path processingDir = Paths.get("target/processing");

  @AfterEach
  @BeforeEach
  public void before() throws Exception {
    FileUtils.deleteQuietly(processingDir.toFile());
  }

  @Test
  public void test() throws Exception {
    Instant now = Instant.now();
    UUID traceId = UUID.randomUUID();

    Path removalTxt = processingDir.resolve("dac").resolve("aoml").resolve(now.toString()).resolve("aoml_removal.txt");
    Files.createDirectories(removalTxt.getParent());
    Files.copy(Paths.get("src/test/resources/aoml_removal.txt"), removalTxt);

    MessageSender messageSender = mock(MessageSender.class);
    String fileMoveQueue = "fileMoveQueue";
    JsonMapper jsonMapper = ArgonautJsonMapperFactory.getJsonMapper();

    FileStore processingFileStore = new TestFileStore(processingDir);
    DefaultRemovalFileMessageSplitter processor = new DefaultRemovalFileMessageSplitter();
    processor.setProcessingFileStore(processingFileStore);
    processor.setMessageSender(messageSender);
    processor.setFileMoveQueue(fileMoveQueue);
    processor.setJsonMapper(jsonMapper);


    NcSubmissionMessage message = NcSubmissionMessage.builder()
        .withOperation(Operation.REMOVE)
        .withFileName("aoml_removal.txt")
        .withFileType(ArgoFileType.REMOVAL_TXT)
        .withDac("aoml")
        .withTimestamp(now)
        .withTraceId(traceId)
        .build();

    processor.splitRemovalFileMessages(message);


    verify(messageSender).sendJson(eq(fileMoveQueue), eq(jsonMapper.writeValueAsString(NcSubmissionMessage.builder()
        .withOperation(Operation.REMOVE)
        .withFileName("1234_tech.nc")
        .withFileType(ArgoFileType.TECHNICAL_DATA)
        .withDac("aoml")
        .withTimestamp(now)
        .withTraceId(traceId)
        .withFloatId("1234")
        .build())));
    verify(messageSender).sendJson(eq(fileMoveQueue), eq(jsonMapper.writeValueAsString(NcSubmissionMessage.builder()
        .withOperation(Operation.REMOVE)
        .withFileName("R1234_001.nc")
        .withFileType(ArgoFileType.PROFILE_CORE)
        .withDac("aoml")
        .withTimestamp(now)
        .withTraceId(traceId)
        .withFloatId("1234")
        .build())));
    verify(messageSender).sendJson(eq(fileMoveQueue), eq(jsonMapper.writeValueAsString(NcSubmissionMessage.builder()
        .withOperation(Operation.REMOVE)
        .withFileName("BR1234_001.nc")
        .withFileType(ArgoFileType.PROFILE_BIOCHEMICAL)
        .withDac("aoml")
        .withTimestamp(now)
        .withTraceId(traceId)
        .withFloatId("1234")
        .build())));
    verify(messageSender).sendJson(eq(fileMoveQueue), eq(jsonMapper.writeValueAsString(NcSubmissionMessage.builder()
        .withOperation(Operation.REMOVE)
        .withFileName("1234_Rtraj.nc")
        .withFileType(ArgoFileType.TRAJECTORY)
        .withDac("aoml")
        .withTimestamp(now)
        .withTraceId(traceId)
        .withFloatId("1234")
        .build())));
    verify(messageSender).sendJson(eq(fileMoveQueue), eq(jsonMapper.writeValueAsString(NcSubmissionMessage.builder()
        .withOperation(Operation.REMOVE)
        .withFileName("1234_meta.nc")
        .withFileType(ArgoFileType.METADATA)
        .withDac("aoml")
        .withTimestamp(now)
        .withTraceId(traceId)
        .withFloatId("1234")
        .build())));

  }
}