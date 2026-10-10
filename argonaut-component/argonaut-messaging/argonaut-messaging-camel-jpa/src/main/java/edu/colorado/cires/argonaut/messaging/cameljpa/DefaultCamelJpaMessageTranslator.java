package edu.colorado.cires.argonaut.messaging.cameljpa;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;
import java.util.function.Supplier;

public class DefaultCamelJpaMessageTranslator implements CamelJpaMessageTranslator {

  private Supplier<UUID> uuidSupplier = () -> UUID.randomUUID();
  private Supplier<Instant> timestampSupplier = () -> Instant.now();

  @Override
  public Object translate(String queue, String message) {
    ArgonautMessageEntity entity = new ArgonautMessageEntity();
    entity.setJsonMessage(message);
    entity.setId(uuidSupplier.get());
    entity.setQueueTime(timestampSupplier.get().atOffset(ZoneOffset.UTC).toZonedDateTime());
    entity.setQueue(queue);
    return entity;
  }

  @Override
  public String receive(ArgonautMessageEntity entity) {
    return entity.getJsonMessage();
  }
}
