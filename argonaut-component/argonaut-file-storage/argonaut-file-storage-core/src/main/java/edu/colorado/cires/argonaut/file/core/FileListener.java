package edu.colorado.cires.argonaut.file.core;

import edu.colorado.cires.argonaut.messaging.core.databind.DacSubmittedFileMessage;

public interface FileListener {

  DacSubmittedFileMessage onDacFileSubmitted();

}
