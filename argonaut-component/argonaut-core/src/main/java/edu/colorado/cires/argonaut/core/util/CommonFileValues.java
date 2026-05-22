package edu.colorado.cires.argonaut.core.util;

import java.time.Instant;

public interface CommonFileValues {

  String getDataType();

  String getFormatVersion();

  String getHandbookVersion();

  Instant getReferenceDateTime();

  Instant getDateCreation();

  Instant getDateUpdate();
}
