package edu.colorado.cires.argonaut.processor.core;

import jakarta.xml.bind.DatatypeConverter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Locale;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;

public final class ChecksumUtils {

  private ChecksumUtils() {

  }

  public static String calculateMd5Checksum(InputStream inputStream) {
    String derivedChecksum = "";
    try {
      MessageDigest messageDigest = DigestUtils.getDigest("md5");
      byte[] digest = DigestUtils.digest(messageDigest, inputStream);
      derivedChecksum = DatatypeConverter.printHexBinary(digest).toLowerCase(Locale.ENGLISH);
    } catch (IOException e) {
      throw new RuntimeException("Failed to derive checksum", e);
    }
    return derivedChecksum;
  }


  public static ByteArrayInputStream md5InputStream(InputStream dataInputStream, String dataFileName) {
    String fileContent = calculateMd5Checksum(dataInputStream) + "  " + dataFileName + "\n";
    return new ByteArrayInputStream(fileContent.getBytes(StandardCharsets.UTF_8));
  }

  public static void writeMd5ToOutputStream(InputStream dataInputStream, String dataFileName, OutputStream outputStream) {
    try (InputStream inputStream = md5InputStream(dataInputStream, dataFileName)) {
      IOUtils.copy(inputStream, outputStream);
    } catch (IOException e) {
      throw new RuntimeException("Unable to create md5 for file: " + dataFileName, e);
    }
  }

}
