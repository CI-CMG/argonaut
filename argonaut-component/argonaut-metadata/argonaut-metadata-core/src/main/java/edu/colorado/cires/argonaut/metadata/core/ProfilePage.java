package edu.colorado.cires.argonaut.metadata.core;

import edu.colorado.cires.argonaut.messaging.core.databind.ProfileOperation;
import java.util.List;
import java.util.Optional;

public interface ProfilePage extends IndexPageRequest {

  @Override
  int getPageNumber();

  @Override
  int getPageSize();

  int getTotalPages();

  long getTotalRecords();

  List<ProfileOperation> getPage();

  Optional<IndexPageRequest> getNextPage();
}
