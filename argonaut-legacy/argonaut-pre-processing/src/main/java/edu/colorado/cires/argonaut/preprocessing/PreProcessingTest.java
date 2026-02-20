package edu.colorado.cires.argonaut.preprocessing;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.apache.commons.io.IOUtils;
import software.amazon.awssdk.auth.credentials.AnonymousCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.endpoints.internal.Value.Str;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.S3Object;

public class PreProcessingTest {

  private static final LocalDateTime YESTERDAY = LocalDateTime.now().minusDays(1);

  private static Set<String> getOnPremFiles() throws Exception {
    Set<String> onPremFiles = new HashSet<>();

    try (BufferedReader reader = Files.newBufferedReader(
        Paths.get("/Users/cslater/projects/argo-pipeline/argonaut-pre-processing/src/main/resources/argo_all.txt"))) {
      reader.lines().forEach(line -> {
        line = line.replaceAll("\\s+", " ");
        String[] split = line.split(" ");
        String dateString = split[0];
        String timeString = split[1];
        String filename = split[3];
        LocalDateTime dateTime = LocalDateTime.parse(dateString + "T" + timeString);
        if (dateTime.isBefore(YESTERDAY)) {
          onPremFiles.add(filename);
        }
      });
    }
    return onPremFiles;
  }

  private static Set<String> getNccfFiles() throws Exception {
    Set<String> nccfFiles = new HashSet<>();
    try (BufferedReader reader = Files.newBufferedReader(
        Paths.get("/Users/cslater/projects/argo-pipeline/argonaut-pre-processing/src/main/resources/trust_results.txt"))) {
      reader.lines().forEach(line -> {
        line = line.replaceAll("\\s+", " ");
        String[] split = line.split(" ");
        String filename = split[3];
        if (filename.contains("/profiles/") && filename.matches("ARGO/.+")) {
          nccfFiles.add(filename.replaceAll("^ARGO/", ""));
        }
      });
    }
    return nccfFiles;
  }

  private static Set<String> getFrenchGdacFiles() throws Exception {
    Set<String> frenchGdacFiles = new HashSet<>();
//    JsonNode frenchJson = new ObjectMapper().readTree(Paths.get("/Users/cslater/projects/argo-pipeline/argonaut-pre-processing/src/main/resources/results.json").toFile());
//    ArrayNode contents = (ArrayNode) frenchJson.get("Contents");
//    contents.forEach(content -> {
//      String filename = content.get("Key").textValue().replaceAll("^pub/dac/", "");
//      if (filename.contains("/profiles/")) {
//        frenchGdacFiles.add(filename);
//      }
//
//    });

    try (BufferedReader reader = Files.newBufferedReader(
        Paths.get("/Users/cslater/projects/argo-pipeline/argonaut-pre-processing/src/main/resources/french_gdac.txt"))) {
      reader.lines().forEach(line -> {
        line = line.replaceAll("\\s+", " ");
        String[] split = line.split(" ");
        String filename = split[3];
        if (filename.contains("/profiles/") && filename.matches("pub/dac/.+")) {
          frenchGdacFiles.add(filename.replaceAll("^pub/dac/", ""));
        }
      });
    }

    return frenchGdacFiles;
  }

  private static Set<String> getOldCloudMissingFiles() throws Exception {
    Set<String> oldCloudMissingFiles = new HashSet<>();
    try (BufferedReader reader = Files.newBufferedReader(
        Paths.get("/Users/cslater/projects/argo-pipeline/argonaut-pre-processing/src/main/resources/missing_files_cloud.txt"))) {
      reader.lines().forEach(line -> {
        line = line.replaceAll("\\s+", " ");
        String[] split = line.split(" ");
        String filename = split[0];
        if (filename.contains("/profiles/")) {
          oldCloudMissingFiles.add(filename);
        }
      });
    }
    return oldCloudMissingFiles;
  }

