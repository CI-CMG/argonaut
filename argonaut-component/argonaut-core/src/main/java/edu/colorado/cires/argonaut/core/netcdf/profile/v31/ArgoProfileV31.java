package edu.colorado.cires.argonaut.core.netcdf.profile.v31;

import java.time.Instant;
import java.util.List;

public interface ArgoProfileV31 {

  // https://archimer.ifremer.fr/doc/00187/29825/120885.pdf

//  :title = "Argo float vertical profile";
//:institution = "CSIRO";
//:source = "Argo float";
//:history = "2011-04-22T06:00:00Z creation";
//:references = "http://www.argodatamgt.org/Documentation";
//:id = "https://doi.org/10.17882/42182";
//:comment = "free text";
//:user_manual_version = "3.4";
//:Conventions = "Argo-3.1 CF-1.6";
//:featureType = "trajectoryProfile";
// comment_on_resolution = Optional comment on parameter resolution

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

  /*
  DATE_TIME = 14; This dimension is the length of an ASCII date and time value.
Date_time convention is : YYYYMMDDHHMISS
YYYY : year
MM : month
DD : day
HH : hour of the day (as 0 to 23)
MI : minutes (as 0 to 59)
SS : seconds (as 0 to 59)
Date and time values are always in universal time coordinates (UTC).
Examples :
20010105172834 : January 5th 2001 17:28:34
19971217000000 : December 17th 1997 00:00:00
   */



  /*
  char DATA_TYPE(STRING16);
  DATA_TYPE:long_name = "Data type";
  DATA_TYPE:conventions = "Argo reference table 1";
  DATA_TYPE:_FillValue = " ";
  This field contains the type of data
  contained in the file.
  The list of acceptable data types is in
  the reference table 1
  (https://vocab.nerc.ac.uk/collection/R0
  1/).
  Example : Argo profile
   */
  String getDataType();

  /*
  char FORMAT_VERSION(STRING4);
  FORMAT_VERSION:long_name = "File format version";
  FORMAT_VERSION:_FillValue = " ";
  File format version
  Example
   */
  String getFormatVersion();

  /*
  char HANDBOOK_VERSION(STRING4);
  HANDBOOK_VERSION:long_name = "Data handbook
  version";
  HANDBOOK_VERSION:_FillValue = " ";
  Version number of the data handbook.
  This field indicates that the data
  contained in this file are managed
  according to the policy described in the
  Argo data management handbook.
  Example 1.0
   */
  String getHandbookVersion();

  /*
  char REFERENCE_DATE_TIME(DATE_TIME);
  REFERENCE_DATE_TIME:long_name = "Date of
  reference for Julian days";
  REFERENCE_DATE_TIME:conventions =
  "YYYYMMDDHHMISS";
  REFERENCE_DATE_TIME:_FillValue = " ";
  Date of reference for Julian days.
  The recommended reference date time
  is “19500101000000” : January 1st
  1950 00:00:00
   */
  Instant getReferenceDateTime();

  /*
  char DATE_CREATION(DATE_TIME);
  DATE_CREATION:long_name = "Date of file creation";
  DATE_CREATION:conventions = "YYYYMMDDHHMISS";
  DATE_CREATION:_FillValue = " ";
  Date and time (UTC) of creation of this
  file.
  Format : YYYYMMDDHHMISS
  Example :
  20011229161700 : December 29th 2001 16 :17 :00
   */
  Instant getDateCreation();


  /*
  char DATE_UPDATE(DATE_TIME);
  DATE_UPDATE:long_name = "Date of update of this
  file";
  DATE_UPDATE:conventions = "YYYYMMDDHHMISS";
  DATE_UPDATE:_FillValue = " ";
  Date and time (UTC) of update of this
  file.
  Format : YYYYMMDDHHMISS
  Example :
  20011230090500 : December 30th 2001
  09 :05 :00
   */
  Instant getDateUpdate();

  /*
  char PLATFORM_NUMBER(N_PROF,
  STRING8);
  PLATFORM_NUMBER:long_name = "Float
  unique identifier";
  PLATFORM_NUMBER:conventions = "WMO
  float identifier : A9IIIII";
  PLATFORM_NUMBER:_FillValue = " ";
  WMO float identifier.
  WMO is the World Meteorological Organization.
  This platform number is unique.
  Example : 6900045
   */
  String getPlatformNumber();

