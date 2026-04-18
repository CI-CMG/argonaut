package edu.colorado.cires.argonaut.core.netcdf.metadata.v31;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import org.junit.jupiter.api.Test;

public class ArgoMetadataV31ReaderTest {

  @Test
  public void testRead() throws Exception {
    Path path = Paths.get("../../test-data/dac/aoml/1900722/1900722_meta.nc");
    try (ArgoMetadataV31Reader reader = new ArgoMetadataV31Reader(path)) {
      ArgoMetadataV31 metadata = reader.getMetadata();
      assertEquals("Argo float metadata file", metadata.getTitle());
      assertEquals("AOML", metadata.getInstitution());
      assertEquals("Argo float", metadata.getSource());
      assertEquals("2016-06-23T15:09:33Z creation", metadata.getHistory());
      assertEquals("http://www.argodatamgt.org/Documentation", metadata.getReferences());
      assertEquals("free text", metadata.getComment());
      assertEquals("3.1", metadata.getUserManualVersion());
      assertEquals("Argo-3.1 CF-1.6", metadata.getConventions());
      assertEquals("Argo meta-data", metadata.getDataType());
      assertEquals("3.1", metadata.getFormatVersion());
      assertEquals("1.2", metadata.getHandbookVersion());
      assertEquals(Instant.parse("2016-06-23T15:09:33.000Z"), metadata.getDateCreation());
      assertEquals(Instant.parse("2016-06-23T15:09:33.000Z"), metadata.getDateUpdate());
      assertEquals("1900722", metadata.getPlatformNumber());
      assertNull(metadata.getPlatformWigosId());
      assertEquals("17964", metadata.getPtt());
//      assertEquals("ARGOS", metadata.getTransSystem());
//      assertEquals("12281", metadata.getTransSystemId());
//      assertEquals("n/a", metadata.getTransFrequency());
//      assertEquals("ARGOS", metadata.getPositioningSystem());
      assertEquals("FLOAT", metadata.getPlatformFamily());
      assertEquals("APEX", metadata.getPlatformType());
      assertEquals("WRC", metadata.getPlatformMaker());
      assertEquals("012606", metadata.getFirmwareVersion());
      assertEquals("012606", metadata.getManualVersion());
      assertEquals("2596", metadata.getFloatSerialNumber());
      assertEquals("n/a", metadata.getStandardFormatId());
      assertEquals("APEX_TSO7", metadata.getDacFormatId());
      assertEquals("846", metadata.getWmoInstrumentType());
      assertEquals("US ARGO PROJECT", metadata.getProjectName());
      assertNull(metadata.getProgramName());
      assertEquals("AO", metadata.getDataCenter());
      assertEquals("STEPHEN RISER", metadata.getPrincipalInvestigatorName());
      assertEquals("n/a", metadata.getAnomaly());
      assertEquals("Alkaline and Lithium", metadata.getBatteryType());
      assertEquals("board - 1 (s/n: 5035);", metadata.getBatteryPacks());
//      ArgoMetadataV31ControllerBoard getPrimaryControllerBoard());
//      ArgoMetadataV31ControllerBoard getSecondaryControllerBoard());
      assertEquals("n/a", metadata.getSpecialFeatures());
      assertEquals("STEPHEN RISER", metadata.getFloatOwner());
      assertEquals("UW, Seattle", metadata.getOperatingInstitution());
      assertEquals("n/a", metadata.getCustomization());
      assertEquals(Instant.parse("2006-10-21T06:06:00.000Z"), metadata.getLaunchDate());
      assertEquals(-40.31666564941406, metadata.getLaunchLatitude(), 0.0000001);
      assertEquals(73.33333587646484, metadata.getLaunchLongitude(), 0.0000001);
      assertEquals("1", metadata.getLaunchQc());
      assertEquals(Instant.parse("2006-10-21T10:36:00.000Z"), metadata.getStartDate());
      assertEquals("1", metadata.getStartDateQc());
      assertEquals(Instant.parse("2006-10-21T04:36:00.000Z"), metadata.getStartupDate());
      assertEquals("1", metadata.getStartupDateQc());
      assertEquals("Kilo Moana", metadata.getDeploymentPlatform());
      assertEquals("n/a", metadata.getDeploymentCruiseId());
      assertEquals("n/a", metadata.getDeploymentReferenceStationId());
      assertNull(metadata.getEndMissionDate());
      assertNull(metadata.getEndMissionStatus());
    }
  }
}