package edu.colorado.cires.argonaut.processor.core;

import edu.colorado.cires.argonaut.file.core.FileStore;
import edu.colorado.cires.argonaut.messaging.core.databind.ArgoFileType;
import edu.colorado.cires.argonaut.messaging.core.databind.ArgoFileTypeDetails;
import edu.colorado.cires.argonaut.messaging.core.databind.ProfileMode;
import edu.colorado.cires.argonaut.messaging.core.databind.ProfileOperation;
import edu.colorado.cires.argonaut.messaging.core.queue.MessageSender;
import edu.colorado.cires.argonaut.metadata.core.DefaultRecentProfileSearch;
import edu.colorado.cires.argonaut.metadata.core.MetadataStore;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.databind.json.JsonMapper;

public class DefaultLatestProfileMergeTrigger implements LatestProfileMergeTrigger {

  private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
  private static final Logger LOGGER = LoggerFactory.getLogger(DefaultLatestProfileMergeTrigger.class);


  private Supplier<UUID> traceIdGenerator = () -> UUID.randomUUID();
  private Supplier<Instant> nowGenerator = () -> Instant.now();
  private MetadataStore metadataStore;
  private MessageSender messageSender;
  private JsonMapper jsonMapper;
  private String mergeQueue;
  private FileStore outputFileStore;
  private boolean enabled = true;
  private int daysBack = 30;
  private int limit = 10000;

  public void setLimit(int limit) {
    if (limit > 10000) {
      throw new IllegalArgumentException("limit can't be greater than 10000");
    }
    this.limit = limit;
  }

  public void setOutputFileStore(FileStore outputFileStore) {
    this.outputFileStore = outputFileStore;
  }

  public void setDaysBack(int daysBack) {
    this.daysBack = daysBack;
  }

  public void setTraceIdGenerator(Supplier<UUID> traceIdGenerator) {
    this.traceIdGenerator = traceIdGenerator;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public void setMetadataStore(MetadataStore metadataStore) {
    this.metadataStore = metadataStore;
  }

  public void setMessageSender(MessageSender messageSender) {
    this.messageSender = messageSender;
  }

  public void setJsonMapper(JsonMapper jsonMapper) {
    this.jsonMapper = jsonMapper;
  }

  public void setMergeQueue(String mergeQueue) {
    this.mergeQueue = mergeQueue;
  }

  public void setNowGenerator(Supplier<Instant> nowGenerator) {
    this.nowGenerator = nowGenerator;
  }

  public DefaultLatestProfileMergeTrigger withDaysBack(int daysBack) {
    this.daysBack = daysBack;
    return this;
  }

  @Override
  public void trigger() {
    if (enabled) {
      execute();
    }
  }

  private static List<String> getExpectedFileNamePrefixes(List<LocalDate> days) {
    List<String> fileNamePrefixes = new ArrayList<>(days.size() * 2);
    for (LocalDate day : days) {
      fileNamePrefixes.add("D" + FORMATTER.format(day) + "_prof_");
      fileNamePrefixes.add("R" + FORMATTER.format(day) + "_prof_");
    }
    return fileNamePrefixes;
  }

  private List<LocalDate> getExpectedDates() {
    // list will always have the current date
    List<LocalDate> days = new ArrayList<>(daysBack + 1);
    LocalDate today = ZonedDateTime.ofInstant(nowGenerator.get(), ZoneId.of("UTC")).toLocalDate();
    days.add(today);
    for (int i = 1; i <= daysBack + 1; i++) {
      days.add(today.minusDays(i));
    }
    return days;

  }

  public void execute() {
    LOGGER.info("Triggered Recent Profile Merge Aggregator");
    String outputDirectory = outputFileStore.appendToPath(outputFileStore.getRoot(), "latest_data");
    List<String> existingFiles = outputFileStore.listFileNamesInDirectory(outputDirectory);
    List<LocalDate> expectedDates = getExpectedDates();
    List<String> expectedPrefixes = getExpectedFileNamePrefixes(expectedDates);

    Set<String> toRemove = new HashSet<>();
    for (String fileName : existingFiles) {
      ArgoFileTypeDetails fileType = ArgoFileType.getFileNameDetails(fileName);
      if (fileType.getType() == ArgoFileType.LATEST_PROFILE_MERGE) {
        String fileNamePrefix = fileName.substring(0, 15);
        if (!expectedPrefixes.contains(fileNamePrefix)) {
          toRemove.add(fileName.substring(0, 9));
        }
      }
    }

    UUID traceId = this.traceIdGenerator.get();

    for (ProfileMode profileMode : Arrays.asList(ProfileMode.REAL_TIME, ProfileMode.DELAYED_MODE)) {
      for (LocalDate date : expectedDates) {
        ProfileOperation result = metadataStore.findUpdatedOrMissingLatestMergeFiles(
            DefaultRecentProfileSearch.builder()
                .withLimit(limit)
                .withProfileMode(profileMode)
                .withLastUpdatedDateGe(date.atStartOfDay().toInstant(ZoneOffset.UTC))
                .withLastUpdatedDateLt(date.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC))
                .build());
        if (!result.getFiles().isEmpty()) {
          messageSender.sendJson(mergeQueue, jsonMapper.writeValueAsString(ProfileOperation.builder(result).withTraceId(traceId).build()));
        }
      }
    }

    for (String fileToRemove : toRemove) {
      messageSender.sendJson(mergeQueue, jsonMapper.writeValueAsString(ProfileOperation.builder()
          .withTraceId(traceId)
          .withFileName(fileToRemove)
          .build()));
    }
  }
}
