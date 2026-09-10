package edu.colorado.cires.argonaut.core.merge.multiprof;

import java.nio.file.Path;

public interface LocalPathSupplier {

  String getDac();
  String getFileName();
  void prepare();
  Path getLocalPath();
  void cleanUp();

}
