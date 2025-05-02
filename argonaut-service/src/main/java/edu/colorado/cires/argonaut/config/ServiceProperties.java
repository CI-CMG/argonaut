package edu.colorado.cires.argonaut.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@ConfigurationProperties(prefix = "argonaut")
@Validated
public class ServiceProperties {


  @NotNull
  private Path workDirectory;
  @NotNull
  private Path submissionDirectory;
  @NotNull
  private Path outputDirectory;
  @NotNull
  private Duration fileCheckerTimeout;

  @Min(1)
  private int validationThreads;
  @Min(1)
  private int submissionReportThreads;
  @NotBlank
  private String fileCheckerHeap;


  @NotNull
  private List<@Valid DacConfig> dacs;
  @NotBlank
  private String indexCron;
  @NotBlank
  private String gdacSyncCron;
  @NotNull
  private Duration floatMergeQuietTimeout;

  public int getSubmissionReportThreads() {
    return submissionReportThreads;
  }

  public void setSubmissionReportThreads(int submissionReportThreads) {
    this.submissionReportThreads = submissionReportThreads;
  }

  public String getFileCheckerHeap() {
    return fileCheckerHeap;
  }

  public void setFileCheckerHeap(String fileCheckerHeap) {
    this.fileCheckerHeap = fileCheckerHeap;
  }

  public int getValidationThreads() {
    return validationThreads;
  }

  public void setValidationThreads(int validationThreads) {
    this.validationThreads = validationThreads;
  }

  public Path getSubmissionDirectory() {
    return submissionDirectory;
  }

  public void setSubmissionDirectory(Path submissionDirectory) {
    this.submissionDirectory = submissionDirectory.toAbsolutePath().normalize();
  }


  public Path getOutputDirectory() {
    return outputDirectory;
  }

  public void setOutputDirectory(Path outputDirectory) {
    this.outputDirectory = outputDirectory.toAbsolutePath().normalize();
  }

  public String getGdacSyncCron() {
    return gdacSyncCron;
  }

  public void setGdacSyncCron(String gdacSyncCron) {
    this.gdacSyncCron = gdacSyncCron;
  }

  public String getIndexCron() {
    return indexCron;
  }

  public void setIndexCron(String indexCron) {
    this.indexCron = indexCron;
  }

  public Path getWorkDirectory() {
    return workDirectory;
  }

  public void setWorkDirectory(Path workDirectory) {
    this.workDirectory = workDirectory.toAbsolutePath().normalize();
  }

  public List<DacConfig> getDacs() {
    return dacs;
  }

  public void setDacs(List<DacConfig> dacs) {
    this.dacs = dacs;
  }

  public Duration getFloatMergeQuietTimeout() {
    return floatMergeQuietTimeout;
  }

  public void setFloatMergeQuietTimeout(Duration floatMergeQuietTimeout) {
    this.floatMergeQuietTimeout = floatMergeQuietTimeout;
  }

  public Duration getFileCheckerTimeout() {
    return fileCheckerTimeout;
  }

  public void setFileCheckerTimeout(Duration fileCheckerTimeout) {
    this.fileCheckerTimeout = fileCheckerTimeout;
  }

  public static class DacConfig {
    @NotBlank
    private String name;
    @NotNull
    private List<@NotBlank String> email = new ArrayList<>();

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public List<String> getEmail() {
      return email;
    }

    public void setEmail(List<String> email) {
      this.email = email;
    }
  }
}
