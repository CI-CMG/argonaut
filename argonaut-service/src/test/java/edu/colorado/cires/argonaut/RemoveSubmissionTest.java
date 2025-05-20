package edu.colorado.cires.argonaut;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import edu.colorado.cires.argonaut.config.ServiceProperties;
import edu.colorado.cires.argonaut.message.NcSubmissionMessage;
import edu.colorado.cires.argonaut.route.QueueConsts;
import edu.colorado.cires.argonaut.service.SubmissionTimestampService;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.camel.EndpointInject;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.MockEndpointsAndSkip;
import org.junit.jupiter.api.Test;
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
@MockEndpointsAndSkip(QueueConsts.VALIDATION_SUCCESS + "|" + QueueConsts.SUBMISSION_REPORT)
public class RemoveSubmissionTest {


  static {
    System.setProperty("camel.threads.virtual.enabled", "true");
  }

  @EndpointInject("mock:" + QueueConsts.VALIDATION_SUCCESS)
  private MockEndpoint validationSuccess;

  @EndpointInject("mock:" + QueueConsts.SUBMISSION_REPORT)
  private MockEndpoint submissionReport;

  @MockitoBean
  private SubmissionTimestampService submissionTimestampService;


  @Autowired
  private ServiceProperties serviceProperties;

  @Test
  public void testSubmitCorrectRemovalFile() throws Exception {

    List<String> expectedFileNames = Arrays.asList(
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
        "5904397_tech.nc"
    );

    String timestamp = Instant.now().toString();
    when(submissionTimestampService.generateTimestamp()).thenReturn(timestamp);

    Path submissionDir = serviceProperties.getSubmissionDirectory();
    Path aomlDir = submissionDir.resolve("dac").resolve("aoml");
    Path submitDir = aomlDir.resolve("submit");

    Path submissionProcessingDir = aomlDir.resolve("processing");
    Path submissionProcessedDir = aomlDir.resolve("processed");
    Path aomlProcessingDir = serviceProperties.getWorkDirectory().resolve("processing/dac/aoml");

    FileTestUtils.emptyDirectory(submitDir);
    FileTestUtils.emptyDirectory(submissionProcessingDir);
    FileTestUtils.emptyDirectory(aomlProcessingDir);
    FileTestUtils.emptyDirectory(submissionProcessedDir);

    String fileName = "aoml_removal.txt";
    Path submittedFile = submitDir.resolve(fileName);
    Path copyFile = aomlDir.resolve(fileName);

    validationSuccess.expectedMessageCount(13);
    validationSuccess.setAssertPeriod(200);
    submissionReport.expectedMessageCount(0);
    submissionReport.setAssertPeriod(200);

    // copy before moving to prevent state where file is picked up halfway
    Files.copy(Paths.get("src/test/resources").resolve("aoml_removal2.txt"), copyFile);
    Files.move(copyFile, submittedFile);

    MockEndpoint.assertIsSatisfied(validationSuccess, submissionReport);

    Set<NcSubmissionMessage> validationMessages = new HashSet<>();
    expectedFileNames.forEach(name -> {
      boolean profile = name.startsWith("R");
      String floatId = name.split("_")[0].replaceAll("R", "");

      NcSubmissionMessage expectedMessage = NcSubmissionMessage.builder()
          .withProfile(profile)
          .withOperation(NcSubmissionMessage.Operation.REMOVE)
          .withDac("aoml")
          .withFileName(name)
          .withTimestamp(timestamp)
          .withFloatId(floatId)
          .withNumberOfFilesInSubmission(13)
          .build();

      validationMessages.add(expectedMessage);
    });

    Set<NcSubmissionMessage> receivedMessages = validationSuccess.getExchanges().stream()
        .map(exchange -> exchange.getIn().getBody(NcSubmissionMessage.class))
        .collect(Collectors.toSet());

    assertEquals(validationMessages, receivedMessages);

    assertFalse(Files.exists(submittedFile));
    assertTrue(Files.exists(submissionProcessedDir.resolve(timestamp).resolve(fileName)));


  }

  @Test
  public void testSubmitIncorrectRemovalFile() throws Exception {

    String timestamp = Instant.now().toString();
    when(submissionTimestampService.generateTimestamp()).thenReturn(timestamp);

    Path submissionDir = serviceProperties.getSubmissionDirectory();
    Path aomlDir = submissionDir.resolve("dac").resolve("aoml");
    Path submitDir = aomlDir.resolve("submit");

    Path submissionProcessingDir = aomlDir.resolve("processing");
    Path submissionProcessedDir = aomlDir.resolve("processed");
    Path aomlProcessingDir = serviceProperties.getWorkDirectory().resolve("processing/dac/aoml");

    FileTestUtils.emptyDirectory(submitDir);
    FileTestUtils.emptyDirectory(submissionProcessingDir);
    FileTestUtils.emptyDirectory(aomlProcessingDir);
    FileTestUtils.emptyDirectory(submissionProcessedDir);

    String fileName = "foobar_removal.txt";
    Path submittedFile = submitDir.resolve(fileName);
    Path copyFile = aomlDir.resolve(fileName);

    validationSuccess.expectedMessageCount(0);
    validationSuccess.setAssertPeriod(200);
    submissionReport.expectedMessageCount(1);
    submissionReport.setAssertPeriod(200);

    // copy before moving to prevent state where file is picked up halfway
    Files.copy(Paths.get("src/test/resources").resolve("aoml_removal2.txt"), copyFile);
    Files.move(copyFile, submittedFile);

    MockEndpoint.assertIsSatisfied(validationSuccess, submissionReport);

    NcSubmissionMessage receivedMessage = submissionReport.getExchanges().stream()
        .map(exchange -> exchange.getIn().getBody(NcSubmissionMessage.class))
        .findFirst().orElse(null);

    NcSubmissionMessage expectedMessage = NcSubmissionMessage.builder()
        .withProfile(false)
        .withFileName(fileName)
        .withValidationErrors(
            Collections.singletonList("removal file name does not match DAC 'aoml' (aoml_removal.txt): foobar_removal.txt"))
        .withProfile(false)
        .withOperation(NcSubmissionMessage.Operation.REMOVE)
        .withDac("aoml")
        .withNumberOfFilesInSubmission(1)
        .withTimestamp(timestamp)
        .build();

    assertEquals(expectedMessage, receivedMessage);

    assertFalse(Files.exists(submittedFile));
    assertTrue(Files.exists(submissionProcessedDir.resolve(timestamp).resolve(fileName)));


  }


}
