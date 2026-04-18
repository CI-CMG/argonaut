package edu.colorado.cires.argonaut.core.netcdf.metadata.v31;

import edu.colorado.cires.argonaut.core.netcdf.metadata.v31.impl.NetCdfTiedArgoMetadataV31;
import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Path;
import ucar.nc2.NetcdfFile;
import ucar.nc2.NetcdfFiles;

public class ArgoMetadataV31Reader implements Closeable {

  private final NetcdfFile netcdf;

  public ArgoMetadataV31Reader(Path netCdfFile) throws IOException {
    netcdf = NetcdfFiles.open(netCdfFile.toString());
  }

  public ArgoMetadataV31 getMetadata() {
    return new NetCdfTiedArgoMetadataV31(netcdf);
  }

  @Override
  public void close() throws IOException {
    netcdf.close();
  }
}
