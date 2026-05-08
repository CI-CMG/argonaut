package edu.colorado.cires.argonaut.metadata.core;

import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord;
import java.util.Optional;

public interface MetadataStore {

  void updateIndex(MetadataRecord record);
  Optional<MetadataRecord> findByFile(String file);
//  MetadataRecordPage findAllPage(IndexPageRequest pageRequest);
  MetadataRecordPage findProfilePage(String floatId, String dac, IndexPageRequest pageRequest);
  FloatMergeGroupPage findUpdatedOrMissingMergeFilesPage(IndexPageRequest pageRequest);
  ProfilePage findUpdatedOrMissingSyntheticProfilesPage(IndexPageRequest pageRequest);

}
