package edu.colorado.cires.argonaut;


import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ValidationProcessorTest {
  private static final Path dacDir = Paths.get("target/dac");
  private static final Path workDir = Paths.get("target/work");
  private ObjectMapper objectMapper = new ObjectMapper();

  @BeforeEach
  public void before() throws Exception {
    FileUtils.deleteQuietly(dacDir.toFile());
    Files.createDirectories(dacDir);
    FileUtils.deleteQuietly(workDir.toFile());
    Files.createDirectories(workDir);
  }

  @AfterEach
  public void after() throws Exception {
    FileUtils.deleteQuietly(dacDir.toFile());
    FileUtils.deleteQuietly(workDir.toFile());
  }
  @Test
  public void testParseXml() throws Exception {
    ValidationMessage validationMessage = new ValidationMessage(
        Paths.get("src/test/resources/validation/1901830_meta.nc"),
        Paths.get("src/test/resources/validation/1901830_meta.nc.filecheck")
    );
    ServiceProperties serviceProperties = new ServiceProperties();
    serviceProperties.setWorkDirectory(workDir.toString());
    serviceProperties.setDacDirectory(dacDir.toString());

    Path stagingDir = dacDir.resolve("aoml/staging");
    Files.createDirectories(stagingDir);
    Path processingDir = workDir.resolve("processing").resolve("aoml");
    Files.createDirectories(processingDir);
    Path errorDir = workDir.resolve("error").resolve("aoml");
    String fileName1 = "1901830_meta.nc";
    Path stagedFile = stagingDir.resolve(fileName);
    Path readyFile = stagingDir.resolve("nc_2025.04.02_16.15.tar.gz.ready");
    Files.copy(Paths.get("src/test/resources/aoml").resolve(fileName), stagedFile);

    Files.
//    ProducerTemplate producerTemplate = mock(ProducerTemplate.class);
//    ValidationProcessor processor = new ValidationProcessor(serviceProperties, producerTemplate, objectMapper);
//    Exchange exchange = mock(Exchange.class);
//    Message message = mock(Message.class);
//    when(message.getBody(ValidationMessage.class)).thenReturn(validationMessage);
//    when(exchange.getIn()).thenReturn(message);
//    processor.process(exchange);
  }

}