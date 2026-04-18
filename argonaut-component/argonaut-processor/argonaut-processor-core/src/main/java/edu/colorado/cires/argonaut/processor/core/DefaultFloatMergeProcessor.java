package edu.colorado.cires.argonaut.processor.core;

import edu.colorado.cires.argonaut.file.core.FileStore;
import edu.colorado.cires.argonaut.messaging.core.databind.FloatMergeGroup;
import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord;
import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord.Action;
import edu.colorado.cires.argonaut.messaging.core.databind.NcSubmissionMessage.FileType;
import edu.colorado.cires.argonaut.messaging.core.queue.MessageSender;
import edu.colorado.cires.argonaut.metadata.core.DefaultIndexPageRequest;
import edu.colorado.cires.argonaut.metadata.core.IndexPageRequest;
import edu.colorado.cires.argonaut.metadata.core.MetadataRecordPage;
import edu.colorado.cires.argonaut.metadata.core.MetadataStore;
import edu.colorado.cires.argonaut.processor.core.merge.CoreProfile;
import edu.colorado.cires.argonaut.processor.core.merge.MultiFloatMergeService;
import edu.colorado.cires.argonaut.processor.core.merge.ProfileNcConsts;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import tools.jackson.databind.json.JsonMapper;
import ucar.nc2.NetcdfFile;
import ucar.nc2.NetcdfFiles;

public class DefaultFloatMergeProcessor implements FloatMergeProcessor {

  private FileStore outputFileStore;
  private Path localTempDir;
  private MetadataStore metadataStore;
  private MessageSender messageSender;
  private String updateIndexQueue;
  private JsonMapper jsonMapper;

  public void setJsonMapper(JsonMapper jsonMapper) {
    this.jsonMapper = jsonMapper;
  }

  public void setUpdateIndexQueue(String updateIndexQueue) {
    this.updateIndexQueue = updateIndexQueue;
  }

  public void setOutputFileStore(FileStore outputFileStore) {
    this.outputFileStore = outputFileStore;
  }

  public void setLocalTempDir(Path localTempDir) {
    this.localTempDir = localTempDir;
    try {
      Files.createDirectories(localTempDir);
    } catch (IOException e) {
      throw new RuntimeException("Unable to create temp directory: " + localTempDir, e);
    }
  }

  public void setMetadataStore(MetadataStore metadataStore) {
    this.metadataStore = metadataStore;
  }

  public void setMessageSender(MessageSender messageSender) {
    this.messageSender = messageSender;
  }

  private List<String> getFiles(String dac, String floatId) {
    List<String> files = new LinkedList<>();
    MetadataRecordPage page = metadataStore.findProfilePage(floatId, dac, DefaultIndexPageRequest.builder().build());
    files.addAll(page.getPage().stream().map(MetadataRecord::getFile).toList());
    Optional<IndexPageRequest> maybeNextPage = page.getNextPage();
    while (maybeNextPage.isPresent()) {
      page = metadataStore.findProfilePage(floatId, dac, maybeNextPage.get());
      files.addAll(page.getPage().stream().map(MetadataRecord::getFile).toList());
      maybeNextPage = page.getNextPage();
    }
    return files;
  }

