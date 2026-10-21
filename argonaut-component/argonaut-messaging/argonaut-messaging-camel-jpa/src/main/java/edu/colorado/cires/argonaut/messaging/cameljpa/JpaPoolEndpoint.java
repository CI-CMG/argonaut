package edu.colorado.cires.argonaut.messaging.cameljpa;

import jakarta.persistence.EntityManagerFactory;
import java.lang.reflect.Proxy;
import org.apache.camel.Category;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.component.jpa.JpaComponent;
import org.apache.camel.component.jpa.JpaConstants;
import org.apache.camel.component.jpa.JpaEndpoint;
import org.apache.camel.component.jpa.TransactionStrategy;
import org.apache.camel.spi.UriEndpoint;

@UriEndpoint(firstVersion = "1.0.0", scheme = "jpaPool", title = "JPAPool", syntax = "jpaPool:entityType",
    category = { Category.DATABASE }, headersClass = JpaConstants.class)
public class JpaPoolEndpoint extends JpaEndpoint {

  private final TransactionStrategy transactionStrategy = new ThreadContextTransactionStrategy();

  public JpaPoolEndpoint() {
    super();
  }

  public JpaPoolEndpoint(String uri, JpaComponent component) {
    super(uri, component);
  }

  //Override in order to create a JpaPoolConsumer
  @Override
  public Consumer createConsumer(Processor processor) throws Exception {
    validate();
    JpaPoolConsumer consumer = new JpaPoolConsumer(this, processor);
    consumer.setMaxMessagesPerPoll(getMaxMessagesPerPoll());
    consumer.setQuery(getQuery());
    consumer.setNamedQuery(getNamedQuery());
    consumer.setNativeQuery(getNativeQuery());
    consumer.setLockModeType(getLockModeType());
    consumer.setParameters(getParameters());
    consumer.setResultClass(getResultClass());
    consumer.setTransacted(isTransacted());
    consumer.setSkipLockedEntity(isSkipLockedEntity());
    consumer.setDeleteHandler(getDeleteHandler());
    consumer.setPreDeleteHandler(getPreDeleteHandler());
    configureConsumer(consumer);
    return consumer;
  }

  //Override in order to use jpaPool prefix
  @Override
  protected String createEndpointUri() {
    return "jpaPool" + (getEntityType() != null ? "://" + getEntityType().getName() : "");
  }

  //Override in order to use a proxy that will not create a class level EntityManager
  @Override
  public EntityManagerFactory getEntityManagerFactory() {
    return (EntityManagerFactory) Proxy.newProxyInstance(
        JpaPoolEndpoint.class.getClassLoader(),
        new Class[] { EntityManagerFactory.class },
        new JpaConsumerEntityManagerFactoryInvocationHandler(super.getEntityManagerFactory()));
  }


  //Override in order to force use of the ThreadContextTransactionStrategy
  @Override
  public TransactionStrategy getTransactionStrategy() {
    return transactionStrategy;
  }

}
