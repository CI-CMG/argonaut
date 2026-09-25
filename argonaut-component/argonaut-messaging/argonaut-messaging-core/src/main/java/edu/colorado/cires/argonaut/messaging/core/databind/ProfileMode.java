package edu.colorado.cires.argonaut.messaging.core.databind;

public enum ProfileMode {
  REAL_TIME("R"),
  DELAYED_MODE("D"),
  REAL_TIME_ADJUSTED("A");

  public static ProfileMode fromPrefix(String prefix) {
    switch (prefix) {
      case "D":
        return ProfileMode.DELAYED_MODE;
      case "R":
        return ProfileMode.REAL_TIME;
      case "A":
        return ProfileMode.REAL_TIME_ADJUSTED;
      default:
        throw new IllegalArgumentException("Invalid prefix: " + prefix);
    }
  }

  private final String prefix;

  ProfileMode(String prefix) {
    this.prefix = prefix;
  }

  public String getPrefix() {
    return prefix;
  }
}
