package edu.colorado.cires.argonaut.processor.core;

import edu.colorado.cires.argonaut.messaging.core.databind.ProfileOperation;
import edu.colorado.cires.argonaut.messaging.core.queue.MessageSender;
import edu.colorado.cires.argonaut.metadata.core.DefaultRemovedFileSearch;
import edu.colorado.cires.argonaut.metadata.core.MetadataStore;
import edu.colorado.cires.argonaut.metadata.core.RemovedFilePage;
import edu.colorado.cires.argonaut.metadata.core.RemovedFileSearch;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.databind.json.JsonMapper;

public class DefaultRemovedFileDeleteTrigger implements RemovedFileDeleteTrigger {
  private static final Logger LOGGER = LoggerFactory.getLogger(DefaultRemovedFileDeleteTrigger.class);


  private Supplier<UUID> traceIdGenerator = () -> UUID.randomUUID();
  private Supplier<Instant> nowGenerator = () -> Instant.now();
  private MetadataStore metadataStore;
  private MessageSender messageSender;
  private JsonMapper jsonMapper;
  private String removedFileDeleteQueue;
  private int pageSize = 200;
  private boolean enabled = true;
  private int removedFileRetentionDays;

  public void setRemovedFileRetentionDays(int removedFileRetentionDays) {
    this.removedFileRetentionDays = removedFileRetentionDays;
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

  public void setRemovedFileDeleteQueue(String removedFileDeleteQueue) {
    this.removedFileDeleteQueue = removedFileDeleteQueue;
  }

  public void setPageSize(int pageSize) {
    this.pageSize = pageSize;
  }

  void setNowGenerator(Supplier<Instant> nowGenerator) {
    this.nowGenerator = nowGenerator;
  }

  private void sendMessages(RemovedFilePage page) {
    for (ProfileOperation fmg : page.getPage()) {
      messageSender.sendJson(removedFileDeleteQueue, jsonMapper.writeValueAsString(
          ProfileOperation.builder(fmg)
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

  public void execute() {
    LOGGER.info("Triggered file delete query");
    RemovedFilePage page = metadataStore.findRemovedFilesPage(DefaultRemovedFileSearch.builder()
        .withOlderThan(nowGenerator.get().minus(removedFileRetentionDays, ChronoUnit.DAYS))
        .withPageSize(pageSize)
        .build());
    sendMessages(page);
    Optional<RemovedFileSearch> maybeNextPage = page.getNextPage();
    while (maybeNextPage.isPresent()) {
      page = metadataStore.findRemovedFilesPage(DefaultRemovedFileSearch.builder(maybeNextPage.get()).build());
      sendMessages(page);
      maybeNextPage = page.getNextPage();
    }
  }
}
