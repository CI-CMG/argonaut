package edu.colorado.cires.argonaut.service.merge;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import ucar.ma2.Array;
import ucar.ma2.ArrayDouble;
import ucar.ma2.ArrayFloat;
import ucar.ma2.ArrayInt;
import ucar.ma2.ArrayString;
import ucar.ma2.Index;
import ucar.ma2.InvalidRangeException;
import ucar.ma2.Range;
import ucar.nc2.NetcdfFile;
import ucar.nc2.NetcdfFiles;
import ucar.nc2.Variable;

public class MergeProfile {
    private final String dataType = "Argo profile    ";
    private final String formatVersion = "3.1";
    private final String handBookVersion = "1.2";
    private final String referenceDateTime = "19500101000000";
    private Instant currentDateTime;
    private List<CoreProfile> profileNcFiles;
    private int nProf;
    private int nParam;
    private int nCalib;
    private int nLevels;

    private List<Integer> cycleNumbers;
    private List<String> platformNumbers;
    private List<String> projectNames;
    private List<String> piNames;
    private List<List<String>> stationParameters;
    private String directions;
    private List<String> dataCenters;

    private List<String> dcReferences;
    private List<String> dataStateIndicators;
    private String dataModes;
    private List<String> platformTypes;
    private List<String> floatSerialNos;
    private List<String> firmWareVersions;
    private List<String> wmoInstTypes;

    private List<Double> julds;
    private String juldQc;
    private List<Double> juldLocations;
    private List<Double> latitudes;
    private List<Double> longitudes;
    private String positionQcs;
    private List<String> positioningSystems;
    private String profilePresQcs;
    private String profileTempQcs;
    private String profilePsalQcs;
    private List<String> verticalSamplingSchemes;
    private List<Integer> configMissionNumbers;
    private List<List<Float>> press;
    private List<String> presQcs;
    private List<List<Float>> presAdjusteds;
    private List<String> presAdjustedQcs;
    private List<List<Float>> presAdjustedErrors;
    private List<List<Float>> temps;
    private List<String> tempQcs;
    private List<List<Float>> tempAdjusteds;
    private List<String> tempAdjustedQcs;
    private List<List<Float>> tempAdjustedErrors;
    private List<List<Float>> psals;
    private List<String> psalQcs;
    private List<List<Float>> psalAdjusteds;
    private List<String> psalAdjustedQcs;
    private List<List<Float>> psalAdjustedErrors;
    private List<List<List<String>>> parameters;
    private List<List<List<String>>> scientifiCalibEquations;
  private List<List<List<String>>> scientifiCalibCoefficients;
  private List<List<List<String>>> scientifiCalibComments;
  private List<List<List<String>>> scientifiCalibDates;

  public ArrayString getDataType(){
    return getArrayStringD1(new int[] {ProfileNcConsts.STRING16}, this.dataType);
  }

  public ArrayString getFormatVersion() {
    return getArrayStringD1(new int[] {ProfileNcConsts.STRING4}, this.formatVersion);
  }

  public ArrayString getHandBookVersion() {
    return getArrayStringD1(new int[] {ProfileNcConsts.STRING4}, this.handBookVersion);
  }

  public ArrayString getReferenceDateTime() {
    return getArrayStringD1(new int[] {ProfileNcConsts.DATE_TIME}, this.referenceDateTime);
  }

  public ArrayString getCurrentDateTime() {
    LocalDateTime ldt = LocalDateTime.ofInstant(this.currentDateTime, ZoneOffset.UTC);
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    return getArrayStringD1(new int[] {ProfileNcConsts.DATE_TIME}, dateTimeFormatter.format(ldt));
  }

  public void setProfileNcFiles(List<CoreProfile> profileNcFiles) {
      this.profileNcFiles = profileNcFiles;
    }

    public void setDimension() {
      this.nProf = profileNcFiles.size();
      this.currentDateTime = Instant.now();
      CoreProfile  maxnParm = this.profileNcFiles
          .stream()
          .max(Comparator.comparing(CoreProfile::getnParam))
          .orElseThrow(NoSuchElementException::new);
      setnParam(maxnParm.getnParam());

      CoreProfile  maxnCalib = this.profileNcFiles
          .stream()
          .max(Comparator.comparing(CoreProfile::getnCalib))
          .orElseThrow(NoSuchElementException::new);
      setnCalib(maxnCalib.getnCalib());

      CoreProfile  maxnLevel = this.profileNcFiles
          .stream()
          .max(Comparator.comparing(CoreProfile::getnLevels))
          .orElseThrow(NoSuchElementException::new);
      setnLevels(maxnLevel.getnLevels());
    }

  public void setCurrentDateTime() {
    this.currentDateTime = Instant.now();
  }

  public int getnProf() {
      return nProf;
    }

    public int getnParam() {
      return nParam;
    }

    public void setnParam(int nParam) {
      this.nParam = nParam;
    }


    public int getnCalib() {
      return nCalib;
    }

    public void setnCalib(int nCalib) {
      this.nCalib = nCalib;
    }

    public int getnLevels() {
      return nLevels;
    }

    public void setnLevels(int nLevel) {
      this.nLevels = nLevel;
    }

