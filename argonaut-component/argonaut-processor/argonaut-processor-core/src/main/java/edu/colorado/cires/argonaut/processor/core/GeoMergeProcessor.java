package edu.colorado.cires.argonaut.processor.core;

import edu.colorado.cires.argonaut.messaging.core.databind.GeoMergeInfo;

public interface GeoMergeProcessor {

  void merge(GeoMergeInfo message);

}
