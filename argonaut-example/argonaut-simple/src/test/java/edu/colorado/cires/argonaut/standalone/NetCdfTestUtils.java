package edu.colorado.cires.argonaut.standalone;

import static edu.colorado.cires.argonaut.core.util.NetCdfWriteUtils.REFERENCE_DATE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoProfileV31;
import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoProfileV31Level;
import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoProfileV31Reader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class NetCdfTestUtils {

  public static void assertFilesEqual(Path expectedFile, Path mergedFile) throws IOException {

    try (
        ArgoProfileV31Reader reader = new ArgoProfileV31Reader(mergedFile);
        ArgoProfileV31Reader expectedReader = new ArgoProfileV31Reader(expectedFile);
    ) {
      List<ArgoProfileV31> profiles = reader.getMultiProfile().getProfiles();
      List<ArgoProfileV31> expectedProfiles = expectedReader.getMultiProfile().getProfiles();
      assertEquals(expectedProfiles.size(), profiles.size());
      for (int q = 0; q < expectedProfiles.size(); q++) {
        ArgoProfileV31 profile = profiles.get(q);
        ArgoProfileV31 expectedProfile = expectedProfiles.get(q);

        assertEquals(profile.getProfileIndex(), profile.getProfileIndex());
        assertEquals(REFERENCE_DATE, profile.getReferenceDateTime());
        assertNotNull(profile.getDateCreation());
        assertNotNull(profile.getDateUpdate());
        assertTrue(profile.getProfileHistory().isEmpty());
        assertEquals(expectedProfile.getCycleNumber(), profile.getCycleNumber());
        assertEquals(expectedProfile.getDirection(), profile.getDirection());
        assertEquals(expectedProfile.getDataMode(), profile.getDataMode());
        assertTrue(Math.abs(expectedProfile.getJulianDate().toEpochMilli() - profile.getJulianDate().toEpochMilli()) < 1000);
        assertEquals(expectedProfile.getJulianDateQc(), profile.getJulianDateQc());
//        Long expectedJulianDateOfLocation = expectedProfile.getJulianDateOfLocation() == null ? null : expectedProfile.getJulianDateOfLocation().toEpochMilli();
//        Long profileJulianDateOfLocation = profile.getJulianDateOfLocation() == null ? null : profile.getJulianDateOfLocation().toEpochMilli();
//        if(expectedJulianDateOfLocation == null || profileJulianDateOfLocation == null){
//          assertEquals(expectedJulianDateOfLocation, profileJulianDateOfLocation);
//        } else {
//          assertTrue(Math.abs(expectedJulianDateOfLocation - profileJulianDateOfLocation) < 1000);
//        }
        if (expectedProfile.getLatitude() == null) {
          assertNull(profile.getLatitude());
        } else {
          assertEquals(expectedProfile.getLatitude(), profile.getLatitude(), 0.0000001);
        }
        if (expectedProfile.getLongitude() == null) {
          assertNull(profile.getLongitude());
        } else {
          assertEquals(expectedProfile.getLongitude(), profile.getLongitude(), 0.0000001);
        }
        assertEquals(expectedProfile.getPositionQc(), profile.getPositionQc());
        assertEquals(expectedProfile.getParameter("PRES").getQc(), profile.getParameter("PRES").getQc());
        assertEquals(expectedProfile.getParameter("TEMP").getQc(), profile.getParameter("TEMP").getQc());
        assertEquals(expectedProfile.getParameter("PSAL").getQc(), profile.getParameter("PSAL").getQc());
        assertEquals(expectedProfile.getConfigMissionNumber(), profile.getConfigMissionNumber());

        for (String parameterName : Arrays.asList("PRES", "TEMP", "PSAL")) {
//          List<ArgoProfileV31Calibration> calibrations = profile.getParameter(parameterName).getCalibrations();
//          List<ArgoProfileV31Calibration> expectedCalibrations = expectedProfile.getParameter(parameterName).getCalibrations();
//          assertEquals(expectedCalibrations.size(), calibrations.size());
//          for (int c = 0; c < calibrations.size(); c++) {
//            ArgoProfileV31Calibration calibration = calibrations.get(c);
//            ArgoProfileV31Calibration expectedCalibration = expectedCalibrations.get(c);
//            assertEquals(expectedCalibration.getDate(), calibration.getDate());
//            assertEquals(expectedCalibration.getParameterName(), calibration.getParameterName());
//            assertEquals(expectedCalibration.getEquation(), calibration.getEquation());
//            assertEquals(expectedCalibration.getCoefficient(), calibration.getCoefficient());
//            assertEquals(expectedCalibration.getComment(), calibration.getComment());
//          }

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


        assertEquals(new HashSet<>(expectedProfile.getStationParameters()), new HashSet<>(profile.getStationParameters()));
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
