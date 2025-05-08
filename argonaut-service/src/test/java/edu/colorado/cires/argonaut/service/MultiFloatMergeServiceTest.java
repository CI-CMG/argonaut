package edu.colorado.cires.argonaut.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.TreeSet;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MultiFloatMergeServiceTest {

  private static final Path submissionDir = Paths.get("target/submission");

  @BeforeEach
  public void before() throws Exception {
    FileUtils.deleteQuietly(submissionDir.toFile());
    Files.createDirectories(submissionDir);
  }

  @AfterEach
  public void after() throws Exception {
//    FileUtils.deleteQuietly(dacDir.toFile());
  }

  @Test
  public void smokeTest() throws Exception {
    String[] files = new String[]{"D2901615_001.nc", "D2901615_002.nc", "D2901615_003.nc"};
    Path daceDir = submissionDir.resolve("2901615");
    Path profileDir = daceDir.resolve("profiles");
    Path resourceDir = Paths.get("src/test/resources/float_merge/nmdis/2901615/profiles");

    Files.createDirectories(profileDir);
    Set<Path> profileNcFiles = new TreeSet<>();
    for (String fileName: files) {
      Path profileFile = profileDir.resolve(fileName);
      profileNcFiles.add(profileFile);
      Files.copy(resourceDir.resolve(fileName), profileFile);
    }

    Path outputFile = daceDir.resolve("2901615_prof.nc");
    MultiFloatMergeService floatMergeService = new MultiFloatMergeService();
    floatMergeService.mergeFloats(outputFile, profileNcFiles);
//    String fileName = "nc_2025.04.02_16.15.tar.gz";
//    Path stagedFile = stagingDir.resolve(fileName);
//    Path readyFile = stagingDir.resolve("nc_2025.04.02_16.15.tar.gz.ready");
//    Files.copy(Paths.get("src/test/resources/aoml").resolve(fileName), stagedFile);

//    AomlProcessor processor = new AomlProcessor(serviceProperties);
//    Exchange exchange = mock(Exchange.class);
//    Message message = mock(Message.class);
//    when(message.getBody(File.class)).thenReturn(readyFile.toFile());
//    when(exchange.getIn()).thenReturn(message);
//    processor.process(exchange);

  }
}