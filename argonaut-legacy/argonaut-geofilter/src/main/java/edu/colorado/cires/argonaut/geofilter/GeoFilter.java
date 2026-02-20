package edu.colorado.cires.argonaut.geofilter;

import edu.colorado.cires.argonaut.postgres.entity.OceanEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Query;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.filter.FilterFactory;
import org.geotools.api.referencing.FactoryException;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.FeatureIterator;
import org.geotools.referencing.CRS;
import org.geotools.util.factory.GeoTools;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GeoFilter {

  private static final Logger LOGGER = LoggerFactory.getLogger(GeoFilter.class);

  private static final String SHAPE_FILE_DIR = "GOaS_v1_20211214";
  private static final String SHAPE_FILE_ZIP = SHAPE_FILE_DIR + ".zip";
  private static final String SHAPE_FILE = "goas_v01.shp";
  private static final Set<String> SHAPE_FILES = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
      "goas_v01.cpg",
      "goas_v01.dbf",
      "goas_v01.prj",
      SHAPE_FILE,
      "goas_v01.shx",
      "LICENSE_GOAS_v1.txt"
  )));

  private final FilterFactory filterFactory = CommonFactoryFinder.getFilterFactory(GeoTools.getDefaultHints());
  private final Path workingDirectory;
  private final EntityManagerFactory emf;
  private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

  public GeoFilter(Path workingDirectory, EntityManagerFactory emf) {
    this.workingDirectory = workingDirectory;
    this.emf = emf;
  }

  private static void unzip(Path zipFile, Path outputDir) throws IOException {
    try (ZipInputStream zin = new ZipInputStream(Files.newInputStream(zipFile))) {
      ZipEntry entry;
      while ((entry = zin.getNextEntry()) != null) {
        Path path = outputDir.resolve(Paths.get(entry.getName())).normalize();
        if (path.startsWith(outputDir)) { //check for zip slip attack: https://snyk.io/research/zip-slip-vulnerability
          if (entry.isDirectory()) {
            Files.createDirectories(path);
          } else {
            Path parent = path.getParent();
            if (parent != null && !Files.exists(parent)) {
              Files.createDirectories(parent);
            }
            try (OutputStream out = Files.newOutputStream(path)) {
              IOUtils.copy(zin, out);
            }
          }
        } else {
          throw new RuntimeException("Invalid zip file");
        }
      }
    }
  }

  public void initialize() {

    Set<MarineRegionsOcean> toLoad = new HashSet<>(Arrays.asList(MarineRegionsOcean.values()));
    try (EntityManager em = emf.createEntityManager()) {
      Query query = em.createQuery("SELECT o.name FROM OceanEntity o");
      List<String> results = query.getResultList();
      for (String name : results) {
        toLoad.remove(MarineRegionsOcean.getMarineRegionsOcean(name));
      }
    }

    if (!toLoad.isEmpty()) {

      Path extractDirectory = workingDirectory.resolve(SHAPE_FILE_DIR);
      Path zipFile = extractDirectory.resolve(SHAPE_FILE_ZIP);
      boolean dataInitialized = true;
      for (String fileName : SHAPE_FILES) {
        Path file = extractDirectory.resolve(fileName);
        if (!Files.exists(file)) {
          dataInitialized = false;
          break;
        }
      }
      if (!dataInitialized) {
        FileUtils.deleteQuietly(extractDirectory.toFile());
        try {
          Files.createDirectories(extractDirectory);
        } catch (IOException e) {
          throw new RuntimeException("Unable to create " + extractDirectory, e);
        }
        try (
            InputStream in = getClass().getResourceAsStream(SHAPE_FILE_ZIP);
            OutputStream out = Files.newOutputStream(zipFile);
        ) {
          IOUtils.copy(in, out);
        } catch (IOException e) {
          throw new RuntimeException("Unable to extract ocean location data zip file", e);
        }

        try {
          unzip(zipFile, extractDirectory);
        } catch (IOException e) {
          throw new RuntimeException("Unable to unzip ocean location data", e);
        }

        FileUtils.deleteQuietly(zipFile.toFile());
      }

      ShapefileDataStore dataStore = null;
      try {
        try {
          dataStore = new ShapefileDataStore(extractDirectory.resolve(SHAPE_FILE).toFile().toURI().toURL());
        } catch (MalformedURLException e) {
          throw new RuntimeException("Unable to set url", e);
        }
        dataStore.setGeometryFactory(geometryFactory);
        try {
          dataStore.forceSchemaCRS(CRS.decode("EPSG:4326"));
        } catch (IOException | FactoryException e) {
          throw new RuntimeException("Unable to set CRS when extracting shapefile data", e);
        }
        for (MarineRegionsOcean name : toLoad) {
          LOGGER.info("Loading {} map", name.getName());
          SimpleFeatureCollection collection;
          try {
            collection = dataStore.getFeatureSource()
                .getFeatures(filterFactory.equals(filterFactory.property("name"), filterFactory.literal(name.getName())));
          } catch (IOException e) {
            throw new RuntimeException("Unable to query ocean location data", e);
          }
          try (
              EntityManager em = emf.createEntityManager();
              FeatureIterator<SimpleFeature> areas = collection.features();
          ) {
            if (areas.hasNext()) {
              SimpleFeature area = areas.next();
              OceanEntity entity = new OceanEntity();
              entity.setName(name.getName());
              entity.setShape((Geometry) area.getAttribute("the_geom"));
              EntityTransaction tx = em.getTransaction();
              tx.begin();
              try {
                em.persist(entity);
                tx.commit();
              } catch (Exception e) {
                tx.rollback();
                throw e;
              }
            } else {
              throw new IllegalStateException("Shapefile missing " + name.getName());
            }
          }
          LOGGER.info("Done loading {} map", name.getName());
        }
      } finally {
        if (dataStore != null) {
          dataStore.dispose();
        }
      }
      FileUtils.deleteQuietly(extractDirectory.toFile());
    }
  }

  public ArgoOcean determineArgoOcean(Point point) {

    Optional<MarineRegionsOcean> maybeOcean;
    try (EntityManager em = emf.createEntityManager()) {
      Query query = em.createQuery("select o.name from OceanEntity o where intersects(o.shape, :point)");
      query.setParameter("point", point);
      List<String> oceans = query.getResultList();
      if (oceans.isEmpty()) {
        maybeOcean = Optional.empty();
      } else {
        maybeOcean = Optional.of(Objects.requireNonNull(MarineRegionsOcean.getMarineRegionsOcean(oceans.getFirst())));
      }
    }

    return maybeOcean.map(ocean -> {
          switch (ocean) {
            case NORTH_PACIFIC_OCEAN:
            case SOUTH_PACIFIC_OCEAN:
            case SOUTH_CHINA_AND_EASTER_ARCHIPELAGIC_SEAS:
              return ArgoOcean.PACIFIC_OCEAN;
            case NORTH_ATLANTIC_OCEAN:
            case SOUTH_ATLANTIC_OCEAN:
            case MEDITERRANEAN_REGION:
            case BALTIC_SEA:
              return ArgoOcean.ATLANTIC_OCEAN;
            case INDIAN_OCEAN:
              return ArgoOcean.INDIAN_OCEAN;
            case ARCTIC_OCEAN:
              if (point.getX() >= -70d && point.getX() < 20d) {
                return ArgoOcean.ATLANTIC_OCEAN;
              }
              return ArgoOcean.PACIFIC_OCEAN;
            case SOUTHERN_OCEAN:
              if (point.getX() >= -70d && point.getX() < 20d) {
                return ArgoOcean.ATLANTIC_OCEAN;
              }
              if (point.getX() >= 20d && point.getX() < 145d) {
                return ArgoOcean.INDIAN_OCEAN;
              }
              return ArgoOcean.PACIFIC_OCEAN;
            default:
              return ArgoOcean.UNKNOWN;
          }
        })
        .orElse(ArgoOcean.UNKNOWN);
  }
}
