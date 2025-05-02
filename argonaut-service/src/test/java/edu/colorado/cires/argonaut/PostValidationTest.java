package edu.colorado.cires.argonaut;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.colorado.cires.argonaut.config.ServiceProperties;
import edu.colorado.cires.argonaut.message.NcSubmissionMessage;
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
@MockEndpointsAndSkip(QueueConsts.FILE_MOVED)
public class PostValidationTest {

  static {
    System.setProperty("camel.threads.virtual.enabled", "true");
  }

  @EndpointInject("mock:" + QueueConsts.FILE_MOVED)
  private MockEndpoint fileMoved;

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

    producerTemplate.sendBody(QueueConsts.VALIDATION_SUCCESS, message);

    fileMoved.assertIsSatisfied();

    assertFalse(Files.exists(processingFile));
    Path outputFile = ArgonautFileUtils.getOutputProfileDir(serviceProperties, dac, floatId, false).resolve(fileName);
    assertTrue(Files.exists(outputFile));

  }


}
