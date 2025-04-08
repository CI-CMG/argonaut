package edu.colorado.cires.argonaut;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class Routes extends RouteBuilder {

  private final AomlProcessor aomlProcessor;
  private final FrenchGdacProcessor frenchGdacProcessor;
  private final ValidationProcessor validationProcessor;

  public Routes(AomlProcessor aomlProcessor, FrenchGdacProcessor frenchGdacProcessor, ValidationProcessor validationProcessor) {
    this.aomlProcessor = aomlProcessor;
    this.frenchGdacProcessor = frenchGdacProcessor;
    this.validationProcessor = validationProcessor;
  }

  @Override
  public void configure() throws Exception {
    from("file:{{argo.aoml-dac-directory}}/argo/staging"
        + "?doneFileName=${file:name}.ready"
        + "&moveFailed=../error"
        + "&preMove=../processing"
        + "&move=../done"
        + "&bridgeErrorHandler=true")
      .process(aomlProcessor);

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
