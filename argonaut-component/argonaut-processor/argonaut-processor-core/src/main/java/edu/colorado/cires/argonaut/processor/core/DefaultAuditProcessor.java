package edu.colorado.cires.argonaut.processor.core;

import edu.colorado.cires.argonaut.audit.core.AuditStore;
import edu.colorado.cires.argonaut.messaging.core.databind.AuditMessage;

public class DefaultAuditProcessor implements AuditProcessor {

  private AuditStore auditStore;

  public void setAuditStore(AuditStore auditStore) {
    this.auditStore = auditStore;
  }

  @Override
  public void recordEvent(AuditMessage auditMessage) {
    auditStore.recordEvent(auditMessage);
  }

}
