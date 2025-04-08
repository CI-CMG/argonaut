package edu.colorado.cires.argonaut;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AomlProcessorTest {

  private static final Path dacDir = Paths.get("target/dac");

  @BeforeEach
  public void before() throws Exception {
    FileUtils.deleteQuietly(dacDir.toFile());
    Files.createDirectories(dacDir);
  }

  @AfterEach
  public void after() throws Exception {
//    FileUtils.deleteQuietly(dacDir.toFile());
  }

  @Test
  public void smokeTest() throws Exception {

    ServiceProperties serviceProperties = new ServiceProperties();
    serviceProperties.setWorkDirectory("target/work");
    serviceProperties.setDacDirectory(dacDir.toString());

    Path stagingDir = dacDir.resolve("aoml/staging");
    Files.createDirectories(stagingDir);
    String fileName = "nc_2025.04.02_16.15.tar.gz";
    Path stagedFile = stagingDir.resolve(fileName);
    Path readyFile = stagingDir.resolve("nc_2025.04.02_16.15.tar.gz.ready");
    Files.copy(Paths.get("src/test/resources/aoml").resolve(fileName), stagedFile);

    AomlProcessor processor = new AomlProcessor(serviceProperties);
    Exchange exchange = mock(Exchange.class);
    Message message = mock(Message.class);
    when(message.getBody(File.class)).thenReturn(readyFile.toFile());
    when(exchange.getIn()).thenReturn(message);
    processor.process(exchange);


  }

}