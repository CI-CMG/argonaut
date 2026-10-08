package edu.colorado.cires.argonaut.messaging.cameljpa;

import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.support.TransactionTemplate;

@CamelSpringTest
@ContextConfiguration({"MessagingCamelJpaTest.xml"})
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class MessagingCamelJpaTest {

  @EndpointInject("mock:end")
  private MockEndpoint end;

  @Autowired
  private ProducerTemplate producerTemplate;

  @Autowired
  private TransactionTemplate transactionTemplate;

  @Autowired
  private EntityManagerFactory entityManagerFactory;

  @Autowired
  private EntityManager em;


  @BeforeEach
  @AfterEach
  public void setup() throws Exception{
    transactionTemplate.executeWithoutResult(s -> {
      em.createQuery("delete from JsonMessageEntity").executeUpdate();
    });
  }

  @Test
  public void test() throws Exception{


    end.expectedMessageCount(4);
    end.setAssertPeriod(1100);

    producerTemplate.sendBody("seda:start", "{\"id\":\"1\"}");
    producerTemplate.sendBody("seda:start", "{\"id\":\"2\"}");
    producerTemplate.sendBody("seda:start", "{\"id\":\"3\"}");
    producerTemplate.sendBody("seda:start", "{\"id\":\"4\"}");

    end.assertIsSatisfied();

    List<Exchange> received = end.getExchanges();
    assertEquals("{\"id\":\"1\"}", received.get(0).getIn().getBody(String.class));
    assertEquals("{\"id\":\"2\"}", received.get(1).getIn().getBody(String.class));
    assertEquals("{\"id\":\"3\"}", received.get(2).getIn().getBody(String.class));
    assertEquals("{\"id\":\"4\"}", received.get(3).getIn().getBody(String.class));

  }

}