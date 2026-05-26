package edu.colorado.cires.argonaut.core.netcdf.profile.v31;

import edu.colorado.cires.argonaut.core.netcdf.profile.v31.impl.ArgoMultiProfileV31Bean;
import edu.colorado.cires.argonaut.core.netcdf.profile.v31.impl.ArgoProfileV31Bean;
import edu.colorado.cires.argonaut.core.netcdf.profile.v31.impl.ArgoProfileV31CalibrationBean;
import edu.colorado.cires.argonaut.core.netcdf.profile.v31.impl.ArgoProfileV31HistoryBean;
import edu.colorado.cires.argonaut.core.netcdf.profile.v31.impl.ArgoProfileV31HistorySoftwareBean;
import edu.colorado.cires.argonaut.core.netcdf.profile.v31.impl.ArgoProfileV31LevelBean;
import edu.colorado.cires.argonaut.core.netcdf.profile.v31.impl.ArgoProfileV31ParameterBean;
import java.util.List;

public final class ProfileCopyUtils {

  public static ArgoProfileV31Bean copyProfileToMemory(ArgoProfileV31 source) {
    return copyProfileToMemory(source, source.getProfileIndex());
  }

  public static ArgoProfileV31Bean copyProfileToMemory(ArgoProfileV31 source, int profileIndex) {
    ArgoMultiProfileV31Bean parent = new ArgoMultiProfileV31Bean();
    parent.setTitle(source.getTitle());
    parent.setInstitution(source.getInstitution());
    parent.setSource(source.getSource());
    parent.setHistory(source.getHistory());
    parent.setReferences(source.getReferences());
    parent.setId(source.getId());
    parent.setComment(source.getComment());
    parent.setUserManualVersion(source.getUserManualVersion());
    parent.setConventions(source.getConventions());
    parent.setFeatureType(source.getFeatureType());
    parent.setCommentOnResolution(source.getCommentOnResolution());
    parent.setDataType(source.getDataType());
    parent.setFormatVersion(source.getFormatVersion());
    parent.setHandbookVersion(source.getHandbookVersion());
    parent.setReferenceDateTime(source.getReferenceDateTime());
    parent.setDateCreation(source.getDateCreation());
    parent.setDateUpdate(source.getDateUpdate());

    ArgoProfileV31Bean profile = new ArgoProfileV31Bean(parent, profileIndex);
    profile.setPlatformNumber(source.getPlatformNumber());
    profile.setProjectName(source.getProjectName());
    profile.setPrincipalInvestigatorName(source.getPrincipalInvestigatorName());
    profile.setCycleNumber(source.getCycleNumber());
    profile.setDirection(source.getDirection());
    profile.setDataCenter(source.getDataCenter());
    profile.setDataCenterReference(source.getDataCenterReference());
    profile.setDataStateIndicator(source.getDataStateIndicator());
    profile.setDataMode(source.getDataMode());
    profile.setPlatformType(source.getPlatformType());
    profile.setFloatSerialNumber(source.getFloatSerialNumber());
    profile.setFirmwareVersion(source.getFirmwareVersion());
    profile.setWmoInstrumentType(source.getWmoInstrumentType());
    profile.setJulianDate(source.getJulianDate());
    profile.setJulianDateQc(source.getJulianDateQc());
    profile.setLatitude(source.getLatitude());
    profile.setLongitude(source.getLongitude());
    profile.setJulianDateOfLocation(source.getJulianDateOfLocation());
    profile.setPositionQc(source.getPositionQc());
    profile.setPositioningSystem(source.getPositioningSystem());
    profile.setPositionErrorReported(source.getPositionErrorReported());
    profile.setPositionErrorEstimated(source.getPositionErrorEstimated());
    profile.setPositionErrorEstimatedComment(source.getPositionErrorEstimatedComment());
    profile.setVerticalSamplingScheme(source.getVerticalSamplingScheme());
    profile.setConfigMissionNumber(source.getConfigMissionNumber());
    profile.setDataMode(source.getDataMode());

    List<ArgoProfileV31Parameter> sourceParameters = source.getParameters();
    for (ArgoProfileV31Parameter sourceParameter : sourceParameters) {
      ArgoProfileV31ParameterBean parameter = new ArgoProfileV31ParameterBean(profile, sourceParameter.getParameterName(),
          sourceParameter.getParameterIndex());
      profile.getParameters().add(parameter);

      parameter.setQc(sourceParameter.getQc());
      parameter.setDataMode(sourceParameter.getDataMode());

      List<ArgoProfileV31Level> levels = sourceParameter.getLevels();
      for (ArgoProfileV31Level sourceLevel : levels) {
        ArgoProfileV31LevelBean level = new ArgoProfileV31LevelBean(parameter, sourceLevel.getLevelIndex());
        parameter.getLevels().add(level);

        level.setValue(sourceLevel.getValue());
        level.setAdjustedValue(sourceLevel.getAdjustedValue());
        level.setAdjustedQc(sourceLevel.getAdjustedQc());
        level.setAdjustedErrorValue(sourceLevel.getAdjustedErrorValue());
        level.setQc(sourceLevel.getQc());
      }

      List<ArgoProfileV31Calibration> calibrations = sourceParameter.getCalibrations();
      for (ArgoProfileV31Calibration sourceCalibration : calibrations) {
        ArgoProfileV31CalibrationBean calibration = new ArgoProfileV31CalibrationBean(parameter, sourceCalibration.getCalibrationIndex());
        parameter.getCalibrations().add(calibration);

        calibration.setEquation(sourceCalibration.getEquation());
        calibration.setCoefficient(sourceCalibration.getCoefficient());
        calibration.setComment(sourceCalibration.getComment());
        calibration.setDate(sourceCalibration.getDate());
      }


    }

    List<ArgoProfileV31History> sourceHistories = source.getProfileHistory();
    for (ArgoProfileV31History sourceHistory : sourceHistories) {
      ArgoProfileV31HistoryBean history = new ArgoProfileV31HistoryBean(profile, sourceHistory.getHistoryIndex());
      profile.getProfileHistory().add(history);

      history.setInstitution(sourceHistory.getInstitution());
      history.setStep(sourceHistory.getStep());
      history.setDate(sourceHistory.getDate());
      history.setAction(sourceHistory.getAction());
      history.setParameter(sourceHistory.getParameter());
      history.setStartPressure(sourceHistory.getStartPressure());
      history.setStopPressure(sourceHistory.getStopPressure());
      history.setPreviousValue(sourceHistory.getPreviousValue());
      history.setQcTest(sourceHistory.getQcTest());

      ArgoProfileV31HistorySoftware sourceSoftware = sourceHistory.getSoftware();
      if (sourceSoftware != null) {
        history.setSoftware(
            new ArgoProfileV31HistorySoftwareBean(sourceSoftware.getName(), sourceSoftware.getRelease(), sourceSoftware.getReference()));
      }
    }
    return profile;
  }

  private ProfileCopyUtils() {

  }
}
