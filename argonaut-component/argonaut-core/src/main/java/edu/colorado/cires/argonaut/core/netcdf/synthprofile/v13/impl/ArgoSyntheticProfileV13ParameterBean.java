package edu.colorado.cires.argonaut.core.netcdf.synthprofile.v13.impl;

import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoProfileDataMode;
import edu.colorado.cires.argonaut.core.netcdf.synthprofile.v13.ArgoSyntheticProfileV13Level;
import edu.colorado.cires.argonaut.core.netcdf.synthprofile.v13.ArgoSyntheticProfileV13Parameter;
import java.util.List;

public class ArgoSyntheticProfileV13ParameterBean implements ArgoSyntheticProfileV13Parameter {

  private ArgoProfileDataMode dataMode;
  private String parameterName;
  private String qc;
  private List<ArgoSyntheticProfileV13Level> levels;


  @Override
  public String getParameterName() {
    return parameterName;
  }

  public void setParameterName(String parameterName) {
    this.parameterName = parameterName;
  }

  @Override
  public ArgoProfileDataMode getDataMode() {
    return dataMode;
  }

  public void setDataMode(ArgoProfileDataMode dataMode) {
    this.dataMode = dataMode;
  }

  @Override
  public String getQc() {
    return qc;
  }

  public void setQc(String qc) {
    this.qc = qc;
  }

  @Override
  public List<ArgoSyntheticProfileV13Level> getLevels() {
    return levels;
  }

  public void setLevels(List<ArgoSyntheticProfileV13Level> levels) {
    this.levels = levels;
  }
}
