package edu.colorado.cires.argonaut.messaging.cameljpa;

import edu.colorado.cires.argonaut.messaging.camel.ArgonautCamelMessageTranslator;
import edu.colorado.cires.argonaut.messaging.core.queue.MessageSender;
import jakarta.persistence.EntityManager;
import org.apache.camel.CamelContext;
import org.apache.camel.CamelContextAware;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.jpa.JpaConstants;

public class CamelJpaMessageSender implements MessageSender, CamelContextAware {

  private static final String JPA_POOL_URL = String.format("jpaPool:%s?usePassedInEntityManager=true", ArgonautMessageEntity.class.getName());
  private static final String JPA_URL = String.format("jpa:%s?usePassedInEntityManager=true", ArgonautMessageEntity.class.getName());


  private CamelContext camelContext;
  private String producerTemplateId;
  private ArgonautCamelMessageTranslator messageTranslator = new DefaultCamelJpaMessageTranslator();
  private boolean useJpaPoolEndpoint = true;

  @Override
  public void sendJson(String queue, String json) {
    EntityManager entityManager = EntityManagerThreadContext.getEntityManager();
    ProducerTemplate producerTemplate = camelContext.getRegistry().lookupByNameAndType(producerTemplateId, ProducerTemplate.class);
    String url = JPA_POOL_URL;
    if (!useJpaPoolEndpoint) {
      url = JPA_URL;
    }
    producerTemplate.sendBodyAndHeader(url, messageTranslator.translate(queue, json), JpaConstants.ENTITY_MANAGER, entityManager);
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
}
