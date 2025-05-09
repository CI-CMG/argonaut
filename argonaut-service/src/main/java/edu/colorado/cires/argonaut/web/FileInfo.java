package edu.colorado.cires.argonaut.web;

import java.util.Locale;
import java.util.Objects;

public class FileInfo implements Comparable<FileInfo> {

  private final String name;
  private final boolean isDirectory;
  private final String lastModified;
  private final String size;
  private final boolean isParentDirectory;
  private final String path;

  public FileInfo(String name, boolean isDirectory, String lastModified, String size, boolean isParentDirectory, String path) {
    this.name = name;
    this.isDirectory = isDirectory;
    this.lastModified = lastModified;
    this.size = size;
    this.isParentDirectory = isParentDirectory;
    this.path = path;
  }

  public String getPath() {
    return path;
  }

  public boolean isParentDirectory() {
    return isParentDirectory;
  }

  public String getName() {
    return name;
  }

  public boolean isDirectory() {
    return isDirectory;
  }

  public String getLastModified() {
    return lastModified;
  }

  public String getSize() {
    return size;
  }

  public String getAlt() {
    if (isParentDirectory) {
      return "PARENTDIR";
    }
    if (isDirectory) {
      return "DIR";
    }
    return "FILE";
  }

  public String getImg() {
    if (isParentDirectory) {
      return "back";
    }
    if (isDirectory) {
      return "folder";
    }
    if (name.endsWith(".gz") || name.endsWith(".zip")) {
      return "compressed";
    }
    if (name.endsWith(".txt")) {
      return "text";
    }
    return "unknown";
  }


  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FileInfo fileInfo = (FileInfo) o;
    String nameLower = name == null ? null : name.toLowerCase(Locale.ENGLISH);
    String otherNameLower = fileInfo.name == null ? null : fileInfo.name.toLowerCase(Locale.ENGLISH);
    return isParentDirectory == fileInfo.isParentDirectory && Objects.equals(nameLower, otherNameLower);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, isParentDirectory);
  }

  @Override
  public int compareTo(FileInfo fileInfo) {
    if (fileInfo == null) {
      return -1;
    }
    if (isParentDirectory) {
      return -1;
    }
    if (isDirectory == fileInfo.isDirectory) {
      String nameLower = name == null ? null : name.toLowerCase(Locale.ENGLISH);
      String otherNameLower = fileInfo.name == null ? null : fileInfo.name.toLowerCase(Locale.ENGLISH);
      if (nameLower == null) {
        return 1;
      }
      if (otherNameLower == null) {
        return -1;
      }
      return nameLower.compareTo(otherNameLower);
    } else if (isDirectory) {
      return -1;
    } else {
      return 1;
    }

  }
}
