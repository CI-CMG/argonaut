package edu.colorado.cires.argonaut.core.netcdf.profile.v31.impl;

import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoProfileV31History;
import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoProfileV31HistorySoftware;
import edu.colorado.cires.argonaut.core.util.NetCdfReadUtils;
import java.time.Instant;
import ucar.nc2.NetcdfFile;

public class NetCdfTiedArgoProfileV31History implements ArgoProfileV31History {

  private final NetCdfTiedArgoProfileV31 parent;
  private final NetcdfFile netcdf;
  private final String institution;
  private final String step;
  private final NetCdfTiedArgoProfileV31HistorySoftware software;
  private final Instant date;
  private final String action;
  private final String parameter;
  private final Float startPressure;
  private final Float stopPressure;
  private final Float previousValue;
  private final String qcTest;
  private final int historyIndex;

  public NetCdfTiedArgoProfileV31History(NetCdfTiedArgoProfileV31 parent, int historyIndex) {
    this.parent = parent;
    netcdf = parent.getNetcdf();
    this.historyIndex = historyIndex;
    institution = NetCdfReadUtils.getLevel2String(netcdf, historyIndex, parent.getProfileIndex(), "HISTORY_INSTITUTION");
    step = NetCdfReadUtils.getLevel2String(netcdf, historyIndex, parent.getProfileIndex(), "HISTORY_STEP");
    software = new NetCdfTiedArgoProfileV31HistorySoftware(
        NetCdfReadUtils.getLevel2String(netcdf, historyIndex, parent.getProfileIndex(), "HISTORY_SOFTWARE"),
        NetCdfReadUtils.getLevel2String(netcdf, historyIndex, parent.getProfileIndex(), "HISTORY_SOFTWARE_RELEASE"),
        NetCdfReadUtils.getLevel2String(netcdf, historyIndex, parent.getProfileIndex(), "HISTORY_REFERENCE")
    );
    date = NetCdfReadUtils.getLevel2Instant(netcdf, historyIndex, parent.getProfileIndex(), "HISTORY_DATE");
    action = NetCdfReadUtils.getLevel2String(netcdf, historyIndex, parent.getProfileIndex(), "HISTORY_ACTION");
    parameter = NetCdfReadUtils.getLevel2String(netcdf, historyIndex, parent.getProfileIndex(), "HISTORY_PARAMETER");
    startPressure = NetCdfReadUtils.getLevel2Float(netcdf, historyIndex, parent.getProfileIndex(), "HISTORY_START_PRES");
    stopPressure = NetCdfReadUtils.getLevel2Float(netcdf, historyIndex, parent.getProfileIndex(), "HISTORY_STOP_PRES");
    previousValue = NetCdfReadUtils.getLevel2Float(netcdf, historyIndex, parent.getProfileIndex(), "HISTORY_PREVIOUS_VALUE");
    qcTest = NetCdfReadUtils.getLevel2String(netcdf, historyIndex, parent.getProfileIndex(), "HISTORY_QCTEST");
  }

  @Override
  public int getHistoryIndex() {
    return historyIndex;
  }

  @Override
  public String getInstitution() {
    return institution;
  }

  @Override
  public String getStep() {
    return step;
  }

  @Override
  public ArgoProfileV31HistorySoftware getSoftware() {
    return software;
  }

  @Override
  public Instant getDate() {
    return date;
  }

  @Override
  public String getAction() {
    return action;
  }

  @Override
  public String getParameter() {
    return parameter;
  }

  @Override
  public Float getStartPressure() {
    return startPressure;
  }

  @Override
  public Float getStopPressure() {
    return stopPressure;
  }

  @Override
  public Float getPreviousValue() {
    return previousValue;
  }

  @Override
  public String getQcTest() {
    return qcTest;
  }
}