  /*
  char PROJECT_NAME(N_PROF, STRING64);
  PROJECT_NAME:long_name = "Name of the
  project";
  PROJECT_NAME:_FillValue = " ";
  Name of the project that operates the float.
  Multiple projects can be separated by commas.
  Example: “GYROSCOPE, GMMC”
   */
  String getProjectName();

  /*
  char PI_NAME (N_PROF, STRING64);
  PI_NAME:long_name = "Name of the
  principal investigator";
  PI_NAME:_FillValue = " ";
  Name of the principal investigator responsible
  for the profiling float. Example: “Julia UITZ”.
  Valid PI names are listed in reference table R40
  ( https://vocab.nerc.ac.uk/collection/R40/).
  Multiple names can be concatenated, separated
  by commas
   */
  String getPrincipalInvestigatorName();

  /*
  char STATION_PARAMETERS(N_PROF,
  N_PARAM, STRING16);
  STATION_PARAMETERS:long_name = "List of
  available parameters for the station";
  STATION_PARAMETERS:conventions = "Argo
  reference table 3";
  STATION_PARAMETERS:_FillValue = " ";
  List of parameters contained in this profile.
  The parameter names are listed in reference
  table 3
  (https://vocab.nerc.ac.uk/collection/R03/).
  Examples : TEMP, PSAL, CNDC
  TEMP : temperature
  PSAL : practical salinity
  CNDC : conductivity
   */
  List<String> getStationParameters();

  /*
  int CYCLE_NUMBER(N_PROF);
  CYCLE_NUMBER:long_name = "Float cycle
  number";
  CYCLE_NUMBER:conventions = "0...N, 0 :
  launch cycle (if exists), 1 : first complete
  cycle";
  CYCLE_NUMBER:_FillValue = 99999;
  Float cycle number.
  See §1.6: float cycle definition.int CYCLE_NUMBER(N_PROF);
  CYCLE_NUMBER:long_name = "Float cycle
  number";
  CYCLE_NUMBER:conventions = "0...N, 0 :
  launch cycle (if exists), 1 : first complete
  cycle";
  CYCLE_NUMBER:_FillValue = 99999;
  Float cycle number.
  See §1.6: float cycle definition.
   */
  int getCycleNumber();

  /*
  char DIRECTION(N_PROF);
  DIRECTION:long_name = "Direction of the
  station profiles";
  DIRECTION:conventions = "A: ascending
  profiles, D: descending profiles";
  DIRECTION:_FillValue = " ";
  Type of profile on which measurement occurs.
  A : ascending profile
  D : descending profile
   */
  ArgoProfileDirection getDirection();

  /*
  char DATA_CENTRE(N_PROF, STRING2);
  DATA_CENTRE:long_name = "Data centre in
  charge of float data processing";
  DATA_CENTRE:conventions = "Argo
  reference table 4";
  DATA_CENTRE:_FillValue = " ";
  Code for the data centre in charge of the float
  data management.
  The data centre codes are described in the
  reference table 4
  (https://vocab.nerc.ac.uk/collection/R04/).
  Example : “ME" for MEDS
   */
  String getDataCenter();

  /*
  char DC_REFERENCE(N_PROF, STRING32);
  DC_REFERENCE:long_name = "Station unique
  identifier in data centre";
  DC_REFERENCE:conventions = "Data centre
  convention";
  DC_REFERENCE:_FillValue = " ";
  Unique identifier of the profile in the data
  centre.
  Data centres may have different identifier
  schemes.
  DC_REFERENCE is therefore not unique across
  data centres.
   */
  String getDataCenterReference();

  /*
  char DATA_STATE_INDICATOR(N_PROF,
  STRING4);
  DATA_STATE_INDICATOR:long_name =
  "Degree of processing the data have passed
  through";
  DATA_STATE_INDICATOR:conventions =
  "Argo reference table 6";
  DATA_STATE_INDICATOR:_FillValue = " ";
  Degree of processing the data has passed
  through.
  The data state indicator is described in the
  reference table 6
  (https://vocab.nerc.ac.uk/collection/R06/).
   */
  String getDataStateIndicator();

  /*
  char DATA_MODE(N_PROF);
  DATA_MODE:long_name = "Delayed mode or
  real time data";
  DATA_MODE:conventions = "R : real time; D
  : delayed mode; A : real time with
  adjustment";
  DATA_MODE:_FillValue = " ";
  Indicates if the profile contains real time,
  delayed mode or adjusted data.
  R : real time data
  D : delayed mode data
  A : real time data with adjusted values
   */
  ArgoProfileDataMode getDataMode();

