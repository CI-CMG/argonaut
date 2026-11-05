package edu.colorado.cires.argonaut.core.merge.synthetic;

import edu.colorado.cires.argonaut.core.netcdf.metadata.v31.ArgoMetadataV31;
import edu.colorado.cires.argonaut.core.netcdf.metadata.v31.ArgoMetadataV31Reader;
import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoProfileV31;
import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoProfileV31Reader;
import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

class MergeContext implements Closeable {

  private final ArgoMetadataV31Reader metadataReader;
  private final ArgoProfileV31Reader cProfileReader;
  private final ArgoProfileV31Reader bProfileReader;

  private List<ArgoProfileV31> cProfiles = Collections.emptyList();
  private List<ArgoProfileV31> bProfiles = Collections.emptyList();
  private ArgoMetadataV31 metadata = null;
  private List<String> stationParameters = Collections.emptyList();

  private MergeContext(ArgoMetadataV31Reader metadataReader, ArgoProfileV31Reader cProfileReader,
    ArgoProfileV31Reader bProfileReader) {
    this.metadataReader = metadataReader;
    this.cProfileReader = cProfileReader;
    this.bProfileReader = bProfileReader;

    init();
  }

  public static MergeContext create(Path metadataPath, Path cProfilePath, Path bProfilePath) throws IOException {
    return new MergeContext(
      new ArgoMetadataV31Reader(metadataPath),
      new ArgoProfileV31Reader(cProfilePath),
      new ArgoProfileV31Reader(bProfilePath)
    );
  }

  private void init() {
    this.metadata = metadataReader.getMetadata();
    this.cProfiles = cProfileReader.getMultiProfile().getProfiles();
    this.bProfiles = bProfileReader.getMultiProfile().getProfiles();
    this.stationParameters = getStationParameters(cProfiles, bProfiles);
  }

  @Override
  public void close() throws IOException {
    metadataReader.close();
    cProfileReader.close();
    bProfileReader.close();
  }

  public List<ArgoProfileV31> getCProfiles() {
    return cProfiles;
  }

  public List<ArgoProfileV31> getBProfiles() {
    return bProfiles;
  }

  public ArgoMetadataV31 getMetadata() {
    return metadata;
  }

  public List<String> getStationParameters() {
    return stationParameters;
  }

  private static List<String> getStationParameters(List<ArgoProfileV31> cProfiles, List<ArgoProfileV31> bProfiles) {
    Set<String> parameterNames = new LinkedHashSet<>();
    for (ArgoProfileV31 profile : cProfiles) {
      for (String parameterName : profile.getStationParameters()) {
        if (MergeConstants.CoreParameterFilter.isSupportedParameter(parameterName)) {
          parameterNames.add(parameterName);
        }
      }
    }
    for (ArgoProfileV31 profile : bProfiles) {
      for (String parameterName : profile.getStationParameters()) {
        if (MergeConstants.BioParameterFilter.isSupportedParameter(parameterName)) {
          parameterNames.add(parameterName);
        }
      }
    }
    return new ArrayList<>(parameterNames);
  }

}
