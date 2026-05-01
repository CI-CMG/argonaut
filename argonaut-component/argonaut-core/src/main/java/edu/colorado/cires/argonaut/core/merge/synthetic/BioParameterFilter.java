package edu.colorado.cires.argonaut.core.merge.synthetic;

import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoProfileV31Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class BioParameterFilter {

  private BioParameterFilter() {

  }

  public static boolean isSupportedParameter(ArgoProfileV31Parameter parameter) {
    return isSupportedParameter(parameter.getParameterName());
  }

  public static boolean isSupportedParameter(String parameterName) {
    for (Pattern pattern : B_PARAM_PATTERNS) {
      Matcher matcher = pattern.matcher(parameterName);
      if (matcher.matches()) {
        return true;
      }
    }
    return false;
  }

  private static final List<Pattern> B_PARAM_PATTERNS = Arrays.asList(
      Pattern.compile("BBP[0-9]+"),
      Pattern.compile("BISULFIDE"),
      Pattern.compile("CDOM"),
      Pattern.compile("CHLA"),
      Pattern.compile("CP[0-9]+"),
      Pattern.compile("DOWN_IRRADIANCE[0-9]+"),
      Pattern.compile("DOWNWELLING_PAR"),
      Pattern.compile("DOXY"),
      Pattern.compile("NITRATE"),
      Pattern.compile("PH_IN_SITU_TOTAL"),
      Pattern.compile("TURBIDITY"),
      Pattern.compile("UP_RADIANCE[0-9]+")
  );

}
