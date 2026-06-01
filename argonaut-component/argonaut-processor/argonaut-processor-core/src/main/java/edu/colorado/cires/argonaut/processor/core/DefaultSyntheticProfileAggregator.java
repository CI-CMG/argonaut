package edu.colorado.cires.argonaut.processor.core;

import edu.colorado.cires.argonaut.file.core.FileStore;
import edu.colorado.cires.argonaut.messaging.core.databind.ProfileOperation;
import edu.colorado.cires.argonaut.messaging.core.queue.MessageSender;
import edu.colorado.cires.argonaut.metadata.core.DefaultIndexPageRequest;
import edu.colorado.cires.argonaut.metadata.core.IndexPageRequest;
import edu.colorado.cires.argonaut.metadata.core.MetadataStore;
import edu.colorado.cires.argonaut.metadata.core.ProfilePage;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import tools.jackson.databind.json.JsonMapper;

public class DefaultSyntheticProfileAggregator implements SyntheticProfileAggregator {

  private Supplier<UUID> traceIdGenerator = () -> UUID.randomUUID();
  private MetadataStore metadataStore;
  private MessageSender messageSender;
  private JsonMapper jsonMapper;
  private String syntheticProfileQueue;
  private int pageSize = 200;
  private boolean enabled = true;
  private FileStore outputFileStore;

  public void setTraceIdGenerator(Supplier<UUID> traceIdGenerator) {
    this.traceIdGenerator = traceIdGenerator;
  }

  public void setOutputFileStore(FileStore outputFileStore) {
    this.outputFileStore = outputFileStore;
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

  public void setSyntheticProfileQueue(String syntheticProfileQueue) {
    this.syntheticProfileQueue = syntheticProfileQueue;
  }

  public void setPageSize(int pageSize) {
    this.pageSize = pageSize;
  }

  private void sendMessages(ProfilePage page) {
    for (ProfileOperation profile : page.getPage()) {
      messageSender.sendJson(syntheticProfileQueue, jsonMapper.writeValueAsString(ProfileOperation.builder(profile)
              .withTraceId(traceIdGenerator.get())
              .withFileName(getFileName(profile))
          .build()));
    }
  }

  private String getFileName(ProfileOperation profile) {
    return profile.getFiles().stream()
        .map(outputFileStore::getFileName)
        .filter(f -> f.startsWith("B"))
        .map(f -> f.replace("B", "S"))
        .findFirst()
        .orElse("S" + profile.getFloatId() + ".nc");
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
