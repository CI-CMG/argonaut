package edu.colorado.cires.argonaut.processor.core;

import edu.colorado.cires.argonaut.messaging.core.databind.DacSubmittedFileMessage;
import edu.colorado.cires.argonaut.messaging.core.databind.NcSubmissionMessage;
import java.util.Optional;

public interface SubmissionProcessor {

  Optional<NcSubmissionMessage> moveToProcessing(DacSubmittedFileMessage submittedFile);
}
