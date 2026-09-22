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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ucar.ma2.InvalidRangeException;


public class DefaultMultiProfileMerger implements MultiProfileMerger {

  private static final Logger LOGGER = LoggerFactory.getLogger(DefaultMultiProfileMerger.class);

  private static final Pattern FILE_NAME_REGEX = Pattern.compile("[A-Z]*([0-9]+)_([0-9]+)(D?)\\.nc");

  private final String institution;
  private final boolean readOnlyFirstProfileInFile;

  public DefaultMultiProfileMerger(String institution, boolean readOnlyFirstProfileInFile) {
    this.institution = institution;
    this.readOnlyFirstProfileInFile = readOnlyFirstProfileInFile;
  }

  @Override
  public void mergeProfiles(List<LocalPathSupplier> orderedInputFileSuppliers,  List<String> validParameterNames, Path outputPath) throws IOException {
    Path parent = outputPath.getParent();
    if (parent != null) {
      Files.createDirectories(parent);
    }
    Set<String> parameterNames = new LinkedHashSet<>();
    SimpleArgoNetCdfDimensions dimensions = new SimpleArgoNetCdfDimensions();

    for (LocalPathSupplier supplier : orderedInputFileSuppliers) {
      supplier.prepare();
      try {
        Path path = supplier.getLocalPath();
        populateDimensionsAndParameters(path, dimensions, parameterNames);
      } finally {
        try {
          supplier.cleanUp();
        } catch (Exception e) {
          LOGGER.warn("An error occurred when cleaning up profile merge source  " + supplier.getFileName(), e);
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
    try (MultiProfileIterator iterator = new MultiProfileIterator(orderedInputFileSuppliers, readOnlyFirstProfileInFile)) {
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

  public static List<LocalPathSupplier> orderByCycleNumberAndDirection(List<LocalPathSupplier> inputFileSuppliers) {
    // use file name, rather than reading data as an optimization when files are not located
    // on the same file system, like S3
    return inputFileSuppliers.stream().sorted((lps1, lps2) -> {
      if (lps1.getDac().equals(lps2.getDac())) {
        String file1 = lps1.getFileName();
        String file2 = lps2.getFileName();
        Matcher matcher1 = FILE_NAME_REGEX.matcher(file1);
        if (!matcher1.matches()) {
          throw new IllegalArgumentException("Invalid file name: " + file1);
        }
        Matcher matcher2 = FILE_NAME_REGEX.matcher(file2);
        if (!matcher2.matches()) {
          throw new IllegalArgumentException("Invalid file name: " + file2);
        }
        long floatId1 = Long.parseLong(matcher1.group(1));
        long floatId2 = Long.parseLong(matcher2.group(1));
        if (floatId1 == floatId2) {
          String d1 = matcher1.group(3).isEmpty() ? "A" : "D";
          String d2 = matcher2.group(3).isEmpty() ? "A" : "D";
          int c1 = Integer.parseInt(matcher1.group(2));
          int c2 = Integer.parseInt(matcher2.group(2));
          if (c1 == c2) {
            // D before A
            return d2.compareTo(d1);
          } else {
            return Integer.compare(c1, c2);
          }
        } else {
          return Long.compare(floatId1, floatId2);
        }
      } else {
        return lps1.getDac().compareTo(lps2.getDac());
      }

    }).toList();
  }

  public static List<LocalPathSupplier> orderByJulianDateDescending(List<LocalPathSupplier> inputFileSuppliers) {
    return inputFileSuppliers.stream().sorted((lps1, lps2) -> {
      if (lps1.getJulD().equals(lps2.getJulD())) {
        String file1 = lps1.getFileName();
        String file2 = lps2.getFileName();
        Matcher matcher1 = FILE_NAME_REGEX.matcher(file1);
        if (!matcher1.matches()) {
          throw new IllegalArgumentException("Invalid file name: " + file1);
        }
        Matcher matcher2 = FILE_NAME_REGEX.matcher(file2);
        if (!matcher2.matches()) {
          throw new IllegalArgumentException("Invalid file name: " + file2);
        }
        long floatId1 = Long.parseLong(matcher1.group(1));
        long floatId2 = Long.parseLong(matcher2.group(1));
        if (floatId1 == floatId2) {
          String d1 = matcher1.group(3).isEmpty() ? "A" : "D";
          String d2 = matcher2.group(3).isEmpty() ? "A" : "D";
          int c1 = Integer.parseInt(matcher1.group(2));
          int c2 = Integer.parseInt(matcher2.group(2));
          if (c1 == c2) {
            // D before A
            return d2.compareTo(d1);
          } else {
            return Integer.compare(c1, c2);
          }
        } else {
          return Long.compare(floatId1, floatId2);
        }
      } else {
        return lps2.getJulD().compareTo(lps1.getJulD());
      }

    }).toList();
  }


  public static List<LocalPathSupplier> orderByCycleThenJulD(List<LocalPathSupplier> inputFileSuppliers) {
    return inputFileSuppliers.stream().sorted((lps1, lps2) -> {
      String file1 = lps1.getFileName();
      String file2 = lps2.getFileName();
      Matcher matcher1 = FILE_NAME_REGEX.matcher(file1);
      if (!matcher1.matches()) {
        throw new IllegalArgumentException("Invalid file name: " + file1);
      }
      Matcher matcher2 = FILE_NAME_REGEX.matcher(file2);
      if (!matcher2.matches()) {
        throw new IllegalArgumentException("Invalid file name: " + file2);
      }
      long floatId1 = Long.parseLong(matcher1.group(1));
      long floatId2 = Long.parseLong(matcher2.group(1));
      int c1 = Integer.parseInt(matcher1.group(2));
      int c2 = Integer.parseInt(matcher2.group(2));
      String d1 = matcher1.group(3).isEmpty() ? "A" : "D";
      String d2 = matcher2.group(3).isEmpty() ? "A" : "D";

      if (c1 == c2) {
        if (lps1.getJulD().equals(lps2.getJulD())) {
          if (d2.equals(d1)) {
            return Long.compare(floatId1, floatId2);
          } else {
            // D before A
            return d2.compareTo(d1);
          }
        } else {
          return lps1.getJulD().compareTo(lps2.getJulD());
        }
      } else {
        return Integer.compare(c1, c2);
      }
    }).toList();
  }

  private void populateDimensionsAndParameters(Path path, SimpleArgoNetCdfDimensions dimensions, Set<String> parameterNames) {
    try (
        ArgoProfileV31Reader reader = new ArgoProfileV31Reader(path);
    ) {
      ArgoMultiProfileV31 multiProfile = reader.getMultiProfile();
      int numProfiles = multiProfile.getNumberOfProfiles();
      if (readOnlyFirstProfileInFile) {
        numProfiles = 1;
      }
      dimensions.setProfiles(dimensions.getProfiles() + numProfiles);
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
