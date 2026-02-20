package edu.colorado.cires.argonaut.processor.core;

import edu.colorado.cires.argonaut.messaging.core.databind.DacSubmittedFileMessage;
import edu.colorado.cires.argonaut.messaging.core.databind.NcSubmissionMessage;
import edu.colorado.cires.argonaut.messaging.core.databind.RemovalMessage;
import edu.colorado.cires.argonaut.messaging.core.queue.MessageSender;
import java.util.List;
import tools.jackson.databind.json.JsonMapper;

public class SubmissionRoutingProcessor {

  private MessageSender messageSender;
  private TarballSubmissionProcessor tarballSubmissionProcessor;
  private RemovalFileValidationProcessor removalFileValidationProcessor;
  private JsonMapper jsonMapper;
  private String submitDataQueue;
  private String submitRemovalQueue;

  public void setMessageSender(MessageSender messageSender) {
    this.messageSender = messageSender;
  }

  public void setTarballSubmissionProcessor(TarballSubmissionProcessor tarballSubmissionProcessor) {
    this.tarballSubmissionProcessor = tarballSubmissionProcessor;
  }

  public void setRemovalFileValidationProcessor(RemovalFileValidationProcessor removalFileValidationProcessor) {
    this.removalFileValidationProcessor = removalFileValidationProcessor;
  }

  public void setJsonMapper(JsonMapper jsonMapper) {
    this.jsonMapper = jsonMapper;
  }

  public void setSubmitDataQueue(String submitDataQueue) {
    this.submitDataQueue = submitDataQueue;
  }

  public void setSubmitRemovalQueue(String submitRemovalQueue) {
    this.submitRemovalQueue = submitRemovalQueue;
  }

  public void choice(DacSubmittedFileMessage message) {
    if (message.getPath().endsWith(".tar.gz")) {
      List<NcSubmissionMessage> submissionMessages = tarballSubmissionProcessor.untarAndMoveToProcessing(message);
      for (NcSubmissionMessage submissionMessage : submissionMessages) {
        messageSender.sendJson(submitDataQueue, jsonMapper.writeValueAsString(submissionMessage));
      }
    } else if (message.getPath().endsWith("_removal.txt")) {
      //TODO should this be a list?
      RemovalMessage removalMessage = removalFileValidationProcessor.validate(message);
      messageSender.sendJson(submitRemovalQueue, jsonMapper.writeValueAsString(removalMessage));
    } else {
      // TODO
      throw new UnsupportedOperationException("Unsupported file type: " + message.getPath());
    }
  }

}
