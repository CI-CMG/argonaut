package edu.colorado.cires.argonaut.processor.core;

import edu.colorado.cires.argonaut.messaging.core.databind.FloatMergeGroup;

public interface FloatMergeProcessor {

  void merge(FloatMergeGroup message);

}
