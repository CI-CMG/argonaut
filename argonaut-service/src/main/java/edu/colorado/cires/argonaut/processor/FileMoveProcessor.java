package edu.colorado.cires.argonaut.processor;

import edu.colorado.cires.argonaut.config.ServiceProperties;
import edu.colorado.cires.argonaut.message.NcSubmissionMessage;
import edu.colorado.cires.argonaut.util.ArgonautFileUtils;
import java.nio.file.Path;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FileMoveProcessor implements Processor {

  private static final Logger LOGGER = LoggerFactory.getLogger(FileMoveProcessor.class);

  private final ServiceProperties serviceProperties;

  @Autowired
  public FileMoveProcessor(ServiceProperties serviceProperties) {
    this.serviceProperties = serviceProperties;
  }

  private void handleAdd(NcSubmissionMessage message) {
    Path source = ArgonautFileUtils.getProcessingProfileDir(serviceProperties, message.getDac(), message.getFloatId(), message.isProfile())
        .resolve(message.getFileName());
    Path destDir;
    if (message.getValidationError().isEmpty()) {
      destDir = ArgonautFileUtils.getOutputProfileDir(serviceProperties, message.getDac(), message.getFloatId(), message.isProfile());
    } else {
      destDir = ArgonautFileUtils.getRejectProfileDir(serviceProperties, message.getDac(), message.getTimestamp(), message.getFloatId(), message.isProfile());
    }
    ArgonautFileUtils.createDirectories(destDir);
    Path dest = destDir.resolve(message.getFileName());
    LOGGER.info("Moving file {} to {}", source, dest);
    ArgonautFileUtils.move(source, dest);
  }

  private void handleRemove(NcSubmissionMessage message) {
    Path source = ArgonautFileUtils.getOutputProfileDir(serviceProperties, message.getDac(), message.getFloatId(), message.isProfile())
        .resolve(message.getFileName());
    Path destDir = ArgonautFileUtils.getRemovedProfileDir(serviceProperties, message.getDac(), message.getTimestamp(), message.getFloatId(), message.isProfile());
    ArgonautFileUtils.createDirectories(destDir);
    Path dest = destDir.resolve(message.getFileName());
    LOGGER.info("Moving file {} to {}", source, dest);
    ArgonautFileUtils.move(source, dest);
  }

  @Override
  public void process(Exchange exchange) throws Exception {
    NcSubmissionMessage message = exchange.getIn().getBody(NcSubmissionMessage.class);
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
}
