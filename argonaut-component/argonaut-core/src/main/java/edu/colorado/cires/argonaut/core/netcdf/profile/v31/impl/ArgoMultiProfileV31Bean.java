package edu.colorado.cires.argonaut.core.netcdf.profile.v31.impl;

import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoMultiProfileV31;
import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoProfileV31;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class ArgoMultiProfileV31Bean implements ArgoMultiProfileV31 {

  private String title;
  private String institution;
  private String source;
  private String history;
  private String references;
  private String id;
  private String comment;
  private String userManualVersion;
  private String conventions;
  private String featureType;
  private String commentOnResolution;
  private String dataType;
  private String formatVersion;
  private String handbookVersion;
  private Instant referenceDateTime;
  private Instant dateCreation;
  private Instant dateUpdate;
  private List<ArgoProfileV31> profiles = new ArrayList<>();

  @Override
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  @Override
  public String getInstitution() {
    return institution;
  }

  public void setInstitution(String institution) {
    this.institution = institution;
  }

  @Override
  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  @Override
  public String getHistory() {
    return history;
  }

  public void setHistory(String history) {
    this.history = history;
  }

  @Override
  public String getReferences() {
    return references;
  }

  public void setReferences(String references) {
    this.references = references;
  }

  @Override
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  @Override
  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  @Override
  public String getUserManualVersion() {
    return userManualVersion;
  }

  public void setUserManualVersion(String userManualVersion) {
    this.userManualVersion = userManualVersion;
  }

  @Override
  public String getConventions() {
    return conventions;
  }

  public void setConventions(String conventions) {
    this.conventions = conventions;
  }

  @Override
  public String getFeatureType() {
    return featureType;
  }

  public void setFeatureType(String featureType) {
    this.featureType = featureType;
  }

  @Override
  public String getCommentOnResolution() {
    return commentOnResolution;
  }

  public void setCommentOnResolution(String commentOnResolution) {
    this.commentOnResolution = commentOnResolution;
  }

  @Override
  public String getDataType() {
    return dataType;
  }

  public void setDataType(String dataType) {
    this.dataType = dataType;
  }

  @Override
  public String getFormatVersion() {
    return formatVersion;
  }

  public void setFormatVersion(String formatVersion) {
    this.formatVersion = formatVersion;
  }

  @Override
  public String getHandbookVersion() {
    return handbookVersion;
  }

  public void setHandbookVersion(String handbookVersion) {
    this.handbookVersion = handbookVersion;
  }

  @Override
  public Instant getReferenceDateTime() {
    return referenceDateTime;
  }

  public void setReferenceDateTime(Instant referenceDateTime) {
    this.referenceDateTime = referenceDateTime;
  }

  @Override
  public Instant getDateCreation() {
    return dateCreation;
  }

  public void setDateCreation(Instant dateCreation) {
    this.dateCreation = dateCreation;
  }

  @Override
  public Instant getDateUpdate() {
    return dateUpdate;
  }

  public void setDateUpdate(Instant dateUpdate) {
    this.dateUpdate = dateUpdate;
  }

  @Override
  public int getNumberOfProfiles() {
    return profiles.size();
  }

  @Override
  public List<ArgoProfileV31> getProfiles() {
    return profiles;
  }

  @Override
  public ArgoProfileV31 getProfile(int profileIndex) {
    return profiles.get(profileIndex);
  }

  public void setProfiles(List<ArgoProfileV31Bean> profiles) {
    if (profiles == null) {
      this.profiles = new ArrayList<>();
    } else {
      this.profiles = new ArrayList<>(profiles);
    }
  }

}
