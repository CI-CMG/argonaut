package edu.colorado.cires.argonaut.core.netcdf.synthprofile.v13;

import edu.colorado.cires.argonaut.core.util.ArgoNetCdfCreatedDimensions;
import edu.colorado.cires.argonaut.core.util.CommonParameterAttributes;
import edu.colorado.cires.argonaut.core.util.NetCdfWriteUtils;
import edu.colorado.cires.argonaut.core.util.ParameterAttributes;
import edu.colorado.cires.argonaut.core.util.SimpleArgoNetCdfDimensions;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import ucar.ma2.DataType;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.Attribute;
import ucar.nc2.Variable;
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
    dimensionSpec.setCalibrations(1);

    ArgoNetCdfCreatedDimensions dimensions = CommonParameterAttributes.addCommonDimensions(builder, dimensionSpec);
    CommonParameterAttributes.addCommonVariables(builder, dimensions, profile.getParameters().stream().map(ArgoSyntheticProfileV13Parameter::getParameterName).toList());


    for (ArgoSyntheticProfileV13Parameter parameter : profile.getParameters()) {
      if (!"PRES".equals(parameter.getParameterName())) {
        ParameterAttributes<Float> att = CommonParameterAttributes.getParameterAttributes(parameter.getParameterName());
        Variable.Builder vb = builder.addVariable(parameter.getParameterName() + "_dPRES", DataType.FLOAT, Arrays.asList(dimensions.getnProfDim(), dimensions.getnLevelsDim()))
            .addAttribute(new Attribute("long_name", "TEMP pressure displacement from original sampled value"))
            .addAttribute(new Attribute("_FillValue", 99999f));
        if (att.getUnits() != null) {
          vb.addAttribute(new Attribute("units", att.getUnits()));
        }
      }
    }

    try (NetcdfFormatWriter writer = builder.build()) {
      List<ArgoSyntheticProfileV13Parameter> parameters = profile.getParameters();

      NetCdfWriteUtils.writeCommonFileLevelValues(writer, profile);
      NetCdfWriteUtils.writeCommonProfileLevelValues(writer, profile, profile, 0);
      for (int  parameterIndex = 0; parameterIndex < parameters.size(); parameterIndex++) {
        ArgoSyntheticProfileV13Parameter parameter = parameters.get(parameterIndex);
        String parameterName = parameter.getParameterName();

        NetCdfWriteUtils.writeCommonParameterLevelValues(writer, parameterName, 0, parameterIndex, parameter);

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
