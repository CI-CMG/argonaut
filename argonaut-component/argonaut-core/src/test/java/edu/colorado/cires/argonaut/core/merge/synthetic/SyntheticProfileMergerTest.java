package edu.colorado.cires.argonaut.core.merge.synthetic;

import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;

public class SyntheticProfileMergerTest {

  @Test
  public void test() throws Exception {

    Path cProfilePath  = Paths.get("src/test/resources/dac/meds/4902691/profiles/R4902691_034.nc");
    Path bProfilePath = Paths.get("src/test/resources/dac/meds/4902691/profiles/BR4902691_034.nc");
    Path metaPath = Paths.get("src/test/resources/dac/meds/4902691/4902691_meta.nc");
    Path outputPath = Paths.get("target/output/dac/meds/4902691/profiles/SR4902691_034.nc");
    SyntheticProfileMerger syntheticProfileMerger = new SyntheticProfileMerger(cProfilePath, bProfilePath, metaPath, outputPath);
    syntheticProfileMerger.mergeProfiles();

  }

}