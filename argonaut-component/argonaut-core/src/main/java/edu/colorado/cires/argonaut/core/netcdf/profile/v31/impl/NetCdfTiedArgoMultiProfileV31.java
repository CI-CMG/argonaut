package edu.colorado.cires.argonaut.core.netcdf.profile.v31.impl;

import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoMultiProfileV31;
import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoProfileV31;
import edu.colorado.cires.argonaut.core.util.NetCdfReadUtils;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import ucar.nc2.NetcdfFile;

public class NetCdfTiedArgoMultiProfileV31 implements ArgoMultiProfileV31 {

  private final NetcdfFile netcdf;
  private final String title;
  private final String institution;
  private final String source;
  private final String history;
  private final String references;
  private final String id;
  private final String comment;
  private final String userManualVersion;
  private final String conventions;
  private final String featureType;
  private final String commentOnResolution;
  private final String dataType;
  private final String formatVersion;
  private final String handbookVersion;
  private final Instant referenceDateTime;
  private final Instant dateCreation;
  private final Instant dateUpdate;
  private final int numberOfProfiles;

  public NetCdfTiedArgoMultiProfileV31(NetcdfFile netcdf) {
    this.netcdf = netcdf;
    title = NetCdfReadUtils.getGlobalAttributeString(netcdf, "title");
    institution = NetCdfReadUtils.getGlobalAttributeString(netcdf, "institution");
    source = NetCdfReadUtils.getGlobalAttributeString(netcdf, "source");
    history = NetCdfReadUtils.getGlobalAttributeString(netcdf, "history");
    references = NetCdfReadUtils.getGlobalAttributeString(netcdf, "references");
    id = NetCdfReadUtils.getGlobalAttributeString(netcdf, "id");
    comment = NetCdfReadUtils.getGlobalAttributeString(netcdf, "comment");
    userManualVersion = NetCdfReadUtils.getGlobalAttributeString(netcdf, "user_manual_version");
    conventions = NetCdfReadUtils.getGlobalAttributeString(netcdf, "Conventions");
    featureType = NetCdfReadUtils.getGlobalAttributeString(netcdf, "featureType");
    commentOnResolution = NetCdfReadUtils.getGlobalAttributeString(netcdf, "comment_on_resolution");
    numberOfProfiles = NetCdfReadUtils.getDimensionSize(netcdf, "N_PROF");
    dataType = NetCdfReadUtils.getString(netcdf, "DATA_TYPE");
    formatVersion = NetCdfReadUtils.getString(netcdf, "FORMAT_VERSION");
    handbookVersion = NetCdfReadUtils.getString(netcdf, "HANDBOOK_VERSION");
    referenceDateTime = NetCdfReadUtils.getInstant(netcdf, "REFERENCE_DATE_TIME");
    dateCreation = NetCdfReadUtils.getInstant(netcdf, "DATE_CREATION");
    dateUpdate = NetCdfReadUtils.getInstant(netcdf, "DATE_UPDATE");

  }

  NetcdfFile getNetcdf() {
    return netcdf;
  }

  @Override
  public String getTitle() {
    return title;
  }

  @Override
  public String getInstitution() {
    return institution;
  }

  @Override
  public String getSource() {
    return source;
  }

  @Override
  public String getHistory() {
    return history;
  }

  @Override
  public String getReferences() {
    return references;
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public String getComment() {
    return comment;
  }

  @Override
  public String getUserManualVersion() {
    return userManualVersion;
  }

  @Override
  public String getConventions() {
    return conventions;
  }

  @Override
  public String getFeatureType() {
    return featureType;
  }

  @Override
  public String getCommentOnResolution() {
    return commentOnResolution;
  }

  @Override
  public String getDataType() {
    return dataType;
  }

  @Override
  public String getFormatVersion() {
    return formatVersion;
  }

  @Override
  public String getHandbookVersion() {
    return handbookVersion;
  }

  @Override
  public Instant getReferenceDateTime() {
    return referenceDateTime;
  }

  @Override
  public Instant getDateCreation() {
    return dateCreation;
  }

  @Override
  public Instant getDateUpdate() {
    return dateUpdate;
  }

  @Override
  public int getNumberOfProfiles() {
    return numberOfProfiles;
  }

  @Override
  public List<ArgoProfileV31> getProfiles() {
    List<ArgoProfileV31> profiles = new ArrayList<>(numberOfProfiles);
    for (int profileIndex = 0; profileIndex < numberOfProfiles; profileIndex++) {
      profiles.add(new NetCdfTiedArgoProfileV31(this, profileIndex));
    }
    return profiles;
  }

  @Override
  public ArgoProfileV31 getProfile(int profileIndex) {
    return new NetCdfTiedArgoProfileV31(this, profileIndex);
  }
}
