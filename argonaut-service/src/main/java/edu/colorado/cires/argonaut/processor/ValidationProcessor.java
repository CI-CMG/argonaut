package edu.colorado.cires.argonaut.processor;

import edu.colorado.cires.argonaut.ServiceProperties;
import edu.colorado.cires.argonaut.message.NcSubmissionMessage;
import edu.colorado.cires.argonaut.util.ArgonautFileUtils;
import edu.colorado.cires.argonaut.xml.filecheck.FileCheckResults;
import edu.colorado.cires.cmg.shellexecutor.DefaultShellExecutor;
import edu.colorado.cires.cmg.shellexecutor.ShellExecutor;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Arrays;
import java.util.function.Consumer;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ValidationProcessor implements Processor {

  private static final Logger LOGGER = LoggerFactory.getLogger(ValidationProcessor.class);

  //TODO clean up
//  private final Path workingDir;
//  private final Path processingDir;
//  private final Path validateDir;
//  private final Path errorDir;
//  private final ProducerTemplate producerTemplate;

  private final ShellExecutor shellExecutor = new DefaultShellExecutor();
  private final Path java;
  private final Path specDir;
  private final Path workingDir;
  private final Path exeJar;
  private final Duration timeout;
  private final ServiceProperties serviceProperties;

  @Autowired
  public ValidationProcessor(ServiceProperties serviceProperties) {
    this.serviceProperties = serviceProperties;
//    this.producerTemplate = producerTemplate;
//    workingDir = serviceProperties.getWorkDirectory();
//    processingDir = workingDir.resolve("processing");
//    validateDir = workingDir.resolve("validated");
//    try {
//      Files.createDirectories(validateDir);
//    } catch (IOException e) {
//      throw new RuntimeException("Unable to create validated directory", e);
//    }
//    errorDir = workingDir.resolve("error");

    java = Paths.get(System.getProperty("java.home")).resolve("bin").resolve("java");
    workingDir = serviceProperties.getWorkDirectory();
    ArgonautFileUtils.createDirectories(workingDir);
    specDir = workingDir.resolve("file_checker_spec");
    ClassLoader classLoader = getClass().getClassLoader();
    URL execResource = classLoader.getResource("file_checker_exec.jar");
    URL specResource = classLoader.getResource("file_checker_spec.zip");

    if (execResource == null) {
      throw new IllegalArgumentException("file_checker_exec.jar not found");
    }
    if (specResource == null) {
      throw new IllegalArgumentException("file_checker_spec.zip not found");
    }

    exeJar = workingDir.resolve("file_checker_exec.jar");
    ArgonautFileUtils.copy(execResource, exeJar);

    timeout = serviceProperties.getFileCheckerTimeout();
    Path destPath = workingDir.resolve("file_checker_spec");
    ArgonautFileUtils.unzip(specResource, destPath);
  }

  private static String validateXml(Path fileCheckXmlFile) {
    FileCheckResults checkResults;
    try (
        Reader reader = Files.newBufferedReader(fileCheckXmlFile, StandardCharsets.UTF_8)) {
      checkResults = (FileCheckResults) JAXBContext.newInstance(FileCheckResults.class).createUnmarshaller().unmarshal(reader);
    } catch (JAXBException | IOException e) {
      throw new IllegalStateException("Unable to parse " + fileCheckXmlFile, e);
    }
    if (checkResults.getStatus().equals("FILE-ACCEPTED")) {
      return null;
//      Path validateDacDir = workingDir.resolve(dac);
//      Path fileCheckFileValidate = validateDacDir.resolve(fileCheckXmlFile.getFileName());
//      Path ncFileValidate = validateDir.resolve(ncFile.getFileName());
//      try {
//        Files.createDirectories(validateDacDir);
//      } catch (IOException e) {
//        throw new RuntimeException("Unable to create "+ dac + " directory", e);
//      }
//      try{
//        Files.move(ncFile, ncFileValidate);
//      } catch (IOException e) {
//        throw new RuntimeException("Unable to move " + ncFile + " to " + ncFileValidate, e);
//      }
//      try{
//        Files.move(fileCheckXmlFile,fileCheckFileValidate);
//      } catch (IOException e) {
//        throw new RuntimeException("Unable to move " + fileCheckXmlFile + " to " + fileCheckFileValidate, e);
//      }
//      producerTemplate.sendBody("seda:postvalidate", new PostValidationMessage(ncFileValidate,fileCheckFileValidate));
    } else {
      return checkResults.getStatus();
//      Path errorDacDir = errorDir.resolve(dac);
//      Path erFileCheckFile = errorDacDir.resolve(fileCheckXmlFile.getFileName());
//      Path erNcFile = errorDacDir.resolve(ncFile.getFileName());
//      try {
//        Files.createDirectories(errorDir);
//      } catch (IOException e) {
//        throw new RuntimeException("Unable to create error " + dac + " directory", e);
//      }
//      try{
//        Files.move(ncFile, erNcFile);
//      } catch (IOException e) {
//        throw new RuntimeException("Unable to move " + ncFile + " to " + erNcFile, e);
//      }
//      try{
//        Files.move(fileCheckXmlFile, erFileCheckFile);
//      } catch (IOException e) {
//        throw new RuntimeException("Unable to move " + fileCheckXmlFile + " to " + erFileCheckFile, e);
//      }
//      producerTemplate.sendBody("seda:error", new ErrorMessage(ncFile.getFileName().toString(),checkResults.getStatus()));
    }
  }


  private void checkFile(String dac, Path dir, String fileName) {
    try {

      Consumer<String> logger = s -> LOGGER.info("File Checker: {}", s);

      String[] args = {
          java.toAbsolutePath().normalize().toString(),
          "-Xmx" + serviceProperties.getFileCheckerHeap(),
          "-jar",
          exeJar.toAbsolutePath().normalize().toString(),
          dac,
          specDir.toAbsolutePath().normalize().toString(),
          dir.toAbsolutePath().normalize().toString(),
          dir.toAbsolutePath().normalize().toString(),
          fileName
      };

      LOGGER.info("File Checker Running {}", Arrays.toString(args));

      int exitCode = shellExecutor.execute(dir, logger, timeout.toMillis(), args);
      if (exitCode != 0) {
        throw new RuntimeException("Error executing file checker: " + exitCode + " " + dir + "/" + fileName);
      }

    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException("Executing file checker was interrupted", e);
    } catch (IOException e) {
      throw new RuntimeException("An error occurred running file checker: " + dir + "/" + fileName, e);
    }
  }

  @Override
  public void process(Exchange exchange) throws Exception {
    NcSubmissionMessage ncSubmissionMessage = exchange.getIn().getBody(NcSubmissionMessage.class);
    String dac = ncSubmissionMessage.getDac();
    String floatId = ncSubmissionMessage.getFloatId();
    String fileName = ncSubmissionMessage.getFileName();
    boolean isProfile = ncSubmissionMessage.isProfile();
    Path processingDacDir = ArgonautFileUtils.getProcessingProfileDir(serviceProperties, dac, floatId, isProfile);
    checkFile(dac, processingDacDir, fileName);
    Path fileCheckXmlFile = processingDacDir.resolve(fileName + ".filecheck");
    String error = validateXml(fileCheckXmlFile);
    if (error != null) {
      ncSubmissionMessage.setValidationError(error);
    }
    FileUtils.deleteQuietly(fileCheckXmlFile.toFile());
  }
}
