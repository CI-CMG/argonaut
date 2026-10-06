package edu.colorado.cires.argonaut.standalone;

import edu.colorado.cires.argonaut.file.core.FileStore;
import edu.colorado.cires.argonaut.messaging.core.databind.SubmissionReportSet;
import edu.colorado.cires.argonaut.processor.core.SubmissionReportAuditPostProcessor;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import org.apache.commons.io.IOUtils;

public class SubmissionReportFileSaver extends SubmissionReportAuditPostProcessor {

  private FileStore outputFileStore;

  public void setOutputFileStore(FileStore outputFileStore) {
    this.outputFileStore = outputFileStore;
  }

  @Override
  protected SubmissionReportSet addToReport(SubmissionReportSet submissionReportSet) {
    if (!submissionReportSet.getEvents().isEmpty()) {
      String path = outputFileStore.appendToPath(
          outputFileStore.getRoot(),
          "report",
          submissionReportSet.getDac(),
          Instant.now().toString() + "_report.html");

      try (OutputStream outputStream = outputFileStore.getOutputStream(path)) {
        IOUtils.write(submissionReportSet.getReport(), outputStream, StandardCharsets.UTF_8);
      } catch (IOException e) {
        throw new RuntimeException("Unable to save report " + path, e);
      }
    }
    return submissionReportSet;
  }

}
