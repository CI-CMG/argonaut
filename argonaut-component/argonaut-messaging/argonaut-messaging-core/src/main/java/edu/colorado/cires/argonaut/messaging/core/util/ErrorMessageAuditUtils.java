package edu.colorado.cires.argonaut.messaging.core.util;

import edu.colorado.cires.argonaut.messaging.core.databind.AuditEventProcessor;
import edu.colorado.cires.argonaut.messaging.core.databind.AuditMessage;
import edu.colorado.cires.argonaut.messaging.core.databind.AuditMessage.EventType;
import edu.colorado.cires.argonaut.messaging.core.databind.TracedMessage;
import java.time.Instant;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

public final class ErrorMessageAuditUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(ErrorMessageAuditUtils.class);

  public static AuditMessage buildErrorMessageAuditMessage(Exception exception, Object processingMessage, AuditEventProcessor processor, JsonMapper jsonMapper) {
    LOGGER.info("Preparing error audit message for body: {}", processingMessage);

    String fileName = null;
    UUID traceId = null;
    String dac = null;

    if (processingMessage instanceof TracedMessage) {
      TracedMessage message = (TracedMessage) processingMessage;
      fileName = message.getFileName();
      traceId = message.getTraceId();
      dac = message.getDac();
    } else if (processingMessage instanceof String) {
      try {
        JsonNode json = jsonMapper.readTree((String) processingMessage);
        fileName = json.get("fileName").asString();
        dac = json.get("dac").asString();
        traceId = UUID.fromString(json.get("traceId").asString());
      } catch (Exception e) {
        LOGGER.warn("Failed to parse JSON message when creating error audit message for body: {}", processingMessage, e);
      }
    }

    AuditMessage auditMessage = AuditMessage.builder()
        .withEventType(EventType.ERROR)
        .withStackTrace(ExceptionUtils.getStackTrace(exception))
        .withMessage(ExceptionUtils.getRootCauseMessage(exception))
        .withTimestamp(Instant.now())
        .withProcessor(processor)
        .withTraceId(traceId == null ? UUID.randomUUID() : traceId)
        .withFileName(StringUtils.isBlank(fileName) ? "unknown" : fileName)
        .withDac(StringUtils.isBlank(dac) ? "unknown" : dac)
        .build();


    LOGGER.info("Error audit message: {}", auditMessage);

    return auditMessage;
  }

  private ErrorMessageAuditUtils() {

  }
}
