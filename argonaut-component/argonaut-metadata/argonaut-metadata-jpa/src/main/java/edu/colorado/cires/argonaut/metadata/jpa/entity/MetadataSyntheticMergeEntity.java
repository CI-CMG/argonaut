package edu.colorado.cires.argonaut.metadata.jpa.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "metadata_synthetic_merge")
public class MetadataSyntheticMergeEntity {

  @Id
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.REFRESH})
  @JoinColumn(name = "metadata", nullable = false)
  private MetadataFileEntity metadata;

  @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.REFRESH})
  @JoinColumn(name = "profile", nullable = false)
  private ProfileFileEntity profile;

  @Column(name = "synthetic_merge_time", nullable = false)
  private ZonedDateTime syntheticMergeTime;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public MetadataFileEntity getMetadata() {
    return metadata;
  }

  public void setMetadata(MetadataFileEntity metadata) {
    this.metadata = metadata;
  }

  public ProfileFileEntity getProfile() {
    return profile;
  }

  public void setProfile(ProfileFileEntity profile) {
    this.profile = profile;
  }

  public ZonedDateTime getSyntheticMergeTime() {
    return syntheticMergeTime;
  }

  public void setSyntheticMergeTime(ZonedDateTime syntheticMergeTime) {
    this.syntheticMergeTime = syntheticMergeTime;
  }
}
