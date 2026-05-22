package edu.colorado.cires.argonaut.core.netcdf.profile.v31.impl;

import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoProfileV31History;
import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoProfileV31HistorySoftware;
import java.time.Instant;

public class ArgoProfileV31HistoryBean implements ArgoProfileV31History {

  private final ArgoProfileV31Bean parent;
  private final int historyIndex;
  private String institution;
  private String step;
  private ArgoProfileV31HistorySoftwareBean software;
  private Instant date;
  private String action;
  private String parameter;
  private Float startPressure;
  private Float stopPressure;
  private Float previousValue;
  private String qcTest;

  public ArgoProfileV31HistoryBean(ArgoProfileV31Bean parent, int historyIndex) {
    this.parent = parent;
    this.historyIndex = historyIndex;
  }

  public int getHistoryIndex() {
    return historyIndex;
  }

  public int getProfileIndex() {
    return parent.getProfileIndex();
  }

  @Override
  public String getInstitution() {
    return institution;
  }

  public void setInstitution(String institution) {
    this.institution = institution;
  }

  @Override
  public String getStep() {
    return step;
  }

  public void setStep(String step) {
    this.step = step;
  }

  @Override
  public ArgoProfileV31HistorySoftware getSoftware() {
    return software;
  }

  public void setSoftware(ArgoProfileV31HistorySoftwareBean software) {
    this.software = software;
  }

  @Override
  public Instant getDate() {
    return date;
  }

  public void setDate(Instant date) {
    this.date = date;
  }

  @Override
  public String getAction() {
    return action;
  }

  public void setAction(String action) {
    this.action = action;
  }

  @Override
  public String getParameter() {
    return parameter;
  }

  public void setParameter(String parameter) {
    this.parameter = parameter;
  }

  @Override
  public Float getStartPressure() {
    return startPressure;
  }

  public void setStartPressure(Float startPressure) {
    this.startPressure = startPressure;
  }

  @Override
  public Float getStopPressure() {
    return stopPressure;
  }

  public void setStopPressure(Float stopPressure) {
    this.stopPressure = stopPressure;
  }

  @Override
  public Float getPreviousValue() {
    return previousValue;
  }

  public void setPreviousValue(Float previousValue) {
    this.previousValue = previousValue;
  }

  @Override
  public String getQcTest() {
    return qcTest;
  }

  public void setQcTest(String qcTest) {
    this.qcTest = qcTest;
  }
}
