package edu.colorado.cires.argonaut.core.merge.synthetic;

import java.util.List;
import java.util.Map;
import java.util.Optional;

record SynthProfile(int index, Map<String, SynthParameter> parameters, List<String> supportedParameters) {
  public void putParameter(SynthParameter parameter) {
    parameters.put(parameter.parameterName(), parameter);
  }

  public Optional<SynthParameter> getParameter(String parameterName) {
    return Optional.ofNullable(parameters.get(parameterName));
  }
}
