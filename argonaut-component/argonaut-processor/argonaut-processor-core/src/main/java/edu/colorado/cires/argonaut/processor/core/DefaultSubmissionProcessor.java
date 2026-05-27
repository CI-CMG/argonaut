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

  static Optional<NcSubmissionMessage> moveSingleFile(DacSubmittedFileMessage submittedFile, Path file, String fileName,
      FileStore processingFileStore) {
    ArgoFileTypeDetails fileTypeDetails = ArgoFileType.getFileNameDetails(fileName);
    if (fileTypeDetails.getType() == ArgoFileType.TECHNICAL_DATA ||
        fileTypeDetails.getType() == ArgoFileType.PROFILE_CORE ||
        fileTypeDetails.getType() == ArgoFileType.PROFILE_BIOCHEMICAL ||
        fileTypeDetails.getType() == ArgoFileType.TRAJECTORY ||
        fileTypeDetails.getType() == ArgoFileType.METADATA
    ) {
      String floatDir = fileTypeDetails.getFloatId();
      boolean profile = ArgoFileType.isProfile(fileTypeDetails.getType());
      NcSubmissionMessage ncSubmissionMessage = NcSubmissionMessage.builder()
          .withOperation(Operation.ADD)
          .withFileName(fileName)
          .withFileType(fileTypeDetails.getType())
          .withFloatId(floatDir)
          .withDac(submittedFile.getDac())
          .withTimestamp(submittedFile.getTimestamp())
          .build();
      String processingDacDir = processingFileStore.appendToPath(processingFileStore.getRoot(), "dac", submittedFile.getDac(),
          submittedFile.getTimestamp().toString(), floatDir);
      if (profile) {
        processingDacDir = processingFileStore.appendToPath(processingDacDir, "profiles");
      }
      String ncFile = processingFileStore.appendToPath(processingDacDir, fileName);
      LOGGER.info("Adding to processing directory {}", ncFile);
      try {
        processingFileStore.uploadLocalFile(file, ncFile);
      } catch (IOException e) {
        throw new RuntimeException("Unable to upload file: " + file + " to " + ncFile, e);
      }
      return Optional.of(ncSubmissionMessage);
    }
    return Optional.empty();
  }

  @Override
  public Optional<NcSubmissionMessage> moveToProcessing(DacSubmittedFileMessage submittedFile) {

    String fileName = submissionFileStore.getFileName(submittedFile.getPath());

    Path tempWorkDir;
    try {
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
