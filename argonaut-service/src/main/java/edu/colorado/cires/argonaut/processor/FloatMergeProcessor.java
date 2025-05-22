package edu.colorado.cires.argonaut.processor;

import edu.colorado.cires.argonaut.config.ServiceProperties;
import edu.colorado.cires.argonaut.message.NcSubmissionMessage;
import edu.colorado.cires.argonaut.service.merge.CoreProfile;
import edu.colorado.cires.argonaut.service.merge.MultiFloatMergeService;
import edu.colorado.cires.argonaut.service.merge.ProfileNcConsts;
import edu.colorado.cires.argonaut.util.ArgonautFileUtils;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ucar.nc2.NetcdfFile;
import ucar.nc2.NetcdfFiles;

@Component
public class FloatMergeProcessor implements Processor {
  private static final Logger LOGGER = LoggerFactory.getLogger(FloatMergeProcessor.class);

  private final Path workingDir;
  private final Path processingDir;
  private final ServiceProperties serviceProperties;
  MultiFloatMergeService floatMergeService;

  @Autowired
  public FloatMergeProcessor(ServiceProperties serviceProperties) {
    this.serviceProperties = serviceProperties;
    workingDir = serviceProperties.getWorkDirectory();
    processingDir = ArgonautFileUtils.getProcessingDir(serviceProperties);
    floatMergeService = new MultiFloatMergeService();
  }

  @Override
  public void process(Exchange exchange) throws Exception {
      NcSubmissionMessage message = exchange.getIn().getBody(NcSubmissionMessage.class);
      String dac = message.getDac();
      String floatId = message.getFloatId();
      Path profilesDir = ArgonautFileUtils.getOutputProfileDir(serviceProperties,dac, floatId, true);
      Set<CoreProfile> coreProfileSet = new HashSet<>();
      try(DirectoryStream<Path> stream = Files.newDirectoryStream(profilesDir)) {
        for (Path path : stream) {
          if (!Files.isDirectory(path)) {
            try (NetcdfFile ncfile = NetcdfFiles.open(path.toString())) {
              if (ncfile.findVariable(ProfileNcConsts.TEMP) != null){
                CoreProfile cp = new CoreProfile();
                cp.setFile(path);
                cp.setnParam(ncfile.findDimension(ProfileNcConsts.N_PARAM).getLength());
                cp.setnCalib(ncfile.findDimension(ProfileNcConsts.N_CALIB).getLength());
                cp.setnLevels(ncfile.findDimension(ProfileNcConsts.N_LEVELS).getLength());
                cp.setCycleNumber(ncfile.findVariable(ProfileNcConsts.CYCLE_NUMBER).read().getInt(0));
                cp.setJuld(ncfile.findVariable(ProfileNcConsts.JULD).read().getFloat(0));
                cp.setLatitude(ncfile.findVariable(ProfileNcConsts.LATITUDE).read().getDouble(0));
                cp.setLongitude(ncfile.findVariable(ProfileNcConsts.LONGITUDE).read().getDouble(0));
                coreProfileSet.add(cp);
              }
            } catch (IOException e) {
              throw new RuntimeException("An error opening core profile file : " + path, e);
            }
          }
        }
      }
      List<CoreProfile> sortedCoreProfiles = coreProfileSet
          .stream()
          .sorted(Comparator.comparing(CoreProfile::getCycleNumber)
              .thenComparing(CoreProfile::getJuld))
          .collect(Collectors.toList());

      floatMergeService.mergeFloats(ArgonautFileUtils.getOutputProfileDir(serviceProperties,dac, floatId, false), sortedCoreProfiles);

  }
}
