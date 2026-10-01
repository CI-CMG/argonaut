package edu.colorado.cires.argonaut.processor.core;

import edu.colorado.cires.argonaut.messaging.core.databind.AuditMessage;

public interface AuditProcessor {

  void recordEvent(AuditMessage auditMessage);

}
