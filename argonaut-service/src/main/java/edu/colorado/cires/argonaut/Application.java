package edu.colorado.cires.argonaut;

import java.io.File;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.system.ApplicationHome;

@SpringBootApplication
public class Application {

  static {
    System.setProperty("camel.threads.virtual.enabled", "true");
    File svcHome = new ApplicationHome().getDir();
    String path = svcHome.getAbsolutePath();
    System.setProperty("svc.home", path);
  }

  public static void main(String[] args){
    SpringApplication.run(Application.class, args);
  }
}
