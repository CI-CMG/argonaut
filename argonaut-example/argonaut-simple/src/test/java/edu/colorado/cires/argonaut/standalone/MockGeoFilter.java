package edu.colorado.cires.argonaut.standalone;

import edu.colorado.cires.argonaut.messaging.core.databind.ArgoOcean;
import edu.colorado.cires.argonaut.processor.core.GeoFilter;

public class MockGeoFilter implements GeoFilter {

  @Override
  public ArgoOcean determineArgoOcean(Double longitude, Double latitude) {
    return ArgoOcean.ATLANTIC_OCEAN;
  }
}
