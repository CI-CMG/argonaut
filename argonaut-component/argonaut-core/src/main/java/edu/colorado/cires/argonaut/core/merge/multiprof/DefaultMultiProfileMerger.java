package edu.colorado.cires.argonaut.core.merge.multiprof;

import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoMultiProfileV31;
import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoProfileV31;
import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoProfileV31Parameter;
import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoProfileV31Reader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class DefaultMultiProfileMerger implements MultiProfileMerger {

  @Override
  public void mergeProfiles(List<Path> inputFiles, Path outputPath) throws IOException {
//    List<Path> orderedInputFiles = orderByCycleNumberAndDirection(inputFiles);
//    Dimensions dimensions = getDimensions(orderedInputFiles);
  }

  private static List<Path> orderByCycleNumberAndDirection(List<Path> inputFiles) {
    return inputFiles.stream().sorted((file1, file2) -> {
      try(
          ArgoProfileV31Reader reader1 = new ArgoProfileV31Reader(file1);
          ArgoProfileV31Reader reader2 = new ArgoProfileV31Reader(file2);
      ) {
        ArgoProfileV31 profile1 = reader1.getMultiProfile().getProfile(0);
        ArgoProfileV31 profile2 = reader2.getMultiProfile().getProfile(0);
        String d1 = profile1.getDirection();
        String d2 = profile2.getDirection();
        int c1 = profile1.getCycleNumber();
        int c2 = profile2.getCycleNumber();
        if (c1 == c2) {
          // D before A
          return d2.compareTo(d1);
        } else {
          return Integer.compare(c1, c2);
        }
      } catch (IOException e) {
        throw new IllegalArgumentException("Unable to read NetCDF files " + file1 + ", " + file2, e);
      }
    }).toList();
  }

  private static Dimensions getDimensions(List<Path> inputFiles) {
    Dimensions dimensions = new Dimensions();
    for (Path path : inputFiles) {
      try(
          ArgoProfileV31Reader reader = new ArgoProfileV31Reader(path);
      ) {
        ArgoMultiProfileV31 multiProfile = reader.getMultiProfile();
        int profiles = multiProfile.getNumberOfProfiles();
        if (profiles > dimensions.getProfiles()) {
          dimensions.setProfiles(profiles);
        }
        ArgoProfileV31 profile =  multiProfile.getProfile(0);
        for (ArgoProfileV31Parameter parameter : profile.getParameters()) {
          dimensions.getParameters().add(parameter.getParameterName());
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

      } catch (IOException e) {
        throw new RuntimeException("Unable to read " + path, e);
      }
    }

    return dimensions;
  }

  private static class Dimensions {
    private int profiles;
    private int levels;
    private int histories;
    private Set<String> parameters = new LinkedHashSet<>();
    private int calibrations;

    public int getProfiles() {
      return profiles;
    }

    public void setProfiles(int profiles) {
      this.profiles = profiles;
    }

    public int getLevels() {
      return levels;
    }

    public void setLevels(int levels) {
      this.levels = levels;
    }

    public int getHistories() {
      return histories;
    }

    public void setHistories(int histories) {
      this.histories = histories;
    }

    public Set<String> getParameters() {
      return parameters;
    }

    public int getCalibrations() {
      return calibrations;
    }

    public void setCalibrations(int calibrations) {
      this.calibrations = calibrations;
    }

  }

}
