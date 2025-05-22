package edu.colorado.cires.argonaut.route;

import edu.colorado.cires.argonaut.aggregator.SubmissionCompleteAggregationStrategy;
import edu.colorado.cires.argonaut.config.ServiceProperties;
import edu.colorado.cires.argonaut.message.HeaderConsts;
import edu.colorado.cires.argonaut.message.NcSubmissionMessagePredicate;
import edu.colorado.cires.argonaut.message.RemovalMessagePredicate;
import edu.colorado.cires.argonaut.processor.DeserializeNcSubmissionMessage;
import edu.colorado.cires.argonaut.processor.DeserializeRemovalMessage;
import edu.colorado.cires.argonaut.processor.FileChangedPersistenceProcessor;
import edu.colorado.cires.argonaut.processor.FileMoveProcessor;
import edu.colorado.cires.argonaut.processor.FloatMergeProcessor;
import edu.colorado.cires.argonaut.processor.RemovalFileValidator;
import edu.colorado.cires.argonaut.processor.RemovalMessageTranslator;
import edu.colorado.cires.argonaut.processor.SerializeMessage;
import edu.colorado.cires.argonaut.processor.SubmissionReportProcessor;
import edu.colorado.cires.argonaut.processor.ValidationProcessor;
import edu.colorado.cires.argonaut.service.SubmissionTimestampService;
import edu.colorado.cires.argonaut.submission.SubmissionProcessor;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
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
  private final DeserializeNcSubmissionMessage deserializeNcSubmissionMessage;
  private final SerializeMessage serializeMessage;
  private final DeserializeRemovalMessage deserializeRemovalMessage;
  private final FileChangedPersistenceProcessor fileChangedPersistenceProcessor;

  public Routes(ServiceProperties serviceProperties, SubmissionProcessor submissionProcessor, ValidationProcessor validationProcessor,
      SubmissionTimestampService submissionTimestampService,
      FileMoveProcessor fileMoveProcessor, SubmissionReportProcessor submissionReportProcessor,
      SubmissionCompleteAggregationStrategy submissionCompleteAggregationStrategy, FloatMergeProcessor floatMergeProcessor,
      RemovalFileValidator removalFileValidator, RemovalMessageTranslator removalMessageTranslator,
      DeserializeNcSubmissionMessage deserializeNcSubmissionMessage, SerializeMessage serializeMessage,
      DeserializeRemovalMessage deserializeRemovalMessage, FileChangedPersistenceProcessor fileChangedPersistenceProcessor) {
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
    this.deserializeNcSubmissionMessage = deserializeNcSubmissionMessage;
    this.serializeMessage = serializeMessage;
    this.deserializeRemovalMessage = deserializeRemovalMessage;
    this.fileChangedPersistenceProcessor = fileChangedPersistenceProcessor;
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

    from(QueueConsts.SUBMIT_DATA)
        .process(new Processor() {@Override public void process(Exchange exchange)throws Exception {
        exchange.getIn().setBody(exchange.getIn().getBody(File.class).toPath());
  }})
//        .transform(simple("${body.toPath()}"))
        .bean("submissionProcessor", "untarAndMoveToProcessing(${header." + HeaderConsts.DAC +"}, ${header."+ HeaderConsts.SUBMISSION_TIMESTAMP + "}, ${body})")
        .split(body())
        .process(serializeMessage)
        .to(QueueConsts.VALIDATION);

    from(QueueConsts.VALIDATION + "?concurrentConsumers=" + serviceProperties.getValidationThreads())
      .process(deserializeNcSubmissionMessage)
      .process(validationProcessor)
      .choice()
        .when(NcSubmissionMessagePredicate.IS_VALID)
          .process(serializeMessage)
          .to(QueueConsts.VALIDATION_SUCCESS)
        .otherwise()
          .process(serializeMessage)
          .to(QueueConsts.FILE_OUTPUT);

    from(QueueConsts.VALIDATION_SUCCESS)
      .multicast().parallelProcessing()
        .to(
            QueueConsts.FILE_OUTPUT,
            QueueConsts.LATEST_MERGE_AGG,
            QueueConsts.GEO_MERGE_AGG
        );

    from(QueueConsts.FILE_OUTPUT)
        .process(deserializeNcSubmissionMessage)
        .process(fileMoveProcessor)
        .process(serializeMessage)
        .to(QueueConsts.FILE_MOVED);

    from(QueueConsts.FILE_MOVED)
        .multicast().parallelProcessing()
        .to(QueueConsts.SUBMISSION_REPORT, QueueConsts.UPDATE_INDEX);

    from(QueueConsts.SUBMISSION_REPORT + "?concurrentConsumers=" + serviceProperties.getSubmissionReportThreads())
        .process(deserializeNcSubmissionMessage)
        .process(submissionReportProcessor)
        .process(serializeMessage)
        .to(QueueConsts.SUBMISSION_COMPLETE_AGG);

    from(QueueConsts.SUBMISSION_COMPLETE_AGG)
        .process(deserializeNcSubmissionMessage)
        .aggregate(simple("${body.dac}_${body.timestamp}"), submissionCompleteAggregationStrategy)
        .process(serializeMessage)
        .to(QueueConsts.PREPARE_SUBMISSION_EMAIL);

    from(QueueConsts.FLOAT_MERGE)
        .process(deserializeNcSubmissionMessage)
        .process(floatMergeProcessor)
        .process(serializeMessage)
        .to(QueueConsts.UPDATE_INDEX);

    from(QueueConsts.SUBMIT_REMOVAL)
        .process(removalFileValidator)
        .choice()
          .when(RemovalMessagePredicate.IS_VALID)
            .process(serializeMessage)
            .to(QueueConsts.REMOVAL_SPLITTER)
          .otherwise()
            .process(removalMessageTranslator)
            .process(serializeMessage)
            .to(QueueConsts.SUBMISSION_REPORT);

    from(QueueConsts.REMOVAL_SPLITTER)
        .process(deserializeRemovalMessage)
        .split(simple("${body.removalFiles}"))
        .process(serializeMessage)
        .to(QueueConsts.VALIDATION_SUCCESS);

    from(QueueConsts.UPDATE_INDEX)
        .process(deserializeNcSubmissionMessage)
        .process(fileChangedPersistenceProcessor);

    // @formatter:on

  }
}
