package edu.colorado.cires.argonaut.processor.core;

import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord;

public interface UpdateIndexProcessor {

  void updateIndex(MetadataRecord record);

}
