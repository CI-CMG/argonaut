package edu.colorado.cires.argonaut.messaging.core.databind;


import java.time.Instant;
import java.util.Objects;
import tools.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = DacSubmittedFileMessage.Builder.class)
public final class DacSubmittedFileMessage {

  public static Builder builder() {
    return new Builder();
  }

  public static Builder builder(DacSubmittedFileMessage source) {
    return new Builder(source);
  }


  public static final class Builder {

    private String path;
    private String processingPath;
    private String processedPath;
    private Instant timestamp;
    private String dac;

    private Builder() {

    }

    private Builder(DacSubmittedFileMessage source) {
      path = source.path;
      timestamp = source.timestamp;
      dac = source.dac;
    }

    public Builder withProcessingPath(String processingPath) {
      this.processingPath = processingPath;
      return this;
    }

    public Builder withProcessedPath(String processedPath) {
      this.processedPath = processedPath;
      return this;
    }

    public Builder withPath(String path) {
      this.path = path;
      return this;
    }

    public Builder withTimestamp(Instant timestamp) {
      this.timestamp = timestamp;
      return this;
    }

    public Builder withDac(String dac) {
      this.dac = dac;
      return this;
    }

    public DacSubmittedFileMessage build() {
      return new DacSubmittedFileMessage(path, processingPath, processedPath, timestamp, dac);
    }
  }

  private final String path;
  private String processingPath;
  private String processedPath;
  private final Instant timestamp;
  private final String dac;

  private DacSubmittedFileMessage(String path, String processingPath, String processedPath, Instant timestamp, String dac) {
    this.path = path;
    this.processingPath = processingPath;
    this.processedPath = processedPath;
    this.timestamp = timestamp;
    this.dac = dac;
  }

  public Instant getTimestamp() {
    return timestamp;
  }

  public String getDac() {
    return dac;
  }

  public String getPath() {
    return path;
  }

  public String getProcessingPath() {
    return processingPath;
  }

  public String getProcessedPath() {
    return processedPath;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DacSubmittedFileMessage that = (DacSubmittedFileMessage) o;
    return Objects.equals(path, that.path) && Objects.equals(processingPath, that.processingPath) && Objects.equals(
        processedPath, that.processedPath) && Objects.equals(timestamp, that.timestamp) && Objects.equals(dac, that.dac);
  }

  @Override
  public int hashCode() {
    return Objects.hash(path, processingPath, processedPath, timestamp, dac);
  }

  @Override
  public String toString() {
    return "DacSubmittedFileMessage{" +
        "path='" + path + '\'' +
        ", processingPath='" + processingPath + '\'' +
        ", processedPath='" + processedPath + '\'' +
        ", timestamp=" + timestamp +
        ", dac='" + dac + '\'' +
        '}';
  }

}
