package edu.colorado.cires.argonaut;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.stream.Streams;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


class SubmissionProcessorTest {

  private static final Path dacDir = Paths.get("target/dac");
  private static final Path workDir = Paths.get("target/work");

  private static final String[] files = new String[]{
      "1901830_meta.nc",
      "1901830_Rtraj.nc",
      "1901830_tech.nc",
      "1901843_Rtraj.nc",
      "1901843_tech.nc",
      "1902195_meta.nc",
      "1902195_Rtraj.nc",
      "1902195_tech.nc",
      "3901276_Rtraj.nc",
      "3901276_tech.nc",
      "3901471_Rtraj.nc",
      "3901471_tech.nc",
      "3901480_Rtraj.nc",
      "3901480_tech.nc",
      "3902534_Rtraj.nc",
      "3902534_tech.nc",
      "4902337_meta.nc",
      "4902337_Rtraj.nc",
      "4902337_tech.nc",
      "4902349_meta.nc",
      "4902349_Rtraj.nc",
      "4902349_tech.nc",
      "4902907_meta.nc",
      "4902907_Rtraj.nc",
      "4902907_tech.nc",
      "4902951_meta.nc",
      "4902951_Rtraj.nc",
      "4902951_tech.nc",
      "4902997_meta.nc",
      "4902997_Rtraj.nc",
      "4902997_tech.nc",
      "4903000_meta.nc",
      "4903000_Rtraj.nc",
      "4903000_tech.nc",
      "4903180_meta.nc",
      "4903180_Rtraj.nc",
      "4903180_tech.nc",
      "5902487_Rtraj.nc",
      "5902487_tech.nc",
      "5902490_Rtraj.nc",
      "5902490_tech.nc",
      "5902499_Rtraj.nc",
      "5902499_tech.nc",
      "5904627_meta.nc",
      "5904627_Rtraj.nc",
      "5904627_tech.nc",
      "5904773_meta.nc",
      "5904773_Rtraj.nc",
      "5904773_tech.nc",
      "5904774_meta.nc",
      "5904774_Rtraj.nc",
      "5904774_tech.nc",
      "5904810_meta.nc",
      "5904810_Rtraj.nc",
      "5904810_tech.nc",
      "5904812_meta.nc",
      "5904812_Rtraj.nc",
      "5904812_tech.nc",
      "5904941_meta.nc",
      "5904941_Rtraj.nc",
      "5904941_tech.nc",
      "5905098_meta.nc",
      "5905098_Rtraj.nc",
      "5905098_tech.nc",
      "5905244_Rtraj.nc",
      "5905244_tech.nc",
      "5905248_Rtraj.nc",
      "5905248_tech.nc",
      "5905289_meta.nc",
      "5905289_Rtraj.nc",
      "5905289_tech.nc",
      "5905315_meta.nc",
      "5905315_Rtraj.nc",
      "5905315_tech.nc",
      "5905316_meta.nc",
      "5905316_Rtraj.nc",
      "5905316_tech.nc",
      "5905669_meta.nc",
      "5905669_Rtraj.nc",
      "5905669_tech.nc",
      "5905670_meta.nc",
      "5905670_Rtraj.nc",
      "5905670_tech.nc",
      "5905746_meta.nc",
      "5905746_Rtraj.nc",
      "5905746_tech.nc",
      "5906936_Rtraj.nc",
      "5906936_tech.nc",
      "5906945_Rtraj.nc",
      "5906945_tech.nc",
      "5906946_Rtraj.nc",
      "5906946_tech.nc",
      "5906947_Rtraj.nc",
      "5906947_tech.nc",
      "5907024_Rtraj.nc",
      "5907024_tech.nc",
      "7902059_meta.nc",
      "7902059_Rtraj.nc",
      "7902059_tech.nc",
      "7902143_meta.nc",
      "7902143_Rtraj.nc",
      "7902143_tech.nc"
  };

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
  public void smokeTest() throws Exception {


    ServiceProperties serviceProperties = new ServiceProperties();
    serviceProperties.setWorkDirectory(workDir.toString());
    serviceProperties.setDacDirectory(dacDir.toString());

    Path stagingDir = dacDir.resolve("aoml/staging");
    Files.createDirectories(stagingDir);
    Path processingDir = workDir.resolve("processing").resolve("aoml");
    Path errorDir = workDir.resolve("error").resolve("aoml");
    String fileName = "nc_2025.04.02_16.15.tar.gz";
    Path stagedFile = stagingDir.resolve(fileName);
    Path readyFile = stagingDir.resolve("nc_2025.04.02_16.15.tar.gz.ready");
    Files.copy(Paths.get("src/test/resources/aoml").resolve(fileName), stagedFile);

    ProducerTemplate producerTemplate = mock(ProducerTemplate.class);

    SubmissionProcessor processor = new SubmissionProcessor(serviceProperties, producerTemplate);
    Exchange exchange = mock(Exchange.class);
    Message message = mock(Message.class);
    when(message.getBody(File.class)).thenReturn(readyFile.toFile());
    when(exchange.getIn()).thenReturn(message);
    processor.process(exchange);

    Set<String> processedFiles = new TreeSet<>();
    try(Stream<Path> stream = Files.list(processingDir)) {
      stream.map(Path::getFileName).map(Path::toString).forEach(processedFiles::add);
    }
    List<ValidationMessage> validationMessages = new ArrayList<>();
    Set<String> expectedFiles = new TreeSet<>();
    Streams.of(files).forEach(name -> {
      expectedFiles.add(name);
      expectedFiles.add(name + ".filecheck");
      validationMessages.add(new ValidationMessage(processingDir.resolve(name), processingDir.resolve(name + ".filecheck")));
    });
    assertEquals(expectedFiles, processedFiles);

    validationMessages.forEach(vm -> verify(producerTemplate).sendBody(eq("seda:validation"), eq(vm)));
  }

}