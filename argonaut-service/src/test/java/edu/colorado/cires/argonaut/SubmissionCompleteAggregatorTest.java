package edu.colorado.cires.argonaut;

import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.colorado.cires.argonaut.config.ServiceProperties;
import edu.colorado.cires.argonaut.message.NcSubmissionMessage;
import edu.colorado.cires.argonaut.message.NcSubmissionMessage.Operation;
import edu.colorado.cires.argonaut.message.SubmissionCompleteMessage;
import edu.colorado.cires.argonaut.route.QueueConsts;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.MockEndpointsAndSkip;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@CamelSpringBootTest
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
@MockEndpointsAndSkip(QueueConsts.PREPARE_SUBMISSION_EMAIL)
public class SubmissionCompleteAggregatorTest {

  static {
    System.setProperty("camel.threads.virtual.enabled", "true");
  }

  @EndpointInject("mock:" + QueueConsts.PREPARE_SUBMISSION_EMAIL)
  private MockEndpoint prepareEmail;

  @Autowired
  private ServiceProperties serviceProperties;

  @Autowired
  private ProducerTemplate producerTemplate;

  @Test
  public void testDataSubmission() throws Exception {

    Instant timestamp = Instant.now();

    Set<SubmissionCompleteMessage> expected = new HashSet<>();
    Set<NcSubmissionMessage> messages = new HashSet<>();

    for (String dac : Arrays.asList("aoml", "csiro")) {
      for (int i = 0; i < 10; i++) {
        String timestampStr = timestamp.minusSeconds(i).toString();
        String floatId = "190183" + i;

        SubmissionCompleteMessage expectedMessage = new SubmissionCompleteMessage();
        expectedMessage.setTimeStamp(timestampStr);
        expectedMessage.setNumberOfFiles(3);
        expectedMessage.setDac(dac);

        for (String fileName : Arrays.asList(floatId + "_meta.nc", floatId + "_tech.nc", floatId + "_Rtraj.nc")) {
          NcSubmissionMessage message = new NcSubmissionMessage();
          message.setFloatId(floatId);
          message.setDac(dac);
          message.setTimestamp(timestampStr);
          message.setFileName(fileName);
          message.setProfile(false);
          message.setNumberOfFilesInSubmission(3);

          messages.add(message);

          expectedMessage.getFilesCompleted().add(fileName);
        }

        expected.add(expectedMessage);
      }
    }

    prepareEmail.expectedMessageCount(20);

    messages.forEach(message -> producerTemplate.sendBody(QueueConsts.SUBMISSION_COMPLETE_AGG, message));

    prepareEmail.assertIsSatisfied();

    Set<SubmissionCompleteMessage> actual = prepareEmail.getExchanges().stream()
        .map(exchange -> exchange.getIn().getBody(SubmissionCompleteMessage.class))
        .collect(Collectors.toSet());

    assertEquals(expected, actual);

  }


  @Test
  public void testRemoval() throws Exception {

    String timestamp = Instant.now().toString();
    String dac = "aoml";

    SubmissionCompleteMessage expectedMessage = new SubmissionCompleteMessage();
    expectedMessage.setTimeStamp(timestamp);
    expectedMessage.setNumberOfFiles(13);
    expectedMessage.setDac(dac);

    Set<NcSubmissionMessage> messages = new HashSet<>();

    for (String fileName : Arrays.asList(
        "R5904398_001.nc",
        "R5904398_004.nc",
        "R5904398_005.nc",
        "R5904398_006.nc",
        "5904398_Rtraj.nc",
        "5904398_meta.nc",
        "R5904397_001.nc",
        "R5904397_004.nc",
        "R5904397_005.nc",
        "R5904397_006.nc",
        "5904397_Rtraj.nc",
        "5904397_meta.nc",
        "5904397_tech.nc")
    ) {

      String floatId = fileName.replaceAll("R", "").split("_")[0];

      NcSubmissionMessage message = new NcSubmissionMessage();
      message.setFloatId(floatId);
      message.setDac(dac);
      message.setTimestamp(timestamp);
      message.setFileName(fileName);
      message.setProfile(fileName.contains("R"));
      message.setNumberOfFilesInSubmission(13);
      message.setOperation(Operation.REMOVE);

      messages.add(message);

      expectedMessage.getFilesCompleted().add(fileName);

    }

    prepareEmail.expectedMessageCount(1);

    messages.forEach(message -> producerTemplate.sendBody(QueueConsts.SUBMISSION_COMPLETE_AGG, message));

    prepareEmail.assertIsSatisfied();

    SubmissionCompleteMessage actual = prepareEmail.getExchanges().stream()
        .map(exchange -> exchange.getIn().getBody(SubmissionCompleteMessage.class))
        .findFirst().orElse(null);

    assertEquals(expectedMessage, actual);

  }


  @Test
  public void testInvalidRemoval() throws Exception {

    String timestamp = Instant.now().toString();
    String dac = "aoml";

    SubmissionCompleteMessage expectedMessage = new SubmissionCompleteMessage();
    expectedMessage.setTimeStamp(timestamp);
    expectedMessage.setNumberOfFiles(1);
    expectedMessage.setDac(dac);
    expectedMessage.getFilesCompleted().add("foobar_removal.txt");

    NcSubmissionMessage message = new NcSubmissionMessage();
    message.setDac(dac);
    message.setTimestamp(timestamp);
    message.setFileName("foobar_removal.txt");
    message.setProfile(false);
    message.setNumberOfFilesInSubmission(1);
    message.setOperation(Operation.REMOVE);

    prepareEmail.expectedMessageCount(1);

    producerTemplate.sendBody(QueueConsts.SUBMISSION_COMPLETE_AGG, message);

    prepareEmail.assertIsSatisfied();

    SubmissionCompleteMessage actual = prepareEmail.getExchanges().stream()
        .map(exchange -> exchange.getIn().getBody(SubmissionCompleteMessage.class))
        .findFirst().orElse(null);

    assertEquals(expectedMessage, actual);

  }


}
