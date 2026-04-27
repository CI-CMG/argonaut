package edu.colorado.cires.argonaut.core.merge.synthetic;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.colorado.cires.argonaut.core.netcdf.synthprofile.v13.ArgoSyntheticProfileV13;
import edu.colorado.cires.argonaut.core.netcdf.synthprofile.v13.ArgoSyntheticProfileV13Level;
import edu.colorado.cires.argonaut.core.netcdf.synthprofile.v13.ArgoSyntheticProfileV13Parameter;
import edu.colorado.cires.argonaut.core.netcdf.synthprofile.v13.ArgoSyntheticProfileV13Reader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SyntheticProfileMergerTest {

  @BeforeEach
  public void setup() {
    FileUtils.deleteQuietly(Paths.get("target/output").toFile());
  }

  @Test
  public void test() throws Exception {

    Path cProfilePath = Paths.get("src/test/resources/dac/meds/4902691/profiles/R4902691_034.nc");
    Path bProfilePath = Paths.get("src/test/resources/dac/meds/4902691/profiles/BR4902691_034.nc");
    Path metaPath = Paths.get("src/test/resources/dac/meds/4902691/4902691_meta.nc");
    Path outputPath = Paths.get("target/output/dac/meds/4902691/profiles/SR4902691_034.nc");
    Path expectedPath = Paths.get("src/test/resources/dac/meds/4902691/profiles/SR4902691_034.nc");
    SyntheticProfileMerger syntheticProfileMerger = new SyntheticProfileMerger(cProfilePath, bProfilePath, metaPath, outputPath);
    syntheticProfileMerger.mergeProfiles();

    try (
        ArgoSyntheticProfileV13Reader reader = new ArgoSyntheticProfileV13Reader(outputPath);
        ArgoSyntheticProfileV13Reader expectedReader = new ArgoSyntheticProfileV13Reader(expectedPath);
    ) {
      List<ArgoSyntheticProfileV13> profiles = reader.getMultiProfile().getProfiles();
      List<ArgoSyntheticProfileV13> expectedProfiles = expectedReader.getMultiProfile().getProfiles();
      assertEquals(1, profiles.size());
      assertEquals(1, expectedProfiles.size());
      ArgoSyntheticProfileV13 profile = profiles.get(0);
      ArgoSyntheticProfileV13 expectedProfile = profiles.get(0);
      List<ArgoSyntheticProfileV13Parameter> parameters = profile.getParameters();
      List<ArgoSyntheticProfileV13Parameter> expectedParameters = expectedProfile.getParameters();

      // TODO only testing PRES and TEMP right now
      ArgoSyntheticProfileV13Parameter pres = parameters.stream().filter(p -> p.getParameterName().equals("PRES")).findFirst().orElseThrow();
      ArgoSyntheticProfileV13Parameter expectedPres = expectedParameters.stream().filter(p -> p.getParameterName().equals("PRES")).findFirst()
          .orElseThrow();
      assertParametersEqual(expectedPres, pres);

      ArgoSyntheticProfileV13Parameter temp = parameters.stream().filter(p -> p.getParameterName().equals("TEMP")).findFirst().orElseThrow();
      ArgoSyntheticProfileV13Parameter expectedTemp = expectedParameters.stream().filter(p -> p.getParameterName().equals("TEMP")).findFirst()
          .orElseThrow();
      assertParametersEqual(expectedTemp, temp);

    }

  }

  private static void assertParametersEqual(ArgoSyntheticProfileV13Parameter expectedParam, ArgoSyntheticProfileV13Parameter actualParam) {
    float[] expected = fromList(expectedParam.getLevels().stream().map(ArgoSyntheticProfileV13Level::getOriginalValue).toList(),
        Float.MAX_VALUE - 1f);
    float[] actual = fromList(actualParam.getLevels().stream().map(ArgoSyntheticProfileV13Level::getOriginalValue).toList(), Float.MAX_VALUE - 1f);
    assertArrayEquals(expected, actual, 0.0001f);
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