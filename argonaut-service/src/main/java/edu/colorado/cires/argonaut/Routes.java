package edu.colorado.cires.argonaut;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class Routes extends RouteBuilder {

  private static final Logger LOGGER = LoggerFactory.getLogger(Routes.class);

  private final AomlProcessor aomlProcessor;
  private final PostValidationProcessor postValidationProcessor;
  private final ServiceProperties serviceProperties;
  private final ErrorProcessor errorProcessor;

  public Routes(ServiceProperties serviceProperties, AomlProcessor aomlProcessor, PostValidationProcessor postValidationProcessor,
      ErrorProcessor errorProcessor) {
    this.aomlProcessor = aomlProcessor;
    this.postValidationProcessor = postValidationProcessor;
    this.serviceProperties = serviceProperties;
    this.errorProcessor = errorProcessor;
    serviceProperties.getDacs().forEach(dac -> {
      Path dir = Paths.get(serviceProperties.getWorkDirectory()).resolve("processing").resolve(dac);
      try {
        Files.createDirectories(dir);
      } catch (IOException e) {
        throw new RuntimeException("Unable to create processing directory " + dir, e);
      }
    });
  }

  @Override
  public void configure() throws Exception {
    serviceProperties.getDacs()
            .forEach(dac -> from("file:{{argonaut.dac-directory}}/" + dac + "/staging"
                + "?doneFileName=${file:name}.ready"
                + "&moveFailed=../error"
                + "&preMove=../processing"
                + "&move=../done"
                + "&bridgeErrorHandler=true").process(aomlProcessor));

    from("seda:validation").process(postValidationProcessor);
    from("seda:error").process(errorProcessor);

    from("seda:update-index").process(exchange -> LOGGER.info("seda:update-index: {}", exchange.getIn().getBody()));
    from("seda:gdac-sync").process(exchange -> LOGGER.info("seda:gdac-sync: {}", exchange.getIn().getBody()));
    from("seda:geo-merge").process(exchange -> LOGGER.info("seda:geo-merge: {}", exchange.getIn().getBody()));
    from("seda:prof-merge").process(exchange -> LOGGER.info("seda:prof-merge: {}", exchange.getIn().getBody()));
  }
}
