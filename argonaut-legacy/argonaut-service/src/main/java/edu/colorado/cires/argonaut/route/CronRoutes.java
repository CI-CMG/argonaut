package edu.colorado.cires.argonaut.route;

import edu.colorado.cires.argonaut.processor.FloatMergeAggregatorProcessor;
import edu.colorado.cires.argonaut.processor.SerializeMessage;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CronRoutes extends RouteBuilder {

  private static final Logger LOGGER = LoggerFactory.getLogger(CronRoutes.class);

  private final FloatMergeAggregatorProcessor floatMergeAggregatorProcessor;
  private final SerializeMessage serializeMessage;

  @Autowired
  public CronRoutes(FloatMergeAggregatorProcessor floatMergeAggregatorProcessor, SerializeMessage serializeMessage) {
    this.floatMergeAggregatorProcessor = floatMergeAggregatorProcessor;
    this.serializeMessage = serializeMessage;
  }

  @Override
  public void configure() throws Exception {
    // @formatter:off

    from("cron:index?schedule=RAW({{argonaut.index-cron}})")
      .routeId("index-cron")
      .process(exchange -> LOGGER.info("index-cron triggered: {}", exchange.getIn().getBody()));

    from("cron:merge-float?schedule=RAW({{argonaut.merge-float-cron}})")
      .routeId("merge-float-cron")
      .process(floatMergeAggregatorProcessor)
      .split(body())
      .process(serializeMessage)
      .to(QueueConsts.FLOAT_MERGE);

    // @formatter:on
  }
}
