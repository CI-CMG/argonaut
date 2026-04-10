package edu.colorado.cires.argonaut.processor.core;

import edu.colorado.cires.argonaut.file.core.FileStore;
import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord;
import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord.Action;
import edu.colorado.cires.argonaut.messaging.core.databind.NcSubmissionMessage;
import edu.colorado.cires.argonaut.messaging.core.databind.NcSubmissionMessage.FileType;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import org.apache.commons.io.FileUtils;
import ucar.nc2.NetcdfFile;
import ucar.nc2.NetcdfFiles;

public class DefaultMetadataRecordTransformationProcessor implements MetadataRecordTransformationProcessor {


  private static final int MS_DAY = 1000 * 60 * 60 * 24;
  private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

  private FileStore outputFileStore;
  private Path localTempDir;
  private GeoFilter geoFilter;

  @Override
  public MetadataRecord transformNcSubmissionMessage(NcSubmissionMessage message) {
    if (message.getValidationErrors() != null && !message.getValidationErrors().isEmpty()) {
      return MetadataRecord.builder()
          .withAction(Action.NONE)
          .build();
    }
    String file = outputFileStore.appendToPath(message.getDac(), message.getFloatId());
    if (FileType.PROFILE == message.getFileType()) {
      file = outputFileStore.appendToPath(file, "profiles");
    }
    file = outputFileStore.appendToPath(file, message.getFileName());
    switch (message.getOperation()) {
      case ADD:
        return createUpdateMessage(message, file);
      case REMOVE:
        return MetadataRecord.builder()
            .withFile(file)
            .withAction(Action.REMOVE)
            .build();
      default:
        return MetadataRecord.builder()
            .withFile(file)
            .withAction(Action.NONE)
            .build();
    }
  }

  private MetadataRecord createUpdateMessage(NcSubmissionMessage message, String file) {
    String path = outputFileStore.appendToPath(outputFileStore.getRoot(), "dac", file);
    Path ncFile;
    try {
      ncFile = Files.createTempFile(localTempDir, null, ".nc");
    } catch (IOException e) {
      throw new RuntimeException("Unable to create temp file", e);
    }
    Instant date;
    double latitude;
    double longitude;
    String profilerType;
    String institution;
    Instant dateUpdate;
    try {
      try {
        outputFileStore.downloadLocalFile(path, ncFile);
      } catch (IOException e) {
        throw new RuntimeException("Unable to download " + path, e);
      }
      try (NetcdfFile netcdf = NetcdfFiles.open(ncFile.toString())) {
        date = readDate(netcdf);
        latitude = netcdf.findVariable("LATITUDE").read().getDouble(0);
        longitude = netcdf.findVariable("LONGITUDE").read().getDouble(0);
        profilerType = netcdf.findVariable("WMO_INST_TYPE").read().toString().trim();
        institution = netcdf.findVariable("DATA_CENTRE").read().toString().trim();
        dateUpdate = LocalDateTime.parse(netcdf.findVariable("DATE_UPDATE").read().toString(),  DATE_TIME_FORMATTER).atZone(ZoneId.of("UTC")).toInstant();
      } catch (IOException e) {
        throw new RuntimeException("Unable to read NetCDF file: " + path, e);
      }
    } finally {
      FileUtils.deleteQuietly(ncFile.toFile());
    }

    /*
    private Double latitudeMin;
    private Double latitudeMax;
    private Double longitudeMin;
    private Double longitudeMax;
    private Ocean ocean;
    private String parameters;
    private String parameterDataMode;
     */

    return MetadataRecord.builder()
        .withFile(file)
        .withDate(date)
        .withAction(Action.UPDATE)
        .withLatitude(latitude)
        .withLongitude(longitude)
        .withOcean(geoFilter.determineArgoOcean(longitude, latitude))
        .withProfilerType(profilerType)
        .withInstitution(institution)
        .withDateUpdate(dateUpdate)
        .build();
  }

  private static Instant readDate(NetcdfFile netcdf) throws IOException {
    String refDayStr = netcdf.findVariable("REFERENCE_DATE_TIME").read().toString();
    Instant refDay = LocalDateTime.parse(refDayStr, DATE_TIME_FORMATTER).atZone(ZoneId.of("UTC")).toInstant();
    double daysSince1950 = netcdf.findVariable("JULD").read().getDouble(0);
    int days = (int) (daysSince1950);
    double fractDay = daysSince1950 - (double) days;
    int fract = (int)(fractDay * (double) MS_DAY);
    return refDay.plus(days, ChronoUnit.DAYS).plus(fract, ChronoUnit.MILLIS);
  }

  public void setOutputFileStore(FileStore outputFileStore) {
    this.outputFileStore = outputFileStore;
  }

  public void setLocalTempDir(Path localTempDir) {
    this.localTempDir = localTempDir;
    try {
      Files.createDirectories(localTempDir);
    } catch (IOException e) {
      throw new RuntimeException("Unable to create temp directory: " + localTempDir, e);
    }
  }

  public void setGeoFilter(GeoFilter geoFilter) {
    this.geoFilter = geoFilter;
  }
}
