package edu.colorado.cires.argonaut.messaging.core.databind;

public enum ProfileMode {
  REAL_TIME("R", "R"),
  DELAYED_MODE("D", "D"),
  REAL_TIME_ADJUSTED("R", "A");

  public static ProfileMode fromCharacter(String c) {
    switch (c) {
      case "D":
        return ProfileMode.DELAYED_MODE;
      case "R":
        return ProfileMode.REAL_TIME;
      case "A":
        return ProfileMode.REAL_TIME_ADJUSTED;
      default:
        throw new IllegalArgumentException("Invalid character: " + c);
    }
  }

  private final String filePrefix;
  private final String character;

  ProfileMode(String filePrefix, String character) {
    this.filePrefix = filePrefix;
    this.character = character;
  }

  public String getFilePrefix() {
    return filePrefix;
  }

  public String getCharacter() {
    return character;
  }
}