  /*
  char PLATFORM_TYPE(N_PROF, STRING32);
  PLATFORM_TYPE:long_name = "Type of
  float";
  PLATFORM_TYPE:conventions = "Argo
  reference table 23";
  PLATFORM_TYPE:_FillValue = " ";
  Type of float listed in reference table 23
  (https://vocab.nerc.ac.uk/collection/R23/).
  Example: SOLO, APEX, PROVOR, ARVOR, NINJA
   */
  String getPlatformType();

  /*
  char FLOAT_SERIAL_NO(N_PROF,
  STRING32);
  FLOAT_SERIAL_NO:long_name = "Serial
  Serial number of the float.
  Example 1679
   */
  String getFloatSerialNumber();

  /*
  char FIRMWARE_VERSION(N_PROF,
  STRING64);
  FIRMWARE_VERSION:long_name =
  "Instrument firmware version";
  FIRMWARE_VERSION:_FillValue = " ";
  Firmware version of the float.
  Example : "013108"
  The dimension STRING32 instead of STRING64
  remains accepted.
   */
  String getFirmwareVersion();

  /*
  char WMO_INST_TYPE(N_PROF, STRING4);
  WMO_INST_TYPE:long_name = "Coded
  instrument type”;
  WMO_INST_TYPE:conventions = "Argo
  reference table 8";
  WMO_INST_TYPE:_FillValue = " ";
  Instrument type from WMO code table 1770.
  A subset of WMO table 1770 is documented in
  the reference table 8
  (https://vocab.nerc.ac.uk/collection/R08/).
  Example :
  846 : Webb Research float, Seabird sensor
   */
  String getWmoInstrumentType();

  /*
  double JULD(N_PROF);
  JULD:long_name = "Julian day (UTC) of the
  station relative to REFERENCE_DATE_TIME";
  JULD:standard_name = "time";
  JULD:units = "days since 1950-01-01
  00:00:00 UTC";
  JULD:conventions = "Relative julian days with
  decimal part (as parts of day)";
  JULD:resolution = X;
  JULD:_FillValue = 999999.;
  JULD:axis = "T";
  Julian day of the profile.
  The integer part represents the day, the decimal
  part represents the time of the profile.
  Date and time are in Universal Time.
  The Julian day is relative to
  REFERENCE_DATE_TIME.
  Example :
  18833.8013889885 : July 25 2001 19:14:00
   */
  Instant getJulianDate();

  /*
  char JULD_QC(N_PROF);
  JULD_QC:long_name = "Quality on date and
  time";
  JULD_QC:conventions = "Argo reference table
  2";
  JULD_QC:_FillValue = " ";
  Quality flag on JULD date and time.
  The flag scale is described in the reference
  tables 2
  (https://vocab.nerc.ac.uk/collection/RR2/ for
  real time and
  https://vocab.nerc.ac.uk/collection/RD2/ for
  delayed mode).
  Example :
  1: the date and time seems correct
   */
  String getJulianDateQc();

  /*
  double JULD_LOCATION(N_PROF);
  JULD_LOCATION:long_name = "Julian day
  (UTC) of the location relative to
  REFERENCE_DATE_TIME";
  JULD_LOCATION:units = "days since 1950-
  01-01 00:00:00 UTC";
  JULD_LOCATION:conventions = "Relative
  julian days with decimal part (as parts of
  day)";
  JULD_LOCATION:resolution = X;
  JULD_LOCATION:_FillValue = 999999.;
  Julian day of the location of the profile.
  The integer part represents the day, the decimal
  part represents the time of the profile.
  Date and time are in Universal Time.
  The Julian day is relative to
  REFERENCE_DATE_TIME.
  Example :
  18833.8013889885 : July 25 2001 19:14:00
   */
  Instant getJulianDateOfLocation();

  /*
  double LATITUDE(N_PROF);
  LATITUDE:long_name = "Latitude of the
  station, best estimate";
  LATITUDE:standard_name = "latitude";
  LATITUDE:units = "degree_north";
  LATITUDE:_FillValue = 99999.;
  LATITUDE:valid_min = -90.;
  LATITUDE:valid_max = 90.;
  LATITUDE:axis = "Y";
  Latitude of the profile.
  Unit : degree north
  This field contains the best estimated latitude.
  The latitude value may be improved in delayed
  mode.
  The measured locations of the float are located
  in the trajectory file.
  Example : 44.4991 : 44° 29’ 56.76’’ N
   */
  double getLatitude();

