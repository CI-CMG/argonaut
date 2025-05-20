package edu.colorado.cires.argonaut;

import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.colorado.cires.argonaut.config.ServiceProperties;
import edu.colorado.cires.argonaut.message.NcSubmissionMessage;
import edu.colorado.cires.argonaut.message.NcSubmissionMessage.Operation;
import edu.colorado.cires.argonaut.message.SubmissionCompleteMessage;
import edu.colorado.cires.argonaut.route.QueueConsts;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
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

        SubmissionCompleteMessage.Builder expectedMessage = SubmissionCompleteMessage.builder()
            .withTimeStamp(timestampStr)
            .withNumberOfFiles(3)
            .withDac(dac);

        for (String fileName : Arrays.asList(floatId + "_meta.nc", floatId + "_tech.nc", floatId + "_Rtraj.nc")) {
          NcSubmissionMessage message = NcSubmissionMessage.builder()
              .withFloatId(floatId)
              .withDac(dac)
              .withTimestamp(timestampStr)
              .withFileName(fileName)
              .withProfile(false)
              .withNumberOfFilesInSubmission(3)
              .build();

          messages.add(message);

          expectedMessage.addCompletedFile(fileName);
        }

        expected.add(expectedMessage.build());
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

    SubmissionCompleteMessage.Builder expectedMessage = SubmissionCompleteMessage.builder()
        .withTimeStamp(timestamp)
        .withNumberOfFiles(13)
        .withDac(dac);

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

      NcSubmissionMessage message = NcSubmissionMessage.builder()
          .withFloatId(floatId)
          .withDac(dac)
          .withTimestamp(timestamp)
          .withFileName(fileName)
          .withProfile(fileName.contains("R"))
          .withNumberOfFilesInSubmission(13)
          .withOperation(Operation.REMOVE)
          .build();

      messages.add(message);

      expectedMessage.addCompletedFile(fileName);

    }

    prepareEmail.expectedMessageCount(1);

    messages.forEach(message -> producerTemplate.sendBody(QueueConsts.SUBMISSION_COMPLETE_AGG, message));

    prepareEmail.assertIsSatisfied();

    SubmissionCompleteMessage actual = prepareEmail.getExchanges().stream()
        .map(exchange -> exchange.getIn().getBody(SubmissionCompleteMessage.class))
        .findFirst().orElse(null);

    assertEquals(expectedMessage.build(), actual);

  }


  @Test
  public void testInvalidRemoval() throws Exception {

    String timestamp = Instant.now().toString();
    String dac = "aoml";

    SubmissionCompleteMessage expectedMessage = SubmissionCompleteMessage.builder()
        .withTimeStamp(timestamp)
        .withNumberOfFiles(1)
        .withDac(dac)
        .withCompletedFiles(Collections.singletonList("foobar_removal.txt"))
        .build();

    NcSubmissionMessage message = NcSubmissionMessage.builder()
        .withDac(dac)
        .withTimestamp(timestamp)
        .withFileName("foobar_removal.txt")
        .withProfile(false)
        .withNumberOfFilesInSubmission(1)
        .withOperation(Operation.REMOVE)
        .build();

    prepareEmail.expectedMessageCount(1);

    producerTemplate.sendBody(QueueConsts.SUBMISSION_COMPLETE_AGG, message);

    prepareEmail.assertIsSatisfied();

    SubmissionCompleteMessage actual = prepareEmail.getExchanges().stream()
        .map(exchange -> exchange.getIn().getBody(SubmissionCompleteMessage.class))
        .findFirst().orElse(null);

    assertEquals(expectedMessage, actual);

  }


}
