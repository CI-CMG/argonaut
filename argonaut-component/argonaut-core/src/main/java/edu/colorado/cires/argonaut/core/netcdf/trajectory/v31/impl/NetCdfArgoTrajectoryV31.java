package edu.colorado.cires.argonaut.core.netcdf.trajectory.v31.impl;

import static edu.colorado.cires.argonaut.core.util.NetCdfReadUtils.getDimensionSize;
import static edu.colorado.cires.argonaut.core.util.NetCdfReadUtils.getGlobalAttributeString;
import static edu.colorado.cires.argonaut.core.util.NetCdfReadUtils.getInstant;
import static edu.colorado.cires.argonaut.core.util.NetCdfReadUtils.getLevel1Double;
import static edu.colorado.cires.argonaut.core.util.NetCdfReadUtils.getLevel1Float;
import static edu.colorado.cires.argonaut.core.util.NetCdfReadUtils.getLevel1Integer;
import static edu.colorado.cires.argonaut.core.util.NetCdfReadUtils.getLevel1String;
import static edu.colorado.cires.argonaut.core.util.NetCdfReadUtils.getString;
import static edu.colorado.cires.argonaut.core.util.NetCdfReadUtils.getLevel1Instant;

import edu.colorado.cires.argonaut.core.netcdf.trajectory.v31.ArgoTrajectoryV31;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import ucar.nc2.NetcdfFile;
import ucar.nc2.NetcdfFiles;

public class NetCdfArgoTrajectoryV31 implements ArgoTrajectoryV31, AutoCloseable {

  private final NetcdfFile netcdfFile;

  private NetCdfArgoTrajectoryV31(Path path) throws IOException {
    this.netcdfFile = NetcdfFiles.open(path.toString());
  }

  public static NetCdfArgoTrajectoryV31 create(Path path) throws IOException {
    return new NetCdfArgoTrajectoryV31(path);
  }

  @Override
  public void close() throws IOException {
    netcdfFile.close();
  }

  @Override
  public String getTitle() {
    return getGlobalAttributeString(netcdfFile, "title");
  }

  @Override
  public String getInstitution() {
    return getGlobalAttributeString(netcdfFile, "institution");
  }

  @Override
  public String getSource() {
    return getGlobalAttributeString(netcdfFile, "source");
  }

  @Override
  public String getHistory() {
    return getGlobalAttributeString(netcdfFile, "history");
  }

  @Override
  public String getReferences() {
    return getGlobalAttributeString(netcdfFile, "references");
  }

  @Override
  public String getComment() {
    return getGlobalAttributeString(netcdfFile, "commet");
  }

  @Override
  public String getUserManualVersion() {
    return getGlobalAttributeString(netcdfFile, "user_manual_version");
  }

  @Override
  public String getConventions() {
    return getGlobalAttributeString(netcdfFile, "Conventions");
  }

  @Override
  public String getFeatureType() {
    return getGlobalAttributeString(netcdfFile, "featureType");
  }

  @Override
  public String getDataType() {
    return getString(netcdfFile, "DATA_TYPE");
  }

  @Override
  public String getFormatVersion() {
    return getString(netcdfFile, "FORMAT_VERSION");
  }

  @Override
  public String getHandbookVersion() {
    return getString(netcdfFile, "HANDBOOK_VERSION");
  }

  @Override
  public Instant getReferenceDateTime() {
    return getInstant(netcdfFile, "REFERENCE_DATE_TIME");
  }

  @Override
  public Instant getDateCreation() {
    return getInstant(netcdfFile, "DATE_CREATION");
  }

  @Override
  public Instant getDateUpdate() {
    return getInstant(netcdfFile, "DATE_UPDATE");
  }

  @Override
  public String getPlatformNumber() {
    return getString(netcdfFile, "PLATFORM_NUMBER");
  }

  @Override
  public String getProjectName() {
    return getString(netcdfFile, "PROJECT_NAME");
  }

  @Override
  public String getPrincipalInvestigatorName() {
    return getString(netcdfFile, "PI_NAME");
  }

  @Override
  public Integer getNMeasurements() {
    return getDimensionSize(netcdfFile, "N_MEASUREMENT");
  }

