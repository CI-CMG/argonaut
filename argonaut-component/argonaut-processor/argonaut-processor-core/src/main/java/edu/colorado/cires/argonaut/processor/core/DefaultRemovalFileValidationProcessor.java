package edu.colorado.cires.argonaut.processor.core;

import edu.colorado.cires.argonaut.file.core.FileStore;
import edu.colorado.cires.argonaut.messaging.core.databind.DacSubmittedFileMessage;
import edu.colorado.cires.argonaut.messaging.core.databind.NcSubmissionMessage;
import edu.colorado.cires.argonaut.messaging.core.databind.NcSubmissionMessage.Operation;
import edu.colorado.cires.argonaut.messaging.core.databind.RemovalMessage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

public class DefaultRemovalFileValidationProcessor implements RemovalFileValidationProcessor {

  private static final Pattern FILE_NAME_PATTERN = Pattern.compile("([A-Z]+)?([0-9]+)_(.+)\\.nc(\\.filecheck)?");


  private FileStore fileStore;

  public void setFileStore(FileStore fileStore) {
    this.fileStore = fileStore;
  }

  @Override
  public RemovalMessage validate(DacSubmittedFileMessage message) {
//    String dac = message.getDac();
//    Instant timestamp = message.getTimestamp();
//    String fileName = fileStore.getFileName(message.getPath());
//    String processingPath = fileStore.appendToPath(
//            fileStore.getRoot(),
//            "dac",
//            message.getDac(),
//            "processing"
//    );
//    RemovalMessage output = null;
//    try {
//      output = RemovalMessage.builder()
//          .withFileName(fileName)
//          .withTimestamp(timestamp)
//          .withDac(dac)
//          .withValidationErrors(validate(dac, message.getPath()))
//          .build();
//
//      if (output.getValidationErrors().isEmpty()) {
//        output = RemovalMessage.builder(output).withRemovalFiles(parse(dac, timestamp, message.getPath())).build();
//      }
//    } finally {
//      fileStore.move(message.getPath(), fileStore.appendToPath(message.getProcessedPath(), timestamp.toString(), fileName));
//    }
    return null;
  }

  private List<String> validate(String dac, String fileName) {
    List<String> errors = new ArrayList<>();
    if (!fileName.equals(dac + "_removal.txt")) {
      errors.add("removal file name does not match DAC '" + dac + "' (" + dac + "_removal.txt): " + fileName);
    }
    return errors;
  }

  private List<NcSubmissionMessage> parse(String dac, Instant timestamp, String removalFile)  {
    List<NcSubmissionMessage> messages = new ArrayList<>();
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(fileStore.getInputStream(removalFile), StandardCharsets.UTF_8))) {
      reader.lines().forEach(line -> {
        if (StringUtils.isNotBlank(line)) {
           ncSubmissionMessageFromFileName(line.trim()).ifPresent(message -> {
            messages.add(NcSubmissionMessage.builder(message)
                .withDac(dac)
                .withTimestamp(timestamp)
                .withOperation(Operation.REMOVE)
                .build());
          });
        }
      });
    } catch (IOException e) {
      throw new RuntimeException("Unable to parse removal file", e);
    }
    return messages.stream().map(NcSubmissionMessage::builder).map(builder -> builder.withNumberOfFilesInSubmission(messages.size()).build()).collect(
        Collectors.toList());
  }


  //TODO common library?
  private static Optional<NcSubmissionMessage> ncSubmissionMessageFromFileName(String fileName) {
    Matcher matcher = FILE_NAME_PATTERN.matcher(fileName);
    if (matcher.matches()) {
      String floatDir = matcher.group(2);
      boolean profile = matcher.group(1) != null;
      NcSubmissionMessage ncSubmissionMessage = NcSubmissionMessage.builder()
          .withFileName(fileName)
          .withProfile(profile)
          .withFloatId(floatDir)
          .build();
      return Optional.of(ncSubmissionMessage);
    }
    return Optional.empty();
  }


}
