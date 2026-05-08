package edu.colorado.cires.argonaut.processor.core;

import edu.colorado.cires.argonaut.file.core.FileStore;
import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord;
import edu.colorado.cires.argonaut.messaging.core.databind.ProfileOperation;
import edu.colorado.cires.argonaut.messaging.core.queue.MessageSender;
import edu.colorado.cires.argonaut.metadata.core.DefaultIndexPageRequest;
import edu.colorado.cires.argonaut.metadata.core.IndexPageRequest;
import edu.colorado.cires.argonaut.metadata.core.MetadataRecordPage;
import edu.colorado.cires.argonaut.metadata.core.MetadataStore;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import tools.jackson.databind.json.JsonMapper;

public class DefaultSyntheticProfileProcessor implements SyntheticProfileProcessor {

  private FileStore outputFileStore;
  private Path localTempDir;
  private MetadataStore metadataStore;
  private MessageSender messageSender;
  private String updateIndexQueue;
  private JsonMapper jsonMapper;

  public void setJsonMapper(JsonMapper jsonMapper) {
    this.jsonMapper = jsonMapper;
  }

  public void setUpdateIndexQueue(String updateIndexQueue) {
    this.updateIndexQueue = updateIndexQueue;
  }

  public void setOutputFileStore(FileStore outputFileStore) {
    this.outputFileStore = outputFileStore;
  }

  public void setLocalTempDir(Path localTempDir) {
    this.localTempDir = localTempDir;
    try {
      Files.createDirectories(localTempDir);
    } catch (IOException e) {
      throw new RuntimeException("Unable to create temp directory: " + localTempDir, e);
    }
  }

  public void setMetadataStore(MetadataStore metadataStore) {
    this.metadataStore = metadataStore;
  }

  public void setMessageSender(MessageSender messageSender) {
    this.messageSender = messageSender;
  }

  private List<String> getFiles(String dac, String floatId) {
    List<String> files = new LinkedList<>();
    MetadataRecordPage page = metadataStore.findProfilePage(floatId, dac, DefaultIndexPageRequest.builder().build());
    files.addAll(page.getPage().stream().map(MetadataRecord::getFile).toList());
    Optional<IndexPageRequest> maybeNextPage = page.getNextPage();
    while (maybeNextPage.isPresent()) {
      page = metadataStore.findProfilePage(floatId, dac, maybeNextPage.get());
      files.addAll(page.getPage().stream().map(MetadataRecord::getFile).toList());
      maybeNextPage = page.getNextPage();
    }
    return files;
  }

  @Override
  public void generateSyntheticProfile(ProfileOperation message) {

  }
}
