package edu.colorado.cires.argonaut.processor.core.transform;

import edu.colorado.cires.argonaut.core.netcdf.metadata.v31.ArgoMetadataV31;
import edu.colorado.cires.argonaut.core.netcdf.metadata.v31.ArgoMetadataV31Reader;
import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoMultiProfileV31;
import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoProfileV31;
import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoProfileV31Reader;
import edu.colorado.cires.argonaut.core.netcdf.synthprofile.v13.ArgoSyntheticMultiProfileV13;
import edu.colorado.cires.argonaut.core.netcdf.synthprofile.v13.ArgoSyntheticProfileV13;
import edu.colorado.cires.argonaut.core.netcdf.synthprofile.v13.ArgoSyntheticProfileV13Parameter;
import edu.colorado.cires.argonaut.core.netcdf.synthprofile.v13.ArgoSyntheticProfileV13Reader;
import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord;
import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord.Action;
import edu.colorado.cires.argonaut.messaging.core.databind.NcSubmissionMessage.FileType;
import edu.colorado.cires.argonaut.processor.core.GeoFilter;
import java.io.IOException;
import java.nio.file.Path;

public final class NetCdfMetadataRecord {

  private static String formatCycleNumber(int cycleNumber) {
    return String.format("%03d", cycleNumber);
  }

  public static MetadataRecord fromV31Profile(String file, String dac, Path ncFile, GeoFilter geoFilter) throws IOException {
    try (ArgoProfileV31Reader reader = new ArgoProfileV31Reader(ncFile)) {
      ArgoMultiProfileV31 multiProfile = reader.getMultiProfile();
      ArgoProfileV31 profile = null;
      for (int profileIndex = 0; profileIndex < multiProfile.getNumberOfProfiles(); profileIndex++) {
        ArgoProfileV31 pressProfile = multiProfile.getProfile(profileIndex);
        if (pressProfile.getStationParameters().contains("PRES")) {
          profile = pressProfile;
          break;
        }
      }
      if (profile == null) {
        throw new IllegalArgumentException("Pressure profile could not be found");
      }
      FileType fileType;
      switch (multiProfile.getDataType()) {
        case "B-Argo profile":
          fileType = FileType.B_ARGO_PROFILE;
          break;
        default:
          fileType = FileType.CORE_ARGO_PROFILE;
          break;
      }

      return MetadataRecord.builder()
          .withFile(file)
          .withDac(dac)
          .withFloatId(profile.getPlatformNumber())
          .withParameterDataMode(profile.getParameter("PRES").getDataMode())
          .withDirection(profile.getDirection())
          .withCycleNumber(formatCycleNumber(profile.getCycleNumber()))
          .withDate(profile.getJulianDate())
          .withAction(Action.UPDATE)
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

  public static MetadataRecord fromV13SyntheticProfile(String file, String dac, Path ncFile, GeoFilter geoFilter) throws IOException {
    try (ArgoSyntheticProfileV13Reader reader = new ArgoSyntheticProfileV13Reader(ncFile)) {
      ArgoSyntheticMultiProfileV13 multiProfile = reader.getMultiProfile();
      ArgoSyntheticProfileV13 profile = null;
      for (ArgoSyntheticProfileV13 pressProfile : multiProfile.getProfiles()) {
        if (pressProfile.getStationParameters().contains("PRES")) {
          profile = pressProfile;
          break;
        }
      }
      if (profile == null) {
        throw new IllegalArgumentException("Pressure profile could not be found");
      }

      ArgoSyntheticProfileV13Parameter parameter = profile.getParameters().stream().filter(p -> p.getParameterName().equals("PRES")).findFirst().orElseThrow();

      return MetadataRecord.builder()
          .withFile(file)
          .withDac(dac)
          .withFloatId(profile.getPlatformNumber())
          .withParameterDataMode(parameter.getDataMode())
          .withDirection(profile.getDirection())
          .withCycleNumber(formatCycleNumber(profile.getCycleNumber()))
          .withDate(profile.getJulianDate())
          .withAction(Action.UPDATE)
          .withLatitude(profile.getLatitude())
          .withLongitude(profile.getLongitude())
          .withOcean(geoFilter.determineArgoOcean(profile.getLongitude(), profile.getLatitude()))
          .withProfilerType(profile.getWmoInstrumentType())
          .withInstitution(profile.getDataCenter())
          .withDateUpdate(profile.getDateUpdate())
          .withFileType(FileType.BGC_ARGO_SYNTH_PROFILE)

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
          .withLatitude(metadata.getLaunchLatitude())
          .withLongitude(metadata.getLaunchLongitude())
          .withOcean(geoFilter.determineArgoOcean(metadata.getLaunchLongitude(), metadata.getLaunchLatitude()))
          .withProfilerType(metadata.getWmoInstrumentType())
          .withInstitution(metadata.getDataCenter())
          .withDateUpdate(metadata.getDateUpdate())
          .withFileType(FileType.METADATA)
          .build();
    }


  }

  private NetCdfMetadataRecord() {

  }
}
