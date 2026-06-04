package edu.colorado.cires.argonaut.messaging.core.util;

import edu.colorado.cires.argonaut.messaging.core.databind.AuditEventProcessor;
import edu.colorado.cires.argonaut.messaging.core.databind.AuditMessage;
import edu.colorado.cires.argonaut.messaging.core.databind.AuditMessage.EventType;
import edu.colorado.cires.argonaut.messaging.core.databind.TracedMessage;
import java.time.Instant;
import java.util.UUID;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ObjectNode;

public final class ErrorMessageAuditUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(ErrorMessageAuditUtils.class);

  public static AuditMessage buildErrorMessageAuditMessage(Exception exception, Object processingMessage, AuditEventProcessor processor, JsonMapper jsonMapper) {
    LOGGER.info("Preparing error audit message for body: {}", processingMessage);

    AuditMessage auditMessage = AuditMessage.builder()
        .withEventType(EventType.ERROR)
        .withStackTrace(ExceptionUtils.getStackTrace(exception))
        .withMessage(ExceptionUtils.getRootCauseMessage(exception))
        .withTimestamp(Instant.now())
        .withProcessor(processor)
        .build();
    if (processingMessage instanceof TracedMessage) {
      TracedMessage message = (TracedMessage) processingMessage;
      auditMessage = AuditMessage.builder(auditMessage)
          .withTraceId(message.getTraceId())
          .withDac(message.getDac())
          .build();
    } else if (processingMessage instanceof String) {
      JsonNode json = jsonMapper.readTree((String) processingMessage);
      String traceId = json.get("traceId").asString();
      String dac = json.get("dac").asString();
      auditMessage = AuditMessage.builder(auditMessage)
          .withTraceId(UUID.fromString(traceId))
          .withDac(dac)
          .build();
    } else {
      auditMessage = AuditMessage.builder(auditMessage)
          .withTraceId(UUID.randomUUID())
          .withDac("unknown")
          .build();
    }

    LOGGER.info("Error audit message: {}", auditMessage);

    return auditMessage;
  }

  private ErrorMessageAuditUtils() {

  }
}
