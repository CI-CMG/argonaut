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
    NcSubmissionMessage ncSubmissionMessage = new NcSubmissionMessage();
    ncSubmissionMessage.setOperation(Operation.REMOVE);
    ncSubmissionMessage.setDac(message.getDac());
    ncSubmissionMessage.setTimestamp(message.getTimestamp());
    ncSubmissionMessage.setProfile(false);
    ncSubmissionMessage.setFileName(message.getFileName());
    ncSubmissionMessage.setNumberOfFilesInSubmission(1);
    ncSubmissionMessage.setValidationError(message.getValidationError());
    exchange.getIn().setBody(ncSubmissionMessage);
  }
}
