package edu.colorado.cires.argonaut.standalone;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.colorado.cires.argonaut.messaging.camel.ArgonautCamelMessageSender;
import edu.colorado.cires.argonaut.messaging.core.databind.NcSubmissionMessage;
import edu.colorado.cires.argonaut.messaging.core.databind.NcSubmissionMessage.FileType;
import edu.colorado.cires.argonaut.processor.core.ValidationProcessor;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.stream.Stream;
import org.apache.camel.EndpointInject;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringTest;
import org.apache.camel.test.spring.junit5.MockEndpointsAndSkip;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import tools.jackson.databind.json.JsonMapper;

@CamelSpringTest
@TestPropertySource
@ContextConfiguration({"PostValidationTest.xml"})
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@MockEndpointsAndSkip("seda:file-moved")
public class PostValidationTest {

  //TODO
//  static {
//    System.setProperty("camel.threads.virtual.enabled", "true");
//  }

  @EndpointInject("mock:seda:file-moved")
  private MockEndpoint fileMoved;

  @Autowired
  @Qualifier("validationProcessor")
  private ValidationProcessor validationProcessor;

  @Autowired
  @Qualifier("jsonMapper")
  private JsonMapper jsonMapper;

  @Autowired
  private ArgonautCamelMessageSender messageSender;


  private static final Path processingDir = Paths.get("processing");
  private static final Path workDir = Paths.get("work");
  private static final Path submissionDir = Paths.get("submission");
  private static final Path outputDir = Paths.get("output");


  private static final Path aomlProcessingDir = processingDir.resolve("dac/aoml");
  private static final Path submissionAomlDir = submissionDir.resolve("dac/aoml");
  private static final Path submitDir = submissionDir.resolve("dac/aoml/submit");

  private static final Path submissionProcessingDir = submissionAomlDir.resolve("processing");
  private static final Path submissionProcessedDir = submissionAomlDir.resolve("processed");
  private static Instant timestamp = LocalDateTime.of(2026, 2, 20, 1, 2, 3).atZone(ZoneId.of("UTC")).toInstant();


  @BeforeEach
  public void setup() throws Exception {
    Mockito.reset(validationProcessor);

    if (Files.exists(outputDir)) {
      try (Stream<Path> stream = Files.list(outputDir)) {
        stream.forEach(filedir -> {
          FileUtils.deleteQuietly(filedir.toFile());
        });
      }
    }

    if (Files.exists(workDir)) {
      try (Stream<Path> stream = Files.list(workDir)) {
        stream.forEach(filedir -> {
          FileUtils.deleteQuietly(filedir.toFile());
        });
      }
    }

    if (Files.exists(submitDir)) {
      try (Stream<Path> stream = Files.list(submitDir)) {
        stream.forEach(filedir -> {
          FileUtils.deleteQuietly(filedir.toFile());
        });
      }
    }

    if (Files.exists(submissionProcessingDir)) {
      try (Stream<Path> stream = Files.list(submissionProcessingDir)) {
        stream.forEach(filedir -> {
          FileUtils.deleteQuietly(filedir.toFile());
        });
      }
    }

    if (Files.exists(submissionProcessedDir)) {
      try (Stream<Path> stream = Files.list(submissionProcessedDir)) {
        stream.forEach(filedir -> {
          FileUtils.deleteQuietly(filedir.toFile());
        });
      }
    }

    if (Files.exists(aomlProcessingDir)) {
      try (Stream<Path> stream = Files.list(aomlProcessingDir)) {
        stream.forEach(filedir -> {
          FileUtils.deleteQuietly(filedir.toFile());
        });
      }
    }

  }

  @AfterEach
  public void cleanup() throws Exception {
    setup();
  }

  private static void unTarGz(Path tarGz, Path tempDir) throws IOException {
    tempDir = tempDir.normalize();
    try (InputStream inputStream = Files.newInputStream(tarGz)) {
      TarArchiveInputStream tar = new TarArchiveInputStream(new GzipCompressorInputStream(inputStream));
      ArchiveEntry entry;
      while ((entry = tar.getNextEntry()) != null) {
        Path extractTo = tempDir.resolve(entry.getName()).normalize();
        if (!extractTo.startsWith(tempDir)) {
          throw new IllegalArgumentException("Un tarring escaped destination location: " + tempDir + " - " + extractTo);
        }
        if (entry.isDirectory()) {
          Files.createDirectories(extractTo);
        } else {
          Files.copy(tar, extractTo);
        }
      }
    }
  }

