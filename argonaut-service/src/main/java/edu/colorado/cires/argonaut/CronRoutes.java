package edu.colorado.cires.argonaut;

import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CronRoutes extends RouteBuilder {

  private static final Logger LOGGER = LoggerFactory.getLogger(CronRoutes.class);


  private final ServiceProperties serviceProperties;

  public CronRoutes(ServiceProperties serviceProperties) {
    this.serviceProperties = serviceProperties;
  }

  @Override
  public void configure() throws Exception {
    from("cron:tab?schedule=0/1+*+*+*+*+?")
      .process(exchange -> LOGGER.info("Cron route triggered: {}", exchange.getIn().getBody()));
  }
}
