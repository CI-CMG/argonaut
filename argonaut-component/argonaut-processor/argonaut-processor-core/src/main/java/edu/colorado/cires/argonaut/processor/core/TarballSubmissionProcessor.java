package edu.colorado.cires.argonaut.processor.core;

import edu.colorado.cires.argonaut.messaging.core.databind.DacSubmittedFileMessage;
import edu.colorado.cires.argonaut.messaging.core.databind.NcSubmissionMessage;
import java.util.List;

public interface TarballSubmissionProcessor {

  List<NcSubmissionMessage> untarAndMoveToProcessing(DacSubmittedFileMessage submittedFile);
}
