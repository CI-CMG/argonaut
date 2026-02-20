package edu.colorado.cires.argonaut.standalone;

import edu.colorado.cires.argonaut.file.core.FileStore;
import edu.colorado.cires.argonaut.messaging.core.databind.DacSubmittedFileMessage;

public class MoveSubmitToProcessingDir {

  private FileStore fileStore;

  public void setFileStore(FileStore fileStore) {
    this.fileStore = fileStore;
  }

  public DacSubmittedFileMessage moveToProcessing(DacSubmittedFileMessage message) {
    String to = fileStore.appendToPath(
            fileStore.getRoot(),
            "dac",
            message.getDac(),
            "processing",
            message.getTimestamp().toString(),
            fileStore.getFileName(message.getPath())
    );
    fileStore.move(message.getPath(), to);
    return DacSubmittedFileMessage.builder(message).withPath(to).build();
  }

}
