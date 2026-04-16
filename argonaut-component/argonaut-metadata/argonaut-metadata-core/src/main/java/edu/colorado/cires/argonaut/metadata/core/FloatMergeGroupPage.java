package edu.colorado.cires.argonaut.metadata.core;

import edu.colorado.cires.argonaut.messaging.core.databind.FloatMergeGroup;
import java.util.List;
import java.util.Optional;

public interface FloatMergeGroupPage extends IndexPageRequest {

  @Override
  int getPageNumber();

  @Override
  int getPageSize();

  int getTotalPages();

  long getTotalRecords();

  List<FloatMergeGroup> getPage();

  Optional<IndexPageRequest> getNextPage();
}
