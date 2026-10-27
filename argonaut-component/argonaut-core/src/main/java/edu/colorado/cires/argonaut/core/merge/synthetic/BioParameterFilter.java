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
    Pattern.compile("BBP\\d+(?:_?\\d+)?"),
    Pattern.compile("BISULFIDE(?:_?\\d+)?"),
    Pattern.compile("CDOM(?:_?\\d+)?"),
    Pattern.compile("CHLA(?:_?\\d+)?"),
    Pattern.compile("CHLA_FLUORESCENCE(?:_?\\d+)?"),
    Pattern.compile("CP\\d+(?:_?\\d+)?"),
    Pattern.compile("DOWN_IRRADIANCE\\d+(?:_?\\d+)?"),
    Pattern.compile("DOWNWELLING_PAR(?:_?\\d+)?"),
    Pattern.compile("DOXY(?:_?\\d+)?"),
    Pattern.compile("NITRATE(?:_?\\d+)?"),
    Pattern.compile("PH_IN_SITU_TOTAL(?:_?\\d+)?"),
    Pattern.compile("TURBIDITY(?:_?\\d+)?"),
    Pattern.compile("UP_RADIANCE\\d+(?:_?\\d+)?")
  );

}
