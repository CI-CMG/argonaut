package edu.colorado.cires.argonaut.standalone;

import tools.jackson.databind.json.JsonMapper;

public class DeserializationProcessor {

  private JsonMapper jsonMapper;

  public void setJsonMapper(JsonMapper jsonMapper) {
    this.jsonMapper = jsonMapper;
  }

  public <T> T deserialize(String message, Class<T> clazz) {
    return jsonMapper.readValue(message, clazz);
  }

}
