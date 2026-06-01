package edu.colorado.cires.argonaut.audit.core;

import edu.colorado.cires.argonaut.messaging.core.databind.AuditMessage;

public interface AuditStorageProcessor {

  void recordEvent(AuditMessage auditMessage);

}
