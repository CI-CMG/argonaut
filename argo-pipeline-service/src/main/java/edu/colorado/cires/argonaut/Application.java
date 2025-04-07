package edu.colorado.cires.argonaut;

import java.io.File;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.system.ApplicationHome;

@SpringBootApplication
public class Application {
  public static void main(String[] args){
    File svcHome = new ApplicationHome().getDir();
    String path = svcHome.getAbsolutePath();
    System.setProperty("svc.home", path);
    SpringApplication.run(Application.class, args);
  }
}
