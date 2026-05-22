package edu.colorado.cires.argonaut.core.util;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import ucar.ma2.DataType;
import ucar.nc2.Attribute;
import ucar.nc2.Dimension;
import ucar.nc2.write.NetcdfFormatWriter;

public final class CommonParameterAttributes {

  private static final Pattern BBP_REGEX = Pattern.compile("BBP([0-9]+)");
  private static final Pattern CP_REGEX = Pattern.compile("CP([0-9]+)");
  private static final Pattern DOWN_IRRADIANCE_REGEX = Pattern.compile("DOWN_IRRADIANCE([0-9]+)");
  private static final Pattern UP_RADIANCE_REGEX = Pattern.compile("UP_RADIANCE([0-9]+)");


  private static final ParameterAttributes<Float> PRESS_PARAM_ATTR = new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Sea water pressure, equals 0 at sea-level";
    }

    @Override
    public String getStandardName() {
      return "sea_water_pressure";
    }

    @Override
    public String getUnits() {
      return "decibar";
    }

    @Override
    public Float getValidMin() {
      return 0f;
    }

    @Override
    public Float getValidMax() {
      return 12000f;
    }

    @Override
    public String getCFormat() {
      return "%.3f";
    }

    @Override
    public String getFortranFormat() {
      return "F.3";
    }

    @Override
    public Float getResolution() {
      return 0.001f;
    }

    @Override
    public String getAxis() {
      return "Z";
    }

  };

  private static final ParameterAttributes<Float> TEMP_PARAM_ATTR = new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Sea temperature in-situ ITS-90 scale";
    }

    @Override
    public String getStandardName() {
      return "sea_water_temperature";
    }

    @Override
    public String getUnits() {
      return "degree_Celsius";
    }

    @Override
    public Float getValidMin() {
      return -2.5f;
    }

    @Override
    public Float getValidMax() {
      return 40.0f;
    }

    @Override
    public String getCFormat() {
      return "%.3f";
    }

    @Override
    public String getFortranFormat() {
      return "F.3";
    }

    @Override
    public Float getResolution() {
      return 0.001f;
    }
  };

  private static final ParameterAttributes<Float> DOXY_PARAM_ATTR = new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Dissolved oxygen";
    }

    @Override
    public String getStandardName() {
      return "moles_of_oxygen_per_unit_mass_in_sea_water";
    }

    @Override
    public String getUnits() {
      return "micromole/kg";
    }

    @Override
    public Float getValidMin() {
      return -5f;
    }

    @Override
    public Float getValidMax() {
      return 600f;
    }

    @Override
    public String getCFormat() {
      return "%.3f";
    }

    @Override
    public String getFortranFormat() {
      return "F.3";
    }

    @Override
    public Float getResolution() {
      return 0.001f;
    }

  };

  private static final ParameterAttributes<Float> PSAL_PARAM_ATTR = new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Practical salinity";
    }

    @Override
    public String getStandardName() {
      return "sea_water_salinity";
    }

    @Override
    public String getUnits() {
      return "psu";
    }

    @Override
    public Float getValidMin() {
      return 2.0f;
    }

    @Override
    public Float getValidMax() {
      return 41.0f;
    }

    @Override
    public String getCFormat() {
      return "%.4f";
    }

    @Override
    public String getFortranFormat() {
      return "F.4";
    }

    @Override
    public Float getResolution() {
      return 1.0E-4f;
    }
  };

  private static final ParameterAttributes<Float> DOWNWELLING_PAR_PARAM_ATTR = new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Downwelling photosynthetic available radiation";
    }

    @Override
    public String getStandardName() {
      return "downwelling_photosynthetic_photon_flux_in_sea_water";
    }

    @Override
    public String getUnits() {
      return "microMoleQuanta/m^2/sec";
    }

    @Override
    public String getCFormat() {
      return "%.3f";
    }

    @Override
    public String getFortranFormat() {
      return "F.3";
    }

    @Override
    public Float getResolution() {
      return 0.001f;
    }

  };

  private static final ParameterAttributes<Float> NITRATE_PARAM_ATTR = new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Nitrate";
    }

    @Override
    public String getStandardName() {
      return "moles_of_nitrate_per_unit_mass_in_sea_water";
    }

    @Override
    public String getUnits() {
      return "micromole/kg";
    }

    @Override
    public String getCFormat() {
      return "%.3f";
    }

    @Override
    public String getFortranFormat() {
      return "F.3";
    }

    @Override
    public Float getResolution() {
      return 0.001f;
    }

  };

  public static ParameterAttributes<Float> getParameterAttributes(String parameterName) {
    if ("PRES".equals(parameterName)) {
      return PRESS_PARAM_ATTR;
    }
    if ("TEMP".equals(parameterName)) {
      return TEMP_PARAM_ATTR;
    }
    if ("PSAL".equals(parameterName)) {
      return PSAL_PARAM_ATTR;
    }
    if ("DOXY".equals(parameterName)) {
      return DOXY_PARAM_ATTR;
    }
    if ("CDOM".equals(parameterName)) {
      //TODO
      return null;
    }
    if ("CHLA".equals(parameterName)) {
      //TODO
      return null;
    }
    if ("BISULFIDE".equals(parameterName)) {
      //TODO
      return null;
    }
    if ("NITRATE".equals(parameterName)) {
      return NITRATE_PARAM_ATTR;
    }
    if ("DOWNWELLING_PAR".equals(parameterName)) {
      return DOWNWELLING_PAR_PARAM_ATTR;
    }
    if ("PH_IN_SITU_TOTAL".equals(parameterName)) {
      //TODO
      return null;
    }
    if ("TURBIDITY".equals(parameterName)) {
      //TODO
      return null;
    }
    Matcher matcher = DOWN_IRRADIANCE_REGEX.matcher(parameterName);
    if (matcher.matches()) {
      String diNum = matcher.group(1);
      return new ParameterAttributes<>() {
        @Override
        public String getLongName() {
          return "Downwelling irradiance at " + diNum + " nanometers";
        }

        @Override
        public String getUnits() {
          return "W/m^2/nm";
        }

        @Override
        public String getCFormat() {
          return "%.6f";
        }

        @Override
        public String getFortranFormat() {
          return "F.6";
        }

        @Override
        public Float getResolution() {
          return 1.0E-6f;
        }
      };
    }

    matcher = BBP_REGEX.matcher(parameterName);
    if (matcher.matches()) {
      // TODO
      throw new UnsupportedOperationException("BBP regular expression not supported");
    }

    matcher = CP_REGEX.matcher(parameterName);
    if (matcher.matches()) {
      // TODO
      throw new UnsupportedOperationException("CP regular expression not supported");
    }

    matcher = UP_RADIANCE_REGEX.matcher(parameterName);
    if (matcher.matches()) {
      // TODO
      throw new UnsupportedOperationException("UP-RADIANCE regular expression not supported");
    }

    return new ParameterAttributes<>() {
      @Override
      public String getLongName() {
        return parameterName;
      }
    };
  }


  public static void addGlobalAttributes(NetcdfFormatWriter.Builder builder, String institution, String softwareVersion) {
    builder.addAttribute(new Attribute("title", "Argo float vertical profile"));
    if (institution != null) {
      builder.addAttribute(new Attribute("institution", institution));
    }
    builder.addAttribute(new Attribute("source", "Argo float"));
    builder.addAttribute(new Attribute("history", Instant.now().toString() + " creation (Argonaut " + softwareVersion + ")"));
    builder.addAttribute(new Attribute("references", "http://www.argodatamgt.org/Documentation, https://github.com/CI-CMG/argonaut"));
    builder.addAttribute(new Attribute("user_manual_version", "1.0"));
    builder.addAttribute(new Attribute("Conventions", "Argo-3.1 CF-1.6"));
    builder.addAttribute(new Attribute("featureType", "trajectoryProfile"));
    builder.addAttribute(new Attribute("software_version", softwareVersion + " (Argonaut " + softwareVersion + ")"));
    builder.addAttribute(new Attribute("id", "https://doi.org/10.17882/42182"));
  }

  public static ArgoNetCdfCreatedDimensions addCommonDimensions(NetcdfFormatWriter.Builder builder, ArgoNetCdfDimensions dimensions) {
    Dimension dateTimeDim = builder.addDimension("DATE_TIME", 14);
    Dimension string256Dim = builder.addDimension("STRING256", 256);
    Dimension string64Dim = builder.addDimension("STRING64", 64);
    Dimension string32Dim = builder.addDimension("STRING32", 32);
    Dimension string16Dim = builder.addDimension("STRING16", 16);
    Dimension string8Dim = builder.addDimension("STRING8", 8);
    Dimension string4Dim = builder.addDimension("STRING4", 4);
    Dimension string2Dim = builder.addDimension("STRING2", 2);
    Dimension nProfDim = builder.addDimension("N_PROF", dimensions.getProfiles());
    Dimension nParamDim = builder.addDimension("N_PARAM", dimensions.getParameters());
    Dimension nLevelsDim = builder.addDimension("N_LEVELS", dimensions.getLevels());
    Dimension nCalibDim = builder.addDimension("N_CALIB", dimensions.getCalibrations());
    Dimension nHistoryDim = builder.addDimension("N_HISTORY", dimensions.getHistories());
    return new ArgoNetCdfCreatedDimensions(
        dateTimeDim, string256Dim, string64Dim, string32Dim, string16Dim, string8Dim,
        string4Dim, string2Dim, nProfDim, nParamDim, nLevelsDim, nCalibDim,
        nHistoryDim
    );
  }

  public static void addCommonVariables(NetcdfFormatWriter.Builder builder, ArgoNetCdfCreatedDimensions dimensions, List<String> parameterNames) {

    Dimension dateTimeDim = dimensions.getDateTimeDim();
    Dimension string256Dim = dimensions.getString256Dim();
    Dimension string64Dim = dimensions.getString64Dim();
    Dimension string32Dim = dimensions.getString32Dim();
    Dimension string16Dim = dimensions.getString16Dim();
    Dimension string8Dim = dimensions.getString8Dim();
    Dimension string4Dim = dimensions.getString4Dim();
    Dimension string2Dim = dimensions.getString2Dim();
    Dimension nProfDim = dimensions.getnProfDim();
    Dimension nParamDim = dimensions.getnParamDim();
    Dimension nLevelsDim = dimensions.getnLevelsDim();
    Dimension nCalibDim = dimensions.getnCalibDim();
    Dimension nHistoryDim = dimensions.getnHistoryDim();


    builder.addVariable("DATA_TYPE", DataType.CHAR, Collections.singletonList(string32Dim))
        .addAttribute(new Attribute("_FillValue", " "))
        .addAttribute(new Attribute("long_name", "Data type"))
        .addAttribute(new Attribute("conventions", "Argo reference table 1"));

    builder.addVariable("FORMAT_VERSION", DataType.CHAR, Collections.singletonList(string4Dim))
        .addAttribute(new Attribute("_FillValue", " "))
        .addAttribute(new Attribute("long_name", "File format version"));

    builder.addVariable("HANDBOOK_VERSION", DataType.CHAR, Collections.singletonList(string4Dim))
        .addAttribute(new Attribute("_FillValue", " "))
        .addAttribute(new Attribute("long_name", "Data handbook version"));

    builder.addVariable("REFERENCE_DATE_TIME", DataType.CHAR, Collections.singletonList(dateTimeDim))
        .addAttribute(new Attribute("_FillValue", " "))
        .addAttribute(new Attribute("long_name", "Date of reference for Julian days"))
        .addAttribute(new Attribute("conventions", "YYYYMMDDHHMISS"));

    builder.addVariable("DATE_CREATION", DataType.CHAR, Collections.singletonList(dateTimeDim))
        .addAttribute(new Attribute("_FillValue", " "))
        .addAttribute(new Attribute("long_name", "Date of file creation"))
        .addAttribute(new Attribute("conventions", "YYYYMMDDHHMISS"));

    builder.addVariable("DATE_UPDATE", DataType.CHAR, Collections.singletonList(dateTimeDim))
        .addAttribute(new Attribute("_FillValue", " "))
        .addAttribute(new Attribute("long_name", "Date of update of this file"))
        .addAttribute(new Attribute("conventions", "YYYYMMDDHHMISS"));

    builder.addVariable("PLATFORM_NUMBER", DataType.CHAR, Arrays.asList(nProfDim, string8Dim))
        .addAttribute(new Attribute("_FillValue", " "))
        .addAttribute(new Attribute("long_name", "Float unique identifier"))
        .addAttribute(new Attribute("conventions", "WMO float identifier : A9IIIII"));

    builder.addVariable("PROJECT_NAME", DataType.CHAR, Arrays.asList(nProfDim, string64Dim))
        .addAttribute(new Attribute("_FillValue", " "))
        .addAttribute(new Attribute("long_name", "Name of the project"));

    builder.addVariable("PI_NAME", DataType.CHAR, Arrays.asList(nProfDim, string64Dim))
        .addAttribute(new Attribute("_FillValue", " "))
        .addAttribute(new Attribute("long_name", "Name of the principal investigator"));

    builder.addVariable("STATION_PARAMETERS", DataType.CHAR, Arrays.asList(nProfDim, nParamDim, string64Dim))
        .addAttribute(new Attribute("_FillValue", " "))
        .addAttribute(new Attribute("long_name", "List of available parameters for the station"))
        .addAttribute(new Attribute("conventions", "Argo reference table 3"));

    builder.addVariable("CYCLE_NUMBER", DataType.INT, Collections.singletonList(nProfDim))
        .addAttribute(new Attribute("_FillValue", 99999))
        .addAttribute(new Attribute("long_name", "Float cycle number"))
        .addAttribute(new Attribute("conventions", "0...N, 0 : launch cycle (if exists), 1 : first complete cycle"));

    builder.addVariable("DIRECTION", DataType.CHAR, Collections.singletonList(nProfDim))
        .addAttribute(new Attribute("_FillValue", " "))
        .addAttribute(new Attribute("long_name", "Direction of the station profile"))
        .addAttribute(new Attribute("conventions", "A: ascending profiles, D: descending profiles"));

    builder.addVariable("DATA_CENTRE", DataType.CHAR, Arrays.asList(nProfDim, string2Dim))
        .addAttribute(new Attribute("_FillValue", " "))
        .addAttribute(new Attribute("long_name", "Data centre in charge of float data processing"))
        .addAttribute(new Attribute("conventions", "Argo reference table 4"));

    builder.addVariable("PARAMETER_DATA_MODE", DataType.CHAR, Arrays.asList(nProfDim, nParamDim))
        .addAttribute(new Attribute("_FillValue", " "))
        .addAttribute(new Attribute("long_name", "Delayed mode or real time data"))
        .addAttribute(new Attribute("conventions", "R : real time; D : delayed mode; A : real time with adjustment"));

    builder.addVariable("PLATFORM_TYPE", DataType.CHAR, Arrays.asList(nProfDim, string32Dim))
        .addAttribute(new Attribute("_FillValue", " "))
        .addAttribute(new Attribute("long_name", "Type of float"))
        .addAttribute(new Attribute("conventions", "Argo reference table 23"));

    builder.addVariable("FLOAT_SERIAL_NO", DataType.CHAR, Arrays.asList(nProfDim, string32Dim))
        .addAttribute(new Attribute("_FillValue", " "))
        .addAttribute(new Attribute("long_name", "Serial number of the float"));

    builder.addVariable("FIRMWARE_VERSION", DataType.CHAR, Arrays.asList(nProfDim, string32Dim))
        .addAttribute(new Attribute("_FillValue", " "))
        .addAttribute(new Attribute("long_name", "Instrument firmware version"));

    builder.addVariable("WMO_INST_TYPE", DataType.CHAR, Arrays.asList(nProfDim, string4Dim))
        .addAttribute(new Attribute("_FillValue", " "))
        .addAttribute(new Attribute("long_name", "Coded instrument type"))
        .addAttribute(new Attribute("conventions", "Argo reference table 8"));

    builder.addVariable("JULD", DataType.DOUBLE, Arrays.asList(nProfDim))
        .addAttribute(new Attribute("_FillValue", 999999d))
        .addAttribute(new Attribute("long_name", "Julian day (UTC) of the station relative to REFERENCE_DATE_TIME"))
        .addAttribute(new Attribute("standard_name", "time"))
        .addAttribute(new Attribute("units", "days since 1950-01-01 00:00:00 UTC"))
        .addAttribute(new Attribute("conventions", "Relative julian days with decimal part (as parts of day)"))
        .addAttribute(new Attribute("axis", "T"))
        .addAttribute(new Attribute("resolution", 1.0E-5));

    builder.addVariable("JULD_QC", DataType.CHAR, Arrays.asList(nProfDim))
        .addAttribute(new Attribute("_FillValue", " "))
        .addAttribute(new Attribute("long_name", "Quality on date and time"))
        .addAttribute(new Attribute("conventions", "Argo reference table 2"));

    builder.addVariable("JULD_LOCATION", DataType.DOUBLE, Arrays.asList(nProfDim))
        .addAttribute(new Attribute("resolution", 1.0E-5))
        .addAttribute(new Attribute("_FillValue", 999999d))
        .addAttribute(new Attribute("long_name", "Julian day (UTC) of the location relative to REFERENCE_DATE_TIME"))
        .addAttribute(new Attribute("units", "days since 1950-01-01 00:00:00 UTC"))
        .addAttribute(new Attribute("conventions", "Relative julian days with decimal part (as parts of day)"));

    builder.addVariable("LATITUDE", DataType.DOUBLE, Arrays.asList(nProfDim))
        .addAttribute(new Attribute("_FillValue", 999999d))
        .addAttribute(new Attribute("long_name", "Latitude of the station, best estimate"))
        .addAttribute(new Attribute("standard_name", "latitude"))
        .addAttribute(new Attribute("units", "degree_north"))
        .addAttribute(new Attribute("valid_min", -90d))
        .addAttribute(new Attribute("valid_max", 90d))
        .addAttribute(new Attribute("axis", "Y"));

    builder.addVariable("LONGITUDE", DataType.DOUBLE, Arrays.asList(nProfDim))
        .addAttribute(new Attribute("_FillValue", 999999d))
        .addAttribute(new Attribute("long_name", "Longitude of the station, best estimate"))
        .addAttribute(new Attribute("standard_name", "longitude"))
        .addAttribute(new Attribute("units", "degree_north"))
        .addAttribute(new Attribute("valid_min", -180d))
        .addAttribute(new Attribute("valid_max", 180d))
        .addAttribute(new Attribute("axis", "X"));

    builder.addVariable("POSITION_QC", DataType.CHAR, Arrays.asList(nProfDim))
        .addAttribute(new Attribute("_FillValue", " "))
        .addAttribute(new Attribute("long_name", "Quality on position (latitude and longitude)"))
        .addAttribute(new Attribute("conventions", "Argo reference table 2"));

    builder.addVariable("POSITIONING_SYSTEM", DataType.CHAR, Arrays.asList(nProfDim, string8Dim))
        .addAttribute(new Attribute("_FillValue", " "))
        .addAttribute(new Attribute("long_name", "Positioning system"));

    builder.addVariable("CONFIG_MISSION_NUMBER", DataType.INT, Arrays.asList(nProfDim))
        .addAttribute(new Attribute("_FillValue", 99999))
        .addAttribute(new Attribute("long_name", "Unique number denoting the missions performed by the float"))
        .addAttribute(new Attribute("conventions", "1...N, 1 : first complete mission"));

    builder.addVariable("PARAMETER", DataType.CHAR, Arrays.asList(nProfDim, nCalibDim, nParamDim, string64Dim))
        .addAttribute(new Attribute("_FillValue", " "))
        .addAttribute(new Attribute("long_name", "List of parameters with calibration information"))
        .addAttribute(new Attribute("conventions", "Argo reference table 3"));

    builder.addVariable("SCIENTIFIC_CALIB_EQUATION", DataType.CHAR, Arrays.asList(nProfDim, nCalibDim, nParamDim, string256Dim))
        .addAttribute(new Attribute("_FillValue", " "))
        .addAttribute(new Attribute("long_name", "Calibration equation for this parameter"))
        .addAttribute(new Attribute("conventions", "Argo reference table 3"));

    builder.addVariable("SCIENTIFIC_CALIB_COEFFICIENT", DataType.CHAR, Arrays.asList(nProfDim, nCalibDim, nParamDim, string256Dim))
        .addAttribute(new Attribute("_FillValue", " "))
        .addAttribute(new Attribute("long_name", "Calibration coefficients for this equation"))
        .addAttribute(new Attribute("conventions", "Argo reference table 3"));

    builder.addVariable("SCIENTIFIC_CALIB_COMMENT", DataType.CHAR, Arrays.asList(nProfDim, nCalibDim, nParamDim, string256Dim))
        .addAttribute(new Attribute("_FillValue", " "))
        .addAttribute(new Attribute("long_name", "Comment applying to this parameter calibration"));

    builder.addVariable("SCIENTIFIC_CALIB_DATE", DataType.CHAR, Arrays.asList(nProfDim, nCalibDim, nParamDim, dateTimeDim))
        .addAttribute(new Attribute("_FillValue", " "))
        .addAttribute(new Attribute("long_name", "Date of calibration"))
        .addAttribute(new Attribute("conventions", "YYYYMMDDHHMISS"));

    builder.addVariable("HISTORY_REFERENCE", DataType.CHAR, Arrays.asList(nHistoryDim, nProfDim, string64Dim))
        .addAttribute(new Attribute("_FillValue", " "))
        .addAttribute(new Attribute("long_name", "Reference of database"))
        .addAttribute(new Attribute("conventions", "Institution dependent"));

    builder.addVariable("HISTORY_INSTITUTION", DataType.CHAR, Arrays.asList(nHistoryDim, nProfDim, string4Dim))
        .addAttribute(new Attribute("_FillValue", " "))
        .addAttribute(new Attribute("long_name", "Institution which performed action"))
        .addAttribute(new Attribute("conventions", "Argo reference table 4"));

    builder.addVariable("HISTORY_STEP", DataType.CHAR, Arrays.asList(nHistoryDim, nProfDim, string4Dim))
        .addAttribute(new Attribute("_FillValue", " "))
        .addAttribute(new Attribute("long_name", "Step in data processing"))
        .addAttribute(new Attribute("conventions", "Argo reference table 12"));

    builder.addVariable("HISTORY_SOFTWARE", DataType.CHAR, Arrays.asList(nHistoryDim, nProfDim, string4Dim))
        .addAttribute(new Attribute("_FillValue", " "))
        .addAttribute(new Attribute("long_name", "Name of software which performed action"))
        .addAttribute(new Attribute("conventions", "Institution dependent"));

    builder.addVariable("HISTORY_SOFTWARE_RELEASE", DataType.CHAR, Arrays.asList(nHistoryDim, nProfDim, string4Dim))
        .addAttribute(new Attribute("_FillValue", " "))
        .addAttribute(new Attribute("long_name", "Version/release of software which performed action"))
        .addAttribute(new Attribute("conventions", "Institution dependent"));

    builder.addVariable("HISTORY_ACTION", DataType.CHAR, Arrays.asList(nHistoryDim, nProfDim, string4Dim))
        .addAttribute(new Attribute("_FillValue", " "))
        .addAttribute(new Attribute("long_name", "Action performed on data"))
        .addAttribute(new Attribute("conventions", "Argo reference table 7"));

    builder.addVariable("HISTORY_PARAMETER", DataType.CHAR, Arrays.asList(nHistoryDim, nProfDim, string16Dim))
        .addAttribute(new Attribute("_FillValue", " "))
        .addAttribute(new Attribute("long_name", "Station parameter action is performed on"))
        .addAttribute(new Attribute("conventions", "Argo reference table 3"));

    builder.addVariable("HISTORY_QCTEST", DataType.CHAR, Arrays.asList(nHistoryDim, nProfDim, string16Dim))
        .addAttribute(new Attribute("_FillValue", " "))
        .addAttribute(new Attribute("long_name", "Documentation of tests performed, tests failed (in hex form)"))
        .addAttribute(new Attribute("conventions", "Write tests performed when ACTION=QCP$; tests failed when ACTION=QCF$"));


    builder.addVariable("HISTORY_DATE", DataType.CHAR, Arrays.asList(nHistoryDim, nProfDim, dateTimeDim))
        .addAttribute(new Attribute("_FillValue", " "))
        .addAttribute(new Attribute("long_name", "Date the history record was created"))
        .addAttribute(new Attribute("conventions", "YYYYMMDDHHMISS"));

    builder.addVariable("HISTORY_START_PRES", DataType.FLOAT, Arrays.asList(nHistoryDim, nProfDim))
        .addAttribute(new Attribute("_FillValue", 99999f))
        .addAttribute(new Attribute("long_name", "Start pressure action applied on"))
        .addAttribute(new Attribute("units", "decibar"));

    builder.addVariable("HISTORY_STOP_PRES", DataType.FLOAT, Arrays.asList(nHistoryDim, nProfDim))
        .addAttribute(new Attribute("_FillValue", 99999f))
        .addAttribute(new Attribute("long_name", "Stop pressure action applied on"))
        .addAttribute(new Attribute("units", "decibar"));

    builder.addVariable("HISTORY_PREVIOUS_VALUE", DataType.FLOAT, Arrays.asList(nHistoryDim, nProfDim))
        .addAttribute(new Attribute("_FillValue", 99999f))
        .addAttribute(new Attribute("long_name", "Parameter/Flag previous value before action"))
        .addAttribute(new Attribute("units", "decibar"));

    for (String parameterName : parameterNames) {
      builder.addVariable("PROFILE_" + parameterName + "_QC", DataType.CHAR, Arrays.asList(nProfDim))
          .addAttribute(new Attribute("long_name", "Global quality flag of " + parameterName + " profile"))
          .addAttribute(new Attribute("conventions", "Argo reference table 2a"))
          .addAttribute(new Attribute("_FillValue", " "));

      NetCdfWriteUtils.buildParameterVariable(builder, parameterName, nProfDim, nLevelsDim);

      builder.addVariable(parameterName + "_QC", DataType.CHAR, Arrays.asList(nProfDim, nLevelsDim))
          .addAttribute(new Attribute("long_name", "quality flag"))
          .addAttribute(new Attribute("conventions", "Argo reference table 2"))
          .addAttribute(new Attribute("_FillValue", " "));

      NetCdfWriteUtils.buildParameterVariable(builder, parameterName, "ADJUSTED", nProfDim, nLevelsDim);

      builder.addVariable(parameterName + "_ADJUSTED_QC", DataType.CHAR, Arrays.asList(nProfDim, nLevelsDim))
          .addAttribute(new Attribute("long_name", "quality flag"))
          .addAttribute(new Attribute("conventions", "Argo reference table 2"))
          .addAttribute(new Attribute("_FillValue", " "));

      builder.addVariable(parameterName + "_ADJUSTED_ERROR", DataType.FLOAT, Arrays.asList(nProfDim, nLevelsDim))
          .addAttribute(new Attribute("long_name", "Contains the error on the adjusted values as determined by the delayed mode QC process"))
          .addAttribute(new Attribute("_FillValue", 99999f))
          .addAttribute(new Attribute("units", "decibar"))
          .addAttribute(new Attribute("C_format", "%.3f"))
          .addAttribute(new Attribute("FORTRAN_format", "F.3"))
          .addAttribute(new Attribute("resolution", 0.001f));
    }

  }
}
