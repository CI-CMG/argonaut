package edu.colorado.cires.argonaut.messaging.cameljpa;

import jakarta.persistence.EntityManager;

class EntityManagerThreadContext {
  private static final ThreadLocal<EntityManager> CURRENT_ENTITY_MANAGER = new ThreadLocal<>();

  public static void setEntityManager(EntityManager entityManager) {
    CURRENT_ENTITY_MANAGER.set(entityManager);
  }

  public static EntityManager getEntityManager() {
    return CURRENT_ENTITY_MANAGER.get();
  }

  public static void clear() {
    CURRENT_ENTITY_MANAGER.remove();
  }

}
