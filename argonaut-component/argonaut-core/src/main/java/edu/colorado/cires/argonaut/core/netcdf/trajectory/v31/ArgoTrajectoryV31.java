package edu.colorado.cires.argonaut.core.netcdf.trajectory.v31;

import java.time.Instant;
import org.jspecify.annotations.Nullable;

public interface ArgoTrajectoryV31 {

  String getTitle();

  String getInstitution();

  String getSource();

  String getHistory();

  String getReferences();

  String getComment();

  String getUserManualVersion();

  String getConventions();

  String getFeatureType();

  // Data type
  String getDataType();

  // File format version
  String getFormatVersion();

  // Data handbook version
  String getHandbookVersion();

  // Date of reference for Julian days
  Instant getReferenceDateTime();

  // Date of file creation
  Instant getDateCreation();

  // Date of update of this field
  Instant getDateUpdate();

  // Float unique identifier
  String getPlatformNumber();

  // Name of the project
  String getProjectName();

  // Name of the principal investigator
  String getPrincipalInvestigatorName();

  // Number of measurements
  Integer getNMeasurements();

  // Number of parameters
  Integer getNParameters();

  // Length of history
  Integer getNHistory();

  // Number of cycles
  Integer getNCycles();

  // Name of station parameter
  String getTrajectoryParameter(Integer nParameter);

  // Data center in charge of Float data processing
  String getDataCenter();

  // Degree of processing the data have passed through
  String getDataStateIndicator();

  // Type of Float
  String getPlatformType();

  // Serial number of the Float
  String getFloatSerialNumber();

  // Instrument firmware version
  String getFirmwareVersion();

  // Coded instrument type
  String getWmoInstrumentType();

  // Positioning system
  String getPositioningSystem();

  // Julian day (UTC) of each measurement relative to referenceDateTime
  Double getJuld(Integer nMeasurement);

  // Status of the date and time
  String getJuldStatus(Integer nMeasurement);

  // Quality on date and time
  String getJuldQc(Integer nMeasurement);

  // Adjusted julian day (UTC) of each measurement relative to referenceDateTime
  Double getJuldAdjusted(Integer nMeasurement);

  // Status of the juldAdjusted date
  String getJuldAdjustedStatus(Integer nMeasurement);

  // Quality on adjusted date and time
  String getJuldAdjustedQc(Integer nMeasurement);

  // Latitude of each location
  @Nullable
  Double getLatitude(Integer nMeasurement);

  // Longitude of each location
  @Nullable
  Double getLongitude(Integer nMeasurement);

  // Estimated accuracy in latitude and longitude
  String getPositionAccuracy(Integer nMeasurement);

  // Quality on position
  String getPositionQc(Integer nMeasurement);

  // Float cycle number of the measurement
  Integer getCycleNumber(Integer nMeasurement);

  // Adjusted Float cycle number of the measurement
  Integer getCycleNumberAdjusted(Integer nMeasurement);

  // Flag referring to a measurement event in the cycle
  Integer getMeasurementCode(Integer nMeasurement);

  // Sea water pressure, equals 0 at sea-level
  Float getPres(Integer nMeasurement);

  // Quality flag
  String getPresQc(Integer nMeasurement);

  // Sea water pressure, equals 0 at sea-level
  Float getPresAdjusted(Integer nMeasurement);

  // quality flag
  String getPresAdjustedQc(Integer nMeasurement);

  // Contains the error on the adjusted values as determined by the delayed mode QC process
  Float getPresAdjustedError(Integer nMeasurement);

  // Sea temperature in-situ ITS-90 scale
  Float getTemp(Integer nMeasurement);

  // quality flag
  String getTempQc(Integer nMeasurement);

  // Sea temperature in-situ ITS-90 scale
  Float getTempAdjusted(Integer nMeasurement);

  // quality flag
  String getTempAdjustedQc(Integer nMeasurement);

  // Contains the error on the adjusted values as determined by the delayed mode QC process
  Float getTempAdjustedError(Integer nMeasurement);

  // Practical salinity
  Float getPsal(Integer nMeasurement);

  // quality flag
  String getPsalQc(Integer nMeasurement);

  // Practical salinity
  Float getPsalAdjusted(Integer nMeasurement);

  // quality flag
  String getPsalAdjustedQc(Integer nMeasurement);

  // Contains the rror on the adjusted values as determined by the delayed mode QC process
  Float getPsalAdjustedError(Integer nMeasurement);

  // Major axis of error ellipse from positioning system
  Float getAxesErrorEllipseMajor(Integer nMeasurement);

  // Minor axis of error ellipse from positioning system
  Float getAxesErrorEllipseMinor(Integer nMeasurement);

  // Angle of error ellipse from positioning system
  Float getAxesErrorEllipseAngle(Integer nMeasurement);

  // Satellite name from positioning system
  String getSatelliteName(Integer nMeasurement);

  // Descent start date of the cycle
  Double getJuldDescentStart(Integer nCycle);