    public void setVariables() {
      setCycleNumbers(this.profileNcFiles.stream().map(CoreProfile::getCycleNumber).collect(Collectors.toList()));
      setJulds(this.profileNcFiles.stream().map(CoreProfile::getJuld).collect(Collectors.toList()));
      setLatitudes(this.profileNcFiles.stream().map(CoreProfile::getLatitude).collect(Collectors.toList()));
      setLongitude(this.profileNcFiles.stream().map(CoreProfile::getLongitude).collect(Collectors.toList()));
      for (CoreProfile profileNcFile: profileNcFiles){
        try (NetcdfFile ncfile = NetcdfFiles.open(profileNcFile.getFile().toString())) {
          try {
            addPlatformNumber(ncfile.findVariable(ProfileNcConsts.PLATFORM_NUMBER).read("0,:").toString());
          } catch (InvalidRangeException e) {
            throw new RuntimeException("An error reading: " + ProfileNcConsts.PLATFORM_NUMBER + " from " + profileNcFile, e);
          }
          try{
            addProjectName(ncfile.findVariable(ProfileNcConsts.PROJECT_NAME).read("0,:").toString());
          } catch (InvalidRangeException e) {
            throw new RuntimeException("An error reading: " + ProfileNcConsts.PROJECT_NAME + " from " + profileNcFile, e);
          }
          try{
            addPiName(ncfile.findVariable(ProfileNcConsts.PI_NAME).read("0,:").toString());
          } catch (InvalidRangeException e) {
            throw new RuntimeException("An error reading: " + ProfileNcConsts.PI_NAME + " from " + profileNcFile, e);
          }
          try{
            addStationParameter(ncfile.findVariable(ProfileNcConsts.STATION_PARAMETERS).read("0,:,:").reduce(0));
          } catch (InvalidRangeException e) {
            throw new RuntimeException("An error reading: " + ProfileNcConsts.STATION_PARAMETERS + " from " + profileNcFile, e);
          }

          try{
            addDirection(ncfile.findVariable(ProfileNcConsts.DIRECTION).read("0").toString());
          } catch (InvalidRangeException e) {
            throw new RuntimeException("An error reading: " + ProfileNcConsts.DIRECTION + " from " + profileNcFile, e);
          }
          try{
            addDataCenters(ncfile.findVariable(ProfileNcConsts.DATA_CENTRE).read("0,:").toString());
          } catch (InvalidRangeException e) {
            throw new RuntimeException("An error reading: " + ProfileNcConsts.DATA_CENTRE + " from " + profileNcFile, e);
          }
          try{
            addDcReference(ncfile.findVariable(ProfileNcConsts.DC_REFERENCE).read("0,:").toString());
          } catch (InvalidRangeException e) {
            throw new RuntimeException("An error reading: " + ProfileNcConsts.DC_REFERENCE + " from " + profileNcFile, e);
          }
          try{
            addDataStateIndicators(ncfile.findVariable(ProfileNcConsts.DATA_STATE_INDICATOR).read("0,:").toString());
          } catch (InvalidRangeException e) {
            throw new RuntimeException("An error reading: " + ProfileNcConsts.DATA_STATE_INDICATOR + " from " + profileNcFile, e);
          }
          try{
            addDataModes(ncfile.findVariable(ProfileNcConsts.DATA_MODE).read("0").toString());
          } catch (InvalidRangeException e) {
            throw new RuntimeException("An error reading: " + ProfileNcConsts.DATA_MODE + " from " + profileNcFile, e);
          }
          try{
            addPlatformTypes(ncfile.findVariable(ProfileNcConsts.PLATFORM_TYPE).read("0,:").toString());
          } catch (InvalidRangeException e) {
            throw new RuntimeException("An error reading: " + ProfileNcConsts.PLATFORM_TYPE + " from " + profileNcFile, e);
          }
          try{
            addFloatSerialNos(ncfile.findVariable(ProfileNcConsts.FLOAT_SERIAL_NO).read("0,:").toString());
          } catch (InvalidRangeException e) {
            throw new RuntimeException("An error reading: " + ProfileNcConsts.FLOAT_SERIAL_NO + " from " + profileNcFile, e);
          }
          try{
            addFirmWareVersions(ncfile.findVariable(ProfileNcConsts.FIRMWARE_VERSION).read("0,:").toString());
          } catch (InvalidRangeException e) {
            throw new RuntimeException("An error reading: " + ProfileNcConsts.FIRMWARE_VERSION + " from " + profileNcFile, e);
          }
          try{
            addWmoInstTypes(ncfile.findVariable(ProfileNcConsts.WMO_INST_TYPE).read("0,:").toString());
          } catch (InvalidRangeException e) {
            throw new RuntimeException("An error reading: " + ProfileNcConsts.WMO_INST_TYPE + " from " + profileNcFile, e);
          }
          try{
            addJuldQc(ncfile.findVariable(ProfileNcConsts.JULD_QC).read("0").toString());
          } catch (InvalidRangeException e) {
            throw new RuntimeException("An error reading: " + ProfileNcConsts.JULD_QC + " from " + profileNcFile, e);
          }
          addJuldLocations(ncfile.findVariable(ProfileNcConsts.JULD_LOCATION).read().getDouble(0));
          try{
            addPositionQcs(ncfile.findVariable(ProfileNcConsts.POSITION_QC).read("0").toString());
          } catch (InvalidRangeException e) {
            throw new RuntimeException("An error reading: " + ProfileNcConsts.POSITION_QC + " from " + profileNcFile, e);
          }
          try{
            addPositioningSystem(ncfile.findVariable(ProfileNcConsts.POSITIONING_SYSTEM).read("0,:").toString());
          } catch (InvalidRangeException e) {
            throw new RuntimeException("An error reading: " + ProfileNcConsts.POSITIONING_SYSTEM + " from " + profileNcFile, e);
          }
          try{
            addProfilePresQcs(ncfile.findVariable(ProfileNcConsts.PROFILE_PRES_QC).read("0").toString());
          } catch (InvalidRangeException e) {
            throw new RuntimeException("An error reading: " + ProfileNcConsts.PROFILE_PRES_QC + " from " + profileNcFile, e);
          }
          try{
            addProfileTempQcs(ncfile.findVariable(ProfileNcConsts.PROFILE_TEMP_QC).read("0").toString());
          } catch (InvalidRangeException e) {
            throw new RuntimeException("An error reading: " + ProfileNcConsts.PROFILE_TEMP_QC + " from " + profileNcFile, e);
          }
          try{
            addProfilePsalQcs(ncfile.findVariable(ProfileNcConsts.PROFILE_PSAL_QC).read("0").toString());
          } catch (InvalidRangeException e) {
            throw new RuntimeException("An error reading: " + ProfileNcConsts.PROFILE_PSAL_QC + " from " + profileNcFile, e);
          }
          try{
            addVerticalSamplingSchemes(ncfile.findVariable(ProfileNcConsts.VERTICAL_SAMPLING_SCHEME).read("0,:").toString());
          } catch (InvalidRangeException e) {
            throw new RuntimeException("An error reading: " + ProfileNcConsts.VERTICAL_SAMPLING_SCHEME + " from " + profileNcFile, e);
          }
          addConfigMissionNumbers(ncfile.findVariable(ProfileNcConsts.CONFIG_MISSION_NUMBER).read().getInt(0));

          try{
            addPress(ncfile.findVariable(ProfileNcConsts.PRES).read("0,:"));
          } catch (InvalidRangeException e) {
            throw new RuntimeException("An error reading: " + ProfileNcConsts.PRES + " from " + profileNcFile, e);
          }
          try{
            addPresQcs(ncfile.findVariable(ProfileNcConsts.PRES_QC).read("0,:").toString());
          } catch (InvalidRangeException e) {
            throw new RuntimeException("An error reading: " + ProfileNcConsts.PRES_QC + " from " + profileNcFile, e);
          }
          try{
            addPresAdjusteds(ncfile.findVariable(ProfileNcConsts.PRES_ADJUSTED).read("0,:"));
          } catch (InvalidRangeException e) {
            throw new RuntimeException("An error reading: " + ProfileNcConsts.PRES_ADJUSTED + " from " + profileNcFile, e);
          }
          try{
            addPresAdjustedQcs(ncfile.findVariable(ProfileNcConsts.PRES_ADJUSTED_QC).read("0,:").toString());
          } catch (InvalidRangeException e) {
            throw new RuntimeException("An error reading: " + ProfileNcConsts.PRES_ADJUSTED_QC + " from " + profileNcFile, e);
          }
          try{
            addPresAdjustedErrors(ncfile.findVariable(ProfileNcConsts.PRES_ADJUSTED_ERROR).read("0,:"));
          } catch (InvalidRangeException e) {
            throw new RuntimeException("An error reading: " + ProfileNcConsts.PRES_ADJUSTED_ERROR + " from " + profileNcFile, e);
          }

          try{
            addTemps(ncfile.findVariable(ProfileNcConsts.TEMP).read("0,:"));
          } catch (InvalidRangeException e) {
            throw new RuntimeException("An error reading: " + ProfileNcConsts.TEMP + " from " + profileNcFile, e);
          }
          try{
            addTempQcs(ncfile.findVariable(ProfileNcConsts.TEMP_QC).read("0,:").toString());
          } catch (InvalidRangeException e) {
            throw new RuntimeException("An error reading: " + ProfileNcConsts.TEMP_QC + " from " + profileNcFile, e);
          }
          try{
            addTempAdjusteds(ncfile.findVariable(ProfileNcConsts.TEMP_ADJUSTED).read("0,:"));
          } catch (InvalidRangeException e) {
            throw new RuntimeException("An error reading: " + ProfileNcConsts.TEMP_ADJUSTED + " from " + profileNcFile, e);
          }
          try{
            addTempAdjustedQcs(ncfile.findVariable(ProfileNcConsts.TEMP_ADJUSTED_QC).read("0,:").toString());
          } catch (InvalidRangeException e) {
            throw new RuntimeException("An error reading: " + ProfileNcConsts.TEMP_ADJUSTED_QC + " from " + profileNcFile, e);
          }
          try{
            addTempAdjustedErrors(ncfile.findVariable(ProfileNcConsts.TEMP_ADJUSTED_ERROR).read("0,:"));
          } catch (InvalidRangeException e) {
            throw new RuntimeException("An error reading: " + ProfileNcConsts.TEMP_ADJUSTED_ERROR + " from " + profileNcFile, e);
          }

          try{
            addPsals(ncfile.findVariable(ProfileNcConsts.PSAL).read("0,:"));
          } catch (InvalidRangeException e) {
            throw new RuntimeException("An error reading: " + ProfileNcConsts.PSAL + " from " + profileNcFile, e);
          }
          try{
            addPsalQcs(ncfile.findVariable(ProfileNcConsts.PSAL_QC).read("0,:").toString());
          } catch (InvalidRangeException e) {
            throw new RuntimeException("An error reading: " + ProfileNcConsts.PSAL_QC + " from " + profileNcFile, e);
          }
          try{
            addPsalAdjusteds(ncfile.findVariable(ProfileNcConsts.PSAL_ADJUSTED).read("0,:"));
          } catch (InvalidRangeException e) {
            throw new RuntimeException("An error reading: " + ProfileNcConsts.PSAL_ADJUSTED + " from " + profileNcFile, e);
          }
          try{
            addPsalAdjustedQcs(ncfile.findVariable(ProfileNcConsts.PSAL_ADJUSTED_QC).read("0,:").toString());
          } catch (InvalidRangeException e) {
            throw new RuntimeException("An error reading: " + ProfileNcConsts.PSAL_ADJUSTED_QC + " from " + profileNcFile, e);
          }
          try{
            addPsalAdjustedErrors(ncfile.findVariable(ProfileNcConsts.PSAL_ADJUSTED_ERROR).read("0,:"));
          } catch (InvalidRangeException e) {
            throw new RuntimeException("An error reading: " + ProfileNcConsts.PSAL_ADJUSTED_ERROR + " from " + profileNcFile, e);
          }

          try{
            addParameters(ncfile.findVariable(ProfileNcConsts.PARAMETER));
          } catch (InvalidRangeException |IOException e) {
            throw new RuntimeException("An error reading: " + ProfileNcConsts.PARAMETER + " from " + profileNcFile, e);
          }

          try{
            addScientifiCalibEquations(ncfile.findVariable(ProfileNcConsts.SCIENTIFIC_CALIB_EQUATION));
          } catch (InvalidRangeException |IOException e) {
            throw new RuntimeException("An error reading: " + ProfileNcConsts.SCIENTIFIC_CALIB_EQUATION + " from " + profileNcFile, e);
          }
          try{
            addScientifiCalibCoefficients(ncfile.findVariable(ProfileNcConsts.SCIENTIFIC_CALIB_COEFFICIENT));
          } catch (InvalidRangeException |IOException e) {
            throw new RuntimeException("An error reading: " + ProfileNcConsts.SCIENTIFIC_CALIB_COEFFICIENT + " from " + profileNcFile, e);
          }
          try{
            addScientifiCalibComments(ncfile.findVariable(ProfileNcConsts.SCIENTIFIC_CALIB_COMMENT));
          } catch (InvalidRangeException |IOException e) {
            throw new RuntimeException("An error reading: " + ProfileNcConsts.SCIENTIFIC_CALIB_COMMENT + " from " + profileNcFile, e);
          }
          try{
            addScientifiCalibDates(ncfile.findVariable(ProfileNcConsts.SCIENTIFIC_CALIB_DATE));
          } catch (InvalidRangeException |IOException e) {
            throw new RuntimeException("An error reading: " + ProfileNcConsts.SCIENTIFIC_CALIB_DATE + " from " + profileNcFile, e);
          }

        } catch (IOException e) {
          throw new RuntimeException("An error opening profile nc file : " + profileNcFile, e);
        }
      }
    }
    public ArrayInt getCycleNumbers() {
      int[] shape = new int[] {nProf};
      ArrayInt cnums = new ArrayInt(shape, true);
      Index ima = cnums.getIndex();
      for (int i = 0; i < cycleNumbers.size(); i++){
        int x = cycleNumbers.get(i);
        cnums.setInt(ima.set(i), x);
      }
      return cnums;
    }

