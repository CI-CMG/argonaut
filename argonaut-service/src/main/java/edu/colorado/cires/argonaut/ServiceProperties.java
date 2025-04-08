package edu.colorado.cires.argonaut;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@ConfigurationProperties(prefix = "argo")
@Validated
public class ServiceProperties {

  @NotBlank
  private String dacDirectory;
  @NotBlank
  private String workDirectory;

  public String getDacDirectory() {
    return dacDirectory;
  }

  public void setDacDirectory(String dacDirectory) {
    this.dacDirectory = dacDirectory;
  }

  public String getWorkDirectory() {
    return workDirectory;
  }

  public void setWorkDirectory(String workDirectory) {
    this.workDirectory = workDirectory;
  }
}
