package edu.colorado.cires.argonaut.metadata.jpa.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "argonaut_synthetic_merge_metadata", indexes = {
    @Index(name = "argonaut_synthetic_merge_metadata_metadata_idx", columnList = "argo_float_metadata"),
    @Index(name = "argonaut_synthetic_merge_metadata_profile_idx", columnList = "argo_float_profile")
})
public class ArgonautSyntheticMergeMetadataEntity {

  @Id
  @Column(name = "id", nullable = false, length = 36)
  private String id;

  @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.REFRESH})
  @JoinColumn(name = "argo_float_metadata", nullable = false)
  private ArgoFloatMetadataEntity argoFloatMetadata;

  @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.REFRESH})
  @JoinColumn(name = "argo_float_profile", nullable = false)
  private ArgoFloatProfileEntity argoFloatProfile;

  @Column(name = "synthetic_merge_time", nullable = false)
  private ZonedDateTime syntheticMergeTime;

  public UUID getId() {
    return id == null ? null : UUID.fromString(id);
  }

  public void setId(UUID id) {
    this.id = id == null ? null : id.toString();
  }

  public ArgoFloatMetadataEntity getArgoFloatMetadata() {
    return argoFloatMetadata;
  }

  public void setArgoFloatMetadata(ArgoFloatMetadataEntity metadata) {
    this.argoFloatMetadata = metadata;
  }

  public ArgoFloatProfileEntity getArgoFloatProfile() {
    return argoFloatProfile;
  }

  public void setArgoFloatProfile(ArgoFloatProfileEntity profile) {
    this.argoFloatProfile = profile;
  }

  public ZonedDateTime getSyntheticMergeTime() {
    return syntheticMergeTime;
  }

  public void setSyntheticMergeTime(ZonedDateTime syntheticMergeTime) {
    this.syntheticMergeTime = syntheticMergeTime;
  }
}
