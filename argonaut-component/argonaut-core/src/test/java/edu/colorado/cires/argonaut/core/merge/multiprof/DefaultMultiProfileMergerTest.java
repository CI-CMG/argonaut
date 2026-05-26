package edu.colorado.cires.argonaut.core.merge.multiprof;


import static edu.colorado.cires.argonaut.core.util.NetCdfWriteUtils.REFERENCE_DATE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoProfileV31;
import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoProfileV31Calibration;
import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoProfileV31Level;
import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoProfileV31Reader;
import edu.colorado.cires.argonaut.core.util.NetCdfReadUtils;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DefaultMultiProfileMergerTest {

  private final Path outputDir = Paths.get("target/output");

  @BeforeEach
  public void setup() {
    FileUtils.deleteQuietly(outputDir.toFile());
  }

  @Test
  public void test() throws Exception {
    Path dacPath = Paths.get("src/test/resources/dac/meds/4902704");

    List<LocalPathSupplier> pathSuppliers;
    try (Stream<Path> stream = Files.list(dacPath.resolve("profiles"))) {
      pathSuppliers = stream
          .filter(Files::isRegularFile)
          .filter(f -> f.getFileName().toString().endsWith(".nc"))
          .map(SameFileSystemPathSupplier::new)
          .map(ps -> (LocalPathSupplier) ps)
          .toList();
    }
    Path output = outputDir.resolve("4902704_prof.nc");
    DefaultMultiProfileMerger merger = new DefaultMultiProfileMerger("My Institute");
    merger.mergeProfiles(pathSuppliers, Arrays.asList("PRES", "TEMP", "PSAL"), output);

    try (
        ArgoProfileV31Reader reader = new ArgoProfileV31Reader(output);
        ArgoProfileV31Reader expectedReader = new ArgoProfileV31Reader(dacPath.resolve("4902704_prof.nc"));
    ) {
      List<ArgoProfileV31> profiles = reader.getMultiProfile().getProfiles();
      // explicitly check the first profile values to ensure reader is working as expected
      assertEquals(0, profiles.get(0).getProfileIndex());
      assertEquals(REFERENCE_DATE, profiles.get(0).getReferenceDateTime());
      assertNotNull(profiles.get(0).getDateCreation());
      assertNotNull(profiles.get(0).getDateUpdate());
      assertTrue(profiles.get(0).getProfileHistory().isEmpty());
      assertEquals(1, profiles.get(0).getCycleNumber());
      assertEquals("D", profiles.get(0).getDirection());
      assertEquals("R", profiles.get(0).getDataMode());
      assertEquals(27427.895833333332, NetCdfReadUtils.dateToJulianDate(REFERENCE_DATE, profiles.get(0).getJulianDate()), 0.0000001);
      assertEquals("1", profiles.get(0).getJulianDateQc());
      assertEquals(27427.895833333332, NetCdfReadUtils.dateToJulianDate(REFERENCE_DATE, profiles.get(0).getJulianDateOfLocation()), 0.0000001);
      assertEquals(-73.5818862915039, profiles.get(0).getLatitude(), 0.0000001);
      assertEquals(-172.48233032226562, profiles.get(0).getLongitude(), 0.0000001);
      assertEquals("1", profiles.get(0).getPositionQc());
      assertEquals("A", profiles.get(0).getParameter("PRES").getQc());
      assertEquals("B", profiles.get(0).getParameter("TEMP").getQc());
      assertEquals("B", profiles.get(0).getParameter("PSAL").getQc());
      assertEquals(1, profiles.get(0).getConfigMissionNumber());
      assertNull(profiles.get(0).getParameter("PRES").getCalibrations().get(0).getDate());
      assertEquals("PRES", profiles.get(0).getParameter("PRES").getCalibrations().get(0).getParameterName());
      assertNull(profiles.get(0).getParameter("PRES").getCalibrations().get(0).getEquation());
      assertNull(profiles.get(0).getParameter("PRES").getCalibrations().get(0).getCoefficient());
      assertNull(profiles.get(0).getParameter("PRES").getCalibrations().get(0).getComment());
      assertNull(profiles.get(0).getParameter("TEMP").getCalibrations().get(0).getDate());
      assertEquals("TEMP", profiles.get(0).getParameter("TEMP").getCalibrations().get(0).getParameterName());
      assertNull(profiles.get(0).getParameter("TEMP").getCalibrations().get(0).getEquation());
      assertNull(profiles.get(0).getParameter("TEMP").getCalibrations().get(0).getCoefficient());
      assertNull(profiles.get(0).getParameter("TEMP").getCalibrations().get(0).getComment());
      assertNull(profiles.get(0).getParameter("PSAL").getCalibrations().get(0).getDate());
      assertEquals("PSAL", profiles.get(0).getParameter("PSAL").getCalibrations().get(0).getParameterName());
      assertNull(profiles.get(0).getParameter("PSAL").getCalibrations().get(0).getEquation());
      assertNull(profiles.get(0).getParameter("PSAL").getCalibrations().get(0).getCoefficient());
      assertNull(profiles.get(0).getParameter("PSAL").getCalibrations().get(0).getComment());

      List<ArgoProfileV31Level> presLevels = profiles.get(0).getParameter("PRES").getLevels();
      assertEquals(11.9f, presLevels.get(0).getValue(), 0.001f);
      assertEquals(16.7f, presLevels.get(1).getValue(), 0.001f);
      assertEquals(21.7f, presLevels.get(2).getValue(), 0.001f);
      assertEquals(27.1f, presLevels.get(3).getValue(), 0.001f);
      assertEquals(32.1f, presLevels.get(4).getValue(), 0.001f);
      assertEquals(36.5f, presLevels.get(5).getValue(), 0.001f);
      assertNull(presLevels.get(680).getValue());

      assertEquals("1", presLevels.get(0).getQc());
      assertEquals("1", presLevels.get(1).getQc());
      assertEquals("1", presLevels.get(2).getQc());
      assertEquals("1", presLevels.get(3).getQc());
      assertEquals("1", presLevels.get(4).getQc());
      assertEquals("1", presLevels.get(5).getQc());
      assertNull(presLevels.get(680).getQc());

      assertNull(presLevels.get(0).getAdjustedValue());
      assertNull(presLevels.get(1).getAdjustedValue());
      assertNull(presLevels.get(2).getAdjustedValue());
      assertNull(presLevels.get(3).getAdjustedValue());
      assertNull(presLevels.get(4).getAdjustedValue());
      assertNull(presLevels.get(5).getAdjustedValue());
      assertNull(presLevels.get(680).getAdjustedValue());

      assertNull(presLevels.get(0).getAdjustedQc());
      assertNull(presLevels.get(1).getAdjustedQc());
      assertNull(presLevels.get(2).getAdjustedQc());
      assertNull(presLevels.get(3).getAdjustedQc());
      assertNull(presLevels.get(4).getAdjustedQc());
      assertNull(presLevels.get(5).getAdjustedQc());
      assertNull(presLevels.get(680).getAdjustedQc());

      assertNull(presLevels.get(0).getAdjustedErrorValue());
      assertNull(presLevels.get(1).getAdjustedErrorValue());
      assertNull(presLevels.get(2).getAdjustedErrorValue());
      assertNull(presLevels.get(3).getAdjustedErrorValue());
      assertNull(presLevels.get(4).getAdjustedErrorValue());
      assertNull(presLevels.get(5).getAdjustedErrorValue());
      assertNull(presLevels.get(680).getAdjustedErrorValue());

      List<ArgoProfileV31Level> tempLevels = profiles.get(0).getParameter("TEMP").getLevels();
      assertEquals(-1.172f, tempLevels.get(0).getValue(), 0.001f);
      assertEquals(-1.172f, tempLevels.get(1).getValue(), 0.001f);
      assertEquals(-1.175f, tempLevels.get(2).getValue(), 0.001f);
      assertEquals(-1.323f, tempLevels.get(3).getValue(), 0.001f);
      assertEquals(-1.608f, tempLevels.get(4).getValue(), 0.001f);
      assertEquals(-1.671f, tempLevels.get(5).getValue(), 0.001f);
      assertNull(tempLevels.get(680).getValue());

      assertEquals("1", tempLevels.get(0).getQc());
      assertEquals("1", tempLevels.get(1).getQc());
      assertEquals("4", tempLevels.get(2).getQc());
      assertEquals("4", tempLevels.get(3).getQc());
      assertEquals("4", tempLevels.get(4).getQc());
      assertEquals("4", tempLevels.get(5).getQc());
      assertNull(tempLevels.get(680).getQc());

      assertNull(tempLevels.get(0).getAdjustedValue());
      assertNull(tempLevels.get(1).getAdjustedValue());
      assertNull(tempLevels.get(2).getAdjustedValue());
      assertNull(tempLevels.get(3).getAdjustedValue());
      assertNull(tempLevels.get(4).getAdjustedValue());
      assertNull(tempLevels.get(5).getAdjustedValue());
      assertNull(tempLevels.get(680).getAdjustedValue());

      assertNull(tempLevels.get(0).getAdjustedQc());
      assertNull(tempLevels.get(1).getAdjustedQc());
      assertNull(tempLevels.get(2).getAdjustedQc());
      assertNull(tempLevels.get(3).getAdjustedQc());
      assertNull(tempLevels.get(4).getAdjustedQc());
      assertNull(tempLevels.get(5).getAdjustedQc());
      assertNull(tempLevels.get(680).getAdjustedQc());

      assertNull(tempLevels.get(0).getAdjustedErrorValue());
      assertNull(tempLevels.get(1).getAdjustedErrorValue());
      assertNull(tempLevels.get(2).getAdjustedErrorValue());
      assertNull(tempLevels.get(3).getAdjustedErrorValue());
      assertNull(tempLevels.get(4).getAdjustedErrorValue());
      assertNull(tempLevels.get(5).getAdjustedErrorValue());
      assertNull(tempLevels.get(680).getAdjustedErrorValue());

      List<ArgoProfileV31Level> psalLevels = profiles.get(0).getParameter("PSAL").getLevels();
      assertEquals(33.533f, psalLevels.get(0).getValue(), 0.001f);
      assertEquals(33.544f, psalLevels.get(1).getValue(), 0.001f);
      assertEquals(33.551f, psalLevels.get(2).getValue(), 0.001f);
      assertEquals(33.7f, psalLevels.get(3).getValue(), 0.001f);
      assertEquals(34.005f, psalLevels.get(4).getValue(), 0.001f);
      assertEquals(34.082f, psalLevels.get(5).getValue(), 0.001f);
      assertNull(psalLevels.get(680).getValue());

      assertEquals("1", psalLevels.get(0).getQc());
      assertEquals("1", psalLevels.get(1).getQc());
      assertEquals("4", psalLevels.get(2).getQc());
      assertEquals("4", psalLevels.get(3).getQc());
      assertEquals("4", psalLevels.get(4).getQc());
      assertEquals("4", psalLevels.get(5).getQc());
      assertNull(psalLevels.get(680).getQc());

      assertNull(psalLevels.get(0).getAdjustedValue());
      assertNull(psalLevels.get(1).getAdjustedValue());
      assertNull(psalLevels.get(2).getAdjustedValue());
      assertNull(psalLevels.get(3).getAdjustedValue());
      assertNull(psalLevels.get(4).getAdjustedValue());
      assertNull(psalLevels.get(5).getAdjustedValue());
      assertNull(psalLevels.get(680).getAdjustedValue());

      assertNull(psalLevels.get(0).getAdjustedQc());
      assertNull(psalLevels.get(1).getAdjustedQc());
      assertNull(psalLevels.get(2).getAdjustedQc());
      assertNull(psalLevels.get(3).getAdjustedQc());
      assertNull(psalLevels.get(4).getAdjustedQc());
      assertNull(psalLevels.get(5).getAdjustedQc());
      assertNull(psalLevels.get(680).getAdjustedQc());

      assertNull(psalLevels.get(0).getAdjustedErrorValue());
      assertNull(psalLevels.get(1).getAdjustedErrorValue());
      assertNull(psalLevels.get(2).getAdjustedErrorValue());
      assertNull(psalLevels.get(3).getAdjustedErrorValue());
      assertNull(psalLevels.get(4).getAdjustedErrorValue());
      assertNull(psalLevels.get(5).getAdjustedErrorValue());
      assertNull(psalLevels.get(680).getAdjustedErrorValue());

      assertEquals(Arrays.asList("PRES", "TEMP", "PSAL"), profiles.get(0).getStationParameters());
      assertEquals("ME", profiles.get(0).getDataCenter());
      assertEquals("Primary sampling: averaged", profiles.get(0).getVerticalSamplingScheme());
      assertEquals("4902704_9999_TE", profiles.get(0).getDataCenterReference());
      assertEquals("ARVOR", profiles.get(0).getPlatformType());
      assertEquals("260024CA13", profiles.get(0).getFloatSerialNumber());
      assertEquals("n/a", profiles.get(0).getFirmwareVersion());
      assertEquals("2B", profiles.get(0).getDataStateIndicator());
      assertEquals("844", profiles.get(0).getWmoInstrumentType());
      assertEquals("Argo Canada", profiles.get(0).getProjectName());
      assertEquals("Blair Greenan", profiles.get(0).getPrincipalInvestigatorName());
      assertEquals("4902704", profiles.get(0).getPlatformNumber());
      assertEquals("IRIDIUM", profiles.get(0).getPositioningSystem());
      assertEquals("Argo profile", profiles.get(0).getDataType());
      assertEquals("3.1", profiles.get(0).getFormatVersion());
      assertEquals("1.2", profiles.get(0).getHandbookVersion());

      // now that reader is verified, use an actual example to test every value

      List<ArgoProfileV31> expectedProfiles = expectedReader.getMultiProfile().getProfiles();
      assertEquals(85, profiles.size());
      assertEquals(85, expectedProfiles.size());
      for (int i = 0; i < expectedProfiles.size(); i++) {
        ArgoProfileV31 profile = profiles.get(0);
        ArgoProfileV31 expectedProfile = expectedProfiles.get(0);

        assertEquals(profile.getProfileIndex(), profile.getProfileIndex());
        assertEquals(REFERENCE_DATE, profile.getReferenceDateTime());
        assertNotNull(profile.getDateCreation());
        assertNotNull(profile.getDateUpdate());
        assertTrue(profile.getProfileHistory().isEmpty());
        assertEquals(expectedProfile.getCycleNumber(), profile.getCycleNumber());
        assertEquals(expectedProfile.getDirection(), profile.getDirection());
        assertEquals(expectedProfile.getDataMode(), profile.getDataMode());
        assertEquals(expectedProfile.getJulianDate(), profile.getJulianDate());
        assertEquals(expectedProfile.getJulianDateQc(), profile.getJulianDateQc());
        assertEquals(expectedProfile.getJulianDateOfLocation(), profile.getJulianDateOfLocation());
        assertEquals(expectedProfile.getLatitude(), profile.getLatitude(), 0.0000001);
        assertEquals(expectedProfile.getLongitude(), profile.getLongitude(), 0.0000001);
        assertEquals(expectedProfile.getPositionQc(), profile.getPositionQc());
        assertEquals(expectedProfile.getParameter("PRES").getQc(), profile.getParameter("PRES").getQc());
        assertEquals(expectedProfile.getParameter("TEMP").getQc(), profile.getParameter("TEMP").getQc());
        assertEquals(expectedProfile.getParameter("PSAL").getQc(), profile.getParameter("PSAL").getQc());
        assertEquals(expectedProfile.getConfigMissionNumber(), profile.getConfigMissionNumber());

        for (String parameterName : Arrays.asList("PRES", "TEMP", "PSAL")) {
          List<ArgoProfileV31Calibration> calibrations = profile.getParameter(parameterName).getCalibrations();
          List<ArgoProfileV31Calibration> expectedCalibrations = expectedProfile.getParameter(parameterName).getCalibrations();
          assertEquals(expectedCalibrations.size(), calibrations.size());
          for (int c = 0; c < calibrations.size(); c++) {
            ArgoProfileV31Calibration calibration = calibrations.get(c);
            ArgoProfileV31Calibration expectedCalibration = expectedCalibrations.get(c);
            assertEquals(expectedCalibration.getDate(), calibration.getDate());
            assertEquals(expectedCalibration.getParameterName(), calibration.getParameterName());
            assertEquals(expectedCalibration.getEquation(), calibration.getEquation());
            assertEquals(expectedCalibration.getCoefficient(), calibration.getCoefficient());
            assertEquals(expectedCalibration.getComment(), calibration.getComment());
          }

          List<ArgoProfileV31Level> levels = profile.getParameter(parameterName).getLevels();
          List<ArgoProfileV31Level> expectedlevels = expectedProfile.getParameter(parameterName).getLevels();
          assertEquals(expectedlevels.size(), levels.size());
          for (int l = 0; l < levels.size(); l++) {
            ArgoProfileV31Level level = levels.get(l);
            ArgoProfileV31Level expectedLevel = expectedlevels.get(l);

            Float expectedValue = expectedLevel.getValue();
            if (expectedValue != null) {
              assertEquals(expectedValue, level.getValue(), 0.001f);
            } else {
              assertNull(level.getValue());
            }

            assertEquals(expectedLevel.getQc(), level.getQc());

            Float expectedAdjustedValue = expectedLevel.getAdjustedValue();
            if (expectedAdjustedValue != null) {
              assertEquals(expectedAdjustedValue, level.getAdjustedValue(), 0.001f);
            } else {
              assertNull(level.getAdjustedValue());
            }

            assertEquals(expectedLevel.getAdjustedQc(), level.getAdjustedQc());

            Float expectedAdjustedErrorValue = expectedLevel.getAdjustedErrorValue();
            if (expectedAdjustedErrorValue != null) {
              assertEquals(expectedAdjustedErrorValue, level.getAdjustedErrorValue(), 0.001f);
            } else {
              assertNull(level.getAdjustedErrorValue());
            }
          }
        }

        assertEquals(expectedProfile.getStationParameters(), profile.getStationParameters());
        assertEquals(expectedProfile.getDataCenter(), profile.getDataCenter());
        assertEquals(expectedProfile.getVerticalSamplingScheme(), profile.getVerticalSamplingScheme());
        assertEquals(expectedProfile.getDataCenterReference(), profile.getDataCenterReference());
        assertEquals(expectedProfile.getPlatformType(), profile.getPlatformType());
        assertEquals(expectedProfile.getFloatSerialNumber(), profile.getFloatSerialNumber());
        assertEquals(expectedProfile.getFirmwareVersion(), profile.getFirmwareVersion());
        assertEquals(expectedProfile.getDataStateIndicator(), profile.getDataStateIndicator());
        assertEquals(expectedProfile.getWmoInstrumentType(), profile.getWmoInstrumentType());
        assertEquals(expectedProfile.getProjectName(), profile.getProjectName());
        assertEquals(expectedProfile.getPrincipalInvestigatorName(), profile.getPrincipalInvestigatorName());
        assertEquals(expectedProfile.getPlatformNumber(), profile.getPlatformNumber());
        assertEquals(expectedProfile.getPositioningSystem(), profile.getPositioningSystem());
        assertEquals(expectedProfile.getDataType(), profile.getDataType());
        assertEquals(expectedProfile.getFormatVersion(), profile.getFormatVersion());
        assertEquals(expectedProfile.getHandbookVersion(), profile.getHandbookVersion());

      }

    }

  }

}