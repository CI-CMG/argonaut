package edu.colorado.cires.argonaut.core.merge.synthetic;

record SynthParameter(String parameterName, Float value, Long pDiff, String qc, Float adjustedValue, Float adjustedError, String adjustedQc) {
  public SynthParameter withPDiff(Long pDiff) {
    return new SynthParameter(parameterName, value, pDiff, qc, adjustedValue, adjustedError, adjustedQc);
  }
}
