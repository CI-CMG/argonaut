package edu.colorado.cires.argonaut.core.netcdf.profile.v31;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

public class ArgoProfileV31ReaderTest {

  @Test
  public void testRead() throws Exception {
    Path path = Paths.get("../../test-data/dac/aoml/1900722/profiles/D1900722_010.nc");
    try (ArgoProfileV31Reader reader = new ArgoProfileV31Reader(path)) {
      ArgoMultiProfileV31 profile = reader.getMultiProfile();
      assertEquals("Argo float vertical profile", profile.getTitle());
      assertEquals("AOML", profile.getInstitution());
      assertEquals("2012-05-20 AOML 2.2 creation; 2015-05-29T19:36:23Z UW 3.1 conversion", profile.getHistory());
      assertEquals("http://www.argodatamgt.org/Documentation", profile.getReferences());
      assertNull(profile.getId());
      assertEquals("free text", profile.getComment());
      assertEquals("3.1", profile.getUserManualVersion());
      assertEquals("Argo-3.1 CF-1.6", profile.getConventions());
      assertEquals("trajectoryProfile", profile.getFeatureType());
      assertEquals("Argo float", profile.getSource());
      assertNull(profile.getCommentOnResolution());
      ArgoProfileV31 p0 = profile.getProfile(0);
      assertEquals("Argo float vertical profile", p0.getTitle());
      assertEquals("AOML", p0.getInstitution());
      assertEquals("2012-05-20 AOML 2.2 creation; 2015-05-29T19:36:23Z UW 3.1 conversion", p0.getHistory());
      assertEquals("http://www.argodatamgt.org/Documentation", p0.getReferences());
      assertNull(p0.getId());
      assertEquals("free text", p0.getComment());
      assertEquals("3.1", p0.getUserManualVersion());
      assertEquals("Argo-3.1 CF-1.6", p0.getConventions());
      assertEquals("trajectoryProfile", p0.getFeatureType());
      assertEquals("Argo float", p0.getSource());
      assertNull(p0.getCommentOnResolution());
      assertEquals("1900722", p0.getPlatformNumber());
      assertEquals("US ARGO PROJECT", p0.getProjectName());
      assertEquals("STEPHEN RISER", p0.getPrincipalInvestigatorName());
      assertEquals(Arrays.asList("PRES", "TEMP", "PSAL"), p0.getStationParameters());
      assertEquals(10, p0.getCycleNumber());
      assertEquals(ArgoProfileDirection.A, p0.getDirection());
      assertEquals("AO", p0.getDataCenter());
      assertEquals("1726_17964_010", p0.getDataCenterReference());
      assertEquals("2C", p0.getDataStateIndicator());
      assertEquals(ArgoProfileDataMode.D, p0.getDataMode());
      assertEquals("APEX", p0.getPlatformType());
      assertEquals("2596", p0.getFloatSerialNumber());
      assertEquals("012606", p0.getFirmwareVersion());
      assertEquals("846", p0.getWmoInstrumentType());
      assertEquals(Instant.parse("2007-01-21T04:22:15Z"), p0.getJulianDate());
      assertEquals("1", p0.getJulianDateQc());
      assertEquals(Instant.parse("2007-01-21T18:48:33.002Z"), p0.getJulianDateOfLocation());
      assertEquals(-38.507, p0.getLatitude(), 0.001);
      assertEquals(76.029, p0.getLongitude(), 0.001);
      assertEquals("1", p0.getPositionQc());
      assertEquals("ARGOS", p0.getPositioningSystem());
      assertNull(p0.getPositionErrorReported());
      assertNull(p0.getPositionErrorEstimatedComment());
      assertEquals("Primary sampling: discrete []", p0.getVerticalSamplingScheme());
      assertEquals(1, p0.getConfigMissionNumber());
      List<ArgoProfileV31History> history = p0.getProfileHistory();
      assertEquals(4, history.size());
      ArgoProfileV31History h0 = history.get(0);
      assertEquals("AO", h0.getInstitution());
      assertEquals("ARGQ", h0.getStep());
      assertNull(h0.getSoftware().getName());
      assertNull(h0.getSoftware().getReference());
      assertNull(h0.getSoftware().getRelease());
      assertEquals(Instant.parse("2012-05-20T12:27:39.000Z"), h0.getDate());
      assertEquals("QCP$", h0.getAction());
      assertNull(h0.getParameter());
      assertNull(h0.getStartPressure());
      assertNull(h0.getStopPressure());
      assertNull(h0.getPreviousValue());
      assertEquals("FFBFE", h0.getQcTest());

      ArgoProfileV31History h1 = history.get(1);
      assertEquals("AO", h1.getInstitution());
      assertEquals("ARGQ", h1.getStep());
      assertNull(h1.getSoftware().getName());
      assertNull(h1.getSoftware().getReference());
      assertNull(h1.getSoftware().getRelease());
      assertEquals(Instant.parse("2012-05-20T12:27:39.000Z"), h1.getDate());
      assertEquals("QCF$", h1.getAction());
      assertNull(h1.getParameter());
      assertNull(h1.getStartPressure());
      assertNull(h1.getStopPressure());
      assertNull(h1.getPreviousValue());
      assertEquals("0", h1.getQcTest());

      ArgoProfileV31History h2 = history.get(2);
      assertEquals("AO", h2.getInstitution());
      assertEquals("ARCA", h2.getStep());
      assertEquals("ADJP", h2.getSoftware().getName());
      assertNull(h2.getSoftware().getReference());
      assertNull(h2.getSoftware().getRelease());
      assertEquals(Instant.parse("2012-05-20T12:27:39.000Z"), h2.getDate());
      assertEquals("IP", h2.getAction());
      assertNull(h2.getParameter());
      assertNull(h2.getStartPressure());
      assertNull(h2.getStopPressure());
      assertNull(h2.getPreviousValue());
      assertNull(h2.getQcTest());

      ArgoProfileV31History h3 = history.get(3);
      assertEquals("UW", h3.getInstitution());
      assertEquals("ARSQ", h3.getStep());
      assertEquals("UWQC", h3.getSoftware().getName());
      assertEquals("WOD & nearby Argo as visual check", h3.getSoftware().getReference());
      assertNull(h3.getSoftware().getRelease());
      assertEquals(Instant.parse("2012-07-05T14:07:57.000Z"), h3.getDate());
      assertEquals("IP", h3.getAction());
      assertNull(h3.getParameter());
      assertNull(h3.getStartPressure());
      assertNull(h3.getStopPressure());
      assertNull(h3.getPreviousValue());
      assertNull(h3.getQcTest());

      List<ArgoProfileV31Parameter> parameters = p0.getParameters();
      assertEquals(3, parameters.size());
      ArgoProfileV31Parameter pres = parameters.get(0);
      assertEquals("PRES", pres.getParameterName());
      assertEquals("A", pres.getQc());

      List<ArgoProfileV31Level> presLevels = pres.getLevels();
      assertEquals(70, presLevels.size());
      assertEquals("1", presLevels.get(3).getAdjustedQc());
      assertEquals("1", presLevels.get(3).getQc());
      assertEquals("PRES", presLevels.get(3).getParameterName());
      assertEquals(3, presLevels.get(3).getLevelIndex());
      assertEquals(0, presLevels.get(3).getProfileIndex());
      assertEquals(30.3f, presLevels.get(3).getValue(), 0.001f);
      assertEquals(2.4, presLevels.get(3).getAdjustedErrorValue(), 0.001f);
      assertEquals(30.2f, presLevels.get(3).getAdjustedValue(), 0.001f);

      List<ArgoProfileV31Calibration> presCalibs = pres.getCalibrations();
      assertEquals(1, presCalibs.size());
      assertEquals("dP =0.1 dbar.", presCalibs.get(0).getCoefficient());
      assertEquals("Pressures adjusted by using pressure offset at the sea surface. The quoted error is manufacturer specified accuracy in dbar.", presCalibs.get(0).getComment());
      assertEquals("PRES", presCalibs.get(0).getParameterName());
      assertEquals(0, presCalibs.get(0).getProfileIndex());
      assertEquals("PRES_ADJUSTED = PRES - dP", presCalibs.get(0).getEquation());
      assertEquals(0, presCalibs.get(0).getCalibrationIndex());
      assertEquals(Instant.parse("2012-07-05T14:07:57.000Z"), presCalibs.get(0).getDate());



      ArgoProfileV31Parameter temp = parameters.get(1);
      assertEquals("TEMP", temp.getParameterName());
      assertEquals("A", temp.getQc());

      List<ArgoProfileV31Level> tempLevels = temp.getLevels();
      assertEquals(70, tempLevels.size());
      assertEquals("1", tempLevels.get(3).getAdjustedQc());
      assertEquals("1", tempLevels.get(3).getQc());
      assertEquals("TEMP", tempLevels.get(3).getParameterName());
      assertEquals(3, tempLevels.get(3).getLevelIndex());
      assertEquals(0, tempLevels.get(3).getProfileIndex());
      assertEquals(16.154f, tempLevels.get(3).getValue(), 0.001f);
      assertEquals(0.002f, tempLevels.get(3).getAdjustedErrorValue(), 0.001f);
      assertEquals(16.154f, tempLevels.get(3).getAdjustedValue(), 0.001f);

      List<ArgoProfileV31Calibration> tempCalibs = temp.getCalibrations();
      assertEquals(1, tempCalibs.size());
      assertEquals("none", tempCalibs.get(0).getCoefficient());
      assertEquals("The quoted error is manufacturer specified accuracy with respect to ITS-90 at time of laboratory calibration.", tempCalibs.get(0).getComment());
      assertEquals("TEMP", tempCalibs.get(0).getParameterName());
      assertEquals(0, tempCalibs.get(0).getProfileIndex());
      assertEquals("none", tempCalibs.get(0).getEquation());
      assertEquals(0, tempCalibs.get(0).getCalibrationIndex());
      assertEquals(Instant.parse("2012-07-05T14:07:57.000Z"), tempCalibs.get(0).getDate());

      ArgoProfileV31Parameter psal = parameters.get(2);
      assertEquals("PSAL", psal.getParameterName());
      assertEquals("A", psal.getQc());

      List<ArgoProfileV31Level> psalLevels = psal.getLevels();
      assertEquals(70, psalLevels.size());
      assertEquals("1", psalLevels.get(3).getAdjustedQc());
      assertEquals("1", psalLevels.get(3).getQc());
      assertEquals("PSAL", psalLevels.get(3).getParameterName());
      assertEquals(3, psalLevels.get(3).getLevelIndex());
      assertEquals(0, psalLevels.get(3).getProfileIndex());
      assertEquals(35.087f, psalLevels.get(3).getValue(), 0.001f);
      assertEquals(0.01f, psalLevels.get(3).getAdjustedErrorValue(), 0.001f);
      assertEquals(35.09f, psalLevels.get(3).getAdjustedValue(), 0.001f);

      List<ArgoProfileV31Calibration> psalCalibs = psal.getCalibrations();
      assertEquals(1, psalCalibs.size());
      assertEquals("CTM: alpha=0.0267, tau=18.6s, mean_ascent_rate = 0.09 dbar/s", psalCalibs.get(0).getCoefficient());
      assertEquals("No significant salinity drift detected. Salinity adjusted for effects of pressure adjustment. Conductivity cell thermal mass correction (CTM) applied. The quoted error is max[0.01, sqrt(OWerr^2+CTMerr^2)] in PSS-78.", psalCalibs.get(0).getComment());
      assertEquals("PSAL", psalCalibs.get(0).getParameterName());
      assertEquals(0, psalCalibs.get(0).getProfileIndex());
      assertEquals("PSAL_ADJUSTED = sw_salt( sw_cndr(PSAL,TEMP,PRES), TEMP, PRES_ADJUSTED ) + CTM", psalCalibs.get(0).getEquation());
      assertEquals(0, psalCalibs.get(0).getCalibrationIndex());
      assertEquals(Instant.parse("2012-07-05T14:07:57.000Z"), psalCalibs.get(0).getDate());
    }
  }
}