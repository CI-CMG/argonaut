package edu.colorado.cires.argonaut.processor.core;

import edu.colorado.cires.argonaut.messaging.core.databind.GeoMergeInfo;
import edu.colorado.cires.argonaut.messaging.core.queue.MessageSender;
import edu.colorado.cires.argonaut.metadata.core.DefaultIndexPageRequest;
import edu.colorado.cires.argonaut.metadata.core.GeoMergePage;
import edu.colorado.cires.argonaut.metadata.core.IndexPageRequest;
import edu.colorado.cires.argonaut.metadata.core.MetadataStore;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.databind.json.JsonMapper;

public class DefaultGeoMergeAggregator implements FloatMergeAggregator {

  private static final Logger LOGGER = LoggerFactory.getLogger(DefaultGeoMergeAggregator.class);


  private Supplier<UUID> traceIdGenerator = () -> UUID.randomUUID();
  private MetadataStore metadataStore;
  private MessageSender messageSender;
  private JsonMapper jsonMapper;
  private String geoMergeQueue;
  private int pageSize = 200;
  private boolean enabled = true;

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

  public void setGeoMergeQueue(String geoMergeQueue) {
    this.geoMergeQueue = geoMergeQueue;
  }

  public void setPageSize(int pageSize) {
    this.pageSize = pageSize;
  }

  private void sendMessages(GeoMergePage page) {
    for (GeoMergeInfo fmg : page.getPage()) {
      messageSender.sendJson(geoMergeQueue, jsonMapper.writeValueAsString(
          GeoMergeInfo.builder(fmg)
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
    LOGGER.info("Triggered Geo Merge Aggregator");
    GeoMergePage page = metadataStore.findUpdatedOrMissingGeoMergeFilesPage(DefaultIndexPageRequest.builder().withPageSize(pageSize).build());
    sendMessages(page);
    Optional<IndexPageRequest> maybeNextPage = page.getNextPage();
    while (maybeNextPage.isPresent()) {
      page = metadataStore.findUpdatedOrMissingGeoMergeFilesPage(DefaultIndexPageRequest.builder(maybeNextPage.get()).build());
      sendMessages(page);
      maybeNextPage = page.getNextPage();
    }
  }
}
