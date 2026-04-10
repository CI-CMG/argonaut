package edu.colorado.cires.argonaut.metadata.core;

import edu.colorado.cires.argonaut.messaging.core.databind.NcSubmissionMessage.FileType;
import java.util.Optional;

public interface IndexPageRequest {

  int getPageNumber();
  int getPageSize();
  Optional<String> getSearchDacEquals();
  Optional<String> getSearchFloatIdEquals();
  Optional<FileType> getSearchFileTypeEquals();

}
