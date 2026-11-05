package edu.colorado.cires.argonaut.core.merge.synthetic;

import edu.colorado.cires.argonaut.core.merge.synthetic.AxisCalculator.SyntheticAxis;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

public class DefaultSyntheticProfileMerger implements SyntheticProfileMerger {

  public void mergeProfiles(Path cProfilePath, Path bProfilePath, Path metaPath, Path outputPath) throws IOException {
    try (MergeContext mergeContext = MergeContext.create(metaPath, cProfilePath, bProfilePath)) {
      SyntheticAxis syntheticAxis = AxisCalculator.calculate(mergeContext);
      Map<String, Map<Long, FloatQc>> interpolatedValues = MergeInterpolator.interpolateValues(mergeContext, syntheticAxis);
      MergeWriter.writeSProfileFile(mergeContext, interpolatedValues, outputPath);
    }

  }

  public record FloatQc(float value, String qc, Float pressureDisplacement,
                         Float adjustedValue, Float adjustedError, String adjustedQc) {}

}
