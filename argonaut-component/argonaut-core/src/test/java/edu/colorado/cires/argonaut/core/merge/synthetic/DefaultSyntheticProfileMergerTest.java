package edu.colorado.cires.argonaut.core.merge.synthetic;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.colorado.cires.argonaut.core.netcdf.synthprofile.v13.ArgoSyntheticProfileV13;
import edu.colorado.cires.argonaut.core.netcdf.synthprofile.v13.ArgoSyntheticProfileV13Calibration;
import edu.colorado.cires.argonaut.core.netcdf.synthprofile.v13.ArgoSyntheticProfileV13Level;
import edu.colorado.cires.argonaut.core.netcdf.synthprofile.v13.ArgoSyntheticProfileV13Parameter;
import edu.colorado.cires.argonaut.core.netcdf.synthprofile.v13.ArgoSyntheticProfileV13Reader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DefaultSyntheticProfileMergerTest {

  @BeforeEach
  public void setup() {
    FileUtils.deleteQuietly(Paths.get("target/output").toFile());
  }

  private static void assertParameterValuesEqual(ArgoSyntheticProfileV13Parameter expected, ArgoSyntheticProfileV13Parameter actual) {
    assertParametersEqual(expected, actual);
    assertQcEqual(expected, actual);
    assertDPressEqual(expected, actual);
    assertAdjustedQcEqual(expected, actual);
    assertAdjustedParametersEqual(expected, actual);
    assertAdjustedErrorEqual(expected, actual);
    assertEquals(expected.getDataMode(), actual.getDataMode());
    assertEquals(expected.getQc(), actual.getQc());
  }

  private static void assertCalibrationsEqual(ArgoSyntheticProfileV13Calibration expected, ArgoSyntheticProfileV13Calibration actual) {
    assertEquals(expected.getCalibrationIndex(), actual.getCalibrationIndex());
    assertEquals(expected.getProfileIndex(), actual.getProfileIndex());
    assertEquals(expected.getCoefficient(), actual.getCoefficient());
    assertEquals(expected.getDate(), actual.getDate());
    assertEquals(expected.getEquation(), actual.getEquation());
    assertEquals(expected.getComment(), actual.getComment());
    assertEquals(expected.getParameterName(), actual.getParameterName());
  }

  @Test
  public void test() throws Exception {

    Path cProfilePath = Paths.get("src/test/resources/dac/meds/4902691/profiles/R4902691_034.nc");
    Path bProfilePath = Paths.get("src/test/resources/dac/meds/4902691/profiles/BR4902691_034.nc");
    Path metaPath = Paths.get("src/test/resources/dac/meds/4902691/4902691_meta.nc");
    Path outputPath = Paths.get("target/output/dac/meds/4902691/profiles/SR4902691_034.nc");
    Path expectedPath = Paths.get("src/test/resources/dac/meds/4902691/profiles/SR4902691_034.nc");
    DefaultSyntheticProfileMerger syntheticProfileMerger = new DefaultSyntheticProfileMerger();
    syntheticProfileMerger.mergeProfiles(cProfilePath, bProfilePath, metaPath, outputPath);

    try (
        ArgoSyntheticProfileV13Reader reader = new ArgoSyntheticProfileV13Reader(outputPath);
        ArgoSyntheticProfileV13Reader expectedReader = new ArgoSyntheticProfileV13Reader(expectedPath);
    ) {


      List<ArgoSyntheticProfileV13> profiles = reader.getMultiProfile().getProfiles();
      List<ArgoSyntheticProfileV13> expectedProfiles = expectedReader.getMultiProfile().getProfiles();
      assertEquals(1, profiles.size());
      assertEquals(1, expectedProfiles.size());
      ArgoSyntheticProfileV13 profile = profiles.get(0);
      ArgoSyntheticProfileV13 expectedProfile = expectedProfiles.get(0);

      assertEquals(expectedProfile.getTitle(), profile.getTitle());
      assertEquals(expectedProfile.getInstitution(), profile.getInstitution());
      assertEquals(expectedProfile.getSource(), profile.getSource());
      assertTrue(profile.getHistory().contains(" creation (Argonaut "));
      assertEquals("http://www.argodatamgt.org/Documentation, https://github.com/CI-CMG/argonaut", profile.getReferences());
      assertEquals(expectedProfile.getUserManualVersion(), profile.getUserManualVersion());
      assertEquals(expectedProfile.getConventions(), profile.getConventions());
      assertEquals(expectedProfile.getFeatureType(), profile.getFeatureType());
      assertEquals(expectedProfile.getId(), profile.getId());
      assertTrue(profile.getSoftwareVersion().contains(" (Argonaut "));

      assertEquals(expectedProfile.getConfigMissionNumber(), profile.getConfigMissionNumber());
      assertEquals(expectedProfile.getCycleNumber(), profile.getCycleNumber());
      assertEquals(expectedProfile.getDataCenter(), profile.getDataCenter());
      assertEquals(expectedProfile.getDataType(), profile.getDataType());
      assertNotNull(profile.getDateCreation());
      assertNotNull(profile.getDateUpdate());
      assertEquals(expectedProfile.getDirection(), profile.getDirection());
      assertEquals(expectedProfile.getFirmwareVersion(), profile.getFirmwareVersion());
      assertEquals(expectedProfile.getFloatSerialNumber(), profile.getFloatSerialNumber());
      assertEquals(expectedProfile.getFormatVersion(), profile.getFormatVersion());
      assertEquals(expectedProfile.getHandbookVersion(), profile.getHandbookVersion());
      assertEquals(expectedProfile.getJulianDate(), profile.getJulianDate());
      assertEquals(expectedProfile.getJulianDateOfLocation(), profile.getJulianDateOfLocation());
      assertEquals(expectedProfile.getJulianDateQc(), profile.getJulianDateQc());
      assertEquals(expectedProfile.getLatitude(), profile.getLatitude(), 0.0001);
      assertEquals(expectedProfile.getLongitude(), profile.getLongitude(), 0.0001);
      assertEquals(expectedProfile.getPrincipalInvestigatorName(), profile.getPrincipalInvestigatorName());
      assertEquals(expectedProfile.getPlatformNumber(), profile.getPlatformNumber());
      assertEquals(expectedProfile.getPlatformType(), profile.getPlatformType());
      assertEquals(expectedProfile.getPositionQc(), profile.getPositionQc());
      assertEquals(expectedProfile.getPositioningSystem(), profile.getPositioningSystem());
      assertEquals(expectedProfile.getProjectName(), profile.getProjectName());
      assertEquals(expectedProfile.getReferenceDateTime(), profile.getReferenceDateTime());
      assertEquals(expectedProfile.getStationParameters(), profile.getStationParameters());
      assertEquals(expectedProfile.getWmoInstrumentType(), profile.getWmoInstrumentType());


      List<ArgoSyntheticProfileV13Parameter> parameters = profile.getParameters();
      List<ArgoSyntheticProfileV13Parameter> expectedParameters = expectedProfile.getParameters();

      List<String> parameterNames = parameters.stream().map(ArgoSyntheticProfileV13Parameter::getParameterName).toList();
      List<String> expectedParameterNames = expectedParameters.stream().map(ArgoSyntheticProfileV13Parameter::getParameterName).toList();
      assertEquals(expectedParameterNames, parameterNames);
      for(String parameterName : parameterNames){
        ArgoSyntheticProfileV13Parameter param = parameters.stream().filter(p -> p.getParameterName().equals(parameterName)).findFirst().orElseThrow();
        ArgoSyntheticProfileV13Parameter expectedParam = expectedParameters.stream().filter(p -> p.getParameterName().equals(parameterName)).findFirst()
            .orElseThrow();
        assertParameterValuesEqual(expectedParam, param);

        List<ArgoSyntheticProfileV13Calibration> calibrations = param.getCalibrations();
        List<ArgoSyntheticProfileV13Calibration> expectedCalibrations = expectedParam.getCalibrations();
        assertEquals(expectedCalibrations.size(), calibrations.size());
        for (int i = 0; i < expectedCalibrations.size(); i++) {
          assertCalibrationsEqual(expectedCalibrations.get(i), calibrations.get(i));
        }
      }




//      ArgoSyntheticProfileV13Parameter temp = parameters.stream().filter(p -> p.getParameterName().equals("TEMP")).findFirst().orElseThrow();
//      ArgoSyntheticProfileV13Parameter expectedTemp = expectedParameters.stream().filter(p -> p.getParameterName().equals("TEMP")).findFirst()
//          .orElseThrow();
//      assertParameterValuesEqual(expectedTemp, temp);
//
//      ArgoSyntheticProfileV13Parameter pSal = parameters.stream().filter(p -> p.getParameterName().equals("PSAL")).findFirst().orElseThrow();
//      ArgoSyntheticProfileV13Parameter expectedPSal = expectedParameters.stream().filter(p -> p.getParameterName().equals("PSAL")).findFirst()
//          .orElseThrow();
//      assertParameterValuesEqual(expectedPSal, pSal);
//
//      ArgoSyntheticProfileV13Parameter downwellingPar = parameters.stream().filter(p -> p.getParameterName().equals("DOWNWELLING_PAR")).findFirst().orElseThrow();
//      ArgoSyntheticProfileV13Parameter expectedDownwellingPar = expectedParameters.stream().filter(p -> p.getParameterName().equals("DOWNWELLING_PAR")).findFirst()
//          .orElseThrow();
//      assertParameterValuesEqual(expectedDownwellingPar, downwellingPar);
//
//      ArgoSyntheticProfileV13Parameter d380 = parameters.stream().filter(p -> p.getParameterName().equals("DOWN_IRRADIANCE380")).findFirst().orElseThrow();
//      ArgoSyntheticProfileV13Parameter expectedD380 = expectedParameters.stream().filter(p -> p.getParameterName().equals("DOWN_IRRADIANCE380")).findFirst()
//          .orElseThrow();
//      assertParameterValuesEqual(expectedD380, d380);
//
//      ArgoSyntheticProfileV13Parameter d412 = parameters.stream().filter(p -> p.getParameterName().equals("DOWN_IRRADIANCE412")).findFirst().orElseThrow();
//      ArgoSyntheticProfileV13Parameter expectedD412 = expectedParameters.stream().filter(p -> p.getParameterName().equals("DOWN_IRRADIANCE412")).findFirst()
//          .orElseThrow();
//      assertParameterValuesEqual(expectedD412, d412);
//
//      ArgoSyntheticProfileV13Parameter d490 = parameters.stream().filter(p -> p.getParameterName().equals("DOWN_IRRADIANCE490")).findFirst().orElseThrow();
//      ArgoSyntheticProfileV13Parameter expectedD490 = expectedParameters.stream().filter(p -> p.getParameterName().equals("DOWN_IRRADIANCE490")).findFirst()
//          .orElseThrow();
//      assertParameterValuesEqual(expectedD490, d490);
//
//      ArgoSyntheticProfileV13Parameter doxyPar = parameters.stream().filter(p -> p.getParameterName().equals("DOXY")).findFirst().orElseThrow();
//      ArgoSyntheticProfileV13Parameter expectedDoxyPar = expectedParameters.stream().filter(p -> p.getParameterName().equals("DOXY")).findFirst()
//          .orElseThrow();
//      assertParameterValuesEqual(expectedDoxyPar, doxyPar);
//
//      ArgoSyntheticProfileV13Parameter nitrate = parameters.stream().filter(p -> p.getParameterName().equals("NITRATE")).findFirst().orElseThrow();
//      ArgoSyntheticProfileV13Parameter expectedNitrate = expectedParameters.stream().filter(p -> p.getParameterName().equals("NITRATE")).findFirst()
//          .orElseThrow();
//      assertParameterValuesEqual(expectedNitrate, nitrate);

    }

  }

  private static void assertParametersEqual(ArgoSyntheticProfileV13Parameter expectedParam, ArgoSyntheticProfileV13Parameter actualParam) {
    float[] expected = fromList(expectedParam.getLevels().stream().map(ArgoSyntheticProfileV13Level::getOriginalValue).toList(),
        Float.MAX_VALUE - 1f);
    float[] actual = fromList(actualParam.getLevels().stream().map(ArgoSyntheticProfileV13Level::getOriginalValue).toList(), Float.MAX_VALUE - 1f);
    assertArrayEquals(expected, actual, 0.0001f);
  }

  private static void assertAdjustedParametersEqual(ArgoSyntheticProfileV13Parameter expectedParam, ArgoSyntheticProfileV13Parameter actualParam) {
    float[] expected = fromList(expectedParam.getLevels().stream().map(ArgoSyntheticProfileV13Level::getAdjustedValue).toList(),
        Float.MAX_VALUE - 1f);
    float[] actual = fromList(actualParam.getLevels().stream().map(ArgoSyntheticProfileV13Level::getAdjustedValue).toList(), Float.MAX_VALUE - 1f);
    assertArrayEquals(expected, actual, 0.0001f);
  }

  private static void assertAdjustedErrorEqual(ArgoSyntheticProfileV13Parameter expectedParam, ArgoSyntheticProfileV13Parameter actualParam) {
    float[] expected = fromList(expectedParam.getLevels().stream().map(ArgoSyntheticProfileV13Level::getAdjustedErrorValue).toList(),
        Float.MAX_VALUE - 1f);
    float[] actual = fromList(actualParam.getLevels().stream().map(ArgoSyntheticProfileV13Level::getAdjustedErrorValue).toList(), Float.MAX_VALUE - 1f);
    assertArrayEquals(expected, actual, 0.0001f);
  }

  private static void assertDPressEqual(ArgoSyntheticProfileV13Parameter expectedParam, ArgoSyntheticProfileV13Parameter actualParam) {
    float[] expected = fromList(expectedParam.getLevels().stream().map(ArgoSyntheticProfileV13Level::getPressureDisplacement).toList(),
        Float.MAX_VALUE - 1f);
    float[] actual = fromList(actualParam.getLevels().stream().map(ArgoSyntheticProfileV13Level::getPressureDisplacement).toList(), Float.MAX_VALUE - 1f);
    assertArrayEquals(expected, actual, 0.0001f);
  }

  private static void assertQcEqual(ArgoSyntheticProfileV13Parameter expectedParam, ArgoSyntheticProfileV13Parameter actualParam) {
    List<String> expected = expectedParam.getLevels().stream().map(ArgoSyntheticProfileV13Level::getQc).toList();
    List<String> actual = actualParam.getLevels().stream().map(ArgoSyntheticProfileV13Level::getQc).toList();
    assertEquals(expected, actual);
  }

  private static void assertAdjustedQcEqual(ArgoSyntheticProfileV13Parameter expectedParam, ArgoSyntheticProfileV13Parameter actualParam) {
    List<String> expected = expectedParam.getLevels().stream().map(ArgoSyntheticProfileV13Level::getAdjustedQc).toList();
    List<String> actual = actualParam.getLevels().stream().map(ArgoSyntheticProfileV13Level::getAdjustedQc).toList();
    assertEquals(expected, actual);
  }

  private static float[] fromList(List<Float> list, float fillValue) {
    float[] result = new float[list.size()];
    for (int i = 0; i < list.size(); i++) {
      Float f = list.get(i);
      if (f != null) {
        result[i] = list.get(i);
      } else {
        result[i] = fillValue;
      }
    }
    return result;
  }


}