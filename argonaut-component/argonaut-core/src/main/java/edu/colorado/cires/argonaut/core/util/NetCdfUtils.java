package edu.colorado.cires.argonaut.core.util;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
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

  public static String getGlobalAttributeString(NetcdfFile netcdf, String name) {
    Attribute attribute = netcdf.findGlobalAttribute(name);
    if (attribute == null) {
      return null;
    }
    String value = attribute.getStringValue();
    if (value == null) {
      return null;
    }
    value = value.trim();
    if (value.isEmpty()) {
      return null;
    }
    return value;
  }

  private static <T> T doWithVariable(
      NetcdfFile netcdf,
      String variableName,
      T defaultFillValue,
      Function<Attribute, T> getFillValue,
      Function<Variable, T> read
      ) {
    Variable variable = netcdf.findVariable(variableName);
    if (variable == null) {
      return null;
    }
    T fillValue = defaultFillValue;
    Attribute attr = variable.attributes().findAttribute("_FillValue");
    if (attr != null) {
      fillValue = getFillValue.apply(attr);
    }
    T value = read.apply(variable);
    if (value == null || value.equals(fillValue)) {
      return null;
    }
    return value;
  }

  private static String doWithVariableString(NetcdfFile netcdf, String variableName, Function<Variable, int[]> getOrigin,  Function<Variable, int[]> getShape) {
    return doWithVariable(netcdf, variableName, " ", Attribute::getStringValue, variable -> {
      int[] origin = getOrigin.apply(variable);
      int[] shape = getShape.apply(variable);
      try {
        String value = variable.read(origin, shape).toString().trim();
        if (value.isEmpty()) {
          return null;
        }
        return value;
      } catch (ArrayIndexOutOfBoundsException e) {
        //TODO log warning
        return null;

      } catch (IOException | InvalidRangeException e) {
        throw new RuntimeException("Unable to read " + variableName, e);
      }
    });
  }

  private static Float doWithVariableFloat(NetcdfFile netcdf, String variableName, Function<Variable, int[]> getOrigin,  Function<Variable, int[]> getShape) {
    return doWithVariable(netcdf, variableName, 99999f, attr -> attr.getNumericValue().floatValue(), variable -> {
      int[] origin = getOrigin.apply(variable);
      int[] shape = getShape.apply(variable);
      try {
        return variable.read(origin, shape).getFloat(0);
      } catch (ArrayIndexOutOfBoundsException e) {
        //TODO log warning
        return null;
      } catch (IOException | InvalidRangeException e) {
        throw new RuntimeException("Unable to read " + variableName, e);
      }
    });
  }

  private static Integer doWithVariableInteger(NetcdfFile netcdf, String variableName, Function<Variable, int[]> getOrigin,  Function<Variable, int[]> getShape) {
    return doWithVariable(netcdf, variableName, 99999, attr -> (int) attr.getNumericValue(), variable -> {
      int[] origin = getOrigin.apply(variable);
      int[] shape = getShape.apply(variable);
      try {
        return variable.read(origin, shape).getInt(0);
      } catch (ArrayIndexOutOfBoundsException e) {
        //TODO log warning
        return null;

      } catch (IOException | InvalidRangeException e) {
        throw new RuntimeException("Unable to read " + variableName, e);
      }
    });
  }

  private static Double doWithVariableDouble(NetcdfFile netcdf, String variableName, Function<Variable, int[]> getOrigin,  Function<Variable, int[]> getShape) {
    return doWithVariable(netcdf, variableName, 999999d, attr -> (double) attr.getNumericValue(), variable -> {
      int[] origin = getOrigin.apply(variable);
      int[] shape = getShape.apply(variable);
      try {
        return variable.read(origin, shape).getDouble(0);
      } catch (ArrayIndexOutOfBoundsException e) {
        //TODO log warning
        return null;

      } catch (IOException | InvalidRangeException e) {
        throw new RuntimeException("Unable to read " + variableName, e);
      }
    });
  }

  public static Float getLevel1Float(NetcdfFile netcdf, int index, String variableName) {
    return doWithVariableFloat(netcdf, variableName, variable -> new int[]{index}, variable -> {
      int[] shape = variable.getShape();
      shape[0] = 1;
      return shape;
    });
  }

  public static Integer getLevel1Integer(NetcdfFile netcdf, int level1Index, String variableName) {
    return doWithVariableInteger(netcdf, variableName, variable -> new int[]{level1Index}, variable -> {
      int[] shape = variable.getShape();
      shape[0] = 1;
      return shape;
    });
  }

  public static Double getLevel1Double(NetcdfFile netcdf, int level1Index, String variableName) {
    return doWithVariableDouble(netcdf, variableName, variable -> new int[]{level1Index}, variable -> {
      int[] shape = variable.getShape();
      shape[0] = 1;
      return shape;
    });
  }


  public static String getString(NetcdfFile netcdf, String variableName) {
    return doWithVariableString(netcdf, variableName, variable -> new int[]{0}, Variable::getShape);
  }

  public static Double getDouble(NetcdfFile netcdf, String variableName) {
    return doWithVariableDouble(netcdf, variableName, variable -> new int[]{0}, Variable::getShape);
  }

  public static String getLevel1String(NetcdfFile netcdf, int index, String variableName) {
    return doWithVariableString(netcdf, variableName, variable -> new int[]{index, 0}, variable -> {
      int[] shape = variable.getShape();
      shape[0] = 1;
      return shape;
    });
  }

  public static String getLevel2String(NetcdfFile netcdf, int indexLevel1, int indexLevel2, String variableName) {
    return doWithVariableString(netcdf, variableName, variable -> new int[]{indexLevel1, indexLevel2, 0}, variable -> {
      int[] shape = variable.getShape();
      shape[0] = 1;
      shape[1] = 1;
      return shape;
    });
  }

  public static Instant getLevel2Instant(NetcdfFile netcdf, int indexLevel1, int indexLevel2, String variableName) {
    String value = getLevel2String(netcdf, indexLevel1, indexLevel2, variableName);
    if (value == null) {
      return null;
    }
    return LocalDateTime.parse(value, DATE_TIME_FORMATTER).atZone(ZoneId.of("UTC")).toInstant();
  }

  public static List<String> getListOfString(NetcdfFile netcdf, String variableName) {
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
//    shape[0] = 1;
    Array array;
    try {
      array = variable.read(new int[]{0, 0}, shape);
    } catch (ArrayIndexOutOfBoundsException e) {
      //TODO log warning
      return null;

    } catch (InvalidRangeException | IOException e) {
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


  public static List<String> getLevel1ListOfString(NetcdfFile netcdf, int level1Index, String variableName) {
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
      array = variable.read(new int[]{level1Index, 0, 0}, shape);
    } catch (ArrayIndexOutOfBoundsException e) {
      //TODO log warning
      return null;

    } catch (InvalidRangeException | IOException e) {
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


  public static Instant calculateJulianDate(Instant referenceDateTime, Double daysSinceRef) {
    if (daysSinceRef == null || referenceDateTime == null) {
      return null;
    }
    int days = daysSinceRef.intValue();
    double fractDay = daysSinceRef - (double) days;
    int fract = (int)(fractDay * (double) MS_DAY);
    return referenceDateTime.plus(days, ChronoUnit.DAYS).plus(fract, ChronoUnit.MILLIS);
  }

  public static Float getLevel2Float(NetcdfFile netcdf, int level1Index, int level2Index, String variableName)  {
    return doWithVariableFloat(netcdf, variableName, variable -> new int[]{level1Index, level2Index}, variable -> {
      int[] shape = variable.getShape();
      shape[0] = 1;
      shape[1] = 1;
      return shape;
    });
  }

  public static Instant getLevel3Instant(NetcdfFile netcdf, int level1Index, int level2Index, int level3Index, String variableName) {
    String value = getLevel3String(netcdf, level1Index, level2Index, level3Index, variableName);
    if (value == null) {
      return null;
    }
    return LocalDateTime.parse(value, DATE_TIME_FORMATTER).atZone(ZoneId.of("UTC")).toInstant();
  }

  public static String getLevel3String(NetcdfFile netcdf, int level1Index, int level2Index, int level3Index, String variableName) {
    return doWithVariableString(netcdf, variableName, variable -> new int[]{level1Index, level2Index, level3Index, 0}, variable -> {
      int[] shape = variable.getShape();
      shape[0] = 1;
      shape[1] = 1;
      shape[2] = 1;
      return shape;
    });
  }

  public static int getDimensionSize(NetcdfFile netcdf, String dimensionName) {
    Dimension dimension = netcdf.findDimension(dimensionName);
    if (dimension == null) {
      return 0;
    }
    return dimension.getLength();
  }

  public static Instant getInstant(NetcdfFile netcdf, String variableName) {
    String value = getString(netcdf, variableName);
    if (value == null) {
      return null;
    }
    return LocalDateTime.parse(value, DATE_TIME_FORMATTER).atZone(ZoneId.of("UTC")).toInstant();
  }
}
