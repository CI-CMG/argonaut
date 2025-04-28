package edu.colorado.cires.argonaut;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component
public class PostValidationProcessor implements Processor {

  @Override
  public void process(Exchange exchange) throws Exception {
    ValidationMessage validationMessage = exchange.getIn().getBody(ValidationMessage.class);
//    FileCheckResults checkResults;
//    try (Reader reader = Files.newBufferedReader(validationMessage.getFileCheckXmlFile(), StandardCharsets.UTF_8)) {
//      checkResults = (FileCheckResults) JAXBContext.newInstance(FileCheckResults.class).createUnmarshaller().unmarshal(reader);
//    } catch (JAXBException | IOException e) {
//      throw new IllegalStateException("Unable to parse " + validationMessage.getFileCheckXmlFile(), e);
//    }
//    System.out.println(checkResults.getStatus());
  }
}
