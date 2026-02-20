package edu.colorado.cires.argonaut.file.local;

import edu.colorado.cires.argonaut.file.core.FileStore;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;

public class LocalFileStore implements FileStore {

  private Path rootPath;

  public void setRootPath(Path root) {
    this.rootPath = root;
  }

  @Override
  public String getRoot() {
    return rootPath.toString();
  }

  @Override
  public void move(String from, String to) {
    Path toPath = Paths.get(to);
    Path fromPath = Paths.get(from);
    copy(fromPath, toPath, true);
  }

  private static void copy(Path fromPath, Path toPath, boolean delete) {
    try {
      Path parent = toPath.getParent();
      if (parent != null) {
        Files.createDirectories(parent);
      }
      Files.copy(fromPath, toPath, StandardCopyOption.REPLACE_EXISTING);
      if (delete) {
        Files.delete(fromPath);
      }
    } catch (IOException e) {
      throw new RuntimeException("Unable to move " + fromPath + " -> " + toPath, e);
    }
  }

  @Override
  public String appendToPath(String base, String... parts) {
    Path basePath = Paths.get(base);
    for (String part : parts) {
      basePath = basePath.resolve(part);
    }
    return basePath.normalize().toString();
  }

  @Override
  public String getFileName(String path) {
    Path fn = Paths.get(path).getFileName();
    if (fn != null) {
      return fn.toString();
    }
    return null;
  }

  @Override
  public void downloadLocalFile(String path, Path localFile) throws IOException {
    Path fromPath = Paths.get(path);
    copy(fromPath, localFile, false);
  }

  @Override
  public void uploadLocalFile(Path localFile, String path) throws IOException {
    Path toPath = Paths.get(path);
    copy(localFile, toPath, false);
  }

  @Override
  public InputStream getInputStream(String path) throws IOException {
    return Files.newInputStream(Paths.get(path));
  }
}
