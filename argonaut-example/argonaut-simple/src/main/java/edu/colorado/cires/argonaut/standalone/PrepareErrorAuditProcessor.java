package edu.colorado.cires.argonaut.standalone;

import edu.colorado.cires.argonaut.messaging.core.databind.AuditEventProcessor;
import edu.colorado.cires.argonaut.messaging.core.databind.AuditMessage;
import edu.colorado.cires.argonaut.messaging.core.util.ErrorMessageAuditUtils;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.databind.json.JsonMapper;

public class PrepareErrorAuditProcessor implements Processor {

  private static final Logger LOGGER = LoggerFactory.getLogger(PrepareErrorAuditProcessor.class);

  private JsonMapper jsonMapper;

  public void setJsonMapper(JsonMapper jsonMapper) {
    this.jsonMapper = jsonMapper;
  }

  @Override
  public void process(Exchange exchange) throws Exception {
    AuditEventProcessor processor = AuditEventProcessor.valueOf(exchange.getIn().getHeader("AuditEventProcessor", String.class));
    Exception cause = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
    LOGGER.error("Error caught: ", cause);
    Object sourceMessage = exchange.getIn().getBody();
    AuditMessage auditMessage = ErrorMessageAuditUtils.buildErrorMessageAuditMessage(cause, sourceMessage, processor);
    exchange.getIn().setBody(jsonMapper.writeValueAsString(auditMessage));
  }
}
