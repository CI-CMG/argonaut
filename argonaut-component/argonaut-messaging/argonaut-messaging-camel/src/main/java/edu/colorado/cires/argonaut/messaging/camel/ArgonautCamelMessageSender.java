package edu.colorado.cires.argonaut.messaging.camel;

import edu.colorado.cires.argonaut.messaging.core.queue.MessageSender;
import org.apache.camel.CamelContext;
import org.apache.camel.CamelContextAware;
import org.apache.camel.ProducerTemplate;

public class ArgonautCamelMessageSender implements MessageSender, CamelContextAware {

  private CamelContext camelContext;
  private String producerTemplateId;

  @Override
  public void sendJson(String queue, String json) {
    ProducerTemplate producerTemplate = camelContext.getRegistry().lookupByNameAndType(producerTemplateId, ProducerTemplate.class);
    producerTemplate.sendBody(queue, json);
  }

  @Override
  public void setCamelContext(CamelContext camelContext) {
    this.camelContext = camelContext;
  }

  @Override
  public CamelContext getCamelContext() {
    return camelContext;
  }

  public void setProducerTemplateId(String producerTemplateId) {
    this.producerTemplateId = producerTemplateId;
  }
}
