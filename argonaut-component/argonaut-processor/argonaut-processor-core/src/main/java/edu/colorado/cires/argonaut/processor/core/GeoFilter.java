package edu.colorado.cires.argonaut.processor.core;

import edu.colorado.cires.argonaut.messaging.core.databind.ArgoOcean;

public interface GeoFilter {

  ArgoOcean determineArgoOcean(double longitude, double latitude);
}
