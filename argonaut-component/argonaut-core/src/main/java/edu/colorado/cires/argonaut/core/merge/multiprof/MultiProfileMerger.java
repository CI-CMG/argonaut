package edu.colorado.cires.argonaut.core.merge.multiprof;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface MultiProfileMerger {

  void mergeProfiles(List<Path> inputFiles, Path outputPath) throws IOException;

}
