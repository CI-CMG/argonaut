package edu.colorado.cires.argonaut;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import org.apache.camel.Exchange;
import org.junit.jupiter.api.Test;

class AomlProcessorTest {

  @Test
  public void smokeTest() throws Exception {
    ServiceProperties serviceProperties = new ServiceProperties();
    serviceProperties.setWorkDirectory("target/work");
    AomlProcessor processor = new AomlProcessor(serviceProperties);
    processor.process(mock(Exchange.class));
  }

}