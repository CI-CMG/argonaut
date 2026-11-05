package edu.colorado.cires.argonaut.core.merge.synthetic;

import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoProfileV31Level;
import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoProfileV31Parameter;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class MergeConstants {
  private MergeConstants() {}

  static final List<String> GOOD_QC = Arrays.asList("1", "2", "5");
  static final List<String> GOOD_QC_8 = Arrays.asList("1", "2", "5", "8");
  static final List<String> BAD_QC = Arrays.asList("3", "4");
  static final BigDecimal DBAR_CONVERSION = new BigDecimal("1000");

  static final class CoreParameterFilter {

    private CoreParameterFilter() {

    }

    public static boolean isSupportedParameter(ArgoProfileV31Parameter parameter, ArgoProfileV31Level level) {
      return C_QC.contains(level.getQc()) && C_PARAMS.contains(parameter.getParameterName());
    }

    public static boolean isSupportedParameter(String parameterName) {
      return C_PARAMS.contains(parameterName);
    }

    static final Set<String> C_PARAMS = Collections.unmodifiableSet(new LinkedHashSet<>(Arrays.asList("PRES", "TEMP", "PSAL")));
    private static final  Set<String> C_QC = Collections.unmodifiableSet(new HashSet<>(Arrays.asList("1", "2", "3", "4", "5")));

  }

  static final class BioParameterFilter {

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
}