  @Override
  public Integer getNHistory() {
    return getDimensionSize(netcdfFile, "N_HISTORY");
  }

  @Override
  public Integer getNCycles() {
    return getDimensionSize(netcdfFile, "N_CYCLE");
  }

  @Override
  public Integer getNParameters() {
    return getDimensionSize(netcdfFile, "N_PARAM");
  }

  @Override
  public String getTrajectoryParameter(Integer nParameter) {
    return getLevel1String(netcdfFile, nParameter, "TRAJECTORY_PARAMETER");
  }

  @Override
  public String getDataCenter() {
    return getString(netcdfFile, "DATA_CENTRE");
  }

  @Override
  public String getDataStateIndicator() {
    return getString(netcdfFile, "DATA_STATE_INDICATOR");
  }

  @Override
  public String getPlatformType() {
    return getString(netcdfFile, "PLATFORM_TYPE");
  }

  @Override
  public String getFloatSerialNumber() {
    return getString(netcdfFile, "FLOAT_SERIAL_NO");
  }

  @Override
  public String getFirmwareVersion() {
    return getString(netcdfFile, "FIRMWARE_VERSION");
  }

  @Override
  public String getWmoInstrumentType() {
    return getString(netcdfFile, "WMO_INST_TYPE");
  }

  @Override
  public String getPositioningSystem() {
    return getString(netcdfFile, "POSITIONING_SYSTEM");
  }

  @Override
  public Double getJuld(Integer nMeasurement) {
    return getLevel1Double(netcdfFile, nMeasurement, "JULD");
  }

  @Override
  public String getJuldStatus(Integer nMeasurement) {
    return getLevel1String(netcdfFile, nMeasurement, "JULD_STATUS");
  }

  @Override
  public String getJuldQc(Integer nMeasurement) {
    return getLevel1String(netcdfFile, nMeasurement, "JULD_QC");
  }

  @Override
  public Double getJuldAdjusted(Integer nMeasurement) {
    return getLevel1Double(netcdfFile, nMeasurement, "JULD_ADJUSTED");
  }

  @Override
  public String getJuldAdjustedStatus(Integer nMeasurement) {
    return getLevel1String(netcdfFile, nMeasurement, "JULD_ADJUSTED_STATUS");
  }

  @Override
  public String getJuldAdjustedQc(Integer nMeasurement) {
    return getLevel1String(netcdfFile, nMeasurement, "JULD_ADJUSTED_QC");
  }

  @Override
  public Double getLatitude(Integer nMeasurement) {
    return getLevel1Double(netcdfFile, nMeasurement, "LATITUDE");
  }

  @Override
  public Double getLongitude(Integer nMeasurement) {
    return getLevel1Double(netcdfFile, nMeasurement, "LONGITUDE");
  }

  @Override
  public String getPositionAccuracy(Integer nMeasurement) {
    return getLevel1String(netcdfFile, nMeasurement, "POSITION_ACCURACY");
  }

  @Override
  public String getPositionQc(Integer nMeasurement) {
    return getLevel1String(netcdfFile, nMeasurement, "POSITION_QC");
  }

  @Override
  public Integer getCycleNumber(Integer nMeasurement) {
    return getLevel1Integer(netcdfFile, nMeasurement, "CYCLE_NUMBER");
  }

  @Override
  public Integer getCycleNumberAdjusted(Integer nMeasurement) {
    return getLevel1Integer(netcdfFile, nMeasurement, "CYCLE_NUMBER_ADJUSTED");
  }

  @Override
  public Integer getMeasurementCode(Integer nMeasurement) {
    return getLevel1Integer(netcdfFile, nMeasurement, "MEASUREMENT_CODE");
  }

  @Override
  public Float getPres(Integer nMeasurement) {
    return getLevel1Float(netcdfFile, nMeasurement, "PRES");
  }

  @Override
  public String getPresQc(Integer nMeasurement) {
    return getLevel1String(netcdfFile, nMeasurement, "PRES_QC");
  }

  @Override
  public Float getPresAdjusted(Integer nMeasurement) {
    return getLevel1Float(netcdfFile, nMeasurement, "PRES_ADJUSTED");
  }

