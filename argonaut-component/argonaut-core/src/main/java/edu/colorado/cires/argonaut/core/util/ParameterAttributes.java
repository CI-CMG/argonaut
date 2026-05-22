package edu.colorado.cires.argonaut.core.util;

public interface ParameterAttributes<T> {

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
