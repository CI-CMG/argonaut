package edu.colorado.cires.argonaut.core.netcdf.synthprofile.v13;

import edu.colorado.cires.argonaut.core.util.CommonFileValues;
import edu.colorado.cires.argonaut.core.util.CommonProfileValues;
import java.time.Instant;
import java.util.List;

public interface ArgoSyntheticProfileV13 extends CommonProfileValues, CommonFileValues {

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

  List<String> getStationParameters();

  List<ArgoSyntheticProfileV13Parameter> getParameters();

  String getSoftwareVersion();
}
