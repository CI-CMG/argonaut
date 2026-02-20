package edu.colorado.cires.argonaut.aggregator;

import edu.colorado.cires.argonaut.message.NcSubmissionMessage;
import edu.colorado.cires.argonaut.message.SubmissionCompleteMessage;
import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.apache.camel.Predicate;
import org.springframework.stereotype.Component;

@Component
public class SubmissionCompleteAggregationStrategy implements AggregationStrategy, Predicate {

  @Override
  public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
    NcSubmissionMessage ncSubmissionMessage = newExchange.getIn().getBody(NcSubmissionMessage.class);
    SubmissionCompleteMessage.Builder next;
    if(oldExchange == null) {
      next = SubmissionCompleteMessage.builder()
          .withDac(ncSubmissionMessage.getDac())
          .withNumberOfFiles(ncSubmissionMessage.getNumberOfFilesInSubmission())
          .withTimeStamp(ncSubmissionMessage.getTimestamp());
    } else {
      next = SubmissionCompleteMessage.builder(oldExchange.getIn().getBody(SubmissionCompleteMessage.class));
    }
    next.addCompletedFile(ncSubmissionMessage.getFileName());
    newExchange.getIn().setBody(next.build());
    return newExchange;
  }

  @Override
  public boolean matches(Exchange exchange) {
    SubmissionCompleteMessage message = exchange.getIn().getBody(SubmissionCompleteMessage.class);
    return message.getCompletedFiles().size() == message.getNumberOfFiles();
  }
}
