package edu.colorado.cires.argonaut.processor.core;

import edu.colorado.cires.argonaut.file.core.FileStore;
import edu.colorado.cires.argonaut.messaging.core.databind.ArgoFileType;
import edu.colorado.cires.argonaut.messaging.core.databind.NcSubmissionMessage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

public class DefaultRemovalFileValidationProcessor implements RemovalFileValidationProcessor {

  private FileStore processingFileStore;

  public void setProcessingFileStore(FileStore processingFileStore) {
    this.processingFileStore = processingFileStore;
  }

  @Override
  public NcSubmissionMessage validate(NcSubmissionMessage ncSubmissionMessage) {
    String dac = ncSubmissionMessage.getDac();
    String fileName = ncSubmissionMessage.getFileName();
    String remotePath = resolveDownloadPath(ncSubmissionMessage);
    List<String> errors = validateFile(remotePath, dac, fileName);
    return NcSubmissionMessage.builder(ncSubmissionMessage).withValidationErrors(errors).build();

  }

  private List<String> validateFile(String remotePath, String dac, String fileName) {
    if (!fileName.equals(dac + "_removal.txt")) {
      return Collections.singletonList("file name does not start with DAC identifier: '" + fileName + "', " + dac);
    }
    try (BufferedReader reader = new BufferedReader(
        new InputStreamReader(processingFileStore.getInputStream(remotePath), StandardCharsets.US_ASCII))) {
      List<String> errors = new ArrayList<>();
      int lineNumber = 0;
      String line;
      while ((line = reader.readLine()) != null) {
        if (StringUtils.isNotBlank(line)) {
          lineNumber++;
          line = line.trim();
          ArgoFileType type = ArgoFileType.forFileName(line);
          if (type != ArgoFileType.TECHNICAL_DATA &&
              type != ArgoFileType.PROFILE_CORE &&
              type != ArgoFileType.PROFILE_BIOCHEMICAL &&
              type != ArgoFileType.TRAJECTORY &&
              type != ArgoFileType.METADATA) {
            errors.add("file name on line " + lineNumber + " does not match a known type");
          }
        }
      }
      if (lineNumber == 0) {
        errors.add("removal file did not contain any file names");
      }
      return errors;
    } catch (IOException e) {
      return Collections.singletonList("unable to read removal file: " + ExceptionUtils.getRootCauseMessage(e));
    }
  }

  private String resolveDownloadPath(NcSubmissionMessage ncSubmissionMessage) {
    return processingFileStore.appendToPath(
        processingFileStore.getRoot(),
        "dac",
        ncSubmissionMessage.getDac(),
        ncSubmissionMessage.getTimestamp().toString(),
        ncSubmissionMessage.getFileName());
  }

  private void download(String remotePath, Path localPath) {
    try {
      processingFileStore.downloadLocalFile(remotePath, localPath);
    } catch (IOException e) {
      throw new RuntimeException("Unable to download " + remotePath, e);
    }
  }


}
