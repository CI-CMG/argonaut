package edu.colorado.cires.argonaut;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.MockEndpointsAndSkip;
import org.apache.commons.lang3.stream.Streams;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@CamelSpringBootTest
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
//@UseAdviceWith
@MockEndpointsAndSkip("seda:.*")
public class SubmissionTest {

  static {
    System.setProperty("camel.threads.virtual.enabled", "true");
  }


  @Autowired
  private ServiceProperties serviceProperties;

  @Test
  public void testSubmissionHappyPath() throws Exception {
    Path submissionDir = serviceProperties.getSubmissionDirectory();
    Path aomlDir = submissionDir.resolve("aoml");
    Path submitDir = aomlDir.resolve("submit");
    Path submitProcessingDir = aomlDir.resolve("processing");
    Path submitProcessedDir = aomlDir.resolve("processed");

    Path processingDir = serviceProperties.getProcessingDirectory();


    String fileName = "nc_2025.04.02_16.15.tar.gz";
    Path stagedFile = stagingDir.resolve(fileName);
    Path readyFile = stagingDir.resolve("nc_2025.04.02_16.15.tar.gz.ready");


    
    Files.copy(Paths.get("src/test/resources/aoml").resolve(fileName), stagedFile);



    validation.expectedMessageCount(102);
    validation.setAssertPeriod(2000);
    error.expectedMessageCount(0);
    error.setAssertPeriod(2000);
    Files.write(readyFile, new byte[0]);

    MockEndpoint.assertIsSatisfied(validation,error);
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
  }


}
