package edu.colorado.cires.argonaut.messaging.core.databind;

public enum ArgoOcean {
  ATLANTIC_OCEAN("A", "atlantic_ocean"),
  INDIAN_OCEAN("I", "indian_ocean"),
  PACIFIC_OCEAN("P", "pacific_ocean"),
  UNKNOWN("U", null);

  private final String code;
  private final String directory;

  ArgoOcean(String code, String directory) {
    this.code = code;
    this.directory = directory;
  }

  public String getCode() {
    return code;
  }

  public String getDirectory() {
    return directory;
  }

  public static ArgoOcean fromCode(String code) {
    for (ArgoOcean argoOcean : ArgoOcean.values()) {
      if (argoOcean.getCode().equals(code)) {
        return argoOcean;
      }
    }
    throw new IllegalArgumentException(String.format("ArgoOcean '%s' does not exist", code));
  }
}
