package edu.colorado.cires.argonaut.messaging.core.databind;

import edu.colorado.cires.argonaut.messaging.core.databind.FloatMergeGroup.Builder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import tools.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = ProfileOperation.Builder.class)
public class ProfileOperation {

  public static Builder builder() {
    return new Builder();
  }

  public static Builder builder(ProfileOperation source) {
    return new Builder(source);
  }

  private final String dac;
  private final String profile;
  private final List<String> fileNames;

  private ProfileOperation(String dac, String profile, List<String> fileNames) {
    this.dac = dac;
    this.profile = profile;
    this.fileNames = fileNames;
  }

  public String getDac() {
    return dac;
  }

  public String getProfile() {
    return profile;
  }

  public List<String> getFileNames() {
    return fileNames;
  }

  public static final class Builder {
    private String dac;
    private String profile;
    private List<String> fileNames = Collections.emptyList();

    private Builder() {

    }

    private Builder(ProfileOperation source) {
      this.dac = source.dac;
      this.profile = source.profile;
      this.fileNames = source.fileNames;
    }

    public Builder withDac(String dac) {
      this.dac = dac;
      return this;
    }

    public Builder withProfile(String profile) {
      this.profile = profile;
      return this;
    }

    public Builder withFileNames(List<String> fileNames) {
      if (fileNames == null) {
        this.fileNames = Collections.emptyList();
      } else {
        this.fileNames = Collections.unmodifiableList(new ArrayList<>(fileNames));
      }
      return this;
    }

    public ProfileOperation build() {
      return new ProfileOperation(dac, profile, fileNames);
    }
  }
}
