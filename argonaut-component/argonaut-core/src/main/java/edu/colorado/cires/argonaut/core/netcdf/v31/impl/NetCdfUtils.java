package edu.colorado.cires.argonaut.core.netcdf.v31.impl;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import ucar.ma2.Array;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.Attribute;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

public final class NetCdfUtils {

  private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
  private static final int MS_DAY = 1000 * 60 * 60 * 24;

  private NetCdfUtils() {

  }

  public static String getStringGlobalAttribute(NetcdfFile netcdf, String name) {
    Attribute attribute = netcdf.findGlobalAttribute(name);
    if (attribute == null) {
      return null;
    }
    String value = attribute.getStringValue();
    if (value == null || value.isEmpty()) {
      return null;
    }
    return value.trim();
  }

  public static String getString(NetcdfFile netcdf, String variableName) throws IOException {
    Variable variable = netcdf.findVariable(variableName);
    if (variable == null) {
      return null;
    }
    String fillValue = " ";
    Attribute attr = variable.attributes().findAttribute("_FillValue");
    if (attr != null) {
      fillValue = attr.getStringValue();
    }
    String value = variable.read().toString();
    if (value == null) {
      return null;
    }
    value = value.trim();
    if (value.isEmpty() || value.equals(fillValue)) {
      return null;
    }
    return value;
  }

  public static List<String> getProfileListOfString(NetcdfFile netcdf, int index, String variableName) throws IOException {
    Variable variable = netcdf.findVariable(variableName);
    if (variable == null) {
      return null;
    }
    String fillValue = " ";
    Attribute attr = variable.attributes().findAttribute("_FillValue");
    if (attr != null) {
      fillValue = attr.getStringValue();
    }
    int[] shape = variable.getShape();
    shape[0] = 1;
    Array array;
    try {
      array = variable.read(new int[]{index, 0, 0}, shape);
    } catch (InvalidRangeException e) {
      throw new RuntimeException("InvalidRangeException for " + variableName, e);
    }
    char[][] charArray = (char[][]) array.reduce().copyToNDJavaArray();
    List<String> result = new ArrayList<>(charArray.length);
    for(char[] ca : charArray){
      String value = new String(ca).trim();
      if (!value.isEmpty() && !value.equals(fillValue)) {
        result.add(value);
      }
    }
    return result;
  }

  public static Integer getProfileInt(NetcdfFile netcdf, int index, String variableName) throws IOException {
    Variable variable = netcdf.findVariable(variableName);
    if (variable == null) {
      return null;
    }
    int fillValue = 99999;
    Attribute attr = variable.attributes().findAttribute("_FillValue");
    if (attr != null) {
      fillValue = (int) attr.getNumericValue();
    }
    int[] shape = variable.getShape();
    shape[0] = 1;
    int value;
    try {
      value = variable.read(new int[] {index}, new int[] {1}).getInt(0);
    } catch (InvalidRangeException e) {
      throw new RuntimeException("InvalidRangeException for " + variableName, e);
    }
    if (value == fillValue) {
      return null;
    }
    return value;
  }


  public static Instant calculateJulianDate(Instant referenceDateTime, Double daysSinceRef) {
    if (daysSinceRef == null || referenceDateTime == null) {
      return null;
    }
    int days = daysSinceRef.intValue();
    double fractDay = daysSinceRef - (double) days;
    int fract = (int)(fractDay * (double) MS_DAY);
    return referenceDateTime.plus(days, ChronoUnit.DAYS).plus(fract, ChronoUnit.MILLIS);
  }

  public static Float getProfileFloat(NetcdfFile netcdf, int index, String variableName) throws IOException {
    Variable variable = netcdf.findVariable(variableName);
    if (variable == null) {
      return null;
    }
    float fillValue = 99999f;
    Attribute attr = variable.attributes().findAttribute("_FillValue");
    if (attr != null) {
      fillValue = (float) attr.getNumericValue();
    }
    int[] shape = variable.getShape();
    shape[0] = 1;
    float value;
    try {
      value = variable.read(new int[] {index}, new int[] {1}).getFloat(0);
    } catch (InvalidRangeException e) {
      throw new RuntimeException("InvalidRangeException for " + variableName, e);
    }
    if (value == fillValue) {
      return null;
    }
    return value;
  }

  public static Double getProfileDouble(NetcdfFile netcdf, int index, String variableName) throws IOException {
    Variable variable = netcdf.findVariable(variableName);
    if (variable == null) {
      return null;
    }
    double fillValue = 999999d;
    Attribute attr = variable.attributes().findAttribute("_FillValue");
    if (attr != null) {
      fillValue = (double) attr.getNumericValue();
    }
    int[] shape = variable.getShape();
    shape[0] = 1;
    double value;
    try {
      value = variable.read(new int[] {index}, new int[] {1}).getDouble(0);
    } catch (InvalidRangeException e) {
      throw new RuntimeException("InvalidRangeException for " + variableName, e);
    }
    if (value == fillValue) {
      return null;
    }
    return value;
  }

