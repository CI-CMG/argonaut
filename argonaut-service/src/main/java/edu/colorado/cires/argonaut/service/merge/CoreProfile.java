package edu.colorado.cires.argonaut.service.merge;

import java.nio.file.Path;

public class CoreProfile {
  private Path file;
  private int nParam;
  private int nCalib;
  private int nLevels;
  private int cycleNumber;
  private double juld;
  private double latitude;
  private double longitude;

  public CoreProfile() {
  }

  public CoreProfile(int cycleNumber, float juld) {
    this.cycleNumber = cycleNumber;
    this.juld = juld;
  }

  public Path getFile() {
    return file;
  }

  public void setFile(Path file) {
    this.file = file;
  }

  public int getnParam() {
    return nParam;
  }

  public void setnParam(int nParam) {
    this.nParam = nParam;
  }

  public int getnCalib() {
    return nCalib;
  }

  public void setnCalib(int nCalib) {
    this.nCalib = nCalib;
  }

  public int getnLevels() {
    return nLevels;
  }

  public void setnLevels(int nLevels) {
    this.nLevels = nLevels;
  }

  public int getCycleNumber() {
    return cycleNumber;
  }

  public void setCycleNumber(int cycleNumber) {
    this.cycleNumber = cycleNumber;
  }

  public double getJuld() {
    return juld;
  }

  public void setJuld(double juld) {
    this.juld = juld;
  }

  public double getLatitude() {
    return latitude;
  }

  public void setLatitude(double latitude) {
    this.latitude = latitude;
  }

  public double getLongitude() {
    return longitude;
  }

  public void setLongitude(double longitude) {
    this.longitude = longitude;
  }
}
