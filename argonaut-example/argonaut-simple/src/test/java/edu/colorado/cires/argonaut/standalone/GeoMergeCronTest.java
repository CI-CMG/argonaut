package edu.colorado.cires.argonaut.standalone;

import static edu.colorado.cires.argonaut.core.util.NetCdfWriteUtils.REFERENCE_DATE;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoProfileV31;
import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoProfileV31Calibration;
import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoProfileV31Level;
import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoProfileV31Reader;
import edu.colorado.cires.argonaut.metadata.jpa.entity.ProfileFileEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;
import org.apache.camel.test.spring.junit5.CamelSpringTest;
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
@ContextConfiguration({"GeoMergeCronTest.xml"})
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class GeoMergeCronTest {

  @Autowired
  @Qualifier("jsonMapper")
  private JsonMapper jsonMapper;

  @Autowired
  @Qualifier("entityManagerFactory")
  private EntityManagerFactory entityManagerFactory;

  private static final Path processingDir = Paths.get("processing");
  private static final Path workDir = Paths.get("work");
  private static final Path submissionDir = Paths.get("submission");
  private static final Path outputDir = Paths.get("output");

  private static final Path processingDacDir = processingDir.resolve("dac");
  private static final Path submissionDacDir = submissionDir.resolve("dac");

  private static final Instant timestamp = LocalDateTime.of(2026, 2, 20, 1, 2, 3).atZone(ZoneId.of("UTC")).toInstant();


  @BeforeEach
  public void setup() throws Exception {

    try (EntityManager em = entityManagerFactory.createEntityManager()) {
      EntityTransaction tx = em.getTransaction();
      tx.begin();
      try {
        em.createQuery("delete from ProfileFileEntity").executeUpdate();
        em.createQuery("delete from ProfileMergeFileEntity ").executeUpdate();
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

  private record Submission (String dac, String floatId, String direction, String cycle) {}


  private static final Map<String, String> DAC_MAP;

  static {
    Map<String, String> map = new HashMap<>();
    map.put("AO", "aoml");
    map.put("IN", "incois");
    map.put("CS", "csiro");
    map.put("IF", "coriolis");
    map.put("HZ", "csio");
    map.put("JA", "jma");
    map.put("BO", "bodc");
    DAC_MAP = Collections.unmodifiableMap(map);
  }


  @Test
  public void testSubmitAndCreateMultiProf() throws Exception {

    List<Submission> submissions = Arrays.asList(
        new Submission("IN", "2902200",	"A",	"251"),
        new Submission("AO", "7900664",	"A",	"315"),
        new Submission("AO", "5902500",	"A",	"226"),
        new Submission("AO", "1902216",	"A",	"226"),
        new Submission("AO", "5906002",	"A",	"148"),
        new Submission("CS", "5905172",	"A",	"232"),
        new Submission("AO", "1902037",	"A",	"148"),
        new Submission("CS", "7900917",	"A",	"069"),
        new Submission("IF", "6901982",	"A",	"296"),
        new Submission("AO", "1902202",	"A",	"153"),
        new Submission("AO", "4903028",	"A",	"153"),
        new Submission("IF", "6904069",	"A",	"069"),
        new Submission("AO", "5905770",	"A",	"157"),
        new Submission("IF", "7900513",	"A",	"149"),
        new Submission("IN", "2902270",	"A",	"144"),
        new Submission("AO", "1902192",	"A",	"173"),
        new Submission("IF", "6903151",	"A",	"005"),
        new Submission("AO", "5906142",	"A",	"092"),
        new Submission("IN", "2902219",	"A",	"220"),
        new Submission("AO", "5904722",	"A",	"248"),
        new Submission("HZ", "2902766",	"A",	"109"),
        new Submission("IN", "2902264",	"A",	"258"),
        new Submission("AO", "1902201",	"A",	"166"),
        new Submission("AO", "3902280",	"A",	"039"),
        new Submission("AO", "5906032",	"A",	"134"),
        new Submission("AO", "5904813",	"A",	"228"),
        new Submission("AO", "5906290",	"A",	"087"),
        new Submission("AO", "5906036",	"A",	"139"),
        new Submission("IF", "6903058",	"A",	"307"),
        new Submission("IF", "6903062",	"A",	"307"),
        new Submission("IF", "6903063",	"A",	"307"),
        new Submission("CS", "7900904",	"A",	"224"),
        new Submission("IF", "6903008",	"A",	"307"),
        new Submission("IN", "2902297",	"A",	"105"),
        new Submission("IF", "6903046",	"A",	"307"),
        new Submission("AO", "5904831",	"A",	"226"),
        new Submission("IN", "2902290",	"A",	"125"),
        new Submission("IN", "2902303",	"A",	"104"),
        new Submission("CS", "5905170",	"A",	"238"),
        new Submission("AO", "3901829",	"A",	"195"),
        new Submission("IN", "2902188",	"A",	"268"),
        new Submission("HZ", "2902774",	"A",	"112"),
        new Submission("HZ", "2902778",	"A",	"111"),
        new Submission("JA", "1902335",	"A",	"129"),
        new Submission("IF", "3902011",	"A",	"164"),
        new Submission("AO", "1902028",	"A",	"239"),
        new Submission("IF", "6903045",	"A",	"076"),
        new Submission("CS", "5905498",	"A",	"030"),
        new Submission("AO", "2903142",	"A",	"007"),
        new Submission("IF", "7900514",	"A",	"149"),
        new Submission("IF", "7900576",	"A",	"075"),
        new Submission("AO", "1901701",	"A",	"336"),
        new Submission("HZ", "2902775",	"A",	"148"),
        new Submission("CS", "5905472",	"A",	"090"),
        new Submission("CS", "5905531",	"A",	"018"),
        new Submission("CS", "5905212",	"A",	"189"),
        new Submission("CS", "5905177",	"A",	"231"),
        new Submission("JA", "1902334",	"A",	"112"),
        new Submission("AO", "5902521",	"D",	"168"),
        new Submission("CS", "5905532",	"A",	"005"),
        new Submission("BO", "1901918",	"A",	"022"),
        new Submission("AO", "5905088",	"A",	"215"),
        new Submission("AO", "1902264",	"A",	"090"),
        new Submission("IN", "2902198",	"A",	"251"),
        new Submission("AO", "5906147",	"A",	"088"),

        // should not be in merged file
        new Submission("AO", "5906287",	"A",	"086"),
        new Submission("IF", "6903271",	"A",	"383")
    );

    Collections.shuffle(submissions);

    Files.createDirectories(workDir.resolve("temp"));

    Set<Path> submittedFiles = new TreeSet<>();

    // copy before moving to prevent state where file is picked up halfway
    for (Submission submission : submissions) {
      Path file1 = Paths.get("src/test/resources/dac").resolve(DAC_MAP.get(submission.dac())).resolve(submission.floatId()).resolve("profiles").resolve("R" + submission.floatId() + "_" + submission.cycle() + (submission.direction().equals("D") ? "D" : "") + ".nc");
      Path file2 = Paths.get("src/test/resources/dac").resolve(DAC_MAP.get(submission.dac())).resolve(submission.floatId()).resolve("profiles").resolve("D" + submission.floatId() + "_" + submission.cycle() + (submission.direction().equals("D") ? "D" : "") + ".nc");
      Path file = Files.exists(file1) ? file1 : file2;
      Path tempFile = workDir.resolve("temp").resolve(file.getFileName());
      Files.copy(file, tempFile);
      Files.move(tempFile, submissionDacDir.resolve(DAC_MAP.get(submission.dac())).resolve("submit").resolve(file.getFileName()));
      submittedFiles.add(Paths.get(DAC_MAP.get(submission.dac())).resolve(submission.floatId()).resolve("profiles").resolve(file.getFileName()));
    }


    await().pollInterval(Duration.ofSeconds(10)).atMost(Duration.ofMinutes(4)).untilAsserted(() -> {

      submittedFiles.stream().map(Path::toString).forEach(path -> {
        try (EntityManager em = entityManagerFactory.createEntityManager()) {
          ProfileFileEntity profile = em.find(ProfileFileEntity.class, path);
          assertNotNull(profile, "missing " + path);
          assertNotNull(profile.getGeoMergeTime(), "missing merge time " + path);
        }
      });
    });

    Path merged = outputDir.resolve("geo/indian_ocean/2023/01/20230109_prof.nc");
    assertTrue(Files.exists(merged));

    submittedFiles.stream().map(file -> outputDir.resolve("dac").resolve(file)).forEach(path -> {
      assertTrue(Files.exists(path));
    });

    assertFilesEqual(Paths.get("src/test/resources/geo/indian_ocean/2023/01/20230109_prof.nc"), merged);




  }


  private static class ProfileSorter implements Comparable<ProfileSorter> {

    private final ArgoProfileV31 profile;
    private final String dac;
    private final int cycle;
    private final String floatId;
    private final String direction;

    private ProfileSorter(ArgoProfileV31 profile) {
      this.profile = profile;
      dac = DAC_MAP.get(profile.getDataCenter());
      cycle = profile.getCycleNumber();
      floatId = profile.getPlatformNumber();
      direction = profile.getDirection();
    }

    @Override
    public int compareTo(ProfileSorter o2) {
      if (dac.equals(o2.dac) ) {
        if (floatId.equals(o2.floatId) ) {
          if (cycle == o2.cycle) {
            return o2.direction.compareTo(direction);
          } else {
            return Integer.compare(cycle, o2.cycle);
          }
        } else {
          return Long.compare(Long.parseLong(floatId), Long.parseLong(o2.floatId));
        }
      } else {
        return dac.compareTo(o2.dac);
      }
    }
  }

  private void assertFilesEqual(Path expectedFile, Path mergedFile) throws IOException {

    try (
        ArgoProfileV31Reader reader = new ArgoProfileV31Reader(mergedFile);
        ArgoProfileV31Reader expectedReader = new ArgoProfileV31Reader(expectedFile);
    ) {
      List<ArgoProfileV31> profiles = reader.getMultiProfile().getProfiles();
      List<ArgoProfileV31> expectedProfiles = expectedReader.getMultiProfile().getProfiles();
      assertEquals(expectedProfiles.size(), profiles.size());
      List<ProfileSorter> expectedSorters = new ArrayList<>();
      for (int q = 0; q < expectedProfiles.size(); q++) {
        ArgoProfileV31 expectedProfile = expectedProfiles.get(q);
        expectedSorters.add(new ProfileSorter(expectedProfile));
      }
      Collections.sort(expectedSorters);
      for (int q = 0; q < expectedProfiles.size(); q++) {
        ArgoProfileV31 profile = profiles.get(q);
        ArgoProfileV31 expectedProfile = expectedSorters.get(q).profile;

        assertEquals(profile.getProfileIndex(), profile.getProfileIndex());
        assertEquals(REFERENCE_DATE, profile.getReferenceDateTime());
        assertNotNull(profile.getDateCreation());
        assertNotNull(profile.getDateUpdate());
        assertTrue(profile.getProfileHistory().isEmpty());
        assertEquals(expectedProfile.getCycleNumber(), profile.getCycleNumber());
        assertEquals(expectedProfile.getDirection(), profile.getDirection());
        assertEquals(expectedProfile.getDataMode(), profile.getDataMode());
        assertTrue(Math.abs(expectedProfile.getJulianDate().toEpochMilli() - profile.getJulianDate().toEpochMilli()) < 1000);
        assertEquals(expectedProfile.getJulianDateQc(), profile.getJulianDateQc());
        assertTrue(Math.abs(expectedProfile.getJulianDateOfLocation().toEpochMilli() - profile.getJulianDateOfLocation().toEpochMilli()) < 1000);
        if (expectedProfile.getLatitude() == null) {
          assertNull(profile.getLatitude());
        } else {
          assertEquals(expectedProfile.getLatitude(), profile.getLatitude(), 0.0000001);
        }
        if (expectedProfile.getLongitude() == null) {
          assertNull(profile.getLongitude());
        } else {
          assertEquals(expectedProfile.getLongitude(), profile.getLongitude(), 0.0000001);
        }
        assertEquals(expectedProfile.getPositionQc(), profile.getPositionQc());
        assertEquals(expectedProfile.getParameter("PRES").getQc(), profile.getParameter("PRES").getQc());
        assertEquals(expectedProfile.getParameter("TEMP").getQc(), profile.getParameter("TEMP").getQc());
        assertEquals(expectedProfile.getParameter("PSAL").getQc(), profile.getParameter("PSAL").getQc());
        assertEquals(expectedProfile.getConfigMissionNumber(), profile.getConfigMissionNumber());

        for (String parameterName : Arrays.asList("PRES", "TEMP", "PSAL")) {
          List<ArgoProfileV31Calibration> calibrations = profile.getParameter(parameterName).getCalibrations();
          List<ArgoProfileV31Calibration> expectedCalibrations = expectedProfile.getParameter(parameterName).getCalibrations();
          assertEquals(expectedCalibrations.size(), calibrations.size());
          for (int c = 0; c < calibrations.size(); c++) {
            ArgoProfileV31Calibration calibration = calibrations.get(c);
            ArgoProfileV31Calibration expectedCalibration = expectedCalibrations.get(c);
            assertEquals(expectedCalibration.getDate(), calibration.getDate());
            assertEquals(expectedCalibration.getParameterName(), calibration.getParameterName());
            assertEquals(expectedCalibration.getEquation(), calibration.getEquation());
            assertEquals(expectedCalibration.getCoefficient(), calibration.getCoefficient());
            assertEquals(expectedCalibration.getComment(), calibration.getComment());
          }

          List<ArgoProfileV31Level> levels = profile.getParameter(parameterName).getLevels();
          List<ArgoProfileV31Level> expectedlevels = expectedProfile.getParameter(parameterName).getLevels();
          assertEquals(expectedlevels.size(), levels.size());
          for (int l = 0; l < levels.size(); l++) {
            ArgoProfileV31Level level = levels.get(l);
            ArgoProfileV31Level expectedLevel = expectedlevels.get(l);

            Float expectedValue = expectedLevel.getValue();
            if (expectedValue != null) {
              assertEquals(expectedValue, level.getValue(), 0.001f);
            } else {
              assertNull(level.getValue());
            }

            assertEquals(expectedLevel.getQc(), level.getQc());

            Float expectedAdjustedValue = expectedLevel.getAdjustedValue();
            if (expectedAdjustedValue != null) {
              assertEquals(expectedAdjustedValue, level.getAdjustedValue(), 0.001f);
            } else {
              assertNull(level.getAdjustedValue());
            }

            assertEquals(expectedLevel.getAdjustedQc(), level.getAdjustedQc());

            Float expectedAdjustedErrorValue = expectedLevel.getAdjustedErrorValue();
            if (expectedAdjustedErrorValue != null) {
              assertEquals(expectedAdjustedErrorValue, level.getAdjustedErrorValue(), 0.001f);
            } else {
              assertNull(level.getAdjustedErrorValue());
            }
          }
        }


        assertEquals(new HashSet<>(expectedProfile.getStationParameters()), new HashSet<>(profile.getStationParameters()));
        assertEquals(expectedProfile.getDataCenter(), profile.getDataCenter());
        assertEquals(expectedProfile.getVerticalSamplingScheme(), profile.getVerticalSamplingScheme());
        assertEquals(expectedProfile.getDataCenterReference(), profile.getDataCenterReference());
        assertEquals(expectedProfile.getPlatformType(), profile.getPlatformType());
        assertEquals(expectedProfile.getFloatSerialNumber(), profile.getFloatSerialNumber());
        assertEquals(expectedProfile.getFirmwareVersion(), profile.getFirmwareVersion());
        assertEquals(expectedProfile.getDataStateIndicator(), profile.getDataStateIndicator());
        assertEquals(expectedProfile.getWmoInstrumentType(), profile.getWmoInstrumentType());
        assertEquals(expectedProfile.getProjectName(), profile.getProjectName());
        assertEquals(expectedProfile.getPrincipalInvestigatorName(), profile.getPrincipalInvestigatorName());
        assertEquals(expectedProfile.getPlatformNumber(), profile.getPlatformNumber());
        assertEquals(expectedProfile.getPositioningSystem(), profile.getPositioningSystem());
        assertEquals(expectedProfile.getDataType(), profile.getDataType());
        assertEquals(expectedProfile.getFormatVersion(), profile.getFormatVersion());
        assertEquals(expectedProfile.getHandbookVersion(), profile.getHandbookVersion());

      }

    }

  }

}
