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
  private List<@Valid DacConfig> dacs = new ArrayList<>();
  @NotBlank
  private String indexCron;
  @NotBlank
  private String gdacSyncCron;
  @NotNull
  private Duration floatMergeQuietTimeout;

  @NotNull
  @Valid
  private FtpServerConfig localSubmissionFtpServer;

  @NotNull
  @Valid
  private FtpServerConfig localOutputFtpServer;


  public FtpServerConfig getLocalOutputFtpServer() {
    return localOutputFtpServer;
  }

  public void setLocalOutputFtpServer(FtpServerConfig localOutputFtpServer) {
    this.localOutputFtpServer = localOutputFtpServer;
  }

  public FtpServerConfig getLocalSubmissionFtpServer() {
    return localSubmissionFtpServer;
  }

  public void setLocalSubmissionFtpServer(FtpServerConfig localSubmissionFtpServer) {
    this.localSubmissionFtpServer = localSubmissionFtpServer;
  }

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

  public enum PasswordEncoding {
    none, sha1, sha256, sha512, md5
  }

  public static class FtpServerConfig {
    private boolean enabled = false;
    private boolean sslEnabled = true;
    private boolean anonymousLoginEnabled = false;
    private Path userFile;
    @NotNull
    private PasswordEncoding passwordEncoding;
    private Path keystoreFile;
    private String keystorePassword;
    @Min(1)
    private int port;

    public boolean isAnonymousLoginEnabled() {
      return anonymousLoginEnabled;
    }

    public void setAnonymousLoginEnabled(boolean anonymousLoginEnabled) {
      this.anonymousLoginEnabled = anonymousLoginEnabled;
    }

    public boolean isSslEnabled() {
      return sslEnabled;
    }

    public void setSslEnabled(boolean sslEnabled) {
      this.sslEnabled = sslEnabled;
    }

    public int getPort() {
      return port;
    }

    public void setPort(int port) {
      this.port = port;
    }

    public Path getKeystoreFile() {
      return keystoreFile;
    }

    public void setKeystoreFile(Path keystoreFile) {
      this.keystoreFile = keystoreFile;
    }

    public String getKeystorePassword() {
      return keystorePassword;
    }

    public void setKeystorePassword(String keystorePassword) {
      this.keystorePassword = keystorePassword;
    }

    public boolean isEnabled() {
      return enabled;
    }

    public void setEnabled(boolean enabled) {
      this.enabled = enabled;
    }

    public Path getUserFile() {
      return userFile;
    }

    public void setUserFile(Path userFile) {
      this.userFile = userFile;
    }

    public PasswordEncoding getPasswordEncoding() {
      return passwordEncoding;
    }

    public void setPasswordEncoding(PasswordEncoding passwordEncoding) {
      this.passwordEncoding = passwordEncoding;
    }
  }
}
