package edu.colorado.cires.argonaut.standalone;

import edu.colorado.cires.argonaut.messaging.core.databind.DacSubmittedFileMessage;
import java.io.File;
import java.time.Instant;
import java.util.UUID;
import java.util.function.Supplier;

public class FileToSubmissionMessage {

  private Supplier<Instant> timestampGenerator = Instant::now;
  private Supplier<UUID> traceIdGenerator = UUID::randomUUID;

  public void setTimestampGenerator(Supplier<Instant> timestampGenerator) {
    this.timestampGenerator = timestampGenerator;
  }

  public void setTraceIdGenerator(Supplier<UUID> traceIdGenerator) {
    this.traceIdGenerator = traceIdGenerator;
  }

  public DacSubmittedFileMessage convert(File file, String dac) {
    return DacSubmittedFileMessage.builder()
        .withTraceId(traceIdGenerator.get())
        .withFileName(file.getName())
        .withDac(dac)
        .withTimestamp(timestampGenerator.get())
        .withPath(file.toString())
        .build();
  }
}
