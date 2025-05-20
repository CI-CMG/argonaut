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
import java.util.Collections;
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
@MockEndpointsAndSkip(
    QueueConsts.FILE_MOVED + "|" + QueueConsts.FLOAT_MERGE_AGG + "|" + QueueConsts.GEO_MERGE_AGG + "|" + QueueConsts.LATEST_MERGE_AGG)
public class PostValidationTest {

  static {
    System.setProperty("camel.threads.virtual.enabled", "true");
  }

  @EndpointInject("mock:" + QueueConsts.FILE_MOVED)
  private MockEndpoint fileMoved;

  @EndpointInject("mock:" + QueueConsts.FLOAT_MERGE_AGG)
  private MockEndpoint floatMergeAgg;

  @EndpointInject("mock:" + QueueConsts.GEO_MERGE_AGG)
  private MockEndpoint geoMergeAgg;

  @EndpointInject("mock:" + QueueConsts.LATEST_MERGE_AGG)
  private MockEndpoint latestMergeAgg;

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

    NcSubmissionMessage message = NcSubmissionMessage.builder()
        .withFloatId(floatId)
        .withDac(dac)
        .withTimestamp(timestamp)
        .withFileName(fileName)
        .withProfile(false)
        .build();

    FileTestUtils.emptyDirectory(serviceProperties.getOutputDirectory());
    FileTestUtils.emptyDirectory(ArgonautFileUtils.getProcessingDir(serviceProperties));

    Path testFile = Paths.get("src/test/resources/" + fileName);
    Path processingFile = ArgonautFileUtils.getProcessingProfileDir(serviceProperties, dac, floatId, false).resolve(fileName);

    ArgonautFileUtils.copy(testFile, processingFile);

    fileMoved.expectedMessageCount(1);
    floatMergeAgg.setExpectedMessageCount(1);
    geoMergeAgg.expectedMessageCount(1);
    latestMergeAgg.expectedMessageCount(1);

    producerTemplate.sendBody(QueueConsts.VALIDATION_SUCCESS, message);

    MockEndpoint.assertIsSatisfied(fileMoved, floatMergeAgg, geoMergeAgg, latestMergeAgg);

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

    NcSubmissionMessage message = NcSubmissionMessage.builder()
        .withFloatId(floatId)
        .withDac(dac)
        .withTimestamp(timestamp)
        .withFileName(fileName)
        .withProfile(true)
        .withValidationErrors(Collections.singletonList("very bad profile"))
        .withNumberOfFilesInSubmission(100)
        .build();

    FileTestUtils.emptyDirectory(serviceProperties.getOutputDirectory());
    FileTestUtils.emptyDirectory(ArgonautFileUtils.getProcessingDir(serviceProperties));

    Path testFile = Paths.get("src/test/resources/" + fileName);
    Path processingFile = ArgonautFileUtils.getProcessingProfileDir(serviceProperties, dac, floatId, true).resolve(fileName);

    ArgonautFileUtils.copy(testFile, processingFile);

    fileMoved.expectedMessageCount(1);
    floatMergeAgg.setExpectedMessageCount(0);
    floatMergeAgg.setAssertPeriod(200);
    geoMergeAgg.expectedMessageCount(0);
    geoMergeAgg.setAssertPeriod(200);
    latestMergeAgg.expectedMessageCount(0);
    latestMergeAgg.setAssertPeriod(200);

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

    NcSubmissionMessage message = NcSubmissionMessage.builder()
        .withFloatId(floatId)
        .withDac(dac)
        .withTimestamp(timestamp)
        .withFileName(fileName)
        .withProfile(true)
        .withOperation(Operation.REMOVE)
        .withNumberOfFilesInSubmission(13)
        .build();

    FileTestUtils.emptyDirectory(serviceProperties.getOutputDirectory());

    Path testFile = Paths.get("src/test/resources").resolve(fileName);
    Path outputFile = Paths.get("target/output/dac").resolve(dac).resolve(floatId).resolve("profiles").resolve(fileName);
    Path removedFile = Paths.get("target/output/removed/dac").resolve(dac).resolve(timestamp).resolve(floatId).resolve("profiles").resolve(fileName);
    Files.createDirectories(outputFile.getParent());

    ArgonautFileUtils.copy(testFile, outputFile);
    assertTrue(Files.exists(outputFile));
    assertFalse(Files.exists(removedFile));

