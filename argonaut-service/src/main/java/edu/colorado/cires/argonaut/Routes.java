package edu.colorado.cires.argonaut;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.camel.builder.RouteBuilder;
import org.apache.commons.lang3.stream.Streams;
import org.springframework.stereotype.Component;

@Component
public class Routes extends RouteBuilder {

  private final AomlProcessor aomlProcessor;
  private final ValidationProcessor validationProcessor;

  public Routes(ServiceProperties serviceProperties, AomlProcessor aomlProcessor, ValidationProcessor validationProcessor) {
    this.aomlProcessor = aomlProcessor;
    this.validationProcessor = validationProcessor;
    Streams.of(SupportedDacs.values()).forEach(dac -> {
      Path dir = Paths.get(serviceProperties.getWorkDirectory()).resolve("processing").resolve(dac.name());
      try {
        Files.createDirectories(dir);
      } catch (IOException e) {
        throw new RuntimeException("Unable to create processing directory " + dir, e);
      }
    });
  }

  @Override
  public void configure() throws Exception {
    Streams.of(SupportedDacs.values()).map(SupportedDacs::toString)
            .forEach(dac -> from("file:{{argo.aoml-dac-directory}}/" + dac + "/staging"
                + "?doneFileName=${file:name}.ready"
                + "&moveFailed=../error"
                + "&preMove=../processing"
                + "&move=../done"
                + "&bridgeErrorHandler=true").process(aomlProcessor));

    from("seda:validation").process(validationProcessor);

//    from("file:{{argo.french-gdac-directory}}"
//        + "?doneFileName=${file:name}.ready"
//        + "&moveFailed=../error"
//        + "&preMove=../processing"
//        + "&move=../done"
//        + "&bridgeErrorHandler=true")
//        .process(frenchGdacProcessor);
  }
}
