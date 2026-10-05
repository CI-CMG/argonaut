package edu.colorado.cires.argonaut.processor.core.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import edu.colorado.cires.argonaut.core.netcdf.technical.v31.ArgoTechnicalV31;
import edu.colorado.cires.argonaut.core.netcdf.trajectory.v31.ArgoTrajectoryV31;
import edu.colorado.cires.argonaut.messaging.core.databind.ArgoFileType;
import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord;
import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord.Action;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class NetCdfMetadataRecordTest {

  private static final String TEST_FILE = "file";
  private static final String TEST_DAC = "dac";
  private static final String FLOAT_ID = "float-id";
  private static final String PROFILER_TYPE = "profiler-type";
  private static final String INSTITUTION = "institution";
  private static final Instant DATE_UPDATE = Instant.now();

  @Test
  void fromV31Trajectory() {
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

    MetadataRecord actual = NetCdfMetadataRecord.fromV31Trajectory(TEST_FILE, TEST_DAC, traj);

    assertEquals(
      MetadataRecord.builder()
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
        .withLongitudeMin(-9d)
        .build(),
      actual
    );
  }

  @Test
  void fromV31Technical() {
    ArgoTechnicalV31 traj = mock(ArgoTechnicalV31.class);
    when(traj.getPlatformNumber()).thenReturn(FLOAT_ID);
    when(traj.getDataCenter()).thenReturn(INSTITUTION);
    when(traj.getDateUpdate()).thenReturn(DATE_UPDATE);

    MetadataRecord actual = NetCdfMetadataRecord.fromV31Technical(TEST_FILE, TEST_DAC, traj);

    assertEquals(
      MetadataRecord.builder()
        .withAction(Action.UPDATE)
        .withFileType(ArgoFileType.TECHNICAL_DATA)
        .withFile(TEST_FILE)
        .withDac(TEST_DAC)
        .withFloatId(FLOAT_ID)
        .withInstitution(INSTITUTION)
        .withDateUpdate(DATE_UPDATE)
        .build(),
      actual
    );
  }
}