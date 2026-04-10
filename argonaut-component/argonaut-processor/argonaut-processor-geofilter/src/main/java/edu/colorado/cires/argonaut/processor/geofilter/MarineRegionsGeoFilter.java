package edu.colorado.cires.argonaut.processor.geofilter;

import edu.colorado.cires.argonaut.messaging.core.databind.ArgoOcean;
import edu.colorado.cires.argonaut.processor.core.GeoFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.geotools.api.data.SimpleFeatureSource;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.filter.FilterFactory;
import org.geotools.data.DataUtilities;
import org.geotools.data.collection.SpatialIndexFeatureCollection;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.store.ContentFeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.util.factory.GeoTools;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;

public class MarineRegionsGeoFilter implements GeoFilter {

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
  private Path workingDirectory;
  private SimpleFeatureSource source;
  private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
  private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);


  public void setWorkingDirectory(Path workingDirectory) {
    this.workingDirectory = workingDirectory;
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

  public void initialize() throws IOException {
    lock.writeLock().lock();
    try {
      boolean initialized = true;
      Path extractDirectory = workingDirectory.resolve(SHAPE_FILE_DIR);
      for (String fileName : SHAPE_FILES) {
        if (!Files.exists(extractDirectory.resolve(fileName))) {
          initialized = false;
          break;
        }
      }
      if (!initialized) {
        FileUtils.deleteQuietly(extractDirectory.toFile());
        Files.createDirectories(extractDirectory);
        Path zipFile = extractDirectory.resolve(SHAPE_FILE_ZIP);
        FileUtils.deleteQuietly(zipFile.toFile());
        try (
            InputStream in = getClass().getResourceAsStream(SHAPE_FILE_ZIP);
            OutputStream out = Files.newOutputStream(zipFile);
        ) {
          IOUtils.copy(in, out);
        }
        try {
          unzip(zipFile, extractDirectory);
        } finally {
          FileUtils.deleteQuietly(zipFile.toFile());
        }
      }
      ShapefileDataStore dataStore = new ShapefileDataStore(extractDirectory.resolve(SHAPE_FILE).toFile().toURI().toURL());
      dataStore.setIndexed(false);
      dataStore.setIndexCreationEnabled(false);
      dataStore.setFidIndexed(false);
      dataStore.setTryCPGFile(true);
      dataStore.setGeometryFactory(geometryFactory);
      try {
        ContentFeatureSource featureSource = dataStore.getFeatureSource();
        SpatialIndexFeatureCollection collection = new SpatialIndexFeatureCollection(featureSource.getSchema());
        collection.addAll(featureSource.getFeatures());
        source = DataUtilities.source(collection);
      } finally {
        dataStore.dispose();
      }
    } finally {
      lock.writeLock().unlock();
    }
  }

  @Override
  public ArgoOcean determineArgoOcean(double longitude, double latitude) {
    Point point = geometryFactory.createPoint(new Coordinate(longitude, latitude));
    lock.readLock().lock();
    try {
      SimpleFeatureCollection collection = source.getFeatures(
          filterFactory.intersects(filterFactory.property("the_geom"), filterFactory.literal(point)));
      Optional<MarineRegionsOcean> maybeOcean;
      try (SimpleFeatureIterator it = collection.features()) {
        if (it.hasNext()) {
          SimpleFeature feature = it.next();
          String name = feature.getProperty("name").getValue().toString();
          maybeOcean = Optional.of(MarineRegionsOcean.getMarineRegionsOcean(name));
        } else {
          maybeOcean = Optional.empty();
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
    } catch (IOException e) {
      throw new RuntimeException("An error occurred when deterining ocean", e);
    } finally {
      lock.readLock().unlock();
    }
  }
}
