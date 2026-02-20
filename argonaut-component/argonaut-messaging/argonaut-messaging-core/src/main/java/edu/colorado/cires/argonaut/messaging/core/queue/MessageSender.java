package edu.colorado.cires.argonaut.messaging.core.queue;

public interface MessageSender {

  void sendJson(String queue, String json);
}