    public void setCycleNumbers(List<Integer> cycleNumbers) {
      this.cycleNumbers = cycleNumbers;
    }

    public ArrayString getPlatformNumbers() {
      return getArrayStringD2(new int[] {nProf, ProfileNcConsts.STRING8}, this.platformNumbers);
    }

    public void addPlatformNumber(String platformNumber) {
      if (this.platformNumbers == null){
        this.platformNumbers = new ArrayList<>();
      }
      this.platformNumbers.add(platformNumber);
    }

    public ArrayString getProjectNames() {
      return getArrayStringD2(new int[] {nProf, ProfileNcConsts.STRING64}, this.projectNames);
    }

    public void addProjectName(String projectName) {
      if (this.projectNames == null){
        this.projectNames = new ArrayList<>();
      }
      this.projectNames.add(projectName);
    }

    public ArrayString getPiNames() {
      return getArrayStringD2(new int[] {nProf, ProfileNcConsts.STRING64}, this.piNames);
    }

    public void addPiName(String piName) {
      if (this.piNames == null){
        this.piNames = new ArrayList<>();
      }
      this.piNames.add(piName);
    }

    public ArrayString getStationParameters() {
      int[] shape = new int[] {nProf, nParam, ProfileNcConsts.STRING16};
      ArrayString data = new ArrayString.D3(shape[0], shape[1], shape[2]);
      Index ima = data.getIndex();
      for (int x = 0; x < shape[0]; x++){
        List<String> row = stationParameters.get(x);
        for(int y = 0; y < shape[1]; y++){
          data.set(ima.set(x,y),  row.get(y));
        }
      }
      return data;
    }

