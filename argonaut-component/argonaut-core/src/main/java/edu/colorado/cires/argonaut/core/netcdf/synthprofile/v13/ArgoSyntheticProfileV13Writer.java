package edu.colorado.cires.argonaut.core.netcdf.synthprofile.v13;

import static edu.colorado.cires.argonaut.core.util.NetCdfUtils.dateToJulianDate;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import ucar.ma2.ArrayChar;
import ucar.ma2.ArrayDouble;
import ucar.ma2.ArrayFloat;
import ucar.ma2.ArrayInt;
import ucar.ma2.ArrayString;
import ucar.ma2.DataType;
import ucar.ma2.Index;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.Attribute;
import ucar.nc2.Dimension;
import ucar.nc2.Variable;
import ucar.nc2.write.NetcdfFormatWriter;

public class ArgoSyntheticProfileV13Writer {

  private interface ParameterAttributes<T> {

    String getLongName();

    default String getStandardName() {
      return null;
    }

    default String getUnits() {
      return null;
    }

    default T getValidMin() {
      return null;
    }

    default T getValidMax() {
      return null;
    }

    default String getCFormat() {
      return null;
    }

    default String getFortranFormat() {
      return null;
    }

    default T getResolution() {
      return null;
    }

    default String getAxis() {
      return null;
    }

  }

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

  private static final Pattern BBP_REGEX = Pattern.compile("BBP([0-9]+)");
  private static final Pattern CP_REGEX = Pattern.compile("CP([0-9]+)");
  private static final Pattern DOWN_IRRADIANCE_REGEX = Pattern.compile("DOWN_IRRADIANCE([0-9]+)");
  private static final Pattern UP_RADIANCE_REGEX = Pattern.compile("UP_RADIANCE([0-9]+)");

