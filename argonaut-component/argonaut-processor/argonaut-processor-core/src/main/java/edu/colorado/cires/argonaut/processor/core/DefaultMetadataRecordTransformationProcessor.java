package edu.colorado.cires.argonaut.processor.core;

import edu.colorado.cires.argonaut.file.core.FileStore;
import edu.colorado.cires.argonaut.messaging.core.databind.ArgoFileType;
import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord;
import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord.Action;
import edu.colorado.cires.argonaut.messaging.core.databind.NcSubmissionMessage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.function.FailableFunction;

public class DefaultMetadataRecordTransformationProcessor implements MetadataRecordTransformationProcessor {

  private FileStore outputFileStore;
  private Path localTempDir;
  private FailableFunction<ReadFileRequest, MetadataRecord, IOException> fileReader;

  @Override
  public MetadataRecord transformNcSubmissionMessage(NcSubmissionMessage message) {
    String file = outputFileStore.appendToPath(message.getDac(), message.getFloatId());
    if (ArgoFileType.isProfile(message.getFileType())) {
      file = outputFileStore.appendToPath(file, "profiles");
    }
    file = outputFileStore.appendToPath(file, message.getFileName());
    switch (message.getOperation()) {
      case ADD:
        return createUpdateMessage(message, file);
      case REMOVE:
        return MetadataRecord.builder()
            .withDac(message.getDac())
            .withTraceId(message.getTraceId())
            .withFileType(message.getFileType())
            .withFile(file)
            .withFileName(message.getFileName())
            .withActionTimestamp(Instant.now())
            .withAction(Action.REMOVE)
            .build();
      default:
        return MetadataRecord.builder()
            .withFile(file)
            .withFileName(message.getFileName())
            .withActionTimestamp(Instant.now())
            .withAction(Action.NONE)
            .build();
    }
  }

  private MetadataRecord createUpdateMessage(NcSubmissionMessage message, String file) {
    String path = outputFileStore.appendToPath(outputFileStore.getRoot(), "dac", file);
    Path ncFile;
    try {
      ncFile = Files.createTempFile(localTempDir, null, ".nc");
    } catch (IOException e) {
      throw new RuntimeException("Unable to create temp file", e);
    }
    try {
      try {
        outputFileStore.downloadLocalFile(path, ncFile);
      } catch (IOException e) {
        throw new RuntimeException("Unable to download " + path, e);
      }
      MetadataRecord metadataRecord;
      try {
        metadataRecord = fileReader.apply(new ReadFileRequest(file, ncFile, message));
      } catch (Exception e) {
        throw new RuntimeException("Unable to parse NetCDF file " + path, e);
      }
      return MetadataRecord.builder(metadataRecord)
          .withTraceId(message.getTraceId())
          .withFileName(message.getFileName())
          .build();
    } finally {
      FileUtils.deleteQuietly(ncFile.toFile());
    }

  }

  public void setOutputFileStore(FileStore outputFileStore) {
    this.outputFileStore = outputFileStore;
  }

  public void setLocalTempDir(Path localTempDir) {
    this.localTempDir = localTempDir;
    try {
      Files.createDirectories(localTempDir);
    } catch (IOException e) {
      throw new RuntimeException("Unable to create temp directory: " + localTempDir, e);
    }
  }

  public void setFileReader(FailableFunction<ReadFileRequest, MetadataRecord, IOException> fileReader) {
    this.fileReader = fileReader;
  }
}
