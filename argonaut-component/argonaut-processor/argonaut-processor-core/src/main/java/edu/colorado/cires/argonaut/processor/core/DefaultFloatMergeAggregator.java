package edu.colorado.cires.argonaut.processor.core;

import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord;
import edu.colorado.cires.argonaut.messaging.core.queue.MessageSender;
import edu.colorado.cires.argonaut.metadata.core.DefaultIndexPageRequest;
import edu.colorado.cires.argonaut.metadata.core.FloatMergeGroupPage;
import edu.colorado.cires.argonaut.metadata.core.IndexPageRequest;
import edu.colorado.cires.argonaut.metadata.core.MetadataRecordPage;
import edu.colorado.cires.argonaut.metadata.core.MetadataStore;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import tools.jackson.databind.json.JsonMapper;

public class DefaultFloatMergeAggregator implements FloatMergeAggregator {

  private MetadataStore metadataStore;
  private MessageSender messageSender;
  private JsonMapper jsonMapper;

  public void setMetadataStore(MetadataStore metadataStore) {
    this.metadataStore = metadataStore;
  }

  public void setMessageSender(MessageSender messageSender) {
    this.messageSender = messageSender;
  }

  public void setJsonMapper(JsonMapper jsonMapper) {
    this.jsonMapper = jsonMapper;
  }

  private void sendMessages(FloatMergeGroupPage page) {

  }

  @Override
  public void trigger() {
    Instant now = Instant.now();
    FloatMergeGroupPage page = metadataStore.findUpdatedOrMissingMergeFilesPage(DefaultIndexPageRequest.builder().build());
    sendMessages(page);
    Optional<IndexPageRequest> maybeNextPage = page.getNextPage();
    while (maybeNextPage.isPresent()) {
      page = metadataStore.findUpdatedOrMissingMergeFilesPage(maybeNextPage.get());
      sendMessages(page);
      maybeNextPage = page.getNextPage();
    }
  }
}
