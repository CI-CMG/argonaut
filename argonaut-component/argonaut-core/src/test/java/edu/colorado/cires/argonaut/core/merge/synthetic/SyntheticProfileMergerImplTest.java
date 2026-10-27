package edu.colorado.cires.argonaut.core.merge.synthetic;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class SyntheticProfileMergerImplTest {

  private final SyntheticProfileMerger merger = new SyntheticProfileMergerImpl();
  private final Path testPath = Paths.get("target/test-output");

  @BeforeEach
  void setUp() throws IOException {
    cleanTestPath();
    FileUtils.forceMkdir(testPath.toFile());
  }

  @AfterEach
  void tearDown() {
    cleanTestPath();
  }

  private void cleanTestPath() {
    FileUtils.deleteQuietly(testPath.toFile());
  }

  private record TestCase(String cProfilePath, String bProfilePath, String metaPath, String expectedPath, boolean expectError, @Nullable String expectedErrorMessage) {}

  private static List<Named<TestCase>> testCases() {
    return List.of(
      // non-existent metadata file = no merge is performed
      Named.of("no metadata file", new TestCase(
        "src/test/resources/dac/meds/4902691/profiles/R4902691_034.nc",
        "src/test/resources/dac/meds/4902691/profiles/BR4902691_034.nc",
        "test.nc",
        null,
        true,
        "test.nc does not exist, merge cannot be performed"
      )),
      // core files non-existent = no merge is performed
      Named.of("metadata file", new TestCase(
        "cTest.nc",
        "bTest.nc",
        "src/test/resources/dac/meds/4902691/4902691_meta.nc",
        null,
        true,
        "cTest.nc and bTest.nc do not exist, merge cannot be performed"
      )),
      // output for 4902691 should be byte-equal
      Named.of("4902691", new TestCase(
        "src/test/resources/dac/meds/4902691/profiles/R4902691_034.nc",
        "src/test/resources/dac/meds/4902691/profiles/BR4902691_034.nc",
        "src/test/resources/dac/meds/4902691/4902691_meta.nc",
        "src/test/resources/dac/meds/4902691/4902691_Sprof.nc",
        false,
        null
      ))
    );
  }

  @ParameterizedTest
  @MethodSource("testCases")
  void mergeProfiles(TestCase testCase) throws IOException {
    Path outputPath = testPath.resolve("output.nc");

    Executable runMerger = () -> merger.mergeProfiles(Paths.get(testCase.cProfilePath), Paths.get(testCase.bProfilePath),
      Paths.get(testCase.metaPath), outputPath);

    if (testCase.expectError) {
      Exception exception = assertThrows(Exception.class, runMerger);
      assertEquals(testCase.expectedErrorMessage, exception.getMessage());
      return;
    }

    assertDoesNotThrow(runMerger);

    try (
      InputStream expectedInputStream = Files.newInputStream(Paths.get(testCase.expectedPath));
      InputStream actualInputStream = Files.newInputStream(outputPath)
    ) {
      assertArrayEquals(expectedInputStream.readAllBytes(), actualInputStream.readAllBytes());
    }
  }
}