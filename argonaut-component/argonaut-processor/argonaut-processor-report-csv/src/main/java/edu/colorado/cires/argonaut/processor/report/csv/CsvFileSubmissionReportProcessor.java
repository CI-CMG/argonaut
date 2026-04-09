package edu.colorado.cires.argonaut.processor.report.csv;

import edu.colorado.cires.argonaut.file.core.FileStore;
import edu.colorado.cires.argonaut.messaging.core.databind.NcSubmissionMessage;
import edu.colorado.cires.argonaut.processor.core.SubmissionReportProcessor;

public class CsvFileSubmissionReportProcessor implements SubmissionReportProcessor {

  private FileStore submissionFileStore;

  @Override
  public void appendReport(NcSubmissionMessage message) {
//    String submissionReportCsv = submissionFileStore.appendToPath(submissionFileStore.getRoot(), "dac", message.getDac(), "processed",
//        message.getTimestamp().toString(), "submission_report.csv");
//    submissionFileStore
//    String processedDir = ArgonautFileUtils.getSubmissionProcessedDirForDac(serviceProperties, message.getDac()).resolve(message.getTimestamp());
//    Path submissionReportCsv = processedDir.resolve("submission_report.csv");
//    ReentrantLock lock;
//    synchronized (lockMap) {
//      lock = lockMap.get(submissionReportCsv);
//      if (lock == null) {
//        lock = new ReentrantLock();
//        lockMap.put(submissionReportCsv, lock);
//        countMap.put(submissionReportCsv, 0);
//      }
//      countMap.put(submissionReportCsv, countMap.get(submissionReportCsv) + 1);
//    }
//    lock.lock();
//    try {
//      ArgonautFileUtils.createDirectories(processedDir);
//      CSVFormat csvFormat = CSVFormat.DEFAULT.builder().setTrim(true).get();
//      String reportMessage = message.getValidationErrors().isEmpty() ? successMessage(message) : String.join("\n", message.getValidationErrors());
//      try (
//          FileWriter writer = new FileWriter(submissionReportCsv.toFile(), StandardCharsets.UTF_8, true);
//          CSVPrinter printer = new CSVPrinter(writer, csvFormat)
//      ) {
//        printer.printRecord(
//            valueOrEmpty(message.getTimestamp()),
//            valueOrEmpty(message.getDac()),
//            valueOrEmpty(message.getFloatId()),
//            valueOrEmpty(message.getFileName()),
//            valueOrEmpty(reportMessage)
//        );
//      }
//    } finally {
//      lock.unlock();
//      synchronized (lockMap) {
//        int count = countMap.get(submissionReportCsv);
//        if (count == 1) {
//          lockMap.remove(submissionReportCsv);
//          countMap.remove(submissionReportCsv);
//        } else {
//          countMap.put(submissionReportCsv, count - 1);
//        }
//      }
//    }

  }


  private static String successMessage(NcSubmissionMessage message) {
    switch (message.getOperation()) {
      case ADD:
        return "added";
      case REMOVE:
        return "removed";
      default:
        throw new IllegalArgumentException("Operation not supported: " + message.getOperation());
    }
  }

  private static String valueOrEmpty(String value) {
    if (value == null) {
      return "";
    }
    return value.trim();
  }
}
