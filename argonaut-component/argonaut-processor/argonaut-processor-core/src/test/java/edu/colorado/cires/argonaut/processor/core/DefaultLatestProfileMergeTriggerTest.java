package edu.colorado.cires.argonaut.processor.core;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import edu.colorado.cires.argonaut.file.core.FileStore;
import edu.colorado.cires.argonaut.messaging.core.databind.ArgoFileType;
import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord;
import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord.FileStatus;
import edu.colorado.cires.argonaut.messaging.core.databind.ProfileMode;
import edu.colorado.cires.argonaut.messaging.core.databind.ProfileOperation;
import edu.colorado.cires.argonaut.messaging.core.queue.MessageSender;
import edu.colorado.cires.argonaut.messaging.core.util.ArgonautJsonMapperFactory;
import edu.colorado.cires.argonaut.metadata.core.DefaultRecentProfileSearch;
import edu.colorado.cires.argonaut.metadata.core.MetadataStore;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

public class DefaultLatestProfileMergeTriggerTest {

  private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

  record Group(Instant date, List<String> names) {

  }

  @Test
  public void test() throws Exception {
    MessageSender messageSender = mock(MessageSender.class);
    JsonMapper jsonMapper = ArgonautJsonMapperFactory.getJsonMapper();
    MetadataStore metadataStore = mock(MetadataStore.class);
    FileStore outputFileStore = new TestFileStore(Paths.get("src/test/resources/out_latest"));
    String queue = "seda:merge";
    UUID traceId = UUID.randomUUID();
    DefaultLatestProfileMergeTrigger aggregator = new DefaultLatestProfileMergeTrigger();
    aggregator.setTraceIdGenerator(() -> traceId);
    aggregator.setNowGenerator(() -> Instant.parse("2026-09-23T12:20:00Z"));
    aggregator.setMetadataStore(metadataStore);
    aggregator.setMessageSender(messageSender);
    aggregator.setJsonMapper(jsonMapper);
    aggregator.setMergeQueue(queue);
    aggregator.setOutputFileStore(outputFileStore);
    aggregator.setDaysBack(3);

    final int secondsPerDay = 24 * 60 * 60;

    List<Group> groups = Arrays.asList(
        new Group(Instant.parse("2026-09-23T00:00:00Z"), Arrays.asList(
            "R20260823",
            "R20260824",
            "R20260825",
            "R20260826",
            "R20260827",
            "R20260828",
            "R20260829")),
        new Group(Instant.parse("2026-09-22T00:00:00Z"), Arrays.asList(
            "R20260830",
            "R20260831",
            "R20260901",
            "R20260902",
            "R20260903",
            "R20260904",
            "R20260905",
            "R20260906",
            "R20260907",
            "R20260908")),
        new Group(Instant.parse("2026-09-21T00:00:00Z"), Arrays.asList(
            "R20260909",
            "R20260910",
            "R20260911",
            "R20260912",
            "R20260913",
            "R20260914",
            "R20260915",
            "R20260916",
            "R20260917",
            "R20260918",
            "R20260919",
            "R20260920",
            "R20260921",
            "R20260922",
            "R20260923",
            "D20260823",
            "D20260824",
            "D20260825",
            "D20260826",
            "D20260827",
            "D20260828",
            "D20260829",
            "D20260830",
            "D20260831",
            "D20260901",
            "D20260902",
            "D20260903",
            "D20260904",
            "D20260905")),
        new Group(Instant.parse("2026-09-20T00:00:00Z"), Arrays.asList(
            "D20260906",
            "D20260907",
            "D20260908",
            "D20260909",
            "D20260910",
            "D20260911",
            "D20260912",
            "D20260913",
            "D20260914",
            "D20260915",
            "D20260916",
            "D20260917",
            "D20260918",
            "D20260919",
            "D20260920",
            "D20260921",
            "D20260922",
            "D20260923"
        ))
    );

    for (Group group : groups) {

      List<MetadataRecord> dModes = new ArrayList<>();
      List<MetadataRecord> rModes = new ArrayList<>();

      for (String fileName : group.names()) {
        ProfileMode profileMode = fileName.startsWith("D") ? ProfileMode.DELAYED_MODE : ProfileMode.REAL_TIME;
        String floatId = fileName.substring(1, 9);

        List<MetadataRecord> modeList = rModes;
        if (profileMode == ProfileMode.DELAYED_MODE) {
          modeList = dModes;
        }

        List<MetadataRecord> metadataRecords = Arrays.asList(

            MetadataRecord.builder()
                .withDac("aoml")
                .withFloatId(floatId)
                .withFile("aoml/" + floatId + "/profiles/" + fileName + "_001.nc")
                .withFileStatus(FileStatus.ACTIVE)
                .withFileName(fileName + "_001.nc")
                .withCycleNumber("001")
                .withActionTimestamp(Instant.parse("2026-09-23T12:00:00Z"))
                .withDate(Instant.parse("2020-09-23T01:00:00Z"))
                .withFileType(ArgoFileType.PROFILE_CORE)
                .build(),
            MetadataRecord.builder()
                .withDac("bodc")
                .withFloatId("1" + floatId)
                .withFile("bodc/1" + floatId + "/profiles/1" + fileName + "_001.nc")
                .withFileStatus(FileStatus.REMOVED)
                .withFileName("1" + fileName + "_001.nc")
                .withCycleNumber("001")
                .withActionTimestamp(Instant.parse("2026-09-23T12:00:00Z"))
                .withDate(Instant.parse("2020-09-23T01:00:00Z"))
                .withFileType(ArgoFileType.PROFILE_CORE)
                .build()
        );

        modeList.addAll(metadataRecords);

      }

      when(metadataStore.findUpdatedOrMissingLatestMergeFiles(
          eq(DefaultRecentProfileSearch.builder()
              .withLimit(10000)
              .withProfileMode(ProfileMode.DELAYED_MODE)
              .withLastUpdatedDateGe(group.date())
              .withLastUpdatedDateLt(group.date().plusSeconds(secondsPerDay))
              .build()))
      ).thenReturn(
          ProfileOperation.builder()
              .withFileName("D" + group.date().toString().substring(0, 10).replaceAll("-", ""))
              .withFiles(dModes)
              .build());

      when(metadataStore.findUpdatedOrMissingLatestMergeFiles(
          eq(DefaultRecentProfileSearch.builder()
              .withLimit(10000)
              .withProfileMode(ProfileMode.REAL_TIME)
              .withLastUpdatedDateGe(group.date())
              .withLastUpdatedDateLt(group.date().plusSeconds(secondsPerDay))
              .build()))
      ).thenReturn(
          ProfileOperation.builder()
              .withFileName("R" + group.date().toString().substring(0, 10).replaceAll("-", ""))
              .withFiles(rModes)
              .build());
    }

    aggregator.trigger();

    List<String> expectedRequests = Arrays.asList(
        "D20260923", //always request today
        "R20260923", //always request today
        "D20260920",
        "R20260920"
    );

    for (String request : expectedRequests) {

      ProfileMode profileMode = request.substring(0, 1).equals("D") ? ProfileMode.DELAYED_MODE : ProfileMode.REAL_TIME;
      Instant date = LocalDate.parse(request.substring(1, request.length()), DATE_FORMAT).atStartOfDay().toInstant(ZoneOffset.UTC);

      verify(metadataStore, times(1)).findUpdatedOrMissingLatestMergeFiles(
          eq(DefaultRecentProfileSearch.builder()
              .withLimit(10000)
              .withProfileMode(profileMode)
              .withLastUpdatedDateGe(date)
              .withLastUpdatedDateLt(date.plusSeconds(secondsPerDay))
              .build()));

    }

    verifyNoMoreInteractions(metadataStore);

    List<MetadataRecord> r20260923Files = new ArrayList<>();
    groups.stream().filter(s -> s.date().equals(Instant.parse("2026-09-23T00:00:00Z"))).flatMap(s -> s.names().stream())
        .filter(s -> s.startsWith("R"))
        .forEach(fileName -> {
          String floatId = fileName.substring(1, 9);
          r20260923Files.add(MetadataRecord.builder()
              .withDac("aoml")
              .withFloatId(floatId)
              .withFile("aoml/" + floatId + "/profiles/" + fileName + "_001.nc")
              .withFileStatus(FileStatus.ACTIVE)
              .withFileName(fileName + "_001.nc")
              .withCycleNumber("001")
              .withActionTimestamp(Instant.parse("2026-09-23T12:00:00Z"))
              .withDate(Instant.parse("2020-09-23T01:00:00Z"))
              .withFileType(ArgoFileType.PROFILE_CORE)
              .build());
          r20260923Files.add(MetadataRecord.builder()
              .withDac("bodc")
              .withFloatId("1" + floatId)
              .withFile("bodc/1" + floatId + "/profiles/1" + fileName + "_001.nc")
              .withFileStatus(FileStatus.REMOVED)
              .withFileName("1" + fileName + "_001.nc")
              .withCycleNumber("001")
              .withActionTimestamp(Instant.parse("2026-09-23T12:00:00Z"))
              .withDate(Instant.parse("2020-09-23T01:00:00Z"))
              .withFileType(ArgoFileType.PROFILE_CORE)
              .build());
        });

    verify(messageSender, times(1)).sendJson(eq(queue), eq(jsonMapper.writeValueAsString(ProfileOperation.builder()
        .withFileName("R20260923")
        .withTraceId(traceId)
        .withFiles(r20260923Files)
        .build())));

    List<MetadataRecord> d20260920Files = new ArrayList<>();
    groups.stream().filter(s -> s.date().equals(Instant.parse("2026-09-20T00:00:00Z"))).flatMap(s -> s.names().stream())
        .filter(s -> s.startsWith("D"))
        .forEach(fileName -> {
          String floatId = fileName.substring(1, 9);
          d20260920Files.add(MetadataRecord.builder()
              .withDac("aoml")
              .withFloatId(floatId)
              .withFile("aoml/" + floatId + "/profiles/" + fileName + "_001.nc")
              .withFileStatus(FileStatus.ACTIVE)
              .withFileName(fileName + "_001.nc")
              .withCycleNumber("001")
              .withActionTimestamp(Instant.parse("2026-09-23T12:00:00Z"))
              .withDate(Instant.parse("2020-09-23T01:00:00Z"))
              .withFileType(ArgoFileType.PROFILE_CORE)
              .build());
          d20260920Files.add(MetadataRecord.builder()
              .withDac("bodc")
              .withFloatId("1" + floatId)
              .withFile("bodc/1" + floatId + "/profiles/1" + fileName + "_001.nc")
              .withFileStatus(FileStatus.REMOVED)
              .withFileName("1" + fileName + "_001.nc")
              .withCycleNumber("001")
              .withActionTimestamp(Instant.parse("2026-09-23T12:00:00Z"))
              .withDate(Instant.parse("2020-09-23T01:00:00Z"))
              .withFileType(ArgoFileType.PROFILE_CORE)
              .build());
        });

    verify(messageSender, times(1)).sendJson(eq(queue), eq(jsonMapper.writeValueAsString(ProfileOperation.builder()
        .withFileName("D20260920")
        .withTraceId(traceId)
        .withFiles(d20260920Files)
        .build())));

    List<String> removed = Arrays.asList(
        "D20260524",
        "D20260624",
        "R20260624"
    );

    for (String file : removed) {
      verify(messageSender, times(1)).sendJson(eq(queue), eq(jsonMapper.writeValueAsString(ProfileOperation.builder()
          .withFileName(file)
          .withFiles(Collections.emptyList())
          .withTraceId(traceId)
          .build())));
    }

    verifyNoMoreInteractions(messageSender);

  }

}