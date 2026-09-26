package edu.colorado.cires.argonaut.standalone;

import org.apache.camel.spring.Main;

public class Application {

  public static void main(String[] args) throws Exception {
    System.setProperty("camel.threads.virtual.enabled", "true");
    Main main = new Main();
    main.setApplicationContextUri("application-context.xml");
    main.run(args);
  }

}
