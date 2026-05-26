package edu.colorado.cires.argonaut.processor.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.colorado.cires.argonaut.core.merge.multiprof.DefaultMultiProfileMerger;
import edu.colorado.cires.argonaut.core.merge.multiprof.LocalPathSupplier;
import edu.colorado.cires.argonaut.core.merge.multiprof.MultiProfileMerger;
import edu.colorado.cires.argonaut.file.core.FileStore;
import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord;
import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord.Action;
import edu.colorado.cires.argonaut.messaging.core.databind.ProfileOperation;
import edu.colorado.cires.argonaut.messaging.core.queue.MessageSender;
import edu.colorado.cires.argonaut.messaging.core.util.ArgonautJsonMapperFactory;
import edu.colorado.cires.argonaut.metadata.core.MetadataStore;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import tools.jackson.databind.json.JsonMapper;

public class DefaultFloatMergeProcessorTest {

  private final Path localTempDir = Paths.get("target/mergetest");
  private final JsonMapper jsonMapper = ArgonautJsonMapperFactory.getJsonMapper();

  @BeforeEach
  public void setup() throws IOException {
    FileUtils.deleteDirectory(localTempDir.toFile());
  }

  @AfterEach
  public void cleanup() throws IOException {
    FileUtils.deleteDirectory(localTempDir.toFile());
  }