  @Override
  public void merge(FloatMergeGroup message) {
    String dac = message.getDac();
    String floatId = message.getFloatId();

    String fileName = floatId + "_prof.nc";
    String outputFileForMetadata = outputFileStore.appendToPath(dac, floatId, fileName);
    String outputFile = outputFileStore.appendToPath(outputFileStore.getRoot(), "dac", outputFileForMetadata);

    List<String> files = getFiles(dac, floatId);
    if (!files.isEmpty()) {
      Double latitudeMin = null;
      Double latitudeMax = null;
      Double longitudeMin = null;
      Double longitudeMax = null;
      String profilerType = null;
      String institution = null;
      Set<CoreProfile> coreProfileSet = new HashSet<>();
//    List<String> associatedFiles = new ArrayList<>();
      for (String file : files) {
        String path = outputFileStore.appendToPath(outputFileStore.getRoot(), "dac", file);
//      String fileName = outputFileStore.getFileName(path);
        Path tempFile;
        try {
          tempFile = Files.createTempFile(localTempDir, "float_merge_src_", ".nc");
        } catch (IOException e) {
          throw new RuntimeException("Unable to create temp file " + path, e);
        }
        try {
          outputFileStore.downloadLocalFile(path, tempFile);
          try (NetcdfFile ncfile = NetcdfFiles.open(tempFile.toString())) {
            if (ncfile.findVariable(ProfileNcConsts.TEMP) != null) {
//            associatedFiles.add(fileName);

              double latitude = ncfile.findVariable(ProfileNcConsts.LATITUDE).read().getDouble(0);
              double longitude = ncfile.findVariable(ProfileNcConsts.LONGITUDE).read().getDouble(0);

              if (latitudeMin == null) {
                latitudeMin = latitude;
              } else if (latitude < latitudeMin) {
                latitudeMin = latitude;
              }

              if (latitudeMax == null) {
                latitudeMax = latitude;
              } else if (latitude > latitudeMax) {
                latitudeMax = latitude;
              }

              if (longitudeMin == null) {
                longitudeMin = longitude;
              } else if (longitude < longitudeMin) {
                longitudeMin = longitude;
              }

              if (longitudeMax == null) {
                longitudeMax = longitude;
              } else if (longitude > longitudeMax) {
                longitudeMax = longitude;
              }

              profilerType = ncfile.findVariable(ProfileNcConsts.WMO_INST_TYPE).read().toString();
              institution = ncfile.findVariable(ProfileNcConsts.DATA_CENTRE).read().toString();

              CoreProfile cp = new CoreProfile();
              cp.setFile(file);
              cp.setnParam(ncfile.findDimension(ProfileNcConsts.N_PARAM).getLength());
              cp.setnCalib(ncfile.findDimension(ProfileNcConsts.N_CALIB).getLength());
              cp.setnLevels(ncfile.findDimension(ProfileNcConsts.N_LEVELS).getLength());
              cp.setCycleNumber(ncfile.findVariable(ProfileNcConsts.CYCLE_NUMBER).read().getInt(0));
              cp.setJuld(ncfile.findVariable(ProfileNcConsts.JULD).read().getFloat(0));
              cp.setLatitude(latitude);
              cp.setLongitude(longitude);
              coreProfileSet.add(cp);
            }
          }
        } catch (IOException e) {
          throw new RuntimeException("Unable to merge " + path, e);
        } finally {
          FileUtils.deleteQuietly(tempFile.toFile());
        }
      }

      List<CoreProfile> sortedCoreProfiles = coreProfileSet
          .stream()
          .sorted(Comparator.comparing(CoreProfile::getCycleNumber)
              .thenComparing(CoreProfile::getJuld))
          .collect(Collectors.toList());

      Instant currentDateTime = Instant.now();

      Path mergedFile;
      try {
        mergedFile = Files.createTempFile("float_merged_", ".nc");
      } catch (IOException e) {
        throw new RuntimeException("Unable to create output merge temp file", e);
      }
      try {
        MultiFloatMergeService.mergeFloats(mergedFile, sortedCoreProfiles, currentDateTime);
        outputFileStore.uploadLocalFile(mergedFile, outputFile);
        messageSender.sendJson(
            updateIndexQueue,
            jsonMapper.writeValueAsString(MetadataRecord.builder()
                .withFile(outputFileForMetadata)
                .withAction(Action.FLOAT_MERGE)
                .withFileType(FileType.CORE_ARGO_PROFILE)
                .build()));
      } catch (IOException e) {
        throw new RuntimeException("Unable to merge " + mergedFile, e);
      } finally {
        FileUtils.deleteQuietly(mergedFile.toFile());
      }



    /*

  TODO
  private final String parameters;
  private final String parameterDataMode;
     */

      messageSender.sendJson(
          updateIndexQueue,
          jsonMapper.writeValueAsString(MetadataRecord.builder()
              .withFile(outputFileForMetadata)
              .withLongitudeMin(longitudeMin)
              .withLongitudeMax(longitudeMax)
              .withLatitudeMin(latitudeMin)
              .withLatitudeMax(latitudeMax)
              .withProfilerType(profilerType)
              .withInstitution(institution)
              .withDateUpdate(currentDateTime)
              .withAction(Action.UPDATE)
              .withFileType(FileType.PROFILE_MERGE)
              .build()));
    }


  }
}
