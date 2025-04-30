package edu.colorado.cires.argonaut;

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

  public Routes(ServiceProperties serviceProperties, SubmissionProcessor submissionProcessor, ValidationProcessor validationProcessor,
      PostValidationProcessor postValidationProcessor, ErrorProcessor errorProcessor) {
    this.submissionProcessor = submissionProcessor;
    this.validationProcessor = validationProcessor;
    this.serviceProperties = serviceProperties;
    this.postValidationProcessor = postValidationProcessor;
    this.errorProcessor = errorProcessor;
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

              from("file:" + dacSubmitDir)
                      .routeId("dac-submit-" + dac.getName())
                      .process(submissionProcessor);
            });

    from("seda:validation").process(validationProcessor);
    from("seda:postvalidation").process(postValidationProcessor);
    from("seda:error").process(errorProcessor);

    from("seda:update-index").process(exchange -> LOGGER.info("seda:update-index: {}", exchange.getIn().getBody()));
    from("seda:gdac-sync").process(exchange -> LOGGER.info("seda:gdac-sync: {}", exchange.getIn().getBody()));
    from("seda:geo-merge").process(exchange -> LOGGER.info("seda:geo-merge: {}", exchange.getIn().getBody()));
    from("seda:remove").process(exchange -> LOGGER.info("seda:remove: {}", exchange.getIn().getBody()));
    from("seda:float-merge").process(exchange -> LOGGER.info("seda:float-merge: {}", exchange.getIn().getBody()));

    from("seda:float-merge-aggregate")
        .aggregate(simple("${body.dac}_${body.floatId}"), AggregationStrategies.useOriginal())
        .completionTimeout(serviceProperties.getFloatMergeQuietTimeout().toString())
        .to("seda:float-merge");

  }
}
