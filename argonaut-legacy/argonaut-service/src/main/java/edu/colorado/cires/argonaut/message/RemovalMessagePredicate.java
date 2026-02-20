package edu.colorado.cires.argonaut.message;

import org.apache.camel.Predicate;

public final class RemovalMessagePredicate {

  public static final Predicate IS_VALID = exchange -> exchange.getIn().getBody(RemovalMessage.class).getValidationErrors().isEmpty();

}
