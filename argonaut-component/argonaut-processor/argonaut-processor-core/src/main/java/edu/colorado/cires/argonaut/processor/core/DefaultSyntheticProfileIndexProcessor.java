package edu.colorado.cires.argonaut.processor.core;

import edu.colorado.cires.argonaut.metadata.core.IndexPageRequest;
import edu.colorado.cires.argonaut.metadata.core.MetadataRecordPage;
import edu.colorado.cires.argonaut.metadata.core.MetadataStore;

public class DefaultSyntheticProfileIndexProcessor extends BaseIndexProcessor {

  public DefaultSyntheticProfileIndexProcessor() {
    super(
        "Synthetic-Profile directory file of the Argo Global Data Assembly Center",
        "The directory file describes all individual synthetic-profile files of the argo GDAC data store.",
        "argo_synthetic-profile_index");
  }

  @Override
  protected MetadataRecordPage queryPage(MetadataStore metadataStore, IndexPageRequest indexPageRequest) {
    return metadataStore.getSyntheticProfileIndexPage(indexPageRequest);
  }

}
