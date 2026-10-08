package edu.colorado.cires.argonaut.messaging.cameljpa;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class CamelJpaParameterFactory {

  public static Map<String, String> queueParameter(String queue) {
    Map<String, String> map = new HashMap<>();
    map.put("queue", queue);
    return Collections.unmodifiableMap(map);
  }
}
