package edu.colorado.cires.argonaut.file.local;

import edu.colorado.cires.argonaut.file.core.FileStore;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;

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
    Path toPath = validatePath(to);
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
    Path toPath = validatePath(path);
    copy(localFile, toPath, false);
  }

  @Override
  public InputStream getInputStream(String path) throws IOException {
    return Files.newInputStream(validatePath(path));
  }

  private Path validatePath(String path) {
    Path rootAbsolutePath = rootPath.toAbsolutePath().normalize();
    Path toPath = Paths.get(path).toAbsolutePath().normalize();
    if (!toPath.startsWith(rootAbsolutePath)) {
      throw new IllegalArgumentException("Path " + toPath + " does not start with " + rootAbsolutePath);
    }
    return toPath;
  }

  @Override
  public OutputStream getOutputStream(String path) throws IOException {
    Path toPath = validatePath(path);
    Path parent = toPath.getParent();
    if (parent != null) {
      Files.createDirectories(parent);
    }
    return Files.newOutputStream(toPath, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
  }

  @Override
  public void delete(String path) {
    Path toPath = validatePath(path);
    //TODO delete empty directories
    try {
      Files.delete(toPath);
    } catch (IOException e) {
      throw new RuntimeException("Unable to delete " + toPath, e);
    }
  }

  @Override
  public boolean fileExists(String path) {
    return Files.isRegularFile(validatePath(path));
  }
}
