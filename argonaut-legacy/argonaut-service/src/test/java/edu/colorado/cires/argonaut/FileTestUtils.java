package edu.colorado.cires.argonaut;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import org.apache.commons.io.FileUtils;

public final class FileTestUtils {

  public static void emptyDirectory(Path dir) {
    if (Files.exists(dir)) {
      try(Stream<Path> stream = Files.list(dir)) {
        stream.forEach(fileOrDir -> FileUtils.deleteQuietly(fileOrDir.toFile()));
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  private FileTestUtils() {

  }
}
