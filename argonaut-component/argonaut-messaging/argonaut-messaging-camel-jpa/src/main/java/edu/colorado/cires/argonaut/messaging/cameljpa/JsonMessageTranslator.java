package edu.colorado.cires.argonaut.messaging.cameljpa;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

public class JsonMessageTranslator implements Function<String, JsonMessageEntity> {

  private Supplier<UUID> uuidSupplier = () -> UUID.randomUUID();
  private Supplier<Instant> timestampSupplier = () -> Instant.now();

  @Override
  public JsonMessageEntity apply(String message) {
    JsonMessageEntity entity = new JsonMessageEntity();
    entity.setJsonMessage(message);
    entity.setId(uuidSupplier.get());
    entity.setQueueTime(timestampSupplier.get().atOffset(ZoneOffset.UTC).toZonedDateTime());
    return entity;
  }
}