  @Override
  public String getPresAdjustedQc(Integer nMeasurement) {
    return getLevel1String(netcdfFile, nMeasurement, "PRES_ADJUSTED_QC");
  }

  @Override
  public Float getPresAdjustedError(Integer nMeasurement) {
    return getLevel1Float(netcdfFile, nMeasurement, "PRES_ADJUSTED_ERROR");
  }

  @Override
  public Float getTemp(Integer nMeasurement) {
    return getLevel1Float(netcdfFile, nMeasurement, "TEMP");
  }

  @Override
  public String getTempQc(Integer nMeasurement) {
    return getLevel1String(netcdfFile, nMeasurement, "TEMP_QC");
  }

  @Override
  public Float getTempAdjusted(Integer nMeasurement) {
    return getLevel1Float(netcdfFile, nMeasurement, "TEMP_ADJUSTED");
  }

  @Override
  public String getTempAdjustedQc(Integer nMeasurement) {
    return getLevel1String(netcdfFile, nMeasurement, "TEMP_ADJUSTED_QC");
  }

  @Override
  public Float getTempAdjustedError(Integer nMeasurement) {
    return getLevel1Float(netcdfFile, nMeasurement, "TEMP_ADJUSTED_ERROR");
  }

  @Override
  public Float getPsal(Integer nMeasurement) {
    return getLevel1Float(netcdfFile, nMeasurement, "PSAL");
  }

  @Override
  public String getPsalQc(Integer nMeasurement) {
    return getLevel1String(netcdfFile, nMeasurement, "PSAL_QC");
  }

  @Override
  public Float getPsalAdjusted(Integer nMeasurement) {
    return getLevel1Float(netcdfFile, nMeasurement, "PSAL_ADJUSTED");
  }

  @Override
  public String getPsalAdjustedQc(Integer nMeasurement) {
    return getLevel1String(netcdfFile, nMeasurement, "PSAL_ADJUSTED_QC");
  }

  @Override
  public Float getPsalAdjustedError(Integer nMeasurement) {
    return getLevel1Float(netcdfFile, nMeasurement, "PSAL_ADJUSTED_ERROR");
  }

  @Override
  public Float getAxesErrorEllipseMajor(Integer nMeasurement) {
    return getLevel1Float(netcdfFile, nMeasurement, "AXES_ERROR_ELLIPSE_MAJOR");
  }

  @Override
  public Float getAxesErrorEllipseMinor(Integer nMeasurement) {
    return getLevel1Float(netcdfFile, nMeasurement, "AXES_ERROR_ELLIPSE_MINOR");
  }

  @Override
  public Float getAxesErrorEllipseAngle(Integer nMeasurement) {
    return getLevel1Float(netcdfFile, nMeasurement, "AXES_ERROR_ELLIPSE_ANGLE");
  }

  @Override
  public String getSatelliteName(Integer nMeasurement) {
    return getLevel1String(netcdfFile, nMeasurement, "SATELLITE_NAME");
  }

  @Override
  public Double getJuldDescentStart(Integer nCycle) {
    return getLevel1Double(netcdfFile, nCycle, "JULD_DESCENT_START");
  }

  @Override
  public String getJuldDescentStartStatus(Integer nCycle) {
    return getLevel1String(netcdfFile, nCycle, "JULD_DESCENT_START_STATUS");
  }

  @Override
  public Double getJuldFirstStabilization(Integer nCycle) {
    return getLevel1Double(netcdfFile, nCycle, "JULD_FIRST_STABILIZATION");
  }

  @Override
  public String getJuldFirstStabilizationStatus(Integer nCycle) {
    return getLevel1String(netcdfFile, nCycle, "JULD_FIRST_STABILIZATION_STATUS");
  }

  @Override
  public Double getJuldDescentEnd(Integer nCycle) {
    return getLevel1Double(netcdfFile, nCycle, "JULD_DESCENT_END");
  }

  @Override
  public String getJuldDescentEndStatus(Integer nCycle) {
    return getLevel1String(netcdfFile, nCycle, "JULD_DESCENT_END_STATUS");
  }

  @Override
  public Double getJuldParkStart(Integer nCycle) {
    return getLevel1Double(netcdfFile, nCycle, "JULD_PARK_START");
  }

