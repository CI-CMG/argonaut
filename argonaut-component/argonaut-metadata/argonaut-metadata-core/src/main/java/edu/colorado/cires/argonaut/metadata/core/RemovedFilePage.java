package edu.colorado.cires.argonaut.metadata.core;

import edu.colorado.cires.argonaut.messaging.core.databind.ProfileOperation;
import java.util.List;
import java.util.Optional;

public interface RemovedFilePage extends RemovedFileSearch {

  @Override
  int getPageNumber();

  @Override
  int getPageSize();

  int getTotalPages();

  long getTotalRecords();

  List<ProfileOperation> getPage();

  Optional<RemovedFileSearch> getNextPage();
}
