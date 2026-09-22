package edu.colorado.cires.argonaut.core.merge.multiprof;

import java.nio.file.Path;
import java.time.Instant;

public interface LocalPathSupplier {

  Instant getJulD();
  String getDac();
  String getFileName();
  void prepare();
  Path getLocalPath();
  void cleanUp();

}