    public void addStationParameter(Array stationParameter) {
      if (this.stationParameters == null){
        this.stationParameters = new ArrayList<>();
      }
      List<String> data = new ArrayList<>(Arrays.asList(stationParameter.toString().split(",")));
      if (data.size() < this.nParam){
        String empty = String.format("%-"+ ProfileNcConsts.STRING16 + "." + ProfileNcConsts.STRING16 + "s","");
        for(int i=data.size(); i<nLevels; i++){
          data.add(empty);
        }
      }
      this.stationParameters.add(data);
    }

    public ArrayString getDirections() {
      return getArrayStringD1(new int[] {nProf}, this.directions);
    }

    public void addDirection(String direction) {
      if (this.directions == null){
        this.directions = new String();
      }
      this.directions += direction;
    }

    public ArrayString getDataCenters() {
      return getArrayStringD2(new int[] {nProf, ProfileNcConsts.STRING2}, this.dataCenters);
    }

    public void addDataCenters(String dataCenter) {
      if (this.dataCenters == null){
        this.dataCenters = new ArrayList<>();
      }
      this.dataCenters.add(dataCenter);
    }

    public ArrayString getDcReferences() {
      return getArrayStringD2(new int[] {nProf, ProfileNcConsts.STRING32}, this.dcReferences);
    }

