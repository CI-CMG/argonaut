package edu.colorado.cires.argonaut.processor.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.colorado.cires.argonaut.core.merge.synthetic.DefaultSyntheticProfileMerger;
import edu.colorado.cires.argonaut.core.merge.synthetic.SyntheticProfileMerger;
import edu.colorado.cires.argonaut.file.core.FileStore;
import edu.colorado.cires.argonaut.messaging.core.databind.ArgoOcean;
import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord;
import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord.Action;
import edu.colorado.cires.argonaut.messaging.core.databind.NcSubmissionMessage.FileType;
import edu.colorado.cires.argonaut.messaging.core.databind.ProfileOperation;
import edu.colorado.cires.argonaut.messaging.core.queue.MessageSender;
import edu.colorado.cires.argonaut.messaging.core.util.ArgonautJsonMapperFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import tools.jackson.databind.json.JsonMapper;

public class DefaultSyntheticProfileProcessorTest {

  private final Path workingDir = Paths.get("target/working");
  private final JsonMapper jsonMapper = ArgonautJsonMapperFactory.getJsonMapper();
  private static final String updateIndexQueue = "updateIndexQueue";

  @BeforeEach
  public void setup() {
    FileUtils.deleteQuietly(workingDir.toFile());
  }

  @AfterEach
  public void cleanup() {
    FileUtils.deleteQuietly(workingDir.toFile());
  }

