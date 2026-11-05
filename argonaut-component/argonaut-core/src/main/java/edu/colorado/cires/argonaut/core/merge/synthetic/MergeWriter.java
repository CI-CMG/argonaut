package edu.colorado.cires.argonaut.core.merge.synthetic;

import edu.colorado.cires.argonaut.core.merge.synthetic.DefaultSyntheticProfileMerger.FloatQc;
import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoProfileV31;
import edu.colorado.cires.argonaut.core.netcdf.profile.v31.ArgoProfileV31Parameter;
import edu.colorado.cires.argonaut.core.netcdf.synthprofile.v13.ArgoSyntheticProfileV13Calibration;
import edu.colorado.cires.argonaut.core.netcdf.synthprofile.v13.ArgoSyntheticProfileV13Level;
import edu.colorado.cires.argonaut.core.netcdf.synthprofile.v13.ArgoSyntheticProfileV13Writer;
import edu.colorado.cires.argonaut.core.netcdf.synthprofile.v13.impl.ArgoSyntheticProfileV13Bean;
import edu.colorado.cires.argonaut.core.netcdf.synthprofile.v13.impl.ArgoSyntheticProfileV13CalibrationBean;
import edu.colorado.cires.argonaut.core.netcdf.synthprofile.v13.impl.ArgoSyntheticProfileV13LevelBean;
import edu.colorado.cires.argonaut.core.netcdf.synthprofile.v13.impl.ArgoSyntheticProfileV13ParameterBean;
import edu.colorado.cires.argonaut.core.util.SoftwareVersion;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import ucar.ma2.InvalidRangeException;

final class MergeWriter {
  private MergeWriter() {}

  public static void writeSProfileFile(
    MergeContext context,
    Map<String, Map<Long, FloatQc>> interpolatedValues,
    Path outputPath
  ) throws IOException {
    List<ArgoProfileV31> cProfiles = context.getCProfiles();
    List<ArgoProfileV31> bProfiles = context.getBProfiles();

    Instant now = Instant.now();

    ArgoSyntheticProfileV13Bean profile = new ArgoSyntheticProfileV13Bean();
    ArgoProfileV31 pressProfile = cProfiles.stream().filter(p -> p.getStationParameters().contains("PRES")).findFirst().orElseThrow();
    profile.setInstitution(context.getMetadata().getInstitution());
    profile.setCycleNumber(pressProfile.getCycleNumber());
    profile.setDataCenter(pressProfile.getDataCenter());
    profile.setDataType("Argo synthetic profile");
    profile.setFormatVersion("1.0");
    profile.setHandbookVersion("1.2");
    profile.setDateCreation(now);
    profile.setDateUpdate(now);
    profile.setPlatformNumber(pressProfile.getPlatformNumber());
    profile.setProjectName(pressProfile.getProjectName());
    profile.setPrincipalInvestigatorName(pressProfile.getPrincipalInvestigatorName());
    profile.setCycleNumber(pressProfile.getCycleNumber());
    profile.setDirection(pressProfile.getDirection());
    profile.setDataCenter(pressProfile.getDataCenter());
    profile.setPlatformType(pressProfile.getPlatformType());
    profile.setFloatSerialNumber(pressProfile.getFloatSerialNumber());
    profile.setFirmwareVersion(pressProfile.getFirmwareVersion());
    profile.setWmoInstrumentType(pressProfile.getWmoInstrumentType());
    profile.setJulianDate(pressProfile.getJulianDate());
    profile.setJulianDateQc(pressProfile.getJulianDateQc());
    profile.setJulianDateOfLocation(pressProfile.getJulianDateOfLocation());
    profile.setLongitude(pressProfile.getLongitude());
    profile.setLatitude(pressProfile.getLatitude());
    profile.setPositionQc(pressProfile.getPositionQc());
    profile.setPositioningSystem(pressProfile.getPositioningSystem());
    profile.setConfigMissionNumber(pressProfile.getConfigMissionNumber());

    for (String parameterName : context.getStationParameters()) {
      Map<Long, FloatQc> values = interpolatedValues.get(parameterName);
      ArgoSyntheticProfileV13ParameterBean param = new ArgoSyntheticProfileV13ParameterBean();
      List<ArgoSyntheticProfileV13Level> levels = new ArrayList<>();
      param.setLevels(levels);
      param.setParameterName(parameterName);
      param.setQc(getParamQc(values));
      param.setDataMode(getParameterDataMode(parameterName, cProfiles, bProfiles));

      param.setCalibrations(getCalibrations(parameterName, MergeConstants.BioParameterFilter.isSupportedParameter(parameterName) ? bProfiles : cProfiles));

      for (Map.Entry<Long, FloatQc> entry : values.entrySet()) {
        ArgoSyntheticProfileV13LevelBean levelBean = new ArgoSyntheticProfileV13LevelBean();
        FloatQc floatQc = entry.getValue();
        if (floatQc != null) {
          levelBean.setOriginalValue(floatQc.value());
          levelBean.setQc(floatQc.qc());
          levelBean.setPressureDisplacement(floatQc.pressureDisplacement());
          levelBean.setAdjustedValue(floatQc.adjustedValue());
          levelBean.setAdjustedQc(floatQc.adjustedQc());
          levelBean.setAdjustedError(floatQc.adjustedError());
        }
        levels.add(levelBean);
      }

      profile.getParameters().add(param);
    }

    Path parent = outputPath.getParent();
    if (parent != null) {
      Files.createDirectories(parent);
    }

    try {
      ArgoSyntheticProfileV13Writer.writeSingleProfile(outputPath, profile, SoftwareVersion.getVersion());
    } catch (InvalidRangeException e) {
      throw new RuntimeException(e);
    }

  }

