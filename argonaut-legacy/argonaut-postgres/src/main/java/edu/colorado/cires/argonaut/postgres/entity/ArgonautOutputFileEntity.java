package edu.colorado.cires.argonaut.postgres.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "output_file")
public class ArgonautOutputFileEntity {

  @Id
  @Column(name = "path", unique = true, nullable = false, length = 120)
  private String path;

  @Column(name = "dac", nullable = false, length = 20)
  private String dac;

  @Column(name = "file_name", nullable = false, length = 30)
  private String fileName;

  @Column(name = "date_time", nullable = false)
  private LocalDateTime dateTime;

  @Column(name = "file_type", nullable = false, length = 20)
  private String fileType;

  @Column(name = "float_id", nullable = false, length = 7)
  private String floatId;

  @Column(name = "ocean", nullable = false, length = 1)
  private String ocean;

//  @Column(nullable = false)
//  private boolean removed;

//  @Column(name = "float_merged", nullable = false)
//  private boolean floatMerged;

  @Column(name = "geo_merged", nullable = false)
  private boolean geoMerged;

  @Column(name = "latest_merged", nullable = false)
  private boolean latestMerged;

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
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

  public LocalDateTime getDateTime() {
    return dateTime;
  }

  public void setDateTime(LocalDateTime dateTime) {
    this.dateTime = dateTime;
  }

  public String getFileType() {
    return fileType;
  }

  public void setFileType(String fileType) {
    this.fileType = fileType;
  }

  public String getFloatId() {
    return floatId;
  }

  public void setFloatId(String floatId) {
    this.floatId = floatId;
  }

  public String getOcean() {
    return ocean;
  }

  public void setOcean(String ocean) {
    this.ocean = ocean;
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
