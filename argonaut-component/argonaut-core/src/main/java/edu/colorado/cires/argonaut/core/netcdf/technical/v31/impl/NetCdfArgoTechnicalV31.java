package edu.colorado.cires.argonaut.core.netcdf.technical.v31.impl;

import static edu.colorado.cires.argonaut.core.util.NetCdfReadUtils.getDimensionSize;
import static edu.colorado.cires.argonaut.core.util.NetCdfReadUtils.getGlobalAttributeString;
import static edu.colorado.cires.argonaut.core.util.NetCdfReadUtils.getInstant;
import static edu.colorado.cires.argonaut.core.util.NetCdfReadUtils.getLevel1Integer;
import static edu.colorado.cires.argonaut.core.util.NetCdfReadUtils.getLevel1String;
import static edu.colorado.cires.argonaut.core.util.NetCdfReadUtils.getString;

import edu.colorado.cires.argonaut.core.netcdf.technical.v31.ArgoTechnicalV31;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import ucar.nc2.NetcdfFile;
import ucar.nc2.NetcdfFiles;

public class NetCdfArgoTechnicalV31 implements ArgoTechnicalV31, AutoCloseable {

  private final NetcdfFile netcdfFile;

  private NetCdfArgoTechnicalV31(Path path) throws IOException {
    this.netcdfFile = NetcdfFiles.open(path.toString());
  }

  public static NetCdfArgoTechnicalV31 create(Path path) throws IOException {
    return new NetCdfArgoTechnicalV31(path);
  }

  @Override
  public void close() throws IOException {
    netcdfFile.close();
  }

  @Override
  public String getTitle() {
    return getGlobalAttributeString(netcdfFile, "title");
  }

  @Override
  public String getInstitution() {
    return getGlobalAttributeString(netcdfFile, "institution");
  }

  @Override
  public String getSource() {
    return getGlobalAttributeString(netcdfFile, "source");
  }

  @Override
  public String getHistory() {
    return getGlobalAttributeString(netcdfFile, "history");
  }

  @Override
  public String getReferences() {
    return getGlobalAttributeString(netcdfFile, "references");
  }

  @Override
  public String getComment() {
    return getGlobalAttributeString(netcdfFile, "commet");
  }

  @Override
  public String getUserManualVersion() {
    return getGlobalAttributeString(netcdfFile, "user_manual_version");
  }

  @Override
  public String getConventions() {
    return getGlobalAttributeString(netcdfFile, "Conventions");
  }

  @Override
  public String getPlatformNumber() {
    return getString(netcdfFile, "PLATFORM_NUMBER");
  }

  @Override
  public String getDataType() {
    return getString(netcdfFile, "DATA_TYPE");
  }

  @Override
  public String getFormatVersion() {
    return getString(netcdfFile, "FORMAT_VERSION");
  }

  @Override
  public String getHandbookVersion() {
    return getString(netcdfFile, "HANDBOOK_VERSION");
  }

  @Override
  public String getDataCenter() {
    return getString(netcdfFile, "DATA_CENTRE");
  }

  @Override
  public Instant getDateCreation() {
    return getInstant(netcdfFile, "DATE_CREATION");
  }

  @Override
  public Instant getDateUpdate() {
    return getInstant(netcdfFile, "DATE_UPDATE");
  }

  @Override
  public Integer getNParameters() {
    return getDimensionSize(netcdfFile, "N_TECH_PARAM");
  }

  @Override
  public String getTechnicalParameterName(Integer nParameter) {
    return getLevel1String(netcdfFile, nParameter, "TECHNICAL_PARAMETER_NAME");
  }

  @Override
  public String getTechnicalParameterValue(Integer nParameter) {
    return getLevel1String(netcdfFile, nParameter, "TECHNICAL_PARAMETER_VALUE");
  }

  @Override
  public Integer getCycleNumber(Integer nParameter) {
    return getLevel1Integer(netcdfFile, nParameter, "CYCLE_NUMBER");
  }
}
