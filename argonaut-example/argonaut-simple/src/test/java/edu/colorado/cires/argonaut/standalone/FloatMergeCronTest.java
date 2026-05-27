package edu.colorado.cires.argonaut.standalone;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.colorado.cires.argonaut.metadata.jpa.entity.ProfileFileEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
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
import org.springframework.test.context.TestPropertySource;
import tools.jackson.databind.json.JsonMapper;

@CamelSpringTest
@TestPropertySource
@ContextConfiguration({"FloatMergeCronTest.xml"})
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class FloatMergeCronTest {

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


  private static final Path medsProcessingDir = processingDir.resolve("dac/meds");
  private static final Path submissionMedsDir = submissionDir.resolve("dac/meds");
  private static final Path submitDir = submissionDir.resolve("dac/meds/submit");

  private static final Path submissionProcessingDir = submissionMedsDir.resolve("processing");
  private static final Path submissionProcessedDir = submissionMedsDir.resolve("processed");
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

    if (Files.exists(medsProcessingDir)) {
      try (Stream<Path> stream = Files.list(medsProcessingDir)) {
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
  public void testSubmitAndCreateMultiProf() throws Exception {
    List<String> fileNames = Arrays.asList("R4902704_001.nc",
        "R4902704_001D.nc",
        "R4902704_002.nc",
        "R4902704_003.nc",
        "R4902704_004.nc",
        "R4902704_005.nc",
        "R4902704_006.nc",
        "R4902704_007.nc",
        "R4902704_008.nc",
        "R4902704_009.nc",
        "R4902704_010.nc",
        "R4902704_011.nc",
        "R4902704_012.nc",
        "R4902704_013.nc",
        "R4902704_014.nc",
        "R4902704_015.nc",
        "R4902704_016.nc",
        "R4902704_017.nc",
        "R4902704_018.nc",
        "R4902704_019.nc",
        "R4902704_020.nc",
        "R4902704_021.nc",
        "R4902704_022.nc",
        "R4902704_023.nc",
        "R4902704_024.nc",
        "R4902704_025.nc",
        "R4902704_026.nc",
        "R4902704_027.nc",
        "R4902704_028.nc",
        "R4902704_029.nc",
        "R4902704_030.nc",
        "R4902704_031.nc",
        "R4902704_032.nc",
        "R4902704_033.nc",
        "R4902704_034.nc",
        "R4902704_035.nc",
        "R4902704_036.nc",
        "R4902704_054.nc",
        "R4902704_055.nc",
        "R4902704_056.nc",
        "R4902704_057.nc",
        "R4902704_058.nc",
        "R4902704_059.nc",
        "R4902704_060.nc",
        "R4902704_061.nc",
        "R4902704_062.nc",
        "R4902704_063.nc",
        "R4902704_064.nc",
        "R4902704_065.nc",
        "R4902704_066.nc",
        "R4902704_067.nc",
        "R4902704_068.nc",
        "R4902704_069.nc",
        "R4902704_070.nc",
        "R4902704_071.nc",
        "R4902704_072.nc",
        "R4902704_073.nc",
        "R4902704_074.nc",
        "R4902704_075.nc",
        "R4902704_076.nc",
        "R4902704_077.nc",
        "R4902704_078.nc",
        "R4902704_079.nc",
        "R4902704_080.nc",
        "R4902704_081.nc",
        "R4902704_082.nc",
        "R4902704_083.nc",
        "R4902704_084.nc",
        "R4902704_085.nc",
        "R4902704_086.nc",
        "R4902704_087.nc",
        "R4902704_088.nc",
        "R4902704_089.nc",
        "R4902704_090.nc",
        "R4902704_091.nc",
        "R4902704_092.nc",
        "R4902704_093.nc",
        "R4902704_094.nc",
        "R4902704_095.nc",
        "R4902704_096.nc",
        "R4902704_097.nc",
        "R4902704_098.nc",
        "R4902704_099.nc",
        "R4902704_105.nc",
        "R4902704_130.nc");

    // copy before moving to prevent state where file is picked up halfway
    for (String fileName : fileNames) {
      Path copyFile = submissionDir.resolve(fileName);
      Path submittedFile = submitDir.resolve(fileName);
      Files.copy(Paths.get("src/test/resources/meds/4902704/profiles").resolve(fileName), copyFile);
      Files.move(copyFile, submittedFile);
    }

    await().pollInterval(Duration.ofSeconds(10)).atMost(Duration.ofMinutes(4)).untilAsserted(() -> {

      fileNames.stream().map(fileName -> "meds/4902704/profiles/" + fileName).forEach(path -> {
        try (EntityManager em = entityManagerFactory.createEntityManager()) {
          ProfileFileEntity profile = em.find(ProfileFileEntity.class, path);
          assertNotNull(profile, "missing " + path);
          assertNotNull(profile.getMultiFloatMergeTime(), "missing merge time " + path);
          assertNotNull(profile.getCycle().getFloatId().getProfileMerge());
        }
      });
    });

    assertTrue(Files.exists(outputDir.resolve("dac/meds/4902704/4902704_prof.nc")));

    fileNames.stream().map(fileName -> outputDir.resolve("dac/meds/4902704/profiles/").resolve(fileName)).forEach(path -> {
      assertTrue(Files.exists(path));
    });
  }

}
