package edu.colorado.cires.argonaut;

import java.nio.file.Path;
import java.util.Objects;

public class ErrorMessage {
  private final Path file;
  private final String message;

  public ErrorMessage(Path file, String message) {
    this.file = file;
    this.message = message;
  }

  public Path getFile() {
    return file;
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
    return Objects.equals(file, that.file) && Objects.equals(message, that.message);
  }

  @Override
  public int hashCode() {
    return Objects.hash(file, message);
  }

  @Override
  public String toString() {
    return "ErrorMessage{" +
        "file=" + file +
        ", message='" + message + '\'' +
        '}';
  }
}
