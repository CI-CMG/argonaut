package edu.colorado.cires.argonaut.processor.core.transform;

import edu.colorado.cires.argonaut.core.netcdf.metadata.v31.ArgoMetadataV31;
import edu.colorado.cires.argonaut.core.netcdf.metadata.v31.ArgoMetadataV31Reader;
import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoMultiProfileV31;
import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoProfileV31;
import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoProfileV31Parameter;
import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoProfileV31Reader;
import edu.colorado.cires.argonaut.core.netcdf.synthprofile.v13.ArgoSyntheticMultiProfileV13;
import edu.colorado.cires.argonaut.core.netcdf.synthprofile.v13.ArgoSyntheticProfileV13;
import edu.colorado.cires.argonaut.core.netcdf.synthprofile.v13.ArgoSyntheticProfileV13Parameter;
import edu.colorado.cires.argonaut.core.netcdf.synthprofile.v13.ArgoSyntheticProfileV13Reader;
import edu.colorado.cires.argonaut.core.netcdf.trajectory.v31.ArgoTrajectoryV31;
import edu.colorado.cires.argonaut.core.util.CommonParameterValues;
import edu.colorado.cires.argonaut.messaging.core.databind.ArgoFileType;
import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord;
import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord.Action;
import edu.colorado.cires.argonaut.messaging.core.databind.ProfileMode;
import edu.colorado.cires.argonaut.processor.core.GeoFilter;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.lang3.function.TriFunction;

public final class NetCdfMetadataRecord {

  private static String formatCycleNumber(int cycleNumber) {
    return String.format("%03d", cycleNumber);
  }

  public static MetadataRecord fromV31Profile(String file, String dac, Path ncFile, GeoFilter geoFilter) throws IOException {
    try (ArgoProfileV31Reader reader = new ArgoProfileV31Reader(ncFile)) {
      ArgoMultiProfileV31 multiProfile = reader.getMultiProfile();
      ArgoProfileV31 profile = null;
      for (int profileIndex = 0; profileIndex < multiProfile.getNumberOfProfiles(); profileIndex++) {
        ArgoProfileV31 aProfile = multiProfile.getProfile(profileIndex);
        if(aProfile.getStationParameters().size() > 1 && aProfile.getStationParameters().contains("PRES")){
          profile = aProfile;
          break;
        }
      }
      if (profile == null) {
        throw new IllegalArgumentException("Pressure profile could not be found");
      }
      ArgoFileType fileType;
      switch (multiProfile.getDataType()) {
        case "B-Argo profile":
          fileType = ArgoFileType.PROFILE_BIOCHEMICAL;
          break;
        default:
          fileType = ArgoFileType.PROFILE_CORE;
          break;
      }

      String parameterDataMode = profile.getParameters().stream().map(ArgoProfileV31Parameter::getDataMode).collect(Collectors.joining());

      ProfileMode profileMode = null;
      if(profile.getDataMode() != null) {
        profileMode = ProfileMode.fromCharacter(profile.getDataMode());
      } else {
        profileMode = ProfileMode.fromCharacter(profile.getParameters().stream().filter(p -> p.getParameterName().equals("PRES")).findFirst().orElseThrow().getDataMode());
      }

      return MetadataRecord.builder()
          .withFile(file)
          .withDac(dac)
          .withFloatId(profile.getPlatformNumber())
          .withProfileMode(profileMode)
          .withParameterDataMode(parameterDataMode)
          .withParameters(profile.getStationParameters())
          .withDirection(profile.getDirection())
          .withCycleNumber(formatCycleNumber(profile.getCycleNumber()))
          .withDate(profile.getJulianDate())
          .withAction(Action.UPDATE)
          .withActionTimestamp(Instant.now())
          .withLatitude(profile.getLatitude())
          .withLongitude(profile.getLongitude())
          .withOcean(geoFilter.determineArgoOcean(profile.getLongitude(), profile.getLatitude()))
          .withProfilerType(profile.getWmoInstrumentType())
          .withInstitution(profile.getDataCenter())
          .withDateUpdate(profile.getDateUpdate())
          .withFileType(fileType)
          .build();
    }
  }

