package edu.colorado.cires.argonaut.messaging.cameljpa;

import jakarta.persistence.EntityManagerFactory;
import java.lang.StackWalker.StackFrame;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class JpaConsumerEntityManagerFactoryInvocationHandler implements InvocationHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(JpaConsumerEntityManagerFactoryInvocationHandler.class);

  private final EntityManagerFactory entityManagerFactory;

  public JpaConsumerEntityManagerFactoryInvocationHandler(EntityManagerFactory entityManagerFactory) {
    this.entityManagerFactory = entityManagerFactory;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    if (method.getName().equals("createEntityManager")) {
      Optional<String> callerName = StackWalker.getInstance().walk(frames -> frames.skip(1).findFirst().map(StackFrame::getMethodName));
      if (callerName.isPresent() && callerName.get().equals("doInit")) {
        LOGGER.debug("Skipping creation of JpaConsumer entityManager instance in {} method", callerName);
        return null;
      }
    }
    return method.invoke(entityManagerFactory, args);
  }
}
