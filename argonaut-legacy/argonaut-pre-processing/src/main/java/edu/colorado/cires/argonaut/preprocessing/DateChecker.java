package edu.colorado.cires.argonaut.preprocessing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class DateChecker {

  private static class Difference {
    private final String key;
    private final int nccfSize;
    private final int frenchSize;
    private final Instant originalTimestamp;
    private final Instant nccfLastModified;
    private final Instant frenchLastModified;

    public Difference(String key, int nccfSize, int frenchSize, Instant originalTimestamp, Instant nccfLastModified, Instant frenchLastModified) {
      this.key = key;
      this.nccfSize = nccfSize;
      this.frenchSize = frenchSize;
      this.originalTimestamp = originalTimestamp;
      this.nccfLastModified = nccfLastModified;
      this.frenchLastModified = frenchLastModified;
    }

    public String getKey() {
      return key;
    }

    public int getNccfSize() {
      return nccfSize;
    }

    public int getFrenchSize() {
      return frenchSize;
    }

    public Instant getOriginalTimestamp() {
      return originalTimestamp;
    }

    public Instant getNccfLastModified() {
      return nccfLastModified;
    }

    public Instant getFrenchLastModified() {
      return frenchLastModified;
    }

    public static String headers() {
      return "KEY,NCCF_SIZE,FRENCH_SIZE,ORIGINAL_TIMESTAMP_METADATA,NCCF_LAST_MODIFIED,FRENCH_LAST_MODIFIED";
    }

    @Override
    public String toString() {
      return key + ","
          + (nccfSize == -1 ? "" : nccfSize) + ","
          + (frenchSize == -1 ? "" : frenchSize) + ","
          + (originalTimestamp == null ? "" : originalTimestamp) + ","
          + (nccfLastModified == null ? "" : nccfLastModified) + ","
          + (frenchLastModified == null ? "" : frenchLastModified);
    }
  }

  private static void writeFile(Collection<Difference> differences, String fileName) throws Exception{
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
      writer.write(Difference.headers());
      writer.write("\n");
      for (Difference difference : differences) {
        writer.write(difference.toString());
        writer.write("\n");
      }
    }
  }

  public static void main(String[] args) throws Exception {
    Map<String, FileDateInfo> frenchGdac = getFrenchGdacFiles();
    Set<String> invalidOriginalTimestamps = new TreeSet<>();
    Map<String, FileDateInfo> nccf = getNccfFiles(invalidOriginalTimestamps);

    List<Difference> sizeDiffers = new ArrayList<>();
    List<Difference> frenchNewerComparedWithOriginalTimestamp = new ArrayList<>();
    List<Difference> frenchNewerComparedWithLastModified = new ArrayList<>();
    List<Difference> frenchLastModifiedDoesNotEqualOriginalTimestamp = new ArrayList<>();
    for (FileDateInfo nccfFile : nccf.values()) {
      FileDateInfo frenchFile = frenchGdac.get(nccfFile.getKey());
      if (frenchFile != null) {
        Difference difference = new Difference(nccfFile.getKey(), nccfFile.getSize(), frenchFile.getSize(), nccfFile.getOriginalLastModified(), nccfFile.getLastModified(), frenchFile.getLastModified());
        if (difference.getNccfSize() != difference.getFrenchSize()) {
          sizeDiffers.add(difference);
        }
        if (difference.getFrenchLastModified().isAfter(difference.getOriginalTimestamp())) {
          frenchNewerComparedWithOriginalTimestamp.add(difference);
        }
        if (difference.getFrenchLastModified().isAfter(difference.getNccfLastModified())) {
          frenchNewerComparedWithLastModified.add(difference);
        }
        if (difference.getFrenchLastModified().compareTo(difference.getOriginalTimestamp()) != 0) {
          frenchLastModifiedDoesNotEqualOriginalTimestamp.add(difference);
        }
      }
    }

    writeFile(invalidOriginalTimestamps.stream().map(key -> new Difference(key, -1, -1, null, null, null)).toList(), "/Users/cslater/projects/argo-pipeline/argonaut-pre-processing/target/invalid_original_timestamps.csv");
    writeFile(sizeDiffers, "/Users/cslater/projects/argo-pipeline/argonaut-pre-processing/target/size_differs.csv");
    writeFile(frenchNewerComparedWithOriginalTimestamp, "/Users/cslater/projects/argo-pipeline/argonaut-pre-processing/target/french_newer_than_original_timestamp.csv");
    writeFile(frenchNewerComparedWithLastModified, "/Users/cslater/projects/argo-pipeline/argonaut-pre-processing/target/french_newer_than_nccf_last_modified.csv");
    writeFile(frenchLastModifiedDoesNotEqualOriginalTimestamp, "/Users/cslater/projects/argo-pipeline/argonaut-pre-processing/target/french_last_modified_different_than_original_timestamp.csv");

  }


  private static Map<String, FileDateInfo> getFrenchGdacFiles() throws Exception {
    Map<String, FileDateInfo> frenchGdacFiles = new HashMap<>();
    try (BufferedReader reader = Files.newBufferedReader(
        Paths.get("/Users/cslater/projects/argo-pipeline/argonaut-pre-processing/src/main/resources/french_gdac.txt"))) {
      reader.lines().forEach(line -> {
        line = line.replaceAll("\\s+", " ");
        String[] split = line.split(" ");
        String filename = split[3];
        String date = split[0];
        String time = split[1];
        ZonedDateTime dateTime = ZonedDateTime.parse(date + "T" + time +"-06:00");
        Instant instant = dateTime.toInstant();
        int size = Integer.parseInt(split[2]);
        if (filename.contains("/profiles/") && filename.matches("pub/dac/.+")) {
          String key = filename.replaceAll("^pub/dac/", "");
          frenchGdacFiles.put(key, new FileDateInfo(key, size, instant, null));
        }
      });
    }

    return frenchGdacFiles;
  }

  private static DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  private static Map<String, FileDateInfo> getNccfFiles(Set<String> invalidOriginalTimestamps) throws Exception {
    Map<String, FileDateInfo> files = new HashMap<>();
    try (BufferedReader reader = Files.newBufferedReader(
        Paths.get("/Users/cslater/projects/argo-pipeline/argonaut-pre-processing/src/main/resources/argo_cloud_details.csv"))) {
      reader.lines().forEach(line -> {
        String[] split = line.split(",");
        String key = split[0].replaceAll("^ARGO/", "");
        int size = Integer.parseInt(split[1]);
        ZonedDateTime dateTime = ZonedDateTime.parse(split[2]);
        Instant instant = dateTime.toInstant();
        Instant originalTimestamp = null;
        try {
          String originalTimestampString = split[3]; // 2023-08-02 10:46:11
          LocalDateTime ldt = LocalDateTime.parse(originalTimestampString, DTF);
          OffsetDateTime zdt = ldt.atOffset(ZoneOffset.ofHours(-4));
          originalTimestamp = zdt.toInstant(); // 2023-08-02T08:46:11-06:00 from s3://argo-gdac-sandbox/pub/dac/aoml/13857/profiles/R13857_001.nc == 2023-08-02T14:46:11Z
        } catch (Exception e) {
          // no-op
        }

        if (originalTimestamp != null) {
          files.put(key, new FileDateInfo(key, size, instant, originalTimestamp));
        } else {
          invalidOriginalTimestamps.add(key);
        }
      });
    }

    return files;
  }

  private static class FileDateInfo {
    private final String key;
    private final int size;
    private final Instant lastModified;
    private final Instant originalLastModified;


    private FileDateInfo(String key, int size, Instant lastModified, Instant originalLastModified) {
      this.key = key;
      this.size = size;
      this.lastModified = lastModified;
      this.originalLastModified = originalLastModified;
    }

    public String getKey() {
      return key;
    }

    public int getSize() {
      return size;
    }

    public Instant getLastModified() {
      return lastModified;
    }

    public Instant getOriginalLastModified() {
      return originalLastModified;
    }

    @Override
    public String toString() {
      return "FileDateInfo{" +
          "key='" + key + '\'' +
          ", size=" + size +
          ", lastModified=" + lastModified +
          ", originalLastModified=" + originalLastModified +
          '}';
    }
  }

}
