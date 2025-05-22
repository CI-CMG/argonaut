package edu.colorado.cires.argonaut.message;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Objects;


//TODO add JSON any setter / getter
@JsonDeserialize(builder = FloatMergeGroup.Builder.class)
public class FloatMergeGroup {

  public static Builder builder() {
    return new Builder();
  }

  public static Builder builder(FloatMergeGroup source) {
    return new Builder(source);
  }

  public static final class Builder {

    private String dac;
    private String floatId;

    private Builder() {

    }

    private Builder(FloatMergeGroup source) {
      this.dac = source.dac;
      this.floatId = source.floatId;
    }

    public Builder withDac(String dac) {
      this.dac = dac;
      return this;
    }

    public Builder withFloatId(String floatId) {
      this.floatId = floatId;
      return this;
    }

    public FloatMergeGroup build() {
      return new FloatMergeGroup(dac, floatId);
    }
  }

  private final String dac;
  private final String floatId;

  private FloatMergeGroup(String dac, String floatId) {
    this.dac = dac;
    this.floatId = floatId;
  }

  public String getDac() {
    return dac;
  }

  public String getFloatId() {
    return floatId;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FloatMergeGroup that = (FloatMergeGroup) o;
    return Objects.equals(dac, that.dac) && Objects.equals(floatId, that.floatId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(dac, floatId);
  }

  @Override
  public String toString() {
    return "FloatMergeGroup{" +
        "dac='" + dac + '\'' +
        ", floatId='" + floatId + '\'' +
        '}';
  }
}
