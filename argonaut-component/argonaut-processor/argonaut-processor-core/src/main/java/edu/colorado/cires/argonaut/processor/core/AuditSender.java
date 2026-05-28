package edu.colorado.cires.argonaut.processor.core;

import edu.colorado.cires.argonaut.messaging.core.databind.AuditMessage;
import edu.colorado.cires.argonaut.messaging.core.databind.AuditMessage.EventType;
import edu.colorado.cires.argonaut.messaging.core.databind.DacSubmittedFileMessage;
import edu.colorado.cires.argonaut.messaging.core.queue.MessageSender;
import edu.colorado.cires.argonaut.messaging.core.databind.AuditEventProcessor;
import tools.jackson.databind.json.JsonMapper;

public class AuditSender {

  private MessageSender messageSender;
  private String auditQueue;
  private JsonMapper jsonMapper;

  public void setAuditQueue(String auditQueue) {
    this.auditQueue = auditQueue;
  }

  public void setJsonMapper(JsonMapper jsonMapper) {
    this.jsonMapper = jsonMapper;
  }

  public void setMessageSender(MessageSender messageSender) {
    this.messageSender = messageSender;
  }

  public void fileReceived(DacSubmittedFileMessage message) {
    messageSender.sendJson(
        auditQueue,
        jsonMapper.writeValueAsString(AuditMessage.builder()
                .withDac(message.getDac())
                .withEventType(EventType.INFO)
                .withFileName(message.getFileName())
                .withMessage("Received file " + message.getFileName())
                .withProcessor(AuditEventProcessor.FILE_RECEIVED)
                .withTimestamp(message.getTimestamp())
                .withTraceId(message.getTraceId())
            .build()));
  }

}
