package edu.colorado.cires.argonaut.processor.core;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.colorado.cires.argonaut.file.core.FileStore;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DefaultSubmissionProcessedCleanupProcessorTest {

  private static final Path submissionDir = Paths.get("target/submission");

  @BeforeEach
  @AfterEach
  public void setup() {
    FileUtils.deleteQuietly(submissionDir.toFile());
  }

  @Test
  public void testCleanup() throws Exception {
    Path aoml1 = submissionDir.resolve("dac/aoml/processed/2026-10-22T07:33:03.694766624Z/D1900427_008.nc");
    Path aoml2 = submissionDir.resolve("dac/aoml/processed/2026-10-22T07:33:03.694766624Z/D1900427_009.nc");
    Path aoml3 = submissionDir.resolve("dac/aoml/processed/2026-10-23T07:33:03.000Z/foo.tar.gz");
    Path aomlReject = submissionDir.resolve("dac/aoml/processed/2026-10-23T07:33:03.000Z/reject/D2000428_001.nc");
    Path aoml4 = submissionDir.resolve("dac/aoml/processed/2026-10-24T07:33:03.000Z/D2000427_001.nc");

    Path bodc1 = submissionDir.resolve("dac/bodc/processed/2026-10-22T07:33:03.694766624Z/D1900427_008.nc");

    Files.createDirectories(aoml1.getParent());
    Files.createDirectories(aoml2.getParent());
    Files.createDirectories(aoml3.getParent());
    Files.createDirectories(aoml4.getParent());
    Files.createDirectories(aomlReject.getParent());
    Files.createDirectories(bodc1.getParent());

    Files.createFile(aoml1);
    Files.createFile(aoml2);
    Files.createFile(aoml3);
    Files.createFile(aoml4);
    Files.createFile(aomlReject);
    Files.createFile(bodc1);

    FileStore fileStore = new TestFileStore(submissionDir);
    DefaultSubmissionProcessedCleanupProcessor processor = new DefaultSubmissionProcessedCleanupProcessor();
    processor.setSubmissionStore(fileStore);
    processor.setNowSupplier(() -> Instant.parse("2026-10-25T00:00:00.000Z"));

    processor.cleanup("aoml", 1);

    assertFalse(Files.exists(aoml1));
    assertFalse(Files.exists(aoml2));
    assertFalse(Files.exists(aoml3));
    assertTrue(Files.exists(aomlReject));
    assertTrue(Files.exists(aoml4));
    assertTrue(Files.exists(bodc1));


  }

  @Test
  public void testCleanupRejects() throws Exception {
    Path aoml1 = submissionDir.resolve("dac/aoml/processed/2026-10-22T07:33:03.694766624Z/D1900427_008.nc");
    Path aoml2 = submissionDir.resolve("dac/aoml/processed/2026-10-22T07:33:03.694766624Z/D1900427_009.nc");
    Path aoml3 = submissionDir.resolve("dac/aoml/processed/2026-10-23T07:33:03.000Z/foo.tar.gz");
    Path aomlReject = submissionDir.resolve("dac/aoml/processed/2026-10-23T07:33:03.000Z/reject/D2000428_001.nc");
    Path aoml4 = submissionDir.resolve("dac/aoml/processed/2026-10-24T07:33:03.000Z/D2000427_001.nc");
    Path aomlReject2 = submissionDir.resolve("dac/aoml/processed/2026-10-24T07:33:03.000Z/reject/D2000428_001.nc");

    Path bodc1 = submissionDir.resolve("dac/bodc/processed/2026-10-22T07:33:03.694766624Z/reject/D1900427_008.nc");

    Files.createDirectories(aoml1.getParent());
    Files.createDirectories(aoml2.getParent());
    Files.createDirectories(aoml3.getParent());
    Files.createDirectories(aoml4.getParent());
    Files.createDirectories(aomlReject.getParent());
    Files.createDirectories(aomlReject2.getParent());
    Files.createDirectories(bodc1.getParent());

    Files.createFile(aoml1);
    Files.createFile(aoml2);
    Files.createFile(aoml3);
    Files.createFile(aoml4);
    Files.createFile(aomlReject);
    Files.createFile(aomlReject2);
    Files.createFile(bodc1);

    FileStore fileStore = new TestFileStore(submissionDir);
    DefaultSubmissionProcessedCleanupProcessor processor = new DefaultSubmissionProcessedCleanupProcessor();
    processor.setSubmissionStore(fileStore);
    processor.setCleanRejects(true);
    processor.setNowSupplier(() -> Instant.parse("2026-10-25T00:00:00.000Z"));

    processor.cleanup("aoml", 1);

    assertTrue(Files.exists(aoml1));
    assertTrue(Files.exists(aoml2));
    assertTrue(Files.exists(aoml3));
    assertFalse(Files.exists(aomlReject));
    assertTrue(Files.exists(aomlReject2));
    assertTrue(Files.exists(aoml4));
    assertTrue(Files.exists(bodc1));


  }

  @Test
  public void testCleanupNoTime() throws Exception {
    Path aoml1 = submissionDir.resolve("dac/aoml/processed/2026-10-22T07:33:03.694766624Z/D1900427_008.nc");
    Path aoml2 = submissionDir.resolve("dac/aoml/processed/2026-10-22T07:33:03.694766624Z/D1900427_009.nc");
    Path aoml3 = submissionDir.resolve("dac/aoml/processed/2026-10-23T07:33:03.000Z/foo.tar.gz");
    Path aomlReject = submissionDir.resolve("dac/aoml/processed/2026-10-23T07:33:03.000Z/reject/D2000428_001.nc");
    Path aoml4 = submissionDir.resolve("dac/aoml/processed/2026-10-24T07:33:03.000Z/D2000427_001.nc");
    Path extra = submissionDir.resolve("dac/aoml/processed/meow/cats.jpg");

    Path bodc1 = submissionDir.resolve("dac/bodc/processed/2026-10-22T07:33:03.694766624Z/D1900427_008.nc");

    Files.createDirectories(aoml1.getParent());
    Files.createDirectories(aoml2.getParent());
    Files.createDirectories(aoml3.getParent());
    Files.createDirectories(aoml4.getParent());
    Files.createDirectories(aomlReject.getParent());
    Files.createDirectories(bodc1.getParent());
    Files.createDirectories(extra.getParent());

    Files.createFile(aoml1);
    Files.createFile(aoml2);
    Files.createFile(aoml3);
    Files.createFile(aoml4);
    Files.createFile(aomlReject);
    Files.createFile(bodc1);
    Files.createFile(extra);

    FileStore fileStore = new TestFileStore(submissionDir);
    DefaultSubmissionProcessedCleanupProcessor processor = new DefaultSubmissionProcessedCleanupProcessor();
    processor.setSubmissionStore(fileStore);
    processor.setNowSupplier(() -> Instant.parse("2026-10-25T00:00:00.000Z"));


    processor.cleanup("aoml", 0);

    assertFalse(Files.exists(aoml1));
    assertFalse(Files.exists(aoml2));
    assertFalse(Files.exists(aoml3));
    assertTrue(Files.exists(aomlReject));
    assertFalse(Files.exists(aoml4));
    assertTrue(Files.exists(bodc1));
    assertTrue(Files.exists(extra));


  }


  @Test
  public void testCleanupRejectsNoTime() throws Exception {
    Path aoml1 = submissionDir.resolve("dac/aoml/processed/2026-10-22T07:33:03.694766624Z/D1900427_008.nc");
    Path aoml2 = submissionDir.resolve("dac/aoml/processed/2026-10-22T07:33:03.694766624Z/D1900427_009.nc");
    Path aoml3 = submissionDir.resolve("dac/aoml/processed/2026-10-23T07:33:03.000Z/foo.tar.gz");
    Path aomlReject = submissionDir.resolve("dac/aoml/processed/2026-10-23T07:33:03.000Z/reject/D2000428_001.nc");
    Path aoml4 = submissionDir.resolve("dac/aoml/processed/2026-10-24T07:33:03.000Z/D2000427_001.nc");
    Path aomlReject2 = submissionDir.resolve("dac/aoml/processed/2026-10-24T07:33:03.000Z/reject/D2000428_001.nc");

    Path bodc1 = submissionDir.resolve("dac/bodc/processed/2026-10-22T07:33:03.694766624Z/reject/D1900427_008.nc");

    Files.createDirectories(aoml1.getParent());
    Files.createDirectories(aoml2.getParent());
    Files.createDirectories(aoml3.getParent());
    Files.createDirectories(aoml4.getParent());
    Files.createDirectories(aomlReject.getParent());
    Files.createDirectories(aomlReject2.getParent());
    Files.createDirectories(bodc1.getParent());

    Files.createFile(aoml1);
    Files.createFile(aoml2);
    Files.createFile(aoml3);
    Files.createFile(aoml4);
    Files.createFile(aomlReject);
    Files.createFile(aomlReject2);
    Files.createFile(bodc1);

    FileStore fileStore = new TestFileStore(submissionDir);
    DefaultSubmissionProcessedCleanupProcessor processor = new DefaultSubmissionProcessedCleanupProcessor();
    processor.setSubmissionStore(fileStore);
    processor.setCleanRejects(true);
    processor.setNowSupplier(() -> Instant.parse("2026-10-25T00:00:00.000Z"));


    processor.cleanup("aoml", 0);

    assertTrue(Files.exists(aoml1));
    assertTrue(Files.exists(aoml2));
    assertTrue(Files.exists(aoml3));
    assertFalse(Files.exists(aomlReject));
    assertFalse(Files.exists(aomlReject2));
    assertTrue(Files.exists(aoml4));
    assertTrue(Files.exists(bodc1));


  }
}