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
@Table(name = "file_removed_time")
public class FileRemovedTimeEntity {

  @Id
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.REFRESH})
  @JoinColumn(name = "metadata")
  private MetadataFileEntity metadata;

  @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.REFRESH})
  @JoinColumn(name = "profile")
  private ProfileFileEntity profile;

  @Column(name = "file_type", length = 50, nullable = false)
  private String fileType;

  @Column(name = "removed_time", nullable = false)
  private ZonedDateTime removedTime;

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
