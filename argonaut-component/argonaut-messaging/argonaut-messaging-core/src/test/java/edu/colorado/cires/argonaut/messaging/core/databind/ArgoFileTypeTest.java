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

  }


  /*
  aoml/13857/13857_meta.nc
  coriolis/4903784/profiles/R4903784_048.nc,20240207103447,55.355,14.559,A,834,IF,20250812074725
coriolis/4903784/profiles/R4903784_048D.nc,20240207091007,55.378,14.579,A,834,IF,20250812074725
   */
}
