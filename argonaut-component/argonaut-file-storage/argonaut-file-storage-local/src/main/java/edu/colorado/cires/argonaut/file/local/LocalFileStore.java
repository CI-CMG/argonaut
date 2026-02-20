package edu.colorado.cires.argonaut.file.local;

import edu.colorado.cires.argonaut.file.core.FileStore;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

public class LocalFileStore implements FileStore {

  @Override
  public void move(String from, String to) {

  }

  @Override
  public String appendToPath(String base, String... parts) {
    return "";
  }

  @Override
  public String getFileName(String path) {
    return "";
  }

  @Override
  public void downloadLocalFile(String path, Path localFile) throws IOException {

  }

  @Override
  public void uploadLocalFile(Path localFile, String path) throws IOException {

  }

  @Override
  public InputStream getInputStream(String path) throws IOException {
    return null;
  }
}
