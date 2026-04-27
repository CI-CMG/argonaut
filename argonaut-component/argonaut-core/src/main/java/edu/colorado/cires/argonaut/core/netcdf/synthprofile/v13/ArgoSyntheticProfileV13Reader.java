package edu.colorado.cires.argonaut.core.netcdf.synthprofile.v13;

import edu.colorado.cires.argonaut.core.netcdf.synthprofile.v13.impl.NetCdfTiedArgoSyntheticMultiProfileV13;
import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Path;
import ucar.nc2.NetcdfFile;
import ucar.nc2.NetcdfFiles;

public class ArgoSyntheticProfileV13Reader implements Closeable {

  private final NetcdfFile netcdf;

  public ArgoSyntheticProfileV13Reader(Path netCdfFile) throws IOException {
    netcdf = NetcdfFiles.open(netCdfFile.toString());
  }

  public ArgoSyntheticMultiProfileV13 getMultiProfile() {
    return new NetCdfTiedArgoSyntheticMultiProfileV13(netcdf);
  }

  @Override
  public void close() throws IOException {
    netcdf.close();
  }
}
