package edu.colorado.cires.argonaut.processor;

import edu.colorado.cires.argonaut.ServiceProperties;
import edu.colorado.cires.argonaut.message.HeaderConsts;
import edu.colorado.cires.argonaut.message.NcSubmissionMessage;
import edu.colorado.cires.argonaut.util.ArgonautFileUtils;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SubmissionProcessor implements Processor {

  private static final Logger LOGGER = LoggerFactory.getLogger(SubmissionProcessor.class);
  private static final Pattern FILE_NAME_PATTERN = Pattern.compile("([A-Z]+)?([0-9]+)_(.+)\\.nc(\\.filecheck)?");

  private final Path workingDir;
  private final Path processingDir;
  private final ServiceProperties serviceProperties;

  @Autowired
  public SubmissionProcessor(ServiceProperties serviceProperties) {
    this.serviceProperties = serviceProperties;
    workingDir = serviceProperties.getWorkDirectory();
    processingDir = ArgonautFileUtils.getProcessingDir(serviceProperties);
    ArgonautFileUtils.createDirectories(processingDir);
  }

  @Override
  public void process(Exchange exchange) throws Exception {
    List<NcSubmissionMessage> output = new ArrayList<>();

    String dac = exchange.getIn().getHeader(HeaderConsts.DAC, String.class);
    String timestamp = exchange.getIn().getHeader(HeaderConsts.SUBMISSION_TIMESTAMP, String.class);
    Path submittedTarGzFile = exchange.getIn().getBody(File.class).toPath();
    Path submissionProcessingDir = ArgonautFileUtils.getSubmissionProcessingDirForDac(serviceProperties, dac).resolve(timestamp);
    Path submissionProcessedDir = ArgonautFileUtils.getSubmissionProcessedDirForDac(serviceProperties, dac).resolve(timestamp);
    ArgonautFileUtils.createDirectories(submissionProcessingDir);
    ArgonautFileUtils.createDirectories(submissionProcessedDir);
    String tarGzFileFileName = submittedTarGzFile.getFileName().toString();
    Path tarGzFile = submissionProcessingDir.resolve(tarGzFileFileName);

    LOGGER.info("Moving {} to {}", submittedTarGzFile, tarGzFile);
    ArgonautFileUtils.move(submittedTarGzFile, tarGzFile);

    Path tempDir = Files.createTempDirectory(workingDir, "processing");

    try {
      ArgonautFileUtils.unTar(tarGzFile, tempDir);
      try (Stream<Path> files = Files.list(tempDir)) {
        files.filter(Files::isRegularFile).forEach(file -> {
          String fileName = file.getFileName().toString();
          Matcher matcher = FILE_NAME_PATTERN.matcher(fileName);
          if (matcher.matches()) {
            String floatDir = matcher.group(2);
            Path processingDacDir = processingDir.resolve("dac").resolve(dac).resolve(floatDir);
            boolean profile = matcher.group(1) != null;
            if (profile) {
              processingDacDir = processingDacDir.resolve("profiles");
            }
            ArgonautFileUtils.createDirectories(processingDacDir);
            Path ncFile = processingDacDir.resolve(file.getFileName());
            LOGGER.info("Adding to processing directory {}", ncFile);
            ArgonautFileUtils.move(file, ncFile);

            NcSubmissionMessage ncSubmissionMessage = new NcSubmissionMessage();
            ncSubmissionMessage.setDac(dac);
            ncSubmissionMessage.setFileName(fileName);
            ncSubmissionMessage.setProfile(profile);
            ncSubmissionMessage.setTimestamp(timestamp);
            ncSubmissionMessage.setFloatId(floatDir);

            output.add(ncSubmissionMessage);
          }

        });
      }
    } finally {
      FileUtils.deleteQuietly(tempDir.toFile());
      ArgonautFileUtils.move(tarGzFile, submissionProcessedDir.resolve(tarGzFileFileName));
    }

    exchange.getIn().setBody(output);

  }

  // TODO cleanup
//  @Override
//  public void process(Exchange exchange) throws Exception {
//    List<NcSubmissionMessage> output = new ArrayList<>();
//
//
//    String dac = exchange.getIn().getHeader(HeaderConsts.DAC, String.class);
//    String timestamp = exchange.getIn().getHeader(HeaderConsts.SUBMISSION_TIMESTAMP, String.class);
//    Path submittedTarGzFile = exchange.getIn().getBody(File.class).toPath();
//    Path dacDir = submissionDir.resolve("dac").resolve(dac);
//    Path submissionProcessingDir = dacDir.resolve("processing").resolve(timestamp);
//    Path submissionProcessedDir = dacDir.resolve("processed").resolve(timestamp);
//    ArgonautFileUtils.createDirectories(submissionProcessingDir);
//    String tarGzFileFileName = submittedTarGzFile.getFileName().toString();
//    Path tarGzFile = submissionProcessingDir.resolve(tarGzFileFileName);
//
//    ArgonautFileUtils.move(submittedTarGzFile, tarGzFile);
////    FileResolver resolver = resolveTarGz(readyFile);
//
//    Path tempDir = Files.createTempDirectory(workingDir, "processing");
//    Map<String, FileCheckPair> fileCheckPairMap = new HashMap<>();
//    try {
//      unTar(tarGzFile, tempDir);
//      try {
//
//        Consumer<String> logger = System.out::println;
//
//        String[] args = {
//            java.toAbsolutePath().normalize().toString(),
//            "-jar",
//            exeJar.toAbsolutePath().normalize().toString(),
//            dac,
//            specDir.toAbsolutePath().normalize().toString(),
//            tempDir.toAbsolutePath().normalize().toString(),
//            tempDir.toAbsolutePath().normalize().toString()
//        };
//
//        LOGGER.info("Running " + Arrays.toString(args));
//
//        int exitCode = shellExecutor.execute(tempDir, logger, timeout.toMillis(), args);
//        if (exitCode != 0) {
//          throw new RuntimeException("Error executing file checker: " + exitCode);
//        }
//
//      } catch (InterruptedException e) {
//        Thread.currentThread().interrupt();
//        throw new RuntimeException(e);
//      }
//      try (Stream<Path> files = Files.list(tempDir)) {
//        files.filter(Files::isRegularFile)
//            .forEach(file -> {
//              String fileName = file.getFileName().toString();
//              Matcher matcher = FILE_NAME_PATTERN.matcher(fileName);
//              if(matcher.matches()) {
//                String floatDir = matcher.group(2);
//
//                String key = (matcher.group(1) == null ? "" : matcher.group(1)) + matcher.group(2) + "_" + matcher.group(3);
//
//                FileCheckPair fileCheckPair = fileCheckPairMap.get(key);
//                if (fileCheckPair == null){
//                  fileCheckPair = new FileCheckPair();
//                }
//                if (file.getFileName().toString().contains(".filecheck")){
//                  fileCheckPair.setFileCheckFile(file);
//                } else {
//                  fileCheckPair.setNcFile(file);
//                }
//
//                if (fileCheckPair.isComplete()){
//
//
//
//                  Path processingDacDir = processingDir.resolve("dac").resolve(dac).resolve(floatDir);
//
//                  // TODO is this assumption correct? Do we need to open the NetCDF file and check?
//                  if (!Character.isDigit(key.charAt(0))){
//                    processingDacDir = processingDir.resolve("profile");
//                  }
//                  ArgonautFileUtils.createDirectories(processingDacDir);
//                  Path fileCheckFile = processingDacDir.resolve(fileCheckPair.getFileCheckFile().getFileName());
//                  Path ncFile = processingDacDir.resolve(fileCheckPair.getNcFile().getFileName());
//
//                  //TODO add logging
//                  ArgonautFileUtils.move(fileCheckPair.getNcFile(), ncFile);
//                  ArgonautFileUtils.move(fileCheckPair.getFileCheckFile(), fileCheckFile);
//
//                  producerTemplate.sendBody("seda:validation", new ValidationMessage(ncFile, fileCheckFile));
//                  fileCheckPairMap.remove(key);
//                } else {
//                  fileCheckPairMap.put(key, fileCheckPair);
//                }
//
//              } else {
//                //TODO handle this properly
//                throw new IllegalArgumentException("Invalid file name: " + fileName);
//              }
//
//          });
//      }
//    } finally {
//      FileUtils.deleteQuietly(tempDir.toFile());
//      ArgonautFileUtils.createDirectories(submissionProcessedDir);
//      ArgonautFileUtils.move(tarGzFile, submissionProcessedDir.resolve(tarGzFileFileName));
//    }
//    //incomplete pairs
//    //TODO uncomment this after happy path is tested

  /// /    fileCheckPairMap.forEach((key, fileCheckPair) ->{ /      Path errorDacDir = errorDir.resolve(resolver.dac); /      Path tempFile =
  /// fileCheckPair.getExistingFile(); /      Path erFile = errorDacDir.resolve(fileCheckPair.getFileCheckFile().getFileName()); /      try { /
  /// Files.createDirectories(errorDir); /      } catch (IOException e) { /        throw new RuntimeException("Unable to create error "+ resolver.dac
  /// + " directory", e); /      } /      try{ /        Files.move(tempFile, erFile); /      } catch (IOException e) { /        throw new
  /// RuntimeException("Unable to move " + fileCheckPair.getNcFile() + " to " + erFile, e); /      } /      producerTemplate.sendBody("seda:error",
  /// new ErrorMessage(key,"Error executing AOML Processor")); /    });
//
//
//    exchange.getIn().setBody(output);
//
//  }

//  private FileResolver resolveTarGz(File readyFile) {
//    Path readyPath = readyFile.toPath();
//    Path parent = readyPath.getParent();
//    if (readyPath.getParent() == null || readyPath.getParent().getParent() == null) {
//      throw new RuntimeException("Tar gz file parent is null");
//    }
//    parent = parent.toAbsolutePath().normalize();
//    String dac = parent.getParent().toAbsolutePath().normalize().getFileName().toString();
//    String readyFileName = readyFile.getName();
//    String fileName = readyFileName.replaceAll("\\.ready$", "");
//    return new FileResolver(parent.resolve(fileName), dac);
//  }

//  private static class FileResolver {
//
//    private final Path tarGzFile;
//    private final String dac;
//
//    private FileResolver(Path tarGzFile, String dac) {
//      this.tarGzFile = tarGzFile;
//      this.dac = dac;
//    }
//
//    public Path getTarGzFile() {
//      return tarGzFile;
//    }
//
//    public String getDac() {
//      return dac;
//    }
//  }
}
