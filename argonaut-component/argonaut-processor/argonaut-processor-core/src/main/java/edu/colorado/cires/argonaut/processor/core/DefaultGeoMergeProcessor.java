package edu.colorado.cires.argonaut.processor.core;

import edu.colorado.cires.argonaut.core.merge.multiprof.LocalPathSupplier;
import edu.colorado.cires.argonaut.core.merge.multiprof.MultiProfileMerger;
import edu.colorado.cires.argonaut.file.core.FileStore;
import edu.colorado.cires.argonaut.messaging.core.databind.ArgoFileType;
import edu.colorado.cires.argonaut.messaging.core.databind.DacFloatFilePath;
import edu.colorado.cires.argonaut.messaging.core.databind.GeoMergeInfo;
import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord;
import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord.Action;
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

public class DefaultGeoMergeProcessor implements GeoMergeProcessor {

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

  private List<LocalPathSupplier> getInputFileSuppliers(GeoMergeInfo message) {
    List<LocalPathSupplier> result = new ArrayList<>(message.getFiles().size());
    for (DacFloatFilePath dacPath : message.getFiles()) {
      result.add(new LocalPathSupplier() {

        private Path tempFile = null;

        @Override
        public String getFileName() {
          return outputFileStore.getFileName(dacPath.getFile());
        }

        @Override
        public void prepare() {
          try {
            tempFile = Files.createTempFile(localTempDir, "geo-merge-download-", ".nc");
            String path = outputFileStore.appendToPath(outputFileStore.getRoot(), "dac", dacPath.getFile());
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
  public void merge(GeoMergeInfo message) {
    if (message.getOcean().getDirectory() != null) {
      String fileName = String.format("%04d%02d%02d_prof.nc", message.getYear(), message.getMonth(), message.getDay());
      String outputFile = outputFileStore.appendToPath(
          outputFileStore.getRoot(),
          "geo",
          message.getOcean().getDirectory(),
          String.format("%04d", message.getYear()),
          String.format("%02d", message.getMonth()),
          fileName
      );

      Path localOutputFile;
      try {
        localOutputFile = Files.createTempFile(localTempDir, "geo-merge-", ".nc");
      } catch (IOException e) {
        throw new RuntimeException("Unable to create temporary output file", e);
      }

      try {
        try {
          merger.mergeProfiles(getInputFileSuppliers(message), PARAMETERS, localOutputFile);
        } catch (IOException e) {
          throw new RuntimeException("Unable to merge geo data " + fileName, e);
        }
        try {
          outputFileStore.uploadLocalFile(localOutputFile, outputFile);
        } catch (IOException e) {
          throw new RuntimeException("Unable to upload merge file " + fileName, e);
        }

        Instant now = Instant.now();

        for (DacFloatFilePath filePath : message.getFiles()) {
          messageSender.sendJson(
              updateIndexQueue,
              jsonMapper.writeValueAsString(MetadataRecord.builder()
                  .withTraceId(message.getTraceId())
                  .withFileName(outputFileStore.getFileName(filePath.getFile()))
                  .withFile(filePath.getFile())
                  .withDac(filePath.getDac())
                  .withFloatId(filePath.getFloatId())
                  .withAction(Action.GEO_MERGE)
                  .withFileType(ArgoFileType.PROFILE_CORE)
                  .withActionTimestamp(now)
                  .build()));
        }

      } finally {
        FileUtils.deleteQuietly(localOutputFile.toFile());
      }
    }
  }
}
