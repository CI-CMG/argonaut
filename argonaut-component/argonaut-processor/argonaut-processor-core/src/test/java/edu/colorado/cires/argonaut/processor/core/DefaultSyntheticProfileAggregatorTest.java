package edu.colorado.cires.argonaut.processor.core;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.colorado.cires.argonaut.messaging.core.databind.ProfileOperation;
import edu.colorado.cires.argonaut.messaging.core.queue.MessageSender;
import edu.colorado.cires.argonaut.messaging.core.util.ArgonautJsonMapperFactory;
import edu.colorado.cires.argonaut.metadata.core.DefaultProfilePage;
import edu.colorado.cires.argonaut.metadata.core.IndexPageRequest;
import edu.colorado.cires.argonaut.metadata.core.MetadataStore;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import tools.jackson.databind.json.JsonMapper;

public class DefaultSyntheticProfileAggregatorTest {

  private final JsonMapper jsonMapper = ArgonautJsonMapperFactory.getJsonMapper();

  @Test
  public void test() throws Exception {
    int pageSize = 5;
    int maxResults = pageSize + 1;


    MessageSender messageSender = mock(MessageSender.class);
    MetadataStore metadataStore = mock(MetadataStore.class);

    List<ProfileOperation> expected = new ArrayList<>(maxResults);
    when(metadataStore.findUpdatedOrMissingSyntheticProfilesPage(any())).thenAnswer(invocation -> {
      IndexPageRequest pageRequest = invocation.getArgument(0, IndexPageRequest.class);
      List<ProfileOperation> page = new ArrayList<>(pageRequest.getPageSize());
      int start = (pageRequest.getPageNumber() - 1) * pageRequest.getPageSize();
      int end = Math.min(maxResults, start + pageRequest.getPageSize());
      for (int i = start; i < end; i++) {
        ProfileOperation profileOperation = ProfileOperation.builder()
            .withFloatId("" + i)
            .withDac("aoml")
            .withFiles(Arrays.asList(
                "aoml/" + i + "/profiles/R" + i + "_001.nc",
                "aoml/" + i + "/profiles/BR" + i + "_001.nc",
                "aoml/" + i + "/" + i + "_meta.nc"
            ))
            .build();
        expected.add(profileOperation);
        page.add(profileOperation);
      }

      return DefaultProfilePage.builder()
          .withTotalRecords(maxResults)
          .withPage(page)
          .withIndexPageRequest(pageRequest)
          .build();
    });

    DefaultSyntheticProfileAggregator aggregator = new DefaultSyntheticProfileAggregator();
    aggregator.setEnabled(true);
    aggregator.setJsonMapper(jsonMapper);
    aggregator.setSyntheticProfileQueue("synth-queue");
    aggregator.setMessageSender(messageSender);
    aggregator.setMetadataStore(metadataStore);
    aggregator.setPageSize(5);

    aggregator.trigger();

    ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
    verify(messageSender, times(maxResults)).sendJson(eq("synth-queue"), captor.capture());
    assertEquals(expected, captor.getAllValues().stream().map(json -> jsonMapper.readValue(json, ProfileOperation.class)).toList());

  }
}