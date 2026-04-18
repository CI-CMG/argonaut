package edu.colorado.cires.argonaut.core.netcdf.v31;

import java.util.List;

public interface ArgoProfileV31Parameter {

  String getParameterName();

  /*
  char PROFILE_<PARAM>_QC(N_PROF);
  PROFILE_<PARAM>_QC:long_name =
  "Global quality flag of <PARAM> profile";
  PROFILE_<PARAM>_QC:conventions = "Argo
  reference table 2a";
  PROFILE_<PARAM>_QC:_FillValue = " ";
  Global quality flag on the PARAM profile.
  PARAM is among the STATION_PARAMETERS.
  The overall flag is set to indicate the percentage
  of good data in the profile as described in
  reference table 2a
  (https://vocab.nerc.ac.uk/collection/RP2/).
  Example :
  PROFILE_TEMP_QC = A : the temperature
  profile contains only good values
  PROFILE_PSAL_QC = C : the salinity profile
  contains 50% to 75% good values
   */
  String getQc();

  List<ArgoProfileV31Level> getLevels();

  List<ArgoProfileV31Calibration> getCalibrations();
}
