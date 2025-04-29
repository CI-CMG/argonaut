package edu.colorado.cires.argonaut;

import java.nio.file.Path;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ucar.nc2.Dimension;
import ucar.nc2.write.NetcdfFormatWriter;

@Component
public class MergeProcessor implements Processor {

  private final ServiceProperties serviceProperties;

  @Autowired
  public MergeProcessor(ServiceProperties serviceProperties) {
    this.serviceProperties = serviceProperties;
  }

  @Override
  public void process(Exchange exchange) throws Exception {
    TempMergeRequest mergeRequest = exchange.getIn().getBody(TempMergeRequest.class);



    NetcdfFormatWriter.Builder builder = NetcdfFormatWriter.createNewNetcdf3("todofilename");

    // DATE_TIME and STRINGx: These dimensions are essentially constants. They are set to their typical values as documented in the User’s Manual.
    Dimension dateTimeDim = builder.addDimension("DATE_TIME", 14);
    Dimension string256Dim = builder.addDimension("STRING256", 256);
    Dimension string64Dim = builder.addDimension("STRING64", 64);
    Dimension string32Dim = builder.addDimension("STRING32", 32);
    Dimension string16Dim = builder.addDimension("STRING16", 16);
    Dimension string8Dim = builder.addDimension("STRING8", 8);
    Dimension string4Dim = builder.addDimension("STRING4", 4);
    Dimension string2Dim = builder.addDimension("STRING2", 2);

    // N_PROF: Set to the same value as N_PROF in both the core-file and bio-file, which are required to be the same. (See §2.14 for the validation details.)
    Dimension nProfDim = builder.addDimension("N_PROF", 2);
    // N_PARAM: Described in the next section.
    Dimension nParamDim = builder.addDimension("N_PARAM", 4);
    // N_LEVELS: This setting is required to be the same in the core-file and bio-file. N_LEVELS in
    // the merge-file will be set to this value. (See §2.14 for the validation details.)
    Dimension nLevelsDim = builder.addDimension("N_LEVELS", 583);
    // N_HISTORY: History variables are not included in the merge-file. This dimension is included in the merge file as the UNLIMITED dimension for symmetry with the core- and bio-files but its value will always be 0.
    Dimension nHistoryDim = builder.addUnlimitedDimension("N_HISTORY");
    // N_CALIB: The N_CALIB setting in the merge-file will be the maximum of the settings in the core- file and bio-file.
    Dimension nCalibDim = builder.addDimension("N_CALIB", 1);



//    List<Dimension> dims = new ArrayList<Dimension>();
//    dims.add(latDim);
//    dims.add(lonDim);



  }

  public static class TempMergeRequest {
    private final Path coreFile;
    private final Path bioFile;

    public TempMergeRequest(Path coreFile, Path bioFile) {
      this.coreFile = coreFile;
      this.bioFile = bioFile;
    }

    public Path getCoreFile() {
      return coreFile;
    }

    public Path getBioFile() {
      return bioFile;
    }
  }
}
