package edu.colorado.cires.argonaut.processor.core;

import edu.colorado.cires.argonaut.core.merge.multiprof.DefaultMultiProfileMerger;
import edu.colorado.cires.argonaut.core.merge.multiprof.LocalPathSupplier;
import edu.colorado.cires.argonaut.core.merge.multiprof.MultiProfileMerger;
import edu.colorado.cires.argonaut.file.core.FileStore;
import edu.colorado.cires.argonaut.messaging.core.databind.ArgoFileType;
import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord;
import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord.Action;
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
import tools.jackson.databind.json.JsonMapper;

public class DefaultFloatMergeProcessor implements FloatMergeProcessor {

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
    for (String file : message.getFiles()) {
      result.add(new LocalPathSupplier() {

        private Path tempFile = null;

        @Override
        public String getFileName() {
          return outputFileStore.getFileName(file);
        }

        @Override
        public void prepare() {
          try {
            tempFile = Files.createTempFile(localTempDir, "float-merge-download-", ".nc");
            String path = outputFileStore.appendToPath(outputFileStore.getRoot(), "dac", file);
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

  @Override
  public void merge(ProfileOperation message) {
    String dac = message.getDac();
    String floatId = message.getFloatId();

    String fileName = floatId + "_prof.nc";
    String outputFileForMetadata = outputFileStore.appendToPath(dac, floatId, fileName);
    String outputFile = outputFileStore.appendToPath(outputFileStore.getRoot(), "dac", outputFileForMetadata);

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
        throw new RuntimeException("Unable to merge float data " + fileName, e);
      }
      try {
        outputFileStore.uploadLocalFile(localOutputFile, outputFile);
      } catch (IOException e) {
        throw new RuntimeException("Unable to upload merge file " + fileName, e);
      }

      Instant now = Instant.now();

      for (String filePath : message.getFiles()) {
        messageSender.sendJson(
            updateIndexQueue,
            jsonMapper.writeValueAsString(MetadataRecord.builder()
                .withTraceId(message.getTraceId())
                .withFileName(outputFileStore.getFileName(filePath))
                .withFile(filePath)
                .withDac(dac)
                .withFloatId(floatId)
                .withAction(Action.FLOAT_MERGE)
                .withFileType(ArgoFileType.PROFILE_CORE)
                .withActionTimestamp(now)
                .build()));
      }

      messageSender.sendJson(
          updateIndexQueue,
          jsonMapper.writeValueAsString(MetadataRecord.builder()
              .withTraceId(message.getTraceId())
              .withActionTimestamp(now)
              .withFileName(fileName)
              .withFile(outputFileForMetadata)
              .withDate(now)
              .withDateUpdate(now)
              .withAction(Action.UPDATE)
              .withDac(dac)
              .withFloatId(floatId)
              .withFileType(ArgoFileType.PROFILE_MULTI_CYCLE)
              .build()));


    } finally {
      FileUtils.deleteQuietly(localOutputFile.toFile());
    }
  }
}
