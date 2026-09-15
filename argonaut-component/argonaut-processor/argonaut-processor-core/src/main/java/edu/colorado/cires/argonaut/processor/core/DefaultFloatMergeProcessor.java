package edu.colorado.cires.argonaut.processor.core;

import edu.colorado.cires.argonaut.core.merge.multiprof.LocalPathSupplier;
import edu.colorado.cires.argonaut.core.merge.multiprof.MultiProfileMerger;
import edu.colorado.cires.argonaut.file.core.FileStore;
import edu.colorado.cires.argonaut.messaging.core.databind.ArgoFileType;
import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord;
import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord.Action;
import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord.FileStatus;
import edu.colorado.cires.argonaut.messaging.core.databind.ProfileOperation;
import edu.colorado.cires.argonaut.messaging.core.queue.MessageSender;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.databind.json.JsonMapper;

public class DefaultFloatMergeProcessor implements FloatMergeProcessor {

  private static final Logger LOGGER = LoggerFactory.getLogger(DefaultFloatMergeProcessor.class);

  private static final List<String> PARAMETERS = Arrays.asList("PRES", "TEMP", "PSAL");

  private FileStore outputFileStore;
  private Path localTempDir;
  private MessageSender messageSender;
  private String updateIndexQueue;
  private JsonMapper jsonMapper;
  private MultiProfileMerger merger;


  public void setJsonMapper(JsonMapper jsonMapper) {
    this.jsonMapper = jsonMapper;
  }

  public void setUpdateIndexQueue(String updateIndexQueue) {
    this.updateIndexQueue = updateIndexQueue;
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

  public void setMessageSender(MessageSender messageSender) {
    this.messageSender = messageSender;
  }

  public void setMerger(MultiProfileMerger merger) {
    this.merger = merger;
  }

  private List<LocalPathSupplier> getInputFileSuppliers(ProfileOperation message) {
    List<LocalPathSupplier> result = new ArrayList<>(message.getFiles().size());
    for (MetadataRecord metadataRecord : message.getFiles()) {
      result.add(new LocalPathSupplier() {

        private Path tempFile = null;

        @Override
        public String getDac() {
          return message.getDac();
        }

        @Override
        public String getFileName() {
          return metadataRecord.getFileName();
        }

        @Override
        public void prepare() {
          try {
            tempFile = Files.createTempFile(localTempDir, "float-merge-download-", ".nc");
            String path = outputFileStore.appendToPath(outputFileStore.getRoot(), "dac", metadataRecord.getFile());
            outputFileStore.downloadLocalFile(path, tempFile);
          } catch (IOException e) {
            throw new RuntimeException("Unable to create temporary download file: " + tempFile, e);
          }
        }

        @Override
        public Path getLocalPath() {
          return tempFile;
        }

        @Override
        public void cleanUp() {
          if (tempFile != null) {
            FileUtils.deleteQuietly(tempFile.toFile());
          }
        }
      });
    }
    return result;
  }

  private static boolean isRemoveMergeFile(ProfileOperation message) {
    long activeFiles = message.getFiles().stream()
        .filter(metadataRecord -> metadataRecord.getFileStatus() == FileStatus.ACTIVE)
        .count();
    return activeFiles == 0L;
  }

  private void removeMergeFile(ProfileOperation message) {
    String outputFile = getOutputFile(message);
    outputFileStore.delete(outputFile);
    LOGGER.info("Removed multi-cycle merge file: {}", outputFile);
  }

  private String getOutputFile(ProfileOperation message) {
    String floatId = message.getFloatId();
    String fileName = floatId + "_prof.nc";
    return outputFileStore.appendToPath(outputFileStore.getRoot(), "dac", message.getDac(), floatId, fileName);
  }

  private void mergeProfiles(ProfileOperation message) {
    String outputFile = getOutputFile(message);
    Path localOutputFile;
    try {
      localOutputFile = Files.createTempFile(localTempDir, "float-merge-", ".nc");
    } catch (IOException e) {
      throw new RuntimeException("Unable to create temporary output file", e);
    }

    try {
      try {
        merger.mergeProfiles(getInputFileSuppliers(message), PARAMETERS, localOutputFile);
      } catch (IOException e) {
        throw new RuntimeException("Unable to merge float data " + outputFile, e);
      }
      try {
        outputFileStore.uploadLocalFile(localOutputFile, outputFile);
      } catch (IOException e) {
        throw new RuntimeException("Unable to upload merge file " + outputFile, e);
      }

      LOGGER.info("Updated multi-cycle merge file: {}", outputFile);

    } finally {
      FileUtils.deleteQuietly(localOutputFile.toFile());
    }
  }

  private void notifyMergeCompleted(ProfileOperation message, boolean remove) {
    Instant now = Instant.now();

    for (MetadataRecord metadataRecord : message.getFiles()) {
      messageSender.sendJson(
          updateIndexQueue,
          jsonMapper.writeValueAsString(MetadataRecord.builder()
              .withTraceId(message.getTraceId())
              .withFileName(metadataRecord.getFileName())
              .withFile(metadataRecord.getFile())
              .withDac(message.getDac())
              .withFloatId(message.getFloatId())
              .withAction(metadataRecord.getFileStatus() == FileStatus.REMOVED ? Action.FLOAT_MERGE_REMOVE : Action.FLOAT_MERGE)
              .withFileType(ArgoFileType.PROFILE_CORE)
              .withActionTimestamp(now)
              .build()));
    }

    String fileName = message.getFloatId() + "_prof.nc";
    String outputFileForMetadata = outputFileStore.appendToPath(message.getDac(), message.getFloatId(), fileName);

    messageSender.sendJson(
        updateIndexQueue,
        jsonMapper.writeValueAsString(MetadataRecord.builder()
            .withTraceId(message.getTraceId())
            .withActionTimestamp(now)
            .withFileName(fileName)
            .withFile(outputFileForMetadata)
            .withDate(now)
            .withDateUpdate(now)
            .withAction(remove ? Action.REMOVE : Action.UPDATE)
            .withDac(message.getDac())
            .withFloatId(message.getFloatId())
            .withFileType(ArgoFileType.PROFILE_MULTI_CYCLE)
            .build()));
  }

  @Override
  public void merge(ProfileOperation message) {
    boolean remove = isRemoveMergeFile(message);
    if (remove) {
      removeMergeFile(message);
    } else {
      mergeProfiles(message);
    }
    notifyMergeCompleted(message, remove);
  }


}
