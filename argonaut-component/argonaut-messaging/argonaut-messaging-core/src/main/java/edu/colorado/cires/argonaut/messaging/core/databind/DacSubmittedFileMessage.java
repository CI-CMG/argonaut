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
    private Instant timestamp;
    private String dac;

    private Builder() {

    }

    private Builder(DacSubmittedFileMessage source) {
      path = source.path;
      timestamp = source.timestamp;
      dac = source.dac;
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
      return new DacSubmittedFileMessage(path, timestamp, dac);
    }
  }

  private final String path;
  private final Instant timestamp;
  private final String dac;

  private DacSubmittedFileMessage(String path, Instant timestamp, String dac) {
    this.path = path;
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

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    DacSubmittedFileMessage that = (DacSubmittedFileMessage) o;
    return Objects.equals(path, that.path) && Objects.equals(timestamp, that.timestamp) && Objects.equals(dac, that.dac);
  }

  @Override
  public int hashCode() {
    return Objects.hash(path, timestamp, dac);
  }

  @Override
  public String toString() {
    return "DacSubmittedFileMessage{" +
            "path='" + path + '\'' +
            ", timestamp=" + timestamp +
            ", dac='" + dac + '\'' +
            '}';
  }

}
