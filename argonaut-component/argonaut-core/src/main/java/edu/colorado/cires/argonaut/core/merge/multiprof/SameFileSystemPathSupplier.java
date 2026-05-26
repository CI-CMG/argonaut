package edu.colorado.cires.argonaut.core.merge.multiprof;

import java.nio.file.Path;
import java.util.Objects;

public class SameFileSystemPathSupplier implements LocalPathSupplier {

  private final Path path;

  public SameFileSystemPathSupplier(Path path) {
    this.path = path;
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
