package edu.colorado.cires.argonaut.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import ucar.nc2.NetcdfFile;
import ucar.nc2.NetcdfFiles;
import ucar.nc2.Variable;

@Disabled
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

    assertTrue(Files.exists(outputFile));
    try (NetcdfFile ncfile = NetcdfFiles.open(outputFile.toString())) {

      Variable cycleNumbers = ncfile.findVariable("CYCLE_NUMBER");
      assertTrue(Arrays.equals(new int[]{3}, cycleNumbers.getShape()));
      assertEquals(3, cycleNumbers.read().getSize());
      assertEquals(1, cycleNumbers.read().getInt(0));

      Variable platformNumber = ncfile.findVariable(ProfileNcConsts.PLATFORM_NUMBER);
      assertTrue(Arrays.equals(new int[]{3, ProfileNcConsts.STRING8}, platformNumber.getShape()));
      assertEquals("2901615 ,2901615 ,2901615 ", platformNumber.read(":,:").toString());
      assertEquals( "Argo China SOA                                                  ", ncfile.findVariable("PROJECT_NAME").read("0,:").toString());
      assertEquals("Fengying JI                                                     ", ncfile.findVariable("PI_NAME").read("1,:").toString());
      Variable temp = ncfile.findVariable(ProfileNcConsts.STATION_PARAMETERS);
      assertEquals("PRES            ,TEMP            ,PSAL            ", ncfile.findVariable(ProfileNcConsts.STATION_PARAMETERS).read("0,:,:").toString());
    }


  }
}