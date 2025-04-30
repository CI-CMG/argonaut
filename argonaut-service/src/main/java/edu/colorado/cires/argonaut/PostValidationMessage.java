package edu.colorado.cires.argonaut;

import java.nio.file.Path;
import java.util.Objects;

public class PostValidationMessage {
  private final Path ncFile;
  private final Path fileCheckXmlFile;

  public PostValidationMessage(Path ncFile, Path fileCheckXmlFile) {
    this.ncFile = ncFile;
    this.fileCheckXmlFile = fileCheckXmlFile;
  }

  public Path getNcFile() {
    return ncFile;
  }

  public Path getFileCheckXmlFile() {
    return fileCheckXmlFile;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PostValidationMessage that = (PostValidationMessage) o;
    return Objects.equals(ncFile, that.ncFile) && Objects.equals(fileCheckXmlFile, that.fileCheckXmlFile);
  }

  @Override
  public int hashCode() {
    return Objects.hash(ncFile, fileCheckXmlFile);
  }

  @Override
  public String toString() {
    return "ValidationMessage{" +
        "ncFile=" + ncFile +
        ", fileCheckXmlFile=" + fileCheckXmlFile +
        '}';
  }
}
