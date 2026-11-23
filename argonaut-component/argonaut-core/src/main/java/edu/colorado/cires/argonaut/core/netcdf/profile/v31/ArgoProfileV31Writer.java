package edu.colorado.cires.argonaut.core.netcdf.profile.v31;

import static edu.colorado.cires.argonaut.core.util.NetCdfWriteUtils.REFERENCE_DATE;

import edu.colorado.cires.argonaut.core.util.ArgoNetCdfCreatedDimensions;
import edu.colorado.cires.argonaut.core.util.ArgoNetCdfDimensions;
import edu.colorado.cires.argonaut.core.util.CommonFileValues;
import edu.colorado.cires.argonaut.core.util.CommonParameterAttributes;
import edu.colorado.cires.argonaut.core.util.CommonParameterAttributes.ArgoNetCdfVariables;
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
    ArgoNetCdfVariables.DC_REFERENCE.addToNetCdf(dimensions, builder);
    ArgoNetCdfVariables.DATA_STATE_INDICATOR.addToNetCdf(dimensions, builder);
    ArgoNetCdfVariables.DATA_MODE.addToNetCdf(dimensions, builder);
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

//    POSITION_ERROR_REPORTED
//    POSITION_ERROR_ESTIMATED
//    POSITION_ERROR_ESTIMATED_COMMENT

    parameterNames.forEach(parameterName -> ArgoNetCdfVariables.PROFILE_QC.addToNetCdf(dimensions, builder, parameterName));


    ArgoNetCdfVariables.VERTICAL_SAMPLING_SCHEME.addToNetCdf(dimensions, builder);
    ArgoNetCdfVariables.CONFIG_MISSION_NUMBER.addToNetCdf(dimensions, builder);


    parameterNames.forEach(parameterName -> {
      ArgoNetCdfVariables.PARAMETER_VAR.addToNetCdf(dimensions, builder, parameterName);
      ArgoNetCdfVariables.PARAMETER_VAR_QC.addToNetCdf(dimensions, builder, parameterName);
      ArgoNetCdfVariables.PARAMETER_VAR_ADJUSTED.addToNetCdf(dimensions, builder, parameterName);
      ArgoNetCdfVariables.PARAMETER_VAR_ADJUSTED_QC.addToNetCdf(dimensions, builder, parameterName);
      ArgoNetCdfVariables.PARAMETER_VAR_ADJUSTED_ERROR.addToNetCdf(dimensions, builder, parameterName);
    });


    ArgoNetCdfVariables.PARAMETER.addToNetCdf(dimensions, builder);
    ArgoNetCdfVariables.SCIENTIFIC_CALIB_EQUATION.addToNetCdf(dimensions, builder);
    ArgoNetCdfVariables.SCIENTIFIC_CALIB_COEFFICIENT.addToNetCdf(dimensions, builder);
    ArgoNetCdfVariables.SCIENTIFIC_CALIB_COMMENT.addToNetCdf(dimensions, builder);
    ArgoNetCdfVariables.SCIENTIFIC_CALIB_DATE.addToNetCdf(dimensions, builder);
    ArgoNetCdfVariables.HISTORY_INSTITUTION.addToNetCdf(dimensions, builder);
    ArgoNetCdfVariables.HISTORY_STEP.addToNetCdf(dimensions, builder);
    ArgoNetCdfVariables.HISTORY_SOFTWARE.addToNetCdf(dimensions, builder);
    ArgoNetCdfVariables.HISTORY_SOFTWARE_RELEASE.addToNetCdf(dimensions, builder);
    ArgoNetCdfVariables.HISTORY_REFERENCE.addToNetCdf(dimensions, builder);
    ArgoNetCdfVariables.HISTORY_DATE.addToNetCdf(dimensions, builder);
    ArgoNetCdfVariables.HISTORY_ACTION.addToNetCdf(dimensions, builder);
    ArgoNetCdfVariables.HISTORY_PARAMETER.addToNetCdf(dimensions, builder);
    ArgoNetCdfVariables.HISTORY_START_PRES.addToNetCdf(dimensions, builder);
    ArgoNetCdfVariables.HISTORY_STOP_PRES.addToNetCdf(dimensions, builder);
    ArgoNetCdfVariables.HISTORY_PREVIOUS_VALUE.addToNetCdf(dimensions, builder);
    ArgoNetCdfVariables.HISTORY_QCTEST.addToNetCdf(dimensions, builder);


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
