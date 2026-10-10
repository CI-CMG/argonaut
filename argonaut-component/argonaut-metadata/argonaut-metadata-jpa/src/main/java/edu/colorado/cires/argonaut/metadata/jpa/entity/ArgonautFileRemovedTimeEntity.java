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
@Table(name = "argonaut_file_removed_time", indexes = {
    @Index(name = "argonaut_file_removed_time_metadata_idx", columnList = "argo_float_metadata"),
    @Index(name = "argonaut_file_removed_time_profile_idx", columnList = "argo_float_profile"),
    @Index(name = "argonaut_file_removed_time_file_type_idx", columnList = "file_type"),
    @Index(name = "argonaut_file_removed_time_removed_time_idx", columnList = "removed_time")
})
public class ArgonautFileRemovedTimeEntity {

  @Id
  @Column(name = "id", length = 36, nullable = false)
  private String id;

  @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.REFRESH})
  @JoinColumn(name = "argo_float_metadata")
  private ArgoFloatMetadataEntity argoFloatMetadata;

  @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.REFRESH})
  @JoinColumn(name = "argo_float_profile")
  private ArgoFloatProfileEntity argoFloatProfile;

  @Column(name = "file_type", length = 50, nullable = false)
  private String fileType;

  @Column(name = "removed_time", nullable = false)
  private ZonedDateTime removedTime;

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

  public ZonedDateTime getRemovedTime() {
    return removedTime;
  }

  public void setRemovedTime(ZonedDateTime removedTime) {
    this.removedTime = removedTime;
  }

  public String getFileType() {
    return fileType;
  }

  public void setFileType(String fileType) {
    this.fileType = fileType;
  }
}
