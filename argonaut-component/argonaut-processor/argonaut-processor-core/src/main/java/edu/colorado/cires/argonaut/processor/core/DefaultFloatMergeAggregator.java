package edu.colorado.cires.argonaut.processor.core;

import edu.colorado.cires.argonaut.messaging.core.databind.FloatMergeGroup;
import edu.colorado.cires.argonaut.messaging.core.queue.MessageSender;
import edu.colorado.cires.argonaut.metadata.core.DefaultIndexPageRequest;
import edu.colorado.cires.argonaut.metadata.core.FloatMergeGroupPage;
import edu.colorado.cires.argonaut.metadata.core.IndexPageRequest;
import edu.colorado.cires.argonaut.metadata.core.MetadataStore;
import java.util.Optional;
import tools.jackson.databind.json.JsonMapper;

public class DefaultFloatMergeAggregator implements FloatMergeAggregator {

  private MetadataStore metadataStore;
  private MessageSender messageSender;
  private JsonMapper jsonMapper;
  private String floatMergeQueue;
  private int pageSize = 200;
  private boolean enabled = true;

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

  public void setFloatMergeQueue(String floatMergeQueue) {
    this.floatMergeQueue = floatMergeQueue;
  }

  public void setPageSize(int pageSize) {
    this.pageSize = pageSize;
  }

  private void sendMessages(FloatMergeGroupPage page) {
    for (FloatMergeGroup fmg : page.getPage()) {
      messageSender.sendJson(floatMergeQueue, jsonMapper.writeValueAsString(fmg));
    }
  }

  @Override
  public void trigger() {
    if (enabled) {
      FloatMergeGroupPage page = metadataStore.findUpdatedOrMissingMergeFilesPage(DefaultIndexPageRequest.builder().withPageSize(pageSize).build());
      sendMessages(page);
      Optional<IndexPageRequest> maybeNextPage = page.getNextPage();
      while (maybeNextPage.isPresent()) {
        page = metadataStore.findUpdatedOrMissingMergeFilesPage(DefaultIndexPageRequest.builder(maybeNextPage.get()).build());
        sendMessages(page);
        maybeNextPage = page.getNextPage();
      }
    }
  }
}
