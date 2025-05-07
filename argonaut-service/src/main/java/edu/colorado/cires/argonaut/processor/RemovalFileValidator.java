package edu.colorado.cires.argonaut.processor;

import edu.colorado.cires.argonaut.config.ServiceProperties;
import edu.colorado.cires.argonaut.message.HeaderConsts;
import edu.colorado.cires.argonaut.message.NcSubmissionMessage;
import edu.colorado.cires.argonaut.message.NcSubmissionMessage.Operation;
import edu.colorado.cires.argonaut.message.RemovalMessage;
import edu.colorado.cires.argonaut.util.ArgonautFileUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RemovalFileValidator implements Processor {

  private final ServiceProperties serviceProperties;

  @Autowired
  public RemovalFileValidator(ServiceProperties serviceProperties) {
    this.serviceProperties = serviceProperties;
  }

  private List<String> validate(String dac, Path removalFile) {
    List<String> errors = new ArrayList<>();
    String fileName = removalFile.getFileName().toString();
    if (!fileName.equals(dac + "_removal.txt")) {
      errors.add("removal file name does not match DAC '" + dac + "' (" + dac + "_removal.txt): " + fileName);
    }
    return errors;
  }

  private List<NcSubmissionMessage> parse(String dac, String timestamp, Path removalFile) throws IOException {
    List<NcSubmissionMessage> messages = new ArrayList<>();
    try (BufferedReader reader = new BufferedReader(new FileReader(removalFile.toFile()))) {
      reader.lines().forEach(line -> {
        if (StringUtils.isNotBlank(line)) {
          ArgonautFileUtils.ncSubmissionMessageFromFileName(line.trim()).ifPresent(message -> {
            message.setDac(dac);
            message.setTimestamp(timestamp);
            message.setOperation(Operation.REMOVE);
            messages.add(message);
          });
        }
      });
    }
    messages.forEach(message -> message.setNumberOfFilesInSubmission(messages.size()));
    return messages;
  }

  @Override
  public void process(Exchange exchange) throws Exception {
    String dac = exchange.getIn().getHeader(HeaderConsts.DAC, String.class);
    String timestamp = exchange.getIn().getHeader(HeaderConsts.SUBMISSION_TIMESTAMP, String.class);
    Path removalFile = exchange.getIn().getBody(File.class).toPath();
    Path submissionProcessedDir = ArgonautFileUtils.getSubmissionProcessedDirForDac(serviceProperties, dac).resolve(timestamp);
    ArgonautFileUtils.createDirectories(submissionProcessedDir);
    try {
      RemovalMessage output = new RemovalMessage();
      output.setFileName(removalFile.getFileName().toString());
      output.setTimestamp(timestamp);
      output.setDac(dac);
      exchange.getIn().setBody(output);
      output.setValidationError(validate(dac, removalFile));
      if (output.getValidationError().isEmpty()) {
        output.setFilesToRemove(parse(dac, timestamp, removalFile));
      }
    } finally {
      ArgonautFileUtils.move(removalFile, submissionProcessedDir.resolve(removalFile.getFileName()));
    }
  }
}
