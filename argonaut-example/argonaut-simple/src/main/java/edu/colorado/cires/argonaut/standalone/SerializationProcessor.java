package edu.colorado.cires.argonaut.standalone;

import tools.jackson.databind.json.JsonMapper;

public class SerializationProcessor {

  private JsonMapper jsonMapper;

  public void setJsonMapper(JsonMapper jsonMapper) {
    this.jsonMapper = jsonMapper;
  }

  public String serialize(Object message) {
    return jsonMapper.writeValueAsString(message);
  }

}
