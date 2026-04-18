package edu.colorado.cires.argonaut.core.netcdf.profile.v31.impl;

import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoProfileV31Calibration;
import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoProfileV31Level;
import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoProfileV31Parameter;
import edu.colorado.cires.argonaut.core.util.NetCdfUtils;
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


  public NetCdfTiedArgoProfileV31Parameter(NetCdfTiedArgoProfileV31 parent, String parameterName, int paramIndex) {
    this.parent = parent;
    netcdf = parent.getNetcdf();
    this.parameterName = parameterName;
    profileIndex = parent.getProfileIndex();
    numLevels = NetCdfUtils.getDimensionSize(netcdf, "N_LEVELS");
    numCalibrations = NetCdfUtils.getDimensionSize(netcdf, "N_CALIB");
    this.paramIndex = paramIndex;
    qc = NetCdfUtils.getLevel1String(netcdf, parent.getProfileIndex(), "PROFILE_" + parameterName + "_QC");
  }

  NetcdfFile getNetcdf() {
    return netcdf;
  }

  int getProfileIndex() {
    return profileIndex;
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
    List<ArgoProfileV31Level> levels = new ArrayList<>(numLevels);
    for (int levelIndex = 0; levelIndex < numLevels; levelIndex++) {
      levels.add(new NetCdfTiedArgoProfileV31Level(this, levelIndex));
    }
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

  int getParamIndex() {
    return paramIndex;
  }
}
