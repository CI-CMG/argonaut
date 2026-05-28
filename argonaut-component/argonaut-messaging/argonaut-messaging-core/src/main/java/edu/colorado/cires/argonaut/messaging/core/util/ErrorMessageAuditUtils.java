package edu.colorado.cires.argonaut.messaging.core.util;

import edu.colorado.cires.argonaut.messaging.core.databind.AuditEventProcessor;
import edu.colorado.cires.argonaut.messaging.core.databind.AuditMessage;
import edu.colorado.cires.argonaut.messaging.core.databind.AuditMessage.EventType;
import edu.colorado.cires.argonaut.messaging.core.databind.DacSubmittedFileMessage;
import org.apache.commons.lang3.exception.ExceptionUtils;

public final class ErrorMessageAuditUtils {

  public static AuditMessage buildErrorMessageAuditMessage(Exception exception, Object processingMessage, AuditEventProcessor processor){
    AuditMessage auditMessage = AuditMessage.builder()
        .withEventType(EventType.ERROR)
        .withStackTrace(ExceptionUtils.getStackTrace(exception))
        .withMessage(ExceptionUtils.getRootCauseMessage(exception))
        .withProcessor(processor)
        .build();
    if (processingMessage instanceof DacSubmittedFileMessage) {
      DacSubmittedFileMessage message = (DacSubmittedFileMessage) processingMessage;
      auditMessage = AuditMessage.builder(auditMessage)
          .withTraceId(message.getTraceId())
          .withTimestamp(message.getTimestamp())
          .withDac(message.getDac())
          .build();
    }
    return auditMessage;
  }

  private ErrorMessageAuditUtils() {

  }
}
