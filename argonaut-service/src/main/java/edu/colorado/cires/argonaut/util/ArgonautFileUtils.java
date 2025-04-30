package edu.colorado.cires.argonaut.util;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.commons.io.IOUtils;

public class ArgonautFileUtils {

  public static void createDirectories(Path path) {
    try {
      Files.createDirectories(path);
    } catch (IOException e) {
      throw new RuntimeException("Unable to create directory: " + path, e);
    }
  }

  public static void copy(URL resource, Path dest) {
    try {
      IOUtils.copy(resource, dest.toFile());
    } catch (IOException e) {
      throw new RuntimeException("Unable to copy resource: " + resource + " to " + dest, e);
    }
  }

  public static void move(Path src, Path dest) {
    try{
      Files.move(src, dest);
    } catch (IOException e) {
      throw new RuntimeException("Unable to move " + src + " to " + dest, e);
    }
  }

  private ArgonautFileUtils() {

  }
}
