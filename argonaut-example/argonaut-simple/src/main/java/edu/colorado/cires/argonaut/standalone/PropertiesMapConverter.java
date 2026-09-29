package edu.colorado.cires.argonaut.standalone;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public class PropertiesMapConverter {


  public static Map<String, String> fromPropertyValue(String propertyValue) {
    if (propertyValue == null || propertyValue.isEmpty()) {
      return Collections.emptyMap();
    }
    Map<String, String> map = new TreeMap<>();
    String[] parts = propertyValue.split(",");
    for (String part : parts) {
      String[] kv = part.split("\\|");
      String key = kv[0].trim();
      String value = kv[1].trim();
      map.put(key, value);
    }
    return map;
  }


}
