package edu.colorado.cires.argonaut;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.colorado.cires.argonaut.config.ServiceProperties;
import edu.colorado.cires.argonaut.message.NcSubmissionMessage;
import edu.colorado.cires.argonaut.message.NcSubmissionMessage.Operation;
import edu.colorado.cires.argonaut.route.QueueConsts;
import edu.colorado.cires.argonaut.util.ArgonautFileUtils;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
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
@MockEndpointsAndSkip(QueueConsts.FILE_MOVED + "|" + QueueConsts.FLOAT_MERGE_AGG)
public class PostValidationTest {

  static {
    System.setProperty("camel.threads.virtual.enabled", "true");
  }

  @EndpointInject("mock:" + QueueConsts.FILE_MOVED)
  private MockEndpoint fileMoved;

  @EndpointInject("mock:" + QueueConsts.FLOAT_MERGE_AGG)
  private MockEndpoint floatMergeAgg;

  @Autowired
  private ServiceProperties serviceProperties;

  @Autowired
  private ProducerTemplate producerTemplate;

  @Test
  public void testSubmitDataHappyNoProfile() throws Exception {

    String timestamp = Instant.now().toString();
    String floatId = "1901830";
    String dac = "aoml";
    String fileName = "1901830_meta.nc";

    NcSubmissionMessage message = new NcSubmissionMessage();
    message.setFloatId(floatId);
    message.setDac(dac);
    message.setTimestamp(timestamp);
    message.setFileName(fileName);
    message.setProfile(false);

    FileTestUtils.emptyDirectory(serviceProperties.getOutputDirectory());
    FileTestUtils.emptyDirectory(ArgonautFileUtils.getProcessingDir(serviceProperties));

    Path testFile = Paths.get("src/test/resources/" + fileName);
    Path processingFile = ArgonautFileUtils.getProcessingProfileDir(serviceProperties, dac, floatId, false).resolve(fileName);

    ArgonautFileUtils.copy(testFile, processingFile);

    fileMoved.expectedMessageCount(1);
    floatMergeAgg.setExpectedMessageCount(1);

    producerTemplate.sendBody(QueueConsts.VALIDATION_SUCCESS, message);

    MockEndpoint.assertIsSatisfied(fileMoved, floatMergeAgg);

    NcSubmissionMessage floatMergeAggMessage = floatMergeAgg.getExchanges().get(0).getIn().getBody(NcSubmissionMessage.class);
    assertEquals(floatId, floatMergeAggMessage.getFloatId());
    assertEquals(dac, floatMergeAggMessage.getDac());

    assertFalse(Files.exists(processingFile));
    Path outputFile = ArgonautFileUtils.getOutputProfileDir(serviceProperties, dac, floatId, false).resolve(fileName);
    assertTrue(Files.exists(outputFile));

  }


  @Test
  public void testSubmitErrorProfile() throws Exception {

    String timestamp = Instant.now().toString();
    String floatId = "5905716";
    String dac = "aoml";
    String fileName = "R5905716_245.nc";

    NcSubmissionMessage message = new NcSubmissionMessage();
    message.setFloatId(floatId);
    message.setDac(dac);
    message.setTimestamp(timestamp);
    message.setFileName(fileName);
    message.setProfile(true);
    message.getValidationError().add("very bad profile");
    message.setNumberOfFilesInSubmission(100);

    FileTestUtils.emptyDirectory(serviceProperties.getOutputDirectory());
    FileTestUtils.emptyDirectory(ArgonautFileUtils.getProcessingDir(serviceProperties));

    Path testFile = Paths.get("src/test/resources/" + fileName);
    Path processingFile = ArgonautFileUtils.getProcessingProfileDir(serviceProperties, dac, floatId, true).resolve(fileName);

    ArgonautFileUtils.copy(testFile, processingFile);

    fileMoved.expectedMessageCount(1);
    floatMergeAgg.setExpectedMessageCount(0);
    floatMergeAgg.setAssertPeriod(200);

    producerTemplate.sendBody(QueueConsts.FILE_OUTPUT, message);

    MockEndpoint.assertIsSatisfied(fileMoved, floatMergeAgg);

    assertFalse(Files.exists(processingFile));
    Path outputFile = ArgonautFileUtils.getRejectProfileDir(serviceProperties, dac, timestamp, floatId, true).resolve(fileName);
    assertTrue(Files.exists(outputFile));

  }

