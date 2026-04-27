package edu.colorado.cires.argonaut.core.netcdf.synthprofile.v13.impl;

import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoProfileDataMode;
import edu.colorado.cires.argonaut.core.netcdf.synthprofile.v13.ArgoSyntheticProfileV13Level;
import edu.colorado.cires.argonaut.core.netcdf.synthprofile.v13.ArgoSyntheticProfileV13Parameter;
import edu.colorado.cires.argonaut.core.util.NetCdfUtils;
import java.util.ArrayList;
import java.util.List;
import ucar.nc2.NetcdfFile;

public class NetCdfTiedArgoSyntheticProfileV13Parameter implements ArgoSyntheticProfileV13Parameter {

  private final NetCdfTiedArgoSyntheticProfileV13 parent;
  private final NetcdfFile netcdf;
  private final String parameterName;
  private final int profileIndex;
  private final String qc;
  private final int numLevels;
  private final int numCalibrations;
  private final int paramIndex;
  private final List<ArgoSyntheticProfileV13Level> levels;


  public NetCdfTiedArgoSyntheticProfileV13Parameter(NetCdfTiedArgoSyntheticProfileV13 parent, String parameterName, int paramIndex) {
    this.parent = parent;
    netcdf = parent.getNetcdf();
    this.parameterName = parameterName;
    profileIndex = parent.getProfileIndex();
    numLevels = NetCdfUtils.getDimensionSize(netcdf, "N_LEVELS");
    numCalibrations = NetCdfUtils.getDimensionSize(netcdf, "N_CALIB");
    this.paramIndex = paramIndex;
    qc = NetCdfUtils.getLevel1String(netcdf, parent.getProfileIndex(), "PROFILE_" + parameterName + "_QC");
    levels = new ArrayList<>(numLevels);
    for (int levelIndex = 0; levelIndex < numLevels; levelIndex++) {
      levels.add(new NetCdfTiedArgoSyntheticProfileV13Level(this, levelIndex));
    }
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
  public ArgoProfileDataMode getDataMode() {
    return null;
  }

  @Override
  public String getQc() {
    return qc;
  }

  @Override
  public List<ArgoSyntheticProfileV13Level> getLevels() {
    return levels;
  }

//  @Override
//  public List<ArgoProfileV31Calibration> getCalibrations() {
//    List<ArgoProfileV31Calibration> calibrations = new ArrayList<>(numCalibrations);
//    for (int calibrationIndex = 0; calibrationIndex < numCalibrations; calibrationIndex++) {
//      calibrations.add(new NetCdfTiedArgoProfileV31Calibration(this, calibrationIndex));
//    }
//    return calibrations;
//  }
//
//  int getParamIndex() {
//    return paramIndex;
//  }
}
