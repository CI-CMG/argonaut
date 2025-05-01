package edu.colorado.cires.argonaut.util;

import edu.colorado.cires.argonaut.ServiceProperties;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.io.IOUtils;

public class ArgonautFileUtils {

  public static void createDirectories(Path path) {
    try {
      Files.createDirectories(path);
    } catch (IOException e) {
      throw new RuntimeException("Unable to create directory: " + path, e);
    }
  }

  public static void copy(URL resource, Path dest) {
    try {
      IOUtils.copy(resource, dest.toFile());
    } catch (IOException e) {
      throw new RuntimeException("Unable to copy resource: " + resource + " to " + dest, e);
    }
  }

  public static void move(Path src, Path dest) {
    try{
      Files.move(src, dest);
    } catch (IOException e) {
      throw new RuntimeException("Unable to move " + src + " to " + dest, e);
    }
  }

  public static void unzip(URL specResource, Path destPath) {
    File destDir = destPath.toFile();

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

  private static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
    File destFile = new File(destinationDir, zipEntry.getName());

    String destDirPath = destinationDir.getCanonicalPath();
    String destFilePath = destFile.getCanonicalPath();

    if (!destFilePath.startsWith(destDirPath + File.separator)) {
      throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
    }

    return destFile;
  }


  //TODO check for zip slip vulnerability
  public static void unTar(Path tarGz, Path tempDir) throws IOException {
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

  public static Path getProcessingDir(ServiceProperties serviceProperties) {
    return serviceProperties.getWorkDirectory().resolve("processing");
  }

  public static Path getProcessingDirForDac(ServiceProperties serviceProperties, String dac) {
    return getProcessingDir(serviceProperties).resolve("dac").resolve(dac);
  }

  public static Path getProcessingProfileDir(ServiceProperties serviceProperties, String dac, String floatId, boolean isProfile) {
    Path dir = getProcessingDirForDac(serviceProperties, dac).resolve(floatId);
    if (isProfile) {
      dir = dir.resolve("profiles");
    }
    return dir;
  }

  public static Path getSubmissionDirForDac(ServiceProperties serviceProperties, String dac) {
    return serviceProperties.getSubmissionDirectory().resolve("dac").resolve(dac);
  }

  public static Path getSubmissionProcessingDirForDac(ServiceProperties serviceProperties, String dac) {
    return getSubmissionDirForDac(serviceProperties, dac).resolve("processing");
  }

  public static Path getSubmissionProcessedDirForDac(ServiceProperties serviceProperties, String dac) {
    return getSubmissionDirForDac(serviceProperties, dac).resolve("processed");
  }

  private ArgonautFileUtils() {

  }
}
