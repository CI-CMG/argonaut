package edu.colorado.cires.argonaut.messaging.cameljpa;

import java.util.Arrays;

public class ExampleSplitter {

  private CamelJpaMessageSender sender;

  public void setSender(CamelJpaMessageSender sender) {
    this.sender = sender;
  }

  public void send(String fileName) {
    Arrays.stream(fileName.split("\\.")).forEach(s -> {
      System.out.println(s);
      sender.sendJson("queue2", s);
    });
  }
}
