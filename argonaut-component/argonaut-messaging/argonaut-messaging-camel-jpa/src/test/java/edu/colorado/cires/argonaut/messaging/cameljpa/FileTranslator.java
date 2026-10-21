package edu.colorado.cires.argonaut.messaging.cameljpa;

import java.io.File;

public class FileTranslator {

  public String createMessage(File file) {
    System.out.println(file.getName());
    return file.getName();
  }

}
