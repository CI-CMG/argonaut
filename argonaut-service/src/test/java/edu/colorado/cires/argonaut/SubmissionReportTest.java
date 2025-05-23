package edu.colorado.cires.argonaut;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.colorado.cires.argonaut.config.ServiceProperties;
import edu.colorado.cires.argonaut.message.NcSubmissionMessage;
import edu.colorado.cires.argonaut.message.NcSubmissionMessage.Operation;
import edu.colorado.cires.argonaut.route.QueueConsts;
import edu.colorado.cires.argonaut.util.ArgonautFileUtils;
import java.io.FileReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.MockEndpointsAndSkip;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
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
@MockEndpointsAndSkip(QueueConsts.SUBMISSION_COMPLETE_AGG + "|" + QueueConsts.UPDATE_INDEX)
public class SubmissionReportTest {

  static {
    System.setProperty("camel.threads.virtual.enabled", "true");
  }

  @EndpointInject("mock:" + QueueConsts.SUBMISSION_COMPLETE_AGG)
  private MockEndpoint submissionCompleteAgg;

  @EndpointInject("mock:" + QueueConsts.UPDATE_INDEX)
  private MockEndpoint updateIndexAgg;

  @Autowired
  private ServiceProperties serviceProperties;

  @Autowired
  private ProducerTemplate producerTemplate;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  public void testReportHappyPath() throws Exception {
    String dac = "aoml";
    Instant timestamp = Instant.now();

    Map<Path, Set<String>> expected = new TreeMap<>();
    Set<NcSubmissionMessage> messages = new HashSet<>();

    for (int i = 0; i < 10; i++) {
      String timestampStr = timestamp.minusSeconds(i).toString();
      String floatId = "190183" + i;

      for (String fileName : Arrays.asList(floatId + "_meta.nc", floatId + "_tech.nc", floatId + "_Rtraj.nc")) {
        NcSubmissionMessage message = NcSubmissionMessage.builder()
            .withFloatId(floatId)
            .withDac(dac)
            .withTimestamp(timestampStr)
            .withFileName(fileName)
            .withProfile(false)
            .build();

        messages.add(message);

        Path path = ArgonautFileUtils.getSubmissionProcessedDirForDac(serviceProperties, dac).resolve(timestampStr).resolve("submission_report.csv");
        Set<String> rows = expected.get(path);
        if (rows == null) {
          rows = new TreeSet<>();
          expected.put(path, rows);
        }
        rows.add(timestampStr + "," + dac + "," + floatId + "," + fileName + ",added");
      }
    }

    FileTestUtils.emptyDirectory(ArgonautFileUtils.getSubmissionProcessedDirForDac(serviceProperties, dac));

    submissionCompleteAgg.expectedMessageCount(messages.size());
    updateIndexAgg.expectedMessageCount(messages.size());

    messages.forEach(message -> {
      try {
        producerTemplate.sendBody(QueueConsts.FILE_MOVED, objectMapper.writeValueAsString(message));
      } catch (JsonProcessingException e) {
        throw new RuntimeException(e);
      }
    });

    MockEndpoint.assertIsSatisfied(submissionCompleteAgg, updateIndexAgg);

    for (Entry<Path, Set<String>> entry : expected.entrySet()) {
      Set<String> actualCsv = new TreeSet<>(Files.readAllLines(entry.getKey()));
      assertEquals(entry.getValue(), actualCsv);
    }

  }


  @Test
  public void testReportErrors() throws Exception {
    String dac = "aoml";
    Instant timestamp = Instant.now();

    Map<Path, Set<List<String>>> expected = new TreeMap<>();
    Set<NcSubmissionMessage> messages = new HashSet<>();

    for (int i = 0; i < 10; i++) {
      String timestampStr = timestamp.minusSeconds(i).toString();
      String floatId = "190183" + i;

      for (String fileName : Arrays.asList("R" + floatId + "_meta.nc", "R" + floatId + "_tech.nc", "R" + floatId + "_Rtraj.nc")) {
        NcSubmissionMessage message = NcSubmissionMessage.builder()
            .withFloatId(floatId)
            .withDac(dac)
            .withTimestamp(timestampStr)
            .withFileName(fileName)
            .withProfile(false)
            .addValidationError(" this is a bad, bad, profile")
            .addValidationError("this error message contains \n \",\\ ")
            .build();

        messages.add(message);

        Path path = ArgonautFileUtils.getSubmissionProcessedDirForDac(serviceProperties, dac).resolve(timestampStr).resolve("submission_report.csv");
        Set<List<String>> rows = expected.get(path);
        if (rows == null) {
          rows = new HashSet<>();
          expected.put(path, rows);
        }
        rows.add(Arrays.asList(timestampStr, dac, floatId, fileName, "this is a bad, bad, profile\nthis error message contains \n \",\\"));
      }
    }

    FileTestUtils.emptyDirectory(ArgonautFileUtils.getSubmissionProcessedDirForDac(serviceProperties, dac));

    submissionCompleteAgg.expectedMessageCount(messages.size());
    updateIndexAgg.expectedMessageCount(messages.size());

    messages.forEach(message -> {
      try {
        producerTemplate.sendBody(QueueConsts.FILE_MOVED, objectMapper.writeValueAsString(message));
      } catch (JsonProcessingException e) {
        throw new RuntimeException(e);
      }
    });

    MockEndpoint.assertIsSatisfied(submissionCompleteAgg, updateIndexAgg);

    Map<Path, Set<List<String>>> actual = new TreeMap<>();

    for (Entry<Path, Set<List<String>>> entry : expected.entrySet()) {
      CSVFormat csvFormat = CSVFormat.DEFAULT.builder().get();
      Set<List<String>> rows = new HashSet<>();
      try (Reader in = new FileReader(entry.getKey().toFile())) {
        Iterable<CSVRecord> records = csvFormat.parse(in);
        for (CSVRecord record : records) {
          List<String> row = new ArrayList<>();
          row.add(record.get(0));
          row.add(record.get(1));
          row.add(record.get(2));
          row.add(record.get(3));
          row.add(record.get(4));
          rows.add(row);
        }

      }
      actual.put(entry.getKey(), rows);
    }

    assertEquals(expected, actual);

  }


