package edu.colorado.cires.argonaut.core.netcdf.profile.v31;

import java.time.Instant;
import java.util.List;

public interface ArgoMultiProfileV31 {

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

  int getNumberOfProfiles();

  List<ArgoProfileV31> getProfiles();

  ArgoProfileV31 getProfile(int profileIndex);

}
