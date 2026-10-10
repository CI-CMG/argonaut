package edu.colorado.cires.argonaut.messaging.cameljpa;

import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.util.Arrays;
import org.apache.camel.EndpointInject;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.support.TransactionTemplate;

@CamelSpringTest
@ContextConfiguration({"CamelJpaMessageSenderTest.xml"})
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class CamelJpaMessageSenderTest {

  @EndpointInject("mock:q1-1")
  private MockEndpoint q11;
  @EndpointInject("mock:q1-2")
  private MockEndpoint q12;
  @EndpointInject("mock:q2-1")
  private MockEndpoint q21;
  @EndpointInject("mock:q2-2")
  private MockEndpoint q22;

  @Autowired
  @Qualifier("q1TransactionTemplate")
  private TransactionTemplate q1TransactionTemplate;

  @Autowired
  @Qualifier("q2TransactionTemplate")
  private TransactionTemplate q2TransactionTemplate;

  @Autowired
  @Qualifier("q1EntityManagerFactory")
  private EntityManagerFactory q1EntityManagerFactory;

  @Autowired
  @Qualifier("q1EntityManagerFactory")
  private EntityManager q1Em;

  @Autowired
  @Qualifier("q2EntityManagerFactory")
  private EntityManager q2Em;

  @Autowired
  @Qualifier("sender1")
  private CamelJpaMessageSender sender1;

  @Autowired
  @Qualifier("sender2")
  private CamelJpaMessageSender sender2;


  @BeforeEach
  @AfterEach
  public void setup() throws Exception{
    q1TransactionTemplate.executeWithoutResult(s -> {
      q1Em.createQuery("delete from ArgonautMessageEntity").executeUpdate();
    });
    q2TransactionTemplate.executeWithoutResult(s -> {
      q2Em.createQuery("delete from ArgonautMessageEntity").executeUpdate();
    });
  }

  @Test
  public void test() throws Exception{


    q11.expectedMessageCount(2);
    q11.setAssertPeriod(1100);

    q12.expectedMessageCount(2);
    q12.setAssertPeriod(1100);

    q21.expectedMessageCount(2);
    q21.setAssertPeriod(1100);

    q22.expectedMessageCount(2);
    q22.setAssertPeriod(1100);

    sender1.sendJson("one", "{\"sender\":\"sender1\",\"queue\":\"one\",\"id\":1}");
    sender1.sendJson("two", "{\"sender\":\"sender1\",\"queue\":\"two\",\"id\":1}");
    sender2.sendJson("one", "{\"sender\":\"sender2\",\"queue\":\"one\",\"id\":1}");
    sender1.sendJson("one", "{\"sender\":\"sender1\",\"queue\":\"one\",\"id\":2}");
    sender2.sendJson("one", "{\"sender\":\"sender2\",\"queue\":\"one\",\"id\":2}");
    sender2.sendJson("two", "{\"sender\":\"sender2\",\"queue\":\"two\",\"id\":1}");
    sender1.sendJson("two", "{\"sender\":\"sender1\",\"queue\":\"two\",\"id\":2}");
    sender2.sendJson("two", "{\"sender\":\"sender2\",\"queue\":\"two\",\"id\":2}");

    MockEndpoint.assertIsSatisfied(q11, q12, q21, q22);

    assertEquals(Arrays.asList("{\"sender\":\"sender1\",\"queue\":\"one\",\"id\":1}", "{\"sender\":\"sender1\",\"queue\":\"one\",\"id\":2}"), q11.getExchanges().stream().map(ex -> ex.getIn().getBody(String.class)).toList());
    assertEquals(Arrays.asList("{\"sender\":\"sender1\",\"queue\":\"two\",\"id\":1}", "{\"sender\":\"sender1\",\"queue\":\"two\",\"id\":2}"), q12.getExchanges().stream().map(ex -> ex.getIn().getBody(String.class)).toList());
    assertEquals(Arrays.asList("{\"sender\":\"sender2\",\"queue\":\"one\",\"id\":1}", "{\"sender\":\"sender2\",\"queue\":\"one\",\"id\":2}"), q21.getExchanges().stream().map(ex -> ex.getIn().getBody(String.class)).toList());
    assertEquals(Arrays.asList("{\"sender\":\"sender2\",\"queue\":\"two\",\"id\":1}", "{\"sender\":\"sender2\",\"queue\":\"two\",\"id\":2}"), q22.getExchanges().stream().map(ex -> ex.getIn().getBody(String.class)).toList());


  }
}