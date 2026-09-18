package edu.colorado.cires.argonaut.processor.core;

import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord;
import edu.colorado.cires.argonaut.messaging.core.databind.ProfileOperation;

public interface RemovedFileDeleteProcessor {

  MetadataRecord delete(ProfileOperation message);

}
