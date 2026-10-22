package edu.colorado.cires.argonaut.messaging.cameljpa;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class ExceptionThrower implements Processor {

  private int count = 0;


  @Override
  public void process(Exchange exchange) throws Exception {
    synchronized (this) {
      count++;
      if (count == 1) {
        throw new RuntimeException("Camels are stupid!");
      }
    }
  }
}
