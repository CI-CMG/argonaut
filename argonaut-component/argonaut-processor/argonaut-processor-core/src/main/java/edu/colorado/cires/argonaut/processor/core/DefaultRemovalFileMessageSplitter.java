package edu.colorado.cires.argonaut.processor.core;

import edu.colorado.cires.argonaut.file.core.FileStore;
import edu.colorado.cires.argonaut.messaging.core.databind.ArgoFileType;
import edu.colorado.cires.argonaut.messaging.core.databind.ArgoFileTypeDetails;
import edu.colorado.cires.argonaut.messaging.core.databind.NcSubmissionMessage;
import edu.colorado.cires.argonaut.messaging.core.databind.NcSubmissionMessage.Operation;
import edu.colorado.cires.argonaut.messaging.core.queue.MessageSender;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.databind.json.JsonMapper;

public class DefaultRemovalFileMessageSplitter implements RemovalFileMessageSplitter {

  private static final Logger LOGGER = LoggerFactory.getLogger(DefaultRemovalFileMessageSplitter.class);

  private MessageSender messageSender;
  private String fileMoveQueue;
  private FileStore processingFileStore;
  private JsonMapper jsonMapper;

  public void setJsonMapper(JsonMapper jsonMapper) {
    this.jsonMapper = jsonMapper;
  }

  public void setMessageSender(MessageSender messageSender) {
    this.messageSender = messageSender;
  }

  public void setFileMoveQueue(String fileMoveQueue) {
    this.fileMoveQueue = fileMoveQueue;
  }

  public void setProcessingFileStore(FileStore processingFileStore) {
    this.processingFileStore = processingFileStore;
  }

  @Override
  public void splitRemovalFileMessages(NcSubmissionMessage ncSubmissionMessage) {
    split(resolveDownloadPath(ncSubmissionMessage), ncSubmissionMessage);
  }

  private void split(String remotePath, NcSubmissionMessage parent) {
    try (BufferedReader reader = new BufferedReader(
        new InputStreamReader(processingFileStore.getInputStream(remotePath), StandardCharsets.US_ASCII))) {
      String fileName;
      while ((fileName = reader.readLine()) != null) {
        fileName = fileName.trim();
        if (StringUtils.isNotBlank(fileName)) {

          ArgoFileTypeDetails fileTypeDetails = ArgoFileType.getFileNameDetails(fileName);
          String floatDir = fileTypeDetails.getFloatId();
          NcSubmissionMessage message = NcSubmissionMessage.builder()
              .withOperation(Operation.REMOVE)
              .withFileName(fileName)
              .withFileType(fileTypeDetails.getType())
              .withDac(parent.getDac())
              .withTimestamp(parent.getTimestamp())
              .withTraceId(parent.getTraceId())
              .withFloatId(floatDir)
              .build();

          LOGGER.info("Notifying file removal {} : {}", message.getDac(), message.getFileName());

          messageSender.sendJson(fileMoveQueue, jsonMapper.writeValueAsString(message));

        }
      }
    } catch (IOException e) {
      throw new RuntimeException("Unable to open removal file", e);
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
}
