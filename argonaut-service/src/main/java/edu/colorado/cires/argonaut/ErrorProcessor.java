package edu.colorado.cires.argonaut;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component
public class ErrorProcessor implements Processor {

  @Override
  public void process(Exchange exchange) throws Exception {
    ErrorMessage errorMessage = exchange.getIn().getBody(ErrorMessage.class);
  }
}
