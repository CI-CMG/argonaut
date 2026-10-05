package edu.colorado.cires.argonaut.core.netcdf.technical.v31.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.Instant;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

class NetCdfArgoTechnicalV31Test {

  private static final NetCdfArgoTechnicalV31 technical;
  static {
    try {
      technical = NetCdfArgoTechnicalV31.create(Paths.get("src/test/resources/dac/aoml/5903712/5903712_tech.nc"));
    } catch (IOException e) {
      throw new RuntimeException("failed to open NetCDF file", e);
    }
  }

  @AfterAll
  static void tearDown() throws IOException {
    technical.close();
  }

  private static final int nParameters = technical.getNParameters();
  private static final int testParameterN = nParameters / 2;

  @Test
  void getTitle() {
    assertEquals("Argo float technical data file", technical.getTitle());
  }

  @Test
  void getInstitution() {
    assertEquals("AOML", technical.getInstitution());
  }

  @Test
  void getSource() {
    assertEquals("Argo float", technical.getSource());
  }

  @Test
  void getHistory() {
    assertEquals("2021-04-28T22:58:03Z creation", technical.getHistory());
  }

  @Test
  void getReferences() {
    assertEquals("http://www.argodatamgt.org/Documentation", technical.getReferences());
  }

  @Test
  void getComment() {
    assertNull(technical.getComment());
  }

  @Test
  void getUserManualVersion() {
    assertEquals("3.1", technical.getUserManualVersion());
  }

  @Test
  void getConventions() {
    assertEquals("Argo-3.1 CF-1.6", technical.getConventions());
  }

  @Test
  void getPlatformNumber() {
    assertEquals("5903712", technical.getPlatformNumber());
  }

  @Test
  void getDataType() {
    assertEquals("Argo technical data", technical.getDataType());
  }

  @Test
  void getFormatVersion() {
    assertEquals("3.1", technical.getFormatVersion());
  }

  @Test
  void getHandbookVersion() {
    assertEquals("1.2", technical.getHandbookVersion());
  }

  @Test
  void getDataCenter() {
    assertEquals("AO", technical.getDataCenter());
  }

  @Test
  void getDateCreation() {
    assertEquals(Instant.parse("2021-04-28T22:58:03Z"), technical.getDateCreation());
  }

  @Test
  void getDateUpdate() {
    assertEquals(Instant.parse("2021-04-28T22:58:03Z"), technical.getDateUpdate());
  }

  @Test
  void getNParameters() {
    assertEquals(6072, technical.getNParameters());
  }

  @Test
  void getTechnicalParameter() {
    assertEquals("NUMBER_RepositionsDuringPark_COUNT", technical.getTechnicalParameterName(testParameterN));
  }

  @Test
  void getTechnicalParameterValue() {
    assertEquals("54",  technical.getTechnicalParameterValue(testParameterN));
  }

  @Test
  void getCycleNumber() {
    assertEquals(139, technical.getCycleNumber(testParameterN));
  }
}