package edu.colorado.cires.argonaut.core.netcdf.synthprofile.v13;

import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoProfileDataMode;
import java.util.List;

public interface ArgoSyntheticProfileV13Parameter {

  ArgoProfileDataMode getDataMode();

  String getQc();


  List<ArgoSyntheticProfileV13Level> getLevels();


}
