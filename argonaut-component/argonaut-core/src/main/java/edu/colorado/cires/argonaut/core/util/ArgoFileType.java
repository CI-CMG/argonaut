package edu.colorado.cires.argonaut.core.util;

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

  public static ArgoFileType forFileName(String fileName) {
    Matcher matcher = PROFILE_CORE_REGEX.matcher(fileName);
    if (matcher.matches()) {
      return ArgoFileType.PROFILE_CORE;
    }
    matcher = PROFILE_MULTI_CYCLE_REGEX.matcher(fileName);
    if (matcher.matches()) {
      return ArgoFileType.PROFILE_MULTI_CYCLE;
    }
    matcher = PROFILE_BIOCHEMICAL_REGEX.matcher(fileName);
    if (matcher.matches()) {
      return ArgoFileType.PROFILE_BIOCHEMICAL;
    }
    matcher = MERGED_PROFILE_SINGLE_CYCLE_REGEX.matcher(fileName);
    if (matcher.matches()) {
      return ArgoFileType.MERGED_PROFILE_SINGLE_CYCLE;
    }
    matcher = TRAJECTORY_REGEX.matcher(fileName);
    if (matcher.matches()) {
      return ArgoFileType.TRAJECTORY;
    }
    matcher = METADATA_REGEX.matcher(fileName);
    if (matcher.matches()) {
      return ArgoFileType.METADATA;
    }
    matcher = TECHNICAL_DATA_REGEX.matcher(fileName);
    if (matcher.matches()) {
      return ArgoFileType.TECHNICAL_DATA;
    }
    matcher = SYNTHETIC_PROFILE_SINGLE_CYCLE_REGEX.matcher(fileName);
    if (matcher.matches()) {
      return ArgoFileType.SYNTHETIC_PROFILE_SINGLE_CYCLE;
    }
    matcher = SYNTHETIC_PROFILE_MULTI_CYCLE_REGEX.matcher(fileName);
    if (matcher.matches()) {
      return ArgoFileType.SYNTHETIC_PROFILE_MULTI_CYCLE;
    }
    return ArgoFileType.AUXILIARY;
  }
}
