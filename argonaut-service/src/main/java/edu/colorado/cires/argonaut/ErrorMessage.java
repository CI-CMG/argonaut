package edu.colorado.cires.argonaut;

import java.util.Objects;

public class ErrorMessage {
  private final String ncFile;
  private final String message;


  public ErrorMessage(String ncFile, String message) {
    this.ncFile = ncFile;
    this.message = message;
  }

  public String getNcFile() {
    return ncFile;
  }

  public String getMessage() {
    return message;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ErrorMessage that = (ErrorMessage) o;
    return Objects.equals(ncFile, that.ncFile) && Objects.equals(message, that.message);
  }

  @Override
  public int hashCode() {
    return Objects.hash(ncFile, message);
  }

  @Override
  public String toString() {
    return "ErrorMessage{" +
        "ncFile='" + ncFile + '\'' +
        ", message='" + message + '\'' +
        '}';
  }

}
