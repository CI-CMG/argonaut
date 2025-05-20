package edu.colorado.cires.argonaut.service.merge;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import ucar.ma2.DataType;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.Attribute;
import ucar.nc2.Dimension;
import ucar.nc2.Variable;
import ucar.nc2.write.NetcdfFormatWriter;

public class MultiFloatMergeService {

  // https://docs.unidata.ucar.edu/netcdf-java/5.4/userguide/writing_netcdf.html


  public void mergeFloats(Path outputFile, List<CoreProfile> coreProfiles) throws IOException {

    MergeProfile mergeProfile = new MergeProfile();
    mergeProfile.setProfileNcFiles(coreProfiles);
    mergeProfile.setDimension();

    NetcdfFormatWriter.Builder builder = NetcdfFormatWriter.createNewNetcdf3(outputFile.toString());

    Dimension dateTimeDim = builder.addDimension("DATE_TIME", ProfileNcConsts.DATE_TIME);
    Dimension string256Dim = builder.addDimension("STRING256", ProfileNcConsts.STRING256);
    Dimension string64Dim = builder.addDimension("STRING64", ProfileNcConsts.STRING64);
    Dimension string32Dim = builder.addDimension("STRING32", ProfileNcConsts.STRING32);
    Dimension string16Dim = builder.addDimension("STRING16", ProfileNcConsts.STRING16);
    Dimension string8Dim = builder.addDimension("STRING8", ProfileNcConsts.STRING8);
    Dimension string4Dim = builder.addDimension("STRING4", ProfileNcConsts.STRING4);
    Dimension string2Dim = builder.addDimension("STRING2", ProfileNcConsts.STRING2);

    Dimension nProfDim = builder.addDimension("N_PROF", mergeProfile.getnProf());
    Dimension nParamDim = builder.addDimension("N_PARAM", mergeProfile.getnParam()); // 3, Maybe 4
    Dimension nLevelsDim = builder.addDimension("N_LEVELS", mergeProfile.getnLevels());
    Dimension nHistoryDim = builder.addUnlimitedDimension("N_HISTORY");
    Dimension nCalibDim = builder.addDimension("N_CALIB", mergeProfile.getnCalib()); // 1

    builder.addVariable(ProfileNcConsts.DATA_TYPE, DataType.STRING, Arrays.asList(string16Dim))
        .addAttribute(new Attribute("long_name", "Data type"))
        .addAttribute(new Attribute("conventions", "Argo reference table 1"))
        .addAttribute(new Attribute("_FillValue", " "));

    builder.addVariable(ProfileNcConsts.FORMAT_VERSION, DataType.STRING, Arrays.asList(string4Dim))
        .addAttribute(new Attribute( "long_name", "File format version"))
        .addAttribute(new Attribute("_FillValue", " "));

    builder.addVariable(ProfileNcConsts.HANDBOOK_VERSION, DataType.STRING, Arrays.asList(string4Dim))
        .addAttribute(new Attribute( "long_name", "Data handbook version"))
        .addAttribute(new Attribute("_FillValue", " "));

    builder.addVariable(ProfileNcConsts.REFERENCE_DATE_TIME, DataType.STRING , Arrays.asList(dateTimeDim))
        .addAttribute(new Attribute( "long_name", "Date of reference for Julian days"))
        .addAttribute(new Attribute( "conventions", "YYYYMMDDHHMISS"))
        .addAttribute(new Attribute("_FillValue", " "));

    builder.addVariable(ProfileNcConsts.DATE_CREATION, DataType.STRING , Arrays.asList(dateTimeDim))
        .addAttribute(new Attribute( "long_name", "Date of file creation"))
        .addAttribute(new Attribute( "conventions", "YYYYMMDDHHMISS"))
        .addAttribute(new Attribute("_FillValue", " "));

    builder.addVariable(ProfileNcConsts.DATE_UPDATE, DataType.STRING , Arrays.asList(dateTimeDim))
        .addAttribute(new Attribute( "long_name", "Date of update of this file"))
        .addAttribute(new Attribute( "conventions", "YYYYMMDDHHMISS"))
        .addAttribute(new Attribute("_FillValue", " "));

    builder.addVariable(ProfileNcConsts.PLATFORM_NUMBER, DataType.STRING, Arrays.asList(nProfDim, string8Dim))
        .addAttribute(new Attribute("long_name", "Float unique identifier"))
        .addAttribute(new Attribute("conventions", "WMO float identifier : A9IIIII"))
        .addAttribute(new Attribute("_FillValue", " "));

    builder.addVariable(ProfileNcConsts.PROJECT_NAME, DataType.STRING, Arrays.asList(nProfDim, string64Dim))
        .addAttribute(new Attribute("long_name", "Name of the project"))
        .addAttribute(new Attribute("_FillValue", " "));

    builder.addVariable(ProfileNcConsts.PI_NAME, DataType.STRING, Arrays.asList(nProfDim, string64Dim))
        .addAttribute(new Attribute("long_name", "Name of the principal investigator"))
        .addAttribute(new Attribute("_FillValue", " "));

    builder.addVariable(ProfileNcConsts.STATION_PARAMETERS, DataType.STRING, Arrays.asList(nProfDim,nParamDim, string16Dim))
        .addAttribute(new Attribute("long_name", "List of available parameters for the station"))
        .addAttribute(new Attribute("conventions", "Argo reference table 3"))
        .addAttribute(new Attribute("_FillValue", " "));

    builder.addVariable(ProfileNcConsts.CYCLE_NUMBER, DataType.INT, Arrays.asList(nProfDim))
        .addAttribute(new Attribute("long_name", "Float cycle number"))
        .addAttribute(new Attribute("conventions", "0...N, 0 : launch cycle (if exists), 1 : first complete cycle"))
        .addAttribute(new Attribute("_FillValue", 99999));

    builder.addVariable(ProfileNcConsts.DIRECTION, DataType.STRING, Arrays.asList(nProfDim))
        .addAttribute(new Attribute("long_name", "Direction of the station profiles"))
        .addAttribute(new Attribute("conventions", "A: ascending profiles, D: descending profiles"))
        .addAttribute(new Attribute("_FillValue", " "));

    builder.addVariable(ProfileNcConsts.DATA_CENTRE, DataType.STRING, Arrays.asList(nProfDim, string2Dim))
        .addAttribute(new Attribute("long_name", "Data centre in charge of float data processing"))
        .addAttribute(new Attribute("conventions", "Argo reference table 4"))
        .addAttribute(new Attribute("_FillValue", " "));

    builder.addVariable(ProfileNcConsts.DC_REFERENCE, DataType.STRING, Arrays.asList(nProfDim, string32Dim))
        .addAttribute(new Attribute("long_name", "Station unique identifier in data centre"))
        .addAttribute(new Attribute("conventions", "Data centre convention"))
        .addAttribute(new Attribute("_FillValue", " "));

    builder.addVariable(ProfileNcConsts.DATA_STATE_INDICATOR, DataType.STRING, Arrays.asList(nProfDim, string4Dim))
        .addAttribute(new Attribute("long_name", "Degree of processing the data have passed through"))
        .addAttribute(new Attribute("conventions", "Argo reference table 6"))
        .addAttribute(new Attribute("_FillValue", " "));

    builder.addVariable(ProfileNcConsts.DATA_MODE, DataType.STRING, Arrays.asList(nProfDim))
        .addAttribute(new Attribute("long_name", "Delayed mode or real time data"))
        .addAttribute(new Attribute("conventions", "R : real time; D : delayed mode; A : real time with adjustment"))
        .addAttribute(new Attribute("_FillValue", " "));

    builder.addVariable(ProfileNcConsts.PLATFORM_TYPE, DataType.STRING, Arrays.asList(nProfDim, string32Dim))
        .addAttribute(new Attribute("long_name", "Type of float"))
        .addAttribute(new Attribute("conventions", "Argo reference table 23"))
        .addAttribute(new Attribute("_FillValue", " "));

    builder.addVariable(ProfileNcConsts.FLOAT_SERIAL_NO, DataType.STRING, Arrays.asList(nProfDim, string32Dim))
        .addAttribute(new Attribute("long_name", "Serial number of the float"))
        .addAttribute(new Attribute("_FillValue", " "));

    builder.addVariable(ProfileNcConsts.FIRMWARE_VERSION, DataType.STRING, Arrays.asList(nProfDim, string32Dim))
        .addAttribute(new Attribute("long_name", "Instrument firmware version"))
        .addAttribute(new Attribute("_FillValue", " "));

    builder.addVariable(ProfileNcConsts.WMO_INST_TYPE, DataType.STRING, Arrays.asList(nProfDim, string4Dim))
        .addAttribute(new Attribute("long_name", "Coded instrument type"))
        .addAttribute(new Attribute("conventions", "Argo reference table 8"))
        .addAttribute(new Attribute("_FillValue", " "));

    builder.addVariable(ProfileNcConsts.JULD, DataType.DOUBLE, Arrays.asList(nProfDim))
        .addAttribute(new Attribute("long_name", "Julian day (UTC) of the station relative to REFERENCE_DATE_TIME"))
        .addAttribute(new Attribute("standard_name", "time"))
        .addAttribute(new Attribute("units", "days since 1950-01-01 00:00:00 UTC"))
        .addAttribute(new Attribute("conventions", "Relative julian days with decimal part (as parts of day)"))
        .addAttribute(new Attribute("resolution", 0.0))
        .addAttribute(new Attribute("_FillValue", 999999.0))
        .addAttribute(new Attribute("axis", "T"));

    builder.addVariable(ProfileNcConsts.JULD_QC, DataType.STRING, Arrays.asList(nProfDim))
        .addAttribute(new Attribute("long_name", "Quality on date and time"))
        .addAttribute(new Attribute("conventions", "Argo reference table 2"))
        .addAttribute(new Attribute("_FillValue", " "));

    builder.addVariable(ProfileNcConsts.JULD_LOCATION, DataType.DOUBLE, Arrays.asList(nProfDim))
        .addAttribute(new Attribute("long_name", "Julian day (UTC) of the location relative to REFERENCE_DATE_TIME"))
        .addAttribute(new Attribute("units", "days since 1950-01-01 00:00:00 UTC"))
        .addAttribute(new Attribute("conventions", "Relative julian days with decimal part (as parts of day)"))
        .addAttribute(new Attribute("resolution", 0.0))
        .addAttribute(new Attribute("_FillValue", 999999.0));

    builder.addVariable(ProfileNcConsts.LATITUDE, DataType.DOUBLE, Arrays.asList(nProfDim))
        .addAttribute(new Attribute("long_name", "Latitude of the station, best estimate"))
        .addAttribute(new Attribute("standard_name", "latitude"))
        .addAttribute(new Attribute("units", "degree_north"))
        .addAttribute(new Attribute("_FillValue", 99999.0))
        .addAttribute(new Attribute("valid_min", -90.0))
        .addAttribute(new Attribute("valid_max", 90.0))
        .addAttribute(new Attribute("axis", "Y"));

    builder.addVariable(ProfileNcConsts.LONGITUDE, DataType.DOUBLE, Arrays.asList(nProfDim))
        .addAttribute(new Attribute("long_name", "Longitude of the station, best estimate"))
        .addAttribute(new Attribute("standard_name", "longitude"))
        .addAttribute(new Attribute("units", "degree_east"))
        .addAttribute(new Attribute("_FillValue", 99999.0))
        .addAttribute(new Attribute("valid_min", -180.0))
        .addAttribute(new Attribute("valid_max", 180.0))
        .addAttribute(new Attribute("axis", "X"));

    builder.addVariable(ProfileNcConsts.POSITION_QC, DataType.STRING, Arrays.asList(nProfDim))
        .addAttribute(new Attribute("long_name", "Quality on position (latitude and longitude)"))
        .addAttribute(new Attribute("conventions", "Argo reference table 2"))
        .addAttribute(new Attribute("_FillValue", " "));

    builder.addVariable(ProfileNcConsts.POSITIONING_SYSTEM, DataType.STRING, Arrays.asList(nProfDim, string8Dim))
        .addAttribute(new Attribute("long_name", "Positioning system"))
        .addAttribute(new Attribute("_FillValue", " "));

    builder.addVariable(ProfileNcConsts.PROFILE_PRES_QC, DataType.STRING, Arrays.asList(nProfDim))
        .addAttribute(new Attribute("long_name", "Global quality flag of PRES mergeProfile"))
        .addAttribute(new Attribute("conventions", "Argo reference table 2a"))
        .addAttribute(new Attribute("_FillValue", " "));

    builder.addVariable(ProfileNcConsts.PROFILE_TEMP_QC, DataType.STRING, Arrays.asList(nProfDim))
        .addAttribute(new Attribute("long_name", "Global quality flag of TEMP mergeProfile"))
        .addAttribute(new Attribute("conventions", "Argo reference table 2a"))
        .addAttribute(new Attribute("_FillValue", " "));

    builder.addVariable(ProfileNcConsts.PROFILE_PSAL_QC, DataType.STRING, Arrays.asList(nProfDim))
        .addAttribute(new Attribute("long_name", "Global quality flag of PSAL mergeProfile"))
        .addAttribute(new Attribute("conventions", "Argo reference table 2a"))
        .addAttribute(new Attribute("_FillValue", " "));

    builder.addVariable(ProfileNcConsts.VERTICAL_SAMPLING_SCHEME, DataType.STRING, Arrays.asList(nProfDim, string256Dim))
        .addAttribute(new Attribute("long_name", "Vertical sampling scheme"))
        .addAttribute(new Attribute("conventions", "Argo reference table 16"))
        .addAttribute(new Attribute("_FillValue", " "));

    builder.addVariable(ProfileNcConsts.CONFIG_MISSION_NUMBER, DataType.INT, Arrays.asList(nProfDim))
        .addAttribute(new Attribute("long_name", "Unique number denoting the missions performed by the float"))
        .addAttribute(new Attribute("conventions", "1...N, 1 : first complete mission"))
        .addAttribute(new Attribute("_FillValue", 99999));

    builder.addVariable(ProfileNcConsts.PRES, DataType.FLOAT, Arrays.asList(nProfDim, nLevelsDim))
        .addAttribute(new Attribute("long_name", "Sea water pressure, equals 0 at sea-level"))
        .addAttribute(new Attribute("standard_name", "sea_water_pressure"))
        .addAttribute(new Attribute("units", "decibar"))
        .addAttribute(new Attribute("_FillValue", ProfileNcConsts.FILL_VALUE_FLOAT))
        .addAttribute(new Attribute("valid_min", 0.0f))
        .addAttribute(new Attribute("valid_max", 12000.0f))
        .addAttribute(new Attribute("C_format", "%7.1f"))
        .addAttribute(new Attribute("FORTRAN_format", "F7.1"))
        .addAttribute(new Attribute("resolution",1.0f))
        .addAttribute(new Attribute("axis", "Z"));

    builder.addVariable(ProfileNcConsts.PRES_QC, DataType.STRING, Arrays.asList(nProfDim, nLevelsDim))
        .addAttribute(new Attribute("long_name", "quality flag"))
        .addAttribute(new Attribute("conventions", "Argo reference table 2"))
        .addAttribute(new Attribute("_FillValue", " "));

    builder.addVariable(ProfileNcConsts.PRES_ADJUSTED, DataType.FLOAT, Arrays.asList(nProfDim, nLevelsDim))
        .addAttribute(new Attribute("long_name", "Sea water pressure, equals 0 at sea-level"))
        .addAttribute(new Attribute("standard_name", "sea_water_pressure"))
        .addAttribute(new Attribute("units", "decibar"))
        .addAttribute(new Attribute("_FillValue", ProfileNcConsts.FILL_VALUE_FLOAT))
        .addAttribute(new Attribute("valid_min", 0.0f))
        .addAttribute(new Attribute("valid_max", 12000.0f))
        .addAttribute(new Attribute("C_format", "%7.1f"))
        .addAttribute(new Attribute("FORTRAN_format", "F7.1"))
        .addAttribute(new Attribute("resolution",1.0f))
        .addAttribute(new Attribute("axis", "Z"));

    builder.addVariable(ProfileNcConsts.PRES_ADJUSTED_QC, DataType.STRING, Arrays.asList(nProfDim, nLevelsDim))
        .addAttribute(new Attribute("long_name", "quality flag"))
        .addAttribute(new Attribute("conventions", "Argo reference table 2"))
        .addAttribute(new Attribute("_FillValue", " "));

    builder.addVariable(ProfileNcConsts.PRES_ADJUSTED_ERROR, DataType.FLOAT, Arrays.asList(nProfDim, nLevelsDim))
        .addAttribute(new Attribute("long_name", "Contains the error on the adjusted values as determined by the delayed mode QC process"))
        .addAttribute(new Attribute("units", "decibar"))
        .addAttribute(new Attribute("_FillValue", ProfileNcConsts.FILL_VALUE_FLOAT))
        .addAttribute(new Attribute("C_format", "%7.1f"))
        .addAttribute(new Attribute("FORTRAN_format", "F7.1"))
        .addAttribute(new Attribute("resolution",1.0f));

    builder.addVariable(ProfileNcConsts.TEMP, DataType.FLOAT, Arrays.asList(nProfDim, nLevelsDim))
        .addAttribute(new Attribute("long_name", "Sea temperature in-situ ITS-90 scale"))
        .addAttribute(new Attribute("standard_name", "sea_water_temperature"))
        .addAttribute(new Attribute("units", "degree_Celsius"))
        .addAttribute(new Attribute("_FillValue", ProfileNcConsts.FILL_VALUE_FLOAT))
        .addAttribute(new Attribute("valid_min", -2.5f))
        .addAttribute(new Attribute("valid_max", 40))
        .addAttribute(new Attribute("C_format", "%9.3f"))
        .addAttribute(new Attribute("FORTRAN_format", "F9.3"))
        .addAttribute(new Attribute("resolution",0.001f));

    builder.addVariable(ProfileNcConsts.TEMP_QC, DataType.STRING, Arrays.asList(nProfDim, nLevelsDim))
        .addAttribute(new Attribute("long_name", "quality flag"))
        .addAttribute(new Attribute("conventions", "Argo reference table 2"))
        .addAttribute(new Attribute("_FillValue", " "));

    builder.addVariable(ProfileNcConsts.TEMP_ADJUSTED, DataType.FLOAT, Arrays.asList(nProfDim, nLevelsDim))
        .addAttribute(new Attribute("long_name", "Sea temperature in-situ ITS-90 scale"))
        .addAttribute(new Attribute("standard_name", "sea_water_temperature"))
        .addAttribute(new Attribute("units", "degree_Celsius"))
        .addAttribute(new Attribute("_FillValue", ProfileNcConsts.FILL_VALUE_FLOAT))
        .addAttribute(new Attribute("valid_min", -2.5f))
        .addAttribute(new Attribute("valid_max", 40))
        .addAttribute(new Attribute("C_format", "%9.3f"))
        .addAttribute(new Attribute("FORTRAN_format", "F9.3"))
        .addAttribute(new Attribute("resolution",0.001f));

    builder.addVariable(ProfileNcConsts.TEMP_ADJUSTED_QC, DataType.STRING, Arrays.asList(nProfDim, nLevelsDim))
        .addAttribute(new Attribute("long_name", "quality flag"))
        .addAttribute(new Attribute("conventions", "Argo reference table 2"))
        .addAttribute(new Attribute("_FillValue", " "));

    builder.addVariable(ProfileNcConsts.TEMP_ADJUSTED_ERROR, DataType.FLOAT, Arrays.asList(nProfDim, nLevelsDim))
        .addAttribute(new Attribute("long_name", "SContains the error on the adjusted values as determined by the delayed mode QC process"))
        .addAttribute(new Attribute("units", "degree_Celsius"))
        .addAttribute(new Attribute("_FillValue", ProfileNcConsts.FILL_VALUE_FLOAT))
        .addAttribute(new Attribute("C_format", "%9.3f"))
        .addAttribute(new Attribute("FORTRAN_format", "F9.3"))
        .addAttribute(new Attribute("resolution",0.001f));

    builder.addVariable(ProfileNcConsts.PSAL, DataType.FLOAT, Arrays.asList(nProfDim, nLevelsDim))
        .addAttribute(new Attribute("long_name", "Practical salinity"))
        .addAttribute(new Attribute("standard_name", "sea_water_salinity"))
        .addAttribute(new Attribute("units", "psu"))
        .addAttribute(new Attribute("_FillValue", ProfileNcConsts.FILL_VALUE_FLOAT))
        .addAttribute(new Attribute("valid_min", 2.0f))
        .addAttribute(new Attribute("valid_max", 41.0f))
        .addAttribute(new Attribute("C_format", "%9.3f"))
        .addAttribute(new Attribute("FORTRAN_format", "F9.3"))
        .addAttribute(new Attribute("resolution",0.001f));

    builder.addVariable(ProfileNcConsts.PSAL_QC, DataType.STRING, Arrays.asList(nProfDim, nLevelsDim))
        .addAttribute(new Attribute("long_name", "quality flag"))
        .addAttribute(new Attribute("conventions", "Argo reference table 2"))
        .addAttribute(new Attribute("_FillValue", " "));

    builder.addVariable(ProfileNcConsts.PSAL_ADJUSTED, DataType.FLOAT, Arrays.asList(nProfDim, nLevelsDim))
        .addAttribute(new Attribute("long_name", "Practical salinity"))
        .addAttribute(new Attribute("standard_name", "sea_water_salinity"))
        .addAttribute(new Attribute("units", "psu"))
        .addAttribute(new Attribute("_FillValue", ProfileNcConsts.FILL_VALUE_FLOAT))
        .addAttribute(new Attribute("valid_min", 2.0f))
        .addAttribute(new Attribute("valid_max", 41.0f))
        .addAttribute(new Attribute("C_format", "%9.3f"))
        .addAttribute(new Attribute("FORTRAN_format", "F9.3"))
        .addAttribute(new Attribute("resolution",0.001f));

    builder.addVariable(ProfileNcConsts.PSAL_ADJUSTED_QC, DataType.STRING, Arrays.asList(nProfDim, nLevelsDim))
        .addAttribute(new Attribute("long_name", "quality flag"))
        .addAttribute(new Attribute("conventions", "Argo reference table 2"))
        .addAttribute(new Attribute("_FillValue", " "));

    builder.addVariable(ProfileNcConsts.PSAL_ADJUSTED_ERROR, DataType.FLOAT, Arrays.asList(nProfDim, nLevelsDim))
        .addAttribute(new Attribute("long_name", "Contains the error on the adjusted values as determined by the delayed mode QC process"))
        .addAttribute(new Attribute("units", "psu"))
        .addAttribute(new Attribute("_FillValue", ProfileNcConsts.FILL_VALUE_FLOAT))
        .addAttribute(new Attribute("C_format", "%9.3f"))
        .addAttribute(new Attribute("FORTRAN_format", "F9.3"))
        .addAttribute(new Attribute("resolution",0.001f));

    builder.addVariable(ProfileNcConsts.PARAMETER, DataType.STRING, Arrays.asList(nProfDim, nCalibDim, nParamDim, string16Dim))
        .addAttribute(new Attribute("long_name", "List of parameters with calibration information"))
        .addAttribute(new Attribute("conventions", "Argo reference table 3"))
        .addAttribute(new Attribute("_FillValue", " "));

    builder.addVariable(ProfileNcConsts.SCIENTIFIC_CALIB_EQUATION, DataType.STRING, Arrays.asList(nProfDim, nCalibDim, nParamDim, string256Dim))
        .addAttribute(new Attribute("long_name", "Calibration equation for this parameter"))
        .addAttribute(new Attribute("_FillValue", " "));

    builder.addVariable(ProfileNcConsts.SCIENTIFIC_CALIB_COEFFICIENT, DataType.STRING, Arrays.asList(nProfDim, nCalibDim, nParamDim, string256Dim))
        .addAttribute(new Attribute("long_name", "Calibration coefficients for this parameter"))
        .addAttribute(new Attribute("_FillValue", " "));

    builder.addVariable(ProfileNcConsts.SCIENTIFIC_CALIB_COMMENT, DataType.STRING, Arrays.asList(nProfDim, nCalibDim, nParamDim, string256Dim))
        .addAttribute(new Attribute("long_name", "Comment applying to this parameter calibration"))
        .addAttribute(new Attribute("_FillValue", " "));

    builder.addVariable(ProfileNcConsts.SCIENTIFIC_CALIB_DATE, DataType.STRING, Arrays.asList(nProfDim, nCalibDim, nParamDim, dateTimeDim))
        .addAttribute(new Attribute("long_name", "Date of calibration"))
        .addAttribute(new Attribute("conventions", "YYYYMMDDHHMISS"))
        .addAttribute(new Attribute("_FillValue", " "));

    Variable.Builder VarHistoryInstitution = builder.addVariable(ProfileNcConsts.HISTORY_INSTITUTION, DataType.STRING, Arrays.asList(nHistoryDim, nProfDim, string4Dim))
        .addAttribute(new Attribute("long_name", "Institution which performed action"))
        .addAttribute(new Attribute("conventions", "Argo reference table 4"))
        .addAttribute(new Attribute("_FillValue", " "));

    Variable.Builder VarHistoryStep = builder.addVariable(ProfileNcConsts.HISTORY_STEP, DataType.STRING, Arrays.asList(nHistoryDim, nProfDim, string4Dim))
        .addAttribute(new Attribute("long_name", "Step in data processing"))
        .addAttribute(new Attribute("conventions", "Argo reference table 12"))
        .addAttribute(new Attribute("_FillValue", " "));

    Variable.Builder VarHistorySoftware = builder.addVariable(ProfileNcConsts.HISTORY_SOFTWARE, DataType.STRING, Arrays.asList(nHistoryDim, nProfDim, string4Dim))
        .addAttribute(new Attribute("long_name", "Name of software which performed action"))
        .addAttribute(new Attribute("conventions", "Institution dependent"))
        .addAttribute(new Attribute("_FillValue", " "));

    Variable.Builder VarHistorySoftwareRelease = builder.addVariable(ProfileNcConsts.HISTORY_SOFTWARE_RELEASE, DataType.STRING, Arrays.asList(nHistoryDim, nProfDim, string4Dim))
        .addAttribute(new Attribute("long_name", "Version/release of software which performed action"))
        .addAttribute(new Attribute("conventions", "Institution dependent"))
        .addAttribute(new Attribute("_FillValue", " "));

    Variable.Builder VarHistoryReference = builder.addVariable(ProfileNcConsts.HISTORY_REFERENCE, DataType.STRING, Arrays.asList(nHistoryDim, nProfDim, string64Dim))
        .addAttribute(new Attribute("long_name", "Reference of database"))
        .addAttribute(new Attribute("conventions", "Institution dependent"))
        .addAttribute(new Attribute("_FillValue", " "));

    Variable.Builder VarHistoryDate = builder.addVariable(ProfileNcConsts.HISTORY_DATE, DataType.STRING, Arrays.asList(nHistoryDim, nProfDim, dateTimeDim))
        .addAttribute(new Attribute("long_name", "history record was created"))
        .addAttribute(new Attribute("conventions", "YYYYMMDDHHMISS"))
        .addAttribute(new Attribute("_FillValue", " "));

    Variable.Builder VarHistoryAction = builder.addVariable(ProfileNcConsts.HISTORY_ACTION, DataType.STRING, Arrays.asList(nHistoryDim, nProfDim, string4Dim))
        .addAttribute(new Attribute("long_name", "Action performed on data"))
        .addAttribute(new Attribute("conventions", "Argo reference table 7"))
        .addAttribute(new Attribute("_FillValue", " "));

    Variable.Builder VarHistoryParameter = builder.addVariable(ProfileNcConsts.HISTORY_PARAMETER, DataType.STRING, Arrays.asList(nHistoryDim, nProfDim, string4Dim))
        .addAttribute(new Attribute("long_name", "Station parameter action is performed on"))
        .addAttribute(new Attribute("conventions", "Argo reference table 3"))
        .addAttribute(new Attribute("_FillValue", " "));

    Variable.Builder VarHistoryStartPres = builder.addVariable(ProfileNcConsts.HISTORY_START_PRES, DataType.FLOAT, Arrays.asList(nHistoryDim, nProfDim))
        .addAttribute(new Attribute("long_name", "Start pressure action applied on"))
        .addAttribute(new Attribute("units", "decibar"))
        .addAttribute(new Attribute("_FillValue", ProfileNcConsts.FILL_VALUE_FLOAT));

    Variable.Builder VarHistoryStopPres = builder.addVariable(ProfileNcConsts.HISTORY_STOP_PRES, DataType.FLOAT, Arrays.asList(nHistoryDim, nProfDim))
        .addAttribute(new Attribute("long_name", "Stop pressure action applied on"))
        .addAttribute(new Attribute("units", "decibar"))
        .addAttribute(new Attribute("_FillValue", ProfileNcConsts.FILL_VALUE_FLOAT));

    Variable.Builder VarHistoryPreviousValue = builder.addVariable(ProfileNcConsts.HISTORY_PREVIOUS_VALUE, DataType.FLOAT, Arrays.asList(nHistoryDim, nProfDim))
        .addAttribute(new Attribute("long_name", "Parameter/Flag previous value before action"))
        .addAttribute(new Attribute("_FillValue", ProfileNcConsts.FILL_VALUE_FLOAT));

    Variable.Builder VarHistoryQcTest = builder.addVariable(ProfileNcConsts.HISTORY_QCTEST, DataType.STRING, Arrays.asList(nHistoryDim, nProfDim, string16Dim))
        .addAttribute(new Attribute("long_name", "Documentation of tests performed, tests failed (in hex form)"))
        .addAttribute(new Attribute("conventions", "Write tests performed when ACTION=QCP$; tests failed when ACTION=QCF$"))
        .addAttribute(new Attribute("_FillValue", " "));

    // global attributes:
    builder.addAttribute(new Attribute("title", "Argo float vertical mergeProfile"));
    builder.addAttribute(new Attribute("institution", "FR GDAC"));
    builder.addAttribute(new Attribute("source", "Argo float"));
    builder.addAttribute(new Attribute("history", "2023-09-05T09:51:37Z creation"));
    builder.addAttribute(new Attribute("references", "http://www.argodatamgt.org/Documentation"));
    builder.addAttribute(new Attribute("user_manual_version", "3.1"));
    builder.addAttribute(new Attribute("Conventions", "Argo-3.1 CF-1.6"));
    builder.addAttribute(new Attribute("featureType", "trajectoryProfile"));

    builder.setFill(true);
    try (NetcdfFormatWriter writer = builder.build()) {

      mergeProfile.setVariables();
      try{
        writer.write(writer.findVariable(ProfileNcConsts.DATA_TYPE), mergeProfile.getDataType());
      } catch (InvalidRangeException e) {
        throw new RuntimeException("An error writing "+ ProfileNcConsts.DATA_TYPE + " to " + outputFile, e);
      }
      try{
        writer.write(writer.findVariable(ProfileNcConsts.FORMAT_VERSION), mergeProfile.getFormatVersion());
      } catch (InvalidRangeException e) {
        throw new RuntimeException("An error writing "+ ProfileNcConsts.FORMAT_VERSION + " to " + outputFile, e);
      }
      try{
        writer.write(writer.findVariable(ProfileNcConsts.HANDBOOK_VERSION), mergeProfile.getHandBookVersion());
      } catch (InvalidRangeException e) {
        throw new RuntimeException("An error writing "+ ProfileNcConsts.HANDBOOK_VERSION + " to " + outputFile, e);
      }
      try{
        writer.write(writer.findVariable(ProfileNcConsts.REFERENCE_DATE_TIME), mergeProfile.getReferenceDateTime());
      } catch (InvalidRangeException e) {
        throw new RuntimeException("An error writing "+ ProfileNcConsts.REFERENCE_DATE_TIME + " to " + outputFile, e);
      }
      try{
        writer.write(writer.findVariable(ProfileNcConsts.DATE_CREATION), mergeProfile.getCurrentDateTime());
      } catch (InvalidRangeException e) {
        throw new RuntimeException("An error writing "+ ProfileNcConsts.DATE_CREATION + " to " + outputFile, e);
      }
      try{
        writer.write(writer.findVariable(ProfileNcConsts.DATE_UPDATE), mergeProfile.getCurrentDateTime());
      } catch (InvalidRangeException e) {
        throw new RuntimeException("An error writing "+ ProfileNcConsts.DATE_UPDATE + " to " + outputFile, e);
      }

      try{
        writer.write(writer.findVariable(ProfileNcConsts.PLATFORM_NUMBER), mergeProfile.getPlatformNumbers());
      } catch (InvalidRangeException e) {
        throw new RuntimeException("An error writing "+ ProfileNcConsts.PLATFORM_NUMBER + " to " + outputFile, e);
      }
      try {
        writer.write(writer.findVariable(ProfileNcConsts.PROJECT_NAME), mergeProfile.getProjectNames());
      } catch (InvalidRangeException e) {
        throw new RuntimeException("An error writing "+ ProfileNcConsts.PROJECT_NAME + " to " + outputFile,e);
      }
      try {
        writer.write(writer.findVariable(ProfileNcConsts.PI_NAME), mergeProfile.getPiNames());
      } catch (InvalidRangeException e) {
        throw new RuntimeException("An error writing "+ ProfileNcConsts.PI_NAME + " to " + outputFile,e);
      }
      try {
        writer.write(writer.findVariable(ProfileNcConsts.CYCLE_NUMBER), mergeProfile.getCycleNumbers());
      } catch (InvalidRangeException e) {
        throw new RuntimeException("An error writing "+ ProfileNcConsts.CYCLE_NUMBER + " to " + outputFile,e);
      }
      try {
        writer.write(writer.findVariable(ProfileNcConsts.STATION_PARAMETERS), mergeProfile.getStationParameters());
      } catch (InvalidRangeException e) {
        throw new RuntimeException("An error writing "+ ProfileNcConsts.STATION_PARAMETERS + " to " + outputFile,e);
      }
      try {
        writer.write(writer.findVariable(ProfileNcConsts.DIRECTION), mergeProfile.getDirections());
      } catch (InvalidRangeException e) {
        throw new RuntimeException("An error writing "+ ProfileNcConsts.DIRECTION + " to " + outputFile,e);
      }
      try {
        writer.write(writer.findVariable(ProfileNcConsts.DATA_CENTRE), mergeProfile.getDataCenters());
      } catch (InvalidRangeException e) {
        throw new RuntimeException("An error writing "+ ProfileNcConsts.DATA_CENTRE + " to " + outputFile,e);
      }
      try {
        writer.write(writer.findVariable(ProfileNcConsts.DC_REFERENCE), mergeProfile.getDcReferences());
      } catch (InvalidRangeException e) {
        throw new RuntimeException("An error writing "+ ProfileNcConsts.DC_REFERENCE + " to " + outputFile,e);
      }
      try {
        writer.write(writer.findVariable(ProfileNcConsts.DATA_STATE_INDICATOR), mergeProfile.getDataStateIndicators());
      } catch (InvalidRangeException e) {
        throw new RuntimeException("An error writing "+ ProfileNcConsts.DATA_STATE_INDICATOR + " to " + outputFile,e);
      }
      try {
        writer.write(writer.findVariable(ProfileNcConsts.DATA_MODE), mergeProfile.getDataModes());
      } catch (InvalidRangeException e) {
        throw new RuntimeException("An error writing "+ ProfileNcConsts.DATA_MODE + " to " + outputFile,e);
      }
      try {
        writer.write(writer.findVariable(ProfileNcConsts.PLATFORM_TYPE), mergeProfile.getPlatformTypes());
      } catch (InvalidRangeException e) {
        throw new RuntimeException("An error writing "+ ProfileNcConsts.PLATFORM_TYPE + " to " + outputFile,e);
      }
      try {
        writer.write(writer.findVariable(ProfileNcConsts.FLOAT_SERIAL_NO), mergeProfile.getFloatSerialNos());
      } catch (InvalidRangeException e) {
        throw new RuntimeException("An error writing "+ ProfileNcConsts.FLOAT_SERIAL_NO + " to " + outputFile,e);
      }
      try {
        writer.write(writer.findVariable(ProfileNcConsts.FIRMWARE_VERSION), mergeProfile.getFirmWareVersions());
      } catch (InvalidRangeException e) {
        throw new RuntimeException("An error writing "+ ProfileNcConsts.FIRMWARE_VERSION + " to " + outputFile,e);
      }
      try {
        writer.write(writer.findVariable(ProfileNcConsts.WMO_INST_TYPE), mergeProfile.getWmoInstTypes());
      } catch (InvalidRangeException e) {
        throw new RuntimeException("An error writing "+ ProfileNcConsts.WMO_INST_TYPE + " to " + outputFile,e);
      }
      try {
        writer.write(writer.findVariable(ProfileNcConsts.JULD), mergeProfile.getJulds());
      } catch (InvalidRangeException e) {
        throw new RuntimeException("An error writing "+ ProfileNcConsts.JULD + " to " + outputFile,e);
      }
      try {
        writer.write(writer.findVariable(ProfileNcConsts.JULD_QC), mergeProfile.getJuldQc());
      } catch (InvalidRangeException e) {
        throw new RuntimeException("An error writing "+ ProfileNcConsts.JULD_QC + " to " + outputFile,e);
      }
      try {
        writer.write(writer.findVariable(ProfileNcConsts.JULD_LOCATION), mergeProfile.getJuldLocations());
      } catch (InvalidRangeException e) {
        throw new RuntimeException("An error writing "+ ProfileNcConsts.JULD_LOCATION + " to " + outputFile,e);
      }
      try {
        writer.write(writer.findVariable(ProfileNcConsts.LATITUDE), mergeProfile.getLatitudes());
      } catch (InvalidRangeException e) {
        throw new RuntimeException("An error writing "+ ProfileNcConsts.LATITUDE + " to " + outputFile,e);
      }
      try {
        writer.write(writer.findVariable(ProfileNcConsts.LONGITUDE), mergeProfile.getLongitude());
      } catch (InvalidRangeException e) {
        throw new RuntimeException("An error writing "+ ProfileNcConsts.LONGITUDE + " to " + outputFile,e);
      }
      try {
        writer.write(writer.findVariable(ProfileNcConsts.POSITION_QC), mergeProfile.getPositionQcs());
      } catch (InvalidRangeException e) {
        throw new RuntimeException("An error writing "+ ProfileNcConsts.POSITION_QC + " to " + outputFile,e);
      }
      try {
        writer.write(writer.findVariable(ProfileNcConsts.POSITIONING_SYSTEM), mergeProfile.getPositioningSystems());
      } catch (InvalidRangeException e) {
        throw new RuntimeException("An error writing "+ ProfileNcConsts.POSITIONING_SYSTEM + " to " + outputFile,e);
      }
      try {
        writer.write(writer.findVariable(ProfileNcConsts.PROFILE_PRES_QC), mergeProfile.getProfilePresQcs());
      } catch (InvalidRangeException e) {
        throw new RuntimeException("An error writing "+ ProfileNcConsts.PROFILE_PRES_QC + " to " + outputFile,e);
      }
      try {
        writer.write(writer.findVariable(ProfileNcConsts.PROFILE_TEMP_QC), mergeProfile.getProfileTempQcs());
      } catch (InvalidRangeException e) {
        throw new RuntimeException("An error writing "+ ProfileNcConsts.PROFILE_TEMP_QC + " to " + outputFile,e);
      }
      try {
        writer.write(writer.findVariable(ProfileNcConsts.PROFILE_PSAL_QC), mergeProfile.getProfilePsalQcs());
      } catch (InvalidRangeException e) {
        throw new RuntimeException("An error writing "+ ProfileNcConsts.PROFILE_PSAL_QC + " to " + outputFile,e);
      }
      try {
        writer.write(writer.findVariable(ProfileNcConsts.VERTICAL_SAMPLING_SCHEME), mergeProfile.getVerticalSamplingSchemes());
      } catch (InvalidRangeException e) {
        throw new RuntimeException("An error writing "+ ProfileNcConsts.VERTICAL_SAMPLING_SCHEME + " to " + outputFile,e);
      }
      try {
        writer.write(writer.findVariable(ProfileNcConsts.CONFIG_MISSION_NUMBER), mergeProfile.getConfigMissionNumbers());
      } catch (InvalidRangeException e) {
        throw new RuntimeException("An error writing "+ ProfileNcConsts.CONFIG_MISSION_NUMBER + " to " + outputFile,e);
      }
      try {
        writer.write(writer.findVariable(ProfileNcConsts.PRES), mergeProfile.getPress());
      } catch (InvalidRangeException e) {
        throw new RuntimeException("An error writing "+ ProfileNcConsts.PRES + " to " + outputFile,e);
      }
      
      try{
        writer.write(writer.findVariable(ProfileNcConsts.PRES), mergeProfile.getPress());
      } catch (InvalidRangeException e) {
        throw new RuntimeException("An error writing: " + ProfileNcConsts.PRES + " to " + outputFile, e);
      }
      try{
        writer.write(writer.findVariable(ProfileNcConsts.PRES_QC), mergeProfile.getPresQcs());
      } catch (InvalidRangeException e) {
        throw new RuntimeException("An error writing: " + ProfileNcConsts.PRES_QC + " to " + outputFile, e);
      }
      try{
        writer.write(writer.findVariable(ProfileNcConsts.PRES_ADJUSTED), mergeProfile.getPresAdjusteds());
      } catch (InvalidRangeException e) {
        throw new RuntimeException("An error writing: " + ProfileNcConsts.PRES_ADJUSTED + " to " + outputFile, e);
      }
      try{
        writer.write(writer.findVariable(ProfileNcConsts.PRES_ADJUSTED_QC), mergeProfile.getPresAdjustedQcs());
      } catch (InvalidRangeException e) {
        throw new RuntimeException("An error writing: " + ProfileNcConsts.PRES_ADJUSTED_QC + " to " + outputFile, e);
      }
      try{
        writer.write(writer.findVariable(ProfileNcConsts.PRES_ADJUSTED_ERROR), mergeProfile.getPresAdjustedErrors());
      } catch (InvalidRangeException e) {
        throw new RuntimeException("An error writing: " + ProfileNcConsts.PRES_ADJUSTED_ERROR + " to " + outputFile, e);
      }

      try{
        writer.write(writer.findVariable(ProfileNcConsts.TEMP), mergeProfile.getTemps());
      } catch (InvalidRangeException e) {
        throw new RuntimeException("An error writing: " + ProfileNcConsts.TEMP + " to " + outputFile, e);
      }
      try{
        writer.write(writer.findVariable(ProfileNcConsts.TEMP_QC), mergeProfile.getTempQcs());
      } catch (InvalidRangeException e) {
        throw new RuntimeException("An error writing: " + ProfileNcConsts.TEMP_QC + " to " + outputFile, e);
      }
      try{
        writer.write(writer.findVariable(ProfileNcConsts.TEMP_ADJUSTED), mergeProfile.getTempAdjusteds());
      } catch (InvalidRangeException e) {
        throw new RuntimeException("An error writing: " + ProfileNcConsts.TEMP_ADJUSTED + " to " + outputFile, e);
      }
      try{
        writer.write(writer.findVariable(ProfileNcConsts.TEMP_ADJUSTED_QC), mergeProfile.getTempAdjustedQcs());
      } catch (InvalidRangeException e) {
        throw new RuntimeException("An error writing: " + ProfileNcConsts.TEMP_ADJUSTED_QC + " to " + outputFile, e);
      }
      try{
        writer.write(writer.findVariable(ProfileNcConsts.TEMP_ADJUSTED_ERROR), mergeProfile.getTempAdjustedErrors());
      } catch (InvalidRangeException e) {
        throw new RuntimeException("An error writing: " + ProfileNcConsts.TEMP_ADJUSTED_ERROR + " to " + outputFile, e);
      }

      try{
        writer.write(writer.findVariable(ProfileNcConsts.PSAL), mergeProfile.getPsals());
      } catch (InvalidRangeException e) {
        throw new RuntimeException("An error writing: " + ProfileNcConsts.PSAL + " to " + outputFile, e);
      }
      try{
        writer.write(writer.findVariable(ProfileNcConsts.PSAL_QC), mergeProfile.getPsalQcs());
      } catch (InvalidRangeException e) {
        throw new RuntimeException("An error writing: " + ProfileNcConsts.PSAL_QC + " to " + outputFile, e);
      }
      try{
        writer.write(writer.findVariable(ProfileNcConsts.PSAL_ADJUSTED), mergeProfile.getPsalAdjusteds());
      } catch (InvalidRangeException e) {
        throw new RuntimeException("An error writing: " + ProfileNcConsts.PSAL_ADJUSTED + " to " + outputFile, e);
      }
      try{
        writer.write(writer.findVariable(ProfileNcConsts.PSAL_ADJUSTED_QC), mergeProfile.getPsalAdjustedQcs());
      } catch (InvalidRangeException e) {
        throw new RuntimeException("An error writing: " + ProfileNcConsts.PSAL_ADJUSTED_QC + " to " + outputFile, e);
      }
      try{
        writer.write(writer.findVariable(ProfileNcConsts.PSAL_ADJUSTED_ERROR), mergeProfile.getPsalAdjustedErrors());
      } catch (InvalidRangeException e) {
        throw new RuntimeException("An error writing: " + ProfileNcConsts.PSAL_ADJUSTED_ERROR + " to " + outputFile, e);
      }

      try{
        writer.write(writer.findVariable(ProfileNcConsts.PARAMETER), mergeProfile.getParameters());
      } catch (InvalidRangeException e) {
        throw new RuntimeException("An error writing: " + ProfileNcConsts.PARAMETER + " to " + outputFile, e);
      }

      try{
        writer.write(writer.findVariable(ProfileNcConsts.PARAMETER), mergeProfile.getParameters());
      } catch (InvalidRangeException e) {
        throw new RuntimeException("An error writing: " + ProfileNcConsts.PARAMETER + " to " + outputFile, e);
      }

      try{
        writer.write(writer.findVariable(ProfileNcConsts.SCIENTIFIC_CALIB_EQUATION), mergeProfile.getScientifiCalibEquations());
      } catch (InvalidRangeException e) {
        throw new RuntimeException("An error writing: " + ProfileNcConsts.SCIENTIFIC_CALIB_EQUATION + " to " + outputFile, e);
      }
      try{
        writer.write(writer.findVariable(ProfileNcConsts.SCIENTIFIC_CALIB_COEFFICIENT), mergeProfile.getScientifiCalibCoefficients());
      } catch (InvalidRangeException e) {
        throw new RuntimeException("An error writing: " + ProfileNcConsts.SCIENTIFIC_CALIB_COEFFICIENT + " to " + outputFile, e);
      }
      try{
        writer.write(writer.findVariable(ProfileNcConsts.SCIENTIFIC_CALIB_COMMENT), mergeProfile.getScientifiCalibComments());
      } catch (InvalidRangeException e) {
        throw new RuntimeException("An error writing: " + ProfileNcConsts.SCIENTIFIC_CALIB_COMMENT + " to " + outputFile, e);
      }
      try{
        writer.write(writer.findVariable(ProfileNcConsts.SCIENTIFIC_CALIB_DATE), mergeProfile.getScientifiCalibDates());
      } catch (InvalidRangeException e) {
        throw new RuntimeException("An error writing: " + ProfileNcConsts.SCIENTIFIC_CALIB_DATE + " to " + outputFile, e);
      }


    }catch (IOException e) {
      throw new RuntimeException("An error occurred creating multi float merge file : " + outputFile, e);
    }
  }

//  private ArrayInt getArrayIntD1(int value){
//    int[] shape = new int[]{1};
//    ArrayInt data = new ArrayInt(shape, false);
//    Index ima = data.getIndex();
//    data.setInt(ima.set(0), value);
//    return data;
//  }
//
//  private ArrayString getArrayStringD1(String  value){
//    int[] shape = {1, ProfileNcConsts.STRING8};
//    ArrayString data = new ArrayString.D1(shape[0]);
//    Index ima = data.getIndex();
//    data.setObject(0,value);
//    return data;
//  }

}
