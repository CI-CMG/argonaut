package edu.colorado.cires.argonaut.core.netcdf.synthprofile.v13.impl;

import edu.colorado.cires.argonaut.core.netcdf.synthprofile.v13.ArgoSyntheticProfileV13Calibration;
import edu.colorado.cires.argonaut.core.netcdf.synthprofile.v13.ArgoSyntheticProfileV13Level;
import edu.colorado.cires.argonaut.core.netcdf.synthprofile.v13.ArgoSyntheticProfileV13Parameter;
import java.util.ArrayList;
import java.util.List;

public class ArgoSyntheticProfileV13ParameterBean implements ArgoSyntheticProfileV13Parameter {

  private String dataMode;
  private String parameterName;
  private String qc;
  private List<ArgoSyntheticProfileV13Level> levels = new ArrayList<>();
  private List<ArgoSyntheticProfileV13Calibration> calibrations = new ArrayList<>();


  @Override
  public String getParameterName() {
    return parameterName;
  }

  public void setParameterName(String parameterName) {
    this.parameterName = parameterName;
  }

  @Override
  public String getDataMode() {
    return dataMode;
  }

  public void setDataMode(String dataMode) {
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

  @Override
  public List<ArgoSyntheticProfileV13Calibration> getCalibrations() {
    return calibrations;
  }

  public void setCalibrations(List<ArgoSyntheticProfileV13Calibration> calibrations) {
    this.calibrations = calibrations;
  }
}
