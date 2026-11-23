package edu.colorado.cires.argonaut.core.netcdf.synthprofile.v13;

import edu.colorado.cires.argonaut.core.util.ArgoNetCdfCreatedDimensions;
import edu.colorado.cires.argonaut.core.util.CommonParameterAttributes;
import edu.colorado.cires.argonaut.core.util.CommonParameterAttributes.ArgoNetCdfVariables;
import edu.colorado.cires.argonaut.core.util.NetCdfWriteUtils;
import edu.colorado.cires.argonaut.core.util.SimpleArgoNetCdfDimensions;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.write.NetcdfFormatWriter;

public class ArgoSyntheticProfileV13Writer {



  public static void writeSingleProfile(Path netCdfFile, ArgoSyntheticProfileV13 profile, String softwareVersion)
      throws IOException, InvalidRangeException {
    // Using NetCDF 3 for thread safety, performance, and ease of use.  If NetCDF 4 is required, it will be added after the POC.
    NetcdfFormatWriter.Builder builder = NetcdfFormatWriter.createNewNetcdf3(netCdfFile.toString());
    builder.setFill(true);
    CommonParameterAttributes.addGlobalAttributes(builder, profile.getInstitution(), softwareVersion);

    SimpleArgoNetCdfDimensions dimensionSpec = new SimpleArgoNetCdfDimensions();
    dimensionSpec.setProfiles(1);
    dimensionSpec.setParameters(profile.getParameters().size());
    dimensionSpec.setLevels(profile.getParameters().get(0).getLevels().size());
    dimensionSpec.setCalibrations(profile.getParameters().stream()
      .map(ArgoSyntheticProfileV13Parameter::getCalibrations)
      .map(List::size)
      .mapToInt(Integer::intValue)
      .max()
      .orElse(0)
    );

    ArgoNetCdfCreatedDimensions dimensions = CommonParameterAttributes.addCommonDimensions(builder, dimensionSpec);

        ArgoNetCdfVariables.DATA_TYPE.addToNetCdf(dimensions, builder);
        ArgoNetCdfVariables.FORMAT_VERSION.addToNetCdf(dimensions, builder);
        ArgoNetCdfVariables.HANDBOOK_VERSION.addToNetCdf(dimensions, builder);
        ArgoNetCdfVariables.REFERENCE_DATE_TIME.addToNetCdf(dimensions, builder);
        ArgoNetCdfVariables.DATE_CREATION.addToNetCdf(dimensions, builder);
        ArgoNetCdfVariables.DATE_UPDATE.addToNetCdf(dimensions, builder);
        ArgoNetCdfVariables.PLATFORM_NUMBER.addToNetCdf(dimensions, builder);
        ArgoNetCdfVariables.PROJECT_NAME.addToNetCdf(dimensions, builder);
        ArgoNetCdfVariables.PI_NAME.addToNetCdf(dimensions, builder);
        ArgoNetCdfVariables.STATION_PARAMETERS.addToNetCdf(dimensions, builder);
        ArgoNetCdfVariables.CYCLE_NUMBER.addToNetCdf(dimensions, builder);
        ArgoNetCdfVariables.DIRECTION.addToNetCdf(dimensions, builder);
        ArgoNetCdfVariables.DATA_CENTRE.addToNetCdf(dimensions, builder);
        ArgoNetCdfVariables.PARAMETER_DATA_MODE.addToNetCdf(dimensions, builder);
        ArgoNetCdfVariables.PLATFORM_TYPE.addToNetCdf(dimensions, builder);
        ArgoNetCdfVariables.FLOAT_SERIAL_NO.addToNetCdf(dimensions, builder);
        ArgoNetCdfVariables.FIRMWARE_VERSION.addToNetCdf(dimensions, builder);
        ArgoNetCdfVariables.WMO_INST_TYPE.addToNetCdf(dimensions, builder);
        ArgoNetCdfVariables.JULD.addToNetCdf(dimensions, builder);
        ArgoNetCdfVariables.JULD_QC.addToNetCdf(dimensions, builder);
        ArgoNetCdfVariables.JULD_LOCATION.addToNetCdf(dimensions, builder);
        ArgoNetCdfVariables.LATITUDE.addToNetCdf(dimensions, builder);
        ArgoNetCdfVariables.LONGITUDE.addToNetCdf(dimensions, builder);
        ArgoNetCdfVariables.POSITION_QC.addToNetCdf(dimensions, builder);
        ArgoNetCdfVariables.POSITIONING_SYSTEM.addToNetCdf(dimensions, builder);
        ArgoNetCdfVariables.CONFIG_MISSION_NUMBER.addToNetCdf(dimensions, builder);
        ArgoNetCdfVariables.PARAMETER.addToNetCdf(dimensions, builder);
        ArgoNetCdfVariables.SCIENTIFIC_CALIB_EQUATION.addToNetCdf(dimensions, builder);
        ArgoNetCdfVariables.SCIENTIFIC_CALIB_COEFFICIENT.addToNetCdf(dimensions, builder);
        ArgoNetCdfVariables.SCIENTIFIC_CALIB_COMMENT.addToNetCdf(dimensions, builder);
        ArgoNetCdfVariables.SCIENTIFIC_CALIB_DATE.addToNetCdf(dimensions, builder);

        profile.getParameters().forEach(parameter -> ArgoNetCdfVariables.PROFILE_QC.addToNetCdf(dimensions, builder, parameter.getParameterName()));

    profile.getParameters().forEach(parameter -> {
      ArgoNetCdfVariables.PARAMETER_VAR.addToNetCdf(dimensions, builder, parameter.getParameterName());
      ArgoNetCdfVariables.PARAMETER_VAR_QC.addToNetCdf(dimensions, builder, parameter.getParameterName());
      if (!parameter.getParameterName().equals("PRES")) {
        ArgoNetCdfVariables.PARAMETER_VAR_DPRESS.addToNetCdf(dimensions, builder, parameter.getParameterName());
      }
      ArgoNetCdfVariables.PARAMETER_VAR_ADJUSTED.addToNetCdf(dimensions, builder, parameter.getParameterName());
      ArgoNetCdfVariables.PARAMETER_VAR_ADJUSTED_QC.addToNetCdf(dimensions, builder, parameter.getParameterName());
      ArgoNetCdfVariables.PARAMETER_VAR_ADJUSTED_ERROR.addToNetCdf(dimensions, builder, parameter.getParameterName());
    });

    try (NetcdfFormatWriter writer = builder.build()) {
      List<ArgoSyntheticProfileV13Parameter> parameters = profile.getParameters();

      NetCdfWriteUtils.writeCommonFileLevelValues(writer, profile);
      NetCdfWriteUtils.writeCommonProfileLevelValues(writer, profile, profile, 0);
      for (int  parameterIndex = 0; parameterIndex < parameters.size(); parameterIndex++) {
        ArgoSyntheticProfileV13Parameter parameter = parameters.get(parameterIndex);
        String parameterName = parameter.getParameterName();

        NetCdfWriteUtils.writeCommonParameterLevelValues(writer, parameterName, 0, parameterIndex, parameter);

        NetCdfWriteUtils.writeCharacter(writer, new int[] {0, parameterIndex}, "PARAMETER_DATA_MODE", parameter.getDataMode());

        if (!"PRES".equals(parameterName)) {
          List<ArgoSyntheticProfileV13Level> levels = parameter.getLevels();
          for (int j = 0; j < levels.size(); j++) {
            ArgoSyntheticProfileV13Level level = levels.get(j);
            NetCdfWriteUtils.writeFloat(writer, new int[] {0, j}, parameterName + "_dPRES", level.getPressureDisplacement());
          }
        }

      }
    }
  }




}