  @Test
  public void testMoveToOutputNoProfiles() throws Exception {
    String[] files = new String[]{
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

    String fileName = "nc_2025.04.02_16.15.tar.gz";
    Path timeStampDir = submissionProcessedDir.resolve(timestamp.toString());
    Path submittedTarGz = timeStampDir.resolve(fileName);
    Files.createDirectories(timeStampDir);
    Files.copy(Paths.get("src/test/resources/aoml").resolve(fileName), submittedTarGz);
    unTarGz(submittedTarGz, timeStampDir);
    Path aomlProcessingTimestampDir = aomlProcessingDir.resolve("2026-02-20T01:02:03Z");

    fileMoved.expectedMessageCount(files.length);
    fileMoved.setAssertPeriod(500);

    for (String name : files) {
      Path floatDir = aomlProcessingTimestampDir.resolve(name.split("_")[0]);
      Files.createDirectories(floatDir);
      Files.move(timeStampDir.resolve(name), floatDir.resolve(name));
      messageSender.sendJson("seda:validation-success", jsonMapper.writeValueAsString(NcSubmissionMessage.builder()
          .withFileType(FileType.UNKNOWN)
          .withDac("aoml")
          .withFileName(name)
          .withTimestamp(timestamp)
          .withFloatId(floatDir.getFileName().toString())
          .withNumberOfFilesInSubmission(102)
          .build()));

    }

    fileMoved.assertIsSatisfied();

    for (String name : files) {
      String floatId = name.split("_")[0];
      Path processing = aomlProcessingTimestampDir.resolve(floatId).resolve(name);
      Path output = outputDir.resolve("dac").resolve("aoml").resolve(floatId).resolve(name);
      assertTrue(Files.isRegularFile(output), "File " + output + " not found");
      assertFalse(Files.exists(processing), "File " + processing + " exists");
    }
  }

  @Test
  public void testMoveToRejectNoProfiles() throws Exception {
    String[] files = new String[]{
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

    String fileName = "nc_2025.04.02_16.15.tar.gz";
    Path timeStampDir = submissionProcessedDir.resolve(timestamp.toString());
    Path submittedTarGz = timeStampDir.resolve(fileName);
    Files.createDirectories(timeStampDir);
    Files.copy(Paths.get("src/test/resources/aoml").resolve(fileName), submittedTarGz);
    unTarGz(submittedTarGz, timeStampDir);
    Path aomlProcessingTimestampDir = aomlProcessingDir.resolve("2026-02-20T01:02:03Z");

    fileMoved.expectedMessageCount(files.length);
    fileMoved.setAssertPeriod(500);

    for (String name : files) {
      Path floatDir = aomlProcessingTimestampDir.resolve(name.split("_")[0]);
      Files.createDirectories(floatDir);
      Files.move(timeStampDir.resolve(name), floatDir.resolve(name));
      messageSender.sendJson("seda:file-output", jsonMapper.writeValueAsString(NcSubmissionMessage.builder()
          .withFileType(FileType.UNKNOWN)
          .withDac("aoml")
          .withFileName(name)
          .withTimestamp(timestamp)
          .withFloatId(floatDir.getFileName().toString())
          .withNumberOfFilesInSubmission(102)
          .withValidationErrors(Collections.singletonList("test error"))
          .build()));

    }

    fileMoved.assertIsSatisfied();

    for (String name : files) {
      String floatId = name.split("_")[0];
      Path processing = aomlProcessingTimestampDir.resolve(floatId).resolve(name);
      Path output = submissionDir.resolve("dac").resolve("aoml").resolve("processed").resolve("2026-02-20T01:02:03Z").resolve("reject").resolve(floatId).resolve(name);
      assertTrue(Files.isRegularFile(output), "File " + output + " not found");
      assertFalse(Files.exists(processing), "File " + processing + " exists");
    }
  }

  @Test
  public void testMoveToOutputWithProfiles() throws Exception {
    String[] files = new String[]{
        "R1902264_173.nc", "R4903218_229.nc", "R4903353_302.nc", "R4903554_141.nc", "R5904629_350.nc", "R7900846_082.nc",
        "R1902264_174.nc", "R4903220_228.nc", "R4903390_130.nc", "R4903554_142.nc", "R5905644_241.nc", "R7900846_083.nc",
        "R3902270_175.nc", "R4903220_229.nc", "R4903410_154.nc", "R5902483_313.nc", "R5905716_244.nc",
        "R4903218_228.nc", "R4903353_301.nc", "R4903410_155.nc", "R5902483_314.nc", "R5905716_245.nc",
    };

    String fileName = "nc_2025.04.16_05.01_w_bad.tar.gz";
    Path timeStampDir = submissionProcessedDir.resolve(timestamp.toString());
    Path submittedTarGz = timeStampDir.resolve(fileName);
    Files.createDirectories(timeStampDir);
    Files.copy(Paths.get("src/test/resources/aoml").resolve(fileName), submittedTarGz);
    unTarGz(submittedTarGz, timeStampDir);
    Path aomlProcessingTimestampDir = aomlProcessingDir.resolve("2026-02-20T01:02:03Z");

    fileMoved.expectedMessageCount(files.length);
    fileMoved.setAssertPeriod(500);

    for (String name : files) {
      Path floatDir = aomlProcessingTimestampDir.resolve(name.split("_")[0]);
      Files.createDirectories(floatDir.resolve("profiles"));
      Files.move(timeStampDir.resolve(name), floatDir.resolve("profiles").resolve(name));
      messageSender.sendJson("seda:validation-success", jsonMapper.writeValueAsString(NcSubmissionMessage.builder()
          .withFileType(FileType.PROFILE)
          .withDac("aoml")
          .withFileName(name)
          .withTimestamp(timestamp)
          .withFloatId(floatDir.getFileName().toString())
          .withNumberOfFilesInSubmission(102)
          .build()));

    }

    fileMoved.assertIsSatisfied();

    for (String name : files) {
      String floatId = name.split("_")[0];
      Path processing = aomlProcessingTimestampDir.resolve(floatId).resolve("profiles").resolve(name);
      Path output = outputDir.resolve("dac").resolve("aoml").resolve(floatId).resolve("profiles").resolve(name);
      assertTrue(Files.isRegularFile(output), "File " + output + " not found");
      assertFalse(Files.exists(processing), "File " + processing + " exists");
    }
  }

  @Test
  public void testMoveToRejectWithProfiles() throws Exception {
    String[] files = new String[]{
        "R1902264_173.nc", "R4903218_229.nc", "R4903353_302.nc", "R4903554_141.nc", "R5904629_350.nc", "R7900846_082.nc",
        "R1902264_174.nc", "R4903220_228.nc", "R4903390_130.nc", "R4903554_142.nc", "R5905644_241.nc", "R7900846_083.nc",
        "R3902270_175.nc", "R4903220_229.nc", "R4903410_154.nc", "R5902483_313.nc", "R5905716_244.nc",
        "R4903218_228.nc", "R4903353_301.nc", "R4903410_155.nc", "R5902483_314.nc", "R5905716_245.nc",
    };

    String fileName = "nc_2025.04.16_05.01_w_bad.tar.gz";
    Path timeStampDir = submissionProcessedDir.resolve(timestamp.toString());
    Path submittedTarGz = timeStampDir.resolve(fileName);
    Files.createDirectories(timeStampDir);
    Files.copy(Paths.get("src/test/resources/aoml").resolve(fileName), submittedTarGz);
    unTarGz(submittedTarGz, timeStampDir);
    Path aomlProcessingTimestampDir = aomlProcessingDir.resolve("2026-02-20T01:02:03Z");

    fileMoved.expectedMessageCount(files.length);
    fileMoved.setAssertPeriod(500);

    for (String name : files) {
      Path floatDir = aomlProcessingTimestampDir.resolve(name.split("_")[0]);
      Files.createDirectories(floatDir.resolve("profiles"));
      Files.move(timeStampDir.resolve(name), floatDir.resolve("profiles").resolve(name));
      messageSender.sendJson("seda:file-output", jsonMapper.writeValueAsString(NcSubmissionMessage.builder()
          .withFileType(FileType.PROFILE)
          .withDac("aoml")
          .withFileName(name)
          .withTimestamp(timestamp)
          .withFloatId(floatDir.getFileName().toString())
          .withNumberOfFilesInSubmission(102)
          .withValidationErrors(Collections.singletonList("test error"))
          .build()));

    }

    fileMoved.assertIsSatisfied();

    for (String name : files) {
      String floatId = name.split("_")[0];
      Path processing = aomlProcessingTimestampDir.resolve(floatId).resolve("profiles").resolve(name);
      Path output = submissionDir.resolve("dac").resolve("aoml").resolve("processed").resolve("2026-02-20T01:02:03Z").resolve("reject").resolve(floatId).resolve("profiles").resolve(name);
      assertTrue(Files.isRegularFile(output), "File " + output + " not found");
      assertFalse(Files.exists(processing), "File " + processing + " exists");
    }
  }
}
