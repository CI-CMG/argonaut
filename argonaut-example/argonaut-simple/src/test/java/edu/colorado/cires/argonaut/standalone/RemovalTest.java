package edu.colorado.cires.argonaut.standalone;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.colorado.cires.argonaut.audit.jpa.entity.AuditEntity;
import edu.colorado.cires.argonaut.metadata.jpa.entity.ProfileFileEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
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
@ContextConfiguration({"RemovalTest.xml"})
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class RemovalTest {

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
  public void testValidRemoval() throws Exception {

    Files.createDirectories(workDir.resolve("temp"));

    List<Path> toBeRemoved = Arrays.asList(
        Paths.get("src/test/resources/dac/coriolis/6903062/profiles/D6903062_307.nc"),
        Paths.get("src/test/resources/dac/coriolis/6901982/profiles/D6901982_296.nc"),
        Paths.get("src/test/resources/dac/aoml/7900664/profiles/D7900664_315.nc")
    );

    List<Path> submissions = new ArrayList<>(Arrays.asList(
        Paths.get("src/test/resources/dac/bodc/1901918/profiles/R1901918_022.nc"),
        Paths.get("src/test/resources/dac/coriolis/6903151/profiles/R6903151_005.nc")
    ));

    submissions.addAll(toBeRemoved);

    // copy before moving to prevent state where file is picked up halfway
    for (Path file : submissions) {
      Path tempFile = workDir.resolve("temp").resolve(file.getFileName());
      Files.copy(file, tempFile);
      String dac = file.getName(4).toString();
      Files.move(tempFile, submissionDacDir.resolve(dac).resolve("submit").resolve(file.getFileName()));
    }

    await().pollInterval(Duration.ofSeconds(10)).atMost(Duration.ofMinutes(4)).untilAsserted(() -> {

      submissions.stream().map(path -> path.subpath(4, 8).toString()).forEach(path -> {
        try (EntityManager em = entityManagerFactory.createEntityManager()) {
          ProfileFileEntity profile = em.find(ProfileFileEntity.class, path);
          assertNotNull(profile, "missing " + path);
          assertEquals("ACTIVE", profile.getFileStatus());
        }
      });
    });

    submissions.stream()
        .map(path -> outputDir.resolve("dac").resolve(path.subpath(4, 8)))
        .forEach(path -> assertTrue(Files.exists(path)));

    List<Path> removalFiles = Arrays.asList(
        Paths.get("src/test/resources/aoml_removal.txt"),
        Paths.get("src/test/resources/coriolis_removal.txt")
    );
    // copy before moving to prevent state where file is picked up halfway
    for (Path file : removalFiles) {
      Path tempFile = workDir.resolve("temp").resolve(file.getFileName());
      Files.copy(file, tempFile);
      String dac = file.getFileName().toString().split("_")[0];
      Files.move(tempFile, submissionDacDir.resolve(dac).resolve("submit").resolve(file.getFileName()));
    }

    await().pollInterval(Duration.ofSeconds(10)).atMost(Duration.ofMinutes(4)).untilAsserted(() -> {

      submissions.stream().map(path -> path.subpath(4, 8).toString()).forEach(path -> {
        try (EntityManager em = entityManagerFactory.createEntityManager()) {
          ProfileFileEntity profile = em.find(ProfileFileEntity.class, path);
          assertNotNull(profile, "missing " + path);
          boolean removed = toBeRemoved.stream().map(p -> p.subpath(4, 8).toString()).collect(Collectors.toSet()).contains(path);
          assertEquals(removed ? "REMOVED" : "ACTIVE", profile.getFileStatus());
        }
      });
    });

    submissions
        .forEach(path -> {
          boolean removed = toBeRemoved.contains(path);
          String dac = path.getName(4).toString();
          assertEquals(!removed, Files.exists(outputDir.resolve("dac").resolve(path.subpath(4, 8))));
          assertEquals(removed, Files.exists(outputDir.resolve("etc").resolve("removed").resolve(dac).resolve(path.getFileName())));
        });

    assertFalse(Files.exists(submissionDir.resolve("dac/aoml/processed/2026-02-20T01:02:03Z/reject/aoml_removal.txt")));
    assertTrue(Files.exists(submissionDir.resolve("dac/aoml/processed/2026-02-20T01:02:03Z/aoml_removal.txt")));

  }


  @Test
  public void testInvalidRemoval() throws Exception {

    Files.createDirectories(workDir.resolve("temp"));

    List<Path> submissions = Arrays.asList(
        Paths.get("src/test/resources/dac/coriolis/6903062/profiles/D6903062_307.nc"),
        Paths.get("src/test/resources/dac/coriolis/6901982/profiles/D6901982_296.nc"),
        Paths.get("src/test/resources/dac/aoml/7900664/profiles/D7900664_315.nc"),
        Paths.get("src/test/resources/dac/bodc/1901918/profiles/R1901918_022.nc"),
        Paths.get("src/test/resources/dac/coriolis/6903151/profiles/R6903151_005.nc")
    );


    // copy before moving to prevent state where file is picked up halfway
    for (Path file : submissions) {
      Path tempFile = workDir.resolve("temp").resolve(file.getFileName());
      Files.copy(file, tempFile);
      String dac = file.getName(4).toString();
      Files.move(tempFile, submissionDacDir.resolve(dac).resolve("submit").resolve(file.getFileName()));
    }

    await().pollInterval(Duration.ofSeconds(10)).atMost(Duration.ofMinutes(4)).untilAsserted(() -> {

      submissions.stream().map(path -> path.subpath(4, 8).toString()).forEach(path -> {
        try (EntityManager em = entityManagerFactory.createEntityManager()) {
          ProfileFileEntity profile = em.find(ProfileFileEntity.class, path);
          assertNotNull(profile, "missing " + path);
          assertEquals("ACTIVE", profile.getFileStatus());
        }
      });
    });

    submissions.stream()
        .map(path -> outputDir.resolve("dac").resolve(path.subpath(4, 8)))
        .forEach(path -> assertTrue(Files.exists(path)));



    // wrong file name
    Path removalFile = Paths.get("src/test/resources/coriolis_removal.txt");

    // copy before moving to prevent state where file is picked up halfway
    Path tempFile = workDir.resolve("temp").resolve(removalFile.getFileName());
    Files.copy(removalFile, tempFile);
    Files.move(tempFile, submissionDacDir.resolve("aoml").resolve("submit").resolve(removalFile.getFileName()));


    await().pollInterval(Duration.ofSeconds(10)).atMost(Duration.ofMinutes(4)).untilAsserted(() -> {

      submissions.stream().map(path -> path.subpath(4, 8).toString()).forEach(path -> {
        try (EntityManager em = entityManagerFactory.createEntityManager()) {
          ProfileFileEntity profile = em.find(ProfileFileEntity.class, path);
          assertNotNull(profile, "missing " + path);
          assertEquals("ACTIVE", profile.getFileStatus());
        }
      });
    });

    submissions
        .forEach(path -> {
          String dac = path.getName(4).toString();
          assertTrue(Files.exists(outputDir.resolve("dac").resolve(path.subpath(4, 8))));
          assertFalse(Files.exists(outputDir.resolve("etc").resolve("removed").resolve(dac).resolve(path.getFileName())));
        });

    try (EntityManager em = auditEntityManagerFactory.createEntityManager()) {
      List<AuditEntity> audits = em.createQuery("select p from AuditEntity p where p.eventType = 'ERROR' and p.fileName = 'coriolis_removal.txt' and p.dac = 'aoml' ", AuditEntity.class).getResultList();
      assertEquals(1, audits.size());
      assertEquals("file name does not start with DAC identifier: 'coriolis_removal.txt', aoml", audits.get(0).getStackTrace());
    }

    assertTrue(Files.exists(submissionDir.resolve("dac/aoml/processed/2026-02-20T01:02:03Z/reject/coriolis_removal.txt")));
    assertFalse(Files.exists(submissionDir.resolve("dac/aoml/processed/2026-02-20T01:02:03Z/coriolis_removal.txt")));


  }

}
