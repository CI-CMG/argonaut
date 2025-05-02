package edu.colorado.cires.argonaut.processor;

import edu.colorado.cires.argonaut.config.ServiceProperties;
import edu.colorado.cires.argonaut.message.NcSubmissionMessage;
import edu.colorado.cires.argonaut.util.ArgonautFileUtils;
import java.io.FileWriter;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SubmissionReportProcessor implements Processor {

  private final ServiceProperties serviceProperties;
  private final Map<Path, ReentrantLock> lockMap = new HashMap<>();
  private final Map<Path, Integer> countMap = new HashMap<>();

  @Autowired
  public SubmissionReportProcessor(ServiceProperties serviceProperties) {
    this.serviceProperties = serviceProperties;
  }

  @Override
  public void process(Exchange exchange) throws Exception {
    NcSubmissionMessage message = exchange.getIn().getBody(NcSubmissionMessage.class);
    Path processedDir = ArgonautFileUtils.getSubmissionProcessedDirForDac(serviceProperties, message.getDac()).resolve(message.getTimestamp());
    Path submissionReportCsv = processedDir.resolve("submission_report.csv");
    ReentrantLock lock;
    synchronized (lockMap) {
      lock = lockMap.get(submissionReportCsv);
      if (lock == null) {
        lock = new ReentrantLock();
        lockMap.put(submissionReportCsv, lock);
        countMap.put(submissionReportCsv, 0);
      }
      countMap.put(submissionReportCsv, countMap.get(submissionReportCsv) + 1);
    }
    lock.lock();
    try {
      ArgonautFileUtils.createDirectories(processedDir);
      CSVFormat csvFormat = CSVFormat.DEFAULT.builder().setTrim(true).get();
      String reportMessage = message.getValidationError().isEmpty() ? "success" : String.join("\n", message.getValidationError());
      try (final CSVPrinter printer = new CSVPrinter(new FileWriter(submissionReportCsv.toFile(), true), csvFormat)) {
        printer.printRecord(message.getTimestamp(), message.getDac(), message.getFloatId(), message.getFileName(), reportMessage);
      }
    } finally {
      lock.unlock();
      synchronized (lockMap) {
        int count = countMap.get(submissionReportCsv);
        if (count == 1) {
          lockMap.remove(submissionReportCsv);
          countMap.remove(submissionReportCsv);
        } else {
          countMap.put(submissionReportCsv, count - 1);
        }
      }
    }

  }
}
