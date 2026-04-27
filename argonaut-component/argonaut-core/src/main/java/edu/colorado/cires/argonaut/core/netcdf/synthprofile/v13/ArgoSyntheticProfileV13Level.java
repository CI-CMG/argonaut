package edu.colorado.cires.argonaut.core.netcdf.synthprofile.v13;

public interface ArgoSyntheticProfileV13Level {

  Float getOriginalValue();
  Float getPressureDisplacement();
  String getQc();
  Float getAdjustedValue();
  String getAdjustedQc();
  Float getAdjustedErrorValue();



}
