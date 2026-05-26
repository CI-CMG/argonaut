package edu.colorado.cires.argonaut.core.util;

import edu.colorado.cires.argonaut.core.merge.synthetic.DefaultSyntheticProfileMerger;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class SoftwareVersion {

  private static final String version;

  static {
    Properties properties = new Properties();
    try (InputStream in = DefaultSyntheticProfileMerger.class.getClassLoader().getResourceAsStream("edu/colorado/cires/argonaut/core/build.properties")) {
      properties.load(in);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    version = properties.getProperty("version");
  }

  public static String getVersion() {
    return version;
  }

}
