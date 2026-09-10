package edu.colorado.cires.argonaut.core.merge.multiprof;

import java.nio.file.Path;
import java.util.Objects;

public class SameFileSystemPathSupplier implements LocalPathSupplier {

  private final Path path;
  private final String dac;

  public SameFileSystemPathSupplier(Path path, String dac) {
    this.path = path;
    this.dac = dac;
  }

  @Override
  public String getDac() {
    return dac;
  }

  @Override
  public String getFileName() {
    return Objects.requireNonNull(path.getFileName()).toString();
  }

  @Override
  public void prepare() {
    // no-op, on same filesystem
  }

  @Override
  public Path getLocalPath() {
    return path;
  }

  @Override
  public void cleanUp() {
    // no-op, on same filesystem
  }
}
