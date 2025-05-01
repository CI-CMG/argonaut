package edu.colorado.cires.argonaut.processor;

import edu.colorado.cires.argonaut.ServiceProperties;
import edu.colorado.cires.argonaut.message.NcSubmissionMessage;
import edu.colorado.cires.argonaut.util.ArgonautFileUtils;
import java.io.FileWriter;
import java.nio.file.Path;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SubmissionReportProcessor implements Processor {

  private final ServiceProperties serviceProperties;

  @Autowired
  public SubmissionReportProcessor(ServiceProperties serviceProperties) {
    this.serviceProperties = serviceProperties;
  }

  @Override
  public void process(Exchange exchange) throws Exception {
    NcSubmissionMessage message = exchange.getIn().getBody(NcSubmissionMessage.class);
    Path processedDir = ArgonautFileUtils.getSubmissionProcessedDirForDac(serviceProperties, message.getDac());
    ArgonautFileUtils.createDirectories(processedDir);
    Path submissionReportCsv = processedDir.resolve("submission_report.csv");
    String reportMessage = message.getValidationError() == null ? "success" : message.getValidationError();
    String row = String.join(",", message.getTimestamp(), message.getDac(), message.getFloatId(), message.getFileName(), reportMessage);
    try(FileWriter fileWriter = new FileWriter(submissionReportCsv.toFile(), true)) {
      fileWriter.write(row);
      fileWriter.write("\n");
    }
  }
}
