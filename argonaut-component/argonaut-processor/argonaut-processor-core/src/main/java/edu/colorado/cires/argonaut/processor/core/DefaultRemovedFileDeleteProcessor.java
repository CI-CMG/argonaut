package edu.colorado.cires.argonaut.processor.core;

import edu.colorado.cires.argonaut.file.core.FileStore;
import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord;
import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord.Action;
import edu.colorado.cires.argonaut.messaging.core.databind.ProfileOperation;
import java.time.Instant;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultRemovedFileDeleteProcessor implements RemovedFileDeleteProcessor {

  private static final Logger LOGGER = LoggerFactory.getLogger(DefaultRemovedFileDeleteProcessor.class);

  private FileStore outputFileStore;
  private Supplier<Instant> timestampSupplier = () -> Instant.now();

  public void setOutputFileStore(FileStore outputFileStore) {
    this.outputFileStore = outputFileStore;
  }

  void setTimestampSupplier(Supplier<Instant> timestampSupplier) {
    this.timestampSupplier = timestampSupplier;
  }

  private String resolveRemovedFile(ProfileOperation message) {
    return outputFileStore.appendToPath(outputFileStore.getRoot(), "etc", "removed", message.getDac(), message.getFiles().get(0).getFileName());
  }

  @Override
  public MetadataRecord delete(ProfileOperation message) {
    String removalPath = resolveRemovedFile(message);
    if (outputFileStore.fileExists(removalPath)) {
      outputFileStore.delete(removalPath);
    }
    LOGGER.info("Deleted file {}", removalPath);

    return MetadataRecord.builder(message.getFiles().get(0))
        .withTraceId(message.getTraceId())
        .withActionTimestamp(timestampSupplier.get())
        .withFloatId(message.getFloatId())
        .withDac(message.getDac())
        .withAction(Action.DELETE_REMOVED_FILE)
        .build();
  }
}
