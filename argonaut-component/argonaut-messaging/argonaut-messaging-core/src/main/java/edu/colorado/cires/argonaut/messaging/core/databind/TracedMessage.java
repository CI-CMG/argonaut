package edu.colorado.cires.argonaut.messaging.core.databind;

import java.util.UUID;

public interface TracedMessage {

  String getDac();
  String getFileName();
  UUID getTraceId();

}