  private static void save(Set<String> fileNames, Path path) throws Exception {
    try (BufferedWriter writer = Files.newBufferedWriter(path)) {
      fileNames.forEach(fileName -> {
        try {
          writer.write(fileName);
          writer.newLine();
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      });
    }
  }


  public static void main(String[] args) throws Exception {

    Set<String> onPremFiles = getOnPremFiles();
    Set<String> frenchGdacFiles = getFrenchGdacFiles();
    Set<String> nccfFiles = getNccfFiles();

    Set<String> filesInFrenchGdacButNotInNccf = new HashSet<>(frenchGdacFiles);
    filesInFrenchGdacButNotInNccf.removeAll(nccfFiles);
    System.out.println("Files in French GDAC, but not in NCCF: " + filesInFrenchGdacButNotInNccf.size());

    Set<String> filesOnPremButNotInFrenchGdac = new HashSet<>(onPremFiles);
    filesOnPremButNotInFrenchGdac.removeAll(frenchGdacFiles);
    System.out.println("Files in ftp-oceans.ncei.noaa.gov, but not in French GDAC: " + filesOnPremButNotInFrenchGdac.size());

    Set<String> filesOnPremButNotInNccf = new HashSet<>(onPremFiles);
    filesOnPremButNotInNccf.removeAll(nccfFiles);
    System.out.println("Files in ftp-oceans.ncei.noaa.gov, but not in NCCF: " + filesOnPremButNotInNccf.size());

//    Set<String> oldMissingFiles = getOldCloudMissingFiles();

//    Set<String> missingFileDiff = new HashSet<>(oldMissingFiles);
//    missingFileDiff.removeAll(filesInFrenchGdacButNotInNccf);
//    System.out.println("Missing files: " + missingFileDiff);

    filesInFrenchGdacButNotInNccf.forEach(path -> {

      try (S3Client s3 = S3Client.builder().region(Region.US_EAST_1).credentialsProvider(AnonymousCredentialsProvider.create()).build()) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
            .bucket("argo-gdac-sandbox")
            .key("pub/dac/" + path)
            .build();

        Path file = Paths.get("/Users/cslater/projects/argo-pipeline/argonaut-pre-processing/src/main/resources/nc").resolve(path);
        Path dir = file.getParent();
        try {
          Files.createDirectories(dir);
        } catch (IOException e) {
          throw new RuntimeException(e);
        }

        ResponseInputStream<GetObjectResponse> response = s3.getObject(getObjectRequest);
        try(OutputStream outputStream = Files.newOutputStream(file)) {
          IOUtils.copy(response, outputStream);
        } catch (IOException e) {
          throw new RuntimeException(e);
        } finally {
          try {
            response.close();
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        }
      }


    });

//    save(new TreeSet<>(filesInFrenchGdacButNotInNccf), Paths.get("/Users/cslater/projects/argo-pipeline/argonaut-pre-processing/src/main/resources/missing_from_test.txt"));

//    new TreeSet<>(filesInFrenchGdacButNotInNccf).forEach(System.out::println);

//    Set<String> filesMissingInFrenchGdacNotInScan = new HashSet<>(filesOnPremButNotInFrenchGdac);
//    filesMissingInFrenchGdacNotInScan.removeAll(missingFiles);

//    System.out.println("Files missing in French GDAC: " + filesOnPremButNotInFrenchGdac.size());
//    System.out.println("Missing files not accounted for from scan: " + filesMissingInFrenchGdacNotInScan);

  }

  //    long startTime = System.currentTimeMillis();
//    try (S3Client s3 = S3Client.builder().region(Region.US_EAST_1).credentialsProvider(AnonymousCredentialsProvider.create()).build()) {
//      ListObjectsV2Request listReq = ListObjectsV2Request.builder()
//          .bucket("argo-gdac-sandbox")
//          .prefix("pub/dac/aoml/")
//          .build();
//      List<String> keys = s3.listObjectsV2Paginator(listReq).contents().stream().map(S3Object::key).toList();
//      System.out.println(keys);
//      System.out.println(keys.size());
//    }
//    long endTime = System.currentTimeMillis();
//
//    System.out.println((endTime - startTime) / 1000);
  //    Set<String> missingFiles = new HashSet<>();

//    try (BufferedReader reader = Files.newBufferedReader(Paths.get("/Users/cslater/projects/argo-pipeline/argonaut-pre-processing/src/main/resources/missing_files_cloud.txt"))) {
//      reader.lines().forEach(line -> {
//        line = line.replaceAll("\\s+", " ");
//        String[] split = line.split(" ");
//        String filename = split[0];
//        missingFiles.add(filename);
//      });
//    }

}
