package edu.colorado.cires.argonaut.processor.core;

import edu.colorado.cires.argonaut.core.netcdf.trajectory.v31.impl.NetCdfArgoTrajectoryV31;
import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord;
import edu.colorado.cires.argonaut.processor.core.transform.NetCdfMetadataRecord;
import java.io.IOException;
import java.nio.file.Path;
import org.apache.commons.lang3.function.TriFunction;

public class ReaderFactory {

  public static TriFunction<String, String, Path, MetadataRecord> trajectoryReader() {
    return (file, dac, netCdfFile) -> {
      try {
        return NetCdfMetadataRecord.fromV31Trajectory(file, dac, netCdfFile, path -> {
          try {
            return new NetCdfArgoTrajectoryV31(path);
          } catch (IOException e) {
            throw new IllegalArgumentException("failed to open NetCDF file", e);
          }
        });
      } catch (Exception e) {
        throw new IllegalArgumentException("failed to parse NetCDF file to trajectory format", e);
      }
    };
  }

}
