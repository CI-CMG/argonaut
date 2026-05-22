package edu.colorado.cires.argonaut.core.netcdf.profile.v31;

import edu.colorado.cires.argonaut.core.util.ArgoNetCdfCreatedDimensions;
import edu.colorado.cires.argonaut.core.util.ArgoNetCdfDimensions;
import edu.colorado.cires.argonaut.core.util.CommonParameterAttributes;
import edu.colorado.cires.argonaut.core.util.NetCdfWriteUtils;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.write.NetcdfFormatWriter;

public class ArgoProfileV31Writer {


  public static void writeMultiProfile(
      Path netCdfFile,
      String softwareVersion,
      String institution,
      List<String> parameterNames,
      ArgoNetCdfDimensions dimensionSpec,
      Iterator<ArgoProfileV31> profileIterator
  ) throws IOException, InvalidRangeException {
    // Using NetCDF 3 for thread safety, performance, and ease of use.  If NetCDF 4 is required, it will be added after the POC.
    NetcdfFormatWriter.Builder builder = NetcdfFormatWriter.createNewNetcdf3(netCdfFile.toString());
    CommonParameterAttributes.addGlobalAttributes(builder, institution, softwareVersion);

    ArgoNetCdfCreatedDimensions dimensions = CommonParameterAttributes.addCommonDimensions(builder, dimensionSpec);
    CommonParameterAttributes.addCommonVariables(builder, dimensions, parameterNames);

    try (NetcdfFormatWriter writer = builder.build()) {
      while (profileIterator.hasNext()) {
        ArgoProfileV31 profile = profileIterator.next();
        List<ArgoProfileV31Parameter> parameters = profile.getParameters();
        NetCdfWriteUtils.writeCommonFileLevelValues(writer, profile);
        NetCdfWriteUtils.writeCommonProfileLevelValues(writer, profile, profile, 0);
        for (int parameterIndex = 0; parameterIndex < parameters.size(); parameterIndex++) {
          ArgoProfileV31Parameter parameter = parameters.get(parameterIndex);
          String parameterName = parameter.getParameterName();
          NetCdfWriteUtils.writeCommonParameterLevelValues(writer, parameterName, 0, parameterIndex, parameter);
        }
      }
    }
  }


}
