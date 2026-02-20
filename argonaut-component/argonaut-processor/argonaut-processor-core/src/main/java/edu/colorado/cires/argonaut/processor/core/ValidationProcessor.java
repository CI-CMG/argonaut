package edu.colorado.cires.argonaut.processor.core;

import edu.colorado.cires.argonaut.messaging.core.databind.NcSubmissionMessage;

public interface ValidationProcessor {

  NcSubmissionMessage validate(NcSubmissionMessage message);

}
