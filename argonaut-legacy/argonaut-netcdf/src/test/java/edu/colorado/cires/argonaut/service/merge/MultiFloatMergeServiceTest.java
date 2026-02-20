package edu.colorado.cires.argonaut.service.merge;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import ucar.nc2.NetcdfFile;
import ucar.nc2.NetcdfFiles;
import ucar.nc2.Variable;

@Disabled
class MultiFloatMergeServiceTest {

  private static final Path submissionDir = Paths.get("target/submission");

  @BeforeEach
  public void before() throws Exception {
    FileUtils.deleteQuietly(submissionDir.toFile());
    Files.createDirectories(submissionDir);
  }

  @AfterEach
  public void after() throws Exception {
   FileUtils.deleteQuietly(submissionDir.toFile());
  }

  @Test
  public void smokeTest() throws Exception {
    String[] files = new String[]{"D2901615_001.nc", "D2901615_002.nc", "D2901615_003.nc","D2901615_004.nc"};

    Path daceDir = submissionDir.resolve("2901615");
    Path profileDir = daceDir.resolve("profiles");
    Path resourceDir = Paths.get("src/test/resources/float_merge/nmdis/2901615/profiles");

    Files.createDirectories(profileDir);
    List<CoreProfile> profileNcFiles = new ArrayList<>();

    for (String fileName: files) {
      CoreProfile coreProfile = new CoreProfile();
      Path ncFile = profileDir.resolve(fileName);
      coreProfile.setFile(ncFile);
      profileNcFiles.add(coreProfile);
      Files.copy(resourceDir.resolve(fileName), ncFile);
      try (NetcdfFile ncfile = NetcdfFiles.open(ncFile.toString())) {
        coreProfile.setCycleNumber(ncfile.findVariable(ProfileNcConsts.CYCLE_NUMBER).read().getInt(0));
        coreProfile.setnParam(ncfile.findDimension(ProfileNcConsts.N_PARAM).getLength());
        coreProfile.setnCalib(ncfile.findDimension(ProfileNcConsts.N_CALIB).getLength());
        coreProfile.setnLevels(ncfile.findDimension(ProfileNcConsts.N_LEVELS).getLength());
        coreProfile.setJuld(ncfile.findVariable(ProfileNcConsts.JULD).read().getDouble(0));
        coreProfile.setLatitude(ncfile.findVariable(ProfileNcConsts.LATITUDE).read().getDouble(0));
        coreProfile.setLongitude(ncfile.findVariable(ProfileNcConsts.LONGITUDE).read().getDouble(0));
      } catch (IOException e) {
        throw new RuntimeException("An error opening profile nc file : " + ncFile, e);
      }
    }

    Path outputFile = daceDir.resolve("2901615_prof.nc");
    MultiFloatMergeService floatMergeService = new MultiFloatMergeService();
    floatMergeService.mergeFloats(outputFile, profileNcFiles);

    assertTrue(Files.exists(outputFile));
    try (NetcdfFile ncfile = NetcdfFiles.open(outputFile.toString())) {

      assertEquals("Argo profile    ", ncfile.findVariable(ProfileNcConsts.DATA_TYPE).read().toString());
      assertEquals("3.1", ncfile.findVariable(ProfileNcConsts.FORMAT_VERSION).read().toString());
      assertEquals("1.2", ncfile.findVariable(ProfileNcConsts.HANDBOOK_VERSION).read().toString());

      Variable cycleNumbers = ncfile.findVariable("CYCLE_NUMBER");
      assertTrue(Arrays.equals(new int[]{4}, cycleNumbers.getShape()));
      assertEquals(1, cycleNumbers.read().getInt(0));

      Variable platformNumber = ncfile.findVariable(ProfileNcConsts.PLATFORM_NUMBER);
      assertTrue(Arrays.equals(new int[]{4, ProfileNcConsts.STRING8}, platformNumber.getShape()));
      assertEquals("2901615 ,2901615 ,2901615 ,2901615 ", platformNumber.read().toString());
      assertEquals( "Argo China SOA                                                  ", ncfile.findVariable("PROJECT_NAME").read("0,:").toString());
      assertEquals("Fengying JI                                                     ", ncfile.findVariable("PI_NAME").read("1,:").toString());
      assertEquals("PRES            ,TEMP            ,PSAL            ", ncfile.findVariable(ProfileNcConsts.STATION_PARAMETERS).read("0,:,:").toString());
      assertEquals("AAAA", ncfile.findVariable(ProfileNcConsts.DIRECTION).read().toString());
      assertEquals("NM,NM,NM,NM", ncfile.findVariable(ProfileNcConsts.DATA_CENTRE).read().toString());
      assertEquals("                                ,                                ,                                ,                                ", ncfile.findVariable(ProfileNcConsts.DC_REFERENCE).read().toString());
      assertEquals("2C  ,2C  ,2C  ,2C  ", ncfile.findVariable(ProfileNcConsts.DATA_STATE_INDICATOR).read().toString());
      assertEquals("DDDD", ncfile.findVariable(ProfileNcConsts.DATA_MODE).read().toString());
      assertEquals("PROVOR                          ", ncfile.findVariable(ProfileNcConsts.PLATFORM_TYPE).read("0,:").toString());
      assertEquals("OIN-08CH-S3-016                 ", ncfile.findVariable(ProfileNcConsts.FLOAT_SERIAL_NO).read("0,:").toString());
      assertEquals("4,84                            ", ncfile.findVariable(ProfileNcConsts.FIRMWARE_VERSION).read("0,:").toString());
      assertEquals("841 ,841 ,841 ,841 ", ncfile.findVariable(ProfileNcConsts.WMO_INST_TYPE).read().toString());
      assertEquals(22471.19093749998, ncfile.findVariable(ProfileNcConsts.JULD).read().getDouble(0));
      assertEquals("1111", ncfile.findVariable(ProfileNcConsts.JULD_QC).read().toString());
      assertEquals(28.899, ncfile.findVariable(ProfileNcConsts.LATITUDE).read().getDouble(2));
      assertEquals(140.666, ncfile.findVariable(ProfileNcConsts.LONGITUDE).read().getDouble(2));
      assertEquals("1111", ncfile.findVariable(ProfileNcConsts.POSITION_QC).read().toString());
      assertEquals("ARGOS   ,ARGOS   ,ARGOS   ,ARGOS   ", ncfile.findVariable(ProfileNcConsts.POSITIONING_SYSTEM).read().toString());
      assertEquals("AAAA", ncfile.findVariable(ProfileNcConsts.PROFILE_PRES_QC).read().toString());
      assertEquals("AAAA", ncfile.findVariable(ProfileNcConsts.PROFILE_TEMP_QC).read().toString());
      assertEquals("AAAA", ncfile.findVariable(ProfileNcConsts.PROFILE_PSAL_QC).read().toString());
      assertEquals("Primary sampling: averaged [10 sec sampling, 25 dbar average from 2000 dbar to 200 dbar; 10 sec sampling, 10 dbar average from 200 dbar to 10.0 dbar]                                                                                                           ", ncfile.findVariable(ProfileNcConsts.VERTICAL_SAMPLING_SCHEME).read("0,:").toString());
      assertEquals(2,  ncfile.findVariable(ProfileNcConsts.CONFIG_MISSION_NUMBER).read().getDouble(0));

      assertEquals(16.0f,ncfile.findVariable(ProfileNcConsts.PRES).read("3,:").getFloat(0));
      assertEquals(ProfileNcConsts.FILL_VALUE_FLOAT,ncfile.findVariable(ProfileNcConsts.PRES).read("3,:").getFloat(91));

      int nLevels = ncfile.findDimension("N_LEVELS").getLength();
      String Qc4_0 =  String.format("%-"+nLevels+"s","1111111111111111111111111111111111111111111111111111111111111111111111");
      assertEquals(Qc4_0,ncfile.findVariable(ProfileNcConsts.PRES_QC).read("3,:").toString());
      assertEquals(26.0f,ncfile.findVariable(ProfileNcConsts.PRES_ADJUSTED).read("3,:").getFloat(1));
      assertEquals(ProfileNcConsts.FILL_VALUE_FLOAT,ncfile.findVariable(ProfileNcConsts.PRES_ADJUSTED).read("3,:").getFloat(nLevels-1));
      assertEquals(15.0f, ncfile.findVariable(ProfileNcConsts.PRES_ADJUSTED).read().getFloat(0));
      assertEquals(Qc4_0,ncfile.findVariable(ProfileNcConsts.PRES_ADJUSTED_QC).read("3,:").toString());
      assertEquals(2.4f,ncfile.findVariable(ProfileNcConsts.PRES_ADJUSTED_ERROR).read("3,:").getFloat(1));
      assertEquals(ProfileNcConsts.FILL_VALUE_FLOAT,ncfile.findVariable(ProfileNcConsts.PRES_ADJUSTED_ERROR).read("3,:").getFloat(70));

//      String TempQc4_0 =  String.format("%-"+nLevels+"s","1111111111111111111111111111111111111111111111111111111111111111111111");
      assertEquals(28.249f,ncfile.findVariable(ProfileNcConsts.TEMP).read("3,:").getFloat(1));
      assertEquals(ProfileNcConsts.FILL_VALUE_FLOAT,ncfile.findVariable(ProfileNcConsts.TEMP).read("3,:").getFloat(nLevels-1));
      assertEquals(Qc4_0,ncfile.findVariable(ProfileNcConsts.TEMP_QC).read("3,:").toString());
      assertEquals(28.268f, ncfile.findVariable(ProfileNcConsts.TEMP_ADJUSTED).read("3,:").getFloat(0));
      assertEquals(Qc4_0,ncfile.findVariable(ProfileNcConsts.TEMP_ADJUSTED_QC).read("3,:").toString());
      assertEquals(0.002f,ncfile.findVariable(ProfileNcConsts.TEMP_ADJUSTED_ERROR).read("3,:").getFloat(1));
      assertEquals(ProfileNcConsts.FILL_VALUE_FLOAT,ncfile.findVariable(ProfileNcConsts.TEMP_ADJUSTED_ERROR).read("3,:").getFloat(70));

//      String PSALQc4_0 =  String.format("%-"+nLevels+"s","1111111111111111111111111111111111111111111111111111111111111111111111");
      assertEquals(34.949f,ncfile.findVariable(ProfileNcConsts.PSAL).read("3,:").getFloat(1));
      assertEquals(ProfileNcConsts.FILL_VALUE_FLOAT,ncfile.findVariable(ProfileNcConsts.PSAL).read("3,:").getFloat(nLevels-1));
      assertEquals(Qc4_0,ncfile.findVariable(ProfileNcConsts.PSAL_QC).read("3,:").toString());
      assertEquals(34.9471f, ncfile.findVariable(ProfileNcConsts.PSAL_ADJUSTED).read("3,:").getFloat(0));
      assertEquals(Qc4_0,ncfile.findVariable(ProfileNcConsts.PSAL_ADJUSTED_QC).read("3,:").toString());
      assertEquals(0.01f,ncfile.findVariable(ProfileNcConsts.PSAL_ADJUSTED_ERROR).read("3,:").getFloat(1));
      assertEquals(ProfileNcConsts.FILL_VALUE_FLOAT,ncfile.findVariable(ProfileNcConsts.PSAL_ADJUSTED_ERROR).read("3,:").getFloat(70));

      assertEquals(String.format("%-"+ ProfileNcConsts.STRING256+"s","PRES_ADJUSTED = PRES"), ncfile.findVariable(ProfileNcConsts.SCIENTIFIC_CALIB_EQUATION).read("0,0,0,:").toString());
      assertEquals(String.format("%-"+ ProfileNcConsts.STRING256+"s","TEMP_ADJUSTED = TEMP"), ncfile.findVariable(ProfileNcConsts.SCIENTIFIC_CALIB_EQUATION).read("0,0,1,:").toString());
      assertEquals(String.format("%-"+ ProfileNcConsts.STRING256+"s","PSAL + dS, where dS is calculated from a potential conductivity (ref to 0 dbar) multiplicative adjustment term r"), ncfile.findVariable(ProfileNcConsts.SCIENTIFIC_CALIB_EQUATION).read("0,0,2,:").toString());

      assertEquals(String.format("%-"+ ProfileNcConsts.STRING256+"s","none"), ncfile.findVariable(ProfileNcConsts.SCIENTIFIC_CALIB_COEFFICIENT).read("0,0,0,:").toString());
      assertEquals(String.format("%-"+ ProfileNcConsts.STRING256+"s","none"), ncfile.findVariable(ProfileNcConsts.SCIENTIFIC_CALIB_COEFFICIENT).read("0,0,1,:").toString());
      assertEquals(String.format("%-"+ ProfileNcConsts.STRING256+"s","r = 0.9999,vertically averaged dS=-0.005"), ncfile.findVariable(ProfileNcConsts.SCIENTIFIC_CALIB_COEFFICIENT).read("0,0,2,:").toString());

      assertEquals(String.format("%-"+ ProfileNcConsts.STRING256+"s","No significant pressure drift detected. Calibration error is manufacturer specified accuracy in dbar"), ncfile.findVariable(ProfileNcConsts.SCIENTIFIC_CALIB_COMMENT).read("0,0,0,:").toString());
      assertEquals("20230905073222",ncfile.findVariable(ProfileNcConsts.SCIENTIFIC_CALIB_DATE).read("0,0,0,:").toString());



//temp
//      Variable temp = ncfile.findVariable(ProfileNcConsts.CONFIG_MISSION_NUMBER);

    }


  }
}