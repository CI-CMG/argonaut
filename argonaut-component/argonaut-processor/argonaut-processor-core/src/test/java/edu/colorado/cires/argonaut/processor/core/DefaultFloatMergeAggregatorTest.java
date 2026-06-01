package edu.colorado.cires.argonaut.processor.core;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.colorado.cires.argonaut.messaging.core.databind.ProfileOperation;
import edu.colorado.cires.argonaut.messaging.core.queue.MessageSender;
import edu.colorado.cires.argonaut.messaging.core.util.ArgonautJsonMapperFactory;
import edu.colorado.cires.argonaut.metadata.core.DefaultIndexPageRequest;
import edu.colorado.cires.argonaut.metadata.core.DefaultProfilePage;
import edu.colorado.cires.argonaut.metadata.core.IndexPageRequest;
import edu.colorado.cires.argonaut.metadata.core.MetadataStore;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

public class DefaultFloatMergeAggregatorTest {

  @Test
  public void test() throws Exception {
    MessageSender messageSender = mock(MessageSender.class);
    JsonMapper jsonMapper = ArgonautJsonMapperFactory.getJsonMapper();
    MetadataStore metadataStore = mock(MetadataStore.class);
    String queue = "seda:float-merge";

    UUID traceId = UUID.randomUUID();
    DefaultFloatMergeAggregator aggregator = new DefaultFloatMergeAggregator();
    aggregator.setFloatMergeQueue(queue);
    aggregator.setMessageSender(messageSender);
    aggregator.setJsonMapper(jsonMapper);
    aggregator.setMetadataStore(metadataStore);
    aggregator.setPageSize(2);
    aggregator.setTraceIdGenerator(() -> traceId);

    when(metadataStore.findUpdatedOrMissingMergeFilesPage(any())).thenAnswer((invocationOnMock) -> {
      IndexPageRequest pageRequest = invocationOnMock.getArgument(0, IndexPageRequest.class);
      List<ProfileOperation> page = Arrays.asList(
          ProfileOperation.builder().withDac("aaaa").withFloatId("" + pageRequest.getPageNumber()).withFiles(Collections.singletonList("aaaa/profiles/" + pageRequest.getPageNumber() + ".nc")).build(),
          ProfileOperation.builder().withDac("bbbb").withFloatId("" + pageRequest.getPageNumber() * 10).withFiles(Collections.singletonList("aaaa/profiles/" + pageRequest.getPageNumber() * 10 + ".nc")).build()
      );
      return DefaultProfilePage.builder().withTotalRecords(4).withIndexPageRequest(DefaultIndexPageRequest.builder(pageRequest).build())
          .withPage(page).build();
    });

    aggregator.trigger();

    verify(metadataStore, times(1)).findUpdatedOrMissingMergeFilesPage(
        eq(DefaultIndexPageRequest.builder().withPageNumber(1).withPageSize(2).build()));
    verify(metadataStore, times(1)).findUpdatedOrMissingMergeFilesPage(
        eq(DefaultIndexPageRequest.builder().withPageNumber(2).withPageSize(2).build()));

    List<String> messages = Arrays.asList(
        jsonMapper.writeValueAsString(ProfileOperation.builder().withDac("aaaa").withFloatId("1").withTraceId(traceId).withFileName("1_prof.nc").withFiles(Collections.singletonList("aaaa/profiles/1.nc")).build()),
        jsonMapper.writeValueAsString(ProfileOperation.builder().withDac("bbbb").withFloatId("10").withTraceId(traceId).withFileName("10_prof.nc").withFiles(Collections.singletonList("aaaa/profiles/10.nc")).build()),
        jsonMapper.writeValueAsString(ProfileOperation.builder().withDac("aaaa").withFloatId("2").withTraceId(traceId).withFileName("2_prof.nc").withFiles(Collections.singletonList("aaaa/profiles/2.nc")).build()),
        jsonMapper.writeValueAsString(ProfileOperation.builder().withDac("bbbb").withFloatId("20").withTraceId(traceId).withFileName("20_prof.nc").withFiles(Collections.singletonList("aaaa/profiles/20.nc")).build())
    );
    for (String message : messages) {
      verify(messageSender, times(1)).sendJson(eq(queue), eq(message));
    }

  }
}