  /*
  double LONGITUDE(N_PROF);
  LONGITUDE:long_name = "Longitude of the
  station, best estimate";
  LONGITUDE:standard_name = "longitude";
  LONGITUDE:units = "degree_east";
  LONGITUDE:_FillValue = 99999.;
  LONGITUDE:valid_min = -180.;
  LONGITUDE:valid_max = 180.;
  LONGITUDE:axis = "X";
  Longitude of the profile.
  Unit : degree east
  This field contains the best estimated longitude.
  The longitude value may be improved in delayed
  mode.
  The measured locations of the float are located
  in the trajectory file.
  Example : 16.7222 : 16° 43’ 19.92’’ E
   */
  double getLongitude();

  /*
  char POSITION_QC(N_PROF);
  POSITION_QC:long_name = "Quality on
  position (latitude and longitude)";
  POSITION_QC:conventions = "Argo reference
  table 2";
  POSITION_QC:_FillValue = " ";
  Quality flag on position.
  The flag on position is set according to
  (LATITUDE, LONGITUDE) quality.
  The flag scale is described in the reference
  tables 2
  (https://vocab.nerc.ac.uk/collection/RR2/ for
  real time and
  https://vocab.nerc.ac.uk/collection/RD2/ for
  delayed mode).
  Example: 1: position seems correct
   */
  String getPositionQc();

  /*
  char POSITIONING_SYSTEM(N_PROF,
  STRING8);
  POSITIONING_SYSTEM:long_name =
  "Positioning system";
  Name of the system in charge of positioning the
  float locations from reference table 9
  (https://vocab.nerc.ac.uk/collection/R09/).
  Examples : ARGOS
   */
  String getPositioningSystem();

  /*
  float
  POSITION_ERROR_REPORTED(N_PROF);
  POSITION_ERROR_REPORTED:long_name =
  "Position error reported by the positioning
  system";
  POSITION_ERROR_REPORTED:units =
  "meters";
  POSITION_ERROR_REPORTED:_FillValue =
  99999.;
  This is an optional variable.
  Position error reported by the positioning
  system.
   */
  Float getPositionErrorReported();

  /*
  float
  POSITION_ERROR_ESTIMATED(N_PROF);
  POSITION_ERROR_ESTIMATED:long_name =
  "Position error estimated by the real-time or
  delayed-mode process";
  POSITION_ERROR_ESTIMATED:units =
  "meters";
  POSITION_ERROR_ESTIMATED:_FillValue =
  99999.;
  This is an optional variable. Position error
  estimated by the real-time or delayed-mode
  process.
   */
  Float getPositionErrorEstimated();

  /*
  char
  POSITION_ERROR_ESTIMATED_COMMENT(N
  _PROF, STRING1024);
  POSITION_ERROR_ESTIMATED_COMMENT:lo
  ng_name = "Comment on the method used
  to determine
  POSITION_ERROR_ESTIMATED";
  POSITION_ERROR_ESTIMATED_COMMENT:_
  FillValue = " ";
  This is an optional variable.
  Comment on the method used to determine
  POSITION_ERROR_ESTIMATED.
   */
  String getPositionErrorEstimatedComment();

  /*
  char VERTICAL_SAMPLING_SCHEME(N_PROF,
  STRING256);
  VERTICAL_SAMPLING_SCHEME:long_name =
  "Vertical sampling scheme";
  VERTICAL_SAMPLING_SCHEME:conventions
  = "Argo reference table 16";
  VERTICAL_SAMPLING_SCHEME:_FillValue = "
  ";
  Use the vertical sampling scheme to differentiate
  and identify profiles from a single-cycle with
  different vertical sampling schemes.
  See reference table 16
  (https://vocab.nerc.ac.uk/collection/R16/).
   */
  String getVerticalSamplingScheme();

  /*
  int CONFIG_MISSION_NUMBER(N_PROF);
  CONFIG_MISSION_NUMBER:long_name = "
  Unique number denoting the missions
  performed by the float";
  CONFIG_MISSION_NUMBER:conventions =
  "1...N, 1 : first complete mission";
  CONFIG_MISSION_NUMBER:_FillValue =
  99999;
  Unique number of the mission to which this
  profile belongs.
  See note on floats with multiple configurations
  §2.4.6.1.
  The number 0 (zero) can be used to denote the
  float’s mission prelude, if it exists.
   */
  int getConfigMissionNumber();

  ArgoProfileV31Parameter getParameter(String parameterName);


  List<ArgoProfileV31Parameter> getParameters();


  List<ArgoProfileV31History> getProfileHistory();


  int getProfileIndex();

}
