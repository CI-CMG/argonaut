package edu.colorado.cires.argonaut.processor.core;

import edu.colorado.cires.argonaut.messaging.core.databind.ProfileOperation;

public interface SyntheticProfileProcessor {

  void generateSyntheticProfile(ProfileOperation message);

}
