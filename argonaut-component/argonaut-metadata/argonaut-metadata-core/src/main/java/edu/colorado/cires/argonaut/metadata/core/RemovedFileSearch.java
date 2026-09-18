package edu.colorado.cires.argonaut.metadata.core;

import edu.colorado.cires.argonaut.messaging.core.databind.ArgoFileType;
import java.time.Instant;
import java.util.List;

public interface RemovedFileSearch extends IndexPageRequest {

  Instant olderThan();
  List<ArgoFileType> forFileTypes();

}
