package edu.colorado.cires.argonaut.metadata.core;

import edu.colorado.cires.argonaut.messaging.core.databind.GeoMergeInfo;
import edu.colorado.cires.argonaut.messaging.core.databind.ProfileOperation;
import java.util.List;
import java.util.Optional;

public interface GeoMergePage extends IndexPageRequest {

  @Override
  int getPageNumber();

  @Override
  int getPageSize();

  int getTotalPages();

  long getTotalRecords();

  List<GeoMergeInfo> getPage();

  Optional<IndexPageRequest> getNextPage();
}
