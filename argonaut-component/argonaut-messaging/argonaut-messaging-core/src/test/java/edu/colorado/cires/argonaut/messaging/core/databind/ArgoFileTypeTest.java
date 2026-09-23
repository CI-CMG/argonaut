package edu.colorado.cires.argonaut.messaging.core.databind;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class ArgoFileTypeTest {

  @Test
  public void testDetermineFileType() throws Exception {
    assertEquals(ArgoFileType.METADATA, ArgoFileType.forFileName("13857_meta.nc"));
    assertEquals(ArgoFileType.TECHNICAL_DATA, ArgoFileType.forFileName("13857_tech.nc"));
    assertEquals(ArgoFileType.PROFILE_CORE, ArgoFileType.forFileName("R4903784_048.nc"));
    assertEquals(ArgoFileType.PROFILE_CORE, ArgoFileType.forFileName("R4903784_048D.nc"));
    assertEquals(ArgoFileType.PROFILE_CORE, ArgoFileType.forFileName("D4903784_048.nc"));
    assertEquals(ArgoFileType.PROFILE_CORE, ArgoFileType.forFileName("D4903784_048D.nc"));
    assertEquals(ArgoFileType.PROFILE_BIOCHEMICAL, ArgoFileType.forFileName("BR1900045_083.nc"));
    assertEquals(ArgoFileType.PROFILE_BIOCHEMICAL, ArgoFileType.forFileName("BR1900045_083D.nc"));
    assertEquals(ArgoFileType.PROFILE_BIOCHEMICAL, ArgoFileType.forFileName("BD1900045_003.nc"));
    assertEquals(ArgoFileType.PROFILE_BIOCHEMICAL, ArgoFileType.forFileName("BD1900045_003D.nc"));
    assertEquals(ArgoFileType.MERGED_PROFILE_SINGLE_CYCLE, ArgoFileType.forFileName("MD1900045_003.nc"));
    assertEquals(ArgoFileType.MERGED_PROFILE_SINGLE_CYCLE, ArgoFileType.forFileName("MR1900045_083D.nc"));
    assertEquals(ArgoFileType.MERGED_PROFILE_SINGLE_CYCLE, ArgoFileType.forFileName("MD1900045_003.nc"));
    assertEquals(ArgoFileType.MERGED_PROFILE_SINGLE_CYCLE, ArgoFileType.forFileName("MD1900045_003D.nc"));
    assertEquals(ArgoFileType.TRAJECTORY, ArgoFileType.forFileName("1900045_Rtraj.nc"));
    assertEquals(ArgoFileType.TRAJECTORY, ArgoFileType.forFileName("1900045_Dtraj.nc"));
    assertEquals(ArgoFileType.SYNTHETIC_PROFILE_SINGLE_CYCLE, ArgoFileType.forFileName("SR6901439_001.nc"));
    assertEquals(ArgoFileType.SYNTHETIC_PROFILE_SINGLE_CYCLE, ArgoFileType.forFileName("SR6901439_001D.nc"));
    assertEquals(ArgoFileType.SYNTHETIC_PROFILE_SINGLE_CYCLE, ArgoFileType.forFileName("SD6901439_001.nc"));
    assertEquals(ArgoFileType.SYNTHETIC_PROFILE_SINGLE_CYCLE, ArgoFileType.forFileName("SD6901439_001D.nc"));
    assertEquals(ArgoFileType.REMOVAL_TXT, ArgoFileType.forFileName("aoml_removal.txt"));
    assertEquals(ArgoFileType.LATEST_PROFILE_MERGE, ArgoFileType.forFileName("D20260722_prof_0.nc"));
    assertEquals(ArgoFileType.LATEST_PROFILE_MERGE, ArgoFileType.forFileName("R20260722_prof_5.nc"));
  }

}
