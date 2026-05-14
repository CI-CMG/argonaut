package edu.colorado.cires.argonaut.metadata.jpa;

import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord;
import edu.colorado.cires.argonaut.metadata.core.IndexPageRequest;
import edu.colorado.cires.argonaut.metadata.core.MetadataStore;
import edu.colorado.cires.argonaut.metadata.core.ProfilePage;
import jakarta.persistence.EntityManagerFactory;
import java.util.Optional;

public class JpaMetadataStore implements MetadataStore {

  private Updater updater;
  private Remover remover;
  private SyntheticMerger syntheticMerger;
  private Finder finder;

  public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
    updater = new Updater(entityManagerFactory);
    remover = new Remover(entityManagerFactory);
    syntheticMerger = new SyntheticMerger(entityManagerFactory);
    finder = new Finder(entityManagerFactory);
  }

  @Override
  public void updateIndex(MetadataRecord record) {
    switch (record.getAction()) {
      case UPDATE:
        updater.update(record);
        break;
      case REMOVE:
        remover.remove(record);
        break;
      case SYNTHETIC_MERGE:
        syntheticMerger.updateSynthMerge(record);
        break;
      case FLOAT_MERGE:
        throw new UnsupportedOperationException("Float merge not yet implemented");
      case NONE:
      default:
        break;
    }
  }


  @Override
  public Optional<MetadataRecord> findByFile(String file) {
    return finder.findByFile(file, false);
  }

  @Override
  public Optional<MetadataRecord> findByFile(String file, boolean includeRemoved) {
    return finder.findByFile(file, includeRemoved);
  }

//  @Override
//  public MetadataRecordPage findProfilePage(String floatId, String dac, IndexPageRequest pageRequest) {
//    throw new UnsupportedOperationException("Not supported yet.");
//  }
//
  @Override
  public ProfilePage findUpdatedOrMissingMergeFilesPage(IndexPageRequest pageRequest) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public ProfilePage findUpdatedOrMissingSyntheticProfilesPage(IndexPageRequest pageRequest) {
    return finder.findUpdatedOrMissingSyntheticProfilesPage(pageRequest);
  }

}
