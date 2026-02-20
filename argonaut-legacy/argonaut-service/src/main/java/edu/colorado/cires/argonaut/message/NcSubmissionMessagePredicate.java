package edu.colorado.cires.argonaut.message;

import org.apache.camel.Predicate;

public final class NcSubmissionMessagePredicate {


  public static final Predicate IS_VALID = exchange -> exchange.getIn().getBody(NcSubmissionMessage.class).getValidationErrors().isEmpty();

}
