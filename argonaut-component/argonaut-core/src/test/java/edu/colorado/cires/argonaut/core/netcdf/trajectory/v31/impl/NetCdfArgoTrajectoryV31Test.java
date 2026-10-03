package edu.colorado.cires.argonaut.core.netcdf.trajectory.v31.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.function.Function;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

class NetCdfArgoTrajectoryV31Test {

  private static final NetCdfArgoTrajectoryV31 trajectory;

  static {
    try {
      trajectory = new NetCdfArgoTrajectoryV31(Paths.get("src/test/resources/dac/aoml/5903712/5903712_Rtraj.nc"));
    } catch (IOException e) {
      throw new RuntimeException("failed to open NetCDF file", e);
    }
  }

  @AfterAll
  static void tearDown() throws Exception {
    trajectory.close();
  }

  private final int nMeasurements = trajectory.getNMeasurements();
  private final int testMeasurementN = nMeasurements / 2;
  private final int nCycles = trajectory.getNCycles();
  private final int testCycleN = nCycles / 2;
  private final int nHistory = trajectory.getNHistory();

  private void assertAllNull(int size, Function<Integer, Object> getter) {
    for (int i = 0; i < size; i++) {
      assertNull(getter.apply(i), "value defined at %d".formatted(i));
    }
  }

  @Test
  void getTitle() {
    assertEquals("Argo float trajectory file", trajectory.getTitle());
  }

  @Test
  void getInstitution() {
    assertEquals("AOML", trajectory.getInstitution());
  }

  @Test
  void getSource() {
    assertEquals("Argo float", trajectory.getSource());
  }

  @Test
  void getHistory() {
    assertEquals("2021-04-28T22:58:03Z creation", trajectory.getHistory());
  }

  @Test
  void getReferences() {
    assertEquals("http://www.argodatamgt.org/Documentation", trajectory.getReferences());
  }

  @Test
  void getComment() {
    assertNull(trajectory.getComment());
  }

  @Test
  void getUserManualVersion() {
    assertEquals("3.1", trajectory.getUserManualVersion());
  }

  @Test
  void getConventions() {
    assertEquals("Argo-3.1 CF-1.6", trajectory.getConventions());
  }

  @Test
  void getFeatureType() {
    assertEquals("trajectory", trajectory.getFeatureType());
  }

  @Test
  void getDataType() {
    assertEquals("Argo trajectory", trajectory.getDataType());
  }

  @Test
  void getFormatVersion() {
    assertNull(trajectory.getFormatVersion());
  }

  @Test
  void getHandbookVersion() {
    assertEquals("1.2", trajectory.getHandbookVersion());
  }

  @Test
  void getReferenceDateTime() {
    assertEquals(Instant.parse("1950-01-01T00:00:00Z"), trajectory.getReferenceDateTime());
  }

  @Test
  void getDateCreation() {
    assertEquals(Instant.parse("2021-04-28T22:58:03Z"), trajectory.getDateCreation());
  }

  @Test
  void getDateUpdate() {
    assertEquals(Instant.parse("2021-04-28T22:58:03Z"), trajectory.getDateUpdate());
  }

  @Test
  void getPlatformNumber() {
    assertEquals("5903712", trajectory.getPlatformNumber());
  }

  @Test
  void getProjectName() {
    assertEquals("Argo equivalent", trajectory.getProjectName());
  }

  @Test
  void getPrincipalInvestigatorName() {
    assertEquals("STEPHEN RISER", trajectory.getPrincipalInvestigatorName());
  }

  @Test
  void getNMeasurements() {
    assertEquals(49949, nMeasurements);
  }

  @Test
  void getNHistory() {
    assertEquals(25, nHistory);
  }

  @Test
  void getNCycles() {
    assertEquals(287, nCycles);
  }

  @Test
  void getTrajectoryParameter() {
    assertAllNull(nMeasurements, trajectory::getTrajectoryParameter);
  }

  @Test
  void getDataCenter() {
    assertEquals("AO", trajectory.getDataCenter());
  }

  @Test
  void getDataStateIndicator() {
    assertEquals("2B", trajectory.getDataStateIndicator());
  }

