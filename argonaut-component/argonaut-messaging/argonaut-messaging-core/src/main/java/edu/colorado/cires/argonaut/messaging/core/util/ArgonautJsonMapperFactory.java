package edu.colorado.cires.argonaut.messaging.core.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.json.JsonMapper;

public class ArgonautJsonMapperFactory {

  private static final JsonMapper JSON_MAPPER = JsonMapper.builder()
      .disable(MapperFeature.DEFAULT_VIEW_INCLUSION)
      .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
      .changeDefaultPropertyInclusion(include -> include.withValueInclusion(JsonInclude.Include.NON_NULL))
      .build();

  public static JsonMapper getJsonMapper() {
    return JSON_MAPPER;
  }

}
