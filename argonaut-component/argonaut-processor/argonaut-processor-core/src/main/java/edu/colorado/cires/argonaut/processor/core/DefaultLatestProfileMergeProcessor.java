package edu.colorado.cires.argonaut.processor.core;

import edu.colorado.cires.argonaut.core.merge.multiprof.DefaultMultiProfileMerger;
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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.databind.json.JsonMapper;

public class DefaultLatestProfileMergeProcessor implements LatestProfileMergeProcessor {

  private static final Logger LOGGER = LoggerFactory.getLogger(DefaultLatestProfileMergeProcessor.class);

  private static final List<String> PARAMETERS = Arrays.asList("PRES", "TEMP", "PSAL");

  private FileStore outputFileStore;
  private Path localTempDir;
  private MessageSender messageSender;
  private String updateIndexQueue;
  private JsonMapper jsonMapper;
  private MultiProfileMerger merger;
  private int maxProfilesPerFile = 1000;


  public void setJsonMapper(JsonMapper jsonMapper) {
    this.jsonMapper = jsonMapper;
  }

  public void setUpdateIndexQueue(String updateIndexQueue) {
    this.updateIndexQueue = updateIndexQueue;
  }

  public void setOutputFileStore(FileStore outputFileStore) {
    this.outputFileStore = outputFileStore;
  }

  public void setMaxProfilesPerFile(int maxProfilesPerFile) {
    this.maxProfilesPerFile = maxProfilesPerFile;
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

  private List<LocalPathSupplier> getInputFileSuppliers(List<MetadataRecord> group) {
    List<LocalPathSupplier> result = new ArrayList<>(group.size());
    for (MetadataRecord metadataRecord : group) {
        result.add(new LocalPathSupplier() {

          private Path tempFile = null;

          @Override
          public Instant getJulD() {
            return metadataRecord.getDate();
          }

          @Override
          public String getDac() {
            return metadataRecord.getDac();
          }

          @Override
          public String getFileName() {
            return metadataRecord.getFileName();
          }

          @Override
          public void prepare() {
            try {
              tempFile = Files.createTempFile(localTempDir, "latest-merge-download-", ".nc");
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
    throw new UnsupportedOperationException("Not implemented yet");
//    String outputFile = getOutputFile(message);
//    outputFileStore.delete(outputFile);
//    LOGGER.info("Removed multi-cycle merge file: {}", outputFile);
  }

  private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

  private String getOutputFile(String fileName) {
    return outputFileStore.appendToPath(outputFileStore.getRoot(), "latest_data", fileName);
  }

  private void mergeProfiles(ProfileOperation message) {
    Instant dateUpdate = message.getFiles().get(0).getDateUpdate();
    String modePrefix = message.getFiles().get(0).getProfileMode().getPrefix();
    String filePrefix = modePrefix + FORMATTER.format(dateUpdate) + "_prof_";


    LinkedList<List<MetadataRecord>> groups = new LinkedList<>();
    int count = 0;
    for (MetadataRecord metadataRecord : message.getFiles()) {
      if (FileStatus.ACTIVE == metadataRecord.getFileStatus()) {
        if (count == 0) {
          groups.add(new LinkedList<>());
        }
        groups.getLast().add(metadataRecord);
        count++;
        if (count == maxProfilesPerFile) {
          count = 0;
        }
      }
    }

    for (int i =  0; i < groups.size(); i++) {
      List<MetadataRecord> group = groups.get(i);
      String fileName =  filePrefix + i + ".nc";

      String outputFile = getOutputFile(fileName);
      Path localOutputFile;
      try {
        localOutputFile = Files.createTempFile(localTempDir, "latest-merge-", ".nc");
      } catch (IOException e) {
        throw new RuntimeException("Unable to create temporary output file", e);
      }

      try {
        try {
          merger.mergeProfiles(DefaultMultiProfileMerger.orderByCycleThenJulD(getInputFileSuppliers(group)), PARAMETERS, localOutputFile);
        } catch (IOException e) {
          throw new RuntimeException("Unable to merge latest data " + outputFile, e);
        }
        try {
          outputFileStore.uploadLocalFile(localOutputFile, outputFile);
        } catch (IOException e) {
          throw new RuntimeException("Unable to upload latest file " + outputFile, e);
        }

        LOGGER.info("Updated latest merge file: {}", outputFile);

      } finally {
        FileUtils.deleteQuietly(localOutputFile.toFile());
      }
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
