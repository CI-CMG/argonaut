package edu.colorado.cires.argonaut.messaging.core.databind;

public enum ProfileMode {
  REAL_TIME("R"),
  DELAYED_MODE("D");

  private final String prefix;

  ProfileMode(String prefix) {
    this.prefix = prefix;
  }

  public String getPrefix() {
    return prefix;
  }
}
