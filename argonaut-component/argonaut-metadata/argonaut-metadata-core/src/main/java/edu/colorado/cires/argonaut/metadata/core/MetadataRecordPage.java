package edu.colorado.cires.argonaut.metadata.core;

import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord;
import java.util.List;
import java.util.Optional;

public interface MetadataRecordPage extends IndexPageRequest {

  @Override
  int getPageNumber();
  @Override
  int getPageSize();
  int getTotalPages();
  long getTotalRecords();
  List<MetadataRecord> getPage();
  Optional<IndexPageRequest> getNextPage();

}
