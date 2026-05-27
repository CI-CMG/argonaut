package edu.colorado.cires.argonaut.processor.core;

import edu.colorado.cires.argonaut.messaging.core.databind.DacSubmittedFileMessage;
import edu.colorado.cires.argonaut.messaging.core.databind.RemovalMessage;

public class DefaultRemovalFileValidationProcessor implements RemovalFileValidationProcessor {


  @Override
  public RemovalMessage validate(DacSubmittedFileMessage message) {
    throw new UnsupportedOperationException("Not supported yet.");
  }


}
