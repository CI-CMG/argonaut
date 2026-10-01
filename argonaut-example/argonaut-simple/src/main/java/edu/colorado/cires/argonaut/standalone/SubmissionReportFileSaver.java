package edu.colorado.cires.argonaut.standalone;

import edu.colorado.cires.argonaut.file.core.FileStore;
import edu.colorado.cires.argonaut.messaging.core.databind.SubmissionReportSet;
import edu.colorado.cires.argonaut.processor.core.SubmissionReportAuditPostProcessor;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;

public class SubmissionReportFileSaver extends SubmissionReportAuditPostProcessor {

  private FileStore submissionFileStore;

  public void setSubmissionFileStore(FileStore submissionFileStore) {
    this.submissionFileStore = submissionFileStore;
  }

  @Override
  protected void doWithReport(SubmissionReportSet submissionReportSet) {

    String path = submissionFileStore.appendToPath(
        submissionFileStore.getRoot(),
        "dac",
        submissionReportSet.getDac(),
        submissionReportSet.getEvents().get(0).getEventId().toString() + "_report.html");

    try (OutputStream outputStream = submissionFileStore.getOutputStream(path)) {
      IOUtils.write(submissionReportSet.getReport(), outputStream, StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new RuntimeException("Unable to save report " + path, e);
    }
  }

}
