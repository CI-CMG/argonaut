package edu.colorado.cires.argonaut.processor.core;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.colorado.cires.argonaut.core.merge.multiprof.LocalPathSupplier;
import edu.colorado.cires.argonaut.core.merge.multiprof.MultiProfileMerger;
import edu.colorado.cires.argonaut.file.core.FileStore;
import edu.colorado.cires.argonaut.messaging.core.databind.ArgoFileType;
import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord;
import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord.Action;
import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord.FileStatus;
import edu.colorado.cires.argonaut.messaging.core.databind.ProfileOperation;
import edu.colorado.cires.argonaut.messaging.core.queue.MessageSender;
import edu.colorado.cires.argonaut.messaging.core.util.ArgonautJsonMapperFactory;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import tools.jackson.databind.json.JsonMapper;

public class DefaultLatestProfileMergeProcessorTest {

  private final Path localTempDir = Paths.get("target/latest");
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
    FileStore outputFileStore = mock(FileStore.class);
    MessageSender messageSender = mock(MessageSender.class);
    MultiProfileMerger merger = mock(MultiProfileMerger.class);
    Instant now = Instant.now();

    String fileName = "D20260923";
    List<String> filesInDir = Arrays.asList(
        "D20260923_prof_0.nc",
        "D20260923_prof_1.nc",
        "D20260923_prof_2.nc",
        "D20260922_prof_0.nc",
        "R20260923_prof_0.nc",
        "R20260922_prof_0.nc"
    );

    when(outputFileStore.getRoot()).thenReturn("/foo/bar");
    when(outputFileStore.appendToPath(eq("/foo/bar"), eq("latest_data"))).thenReturn("/foo/bar/latest_data");
    when(outputFileStore.listFileNamesInDirectory(eq("/foo/bar/latest_data"))).thenReturn(filesInDir);
    when(outputFileStore.appendToPath(eq("/foo/bar/latest_data"), eq("D20260923_prof_0.nc"))).thenReturn("/foo/bar/latest_data/D20260923_prof_0.nc");
    when(outputFileStore.appendToPath(eq("/foo/bar/latest_data"), eq("D20260923_prof_1.nc"))).thenReturn("/foo/bar/latest_data/D20260923_prof_1.nc");
    when(outputFileStore.appendToPath(eq("/foo/bar/latest_data"), eq("D20260923_prof_2.nc"))).thenReturn("/foo/bar/latest_data/D20260923_prof_2.nc");
    when(outputFileStore.appendToPath(eq("/foo/bar"), eq("latest_data"), eq("D20260923_prof_0.nc"))).thenReturn(
        "/foo/bar/latest_data/D20260923_prof_0.nc");
    when(outputFileStore.appendToPath(eq("/foo/bar"), eq("latest_data"), eq("D20260923_prof_1.nc"))).thenReturn(
        "/foo/bar/latest_data/D20260923_prof_1.nc");
    when(outputFileStore.appendToPath(eq("/foo/bar"), eq("dac"), eq("aoml/1234/profiles/D1234_001.nc"))).thenReturn(
        "/foo/bar/dac/aoml/1234/profiles/D1234_001.nc");
    when(outputFileStore.appendToPath(eq("/foo/bar"), eq("dac"), eq("csio/222/profiles/D222_001.nc"))).thenReturn(
        "/foo/bar/dac/csio/222/profiles/D222_001.nc");
    when(outputFileStore.appendToPath(eq("/foo/bar"), eq("dac"), eq("csio/222/profiles/D222_002.nc"))).thenReturn(
        "/foo/bar/dac/csio/222/profiles/D222_002.nc");
    when(outputFileStore.appendToPath(eq("/foo/bar"), eq("dac"), eq("aoml/1234/profiles/D1234_001.nc"))).thenReturn(
        "/foo/bar/dac/aoml/1234/profiles/D1234_001.nc");

    DefaultLatestProfileMergeProcessor processor = new DefaultLatestProfileMergeProcessor();
    processor.setOutputFileStore(outputFileStore);
    processor.setLocalTempDir(localTempDir);
    processor.setMessageSender(messageSender);
    processor.setUpdateIndexQueue("updateIndexQueue");
    processor.setJsonMapper(jsonMapper);
    processor.setMerger(merger);
    processor.setMaxProfilesPerFile(2);
    processor.setNowGenerator(() -> now);

    UUID traceId = UUID.randomUUID();