  public static String getProfileString(NetcdfFile netcdf, int index, String variableName) throws IOException {
    Variable variable = netcdf.findVariable(variableName);
    if (variable == null) {
      return null;
    }
    String fillValue = " ";
    Attribute attr = variable.attributes().findAttribute("_FillValue");
    if (attr != null) {
      fillValue = attr.getStringValue();
    }
    int[] shape = variable.getShape();
    shape[0] = 1;
    String value;
    try {
      value = variable.read(new int[] {index, 0}, shape).toString();
    } catch (InvalidRangeException e) {
      throw new RuntimeException("InvalidRangeException for " + variableName, e);
    }
    if (value == null) {
      return null;
    }
    value = value.trim();
    if (value.isEmpty() || value.equals(fillValue)) {
      return null;
    }
    return value;
  }

  public static Float getProfileHistoryFloat(NetcdfFile netcdf, int index, int historyIndex, String variableName) throws IOException {
    Variable variable = netcdf.findVariable(variableName);
    if (variable == null) {
      return null;
    }
    float fillValue = 99999f;
    Attribute attr = variable.attributes().findAttribute("_FillValue");
    if (attr != null) {
      fillValue = (float) attr.getNumericValue();
    }
    int[] shape = variable.getShape();
    shape[0] = 1;
    shape[1] = 1;
    float value;
    try {
      value = variable.read(new int[] {historyIndex, index}, new int[] {1, 1}).getFloat(0);
    } catch (InvalidRangeException e) {
      throw new RuntimeException("InvalidRangeException for " + variableName, e);
    }
    if (value == fillValue) {
      return null;
    }
    return value;
  }

  public static Float getProfileParameterLevelFloat(NetcdfFile netcdf, int index, int levelIndex, String variableName) throws IOException {
    return getProfileHistoryFloat(netcdf, levelIndex, index, variableName);
  }

  public static String getProfileParameterLevelString(NetcdfFile netcdf, int index, int levelIndex, String variableName) throws IOException {
    return getProfileHistoryString(netcdf, levelIndex, index, variableName);
  }


  public static String getProfileHistoryString(NetcdfFile netcdf, int index, int historyIndex, String variableName) throws IOException {
    Variable variable = netcdf.findVariable(variableName);
    if (variable == null) {
      return null;
    }
    String fillValue = " ";
    Attribute attr = variable.attributes().findAttribute("_FillValue");
    if (attr != null) {
      fillValue = attr.getStringValue();
    }
    int[] shape = variable.getShape();
    shape[0] = 1;
    shape[1] = 1;
    String value;
    try {
      value = variable.read(new int[] {historyIndex, index, 0}, shape).toString();
    } catch (InvalidRangeException e) {
      throw new RuntimeException("InvalidRangeException for " + variableName, e);
    }
    if (value == null) {
      return null;
    }
    value = value.trim();
    if (value.isEmpty() || value.equals(fillValue)) {
      return null;
    }
    return value;
  }

  public static Instant getParameterCalibrationInstant(NetcdfFile netcdf, int profileIndex, int parameterIndex, int calibrationIndex, String variableName) throws IOException {
    String value = getParameterCalibrationString(netcdf, profileIndex, parameterIndex, calibrationIndex, variableName);
    if (value == null) {
      return null;
    }
    return LocalDateTime.parse(value, DATE_TIME_FORMATTER).atZone(ZoneId.of("UTC")).toInstant();
  }

  public static String getParameterCalibrationString(NetcdfFile netcdf, int profileIndex, int parameterIndex, int calibrationIndex, String variableName) throws IOException {
    Variable variable = netcdf.findVariable(variableName);
    if (variable == null) {
      return null;
    }
    String fillValue = " ";
    Attribute attr = variable.attributes().findAttribute("_FillValue");
    if (attr != null) {
      fillValue = attr.getStringValue();
    }
    int[] shape = variable.getShape();
    shape[0] = 1;
    shape[1] = 1;
    shape[2] = 1;
    String value;
    try {
      value = variable.read(new int[] {profileIndex, calibrationIndex, parameterIndex, 0}, shape).toString();
    } catch (InvalidRangeException e) {
      throw new RuntimeException("InvalidRangeException for " + variableName, e);
    }
    if (value == null) {
      return null;
    }
    value = value.trim();
    if (value.isEmpty() || value.equals(fillValue)) {
      return null;
    }
    return value;
  }

  public static Instant getProfileHistoryInstant(NetcdfFile netcdf, int index, int historyIndex, String variableName) throws IOException {
    String value = getProfileHistoryString(netcdf, index, historyIndex, variableName);
    if (value == null) {
      return null;
    }
    return LocalDateTime.parse(value, DATE_TIME_FORMATTER).atZone(ZoneId.of("UTC")).toInstant();
  }

  public static int getDimensionSize(NetcdfFile netcdf, String dimensionName) {
    Dimension dimension = netcdf.findDimension(dimensionName);
    if (dimension == null) {
      return 0;
    }
    return dimension.getLength();
  }

  public static Instant getInstant(NetcdfFile netcdf, String variableName) throws IOException {
    String value = getString(netcdf, variableName);
    if (value == null) {
      return null;
    }
    return LocalDateTime.parse(value, DATE_TIME_FORMATTER).atZone(ZoneId.of("UTC")).toInstant();
  }
}
