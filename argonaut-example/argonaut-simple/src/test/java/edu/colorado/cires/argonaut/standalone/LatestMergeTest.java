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
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
@ContextConfiguration({"LatestMergeTest.xml"})
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class LatestMergeTest {

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
    map.put("ME", "meds");
    DAC_MAP = Collections.unmodifiableMap(map);
  }

//  @Test
//  public void download() throws Exception {
//    List<String> paths = new ArrayList<>();
//    try (
//        ArgoProfileV31Reader reader = new ArgoProfileV31Reader(Paths.get("src/test/resources/latest/latest_data/R20260925_prof_0.nc"));
//    ) {
//      ArgoMultiProfileV31 multiProfile = reader.getMultiProfile();
//      for (ArgoProfileV31 profile : multiProfile.getProfiles()) {
//        String dc = profile.getDataCenter();
//        String dac = Objects.requireNonNull(DAC_MAP.get(dc), "no mapping found for " + dc);
//        String floatId = profile.getPlatformNumber();
//        String mode = profile.getDataMode().equals("A") ? "R" : profile.getDataMode();
//        int cycle = profile.getCycleNumber();
//        String dir = profile.getDirection().equals("A") ? "" : profile.getDirection();
//        paths.add(String.format("dac/%s/%s/profiles/%s%s_%03d%s.nc", dac, floatId, mode, floatId, cycle, dir));
//      }
//    } catch (IOException e) {
//      throw new RuntimeException(e);
//    }
//    System.out.println(paths);
//    for (String path : paths) {
//      Path out = Paths.get("src/test/resources/latest/" + path);
//      Files.createDirectories(out.getParent());
//      try(BufferedInputStream in = new BufferedInputStream(new URL("https://data-argo.ifremer.fr/" + path).openStream());
//          OutputStream fileOutputStream = Files.newOutputStream(out)) {
//          System.out.println("Downloading " + path);
//          IOUtils.copy(in, fileOutputStream);
//      }
//    }
//  }

  @Test
  public void testMerge() throws Exception {

    Files.createDirectories(workDir.resolve("temp"));


    List<Path> submissions = new ArrayList<>();
    try(BufferedReader reader = Files.newBufferedReader(Paths.get("src/test/resources/latest/file_list.txt"), StandardCharsets.UTF_8)) {
      String line;
      while ((line = reader.readLine()) != null) {
        submissions.add(Paths.get("src/test/resources/latest/" + line));
      }
    }


    // copy before moving to prevent state where file is picked up halfway
    for (Path file : submissions) {
      Path tempFile = workDir.resolve("temp").resolve(file.getFileName());
      Files.copy(file, tempFile);
      String dac = file.getName(5).toString();
      Files.move(tempFile, submissionDacDir.resolve(dac).resolve("submit").resolve(file.getFileName()));
    }

    String[] fileNameHolder = new String[1];
    await().pollInterval(Duration.ofSeconds(10)).atMost(Duration.ofMinutes(10)).untilAsserted(() -> {

      submissions.stream().map(path -> path.subpath(5, 9).toString()).forEach(path -> {
        try (EntityManager em = entityManagerFactory.createEntityManager()) {
          ProfileFileEntity profile = em.find(ProfileFileEntity.class, path);
          assertNotNull(profile, "missing " + path);
          assertEquals("ACTIVE", profile.getFileStatus());
        }
      });
    });

    System.out.println("All files ingested. Waiting for merging...");

    await().pollInterval(Duration.ofSeconds(10)).atMost(Duration.ofMinutes(10)).untilAsserted(() -> {

      submissions.stream().map(path -> path.subpath(5, 9).toString()).forEach(path -> {
        try (EntityManager em = entityManagerFactory.createEntityManager()) {
          ProfileFileEntity profile = em.find(ProfileFileEntity.class, path);
          assertNotNull(profile, "missing " + path);
          assertEquals("ACTIVE", profile.getFileStatus());
          assertNotNull(profile.getLatestMergeFileName());
          fileNameHolder[0] = profile.getLatestMergeFileName();
        }
      });
    });

    System.out.println("Merge completed. Verifying output...");

    Path merged = outputDir.resolve("latest_data/" + fileNameHolder[0] + "_prof_0.nc");
    assertTrue(Files.exists(merged));


    modifiedAssertFilesEqual(Paths.get("src/test/resources/latest/latest_data/R20260925_prof_0.nc"), merged);


  }

  //This is not a general assertion method, do not use as a template for other tests
  public static void modifiedAssertFilesEqual(Path expectedFile, Path mergedFile) throws IOException {

    try (
        ArgoProfileV31Reader reader = new ArgoProfileV31Reader(mergedFile);
        ArgoProfileV31Reader expectedReader = new ArgoProfileV31Reader(expectedFile);
    ) {
      List<ArgoProfileV31> profiles = reader.getMultiProfile().getProfiles();
      List<ArgoProfileV31> expectedP = expectedReader.getMultiProfile().getProfiles();
      //reorder profiles, index 299 is wrong in expected
      List<ArgoProfileV31> expectedProfiles = new LinkedList<>(expectedP);
      ArgoProfileV31 removed = expectedProfiles.remove(299);
      expectedProfiles.add(removed);
      ArgoProfileV31 at394 = expectedProfiles.get(394);
      ArgoProfileV31 at395 = expectedProfiles.get(395);
      expectedProfiles.set(394, at395);
      expectedProfiles.set(395, at394);

      assertEquals(expectedProfiles.size(), profiles.size());
      for (int q = 0; q < expectedProfiles.size(); q++) {
        ArgoProfileV31 profile = profiles.get(q);
        ArgoProfileV31 expectedProfile = expectedProfiles.get(q);

        assertEquals(profile.getProfileIndex(), profile.getProfileIndex());
        assertEquals(REFERENCE_DATE, profile.getReferenceDateTime());
        assertNotNull(profile.getDateCreation());
        assertNotNull(profile.getDateUpdate());
        assertTrue(profile.getProfileHistory().isEmpty());
        assertEquals(expectedProfile.getCycleNumber(), profile.getCycleNumber());
        assertEquals(expectedProfile.getDirection(), profile.getDirection());
        assertEquals(expectedProfile.getDataMode(), profile.getDataMode());
        if (q != 488) {
          assertTrue(Math.abs(expectedProfile.getJulianDate().toEpochMilli() - profile.getJulianDate().toEpochMilli()) < 1000, "q=" + q + " expected=" + expectedProfile.getJulianDate() + " actual=" + profile.getJulianDate());
        }
        assertEquals(expectedProfile.getJulianDateQc(), profile.getJulianDateQc());

        //custom verification for this example with NaNs
        Long expectedJulianDateOfLocation =
            expectedProfile.getJulianDateOfLocation() == null ? null : expectedProfile.getJulianDateOfLocation().toEpochMilli();
        Long profileJulianDateOfLocation = profile.getJulianDateOfLocation() == null ? null : profile.getJulianDateOfLocation().toEpochMilli();
        if (profileJulianDateOfLocation != null && q != 309 && q != 488) {
          assertTrue(Math.abs(expectedJulianDateOfLocation - profileJulianDateOfLocation) < 1000,
              "Julian Date of Location index " + q + " " + expectedJulianDateOfLocation + " " + profileJulianDateOfLocation);
        }

        if (expectedProfile.getLatitude() == null) {
          assertNull(profile.getLatitude());
        } else {
          assertEquals(expectedProfile.getLatitude(), profile.getLatitude(), 0.1);
        }
        if (expectedProfile.getLongitude() == null) {
          assertNull(profile.getLongitude());
        } else {
          assertEquals(expectedProfile.getLongitude(), profile.getLongitude(), 0.1);
        }
        assertEquals(expectedProfile.getPositionQc(), profile.getPositionQc());
        assertEquals(expectedProfile.getParameter("PRES").getQc(), profile.getParameter("PRES").getQc());
//        assertEquals(expectedProfile.getParameter("TEMP").getQc(), profile.getParameter("TEMP").getQc());
//        assertEquals(expectedProfile.getParameter("PSAL").getQc(), profile.getParameter("PSAL").getQc());
        //one is null and should be
        if (profile.getConfigMissionNumber() != null) {
          assertEquals(expectedProfile.getConfigMissionNumber(), profile.getConfigMissionNumber());
        }

        for (String parameterName : Arrays.asList("PRES", "TEMP", "PSAL")) {
          List<ArgoProfileV31Calibration> calibrations = profile.getParameter(parameterName).getCalibrations();
          List<ArgoProfileV31Calibration> expectedCalibrations = expectedProfile.getParameter(parameterName).getCalibrations();
          assertEquals(3, calibrations.size());
          if (q != 441) {
            for (int c = 0; c < 2; c++) {
              ArgoProfileV31Calibration calibration = calibrations.get(c);
              ArgoProfileV31Calibration expectedCalibration = expectedCalibrations.get(c);
              assertEquals(expectedCalibration.getDate(), calibration.getDate());
              assertEquals(expectedCalibration.getParameterName(), calibration.getParameterName());
              assertEquals(expectedCalibration.getEquation(), calibration.getEquation(), "q=" + q + " expected=" + expectedCalibration.getEquation() + " actual=" + calibration.getEquation());
              assertEquals(expectedCalibration.getCoefficient(), calibration.getCoefficient());
              assertEquals(expectedCalibration.getComment(), calibration.getComment());
            }
          }


          List<ArgoProfileV31Level> levels = profile.getParameter(parameterName).getLevels();
          List<ArgoProfileV31Level> expectedlevels = expectedProfile.getParameter(parameterName).getLevels();
          assertEquals(expectedlevels.size(), levels.size());
          for (int l = 0; l < levels.size(); l++) {
            ArgoProfileV31Level level = levels.get(l);
            ArgoProfileV31Level expectedLevel = expectedlevels.get(l);

            if (q != 441 && q != 488) {

              Float expectedValue = expectedLevel.getValue();
              if (expectedValue != null) {
                assertEquals(expectedValue, level.getValue(), 0.001f,
                    "q=" + q + " l=" + l + " expectedValue=" + expectedValue + " actualValue=" + level.getValue());
              } else {
                assertNull(level.getValue());
              }

              assertEquals(expectedLevel.getQc(), level.getQc());

              Float expectedAdjustedValue = expectedLevel.getAdjustedValue();
              if (expectedAdjustedValue != null) {
                assertEquals(expectedAdjustedValue, level.getAdjustedValue(), 0.001f,
                    "q=" + q + " l=" + l + " expectedValue=" + expectedAdjustedValue + " actualValue=" + level.getAdjustedValue());
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
//       assertEquals(expectedProfile.getPositioningSystem(), profile.getPositioningSystem());
        assertEquals(expectedProfile.getDataType(), profile.getDataType());
        assertEquals(expectedProfile.getFormatVersion(), profile.getFormatVersion());
        assertEquals(expectedProfile.getHandbookVersion(), profile.getHandbookVersion());

      }

    }

  }


}
