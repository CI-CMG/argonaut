package edu.colorado.cires.argonaut.processor.core;

import edu.colorado.cires.argonaut.messaging.core.databind.ProfileOperation;
import edu.colorado.cires.argonaut.messaging.core.queue.MessageSender;
import edu.colorado.cires.argonaut.metadata.core.DefaultRecentProfileSearch;
import edu.colorado.cires.argonaut.metadata.core.MetadataStore;
import edu.colorado.cires.argonaut.metadata.core.RecentProfilePage;
import edu.colorado.cires.argonaut.metadata.core.RecentProfileSearch;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.databind.json.JsonMapper;

public class DefaultLatestProfileMergeAggregator implements LatestProfileMergeAggregator {

  private static final Logger LOGGER = LoggerFactory.getLogger(DefaultLatestProfileMergeAggregator.class);


  private Supplier<UUID> traceIdGenerator = () -> UUID.randomUUID();
  private Supplier<Instant> nowGenerator = () -> Instant.now();
  private MetadataStore metadataStore;
  private MessageSender messageSender;
  private JsonMapper jsonMapper;
  private String mergeQueue;
  private int pageSize = 10;
  private boolean enabled = true;
  private int daysBack = 30;

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

  public void setPageSize(int pageSize) {
    this.pageSize = pageSize;
  }

  public void setNowGenerator(Supplier<Instant> nowGenerator) {
    this.nowGenerator = nowGenerator;
  }

  public DefaultLatestProfileMergeAggregator withDaysBack(int daysBack) {
    this.daysBack = daysBack;
    return this;
  }

  private void sendMessages(RecentProfilePage page) {
    for (ProfileOperation fmg : page.getPage()) {
      messageSender.sendJson(mergeQueue, jsonMapper.writeValueAsString(
          ProfileOperation.builder(fmg)
              .withFileName(fmg.getFloatId() + "_prof.nc")
              .withTraceId(traceIdGenerator.get())
              .build()));
    }
  }

  @Override
  public void trigger() {
    if (enabled) {
      execute();
    }
  }

  private Instant getDaysBack() {
    return LocalDate.ofInstant(Instant.now(), ZoneId.of("UTC")).atStartOfDay().minusDays(daysBack).toInstant(ZoneOffset.UTC);
  }

  public void execute() {
    LOGGER.info("Triggered Recent Profile Merge Aggregator");
    RecentProfilePage page = metadataStore.findUpdatedOrMissingLatestMergeFilesPage(
        DefaultRecentProfileSearch.builder()
            .withPageSize(pageSize)
            .withYoungerOrEqual(getDaysBack())
            .build());
    sendMessages(page);
    Optional<RecentProfileSearch> maybeNextPage = page.getNextPage();
    while (maybeNextPage.isPresent()) {
      page = metadataStore.findUpdatedOrMissingLatestMergeFilesPage(DefaultRecentProfileSearch.builder(maybeNextPage.get()).build());
      sendMessages(page);
      maybeNextPage = page.getNextPage();
    }
  }
}
