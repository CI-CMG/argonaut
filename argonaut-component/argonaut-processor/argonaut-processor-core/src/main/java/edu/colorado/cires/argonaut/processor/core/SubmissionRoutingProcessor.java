package edu.colorado.cires.argonaut.processor.core;

import edu.colorado.cires.argonaut.messaging.core.databind.DacSubmittedFileMessage;
import edu.colorado.cires.argonaut.messaging.core.databind.NcSubmissionMessage;
import edu.colorado.cires.argonaut.messaging.core.queue.MessageSender;
import java.util.List;
import tools.jackson.databind.json.JsonMapper;

public class SubmissionRoutingProcessor {

  private MessageSender messageSender;
  private TarballSubmissionProcessor tarballSubmissionProcessor;
  private SubmissionProcessor submissionProcessor;
  private JsonMapper jsonMapper;
  private String submitDataQueue;
  private String submitRemovalQueue;
  private AuditSender auditSender;

  public void setSubmissionProcessor(SubmissionProcessor submissionProcessor) {
    this.submissionProcessor = submissionProcessor;
  }

  public void setMessageSender(MessageSender messageSender) {
    this.messageSender = messageSender;
  }

  public void setTarballSubmissionProcessor(TarballSubmissionProcessor tarballSubmissionProcessor) {
    this.tarballSubmissionProcessor = tarballSubmissionProcessor;
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

  public void setAuditSender(AuditSender auditSender) {
    this.auditSender = auditSender;
  }

  public void choice(DacSubmittedFileMessage message) {
    if (message.getPath().endsWith(".tar.gz")) {
      List<NcSubmissionMessage> submissionMessages = tarballSubmissionProcessor.untarAndMoveToProcessing(message);
      for (NcSubmissionMessage submissionMessage : submissionMessages) {
        if (submissionMessage.getFileName().endsWith("_removal.txt")) {
          submitRemoval(submissionMessage);
        } else {
          submitUpdate(submissionMessage);
        }
      }
    } else if (message.getPath().endsWith("_removal.txt")) {
      submissionProcessor.moveToProcessing(message).ifPresent(this::submitRemoval);
    } else if (message.getPath().endsWith(".nc")) {
      submissionProcessor.moveToProcessing(message).ifPresent(this::submitUpdate);
    } else {
      throw new UnsupportedOperationException("Unsupported file type: " + message.getPath());
    }
  }

  private void submitRemoval(NcSubmissionMessage submissionMessage) {
    messageSender.sendJson(submitRemovalQueue, jsonMapper.writeValueAsString(submissionMessage));
  }

  private void submitUpdate(NcSubmissionMessage submissionMessage) {
    auditSender.updateStarted(submissionMessage);
    messageSender.sendJson(submitDataQueue, jsonMapper.writeValueAsString(submissionMessage));
  }

}
