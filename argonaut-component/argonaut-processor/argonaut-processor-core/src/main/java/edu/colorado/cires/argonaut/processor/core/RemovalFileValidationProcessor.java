package edu.colorado.cires.argonaut.processor.core;

import edu.colorado.cires.argonaut.messaging.core.databind.DacSubmittedFileMessage;
import edu.colorado.cires.argonaut.messaging.core.databind.RemovalMessage;

public interface RemovalFileValidationProcessor {

  RemovalMessage validate(DacSubmittedFileMessage message);
}
