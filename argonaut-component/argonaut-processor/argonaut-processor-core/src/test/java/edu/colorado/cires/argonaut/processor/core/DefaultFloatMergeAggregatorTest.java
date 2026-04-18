package edu.colorado.cires.argonaut.processor.core;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.colorado.cires.argonaut.messaging.core.databind.FloatMergeGroup;
import edu.colorado.cires.argonaut.messaging.core.queue.MessageSender;
import edu.colorado.cires.argonaut.messaging.core.util.ArgonautJsonMapperFactory;
import edu.colorado.cires.argonaut.metadata.core.DefaultFloatMergeGroupPage;
import edu.colorado.cires.argonaut.metadata.core.DefaultIndexPageRequest;
import edu.colorado.cires.argonaut.metadata.core.IndexPageRequest;
import edu.colorado.cires.argonaut.metadata.core.MetadataStore;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

public class DefaultFloatMergeAggregatorTest {

  @Test
  public void test() throws Exception {
    MessageSender messageSender = mock(MessageSender.class);
    JsonMapper jsonMapper = ArgonautJsonMapperFactory.getJsonMapper();
    MetadataStore metadataStore = mock(MetadataStore.class);
    String queue = "seda:float-merge";

    DefaultFloatMergeAggregator aggregator = new DefaultFloatMergeAggregator();
    aggregator.setFloatMergeQueue(queue);
    aggregator.setMessageSender(messageSender);
    aggregator.setJsonMapper(jsonMapper);
    aggregator.setMetadataStore(metadataStore);
    aggregator.setPageSize(2);

    when(metadataStore.findUpdatedOrMissingMergeFilesPage(any())).thenAnswer((invocationOnMock) -> {
      IndexPageRequest pageRequest = invocationOnMock.getArgument(0, IndexPageRequest.class);
      List<FloatMergeGroup> page = Arrays.asList(
          FloatMergeGroup.builder().withDac("aaaa").withFloatId("" + pageRequest.getPageNumber()).build(),
          FloatMergeGroup.builder().withDac("bbbb").withFloatId("" + pageRequest.getPageNumber() * 10).build()
      );
      return DefaultFloatMergeGroupPage.builder().withTotalRecords(4).withIndexPageRequest(DefaultIndexPageRequest.builder(pageRequest).build())
          .withPage(page).build();
    });

    aggregator.trigger();

    verify(metadataStore, times(1)).findUpdatedOrMissingMergeFilesPage(
        eq(DefaultIndexPageRequest.builder().withPageNumber(1).withPageSize(2).build()));
    verify(metadataStore, times(1)).findUpdatedOrMissingMergeFilesPage(
        eq(DefaultIndexPageRequest.builder().withPageNumber(2).withPageSize(2).build()));

    List<String> messages = Arrays.asList(
        jsonMapper.writeValueAsString(FloatMergeGroup.builder().withDac("aaaa").withFloatId("1").build()),
        jsonMapper.writeValueAsString(FloatMergeGroup.builder().withDac("bbbb").withFloatId("10").build()),
        jsonMapper.writeValueAsString(FloatMergeGroup.builder().withDac("aaaa").withFloatId("2").build()),
        jsonMapper.writeValueAsString(FloatMergeGroup.builder().withDac("bbbb").withFloatId("20").build())
    );
    for (String message : messages) {
      verify(messageSender, times(1)).sendJson(eq(queue), eq(message));
    }

  }
}