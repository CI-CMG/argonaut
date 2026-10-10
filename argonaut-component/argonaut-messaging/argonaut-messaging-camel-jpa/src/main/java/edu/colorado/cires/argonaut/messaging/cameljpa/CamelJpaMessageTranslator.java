package edu.colorado.cires.argonaut.messaging.cameljpa;

import edu.colorado.cires.argonaut.messaging.camel.ArgonautCamelMessageTranslator;

public interface CamelJpaMessageTranslator extends ArgonautCamelMessageTranslator {

  default Object send(String queue, String json){
    return translate(queue, json);
  }
  String receive(ArgonautMessageEntity entity);
}
