package edu.colorado.cires.argonaut.metadata.core;

import edu.colorado.cires.argonaut.messaging.core.databind.ProfileMode;
import java.time.Instant;

public interface RecentProfileSearch {

  ProfileMode getProfileMode();

  Instant getYoungerOrEqual();

  int getPageNumber();

  int getPageSize();

}