  // Status of the descent start date of the cycle
  String getJuldDescentStartStatus(Integer nCycle);

  // Time when a Float first becomes water-neutral
  Double getJuldFirstStabilization(Integer nCycle);

  // Status of time when a Float first becomes water-neutral
  String getJuldFirstStabilizationStatus(Integer nCycle);

  // Descent end date of the cycle
  Double getJuldDescentEnd(Integer nCycle);

  // Status of descent end date of the cycle
  String getJuldDescentEndStatus(Integer nCycle);

  // Drift start date of the cycle
  Double getJuldParkStart(Integer nCycle);

  // Status of drift start date of the cycle
  String getJuldParkStartStatus(Integer nCycle);

  // Drift end date of the cycle
  Double getJuldParkEnd(Integer nCycle);

  // Status of drift end date of the cycle
  String getJuldParkEndStatus(Integer nCycle);

  // Deep descent end date of the cycle
  Double getJuldDeepDescentEnd(Integer nCycle);

  // Status of deep descent end date of the cycle
  String getJuldDeepDescentEndStatus(Integer nCycle);

  // Deep descent start date of the cycle
  Double getJuldDeepDescentStart(Integer nCycle);

  // Status of deep descent start date of the cycle
  String getJuldDeepDescentStartStatus(Integer nCycle);

  // Start date of the ascent to the surface
  Double getJuldAscentStart(Integer nCycle);

  // Status of start date of the ascent to the surface
  String getJuldAscentStartStatus(Integer nCycle);

  // End date of ascent to the surface
  Double getJuldAscentEnd(Integer nCycle);

  // Status of end date of ascent to the surface
  String getJuldAscentEndStatus(Integer nCycle);

  // Deep ascent start date of the cycle
  Double getJuldDeepAscentStart(Integer nCycle);

  // Status of deep ascent start date of the cycle
  String getJuldDeepAscentStartStatus(Integer nCycle);

  // Start date of transmission
  Double getJuldTransmissionStart(Integer nCycle);

  // Status of start date of transmission
  String getJuldTransmissionStartStatus(Integer nCycle);

  // Date of earliest Float message received
  Double getJuldFirstMessage(Integer nCycle);

  // Status of date of earliest Float message received
  String getJuldFirstMessageStatus(Integer nCycle);

  // Date of earliest location
  Double getJuldFirstLocation(Integer nCycle);

  // Status of date of earliest location
  String getJuldFirstLocationStatus(Integer nCycle);

  // Date of latest location
  Double getJuldLastLocation(Integer nCycle);

  // Status of date of latest location
  String getJuldLastLocationStatus(Integer nCycle);

  // Date of latest Float message received
  Double getJuldLastMessage(Integer nCycle);

  // Status of date of latest Float message received
  String getJuldLastMessageStatus(Integer nCycle);

  // Transmission end date
  Double getJuldTransmissionEnd(Integer nCycle);

  // Status of transmission end date
  String getJuldTransmissionEndStatus(Integer nCycle);

  // Time of Float clock drift
  Double getClockOffset(Integer nCycle);

  // Did the profiler touch the ground for that cycle?
  String getGrounded(Integer nCycle);

  // Best pressure value during park phase
  Float getRepresentativeParkPressure(Integer nCycle);

  // Status of best pressure value during park phase
  String getRepresentativeParkPressureStatus(Integer nCycle);

  // 1...N, 1 : first complete mission
  Integer getConfigMissionNumber(Integer nCycle);

  // Cycle number that corresponds to the current index
  Integer getCycleNumberIndex(Integer nCycle);

  // Adjusted cycle number that corresponds to the current index
  Integer getCycleNumberIndexAdjusted(Integer nCycle);

  // Delayed mode or real time data
  String getDataMode(Integer nCycle);

  // Institution which performed action
  String getHistoryInstitution(Integer nHistory);

  // Step in data processing
  String getHistoryStep(Integer nHistory);

  // Name of software which performed action
  String getHistorySoftware(Integer nHistory);

  // Version/release of software which performed action
  String getHistorySoftwareRelease(Integer nHistory);

  // Reference of database
  String getHistoryReference(Integer nHistory);

  // Date the history was created
  Instant getHistoryDate(Integer nHistory);

  // Action performed on data
  String getHistoryAction(Integer nHistory);

  // Station parameter action is performed on
  String getHistoryParameter(Integer nHistory);

  // Parameter/Flag previous value before action
  Float getHistoryPreviousValue(Integer nHistory);

  // Name of dimension to which historyStartIndex and historyStopIndex correspond
  String getHistoryIndexDimension(Integer nHistory);

  // Start index action applied on
  Integer getHistoryStartIndex(Integer nHistory);

  // Stop index action applied on
  Integer getHistoryStopIndex(Integer nHistory);

  // Documentation of tests performed, tests failed (in hex form)
  String getHistoryQcTest(Integer nHistory);
}