  @Test
  public void testRemovalProfile() throws Exception {

    String timestamp = Instant.now().toString();
    String floatId = "5905716";
    String dac = "aoml";
    String fileName = "R5905716_245.nc";

    NcSubmissionMessage message = new NcSubmissionMessage();
    message.setFloatId(floatId);
    message.setDac(dac);
    message.setTimestamp(timestamp);
    message.setFileName(fileName);
    message.setProfile(true);
    message.setOperation(Operation.REMOVE);
    message.setNumberOfFilesInSubmission(13);

    FileTestUtils.emptyDirectory(serviceProperties.getOutputDirectory());

    Path testFile = Paths.get("src/test/resources").resolve(fileName);
    Path outputFile = Paths.get("target/output/dac").resolve(dac).resolve(floatId).resolve("profiles").resolve(fileName);
    Path removedFile =  Paths.get("target/output/removed/dac").resolve(dac).resolve(timestamp).resolve(floatId).resolve("profiles").resolve(fileName);
    Files.createDirectories(outputFile.getParent());

    ArgonautFileUtils.copy(testFile, outputFile);
    assertTrue(Files.exists(outputFile));
    assertFalse(Files.exists(removedFile));

    fileMoved.expectedMessageCount(1);
    floatMergeAgg.setExpectedMessageCount(1);

    producerTemplate.sendBody(QueueConsts.VALIDATION_SUCCESS, message);

    MockEndpoint.assertIsSatisfied(fileMoved, floatMergeAgg);

    NcSubmissionMessage expectedMessage = new NcSubmissionMessage();
    expectedMessage.setProfile(true);
    expectedMessage.setFloatId(floatId);
    expectedMessage.setDac("aoml");
    expectedMessage.setTimestamp(timestamp);
    expectedMessage.setFileName(fileName);
    expectedMessage.setOperation(Operation.REMOVE);
    expectedMessage.setNumberOfFilesInSubmission(13);

    NcSubmissionMessage floatMergeAggMessage = floatMergeAgg.getExchanges().get(0).getIn().getBody(NcSubmissionMessage.class);
    NcSubmissionMessage fileMovedMessage = fileMoved.getExchanges().get(0).getIn().getBody(NcSubmissionMessage.class);
    assertEquals(expectedMessage, fileMovedMessage);
    assertEquals(expectedMessage, floatMergeAggMessage);

    assertFalse(Files.exists(outputFile));
    assertTrue(Files.exists(removedFile));
  }

  @Test
  public void testRemovalMetadata() throws Exception {

    String timestamp = Instant.now().toString();
    String floatId = "1901830";
    String dac = "aoml";
    String fileName = "1901830_meta.nc";

    NcSubmissionMessage message = new NcSubmissionMessage();
    message.setFloatId(floatId);
    message.setDac(dac);
    message.setTimestamp(timestamp);
    message.setFileName(fileName);
    message.setProfile(false);
    message.setOperation(Operation.REMOVE);
    message.setNumberOfFilesInSubmission(13);

    FileTestUtils.emptyDirectory(serviceProperties.getOutputDirectory());

    Path testFile = Paths.get("src/test/resources").resolve(fileName);
    Path outputFile = Paths.get("target/output/dac").resolve(dac).resolve(floatId).resolve(fileName);
    Path removedFile =  Paths.get("target/output/removed/dac").resolve(dac).resolve(timestamp).resolve(floatId).resolve(fileName);
    Files.createDirectories(outputFile.getParent());

    ArgonautFileUtils.copy(testFile, outputFile);
    assertTrue(Files.exists(outputFile));
    assertFalse(Files.exists(removedFile));

    fileMoved.expectedMessageCount(1);
    floatMergeAgg.setExpectedMessageCount(1);

    producerTemplate.sendBody(QueueConsts.VALIDATION_SUCCESS, message);

    MockEndpoint.assertIsSatisfied(fileMoved, floatMergeAgg);

    NcSubmissionMessage expectedMessage = new NcSubmissionMessage();
    expectedMessage.setProfile(false);
    expectedMessage.setFloatId(floatId);
    expectedMessage.setDac("aoml");
    expectedMessage.setTimestamp(timestamp);
    expectedMessage.setFileName(fileName);
    expectedMessage.setOperation(Operation.REMOVE);
    expectedMessage.setNumberOfFilesInSubmission(13);

    NcSubmissionMessage floatMergeAggMessage = floatMergeAgg.getExchanges().get(0).getIn().getBody(NcSubmissionMessage.class);
    NcSubmissionMessage fileMovedMessage = fileMoved.getExchanges().get(0).getIn().getBody(NcSubmissionMessage.class);
    assertEquals(expectedMessage, fileMovedMessage);
    assertEquals(expectedMessage, floatMergeAggMessage);

    assertFalse(Files.exists(outputFile));
    assertTrue(Files.exists(removedFile));

  }


}
