package edu.colorado.cires.argonaut.route;

import edu.colorado.cires.argonaut.aggregator.SubmissionCompleteAggregationStrategy;
import edu.colorado.cires.argonaut.config.ServiceProperties;
import edu.colorado.cires.argonaut.processor.FileMoveProcessor;
import edu.colorado.cires.argonaut.processor.SubmissionProcessor;
import edu.colorado.cires.argonaut.processor.SubmissionReportProcessor;
import edu.colorado.cires.argonaut.processor.ValidationProcessor;
import edu.colorado.cires.argonaut.message.HeaderConsts;
import edu.colorado.cires.argonaut.message.NcSubmissionMessage;
import edu.colorado.cires.argonaut.service.SubmissionTimestampService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
  private final ServiceProperties serviceProperties;
  private final SubmissionTimestampService submissionTimestampService;
  private final FileMoveProcessor fileMoveProcessor;
  private final SubmissionReportProcessor submissionReportProcessor;
  private final SubmissionCompleteAggregationStrategy submissionCompleteAggregationStrategy;

  public Routes(ServiceProperties serviceProperties, SubmissionProcessor submissionProcessor, ValidationProcessor validationProcessor,
      SubmissionTimestampService submissionTimestampService,
      FileMoveProcessor fileMoveProcessor, SubmissionReportProcessor submissionReportProcessor,
      SubmissionCompleteAggregationStrategy submissionCompleteAggregationStrategy) {
    this.submissionProcessor = submissionProcessor;
    this.validationProcessor = validationProcessor;
    this.serviceProperties = serviceProperties;
    this.submissionTimestampService = submissionTimestampService;
    this.fileMoveProcessor = fileMoveProcessor;
    this.submissionReportProcessor = submissionReportProcessor;
    this.submissionCompleteAggregationStrategy = submissionCompleteAggregationStrategy;
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
                .to(QueueConsts.SUBMIT_DATA)
              .when(simple("${header.CamelFileNameOnly.endsWith('_greylist.csv')}"))
                .to("seda:submit-greylist")
              .when(simple("${header.CamelFileNameOnly.endsWith('_removal.txt')}"))
                .to("seda:submit-removal")
              .otherwise()
                .to("seda:submit-unknown");
        });

    // @formatter:off

    from(QueueConsts.SUBMIT_DATA).process(submissionProcessor).split(body()).to(QueueConsts.VALIDATION);

    from(QueueConsts.VALIDATION + "?concurrentConsumers=" + serviceProperties.getValidationThreads())
      .process(validationProcessor)
      .choice()
        .when(NcSubmissionMessage.IS_VALID)
          .to(QueueConsts.VALIDATION_SUCCESS)
        .otherwise()
          .to(QueueConsts.FILE_OUTPUT);

    from(QueueConsts.VALIDATION_SUCCESS)
      .multicast().parallelProcessing()
        .to(QueueConsts.FILE_OUTPUT);

    from(QueueConsts.FILE_OUTPUT).process(fileMoveProcessor).to(QueueConsts.FILE_MOVED);

    from(QueueConsts.FILE_MOVED)
        .multicast().parallelProcessing()
        .to(QueueConsts.SUBMISSION_REPORT);

    from(QueueConsts.SUBMISSION_REPORT + "?concurrentConsumers=" + serviceProperties.getSubmissionReportThreads())
        .process(submissionReportProcessor)
        .to(QueueConsts.SUBMISSION_COMPLETE_AGG);


    from(QueueConsts.SUBMISSION_COMPLETE_AGG)
        .aggregate(simple("${body.dac}_${body.timestamp}"), submissionCompleteAggregationStrategy)
        .to(QueueConsts.PREPARE_SUBMISSION_EMAIL);


    from("seda:submit-greylist")
      .process(exchange -> LOGGER.info("seda:submit-greylist: {}", exchange.getIn().getBody()));
    from("seda:submit-removal")
      .process(exchange -> LOGGER.info("seda:submit-removal: {}", exchange.getIn().getBody()));
    from("seda:submit-unknown")
      .process(exchange -> LOGGER.info("seda:submit-unknown: {}", exchange.getIn().getBody()));


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

    // @formatter:on

  }
}
