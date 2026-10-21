package edu.colorado.cires.argonaut.messaging.cameljpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.jpa.JpaCloseEntityManagerOnCompletion;
import org.apache.camel.component.jpa.JpaConstants;
import org.apache.camel.support.SynchronizationAdapter;

public class EntityManagerContextProcessor implements Processor {

  private EntityManagerFactory entityManagerFactory;

  public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
    this.entityManagerFactory = entityManagerFactory;
  }

  @Override
  public void process(Exchange exchange) throws Exception {
    EntityManager entityManager = EntityManagerThreadContext.getEntityManager();
    if (entityManager == null) {
      entityManager = exchange.getIn().getHeader(JpaConstants.ENTITY_MANAGER, EntityManager.class);
      if (entityManager == null) {
        entityManager = entityManagerFactory.createEntityManager();
        exchange.getIn().setHeader(JpaConstants.ENTITY_MANAGER, entityManager);
        exchange.getExchangeExtension().addOnCompletion(new JpaCloseEntityManagerOnCompletion(entityManager));
      }
      EntityManagerThreadContext.setEntityManager(entityManager);
      exchange.getExchangeExtension().addOnCompletion(new SynchronizationAdapter() {
        @Override
        public void onDone(Exchange exchange) {
          EntityManagerThreadContext.clear();
        }
      });
    }

  }
}
