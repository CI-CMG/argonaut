package edu.colorado.cires.argonaut.core.netcdf.synthprofile.v13;

import java.time.Instant;
import java.util.List;

public interface ArgoSyntheticMultiProfileV13 {

  String getTitle();

  String getInstitution();

  String getSource();

  String getHistory();

  String getReferences();

  String getId();

  String getComment();

  String getUserManualVersion();

  String getConventions();

  String getFeatureType();

  String getCommentOnResolution();

  String getDataType();

  String getFormatVersion();

  String getHandbookVersion();

  Instant getReferenceDateTime();

  Instant getDateCreation();

  Instant getDateUpdate();

  int getNumberOfProfiles();

  List<ArgoSyntheticProfileV13> getProfiles();


}
