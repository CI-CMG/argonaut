package edu.colorado.cires.argonaut.processor.core;

import edu.colorado.cires.argonaut.messaging.core.databind.NcSubmissionMessage;
import edu.colorado.cires.argonaut.messaging.core.queue.MessageSender;
import tools.jackson.databind.json.JsonMapper;

public class DefaultValidationRouter implements ValidationResultRouter {

  private JsonMapper jsonMapper;
  private MessageSender messageSender;
  private String successQueue;
  private String failureQueue;

  public void setSuccessQueue(String successQueue) {
    this.successQueue = successQueue;
  }

  public void setFailureQueue(String failureQueue) {
    this.failureQueue = failureQueue;
  }

  public void setJsonMapper(JsonMapper jsonMapper) {
    this.jsonMapper = jsonMapper;
  }

  public void setMessageSender(MessageSender messageSender) {
    this.messageSender = messageSender;
  }

  @Override
  public void route(NcSubmissionMessage ncSubmissionMessage) {
    String queue;
    if (ncSubmissionMessage.getValidationErrors().isEmpty()) {
      queue = successQueue;
    } else {
      queue = failureQueue;
    }
    messageSender.sendJson(queue, jsonMapper.writeValueAsString(ncSubmissionMessage));
  }
}
