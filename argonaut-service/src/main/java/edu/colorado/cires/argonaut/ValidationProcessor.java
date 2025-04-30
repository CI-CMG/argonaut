package edu.colorado.cires.argonaut;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.colorado.cires.argonaut.xml.filecheck.FileCheckResults;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ValidationProcessor implements Processor {
  private final Path workingDir;
  private final Path processingDir;
  private final Path validateDir;
  private final Path errorDir;
  private final ProducerTemplate producerTemplate;
  private final ObjectMapper objectMapper;

  @Autowired
  public ValidationProcessor(ServiceProperties serviceProperties, ProducerTemplate producerTemplate, ObjectMapper objectMapper) {
    this.producerTemplate = producerTemplate;
    this.objectMapper = objectMapper;
    workingDir = Paths.get(serviceProperties.getWorkDirectory());
    processingDir = workingDir.resolve("processing");
    validateDir = workingDir.resolve("validated");
    try {
      Files.createDirectories(validateDir);
    } catch (IOException e) {
      throw new RuntimeException("Unable to create validated directory", e);
    }
    errorDir = workingDir.resolve("error");


  }




  @Override
  public void process(Exchange exchange) throws Exception {
//    ValidationMessage validationMessage = exchange.getIn().getBody(ValidationMessage.class);
    String body = exchange.getIn().getBody(String.class);
    ValidationMessage validationMessage = objectMapper.readValue(body,ValidationMessage.class);
    Path ncFile = validationMessage.getNcFile();
    Path fileCheckXmlFile = validationMessage.getFileCheckXmlFile();
    FileCheckResults checkResults;
    try (
        Reader reader = Files.newBufferedReader(fileCheckXmlFile, StandardCharsets.UTF_8)) {
      checkResults = (FileCheckResults) JAXBContext.newInstance(FileCheckResults.class).createUnmarshaller().unmarshal(reader);
    } catch (JAXBException | IOException e) {
      throw new IllegalStateException("Unable to parse " + fileCheckXmlFile, e);
    }
    Path dac = ncFile.getParent().relativize(processingDir);
    if (checkResults.getStatus().equals("FILE-ACCEPTED")){
      Path validateDacDir = workingDir.resolve(dac);
      Path fileCheckFileValidate = validateDacDir.resolve(fileCheckXmlFile.getFileName());
      Path ncFileValidate = validateDir.resolve(ncFile.getFileName());
      try {
        Files.createDirectories(validateDacDir);
      } catch (IOException e) {
        throw new RuntimeException("Unable to create "+ dac + " directory", e);
      }
      try{
        Files.move(ncFile, ncFileValidate);
      } catch (IOException e) {
        throw new RuntimeException("Unable to move " + ncFile + " to " + ncFileValidate, e);
      }
      try{
        Files.move(fileCheckXmlFile,fileCheckFileValidate);
      } catch (IOException e) {
        throw new RuntimeException("Unable to move " + fileCheckXmlFile + " to " + fileCheckFileValidate, e);
      }
      producerTemplate.sendBody("seda:postvalidate", new PostValidationMessage(ncFileValidate,fileCheckFileValidate));
    } else {
      Path errorDacDir = errorDir.resolve(dac);
      Path erFileCheckFile = errorDacDir.resolve(fileCheckXmlFile.getFileName());
      Path erNcFile = errorDacDir.resolve(ncFile.getFileName());
      try {
        Files.createDirectories(errorDir);
      } catch (IOException e) {
        throw new RuntimeException("Unable to create error " + dac + " directory", e);
      }
      try{
        Files.move(ncFile, erNcFile);
      } catch (IOException e) {
        throw new RuntimeException("Unable to move " + ncFile + " to " + erNcFile, e);
      }
      try{
        Files.move(fileCheckXmlFile, erFileCheckFile);
      } catch (IOException e) {
        throw new RuntimeException("Unable to move " + fileCheckXmlFile + " to " + erFileCheckFile, e);
      }
      producerTemplate.sendBody("seda:error", new ErrorMessage(ncFile.getFileName().toString(),checkResults.getStatus()));
    }
  }
}
