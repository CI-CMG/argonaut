package edu.colorado.cires.argonaut.processor.core;

public interface SubmissionProcessedCleanupProcessor {

  void cleanup(String dac, int daysOlderThan);
}
