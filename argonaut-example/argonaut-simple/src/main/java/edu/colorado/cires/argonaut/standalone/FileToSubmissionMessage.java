package edu.colorado.cires.argonaut.standalone;

import edu.colorado.cires.argonaut.messaging.core.databind.DacSubmittedFileMessage;
import java.io.File;
import java.time.Instant;
import java.util.function.Supplier;

public class FileToSubmissionMessage {

  private Supplier<Instant> timestampGenerator = Instant::now;

  public void setTimestampGenerator(Supplier<Instant> timestampGenerator) {
    this.timestampGenerator = timestampGenerator;
  }

  public DacSubmittedFileMessage convert(File file, String dac) {
    return DacSubmittedFileMessage.builder()
        .withDac(dac)
        .withTimestamp(timestampGenerator.get())
        .withPath(file.toString())
        .build();
  }
}
