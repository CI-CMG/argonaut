package edu.colorado.cires.argonaut.file.local;

import edu.colorado.cires.argonaut.file.core.FileListener;
import edu.colorado.cires.argonaut.messaging.core.databind.DacSubmittedFileMessage;

public class LocalFileListener implements FileListener {

  @Override
  public DacSubmittedFileMessage onDacFileSubmitted() {
    return null;
  }
}
