package edu.colorado.cires.argonaut.core.merge.synthetic;

import java.io.IOException;
import java.nio.file.Path;

public interface SyntheticProfileMerger {

  void mergeProfiles(Path cProfilePath, Path bProfilePath, Path metaPath, Path outputPath) throws IOException;
}
