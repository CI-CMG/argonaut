package edu.colorado.cires.argonaut.processor.core;

import edu.colorado.cires.argonaut.messaging.core.databind.AuditMessage;
import edu.colorado.cires.argonaut.messaging.core.databind.AuditMessage.EventType;
import edu.colorado.cires.argonaut.messaging.core.databind.DacSubmittedFileMessage;
import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord;
import edu.colorado.cires.argonaut.messaging.core.databind.NcSubmissionMessage;
import edu.colorado.cires.argonaut.messaging.core.databind.ProfileOperation;
import edu.colorado.cires.argonaut.messaging.core.queue.MessageSender;
import edu.colorado.cires.argonaut.messaging.core.databind.AuditEventProcessor;
import java.time.Instant;
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
                .withMessage("received file " + message.getFileName())
                .withProcessor(AuditEventProcessor.FILE_RECEIVED)
                .withTimestamp(Instant.now())
                .withTraceId(message.getTraceId())
            .build()));
  }

  public void validationStarted(NcSubmissionMessage message) {
    messageSender.sendJson(
        auditQueue,
        jsonMapper.writeValueAsString(AuditMessage.builder()
            .withDac(message.getDac())
            .withEventType(EventType.INFO)
            .withFileName(message.getFileName())
            .withMessage("starting validation")
            .withProcessor(AuditEventProcessor.VALIDATION)
            .withTimestamp(Instant.now())
            .withTraceId(message.getTraceId())
            .build()));
  }

  public void validationSuccess(NcSubmissionMessage message) {
    messageSender.sendJson(
        auditQueue,
        jsonMapper.writeValueAsString(AuditMessage.builder()
            .withDac(message.getDac())
            .withEventType(EventType.INFO)
            .withFileName(message.getFileName())
            .withMessage("validation successful")
            .withProcessor(AuditEventProcessor.VALIDATION)
            .withTimestamp(Instant.now())
            .withTraceId(message.getTraceId())
            .build()));
  }

  public void validationFailure(NcSubmissionMessage message) {
    messageSender.sendJson(
        auditQueue,
        jsonMapper.writeValueAsString(AuditMessage.builder()
            .withDac(message.getDac())
            .withEventType(EventType.ERROR)
            .withFileName(message.getFileName())
            .withMessage("validation failed")
            .withProcessor(AuditEventProcessor.VALIDATION)
            .withTimestamp(Instant.now())
            .withTraceId(message.getTraceId())
            .withStackTrace(String.join("|", message.getValidationErrors()))
            .build()));
  }

  public void movingFileToOutput(NcSubmissionMessage message) {
    messageSender.sendJson(
        auditQueue,
        jsonMapper.writeValueAsString(AuditMessage.builder()
            .withDac(message.getDac())
            .withEventType(EventType.INFO)
            .withFileName(message.getFileName())
            .withMessage("starting adding file to access location")
            .withProcessor(AuditEventProcessor.FILE_STORE)
            .withTimestamp(Instant.now())
            .withTraceId(message.getTraceId())
            .build()));
  }

  public void fileMovedToOutput(MetadataRecord message) {
    messageSender.sendJson(
        auditQueue,
        jsonMapper.writeValueAsString(AuditMessage.builder()
            .withDac(message.getDac())
            .withEventType(EventType.INFO)
            .withFileName(message.getFileName())
            .withMessage("file added to access location: " + message.getFile())
            .withProcessor(AuditEventProcessor.FILE_STORE)
            .withTimestamp(Instant.now())
            .withTraceId(message.getTraceId())
            .build()));
  }

  public void updatingIndex(MetadataRecord message) {
    messageSender.sendJson(
        auditQueue,
        jsonMapper.writeValueAsString(AuditMessage.builder()
            .withDac(message.getDac())
            .withEventType(EventType.INFO)
            .withFileName(message.getFileName())
            .withMessage("started updating index")
            .withProcessor(AuditEventProcessor.INDEXING)
            .withTimestamp(Instant.now())
            .withTraceId(message.getTraceId())
            .build()));
  }

  public void updatedIndex(MetadataRecord message) {
    messageSender.sendJson(
        auditQueue,
        jsonMapper.writeValueAsString(AuditMessage.builder()
            .withDac(message.getDac())
            .withEventType(EventType.INFO)
            .withFileName(message.getFileName())
            .withMessage("updated index")
            .withProcessor(AuditEventProcessor.INDEXING)
            .withTimestamp(Instant.now())
            .withTraceId(message.getTraceId())
            .build()));
  }


  public void generatingProfileMerge(ProfileOperation message) {
    messageSender.sendJson(
        auditQueue,
        jsonMapper.writeValueAsString(AuditMessage.builder()
            .withDac(message.getDac())
            .withEventType(EventType.INFO)
            .withFileName(message.getFileName())
            .withMessage("started generating multi-profile merge")
            .withProcessor(AuditEventProcessor.FILE_GENERATION)
            .withTimestamp(Instant.now())
            .withTraceId(message.getTraceId())
            .build()));
  }

  public void generatedProfileMerge(ProfileOperation message) {
    messageSender.sendJson(
        auditQueue,
        jsonMapper.writeValueAsString(AuditMessage.builder()
            .withDac(message.getDac())
            .withEventType(EventType.INFO)
            .withFileName(message.getFileName())
            .withMessage("generated multi-profile merge")
            .withProcessor(AuditEventProcessor.FILE_GENERATION)
            .withTimestamp(Instant.now())
            .withTraceId(message.getTraceId())
            .build()));
  }


  public void generatingSyntheticProfile(ProfileOperation message) {
    messageSender.sendJson(
        auditQueue,
        jsonMapper.writeValueAsString(AuditMessage.builder()
            .withDac(message.getDac())
            .withEventType(EventType.INFO)
            .withFileName(message.getFileName())
            .withMessage("started generating synthetic profile")
            .withProcessor(AuditEventProcessor.FILE_GENERATION)
            .withTimestamp(Instant.now())
            .withTraceId(message.getTraceId())
            .build()));
  }

  public void generatedSyntheticProfile(ProfileOperation message) {
    messageSender.sendJson(
        auditQueue,
        jsonMapper.writeValueAsString(AuditMessage.builder()
            .withDac(message.getDac())
            .withEventType(EventType.INFO)
            .withFileName(message.getFileName())
            .withMessage("generated synthetic profile")
            .withProcessor(AuditEventProcessor.FILE_GENERATION)
            .withTimestamp(Instant.now())
            .withTraceId(message.getTraceId())
            .build()));
  }


}
