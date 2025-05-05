package edu.colorado.cires.argonaut.processor;

import edu.colorado.cires.argonaut.message.NcSubmissionMessage;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component
public class FloatMergeProcessor implements Processor {

  @Override
  public void process(Exchange exchange) throws Exception {
    NcSubmissionMessage message = exchange.getIn().getBody(NcSubmissionMessage.class);
  }
}
