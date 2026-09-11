package edu.colorado.cires.argonaut.processor.core;

import edu.colorado.cires.argonaut.file.core.FileStore;
import edu.colorado.cires.argonaut.messaging.core.databind.ArgoFileType;
import edu.colorado.cires.argonaut.messaging.core.databind.ArgoFileTypeDetails;
import edu.colorado.cires.argonaut.messaging.core.databind.DacSubmittedFileMessage;
import edu.colorado.cires.argonaut.messaging.core.databind.NcSubmissionMessage;
import edu.colorado.cires.argonaut.messaging.core.databind.NcSubmissionMessage.Operation;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultSubmissionProcessor implements SubmissionProcessor {

  private static final Logger LOGGER = LoggerFactory.getLogger(DefaultSubmissionProcessor.class);

  private FileStore submissionFileStore;
  private FileStore processingFileStore;
  private Path localTempDir;

  public void setSubmissionFileStore(FileStore submissionFileStore) {
    this.submissionFileStore = submissionFileStore;
  }

  public void setLocalTempDir(Path localTempDir) {
    this.localTempDir = localTempDir;
  }

  public void setProcessingFileStore(FileStore processingFileStore) {
    this.processingFileStore = processingFileStore;
  }

  private static boolean isSubmittableDataFile(String fileName) {
    ArgoFileTypeDetails fileTypeDetails = ArgoFileType.getFileNameDetails(fileName);
    return fileTypeDetails.getType() == ArgoFileType.TECHNICAL_DATA ||
        fileTypeDetails.getType() == ArgoFileType.PROFILE_CORE ||
        fileTypeDetails.getType() == ArgoFileType.PROFILE_BIOCHEMICAL ||
        fileTypeDetails.getType() == ArgoFileType.TRAJECTORY ||
        fileTypeDetails.getType() == ArgoFileType.METADATA;
  }

  private static boolean isRemovalFile(String fileName) {
    ArgoFileTypeDetails fileTypeDetails = ArgoFileType.getFileNameDetails(fileName);
    return fileTypeDetails.getType() == ArgoFileType.REMOVAL_TXT;
  }

  private static NcSubmissionMessage createDataSubmissionMessage(DacSubmittedFileMessage submittedFile, String fileName) {
    ArgoFileTypeDetails fileTypeDetails = ArgoFileType.getFileNameDetails(fileName);
    String floatDir = fileTypeDetails.getFloatId();
    return NcSubmissionMessage.builder()
        .withOperation(Operation.ADD)
        .withFileName(fileName)
        .withFileType(fileTypeDetails.getType())
        .withFloatId(floatDir)
        .withDac(submittedFile.getDac())
        .withTimestamp(submittedFile.getTimestamp())
        .withTraceId(submittedFile.getTraceId())
        .build();
  }

  private static NcSubmissionMessage createRemovalMessage(DacSubmittedFileMessage submittedFile, String fileName) {
    return NcSubmissionMessage.builder()
        .withOperation(Operation.REMOVE)
        .withFileName(fileName)
        .withFileType(ArgoFileType.REMOVAL_TXT)
        .withDac(submittedFile.getDac())
        .withTimestamp(submittedFile.getTimestamp())
        .withTraceId(submittedFile.getTraceId())
        .build();
  }

  private static String resolveDataFileName(NcSubmissionMessage message, FileStore processingFileStore) {
    String processingDacDir = processingFileStore.appendToPath(processingFileStore.getRoot(), "dac", message.getDac(),
        message.getTimestamp().toString(), message.getFloatId());
    if (ArgoFileType.isProfile(message.getFileType())) {
      processingDacDir = processingFileStore.appendToPath(processingDacDir, "profiles");
    }
    return processingFileStore.appendToPath(processingDacDir, message.getFileName());
  }

  private static String resolveRemovalFileName(NcSubmissionMessage message, FileStore processingFileStore) {
    return processingFileStore.appendToPath(processingFileStore.getRoot(), "dac", message.getDac(),
        message.getTimestamp().toString(), message.getFileName());
  }

  private static void uploadProcessingFile(Path localFile, String remotePath, FileStore processingFileStore) {
    LOGGER.info("Adding to processing directory {}", remotePath);
    try {
      processingFileStore.uploadLocalFile(localFile, remotePath);
    } catch (IOException e) {
      throw new RuntimeException("Unable to upload file: " + localFile + " to " + remotePath, e);
    }
  }

  static Optional<NcSubmissionMessage> moveSingleFile(DacSubmittedFileMessage submittedFile, Path file, String fileName, FileStore processingFileStore) {
    if (isSubmittableDataFile(fileName)) {
      NcSubmissionMessage message = createDataSubmissionMessage(submittedFile, fileName);
      uploadProcessingFile(file, resolveDataFileName(message, processingFileStore), processingFileStore);
      return Optional.of(message);
    } else if (isRemovalFile(fileName)) {
      NcSubmissionMessage message = createRemovalMessage(submittedFile, fileName);
      uploadProcessingFile(file, resolveRemovalFileName(message, processingFileStore), processingFileStore);
      return Optional.of(message);
    }
    return Optional.empty();
  }

  @Override
  public Optional<NcSubmissionMessage> moveToProcessing(DacSubmittedFileMessage submittedFile) {

    String fileName = submissionFileStore.getFileName(submittedFile.getPath());

    Path tempWorkDir;
    try {
      Files.createDirectories(localTempDir);
      tempWorkDir = Files.createTempDirectory(localTempDir, "processing");
    } catch (IOException e) {
      throw new RuntimeException("An error occurred while creating local work directory", e);
    }

    try {
      Path file = tempWorkDir.resolve(fileName);
      try {
        submissionFileStore.downloadLocalFile(submittedFile.getPath(), file);
      } catch (IOException e) {
        throw new RuntimeException("Unable to copy: " + submittedFile.getPath() + " to " + file, e);
      }
      return moveSingleFile(submittedFile, file, fileName, processingFileStore);
    } finally {
      FileUtils.deleteQuietly(tempWorkDir.toFile());
      String processedPath = submissionFileStore.appendToPath(submissionFileStore.getRoot(), "dac", submittedFile.getDac(), "processed",
          submittedFile.getTimestamp().toString(), fileName);
      submissionFileStore.move(submittedFile.getPath(), processedPath);
    }
  }
}
