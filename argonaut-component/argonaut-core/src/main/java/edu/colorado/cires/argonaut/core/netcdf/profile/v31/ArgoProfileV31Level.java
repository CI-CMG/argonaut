package edu.colorado.cires.argonaut.core.netcdf.profile.v31;

public interface ArgoProfileV31Level {

  int getProfileIndex();

  int getLevelIndex();

  String getParameterName();

  /*
  float <PARAM>(N_PROF, N_LEVELS);
  <PARAM>:long_name = "<X>";
  <PARAM>:standard_name = "<X>";
  <PARAM>:_FillValue = <X>;
  <PARAM>:units = "<X>";
  <PARAM>:valid_min = <X>;
  <PARAM>:valid_max = <X>;
  <PARAM>:C_format = "<X>";
  <PARAM> contains the original values
  of a parameter listed in reference table
  3
  (https://vocab.nerc.ac.uk/collection/R03
  /).
  <X> : this field is specified in the
  reference table 3
  (https://vocab.nerc.ac.uk/collection/R03
  */
  float getValue();

  /*
float <PARAM>_ADJUSTED(N_PROF, N_LEVELS);
<PARAM>_ADJUSTED:long_name = "<X>";
<PARAM>_ADJUSTED:standard_name = "<X>";
<PARAM>_ADJUSTED:_FillValue = <X>;
<PARAM>_ADJUSTED:units = "<X>";
<PARAM>_ADJUSTED:valid_min = <X>;
<PARAM>_ADJUSTED:valid_max = <X>;
<PARAM>_ADJUSTED:C_format = "<X>";
<PARAM>_ADJUSTED:FORTRAN_format = "<X>";
<PARAM>_ADJUSTED:resolution= <X>;
<PARAM>_ADJUSTED contains the
adjusted values derived from the
original values of the parameter.
<X> : this field is specified in the
reference table 3
(https://vocab.nerc.ac.uk/collection/R03
/).
<PARAM>_ADJUSTED is
mandatory.
When no adjustment is performed, the
FillValue is inserted.
 */
  Float getAdjustedValue();

  /*
  char <PARAM>_ADJUSTED_QC(N_PROF, N_LEVELS);
  <PARAM>_ADJUSTED_QC:long_name = "quality flag";
  <PARAM>_ADJUSTED_QC:conventions = "Argo
  reference table 2";
  <PARAM>_ADJUSTED_QC:_FillValue = " ";
  Quality flag applied on each
  <PARAM>_ADJUSTED value.
  The flag scale is specified in reference
  tables 2
  (https://vocab.nerc.ac.uk/collection/RR2
  / for real time and
  https://vocab.nerc.ac.uk/collection/RD2/
  for delayed mode).
  <PARAM>_ADJUSTED_QC is
  mandatory.
  When no adjustment is performed, the
  FillValue is inserted.
 */
  String getAdjustedQc();

  /*
  float <PARAM>_ADJUSTED_ERROR(N_PROF,
  N_LEVELS);
  <PARAM>_ADJUSTED_ERROR:long_name = "Contains
  the error on the adjusted values as determined by the
  delayed mode QC process";
  <PARAM>_ADJUSTED_ERROR:_FillValue = <X>;
  <PARAM>_ADJUSTED_ERROR:units = "<X>";
  <PARAM>_ADJUSTED_ERROR:C_format = "<X>";
  <PARAM>_ADJUSTED_ERROR:FORTRAN_format =
  "<X>";
  <PARAM>_ADJUSTED_ERROR:resolution= <X>;
  <PARAM>_ADJUSTED_ERROR
  Contains the error on the adjusted
  values as determined by the delayed
  mode QC process.
  <X> : this field is specified in the
  reference table 3
  (https://vocab.nerc.ac.uk/collection/R03
  /).
  <PARAM>_ADJUSTED_ERROR is
  mandatory.
  When no adjustment is performed, the
  FillValue is inserted.
 */
  Float getAdjustedErrorValue();

  /*
  char <PARAM>_QC(N_PROF, N_LEVELS);
  <PARAM>_QC:long_name = "quality flag";
  <PARAM>_QC:conventions = "Argo reference table 2";
  <PARAM>_QC:_FillValue = " ";
  Quality flag applied on each <PARAM>
  value.
  The flag scale is specified in reference
  tables 2
  (https://vocab.nerc.ac.uk/collection/RR2
  / for real time and
  https://vocab.nerc.ac.uk/collection/RD2/
  for delayed mode)
 */
  String getQc();

}
