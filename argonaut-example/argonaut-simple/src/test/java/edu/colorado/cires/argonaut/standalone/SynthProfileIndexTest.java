package edu.colorado.cires.argonaut.standalone;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import java.util.zip.GZIPInputStream;
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
@ContextConfiguration({"SynthProfileIndexTest.xml"})
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class SynthProfileIndexTest {

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

  // Actual values from French GDAC
  // bodc/6901174/profiles/SD6901174_143.nc,20160728134500,13.687,-28.076,A,836,BO,PRES TEMP PSAL DOXY DOWN_IRRADIANCE380 DOWN_IRRADIANCE412 DOWN_IRRADIANCE490 DOWNWELLING_PAR CHLA CHLA_FLUORESCENCE BBP700 BBP532,DDDDRRRRAAAR,20260703132654
  // coriolis/6901486/profiles/SD6901486_037.nc,20130924134100,63.508,-36.430,A,836,IF,PRES TEMP PSAL DOXY DOWN_IRRADIANCE380 DOWN_IRRADIANCE412 DOWN_IRRADIANCE490 DOWNWELLING_PAR CHLA CHLA_FLUORESCENCE BBP700 CDOM,DDDDDDDDAADR,20260809072735
  // jma/2900460/profiles/SR2900460_069.nc,20060621072220,43.934,-164.325,P,846,JA,PRES TEMP PSAL DOXY,AAAA,20220922134023
  @Test
  public void testCreateIndex() throws Exception {

    Files.createDirectories(TestDataContext.workDir.resolve("temp"));

    //TODO need to fix synth merge for first 2 examples
    List<Path> submissions = Arrays.asList(
//        Paths.get("src/test/resources/dac/bodc/6901174/profiles/D6901174_143.nc"),
//        Paths.get("src/test/resources/dac/bodc/6901174/profiles/BD6901174_143.nc"),
//        Paths.get("src/test/resources/dac/bodc/6901174/6901174_meta.nc"),
//        Paths.get("src/test/resources/dac/coriolis/6901486/profiles/D6901486_037.nc"),
//        Paths.get("src/test/resources/dac/coriolis/6901486/profiles/BD6901486_037.nc"),
//        Paths.get("src/test/resources/dac/coriolis/6901486/6901486_meta.nc"),
        Paths.get("src/test/resources/dac/jma/2900460/profiles/R2900460_069.nc"),
        Paths.get("src/test/resources/dac/jma/2900460/profiles/BR2900460_069.nc"),
        Paths.get("src/test/resources/dac/jma/2900460/2900460_meta.nc")
    );

    // copy before moving to prevent state where file is picked up halfway
    for (Path file : submissions) {
      Path tempFile = TestDataContext.workDir.resolve("temp").resolve(file.getFileName());
      Files.copy(file, tempFile);
      String dac = file.getName(4).toString();
      Files.move(tempFile, TestDataContext.submissionDacDir.resolve(dac).resolve("submit").resolve(file.getFileName()));
    }

    // note: using dummy value for date_update since this is generated
    List<List<String>> expectedLines = Arrays.asList(
        Arrays.asList("file", "date", "latitude", "longitude", "ocean", "profiler_type", "institution", "parameters", "parameter_data_mode", "date_update"),
//        Arrays.asList("bodc/6901174/profiles/SD6901174_143.nc", "20160728134500", "13.687", "-28.076", "A", "836", "BO", "PRES TEMP PSAL DOXY DOWN_IRRADIANCE380 DOWN_IRRADIANCE412 DOWN_IRRADIANCE490 DOWNWELLING_PAR CHLA CHLA_FLUORESCENCE BBP700 BBP532", "DDDDRRRRAAAR", "20260703132654"),
//        Arrays.asList("coriolis/6901486/profiles/SD6901486_037.nc", "20130924134100", "63.508", "-36.430", "A", "836", "IF", "PRES TEMP PSAL DOXY DOWN_IRRADIANCE380 DOWN_IRRADIANCE412 DOWN_IRRADIANCE490 DOWNWELLING_PAR CHLA CHLA_FLUORESCENCE BBP700 CDOM", "DDDDDDDDAADR", "20260809072735"),
        Arrays.asList("jma/2900460/profiles/SR2900460_069.nc", "20060621072219", "43.934", "-164.325", "A" , "846", "JA", "PRES TEMP PSAL DOXY", "AAAA", "date_update_value")
    );

    await().pollInterval(Duration.ofSeconds(10)).atMost(Duration.ofMinutes(5)).untilAsserted(() -> {

//      assertTrue(Files.exists(outputDir.resolve("dac/bodc/6901174/profiles/SD6901174_143.nc")));
//      assertTrue(Files.exists(outputDir.resolve("dac/coriolis/6901486/profiles/SD6901486_037.nc")));
      assertTrue(Files.exists(TestDataContext.outputDir.resolve("dac/jma/2900460/profiles/SR2900460_069.nc")));

    });

    await().pollInterval(Duration.ofSeconds(10)).atMost(Duration.ofMinutes(3)).untilAsserted(() -> {

      Path textFile = TestDataContext.outputDir.resolve("argo_synthetic-profile_index.txt");
      Path gzFile = TestDataContext.outputDir.resolve("argo_synthetic-profile_index.txt.gz");

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
          String value = record.get(i);
          if (i == 9) {
            if (value.equals("date_update")) {
              line.add(value);
            } else {
              line.add("date_update_value");
            }
          } else {
            line.add(value);
          }
        }
        lines.add(line);
      }
    }
    return lines;
  }

}
