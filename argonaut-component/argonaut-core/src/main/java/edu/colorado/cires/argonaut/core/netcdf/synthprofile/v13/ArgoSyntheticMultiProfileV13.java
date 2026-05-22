package edu.colorado.cires.argonaut.core.netcdf.synthprofile.v13;

import edu.colorado.cires.argonaut.core.util.CommonFileValues;
import java.time.Instant;
import java.util.List;

public interface ArgoSyntheticMultiProfileV13 extends CommonFileValues {

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

  int getNumberOfProfiles();

  List<ArgoSyntheticProfileV13> getProfiles();

  String getSoftwareVersion();

}