  @Test
  void getPlatformType() {
    assertEquals("APEX", trajectory.getPlatformType());
  }

  @Test
  void getFloatSerialNumber() {
    assertEquals("5639", trajectory.getFloatSerialNumber());
  }

  @Test
  void getFirmwareVersion() {
    assertEquals("021112", trajectory.getFirmwareVersion());
  }

  @Test
  void getWmoInstrumentType() {
    assertEquals("846", trajectory.getWmoInstrumentType());
  }

  @Test
  void getPositioningSystem() {
    assertEquals("GPS", trajectory.getPositioningSystem());
  }

  @Test
  void getJuld() {
    assertEquals(24036.14789352278, trajectory.getJuld(testMeasurementN));
  }

  @Test
  void getJuldStatus() {
    assertEquals("2", trajectory.getJuldStatus(testMeasurementN));
  }

  @Test
  void getJuldQc() {
    assertEquals("1", trajectory.getJuldQc(testMeasurementN));
  }

  @Test
  void getJuldAdjusted() {
    assertAllNull(nMeasurements, trajectory::getJuldAdjusted);
  }

  @Test
  void getJuldAdjustedStatus() {
    assertAllNull(nMeasurements, trajectory::getJuldAdjustedStatus);
  }

  @Test
  void getJuldAdjustedQc() {
    for  (Integer i = 0; i < nMeasurements; i++) {
      assertNull(trajectory.getJuldAdjustedQc(i), "value defined at %d".formatted(i));
    }
  }

  @Test
  void getLatitude() {
    assertEquals(15.006667137145996, trajectory.getLatitude(0));
  }

  @Test
  void getLongitude() {
    assertEquals(89.92333221435547, trajectory.getLongitude(0));
  }

  @Test
  void getPositionAccuracy() {
    assertEquals("G", trajectory.getPositionAccuracy(1));
  }

  @Test
  void getPositionQc() {
    assertEquals("0", trajectory.getPositionQc(0));
  }

  @Test
  void getCycleNumber() {
    assertEquals(144, trajectory.getCycleNumber(testMeasurementN));
  }

  @Test
  void getCycleNumberAdjusted() {
    assertAllNull(nMeasurements, trajectory::getCycleNumberAdjusted);
  }

  @Test
  void getMeasurementCode() {
    assertEquals(290, trajectory.getMeasurementCode(testMeasurementN));
  }

  @Test
  void getPres() {
    assertEquals(701.1f, trajectory.getPres(testMeasurementN));
  }

  @Test
  void getPresQc() {
    assertEquals("0" , trajectory.getPresQc(testMeasurementN));
  }

  @Test
  void getPresAdjusted() {
    assertAllNull(nMeasurements, trajectory::getPresAdjusted);
  }

  @Test
  void getPresAdjustedQc() {
    assertAllNull(nMeasurements, trajectory::getPresAdjusted);
  }

  @Test
  void getPresAdjustedError() {
    assertAllNull(nMeasurements, trajectory::getPresAdjustedError);
  }

  @Test
  void getTemp() {
    assertEquals(7.8726f, trajectory.getTemp(testMeasurementN));
  }

  @Test
  void getTempQc() {
    assertEquals("0", trajectory.getTempQc(testMeasurementN));
  }

  @Test
  void getTempAdjusted() {
    assertAllNull(nMeasurements, trajectory::getTempAdjusted);
  }

  @Test
  void getTempAdjustedQc() {
    assertAllNull(nMeasurements, trajectory::getTempAdjustedQc);
  }

  @Test
  void getTempAdjustedError() {
    assertAllNull(nMeasurements, trajectory::getTempAdjustedError);
  }

  @Test
  void getPsal() {
    assertEquals(35.0193f, trajectory.getPsal(10));
  }

  @Test
  void getPsalQc() {
    assertEquals("0", trajectory.getPsalQc(10));
  }

  @Test
  void getPsalAdjusted() {
    assertAllNull(nMeasurements, trajectory::getPsalAdjusted);
  }

  @Test
  void getPsalAdjustedQc() {
    assertAllNull(nMeasurements, trajectory::getPsalAdjustedQc);
  }

