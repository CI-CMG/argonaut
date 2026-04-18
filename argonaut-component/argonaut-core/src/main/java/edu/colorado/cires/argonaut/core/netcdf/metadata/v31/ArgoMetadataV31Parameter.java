package edu.colorado.cires.argonaut.core.netcdf.metadata.v31;

public interface ArgoMetadataV31Parameter {

  String getName();
  String getSensor();
  String getUnits();
  String getAccuracy();
  String getResolution();
  ArgoMetadataV31PredeploymentCalibration getPredeploymentCalibration();
}
