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
import java.math.BigDecimal;
import java.math.RoundingMode;
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
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.zip.GZIPInputStream;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.stubbing.OngoingStubbing;

class BaseIndexProcessorTest {

  private static final Random RANDOM = new Random();
  private static final Supplier<Integer> RANDOM_POSITIVE_INT = () -> RANDOM.nextInt(1, 100);
  private static final Supplier<Double> RANDOM_LATITUDE = () -> RANDOM.nextDouble(-90, 90);
  private static final Supplier<Double> RANDOM_LONGITUDE = () -> RANDOM.nextDouble(-180, 180);
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

  record TestCase(
    String title,
    String description,
    String fileNameBase,
    Supplier<BaseIndexProcessor> factory,
    Function<MetadataStore, OngoingStubbing<MetadataRecordPage>> datastoreStub,
    String expectedHeaders,
    Function<MetadataRecord, String> toExpectedString
  ) {}

  private static List<Named<TestCase>> testCases() {
    Function<Instant, String> formatDate = instant -> {
      LocalDateTime dateUpdate = LocalDateTime.ofInstant(instant, ZoneId.of("UTC"));
      return "%d%02d%02d%02d%02d%02d".formatted(
        dateUpdate.getYear(),
        dateUpdate.getMonthValue(),
        dateUpdate.getDayOfMonth(),
        dateUpdate.getHour(),
        dateUpdate.getMinute(),
        dateUpdate.getSecond()
      );
    };

    Function<Double, String> formatDouble = value -> BigDecimal.valueOf(value).setScale(3, RoundingMode.DOWN).toString();

    return List.of(
      Named.of("technical", new TestCase(
        "Technical directory file of the Argo Global Data Assembly Center",
        "The directory file describes all technical files of the argo ARGO GDAC data store.",
        "ar_index_global_tech",
        TechnicalIndexProcessor::new,
        metadataStore -> when(metadataStore.getTechnicalIndexPage(any())),
        "file,institution,date_update",
        metadataRecord -> "%s,%s,%s".formatted(metadataRecord.getFile(), metadataRecord.getInstitution(), formatDate.apply(metadataRecord.getDateUpdate()))
      )),
      Named.of("trajectory", new TestCase(
        "Trajectory directory file of the Argo Global Data Assembly Center",
        "The directory file describes all trajectory files of the ARGO GDAC data store.",
        "ar_index_global_traj",
        TrajectoryIndexProcessor::new,
        metadataStore -> when(metadataStore.getTrajectoryIndexPage(any())),
        "file,latitude_max,latitude_min,longitude_max,longitude_min,profiler_type,institution,date_update",
        metadataRecord -> "%s,%s,%s,%s,%s,%s,%s,%s".formatted(
          metadataRecord.getFile(),
          formatDouble.apply(metadataRecord.getLatitudeMax()),
          formatDouble.apply(metadataRecord.getLatitudeMin()),
          formatDouble.apply(metadataRecord.getLongitudeMax()),
          formatDouble.apply(metadataRecord.getLongitudeMin()),
          metadataRecord.getProfilerType(),
          metadataRecord.getInstitution(),
          formatDate.apply(metadataRecord.getDateUpdate())
        )
      ))
    );
  }

  @ParameterizedTest
  @MethodSource("testCases")
  void queryPage(TestCase testCase) throws IOException {
    String title = testCase.title();
    String description = testCase.description();
    String fileNameBase = testCase.fileNameBase();
    String project = RANDOM_STRING.get();
    String formatVersion = RANDOM_STRING.get();
    String gdacNode = RANDOM_STRING.get();
    int pageSize = RANDOM_POSITIVE_INT.get();
    int nPages = RANDOM_POSITIVE_INT.get();

    List<MetadataRecord> expectedRecords = new ArrayList<>(nPages * pageSize);

    MetadataStore metadataStore = mock(MetadataStore.class);
    OngoingStubbing<MetadataRecordPage> when = testCase.datastoreStub.apply(metadataStore);
    for (int i = 0; i < nPages; i++) {
      int finalI = i;

      DefaultIndexPageRequest request = DefaultIndexPageRequest.builder()
        .withPageNumber(finalI + 1)
        .withPageSize(pageSize)
        .build();

      when = when.thenAnswer(inv -> {
        MetadataRecordPage page = mock(MetadataRecordPage.class);
        when(page.getPage()).thenReturn(IntStream.range(0, pageSize).boxed()
          .map(ii -> {
            double latitudeMax = RANDOM_LATITUDE.get();
            double longitudeMax = RANDOM_LONGITUDE.get();
            return MetadataRecord.builder()
              .withFile(RANDOM_STRING.get())
              .withInstitution(RANDOM_STRING.get())
              .withDateUpdate(Instant.now().plusSeconds(ii))
              .withLatitudeMax(latitudeMax)
              .withLatitudeMin(latitudeMax - 1)
              .withLongitudeMax(longitudeMax)
              .withLongitudeMin(longitudeMax - 1)
              .withProfilerType(RANDOM_STRING.get())
              .build();
          })
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

    BaseIndexProcessor indexProcessor = testCase.factory().get();
    indexProcessor.setPageSize(1);
    indexProcessor.setFormatVersion(formatVersion);
    indexProcessor.setGdacNode(gdacNode);
    indexProcessor.setProject(project);
    indexProcessor.setLocalTempDir(tempDir);
    indexProcessor.setMetadataStore(metadataStore);
    indexProcessor.setOutputFileStore(fileStore);

    indexProcessor.generateIndex();

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
        assertEquals(testCase.expectedHeaders(), columnHeaders);
        List<String> dataRows = lines.subList(7, lines.size());
        assertEquals(expectedRecords.size(), dataRows.size());

        for (int i = 0; i < expectedRecords.size(); i++) {
          assertEquals(testCase.toExpectedString().apply(expectedRecords.get(i)), dataRows.get(i));
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