package edu.colorado.cires.argonaut.processor.core;

import edu.colorado.cires.argonaut.file.core.FileStore;
import edu.colorado.cires.argonaut.file.local.LocalFileStore;
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
import java.util.Objects;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class TestFileStore implements FileStore {

  private static final Logger LOGGER = LoggerFactory.getLogger(LocalFileStore.class);
  private final ReentrantReadWriteLock deleteEmptyLock = new ReentrantReadWriteLock();

  private final Path rootPath;
  private String stopDeleteEmptyDirsAt;

  TestFileStore(Path rootPath) {
    this.rootPath = rootPath;
  }

  public void setStopDeleteEmptyDirsAt(String stopDeleteEmptyDirsAt) {
    this.stopDeleteEmptyDirsAt = stopDeleteEmptyDirsAt;
  }

  @Override
  public String getRoot() {
    return rootPath.toString();
  }

  @Override
  public void move(String from, String to) {
    Path toPath = validatePath(to);
    Path fromPath = Paths.get(from);
    copyOrMove(fromPath, toPath, true);
  }

  private void copyOrMove(Path fromPath, Path toPath, boolean move) {
    Path deletedParentPath = null;
    deleteEmptyLock.readLock().lock();
    try {
      if (!Files.exists(fromPath)) {
        throw new IllegalStateException("File " + fromPath + " does not exist");
      }

      try {
        Path parent = toPath.getParent();
        if (parent != null) {
          Files.createDirectories(parent);
        }
        if (move) {
          Path fromPathParent = fromPath.getParent();
          Files.move(fromPath, toPath, StandardCopyOption.REPLACE_EXISTING);
          deletedParentPath = fromPathParent;
        } else {
          Files.copy(fromPath, toPath, StandardCopyOption.REPLACE_EXISTING);
        }
      } catch (IOException e) {
        throw new RuntimeException("Unable to move " + fromPath + " -> " + toPath, e);
      }
    } finally {
      deleteEmptyLock.readLock().unlock();
    }

    if (deletedParentPath != null) {
      deleteEmptyLock.writeLock().lock();
      try {
        deleteEmptyDirectories(deletedParentPath);
      } catch (IOException e) {
        LOGGER.warn("Unable to delete empty parent directories for {}", toPath, e);
      } finally {
        deleteEmptyLock.writeLock().unlock();
      }
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
    copyOrMove(fromPath, localFile, false);
  }

  @Override
  public void uploadLocalFile(Path localFile, String path) throws IOException {
    Path toPath = validatePath(path);
    copyOrMove(localFile, toPath, false);
  }

  @Override
  public InputStream getInputStream(String path) throws IOException {
    deleteEmptyLock.readLock().lock();
    try {
      return Files.newInputStream(validatePath(path));
    } finally {
      deleteEmptyLock.readLock().unlock();
    }
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
    deleteEmptyLock.readLock().lock();
    try {
      Path toPath = validatePath(path);
      Path parent = toPath.getParent();
      if (parent != null) {
        Files.createDirectories(parent);
      }
      return Files.newOutputStream(toPath, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
    } finally {
      deleteEmptyLock.readLock().unlock();
    }
  }

  private Path delete(Path toPath) {
    if (Files.isDirectory(toPath)) {
      throw new IllegalArgumentException("Path to delete was not a file: " + toPath);
    }
    try {
      Files.delete(toPath);
    } catch (IOException e) {
      throw new RuntimeException("Unable to delete " + toPath, e);
    }
    return toPath;
  }

  @Override
  public void delete(String path) {
    Path toPath;
    deleteEmptyLock.readLock().lock();
    try {
      toPath = delete(validatePath(path));
    } finally {
      deleteEmptyLock.readLock().unlock();
    }
    deleteEmptyLock.writeLock().lock();
    try {
      deleteEmptyDirectories(toPath.getParent());
    } catch (IOException e) {
      LOGGER.warn("Unable to delete empty parent directories for {}", toPath, e);
    } finally {
      deleteEmptyLock.writeLock().unlock();
    }
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
    if (Objects.requireNonNull(dir.getFileName()).toString().equals(stopDeleteEmptyDirsAt)) {
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
    deleteEmptyLock.readLock().lock();
    try {
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
    } finally {
      deleteEmptyLock.readLock().unlock();
    }
  }
}
