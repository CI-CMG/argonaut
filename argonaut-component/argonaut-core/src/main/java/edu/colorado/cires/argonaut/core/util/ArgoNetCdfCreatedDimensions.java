package edu.colorado.cires.argonaut.core.util;

import ucar.nc2.Dimension;

public class ArgoNetCdfCreatedDimensions {

  private final Dimension dateTimeDim;
  private final Dimension string256Dim;
  private final Dimension string64Dim;
  private final Dimension string32Dim;
  private final Dimension string16Dim;
  private final Dimension string8Dim;
  private final Dimension string4Dim;
  private final Dimension string2Dim;
  private final Dimension nProfDim;
  private final Dimension nParamDim;
  private final Dimension nLevelsDim;
  private final Dimension nCalibDim;
  private final Dimension nHistoryDim;

  ArgoNetCdfCreatedDimensions(Dimension dateTimeDim, Dimension string256Dim, Dimension string64Dim, Dimension string32Dim, Dimension string16Dim, Dimension string8Dim,
      Dimension string4Dim, Dimension string2Dim, Dimension nProfDim, Dimension nParamDim, Dimension nLevelsDim, Dimension nCalibDim,
      Dimension nHistoryDim) {
    this.dateTimeDim = dateTimeDim;
    this.string256Dim = string256Dim;
    this.string64Dim = string64Dim;
    this.string32Dim = string32Dim;
    this.string16Dim = string16Dim;
    this.string8Dim = string8Dim;
    this.string4Dim = string4Dim;
    this.string2Dim = string2Dim;
    this.nProfDim = nProfDim;
    this.nParamDim = nParamDim;
    this.nLevelsDim = nLevelsDim;
    this.nCalibDim = nCalibDim;
    this.nHistoryDim = nHistoryDim;
  }

  public Dimension getDateTimeDim() {
    return dateTimeDim;
  }

  public Dimension getString256Dim() {
    return string256Dim;
  }

  public Dimension getString64Dim() {
    return string64Dim;
  }

  public Dimension getString32Dim() {
    return string32Dim;
  }

  public Dimension getString16Dim() {
    return string16Dim;
  }

  public Dimension getString8Dim() {
    return string8Dim;
  }

  public Dimension getString4Dim() {
    return string4Dim;
  }

  public Dimension getString2Dim() {
    return string2Dim;
  }

  public Dimension getnProfDim() {
    return nProfDim;
  }

  public Dimension getnParamDim() {
    return nParamDim;
  }

  public Dimension getnLevelsDim() {
    return nLevelsDim;
  }

  public Dimension getnCalibDim() {
    return nCalibDim;
  }

  public Dimension getnHistoryDim() {
    return nHistoryDim;
  }
}
