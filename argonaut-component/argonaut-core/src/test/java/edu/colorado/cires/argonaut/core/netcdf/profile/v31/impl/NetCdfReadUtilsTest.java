package edu.colorado.cires.argonaut.core.netcdf.profile.v31.impl;


import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.colorado.cires.argonaut.core.util.NetCdfReadUtils;
import java.time.Instant;
import org.junit.jupiter.api.Test;

public class NetCdfReadUtilsTest {

  @Test
  public void testCalculateJulianDate() {
    assertEquals(Instant.parse("2001-07-25T19:14:00.008Z"), NetCdfReadUtils.calculateJulianDate(Instant.parse("1950-01-01T00:00:00.000Z"), 18833.8013889885));
  }

}