package edu.colorado.cires.argonaut.util;

import edu.colorado.cires.argonaut.config.ServiceProperties;
import edu.colorado.cires.argonaut.message.NcSubmissionMessage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

public class ArgonautFileUtils {

  private static final Pattern FILE_NAME_PATTERN = Pattern.compile("([A-Z]+)?([0-9]+)_(.+)\\.nc(\\.filecheck)?");

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

  public static void copy(Path source, Path dest) {
    try {
      FileUtils.copyFile(source.toFile(), dest.toFile());
    } catch (IOException e) {
      throw new RuntimeException("Unable to copy file: " + source + " to " + dest, e);
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

  public static Path getSubmissionDirForDacSubmit(ServiceProperties serviceProperties, String dac) {
    return getSubmissionDirForDac(serviceProperties, dac).resolve("submit");
  }

  public static Path getSubmissionProcessingDirForDac(ServiceProperties serviceProperties, String dac) {
    return getSubmissionDirForDac(serviceProperties, dac).resolve("processing");
  }

  public static Path getSubmissionProcessedDirForDac(ServiceProperties serviceProperties, String dac) {
    return getSubmissionDirForDac(serviceProperties, dac).resolve("processed");
  }

  public static Path getRemovedProfileDir(ServiceProperties serviceProperties, String dac, String timestamp, String floatId, boolean isProfile) {
    Path dacDir = serviceProperties.getOutputDirectory().resolve("removed").resolve("dac").resolve(dac).resolve(timestamp).resolve(floatId);
    if (isProfile) {
      dacDir = dacDir.resolve("profiles");
    }
    return dacDir;
  }

  public static Path getOutputProfileDir(ServiceProperties serviceProperties, String dac, String floatId, boolean isProfile) {
    Path dacDir = serviceProperties.getOutputDirectory().resolve("dac").resolve(dac).resolve(floatId);
    if (isProfile) {
      dacDir = dacDir.resolve("profiles");
    }
    return dacDir;
  }

  public static Path getRejectProfileDir(ServiceProperties serviceProperties, String dac, String timestamp, String floatId, boolean isProfile) {
    Path dacDir = getSubmissionProcessedDirForDac(serviceProperties, dac).resolve(timestamp).resolve("reject").resolve(floatId);
    if (isProfile) {
      dacDir = dacDir.resolve("profiles");
    }
    return dacDir;
  }

  public static Optional<NcSubmissionMessage> ncSubmissionMessageFromFileName(String fileName) {
    Matcher matcher = FILE_NAME_PATTERN.matcher(fileName);
    if (matcher.matches()) {
      String floatDir = matcher.group(2);
      boolean profile = matcher.group(1) != null;
      NcSubmissionMessage ncSubmissionMessage = new NcSubmissionMessage();
      ncSubmissionMessage.setFileName(fileName);
      ncSubmissionMessage.setProfile(profile);
      ncSubmissionMessage.setFloatId(floatDir);
      return Optional.of(ncSubmissionMessage);
    }
    return Optional.empty();
  }

  public static String getRequiredFileName(Path path) {
    Path fileName = path.getFileName();
    if (fileName == null) {
      throw new IllegalArgumentException("No file name on path: " + path);
    }
    return fileName.toString();
  }

  private ArgonautFileUtils() {

  }
}
