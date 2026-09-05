package edu.colorado.cires.argonaut.processor.core;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.colorado.cires.argonaut.messaging.core.databind.ArgoOcean;
import edu.colorado.cires.argonaut.messaging.core.databind.DacFloatFilePath;
import edu.colorado.cires.argonaut.messaging.core.databind.GeoMergeInfo;
import edu.colorado.cires.argonaut.messaging.core.queue.MessageSender;
import edu.colorado.cires.argonaut.messaging.core.util.ArgonautJsonMapperFactory;
import edu.colorado.cires.argonaut.metadata.core.DefaultGeoMergePage;
import edu.colorado.cires.argonaut.metadata.core.DefaultIndexPageRequest;
import edu.colorado.cires.argonaut.metadata.core.IndexPageRequest;
import edu.colorado.cires.argonaut.metadata.core.MetadataStore;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

public class DefaultGeoMergeAggregatorTest {

  @Test
  public void test() throws Exception {
    MessageSender messageSender = mock(MessageSender.class);
    JsonMapper jsonMapper = ArgonautJsonMapperFactory.getJsonMapper();
    MetadataStore metadataStore = mock(MetadataStore.class);
    String queue = "seda:geo-merge";
    UUID traceId = UUID.randomUUID();
    DefaultGeoMergeAggregator aggregator = new DefaultGeoMergeAggregator();
    aggregator.setGeoMergeQueue(queue);
    aggregator.setMessageSender(messageSender);
    aggregator.setJsonMapper(jsonMapper);
    aggregator.setMetadataStore(metadataStore);
    aggregator.setPageSize(2);
    aggregator.setTraceIdGenerator(() -> traceId);

    when(metadataStore.findUpdatedOrMissingGeoMergeFilesPage(any())).thenAnswer((invocationOnMock) -> {
      IndexPageRequest pageRequest = invocationOnMock.getArgument(0, IndexPageRequest.class);
      List<GeoMergeInfo> page = Arrays.asList(
          GeoMergeInfo.builder()
              .withYear(2000 + pageRequest.getPageNumber())
              .withMonth(10 + pageRequest.getPageNumber())
              .withDay(1 + pageRequest.getPageNumber())
              .withOcean(ArgoOcean.PACIFIC_OCEAN)
              .withTraceId(traceId)
              .withFiles(Collections.singletonList(DacFloatFilePath.builder()
                      .withFile("aaa/bbbb.nc")
                      .withFloatId("bbbb")
                      .withDac("aaa")
                  .build()))
              .build(),
          GeoMergeInfo.builder()
              .withYear(1900 + pageRequest.getPageNumber())
              .withMonth(pageRequest.getPageNumber())
              .withDay(2 + pageRequest.getPageNumber())
              .withOcean(ArgoOcean.ATLANTIC_OCEAN)
              .withTraceId(traceId)
              .withFiles(Collections.singletonList(DacFloatFilePath.builder()
                  .withFile("ccc/dddd.nc")
                  .withFloatId("dddd")
                  .withDac("ccc")
                  .build()))
              .build()
      );
      return DefaultGeoMergePage.builder().withTotalRecords(4).withIndexPageRequest(DefaultIndexPageRequest.builder(pageRequest).build())
          .withPage(page).build();
    });

    aggregator.trigger();

    verify(metadataStore, times(1)).findUpdatedOrMissingGeoMergeFilesPage(
        eq(DefaultIndexPageRequest.builder().withPageNumber(1).withPageSize(2).build()));
    verify(metadataStore, times(1)).findUpdatedOrMissingGeoMergeFilesPage(
        eq(DefaultIndexPageRequest.builder().withPageNumber(2).withPageSize(2).build()));

    List<String> messages = Arrays.asList(
        jsonMapper.writeValueAsString(GeoMergeInfo.builder()
            .withYear(2001)
            .withMonth(11)
            .withDay(2)
            .withOcean(ArgoOcean.PACIFIC_OCEAN)
            .withTraceId(traceId)
            .withFiles(Collections.singletonList(
                DacFloatFilePath.builder()
                    .withFile("aaa/bbbb.nc")
                    .withFloatId("bbbb")
                    .withDac("aaa")
                    .build()
            ))
            .build()),
        jsonMapper.writeValueAsString(GeoMergeInfo.builder()
            .withYear(1901)
            .withMonth(1)
            .withDay(3)
            .withOcean(ArgoOcean.ATLANTIC_OCEAN)
            .withTraceId(traceId)
            .withFiles(Collections.singletonList(
                DacFloatFilePath.builder()
                    .withFile("ccc/dddd.nc")
                    .withFloatId("dddd")
                    .withDac("ccc")
                    .build()
            ))
            .build()),
        jsonMapper.writeValueAsString(GeoMergeInfo.builder()
            .withYear(2002)
            .withMonth(12)
            .withDay(3)
            .withOcean(ArgoOcean.PACIFIC_OCEAN)
            .withTraceId(traceId)
            .withFiles(Collections.singletonList(
                DacFloatFilePath.builder()
                    .withFile("aaa/bbbb.nc")
                    .withFloatId("bbbb")
                    .withDac("aaa")
                    .build()
            ))
            .build()),
        jsonMapper.writeValueAsString(GeoMergeInfo.builder()
            .withYear(1902)
            .withMonth(2)
            .withDay(4)
            .withOcean(ArgoOcean.ATLANTIC_OCEAN)
            .withTraceId(traceId)
            .withFiles(Collections.singletonList(
                DacFloatFilePath.builder()
                    .withFile("ccc/dddd.nc")
                    .withFloatId("dddd")
                    .withDac("ccc")
                    .build()
            ))
            .build())
    );
    for (String message : messages) {
      verify(messageSender, times(1)).sendJson(eq(queue), eq(message));
    }

  }
}