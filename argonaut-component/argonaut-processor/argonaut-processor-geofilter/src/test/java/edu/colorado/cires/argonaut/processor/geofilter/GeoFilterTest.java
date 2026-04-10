package edu.colorado.cires.argonaut.processor.geofilter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.colorado.cires.argonaut.messaging.core.databind.ArgoOcean;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GeoFilterTest {

  private static final Path workingDir = Paths.get("target/geofilter");
  MarineRegionsGeoFilter filter;

  @BeforeEach
  void setup() throws Exception {
    FileUtils.deleteQuietly(workingDir.toFile());
    filter = new MarineRegionsGeoFilter();
    filter.setWorkingDirectory(workingDir);
    filter.initialize();

  }

  @AfterEach
  void teardown() throws Exception {
//    filter.close();
    FileUtils.deleteQuietly(workingDir.toFile());
  }

  @Test
  public void test() throws Exception {

    // North Atlantic
    assertEquals(ArgoOcean.ATLANTIC_OCEAN, filter.determineArgoOcean(0d, 2d));
    // South Atlantic
    assertEquals(ArgoOcean.ATLANTIC_OCEAN, filter.determineArgoOcean(0d, -2d));
    // Mediterranean
    assertEquals(ArgoOcean.ATLANTIC_OCEAN, filter.determineArgoOcean(34.15, 43.1));
    // Baltic
    assertEquals(ArgoOcean.ATLANTIC_OCEAN, filter.determineArgoOcean(20.1, 57.1));
    // Southern Ocean
    assertEquals(ArgoOcean.ATLANTIC_OCEAN, filter.determineArgoOcean(-27.4, -68d));
    // Arctic Ocean
    assertEquals(ArgoOcean.ATLANTIC_OCEAN, filter.determineArgoOcean(-29.5, 87d));

    // North Pacific
    assertEquals(ArgoOcean.PACIFIC_OCEAN, filter.determineArgoOcean(-79d, 4d));
    // South Pacific
    assertEquals(ArgoOcean.PACIFIC_OCEAN, filter.determineArgoOcean(-81d, -4d));
    // North Pacific
    assertEquals(ArgoOcean.PACIFIC_OCEAN, filter.determineArgoOcean(177d, 12d));
    // South Pacific
    assertEquals(ArgoOcean.PACIFIC_OCEAN, filter.determineArgoOcean(177d, -12d));
    // North Pacific
    assertEquals(ArgoOcean.PACIFIC_OCEAN, filter.determineArgoOcean(-177d, 12d));
    // South Pacific
    assertEquals(ArgoOcean.PACIFIC_OCEAN, filter.determineArgoOcean(-177d, -12d));
    // South China
    assertEquals(ArgoOcean.PACIFIC_OCEAN, filter.determineArgoOcean(114d, 12d));
    // Southern Ocean
    assertEquals(ArgoOcean.PACIFIC_OCEAN, filter.determineArgoOcean(173.9, -64d));
    // Southern Ocean
    assertEquals(ArgoOcean.PACIFIC_OCEAN, filter.determineArgoOcean(170.8, -64.2d));
    // Arctic Ocean
    assertEquals(ArgoOcean.PACIFIC_OCEAN, filter.determineArgoOcean(88d, 85.2));
    // Arctic Ocean
    assertEquals(ArgoOcean.PACIFIC_OCEAN, filter.determineArgoOcean(171d, 82));
    // Arctic Ocean
    assertEquals(ArgoOcean.PACIFIC_OCEAN, filter.determineArgoOcean(-137d, 79d));

    // Indian Ocean
    assertEquals(ArgoOcean.INDIAN_OCEAN, filter.determineArgoOcean(84d, -17d));
    // Southern Ocean
    assertEquals(ArgoOcean.INDIAN_OCEAN, filter.determineArgoOcean(76.9, -64.4));
  }
}