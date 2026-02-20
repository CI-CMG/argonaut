package edu.colorado.cires.argonaut.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SerializeMessage implements Processor {

  private final ObjectMapper objectMapper;

  @Autowired
  public SerializeMessage(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public void process(Exchange exchange) throws Exception {
    exchange.getIn().setBody(objectMapper.writeValueAsString(exchange.getIn().getBody()));
  }
}
