package edu.colorado.cires.argonaut.processor.core;

import edu.colorado.cires.argonaut.file.core.FileStore;
import edu.colorado.cires.argonaut.messaging.core.databind.ArgoFileType;
import edu.colorado.cires.argonaut.messaging.core.databind.NcSubmissionMessage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.io.IOUtils;

public class DefaultFileMoveProcessor implements FileMoveProcessor {

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

  private void handleRemove(NcSubmissionMessage message) {
    //TODO
    throw new UnsupportedOperationException("Remove operation is not supported");
    /*
        Path source = ArgonautFileUtils.getOutputProfileDir(serviceProperties, message.getDac(), message.getFloatId(), message.isProfile())
        .resolve(message.getFileName());
    Path destDir = ArgonautFileUtils.getRemovedProfileDir(serviceProperties, message.getDac(), message.getTimestamp(), message.getFloatId(), message.isProfile());
    ArgonautFileUtils.createDirectories(destDir);
    Path dest = destDir.resolve(message.getFileName());
    LOGGER.info("Moving file {} to {}", source, dest);
    ArgonautFileUtils.move(source, dest);
     */
  }

  private void handleAdd(NcSubmissionMessage ncSubmissionMessage) {
    String processingDacDir = processingFileStore.appendToPath(processingFileStore.getRoot(), "dac", ncSubmissionMessage.getDac(),
        ncSubmissionMessage.getTimestamp().toString(), ncSubmissionMessage.getFloatId());
    if (ArgoFileType.isProfile(ncSubmissionMessage.getFileType())) {
      processingDacDir = processingFileStore.appendToPath(processingDacDir, "profiles");
    }
    String processingFile = processingFileStore.appendToPath(processingDacDir, ncSubmissionMessage.getFileName());
    if (ncSubmissionMessage.getValidationErrors().isEmpty()) {
      String destinationDir = outputFileStore.appendToPath(outputFileStore.getRoot(), "dac", ncSubmissionMessage.getDac(),
          ncSubmissionMessage.getFloatId());
      if (ArgoFileType.isProfile(ncSubmissionMessage.getFileType())) {
        destinationDir = outputFileStore.appendToPath(destinationDir, "profiles");
      }
      String destinationFile = outputFileStore.appendToPath(destinationDir, ncSubmissionMessage.getFileName());
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
      if (ArgoFileType.PROFILE_CORE == ncSubmissionMessage.getFileType()) {
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
