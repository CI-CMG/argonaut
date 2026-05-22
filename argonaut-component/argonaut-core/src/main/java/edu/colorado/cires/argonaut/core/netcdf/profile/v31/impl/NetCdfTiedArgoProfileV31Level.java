package edu.colorado.cires.argonaut.core.netcdf.profile.v31.impl;

import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoProfileV31Level;
import edu.colorado.cires.argonaut.core.util.NetCdfReadUtils;
import ucar.nc2.NetcdfFile;

public class NetCdfTiedArgoProfileV31Level implements ArgoProfileV31Level {

  private final NetCdfTiedArgoProfileV31Parameter parent;
  private final int levelIndex;
  private final String parameterName;
  private final Float value;
  private final NetcdfFile netcdf;
  private final Float adjustedValue;
  private final String adjustedQc;
  private final Float adjustedErrorValue;
  private final String qc;

  public NetCdfTiedArgoProfileV31Level(NetCdfTiedArgoProfileV31Parameter parent, int levelIndex) {
    this.parent = parent;
    this.levelIndex = levelIndex;
    this.parameterName = parent.getParameterName();
    netcdf = parent.getNetcdf();
    value = NetCdfReadUtils.getLevel2Float(netcdf, parent.getProfileIndex(), levelIndex, parameterName);
    adjustedValue = NetCdfReadUtils.getLevel2Float(netcdf, parent.getProfileIndex(), levelIndex, parameterName + "_ADJUSTED");
    adjustedQc = NetCdfReadUtils.getLevel2String(netcdf, parent.getProfileIndex(), levelIndex, parameterName + "_ADJUSTED_QC");
    adjustedErrorValue = NetCdfReadUtils.getLevel2Float(netcdf, parent.getProfileIndex(), levelIndex, parameterName + "_ADJUSTED_ERROR");
    qc = NetCdfReadUtils.getLevel2String(netcdf, parent.getProfileIndex(), levelIndex, parameterName + "_QC");
  }


  @Override
  public int getProfileIndex() {
    return parent.getProfileIndex();
  }

  @Override
  public int getLevelIndex() {
    return levelIndex;
  }

  @Override
  public String getParameterName() {
    return parameterName;
  }

  @Override
  public Float getValue() {
    return value;
  }

  @Override
  public Float getAdjustedValue() {
    return adjustedValue;
  }

  @Override
  public String getAdjustedQc() {
    return adjustedQc;
  }

  @Override
  public Float getAdjustedErrorValue() {
    return adjustedErrorValue;
  }

  @Override
  public String getQc() {
    return qc;
  }
}
