package edu.colorado.cires.argonaut.standalone;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
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
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;
import org.apache.camel.test.spring.junit5.CamelSpringTest;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;
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
@ContextConfiguration({"BioProfileIndexTest.xml"})
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class BioProfileIndexTest {

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

  private static final Path processingDir = Paths.get("processing");
  private static final Path workDir = Paths.get("work");
  private static final Path submissionDir = Paths.get("submission");
  private static final Path outputDir = Paths.get("output");

  private static final Path processingDacDir = processingDir.resolve("dac");
  private static final Path submissionDacDir = submissionDir.resolve("dac");

  @BeforeEach
  public void setup() throws Exception {

    try (EntityManager em = entityManagerFactory.createEntityManager()) {
      EntityTransaction tx = em.getTransaction();
      tx.begin();
      try {
        em.createQuery("delete from ProfileParameterEntity").executeUpdate();
        em.createQuery("delete from FileRemovedTimeEntity").executeUpdate();
        em.createQuery("delete from MetadataSyntheticMergeEntity").executeUpdate();
        em.createQuery("delete from ProfileMergeFileEntity").executeUpdate();
        em.createQuery("delete from ProfileFileEntity").executeUpdate();
        em.createQuery("delete from MetadataFileEntity").executeUpdate();
        em.createQuery("delete from CycleEntity").executeUpdate();
        em.createQuery("delete from FloatEntity").executeUpdate();
        em.createQuery("delete from DacEntity").executeUpdate();
        tx.commit();
      } catch (Exception e) {
        tx.rollback();
        throw e;
      }
    }

    try (EntityManager em = auditEntityManagerFactory.createEntityManager()) {
      EntityTransaction tx = em.getTransaction();
      tx.begin();
      try {
        em.createQuery("delete from AuditEntity ").executeUpdate();
        tx.commit();
      } catch (Exception e) {
        tx.rollback();
        throw e;
      }
    }

    if (Files.exists(workDir)) {
      try (Stream<Path> stream = Files.list(workDir)) {
        stream.forEach(filedir -> {
          FileUtils.deleteQuietly(filedir.toFile());
        });
      }
    }

    if (Files.exists(outputDir)) {
      try (Stream<Path> stream = Files.list(outputDir)) {
        stream.forEach(filedir -> {
          FileUtils.deleteQuietly(filedir.toFile());
        });
      }
    }

    if (Files.exists(submissionDacDir)) {
      List<Path> dacs;
      try (Stream<Path> stream = Files.list(submissionDacDir)) {
        dacs = stream.filter(Files::isDirectory).toList();
      }
      for (Path dac : dacs) {
        Path submit = dac.resolve("submit");
        if (Files.exists(submit)) {
          try (Stream<Path> stream = Files.list(submit)) {
            stream.forEach(filedir -> {
              FileUtils.deleteQuietly(filedir.toFile());
            });
          }
        }
        Path processed = dac.resolve("processed");
        Path processing = dac.resolve("processing");
        FileUtils.deleteQuietly(processed.toFile());
        FileUtils.deleteQuietly(processing.toFile());
      }

    }

    if (Files.exists(processingDacDir)) {
      try (Stream<Path> stream = Files.list(processingDacDir)) {
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


  @Test
  public void testCreateIndex() throws Exception {

    Files.createDirectories(workDir.resolve("temp"));

    List<Path> submissions = Arrays.asList(
        Paths.get("src/test/resources/dac/aoml/5906002/profiles/BD5906002_148.nc"),
        Paths.get("src/test/resources/dac/coriolis/3902011/profiles/BD3902011_164.nc"),
        Paths.get("src/test/resources/dac/incois/2902290/profiles/D2902290_125.nc")
    );

    // copy before moving to prevent state where file is picked up halfway
    for (Path file : submissions) {
      Path tempFile = workDir.resolve("temp").resolve(file.getFileName());
      Files.copy(file, tempFile);
      String dac = file.getName(4).toString();
      Files.move(tempFile, submissionDacDir.resolve(dac).resolve("submit").resolve(file.getFileName()));
    }

    List<List<String>> expectedLines = Arrays.asList(
        Arrays.asList("file", "date", "latitude", "longitude", "ocean", "profiler_type", "institution", "parameters", "parameter_data_mode",
            "date_update"),
        Arrays.asList("aoml/5906002/profiles/BD5906002_148.nc", "20230109220733", "-56.818", "110.499", "A", "846", "AO",
            "PRES TEMP_DOXY TPHASE_DOXY DOXY FLUORESCENCE_CHLA CHLA CHLA_FLUORESCENCE BETA_BACKSCATTERING700 BBP700 VRS_PH IB_PH IK_PH VK_PH PH_IN_SITU_FREE PH_IN_SITU_TOTAL UV_INTENSITY_DARK_NITRATE UV_INTENSITY_NITRATE NITRATE",
            "RRRDRAARARRRRRDRRD", "20260625145457"),
        Arrays.asList("coriolis/3902011/profiles/BD3902011_164.nc", "20230109104129", "-39.907", "26.108", "A", "844", "IF",
            "PRES C1PHASE_DOXY C2PHASE_DOXY TEMP_DOXY DOXY", "RRRRD", "20250903104136")
    );

    await().pollInterval(Duration.ofSeconds(10)).atMost(Duration.ofMinutes(2)).untilAsserted(() -> {

      Path textFile = outputDir.resolve("argo_bio-profile_index.txt");
      Path gzFile = outputDir.resolve("argo_bio-profile_index.txt.gz");

      assertTrue(Files.exists(textFile));
      assertTrue(Files.exists(gzFile));

      List<List<String>> lines;
      try (InputStream in = Files.newInputStream(textFile)) {
        lines = readCsvLines(in);
      }
      assertEquals(expectedLines, lines);

      try (InputStream in = new GZIPInputStream(Files.newInputStream(gzFile))) {
        lines = readCsvLines(in);
      }
      assertEquals(expectedLines, lines);
    });


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
