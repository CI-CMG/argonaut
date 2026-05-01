package edu.colorado.cires.argonaut.core.merge.synthetic;

import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoProfileV31Level;
import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoProfileV31Parameter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public final class CoreParameterFilter {

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
