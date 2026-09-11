package edu.colorado.cires.argonaut.processor.core;


import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.colorado.cires.argonaut.messaging.core.databind.ArgoFileType;
import edu.colorado.cires.argonaut.messaging.core.databind.DacSubmittedFileMessage;
import edu.colorado.cires.argonaut.messaging.core.databind.NcSubmissionMessage;
import edu.colorado.cires.argonaut.messaging.core.databind.NcSubmissionMessage.Operation;
import edu.colorado.cires.argonaut.messaging.core.queue.MessageSender;
import edu.colorado.cires.argonaut.messaging.core.util.ArgonautJsonMapperFactory;
import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

public class SubmissionRoutingProcessorTest {

  @Test
  public void testRemoval() {

    MessageSender messageSender = mock(MessageSender.class);
    TarballSubmissionProcessor tarballSubmissionProcessor = mock(TarballSubmissionProcessor.class);
    SubmissionProcessor submissionProcessor = mock(SubmissionProcessor.class);
    JsonMapper jsonMapper = ArgonautJsonMapperFactory.getJsonMapper();
    String submitDataQueue = "submitDataQueue";
    String submitRemovalQueue = "submitRemovalQueue";

    DacSubmittedFileMessage message = DacSubmittedFileMessage.builder()
        .withTraceId(UUID.randomUUID())
        .withFileName("aoml_removal.txt")
        .withDac("aoml")
        .withTimestamp(Instant.now())
        .withPath("foo/bar/aoml_removal.txt")
        .build();

    NcSubmissionMessage resultMessage = NcSubmissionMessage.builder()
        .withOperation(Operation.REMOVE)
        .withFileName("aoml_removal.txt")
        .withFileType(ArgoFileType.REMOVAL_TXT)
        .withDac("aoml")
        .withTimestamp(message.getTimestamp())
        .withTraceId(message.getTraceId())
        .build();


    when(submissionProcessor.moveToProcessing(message)).thenReturn(Optional.of(resultMessage));


    SubmissionRoutingProcessor processor = new SubmissionRoutingProcessor();
    processor.setMessageSender(messageSender);
    processor.setTarballSubmissionProcessor(tarballSubmissionProcessor);
    processor.setSubmissionProcessor(submissionProcessor);
    processor.setJsonMapper(jsonMapper);
    processor.setSubmitDataQueue(submitDataQueue);
    processor.setSubmitRemovalQueue(submitRemovalQueue);

    processor.choice(message);

    verify(messageSender).sendJson(eq(submitRemovalQueue), eq(jsonMapper.writeValueAsString(resultMessage)));

  }

  @Test
  public void testTarGzRemoval() {

    MessageSender messageSender = mock(MessageSender.class);
    TarballSubmissionProcessor tarballSubmissionProcessor = mock(TarballSubmissionProcessor.class);
    SubmissionProcessor submissionProcessor = mock(SubmissionProcessor.class);
    JsonMapper jsonMapper = ArgonautJsonMapperFactory.getJsonMapper();
    String submitDataQueue = "submitDataQueue";
    String submitRemovalQueue = "submitRemovalQueue";

    DacSubmittedFileMessage message = DacSubmittedFileMessage.builder()
        .withTraceId(UUID.randomUUID())
        .withFileName("aoml_foo_bar.tar.gz")
        .withDac("aoml")
        .withTimestamp(Instant.now())
        .withPath("foo/bar/aoml_foo_bar.tar.gz")
        .build();

    NcSubmissionMessage resultMessage = NcSubmissionMessage.builder()
        .withOperation(Operation.REMOVE)
        .withFileName("aoml_removal.txt")
        .withFileType(ArgoFileType.REMOVAL_TXT)
        .withDac("aoml")
        .withTimestamp(message.getTimestamp())
        .withTraceId(message.getTraceId())
        .build();

    NcSubmissionMessage resultMessage2 = NcSubmissionMessage.builder()
        .withOperation(Operation.ADD)
        .withFileName("D1234_001.nc")
        .withFileType(ArgoFileType.PROFILE_CORE)
        .withDac("aoml")
        .withTimestamp(message.getTimestamp())
        .withTraceId(message.getTraceId())
        .build();


    when(tarballSubmissionProcessor.untarAndMoveToProcessing(message)).thenReturn(Arrays.asList(resultMessage, resultMessage2));


    SubmissionRoutingProcessor processor = new SubmissionRoutingProcessor();
    processor.setMessageSender(messageSender);
    processor.setTarballSubmissionProcessor(tarballSubmissionProcessor);
    processor.setSubmissionProcessor(submissionProcessor);
    processor.setJsonMapper(jsonMapper);
    processor.setSubmitDataQueue(submitDataQueue);
    processor.setSubmitRemovalQueue(submitRemovalQueue);

    processor.choice(message);

    verify(messageSender).sendJson(eq(submitRemovalQueue), eq(jsonMapper.writeValueAsString(resultMessage)));
    verify(messageSender).sendJson(eq(submitDataQueue), eq(jsonMapper.writeValueAsString(resultMessage2)));

  }

}