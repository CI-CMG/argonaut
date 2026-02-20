package edu.colorado.cires.argonaut.standalone;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.function.Supplier;

public class StaticTimestampGenerator implements Supplier<Instant> {
    @Override
    public Instant get() {
        return LocalDateTime.of(2026, 2, 20, 1, 2, 3).atZone(ZoneId.of("UTC")).toInstant();
    }
}
