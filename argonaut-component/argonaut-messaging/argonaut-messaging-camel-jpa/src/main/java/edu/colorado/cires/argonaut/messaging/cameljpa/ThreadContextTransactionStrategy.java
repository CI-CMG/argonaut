package edu.colorado.cires.argonaut.messaging.cameljpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.Objects;
import org.apache.camel.component.jpa.TransactionStrategy;

class ThreadContextTransactionStrategy implements TransactionStrategy {

  @Override
  public void executeInTransaction(Runnable runnable) {
    EntityManager entityManager = Objects.requireNonNull(EntityManagerThreadContext.getEntityManager(), "EntityManager was not set. EntityManagerThreadContextProcessor must execute in route.");
    EntityTransaction tx = entityManager.getTransaction();
    if (tx.isActive()) {
      runnable.run();
    } else {
      tx.begin();
      try {
        runnable.run();
        tx.commit();
      } catch (Exception e) {
        tx.rollback();
        throw e;
      }
    }
  }
}