    public void addDcReference(String dcReference) {
      if (this.dcReferences == null){
        this.dcReferences = new ArrayList<>();
      }
      this.dcReferences.add(dcReference);
    }

  public ArrayString getDataStateIndicators() {
    return getArrayStringD2(new int[] {nProf, ProfileNcConsts.STRING4}, this.dataStateIndicators);
  }

  public void addDataStateIndicators(String dataStateIndicator) {
    if (this.dataStateIndicators == null){
      this.dataStateIndicators = new ArrayList<>();
    }
    this.dataStateIndicators.add(dataStateIndicator);
  }

  public ArrayString getDataModes() {
    return getArrayStringD1(new int[] {nProf}, this.dataModes);
  }

  public void addDataModes(String dataMode) {
    if (this.dataModes == null){
      this.dataModes = new String();
    }
    this.dataModes += dataMode ;
  }

  public ArrayString getPlatformTypes() {
    return getArrayStringD2(new int[] {nProf, ProfileNcConsts.STRING32}, this.platformTypes);
  }

  public void addPlatformTypes(String platformType) {
    if (this.platformTypes == null){
      this.platformTypes = new ArrayList<>();
    }
    this.platformTypes.add(platformType);
  }

  public ArrayString getFloatSerialNos() {
    return getArrayStringD2(new int[] {nProf, ProfileNcConsts.STRING32}, this.floatSerialNos);
  }

  public void addFloatSerialNos(String floatSerialNo) {
    if (this.floatSerialNos == null){
      this.floatSerialNos = new ArrayList<>();
    }
    this.floatSerialNos.add(floatSerialNo);
  }

  public ArrayString getFirmWareVersions() {
    return getArrayStringD2(new int[] {nProf, ProfileNcConsts.STRING32}, this.firmWareVersions);
  }

  public void addFirmWareVersions(String firmWareVersion) {
    if (this.firmWareVersions == null){
      this.firmWareVersions = new ArrayList<>();
    }
    this.firmWareVersions.add(firmWareVersion);
  }

  public ArrayString getWmoInstTypes() {
    return getArrayStringD2(new int[] {nProf, ProfileNcConsts.STRING4}, this.wmoInstTypes);
  }

  public void addWmoInstTypes(String wmoInstType) {
    if (this.wmoInstTypes == null){
      this.wmoInstTypes = new ArrayList<>();
    }
    this.wmoInstTypes.add(wmoInstType);
  }

  public ArrayDouble getJulds() {
    int[] shape = new int[]{nProf};
    ArrayDouble data = new ArrayDouble(shape);
    Index ima = data.getIndex();
    for (int i = 0; i < this.julds.size(); i++){
      data.set(ima.set(i), this.julds.get(i));
    }
    return data;
  }

  public void setJulds(List<Double> julds) {
    this.julds = julds;
  }

  public ArrayString getJuldQc() {
    return getArrayStringD1(new int[] {nProf}, this.juldQc);
  }

  public void addJuldQc(String juldQc) {
    if (this.juldQc == null){
      this.juldQc = new String();
    }
    this.juldQc += juldQc;
  }

  public ArrayDouble getJuldLocations() {
    int[] shape = new int[] {nProf};
    ArrayDouble data = new ArrayDouble(shape);
    Index ima = data.getIndex();
    for (int i = 0; i < this.juldLocations.size(); i++){
      data.set(ima.set(i), this.juldLocations.get(i));
    }
    return data;
  }

  public void addJuldLocations(Double juldLocation) {
    if (this.juldLocations == null){
      this.juldLocations = new ArrayList<>();
    }
    this.juldLocations.add(juldLocation);
  }

  public ArrayDouble getLatitudes() {
    int[] shape = new int[] {nProf};
    ArrayDouble data = new ArrayDouble(shape);
    Index ima = data.getIndex();
    for (int i = 0; i < this.latitudes.size(); i++){
      data.set(ima.set(i), this.latitudes.get(i));
    }
    return data;
  }

  public void setLatitudes(List<Double> latitudes) {
    this.latitudes = latitudes;
  }

  public ArrayDouble getLongitude() {
    int[] shape = new int[] {nProf};
    ArrayDouble data = new ArrayDouble(shape);
    Index ima = data.getIndex();
    for (int i = 0; i < this.longitudes.size(); i++){
      data.set(ima.set(i), this.longitudes.get(i));
    }
    return data;
  }

  public void setLongitude(List<Double> longitudes) {
    this.longitudes = longitudes;
  }

  public ArrayString getPositionQcs() {
    return getArrayStringD1(new int[] {nProf}, this.positionQcs);
  }

  public void addPositionQcs(String positionQcs) {
    if (this.positionQcs == null){
      this.positionQcs = new String();
    }
    this.positionQcs+= positionQcs;
  }

  public ArrayString getPositioningSystems() {
    return getArrayStringD2(new int[] {nProf, ProfileNcConsts.STRING8}, this.positioningSystems);
  }

