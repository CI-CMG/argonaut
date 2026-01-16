package edu.colorado.cires.argonaut.geofilter;


import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;

public class GeoFilterIT {
  private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);


  private EntityManagerFactory emf;

  @BeforeEach
  void setup() throws Exception {
    Map<String, String> override = new HashMap<>();
    override.put("jakarta.persistence.jdbc.url", "jdbc:postgresql://localhost:15437/argonaut");
    override.put("jakarta.persistence.jdbc.user", "argonaut");
    override.put("jakarta.persistence.jdbc.password", "letmein");
    emf = Persistence.createEntityManagerFactory("argonaut-postgres", override);
  }

  @AfterEach
  void teardown() throws Exception {
    emf.close();
  }

  @Test
  public void test() throws Exception {
    GeoFilter filter = new GeoFilter(Paths.get("target/workingdir"), emf);
    filter.initialize();

    // North Atlantic
    assertEquals(ArgoOcean.ATLANTIC_OCEAN, filter.determineArgoOcean(geometryFactory.createPoint(new Coordinate(0d, 2d))));
    // South Atlantic
    assertEquals(ArgoOcean.ATLANTIC_OCEAN, filter.determineArgoOcean(geometryFactory.createPoint(new Coordinate(0d, -2d))));
    // Mediterranean
    assertEquals(ArgoOcean.ATLANTIC_OCEAN, filter.determineArgoOcean(geometryFactory.createPoint(new Coordinate(34.15, 43.1))));
    // Baltic
    assertEquals(ArgoOcean.ATLANTIC_OCEAN, filter.determineArgoOcean(geometryFactory.createPoint(new Coordinate(20.1, 57.1))));
    // Southern Ocean
    assertEquals(ArgoOcean.ATLANTIC_OCEAN, filter.determineArgoOcean(geometryFactory.createPoint(new Coordinate(-27.4, -68d))));
    // Arctic Ocean
    assertEquals(ArgoOcean.ATLANTIC_OCEAN, filter.determineArgoOcean(geometryFactory.createPoint(new Coordinate(-29.5, 87d))));

    // North Pacific
    assertEquals(ArgoOcean.PACIFIC_OCEAN, filter.determineArgoOcean(geometryFactory.createPoint(new Coordinate(-79d, 4d))));
    // South Pacific
    assertEquals(ArgoOcean.PACIFIC_OCEAN, filter.determineArgoOcean(geometryFactory.createPoint(new Coordinate(-81d, -4d))));
    // North Pacific
    assertEquals(ArgoOcean.PACIFIC_OCEAN, filter.determineArgoOcean(geometryFactory.createPoint(new Coordinate(177d, 12d))));
    // South Pacific
    assertEquals(ArgoOcean.PACIFIC_OCEAN, filter.determineArgoOcean(geometryFactory.createPoint(new Coordinate(177d, -12d))));
    // North Pacific
    assertEquals(ArgoOcean.PACIFIC_OCEAN, filter.determineArgoOcean(geometryFactory.createPoint(new Coordinate(-177d, 12d))));
    // South Pacific
    assertEquals(ArgoOcean.PACIFIC_OCEAN, filter.determineArgoOcean(geometryFactory.createPoint(new Coordinate(-177d, -12d))));
    // South China
    assertEquals(ArgoOcean.PACIFIC_OCEAN, filter.determineArgoOcean(geometryFactory.createPoint(new Coordinate(114d, 12d))));
    // Southern Ocean
    assertEquals(ArgoOcean.PACIFIC_OCEAN, filter.determineArgoOcean(geometryFactory.createPoint(new Coordinate(173.9, -64d))));
    // Southern Ocean
    assertEquals(ArgoOcean.PACIFIC_OCEAN, filter.determineArgoOcean(geometryFactory.createPoint(new Coordinate(170.8, -64.2d))));
    // Arctic Ocean
    assertEquals(ArgoOcean.PACIFIC_OCEAN, filter.determineArgoOcean(geometryFactory.createPoint(new Coordinate(88d, 85.2))));
    // Arctic Ocean
    assertEquals(ArgoOcean.PACIFIC_OCEAN, filter.determineArgoOcean(geometryFactory.createPoint(new Coordinate(171d, 82))));
    // Arctic Ocean
    assertEquals(ArgoOcean.PACIFIC_OCEAN, filter.determineArgoOcean(geometryFactory.createPoint(new Coordinate(-137d, 79d))));

    // Indian Ocean
    assertEquals(ArgoOcean.INDIAN_OCEAN, filter.determineArgoOcean(geometryFactory.createPoint(new Coordinate(84d, -17d))));
    // Southern Ocean
    assertEquals(ArgoOcean.INDIAN_OCEAN, filter.determineArgoOcean(geometryFactory.createPoint(new Coordinate(76.9, -64.4))));
  }
}