  @Test
  public void testReportRemoval() throws Exception {
    String dac = "aoml";
    Instant timestamp = Instant.now();

    Map<Path, Set<String>> expected = new TreeMap<>();
    Set<NcSubmissionMessage> messages = new HashSet<>();

    for (int i = 0; i < 10; i++) {
      String timestampStr = timestamp.minusSeconds(i).toString();
      String floatId = "190183" + i;

      for (String fileName : Arrays.asList(floatId + "_meta.nc", floatId + "_tech.nc", floatId + "_Rtraj.nc")) {
        NcSubmissionMessage message = NcSubmissionMessage.builder()
            .withFloatId(floatId)
            .withDac(dac)
            .withTimestamp(timestampStr)
            .withFileName(fileName)
            .withProfile(false)
            .withOperation(Operation.REMOVE)
            .build();

        messages.add(message);

        Path path = ArgonautFileUtils.getSubmissionProcessedDirForDac(serviceProperties, dac).resolve(timestampStr).resolve("submission_report.csv");
        Set<String> rows = expected.get(path);
        if (rows == null) {
          rows = new TreeSet<>();
          expected.put(path, rows);
        }
        rows.add(timestampStr + "," + dac + "," + floatId + "," + fileName + ",removed");
      }
    }

    FileTestUtils.emptyDirectory(ArgonautFileUtils.getSubmissionProcessedDirForDac(serviceProperties, dac));

    submissionCompleteAgg.expectedMessageCount(messages.size());
    updateIndexAgg.expectedMessageCount(messages.size());

    messages.forEach(message -> {
      try {
        producerTemplate.sendBody(QueueConsts.FILE_MOVED, objectMapper.writeValueAsString(message));
      } catch (JsonProcessingException e) {
        throw new RuntimeException(e);
      }
    });

    MockEndpoint.assertIsSatisfied(submissionCompleteAgg, updateIndexAgg);

    for (Entry<Path, Set<String>> entry : expected.entrySet()) {
      Set<String> actualCsv = new TreeSet<>(Files.readAllLines(entry.getKey()));
      assertEquals(entry.getValue(), actualCsv);
    }

  }


  @Test
  public void testReportInvalidRemovalFile() throws Exception {
    String dac = "aoml";
    String timestamp = Instant.now().toString();

    NcSubmissionMessage message = NcSubmissionMessage.builder()
        .withDac(dac)
        .withTimestamp(timestamp)
        .withFileName("foobar_removal.txt")
        .withProfile(false)
        .withValidationErrors(Collections.singletonList("removal file name does not match DAC 'aoml' (aoml_removal.txt): foobar_removal.txt"))
        .withOperation(Operation.REMOVE)
        .withNumberOfFilesInSubmission(1)
        .build();

    FileTestUtils.emptyDirectory(ArgonautFileUtils.getSubmissionProcessedDirForDac(serviceProperties, dac));

    submissionCompleteAgg.expectedMessageCount(1);
    updateIndexAgg.expectedMessageCount(1);

    producerTemplate.sendBody(QueueConsts.FILE_MOVED, objectMapper.writeValueAsString(message));

    MockEndpoint.assertIsSatisfied(submissionCompleteAgg, updateIndexAgg);

    CSVFormat csvFormat = CSVFormat.DEFAULT.builder().get();
    List<String> row = new ArrayList<>(5);
    int i = 0;
    try (Reader in = new FileReader("target/submission/dac/aoml/processed/" + timestamp + "/submission_report.csv")) {
      Iterable<CSVRecord> records = csvFormat.parse(in);

      for (CSVRecord record : records) {
        row.add(record.get(0));
        row.add(record.get(1));
        row.add(record.get(2));
        row.add(record.get(3));
        row.add(record.get(4));
        i++;
      }
    }
    assertEquals(1, i);
    assertEquals(
        Arrays.asList(timestamp, dac, "", "foobar_removal.txt", "removal file name does not match DAC 'aoml' (aoml_removal.txt): foobar_removal.txt"),
        row);

  }


}