  public void addPositioningSystem(String positioningSystem) {
    if (this.positioningSystems == null){
      this.positioningSystems = new ArrayList<>();
    }
    this.positioningSystems.add(positioningSystem);
  }

  public ArrayString getProfilePresQcs() {
    return getArrayStringD1(new int[] {nProf}, this.profilePresQcs);
  }

  public void addProfilePresQcs(String profilePresQcs) {
    if (this.profilePresQcs == null){
      this.profilePresQcs = new String();
    }
    this.profilePresQcs += profilePresQcs;
  }

  public ArrayString getProfileTempQcs() {
    return getArrayStringD1(new int[] {nProf}, this.profileTempQcs);
  }

  public void addProfileTempQcs(String profileTempQc) {
    if (this.profileTempQcs == null){
      this.profileTempQcs = new String();
    }
    this.profileTempQcs += profileTempQc;
  }

  public ArrayString getProfilePsalQcs() {
    return getArrayStringD1(new int[] {nProf}, this.profilePsalQcs);
  }

  public void addProfilePsalQcs(String profilePsalQc) {
    if (this.profilePsalQcs == null){
      this.profilePsalQcs = new String();
    }
    this.profilePsalQcs += profilePsalQc;
  }

  public ArrayString getVerticalSamplingSchemes() {
    return getArrayStringD2(new int[] {nProf, ProfileNcConsts.STRING256}, this.verticalSamplingSchemes);
  }

  public void addVerticalSamplingSchemes(String verticalSamplingSchemes) {
    if(this.verticalSamplingSchemes == null){
      this.verticalSamplingSchemes = new ArrayList<>();
    }
    this.verticalSamplingSchemes.add(verticalSamplingSchemes);
  }

  public ArrayInt getConfigMissionNumbers() {
    int[] shape = new int[]{nProf};
    ArrayInt data = new ArrayInt(shape, true);
    Index ima = data.getIndex();
    for (int i = 0; i < shape[0]; i++){
      data.set(ima.set(i), this.configMissionNumbers.get(i));
    }
    return data;
  }

  public void addConfigMissionNumbers(int configMissionNumber) {
    if(this.configMissionNumbers == null){
      this.configMissionNumbers = new ArrayList<>();
    }
    this.configMissionNumbers.add(configMissionNumber);
  }

  public ArrayFloat getPress() {
    return getArrayFloat(nProf, nLevels, this.press);
  }

  public void addPress(Array pres) {
    if (this.press == null){
      this.press = new ArrayList<>();
    }
    List<Float> temp = new ArrayList<>();
    int[] shape = pres.getShape();
    for (int i = 0; i < shape[1]; i++){
     temp.add(pres.getFloat(i));
    }
    if (shape[1] < nLevels){
      for (int y = shape[1]; y < nLevels; y++){
        temp.add(ProfileNcConsts.FILL_VALUE_FLOAT);
      }
    }
    this.press.add(temp);
  }

  public ArrayString getPresQcs() {
    return getArrayStringD2(new int[]{nProf,nLevels}, this.presQcs);
  }

  public void addPresQcs(String presQc) {
    if (this.presQcs == null){
      this.presQcs = new ArrayList<>();
    }
    this.presQcs.add(String.format("%-" + nLevels + "." + nLevels + "s",presQc));
  }

  public ArrayFloat getPresAdjusteds(){
    return getArrayFloat(nProf, nLevels, this.presAdjusteds);
  }

  public void addPresAdjusteds(Array presAdjusted) {
    if (this.presAdjusteds == null){
      this.presAdjusteds = new ArrayList<>();
    }
    List<Float> temp = new ArrayList<>();
    int[] shape = presAdjusted.getShape();
    for (int i = 0; i < shape[1]; i++){
      temp.add(presAdjusted.getFloat(i));
    }
    if (shape[1] < nLevels){
      for (int y = shape[1]; y < nLevels; y++){
        temp.add(ProfileNcConsts.FILL_VALUE_FLOAT);
      }
    }
    this.presAdjusteds.add(temp);
  }

  public ArrayString getPresAdjustedQcs() {
    return  getArrayStringD2(new int[]{nProf,nLevels}, this.presAdjustedQcs);
  }

  public void addPresAdjustedQcs(String presAdjustedQc) {
    if (this.presAdjustedQcs == null){
      this.presAdjustedQcs = new ArrayList<>();
    }
    this.presAdjustedQcs.add(String.format("%-" + nLevels + "." + nLevels + "s",presAdjustedQc));
  }

  public ArrayFloat getPresAdjustedErrors() {
    return getArrayFloat(nProf, nLevels, this.presAdjustedErrors);
  }

  public void addPresAdjustedErrors(Array adjustedError) {
    if (this.presAdjustedErrors == null){
      this.presAdjustedErrors = new ArrayList<List<Float>>();
    }
    List<Float> temp = new ArrayList<>();
    int[] shape = adjustedError.getShape();
    for (int i = 0; i < shape[1]; i++){
      temp.add(adjustedError.getFloat(i));
    }
    if (shape[1] < nLevels){
      for (int y = shape[1]; y < nLevels; y++){
        temp.add(ProfileNcConsts.FILL_VALUE_FLOAT);
      }
    }
    this.presAdjustedErrors.add(temp);
  }

  public ArrayFloat getTemps() {
    return getArrayFloat(nProf, nLevels, this.temps);
  }

