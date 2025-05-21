package edu.colorado.cires.argonaut.route;

import edu.colorado.cires.argonaut.aggregator.SubmissionCompleteAggregationStrategy;
import edu.colorado.cires.argonaut.config.ServiceProperties;
import edu.colorado.cires.argonaut.message.HeaderConsts;
import edu.colorado.cires.argonaut.message.NcSubmissionMessagePredicate;
import edu.colorado.cires.argonaut.message.RemovalMessagePredicate;
import edu.colorado.cires.argonaut.processor.FileMoveProcessor;
import edu.colorado.cires.argonaut.processor.FloatMergeProcessor;
import edu.colorado.cires.argonaut.processor.RemovalFileValidator;
import edu.colorado.cires.argonaut.processor.RemovalMessageTranslator;
import edu.colorado.cires.argonaut.processor.SubmissionProcessor;
import edu.colorado.cires.argonaut.processor.SubmissionReportProcessor;
import edu.colorado.cires.argonaut.processor.ValidationProcessor;
import edu.colorado.cires.argonaut.service.SubmissionTimestampService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
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
  private final FloatMergeProcessor floatMergeProcessor;
  private final RemovalFileValidator removalFileValidator;
  private final RemovalMessageTranslator removalMessageTranslator;

  public Routes(ServiceProperties serviceProperties, SubmissionProcessor submissionProcessor, ValidationProcessor validationProcessor,
      SubmissionTimestampService submissionTimestampService,
      FileMoveProcessor fileMoveProcessor, SubmissionReportProcessor submissionReportProcessor,
      SubmissionCompleteAggregationStrategy submissionCompleteAggregationStrategy, FloatMergeProcessor floatMergeProcessor,
      RemovalFileValidator removalFileValidator, RemovalMessageTranslator removalMessageTranslator) {
    this.submissionProcessor = submissionProcessor;
    this.validationProcessor = validationProcessor;
    this.serviceProperties = serviceProperties;
    this.submissionTimestampService = submissionTimestampService;
    this.fileMoveProcessor = fileMoveProcessor;
    this.submissionReportProcessor = submissionReportProcessor;
    this.submissionCompleteAggregationStrategy = submissionCompleteAggregationStrategy;
    this.floatMergeProcessor = floatMergeProcessor;
    this.removalFileValidator = removalFileValidator;
    this.removalMessageTranslator = removalMessageTranslator;
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
          from("file:" + dacSubmitDir + "?readLock=changed")
            .routeId("dac-submit-" + dac.getName())
            .setHeader(HeaderConsts.DAC, constant(dac.getName()))
            .setHeader(HeaderConsts.SUBMISSION_TIMESTAMP, submissionTimestampService::generateTimestamp)
            .choice()
              .when(simple("${header.CamelFileNameOnly.endsWith('.tar.gz')}"))
                .to(QueueConsts.SUBMIT_DATA)
//              .when(simple("${header.CamelFileNameOnly.endsWith('_greylist.csv')}"))
//                .to("seda:submit-greylist")
              .when(simple("${header.CamelFileNameOnly.endsWith('_removal.txt')}"))
                .to(QueueConsts.SUBMIT_REMOVAL)
              .otherwise()
                .to(QueueConsts.SUBMIT_UNKNOWN);       });

    // @formatter:off

    from(QueueConsts.SUBMIT_DATA).process(submissionProcessor).split(body()).to(QueueConsts.VALIDATION);

    from(QueueConsts.VALIDATION + "?concurrentConsumers=" + serviceProperties.getValidationThreads())
      .process(validationProcessor)
      .choice()
        .when(NcSubmissionMessagePredicate.IS_VALID)
          .to(QueueConsts.VALIDATION_SUCCESS)
        .otherwise()
          .to(QueueConsts.FILE_OUTPUT);

    from(QueueConsts.VALIDATION_SUCCESS)
      .multicast().parallelProcessing()
        .to(
            QueueConsts.FILE_OUTPUT,
            QueueConsts.LATEST_MERGE_AGG,
            QueueConsts.FLOAT_MERGE_AGG,
            QueueConsts.GEO_MERGE_AGG
        );

    from(QueueConsts.FILE_OUTPUT).process(fileMoveProcessor).to(QueueConsts.FILE_MOVED);

    from(QueueConsts.FILE_MOVED)
        .multicast().parallelProcessing()
        .to(QueueConsts.SUBMISSION_REPORT, QueueConsts.UPDATE_INDEX_AGG);

    from(QueueConsts.SUBMISSION_REPORT + "?concurrentConsumers=" + serviceProperties.getSubmissionReportThreads())
        .process(submissionReportProcessor)
        .to(QueueConsts.SUBMISSION_COMPLETE_AGG);

    from(QueueConsts.SUBMISSION_COMPLETE_AGG)
        .aggregate(simple("${body.dac}_${body.timestamp}"), submissionCompleteAggregationStrategy)
        .to(QueueConsts.PREPARE_SUBMISSION_EMAIL);

    from(QueueConsts.FLOAT_MERGE_AGG)
        .aggregate(simple("${body.dac}_${body.floatId}"), (oldExchange,newExchange)-> oldExchange == null?newExchange:oldExchange)
        .completionInterval(serviceProperties.getFloatMergeQuietTimeout().toMillis())
        .to(QueueConsts.FLOAT_MERGE);

    from(QueueConsts.FLOAT_MERGE).process(floatMergeProcessor).to(QueueConsts.UPDATE_INDEX_AGG);

    from(QueueConsts.SUBMIT_REMOVAL).process(removalFileValidator)
        .choice()
          .when(RemovalMessagePredicate.IS_VALID)
            .to(QueueConsts.REMOVAL_SPLITTER)
          .otherwise()
            .process(removalMessageTranslator)
            .to(QueueConsts.SUBMISSION_REPORT);

    from(QueueConsts.REMOVAL_SPLITTER).split(simple("${body.removalFiles}")).to(QueueConsts.VALIDATION_SUCCESS);


    // @formatter:on

  }
}
