package edu.colorado.cires.argonaut.messaging.cameljpa;

import edu.colorado.cires.argonaut.messaging.camel.ArgonautCamelMessageTranslator;
import edu.colorado.cires.argonaut.messaging.core.queue.MessageSender;
import org.apache.camel.CamelContext;
import org.apache.camel.CamelContextAware;
import org.apache.camel.ProducerTemplate;

public class CamelJpaMessageSender implements MessageSender, CamelContextAware {

  private CamelContext camelContext;
  private String producerTemplateId;
  private ArgonautCamelMessageTranslator messageTranslator = new DefaultCamelJpaMessageTranslator();
  private String persistenceUnit = "argonaut-messaging";
  private String entityManagerFactoryId;
  private String transactionStrategyId;

  @Override
  public void sendJson(String queue, String json) {
    ProducerTemplate producerTemplate = camelContext.getRegistry().lookupByNameAndType(producerTemplateId, ProducerTemplate.class);
    String url = String.format("jpa:%s?entityManagerFactory=#%s&transactionStrategy=#%s&persistenceUnit=%s",
        JsonMessageEntity.class.getName(),
        entityManagerFactoryId,
        transactionStrategyId,
        persistenceUnit);
    producerTemplate.sendBody(url, messageTranslator.translate(queue, json));
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

  public void setMessageTranslator(ArgonautCamelMessageTranslator messageTranslator) {
    this.messageTranslator = messageTranslator;
  }

  public void setEntityManagerFactoryId(String entityManagerFactoryId) {
    this.entityManagerFactoryId = entityManagerFactoryId;
  }

  public void setTransactionStrategyId(String transactionStrategyId) {
    this.transactionStrategyId = transactionStrategyId;
  }

  public void setPersistenceUnit(String persistenceUnit) {
    this.persistenceUnit = persistenceUnit;
  }
}
