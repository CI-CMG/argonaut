package edu.colorado.cires.argonaut.messaging.core.databind;

import edu.colorado.cires.argonaut.messaging.core.databind.ArgoFileTypeDetails.DataMode;
import edu.colorado.cires.argonaut.messaging.core.databind.ArgoFileTypeDetails.Direction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum ArgoFileType {
  PROFILE_CORE,
  PROFILE_BIOCHEMICAL,
  SYNTHETIC_PROFILE_SINGLE_CYCLE,
  SYNTHETIC_PROFILE_MULTI_CYCLE,
  MERGED_PROFILE_SINGLE_CYCLE,
  TRAJECTORY,
  METADATA,
  TECHNICAL_DATA,
  AUXILIARY,
  PROFILE_MULTI_CYCLE;

  private static final Pattern PROFILE_MULTI_CYCLE_REGEX = Pattern.compile("([0-9]+)_prof\\.nc");
  private static final Pattern PROFILE_CORE_REGEX = Pattern.compile("([RD])([0-9]+)_([0-9]+)(D?)\\.nc");
  private static final Pattern PROFILE_BIOCHEMICAL_REGEX = Pattern.compile("B([RD])([0-9]+)_([0-9]+)(D?)\\.nc");
  private static final Pattern MERGED_PROFILE_SINGLE_CYCLE_REGEX = Pattern.compile("M([RD])([0-9]+)_([0-9]+)(D?)\\.nc");
  private static final Pattern TRAJECTORY_REGEX = Pattern.compile("([0-9]+)_([RD])traj\\.nc");
  private static final Pattern METADATA_REGEX = Pattern.compile("([0-9]+)_meta\\.nc");
  private static final Pattern TECHNICAL_DATA_REGEX = Pattern.compile("([0-9]+)_tech\\.nc");
  private static final Pattern SYNTHETIC_PROFILE_SINGLE_CYCLE_REGEX = Pattern.compile("S([RD])([0-9]+)_([0-9]+)(D?)\\.nc");
  private static final Pattern SYNTHETIC_PROFILE_MULTI_CYCLE_REGEX = Pattern.compile("([0-9]+)_Sprof\\.nc");

  public static boolean isProfile(ArgoFileType type) {
    switch (type) {
      case PROFILE_CORE:
      case PROFILE_BIOCHEMICAL:
      case SYNTHETIC_PROFILE_SINGLE_CYCLE:
      case MERGED_PROFILE_SINGLE_CYCLE:
        return true;
      default:
        return false;
    }
  }

  public static ArgoFileTypeDetails getFileNameDetails(String fileName) {

    Matcher matcher = PROFILE_CORE_REGEX.matcher(fileName);
    if (matcher.matches()) {
      String dir = matcher.group(4);
      if (dir == null || dir.isEmpty()) {
        dir = "A";
      }
      return new ArgoFileTypeDetails(ArgoFileType.PROFILE_CORE, DataMode.valueOf(matcher.group(1)), matcher.group(2), matcher.group(3),
          Direction.valueOf(dir));
    }
    matcher = PROFILE_MULTI_CYCLE_REGEX.matcher(fileName);
    if (matcher.matches()) {
      return new ArgoFileTypeDetails(ArgoFileType.PROFILE_MULTI_CYCLE, null, matcher.group(1), null, null);
    }
    matcher = PROFILE_BIOCHEMICAL_REGEX.matcher(fileName);
    if (matcher.matches()) {
      String dir = matcher.group(4);
      if (dir == null || dir.isEmpty()) {
        dir = "A";
      }
      return new ArgoFileTypeDetails(ArgoFileType.PROFILE_BIOCHEMICAL, DataMode.valueOf(matcher.group(1)), matcher.group(2), matcher.group(3),
          Direction.valueOf(dir));
    }
    matcher = MERGED_PROFILE_SINGLE_CYCLE_REGEX.matcher(fileName);
    if (matcher.matches()) {
      String dir = matcher.group(4);
      if (dir == null || dir.isEmpty()) {
        dir = "A";
      }
      return new ArgoFileTypeDetails(ArgoFileType.MERGED_PROFILE_SINGLE_CYCLE, DataMode.valueOf(matcher.group(1)), matcher.group(2), matcher.group(3),
          Direction.valueOf(dir));
    }
    matcher = TRAJECTORY_REGEX.matcher(fileName);
    if (matcher.matches()) {
      return new ArgoFileTypeDetails(ArgoFileType.TRAJECTORY, DataMode.valueOf(matcher.group(2)), matcher.group(1), null, null);
    }
    matcher = METADATA_REGEX.matcher(fileName);
    if (matcher.matches()) {
      return new ArgoFileTypeDetails(ArgoFileType.METADATA, null, matcher.group(1), null, null);
    }
    matcher = TECHNICAL_DATA_REGEX.matcher(fileName);
    if (matcher.matches()) {
      return new ArgoFileTypeDetails(ArgoFileType.TECHNICAL_DATA, null, matcher.group(1), null, null);
    }
    matcher = SYNTHETIC_PROFILE_SINGLE_CYCLE_REGEX.matcher(fileName);
    if (matcher.matches()) {
      String dir = matcher.group(4);
      if (dir == null || dir.isEmpty()) {
        dir = "A";
      }
      return new ArgoFileTypeDetails(ArgoFileType.SYNTHETIC_PROFILE_SINGLE_CYCLE, DataMode.valueOf(matcher.group(1)), matcher.group(2),
          matcher.group(3), Direction.valueOf(dir));
    }
    matcher = SYNTHETIC_PROFILE_MULTI_CYCLE_REGEX.matcher(fileName);
    if (matcher.matches()) {
      return new ArgoFileTypeDetails(ArgoFileType.SYNTHETIC_PROFILE_MULTI_CYCLE, null, matcher.group(1), null, null);
    }
    return new ArgoFileTypeDetails(ArgoFileType.AUXILIARY, null, null, null, null);
  }

  public static ArgoFileType forFileName(String fileName) {
    return getFileNameDetails(fileName).getType();
  }
}