    fileMoved.expectedMessageCount(1);
    floatMergeAgg.setExpectedMessageCount(1);
    geoMergeAgg.expectedMessageCount(1);
    latestMergeAgg.expectedMessageCount(1);

    producerTemplate.sendBody(QueueConsts.VALIDATION_SUCCESS, message);

    MockEndpoint.assertIsSatisfied(fileMoved, floatMergeAgg);

    NcSubmissionMessage expectedMessage = NcSubmissionMessage.builder()
        .withProfile(true)
        .withFloatId(floatId)
        .withDac("aoml")
        .withTimestamp(timestamp)
        .withFileName(fileName)
        .withOperation(Operation.REMOVE)
        .withNumberOfFilesInSubmission(13)
        .build();

    NcSubmissionMessage floatMergeAggMessage = floatMergeAgg.getExchanges().get(0).getIn().getBody(NcSubmissionMessage.class);
    NcSubmissionMessage fileMovedMessage = fileMoved.getExchanges().get(0).getIn().getBody(NcSubmissionMessage.class);
    assertEquals(expectedMessage, fileMovedMessage);
    assertEquals(expectedMessage, floatMergeAggMessage);
    assertEquals(expectedMessage, fileMoved.getExchanges().get(0).getIn().getBody(NcSubmissionMessage.class));
    assertEquals(expectedMessage, latestMergeAgg.getExchanges().get(0).getIn().getBody(NcSubmissionMessage.class));

    assertFalse(Files.exists(outputFile));
    assertTrue(Files.exists(removedFile));
  }

  @Test
  public void testRemovalMetadata() throws Exception {

    String timestamp = Instant.now().toString();
    String floatId = "1901830";
    String dac = "aoml";
    String fileName = "1901830_meta.nc";

    NcSubmissionMessage message = NcSubmissionMessage.builder()
        .withFloatId(floatId)
        .withDac(dac)
        .withTimestamp(timestamp)
        .withFileName(fileName)
        .withProfile(false)
        .withOperation(Operation.REMOVE)
        .withNumberOfFilesInSubmission(13)
        .build();

    FileTestUtils.emptyDirectory(serviceProperties.getOutputDirectory());

    Path testFile = Paths.get("src/test/resources").resolve(fileName);
    Path outputFile = Paths.get("target/output/dac").resolve(dac).resolve(floatId).resolve(fileName);
    Path removedFile = Paths.get("target/output/removed/dac").resolve(dac).resolve(timestamp).resolve(floatId).resolve(fileName);
    Files.createDirectories(outputFile.getParent());

    ArgonautFileUtils.copy(testFile, outputFile);
    assertTrue(Files.exists(outputFile));
    assertFalse(Files.exists(removedFile));

    fileMoved.expectedMessageCount(1);
    floatMergeAgg.setExpectedMessageCount(1);
    geoMergeAgg.expectedMessageCount(1);
    latestMergeAgg.expectedMessageCount(1);

    producerTemplate.sendBody(QueueConsts.VALIDATION_SUCCESS, message);

    MockEndpoint.assertIsSatisfied(fileMoved, floatMergeAgg);

    NcSubmissionMessage expectedMessage = NcSubmissionMessage.builder()
        .withProfile(false)
        .withFloatId(floatId)
        .withDac("aoml")
        .withTimestamp(timestamp)
        .withFileName(fileName)
        .withOperation(Operation.REMOVE)
        .withNumberOfFilesInSubmission(13)
        .build();

    NcSubmissionMessage floatMergeAggMessage = floatMergeAgg.getExchanges().get(0).getIn().getBody(NcSubmissionMessage.class);
    NcSubmissionMessage fileMovedMessage = fileMoved.getExchanges().get(0).getIn().getBody(NcSubmissionMessage.class);
    assertEquals(expectedMessage, fileMovedMessage);
    assertEquals(expectedMessage, floatMergeAggMessage);
    assertEquals(expectedMessage, fileMoved.getExchanges().get(0).getIn().getBody(NcSubmissionMessage.class));
    assertEquals(expectedMessage, latestMergeAgg.getExchanges().get(0).getIn().getBody(NcSubmissionMessage.class));

    assertFalse(Files.exists(outputFile));
    assertTrue(Files.exists(removedFile));

  }


}
