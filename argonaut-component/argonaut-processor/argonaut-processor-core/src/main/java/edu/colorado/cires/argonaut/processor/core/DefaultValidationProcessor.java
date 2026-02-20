package edu.colorado.cires.argonaut.processor.core;

import edu.colorado.cires.argonaut.messaging.core.databind.NcSubmissionMessage;

public class DefaultValidationProcessor implements ValidationProcessor {

  @Override
  public NcSubmissionMessage validate(NcSubmissionMessage message) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}
