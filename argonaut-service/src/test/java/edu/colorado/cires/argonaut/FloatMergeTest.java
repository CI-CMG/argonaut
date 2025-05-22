package edu.colorado.cires.argonaut;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.colorado.cires.argonaut.config.ServiceProperties;
import edu.colorado.cires.argonaut.message.NcSubmissionMessage;
import edu.colorado.cires.argonaut.processor.FloatMergeProcessor;
import edu.colorado.cires.argonaut.route.QueueConsts;
import edu.colorado.cires.argonaut.util.ArgonautFileUtils;
import java.nio.file.Path;
import java.nio.file.Paths;
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
public class FloatMergeTest {

  static {
    System.setProperty("camel.threads.virtual.enabled", "true");
  }

  @EndpointInject("mock:" + QueueConsts.UPDATE_INDEX_AGG)
  private MockEndpoint updateIndexAgg;

  @Autowired
  private ProducerTemplate producerTemplate;

  @Autowired
  private ServiceProperties serviceProperties;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  public void testFloatMergeAggregator() throws Exception {
    String[] files = new String[]{"badfile_00.nc", "D2901615_001.nc", "D2901615_002.nc", "D2901615_003.nc", "D2901615_004.nc"};
    String timestamp = Instant.now().toString();
    String floatId = "2901616";
    String dac = "aoml";

    NcSubmissionMessage message = NcSubmissionMessage.builder()
        .withFloatId(floatId)
        .withDac(dac)
        .withTimestamp(timestamp)
        .withProfile(true)
        .build();

    Path outputProfileDir = ArgonautFileUtils.getOutputProfileDir(serviceProperties, dac, floatId, true);
    FileTestUtils.emptyDirectory(outputProfileDir);

    Path resourceDir = Paths.get("src/test/resources/float_merge/nmdis/2901615/profiles");

    for (String fileName : files) {
      Path testFile = resourceDir.resolve(fileName);
      Path outPutFile = outputProfileDir.resolve(fileName);
      ArgonautFileUtils.copy(testFile, outPutFile);
    }
    updateIndexAgg.expectedMessageCount(1);

    producerTemplate.sendBody(QueueConsts.FLOAT_MERGE, objectMapper.writeValueAsString(message));

    updateIndexAgg.assertIsSatisfied();

    NcSubmissionMessage actual = updateIndexAgg.getExchanges().stream()
        .map(e -> e.getIn().getBody(String.class))
        .map(body -> {
          try {
            return objectMapper.readValue(body, NcSubmissionMessage.class);
          } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
          }
        })
        .findFirst().orElse(null);
    assertEquals(message, actual);

  }
}
