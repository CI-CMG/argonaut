package edu.colorado.cires.argonaut.core.netcdf.profile.v31.impl;

import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoProfileV31HistorySoftware;

public class ArgoProfileV31HistorySoftwareBean implements ArgoProfileV31HistorySoftware {

  private final String name;
  private final String release;
  private final String reference;

  public ArgoProfileV31HistorySoftwareBean(String name, String release, String reference) {
    this.name = name;
    this.release = release;
    this.reference = reference;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getRelease() {
    return release;
  }

  @Override
  public String getReference() {
    return reference;
  }
}
