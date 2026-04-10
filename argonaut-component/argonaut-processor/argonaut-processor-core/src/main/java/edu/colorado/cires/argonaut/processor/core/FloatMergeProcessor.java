package edu.colorado.cires.argonaut.processor.core;

import edu.colorado.cires.argonaut.messaging.core.databind.FloatMergeGroup;
import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord;

public interface FloatMergeProcessor {

  MetadataRecord merge(FloatMergeGroup message);

}
