package edu.colorado.cires.argonaut.standalone;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.colorado.cires.argonaut.metadata.jpa.entity.ArgoFloatProfileEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.camel.test.spring.junit5.CamelSpringTest;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
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
@ContextConfiguration({"ProfileIndexTest.xml"})
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class ProfileIndexTest {

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
  public void testParamNullNullBug() throws Exception {

    Files.createDirectories(TestDataContext.workDir.resolve("temp"));

    List<Path> submissions = Arrays.asList(
        Paths.get("src/test/resources/dac/aoml/13857/profiles/D13857_002.nc")
    );

    // copy before moving to prevent state where file is picked up halfway
    for (Path file : submissions) {
      Path tempFile = TestDataContext.workDir.resolve("temp").resolve(file.getFileName());
      Files.copy(file, tempFile);
      String dac = file.getName(4).toString();
      Files.move(tempFile, TestDataContext.submissionDacDir.resolve(dac).resolve("submit").resolve(file.getFileName()));
    }

    String[] parameterDataMode = new String[1];
    await().pollInterval(Duration.ofSeconds(10)).atMost(Duration.ofMinutes(2)).untilAsserted(() -> {


      try(EntityManager em = entityManagerFactory.createEntityManager()) {
        List<ArgoFloatProfileEntity> profiles = em.createQuery("select p from ArgoFloatProfileEntity p", ArgoFloatProfileEntity.class)
            .getResultList();
        assertEquals(1, profiles.size());
        parameterDataMode[0] = profiles.get(0).getParameterDataMode();
      }
    });

    assertEquals("  ", parameterDataMode[0]);


  }

  private static List<List<String>> readCsvLines(InputStream inputStream) throws IOException {
    List<List<String>> lines = new ArrayList<>(3);
    try (CSVParser reader = CSVFormat.DEFAULT.builder().setCommentMarker('#').setSkipHeaderRecord(true).get()
        .parse(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
      for (CSVRecord record : reader) {
        List<String> line = new ArrayList<>(10);
        for (int i = 0; i < 10; i++) {
          line.add(record.get(i));
        }
        lines.add(line);
      }
    }
    return lines;
  }

}
