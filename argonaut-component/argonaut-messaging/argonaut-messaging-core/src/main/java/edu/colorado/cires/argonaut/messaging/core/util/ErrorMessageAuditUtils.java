package edu.colorado.cires.argonaut.messaging.core.util;

import edu.colorado.cires.argonaut.messaging.core.databind.AuditEventProcessor;
import edu.colorado.cires.argonaut.messaging.core.databind.AuditMessage;
import edu.colorado.cires.argonaut.messaging.core.databind.AuditMessage.EventType;
import edu.colorado.cires.argonaut.messaging.core.databind.TracedMessage;
import java.time.Instant;
import java.util.UUID;
import org.apache.commons.lang3.exception.ExceptionUtils;

public final class ErrorMessageAuditUtils {

  public static AuditMessage buildErrorMessageAuditMessage(Exception exception, Object processingMessage, AuditEventProcessor processor) {
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
    } else {
      auditMessage = AuditMessage.builder(auditMessage)
          .withTraceId(UUID.randomUUID())
          .withDac("unknown")
          .build();
    }
    return auditMessage;
  }

  private ErrorMessageAuditUtils() {

  }
}
