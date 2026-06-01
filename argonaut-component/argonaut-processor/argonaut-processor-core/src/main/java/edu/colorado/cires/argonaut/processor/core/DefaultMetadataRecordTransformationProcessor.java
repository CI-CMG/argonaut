package edu.colorado.cires.argonaut.processor.core;

import edu.colorado.cires.argonaut.file.core.FileStore;
import edu.colorado.cires.argonaut.messaging.core.databind.ArgoFileType;
import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord;
import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord.Action;
import edu.colorado.cires.argonaut.messaging.core.databind.NcSubmissionMessage;
import edu.colorado.cires.argonaut.processor.core.transform.NetCdfMetadataRecord;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import org.apache.commons.io.FileUtils;

public class DefaultMetadataRecordTransformationProcessor implements MetadataRecordTransformationProcessor {

  private FileStore outputFileStore;
  private Path localTempDir;
  private GeoFilter geoFilter;

  @Override
  public MetadataRecord transformNcSubmissionMessage(NcSubmissionMessage message) {
    String file = outputFileStore.appendToPath(message.getDac(), message.getFloatId());
    if (ArgoFileType.isProfile(message.getFileType())) {
      file = outputFileStore.appendToPath(file, "profiles");
    }
    file = outputFileStore.appendToPath(file, message.getFileName());
    switch (message.getOperation()) {
      case ADD:
        return createUpdateMessage(message.getFileType(), file, message.getDac(), message.getFileName(), message.getTraceId());
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

  private MetadataRecord createUpdateMessage(ArgoFileType fileType, String file, String dac, String fileName, UUID traceId) {
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
        switch (fileType) {
          case PROFILE_CORE:
          case PROFILE_BIOCHEMICAL:
            metadataRecord = NetCdfMetadataRecord.fromV31Profile(file, dac, ncFile, geoFilter);
            break;
          case METADATA:
            metadataRecord = NetCdfMetadataRecord.fromV31Metadata(file, dac, ncFile, geoFilter);
            break;
          default:
            // TODO Traj files etc.
            throw new UnsupportedOperationException("Unsupported file type: " + fileType);
        }
      } catch (IOException e) {
        throw new RuntimeException("Unable to parse NetCDF file " + path, e);
      }
      return MetadataRecord.builder(metadataRecord)
          .withTraceId(traceId)
          .withFileName(fileName)
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

  public void setGeoFilter(GeoFilter geoFilter) {
    this.geoFilter = geoFilter;
  }
}