  public void addTemps(Array temp) {
    if (this.temps == null){
      this.temps = new ArrayList<List<Float>>();
    }
    List<Float> data = new ArrayList<>();
    int[] shape = temp.getShape();
    for (int i = 0; i < shape[1]; i++){
      data.add(temp.getFloat(i));
    }
    if (shape[1] < nLevels){
      for (int y = shape[1]; y < nLevels; y++){
        data.add(ProfileNcConsts.FILL_VALUE_FLOAT);
      }
    }
    this.temps.add(data);
  }

  public ArrayString getTempQcs() {
    return getArrayStringD2(new int[] {nProf, nLevels}, this.tempQcs);
  }

  public void addTempQcs(String tempQc) {
    if (this.tempQcs == null){
      this.tempQcs = new ArrayList<>();
    }
    this.tempQcs.add(String.format("%-" + nLevels + "." + nLevels + "s",tempQc));
  }

  public ArrayFloat getTempAdjusteds() {
    return getArrayFloat(nProf, nLevels, this.tempAdjusteds);
  }

  public void addTempAdjusteds(Array floatArray) {
    if (this.tempAdjusteds == null){
      this.tempAdjusteds = new ArrayList<List<Float>>();
    }
    List<Float> data = new ArrayList<>();
    int[] shape = floatArray.getShape();
    for (int i = 0; i < shape[1]; i++){
      data.add(floatArray.getFloat(i));
    }
    if (shape[1] < nLevels){
      for (int y = shape[1]; y < nLevels; y++){
        data.add(ProfileNcConsts.FILL_VALUE_FLOAT);
      }
    }
    this.tempAdjusteds.add(data);
  }

  public ArrayString getTempAdjustedQcs() {
    return getArrayStringD2(new int[]{nProf,nLevels}, this.tempAdjustedQcs);
  }

  public void addTempAdjustedQcs(String tempAdjustedQc) {
    if (this.tempAdjustedQcs == null){
      this.tempAdjustedQcs = new ArrayList<>();
    }
    this.tempAdjustedQcs.add(String.format("%-" + nLevels + "." + nLevels + "s",tempAdjustedQc));
  }

  public ArrayFloat getTempAdjustedErrors() {
    return getArrayFloat(nProf, nLevels, this.tempAdjustedErrors);
  }

  public void addTempAdjustedErrors(Array adjustedError) {
    if (this.tempAdjustedErrors == null){
      this.tempAdjustedErrors = new ArrayList<List<Float>>();
    }
    List<Float> data = new ArrayList<>();
    int[] shape = adjustedError.getShape();
    for (int i = 0; i < shape[1]; i++){
      data.add(adjustedError.getFloat(i));
    }
    if (shape[1] < nLevels){
      for (int y = shape[1]; y < nLevels; y++){
        data.add(ProfileNcConsts.FILL_VALUE_FLOAT);
      }
    }
    this.tempAdjustedErrors.add(data);
  }

  public ArrayFloat getPsals() {
    return getArrayFloat(nProf, nLevels, this.psals);
  }

  public void addPsals(Array temp) {
    if (this.psals == null){
      this.psals = new ArrayList<List<Float>>();
    }
    List<Float> data = new ArrayList<>();
    int[] shape = temp.getShape();
    for (int i = 0; i < shape[1]; i++){
      data.add(temp.getFloat(i));
    }
    if (shape[1] < nLevels){
      for (int y = shape[1]; y < nLevels; y++){
        data.add(ProfileNcConsts.FILL_VALUE_FLOAT);
      }
    }
    this.psals.add(data);
  }

  public ArrayString getPsalQcs() {
    return getArrayStringD2(new int[] {nProf, nLevels}, this.psalQcs);
  }

  public void addPsalQcs(String qc) {
    if (this.psalQcs == null){
      this.psalQcs = new ArrayList<>();
    }
    this.psalQcs.add(String.format("%-" + nLevels+ "." + nLevels +"s",qc));
  }

  public ArrayFloat getPsalAdjusteds() {
    return getArrayFloat(nProf, nLevels, this.psalAdjusteds);
  }

  public void addPsalAdjusteds(Array floatArray) {
    if (this.psalAdjusteds == null){
      this.psalAdjusteds = new ArrayList<List<Float>>();
    }
    List<Float> data = new ArrayList<>();
    int[] shape = floatArray.getShape();
    for (int i = 0; i < shape[1]; i++){
      data.add(floatArray.getFloat(i));
    }
    if (shape[1] < nLevels){
      for (int y = shape[1]; y < nLevels; y++){
        data.add(ProfileNcConsts.FILL_VALUE_FLOAT);
      }
    }
    this.psalAdjusteds.add(data);
  }

  public ArrayString getPsalAdjustedQcs() {
    return getArrayStringD2(new int[]{nProf,nLevels}, this.psalAdjustedQcs);
  }

  public void addPsalAdjustedQcs(String adjustedQc) {
    if (this.psalAdjustedQcs == null){
      this.psalAdjustedQcs = new ArrayList<>();
    }
    this.psalAdjustedQcs.add(String.format("%-"+ nLevels + "." + nLevels +"s",adjustedQc));
  }

  public ArrayFloat getPsalAdjustedErrors() {
    return getArrayFloat(nProf, nLevels, this.psalAdjustedErrors);
  }

  public void addPsalAdjustedErrors(Array adjustedError) {
    if (this.psalAdjustedErrors == null){
      this.psalAdjustedErrors = new ArrayList<List<Float>>();
    }
    List<Float> data = new ArrayList<>();
    int[] shape = adjustedError.getShape();
    for (int i = 0; i < shape[1]; i++){
      data.add(adjustedError.getFloat(i));
    }
    if (shape[1] < nLevels){
      for (int y = shape[1]; y < nLevels; y++){
        data.add(ProfileNcConsts.FILL_VALUE_FLOAT);
      }
    }
    this.psalAdjustedErrors.add(data);
  }

