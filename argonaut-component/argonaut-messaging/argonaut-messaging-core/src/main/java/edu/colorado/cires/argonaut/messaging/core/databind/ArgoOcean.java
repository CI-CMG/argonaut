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

  public static ArgoOcean fromCode(String code) {
    for (ArgoOcean argoOcean : ArgoOcean.values()) {
      if (argoOcean.getCode().equals(code)) {
        return argoOcean;
      }
    }
    throw new IllegalArgumentException(String.format("ArgoOcean '%s' does not exist", code));
  }
}