    List<MetadataRecord> records = Arrays.asList(
        MetadataRecord.builder()
            .withDac("aoml")
            .withFloatId("1234")
            .withFile("aoml/1234/profiles/D1234_001.nc")
            .withFileName("D1234_001.nc")
            .withFileStatus(FileStatus.ACTIVE)
            .withCycleNumber("001")
            .withActionTimestamp(Instant.parse("2026-09-23T12:00:00Z"))
            .withDate(Instant.parse("2022-09-23T12:00:00Z"))
            .withFileType(ArgoFileType.PROFILE_CORE)
            .build(),
        MetadataRecord.builder()
            .withDac("bodc")
            .withFloatId("111")
            .withFile("bodc/111/profiles/D111_001.nc")
            .withFileName("D111_001.nc")
            .withFileStatus(FileStatus.REMOVED)
            .withCycleNumber("001")
            .withActionTimestamp(Instant.parse("2026-09-23T02:00:00Z"))
            .withDate(Instant.parse("2021-09-23T12:00:00Z"))
            .withFileType(ArgoFileType.PROFILE_CORE)
            .build(),
        MetadataRecord.builder()
            .withDac("csio")
            .withFloatId("222")
            .withFile("csio/222/profiles/D222_001.nc")
            .withFileName("D222_001.nc")
            .withFileStatus(FileStatus.ACTIVE)
            .withCycleNumber("001")
            .withActionTimestamp(Instant.parse("2026-09-23T13:00:00Z"))
            .withDate(Instant.parse("2020-09-23T12:00:00Z"))
            .withFileType(ArgoFileType.PROFILE_CORE)
            .build(),
        MetadataRecord.builder()
            .withDac("csio")
            .withFloatId("222")
            .withFile("csio/222/profiles/D222_002.nc")
            .withFileName("D222_002.nc")
            .withFileStatus(FileStatus.ACTIVE)
            .withCycleNumber("002")
            .withActionTimestamp(Instant.parse("2026-09-23T13:30:00Z"))
            .withDate(Instant.parse("2020-09-23T12:30:00Z"))
            .withFileType(ArgoFileType.PROFILE_CORE)
            .build()
    );

    ProfileOperation message = ProfileOperation.builder()
        .withTraceId(traceId)
        .withFileName(fileName)
        .withFiles(records)
        .build();

    processor.merge(message);

    verify(outputFileStore, times(1)).delete(eq("/foo/bar/latest_data/D20260923_prof_0.nc"));
    verify(outputFileStore, times(1)).delete(eq("/foo/bar/latest_data/D20260923_prof_1.nc"));
    verify(outputFileStore, times(1)).delete(eq("/foo/bar/latest_data/D20260923_prof_2.nc"));

    ArgumentCaptor<List<LocalPathSupplier>> localPathSupplierCaptor0 = ArgumentCaptor.forClass(List.class);
    verify(merger, times(1)).mergeProfiles(localPathSupplierCaptor0.capture(), eq(Arrays.asList("PRES", "TEMP", "PSAL")), argThat(
        (ArgumentMatcher<Path>) path -> path.getFileName().toString().equals("D20260923_prof_0.nc")));

    ArgumentCaptor<List<LocalPathSupplier>> localPathSupplierCaptor1 = ArgumentCaptor.forClass(List.class);
    verify(merger, times(1)).mergeProfiles(localPathSupplierCaptor1.capture(), eq(Arrays.asList("PRES", "TEMP", "PSAL")), argThat(
        (ArgumentMatcher<Path>) path -> path.getFileName().toString().equals("D20260923_prof_1.nc")));

    List<LocalPathSupplier> localPathSuppliers0 = localPathSupplierCaptor0.getValue();
    assertEquals(2, localPathSuppliers0.size());
    localPathSuppliers0.forEach(LocalPathSupplier::prepare);
    localPathSuppliers0.forEach(LocalPathSupplier::cleanUp);
    localPathSuppliers0.forEach(lps -> assertNotNull(lps.getLocalPath()));
    assertEquals("D222_001.nc", localPathSuppliers0.get(0).getFileName());
    assertEquals("D1234_001.nc", localPathSuppliers0.get(1).getFileName());

    List<LocalPathSupplier> localPathSuppliers1 = localPathSupplierCaptor1.getValue();
    assertEquals(1, localPathSuppliers1.size());
    localPathSuppliers1.forEach(LocalPathSupplier::prepare);
    localPathSuppliers1.forEach(LocalPathSupplier::cleanUp);
    localPathSuppliers1.forEach(lps -> assertNotNull(lps.getLocalPath()));
    assertEquals("D222_002.nc", localPathSuppliers1.get(0).getFileName());

    verify(outputFileStore).downloadLocalFile(eq("/foo/bar/dac/csio/222/profiles/D222_001.nc"), any());
    verify(outputFileStore).downloadLocalFile(eq("/foo/bar/dac/aoml/1234/profiles/D1234_001.nc"), any());
    verify(outputFileStore).downloadLocalFile(eq("/foo/bar/dac/csio/222/profiles/D222_002.nc"), any());

    for (MetadataRecord record : records) {
      verify(messageSender, times(1)).sendJson(eq("updateIndexQueue"), eq(jsonMapper.writeValueAsString(MetadataRecord.builder(record)
          .withAction(record.getFileStatus() == FileStatus.REMOVED ? Action.LATEST_MERGE_REMOVE : Action.LATEST_MERGE)
          .withActionTimestamp(now)
          .withTraceId(traceId)
          .build())));
    }


  }


}