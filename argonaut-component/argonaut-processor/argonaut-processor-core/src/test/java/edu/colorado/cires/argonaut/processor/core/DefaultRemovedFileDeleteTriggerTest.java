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
import edu.colorado.cires.argonaut.messaging.core.databind.ProfileOperation;
import edu.colorado.cires.argonaut.messaging.core.queue.MessageSender;
import edu.colorado.cires.argonaut.messaging.core.util.ArgonautJsonMapperFactory;
import edu.colorado.cires.argonaut.metadata.core.DefaultRemovedFilePage;
import edu.colorado.cires.argonaut.metadata.core.DefaultRemovedFileSearch;
import edu.colorado.cires.argonaut.metadata.core.MetadataStore;
import edu.colorado.cires.argonaut.metadata.core.RemovedFileSearch;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

public class DefaultRemovedFileDeleteTriggerTest {

  @Test
  public void test() throws Exception {
    MessageSender messageSender = mock(MessageSender.class);
    JsonMapper jsonMapper = ArgonautJsonMapperFactory.getJsonMapper();
    MetadataStore metadataStore = mock(MetadataStore.class);
    String queue = "seda:delete-removed-file";
    UUID traceId = UUID.randomUUID();
    Instant now = Instant.parse("2026-12-31T23:59:00Z");
    DefaultRemovedFileDeleteTrigger trigger = new DefaultRemovedFileDeleteTrigger();
    trigger.setMessageSender(messageSender);
    trigger.setJsonMapper(jsonMapper);
    trigger.setMetadataStore(metadataStore);
    trigger.setEnabled(true);
    trigger.setRemovedFileDeleteQueue(queue);
    trigger.setPageSize(1);
    trigger.setRemovedFileRetentionDays(30);
    trigger.setTraceIdGenerator(() -> traceId);
    trigger.setNowGenerator(() -> now);

    ProfileOperation p1 = ProfileOperation.builder()
        .withDac("aoml")
        .withFloatId("13855")
        .withFiles(Collections.singletonList(
            MetadataRecord.builder()
                .withFileType(ArgoFileType.PROFILE_BIOCHEMICAL)
                .withFile("aoml/13855/profiles/BD13855_001.nc")
                .withFileName("BD13855_001.nc")
                .withFileStatus(FileStatus.REMOVED)
                .withActionTimestamp(Instant.parse("2025-10-10T14:00:00Z"))
                .build()
        ))
        .build();

    ProfileOperation p2 = ProfileOperation.builder()
        .withDac("aoml")
        .withFloatId("13857")
        .withFiles(Collections.singletonList(
            MetadataRecord.builder()
                .withFileType(ArgoFileType.PROFILE_CORE)
                .withFile("aoml/13857/profiles/D13857_001.nc")
                .withFileName("D13857_001.nc")
                .withFileStatus(FileStatus.REMOVED)
                .withActionTimestamp(Instant.parse("2025-10-10T12:00:00Z"))
                .build()
        ))
        .build();

    ProfileOperation p3 = ProfileOperation.builder()
        .withDac("aoml")
        .withFloatId("13857")
        .withFiles(Collections.singletonList(
            MetadataRecord.builder()
                .withFileType(ArgoFileType.METADATA)
                .withFile("aoml/13857/13857_meta.nc")
                .withFileName("13857_meta.nc")
                .withFileStatus(FileStatus.REMOVED)
                .withActionTimestamp(Instant.parse("2025-10-10T13:00:00Z"))
                .build()
        ))
        .build();

    when(metadataStore.findRemovedFilesPage(any())).thenAnswer((invocationOnMock) -> {
      RemovedFileSearch pageRequest = invocationOnMock.getArgument(0, RemovedFileSearch.class);
      List<ProfileOperation> page;
      switch (pageRequest.getPageNumber()) {
        case 1:
          page = Arrays.asList(p1);
          break;
        case 2:
          page = Arrays.asList(p2);
          break;
        case 3:
          page = Arrays.asList(p3);
          break;
        default:
          throw new IllegalArgumentException("Invalid page number: " + pageRequest.getPageNumber());
      }
      return DefaultRemovedFilePage.builder()
          .withTotalRecords(3)
          .withIndexPageRequest(DefaultRemovedFileSearch.builder(pageRequest).build())
          .withPage(page)
          .build();
    });

    trigger.trigger();

    Instant date = Instant.parse("2026-12-01T23:59:00Z");

    verify(metadataStore, times(1)).findRemovedFilesPage(
        eq(DefaultRemovedFileSearch.builder().withPageNumber(1).withPageSize(1).withOlderThan(date).build()));
    verify(metadataStore, times(1)).findRemovedFilesPage(
        eq(DefaultRemovedFileSearch.builder().withPageNumber(2).withPageSize(1).withOlderThan(date).build()));
    verify(metadataStore, times(1)).findRemovedFilesPage(
        eq(DefaultRemovedFileSearch.builder().withPageNumber(3).withPageSize(1).withOlderThan(date).build()));

    verify(messageSender, times(1)).sendJson(eq(queue), eq(jsonMapper.writeValueAsString(ProfileOperation.builder(p1).withTraceId(traceId).build())));
    verify(messageSender, times(1)).sendJson(eq(queue), eq(jsonMapper.writeValueAsString(ProfileOperation.builder(p2).withTraceId(traceId).build())));
    verify(messageSender, times(1)).sendJson(eq(queue), eq(jsonMapper.writeValueAsString(ProfileOperation.builder(p3).withTraceId(traceId).build())));

  }
}