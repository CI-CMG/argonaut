package edu.colorado.cires.argonaut.processor.geofilter;

public enum MarineRegionsOcean {
  SOUTHERN_OCEAN("Southern Ocean"),
  SOUTH_ATLANTIC_OCEAN("South Atlantic Ocean"),
  SOUTH_PACIFIC_OCEAN("South Pacific Ocean"),
  NORTH_PACIFIC_OCEAN("North Pacific Ocean"),
  SOUTH_CHINA_AND_EASTER_ARCHIPELAGIC_SEAS("South China and Easter Archipelagic Seas"),
  INDIAN_OCEAN("Indian Ocean"),
  MEDITERRANEAN_REGION("Mediterranean Region"),
  BALTIC_SEA("Baltic Sea"),
  NORTH_ATLANTIC_OCEAN("North Atlantic Ocean"),
  ARCTIC_OCEAN("Arctic Ocean");

  public static MarineRegionsOcean getMarineRegionsOcean(String regionName) {
    for (MarineRegionsOcean marineRegionsOcean : MarineRegionsOcean.values()) {
      if (marineRegionsOcean.getName().equals(regionName)) {
        return marineRegionsOcean;
      }
    }
    return null;
  }

  private final String name;
  MarineRegionsOcean(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
