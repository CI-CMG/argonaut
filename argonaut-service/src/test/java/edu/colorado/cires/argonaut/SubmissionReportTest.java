package edu.colorado.cires.argonaut;

import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.colorado.cires.argonaut.message.NcSubmissionMessage;
import edu.colorado.cires.argonaut.route.QueueConsts;
import edu.colorado.cires.argonaut.util.ArgonautFileUtils;
import java.nio.file.Files;
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
@MockEndpointsAndSkip(QueueConsts.SUBMISSION_COMPLETE_AGG)
public class SubmissionReportTest {

  static {
    System.setProperty("camel.threads.virtual.enabled", "true");
  }

  @EndpointInject("mock:" + QueueConsts.SUBMISSION_COMPLETE_AGG)
  private MockEndpoint submissionCompleteAgg;

  @Autowired
  private ServiceProperties serviceProperties;

  @Autowired
  private ProducerTemplate producerTemplate;

  @Test
  public void testReportHappyPath() throws Exception {

    String timestamp = Instant.now().toString();
    String floatId = "1901830";
    String dac = "aoml";
    String fileName = "1901830_meta.nc";
    String fileName2 = "1901830_tech.nc";

    NcSubmissionMessage message = new NcSubmissionMessage();
    message.setFloatId(floatId);
    message.setDac(dac);
    message.setTimestamp(timestamp);
    message.setFileName(fileName);
    message.setProfile(false);

    NcSubmissionMessage message2 = new NcSubmissionMessage();
    message2.setFloatId(floatId);
    message2.setDac(dac);
    message2.setTimestamp(timestamp);
    message2.setFileName(fileName2);
    message2.setProfile(false);

    FileTestUtils.emptyDirectory(ArgonautFileUtils.getSubmissionProcessedDirForDac(serviceProperties, dac));

    submissionCompleteAgg.expectedMessageCount(2);

    producerTemplate.sendBody(QueueConsts.FILE_MOVED, message);
    producerTemplate.sendBody(QueueConsts.FILE_MOVED, message2);

    submissionCompleteAgg.assertIsSatisfied();

    String expectedCsv = timestamp + ",aoml,1901830,1901830_meta.nc,success\n" + timestamp + ",aoml,1901830,1901830_tech.nc,success\n";
    String actualCsv = Files.readString(ArgonautFileUtils.getSubmissionProcessedDirForDac(serviceProperties, dac).resolve("submission_report.csv"));

    assertEquals(expectedCsv, actualCsv);

  }


}