  @Test
  public void test() throws Exception {

    ProfileOperation profileOperation = ProfileOperation.builder()
        .withDac("meds")
        .withFloatId("4902691")
        .withFiles(Arrays.asList(
            "meds/4902691/profiles/R4902691_034.nc",
            "meds/4902691/profiles/BR4902691_034.nc",
            "meds/4902691/4902691_meta.nc"
        ))
        .build();

    SyntheticProfileMerger syntheticProfileMerger = spy(new DefaultSyntheticProfileMerger());
    MessageSender messageSender = mock(MessageSender.class);
    FileStore fileStore = mock(FileStore.class);
    GeoFilter geoFilter = mock(GeoFilter.class);

    when(fileStore.appendToPath(any(String.class), any(String[].class))).thenAnswer(invocation -> {
      List<String> varArgs = new ArrayList<>(invocation.getArguments().length - 1);
      for (int i = 1; i < invocation.getArguments().length; i++) {
        varArgs.add(invocation.getArgument(i, String.class));
      }
      String joined = String.join("/", varArgs);
      return invocation.getArgument(0, String.class) + "/" + joined;
    });
    when(fileStore.getRoot()).thenReturn("/foo/bar");
    when(fileStore.getFileName(any())).thenAnswer(invocation -> {
      String[] parts = invocation.getArgument(0, String.class).split("/");
      return parts[parts.length - 1];
    });

    ArgumentCaptor<Path> pathCaptor = ArgumentCaptor.forClass(Path.class);
    doAnswer(invocationOnMock -> {
      String path = invocationOnMock.getArgument(0, String.class);
      Path localPath = invocationOnMock.getArgument(1, Path.class);
      String[] parts = path.split("/");
      Files.copy(Paths.get("src/test/resources/synth_proc").resolve(parts[parts.length - 1]), localPath);
      return null;
    }).when(fileStore).downloadLocalFile(any(), pathCaptor.capture());

    when(geoFilter.determineArgoOcean(anyDouble(), anyDouble())).thenReturn(ArgoOcean.INDIAN_OCEAN);


    DefaultSyntheticProfileProcessor processor = new DefaultSyntheticProfileProcessor();
    processor.setLocalTempDir(workingDir);
    processor.setJsonMapper(jsonMapper);
    processor.setSyntheticProfileMerger(syntheticProfileMerger);
    processor.setMessageSender(messageSender);
    processor.setOutputFileStore(fileStore);
    processor.setUpdateIndexQueue(updateIndexQueue);
    processor.setGeoFilter(geoFilter);

    processor.generateSyntheticProfile(profileOperation);

    Path tempDir = pathCaptor.getValue().getParent();

    verify(fileStore, times(1)).downloadLocalFile(eq("/foo/bar/dac/meds/4902691/profiles/R4902691_034.nc"), eq(tempDir.resolve("R4902691_034.nc")));
    verify(fileStore, times(1)).downloadLocalFile(eq("/foo/bar/dac/meds/4902691/profiles/BR4902691_034.nc"), eq(tempDir.resolve("BR4902691_034.nc")));
    verify(fileStore, times(1)).downloadLocalFile(eq("/foo/bar/dac/meds/4902691/4902691_meta.nc"), eq(tempDir.resolve("4902691_meta.nc")));

    verify(syntheticProfileMerger, times(1)).mergeProfiles(eq(tempDir.resolve("R4902691_034.nc")), eq(tempDir.resolve("BR4902691_034.nc")),
        eq(tempDir.resolve("4902691_meta.nc")), eq(tempDir.resolve("SR4902691_034.nc")));

    ArgumentCaptor<String> jsonCaptor = ArgumentCaptor.forClass(String.class);
    verify(messageSender, times(4)).sendJson(eq(updateIndexQueue), jsonCaptor.capture());

    Instant now = Instant.now();
    Map<String, MetadataRecord> jsonMap = new HashMap<>();
    for (String json : jsonCaptor.getAllValues()) {
      MetadataRecord metadataRecord = MetadataRecord.builder(jsonMapper.readValue(json, MetadataRecord.class))
          .withActionTimestamp(now) //override dynamic timestamp to allow for equality assertions
          .build();
      if(metadataRecord.getFileType() == FileType.BGC_ARGO_SYNTH_PROFILE){
        metadataRecord = MetadataRecord.builder(metadataRecord).withDateUpdate(now).build();
      }
      jsonMap.put(metadataRecord.getFile(), metadataRecord);
    }

    assertEquals(MetadataRecord.builder()
        .withActionTimestamp(now)
        .withFile("meds/4902691/profiles/R4902691_034.nc")
        .withAction(Action.SYNTHETIC_MERGE)
        .withDac("meds")
        .withFloatId("4902691")
        .withFileType(FileType.CORE_ARGO_PROFILE)
        .build(), jsonMap.get("meds/4902691/profiles/R4902691_034.nc"));

    assertEquals(MetadataRecord.builder()
        .withActionTimestamp(now)
        .withFile("meds/4902691/profiles/BR4902691_034.nc")
        .withAction(Action.SYNTHETIC_MERGE)
        .withDac("meds")
        .withFloatId("4902691")
        .withFileType(FileType.B_ARGO_PROFILE)
        .build(), jsonMap.get("meds/4902691/profiles/BR4902691_034.nc"));

    assertEquals(MetadataRecord.builder()
        .withActionTimestamp(now)
        .withFile("meds/4902691/4902691_meta.nc")
        .withAction(Action.SYNTHETIC_MERGE)
        .withDac("meds")
        .withFloatId("4902691")
        .withFileType(FileType.METADATA)
        .build(), jsonMap.get("meds/4902691/4902691_meta.nc"));


    assertEquals(MetadataRecord.builder()
        .withActionTimestamp(now)
        .withFile("meds/4902691/profiles/SR4902691_034.nc")
        .withDate(Instant.parse("2026-04-15T11:33:59.999Z"))
        .withDateUpdate(now)
        .withAction(Action.UPDATE)
        .withDac("meds")
        .withFloatId("4902691")
        .withDirection("A")
        .withCycleNumber("034")
        .withParameterDataMode("R")
        .withLatitude(46.97475814819336)
        .withLongitude(-128.1387481689453)
        .withOcean(ArgoOcean.INDIAN_OCEAN)
        .withProfilerType("834")
        .withFileType(FileType.BGC_ARGO_SYNTH_PROFILE)
        .withInstitution("ME")
        .build(), jsonMap.get("meds/4902691/profiles/SR4902691_034.nc"));

  }
}