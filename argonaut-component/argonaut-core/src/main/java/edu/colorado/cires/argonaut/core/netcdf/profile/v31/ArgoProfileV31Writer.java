package edu.colorado.cires.argonaut.core.netcdf.profile.v31;

import static edu.colorado.cires.argonaut.core.util.NetCdfWriteUtils.REFERENCE_DATE;

import edu.colorado.cires.argonaut.core.util.ArgoNetCdfCreatedDimensions;
import edu.colorado.cires.argonaut.core.util.ArgoNetCdfDimensions;
import edu.colorado.cires.argonaut.core.util.CommonFileValues;
import edu.colorado.cires.argonaut.core.util.CommonParameterAttributes;
import edu.colorado.cires.argonaut.core.util.NetCdfWriteUtils;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
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
    builder.setFill(true);
    CommonParameterAttributes.addGlobalAttributes(builder, institution, softwareVersion);

    ArgoNetCdfCreatedDimensions dimensions = CommonParameterAttributes.addCommonDimensions(builder, dimensionSpec);
    CommonParameterAttributes.addCommonVariables(builder, dimensions, parameterNames);

    try (NetcdfFormatWriter writer = builder.build()) {
      while (profileIterator.hasNext()) {
        ArgoProfileV31 profile = profileIterator.next();
        NetCdfWriteUtils.writeCommonFileLevelValues(writer, getCommonFileValues());
        NetCdfWriteUtils.writeCommonProfileLevelValues(writer, profile, profile, profile.getProfileIndex());
        for (int parameterIndex = 0; parameterIndex < parameterNames.size(); parameterIndex++) {
          String parameterName = parameterNames.get(parameterIndex);
          ArgoProfileV31Parameter parameter = profile.getParameter(parameterName);
          if (parameter != null) {
            NetCdfWriteUtils.writeCommonParameterLevelValues(writer, parameterName, profile.getProfileIndex(), parameterIndex, parameter);
          }
        }
      }
    }
  }

  private static CommonFileValues getCommonFileValues() {
    Instant now = Instant.now();
    return new CommonFileValues() {
      @Override
      public String getDataType() {
        return "Argo profile";
      }

      @Override
      public String getFormatVersion() {
        return "3.1";
      }

      @Override
      public String getHandbookVersion() {
        return "1.2";
      }

      @Override
      public Instant getReferenceDateTime() {
        return REFERENCE_DATE;
      }

      @Override
      public Instant getDateCreation() {
        return now;
      }

      @Override
      public Instant getDateUpdate() {
        return now;
      }
    };
  }

}
