package edu.colorado.cires.argonaut.processor.core.transform;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import edu.colorado.cires.argonaut.core.netcdf.technical.v31.ArgoTechnicalV31;
import edu.colorado.cires.argonaut.core.netcdf.trajectory.v31.ArgoTrajectoryV31;
import edu.colorado.cires.argonaut.messaging.core.databind.ArgoFileType;
import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord;
import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord.Action;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class NetCdfMetadataRecordTest {

  private static final String TEST_FILE = "file";
  private static final String TEST_DAC = "dac";
  private static final String FLOAT_ID = "float-id";
  private static final String PROFILER_TYPE = "profiler-type";
  private static final String INSTITUTION = "institution";
  private static final Instant DATE_UPDATE = Instant.now();

  record TestCase(ArgoTrajectoryV31 input, MetadataRecord expected) {}

  private static List<Named<TestCase>> trajectoryTestCases() {
    Function<Consumer<ArgoTrajectoryV31>, ArgoTrajectoryV31> createTrajectory = (consumer) -> {
      ArgoTrajectoryV31 traj = mock(ArgoTrajectoryV31.class);
      when(traj.getPlatformNumber()).thenReturn(FLOAT_ID);
      when(traj.getWmoInstrumentType()).thenReturn(PROFILER_TYPE);
      when(traj.getDataCenter()).thenReturn(INSTITUTION);
      when(traj.getDateUpdate()).thenReturn(DATE_UPDATE);

      int nMeasurements = 10;
      when(traj.getNMeasurements()).thenReturn(nMeasurements);
      for (int i = 0; i < nMeasurements; i++) {
        when(traj.getLatitude(i)).thenReturn(Double.valueOf(i));
        when(traj.getLongitude(i)).thenReturn(Double.valueOf(i * -1));
      }

      consumer.accept(traj);

      return traj;
    };

    Supplier<MetadataRecord.Builder> createExpectedRecord = () -> MetadataRecord.builder()
      .withAction(Action.UPDATE)
      .withFileType(ArgoFileType.TRAJECTORY)
      .withFile(TEST_FILE)
      .withDac(TEST_DAC)
      .withFloatId(FLOAT_ID)
      .withProfilerType(PROFILER_TYPE)
      .withInstitution(INSTITUTION)
      .withDateUpdate(DATE_UPDATE)
      .withLatitudeMax(9d)
      .withLatitudeMin(0d)
      .withLongitudeMax(0d)
      .withLongitudeMin(-9d);

    return List.of(
      Named.of("happy path", new TestCase(
        createTrajectory.apply(argoTrajectoryV31 -> {}),
        createExpectedRecord.get()
          .build()
      )),
      Named.of("null latitude", new TestCase(
        createTrajectory.apply(argoTrajectoryV31 -> when(argoTrajectoryV31.getLatitude(anyInt())).thenReturn(null)),
        createExpectedRecord.get()
          .withLatitudeMax(Double.NaN)
          .withLatitudeMin(Double.NaN)
          .build()
      )),
      Named.of("null longitude", new TestCase(
        createTrajectory.apply(argoTrajectoryV31 -> when(argoTrajectoryV31.getLongitude(anyInt())).thenReturn(null)),
        createExpectedRecord.get()
          .withLongitudeMax(Double.NaN)
          .withLongitudeMin(Double.NaN)
          .build()
      ))
    );
  }

  @ParameterizedTest
  @MethodSource("trajectoryTestCases")
  void fromV31Trajectory(TestCase testCase) {
    MetadataRecord actual = NetCdfMetadataRecord.fromV31Trajectory(TEST_FILE, TEST_DAC, testCase.input);

    Comparator<Double> doubleComparator = (a, b) -> {
      if (Double.isNaN(a) && Double.isNaN(b)) {
        return 0;
      }

      return a.compareTo(b);
    };

    assertThat(actual)
      .usingRecursiveComparison()
      .ignoringFields("actionTimestamp")
      .withComparatorForType(doubleComparator, Double.class)
      .isEqualTo(testCase.expected);

    assertTrue(actual.getActionTimestamp().isBefore(Instant.now()));
  }

  @Test
  void fromV31Technical() {
    ArgoTechnicalV31 traj = mock(ArgoTechnicalV31.class);
    when(traj.getPlatformNumber()).thenReturn(FLOAT_ID);
    when(traj.getDataCenter()).thenReturn(INSTITUTION);
    when(traj.getDateUpdate()).thenReturn(DATE_UPDATE);

    MetadataRecord actual = NetCdfMetadataRecord.fromV31Technical(TEST_FILE, TEST_DAC, traj);

    assertThat(actual)
      .usingRecursiveComparison()
      .ignoringFields("actionTimestamp")
      .isEqualTo(
        MetadataRecord.builder()
          .withAction(Action.UPDATE)
          .withFileType(ArgoFileType.TECHNICAL_DATA)
          .withFile(TEST_FILE)
          .withDac(TEST_DAC)
          .withFloatId(FLOAT_ID)
          .withInstitution(INSTITUTION)
          .withDateUpdate(DATE_UPDATE)
          .build()
      );
    assertTrue(actual.getActionTimestamp().isBefore(Instant.now()));
  }
}