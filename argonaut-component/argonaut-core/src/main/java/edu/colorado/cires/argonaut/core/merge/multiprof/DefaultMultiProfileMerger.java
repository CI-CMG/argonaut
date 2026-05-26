package edu.colorado.cires.argonaut.core.merge.multiprof;

import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoMultiProfileV31;
import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoProfileV31;
import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoProfileV31Parameter;
import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoProfileV31Reader;
import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoProfileV31Writer;
import edu.colorado.cires.argonaut.core.util.SimpleArgoNetCdfDimensions;
import edu.colorado.cires.argonaut.core.util.SoftwareVersion;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ucar.ma2.InvalidRangeException;


public class DefaultMultiProfileMerger implements MultiProfileMerger {

  private static final Logger LOGGER = LoggerFactory.getLogger(DefaultMultiProfileMerger.class);

  private static final Pattern FILE_NAME_REGEX = Pattern.compile("[A-Z]*[0-9]+_([0-9]+)(D?)\\.nc");

  private final String institution;

  public DefaultMultiProfileMerger(String institution) {
    this.institution = institution;
  }

  @Override
  public void mergeProfiles(List<LocalPathSupplier> inputFileSuppliers,  List<String> validParameterNames, Path outputPath) throws IOException {
    Path parent = outputPath.getParent();
    if (parent != null) {
      Files.createDirectories(parent);
    }
    Map<String, LocalPathSupplier> pathSuppliers = new HashMap<>();
    for (LocalPathSupplier supplier : inputFileSuppliers) {
      pathSuppliers.put(supplier.getFileName(), supplier);
    }
    List<String> orderedKeys = orderByCycleNumberAndDirection(inputFileSuppliers);

    Set<String> parameterNames = new LinkedHashSet<>();
    SimpleArgoNetCdfDimensions dimensions = new SimpleArgoNetCdfDimensions();

    List<LocalPathSupplier> orderedInputFileSuppliers = new ArrayList<>(inputFileSuppliers.size());
    for (String orderedKey : orderedKeys) {
      LocalPathSupplier supplier = pathSuppliers.get(orderedKey);
      orderedInputFileSuppliers.add(supplier);
      supplier.prepare();
      try {
        Path path = supplier.getLocalPath();
        populateDimensionsAndParameters(path, dimensions, parameterNames);
      } finally {
        try {
          supplier.cleanUp();
        } catch (Exception e) {
          LOGGER.warn("An error occurred when cleaning up profile merge source  " + orderedKey, e);
        }
      }
    }
    // Override history dimension.  Histories are not merged.
    dimensions.setHistories(0);
    List<String> filteredParameterNames = new ArrayList<>(parameterNames.size());
    for (String parameterName : parameterNames) {
      if (validParameterNames == null || validParameterNames.contains(parameterName)) {
        filteredParameterNames.add(parameterName);
      }
    }
    try (MultiProfileIterator iterator = new MultiProfileIterator(orderedInputFileSuppliers)) {
      ArgoProfileV31Writer.writeMultiProfile(
          outputPath,
          SoftwareVersion.getVersion(),
          institution,
          filteredParameterNames,
          dimensions,
          iterator
      );
    } catch (InvalidRangeException e) {
      throw new RuntimeException("Unable to create multi-profile merge file " + outputPath, e);
    }

  }

  private static List<String> orderByCycleNumberAndDirection(List<LocalPathSupplier> inputFileSuppliers) {
    // use file name, rather than reading data as an optimization when files are not located
    // on the same file system, like S3
    return inputFileSuppliers.stream().map(LocalPathSupplier::getFileName).sorted((file1, file2) -> {
      Matcher matcher1 = FILE_NAME_REGEX.matcher(file1);
      if (!matcher1.matches()) {
        throw new IllegalArgumentException("Invalid file name: " + file1);
      }
      Matcher matcher2 = FILE_NAME_REGEX.matcher(file2);
      if (!matcher2.matches()) {
        throw new IllegalArgumentException("Invalid file name: " + file2);
      }
      String d1 = matcher1.group(2).isEmpty() ? "A" : "D";
      String d2 = matcher2.group(2).isEmpty() ? "A" : "D";
      int c1 = Integer.parseInt(matcher1.group(1));
      int c2 = Integer.parseInt(matcher2.group(1));
      if (c1 == c2) {
        // D before A
        return d2.compareTo(d1);
      } else {
        return Integer.compare(c1, c2);
      }
    }).toList();

//    return inputFiles.stream().sorted((file1, file2) -> {
//      try(
//          ArgoProfileV31Reader reader1 = new ArgoProfileV31Reader(file1);
//          ArgoProfileV31Reader reader2 = new ArgoProfileV31Reader(file2);
//      ) {
//        ArgoProfileV31 profile1 = reader1.getMultiProfile().getProfile(0);
//        ArgoProfileV31 profile2 = reader2.getMultiProfile().getProfile(0);
//        String d1 = profile1.getDirection();
//        String d2 = profile2.getDirection();
//        int c1 = profile1.getCycleNumber();
//        int c2 = profile2.getCycleNumber();
//        if (c1 == c2) {
//          // D before A
//          return d2.compareTo(d1);
//        } else {
//          return Integer.compare(c1, c2);
//        }
//      } catch (IOException e) {
//        throw new IllegalArgumentException("Unable to read NetCDF files " + file1 + ", " + file2, e);
//      }
//    }).toList();
  }

  private static void populateDimensionsAndParameters(Path path, SimpleArgoNetCdfDimensions dimensions, Set<String> parameterNames) {
    try (
        ArgoProfileV31Reader reader = new ArgoProfileV31Reader(path);
    ) {
      ArgoMultiProfileV31 multiProfile = reader.getMultiProfile();
      dimensions.setProfiles(dimensions.getProfiles() + multiProfile.getNumberOfProfiles());
      ArgoProfileV31 profile = multiProfile.getProfile(0);
      for (ArgoProfileV31Parameter parameter : profile.getParameters()) {
        parameterNames.add(parameter.getParameterName());
        int levels = parameter.getLevels().size();
        if (levels > dimensions.getLevels()) {
          dimensions.setLevels(levels);
        }
        int calibrations = parameter.getCalibrations().size();
        if (calibrations > dimensions.getCalibrations()) {
          dimensions.setCalibrations(calibrations);
        }
      }
      int histories = profile.getProfileHistory().size();
      if (histories > dimensions.getHistories()) {
        dimensions.setHistories(histories);
      }
      dimensions.setParameters(parameterNames.size());
    } catch (IOException e) {
      throw new RuntimeException("Unable to read " + path, e);
    }

  }


}
