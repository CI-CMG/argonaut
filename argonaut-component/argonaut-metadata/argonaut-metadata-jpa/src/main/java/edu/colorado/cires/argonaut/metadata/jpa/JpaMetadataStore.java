package edu.colorado.cires.argonaut.metadata.jpa;

import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord;
import edu.colorado.cires.argonaut.metadata.core.IndexPageRequest;
import edu.colorado.cires.argonaut.metadata.core.DefaultMetadataRecordPage;
import edu.colorado.cires.argonaut.metadata.core.MetadataRecordPage;
import edu.colorado.cires.argonaut.metadata.core.MetadataStore;
import edu.colorado.cires.argonaut.metadata.jpa.entity.IndexEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
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
    try (EntityManager em = entityManagerFactory.createEntityManager()) {
      EntityTransaction tx = em.getTransaction();
      tx.begin();
      try {
        em.merge(IndexEntity.fromMetadataRecord(record));
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
  public MetadataRecordPage findPage(IndexPageRequest pageRequest) {
    try (EntityManager em = entityManagerFactory.createEntityManager()) {
      long count = em.createQuery("SELECT count(i.id) FROM IndexEntity i", Long.class).getSingleResult();
      List<IndexEntity> pageResults = em.createQuery("SELECT i FROM IndexEntity i order by i.id asc", IndexEntity.class)
          .setMaxResults(pageRequest.getPageSize())
          .setFirstResult((pageRequest.getPageNumber() - 1) * pageRequest.getPageSize())
          .getResultList();
      return DefaultMetadataRecordPage.builder()
          .withPageNumber(pageRequest.getPageNumber())
          .withPageSize(pageRequest.getPageSize())
          .withTotalRecords(count)
          .withPage(pageResults.stream().map(IndexEntity::toMetadataRecord).collect(Collectors.toList()))
          .build();
    }
  }
}
