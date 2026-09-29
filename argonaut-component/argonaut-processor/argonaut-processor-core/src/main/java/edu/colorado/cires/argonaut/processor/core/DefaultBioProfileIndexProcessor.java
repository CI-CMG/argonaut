package edu.colorado.cires.argonaut.processor.core;

import edu.colorado.cires.argonaut.metadata.core.IndexPageRequest;
import edu.colorado.cires.argonaut.metadata.core.MetadataRecordPage;
import edu.colorado.cires.argonaut.metadata.core.MetadataStore;

public class DefaultBioProfileIndexProcessor extends BaseIndexProcessor {

  public DefaultBioProfileIndexProcessor() {
    super(
        "Bio-Profile directory file of the Argo Global Data Assembly Center",
        "The directory file describes all individual bio-profile files of the ARGO GDAC data store.",
        "argo_bio-profile_index");
  }

  @Override
  protected MetadataRecordPage queryPage(MetadataStore metadataStore, IndexPageRequest indexPageRequest) {
    return metadataStore.getBioProfileIndexPage(indexPageRequest);
  }

}
