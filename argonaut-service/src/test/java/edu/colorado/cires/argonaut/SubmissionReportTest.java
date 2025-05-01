package edu.colorado.cires.argonaut;

import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.colorado.cires.argonaut.message.NcSubmissionMessage;
import edu.colorado.cires.argonaut.route.QueueConsts;
import edu.colorado.cires.argonaut.util.ArgonautFileUtils;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
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
    String dac = "aoml";
    Instant timestamp = Instant.now();

    Map<Path, Set<String>> expected = new TreeMap<>();
    Set<NcSubmissionMessage> messages = new HashSet<>();

    for (int i = 0; i < 10; i++) {
      String timestampStr = timestamp.minusSeconds(i).toString();
      String floatId = "190183" + i;

      for (String fileName : Arrays.asList(floatId + "_meta.nc", floatId + "_tech.nc", floatId + "_Rtraj.nc")) {
        NcSubmissionMessage message = new NcSubmissionMessage();
        message.setFloatId(floatId);
        message.setDac(dac);
        message.setTimestamp(timestampStr);
        message.setFileName(fileName);
        message.setProfile(false);

        messages.add(message);

        Path path = ArgonautFileUtils.getSubmissionProcessedDirForDac(serviceProperties, dac).resolve(timestampStr).resolve("submission_report.csv");
        Set<String> rows = expected.get(path);
        if(rows == null) {
          rows = new TreeSet<>();
          expected.put(path, rows);
        }
        rows.add(timestampStr + "," + dac + "," + floatId + "," + fileName + ",success");
      }
    }

    FileTestUtils.emptyDirectory(ArgonautFileUtils.getSubmissionProcessedDirForDac(serviceProperties, dac));

    submissionCompleteAgg.expectedMessageCount(messages.size());

    messages.forEach(message -> producerTemplate.sendBody(QueueConsts.FILE_MOVED, message));

    submissionCompleteAgg.assertIsSatisfied();

    for (Entry<Path, Set<String>> entry : expected.entrySet()) {
      Set<String> actualCsv = new TreeSet<>(Files.readAllLines(entry.getKey()));
      assertEquals(entry.getValue(), actualCsv);
    }

  }


}