  public static MetadataRecord fromV13SyntheticProfile(String file, String fileName, String dac, Path ncFile, GeoFilter geoFilter) throws IOException {
    try (ArgoSyntheticProfileV13Reader reader = new ArgoSyntheticProfileV13Reader(ncFile)) {
      ArgoSyntheticMultiProfileV13 multiProfile = reader.getMultiProfile();
      ArgoSyntheticProfileV13 profile = null;
      for (int profileIndex = 0; profileIndex < multiProfile.getNumberOfProfiles(); profileIndex++) {
        ArgoSyntheticProfileV13 aProfile = multiProfile.getProfiles().get(profileIndex);
        if(aProfile.getStationParameters().size() > 1 && aProfile.getStationParameters().contains("PRES")){
          profile = aProfile;
          break;
        }
      }
      if (profile == null) {
        throw new IllegalArgumentException("Pressure profile could not be found");
      }

      ArgoSyntheticProfileV13Parameter presParam = profile.getParameters().stream().filter(p -> p.getParameterName().equals("PRES")).findFirst().orElseThrow();


      ProfileMode profileMode = null;
      if(profile.getDataMode() != null) {
        profileMode = ProfileMode.fromCharacter(profile.getDataMode());
      } else {
        profileMode = ProfileMode.fromCharacter(presParam.getDataMode());
      }

      String parameterDataMode = profile.getParameters().stream().map(CommonParameterValues::getDataMode).collect(Collectors.joining());


      return MetadataRecord.builder()
          .withFile(file)
          .withFileName(fileName)
          .withDac(dac)
          .withFloatId(profile.getPlatformNumber())
          .withProfileMode(profileMode)
          .withParameterDataMode(parameterDataMode)
          .withParameters(profile.getStationParameters())
          .withDirection(profile.getDirection())
          .withCycleNumber(formatCycleNumber(profile.getCycleNumber()))
          .withDate(profile.getJulianDate())
          .withAction(Action.UPDATE)
          .withActionTimestamp(Instant.now())
          .withLatitude(profile.getLatitude())
          .withLongitude(profile.getLongitude())
          .withOcean(geoFilter.determineArgoOcean(profile.getLongitude(), profile.getLatitude()))
          .withProfilerType(profile.getWmoInstrumentType())
          .withInstitution(profile.getDataCenter())
          .withDateUpdate(profile.getDateUpdate())
          .withFileType(ArgoFileType.SYNTHETIC_PROFILE_SINGLE_CYCLE)

          .build();
    }
  }


  public static MetadataRecord fromV31Metadata(String file, String dac, Path ncFile, GeoFilter geoFilter) throws IOException {
    try (ArgoMetadataV31Reader reader = new ArgoMetadataV31Reader(ncFile)) {
      ArgoMetadataV31 metadata = reader.getMetadata();
      return MetadataRecord.builder()
          .withFile(file)
          .withDac(dac)
          .withFloatId(metadata.getPlatformNumber())
          .withDate(metadata.getDateUpdate())
          .withAction(Action.UPDATE)
          .withActionTimestamp(Instant.now())
          .withLatitude(metadata.getLaunchLatitude())
          .withLongitude(metadata.getLaunchLongitude())
          .withOcean(geoFilter.determineArgoOcean(metadata.getLaunchLongitude(), metadata.getLaunchLatitude()))
          .withProfilerType(metadata.getWmoInstrumentType())
          .withInstitution(metadata.getDataCenter())
          .withDateUpdate(metadata.getDateUpdate())
          .withFileType(ArgoFileType.METADATA)
          .build();
    }


  }

  public static MetadataRecord fromV31Trajectory(String file, String dac, Path ncFile, Function<Path, ArgoTrajectoryV31> reader) throws Exception {
    try (ArgoTrajectoryV31 trajectory = reader.apply(ncFile)) {
      double minLat = Double.NaN;
      double maxLat = Double.NaN;

      double minLon = Double.NaN;
      double maxLon = Double.NaN;

      TriFunction<Double, Double, BiFunction<Double, Double, Double>, Double> compareLimits = (base, value, comparator) -> {
        assert !Double.isNaN(value);

        if (Double.isNaN(base)) {
          return value;
        }

        return comparator.apply(base, value);
      };

      BiFunction<Double, Double, Double> compareMin = (base, value) -> compareLimits.apply(base, value, Math::min);
      BiFunction<Double, Double, Double> compareMax = (base, value) -> compareLimits.apply(base, value, Math::max);

      for (int i = 0; i < trajectory.getNMeasurements(); i++) {
        double lat = trajectory.getLatitude(i);
        double lon = trajectory.getLongitude(i);

        minLat = compareMin.apply(minLat, lat);
        maxLat = compareMax.apply(maxLat, lat);

        minLon = compareMin.apply(minLon, lon);
        maxLon = compareMax.apply(maxLon, lon);
      }

      return MetadataRecord.builder()
        .withAction(Action.UPDATE)
        .withFileType(ArgoFileType.TRAJECTORY)
        .withFile(file)
        .withDac(dac)
        .withFloatId(trajectory.getPlatformNumber())
        .withProfilerType(trajectory.getWmoInstrumentType())
        .withInstitution(trajectory.getDataCenter())
        .withDateUpdate(trajectory.getDateUpdate())
        .withLatitudeMax(maxLat)
        .withLatitudeMin(minLat)
        .withLongitudeMin(minLon)
        .withLongitudeMax(maxLon)
        .build();
    }
  }

  private NetCdfMetadataRecord() {

  }
}
