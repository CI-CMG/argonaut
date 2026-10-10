package edu.colorado.cires.argonaut.standalone;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;
import org.apache.commons.io.FileUtils;

public final class TestDataContext {

  private TestDataContext() {

  }

  public static final Path processingDir = Paths.get("processing");
  public static final Path workDir = Paths.get("work");
  public static final Path submissionDir = Paths.get("submission");
  public static final Path outputDir = Paths.get("output");
  public static final Path processingDacDir = processingDir.resolve("dac");
  public static final Path submissionDacDir = submissionDir.resolve("dac");

  public static void clear(EntityManagerFactory entityManagerFactory, EntityManagerFactory auditEntityManagerFactory) throws IOException {
    try (EntityManager em = entityManagerFactory.createEntityManager()) {
      EntityTransaction tx = em.getTransaction();
      tx.begin();
      try {
        em.createQuery("delete from ArgoFloatProfileParameterEntity").executeUpdate();
        em.createQuery("delete from ArgonautFileRemovedTimeEntity").executeUpdate();
        em.createQuery("delete from ArgonautSyntheticMergeMetadataEntity").executeUpdate();
        em.createQuery("delete from ArgonautProfileMergeFileEntity").executeUpdate();
        em.createQuery("delete from ArgoFloatProfileEntity").executeUpdate();
        em.createQuery("delete from ArgoFloatMetadataEntity").executeUpdate();
        em.createQuery("delete from ArgoFloatCycleEntity").executeUpdate();
        em.createQuery("delete from ArgoFloatEntity").executeUpdate();
        em.createQuery("delete from ArgoDacEntity").executeUpdate();
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
        em.createQuery("delete from ArgonautAuditEntity ").executeUpdate();
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
}
