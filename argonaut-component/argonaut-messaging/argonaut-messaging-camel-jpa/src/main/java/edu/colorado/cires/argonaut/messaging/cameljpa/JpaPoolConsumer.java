package edu.colorado.cires.argonaut.messaging.cameljpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Query;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePropertyKey;
import org.apache.camel.Processor;
import org.apache.camel.component.jpa.JpaConsumer;
import org.apache.camel.component.jpa.JpaEndpoint;
import org.apache.camel.util.CastUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JpaPoolConsumer extends JpaConsumer {

  private static final Logger LOG = LoggerFactory.getLogger(JpaPoolConsumer.class);

  private final EntityManagerFactory entityManagerFactory;

  private static final class DataHolder {

    private Exchange exchange;
    private Object result;
    private EntityManager manager;

    private DataHolder() {
    }
  }

  public JpaPoolConsumer(JpaEndpoint endpoint, Processor processor) {
    super(endpoint, processor);
    this.entityManagerFactory = endpoint.getEntityManagerFactory();
  }

  //Overriding in order to open connections / create new EntityManagers for each poll
  @Override
  protected int poll() throws Exception {
    // must reset for each poll
    shutdownRunningTask = null;
    pendingExchanges = 0;

    final int[] messagePolled = {0};
    try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
      EntityManagerThreadContext.setEntityManager(entityManager);
      try {

        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();
        try {
          if (getEndpoint().isJoinTransaction()) {
            entityManager.joinTransaction();
          }

          Queue<DataHolder> answer = new LinkedList<>();

          Query toExecute = getQueryFactory().createQuery(entityManager);
          configureParameters(toExecute);
          LOG.trace("Created query {}", toExecute);

          List<?> results = toExecute.getResultList();
          LOG.trace("Got result list from query {}", results);

          // okay we have some response from jpa so lets mark the consumer as ready
          forceConsumerAsReady();

          for (Object result : results) {
            DataHolder holder = new DataHolder();
            holder.manager = entityManager;
            holder.result = result;
            holder.exchange = createExchange(result, entityManager);
            answer.add(holder);
          }

          PersistenceException cause = null;
          try {
            messagePolled[0] = processBatch(CastUtils.cast(answer));
          } catch (PersistenceException e) {
            cause = e;
          } catch (Exception e) {
            cause = new PersistenceException(e);
          }

          if (cause != null) {
            if (!isTransacted()) {
              LOG.warn(
                  "Error processing last message due: {}. Will commit all previous successful processed message, and ignore this last failure.",
                  cause.getMessage(), cause);
            } else {
              // rollback all by throwing exception
              throw cause;
            }
          }

          // commit
          LOG.debug("Flushing EntityManager");
          entityManager.flush();
          // must clear after flush
          entityManager.clear();
          tx.commit();
        } catch (Exception e) {
          tx.rollback();
          throw e;
        }
      } finally {
        EntityManagerThreadContext.clear();
      }
    } catch (Exception e) {
      throw new PersistenceException(e);
    }

    return getEndpoint().getCamelContext().getTypeConverter().convertTo(int.class, messagePolled[0]);
  }

  //Overriding since JpaConsumer.DataHolder is private
  @Override
  public int processBatch(Queue<Object> exchanges) throws Exception {
    int total = exchanges.size();

    // limit if needed
    if (maxMessagesPerPoll > 0 && total > maxMessagesPerPoll) {
      LOG.debug("Limiting to maximum messages to poll {} as there were {} messages in this poll.",
          maxMessagesPerPoll, total);
      total = maxMessagesPerPoll;
    }

    for (int index = 0; index < total && isBatchAllowed(); index++) {
      // only loop if we are started (allowed to run)
      DataHolder holder = org.apache.camel.util.ObjectHelper.cast(DataHolder.class, exchanges.poll());
      EntityManager batchEntityManager = holder.manager;
      Exchange exchange = holder.exchange;
      Object result = holder.result;

      // add current index and total as properties
      exchange.setProperty(ExchangePropertyKey.BATCH_INDEX, index);
      exchange.setProperty(ExchangePropertyKey.BATCH_SIZE, total);
      exchange.setProperty(ExchangePropertyKey.BATCH_COMPLETE, index == total - 1);

      // update pending number of exchanges
      pendingExchanges = total - index - 1;
      if (lockEntity(result, batchEntityManager)) {
        // Run the @PreConsumed callback
        getPreDeleteHandler().deleteObject(batchEntityManager, result, exchange);

        // process the current exchange
        LOG.debug("Processing exchange: {}", exchange);
        try {
          getProcessor().process(exchange);
        } catch (Exception e) {
          exchange.setException(e);
        }

        try {
          if (exchange.getException() != null) {
            // if we failed then throw exception
            throw exchange.getException();
          } else {
            // Run the @Consumed callback
            getDeleteHandler().deleteObject(batchEntityManager, result, exchange);
          }
        } finally {
          releaseExchange(exchange, false);
        }
      }
    }

    return total;
  }
}
