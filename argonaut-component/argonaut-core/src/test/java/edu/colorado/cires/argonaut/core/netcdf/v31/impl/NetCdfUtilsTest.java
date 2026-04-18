package edu.colorado.cires.argonaut.core.netcdf.v31.impl;


import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;
import org.junit.jupiter.api.Test;

public class NetCdfUtilsTest {

  @Test
  public void testCalculateJulianDate() {
    assertEquals(Instant.parse("2001-07-25T19:14:00.008Z"), NetCdfUtils.calculateJulianDate(Instant.parse("1950-01-01T00:00:00.000Z"), 18833.8013889885));
  }

}