  @Test
  public void test() throws Exception {
    FileStore outputFileStore = Mockito.mock(FileStore.class);
    MessageSender messageSender = Mockito.mock(MessageSender.class);
    MultiProfileMerger merger = Mockito.mock(DefaultMultiProfileMerger.class);

    when(outputFileStore.getRoot()).thenReturn("/foo/bar");
    when(outputFileStore.getFileName(eq("aoml/123/profiles/R123_001.nc"))).thenReturn("R123_001.nc");
    when(outputFileStore.getFileName(eq("aoml/123/profiles/R123_001D.nc"))).thenReturn("R123_001D.nc");
    when(outputFileStore.getFileName(eq("aoml/123/profiles/R123_002.nc"))).thenReturn("R123_002.nc");
    when(outputFileStore.appendToPath(eq("/foo/bar"), eq("dac"), eq("aoml/123/profiles/R123_001.nc"))).thenReturn("/foo/bar/dac/aoml/123/profiles/R123_001.nc");
    when(outputFileStore.appendToPath(eq("/foo/bar"), eq("dac"), eq("aoml/123/profiles/R123_001D.nc"))).thenReturn("/foo/bar/dac/aoml/123/profiles/R123_001D.nc");
    when(outputFileStore.appendToPath(eq("/foo/bar"), eq("dac"), eq("aoml/123/profiles/R123_002.nc"))).thenReturn("/foo/bar/dac/aoml/123/profiles/R123_002.nc");
    when(outputFileStore.appendToPath(eq("aoml"), eq("123"), eq("123_prof.nc"))).thenReturn("aoml/123/123_prof.nc");
    when(outputFileStore.appendToPath(eq("/foo/bar"), eq("dac"), eq("aoml/123/123_prof.nc"))).thenReturn("/foo/bar/dac/aoml/123/123_prof.nc");



    DefaultFloatMergeProcessor processor = new DefaultFloatMergeProcessor();
    processor.setOutputFileStore(outputFileStore);
    processor.setLocalTempDir(localTempDir);
    processor.setMessageSender(messageSender);
    processor.setUpdateIndexQueue("updateIndexQueue");
    processor.setJsonMapper(jsonMapper);
    processor.setMerger(merger);


    ProfileOperation message = ProfileOperation.builder()
        .withDac("aoml")
        .withFloatId("123")
        .withFiles(Arrays.asList(
            "aoml/123/profiles/R123_001.nc",
            "aoml/123/profiles/R123_001D.nc",
            "aoml/123/profiles/R123_002.nc"
            ))
        .build();

    processor.merge(message);

    ArgumentCaptor<Path> outputPathCaptor = ArgumentCaptor.forClass(Path.class);
    ArgumentCaptor<List<LocalPathSupplier>> localPathSupplierCaptor = ArgumentCaptor.forClass(List.class);
    verify(merger).mergeProfiles(localPathSupplierCaptor.capture(), eq(Arrays.asList("PRES", "TEMP", "PSAL")), outputPathCaptor.capture());

    List<LocalPathSupplier> localPathSuppliers = localPathSupplierCaptor.getValue();
    assertEquals(3, localPathSuppliers.size());
    LocalPathSupplier localPathSupplier1 = localPathSuppliers.get(0);
    LocalPathSupplier localPathSupplier2 = localPathSuppliers.get(1);
    LocalPathSupplier localPathSupplier3 = localPathSuppliers.get(2);

    localPathSupplier1.prepare();
    assertEquals("R123_001.nc", localPathSupplier1.getFileName());
    assertNotNull(localPathSupplier1.getLocalPath());
    localPathSupplier1.cleanUp();

    localPathSupplier2.prepare();
    assertEquals("R123_001D.nc", localPathSupplier2.getFileName());
    assertNotNull(localPathSupplier2.getLocalPath());
    localPathSupplier2.cleanUp();

    localPathSupplier3.prepare();
    assertEquals("R123_002.nc", localPathSupplier3.getFileName());
    assertNotNull(localPathSupplier3.getLocalPath());
    localPathSupplier3.cleanUp();

    verify(outputFileStore).downloadLocalFile(eq("/foo/bar/dac/aoml/123/profiles/R123_001.nc"), eq(localPathSupplier1.getLocalPath()));
    verify(outputFileStore).downloadLocalFile(eq("/foo/bar/dac/aoml/123/profiles/R123_001D.nc"), eq(localPathSupplier2.getLocalPath()));
    verify(outputFileStore).downloadLocalFile(eq("/foo/bar/dac/aoml/123/profiles/R123_002.nc"), eq(localPathSupplier3.getLocalPath()));


    ArgumentCaptor<String> jsonCaptor = ArgumentCaptor.forClass(String.class);
    verify(messageSender, times(4)).sendJson(eq("updateIndexQueue"), jsonCaptor.capture());
    List<MetadataRecord> metadataRecords = jsonCaptor.getAllValues().stream().map(json -> jsonMapper.readValue(json, MetadataRecord.class)).toList();

    assertEquals(Action.FLOAT_MERGE, metadataRecords.get(0).getAction());
    assertEquals("aoml/123/profiles/R123_001.nc", metadataRecords.get(0).getFile());
    assertEquals("aoml", metadataRecords.get(0).getDac());
    assertEquals("123", metadataRecords.get(0).getFloatId());
    assertNotNull(metadataRecords.get(0).getActionTimestamp());

    assertEquals(Action.FLOAT_MERGE, metadataRecords.get(1).getAction());
    assertEquals("aoml/123/profiles/R123_001D.nc", metadataRecords.get(1).getFile());
    assertEquals("aoml", metadataRecords.get(1).getDac());
    assertEquals("123", metadataRecords.get(1).getFloatId());
    assertNotNull(metadataRecords.get(1).getActionTimestamp());

    assertEquals(Action.FLOAT_MERGE, metadataRecords.get(2).getAction());
    assertEquals("aoml/123/profiles/R123_002.nc", metadataRecords.get(2).getFile());
    assertEquals("aoml", metadataRecords.get(2).getDac());
    assertEquals("123", metadataRecords.get(2).getFloatId());
    assertNotNull(metadataRecords.get(2).getActionTimestamp());

    assertEquals(Action.UPDATE, metadataRecords.get(3).getAction());
    assertEquals("aoml/123/123_prof.nc", metadataRecords.get(3).getFile());
    assertEquals("aoml", metadataRecords.get(3).getDac());
    assertEquals("123", metadataRecords.get(3).getFloatId());
    assertNotNull(metadataRecords.get(3).getActionTimestamp());
    assertNotNull(metadataRecords.get(3).getDateUpdate());
    assertNotNull(metadataRecords.get(3).getDate());

    verify(outputFileStore).uploadLocalFile(eq(outputPathCaptor.getValue()), eq("/foo/bar/dac/aoml/123/123_prof.nc"));

  }
}