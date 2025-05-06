package edu.colorado.cires.argonaut.service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import ucar.ma2.DataType;
import ucar.nc2.Attribute;
import ucar.nc2.Dimension;
import ucar.nc2.Variable;
import ucar.nc2.write.NetcdfFormatWriter;

public class MultiFloatMergeService {

  // https://docs.unidata.ucar.edu/netcdf-java/5.4/userguide/writing_netcdf.html

  public void mergeFloats(Path outputFile, Set<Path> profileNcFiles) throws IOException {

    int nParam = getNParamSize(profileNcFiles);
    int nCalib = getNCalib(profileNcFiles);
    int nLevels = getNlevels(profileNcFiles);

    NetcdfFormatWriter.Builder builder = NetcdfFormatWriter.createNewNetcdf3(outputFile.toString());

    Dimension dateTimeDim = builder.addDimension("DATE_TIME", 14);
    Dimension string256Dim = builder.addDimension("STRING256", 256);
    Dimension string64Dim = builder.addDimension("STRING64", 64);
    Dimension string32Dim = builder.addDimension("STRING32", 32);
    Dimension string16Dim = builder.addDimension("STRING16", 16);
    Dimension string8Dim = builder.addDimension("STRING8", 8);
    Dimension string4Dim = builder.addDimension("STRING4", 4);
    Dimension string2Dim = builder.addDimension("STRING2", 2);

    Dimension nProfDim = builder.addDimension("N_PROF", profileNcFiles.size());
    Dimension nParamDim = builder.addDimension("N_PARAM", nParam); // 3, Maybe 4
    Dimension nLevelsDim = builder.addDimension("N_LEVELS", nLevels);
    Dimension nHistoryDim = builder.addUnlimitedDimension("N_HISTORY");
    Dimension nCalibDim = builder.addDimension("N_CALIB", nCalib); // 1

    List<Dimension> dims = Arrays.asList(
        dateTimeDim,
        string256Dim,
        string64Dim,
        string32Dim,
        string16Dim,
        string8Dim,
        string4Dim,
        string2Dim,
        nProfDim,
        nParamDim,
        nLevelsDim,
        nHistoryDim,
        nCalibDim
    );


    Variable.Builder varDataType = builder.addVariable("DATA_TYPE", DataType.STRING, Arrays.asList(string16Dim))
        .addAttribute(new Attribute("long_name", "Data type"))
        .addAttribute(new Attribute("conventions", "Argo reference table 1"))
        .addAttribute(new Attribute("_FillValue", " "));

    Variable.Builder varFormatVersion = builder.addVariable("FORMAT_VERSION", DataType.STRING, Arrays.asList(string4Dim))
        .addAttribute(new Attribute( "long_name", "File format version"))
        .addAttribute(new Attribute("_FillValue", " "));

    Variable.Builder varHandbookVersion = builder.addVariable("HANDBOOK_VERSION", DataType.STRING, Arrays.asList(string4Dim))
        .addAttribute(new Attribute( "long_name", "Data handbook version"))
        .addAttribute(new Attribute("_FillValue", " "));

    Variable.Builder varReferenceDateTime = builder.addVariable("REFERENCE_DATE_TIME", DataType.STRING , Arrays.asList(dateTimeDim))
        .addAttribute(new Attribute( "long_name", "Date of reference for Julian days"))
        .addAttribute(new Attribute( "conventions", "YYYYMMDDHHMISS"))
        .addAttribute(new Attribute("_FillValue", " "));

    Variable.Builder varDateCreation = builder.addVariable("DATE_CREATION", DataType.STRING , Arrays.asList(dateTimeDim))
        .addAttribute(new Attribute( "long_name", "Date of file creation"))
        .addAttribute(new Attribute( "conventions", "YYYYMMDDHHMISS"))
        .addAttribute(new Attribute("_FillValue", " "));

    Variable.Builder varDateUpdate = builder.addVariable("DATE_UPDATE", DataType.STRING , Arrays.asList(dateTimeDim))
        .addAttribute(new Attribute( "long_name", "Date of update of this file"))
        .addAttribute(new Attribute( "conventions", "YYYYMMDDHHMISS"))
        .addAttribute(new Attribute("_FillValue", " "));

    Variable.Builder varPlatformNum = builder.addVariable("PLATFORM_NUMBER", DataType.STRING, Arrays.asList(nProfDim, string8Dim))
        .addAttribute(new Attribute("long_name", "Float unique identifier"))
        .addAttribute(new Attribute("conventions", "WMO float identifier : A9IIIII"))
        .addAttribute(new Attribute("_FillValue", " "));

    Variable.Builder varProjectName = builder.addVariable("PROJECT_NAME", DataType.STRING, Arrays.asList(nProfDim, string64Dim))
        .addAttribute(new Attribute("long_name", "Name of the project"))
        .addAttribute(new Attribute("_FillValue", " "));

    Variable.Builder varPiName = builder.addVariable("PI_NAME", DataType.STRING, Arrays.asList(nProfDim, string64Dim))
        .addAttribute(new Attribute("long_name", "Name of the principal investigator"))
        .addAttribute(new Attribute("_FillValue", " "));

    Variable.Builder varStationParameters = builder.addVariable("STATION_PARAMETERS", DataType.STRING, Arrays.asList(nProfDim,nParamDim, string16Dim))
        .addAttribute(new Attribute("long_name", "List of available parameters for the station"))
        .addAttribute(new Attribute("conventions", "Argo reference table 3"))
        .addAttribute(new Attribute("_FillValue", " "));

    Variable.Builder varCycleNumber = builder.addVariable("CYCLE_NUMBER", DataType.INT, Arrays.asList(nProfDim))
        .addAttribute(new Attribute("long_name", "Float cycle number"))
        .addAttribute(new Attribute("conventions", "0...N, 0 : launch cycle (if exists), 1 : first complete cycle"))
        .addAttribute(new Attribute("_FillValue", 99999));

    Variable.Builder varDirection = builder.addVariable("DIRECTION", DataType.STRING, Arrays.asList(nProfDim))
        .addAttribute(new Attribute("long_name", "Direction of the station profiles"))
        .addAttribute(new Attribute("conventions", "A: ascending profiles, D: descending profiles"))
        .addAttribute(new Attribute("_FillValue", " "));

    Variable.Builder varDataCentre = builder.addVariable("DATA_CENTRE", DataType.STRING, Arrays.asList(nProfDim, string2Dim))
        .addAttribute(new Attribute("long_name", "Data centre in charge of float data processing"))
        .addAttribute(new Attribute("conventions", "Argo reference table 4"))
        .addAttribute(new Attribute("_FillValue", " "));

    Variable.Builder varDcReference = builder.addVariable("DC_REFERENCE", DataType.STRING, Arrays.asList(nProfDim, string32Dim))
        .addAttribute(new Attribute("long_name", "Station unique identifier in data centre"))
        .addAttribute(new Attribute("conventions", "Data centre convention"))
        .addAttribute(new Attribute("_FillValue", " "));

    Variable.Builder varDataStateIndicator = builder.addVariable("DATA_STATE_INDICATOR", DataType.STRING, Arrays.asList(nProfDim, string4Dim))
        .addAttribute(new Attribute("long_name", "Degree of processing the data have passed through"))
        .addAttribute(new Attribute("conventions", "Argo reference table 6"))
        .addAttribute(new Attribute("_FillValue", " "));

    Variable.Builder varDataMode = builder.addVariable("DATA_MODE", DataType.STRING, Arrays.asList(nProfDim))
        .addAttribute(new Attribute("long_name", "Delayed mode or real time data"))
        .addAttribute(new Attribute("conventions", "R : real time; D : delayed mode; A : real time with adjustment"))
        .addAttribute(new Attribute("_FillValue", " "));

    Variable.Builder varPlatformType = builder.addVariable("PLATFORM_TYPE", DataType.STRING, Arrays.asList(nProfDim, string32Dim))
        .addAttribute(new Attribute("long_name", "Type of float"))
        .addAttribute(new Attribute("conventions", "Argo reference table 23"))
        .addAttribute(new Attribute("_FillValue", " "));

    Variable.Builder varFloatSerialNum = builder.addVariable("FLOAT_SERIAL_NO", DataType.STRING, Arrays.asList(nProfDim, string32Dim))
        .addAttribute(new Attribute("long_name", "Serial number of the float"))
        .addAttribute(new Attribute("_FillValue", " "));

    Variable.Builder varFirmwareVersion = builder.addVariable("FIRMWARE_VERSION", DataType.STRING, Arrays.asList(nProfDim, string32Dim))
        .addAttribute(new Attribute("long_name", "Instrument firmware version"))
        .addAttribute(new Attribute("_FillValue", " "));

    Variable.Builder varWmoInstType = builder.addVariable("WMO_INST_TYPE", DataType.STRING, Arrays.asList(nProfDim, string4Dim))
        .addAttribute(new Attribute("long_name", "Coded instrument type"))
        .addAttribute(new Attribute("conventions", "Argo reference table 8"))
        .addAttribute(new Attribute("_FillValue", " "));

    Variable.Builder varJuld = builder.addVariable("JULD", DataType.DOUBLE, Arrays.asList(nProfDim))
        .addAttribute(new Attribute("long_name", "Julian day (UTC) of the station relative to REFERENCE_DATE_TIME"))
        .addAttribute(new Attribute("standard_name", "time"))
        .addAttribute(new Attribute("units", "days since 1950-01-01 00:00:00 UTC"))
        .addAttribute(new Attribute("conventions", "Relative julian days with decimal part (as parts of day)"))
        .addAttribute(new Attribute("resolution", 0.0))
        .addAttribute(new Attribute("_FillValue", 999999.0))
        .addAttribute(new Attribute("axis", "T"));

    Variable.Builder varJuldQc = builder.addVariable("JULD_QC", DataType.STRING, Arrays.asList(nProfDim))
        .addAttribute(new Attribute("long_name", "Quality on date and time"))
        .addAttribute(new Attribute("conventions", "Argo reference table 2"))
        .addAttribute(new Attribute("_FillValue", " "));

    Variable.Builder varJuldLocation = builder.addVariable("JULD_LOCATION", DataType.DOUBLE, Arrays.asList(nProfDim))
        .addAttribute(new Attribute("long_name", "Julian day (UTC) of the location relative to REFERENCE_DATE_TIME"))
        .addAttribute(new Attribute("units", "days since 1950-01-01 00:00:00 UTC"))
        .addAttribute(new Attribute("conventions", "Relative julian days with decimal part (as parts of day)"))
        .addAttribute(new Attribute("resolution", 0.0))
        .addAttribute(new Attribute("_FillValue", 999999.0));

    Variable.Builder varLatitude = builder.addVariable("LATITUDE", DataType.DOUBLE, Arrays.asList(nProfDim))
        .addAttribute(new Attribute("long_name", "Latitude of the station, best estimate"))
        .addAttribute(new Attribute("standard_name", "latitude"))
        .addAttribute(new Attribute("units", "degree_north"))
        .addAttribute(new Attribute("_FillValue", 99999.0))
        .addAttribute(new Attribute("valid_min", -90.0))
        .addAttribute(new Attribute("valid_max", 90.0))
        .addAttribute(new Attribute("axis", "Y"));

    Variable.Builder varLongitude = builder.addVariable("LONGITUDE", DataType.DOUBLE, Arrays.asList(nProfDim))
        .addAttribute(new Attribute("long_name", "Longitude of the station, best estimate"))
        .addAttribute(new Attribute("standard_name", "longitude"))
        .addAttribute(new Attribute("units", "degree_east"))
        .addAttribute(new Attribute("_FillValue", 99999.0))
        .addAttribute(new Attribute("valid_min", -180.0))
        .addAttribute(new Attribute("valid_max", 180.0))
        .addAttribute(new Attribute("axis", "X"));

    Variable.Builder varPositionQc = builder.addVariable("POSITION_QC", DataType.STRING, Arrays.asList(nProfDim))
        .addAttribute(new Attribute("long_name", "Quality on position (latitude and longitude)"))
        .addAttribute(new Attribute("conventions", "Argo reference table 2"))
        .addAttribute(new Attribute("_FillValue", " "));

    Variable.Builder varPositionSystem = builder.addVariable("POSITIONING_SYSTEM", DataType.STRING, Arrays.asList(nProfDim, string8Dim))
        .addAttribute(new Attribute("long_name", "Positioning system"))
        .addAttribute(new Attribute("_FillValue", " "));

    Variable.Builder varProfilePresQc = builder.addVariable("PROFILE_PRES_QC", DataType.STRING, Arrays.asList(nProfDim))
        .addAttribute(new Attribute("long_name", "Global quality flag of PRES profile"))
        .addAttribute(new Attribute("conventions", "Argo reference table 2a"))
        .addAttribute(new Attribute("_FillValue", " "));

    Variable.Builder varProfileTempQc = builder.addVariable("PROFILE_TEMP_QC", DataType.STRING, Arrays.asList(nProfDim))
        .addAttribute(new Attribute("long_name", "Global quality flag of TEMP profile"))
        .addAttribute(new Attribute("conventions", "Argo reference table 2a"))
        .addAttribute(new Attribute("_FillValue", " "));

    Variable.Builder varProfilePsalQc = builder.addVariable("PROFILE_PSAL_QC", DataType.STRING, Arrays.asList(nProfDim))
        .addAttribute(new Attribute("long_name", "Global quality flag of PSAL profile"))
        .addAttribute(new Attribute("conventions", "Argo reference table 2a"))
        .addAttribute(new Attribute("_FillValue", " "));

    Variable.Builder varVerticalSamplingScheme = builder.addVariable("VERTICAL_SAMPLING_SCHEME", DataType.STRING, Arrays.asList(nProfDim, string256Dim))
        .addAttribute(new Attribute("long_name", "Vertical sampling scheme"))
        .addAttribute(new Attribute("conventions", "Argo reference table 16"))
        .addAttribute(new Attribute("_FillValue", " "));

    Variable.Builder varVConfigMissionNum = builder.addVariable("CONFIG_MISSION_NUMBER", DataType.INT, Arrays.asList(nProfDim))
        .addAttribute(new Attribute("long_name", "Unique number denoting the missions performed by the float"))
        .addAttribute(new Attribute("conventions", "1...N, 1 : first complete mission"))
        .addAttribute(new Attribute("_FillValue", 99999));

    Variable.Builder varPres = builder.addVariable("PRES", DataType.FLOAT, Arrays.asList(nProfDim, nLevelsDim))
        .addAttribute(new Attribute("long_name", "Sea water pressure, equals 0 at sea-level"))
        .addAttribute(new Attribute("standard_name", "sea_water_pressure"))
        .addAttribute(new Attribute("units", "decibar"))
        .addAttribute(new Attribute("_FillValue", 99999.0f))
        .addAttribute(new Attribute("valid_min", 0.0f))
        .addAttribute(new Attribute("valid_max", 12000.0f))
        .addAttribute(new Attribute("C_format", "%7.1f"))
        .addAttribute(new Attribute("FORTRAN_format", "F7.1"))
        .addAttribute(new Attribute("resolution",1.0f))
        .addAttribute(new Attribute("axis", "Z"));

    Variable.Builder varPresQc = builder.addVariable("PRES_QC", DataType.STRING, Arrays.asList(nProfDim, nLevelsDim))
        .addAttribute(new Attribute("long_name", "quality flag"))
        .addAttribute(new Attribute("conventions", "Argo reference table 2"))
        .addAttribute(new Attribute("_FillValue", " "));

    Variable.Builder varPresAdjusted = builder.addVariable("PRES_ADJUSTED", DataType.FLOAT, Arrays.asList(nProfDim, nLevelsDim))
        .addAttribute(new Attribute("long_name", "Sea water pressure, equals 0 at sea-level"))
        .addAttribute(new Attribute("standard_name", "sea_water_pressure"))
        .addAttribute(new Attribute("units", "decibar"))
        .addAttribute(new Attribute("_FillValue", 99999.0f))
        .addAttribute(new Attribute("valid_min", 0.0f))
        .addAttribute(new Attribute("valid_max", 12000.0f))
        .addAttribute(new Attribute("C_format", "%7.1f"))
        .addAttribute(new Attribute("FORTRAN_format", "F7.1"))
        .addAttribute(new Attribute("resolution",1.0f))
        .addAttribute(new Attribute("axis", "Z"));

    Variable.Builder varPresAdjustedQc = builder.addVariable("PRES_ADJUSTED_QC", DataType.STRING, Arrays.asList(nProfDim, nLevelsDim))
        .addAttribute(new Attribute("long_name", "quality flag"))
        .addAttribute(new Attribute("conventions", "Argo reference table 2"))
        .addAttribute(new Attribute("_FillValue", " "));

    Variable.Builder varPresAdjustedError = builder.addVariable("PRES_ADJUSTED_ERROR", DataType.FLOAT, Arrays.asList(nProfDim, nLevelsDim))
        .addAttribute(new Attribute("long_name", "Contains the error on the adjusted values as determined by the delayed mode QC process"))
        .addAttribute(new Attribute("units", "decibar"))
        .addAttribute(new Attribute("_FillValue", 99999.0f))
        .addAttribute(new Attribute("C_format", "%7.1f"))
        .addAttribute(new Attribute("FORTRAN_format", "F7.1"))
        .addAttribute(new Attribute("resolution",1.0f));

    Variable.Builder varTemp= builder.addVariable("TEMP", DataType.FLOAT, Arrays.asList(nProfDim, nLevelsDim))
        .addAttribute(new Attribute("long_name", "Sea temperature in-situ ITS-90 scale"))
        .addAttribute(new Attribute("standard_name", "sea_water_temperature"))
        .addAttribute(new Attribute("units", "degree_Celsius"))
        .addAttribute(new Attribute("_FillValue", 99999.0f))
        .addAttribute(new Attribute("valid_min", -2.5f))
        .addAttribute(new Attribute("valid_max", 40))
        .addAttribute(new Attribute("C_format", "%9.3f"))
        .addAttribute(new Attribute("FORTRAN_format", "F9.3"))
        .addAttribute(new Attribute("resolution",0.001f));

    Variable.Builder varTempQc = builder.addVariable("TEMP_QC", DataType.STRING, Arrays.asList(nProfDim, nLevelsDim))
        .addAttribute(new Attribute("long_name", "quality flag"))
        .addAttribute(new Attribute("conventions", "Argo reference table 2"))
        .addAttribute(new Attribute("_FillValue", " "));

    Variable.Builder varTempAdjusted= builder.addVariable("TEMP_ADJUSTED", DataType.FLOAT, Arrays.asList(nProfDim, nLevelsDim))
        .addAttribute(new Attribute("long_name", "Sea temperature in-situ ITS-90 scale"))
        .addAttribute(new Attribute("standard_name", "sea_water_temperature"))
        .addAttribute(new Attribute("units", "degree_Celsius"))
        .addAttribute(new Attribute("_FillValue", 99999.0f))
        .addAttribute(new Attribute("valid_min", -2.5f))
        .addAttribute(new Attribute("valid_max", 40))
        .addAttribute(new Attribute("C_format", "%9.3f"))
        .addAttribute(new Attribute("FORTRAN_format", "F9.3"))
        .addAttribute(new Attribute("resolution",0.001f));

    Variable.Builder varTempAdjustedQc = builder.addVariable("TEMP_ADJUSTED_QC", DataType.STRING, Arrays.asList(nProfDim, nLevelsDim))
        .addAttribute(new Attribute("long_name", "quality flag"))
        .addAttribute(new Attribute("conventions", "Argo reference table 2"))
        .addAttribute(new Attribute("_FillValue", " "));

    Variable.Builder varTempAdjustedError = builder.addVariable("TEMP_ADJUSTED_ERROR", DataType.FLOAT, Arrays.asList(nProfDim, nLevelsDim))
        .addAttribute(new Attribute("long_name", "SContains the error on the adjusted values as determined by the delayed mode QC process"))
        .addAttribute(new Attribute("units", "degree_Celsius"))
        .addAttribute(new Attribute("_FillValue", 99999.0f))
        .addAttribute(new Attribute("C_format", "%9.3f"))
        .addAttribute(new Attribute("FORTRAN_format", "F9.3"))
        .addAttribute(new Attribute("resolution",0.001f));

    Variable.Builder VarPsal= builder.addVariable("PSAL", DataType.FLOAT, Arrays.asList(nProfDim, nLevelsDim))
        .addAttribute(new Attribute("long_name", "Practical salinity"))
        .addAttribute(new Attribute("standard_name", "sea_water_salinity"))
        .addAttribute(new Attribute("units", "psu"))
        .addAttribute(new Attribute("_FillValue", 99999.0f))
        .addAttribute(new Attribute("valid_min", 2.0f))
        .addAttribute(new Attribute("valid_max", 41.0f))
        .addAttribute(new Attribute("C_format", "%9.3f"))
        .addAttribute(new Attribute("FORTRAN_format", "F9.3"))
        .addAttribute(new Attribute("resolution",0.001f));

    Variable.Builder VarPsalQc = builder.addVariable("PSAL_QC", DataType.STRING, Arrays.asList(nProfDim, nLevelsDim))
        .addAttribute(new Attribute("long_name", "quality flag"))
        .addAttribute(new Attribute("conventions", "Argo reference table 2"))
        .addAttribute(new Attribute("_FillValue", " "));

    Variable.Builder VarPsalAdjusted= builder.addVariable("PSAL_ADJUSTED", DataType.FLOAT, Arrays.asList(nProfDim, nLevelsDim))
        .addAttribute(new Attribute("long_name", "Practical salinity"))
        .addAttribute(new Attribute("standard_name", "sea_water_salinity"))
        .addAttribute(new Attribute("units", "psu"))
        .addAttribute(new Attribute("_FillValue", 99999.0f))
        .addAttribute(new Attribute("valid_min", 2.0f))
        .addAttribute(new Attribute("valid_max", 41.0f))
        .addAttribute(new Attribute("C_format", "%9.3f"))
        .addAttribute(new Attribute("FORTRAN_format", "F9.3"))
        .addAttribute(new Attribute("resolution",0.001f));

    Variable.Builder VarPsalAdjustedQc = builder.addVariable("PSAL_ADJUSTED_QC", DataType.STRING, Arrays.asList(nProfDim, nLevelsDim))
        .addAttribute(new Attribute("long_name", "quality flag"))
        .addAttribute(new Attribute("conventions", "Argo reference table 2"))
        .addAttribute(new Attribute("_FillValue", " "));

    Variable.Builder VarPsalAdjustedError= builder.addVariable("PSAL_ADJUSTED_ERROR", DataType.FLOAT, Arrays.asList(nProfDim, nLevelsDim))
        .addAttribute(new Attribute("long_name", "Contains the error on the adjusted values as determined by the delayed mode QC process"))
        .addAttribute(new Attribute("units", "psu"))
        .addAttribute(new Attribute("_FillValue", 99999.0f))
        .addAttribute(new Attribute("C_format", "%9.3f"))
        .addAttribute(new Attribute("FORTRAN_format", "F9.3"))
        .addAttribute(new Attribute("resolution",0.001f));

    Variable.Builder VarParameter = builder.addVariable("PARAMETER", DataType.STRING, Arrays.asList(nProfDim, nCalibDim, nParamDim, string16Dim))
        .addAttribute(new Attribute("long_name", "List of parameters with calibration information"))
        .addAttribute(new Attribute("conventions", "Argo reference table 3"))
        .addAttribute(new Attribute("_FillValue", " "));

    Variable.Builder VarScientifiCalibEquation = builder.addVariable("SCIENTIFIC_CALIB_EQUATION", DataType.STRING, Arrays.asList(nProfDim, nCalibDim, nParamDim, string256Dim))
        .addAttribute(new Attribute("long_name", "Calibration equation for this parameter"))
        .addAttribute(new Attribute("_FillValue", " "));

    Variable.Builder VarScientifiCalibCoefficient = builder.addVariable("SCIENTIFIC_CALIB_COEFFICIENT", DataType.STRING, Arrays.asList(nProfDim, nCalibDim, nParamDim, string256Dim))
        .addAttribute(new Attribute("long_name", "Calibration coefficients for this parameter"))
        .addAttribute(new Attribute("_FillValue", " "));

    Variable.Builder VarScientifiCalibComment = builder.addVariable("SCIENTIFIC_CALIB_COMMENT", DataType.STRING, Arrays.asList(nProfDim, nCalibDim, nParamDim, string256Dim))
        .addAttribute(new Attribute("long_name", "Comment applying to this parameter calibration"))
        .addAttribute(new Attribute("_FillValue", " "));

    Variable.Builder VarScientifiCalibDate = builder.addVariable("SCIENTIFIC_CALIB_DATE", DataType.STRING, Arrays.asList(nProfDim, nCalibDim, nParamDim, dateTimeDim))
        .addAttribute(new Attribute("long_name", "Date of calibration"))
        .addAttribute(new Attribute("conventions", "YYYYMMDDHHMISS"))
        .addAttribute(new Attribute("_FillValue", " "));

    Variable.Builder VarHistoryInstitution = builder.addVariable("HISTORY_INSTITUTION", DataType.STRING, Arrays.asList(nHistoryDim, nProfDim, string4Dim))
        .addAttribute(new Attribute("long_name", "Institution which performed action"))
        .addAttribute(new Attribute("conventions", "Argo reference table 4"))
        .addAttribute(new Attribute("_FillValue", " "));

    Variable.Builder VarHistoryStep = builder.addVariable("HISTORY_STEP", DataType.STRING, Arrays.asList(nHistoryDim, nProfDim, string4Dim))
        .addAttribute(new Attribute("long_name", "Step in data processing"))
        .addAttribute(new Attribute("conventions", "Argo reference table 12"))
        .addAttribute(new Attribute("_FillValue", " "));

    Variable.Builder VarHistorySoftware = builder.addVariable("HISTORY_SOFTWARE", DataType.STRING, Arrays.asList(nHistoryDim, nProfDim, string4Dim))
        .addAttribute(new Attribute("long_name", "Name of software which performed action"))
        .addAttribute(new Attribute("conventions", "Institution dependent"))
        .addAttribute(new Attribute("_FillValue", " "));

    Variable.Builder VarHistorySoftwareRelease = builder.addVariable("HISTORY_SOFTWARE_RELEASE", DataType.STRING, Arrays.asList(nHistoryDim, nProfDim, string4Dim))
        .addAttribute(new Attribute("long_name", "Version/release of software which performed action"))
        .addAttribute(new Attribute("conventions", "Institution dependent"))
        .addAttribute(new Attribute("_FillValue", " "));

    Variable.Builder VarHistoryReference = builder.addVariable("HISTORY_REFERENCE", DataType.STRING, Arrays.asList(nHistoryDim, nProfDim, string64Dim))
        .addAttribute(new Attribute("long_name", "Reference of database"))
        .addAttribute(new Attribute("conventions", "Institution dependent"))
        .addAttribute(new Attribute("_FillValue", " "));

    Variable.Builder VarHistoryDate = builder.addVariable("HISTORY_DATE", DataType.STRING, Arrays.asList(nHistoryDim, nProfDim, dateTimeDim))
        .addAttribute(new Attribute("long_name", "history record was created"))
        .addAttribute(new Attribute("conventions", "YYYYMMDDHHMISS"))
        .addAttribute(new Attribute("_FillValue", " "));

    Variable.Builder VarHistoryAction = builder.addVariable("HISTORY_ACTION", DataType.STRING, Arrays.asList(nHistoryDim, nProfDim, string4Dim))
        .addAttribute(new Attribute("long_name", "Action performed on data"))
        .addAttribute(new Attribute("conventions", "Argo reference table 7"))
        .addAttribute(new Attribute("_FillValue", " "));

    Variable.Builder VarHistoryParameter = builder.addVariable("HISTORY_PARAMETER", DataType.STRING, Arrays.asList(nHistoryDim, nProfDim, string4Dim))
        .addAttribute(new Attribute("long_name", "Station parameter action is performed on"))
        .addAttribute(new Attribute("conventions", "Argo reference table 3"))
        .addAttribute(new Attribute("_FillValue", " "));

    Variable.Builder VarHistoryStartPres = builder.addVariable("HISTORY_START_PRES", DataType.FLOAT, Arrays.asList(nHistoryDim, nProfDim))
        .addAttribute(new Attribute("long_name", "Start pressure action applied on"))
        .addAttribute(new Attribute("units", "decibar"))
        .addAttribute(new Attribute("_FillValue", 99999.0f));

    Variable.Builder VarHistoryStopPres = builder.addVariable("HISTORY_STOP_PRES", DataType.FLOAT, Arrays.asList(nHistoryDim, nProfDim))
        .addAttribute(new Attribute("long_name", "Stop pressure action applied on"))
        .addAttribute(new Attribute("units", "decibar"))
        .addAttribute(new Attribute("_FillValue", 99999.0f));

    Variable.Builder VarHistoryPreviousValue = builder.addVariable("HISTORY_PREVIOUS_VALUE", DataType.FLOAT, Arrays.asList(nHistoryDim, nProfDim))
        .addAttribute(new Attribute("long_name", "Parameter/Flag previous value before action"))
        .addAttribute(new Attribute("_FillValue", 99999.0f));

    Variable.Builder VarHistoryQcTest = builder.addVariable("HISTORY_QCTEST", DataType.STRING, Arrays.asList(nHistoryDim, nProfDim, string16Dim))
        .addAttribute(new Attribute("long_name", "Documentation of tests performed, tests failed (in hex form)"))
        .addAttribute(new Attribute("conventions", "Write tests performed when ACTION=QCP$; tests failed when ACTION=QCF$"))
        .addAttribute(new Attribute("_FillValue", " "));

    // global attributes:
    builder.addAttribute(new Attribute("title", "Argo float vertical profile"));
    builder.addAttribute(new Attribute("institution", "FR GDAC"));
    builder.addAttribute(new Attribute("source", "Argo float"));
    builder.addAttribute(new Attribute("history", "2023-09-05T09:51:37Z creation"));
    builder.addAttribute(new Attribute("references", "http://www.argodatamgt.org/Documentation"));
    builder.addAttribute(new Attribute("user_manual_version", "3.1"));
    builder.addAttribute(new Attribute("Conventions", "Argo-3.1 CF-1.6"));
    builder.addAttribute(new Attribute("featureType", "trajectoryProfile"));

    try (NetcdfFormatWriter writer = builder.build()) {
      // write data
    }


    /*
     variables:
    char DATA_TYPE(STRING16=16);
      :long_name = "Data type";
      :conventions = "Argo reference table 1";
      :_FillValue = " ";

    char FORMAT_VERSION(STRING4=4);
      :long_name = "File format version";
      :_FillValue = " ";

    char HANDBOOK_VERSION(STRING4=4);
      :long_name = "Data handbook version";
      :_FillValue = " ";

    char REFERENCE_DATE_TIME(DATE_TIME=14);
      :long_name = "Date of reference for Julian days";
      :conventions = "YYYYMMDDHHMISS";
      :_FillValue = " ";

    char DATE_CREATION(DATE_TIME=14);
      :long_name = "Date of file creation";
      :conventions = "YYYYMMDDHHMISS";
      :_FillValue = " ";

    char DATE_UPDATE(DATE_TIME=14);
      :long_name = "Date of update of this file";
      :conventions = "YYYYMMDDHHMISS";
      :_FillValue = " ";

    char PLATFORM_NUMBER(N_PROF=66, STRING8=8);
      :long_name = "Float unique identifier";
      :conventions = "WMO float identifier : A9IIIII";
      :_FillValue = " ";

    char PROJECT_NAME(N_PROF=66, STRING64=64);
      :long_name = "Name of the project";
      :_FillValue = " ";

    char PI_NAME(N_PROF=66, STRING64=64);
      :long_name = "Name of the principal investigator";
      :_FillValue = " ";

    char STATION_PARAMETERS(N_PROF=66, N_PARAM=3, STRING16=16);
      :long_name = "List of available parameters for the station";
      :conventions = "Argo reference table 3";
      :_FillValue = " ";

    int CYCLE_NUMBER(N_PROF=66);
      :long_name = "Float cycle number";
      :conventions = "0...N, 0 : launch cycle (if exists), 1 : first complete cycle";
      :_FillValue = 99999; // int

    char DIRECTION(N_PROF=66);
      :long_name = "Direction of the station profiles";
      :conventions = "A: ascending profiles, D: descending profiles";
      :_FillValue = " ";

    char DATA_CENTRE(N_PROF=66, STRING2=2);
      :long_name = "Data centre in charge of float data processing";
      :conventions = "Argo reference table 4";
      :_FillValue = " ";

    char DC_REFERENCE(N_PROF=66, STRING32=32);
      :long_name = "Station unique identifier in data centre";
      :conventions = "Data centre convention";
      :_FillValue = " ";

    char DATA_STATE_INDICATOR(N_PROF=66, STRING4=4);
      :long_name = "Degree of processing the data have passed through";
      :conventions = "Argo reference table 6";
      :_FillValue = " ";

    char DATA_MODE(N_PROF=66);
      :long_name = "Delayed mode or real time data";
      :conventions = "R : real time; D : delayed mode; A : real time with adjustment";
      :_FillValue = " ";

    char PLATFORM_TYPE(N_PROF=66, STRING32=32);
      :long_name = "Type of float";
      :conventions = "Argo reference table 23";
      :_FillValue = " ";

    char FLOAT_SERIAL_NO(N_PROF=66, STRING32=32);
      :long_name = "Serial number of the float";
      :_FillValue = " ";

    char FIRMWARE_VERSION(N_PROF=66, STRING32=32);
      :long_name = "Instrument firmware version";
      :_FillValue = " ";

    char WMO_INST_TYPE(N_PROF=66, STRING4=4);
      :long_name = "Coded instrument type";
      :conventions = "Argo reference table 8";
      :_FillValue = " ";

    double JULD(N_PROF=66);
      :long_name = "Julian day (UTC) of the station relative to REFERENCE_DATE_TIME";
      :standard_name = "time";
      :units = "days since 1950-01-01 00:00:00 UTC";
      :conventions = "Relative julian days with decimal part (as parts of day)";
      :resolution = 0.0; // double
      :_FillValue = 999999.0; // double
      :axis = "T";

    char JULD_QC(N_PROF=66);
      :long_name = "Quality on date and time";
      :conventions = "Argo reference table 2";
      :_FillValue = " ";

    double JULD_LOCATION(N_PROF=66);
      :long_name = "Julian day (UTC) of the location relative to REFERENCE_DATE_TIME";
      :units = "days since 1950-01-01 00:00:00 UTC";
      :conventions = "Relative julian days with decimal part (as parts of day)";
      :resolution = 0.0; // double
      :_FillValue = 999999.0; // double

    double LATITUDE(N_PROF=66);
      :long_name = "Latitude of the station, best estimate";
      :standard_name = "latitude";
      :units = "degree_north";
      :_FillValue = 99999.0; // double
      :valid_min = -90.0; // double
      :valid_max = 90.0; // double
      :axis = "Y";

    double LONGITUDE(N_PROF=66);
      :long_name = "Longitude of the station, best estimate";
      :standard_name = "longitude";
      :units = "degree_east";
      :_FillValue = 99999.0; // double
      :valid_min = -180.0; // double
      :valid_max = 180.0; // double
      :axis = "X";

    char POSITION_QC(N_PROF=66);
      :long_name = "Quality on position (latitude and longitude)";
      :conventions = "Argo reference table 2";
      :_FillValue = " ";

    char POSITIONING_SYSTEM(N_PROF=66, STRING8=8);
      :long_name = "Positioning system";
      :_FillValue = " ";

    char PROFILE_PRES_QC(N_PROF=66);
      :long_name = "Global quality flag of PRES profile";
      :conventions = "Argo reference table 2a";
      :_FillValue = " ";

    char PROFILE_TEMP_QC(N_PROF=66);
      :long_name = "Global quality flag of TEMP profile";
      :conventions = "Argo reference table 2a";
      :_FillValue = " ";

    char PROFILE_PSAL_QC(N_PROF=66);
      :long_name = "Global quality flag of PSAL profile";
      :conventions = "Argo reference table 2a";
      :_FillValue = " ";

    char VERTICAL_SAMPLING_SCHEME(N_PROF=66, STRING256=256);
      :long_name = "Vertical sampling scheme";
      :conventions = "Argo reference table 16";
      :_FillValue = " ";

    int CONFIG_MISSION_NUMBER(N_PROF=66);
      :long_name = "Unique number denoting the missions performed by the float";
      :conventions = "1...N, 1 : first complete mission";
      :_FillValue = 99999; // int

    float PRES(N_PROF=66, N_LEVELS=93);
      :long_name = "Sea water pressure, equals 0 at sea-level";
      :standard_name = "sea_water_pressure";
      :_FillValue = 99999.0f; // float
      :units = "decibar";
      :valid_min = 0.0f; // float
      :valid_max = 12000.0f; // float
      :C_format = "%7.1f";
      :FORTRAN_format = "F7.1";
      :resolution = 1.0f; // float
      :axis = "Z";

    char PRES_QC(N_PROF=66, N_LEVELS=93);
      :long_name = "quality flag";
      :conventions = "Argo reference table 2";
      :_FillValue = " ";

    float PRES_ADJUSTED(N_PROF=66, N_LEVELS=93);
      :long_name = "Sea water pressure, equals 0 at sea-level";
      :standard_name = "sea_water_pressure";
      :_FillValue = 99999.0f; // float
      :units = "decibar";
      :valid_min = 0.0f; // float
      :valid_max = 12000.0f; // float
      :C_format = "%7.1f";
      :FORTRAN_format = "F7.1";
      :resolution = 1.0f; // float
      :axis = "Z";

    char PRES_ADJUSTED_QC(N_PROF=66, N_LEVELS=93);
      :long_name = "quality flag";
      :conventions = "Argo reference table 2";
      :_FillValue = " ";

    float PRES_ADJUSTED_ERROR(N_PROF=66, N_LEVELS=93);
      :long_name = "Contains the error on the adjusted values as determined by the delayed mode QC process";
      :_FillValue = 99999.0f; // float
      :units = "decibar";
      :C_format = "%7.1f";
      :FORTRAN_format = "F7.1";
      :resolution = 1.0f; // float

    float TEMP(N_PROF=66, N_LEVELS=93);
      :long_name = "Sea temperature in-situ ITS-90 scale";
      :standard_name = "sea_water_temperature";
      :_FillValue = 99999.0f; // float
      :units = "degree_Celsius";
      :valid_min = -2.5f; // float
      :valid_max = 40.0f; // float
      :C_format = "%9.3f";
      :FORTRAN_format = "F9.3";
      :resolution = 0.001f; // float

    char TEMP_QC(N_PROF=66, N_LEVELS=93);
      :long_name = "quality flag";
      :conventions = "Argo reference table 2";
      :_FillValue = " ";

    float TEMP_ADJUSTED(N_PROF=66, N_LEVELS=93);
      :long_name = "Sea temperature in-situ ITS-90 scale";
      :standard_name = "sea_water_temperature";
      :_FillValue = 99999.0f; // float
      :units = "degree_Celsius";
      :valid_min = -2.5f; // float
      :valid_max = 40.0f; // float
      :C_format = "%9.3f";
      :FORTRAN_format = "F9.3";
      :resolution = 0.001f; // float

    char TEMP_ADJUSTED_QC(N_PROF=66, N_LEVELS=93);
      :long_name = "quality flag";
      :conventions = "Argo reference table 2";
      :_FillValue = " ";

    float TEMP_ADJUSTED_ERROR(N_PROF=66, N_LEVELS=93);
      :long_name = "Contains the error on the adjusted values as determined by the delayed mode QC process";
      :_FillValue = 99999.0f; // float
      :units = "degree_Celsius";
      :C_format = "%9.3f";
      :FORTRAN_format = "F9.3";
      :resolution = 0.001f; // float

    float PSAL(N_PROF=66, N_LEVELS=93);
      :long_name = "Practical salinity";
      :standard_name = "sea_water_salinity";
      :_FillValue = 99999.0f; // float
      :units = "psu";
      :valid_min = 2.0f; // float
      :valid_max = 41.0f; // float
      :C_format = "%9.3f";
      :FORTRAN_format = "F9.3";
      :resolution = 0.001f; // float

    char PSAL_QC(N_PROF=66, N_LEVELS=93);
      :long_name = "quality flag";
      :conventions = "Argo reference table 2";
      :_FillValue = " ";

    float PSAL_ADJUSTED(N_PROF=66, N_LEVELS=93);
      :long_name = "Practical salinity";
      :standard_name = "sea_water_salinity";
      :_FillValue = 99999.0f; // float
      :units = "psu";
      :valid_min = 2.0f; // float
      :valid_max = 41.0f; // float
      :C_format = "%9.3f";
      :FORTRAN_format = "F9.3";
      :resolution = 0.001f; // float

    char PSAL_ADJUSTED_QC(N_PROF=66, N_LEVELS=93);
      :long_name = "quality flag";
      :conventions = "Argo reference table 2";
      :_FillValue = " ";

    float PSAL_ADJUSTED_ERROR(N_PROF=66, N_LEVELS=93);
      :long_name = "Contains the error on the adjusted values as determined by the delayed mode QC process";
      :_FillValue = 99999.0f; // float
      :units = "psu";
      :C_format = "%9.3f";
      :FORTRAN_format = "F9.3";
      :resolution = 0.001f; // float

    char PARAMETER(N_PROF=66, N_CALIB=1, N_PARAM=3, STRING16=16);
      :long_name = "List of parameters with calibration information";
      :conventions = "Argo reference table 3";
      :_FillValue = " ";

    char SCIENTIFIC_CALIB_EQUATION(N_PROF=66, N_CALIB=1, N_PARAM=3, STRING256=256);
      :long_name = "Calibration equation for this parameter";
      :_FillValue = " ";

    char SCIENTIFIC_CALIB_COEFFICIENT(N_PROF=66, N_CALIB=1, N_PARAM=3, STRING256=256);
      :long_name = "Calibration coefficients for this equation";
      :_FillValue = " ";

    char SCIENTIFIC_CALIB_COMMENT(N_PROF=66, N_CALIB=1, N_PARAM=3, STRING256=256);
      :long_name = "Comment applying to this parameter calibration";
      :_FillValue = " ";

    char SCIENTIFIC_CALIB_DATE(N_PROF=66, N_CALIB=1, N_PARAM=3, DATE_TIME=14);
      :long_name = "Date of calibration";
      :conventions = "YYYYMMDDHHMISS";
      :_FillValue = " ";

    char HISTORY_INSTITUTION(N_HISTORY=0, N_PROF=66, STRING4=4);
      :long_name = "Institution which performed action";
      :conventions = "Argo reference table 4";
      :_FillValue = " ";

    char HISTORY_STEP(N_HISTORY=0, N_PROF=66, STRING4=4);
      :long_name = "Step in data processing";
      :conventions = "Argo reference table 12";
      :_FillValue = " ";

    char HISTORY_SOFTWARE(N_HISTORY=0, N_PROF=66, STRING4=4);
      :long_name = "Name of software which performed action";
      :conventions = "Institution dependent";
      :_FillValue = " ";

    char HISTORY_SOFTWARE_RELEASE(N_HISTORY=0, N_PROF=66, STRING4=4);
      :long_name = "Version/release of software which performed action";
      :conventions = "Institution dependent";
      :_FillValue = " ";

    char HISTORY_REFERENCE(N_HISTORY=0, N_PROF=66, STRING64=64);
      :long_name = "Reference of database";
      :conventions = "Institution dependent";
      :_FillValue = " ";

    char HISTORY_DATE(N_HISTORY=0, N_PROF=66, DATE_TIME=14);
      :long_name = "Date the history record was created";
      :conventions = "YYYYMMDDHHMISS";
      :_FillValue = " ";

    char HISTORY_ACTION(N_HISTORY=0, N_PROF=66, STRING4=4);
      :long_name = "Action performed on data";
      :conventions = "Argo reference table 7";
      :_FillValue = " ";

    char HISTORY_PARAMETER(N_HISTORY=0, N_PROF=66, STRING16=16);
      :long_name = "Station parameter action is performed on";
      :conventions = "Argo reference table 3";
      :_FillValue = " ";

    float HISTORY_START_PRES(N_HISTORY=0, N_PROF=66);
      :long_name = "Start pressure action applied on";
      :_FillValue = 99999.0f; // float
      :units = "decibar";

    float HISTORY_STOP_PRES(N_HISTORY=0, N_PROF=66);
      :long_name = "Stop pressure action applied on";
      :_FillValue = 99999.0f; // float
      :units = "decibar";

    float HISTORY_PREVIOUS_VALUE(N_HISTORY=0, N_PROF=66);
      :long_name = "Parameter/Flag previous value before action";
      :_FillValue = 99999.0f; // float

    char HISTORY_QCTEST(N_HISTORY=0, N_PROF=66, STRING16=16);
      :long_name = "Documentation of tests performed, tests failed (in hex form)";
      :conventions = "Write tests performed when ACTION=QCP$; tests failed when ACTION=QCF$";
      :_FillValue = " ";

  // global attributes:
  :title = "Argo float vertical profile";
  :institution = "FR GDAC";
  :source = "Argo float";
  :history = "2023-09-05T09:51:37Z creation";
  :references = "http://www.argodatamgt.org/Documentation";
  :user_manual_version = "3.1";
  :Conventions = "Argo-3.1 CF-1.6";
  :featureType = "trajectoryProfile";
}

     */

  }

  private int getNlevels(Set<Path> profileNcFiles) {
    throw new UnsupportedOperationException();
  }

  private int getNCalib(Set<Path> profileNcFiles) {
    throw new UnsupportedOperationException();
  }

  private int getNParamSize(Set<Path> profileNcFiles) {
    throw new UnsupportedOperationException();
  }

}
