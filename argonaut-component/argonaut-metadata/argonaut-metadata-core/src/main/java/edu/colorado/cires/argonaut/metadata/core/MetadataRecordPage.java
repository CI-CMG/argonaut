package edu.colorado.cires.argonaut.metadata.core;

import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord;
import java.util.List;

public interface MetadataRecordPage {

  int getPageNumber();
  int getPageSize();
  int getTotalPages();
  int getTotalRecords();
  List<MetadataRecord> getPage();

}
