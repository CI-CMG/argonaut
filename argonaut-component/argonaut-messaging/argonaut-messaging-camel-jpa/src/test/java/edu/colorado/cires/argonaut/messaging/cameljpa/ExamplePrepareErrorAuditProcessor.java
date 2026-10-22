package edu.colorado.cires.argonaut.messaging.cameljpa;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExamplePrepareErrorAuditProcessor implements Processor {

  private static final Logger LOGGER = LoggerFactory.getLogger(ExamplePrepareErrorAuditProcessor.class);

  private CamelJpaMessageTranslator messageTranslator;

  public CamelJpaMessageTranslator getMessageTranslator() {
    return messageTranslator;
  }

  public void setMessageTranslator(CamelJpaMessageTranslator messageTranslator) {
    this.messageTranslator = messageTranslator;
  }

  @Override
  public void process(Exchange exchange) throws Exception {
    Exception cause = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
    LOGGER.error("Error caught: ", cause);
    Object sourceMessage = exchange.getIn().getBody();
    if (sourceMessage instanceof ArgonautMessageEntity) {
      sourceMessage = ((ArgonautMessageEntity) sourceMessage).getJsonMessage();
    }

    String message = sourceMessage + " : " + ExceptionUtils.getStackTrace(cause);

    exchange.getIn().setBody(messageTranslator.send("audit", message));
  }
}
