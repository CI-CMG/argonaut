package edu.colorado.cires.argonaut.messaging.core.databind;

import java.util.Objects;

public class ArgoFileTypeDetails {

  public enum Direction {
    A, D
  }

  public enum DataMode {
    R, D
  }

  private final ArgoFileType type;
  private final DataMode dataMode;
  private final String floatId;
  private final String cycleNumber;
  private final Direction direction;

  ArgoFileTypeDetails(ArgoFileType type, DataMode dataMode, String floatId, String cycleNumber, Direction direction) {
    this.type = type;
    this.dataMode = dataMode;
    this.floatId = floatId;
    this.cycleNumber = cycleNumber;
    this.direction = direction;
  }

  public ArgoFileType getType() {
    return type;
  }

  public DataMode getDataMode() {
    return dataMode;
  }

  public String getFloatId() {
    return floatId;
  }

  public String getCycleNumber() {
    return cycleNumber;
  }

  public Direction getDirection() {
    return direction;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ArgoFileTypeDetails that = (ArgoFileTypeDetails) o;
    return type == that.type && dataMode == that.dataMode && Objects.equals(floatId, that.floatId) && Objects.equals(cycleNumber,
        that.cycleNumber) && direction == that.direction;
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, dataMode, floatId, cycleNumber, direction);
  }

  @Override
  public String toString() {
    return "ArgoFileTypeDetails{" +
        "type=" + type +
        ", dataMode=" + dataMode +
        ", floatId='" + floatId + '\'' +
        ", cycleNumber='" + cycleNumber + '\'' +
        ", direction=" + direction +
        '}';
  }
}
