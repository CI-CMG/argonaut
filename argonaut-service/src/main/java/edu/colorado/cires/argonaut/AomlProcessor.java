package edu.colorado.cires.argonaut;

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
import java.util.function.Consumer;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AomlProcessor implements Processor {

  private final ShellExecutor shellExecutor = new DefaultShellExecutor();
  private final Path exeJar;
  private final Path java;
  private final Path specDir;
  private final Path workingDir;
  private final Path processingDir;
  private final ProducerTemplate producerTemplate;

  @Autowired
  public AomlProcessor(ServiceProperties serviceProperties, ProducerTemplate producerTemplate) {
    this.producerTemplate = producerTemplate;
    ClassLoader classLoader = getClass().getClassLoader();

    java = Paths.get(System.getProperty("java.home")).resolve("bin").resolve("java");

    workingDir = Paths.get(serviceProperties.getWorkDirectory());
    try {
      Files.createDirectories(workingDir);
    } catch (IOException e) {
      throw new RuntimeException("Unable to create " + workingDir, e);
    }
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
    try {
      IOUtils.copy(execResource, exeJar.toFile());
    } catch (IOException e) {
      throw new RuntimeException("Unable to copy file checker", e);
    }

    unzip(specResource, workingDir);

    processingDir = workingDir.resolve("processing");
    try {
      Files.createDirectories(processingDir);
    } catch (IOException e) {
      throw new RuntimeException("Unable to create processing directory", e);
    }
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
    File readyFile = exchange.getIn().getBody(File.class);
    FileResolver resolver = resolveTarGz(readyFile);

    Path tempDir = Files.createTempDirectory(workingDir, "processing");
    try {
      unTar(resolver.getTarGzFile(), tempDir);
      try {
        long timeout = 120000L;

        Consumer<String> logger = System.out::println;

        int exitCode = shellExecutor.execute(Paths.get("/Users/cslater/projects/argo-pipeline"), logger, timeout,
            java.toAbsolutePath().toString(),
            "-jar",
            exeJar.toAbsolutePath().toString(),
            resolver.getDac(),
            specDir.toAbsolutePath().toString(),
            tempDir.toAbsolutePath().toString(),
            tempDir.toAbsolutePath().toString());
        if (exitCode != 0) {
          throw new RuntimeException("Error executing file checker: " + exitCode);
        }

      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new RuntimeException(e);
      }
      try (Stream<Path> files = Files.list(tempDir)) {
        files.filter(file -> file.getFileName().toString().endsWith(".filecheck"))
            .forEach(file -> {
              String fileName = file.getFileName().toString().replaceAll("\\.filecheck$", "");
              Path fileCheckFile = processingDir.resolve(resolver.dac).resolve(file.getFileName());
              Path tempNcFile = file.getParent().resolve(fileName);
              Path ncFile = processingDir.resolve(resolver.dac).resolve(fileName);
              try {
                Files.move(tempNcFile, ncFile);
              } catch (IOException e) {
                throw new RuntimeException("Unable to move " + tempNcFile + " to " + ncFile, e);
              }
              try {
                Files.move(file, fileCheckFile);
              } catch (IOException e) {
                throw new RuntimeException("Unable to move " + file + " to " + fileCheckFile, e);
              }

              producerTemplate.sendBody("seda:validation", new ValidationMessage(ncFile, fileCheckFile));
            });
      }
    } finally {
      FileUtils.deleteQuietly(tempDir.toFile());
    }


  }

  private FileResolver resolveTarGz(File readyFile) {
    Path readyPath = readyFile.toPath();
    Path parent = readyPath.getParent();
    if (readyPath.getParent() == null || readyPath.getParent().getParent() == null) {
      throw new RuntimeException("Tar gz file parent is null");
    }
    String dac = parent.getParent().getFileName().toString();
    String readyFileName = readyFile.getName();
    String fileName = readyFileName.replaceAll("\\.ready$", "");
    return new FileResolver(parent.resolve(fileName), dac);
  }

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