  private static ParameterAttributes<Float> getParameterAttributes(String parameterName) {
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
      return null;
    }
    if ("CHLA".equals(parameterName)) {
      return null;
    }
    if ("BISULFIDE".equals(parameterName)) {
      return null;
    }
    if ("NITRATE".equals(parameterName)) {
      return NITRATE_PARAM_ATTR;
    }
    if ("DOWNWELLING_PAR".equals(parameterName)) {
      return DOWNWELLING_PAR_PARAM_ATTR;
    }
    if ("PH_IN_SITU_TOTAL".equals(parameterName)) {
      return null;
    }
    if ("TURBIDITY".equals(parameterName)) {
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

  private static void buildParameterVariable(NetcdfFormatWriter.Builder builder, String parameterName, Dimension nProfDim, Dimension nLevelsDim) {
    buildParameterVariable(builder, parameterName, null, nProfDim, nLevelsDim);
  }

  private static void buildParameterVariable(NetcdfFormatWriter.Builder builder, String parameterName, String suffix, Dimension nProfDim,
      Dimension nLevelsDim) {
    String variableName = parameterName;
    if (suffix != null) {
      variableName = variableName + "_" + suffix;
    }
    ParameterAttributes<Float> attributes = getParameterAttributes(parameterName);
    Variable.Builder vb = builder.addVariable(variableName, DataType.FLOAT, Arrays.asList(nProfDim, nLevelsDim))
        .addAttribute(new Attribute("long_name", attributes.getLongName()));
    if (attributes.getStandardName() != null) {
      vb.addAttribute(new Attribute("standard_name", attributes.getStandardName()));
    }
    vb.addAttribute(new Attribute("_FillValue", 99999f));
    if (attributes.getUnits() != null) {
      vb.addAttribute(new Attribute("units", attributes.getUnits()));
    }
    if (attributes.getValidMin() != null) {
      vb.addAttribute(new Attribute("valid_min", attributes.getValidMin()));
    }
    if (attributes.getValidMax() != null) {
      vb.addAttribute(new Attribute("valid_max", attributes.getValidMax()));
    }
    if (attributes.getCFormat() != null) {
      vb.addAttribute(new Attribute("C_format", attributes.getCFormat()));
    }
    if (attributes.getFortranFormat() != null) {
      vb.addAttribute(new Attribute("C_format", attributes.getFortranFormat()));
    }
    if (attributes.getResolution() != null) {
      vb.addAttribute(new Attribute("resolution", attributes.getResolution()));
    }
    if (attributes.getAxis() != null) {
      vb.addAttribute(new Attribute("axis", attributes.getAxis()));
    }
  }

  private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
  private static final Instant REFERENCE_DATE = LocalDateTime.parse("19500101000000", DTF).atZone(ZoneId.of("UTC")).toInstant();

  private static void writeLevel1Double(NetcdfFormatWriter writer, String variableName, Double value) throws InvalidRangeException, IOException {
    Variable variable = writer.findVariable(variableName);
    double fill = variable.findAttribute("_FillValue").getNumericValue().doubleValue();
    ArrayDouble array = new ArrayDouble(variable.getShape());
    Index index = array.getIndex();
    array.set(index.set(0), Objects.requireNonNullElse(value, fill));
    writer.write(variable, array);
  }

  private static void writeLevel1Integer(NetcdfFormatWriter writer, String variableName, Integer value) throws InvalidRangeException, IOException {
    Variable variable = writer.findVariable(variableName);
    int fill = variable.findAttribute("_FillValue").getNumericValue().intValue();
    ArrayInt array = new ArrayInt(variable.getShape(), false);
    Index index = array.getIndex();
    array.set(index.set(0), Objects.requireNonNullElse(value, fill));
    writer.write(variable, array);
  }

  private static void writeLevel1String(NetcdfFormatWriter writer, String variableName, String value) throws InvalidRangeException, IOException {
    Variable variable = writer.findVariable(variableName);
    String strFill = variable.findAttribute("_FillValue").getStringValue();
    ArrayChar arrayChar = new ArrayChar(variable.getShape());
    Index index = arrayChar.getIndex();
    arrayChar.setString(index.set(0), Objects.requireNonNullElse(value, strFill));
    writer.write(variable, arrayChar);
  }

  private static void writeLevel1Date(NetcdfFormatWriter writer, String variableName, Instant value) throws InvalidRangeException, IOException {
    Variable variable = writer.findVariable(variableName);
    String strFill = variable.findAttribute("_FillValue").getStringValue();
    ArrayChar arrayChar = new ArrayChar(variable.getShape());
    Index index = arrayChar.getIndex();
    String dateString = strFill;
    if (value != null) {
      dateString = value.atZone(ZoneId.of("UTC")).toLocalDateTime().format(DTF);
    }
    arrayChar.setString(index.set(0), dateString);
    writer.write(variable, arrayChar);
  }

  public static void writeSingleProfile(Path netCdfFile, ArgoSyntheticProfileV13 profile, String softwareVersion)
      throws IOException, InvalidRangeException {
    // Using NetCDF 3 for thread safety, performance, and ease of use.  If NetCDF 4 is required, it will be added after the POC.
    NetcdfFormatWriter.Builder builder = NetcdfFormatWriter.createNewNetcdf3(netCdfFile.toString());
//    builder.setFill(false);

    builder.addAttribute(new Attribute("title", "Argo float vertical profile"));
    if (profile.getInstitution() != null) {
      builder.addAttribute(new Attribute("institution", profile.getInstitution()));
    }
    builder.addAttribute(new Attribute("source", "Argo float"));
    builder.addAttribute(new Attribute("history", Instant.now().toString() + " creation (Argonaut " + softwareVersion + ")"));
    builder.addAttribute(new Attribute("references", "http://www.argodatamgt.org/Documentation, https://github.com/CI-CMG/argonaut"));
    builder.addAttribute(new Attribute("user_manual_version", "1.0"));
    builder.addAttribute(new Attribute("Conventions", "Argo-3.1 CF-1.6"));
    builder.addAttribute(new Attribute("featureType", "trajectoryProfile"));
    builder.addAttribute(new Attribute("software_version", softwareVersion + " (Argonaut " + softwareVersion + ")"));
    builder.addAttribute(new Attribute("id", "https://doi.org/10.17882/42182"));

    Dimension dateTimeDim = builder.addDimension("DATE_TIME", 14);
    Dimension string256Dim = builder.addDimension("STRING256", 256);
    Dimension string64Dim = builder.addDimension("STRING64", 64);
    Dimension string32Dim = builder.addDimension("STRING32", 32);
    Dimension string8Dim = builder.addDimension("STRING8", 8);
    Dimension string4Dim = builder.addDimension("STRING4", 4);
    Dimension string2Dim = builder.addDimension("STRING2", 2);
    Dimension nProfDim = builder.addDimension("N_PROF", 1);
    Dimension nParamDim = builder.addDimension("N_PARAM", profile.getParameters().size());
    Dimension nLevelsDim = builder.addDimension("N_LEVELS", profile.getParameters().get(0).getLevels().size());
    Dimension nCalibDim = builder.addDimension("N_CALIB", 1); //TODO

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

    for (ArgoSyntheticProfileV13Parameter parameter : profile.getParameters()) {
      builder.addVariable("PROFILE_" + parameter.getParameterName() + "_QC", DataType.CHAR, Arrays.asList(nProfDim))
          .addAttribute(new Attribute("long_name", "Global quality flag of " + parameter.getParameterName() + " profile"))
          .addAttribute(new Attribute("conventions", "Argo reference table 2a"))
          .addAttribute(new Attribute("_FillValue", " "));
    }

    for (ArgoSyntheticProfileV13Parameter parameter : profile.getParameters()) {
      buildParameterVariable(builder, parameter.getParameterName(), nProfDim, nLevelsDim);

      builder.addVariable(parameter.getParameterName() + "_QC", DataType.CHAR, Arrays.asList(nProfDim, nLevelsDim))
          .addAttribute(new Attribute("long_name", "quality flag"))
          .addAttribute(new Attribute("conventions", "Argo reference table 2"))
          .addAttribute(new Attribute("_FillValue", " "));

      if (!"PRES".equals(parameter.getParameterName())) {
        ParameterAttributes<Float> att = getParameterAttributes(parameter.getParameterName());
        Variable.Builder vb = builder.addVariable(parameter.getParameterName() + "_dPRES", DataType.FLOAT, Arrays.asList(nProfDim, nLevelsDim))
            .addAttribute(new Attribute("long_name", "TEMP pressure displacement from original sampled value"))
            .addAttribute(new Attribute("_FillValue", 99999f));
        if (att.getUnits() != null) {
          vb.addAttribute(new Attribute("units", att.getUnits()));
        }

      }

      buildParameterVariable(builder, parameter.getParameterName(), "ADJUSTED", nProfDim, nLevelsDim);

      builder.addVariable(parameter.getParameterName() + "_ADJUSTED_QC", DataType.CHAR, Arrays.asList(nProfDim, nLevelsDim))
          .addAttribute(new Attribute("long_name", "quality flag"))
          .addAttribute(new Attribute("conventions", "Argo reference table 2"))
          .addAttribute(new Attribute("_FillValue", " "));

      builder.addVariable(parameter.getParameterName() + "_ADJUSTED_ERROR", DataType.FLOAT, Arrays.asList(nProfDim, nLevelsDim))
          .addAttribute(new Attribute("long_name", "Contains the error on the adjusted values as determined by the delayed mode QC process"))
          .addAttribute(new Attribute("_FillValue", 99999f))
          .addAttribute(new Attribute("units", "decibar"))
          .addAttribute(new Attribute("C_format", "%.3f"))
          .addAttribute(new Attribute("FORTRAN_format", "F.3"))
          .addAttribute(new Attribute("resolution", 0.001f));


    }



    try (NetcdfFormatWriter writer = builder.build()) {
      List<ArgoSyntheticProfileV13Parameter> parameters = profile.getParameters();

      writeLevel1String(writer, "DATA_TYPE", profile.getDataType());
      writeLevel1String(writer, "FORMAT_VERSION", profile.getFormatVersion());
      writeLevel1String(writer, "HANDBOOK_VERSION", profile.getHandbookVersion());
      writeLevel1Date(writer, "REFERENCE_DATE_TIME", profile.getReferenceDateTime() == null ? REFERENCE_DATE : profile.getReferenceDateTime());
      writeLevel1Date(writer, "DATE_CREATION", profile.getDateCreation() == null ? Instant.now() : profile.getDateCreation());
      writeLevel1Date(writer, "DATE_UPDATE", profile.getDateUpdate() == null ? Instant.now() : profile.getDateUpdate());
      writeLevel1String(writer, "PLATFORM_NUMBER", profile.getPlatformNumber());
      writeLevel1String(writer, "PROJECT_NAME", profile.getProjectName());
      writeLevel1String(writer, "PI_NAME", profile.getPrincipalInvestigatorName());
      writeLevel1Integer(writer, "CYCLE_NUMBER", profile.getCycleNumber());
      writeLevel1String(writer, "DIRECTION", profile.getDirection());
      writeLevel1String(writer, "DATA_CENTRE", profile.getDataCenter());
      writeLevel1String(writer, "PLATFORM_TYPE", profile.getPlatformType());
      writeLevel1String(writer, "FLOAT_SERIAL_NO", profile.getFloatSerialNumber());
      writeLevel1String(writer, "FIRMWARE_VERSION", profile.getFirmwareVersion());
      writeLevel1String(writer, "WMO_INST_TYPE", profile.getWmoInstrumentType());
      writeLevel1String(writer, "JULD_QC", profile.getJulianDateQc());
      writeLevel1Double(writer, "LATITUDE", profile.getLatitude());
      writeLevel1Double(writer, "LONGITUDE", profile.getLongitude());
      writeLevel1String(writer, "POSITION_QC", profile.getPositionQc());
      writeLevel1String(writer, "POSITIONING_SYSTEM", profile.getPositioningSystem());
      writeLevel1Integer(writer, "CONFIG_MISSION_NUMBER", profile.getConfigMissionNumber());
      writeLevel1Double(writer, "JULD", dateToJulianDate(profile.getReferenceDateTime() == null ? REFERENCE_DATE : profile.getReferenceDateTime(), profile.getJulianDate()));
      writeLevel1Double(writer, "JULD_LOCATION", dateToJulianDate(profile.getReferenceDateTime() == null ? REFERENCE_DATE : profile.getReferenceDateTime(), profile.getJulianDateOfLocation()));


      Variable calParamVariable = writer.findVariable("PARAMETER");
      String calParamVariableFill = calParamVariable.findAttribute("_FillValue").getStringValue();
      ArrayChar calParamVariableArray = new ArrayChar(calParamVariable.getShape());
      Index calParamVariableIndex = calParamVariableArray.getIndex();

      Variable calDateVariable = writer.findVariable("SCIENTIFIC_CALIB_DATE");
      String calDateVariableFill = calDateVariable.findAttribute("_FillValue").getStringValue();
      ArrayChar calDateVariableArray = new ArrayChar(calDateVariable.getShape());
      Index calDateVariableIndex = calDateVariableArray.getIndex();

      Variable calCommentVariable = writer.findVariable("SCIENTIFIC_CALIB_COMMENT");
      String calCommentVariableFill = calCommentVariable.findAttribute("_FillValue").getStringValue();
      ArrayChar calCommentVariableArray = new ArrayChar(calCommentVariable.getShape());
      Index calCommentVariableIndex = calCommentVariableArray.getIndex();

      Variable calCoefVariable = writer.findVariable("SCIENTIFIC_CALIB_COEFFICIENT");
      String calCoefVariableFill = calCoefVariable.findAttribute("_FillValue").getStringValue();
      ArrayChar calCoefVariableArray = new ArrayChar(calCoefVariable.getShape());
      Index calCoefVariableIndex = calCoefVariableArray.getIndex();

      Variable calEqVariable = writer.findVariable("SCIENTIFIC_CALIB_EQUATION");
      String calEqVariableFill = calEqVariable.findAttribute("_FillValue").getStringValue();
      ArrayChar calEqVariableArray = new ArrayChar(calEqVariable.getShape());
      Index calEqVariableIndex = calEqVariableArray.getIndex();

      Variable dmVariable = writer.findVariable("PARAMETER_DATA_MODE");
      String dmVariableFill = dmVariable.findAttribute("_FillValue").getStringValue();
      ArrayChar dmVariableArray = new ArrayChar.D2(1, dmVariable.getShape()[1]);
      Index dmVariableArrayIndex = dmVariableArray.getIndex();



      List<String> stationParameters = new ArrayList<>(parameters.size());
      int parameterIndex = 0;
      for (ArgoSyntheticProfileV13Parameter parameter : parameters) {
        String parameterName = parameter.getParameterName();
        stationParameters.add(parameterName);

        Variable pqcVariable = writer.findVariable("PROFILE_" + parameterName + "_QC");
        String pqcVariableFill = pqcVariable.findAttribute("_FillValue").getStringValue();
        ArrayChar pqcVariableArray = new ArrayChar(pqcVariable.getShape());
        Index pqcVariableIndex = pqcVariableArray.getIndex();
        pqcVariableArray.setString(pqcVariableIndex.set(0), Objects.requireNonNullElse(parameter.getQc(), pqcVariableFill));


        dmVariableArray.setString(dmVariableArrayIndex.set(0, parameterIndex), Objects.requireNonNullElse(parameter.getDataMode(), dmVariableFill));

        List<ArgoSyntheticProfileV13Calibration> calibrations = parameter.getCalibrations();
        int calibrationIndex = 0;
        for (ArgoSyntheticProfileV13Calibration calibration : calibrations) {
          calEqVariableArray.setString(calEqVariableIndex.set(0, calibrationIndex, parameterIndex), Objects.requireNonNullElse(calibration.getEquation(), calEqVariableFill));
          calParamVariableArray.setString(calParamVariableIndex.set(0, calibrationIndex, parameterIndex), Objects.requireNonNullElse(parameterName, calParamVariableFill));
          String calDate = calDateVariableFill;
          if (calibration.getDate() != null) {
            calDate = calibration.getDate().atZone(ZoneId.of("UTC")).toLocalDateTime().format(DTF);
          }
          calDateVariableArray.setString(calDateVariableIndex.set(0, calibrationIndex, parameterIndex), calDate);
          calCommentVariableArray.setString(calCommentVariableIndex.set(0, calibrationIndex, parameterIndex), Objects.requireNonNullElse(calibration.getComment(), calCommentVariableFill));
          calCoefVariableArray.setString(calCoefVariableIndex.set(0, calibrationIndex, parameterIndex), Objects.requireNonNullElse(calibration.getCoefficient(), calCoefVariableFill));
          calibrationIndex++;
        }

        List<ArgoSyntheticProfileV13Level> levels = parameter.getLevels();

        Variable paramVariable = writer.findVariable(parameterName);
        float paramFill = paramVariable.findAttribute("_FillValue").getNumericValue().floatValue();
        ArrayFloat originalValueArray = new ArrayFloat.D2(1, levels.size());
        Index originalValueIndex = originalValueArray.getIndex();

        Variable paramQcVariable = writer.findVariable(parameterName + "_QC");
        String paramQcFill = paramQcVariable.findAttribute("_FillValue").getStringValue();
        ArrayChar qcArray = new ArrayChar.D2(1, levels.size());
        Index qcIndex = qcArray.getIndex();

        Variable paramAdjustedVariable = writer.findVariable(parameterName + "_ADJUSTED");
        float paramAdjustedFill = paramAdjustedVariable.findAttribute("_FillValue").getNumericValue().floatValue();
        ArrayFloat adjustedValueArray = new ArrayFloat.D2(1, levels.size());
        Index adjustedValueIndex = adjustedValueArray.getIndex();

        Variable paramAdjustedQcVariable = writer.findVariable(parameterName + "_ADJUSTED_QC");
        String paramAdjustedQcFill = paramAdjustedQcVariable.findAttribute("_FillValue").getStringValue();
        ArrayChar qcAdjustedArray = new ArrayChar.D2(1, levels.size());
        Index qcAdjustedIndex = qcAdjustedArray.getIndex();

        Variable paramAdjustedErrorVariable = writer.findVariable(parameterName + "_ADJUSTED_ERROR");
        float paramAdjustedErrorFill = paramAdjustedErrorVariable.findAttribute("_FillValue").getNumericValue().floatValue();
        ArrayFloat adjustedErrorArray = new ArrayFloat.D2(1, levels.size());
        Index adjustedErrorIndex = adjustedErrorArray.getIndex();

        Variable paramDPresVariable = null;
        ArrayFloat dPressArray = null;
        Index dPressIndex = null;
        float dPressFill = 0f;
        if (!"PRES".equals(parameterName)) {
          paramDPresVariable = writer.findVariable(parameterName + "_dPRES");
          dPressFill = paramDPresVariable.findAttribute("_FillValue").getNumericValue().floatValue();
          dPressArray = new ArrayFloat.D2(1, levels.size());
          dPressIndex = dPressArray.getIndex();
        }

        for (int i = 0; i < levels.size(); i++) {
          ArgoSyntheticProfileV13Level level = levels.get(i);
          originalValueArray.setFloat(originalValueIndex.set(0, i), Objects.requireNonNullElse(level.getOriginalValue(), paramFill));
          qcArray.setChar(qcIndex.set(0, i), Objects.requireNonNullElse(level.getQc(), paramQcFill).charAt(0));
          adjustedValueArray.setFloat(adjustedValueIndex.set(0, i), Objects.requireNonNullElse(level.getAdjustedValue(), paramAdjustedFill));
          qcAdjustedArray.setString(qcAdjustedIndex.set(0, i), Objects.requireNonNullElse(level.getAdjustedQc(), paramAdjustedQcFill));
          adjustedErrorArray.setFloat(adjustedErrorIndex.set(0, i), Objects.requireNonNullElse(level.getAdjustedErrorValue(), paramAdjustedErrorFill));
          if(dPressArray != null) {
            dPressArray.setFloat(dPressIndex.set(0, i), Objects.requireNonNullElse(level.getPressureDisplacement(), dPressFill));
          }
        }

        writer.write(pqcVariable, pqcVariableArray);
        writer.write(paramVariable, originalValueArray);
        writer.write(paramQcVariable, qcArray);
        writer.write(paramAdjustedVariable, adjustedValueArray);
        writer.write(paramAdjustedQcVariable, qcAdjustedArray);
        writer.write(paramAdjustedErrorVariable, adjustedErrorArray);
        if(dPressArray != null) {
          writer.write(paramDPresVariable, dPressArray);
        }

        parameterIndex++;
      }

      writer.write(calCommentVariable, calCommentVariableArray);
      writer.write(calCoefVariable, calCoefVariableArray);
      writer.write(calDateVariable, calDateVariableArray);
      writer.write(calEqVariable, calEqVariableArray);
      writer.write(calParamVariable, calParamVariableArray);

      writer.write(dmVariable, dmVariableArray);
      writer.write(calEqVariable, calEqVariableArray);


      Variable stationParametersVariable = writer.findVariable("STATION_PARAMETERS");
      String stationParametersFill = stationParametersVariable.findAttribute("_FillValue").getStringValue();
      ArrayChar stationParametersArray = new ArrayChar.D3(1, stationParameters.size(), stationParametersVariable.getShape()[2]);
      Index stationParametersIndex = stationParametersArray.getIndex();

      for (int  i = 0; i < stationParameters.size(); i++) {
        stationParametersArray.setString(stationParametersIndex.set(0, i), Objects.requireNonNullElse(stationParameters.get(i), stationParametersFill));
      }
      writer.write(stationParametersVariable, stationParametersArray);

    }
  }




}
