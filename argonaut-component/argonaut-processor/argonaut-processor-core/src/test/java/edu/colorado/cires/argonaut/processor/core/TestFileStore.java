package edu.colorado.cires.argonaut.processor.core;

import edu.colorado.cires.argonaut.file.core.FileStore;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

class TestFileStore implements FileStore {

  private final Path rootPath;

  TestFileStore(Path rootPath) {
    this.rootPath = rootPath;
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

  private void copy(Path fromPath, Path toPath, boolean delete) {
    if (!Files.exists(fromPath)) {
      throw new IllegalStateException("File " + fromPath + " does not exist");
    }

    try {
      Path parent = toPath.getParent();
      if (parent != null) {
        Files.createDirectories(parent);
      }
      Files.copy(fromPath, toPath, StandardCopyOption.REPLACE_EXISTING);
      if (delete) {
        delete(fromPath);
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
    Path toPath = validatePathNoThrow(Paths.get(path));
    if (toPath == null) {
      Path rootAbsolutePath = rootPath.toAbsolutePath().normalize();
      Path absPath = Paths.get(path).toAbsolutePath().normalize();
      throw new IllegalArgumentException("Path " + absPath + " does not start with " + rootAbsolutePath);
    }
    return toPath;
  }

  private Path validatePathNoThrow(Path path) {
    Path rootAbsolutePath = rootPath.toAbsolutePath().normalize();
    Path toPath = path.toAbsolutePath().normalize();
    if (!toPath.startsWith(rootAbsolutePath)) {
      return null;
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

  private void delete(Path toPath) {
    if (Files.isDirectory(toPath)) {
      throw new IllegalArgumentException("Path to delete was not a file: " + toPath);
    }
    try {
      Files.delete(toPath);
    } catch (IOException e) {
      throw new RuntimeException("Unable to delete " + toPath, e);
    }
    try {
      deleteEmptyDirectories(toPath.getParent());
    } catch (IOException e) {
      //
    }
  }

  @Override
  public void delete(String path) {
    delete(validatePath(path));
  }

  private static boolean isEmptyDir(Path dir) throws IOException {
    long count;
    try (Stream<Path> stream = Files.list(dir)) {
      count = stream.count();
    }
    return count == 0;
  }

  private void deleteEmptyDirectories(Path dir) throws IOException {
    if (dir == null) {
      return;
    }
    Path rootAbsolutePath = rootPath.toAbsolutePath().normalize();
    Path absolutePath = validatePathNoThrow(dir);
    if (absolutePath != null
        && !absolutePath.equals(rootAbsolutePath)
        && Files.isDirectory(absolutePath)
        && isEmptyDir(absolutePath)
    ) {
      Path parent = absolutePath.getParent();
      Files.delete(absolutePath);
      deleteEmptyDirectories(parent);
    }
  }

  @Override
  public boolean fileExists(String path) {
    return Files.isRegularFile(validatePath(path));
  }

  @Override
  public List<String> listFileNamesInDirectory(String pathPrefix, boolean includeDirectories) {
    Path path = validatePath(pathPrefix);
    if (!Files.isDirectory(path)) {
      return Collections.emptyList();
    }
    Predicate<Path> filter = includeDirectories ? (p) -> Files.isDirectory(p) || Files.isRegularFile(p) : Files::isRegularFile;
    try (Stream<Path> stream = Files.list(path)) {
      return stream
          .filter(filter)
          .map(Path::getFileName)
          .map(Path::toString)
          .toList();
    } catch (IOException e) {
      throw new RuntimeException("Unable to list files in " + path, e);
    }
  }
}
