package edu.colorado.cires.argonaut.processor.core;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import edu.colorado.cires.argonaut.file.core.FileStore;
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
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

public class  DefaultLatestProfileMergeTriggerTest {

  private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

  @Test
  public void test() throws Exception {
    MessageSender messageSender = mock(MessageSender.class);
    JsonMapper jsonMapper = ArgonautJsonMapperFactory.getJsonMapper();
    MetadataStore metadataStore = mock(MetadataStore.class);
    FileStore outputFileStore = new TestFileStore(Paths.get("src/test/resources/out_latest"));
    String queue = "seda:merge";
    UUID traceId = UUID.randomUUID();
    Instant now = Instant.parse("2026-09-23T12:20:00Z");
    DefaultLatestProfileMergeTrigger aggregator = new DefaultLatestProfileMergeTrigger();
    aggregator.setTraceIdGenerator(() -> traceId);
    aggregator.setNowGenerator(() -> now);
    aggregator.setMetadataStore(metadataStore);
    aggregator.setMessageSender(messageSender);
    aggregator.setJsonMapper(jsonMapper);
    aggregator.setMergeQueue(queue);
    aggregator.setOutputFileStore(outputFileStore);

    List<String> fileNames = Arrays.asList(
        "R20260823",
        "R20260824",
        "R20260825",
        "R20260826",
        "R20260827",
        "R20260828",
        "R20260829",
        "R20260830",
        "R20260831",
        "R20260901",
        "R20260902",
        "R20260903",
        "R20260904",
        "R20260905",
        "R20260906",
        "R20260907",
        "R20260908",
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
        "D20260905",
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
    );

    List<ProfileOperation> messages = new ArrayList<>();

    for (String fileName : fileNames) {
      ProfileMode profileMode = fileName.startsWith("D") ? ProfileMode.DELAYED_MODE : ProfileMode.REAL_TIME;
      String dateStr = fileName.substring(1, 9);
      LocalDate date = LocalDate.parse(dateStr, DATE_FORMAT);

      ProfileOperation response = ProfileOperation.builder()
          .withFileName(fileName)
          .withFiles(Arrays.asList(
              MetadataRecord.builder()
                  .withDac("aoml")
                  .withFloatId(dateStr)
                  .withFile("aoml/"+dateStr+"/profiles/"+fileName+"_001.nc")
                  .withFileStatus(FileStatus.ACTIVE)
                  .build(),
              MetadataRecord.builder()
                  .withDac("bodc")
                  .withFloatId("1"+dateStr)
                  .withFile("bodc/1"+dateStr+"/profiles/1"+fileName+"_001.nc")
                  .withFileStatus(FileStatus.REMOVED)
                  .build()
          ))
          .build();

      messages.add(response);

      when(metadataStore.findUpdatedOrMissingLatestMergeFiles(DefaultRecentProfileSearch.builder()
          .withLimit(10000)
          .withProfileMode(profileMode)
          .withLastUpdatedDateGe(date.atStartOfDay().toInstant(ZoneOffset.UTC))
          .withLastUpdatedDateLt(date.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC))
          .build()))
          .thenReturn(response);
    }


    aggregator.trigger();

    for (String fileName : fileNames) {
      ProfileMode profileMode = fileName.startsWith("D") ? ProfileMode.DELAYED_MODE : ProfileMode.REAL_TIME;
      String dateStr = fileName.substring(1, 9);
      LocalDate date = LocalDate.parse(dateStr, DATE_FORMAT);

      verify(metadataStore, times(1)).findUpdatedOrMissingLatestMergeFiles(
          eq(DefaultRecentProfileSearch.builder()
              .withLimit(10000)
              .withProfileMode(profileMode)
              .withLastUpdatedDateGe(date.atStartOfDay().toInstant(ZoneOffset.UTC))
              .withLastUpdatedDateLt(date.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC))
              .build()));
    }

    verifyNoMoreInteractions(metadataStore);

    for (ProfileOperation response : messages) {
      verify(messageSender, times(1)).sendJson(eq(queue), eq(jsonMapper.writeValueAsString(ProfileOperation.builder(response).withTraceId(traceId).build())));
    }

    verify(messageSender, times(1)).sendJson(eq(queue), eq(jsonMapper.writeValueAsString(ProfileOperation.builder()
        .withFileName("D20260624")
            .withTraceId(traceId)
        .build())));

    verify(messageSender, times(1)).sendJson(eq(queue), eq(jsonMapper.writeValueAsString(ProfileOperation.builder()
        .withFileName("R20260624")
        .withTraceId(traceId)
        .build())));

    verify(messageSender, times(1)).sendJson(eq(queue), eq(jsonMapper.writeValueAsString(ProfileOperation.builder()
        .withFileName("D20260524")
        .withTraceId(traceId)
        .build())));

    verifyNoMoreInteractions(messageSender);

  }

}