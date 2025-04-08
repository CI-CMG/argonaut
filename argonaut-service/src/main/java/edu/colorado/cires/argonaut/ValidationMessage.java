package edu.colorado.cires.argonaut;

import java.nio.file.Path;

public class ValidationMessage {
  private final Path ncFile;
  private final Path fileCheckXmlFile;

  public ValidationMessage(Path ncFile, Path fileCheckXmlFile) {
    this.ncFile = ncFile;
    this.fileCheckXmlFile = fileCheckXmlFile;
  }

  public Path getNcFile() {
    return ncFile;
  }

  public Path getFileCheckXmlFile() {
    return fileCheckXmlFile;
  }
}