  private static String getParamQc(Map<Long, FloatQc> values) {
    int total = 0;
    int good = 0;
    for(FloatQc floatQc : values.values()) {
      if (floatQc != null) {
        String qc = getFloatQcValue(floatQc);
        if (MergeConstants.GOOD_QC_8.contains(qc)) {
          good++;
          total++;
        } else if (MergeConstants.BAD_QC.contains(qc)) {
          total++;
        }
      }
    }

    if (total == 0) {
      return null;
    }
    if (good == 0) {
      return "F";
    }
    if (good == total) {
      return "A";
    }
    double percentGood = (double)good / (double)total;
    if (percentGood >= 0.75) {
      return "B";
    }
    if (percentGood >= 0.5) {
      return "C";
    }
    if (percentGood >= 0.25) {
      return "D";
    }
    return "E";
  }

  private static String getParameterDataMode(String parameterName, List<ArgoProfileV31> cProfiles, List<ArgoProfileV31> bProfiles) {

    for (ArgoProfileV31 profile : cProfiles) {
      for (ArgoProfileV31Parameter parameter : profile.getParameters()) {
        if (parameter.getParameterName().equals(parameterName)) {
          String parameterDataMode = parameter.getDataMode();
          if (parameterDataMode != null) {
            return parameterDataMode;
          } else {
            return profile.getDataMode();
          }
        }
      }
    }

    for (ArgoProfileV31 profile : bProfiles) {
      for (ArgoProfileV31Parameter parameter : profile.getParameters()) {
        if (parameter.getParameterName().equals(parameterName)) {
          String parameterDataMode = parameter.getDataMode();
          if (parameterDataMode != null) {
            return parameterDataMode;
          } else {
            return profile.getDataMode();
          }
        }
      }
    }
    return null;
  }

  private static List<ArgoSyntheticProfileV13Calibration> getCalibrations(String parameterName, List<ArgoProfileV31> profiles) {
    Map<Integer, List<ArgoSyntheticProfileV13Calibration>> collect = profiles.stream()
      .sorted(Comparator.comparingInt(ArgoProfileV31::getProfileIndex))
      .map(ArgoProfileV31::getParameters)
      .flatMap(Collection::stream)
      .filter(parameter -> parameter.getParameterName().equals(parameterName))
      .flatMap(parameter -> parameter.getCalibrations().stream()
        .map(
          source -> (ArgoSyntheticProfileV13Calibration) new ArgoSyntheticProfileV13CalibrationBean(
            parameterName, source.getProfileIndex(), source.getCalibrationIndex(),
            source.getEquation(),
            source.getCoefficient(), source.getComment(), source.getDate()
          ))
      ).collect(Collectors.groupingBy(ArgoSyntheticProfileV13Calibration::getProfileIndex));

    return collect.entrySet().stream()
      .sorted(Entry.comparingByKey())
      .map(Entry::getValue)
      .findFirst()
      .orElse(Collections.emptyList());
  }

  private static String getFloatQcValue(FloatQc floatQc) {
    String qc = floatQc.adjustedQc();
    if (qc == null) {
      qc = floatQc.qc();
    }

    return qc;
  }
}
