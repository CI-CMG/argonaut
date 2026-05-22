package edu.colorado.cires.argonaut.core.netcdf.profile.v31.impl;

import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoProfileV31Calibration;
import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoProfileV31Level;
import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoProfileV31Parameter;
import java.util.ArrayList;
import java.util.List;

public class ArgoProfileV31ParameterBean implements ArgoProfileV31Parameter {

  private final ArgoProfileV31Bean parent;
  private final String parameterName;
  private String qc;
  private final int parameterIndex;
  private List<ArgoProfileV31Level> levels = new ArrayList<>();
  private List<ArgoProfileV31Calibration> calibrations = new ArrayList<>();
  private String dataMode;


  public ArgoProfileV31ParameterBean(ArgoProfileV31Bean parent, String parameterName, int parameterIndex) {
    this.parent = parent;
    this.parameterName = parameterName;
    this.parameterIndex = parameterIndex;
  }

  @Override
  public int getParameterIndex() {
    return parameterIndex;
  }

  @Override
  public String getParameterName() {
    return parameterName;
  }

  @Override
  public String getQc() {
    return qc;
  }

  public void setQc(String qc) {
    this.qc = qc;
  }

  @Override
  public List<ArgoProfileV31Level> getLevels() {
    return levels;
  }

  public void setLevels(List<ArgoProfileV31LevelBean> levels) {
    if (this.levels == null) {
      this.levels = new ArrayList<>();
    } else
      this.levels.addAll(levels);
  }

  @Override
  public String getDataMode() {
    return dataMode;
  }

  public void setDataMode(String dataMode) {
    this.dataMode = dataMode;
  }

  @Override
  public List<ArgoProfileV31Calibration> getCalibrations() {
    return calibrations;
  }

  public void setCalibrations(List<ArgoProfileV31CalibrationBean> calibrations) {
    if (calibrations == null) {
      this.calibrations = new ArrayList<>();
    } else {
      this.calibrations = new ArrayList<>(calibrations);
    }
  }

  public int getProfileIndex() {
    return parent.getProfileIndex();
  }
}
