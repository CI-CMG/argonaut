package edu.colorado.cires.argonaut.processor.core;

import edu.colorado.cires.argonaut.file.core.FileStore;
import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord;
import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord.Action;
import edu.colorado.cires.argonaut.messaging.core.databind.NcSubmissionMessage;
import edu.colorado.cires.argonaut.messaging.core.databind.NcSubmissionMessage.FileType;
import edu.colorado.cires.argonaut.processor.core.transform.NetCdfMetadataRecord;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.commons.io.FileUtils;

public class DefaultMetadataRecordTransformationProcessor implements MetadataRecordTransformationProcessor {

  private FileStore outputFileStore;
  private Path localTempDir;
  private GeoFilter geoFilter;

  @Override
  public MetadataRecord transformNcSubmissionMessage(NcSubmissionMessage message) {
    if (message.getValidationErrors() != null && !message.getValidationErrors().isEmpty()) {
      return MetadataRecord.builder()
          .withAction(Action.NONE)
          .build();
    }
    String file = outputFileStore.appendToPath(message.getDac(), message.getFloatId());
    if (FileType.CORE_ARGO_PROFILE == message.getFileType()) {
      file = outputFileStore.appendToPath(file, "profiles");
    }
    file = outputFileStore.appendToPath(file, message.getFileName());
    switch (message.getOperation()) {
      case ADD:
        return createUpdateMessage(message.getFileType(), file, message.getDac());
      case REMOVE:
        return MetadataRecord.builder()
            .withFile(file)
            .withAction(Action.REMOVE)
            .build();
      default:
        return MetadataRecord.builder()
            .withFile(file)
            .withAction(Action.NONE)
            .build();
    }
  }

  private MetadataRecord createUpdateMessage(FileType fileType, String file, String dac) {
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
      try {
        switch (fileType) {
          case CORE_ARGO_PROFILE:
          case B_ARGO_PROFILE:
            return NetCdfMetadataRecord.fromV31Profile(file, dac, ncFile, geoFilter);
          case METADATA:
            return NetCdfMetadataRecord.fromV31Metadata(file, dac, ncFile, geoFilter);
          default:
            // TODO Traj files etc.
            throw new UnsupportedOperationException("Unsupported file type: " + fileType);
        }
      } catch (IOException e) {
        throw new RuntimeException("Unable to parse NetCDF file " + path, e);
      }
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

  public void setGeoFilter(GeoFilter geoFilter) {
    this.geoFilter = geoFilter;
  }
}
