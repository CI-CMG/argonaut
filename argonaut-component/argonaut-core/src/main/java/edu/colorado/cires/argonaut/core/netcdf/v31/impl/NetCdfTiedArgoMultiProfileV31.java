package edu.colorado.cires.argonaut.core.netcdf.v31.impl;

import edu.colorado.cires.argonaut.core.netcdf.v31.ArgoMultiProfileV31;
import edu.colorado.cires.argonaut.core.netcdf.v31.ArgoProfileV31;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import ucar.nc2.Attribute;
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
    title = NetCdfUtils.getStringGlobalAttribute(netcdf, "title");
    institution = NetCdfUtils.getStringGlobalAttribute(netcdf, "institution");
    source = NetCdfUtils.getStringGlobalAttribute(netcdf, "source");
    history = NetCdfUtils.getStringGlobalAttribute(netcdf, "history");
    references = NetCdfUtils.getStringGlobalAttribute(netcdf, "references");
    id = NetCdfUtils.getStringGlobalAttribute(netcdf, "id");
    comment = NetCdfUtils.getStringGlobalAttribute(netcdf, "comment");
    userManualVersion = NetCdfUtils.getStringGlobalAttribute(netcdf, "user_manual_version");
    conventions = NetCdfUtils.getStringGlobalAttribute(netcdf, "Conventions");
    featureType = NetCdfUtils.getStringGlobalAttribute(netcdf, "featureType");
    commentOnResolution = NetCdfUtils.getStringGlobalAttribute(netcdf, "comment_on_resolution");
    numberOfProfiles = NetCdfUtils.getDimensionSize(netcdf, "N_PROF");
    try {
      dataType = NetCdfUtils.getString(netcdf, "DATA_TYPE");
      formatVersion = NetCdfUtils.getString(netcdf, "FORMAT_VERSION");
      handbookVersion = NetCdfUtils.getString(netcdf, "HANDBOOK_VERSION");
      referenceDateTime = NetCdfUtils.getInstant(netcdf, "REFERENCE_DATE_TIME");
      dateCreation =  NetCdfUtils.getInstant(netcdf, "DATE_CREATION");
      dateUpdate = NetCdfUtils.getInstant(netcdf, "DATE_UPDATE");
    } catch (IOException e) {
      throw new RuntimeException("Unable to initialize multi profile data wrapper", e);
    }

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
    return List.of();
  }

  @Override
  public ArgoProfileV31 getProfile(int profileIndex) {
    return new NetCdfTiedArgoProfileV31(this, profileIndex);
  }
}
