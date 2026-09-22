package edu.colorado.cires.argonaut.processor.core;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.colorado.cires.argonaut.messaging.core.databind.ArgoFileType;
import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord;
import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord.FileStatus;
import edu.colorado.cires.argonaut.messaging.core.databind.ProfileMode;
import edu.colorado.cires.argonaut.messaging.core.databind.ProfileOperation;
import edu.colorado.cires.argonaut.messaging.core.queue.MessageSender;
import edu.colorado.cires.argonaut.messaging.core.util.ArgonautJsonMapperFactory;
import edu.colorado.cires.argonaut.metadata.core.DefaultRecentProfilePage;
import edu.colorado.cires.argonaut.metadata.core.DefaultRecentProfileSearch;
import edu.colorado.cires.argonaut.metadata.core.MetadataStore;
import edu.colorado.cires.argonaut.metadata.core.RecentProfileSearch;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

public class DefaultLatestProfileMergeAggregatorTest {

  @Test
  public void test() throws Exception {
    MessageSender messageSender = mock(MessageSender.class);
    JsonMapper jsonMapper = ArgonautJsonMapperFactory.getJsonMapper();
    MetadataStore metadataStore = mock(MetadataStore.class);
    String queue = "seda:merge";
    UUID traceId = UUID.randomUUID();
    Instant now = Instant.parse("2026-02-02T12:20:00Z");
    DefaultLatestProfileMergeAggregator aggregator = new DefaultLatestProfileMergeAggregator();
    aggregator.setTraceIdGenerator(() -> traceId);
    aggregator.setNowGenerator(() -> now);
    aggregator.setMetadataStore(metadataStore);
    aggregator.setMessageSender(messageSender);
    aggregator.setJsonMapper(jsonMapper);
    aggregator.setMergeQueue(queue);
    aggregator.setPageSize(2);

    when(metadataStore.findUpdatedOrMissingLatestMergeFilesPage(any())).thenAnswer((invocationOnMock) -> {
      RecentProfileSearch pageRequest = invocationOnMock.getArgument(0, RecentProfileSearch.class);
      List<ProfileOperation> page = Arrays.asList(
          ProfileOperation.builder()
              .withTraceId(traceId)
              .withFiles(Arrays.asList(
                  MetadataRecord.builder()
                      .withDac("aoml")
                      .withFile("aoml/12345/profiles/D12345_00" + pageRequest.getPageNumber() + ".nc")
                      .withFileName("D12345_00" + pageRequest.getPageNumber() + ".nc")
                      .withDate(Instant.parse("2025-01-02T00:00:00Z"))
                      .withFileStatus(FileStatus.ACTIVE)
                      .withDateUpdate(Instant.parse("2026-01-02T12:20:00Z"))
                      .withProfileMode(ProfileMode.REAL_TIME)
                      .withFloatId("12345")
                      .withFileType(ArgoFileType.PROFILE_CORE)
                      .build(),
                  MetadataRecord.builder()
                      .withDac("bodc")
                      .withFile("bodc/333/profiles/D333_00" + pageRequest.getPageNumber() + ".nc")
                      .withFileName("D333_00" + pageRequest.getPageNumber() + ".nc")
                      .withDate(Instant.parse("2025-02-02T10:00:00Z"))
                      .withFileStatus(FileStatus.ACTIVE)
                      .withDateUpdate(Instant.parse("2026-01-02T13:22:00Z"))
                      .withProfileMode(ProfileMode.REAL_TIME)
                      .withFloatId("333")
                      .withFileType(ArgoFileType.PROFILE_CORE)
                      .build()
              )).build(),
          ProfileOperation.builder()
              .withTraceId(traceId)
              .withFiles(Arrays.asList(
                  MetadataRecord.builder()
                      .withDac("csio")
                      .withFile("csio/999/profiles/D999_00" + pageRequest.getPageNumber() + ".nc")
                      .withFileName("D999_00" + pageRequest.getPageNumber() + ".nc")
                      .withDate(Instant.parse("2025-01-03T00:00:00Z"))
                      .withFileStatus(FileStatus.REMOVED)
                      .withDateUpdate(Instant.parse("2026-02-02T14:20:00Z"))
                      .withProfileMode(ProfileMode.DELAYED_MODE)
                      .withFloatId("999")
                      .withFileType(ArgoFileType.PROFILE_CORE)
                      .build(),
                  MetadataRecord.builder()
                      .withDac("jma")
                      .withFile("jma/111/profiles/D111_00" + pageRequest.getPageNumber() + ".nc")
                      .withFileName("D111_00" + pageRequest.getPageNumber() + ".nc")
                      .withDate(Instant.parse("2025-01-03T00:10:00Z"))
                      .withFileStatus(FileStatus.ACTIVE)
                      .withDateUpdate(Instant.parse("2026-02-02T14:40:00Z"))
                      .withProfileMode(ProfileMode.DELAYED_MODE)
                      .withFloatId("111")
                      .withFileType(ArgoFileType.PROFILE_CORE)
                      .build()
              )).build()
      );
      return DefaultRecentProfilePage.builder()
          .withTotalRecords(4)
          .withIndexPageRequest(DefaultRecentProfileSearch.builder(pageRequest).build())
          .withPage(page).build();
    });

    aggregator.trigger();

    verify(metadataStore, times(1)).findUpdatedOrMissingLatestMergeFilesPage(
        eq(DefaultRecentProfileSearch.builder().withYoungerOrEqual(Instant.parse("2026-01-03T00:00:00Z")).withPageNumber(1).withPageSize(2).build()));
    verify(metadataStore, times(1)).findUpdatedOrMissingLatestMergeFilesPage(
        eq(DefaultRecentProfileSearch.builder().withYoungerOrEqual(Instant.parse("2026-01-03T00:00:00Z")).withPageNumber(2).withPageSize(2).build()));

    List<String> messages = Arrays.asList(
        jsonMapper.writeValueAsString(ProfileOperation.builder()
            .withTraceId(traceId)
            .withFiles(Arrays.asList(
                MetadataRecord.builder()
                    .withDac("aoml")
                    .withFile("aoml/12345/profiles/D12345_001.nc")
                    .withFileName("D12345_001.nc")
                    .withDate(Instant.parse("2025-01-02T00:00:00Z"))
                    .withFileStatus(FileStatus.ACTIVE)
                    .withDateUpdate(Instant.parse("2026-01-02T12:20:00Z"))
                    .withProfileMode(ProfileMode.REAL_TIME)
                    .withFloatId("12345")
                    .withFileType(ArgoFileType.PROFILE_CORE)
                    .build(),
                MetadataRecord.builder()
                    .withDac("bodc")
                    .withFile("bodc/333/profiles/D333_001.nc")
                    .withFileName("D333_001.nc")
                    .withDate(Instant.parse("2025-02-02T10:00:00Z"))
                    .withFileStatus(FileStatus.ACTIVE)
                    .withDateUpdate(Instant.parse("2026-01-02T13:22:00Z"))
                    .withProfileMode(ProfileMode.REAL_TIME)
                    .withFloatId("333")
                    .withFileType(ArgoFileType.PROFILE_CORE)
                    .build()
            )).build()),


        jsonMapper.writeValueAsString(ProfileOperation.builder()
            .withTraceId(traceId)
            .withFiles(Arrays.asList(
                MetadataRecord.builder()
                    .withDac("csio")
                    .withFile("csio/999/profiles/D999_001.nc")
                    .withFileName("D999_001.nc")
                    .withDate(Instant.parse("2025-01-03T00:00:00Z"))
                    .withFileStatus(FileStatus.REMOVED)
                    .withDateUpdate(Instant.parse("2026-02-02T14:20:00Z"))
                    .withProfileMode(ProfileMode.DELAYED_MODE)
                    .withFloatId("999")
                    .withFileType(ArgoFileType.PROFILE_CORE)
                    .build(),
                MetadataRecord.builder()
                    .withDac("jma")
                    .withFile("jma/111/profiles/D111_001.nc")
                    .withFileName("D111_001.nc")
                    .withDate(Instant.parse("2025-01-03T00:10:00Z"))
                    .withFileStatus(FileStatus.ACTIVE)
                    .withDateUpdate(Instant.parse("2026-02-02T14:40:00Z"))
                    .withProfileMode(ProfileMode.DELAYED_MODE)
                    .withFloatId("111")
                    .withFileType(ArgoFileType.PROFILE_CORE)
                    .build()
            )).build()),


        jsonMapper.writeValueAsString(ProfileOperation.builder()
            .withTraceId(traceId)
            .withFiles(Arrays.asList(
                MetadataRecord.builder()
                    .withDac("aoml")
                    .withFile("aoml/12345/profiles/D12345_002.nc")
                    .withFileName("D12345_002.nc")
                    .withDate(Instant.parse("2025-01-02T00:00:00Z"))
                    .withFileStatus(FileStatus.ACTIVE)
                    .withDateUpdate(Instant.parse("2026-01-02T12:20:00Z"))
                    .withProfileMode(ProfileMode.REAL_TIME)
                    .withFloatId("12345")
                    .withFileType(ArgoFileType.PROFILE_CORE)
                    .build(),
                MetadataRecord.builder()
                    .withDac("bodc")
                    .withFile("bodc/333/profiles/D333_002.nc")
                    .withFileName("D333_002.nc")
                    .withDate(Instant.parse("2025-02-02T10:00:00Z"))
                    .withFileStatus(FileStatus.ACTIVE)
                    .withDateUpdate(Instant.parse("2026-01-02T13:22:00Z"))
                    .withProfileMode(ProfileMode.REAL_TIME)
                    .withFloatId("333")
                    .withFileType(ArgoFileType.PROFILE_CORE)
                    .build()
            )).build()),


        jsonMapper.writeValueAsString(ProfileOperation.builder()
            .withTraceId(traceId)
            .withFiles(Arrays.asList(
                MetadataRecord.builder()
                    .withDac("csio")
                    .withFile("csio/999/profiles/D999_002.nc")
                    .withFileName("D999_002.nc")
                    .withDate(Instant.parse("2025-01-03T00:00:00Z"))
                    .withFileStatus(FileStatus.REMOVED)
                    .withDateUpdate(Instant.parse("2026-02-02T14:20:00Z"))
                    .withProfileMode(ProfileMode.DELAYED_MODE)
                    .withFloatId("999")
                    .withFileType(ArgoFileType.PROFILE_CORE)
                    .build(),
                MetadataRecord.builder()
                    .withDac("jma")
                    .withFile("jma/111/profiles/D111_002.nc")
                    .withFileName("D111_002.nc")
                    .withDate(Instant.parse("2025-01-03T00:10:00Z"))
                    .withFileStatus(FileStatus.ACTIVE)
                    .withDateUpdate(Instant.parse("2026-02-02T14:40:00Z"))
                    .withProfileMode(ProfileMode.DELAYED_MODE)
                    .withFloatId("111")
                    .withFileType(ArgoFileType.PROFILE_CORE)
                    .build()
            )).build())


    );
    for (String message : messages) {
      verify(messageSender, times(1)).sendJson(eq(queue), eq(message));
    }

  }
}