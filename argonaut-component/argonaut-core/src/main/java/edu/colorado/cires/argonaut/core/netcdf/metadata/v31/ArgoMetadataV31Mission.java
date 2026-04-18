package edu.colorado.cires.argonaut.core.netcdf.metadata.v31;

import java.util.List;

public interface ArgoMetadataV31Mission {

  Integer getMissionNumber();
  String getComment();
  List<ArgoMetadataV31Config> getConfigParameters();
}
