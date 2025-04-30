package edu.colorado.cires.argonaut.preprocessing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.S3;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Object;

@Testcontainers
public class S3Test {

  @Container
  private static LocalStackContainer localstack = new LocalStackContainer(DockerImageName.parse("localstack/localstack:3.5.0")).withServices(S3);


  private static S3Client s3;

  @BeforeAll
  public static void setupAll() {
    s3 = S3Client
        .builder()
        .endpointOverride(localstack.getEndpoint())
        .credentialsProvider(
            StaticCredentialsProvider.create(
                AwsBasicCredentials.create(localstack.getAccessKey(), localstack.getSecretKey())
            )
        )
        .region(Region.of(localstack.getRegion()))
        .build();
  }

  private static String getObjectBodyAsString(String bucket, String key) throws IOException {
    ByteArrayOutputStream outString = new ByteArrayOutputStream();
    try (ResponseInputStream<GetObjectResponse> responseInputStream = s3.getObject(b -> b.bucket(bucket).key(key))) {
      IOUtils.copy(responseInputStream, outString);
    }
    return outString.toString(StandardCharsets.UTF_8);
  }

  @Test
  public void testS3MetadataTimestamp() throws Exception {

    final String sourceBucket = "source-bucket";
    final String destBucket = "dest-bucket";
    final String prefix = "test-key/";
    final String key = prefix + "test.json";

    // create test buckets
    s3.createBucket(b -> b.bucket(sourceBucket));
    s3.createBucket(b -> b.bucket(destBucket));

    // save test file to source bucket to get a LastModified
    s3.putObject(b -> b.bucket(sourceBucket).key(key), RequestBody.fromString("{}", StandardCharsets.UTF_8));
    S3Object s3Object = s3.listObjectsV2(b -> b.bucket(sourceBucket).prefix(prefix)).contents().get(0);

    //convert LastModified to original-timestamp format and save to destination bucket
    LocalDateTime originalLastModified = s3Object.lastModified().atOffset(ZoneOffset.ofHours(4)).toLocalDateTime();
    String originalTimestamp = originalLastModified.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    Map<String, String> metadata = new HashMap<>();
    metadata.put("original-timestamp", originalTimestamp);
    s3.putObject(b -> b.bucket(destBucket).key(key).metadata(metadata),
        RequestBody.fromString(getObjectBodyAsString(sourceBucket, key), StandardCharsets.UTF_8));

    //List objects in destination bucket and get metadata, return original-timestamp (there is only one for this test)
    String savedOriginalTimestamp = s3.listObjectsV2(b -> b.bucket(destBucket).prefix("test-key/")).contents()
        .stream()
        .map(o -> s3.headObject(b -> b.bucket(destBucket).key(o.key())).metadata().get("original-timestamp"))
        .findFirst().orElse(null);

    assertEquals(originalTimestamp, savedOriginalTimestamp);
  }

}
