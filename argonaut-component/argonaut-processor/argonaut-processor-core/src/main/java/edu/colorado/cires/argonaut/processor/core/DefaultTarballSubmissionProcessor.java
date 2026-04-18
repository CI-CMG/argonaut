package edu.colorado.cires.argonaut.processor.core;

import edu.colorado.cires.argonaut.file.core.FileStore;
import edu.colorado.cires.argonaut.messaging.core.databind.DacSubmittedFileMessage;
import edu.colorado.cires.argonaut.messaging.core.databind.NcSubmissionMessage;
import edu.colorado.cires.argonaut.messaging.core.databind.NcSubmissionMessage.FileType;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultTarballSubmissionProcessor implements TarballSubmissionProcessor {

  private static final Logger LOGGER = LoggerFactory.getLogger(DefaultTarballSubmissionProcessor.class);

  private static final Pattern FILE_NAME_PATTERN = Pattern.compile("([A-Z]+)?([0-9]+)_(.+)\\.nc(\\.filecheck)?");

  private FileStore submissionFileStore;
  private FileStore processingFileStore;
  private Path localTempDir;

  public void setSubmissionFileStore(FileStore submissionFileStore) {
    this.submissionFileStore = submissionFileStore;
  }

  public void setProcessingFileStore(FileStore processingFileStore) {
    this.processingFileStore = processingFileStore;
  }

  public void setLocalTempDir(Path localTempDir) {
    this.localTempDir = localTempDir;
      try {
          Files.createDirectories(localTempDir);
      } catch (IOException e) {
          throw new RuntimeException("Unable to create temp directory: " + localTempDir, e);
      }
  }

  private static void unTarGz(Path tarGz, Path tempDir) throws IOException {
    tempDir = tempDir.normalize();
    try (InputStream inputStream = Files.newInputStream(tarGz)) {
      TarArchiveInputStream tar = new TarArchiveInputStream(new GzipCompressorInputStream(inputStream));
      ArchiveEntry entry;
      while ((entry = tar.getNextEntry()) != null) {
        Path extractTo = tempDir.resolve(entry.getName()).normalize();
        if (!extractTo.startsWith(tempDir)) {
          throw new IllegalArgumentException("Un tarring escaped destination location: " + tempDir + " - " + extractTo);
        }
        if (entry.isDirectory()) {
          Files.createDirectories(extractTo);
        } else {
          Files.copy(tar, extractTo);
        }
      }
    }
  }

  private static List<Path> listFiles(Path directory) {
    try (Stream<Path> files = Files.list(directory)) {
      return files.toList();
    } catch (IOException e) {
      throw new RuntimeException("Unable to list files" + directory, e);
    }
  }

  public List<NcSubmissionMessage> untarAndMoveToProcessing(DacSubmittedFileMessage submittedFile) {

    String tarGzFileFileName = submissionFileStore.getFileName(submittedFile.getPath());

    Path tempWorkDir;
    try {
      tempWorkDir = Files.createTempDirectory(localTempDir, "processing");
    } catch (IOException e) {
      throw new RuntimeException("An error occurred while creating local work directory", e);
    }

    List<NcSubmissionMessage> output = new ArrayList<>();

    try {
      Path tarGzFile = tempWorkDir.resolve(tarGzFileFileName);
      try {
        submissionFileStore.downloadLocalFile(submittedFile.getPath(), tarGzFile);
      } catch (IOException e) {
        throw new RuntimeException("Unable to copy: " + submittedFile.getPath() + " to " + tarGzFile, e);
      }

      Path tempDir = tempWorkDir.resolve("untar");
      try {
        Files.createDirectories(tempDir);
      } catch (IOException e) {
        throw new RuntimeException("Unable to create untar location: " + tempDir, e);
      }
      try {
        unTarGz(tarGzFile, tempDir);
      } catch (IOException e) {
        throw new RuntimeException("Unable to untar: " + tarGzFile, e);
      }

      for (Path file : listFiles(tempDir)) {
        if (Files.isRegularFile(file)) {
          String fileName = file.getFileName().toString();
          Matcher matcher = FILE_NAME_PATTERN.matcher(fileName);
          if (matcher.matches()) {
            String floatDir = matcher.group(2);
            boolean profile = matcher.group(1) != null;
            NcSubmissionMessage ncSubmissionMessage = NcSubmissionMessage.builder()
                .withFileName(fileName)
                .withFileType(profile ? FileType.CORE_ARGO_PROFILE : FileType.AUXILIARY)
                .withFloatId(floatDir)
                .withDac(submittedFile.getDac())
                .withTimestamp(submittedFile.getTimestamp())
                .build();
            String processingDacDir = processingFileStore.appendToPath(processingFileStore.getRoot(), "dac", submittedFile.getDac(), submittedFile.getTimestamp().toString(), floatDir);
            if (FileType.CORE_ARGO_PROFILE == ncSubmissionMessage.getFileType()) {
              processingDacDir = processingFileStore.appendToPath(processingDacDir, "profiles");
            }
            String ncFile = processingFileStore.appendToPath(processingDacDir, file.getFileName().toString());
            LOGGER.info("Adding to processing directory {}", ncFile);
            try {
              processingFileStore.uploadLocalFile(file, ncFile);
            } catch (IOException e) {
              throw new RuntimeException("Unable to upload file: " + file + " to " + ncFile, e);
            }
            output.add(ncSubmissionMessage);
          }
        }
      }
    } finally {
      FileUtils.deleteQuietly(tempWorkDir.toFile());
      String processedPath = submissionFileStore.appendToPath(submissionFileStore.getRoot(), "dac", submittedFile.getDac(), "processed", submittedFile.getTimestamp().toString(), tarGzFileFileName);
      submissionFileStore.move(submittedFile.getPath(), processedPath);
    }

    List<NcSubmissionMessage> result = new ArrayList<>(output.size());
    for (NcSubmissionMessage ncSubmissionMessage : output) {
      NcSubmissionMessage finalNcSubmissionMessage = NcSubmissionMessage.builder(ncSubmissionMessage)
          .withNumberOfFilesInSubmission(output.size())
          .build();
      result.add(finalNcSubmissionMessage);
    }

    return result;
  }

}
