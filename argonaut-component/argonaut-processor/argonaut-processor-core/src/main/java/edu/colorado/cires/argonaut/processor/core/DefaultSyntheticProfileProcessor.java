package edu.colorado.cires.argonaut.processor.core;

import edu.colorado.cires.argonaut.core.merge.synthetic.SyntheticProfileMerger;
import edu.colorado.cires.argonaut.file.core.FileStore;
import edu.colorado.cires.argonaut.messaging.core.databind.ArgoFileType;
import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord;
import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord.Action;
import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord.FileStatus;
import edu.colorado.cires.argonaut.messaging.core.databind.ProfileOperation;
import edu.colorado.cires.argonaut.messaging.core.queue.MessageSender;
import edu.colorado.cires.argonaut.processor.core.transform.NetCdfMetadataRecord;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.databind.json.JsonMapper;

public class DefaultSyntheticProfileProcessor implements SyntheticProfileProcessor {

  private static final Logger LOGGER = LoggerFactory.getLogger(DefaultSyntheticProfileProcessor.class);

  private FileStore outputFileStore;
  private Path localTempDir;
  private MessageSender messageSender;
  private String updateIndexQueue;
  private JsonMapper jsonMapper;
  private SyntheticProfileMerger syntheticProfileMerger;
  private GeoFilter geoFilter;


  public void setGeoFilter(GeoFilter geoFilter) {
    this.geoFilter = geoFilter;
  }

  public void setSyntheticProfileMerger(SyntheticProfileMerger syntheticProfileMerger) {
    this.syntheticProfileMerger = syntheticProfileMerger;
  }

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

  private static boolean isRemoveMergeFile(ProfileOperation message) {
    boolean containsActiveMetadata = false;
    boolean containsActiveCoreProfile = false;
    boolean containsActiveBioProfile = false;
    for (MetadataRecord metadataRecord : message.getFiles()) {
      if (metadataRecord.getFileStatus() == FileStatus.ACTIVE) {
        switch (metadataRecord.getFileType()) {
          case METADATA:
            containsActiveMetadata = true;
            break;
          case PROFILE_CORE:
            containsActiveCoreProfile = true;
            break;
          case PROFILE_BIOCHEMICAL:
            containsActiveBioProfile = true;
            break;
          default:
            break;
        }
      }
    }

    return !containsActiveMetadata || !containsActiveCoreProfile || !containsActiveBioProfile;
  }

  private static String getFileName(ProfileOperation message) {
    return "S" + message.getFiles().stream().filter(file -> file.getFileType() == ArgoFileType.PROFILE_CORE).map(MetadataRecord::getFileName).findFirst().orElseThrow();
  }

  private String getMetadataPath(ProfileOperation message) {
    return outputFileStore.appendToPath(message.getDac(), message.getFloatId(), "profiles", getFileName(message));
  }

  private String getUploadFile(ProfileOperation message) {
    return outputFileStore.appendToPath(outputFileStore.getRoot(), "dac", getMetadataPath(message));
  }

  private MetadataRecord removeMergeFile(ProfileOperation message) {
    String file = getMetadataPath(message);
    String outputFile = getUploadFile(message);
    String fileName = getFileName(message);
    outputFileStore.delete(outputFile);
    LOGGER.info("Removed synthetic merge file: {}", file);
    Instant now = Instant.now();
    return MetadataRecord.builder()
        .withTraceId(message.getTraceId())
        .withActionTimestamp(now)
        .withFileName(fileName)
        .withFile(file)
        .withDateUpdate(now)
        .withAction(Action.REMOVE)
        .withDac(message.getDac())
        .withFloatId(message.getFloatId())
        .withFileType(ArgoFileType.SYNTHETIC_PROFILE_SINGLE_CYCLE)
        .build();
  }

