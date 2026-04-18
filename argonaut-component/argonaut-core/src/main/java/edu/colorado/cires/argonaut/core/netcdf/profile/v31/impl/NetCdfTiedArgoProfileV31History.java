package edu.colorado.cires.argonaut.core.netcdf.profile.v31.impl;

import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoProfileV31History;
import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoProfileV31HistorySoftware;
import edu.colorado.cires.argonaut.core.util.NetCdfUtils;
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

  public NetCdfTiedArgoProfileV31History(NetCdfTiedArgoProfileV31 parent, int historyIndex) {
    this.parent = parent;
    netcdf = parent.getNetcdf();
    institution = NetCdfUtils.getLevel2String(netcdf, historyIndex, parent.getProfileIndex(), "HISTORY_INSTITUTION");
    step = NetCdfUtils.getLevel2String(netcdf, historyIndex, parent.getProfileIndex(), "HISTORY_STEP");
    software = new NetCdfTiedArgoProfileV31HistorySoftware(
        NetCdfUtils.getLevel2String(netcdf, historyIndex, parent.getProfileIndex(), "HISTORY_SOFTWARE"),
        NetCdfUtils.getLevel2String(netcdf, historyIndex, parent.getProfileIndex(), "HISTORY_SOFTWARE_RELEASE"),
        NetCdfUtils.getLevel2String(netcdf, historyIndex, parent.getProfileIndex(), "HISTORY_REFERENCE")
    );
    date = NetCdfUtils.getLevel2Instant(netcdf, historyIndex, parent.getProfileIndex(), "HISTORY_DATE");
    action = NetCdfUtils.getLevel2String(netcdf, historyIndex, parent.getProfileIndex(), "HISTORY_ACTION");
    parameter = NetCdfUtils.getLevel2String(netcdf, historyIndex, parent.getProfileIndex(), "HISTORY_PARAMETER");
    startPressure = NetCdfUtils.getLevel2Float(netcdf, historyIndex, parent.getProfileIndex(), "HISTORY_START_PRES");
    stopPressure = NetCdfUtils.getLevel2Float(netcdf, historyIndex, parent.getProfileIndex(), "HISTORY_STOP_PRES");
    previousValue = NetCdfUtils.getLevel2Float(netcdf, historyIndex, parent.getProfileIndex(), "HISTORY_PREVIOUS_VALUE");
    qcTest = NetCdfUtils.getLevel2String(netcdf, historyIndex, parent.getProfileIndex(), "HISTORY_QCTEST");
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
