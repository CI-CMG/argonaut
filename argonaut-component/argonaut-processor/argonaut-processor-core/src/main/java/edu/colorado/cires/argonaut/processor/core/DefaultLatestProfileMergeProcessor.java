package edu.colorado.cires.argonaut.processor.core;

import edu.colorado.cires.argonaut.core.merge.multiprof.DefaultMultiProfileMerger;
import edu.colorado.cires.argonaut.core.merge.multiprof.LocalPathSupplier;
import edu.colorado.cires.argonaut.core.merge.multiprof.MultiProfileMerger;
import edu.colorado.cires.argonaut.file.core.FileStore;
import edu.colorado.cires.argonaut.messaging.core.databind.ArgoFileType;
import edu.colorado.cires.argonaut.messaging.core.databind.ArgoFileTypeDetails;
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
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
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
  private Supplier<Instant> nowGenerator = Instant::now;


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

  public void setNowGenerator(Supplier<Instant> nowGenerator) {
    this.nowGenerator = nowGenerator;
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

  private void mergeProfiles(ProfileOperation message) {
    String filePrefix = message.getFileName() + "_prof_";

    List<LocalPathSupplier> sorted = DefaultMultiProfileMerger.orderByJulianDateAcending(
        getInputFileSuppliers(
            message.getFiles().stream().filter(mr -> FileStatus.ACTIVE == mr.getFileStatus()).toList()
        ));

    LinkedList<List<LocalPathSupplier>> groups = new LinkedList<>();
    int count = 0;
    for (LocalPathSupplier lps : sorted) {
      if (count == 0) {
        groups.add(new LinkedList<>());
      }
      groups.getLast().add(lps);
      count++;
      if (count == maxProfilesPerFile) {
        count = 0;
      }
    }

    for (int i = 0; i < groups.size(); i++) {
      List<LocalPathSupplier> group = groups.get(i);
      String fileName = filePrefix + i + ".nc";

      String outputFile = outputFileStore.appendToPath(outputFileStore.getRoot(), "latest_data", fileName);
      Path localOutputDir;
      try {
        localOutputDir = Files.createTempDirectory(localTempDir, "latest-merge-");
      } catch (IOException e) {
        throw new RuntimeException("Unable to create temporary output file", e);
      }
      Path localOutputFile = localOutputDir.resolve(fileName);

      try {
        try {
          merger.mergeProfiles(group, PARAMETERS, localOutputFile);
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
        FileUtils.deleteQuietly(localOutputDir.toFile());
      }
    }


  }

  private void notifyMergeCompleted(ProfileOperation message) {
    Instant now = nowGenerator.get();

    for (MetadataRecord metadataRecord : message.getFiles()) {
      String json = jsonMapper.writeValueAsString(MetadataRecord.builder(metadataRecord)
          .withTraceId(message.getTraceId())
          .withAction(metadataRecord.getFileStatus() == FileStatus.REMOVED ? Action.LATEST_MERGE_REMOVE : Action.LATEST_MERGE)
          .withActionTimestamp(now)
          .withRelatedFiles(Collections.singletonList(message.getFileName()))
          .build());
      messageSender.sendJson(updateIndexQueue, json);
    }
  }

  private void removeOldFiles(ProfileOperation message) {
    String dirPath = outputFileStore.appendToPath(outputFileStore.getRoot(), "latest_data");
    List<String> existingFiles = outputFileStore.listFileNamesInDirectory(dirPath);

    Set<String> toRemove = new HashSet<>();
    for (String fileName : existingFiles) {
      ArgoFileTypeDetails fileType = ArgoFileType.getFileNameDetails(fileName);
      if (fileType.getType() == ArgoFileType.LATEST_PROFILE_MERGE) {
        if (fileName.startsWith(message.getFileName())) {
          toRemove.add(fileName);
        }
      }
    }

    for (String fileName : toRemove) {
      outputFileStore.delete(outputFileStore.appendToPath(dirPath, fileName));
    }
  }

  @Override
  public void merge(ProfileOperation message) {
    removeOldFiles(message);
    if (!isRemoveMergeFile(message)) {
      mergeProfiles(message);
    }
    notifyMergeCompleted(message);
  }


}
