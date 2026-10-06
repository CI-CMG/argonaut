package edu.colorado.cires.argonaut.processor.core;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import edu.colorado.cires.argonaut.file.core.FileStore;
import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord;
import edu.colorado.cires.argonaut.metadata.core.DefaultIndexPageRequest;
import edu.colorado.cires.argonaut.metadata.core.MetadataRecordPage;
import edu.colorado.cires.argonaut.metadata.core.MetadataStore;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.zip.GZIPInputStream;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.OngoingStubbing;

class TechnicalIndexProcessorTest {

  private static final Random RANDOM = new Random();
  private static final Supplier<Integer> RANDOM_POSITIVE_INT = () -> RANDOM.nextInt(1, 100);
  private static final Supplier<String> RANDOM_STRING = () -> UUID.randomUUID().toString();
  private static final Path tempDir = Paths.get("target/test-temp");
  private static final Path outputDir = Paths.get("target/test-output");

  @BeforeEach
  void setUp() {
    deleteDirs();
  }

  @AfterEach
  void tearDown() {
    deleteDirs();
  }

  private static void deleteDirs() {
    FileUtils.deleteQuietly(tempDir.toFile());
    FileUtils.deleteQuietly(outputDir.toFile());
  }

  @Test
  void queryPage() throws IOException {
    String title = "Technical directory file of the Argo Global Data Assembly Center";
    String description = "The directory file describes all technical files of the argo ARGO GDAC data store.";
    String fileNameBase = "ar_index_global_tech";
    String project = RANDOM_STRING.get();
    String formatVersion = RANDOM_STRING.get();
    String gdacNode = RANDOM_STRING.get();
    int pageSize = RANDOM_POSITIVE_INT.get();
    int nPages = RANDOM_POSITIVE_INT.get();

    List<MetadataRecord> expectedRecords = new ArrayList<>(nPages * pageSize);

    MetadataStore metadataStore = mock(MetadataStore.class);
    OngoingStubbing<MetadataRecordPage> when = when(metadataStore.getTechnicalIndexPage(any()));
    for (int i = 0; i < nPages; i++) {
      int finalI = i;

      DefaultIndexPageRequest request = DefaultIndexPageRequest.builder()
        .withPageNumber(finalI + 1)
        .withPageSize(pageSize)
        .build();

      when = when.thenAnswer(inv -> {
        MetadataRecordPage page = mock(MetadataRecordPage.class);
        when(page.getPage()).thenReturn(IntStream.range(0, pageSize).boxed()
          .map(ignored -> MetadataRecord.builder()
            .withFile(RANDOM_STRING.get())
            .withInstitution(RANDOM_STRING.get())
            .withDateUpdate(Instant.now())
            .build())
          .toList());
        when(page.getPageNumber()).thenReturn(finalI + 1);
        when(page.getPageSize()).thenReturn(pageSize);
        when(page.getTotalPages()).thenReturn(nPages);
        when(page.getTotalRecords()).thenReturn((long) nPages * pageSize);
        when(page.getNextPage()).thenAnswer(ignored -> {
          if (finalI == nPages - 1) {
            return Optional.empty();
          }

          return Optional.of(DefaultIndexPageRequest.builder(request)
            .withPageNumber(request.getPageNumber() + 1)
            .withPageSize(pageSize)
            .build());
        });

        expectedRecords.addAll(page.getPage());

        return page;
      });
    }

    AtomicReference<String> atomicFileContent = new AtomicReference<>("");
    FileStore fileStore = mock(FileStore.class);
    when(fileStore.getRoot()).thenReturn(outputDir.toString());
    when(fileStore.appendToPath(anyString(), anyString())).thenAnswer(inv -> Paths.get(inv.getArgument(0), (String) inv.getArgument(1)).toString());
    doAnswer(inv -> {
      File file = ((Path) inv.getArgument(0)).toFile();
      assertEquals("%s.txt".formatted(fileNameBase), file.getName());
      atomicFileContent.set(
        FileUtils.readFileToString(file, StandardCharsets.UTF_8));
      return null;
    }).when(fileStore).uploadLocalFile(any(), anyString());

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    doAnswer(inv -> outputStream).when(fileStore).getOutputStream(anyString());

    TechnicalIndexProcessor technicalIndexProcessor = new TechnicalIndexProcessor();
    technicalIndexProcessor.setPageSize(1);
    technicalIndexProcessor.setFormatVersion(formatVersion);
    technicalIndexProcessor.setGdacNode(gdacNode);
    technicalIndexProcessor.setProject(project);
    technicalIndexProcessor.setLocalTempDir(tempDir);
    technicalIndexProcessor.setMetadataStore(metadataStore);
    technicalIndexProcessor.setOutputFileStore(fileStore);

    technicalIndexProcessor.generateIndex();

    Consumer<byte[]> assertCSVContent = bytes -> {
      try (
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(inputStreamReader)
      ) {
        List<String> lines = new ArrayList<>();

        String line;
        while ((line = reader.readLine()) != null) {
          lines.add(line);
        }

        List<String> header = lines.subList(0, 6);
        assertEquals("# Title : %s".formatted(title), header.getFirst());
        assertEquals("# Description : %s".formatted(description), lines.get(1));
        assertEquals("# Project : %s".formatted(project), lines.get(2));
        assertEquals("# Format version : %s".formatted(formatVersion), lines.get(3));
        String[] split = lines.get(4).split(" : ");
        assertEquals("# Date of update", split[0]);
        assertDoesNotThrow(() -> LocalDateTime.parse(split[1], DateTimeFormatter.ofPattern("yyyMMddHHmmss")));
        assertEquals("# GDAC node : %s".formatted(gdacNode), lines.get(5));

        String columnHeaders = lines.get(6);
        assertEquals("file,institution,date_update", columnHeaders);
        List<String> dataRows = lines.subList(7, lines.size());
        assertEquals(expectedRecords.size(), dataRows.size());

        for (int i = 0; i < expectedRecords.size(); i++) {
          MetadataRecord metadataRecord = expectedRecords.get(i);

          LocalDateTime dateUpdate = LocalDateTime.ofInstant(metadataRecord.getDateUpdate(), ZoneId.of("UTC"));
          String formattedDate = "%d%02d%02d%02d%02d%02d".formatted(
            dateUpdate.getYear(),
            dateUpdate.getMonthValue(),
            dateUpdate.getDayOfMonth(),
            dateUpdate.getHour(),
            dateUpdate.getMinute(),
            dateUpdate.getSecond()
          );
          assertEquals("%s,%s,%s".formatted(metadataRecord.getFile(), metadataRecord.getInstitution(), formattedDate), dataRows.get(i));
        }
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    };

    assertCSVContent.accept(atomicFileContent.get().getBytes(StandardCharsets.UTF_8));

    try (GZIPInputStream gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(outputStream.toByteArray()))) {
      assertCSVContent.accept(gzipInputStream.readAllBytes());
    }
  }
}