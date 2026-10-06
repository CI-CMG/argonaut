package edu.colorado.cires.argonaut.standalone;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.persistence.EntityManagerFactory;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;
import java.util.stream.Stream;
import org.apache.camel.test.spring.junit5.CamelSpringTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import tools.jackson.databind.json.JsonMapper;

@CamelSpringTest
@ContextConfiguration({"NotificationTest.xml"})
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class NotificationTest {

  static {
    System.setProperty("camel.threads.virtual.enabled", "true");
  }

  @Autowired
  @Qualifier("jsonMapper")
  private JsonMapper jsonMapper;

  @Autowired
  @Qualifier("entityManagerFactory")
  private EntityManagerFactory entityManagerFactory;

  @Autowired
  @Qualifier("auditEntityManagerFactory")
  private EntityManagerFactory auditEntityManagerFactory;


  @BeforeEach
  @AfterEach
  public void setup() throws Exception {
    TestDataContext.clear(entityManagerFactory, auditEntityManagerFactory);
  }

  @Test
  public void testNcSubmission() throws Exception {
    Files.createDirectories(TestDataContext.workDir.resolve("temp"));

    String fileName = "D7900664_315.nc";

    // copy before moving to prevent state where file is picked up halfway
    Path tempFile = TestDataContext.workDir.resolve("temp").resolve(fileName);
    Files.copy(Paths.get("src/test/resources/dac/aoml/7900664/profiles").resolve(fileName), tempFile);
    Files.move(tempFile, TestDataContext.submissionDacDir.resolve("aoml").resolve("submit").resolve(fileName));

    await().pollInterval(Duration.ofSeconds(5)).atMost(Duration.ofMinutes(4)).untilAsserted(() -> {
      assertTrue(Files.exists(TestDataContext.outputDir.resolve("dac/aoml/7900664/profiles").resolve(fileName)));
      assertTrue(Files.exists(TestDataContext.outputDir.resolve("report/aoml")));
      Path report;
      try(Stream<Path> files = Files.list(TestDataContext.outputDir.resolve("report/aoml"))) {
        report = files.filter(Files::isRegularFile).filter(path -> path.getFileName().toString().endsWith("_report.html")).findFirst().orElse(null);
      }
      assertNotNull(report);
      assertTrue(Files.readString(report, StandardCharsets.UTF_8).contains("SUBMISSION_SUCCESS"));
    });
  }

  @Test
  public void testNcSubmissionFailure() throws Exception {
    Files.createDirectories(TestDataContext.workDir.resolve("temp"));

    String fileName = "D7900664_315.nc";

    // copy before moving to prevent state where file is picked up halfway
    Path tempFile = TestDataContext.workDir.resolve("temp").resolve(fileName);
    Files.copy(Paths.get("src/test/resources/dac/aoml/7900664/profiles").resolve(fileName), tempFile);
    Files.move(tempFile, TestDataContext.submissionDacDir.resolve("aoml").resolve("submit").resolve(fileName));

    await().pollInterval(Duration.ofSeconds(5)).atMost(Duration.ofMinutes(4)).untilAsserted(() -> {
      assertTrue(Files.exists(TestDataContext.outputDir.resolve("dac/aoml/7900664/profiles").resolve(fileName)));
      assertTrue(Files.exists(TestDataContext.outputDir.resolve("report/aoml")));
      Path report;
      try(Stream<Path> files = Files.list(TestDataContext.outputDir.resolve("report/aoml"))) {
        report = files.filter(Files::isRegularFile).filter(path -> path.getFileName().toString().endsWith("_report.html")).findFirst().orElse(null);
      }
      assertNotNull(report);
      assertTrue(Files.readString(report, StandardCharsets.UTF_8).contains("SUBMISSION_SUCCESS"));
    });
  }



  @Test
  public void testTarGzSubmissionWithFailure() throws Exception {
    Files.createDirectories(TestDataContext.workDir.resolve("temp"));

    String fileName = "nc_2025.04.16_05.01_w_bad.tar.gz";

    // copy before moving to prevent state where file is picked up halfway
    Path tempFile = TestDataContext.workDir.resolve("temp").resolve(fileName);
    Files.copy(Paths.get("src/test/resources/aoml").resolve(fileName), tempFile);
    Files.move(tempFile, TestDataContext.submissionDacDir.resolve("aoml").resolve("submit").resolve(fileName));

    await().pollInterval(Duration.ofSeconds(5)).atMost(Duration.ofMinutes(1)).untilAsserted(() -> {
          assertTrue(Files.exists(TestDataContext.outputDir.resolve("dac/aoml/1902264/profiles/R1902264_173.nc")));
          assertTrue(Files.exists(TestDataContext.outputDir.resolve("dac/aoml/4903218/profiles/R4903218_229.nc")));
          assertTrue(Files.exists(TestDataContext.outputDir.resolve("dac/aoml/4903353/profiles/R4903353_302.nc")));
          assertTrue(Files.exists(TestDataContext.outputDir.resolve("dac/aoml/4903554/profiles/R4903554_141.nc")));
          assertTrue(Files.exists(TestDataContext.outputDir.resolve("dac/aoml/5904629/profiles/R5904629_350.nc")));
          assertTrue(Files.exists(TestDataContext.outputDir.resolve("dac/aoml/7900846/profiles/R7900846_082.nc")));
          assertTrue(Files.exists(TestDataContext.outputDir.resolve("dac/aoml/1902264/profiles/R1902264_174.nc")));
          assertTrue(Files.exists(TestDataContext.outputDir.resolve("dac/aoml/4903220/profiles/R4903220_228.nc")));
          assertTrue(Files.exists(TestDataContext.outputDir.resolve("dac/aoml/4903390/profiles/R4903390_130.nc")));
          assertTrue(Files.exists(TestDataContext.outputDir.resolve("dac/aoml/4903554/profiles/R4903554_142.nc")));
          assertTrue(Files.exists(TestDataContext.outputDir.resolve("dac/aoml/5905644/profiles/R5905644_241.nc")));
          assertTrue(Files.exists(TestDataContext.outputDir.resolve("dac/aoml/7900846/profiles/R7900846_083.nc")));
          assertTrue(Files.exists(TestDataContext.outputDir.resolve("dac/aoml/3902270/profiles/R3902270_175.nc")));
          assertTrue(Files.exists(TestDataContext.outputDir.resolve("dac/aoml/4903220/profiles/R4903220_229.nc")));
          assertTrue(Files.exists(TestDataContext.outputDir.resolve("dac/aoml/4903410/profiles/R4903410_154.nc")));
          assertTrue(Files.exists(TestDataContext.outputDir.resolve("dac/aoml/5902483/profiles/R5902483_313.nc")));
          assertTrue(Files.exists(TestDataContext.outputDir.resolve("dac/aoml/5905716/profiles/R5905716_244.nc")));
          assertTrue(Files.exists(TestDataContext.outputDir.resolve("dac/aoml/4903218/profiles/R4903218_228.nc")));
          assertTrue(Files.exists(TestDataContext.outputDir.resolve("dac/aoml/4903353/profiles/R4903353_301.nc")));
          assertTrue(Files.exists(TestDataContext.outputDir.resolve("dac/aoml/4903410/profiles/R4903410_155.nc")));
          assertTrue(Files.exists(TestDataContext.outputDir.resolve("dac/aoml/5902483/profiles/R5902483_314.nc")));
          assertFalse(Files.exists(TestDataContext.outputDir.resolve("dac/aoml/5905716/profiles/R5905716_245.nc")));
    });

    await().pollInterval(Duration.ofSeconds(5)).atMost(Duration.ofMinutes(4)).untilAsserted(() -> {
      assertTrue(Files.exists(TestDataContext.outputDir.resolve("report/aoml")));
      List<Path> reports;
      try(Stream<Path> files = Files.list(TestDataContext.outputDir.resolve("report/aoml"))) {
        reports = files.filter(Files::isRegularFile).filter(path -> path.getFileName().toString().endsWith("_report.html")).toList();
      }
      String concatReport = "";
      for (Path report: reports) {
        concatReport = concatReport + Files.readString(report, StandardCharsets.UTF_8);
      }
      assertTrue(concatReport.contains("SUBMISSION_SUCCESS"));
      assertTrue(concatReport.contains("SUBMISSION_FAILURE"));
    });
  }

  @Test
  public void testRemoval() throws Exception {

    Files.createDirectories(TestDataContext.workDir.resolve("temp"));

    String fileName = "D7900664_315.nc";

    // copy before moving to prevent state where file is picked up halfway
    Path tempFile = TestDataContext.workDir.resolve("temp").resolve(fileName);
    Files.copy(Paths.get("src/test/resources/dac/aoml/7900664/profiles").resolve(fileName), tempFile);
    Files.move(tempFile, TestDataContext.submissionDacDir.resolve("aoml").resolve("submit").resolve(fileName));

    await().pollInterval(Duration.ofSeconds(5)).atMost(Duration.ofMinutes(1)).untilAsserted(() -> {
      assertTrue(Files.exists(TestDataContext.outputDir.resolve("dac/aoml/7900664/profiles").resolve(fileName)));
    });

    // copy before moving to prevent state where file is picked up halfway
    tempFile = TestDataContext.workDir.resolve("temp").resolve("aoml_removal.txt");
    Files.copy(Paths.get("src/test/resources/aoml_removal.txt"), tempFile);
    Files.move(tempFile, TestDataContext.submissionDacDir.resolve("aoml/submit/aoml_removal.txt"));

    await().pollInterval(Duration.ofSeconds(5)).atMost(Duration.ofMinutes(4)).untilAsserted(() -> {
      assertFalse(Files.exists(TestDataContext.outputDir.resolve("dac/aoml/7900664/profiles").resolve(fileName)));
      assertTrue(Files.exists(TestDataContext.outputDir.resolve("report/aoml")));
      List<Path> reports;
      try(Stream<Path> files = Files.list(TestDataContext.outputDir.resolve("report/aoml"))) {
        reports = files.filter(Files::isRegularFile).filter(path -> path.getFileName().toString().endsWith("_report.html")).toList();
      }
      String concatReport = "";
      for (Path report: reports) {
        concatReport = concatReport + Files.readString(report, StandardCharsets.UTF_8);
      }
      assertTrue(concatReport.contains("SUBMISSION_SUCCESS"));
      assertTrue(concatReport.contains("REMOVAL_SUCCESS"));
    });
  }

}
