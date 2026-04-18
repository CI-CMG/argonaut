package edu.colorado.cires.argonaut.core.merge.synthetic;

import java.nio.file.Path;

public class SyntheticMergeFiles {

  private Path metadataFile;
  private Path coreProfile;
  private Path bioProfile;

  public Path getMetadataFile() {
    return metadataFile;
  }

  public void setMetadataFile(Path metadataFile) {
    this.metadataFile = metadataFile;
  }

  public Path getCoreProfile() {
    return coreProfile;
  }

  public void setCoreProfile(Path coreProfile) {
    this.coreProfile = coreProfile;
  }

  public Path getBioProfile() {
    return bioProfile;
  }

  public void setBioProfile(Path bioProfile) {
    this.bioProfile = bioProfile;
  }
}
