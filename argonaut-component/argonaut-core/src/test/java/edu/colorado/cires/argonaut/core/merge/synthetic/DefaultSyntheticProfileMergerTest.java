package edu.colorado.cires.argonaut.core.merge.synthetic;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.colorado.cires.argonaut.core.netcdf.synthprofile.v13.ArgoSyntheticProfileV13;
import edu.colorado.cires.argonaut.core.netcdf.synthprofile.v13.ArgoSyntheticProfileV13Calibration;
import edu.colorado.cires.argonaut.core.netcdf.synthprofile.v13.ArgoSyntheticProfileV13Level;
import edu.colorado.cires.argonaut.core.netcdf.synthprofile.v13.ArgoSyntheticProfileV13Parameter;
import edu.colorado.cires.argonaut.core.netcdf.synthprofile.v13.ArgoSyntheticProfileV13Reader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class DefaultSyntheticProfileMergerTest {

  @BeforeEach
  void setup() {
    FileUtils.deleteQuietly(Paths.get("target/output").toFile());
  }

  private static void assertParameterValuesEqual(ArgoSyntheticProfileV13Parameter expected, ArgoSyntheticProfileV13Parameter actual) {
    if (expected == null) {
      assertNull(actual);
      return;
    }

    assertParametersEqual(expected, actual);
    assertQcEqual(expected, actual);
    assertDPressEqual(expected, actual);
    assertAdjustedQcEqual(expected, actual);
    assertAdjustedParametersEqual(expected, actual);
    assertAdjustedErrorEqual(expected, actual);
    assertEquals(expected.getDataMode(), actual.getDataMode());
    assertEquals(expected.getQc(), actual.getQc(), "%s QC indicator does not equal expected".formatted(expected.getParameterName()));
  }

  private static void assertCalibrationsEqual(ArgoSyntheticProfileV13Calibration expected, ArgoSyntheticProfileV13Calibration actual) {
    String parameterName = expected.getParameterName();
    assertEquals(expected.getCalibrationIndex(), actual.getCalibrationIndex(), "%s calibration calibrationIndex does not equal expected value".formatted(parameterName));
    assertEquals(expected.getProfileIndex(), actual.getProfileIndex(), "%s calibration profileIndex does not equal expected value".formatted(parameterName));
    assertEquals(expected.getCoefficient(), actual.getCoefficient(), "%s calibration coefficient does not equal expected value".formatted(parameterName));
    assertEquals(expected.getDate(), actual.getDate(), "%s calibration date does not equal expected value".formatted(parameterName));
    assertEquals(expected.getEquation(), actual.getEquation(), "%s calibration equation does not equal expected value".formatted(parameterName));
    assertEquals(expected.getComment(), actual.getComment(), "%s calibration comment does not equal expected value".formatted(parameterName));
    assertEquals(parameterName, actual.getParameterName());
  }

  private record TestCase(String cProfilePath, String bProfilePath, String metaPath, String expectedPath) {}

  private static List<Named<TestCase>> testCases() {
    return List.of(
      Named.of("happy path", new TestCase("src/test/resources/dac/meds/4902691/profiles/R4902691_034.nc",
        "src/test/resources/dac/meds/4902691/profiles/BR4902691_034.nc",
        "src/test/resources/dac/meds/4902691/4902691_meta.nc",
        "src/test/resources/dac/meds/4902691/profiles/SR4902691_034.nc")),
      Named.of("extrapolate out of bounds 1", new TestCase("src/test/resources/dac/coriolis/6901486/profiles/D6901486_037.nc",
        "src/test/resources/dac/coriolis/6901486/profiles/BD6901486_037.nc",
        "src/test/resources/dac/coriolis/6901486/6901486_meta.nc",
        "src/test/resources/dac/coriolis/6901486/profiles/SD6901486_037.nc")),
      Named.of("extrapolate out of bounds 2", new TestCase("src/test/resources/dac/bdoc/6901174/profiles/D6901174_143.nc",
        "src/test/resources/dac/bdoc/6901174/profiles/BD6901174_143.nc",
        "src/test/resources/dac/bdoc/6901174/6901174_meta.nc",
        "src/test/resources/out/6901174/profiles/SD6901174_143.nc")),
      Named.of("core-only result", new TestCase("src/test/resources/dac/aoml/5903712/profiles/D5903712_149.nc",
        "src/test/resources/dac/aoml/5903712/profiles/BD5903712_149.nc",
        "src/test/resources/dac/aoml/5903712/5903712_meta.nc",
        "src/test/resources/dac/aoml/5903712/profiles/SD5903712_149.nc"))
    );
  }

  private static Stream<Named<TestCase>> dacTestCases() throws IOException {
    Path basePath = Paths.get("---CHANGE TO LOCAL DAC PATH---");
    return Files.walk(basePath, 2)
      .filter(path -> path.getNameCount() == basePath.getNameCount() + 2)
      .flatMap(path -> {
        try {
          String profileName = path.getFileName().toString();
          return Files.list(path.resolve("profiles"))
            .map(p -> Pair.of(p.getFileName().toString().split(profileName)[1], p))
            .collect(Collectors.groupingBy(p -> p.getLeft().replace("_", "").replace(".nc", ""))).entrySet().stream()
            .map(entry -> new SimpleEntry<>("%s_%s".formatted(profileName, entry.getKey()), Pair.of(
              path.resolve("%s_meta.nc".formatted(profileName)),
              entry.getValue().stream().map(Pair::getRight).toList())));
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }).map(entry -> {
        Pair<Path, List<Path>> value = entry.getValue();
        List<Path> right = value.getRight();
        return Named.of(entry.getKey(), new TestCase(
          right.stream().filter(path -> path.toFile().getName().startsWith("D")).findFirst().map(Path::toString).orElse(null),
          right.stream().filter(path -> path.toFile().getName().startsWith("B")).findFirst().map(Path::toString).orElse(null),
          value.getLeft().toString(),
          right.stream().filter(path -> path.toFile().getName().startsWith("S")).findFirst().map(Path::toString).orElse(null)
        ));
      });
  }

  @ParameterizedTest
  @MethodSource("dacTestCases")
  void test(TestCase testCase) throws Exception {
    Path cProfilePath = Paths.get(testCase.cProfilePath);
    Path bProfilePath = Paths.get(testCase.bProfilePath);
    Path metaPath = Paths.get(testCase.metaPath);
    Path outputPath = Paths.get("target/output/output.nc");
    Path expectedPath = Paths.get(testCase.expectedPath);
    DefaultSyntheticProfileMerger syntheticProfileMerger = new DefaultSyntheticProfileMerger();
    syntheticProfileMerger.mergeProfiles(cProfilePath, bProfilePath, metaPath, outputPath);

    try (
        ArgoSyntheticProfileV13Reader reader = new ArgoSyntheticProfileV13Reader(outputPath);
        ArgoSyntheticProfileV13Reader expectedReader = new ArgoSyntheticProfileV13Reader(expectedPath)
    ) {


      List<ArgoSyntheticProfileV13> profiles = reader.getMultiProfile().getProfiles();
      List<ArgoSyntheticProfileV13> expectedProfiles = expectedReader.getMultiProfile().getProfiles();
      assertEquals(1, profiles.size());
      assertEquals(1, expectedProfiles.size());
      ArgoSyntheticProfileV13 profile = profiles.getFirst();
      ArgoSyntheticProfileV13 expectedProfile = expectedProfiles.getFirst();

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
      assertEquals(expectedProfile.getLatitude(), profile.getLatitude());
      assertEquals(expectedProfile.getLongitude(), profile.getLongitude());
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
        assertEquals(expectedCalibrations.size(), calibrations.size(), "%s calibrations not of expected size".formatted(parameterName));
        for (int i = 0; i < expectedCalibrations.size(); i++) {
          assertCalibrationsEqual(expectedCalibrations.get(i), calibrations.get(i));
        }
      }




      ArgoSyntheticProfileV13Parameter temp = parameters.stream().filter(p -> p.getParameterName().equals("TEMP")).findFirst().orElseThrow();
      ArgoSyntheticProfileV13Parameter expectedTemp = expectedParameters.stream().filter(p -> p.getParameterName().equals("TEMP")).findFirst()
          .orElseThrow();
      assertParameterValuesEqual(expectedTemp, temp);

      ArgoSyntheticProfileV13Parameter pSal = parameters.stream().filter(p -> p.getParameterName().equals("PSAL")).findFirst().orElseThrow();
      ArgoSyntheticProfileV13Parameter expectedPSal = expectedParameters.stream().filter(p -> p.getParameterName().equals("PSAL")).findFirst()
          .orElseThrow();
      assertParameterValuesEqual(expectedPSal, pSal);

      ArgoSyntheticProfileV13Parameter downwellingPar = parameters.stream().filter(p -> p.getParameterName().equals("DOWNWELLING_PAR")).findFirst().orElseThrow();
      ArgoSyntheticProfileV13Parameter expectedDownwellingPar = expectedParameters.stream().filter(p -> p.getParameterName().equals("DOWNWELLING_PAR")).findFirst()
          .orElseThrow();
      assertParameterValuesEqual(expectedDownwellingPar, downwellingPar);

      ArgoSyntheticProfileV13Parameter d380 = parameters.stream().filter(p -> p.getParameterName().equals("DOWN_IRRADIANCE380")).findFirst().orElseThrow();
      ArgoSyntheticProfileV13Parameter expectedD380 = expectedParameters.stream().filter(p -> p.getParameterName().equals("DOWN_IRRADIANCE380")).findFirst()
          .orElseThrow();
      assertParameterValuesEqual(expectedD380, d380);

      ArgoSyntheticProfileV13Parameter d412 = parameters.stream().filter(p -> p.getParameterName().equals("DOWN_IRRADIANCE412")).findFirst().orElseThrow();
      ArgoSyntheticProfileV13Parameter expectedD412 = expectedParameters.stream().filter(p -> p.getParameterName().equals("DOWN_IRRADIANCE412")).findFirst()
          .orElseThrow();
      assertParameterValuesEqual(expectedD412, d412);

      ArgoSyntheticProfileV13Parameter d490 = parameters.stream().filter(p -> p.getParameterName().equals("DOWN_IRRADIANCE490")).findFirst().orElseThrow();
      ArgoSyntheticProfileV13Parameter expectedD490 = expectedParameters.stream().filter(p -> p.getParameterName().equals("DOWN_IRRADIANCE490")).findFirst()
          .orElseThrow();
      assertParameterValuesEqual(expectedD490, d490);

      ArgoSyntheticProfileV13Parameter doxyPar = parameters.stream().filter(p -> p.getParameterName().equals("DOXY")).findFirst().orElseThrow();
      ArgoSyntheticProfileV13Parameter expectedDoxyPar = expectedParameters.stream().filter(p -> p.getParameterName().equals("DOXY")).findFirst()
          .orElseThrow();
      assertParameterValuesEqual(expectedDoxyPar, doxyPar);


      ArgoSyntheticProfileV13Parameter nitrate = parameters.stream().filter(p -> p.getParameterName().equals("NITRATE")).findFirst().orElse(null);
      ArgoSyntheticProfileV13Parameter expectedNitrate = expectedParameters.stream().filter(p -> p.getParameterName().equals("NITRATE")).findFirst().orElse(null);
      assertParameterValuesEqual(expectedNitrate, nitrate);

    }

  }

  private static void assertParametersEqual(ArgoSyntheticProfileV13Parameter expectedParam, ArgoSyntheticProfileV13Parameter actualParam) {
    float[] expected = fromList(expectedParam.getLevels().stream().map(ArgoSyntheticProfileV13Level::getValue).toList(),
        Float.MAX_VALUE - 1f);
    float[] actual = fromList(actualParam.getLevels().stream().map(ArgoSyntheticProfileV13Level::getValue).toList(), Float.MAX_VALUE - 1f);
    assertArrayEquals(expected, actual, 0.0001f, "%s values not equal".formatted(expectedParam.getParameterName()));
  }

  private static void assertAdjustedParametersEqual(ArgoSyntheticProfileV13Parameter expectedParam, ArgoSyntheticProfileV13Parameter actualParam) {
    float[] expected = fromList(expectedParam.getLevels().stream().map(ArgoSyntheticProfileV13Level::getAdjustedValue).toList(),
        Float.MAX_VALUE - 1f);
    float[] actual = fromList(actualParam.getLevels().stream().map(ArgoSyntheticProfileV13Level::getAdjustedValue).toList(), Float.MAX_VALUE - 1f);
    assertArrayEquals(expected, actual, 0.001f, "%s adjusted values not equal".formatted(expectedParam.getParameterName()));
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
    assertEquals(expected, actual, "%s QC values do not equal expected".formatted(expectedParam.getParameterName()));
  }

  private static void assertAdjustedQcEqual(ArgoSyntheticProfileV13Parameter expectedParam, ArgoSyntheticProfileV13Parameter actualParam) {
    List<String> expected = expectedParam.getLevels().stream().map(ArgoSyntheticProfileV13Level::getAdjustedQc).toList();
    List<String> actual = actualParam.getLevels().stream().map(ArgoSyntheticProfileV13Level::getAdjustedQc).toList();
    assertEquals(expected, actual, "%s adjusted QC values not equal".formatted(expectedParam.getParameterName()));
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