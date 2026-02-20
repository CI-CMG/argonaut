package edu.colorado.cires.argonaut.standalone;

import edu.colorado.cires.argonaut.file.core.FileStore;
import edu.colorado.cires.argonaut.messaging.core.databind.DacSubmittedFileMessage;

import java.nio.file.Path;
import java.nio.file.Paths;

public class MoveSubmitToWorkProcessor {

  private String submitWorkDirectory;
  private FileStore fileStore;

  public void setSubmitWorkDirectory(String submitWorkDirectory) {
    this.submitWorkDirectory = submitWorkDirectory;
  }

  public void setFileStore(FileStore fileStore) {
    this.fileStore = fileStore;
  }

  public DacSubmittedFileMessage moveToWork(DacSubmittedFileMessage message) {
    Path from = Paths.get(message.getPath());
    Path to = Paths.get(submitWorkDirectory).resolve("dac").resolve(message.getDac()).resolve("work").resolve(from.getFileName());
    fileStore.move(from.toString(), to.toString());
    return DacSubmittedFileMessage.builder(message).withPath(to.toString()).build();
  }

}
