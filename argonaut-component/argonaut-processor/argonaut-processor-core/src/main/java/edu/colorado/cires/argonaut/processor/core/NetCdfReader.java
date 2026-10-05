package edu.colorado.cires.argonaut.processor.core;

import edu.colorado.cires.argonaut.core.netcdf.trajectory.v31.impl.NetCdfArgoTrajectoryV31;
import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord;
import edu.colorado.cires.argonaut.messaging.core.databind.NcSubmissionMessage;
import edu.colorado.cires.argonaut.processor.core.transform.NetCdfMetadataRecord;
import java.io.IOException;
import org.apache.commons.lang3.function.FailableFunction;

public class NetCdfReader implements FailableFunction<ReadFileRequest, MetadataRecord, IOException> {

  private GeoFilter geoFilter;

  public void setGeoFilter(GeoFilter geoFilter) {
    this.geoFilter = geoFilter;
  }

  @Override
  public MetadataRecord apply(ReadFileRequest input) throws IOException {
    NcSubmissionMessage message = input.message();
    return switch (message.getFileType()) {
      case PROFILE_CORE, PROFILE_BIOCHEMICAL -> NetCdfMetadataRecord.fromV31Profile(input.file(), message.getDac(), input.ncFile(), geoFilter);
      case METADATA -> NetCdfMetadataRecord.fromV31Metadata(input.file(), message.getDac(), input.ncFile(), geoFilter);
      case TRAJECTORY -> {
        try (NetCdfArgoTrajectoryV31 trajectoryV31 = NetCdfArgoTrajectoryV31.create(input.ncFile())) {
          yield NetCdfMetadataRecord.fromV31Trajectory(input.file(), message.getDac(), trajectoryV31);
        }
      }
      default -> throw new UnsupportedOperationException("Unsupported file type: " + message.getFileType());
    };
  }
}
