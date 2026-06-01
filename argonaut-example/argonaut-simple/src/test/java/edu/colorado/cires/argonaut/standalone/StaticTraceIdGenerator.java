package edu.colorado.cires.argonaut.standalone;

import java.util.UUID;
import java.util.function.Supplier;

public class StaticTraceIdGenerator implements Supplier<UUID> {

    public static UUID TRACE_ID = UUID.randomUUID();

    @Override
    public UUID get() {
        return TRACE_ID;
    }
}
