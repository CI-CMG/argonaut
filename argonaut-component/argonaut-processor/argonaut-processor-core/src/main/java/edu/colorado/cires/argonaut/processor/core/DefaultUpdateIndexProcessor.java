package edu.colorado.cires.argonaut.processor.core;

import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord;
import edu.colorado.cires.argonaut.metadata.core.MetadataStore;

public class DefaultUpdateIndexProcessor implements UpdateIndexProcessor {

  private MetadataStore metadataStore;

  public void setMetadataStore(MetadataStore metadataStore) {
    this.metadataStore = metadataStore;
  }

  @Override
  public void updateIndex(MetadataRecord record) {
    switch (record.getAction()) {
      case UPDATE:
        insertUpdate(record);
        break;
      case REMOVE:
        delete(record);
        break;
      case NONE:
      default:
        break;
    }

  }

  private void delete(MetadataRecord record) {
    //TODO
    throw new UnsupportedOperationException("Not supported yet.");
  }

  private void insertUpdate(MetadataRecord record) {
    metadataStore.updateIndex(record);
  }
}
