package edu.colorado.cires.argonaut.core.netcdf.v31.impl;

import edu.colorado.cires.argonaut.core.netcdf.v31.ArgoProfileV31History;
import edu.colorado.cires.argonaut.core.netcdf.v31.ArgoProfileV31HistorySoftware;
import java.io.IOException;
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
    try {
      institution = NetCdfUtils.getProfileHistoryString(netcdf, parent.getProfileIndex(), historyIndex, "HISTORY_INSTITUTION");
      step = NetCdfUtils.getProfileHistoryString(netcdf, parent.getProfileIndex(), historyIndex, "HISTORY_STEP");
      software = new NetCdfTiedArgoProfileV31HistorySoftware(
          NetCdfUtils.getProfileHistoryString(netcdf, parent.getProfileIndex(), historyIndex, "HISTORY_SOFTWARE"),
          NetCdfUtils.getProfileHistoryString(netcdf, parent.getProfileIndex(), historyIndex, "HISTORY_SOFTWARE_RELEASE"),
          NetCdfUtils.getProfileHistoryString(netcdf, parent.getProfileIndex(), historyIndex, "HISTORY_REFERENCE")
      );
      date = NetCdfUtils.getProfileHistoryInstant(netcdf, parent.getProfileIndex(), historyIndex, "HISTORY_DATE");
      action = NetCdfUtils.getProfileHistoryString(netcdf, parent.getProfileIndex(), historyIndex, "HISTORY_ACTION");
      parameter = NetCdfUtils.getProfileHistoryString(netcdf, parent.getProfileIndex(), historyIndex, "HISTORY_PARAMETER");
      startPressure = NetCdfUtils.getProfileHistoryFloat(netcdf, parent.getProfileIndex(), historyIndex, "HISTORY_START_PRES");
      stopPressure = NetCdfUtils.getProfileHistoryFloat(netcdf, parent.getProfileIndex(), historyIndex, "HISTORY_STOP_PRES");
      previousValue = NetCdfUtils.getProfileHistoryFloat(netcdf, parent.getProfileIndex(), historyIndex, "HISTORY_PREVIOUS_VALUE");
      qcTest = NetCdfUtils.getProfileHistoryString(netcdf, parent.getProfileIndex(), historyIndex, "HISTORY_QCTEST");
    } catch (IOException e) {
      throw new RuntimeException("Unable to initialize profile history data wrapper", e);
    }
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
