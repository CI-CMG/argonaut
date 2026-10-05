package edu.colorado.cires.argonaut.core.netcdf.technical.v31;

import java.time.Instant;

public interface ArgoTechnicalV31 {

  String getTitle();

  String getInstitution();

  String getSource();

  String getHistory();

  String getReferences();

  String getComment();

  String getUserManualVersion();

  String getConventions();

  // Float unique identifier
  String getPlatformNumber();

  // Data type
  String getDataType();

  // File format version
  String getFormatVersion();

  // Data handbook version
  String getHandbookVersion();

  // Data center in charge of Float data processing
  String getDataCenter();

  // Date of file creation
  Instant getDateCreation();

  // Date of update of this field
  Instant getDateUpdate();

  // Number of technical parameters
  Integer getNParameters();

  // Name of technical parameter
  String getTechnicalParameterName(Integer nParameter);

  // Value of technical parameter
  String getTechnicalParameterValue(Integer nParameter);

  // 0...N, 0 : launch cycle (if exists), 1 : first complete cycle
  Integer getCycleNumber(Integer nParameter);

}
