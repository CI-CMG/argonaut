package edu.colorado.cires.argonaut.metadata.jpa;

import static edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord.FileStatus.ACTIVE;

import edu.colorado.cires.argonaut.messaging.core.databind.FloatMergeGroup;
import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord;
import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord.FileStatus;
import edu.colorado.cires.argonaut.metadata.core.DefaultFloatMergeGroupPage;
import edu.colorado.cires.argonaut.metadata.core.DefaultIndexPageRequest;
import edu.colorado.cires.argonaut.metadata.core.DefaultMetadataRecordPage;
import edu.colorado.cires.argonaut.metadata.core.FloatMergeGroupPage;
import edu.colorado.cires.argonaut.metadata.core.IndexPageRequest;
import edu.colorado.cires.argonaut.metadata.core.MetadataRecordPage;
import edu.colorado.cires.argonaut.metadata.core.MetadataStore;
import edu.colorado.cires.argonaut.metadata.jpa.entity.IndexEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Tuple;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class JpaMetadataStore implements MetadataStore {

  private EntityManagerFactory entityManagerFactory;

  public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
    this.entityManagerFactory = entityManagerFactory;
  }

  @Override
  public void updateIndex(MetadataRecord record) {
    switch (record.getAction()) {
      case UPDATE:
        update(record);
        break;
      case REMOVE:
        remove(record);
        break;
      case FLOAT_MERGE:
        updateFloatMerge(record);
        break;
      case NONE:
      default:
        break;
    }


  }

  private void updateFloatMerge(MetadataRecord record) {
    try (EntityManager em = entityManagerFactory.createEntityManager()) {
      EntityTransaction tx = em.getTransaction();
      tx.begin();
      try {
        IndexEntity existing = em.find(IndexEntity.class, record.getFile());
        if (existing != null) {
          existing.setFloatMerged(record.getFileStatus().equals(ACTIVE));
        }
        tx.commit();
      } catch (Exception e) {
        tx.rollback();
        throw e;
      }
    }
  }

  private void update(MetadataRecord record) {
    try (EntityManager em = entityManagerFactory.createEntityManager()) {
      EntityTransaction tx = em.getTransaction();
      tx.begin();
      try {
        IndexEntity entity = IndexEntity.fromMetadataRecord(record);
        entity.setFileStatus(ACTIVE.name());
        em.merge(entity);
        tx.commit();
      } catch (Exception e) {
        tx.rollback();
        throw e;
      }
    }
  }

  private void remove(MetadataRecord record) {
    try (EntityManager em = entityManagerFactory.createEntityManager()) {
      EntityTransaction tx = em.getTransaction();
      tx.begin();
      try {
        IndexEntity existing = em.find(IndexEntity.class, record.getFile());
        if (existing != null) {
          existing.setFileStatus(FileStatus.REMOVED.name());
        }
        tx.commit();
      } catch (Exception e) {
        tx.rollback();
        throw e;
      }
    }
  }

  @Override
  public Optional<MetadataRecord> findByFile(String file) {
    try (EntityManager em = entityManagerFactory.createEntityManager()) {
      IndexEntity result = em.find(IndexEntity.class, file);
      if (result == null) {
        return Optional.empty();
      }
      return Optional.of(result.toMetadataRecord());
    }
  }

  @Override
  public MetadataRecordPage findProfilePage(String floatId, String dac, IndexPageRequest pageRequest) {
    try (EntityManager em = entityManagerFactory.createEntityManager()) {
      long count = em.createQuery("SELECT count(i.id) FROM IndexEntity i WHERE i.fileType = 'CORE_ARGO_PROFILE' AND i.floatId = :floatId AND i.dac = :dac", Long.class)
          .setParameter("floatId", floatId)
          .setParameter("dac", dac)
          .getSingleResult();
      List<IndexEntity> pageResults = em.createQuery("SELECT i FROM IndexEntity i WHERE i.fileType = 'CORE_ARGO_PROFILE' AND i.floatId = :floatId AND i.dac = :dac order by i.id asc", IndexEntity.class)
          .setParameter("floatId", floatId)
          .setParameter("dac", dac)
          .setMaxResults(pageRequest.getPageSize())
          .setFirstResult((pageRequest.getPageNumber() - 1) * pageRequest.getPageSize())
          .getResultList();
      return DefaultMetadataRecordPage.builder()
          .withIndexPageRequest(DefaultIndexPageRequest.builder(pageRequest).build())
          .withTotalRecords(count)
          .withPage(pageResults.stream().map(IndexEntity::toMetadataRecord).collect(Collectors.toList()))
          .build();
    }
  }

  @Override
  public FloatMergeGroupPage findUpdatedOrMissingMergeFilesPage(IndexPageRequest pageRequest) {
    try (EntityManager em = entityManagerFactory.createEntityManager()) {
      long count = em.createQuery("SELECT COUNT(DISTINCT i.floatId) FROM IndexEntity i WHERE i.fileType = 'CORE_ARGO_PROFILE' AND (i.fileStatus = 'ACTIVE' AND i.floatMerged = false ) OR (i.fileStatus = 'REMOVED' AND i.floatMerged = true)", Long.class)
          .getSingleResult();
      List<Tuple> pageResults = em.createQuery("SELECT DISTINCT i.dac as dac, i.floatId as floatId FROM IndexEntity i WHERE i.fileType = 'CORE_ARGO_PROFILE' AND (i.fileStatus = 'ACTIVE' AND i.floatMerged = false ) OR (i.fileStatus = 'REMOVED' AND i.floatMerged = true) order by i.floatId", Tuple.class)
          .setMaxResults(pageRequest.getPageSize())
          .setFirstResult((pageRequest.getPageNumber() - 1) * pageRequest.getPageSize())
          .getResultList();
      return DefaultFloatMergeGroupPage.builder()
          .withIndexPageRequest(DefaultIndexPageRequest.builder(pageRequest).build())
          .withTotalRecords(count)
          .withPage(pageResults.stream().map(t -> FloatMergeGroup.builder()
              .withDac(t.get("dac", String.class))
              .withFloatId(t.get("floatId", String.class))
              .build()).collect(Collectors.toList()))
          .build();
    }
  }

//  @Override
//  public MetadataRecordPage findAllPage(IndexPageRequest pageRequest) {
//    try (EntityManager em = entityManagerFactory.createEntityManager()) {
//      long count = em.createQuery("SELECT count(i.id) FROM IndexEntity i", Long.class).getSingleResult();
//      List<IndexEntity> pageResults = em.createQuery("SELECT i FROM IndexEntity i order by i.id asc", IndexEntity.class)
//          .setMaxResults(pageRequest.getPageSize())
//          .setFirstResult((pageRequest.getPageNumber() - 1) * pageRequest.getPageSize())
//          .getResultList();
//      return DefaultMetadataRecordPage.builder()
//          .withIndexPageRequest(DefaultIndexPageRequest.builder(pageRequest).build())
//          .withTotalRecords(count)
//          .withPage(pageResults.stream().map(IndexEntity::toMetadataRecord).collect(Collectors.toList()))
//          .build();
//    }
//  }
}
