package edu.colorado.cires.argonaut.core.netcdf.v31;

import edu.colorado.cires.argonaut.core.netcdf.v31.impl.NetCdfTiedArgoMultiProfileV31;
import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Path;
import ucar.nc2.NetcdfFile;
import ucar.nc2.NetcdfFiles;

public class ArgoProfileV31Reader implements Closeable {

  private final NetcdfFile netcdf;

  public ArgoProfileV31Reader(Path netCdfFile) throws IOException {
    netcdf = NetcdfFiles.open(netCdfFile.toString());
  }

  public ArgoMultiProfileV31 getMultiProfile() {
    return new NetCdfTiedArgoMultiProfileV31(netcdf);

  }

  public ArgoProfileV31 getProfile() {
    throw new UnsupportedOperationException("Not implemented yet");
  }

  @Override
  public void close() throws IOException {
    netcdf.close();
  }
}
