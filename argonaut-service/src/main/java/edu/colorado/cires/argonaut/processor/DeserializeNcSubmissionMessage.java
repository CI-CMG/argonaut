package edu.colorado.cires.argonaut.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.colorado.cires.argonaut.message.NcSubmissionMessage;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DeserializeNcSubmissionMessage implements Processor {

  private final ObjectMapper objectMapper;

  @Autowired
  public DeserializeNcSubmissionMessage(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public void process(Exchange exchange) throws Exception {
    exchange.getIn().setBody(objectMapper.readValue(exchange.getIn().getBody(String.class), NcSubmissionMessage.class));
  }
}
