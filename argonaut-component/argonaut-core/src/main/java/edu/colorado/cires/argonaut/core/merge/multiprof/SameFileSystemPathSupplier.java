package edu.colorado.cires.argonaut.core.merge.multiprof;

import java.nio.file.Path;
import java.time.Instant;
import java.util.Objects;

public class SameFileSystemPathSupplier implements LocalPathSupplier {

  private final Path path;
  private final String dac;
  private final Instant julD;

  public SameFileSystemPathSupplier(Path path, String dac, Instant julD) {
    this.path = path;
    this.dac = dac;
    this.julD = julD;
  }

  @Override
  public Instant getJulD() {
    return julD;
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
