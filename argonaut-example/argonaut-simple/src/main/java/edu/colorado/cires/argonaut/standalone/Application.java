package edu.colorado.cires.argonaut.standalone;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.spring.Main;

public class Application implements Processor {

  public static void main(String[] args) throws Exception {
    System.out.println("STARTING EMAILDBLISTENER");
    Main main = new Main();
    main.setApplicationContextUri("application-context.xml");
    main.run(args);
  }

  @Override
  public void process(Exchange exchange) throws Exception {
    System.out.println(exchange.getIn().getBody());
  }
}
