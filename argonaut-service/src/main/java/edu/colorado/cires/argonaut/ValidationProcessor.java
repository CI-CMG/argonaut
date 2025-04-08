package edu.colorado.cires.argonaut;

import edu.colorado.cires.argonaut.xml.filecheck.FileCheckResults;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component
public class ValidationProcessor implements Processor {

  @Override
  public void process(Exchange exchange) throws Exception {
    ValidationMessage validationMessage = exchange.getIn().getBody(ValidationMessage.class);
    FileCheckResults checkResults;
    try (Reader reader = Files.newBufferedReader(validationMessage.getFileCheckXmlFile(), StandardCharsets.UTF_8)) {
      checkResults = (FileCheckResults) JAXBContext.newInstance(FileCheckResults.class).createUnmarshaller().unmarshal(reader);
    } catch (JAXBException | IOException e) {
      throw new IllegalStateException("Unable to parse " + validationMessage.getFileCheckXmlFile(), e);
    }
    System.out.println(checkResults.getStatus());
  }
}
