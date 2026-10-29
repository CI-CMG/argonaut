package edu.colorado.cires.argonaut.processor.core;

import edu.colorado.cires.argonaut.file.core.FileStore;
import edu.colorado.cires.argonaut.messaging.core.databind.ArgoFileType;
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
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;
import java.util.stream.Collectors;
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
  private final ReentrantLock lock = new ReentrantLock();

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
      if (lock.tryLock()) {
        try {
          execute();
        } finally {
          lock.unlock();
        }
      } else {
        LOGGER.info("Latest profile merge trigger already in progress, skipping.");
      }
    }
  }


  private List<LocalDate> getExpectedDates() {
    LocalDate today = ZonedDateTime.ofInstant(nowGenerator.get(), ZoneId.of("UTC")).toLocalDate();
    List<LocalDate> days = new ArrayList<>(daysBack + 1);
    days.add(today);
    for (int i = 1; i < daysBack + 1; i++) {
      days.add(today.minusDays(i));
    }
    return days;

  }

  private void requestRemoval(UUID traceId, String removalFileName) {
    messageSender.sendJson(mergeQueue, jsonMapper.writeValueAsString(
        ProfileOperation.builder()
        .withTraceId(traceId)
        .withFileName(removalFileName)
        .build()));
  }

  private void requestUpdate(UUID traceId, String datePart) {
    LocalDate date = LocalDate.parse(datePart, FORMATTER);
    for (ProfileMode profileMode : Arrays.asList(ProfileMode.DELAYED_MODE, ProfileMode.REAL_TIME)) {
      ProfileOperation result = metadataStore.findUpdatedOrMissingLatestMergeFiles(
          DefaultRecentProfileSearch.builder()
              .withLimit(limit)
              .withProfileMode(profileMode)
              .withLastUpdatedDateGe(date.atStartOfDay().toInstant(ZoneOffset.UTC))
              .withLastUpdatedDateLt(date.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC))
              .build());
      if (!result.getFiles().isEmpty()) {
        LOGGER.info("Triggering recent merge {}", result.getFileName());
        messageSender.sendJson(mergeQueue, jsonMapper.writeValueAsString(
            ProfileOperation.builder(result)
                .withTraceId(traceId)
                .withFileName(profileMode.getFilePrefix() + datePart)
                .build()));
      }
    }
  }

  private static String filePrefix(String fileName) {
    return fileName.substring(0, 9);
  }

  private static String fileDatePart(String prefixOrFileName) {
    return prefixOrFileName.substring(1, 9);
  }

  private void execute() {
    LOGGER.info("Triggered Recent Profile Merge Aggregator");
    String outputDirectory = outputFileStore.appendToPath(outputFileStore.getRoot(), "latest_data");



    Set<String> expectedFileDateParts =  getExpectedDates().stream().map(FORMATTER::format).collect(Collectors.toSet());
    List<String> existingFiles = outputFileStore.listFileNamesInDirectory(outputDirectory).stream()
        .filter(fileName -> ArgoFileType.getFileNameDetails(fileName).getType() == ArgoFileType.LATEST_PROFILE_MERGE)
        .toList();
    Set<String> removalPrefixes = existingFiles.stream()
        .map(DefaultLatestProfileMergeTrigger::filePrefix)
        .filter(prefix -> !expectedFileDateParts.contains(fileDatePart(prefix)))
        .collect(Collectors.toSet());

    UUID traceId = this.traceIdGenerator.get();

    removalPrefixes.forEach(prefixToRemove -> requestRemoval(traceId, prefixToRemove));
    expectedFileDateParts.forEach(datePart -> requestUpdate(traceId, datePart));


  }
}
