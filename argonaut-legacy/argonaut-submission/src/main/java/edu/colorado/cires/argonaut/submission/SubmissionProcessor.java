package edu.colorado.cires.argonaut.submission;

import edu.colorado.cires.argonaut.config.ArgonautDirectoryConfig;
import edu.colorado.cires.argonaut.message.NcSubmissionMessage;
import edu.colorado.cires.argonaut.util.ArgonautFileUtils;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SubmissionProcessor {

  private static final Logger LOGGER = LoggerFactory.getLogger(SubmissionProcessor.class);

  private final ArgonautDirectoryConfig serviceProperties;
  private final Path workingDir;
  private final Path processingDir;

  public SubmissionProcessor(ArgonautDirectoryConfig serviceProperties) {
    this.serviceProperties = serviceProperties;
    workingDir = serviceProperties.getWorkDirectory();
    processingDir = ArgonautFileUtils.getProcessingDir(serviceProperties);
    ArgonautFileUtils.createDirectories(processingDir);
  }

  public List<NcSubmissionMessage> untarAndMoveToProcessing(String dac, String timestamp, Path submittedTarGzFile) throws Exception {
    List<NcSubmissionMessage> output = new ArrayList<>();
    Path submissionProcessingDir = ArgonautFileUtils.getSubmissionProcessingDirForDac(serviceProperties, dac).resolve(timestamp);
    Path submissionProcessedDir = ArgonautFileUtils.getSubmissionProcessedDirForDac(serviceProperties, dac).resolve(timestamp);
    ArgonautFileUtils.createDirectories(submissionProcessingDir);
    ArgonautFileUtils.createDirectories(submissionProcessedDir);
    String tarGzFileFileName = ArgonautFileUtils.getRequiredFileName(submittedTarGzFile);
    Path tarGzFile = submissionProcessingDir.resolve(tarGzFileFileName);

    LOGGER.info("Moving {} to {}", submittedTarGzFile, tarGzFile);
    ArgonautFileUtils.move(submittedTarGzFile, tarGzFile);

    Path tempDir = Files.createTempDirectory(workingDir, "processing");

    try {
      ArgonautFileUtils.unTar(tarGzFile, tempDir);
      try (Stream<Path> files = Files.list(tempDir)) {
        files.filter(Files::isRegularFile).forEach(file -> {
          String fileName = file.getFileName().toString();
          ArgonautFileUtils.ncSubmissionMessageFromFileName(fileName).ifPresent(ncSubmissionMessageFromFile -> {
            NcSubmissionMessage ncSubmissionMessage = NcSubmissionMessage.builder(ncSubmissionMessageFromFile)
                .withDac(dac)
                .withTimestamp(timestamp)
                .build();
            Path processingDacDir = processingDir.resolve("dac").resolve(dac).resolve(ncSubmissionMessage.getFloatId());
            if (ncSubmissionMessage.isProfile()) {
              processingDacDir = processingDacDir.resolve("profiles");
            }
            ArgonautFileUtils.createDirectories(processingDacDir);
            Path ncFile = processingDacDir.resolve(file.getFileName());
            LOGGER.info("Adding to processing directory {}", ncFile);
            ArgonautFileUtils.move(file, ncFile);
            output.add(ncSubmissionMessage);
          });
        });
      }
    } finally {
      FileUtils.deleteQuietly(tempDir.toFile());
      ArgonautFileUtils.move(tarGzFile, submissionProcessedDir.resolve(tarGzFileFileName));
    }

    return output.stream().map(NcSubmissionMessage::builder).map(builder -> builder.withNumberOfFilesInSubmission(output.size()).build()).toList();

  }
}
