package edu.colorado.cires.argonaut.core.util;

import static edu.colorado.cires.argonaut.core.util.NetCdfReadUtils.dateToJulianDate;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import ucar.ma2.Array;
import ucar.ma2.ArrayChar;
import ucar.ma2.ArrayDouble;
import ucar.ma2.ArrayFloat;
import ucar.ma2.ArrayInt;
import ucar.ma2.DataType;
import ucar.ma2.Index;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.Attribute;
import ucar.nc2.Dimension;
import ucar.nc2.Variable;
import ucar.nc2.write.NetcdfFormatWriter;

public final class NetCdfWriteUtils {

  public static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
  public static final Instant REFERENCE_DATE = LocalDateTime.parse("19500101000000", DTF).atZone(ZoneId.of("UTC")).toInstant();

  public static void buildParameterVariable(NetcdfFormatWriter.Builder builder, String parameterName, Dimension nProfDim, Dimension nLevelsDim) {
    buildParameterVariable(builder, parameterName, null, nProfDim, nLevelsDim);
  }

  public static void buildParameterVariable(NetcdfFormatWriter.Builder builder, String parameterName, String suffix, Dimension nProfDim,
      Dimension nLevelsDim) {
    String variableName = parameterName;
    if (suffix != null) {
      variableName = variableName + "_" + suffix;
    }
    ParameterAttributes<Float> attributes = CommonParameterAttributes.getParameterAttributes(parameterName);
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

  public static void writeString(NetcdfFormatWriter writer, int[] origin, String variableName, String value)
      throws InvalidRangeException, IOException {
    int[] modOrigin = new int[origin.length + 1];
    for (int i = 0; i < origin.length; i++) {
      modOrigin[i] = origin[i];
    }
    OriginArray<ArrayChar> oa = resolveOrigin(writer, variableName, ArrayChar::new, true);
    oa.getArray().setString(oa.getIndex(), value);
    writer.write(oa.getVariable(), modOrigin, oa.getArray());
  }

  public static void writeDate(NetcdfFormatWriter writer, int[] origin, String variableName, Instant value) throws InvalidRangeException, IOException {
    if (value != null) {
      String dateString = value.atZone(ZoneId.of("UTC")).toLocalDateTime().format(DTF);
      writeString(writer, origin, variableName, dateString);
    }
  }

  public static void writeCharacter(NetcdfFormatWriter writer, int[] origin, String variableName, String value) throws InvalidRangeException, IOException {
    if (value != null && !value.isEmpty()) {
      OriginArray<ArrayChar> oa = resolveOrigin(writer, variableName, ArrayChar::new);
      oa.getArray().setChar(oa.getIndex(), value.charAt(0));
      writer.write(oa.getVariable(), origin, oa.getArray());
    }
  }

  public static void writeDouble(NetcdfFormatWriter writer, int[] origin, String variableName, Double value)
      throws InvalidRangeException, IOException {
    if (value != null) {
      OriginArray<ArrayDouble> oa = resolveOrigin(writer, variableName, ArrayDouble::new);
      oa.getArray().setDouble(oa.getIndex(), value);
      writer.write(oa.getVariable(), origin, oa.getArray());
    }
  }

  public static void writeFloat(NetcdfFormatWriter writer, int[] origin, String variableName, Float value) throws InvalidRangeException, IOException {
    if (value != null) {
      OriginArray<ArrayFloat> oa = resolveOrigin(writer, variableName, ArrayFloat::new);
      oa.getArray().setFloat(oa.getIndex(), value);
      writer.write(oa.getVariable(), origin, oa.getArray());
    }
  }

  private static class OriginArray<A extends Array> {
    private final Index index;
    private final A array;
    private final Variable variable;

    private OriginArray(Index index, A array, Variable variable) {
      this.index = index;
      this.array = array;
      this.variable = variable;
    }

    public Index getIndex() {
      return index;
    }

    public A getArray() {
      return array;
    }

    public Variable getVariable() {
      return variable;
    }
  }

  private static <A extends Array> OriginArray<A> resolveOrigin(NetcdfFormatWriter writer, String variableName, Function<int[], A> arrayFactory) {
    return resolveOrigin(writer, variableName, arrayFactory, false);
  }

  private static <A extends Array> OriginArray<A> resolveOrigin(NetcdfFormatWriter writer, String variableName, Function<int[], A> arrayFactory, boolean string) {
    Variable variable = writer.findVariable(variableName);
    int length = variable.getShape()[variable.getRank() - 1];
    int[] dimensions = new int[variable.getRank()];
    Arrays.fill(dimensions, 1);
    if (string) {
      dimensions[variable.getRank() - 1] = length;
    }
    A array = arrayFactory.apply(dimensions);
    Index index = array.getIndex();
    return new OriginArray<>(index, array, variable);
  }

  public static void writeInteger(NetcdfFormatWriter writer, int[] origin, String variableName, Integer value) throws InvalidRangeException, IOException {
    if (value != null) {
      OriginArray<ArrayInt> oa = resolveOrigin(writer, variableName, dim -> new ArrayInt(dim, false));
      oa.getArray().setInt(oa.getIndex(), value);
      writer.write(oa.getVariable(), origin, oa.getArray());
    }
  }


  public static void writeCommonFileLevelValues(NetcdfFormatWriter writer, CommonFileValues profile) throws InvalidRangeException, IOException {
    writeDate(writer, new int[] {0}, "REFERENCE_DATE_TIME", profile.getReferenceDateTime() == null ? REFERENCE_DATE : profile.getReferenceDateTime());
    writeDate(writer, new int[] {0}, "DATE_CREATION", profile.getDateCreation() == null ? Instant.now() : profile.getDateCreation());
    writeDate(writer, new int[] {0}, "DATE_UPDATE", profile.getDateUpdate() == null ? Instant.now() : profile.getDateUpdate());
    writeString(writer, new int[] {0}, "DATA_TYPE", profile.getDataType());
    writeString(writer, new int[] {0}, "FORMAT_VERSION", profile.getFormatVersion());
    writeString(writer, new int[] {0}, "HANDBOOK_VERSION", profile.getHandbookVersion());
  }

  public static void writeCommonProfileLevelValues(NetcdfFormatWriter writer, CommonFileValues file, CommonProfileValues profile, int profileIndex)
      throws InvalidRangeException, IOException {
    writeString(writer, new int[] {profileIndex}, "PLATFORM_NUMBER", profile.getPlatformNumber());
    writeString(writer, new int[] {profileIndex}, "PROJECT_NAME", profile.getProjectName());
    writeString(writer, new int[] {profileIndex}, "PI_NAME", profile.getPrincipalInvestigatorName());
    writeInteger(writer, new int[] {profileIndex}, "CYCLE_NUMBER", profile.getCycleNumber());
    writeCharacter(writer, new int[] {profileIndex}, "DIRECTION", profile.getDirection());
    writeString(writer, new int[] {profileIndex}, "DATA_CENTRE", profile.getDataCenter());
    writeString(writer, new int[] {profileIndex}, "PLATFORM_TYPE", profile.getPlatformType());
    writeString(writer, new int[] {profileIndex}, "FLOAT_SERIAL_NO", profile.getFloatSerialNumber());
    writeString(writer, new int[] {profileIndex}, "FIRMWARE_VERSION", profile.getFirmwareVersion());
    writeString(writer, new int[] {profileIndex}, "WMO_INST_TYPE", profile.getWmoInstrumentType());
    writeCharacter(writer, new int[] {profileIndex}, "JULD_QC", profile.getJulianDateQc());
    writeDouble(writer, new int[] {profileIndex}, "LATITUDE", profile.getLatitude());
    writeDouble(writer, new int[] {profileIndex}, "LONGITUDE", profile.getLongitude());
    writeCharacter(writer, new int[] {profileIndex}, "POSITION_QC", profile.getPositionQc());
    writeString(writer, new int[] {profileIndex}, "POSITIONING_SYSTEM", profile.getPositioningSystem());
    writeInteger(writer, new int[] {profileIndex}, "CONFIG_MISSION_NUMBER", profile.getConfigMissionNumber());
    writeDouble(writer, new int[] {profileIndex}, "JULD", dateToJulianDate(file.getReferenceDateTime() == null ? REFERENCE_DATE : file.getReferenceDateTime(), profile.getJulianDate()));
    writeDouble(writer, new int[] {profileIndex}, "JULD_LOCATION", dateToJulianDate(file.getReferenceDateTime() == null ? REFERENCE_DATE : file.getReferenceDateTime(), profile.getJulianDateOfLocation()));
  }

  public static void writeCommonParameterLevelValues(NetcdfFormatWriter writer, String parameterName, int profileIndex, int parameterIndex,
      CommonParameterValues parameter) throws InvalidRangeException, IOException {

    writeCharacter(writer, new int[] {profileIndex}, "PROFILE_" + parameterName + "_QC", parameter.getQc());
    writeCharacter(writer, new int[] {profileIndex, parameterIndex}, "PARAMETER_DATA_MODE", parameter.getDataMode());
    writeString(writer, new int[] {profileIndex, parameterIndex}, "STATION_PARAMETERS", parameterName);

    List<? extends CommonCalibrationValues> calibrations = parameter.getCalibrations();
    int calibrationIndex = 0;
    for (CommonCalibrationValues calibration : calibrations) {
      writeString(writer, new int[]{profileIndex, calibrationIndex, parameterIndex}, "SCIENTIFIC_CALIB_EQUATION", calibration.getEquation());
      writeDate(writer, new int[]{profileIndex, calibrationIndex, parameterIndex}, "SCIENTIFIC_CALIB_DATE", calibration.getDate());
      writeString(writer, new int[]{profileIndex, calibrationIndex, parameterIndex}, "SCIENTIFIC_CALIB_COMMENT", calibration.getComment());
      writeString(writer, new int[]{profileIndex, calibrationIndex, parameterIndex}, "SCIENTIFIC_CALIB_COEFFICIENT", calibration.getCoefficient());
      writeString(writer, new int[]{profileIndex, calibrationIndex, parameterIndex}, "PARAMETER", parameterName);
      calibrationIndex++;
    }

    for (int i = 0; i < parameter.getLevels().size(); i++) {
      CommonLevelValues level = parameter.getLevels().get(i);
      writeFloat(writer, new int[]{profileIndex, i}, parameterName, level.getValue());
      writeCharacter(writer, new int[]{profileIndex, i}, parameterName + "_QC", level.getQc());
      writeFloat(writer, new int[]{profileIndex, i}, parameterName + "_ADJUSTED", level.getAdjustedValue());
      writeCharacter(writer, new int[]{profileIndex, i}, parameterName + "_ADJUSTED_QC", level.getAdjustedQc());
      writeFloat(writer, new int[]{profileIndex, i}, parameterName + "_ADJUSTED_ERROR", level.getAdjustedErrorValue());
    }

  }


  private NetCdfWriteUtils() {

  }

}
