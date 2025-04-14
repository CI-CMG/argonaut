package edu.colorado.cires.argonaut;


import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.nio.file.Paths;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.junit.jupiter.api.Test;

public class ValidationProcessorTest {

  @Test
  public void testParseXml() throws Exception {
    ValidationMessage validationMessage = new ValidationMessage(
        Paths.get("src/test/resources/validation/1901830_meta.nc"),
        Paths.get("src/test/resources/validation/1901830_meta.nc.filecheck")
    );

    ValidationProcessor processor = new ValidationProcessor();
    Exchange exchange = mock(Exchange.class);
    Message message = mock(Message.class);
    when(message.getBody(ValidationMessage.class)).thenReturn(validationMessage);
    when(exchange.getIn()).thenReturn(message);
    processor.process(exchange);
  }

}