  @Override
  public String getJuldParkStartStatus(Integer nCycle) {
    return getLevel1String(netcdfFile, nCycle, "JULD_PARK_START_STATUS");
  }

  @Override
  public Double getJuldParkEnd(Integer nCycle) {
    return getLevel1Double(netcdfFile, nCycle, "JULD_PARK_END");
  }

  @Override
  public String getJuldParkEndStatus(Integer nCycle) {
    return getLevel1String(netcdfFile, nCycle, "JULD_PARK_END_STATUS");
  }

  @Override
  public Double getJuldDeepDescentEnd(Integer nCycle) {
    return getLevel1Double(netcdfFile, nCycle, "JULD_DEEP_DESCENT_END");
  }

  @Override
  public String getJuldDeepDescentEndStatus(Integer nCycle) {
    return getLevel1String(netcdfFile, nCycle, "JULD_DEEP_DESCENT_END_STATUS");
  }

  @Override
  public Double getJuldDeepDescentStart(Integer nCycle) {
    return getLevel1Double(netcdfFile, nCycle, "JULD_DEEP_DESCENT_START");
  }

  @Override
  public String getJuldDeepDescentStartStatus(Integer nCycle) {
    return getLevel1String(netcdfFile, nCycle, "JULD_DEEP_DESCENT_START_STATUS");
  }

  @Override
  public Double getJuldAscentStart(Integer nCycle) {
    return getLevel1Double(netcdfFile, nCycle, "JULD_ASCENT_START");
  }

  @Override
  public String getJuldAscentStartStatus(Integer nCycle) {
    return getLevel1String(netcdfFile, nCycle, "JULD_ASCENT_START_STATUS");
  }

  @Override
  public Double getJuldAscentEnd(Integer nCycle) {
    return getLevel1Double(netcdfFile, nCycle, "JULD_ASCENT_END");
  }

  @Override
  public String getJuldAscentEndStatus(Integer nCycle) {
    return getLevel1String(netcdfFile, nCycle, "JULD_ASCENT_END_STATUS");
  }

  @Override
  public Double getJuldDeepAscentStart(Integer nCycle) {
    return getLevel1Double(netcdfFile, nCycle, "JULD_DEEP_ASCENT_START");
  }

  @Override
  public String getJuldDeepAscentStartStatus(Integer nCycle) {
    return getLevel1String(netcdfFile, nCycle, "JULD_DEEP_ASCENT_START_STATUS");
  }

  @Override
  public Double getJuldTransmissionStart(Integer nCycle) {
    return getLevel1Double(netcdfFile, nCycle, "JULD_TRANSMITION_START");
  }

  @Override
  public String getJuldTransmissionStartStatus(Integer nCycle) {
    return getLevel1String(netcdfFile, nCycle, "JULD_TRANSMITION_START_STATUS");
  }

  @Override
  public Double getJuldFirstMessage(Integer nCycle) {
    return getLevel1Double(netcdfFile, nCycle, "JULD_FIRST_MESSAGE");
  }

  @Override
  public String getJuldFirstMessageStatus(Integer nCycle) {
    return getLevel1String(netcdfFile, nCycle, "JULD_FIRST_MESSAGE_STATUS");
  }

  @Override
  public Double getJuldFirstLocation(Integer nCycle) {
    return getLevel1Double(netcdfFile, nCycle, "JULD_FIRST_LOCATION");
  }

  @Override
  public String getJuldFirstLocationStatus(Integer nCycle) {
    return getLevel1String(netcdfFile, nCycle, "JULD_FIRST_LOCATION_STATUS");
  }

  @Override
  public Double getJuldLastLocation(Integer nCycle) {
    return getLevel1Double(netcdfFile, nCycle, "JULD_LAST_LOCATION");
  }

  @Override
  public String getJuldLastLocationStatus(Integer nCycle) {
    return getLevel1String(netcdfFile, nCycle, "JULD_LAST_LOCATION_STATUS");
  }

  @Override
  public Double getJuldLastMessage(Integer nCycle) {
    return getLevel1Double(netcdfFile, nCycle, "JULD_LAST_MESSAGE");
  }

  @Override
  public String getJuldLastMessageStatus(Integer nCycle) {
    return getLevel1String(netcdfFile, nCycle, "JULD_LAST_MESSAGE_STATUS");
  }

