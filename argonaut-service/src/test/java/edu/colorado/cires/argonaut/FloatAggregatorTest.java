package edu.colorado.cires.argonaut;

import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.colorado.cires.argonaut.config.ServiceProperties;
import edu.colorado.cires.argonaut.message.NcSubmissionMessage;
import edu.colorado.cires.argonaut.processor.FloatMergeProcessor;
import edu.colorado.cires.argonaut.route.QueueConsts;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.MockEndpointsAndSkip;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@CamelSpringBootTest
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
@MockEndpointsAndSkip(QueueConsts.UPDATE_INDEX_AGG)
public class FloatAggregatorTest {
  static {
    System.setProperty("camel.threads.virtual.enabled", "true");
  }
  @MockitoBean
  private FloatMergeProcessor floatMergeProcessor;
  @EndpointInject("mock:" + QueueConsts.UPDATE_INDEX_AGG)
  private MockEndpoint updateIndexAgg;

  @Autowired
  private ProducerTemplate producerTemplate;

  @Autowired
  private ServiceProperties serviceProperties;

  @Test
  public void testFloatMergeAggregator() throws Exception {
    String timestamp = Instant.now().toString();
    String dac1 = "aoml1";
    String dac2 = "aoml2";
    String dac3 = "aoml3";

    NcSubmissionMessage message0 = NcSubmissionMessage.builder()
        .withFloatId("0")
        .withDac(dac1)
        .withTimestamp(timestamp)
        .withProfile(true)
        .build();

    NcSubmissionMessage message1 = NcSubmissionMessage.builder()
        .withFloatId("0")
        .withDac(dac1)
        .withTimestamp(timestamp)
        .withProfile(true)
        .build();

    NcSubmissionMessage message2 = NcSubmissionMessage.builder()
        .withFloatId("1")
        .withDac(dac1)
        .withTimestamp(timestamp)
        .withProfile(true)
        .build();

    NcSubmissionMessage message3 = NcSubmissionMessage.builder()
        .withFloatId("1")
        .withDac(dac1)
        .withTimestamp(timestamp)
        .withProfile(true)
        .build();

    NcSubmissionMessage message4 = NcSubmissionMessage.builder()
        .withFloatId("1")
        .withDac(dac1)
        .withTimestamp(timestamp)
        .withProfile(true)
        .build();

    NcSubmissionMessage message5 = NcSubmissionMessage.builder()
        .withFloatId("2")
        .withDac(dac2)
        .withTimestamp(timestamp)
        .withProfile(true)
        .build();

    NcSubmissionMessage message6 = NcSubmissionMessage.builder()
        .withFloatId("2")
        .withDac(dac2)
        .withTimestamp(timestamp)
        .withProfile(true)
        .build();

    NcSubmissionMessage message7 = NcSubmissionMessage.builder()
        .withFloatId("3")
        .withDac(dac3)
        .withTimestamp(timestamp)
        .withProfile(true)
        .build();

    List<NcSubmissionMessage> messages = Arrays.asList(
        message1, message0, message2, message5, message7, message4, message6, message3
    );

    updateIndexAgg.expectedMessageCount(4);
    updateIndexAgg.setAssertPeriod(500);

    messages.forEach(message -> producerTemplate.sendBody(QueueConsts.FLOAT_MERGE_AGG, message));

    updateIndexAgg.assertIsSatisfied();
    List<NcSubmissionMessage> actual = updateIndexAgg.getExchanges().stream().map(e -> e.getIn().getBody(NcSubmissionMessage.class)).sorted().toList();
    List<NcSubmissionMessage> expected = Arrays.asList(message0, message2, message5, message7 );
    expected.sort(NcSubmissionMessage::compareTo);
    assertEquals(expected, actual);
    Mockito.verify(floatMergeProcessor, Mockito.times(1)).process(Mockito.argThat(new BodyMatcher<>(message0)));
    Mockito.verify(floatMergeProcessor, Mockito.times(1)).process(Mockito.argThat(new BodyMatcher<>(message2)));
    Mockito.verify(floatMergeProcessor, Mockito.times(1)).process(Mockito.argThat(new BodyMatcher<>(message5)));
    Mockito.verify(floatMergeProcessor, Mockito.times(1)).process(Mockito.argThat(new BodyMatcher<>(message7)));
  }

  private static class BodyMatcher<T> implements ArgumentMatcher<Exchange> {

    private final T body;

    private BodyMatcher(T body) {
      this.body = body;
    }

    @Override
    public boolean matches(Exchange exchange) {
      return body.equals(exchange.getIn().getBody());
    }
  }
}
