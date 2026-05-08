package edu.colorado.cires.argonaut.processor.core;

import edu.colorado.cires.argonaut.messaging.core.databind.ProfileOperation;
import edu.colorado.cires.argonaut.messaging.core.queue.MessageSender;
import edu.colorado.cires.argonaut.metadata.core.DefaultIndexPageRequest;
import edu.colorado.cires.argonaut.metadata.core.IndexPageRequest;
import edu.colorado.cires.argonaut.metadata.core.MetadataStore;
import edu.colorado.cires.argonaut.metadata.core.ProfilePage;
import java.util.Optional;
import tools.jackson.databind.json.JsonMapper;

public class DefaultSyntheticProfileAggregator implements SyntheticProfileAggregator {

  private MetadataStore metadataStore;
  private MessageSender messageSender;
  private JsonMapper jsonMapper;
  private String syntheticProfileQueue;
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

  public void setSyntheticProfileQueue(String syntheticProfileQueue) {
    this.syntheticProfileQueue = syntheticProfileQueue;
  }

  public void setPageSize(int pageSize) {
    this.pageSize = pageSize;
  }

  private void sendMessages(ProfilePage page) {
    for (ProfileOperation profile : page.getPage()) {
      messageSender.sendJson(syntheticProfileQueue, jsonMapper.writeValueAsString(profile));
    }
  }

  @Override
  public void trigger() {
    if (enabled) {
      ProfilePage page = metadataStore.findUpdatedOrMissingSyntheticProfilesPage(DefaultIndexPageRequest.builder().withPageSize(pageSize).build());
      sendMessages(page);
      Optional<IndexPageRequest> maybeNextPage = page.getNextPage();
      while (maybeNextPage.isPresent()) {
        page = metadataStore.findUpdatedOrMissingSyntheticProfilesPage(DefaultIndexPageRequest.builder(maybeNextPage.get()).build());
        sendMessages(page);
        maybeNextPage = page.getNextPage();
      }
    }
  }
}
