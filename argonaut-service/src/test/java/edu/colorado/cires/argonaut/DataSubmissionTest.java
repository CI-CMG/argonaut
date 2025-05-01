package edu.colorado.cires.argonaut;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import edu.colorado.cires.argonaut.message.HeaderConsts;
import edu.colorado.cires.argonaut.message.NcSubmissionMessage;
import edu.colorado.cires.argonaut.service.SubmissionTimestampService;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.MockEndpointsAndSkip;
import org.apache.commons.lang3.stream.Streams;
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
@MockEndpointsAndSkip("seda:validation-success|seda:validation-failure")
public class DataSubmissionTest {

  private static final String[] files = new String[]{
      "1901830_meta.nc",
      "1901830_Rtraj.nc",
      "1901830_tech.nc",
      "1901843_Rtraj.nc",
      "1901843_tech.nc",
      "1902195_meta.nc",
      "1902195_Rtraj.nc",
      "1902195_tech.nc",
      "3901276_Rtraj.nc",
      "3901276_tech.nc",
      "3901471_Rtraj.nc",
      "3901471_tech.nc",
      "3901480_Rtraj.nc",
      "3901480_tech.nc",
      "3902534_Rtraj.nc",
      "3902534_tech.nc",
      "4902337_meta.nc",
      "4902337_Rtraj.nc",
      "4902337_tech.nc",
      "4902349_meta.nc",
      "4902349_Rtraj.nc",
      "4902349_tech.nc",
      "4902907_meta.nc",
      "4902907_Rtraj.nc",
      "4902907_tech.nc",
      "4902951_meta.nc",
      "4902951_Rtraj.nc",
      "4902951_tech.nc",
      "4902997_meta.nc",
      "4902997_Rtraj.nc",
      "4902997_tech.nc",
      "4903000_meta.nc",
      "4903000_Rtraj.nc",
      "4903000_tech.nc",
      "4903180_meta.nc",
      "4903180_Rtraj.nc",
      "4903180_tech.nc",
      "5902487_Rtraj.nc",
      "5902487_tech.nc",
      "5902490_Rtraj.nc",
      "5902490_tech.nc",
      "5902499_Rtraj.nc",
      "5902499_tech.nc",
      "5904627_meta.nc",
      "5904627_Rtraj.nc",
      "5904627_tech.nc",
      "5904773_meta.nc",
      "5904773_Rtraj.nc",
      "5904773_tech.nc",
      "5904774_meta.nc",
      "5904774_Rtraj.nc",
      "5904774_tech.nc",
      "5904810_meta.nc",
      "5904810_Rtraj.nc",
      "5904810_tech.nc",
      "5904812_meta.nc",
      "5904812_Rtraj.nc",
      "5904812_tech.nc",
      "5904941_meta.nc",
      "5904941_Rtraj.nc",
      "5904941_tech.nc",
      "5905098_meta.nc",
      "5905098_Rtraj.nc",
      "5905098_tech.nc",
      "5905244_Rtraj.nc",
      "5905244_tech.nc",
      "5905248_Rtraj.nc",
      "5905248_tech.nc",
      "5905289_meta.nc",
      "5905289_Rtraj.nc",
      "5905289_tech.nc",
      "5905315_meta.nc",
      "5905315_Rtraj.nc",
      "5905315_tech.nc",
      "5905316_meta.nc",
      "5905316_Rtraj.nc",
      "5905316_tech.nc",
      "5905669_meta.nc",
      "5905669_Rtraj.nc",
      "5905669_tech.nc",
      "5905670_meta.nc",
      "5905670_Rtraj.nc",
      "5905670_tech.nc",
      "5905746_meta.nc",
      "5905746_Rtraj.nc",
      "5905746_tech.nc",
      "5906936_Rtraj.nc",
      "5906936_tech.nc",
      "5906945_Rtraj.nc",
      "5906945_tech.nc",
      "5906946_Rtraj.nc",
      "5906946_tech.nc",
      "5906947_Rtraj.nc",
      "5906947_tech.nc",
      "5907024_Rtraj.nc",
      "5907024_tech.nc",
      "7902059_meta.nc",
      "7902059_Rtraj.nc",
      "7902059_tech.nc",
      "7902143_meta.nc",
      "7902143_Rtraj.nc",
      "7902143_tech.nc"
  };

  static {
    System.setProperty("camel.threads.virtual.enabled", "true");
  }

  @EndpointInject("mock:seda:validation-success")
  private MockEndpoint validationSuccess;

  @EndpointInject("mock:seda:validation-failure")
  private MockEndpoint validationFailure;

  @MockitoBean
  private SubmissionTimestampService submissionTimestampService;


  @Autowired
  private ServiceProperties serviceProperties;

  @Test
  public void testSubmitDataHappyPathNoProfiles() throws Exception {

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

    String fileName = "nc_2025.04.02_16.15.tar.gz";
    Path submittedFile = submitDir.resolve(fileName);
    Path copyFile = aomlDir.resolve(fileName);

    validationSuccess.expectedMessageCount(102);
    validationSuccess.setAssertPeriod(2000);
    validationFailure.expectedMessageCount(0);
    validationFailure.setAssertPeriod(2000);

    // copy before moving to prevent state where file is picked up halfway
    Files.copy(Paths.get("src/test/resources/aoml").resolve(fileName), copyFile);
    Files.move(copyFile, submittedFile);

    MockEndpoint.assertIsSatisfied(60, TimeUnit.SECONDS, validationSuccess, validationFailure);
    Set<Path> processedFiles = new TreeSet<>();
    try (Stream<Path> stream = Files.walk(aomlProcessingDir)) {
      stream.filter(Files::isRegularFile).forEach(processedFiles::add);
    }
    Set<NcSubmissionMessage> validationMessages = new HashSet<>();
    Set<Path> expectedFiles = new TreeSet<>();
    Streams.of(files).forEach(name -> {
      Path floatDir = aomlProcessingDir.resolve(name.split("_")[0]);
      expectedFiles.add(floatDir.resolve(name));
      expectedFiles.add(floatDir.resolve(name + ".filecheck"));

      NcSubmissionMessage expectedMessage = new NcSubmissionMessage();
      expectedMessage.setProfile(false);
      expectedMessage.setDac("aoml");
      expectedMessage.setFileName(name);
      expectedMessage.setTimestamp(timestamp);
      expectedMessage.setFloatId(floatDir.getFileName().toString());

      validationMessages.add(expectedMessage);
    });

    Set<NcSubmissionMessage> receivedMessages = validationSuccess.getExchanges().stream()
        .map(exchange -> exchange.getIn().getBody(NcSubmissionMessage.class))
        .collect(Collectors.toSet());

    assertEquals(expectedFiles, processedFiles);
    assertEquals(validationMessages, receivedMessages);

    assertFalse(Files.exists(submittedFile));
    assertTrue(Files.exists(submissionProcessedDir.resolve(timestamp).resolve(fileName)));


  }


}