  @Override
  public Double getJuldTransmissionEnd(Integer nCycle) {
    return getLevel1Double(netcdfFile, nCycle, "JULD_TRANSMISSION_END");
  }

  @Override
  public String getJuldTransmissionEndStatus(Integer nCycle) {
    return getLevel1String(netcdfFile, nCycle, "JULD_TRANSMISSION_END_STATUS");
  }

  @Override
  public Double getClockOffset(Integer nCycle) {
    return getLevel1Double(netcdfFile, nCycle, "JULD_CLOCK_OFFSET");
  }

  @Override
  public String getGrounded(Integer nCycle) {
    return getLevel1String(netcdfFile, nCycle, "GROUNDED");
  }

  @Override
  public Float getRepresentativeParkPressure(Integer nCycle) {
    return getLevel1Float(netcdfFile, nCycle, "REPRESENTATIVE_PARK_PRESSURE");
  }

  @Override
  public String getRepresentativeParkPressureStatus(Integer nCycle) {
    return getLevel1String(netcdfFile, nCycle, "REPRESENTATIVE_PARK_PRESSURE_STATUS");
  }

  @Override
  public Integer getConfigMissionNumber(Integer nCycle) {
    return getLevel1Integer(netcdfFile, nCycle, "CONFIG_MISSION_NUMBER");
  }

  @Override
  public Integer getCycleNumberIndex(Integer nCycle) {
    return getLevel1Integer(netcdfFile, nCycle, "CYCLE_NUMBER_INDEX");
  }

  @Override
  public Integer getCycleNumberIndexAdjusted(Integer nCycle) {
    return getLevel1Integer(netcdfFile, nCycle, "CYCLE_NUMBER_INDEX_ADJUSTED");
  }

  @Override
  public String getDataMode(Integer nCycle) {
    return getLevel1String(netcdfFile, nCycle, "DATA_MODE");
  }

  @Override
  public String getHistoryInstitution(Integer nHistory) {
    return getLevel1String(netcdfFile, nHistory, "HISTORY");
  }

  @Override
  public String getHistoryStep(Integer nHistory) {
    return getLevel1String(netcdfFile, nHistory, "HISTORY_STEP");
  }

  @Override
  public String getHistorySoftware(Integer nHistory) {
    return getLevel1String(netcdfFile, nHistory, "HISTORY_SOFTWARE");
  }

  @Override
  public String getHistorySoftwareRelease(Integer nHistory) {
    return getLevel1String(netcdfFile, nHistory, "HISTORY_SOFTWARE_RELEASE");
  }

  @Override
  public String getHistoryReference(Integer nHistory) {
    return getLevel1String(netcdfFile, nHistory, "HISTORY_REFERENCE");
  }

  @Override
  public Instant getHistoryDate(Integer nHistory) {
    return getLevel1Instant(netcdfFile, nHistory, "HISTORY_DATE");
  }

  @Override
  public String getHistoryAction(Integer nHistory) {
    return getLevel1String(netcdfFile, nHistory, "HISTORY_ACTION");
  }

  @Override
  public String getHistoryParameter(Integer nHistory) {
    return getLevel1String(netcdfFile, nHistory, "HISTORY_PARAMETER");
  }

  @Override
  public Float getHistoryPreviousValue(Integer nHistory) {
    return getLevel1Float(netcdfFile, nHistory, "HISTORY_PREVIOUS_VALUE");
  }

  @Override
  public String getHistoryIndexDimension(Integer nHistory) {
    return getLevel1String(netcdfFile, nHistory, "HISTORY_INDEX_DIMENSION");
  }

  @Override
  public Integer getHistoryStartIndex(Integer nHistory) {
    return getLevel1Integer(netcdfFile, nHistory, "HISTORY_START_INDEX");
  }

  @Override
  public Integer getHistoryStopIndex(Integer nHistory) {
    return getLevel1Integer(netcdfFile, nHistory, "HISTORY_STOP_INDEX");
  }

  @Override
  public String getHistoryQcTest(Integer nHistory) {
    return getLevel1String(netcdfFile, nHistory, "HISTORY_QCTEST");
  }
}
