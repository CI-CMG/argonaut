package edu.colorado.cires.argonaut.processor;

import edu.colorado.cires.argonaut.message.NcSubmissionMessage;
import edu.colorado.cires.argonaut.message.NcSubmissionMessage.Operation;
import edu.colorado.cires.argonaut.message.RemovalMessage;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;


@Component
public class RemovalMessageTranslator implements Processor {

  @Override
  public void process(Exchange exchange) throws Exception {
    RemovalMessage message = exchange.getIn().getBody(RemovalMessage.class);
    NcSubmissionMessage ncSubmissionMessage = NcSubmissionMessage.builder()
        .withOperation(Operation.REMOVE)
        .withDac(message.getDac())
        .withTimestamp(message.getTimestamp())
        .withProfile(false)
        .withFileName(message.getFileName())
        .withNumberOfFilesInSubmission(1)
        .withValidationErrors(message.getValidationErrors())
        .build();

    exchange.getIn().setBody(ncSubmissionMessage);
  }
}