  @Test
  void getPsalAdjustedError() {
    assertAllNull(nMeasurements, trajectory::getPsalAdjustedError);
  }

  @Test
  void getAxesErrorEllipseMajor() {
    assertAllNull(nMeasurements, trajectory::getAxesErrorEllipseMajor);
  }

  @Test
  void getAxesErrorEllipseMinor() {
    assertAllNull(nMeasurements, trajectory::getAxesErrorEllipseMinor);
  }

  @Test
  void getAxisErrorEllipseAngle() {
    assertAllNull(nMeasurements, trajectory::getAxesErrorEllipseAngle);
  }

  @Test
  void getSatelliteName() {
    assertAllNull(nMeasurements, trajectory::getSatelliteName);
  }

  @Test
  void getJuldDescentStart() {
    assertAllNull(nCycles, trajectory::getJuldDescentStart);
  }

  @Test
  void getJuldDescentStartStatus() {
    assertEquals("9", trajectory.getJuldDescentStartStatus(testCycleN));
  }

  @Test
  void getJuldFirstStabilization() {
    assertAllNull(nCycles, trajectory::getJuldFirstStabilization);
  }

  @Test
  void getJuldFirstStabilizationStatus() {
    assertEquals("9", trajectory.getJuldFirstStabilizationStatus(testCycleN));
  }

  @Test
  void getJuldDescentEnd() {
    assertAllNull(nCycles, trajectory::getJuldDescentEnd);
  }

  @Test
  void getJuldDescentEndStatus() {
    assertEquals("9", trajectory.getJuldDescentEndStatus(testCycleN));
  }

  @Test
  void getJuldParkStart() {
    assertAllNull(nCycles, trajectory::getJuldParkStart);
  }

  @Test
  void getJuldParkStartStatus() {
    assertEquals("9", trajectory.getJuldParkStartStatus(testCycleN));
  }

  @Test
  void getJuldParkEnd() {
    assertAllNull(nCycles, trajectory::getJuldParkEnd);
  }

  @Test
  void getJuldParkEndStatus() {
    assertEquals("9", trajectory.getJuldParkEndStatus(testCycleN));
  }

  @Test
  void getJuldDeepDescentEnd() {
    assertAllNull(nCycles, trajectory::getJuldDeepDescentEnd);
  }

  @Test
  void getJuldDeepDescentEndStatus() {
    assertEquals("9", trajectory.getJuldDeepDescentEndStatus(testCycleN));
  }

  @Test
  void getJuldDeepDescentStart() {
    assertAllNull(nCycles, trajectory::getJuldDeepDescentStart);
  }

  @Test
  void getJuldDeepDescentStartStatus() {
    assertAllNull(nCycles, trajectory::getJuldDeepDescentStartStatus);
  }

  @Test
  void getJuldAscentStart() {
    assertAllNull(nCycles, trajectory::getJuldAscentStart);
  }

  @Test
  void getJuldAscentStartStatus() {
    assertEquals("9", trajectory.getJuldAscentStartStatus(testCycleN));
  }

  @Test
  void getJuldAscentEnd() {
    assertEquals(25049.194930561192, trajectory.getJuldAscentEnd(284));
  }

  @Test
  void getJuldAscentEndStatus() {
    assertEquals("9", trajectory.getJuldAscentEndStatus(testCycleN));
  }

  @Test
  void getJuldDeepAscentStart() {
    assertAllNull(nCycles, trajectory::getJuldDeepAscentStart);
  }

  @Test
  void getJuldDeepAscentStartStatus() {
    assertEquals("9", trajectory.getJuldDeepAscentStartStatus(testCycleN));
  }

  @Test
  void getJuldTransmissionStart() {
    assertAllNull(nCycles, trajectory::getJuldTransmissionStart);
  }

  @Test
  void getJuldTransmissionStartStatus() {
    assertAllNull(nCycles, trajectory::getJuldTransmissionStartStatus);
  }

  @Test
  void getJuldFirstMessage() {
    assertAllNull(nCycles, trajectory::getJuldFirstMessage);
  }