  public ArrayString getParameters() {
    int[] shape = new int[] {nProf, nCalib, nParam, ProfileNcConsts.STRING16};
    return getArrayStringD4(shape, this.parameters);
  }

  public void addParameters(Variable varParameter) throws InvalidRangeException, IOException {
    if (this.parameters == null){
      this.parameters = new ArrayList<List<List<String>>>();
    }
    parameters.add(parseArrayStringD4(varParameter, ProfileNcConsts.STRING16));
  }

  public ArrayString getScientifiCalibEquations() {
    int[] shape = new int[] {nProf, nCalib, nParam, ProfileNcConsts.STRING256};
    return getArrayStringD4(shape, this.scientifiCalibEquations);
  }

  public void addScientifiCalibEquations(Variable var) throws InvalidRangeException, IOException {
    if (this.scientifiCalibEquations == null){
      this.scientifiCalibEquations = new ArrayList<List<List<String>>>();
    }
    scientifiCalibEquations.add(parseArrayStringD4(var, ProfileNcConsts.STRING256));
  }

  public ArrayString getScientifiCalibCoefficients() {
    int[] shape = new int[] {nProf, nCalib, nParam, ProfileNcConsts.STRING256};
    return getArrayStringD4(shape, this.scientifiCalibCoefficients);
  }

  public void addScientifiCalibCoefficients(Variable var) throws InvalidRangeException, IOException {
    if (this.scientifiCalibCoefficients == null){
      this.scientifiCalibCoefficients = new ArrayList<List<List<String>>>();
    }
    scientifiCalibCoefficients.add(parseArrayStringD4(var, ProfileNcConsts.STRING256));
  }

  public ArrayString getScientifiCalibComments() {
    int[] shape = new int[] {nProf, nCalib, nParam, ProfileNcConsts.STRING256};
    return getArrayStringD4(shape, this.scientifiCalibComments);
  }

  public void addScientifiCalibComments(Variable var) throws InvalidRangeException, IOException {
    if (this.scientifiCalibComments == null){
      this.scientifiCalibComments = new ArrayList<List<List<String>>>();
    }
    this.scientifiCalibComments.add(parseArrayStringD4(var, ProfileNcConsts.STRING256));
  }

  public ArrayString getScientifiCalibDates() {
    int[] shape = new int[] {nProf, nCalib, nParam, ProfileNcConsts.DATE_TIME};
    return getArrayStringD4(shape, this.scientifiCalibDates);
  }

  public void addScientifiCalibDates(Variable var) throws InvalidRangeException, IOException {
    if (this.scientifiCalibDates == null){
      this.scientifiCalibDates = new ArrayList<List<List<String>>>();
    }
    this.scientifiCalibDates.add(parseArrayStringD4(var, ProfileNcConsts.DATE_TIME));
  }

  private List<List<String>> parseArrayStringD4(Variable var, int length) throws InvalidRangeException, IOException {
    int[] shape = var.getShape();
    int strIndex = shape[3]-1;
    List<List<String>> list2 = new ArrayList<>();
    for (int y = 0; y < shape[1]; y++) { //nCalib
      List<String> list3 = new ArrayList<>();
      for (int z = 0; z < shape[2]; z++) { //nParam
        String data;
        if (z < shape[2]) {
          List ranges = new ArrayList();
          ranges.add(new Range(0, 0));
          ranges.add(new Range(y, y));
          ranges.add(new Range(z, z));
          ranges.add(new Range(0, strIndex));
          data = String.valueOf(var.read(ranges));
        } else {
          data = " ";
        }
        list3.add(String.format("%-"+ length + "."+  length +"s",data));
      }
      list2.add(list3);
    }
    return list2;
  }
  private ArrayFloat getArrayFloat(int shape0, int shape1, List<List<Float>> ncData){
    int[] shape = new int[] {shape0, shape1};
    ArrayFloat data = new ArrayFloat(shape);
    Index ima = data.getIndex();
    for (int x = 0; x < shape[0]; x++){
      List<Float> row = ncData.get(x);
      for(int y = 0; y < shape[1];y++){
        data.set(ima.set(x,y),row.get(y));
      }
    }
    return data;
  }

  private ArrayString getArrayStringD1(int[] shape, String ncData){
    ArrayString data = new ArrayString(shape);
    Index ima = data.getIndex();
    data.set(ima.set(0),ncData);
    return data;
  }
  private ArrayString getArrayStringD2(int[] shape, List<String> ncData) {
    ArrayString data = new ArrayString(shape);
    Index ima = data.getIndex();
    for (int x = 0; x < shape[0]; x++){
      data.set(ima.set(x),ncData.get(x));
    }
    return data;
  }

  private ArrayString getArrayStringD4(int[] shape, List<List<List<String>>> ncData) {
    ArrayString data = new ArrayString.D4(shape[0], shape[1], shape[2],shape[3]);
    Index ima = data.getIndex();
    for (int x = 0; x< shape[0]; x++) {
      for (int y = 0; y < shape[1]; y++) { //nCalib
        for (int z = 0; z < shape[2]; z++) { //nParam
          data.set(ima.set(x,y,z),ncData.get(x).get(y).get(z));
        }
      }
    }
    return data;
  }
}
