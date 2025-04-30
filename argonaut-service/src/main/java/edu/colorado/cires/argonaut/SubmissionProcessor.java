package edu.colorado.cires.argonaut;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.colorado.cires.argonaut.message.HeaderConsts;
import edu.colorado.cires.argonaut.util.ArgonautFileUtils;
import edu.colorado.cires.cmg.shellexecutor.DefaultShellExecutor;
import edu.colorado.cires.cmg.shellexecutor.ShellExecutor;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SubmissionProcessor implements Processor {

  private static final Logger LOGGER = LoggerFactory.getLogger(SubmissionProcessor.class);
  private static Pattern FILE_NAME_PATTERN = Pattern.compile("([A-Z]+)?([0-9]+)_(.+)\\.nc(\\.filecheck)?");

  private final ShellExecutor shellExecutor = new DefaultShellExecutor();
  private final Path exeJar;
  private final Path java;
  private final Path specDir;
  private final Path workingDir;
  private final Path processingDir;
  private final ProducerTemplate producerTemplate;
  private final Duration timeout;
  private final Path submissionDir;

  @Autowired
  public SubmissionProcessor(ServiceProperties serviceProperties, ProducerTemplate producerTemplate) {
    this.producerTemplate = producerTemplate;
    ClassLoader classLoader = getClass().getClassLoader();

    java = Paths.get(System.getProperty("java.home")).resolve("bin").resolve("java");

    workingDir = serviceProperties.getWorkDirectory();
    ArgonautFileUtils.createDirectories(workingDir);

    specDir = workingDir.resolve("file_checker_spec");

    URL execResource = classLoader.getResource("file_checker_exec.jar");
    URL specResource = classLoader.getResource("file_checker_spec.zip");

    if (execResource == null) {
      throw new IllegalArgumentException("file_checker_exec.jar not found");
    }
    if (specResource == null) {
      throw new IllegalArgumentException("file_checker_spec.zip not found");
    }

    exeJar = workingDir.resolve("file_checker_exec.jar");
    ArgonautFileUtils.copy(execResource, exeJar);

    unzip(specResource, workingDir);

    processingDir = workingDir.resolve("processing");
    ArgonautFileUtils.createDirectories(workingDir);

    timeout = serviceProperties.getFileCheckerTimeout();

    submissionDir = serviceProperties.getSubmissionDirectory();
  }

  private static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
    File destFile = new File(destinationDir, zipEntry.getName());

    String destDirPath = destinationDir.getCanonicalPath();
    String destFilePath = destFile.getCanonicalPath();

    if (!destFilePath.startsWith(destDirPath + File.separator)) {
      throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
    }

    return destFile;
  }

  private static void unzip(URL specResource, Path workingDir) {
    File destDir = workingDir.resolve("file_checker_spec").toFile();

    try (ZipInputStream zis = new ZipInputStream(specResource.openStream())) {
      ZipEntry zipEntry = zis.getNextEntry();
      while (zipEntry != null) {
        File newFile = newFile(destDir, zipEntry);
        if (zipEntry.isDirectory()) {
          if (!newFile.isDirectory() && !newFile.mkdirs()) {
            throw new IOException("Failed to create directory " + newFile);
          }
        } else {
          File parent = newFile.getParentFile();
          if (!parent.isDirectory() && !parent.mkdirs()) {
            throw new IOException("Failed to create directory " + parent);
          }
          try (FileOutputStream fos = new FileOutputStream(newFile)) {
            IOUtils.copy(zis, fos);
          }
        }
        zipEntry = zis.getNextEntry();
      }
    } catch (IOException e) {
      throw new RuntimeException("Unable to read specs", e);
    }
  }

  //TODO check for zip slip vulnerability
  public void unTar(Path tarGz, Path tempDir) throws IOException {
    try (InputStream inputStream = Files.newInputStream(tarGz)) {
      TarArchiveInputStream tar = new TarArchiveInputStream(new GzipCompressorInputStream(inputStream));
      ArchiveEntry entry;
      while ((entry = tar.getNextEntry()) != null) {
        Path extractTo = tempDir.resolve(entry.getName());
        if (entry.isDirectory()) {
          Files.createDirectories(extractTo);
        } else {
          Files.copy(tar, extractTo);
        }
      }
    }
  }


  @Override
  public void process(Exchange exchange) throws Exception {
    String dac = exchange.getIn().getHeader(HeaderConsts.DAC, String.class);
    String timestamp = exchange.getIn().getHeader(HeaderConsts.SUBMISSION_TIMESTAMP, String.class);
    Path submittedTarGzFile = exchange.getIn().getBody(File.class).toPath();
    Path submissionProcessingDir = submissionDir.resolve("dac").resolve(dac).resolve("processing").resolve(timestamp);
    ArgonautFileUtils.createDirectories(submissionProcessingDir);
    String tarGzFileFileName = submittedTarGzFile.getFileName().toString();
    Path tarGzFile = submissionProcessingDir.resolve(tarGzFileFileName);

    ArgonautFileUtils.move(submittedTarGzFile, tarGzFile);
//    FileResolver resolver = resolveTarGz(readyFile);

    Path tempDir = Files.createTempDirectory(workingDir, "processing");
    Map<String, FileCheckPair> fileCheckPairMap = new HashMap<>();
    try {
      unTar(tarGzFile, tempDir);
      try {

        Consumer<String> logger = System.out::println;

        String[] args = {
            java.toAbsolutePath().normalize().toString(),
            "-jar",
            exeJar.toAbsolutePath().normalize().toString(),
            dac,
            specDir.toAbsolutePath().normalize().toString(),
            tempDir.toAbsolutePath().normalize().toString(),
            tempDir.toAbsolutePath().normalize().toString()
        };

        LOGGER.info("Running " + Arrays.toString(args));

        int exitCode = shellExecutor.execute(tempDir, logger, timeout.toMillis(), args);
        if (exitCode != 0) {
          throw new RuntimeException("Error executing file checker: " + exitCode);
        }

      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new RuntimeException(e);
      }
      try (Stream<Path> files = Files.list(tempDir)) {
        files.filter(Files::isRegularFile)
            .forEach(file -> {
              String fileName = file.getFileName().toString();
              Matcher matcher = FILE_NAME_PATTERN.matcher(fileName);
              if(matcher.matches()) {
                String floatDir = matcher.group(2);

                String key = (matcher.group(1) == null ? "" : matcher.group(1)) + matcher.group(2) + "_" + matcher.group(3);

                FileCheckPair fileCheckPair = fileCheckPairMap.get(key);
                if (fileCheckPair == null){
                  fileCheckPair = new FileCheckPair();
                }
                if (file.getFileName().toString().contains(".filecheck")){
                  fileCheckPair.setFileCheckFile(file);
                } else {
                  fileCheckPair.setNcFile(file);
                }

                if (fileCheckPair.isComplete()){



                  Path processingDacDir = processingDir.resolve("dac").resolve(dac).resolve(floatDir);

                  // TODO is this assumption correct? Do we need to open the NetCDF file and check?
                  if (!Character.isDigit(key.charAt(0))){
                    processingDacDir = processingDir.resolve("profile");
                  }
                  ArgonautFileUtils.createDirectories(processingDacDir);
                  Path fileCheckFile = processingDacDir.resolve(fileCheckPair.getFileCheckFile().getFileName());
                  Path ncFile = processingDacDir.resolve(fileCheckPair.getNcFile().getFileName());

                  //TODO add logging
                  ArgonautFileUtils.move(fileCheckPair.getNcFile(), ncFile);
                  ArgonautFileUtils.move(fileCheckPair.getFileCheckFile(), fileCheckFile);

                  producerTemplate.sendBody("seda:validation", new ValidationMessage(ncFile, fileCheckFile));
                  fileCheckPairMap.remove(key);
                } else {
                  fileCheckPairMap.put(key, fileCheckPair);
                }

              } else {
                //TODO handle this properly
                throw new IllegalArgumentException("Invalid file name: " + fileName);
              }

          });
      }
    } finally {
      FileUtils.deleteQuietly(tempDir.toFile());
    }
    //incomplete pairs
    //TODO uncomment this after happy path is tested
//    fileCheckPairMap.forEach((key, fileCheckPair) ->{
//      Path errorDacDir = errorDir.resolve(resolver.dac);
//      Path tempFile = fileCheckPair.getExistingFile();
//      Path erFile = errorDacDir.resolve(fileCheckPair.getFileCheckFile().getFileName());
//      try {
//        Files.createDirectories(errorDir);
//      } catch (IOException e) {
//        throw new RuntimeException("Unable to create error "+ resolver.dac + " directory", e);
//      }
//      try{
//        Files.move(tempFile, erFile);
//      } catch (IOException e) {
//        throw new RuntimeException("Unable to move " + fileCheckPair.getNcFile() + " to " + erFile, e);
//      }
//      producerTemplate.sendBody("seda:error", new ErrorMessage(key,"Error executing AOML Processor"));
//    });
  }

//  private FileResolver resolveTarGz(File readyFile) {
//    Path readyPath = readyFile.toPath();
//    Path parent = readyPath.getParent();
//    if (readyPath.getParent() == null || readyPath.getParent().getParent() == null) {
//      throw new RuntimeException("Tar gz file parent is null");
//    }
//    parent = parent.toAbsolutePath().normalize();
//    String dac = parent.getParent().toAbsolutePath().normalize().getFileName().toString();
//    String readyFileName = readyFile.getName();
//    String fileName = readyFileName.replaceAll("\\.ready$", "");
//    return new FileResolver(parent.resolve(fileName), dac);
//  }

  private static class FileResolver {

    private final Path tarGzFile;
    private final String dac;

    private FileResolver(Path tarGzFile, String dac) {
      this.tarGzFile = tarGzFile;
      this.dac = dac;
    }

    public Path getTarGzFile() {
      return tarGzFile;
    }

    public String getDac() {
      return dac;
    }
  }
}
