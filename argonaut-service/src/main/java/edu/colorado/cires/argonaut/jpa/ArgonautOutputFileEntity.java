package edu.colorado.cires.argonaut.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "argonaut_output_file")
public class ArgonautOutputFileEntity {

  @Id
  @GeneratedValue
  private Long id;

  @Column(nullable = false, length = 10)
  private String dac;

  @Column(name = "file_name", nullable = false, length = 100)
  private String fileName;

  @Column(nullable = false)
  private Instant timestamp;

  @Column(nullable = false)
  private boolean profile;

  @Column(name = "float_id", nullable = false, length = 7)
  private String floatId;

  @Column(nullable = false, length = 1)
  private String region;

  @Column(nullable = false)
  private boolean removed;

  @Column(name = "float_merged", nullable = false)
  private boolean floatMerged;

  @Column(name = "geo_merged", nullable = false)
  private boolean geoMerged;

  @Column(name = "latest_merged", nullable = false)
  private boolean latestMerged;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getDac() {
    return dac;
  }

  public void setDac(String dac) {
    this.dac = dac;
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public Instant getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Instant timestamp) {
    this.timestamp = timestamp;
  }

  public boolean isProfile() {
    return profile;
  }

  public void setProfile(boolean profile) {
    this.profile = profile;
  }

  public String getFloatId() {
    return floatId;
  }

  public void setFloatId(String floatId) {
    this.floatId = floatId;
  }

  public String getRegion() {
    return region;
  }

  public void setRegion(String region) {
    this.region = region;
  }

  public boolean isRemoved() {
    return removed;
  }

  public void setRemoved(boolean removed) {
    this.removed = removed;
  }

  public boolean isFloatMerged() {
    return floatMerged;
  }

  public void setFloatMerged(boolean floatMerged) {
    this.floatMerged = floatMerged;
  }

  public boolean isGeoMerged() {
    return geoMerged;
  }

  public void setGeoMerged(boolean geoMerged) {
    this.geoMerged = geoMerged;
  }

  public boolean isLatestMerged() {
    return latestMerged;
  }

  public void setLatestMerged(boolean latestMerged) {
    this.latestMerged = latestMerged;
  }
}
