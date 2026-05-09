package edu.colorado.cires.argonaut.processor.core;

import edu.colorado.cires.argonaut.file.core.FileStore;
import edu.colorado.cires.argonaut.messaging.core.databind.NcSubmissionMessage;
import edu.colorado.cires.argonaut.messaging.core.databind.NcSubmissionMessage.FileType;
import edu.colorado.cires.argonaut.xml.filecheck.FileCheckResults;
import fr.coriolis.checker.core.ApplicationProperties;
import fr.coriolis.checker.core.ArgoFileCheckExecutor;
import fr.coriolis.checker.core.ArgoFileCheckExecutorContext;
import fr.coriolis.checker.core.ClasspathApplicationProperties;
import fr.coriolis.checker.core.ClasspathVersionInfoProperties;
import fr.coriolis.checker.core.VersionInfoProperties;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import org.apache.commons.io.FileUtils;

public class DefaultValidationProcessor implements ValidationProcessor {

  private Path workingDirectory;
  private FileStore processingFileStore;

  private boolean useOnlineNvs = false;
  private boolean doNulls = false;
  private boolean doFormatOnly = false;
  private boolean doNameCheck = true;
  private boolean doPsalStats = false;
  private boolean doFormatOnlyPre31 = true;

  public void setWorkingDirectory(Path workingDirectory) {
    this.workingDirectory = workingDirectory;
  }

  public void setProcessingFileStore(FileStore processingFileStore) {
    this.processingFileStore = processingFileStore;
  }

  // optional setters

  public void setUseOnlineNvs(boolean useOnlineNvs) {
    this.useOnlineNvs = useOnlineNvs;
  }

  public void setDoNulls(boolean doNulls) {
    this.doNulls = doNulls;
  }

  public void setDoFormatOnly(boolean doFormatOnly) {
    this.doFormatOnly = doFormatOnly;
  }

  public void setDoNameCheck(boolean doNameCheck) {
    this.doNameCheck = doNameCheck;
  }

  public void setDoPsalStats(boolean doPsalStats) {
    this.doPsalStats = doPsalStats;
  }

  public void setDoFormatOnlyPre31(boolean doFormatOnlyPre31) {
    this.doFormatOnlyPre31 = doFormatOnlyPre31;
  }

  // end optional setters


  @Override
  public NcSubmissionMessage validate(NcSubmissionMessage ncSubmissionMessage) {
    Path tempDir;
    try {
      Files.createDirectories(workingDirectory);
      tempDir = Files.createTempDirectory(workingDirectory, "argo-file-check-");
    } catch (IOException e) {
      throw new RuntimeException("Unable to create working directory for file check", e);
    }
    try {
      String downloadPath = resolveDownloadPath(ncSubmissionMessage);
      Path ncFile = tempDir.resolve(ncSubmissionMessage.getFileName());
      download(downloadPath, ncFile);
      new ArgoFileCheckExecutor(createContext(ncSubmissionMessage, tempDir))
          .validateFiles(Collections.singletonList(ncSubmissionMessage.getFileName()));
      List<String> errors = validateXml(tempDir.resolve(ncSubmissionMessage.getFileName() + ".filecheck"));
      return NcSubmissionMessage.builder(ncSubmissionMessage).withValidationErrors(errors).build();
    } finally {
      FileUtils.deleteQuietly(tempDir.toFile());
    }
  }

  private List<String> validateXml(Path xml) {
    FileCheckResults checkResults;
    try (Reader reader = Files.newBufferedReader(xml, StandardCharsets.UTF_8)) {
      checkResults = (FileCheckResults) JAXBContext.newInstance(FileCheckResults.class).createUnmarshaller().unmarshal(reader);
    } catch (IOException | JAXBException e) {
      throw new RuntimeException("Unable to parse filecheck xml " + xml, e);
    }
    if (checkResults.getStatus().equals("FILE-ACCEPTED")) {
      return Collections.emptyList();
    } else {
      List<String> errors;
      if(checkResults.getErrors() != null && checkResults.getErrors().getErrors() != null && !checkResults.getErrors().getErrors().isEmpty()) {
        errors = checkResults.getErrors().getErrors();
      } else  {
        errors = Collections.singletonList(checkResults.getStatus());
      }
      return errors;
    }
  }

  private String resolveDownloadPath(NcSubmissionMessage ncSubmissionMessage) {
    String processingDacDir = processingFileStore.appendToPath(processingFileStore.getRoot(), "dac", ncSubmissionMessage.getDac(),
        ncSubmissionMessage.getTimestamp().toString(), ncSubmissionMessage.getFloatId());
    if (FileType.CORE_ARGO_PROFILE == ncSubmissionMessage.getFileType()) {
      processingDacDir = processingFileStore.appendToPath(processingDacDir, "profiles");
    }
    return processingFileStore.appendToPath(processingDacDir, ncSubmissionMessage.getFileName());
  }

  private void download(String downloadFile, Path ncFile) {
    try {
      processingFileStore.downloadLocalFile(downloadFile, ncFile);
    } catch (IOException e) {
      throw new RuntimeException("Unable to download " + downloadFile, e);
    }
  }

  private ArgoFileCheckExecutorContext createContext(NcSubmissionMessage ncSubmissionMessage, Path tempDir) {
    return new ArgoFileCheckExecutorContext() {
      @Override
      public String getDacName() {
        return ncSubmissionMessage.getDac();
      }

      @Override
      public boolean isUseOnlineNVS() {
        return useOnlineNvs;
      }

      @Override
      public Path getSpecDirName() {
        return null;
      }

      @Override
      public boolean isDoNulls() {
        return doNulls;
      }

      @Override
      public boolean isDoFormatOnly() {
        return doFormatOnly;
      }

      @Override
      public boolean isDoNameCheck() {
        return doNameCheck;
      }

      @Override
      public boolean isDoPsalStats() {
        return doPsalStats;
      }

      @Override
      public boolean isDoFormatOnlyPre31() {
        return doFormatOnlyPre31;
      }

      @Override
      public Path getInDir() {
        return tempDir;
      }

      @Override
      public Path getOutDir() {
        return tempDir;
      }

      @Override
      public boolean isDoXml() {
        return true;
      }

      @Override
      public boolean isUseInternalSpecs() {
        return true;
      }

      @Override
      public ApplicationProperties getApplicationProperties() {
        return ClasspathApplicationProperties.getApplicationProperties();
      }

      @Override
      public VersionInfoProperties getVersionInfoProperties() {
        return ClasspathVersionInfoProperties.getVersionInfoPropertiesProperties();
      }
    };
  }

}
