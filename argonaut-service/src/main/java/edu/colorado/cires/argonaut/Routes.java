package edu.colorado.cires.argonaut;

import edu.colorado.cires.argonaut.message.HeaderConsts;
import edu.colorado.cires.argonaut.service.SubmissionTimestampService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.camel.builder.AggregationStrategies;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class Routes extends RouteBuilder {

  private static final Logger LOGGER = LoggerFactory.getLogger(Routes.class);

  private final SubmissionProcessor submissionProcessor;
  private final ValidationProcessor validationProcessor;
  private final PostValidationProcessor postValidationProcessor;
  private final ServiceProperties serviceProperties;
  private final ErrorProcessor errorProcessor;
  private final SubmissionTimestampService submissionTimestampService;

  public Routes(ServiceProperties serviceProperties, SubmissionProcessor submissionProcessor, ValidationProcessor validationProcessor,
      PostValidationProcessor postValidationProcessor, ErrorProcessor errorProcessor, SubmissionTimestampService submissionTimestampService) {
    this.submissionProcessor = submissionProcessor;
    this.validationProcessor = validationProcessor;
    this.serviceProperties = serviceProperties;
    this.postValidationProcessor = postValidationProcessor;
    this.errorProcessor = errorProcessor;
    this.submissionTimestampService = submissionTimestampService;
  }

  @Override
  public void configure() throws Exception {
    serviceProperties.getDacs()
        .forEach(dac -> {
          Path dacSubmitDir = serviceProperties.getSubmissionDirectory()
              .resolve("dac")
              .resolve(dac.getName())
              .resolve("submit");

          try {
            Files.createDirectories(dacSubmitDir);
          } catch (IOException e) {
            throw new RuntimeException("Unable to create submit directory " + dacSubmitDir, e);
          }


          //TODO add logging
          from("file:" + dacSubmitDir)
            .routeId("dac-submit-" + dac.getName())
            .setHeader(HeaderConsts.DAC, constant(dac.getName()))
            .setHeader(HeaderConsts.SUBMISSION_TIMESTAMP, submissionTimestampService::generateTimestamp)
            .choice()
              .when(simple("${header.CamelFileNameOnly.endsWith('.tar.gz')}"))
                .to("seda:submit-data")
              .when(simple("${header.CamelFileNameOnly.endsWith('_greylist.csv')}"))
                .to("seda:submit-greylist")
              .when(simple("${header.CamelFileNameOnly.endsWith('_removal.txt')}"))
                .to("seda:submit-removal")
              .otherwise()
                .to("seda:submit-unknown");
        });

    from("seda:submit-data")
      .process(submissionProcessor);
    from("seda:submit-greylist")
        .process(exchange -> LOGGER.info("seda:submit-greylist: {}", exchange.getIn().getBody()));
    from("seda:submit-removal")
        .process(exchange -> LOGGER.info("seda:submit-removal: {}", exchange.getIn().getBody()));
    from("seda:submit-unknown")
        .process(exchange -> LOGGER.info("seda:submit-unknown: {}", exchange.getIn().getBody()));


    from("seda:validation")
      .process(validationProcessor);
    from("seda:postvalidation")
      .process(postValidationProcessor);
    from("seda:error")
      .process(errorProcessor);

    from("seda:update-index")
      .process(exchange -> LOGGER.info("seda:update-index: {}", exchange.getIn().getBody()));
    from("seda:gdac-sync")
      .process(exchange -> LOGGER.info("seda:gdac-sync: {}", exchange.getIn().getBody()));
    from("seda:geo-merge")
      .process(exchange -> LOGGER.info("seda:geo-merge: {}", exchange.getIn().getBody()));
    from("seda:remove")
      .process(exchange -> LOGGER.info("seda:remove: {}", exchange.getIn().getBody()));
    from("seda:float-merge")
      .process(exchange -> LOGGER.info("seda:float-merge: {}", exchange.getIn().getBody()));

    from("seda:float-merge-aggregate")
      .aggregate(simple("${body.dac}_${body.floatId}"), AggregationStrategies
        .useOriginal())
      .completionTimeout(serviceProperties.getFloatMergeQuietTimeout().toString())
      .to("seda:float-merge");

  }
}