  private MetadataRecord mergeProfiles(ProfileOperation message) {
    Path tempDir;
    try {
      Files.createDirectories(localTempDir);
      tempDir = Files.createTempDirectory(localTempDir, "synthetic-profile-");
    } catch (IOException e) {
      throw new RuntimeException("Unable to prepare temporary directory", e);
    }

    try {
      List<FileInfo> downloadedFiles = new ArrayList<>();
      for (MetadataRecord record : message.getFiles()) {
        try {
          String fileName = record.getFileName();
          Path downloadedFile = tempDir.resolve(fileName);
          String fullFilePath = outputFileStore.appendToPath(outputFileStore.getRoot(), "dac", record.getFile());
          outputFileStore.downloadLocalFile(fullFilePath, downloadedFile);
          downloadedFiles.add(new FileInfo(fileName, record.getFile(), downloadedFile, ArgoFileType.forFileName(fileName)));
        } catch (IOException e) {
          throw new RuntimeException("Unable to download " + record.getFile(), e);
        }
      }

      FileInfo cProfile = downloadedFiles.stream().filter(fi -> fi.getFileType() == ArgoFileType.PROFILE_CORE).findFirst().orElseThrow();
      FileInfo bProfile = downloadedFiles.stream().filter(fi -> fi.getFileType() == ArgoFileType.PROFILE_BIOCHEMICAL).findFirst().orElseThrow();
      FileInfo meta = downloadedFiles.stream().filter(fi -> fi.getFileType() == ArgoFileType.METADATA).findFirst().orElseThrow();


      String file = getMetadataPath(message);
      String outputFile = getUploadFile(message);
      String fileName = getFileName(message);
      Path outputPath = tempDir.resolve(fileName);

      try {
        syntheticProfileMerger.mergeProfiles(cProfile.getPath(), bProfile.getPath(), meta.getPath(), outputPath);
      } catch (IOException e) {
        throw new RuntimeException("Unable to create synthetic profile", e);
      }

      MetadataRecord result;
      try {
        result = NetCdfMetadataRecord.fromV13SyntheticProfile(file, fileName, message.getDac(), outputPath, geoFilter);
      } catch (IOException e) {
        throw new RuntimeException("Unable to parse synthetic profile", e);
      }

      try {
        outputFileStore.uploadLocalFile(outputPath, outputFile);
      } catch (IOException e) {
        throw new RuntimeException("Unable to upload file " + outputFile, e);
      }

      return result;
    } finally {
      FileUtils.deleteQuietly(tempDir.toFile());
    }
  }

  private void notifyMergeCompleted(ProfileOperation message, MetadataRecord syntheticMerge, boolean remove) {
    Instant now = Instant.now();

    MetadataRecord metadata = null;
    List<String> relatedFiles = new ArrayList<>(3);
    for (MetadataRecord file : message.getFiles()) {
      if (file.getFileType() == ArgoFileType.METADATA) {
        metadata = file;
      }
      if (file.getFileType() == ArgoFileType.PROFILE_BIOCHEMICAL || file.getFileType() == ArgoFileType.PROFILE_CORE) {
        relatedFiles.add(file.getFile());
      }
    }

    messageSender.sendJson(
        updateIndexQueue,
        jsonMapper.writeValueAsString(MetadataRecord.builder(syntheticMerge)
            .withAction(remove ? Action.REMOVE : Action.UPDATE)
            .withActionTimestamp(now)
            .build()));

    if (metadata != null) {
      messageSender.sendJson(
          updateIndexQueue,
          jsonMapper.writeValueAsString(MetadataRecord.builder()
              .withTraceId(message.getTraceId())
              .withFileName(metadata.getFileName())
              .withFile(metadata.getFile())
              .withDac(message.getDac())
              .withFloatId(message.getFloatId())
              .withAction(remove ? Action.SYNTHETIC_MERGE_REMOVE : Action.SYNTHETIC_MERGE)
              .withFileType(ArgoFileType.METADATA)
              .withActionTimestamp(now)
              .withRelatedFiles(relatedFiles)
              .build()));
    } else {
      LOGGER.warn("No metadata record found for synth merge processing: {}", message);
    }
  }


  @Override
  public void generateSyntheticProfile(ProfileOperation message) {

    boolean remove = isRemoveMergeFile(message);
    MetadataRecord syntheticMerge;
    if (remove) {
      syntheticMerge = removeMergeFile(message);
    } else {
      syntheticMerge = mergeProfiles(message);
    }
    notifyMergeCompleted(message, syntheticMerge, remove);
  }

  private static class FileInfo {

    private final String fileName;
    private final String file;
    private final Path path;
    private final ArgoFileType fileType;


    private FileInfo(String fileName, String file, Path path, ArgoFileType fileType) {
      this.fileName = fileName;
      this.file = file;
      this.path = path;
      this.fileType = fileType;
    }

    public String getFileName() {
      return fileName;
    }

    public String getFile() {
      return file;
    }

    public Path getPath() {
      return path;
    }

    public ArgoFileType getFileType() {
      return fileType;
    }
  }

}
