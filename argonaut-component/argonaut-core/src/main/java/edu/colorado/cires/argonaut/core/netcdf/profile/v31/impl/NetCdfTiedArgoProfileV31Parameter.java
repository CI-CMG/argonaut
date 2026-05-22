package edu.colorado.cires.argonaut.core.netcdf.profile.v31.impl;

import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoProfileV31Calibration;
import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoProfileV31Level;
import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoProfileV31Parameter;
import edu.colorado.cires.argonaut.core.util.NetCdfReadUtils;
import java.util.ArrayList;
import java.util.List;
import ucar.nc2.NetcdfFile;

public class NetCdfTiedArgoProfileV31Parameter implements ArgoProfileV31Parameter {

  private final NetCdfTiedArgoProfileV31 parent;
  private final NetcdfFile netcdf;
  private final String parameterName;
  private final int profileIndex;
  private final String qc;
  private final int numLevels;
  private final int numCalibrations;
  private final int paramIndex;
  private final List<ArgoProfileV31Level> levels;
  private final String dataMode;


  public NetCdfTiedArgoProfileV31Parameter(NetCdfTiedArgoProfileV31 parent, String parameterName, int paramIndex) {
    this.parent = parent;
    netcdf = parent.getNetcdf();
    this.parameterName = parameterName;
    profileIndex = parent.getProfileIndex();
    numLevels = NetCdfReadUtils.getDimensionSize(netcdf, "N_LEVELS");
    numCalibrations = NetCdfReadUtils.getDimensionSize(netcdf, "N_CALIB");
    this.paramIndex = paramIndex;
    qc = NetCdfReadUtils.getLevel1String(netcdf, parent.getProfileIndex(), "PROFILE_" + parameterName + "_QC");
    levels = new ArrayList<>(numLevels);
    for (int levelIndex = 0; levelIndex < numLevels; levelIndex++) {
      levels.add(new NetCdfTiedArgoProfileV31Level(this, levelIndex));
    }
    dataMode = NetCdfReadUtils.getLevel2String(netcdf, profileIndex, paramIndex, "PARAMETER_DATA_MODE");

  }

  NetcdfFile getNetcdf() {
    return netcdf;
  }

  int getProfileIndex() {
    return profileIndex;
  }

  @Override
  public int getParameterIndex() {
    return paramIndex;
  }

  @Override
  public String getParameterName() {
    return parameterName;
  }

  @Override
  public String getQc() {
    return qc;
  }

  @Override
  public List<ArgoProfileV31Level> getLevels() {
    return levels;
  }

  @Override
  public List<ArgoProfileV31Calibration> getCalibrations() {
    List<ArgoProfileV31Calibration> calibrations = new ArrayList<>(numCalibrations);
    for (int calibrationIndex = 0; calibrationIndex < numCalibrations; calibrationIndex++) {
      calibrations.add(new NetCdfTiedArgoProfileV31Calibration(this, calibrationIndex));
    }
    return calibrations;
  }

  @Override
  public String getDataMode() {
    return dataMode;
  }
}
