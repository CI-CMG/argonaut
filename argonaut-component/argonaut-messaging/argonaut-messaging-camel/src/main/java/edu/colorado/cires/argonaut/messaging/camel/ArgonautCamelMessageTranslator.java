package edu.colorado.cires.argonaut.messaging.camel;

public interface ArgonautCamelMessageTranslator {

  Object translate(String queue, String json);

}
