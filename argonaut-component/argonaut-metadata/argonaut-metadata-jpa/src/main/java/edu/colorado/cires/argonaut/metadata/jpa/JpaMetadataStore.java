package edu.colorado.cires.argonaut.metadata.jpa;

import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord;
import edu.colorado.cires.argonaut.metadata.core.GeoMergePage;
import edu.colorado.cires.argonaut.metadata.core.IndexPageRequest;
import edu.colorado.cires.argonaut.metadata.core.MetadataStore;
import edu.colorado.cires.argonaut.metadata.core.ProfilePage;
import jakarta.persistence.EntityManagerFactory;
import java.util.Optional;

public class JpaMetadataStore implements MetadataStore {

  private Updater updater;
  private Remover remover;
  private SyntheticMerger syntheticMerger;
  private MultiFloatMerger multiFloatMerger;
  private Finder finder;

  public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
    updater = new Updater(entityManagerFactory);
    remover = new Remover(entityManagerFactory);
    syntheticMerger = new SyntheticMerger(entityManagerFactory);
    multiFloatMerger = new MultiFloatMerger(entityManagerFactory);
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
        multiFloatMerger.updateMultiFloatMerge(record);
        break;
      case GEO_MERGE:
        multiFloatMerger.updateGeoFloatMerge(record);
        break;
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


  @Override
  public ProfilePage findUpdatedOrMissingMergeFilesPage(IndexPageRequest pageRequest) {
    return finder.findUpdatedOrMissingMergeFilesPage(pageRequest);
  }

  @Override
  public GeoMergePage findUpdatedOrMissingGeoMergeFilesPage(IndexPageRequest pageRequest) {
    return finder.findUpdatedOrMissingGeoMergeFilesPage(pageRequest);
  }

  @Override
  public ProfilePage findUpdatedOrMissingSyntheticProfilesPage(IndexPageRequest pageRequest) {
    return finder.findUpdatedOrMissingSyntheticProfilesPage(pageRequest);
  }

}
