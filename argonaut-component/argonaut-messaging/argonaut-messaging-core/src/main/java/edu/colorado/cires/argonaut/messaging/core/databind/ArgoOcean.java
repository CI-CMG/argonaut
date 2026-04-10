package edu.colorado.cires.argonaut.messaging.core.databind;

public enum ArgoOcean {
  ATLANTIC_OCEAN("A"),
  INDIAN_OCEAN("I"),
  PACIFIC_OCEAN("P"),
  UNKNOWN("U");

  private final String code;

  ArgoOcean(String code) {
    this.code = code;
  }

  public String getCode() {
    return code;
  }
}
