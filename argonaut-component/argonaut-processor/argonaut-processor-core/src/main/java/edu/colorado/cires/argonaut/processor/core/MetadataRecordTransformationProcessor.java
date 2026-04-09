package edu.colorado.cires.argonaut.processor.core;

import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord;
import edu.colorado.cires.argonaut.messaging.core.databind.NcSubmissionMessage;

public interface MetadataRecordTransformationProcessor {

  MetadataRecord transformNcSubmissionMessage(NcSubmissionMessage message);
}
