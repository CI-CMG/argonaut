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
  private String frenchGdacDirectory;
  @NotBlank
  private String aomlDacDirectory;
  @NotBlank
  private String workDirectory;

  public String getFrenchGdacDirectory() {
    return frenchGdacDirectory;
  }

  public void setFrenchGdacDirectory(String frenchGdacDirectory) {
    this.frenchGdacDirectory = frenchGdacDirectory;
  }

  public String getAomlDacDirectory() {
    return aomlDacDirectory;
  }

  public void setAomlDacDirectory(String aomlDacDirectory) {
    this.aomlDacDirectory = aomlDacDirectory;
  }

  public String getWorkDirectory() {
    return workDirectory;
  }

  public void setWorkDirectory(String workDirectory) {
    this.workDirectory = workDirectory;
  }
}
