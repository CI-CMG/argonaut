package edu.colorado.cires.argonaut;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.colorado.cires.argonaut.message.HeaderConsts;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
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
//@UseAdviceWith
@MockEndpointsAndSkip("seda:.*")
public class SubmissionTest {

  static {
    System.setProperty("camel.threads.virtual.enabled", "true");
  }


  @EndpointInject("mock:seda:submit-data")
  private MockEndpoint submitData;

  @EndpointInject("mock:seda:submit-greylist")
  private MockEndpoint submitGreylist;

  @EndpointInject("mock:seda:submit-removal")
  private MockEndpoint submitRemoval;

  @EndpointInject("mock:seda:submit-unknown")
  private MockEndpoint submitUnknown;


  @Autowired
  private ServiceProperties serviceProperties;

  @Test
  public void testSubmitDataRouting() throws Exception {
    Path submissionDir = serviceProperties.getSubmissionDirectory();
    Path aomlDir = submissionDir.resolve("dac").resolve("aoml");
    Path submitDir = aomlDir.resolve("submit");

    FileTestUtils.emptyDirectory(submitDir);

    String fileName = "nc_2025.04.02_16.15.tar.gz";
    Path submittedFile = submitDir.resolve(fileName);
    Path copyFile = aomlDir.resolve(fileName);

    submitData.expectedMessageCount(1);
    submitData.setAssertPeriod(100);
    submitGreylist.expectedMessageCount(0);
    submitGreylist.setAssertPeriod(100);
    submitRemoval.expectedMessageCount(0);
    submitRemoval.setAssertPeriod(100);
    submitUnknown.expectedMessageCount(0);
    submitUnknown.setAssertPeriod(100);

    // copy before moving to prevent state where file is picked up halfway
    Files.copy(Paths.get("src/test/resources/aoml").resolve(fileName), copyFile);
    Files.move(copyFile, submittedFile);

    MockEndpoint.assertIsSatisfied(submitData, submitGreylist, submitRemoval, submitUnknown);

    Exchange exchange = submitData.getExchanges().get(0);
    File receivedFile = exchange.getIn().getBody(File.class);
    assertEquals(fileName, receivedFile.getName());
    assertEquals("aoml", exchange.getIn().getHeader(HeaderConsts.DAC, String.class));
    Instant timestamp = Instant.parse(exchange.getIn().getHeader(HeaderConsts.SUBMISSION_TIMESTAMP, String.class));
    assertFalse(Instant.now().isBefore(timestamp));

  }

  @Test
  public void testSubmitGreylistRouting() throws Exception {
    Path submissionDir = serviceProperties.getSubmissionDirectory();
    Path aomlDir = submissionDir.resolve("dac").resolve("aoml");
    Path submitDir = aomlDir.resolve("submit");

    FileTestUtils.emptyDirectory(submitDir);

    String fileName = "aoml_greylist.csv";
    Path submittedFile = submitDir.resolve(fileName);
    Path copyFile = aomlDir.resolve(fileName);

    submitData.expectedMessageCount(0);
    submitData.setAssertPeriod(100);
    submitGreylist.expectedMessageCount(1);
    submitGreylist.setAssertPeriod(100);
    submitRemoval.expectedMessageCount(0);
    submitRemoval.setAssertPeriod(100);
    submitUnknown.expectedMessageCount(0);
    submitUnknown.setAssertPeriod(100);

    // copy before moving to prevent state where file is picked up halfway
    Files.copy(Paths.get("src/test/resources").resolve(fileName), copyFile);
    Files.move(copyFile, submittedFile);

    MockEndpoint.assertIsSatisfied(submitData, submitGreylist, submitRemoval, submitUnknown);

    Exchange exchange = submitGreylist.getExchanges().get(0);
    File receivedFile = exchange.getIn().getBody(File.class);
    assertEquals(fileName, receivedFile.getName());
    assertEquals("aoml", exchange.getIn().getHeader(HeaderConsts.DAC, String.class));
    Instant timestamp = Instant.parse(exchange.getIn().getHeader(HeaderConsts.SUBMISSION_TIMESTAMP, String.class));
    assertFalse(Instant.now().isBefore(timestamp));

  }

  @Test
  public void testSubmitRemovalRouting() throws Exception {
    Path submissionDir = serviceProperties.getSubmissionDirectory();
    Path aomlDir = submissionDir.resolve("dac").resolve("aoml");
    Path submitDir = aomlDir.resolve("submit");

    FileTestUtils.emptyDirectory(submitDir);

    String fileName = "aoml_removal.txt";
    Path submittedFile = submitDir.resolve(fileName);
    Path copyFile = aomlDir.resolve(fileName);

    submitData.expectedMessageCount(0);
    submitData.setAssertPeriod(100);
    submitGreylist.expectedMessageCount(0);
    submitGreylist.setAssertPeriod(100);
    submitRemoval.expectedMessageCount(1);
    submitRemoval.setAssertPeriod(100);
    submitUnknown.expectedMessageCount(0);
    submitUnknown.setAssertPeriod(100);

    // copy before moving to prevent state where file is picked up halfway
    Files.copy(Paths.get("src/test/resources").resolve(fileName), copyFile);
    Files.move(copyFile, submittedFile);

    MockEndpoint.assertIsSatisfied(submitData, submitGreylist, submitRemoval, submitUnknown);

    Exchange exchange = submitRemoval.getExchanges().get(0);
    File receivedFile = exchange.getIn().getBody(File.class);
    assertEquals(fileName, receivedFile.getName());
    assertEquals("aoml", exchange.getIn().getHeader(HeaderConsts.DAC, String.class));
    Instant timestamp = Instant.parse(exchange.getIn().getHeader(HeaderConsts.SUBMISSION_TIMESTAMP, String.class));
    assertFalse(Instant.now().isBefore(timestamp));

  }

  @Test
  public void testSubmitUnknownRouting() throws Exception {
    Path submissionDir = serviceProperties.getSubmissionDirectory();
    Path aomlDir = submissionDir.resolve("dac").resolve("aoml");
    Path submitDir = aomlDir.resolve("submit");

    FileTestUtils.emptyDirectory(submitDir);

    String fileName = "foo.bar";
    Path submittedFile = submitDir.resolve(fileName);
    Path copyFile = aomlDir.resolve(fileName);

    submitData.expectedMessageCount(0);
    submitData.setAssertPeriod(100);
    submitGreylist.expectedMessageCount(0);
    submitGreylist.setAssertPeriod(100);
    submitRemoval.expectedMessageCount(0);
    submitRemoval.setAssertPeriod(100);
    submitUnknown.expectedMessageCount(1);
    submitUnknown.setAssertPeriod(100);

    // copy before moving to prevent state where file is picked up halfway
    Files.copy(Paths.get("src/test/resources").resolve(fileName), copyFile);
    Files.move(copyFile, submittedFile);

    MockEndpoint.assertIsSatisfied(submitData, submitGreylist, submitRemoval, submitUnknown);

    Exchange exchange = submitUnknown.getExchanges().get(0);
    File receivedFile = exchange.getIn().getBody(File.class);
    assertEquals(fileName, receivedFile.getName());
    assertEquals("aoml", exchange.getIn().getHeader(HeaderConsts.DAC, String.class));
    Instant timestamp = Instant.parse(exchange.getIn().getHeader(HeaderConsts.SUBMISSION_TIMESTAMP, String.class));
    assertFalse(Instant.now().isBefore(timestamp));

  }


}
