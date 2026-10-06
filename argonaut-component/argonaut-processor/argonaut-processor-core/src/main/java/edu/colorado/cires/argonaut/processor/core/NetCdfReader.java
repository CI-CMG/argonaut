package edu.colorado.cires.argonaut.processor.core;

import edu.colorado.cires.argonaut.core.netcdf.technical.v31.impl.NetCdfArgoTechnicalV31;
import edu.colorado.cires.argonaut.core.netcdf.trajectory.v31.impl.NetCdfArgoTrajectoryV31;
import edu.colorado.cires.argonaut.messaging.core.databind.ArgoFileType;
import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord;
import edu.colorado.cires.argonaut.messaging.core.databind.NcSubmissionMessage;
import edu.colorado.cires.argonaut.processor.core.transform.NetCdfMetadataRecord;
import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Function;
import org.apache.commons.lang3.function.FailableFunction;

public class NetCdfReader implements FailableFunction<ReadFileRequest, MetadataRecord, IOException> {

  private GeoFilter geoFilter;

  public void setGeoFilter(GeoFilter geoFilter) {
    this.geoFilter = geoFilter;
  }

  @Override
  public MetadataRecord apply(ReadFileRequest input) throws IOException {
    NcSubmissionMessage message = input.message();

    String file = input.file();
    String dac = message.getDac();
    Path path = input.ncFile();
    ArgoFileType fileType = message.getFileType();

    return switch (fileType) {
      case PROFILE_CORE, PROFILE_BIOCHEMICAL -> NetCdfMetadataRecord.fromV31Profile(file, dac, path, geoFilter);
      case METADATA -> NetCdfMetadataRecord.fromV31Metadata(file, dac, path, geoFilter);
      case TRAJECTORY -> readNetCdfFile(path, NetCdfArgoTrajectoryV31::create, (argoTraj) -> NetCdfMetadataRecord.fromV31Trajectory(
        file, dac, argoTraj));
      case TECHNICAL_DATA -> readNetCdfFile(path, NetCdfArgoTechnicalV31::create, (argoTech) -> NetCdfMetadataRecord.fromV31Technical(
        file, dac, argoTech));
      default -> throw new UnsupportedOperationException("Unsupported file type: " + fileType);
    };
  }

  private static <T extends AutoCloseable> MetadataRecord readNetCdfFile(Path path, FailableFunction<Path, T, Exception> read, Function<T, MetadataRecord> parse) {
    try (T object = read.apply(path)) {
      return parse.apply(object);
    } catch (Exception e) {
      throw new IllegalArgumentException("Failed to parse NetCDF file", e);
    }
  }
}
