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

  public ArgonautOutputFileEntity withId(Long id) {
    this.id = id;
    return this;
  }

  public String getDac() {
    return dac;
  }

  public ArgonautOutputFileEntity withDac(String dac) {
    this.dac = dac;
    return this;
  }

  public String getFileName() {
    return fileName;
  }

  public ArgonautOutputFileEntity withFileName(String fileName) {
    this.fileName = fileName;
    return this;
  }

  public Instant getTimestamp() {
    return timestamp;
  }

  public ArgonautOutputFileEntity withTimestamp(Instant timestamp) {
    this.timestamp = timestamp;
    return this;
  }

  public boolean isProfile() {
    return profile;
  }

  public ArgonautOutputFileEntity withProfile(boolean profile) {
    this.profile = profile;
    return this;
  }

  public String getFloatId() {
    return floatId;
  }

  public ArgonautOutputFileEntity withFloatId(String floatId) {
    this.floatId = floatId;
    return this;
  }

  public String getRegion() {
    return region;
  }

  public ArgonautOutputFileEntity withRegion(String region) {
    this.region = region;
    return this;
  }

  public boolean isRemoved() {
    return removed;
  }

  public ArgonautOutputFileEntity withRemoved(boolean removed) {
    this.removed = removed;
    return this;
  }

  public boolean isFloatMerged() {
    return floatMerged;
  }

  public ArgonautOutputFileEntity withFloatMerged(boolean floatMerged) {
    this.floatMerged = floatMerged;
    return this;
  }

  public boolean isGeoMerged() {
    return geoMerged;
  }

  public ArgonautOutputFileEntity withGeoMerged(boolean geoMerged) {
    this.geoMerged = geoMerged;
    return this;
  }

  public boolean isLatestMerged() {
    return latestMerged;
  }

  public ArgonautOutputFileEntity withLatestMerged(boolean latestMerged) {
    this.latestMerged = latestMerged;
    return this;
  }
}