  @Test
  void getJuldFirstMessageStatus() {
    assertEquals("9", trajectory.getJuldFirstMessageStatus(testCycleN));
  }

  @Test
  void getJuldFirstLocation() {
    assertEquals(24032.679664372023, trajectory.getJuldFirstLocation(testCycleN));
  }

  @Test
  void getJuldFirstLocationStatus() {
    assertEquals("2", trajectory.getJuldFirstLocationStatus(testCycleN));
  }

  @Test
  void getJuldLastLocation() {
    assertEquals(24032.679664372023, trajectory.getJuldLastLocation(testCycleN));
  }

  @Test
  void getJuldLastLocationStatus() {
    assertEquals("2", trajectory.getJuldLastLocationStatus(testCycleN));
  }

  @Test
  void getJuldLastMessage() {
    assertAllNull(nCycles, trajectory::getJuldLastMessage);
  }

  @Test
  void getJuldLastMessageStatus() {
    assertEquals("9", trajectory.getJuldLastMessageStatus(testCycleN));
  }

  @Test
  void getJuldTransmissionEnd() {
    assertAllNull(nCycles, trajectory::getJuldTransmissionEnd);
  }

  @Test
  void getJuldTransmissionEndStatus() {
    assertEquals("9", trajectory.getJuldTransmissionEndStatus(testCycleN));
  }

  @Test
  void getClockOffset() {
    assertAllNull(nCycles, trajectory::getClockOffset);
  }

  @Test
  void getGrounded() {
    assertEquals("U", trajectory.getGrounded(testCycleN));
  }

  @Test
  void getRepresentativeParkPressure() {
    assertAllNull(nCycles, trajectory::getRepresentativeParkPressure);
  }

  @Test
  void getRepresentativeParkPressureStatus() {
    assertAllNull(nCycles, trajectory::getRepresentativeParkPressureStatus);
  }

  @Test
  void getConfigMissionNumber() {
    assertEquals(143, trajectory.getConfigMissionNumber(testCycleN));
  }

  @Test
  void getCycleNumberIndex() {
    assertEquals(143, trajectory.getCycleNumberIndex(testCycleN));
  }

  @Test
  void getCycleNumberIndexAdjusted() {
    assertAllNull(nCycles, trajectory::getCycleNumberIndexAdjusted);
  }

  @Test
  void getDataMode() {
    assertEquals("R", trajectory.getDataMode(testCycleN));
  }

  @Test
  void getHistoryInstitution() {
    assertAllNull(nHistory, trajectory::getHistoryInstitution);
  }

  @Test
  void getHistoryStep() {
    assertEquals("ARGQ", trajectory.getHistoryStep(0));
  }

  @Test
  void getHistorySoftware() {
    assertEquals("QCPL", trajectory.getHistorySoftware(0));
  }

  @Test
  void getHistorySoftwareRelease() {
    assertAllNull(nHistory, trajectory::getHistorySoftwareRelease);
  }

  @Test
  void getHistoryReference() {
    assertAllNull(nHistory, trajectory::getHistoryReference);
  }

  @Test
  void getHistoryDate() {
    assertEquals(Instant.parse("2021-04-28T22:58:03Z"), trajectory.getHistoryDate(0));
  }

  @Test
  void getHistoryAction() {
    assertEquals("QCP$", trajectory.getHistoryAction(0));
  }

  @Test
  void getHistoryParameter() {
    assertAllNull(nHistory, trajectory::getHistoryParameter);
  }

  @Test
  void getHistoryPreviousValue() {
    assertAllNull(nHistory, trajectory::getHistoryPreviousValue);
  }

  @Test
  void getHistoryIndexDimension() {
    assertAllNull(nHistory, trajectory::getHistoryIndexDimension);
  }

  @Test
  void getHistoryStartIndex() {
    assertAllNull(nHistory, trajectory::getHistoryStartIndex);
  }

  @Test
  void getHistoryStopIndex() {
    assertAllNull(nHistory, trajectory::getHistoryStopIndex);
  }

  @Test
  void getHistoryQcTest() {
    assertEquals("0", trajectory.getHistoryQcTest(0));
  }
}