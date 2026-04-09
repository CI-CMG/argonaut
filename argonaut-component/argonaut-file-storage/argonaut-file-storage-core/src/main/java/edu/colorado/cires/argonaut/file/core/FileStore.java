package edu.colorado.cires.argonaut.file.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;

public interface FileStore {

  String getRoot();

  void move(String from, String to);

  String appendToPath(String base, String... parts);

  String getFileName(String path);

  void downloadLocalFile(String path, Path localFile) throws IOException;

  void uploadLocalFile(Path localFile, String path) throws IOException;

  InputStream getInputStream(String path) throws IOException;

  OutputStream getOutputStream(String path) throws IOException;

  void delete(String path);

  boolean fileExists(String path);
}
