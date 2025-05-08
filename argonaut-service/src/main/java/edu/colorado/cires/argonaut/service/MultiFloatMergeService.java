package edu.colorado.cires.argonaut.service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import ucar.ma2.DataType;
import ucar.nc2.Attribute;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFile;
import ucar.nc2.NetcdfFiles;
import ucar.nc2.Variable;
import ucar.nc2.write.NetcdfFormatWriter;

public class MultiFloatMergeService {

  // https://docs.unidata.ucar.edu/netcdf-java/5.4/userguide/writing_netcdf.html

  public void mergeFloats(Path outputFile, Set<Path> profileNcFiles) throws IOException {

    Profile profile = new Profile();
    profile.setDimensions(profileNcFiles);
//    int nParam = getNParamSize(profileNcFiles);
//    int nCalib = getNCalib(profileNcFiles);
//    int nLevels = getNlevels(profileNcFiles);

    NetcdfFormatWriter.Builder builder = NetcdfFormatWriter.createNewNetcdf3(outputFile.toString());

    Dimension dateTimeDim = builder.addDimension("DATE_TIME", 14);
    Dimension string256Dim = builder.addDimension("STRING256", 256);
    Dimension string64Dim = builder.addDimension("STRING64", 64);
    Dimension string32Dim = builder.addDimension("STRING32", 32);
    Dimension string16Dim = builder.addDimension("STRING16", 16);
    Dimension string8Dim = builder.addDimension("STRING8", 8);
    Dimension string4Dim = builder.addDimension("STRING4", 4);
    Dimension string2Dim = builder.addDimension("STRING2", 2);

    Dimension nProfDim = builder.addDimension("N_PROF", profile.getnProf());
    Dimension nParamDim = builder.addDimension("N_PARAM", profile.getnParam()); // 3, Maybe 4
    Dimension nLevelsDim = builder.addDimension("N_LEVELS", profile.getnLevels());
    Dimension nHistoryDim = builder.addUnlimitedDimension("N_HISTORY");
    Dimension nCalibDim = builder.addDimension("N_CALIB", profile.getnCalib()); // 1

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

    builder.setFill(true);
    try (NetcdfFormatWriter writer = builder.build()) {
      int index = 0;
      for (Path profileNcFile: profileNcFiles){
        try (NetcdfFile ncfile = NetcdfFiles.open(profileNcFile.toString())) {
          Variable fCycleNum =ncfile.findVariable("CYCLE_NUMBER");
          // ncfile.findVariable("CYCLE_NUMBER").read().getInt(0)
          int[] fShape = fCycleNum.getShape();
        } catch (IOException e) {
          throw new RuntimeException("An error opening profile nc file : " + profileNcFile, e);
        }
      }
    }catch (IOException e) {
      throw new RuntimeException("An error occurred creating multi float merge file : " + outputFile, e);
    }



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

  class Profile {
    int nProf;
    int nParam;
    int nCalib;
    int nLevels;

    public int getnProf() {
      return nProf;
    }

    public int getnParam() {
      return nParam;
    }

    public int getnCalib() {
      return nCalib;
    }

    public int getnLevels() {
      return nLevels;
    }

    public void setDimensions(Set<Path> profileNcFiles) {
      nProf = profileNcFiles.size();
      for (Path profileNcFile: profileNcFiles){
        try (NetcdfFile ncfile = NetcdfFiles.open(profileNcFile.toString())) {
          nParam = maxInt(nParam, ncfile.findDimension("N_PARAM").getLength());
          nCalib = maxInt(nCalib, ncfile.findDimension("N_CALIB").getLength());
          nLevels = maxInt(nLevels, ncfile.findDimension("N_LEVELS").getLength());
        } catch (IOException e) {
          throw new RuntimeException("An error opening profile nc file : " + profileNcFile, e);
        }
      }
    }

    private int maxInt(int x, int y){
      if (y > x){
        return y;
      }
      return x;
    }
  }
}
