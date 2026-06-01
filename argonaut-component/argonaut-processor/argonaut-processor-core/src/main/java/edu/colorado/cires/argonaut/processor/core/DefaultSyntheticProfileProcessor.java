package edu.colorado.cires.argonaut.processor.core;

import edu.colorado.cires.argonaut.core.merge.synthetic.SyntheticProfileMerger;
import edu.colorado.cires.argonaut.file.core.FileStore;
import edu.colorado.cires.argonaut.messaging.core.databind.ArgoFileType;
import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord;
import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord.Action;
import edu.colorado.cires.argonaut.messaging.core.databind.ProfileOperation;
import edu.colorado.cires.argonaut.messaging.core.queue.MessageSender;
import edu.colorado.cires.argonaut.processor.core.transform.NetCdfMetadataRecord;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import tools.jackson.databind.json.JsonMapper;

public class DefaultSyntheticProfileProcessor implements SyntheticProfileProcessor {

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

  @Override
  public void generateSyntheticProfile(ProfileOperation message) {
    Path tempDir;
    try {
      Files.createDirectories(localTempDir);
      tempDir = Files.createTempDirectory(localTempDir, "synthetic-profile-");
    } catch (IOException e) {
      throw new RuntimeException("Unable to prepare temporary directory", e);
    }

    List<MetadataRecord> metadataRecordUpdates = new ArrayList<>(4);
    try {
      List<FileInfo> downloadedFiles = new ArrayList<>();
      for (String file : message.getFiles()) {
        try {
          String fileName = outputFileStore.getFileName(file);
          Path downloadedFile = tempDir.resolve(fileName);
          String fullFilePath = outputFileStore.appendToPath(outputFileStore.getRoot(), "dac", file);
          outputFileStore.downloadLocalFile(fullFilePath, downloadedFile);
          downloadedFiles.add(new FileInfo(fileName, file, downloadedFile, ArgoFileType.forFileName(fileName)));
        } catch (IOException e) {
          throw new RuntimeException("Unable to download " + file, e);
        }
      }

      FileInfo cProfile = downloadedFiles.stream().filter(fi -> fi.getFileType() == ArgoFileType.PROFILE_CORE).findFirst().orElseThrow();
      FileInfo bProfile = downloadedFiles.stream().filter(fi -> fi.getFileType() == ArgoFileType.PROFILE_BIOCHEMICAL).findFirst().orElseThrow();
      FileInfo meta = downloadedFiles.stream().filter(fi -> fi.getFileType() == ArgoFileType.METADATA).findFirst().orElseThrow();
      String sProfileFileName = "S" + cProfile.getFileName();
      Path outputPath = tempDir.resolve(sProfileFileName);

      try {
        syntheticProfileMerger.mergeProfiles(cProfile.getPath(), bProfile.getPath(), meta.getPath(), outputPath);
      } catch (IOException e) {
        throw new RuntimeException("Unable to create synthetic profile", e);
      }

      String sFile = outputFileStore.appendToPath(message.getDac(), message.getFloatId(), "profiles", sProfileFileName);
      String sUploadPath = outputFileStore.appendToPath(outputFileStore.getRoot(), "dac", sFile);

      metadataRecordUpdates.add(createMetadataRecord(cProfile, message));
      metadataRecordUpdates.add(createMetadataRecord(bProfile, message));
      metadataRecordUpdates.add(createMetadataRecord(meta, message));

      try {
        metadataRecordUpdates.add(NetCdfMetadataRecord.fromV13SyntheticProfile(sFile, message.getDac(), outputPath, geoFilter));
      } catch (IOException e) {
        throw new RuntimeException("Unable to parse synthetic profile", e);
      }

      try {
        outputFileStore.uploadLocalFile(outputPath, sUploadPath);
      } catch (IOException e) {
        throw new RuntimeException("Unable to upload file " + sUploadPath, e);
      }

    } finally {
      FileUtils.deleteQuietly(tempDir.toFile());
    }

    metadataRecordUpdates.stream()
        .map(jsonMapper::writeValueAsString)
        .forEach(json -> messageSender.sendJson(updateIndexQueue, json));

  }

  private static MetadataRecord createMetadataRecord(FileInfo fileInfo, ProfileOperation message) {
    return MetadataRecord.builder()
        .withTraceId(message.getTraceId())
        .withFileName(fileInfo.getFileName())
        .withFile(fileInfo.getFile())
        .withAction(Action.SYNTHETIC_MERGE)
        .withDac(message.getDac())
        .withFloatId(message.getFloatId())
        .withFileType(fileInfo.getFileType())
        .build();
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
