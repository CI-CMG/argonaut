package edu.colorado.cires.argonaut.core.util;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import ucar.nc2.NetcdfFile;
import ucar.nc2.NetcdfFiles;

class NetCdfReadUtilsTest {

  private static final Path TEST_FILE = Paths.get("src/test/resources/dac/aoml/5903712/5903712_Rtraj.nc");


  @Test
  void getLevel1Instant() throws IOException {
    try (NetcdfFile file = NetcdfFiles.open(TEST_FILE.toString())) {
      Instant actual = NetCdfReadUtils.getLevel1Instant(file, 1, "HISTORY_DATE");
      assertEquals(actual, Instant.parse("2021-04-28T22:58:03Z"));
    }
  }
}