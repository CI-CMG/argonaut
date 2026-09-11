package edu.colorado.cires.argonaut.processor.core;

import edu.colorado.cires.argonaut.file.core.FileStore;
import edu.colorado.cires.argonaut.messaging.core.databind.ArgoFileType;
import edu.colorado.cires.argonaut.messaging.core.databind.NcSubmissionMessage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultFileMoveProcessor implements FileMoveProcessor {

  private static final Logger LOGGER = LoggerFactory.getLogger(DefaultFileMoveProcessor.class);

  private FileStore submissionFileStore;
  private FileStore processingFileStore;
  private FileStore outputFileStore;

  public void setSubmissionFileStore(FileStore submissionFileStore) {
    this.submissionFileStore = submissionFileStore;
  }

  public void setProcessingFileStore(FileStore processingFileStore) {
    this.processingFileStore = processingFileStore;
  }

  public void setOutputFileStore(FileStore outputFileStore) {
    this.outputFileStore = outputFileStore;
  }

  @Override
  public void moveFile(NcSubmissionMessage message) {
    switch (message.getOperation()) {
      case ADD:
        handleAdd(message);
        break;
      case REMOVE:
        handleRemove(message);
        break;
      default:
        throw new IllegalArgumentException("Unknown operation: " + message.getOperation());
    }
  }

  private String resolveOutputFile(NcSubmissionMessage ncSubmissionMessage) {
    String destinationDir = outputFileStore.appendToPath(outputFileStore.getRoot(), "dac", ncSubmissionMessage.getDac(),
        ncSubmissionMessage.getFloatId());
    if (ArgoFileType.isProfile(ncSubmissionMessage.getFileType())) {
      destinationDir = outputFileStore.appendToPath(destinationDir, "profiles");
    }
    return outputFileStore.appendToPath(destinationDir, ncSubmissionMessage.getFileName());
  }

  private String resolveRemovedFile(NcSubmissionMessage ncSubmissionMessage) {
    return outputFileStore.appendToPath(outputFileStore.getRoot(), "etc", "removed", ncSubmissionMessage.getDac(), ncSubmissionMessage.getFileName());
  }

  private void handleRemove(NcSubmissionMessage ncSubmissionMessage) {
    String source = resolveOutputFile(ncSubmissionMessage);
    String destination = resolveRemovedFile(ncSubmissionMessage);
    outputFileStore.move(source, destination);
    LOGGER.info("Moved removed file from {} to {}", source, destination);
  }

  private void handleAdd(NcSubmissionMessage ncSubmissionMessage) {
    String processingDacDir = processingFileStore.appendToPath(processingFileStore.getRoot(), "dac", ncSubmissionMessage.getDac(),
        ncSubmissionMessage.getTimestamp().toString(), ncSubmissionMessage.getFloatId());
    if (ArgoFileType.isProfile(ncSubmissionMessage.getFileType())) {
      processingDacDir = processingFileStore.appendToPath(processingDacDir, "profiles");
    }
    String processingFile = processingFileStore.appendToPath(processingDacDir, ncSubmissionMessage.getFileName());
    if (ncSubmissionMessage.getValidationErrors().isEmpty()) {
      String destinationFile = resolveOutputFile(ncSubmissionMessage);
      try (InputStream inputStream = processingFileStore.getInputStream(processingFile);
          OutputStream outputStream = outputFileStore.getOutputStream(destinationFile)
      ) {
        IOUtils.copy(inputStream, outputStream);
      } catch (IOException e) {
        throw new RuntimeException("Unable to copy " + processingFile + " to " + destinationFile, e);
      }
      processingFileStore.delete(processingFile);
    } else {
      String destinationDir = submissionFileStore.appendToPath(submissionFileStore.getRoot(), "dac", ncSubmissionMessage.getDac(), "processed",
          ncSubmissionMessage.getTimestamp().toString(), "reject", ncSubmissionMessage.getFloatId().toString());
      if (ArgoFileType.isProfile(ncSubmissionMessage.getFileType())) {
        destinationDir = submissionFileStore.appendToPath(destinationDir, "profiles");
      }
      String destinationFile = submissionFileStore.appendToPath(destinationDir, ncSubmissionMessage.getFileName());
      try (InputStream inputStream = processingFileStore.getInputStream(processingFile);
          OutputStream outputStream = submissionFileStore.getOutputStream(destinationFile)
      ) {
        IOUtils.copy(inputStream, outputStream);
      } catch (IOException e) {
        throw new RuntimeException("Unable to copy " + processingFile + " to " + destinationFile, e);
      }
      processingFileStore.delete(processingFile);
    }
  }
}
