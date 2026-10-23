package edu.colorado.cires.argonaut.core.util;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import ucar.ma2.DataType;
import ucar.nc2.Attribute;
import ucar.nc2.Dimension;
import ucar.nc2.write.NetcdfFormatWriter;

public final class CommonParameterAttributes {

  private static final Pattern BBP_REGEX = Pattern.compile("BBP([0-9]+)");
  private static final Pattern CP_REGEX = Pattern.compile("CP([0-9]+)");
  private static final Pattern DOWN_IRRADIANCE_REGEX = Pattern.compile("DOWN_IRRADIANCE([0-9]+)");
  private static final Pattern UP_RADIANCE_REGEX = Pattern.compile("UP_RADIANCE([0-9]+)");
  private static final Pattern TRANSMITTANCE_PARTICLE_BEAM_ATTENUATION_REGEX = Pattern.compile("TRANSMITTANCE_PARTICLE_BEAM_ATTENUATION([0-9]+)");
  private static final Pattern RAW_DOWNWELLING_IRRADIANCE_REGEX = Pattern.compile("RAW_DOWNWELLING_IRRADIANCE([0-9]+)");
  private static final Pattern RAW_DOWNWELLING_IRRADIANCE_MED_REGEX = Pattern.compile("RAW_DOWNWELLING_IRRADIANCE([0-9]+)_MED");
  private static final Pattern RAW_DOWNWELLING_IRRADIANCE_STD_REGEX = Pattern.compile("RAW_DOWNWELLING_IRRADIANCE([0-9]+)_STD");
  private static final Pattern RAW_UPWELLING_RADIANCE_REGEX = Pattern.compile("RAW_UPWELLING_RADIANCE([0-9]+)");
  private static final Pattern BETA_BACKSCATTERING_REGEX = Pattern.compile("BETA_BACKSCATTERING([0-9]+)");
  private static final Pattern BETA_BACKSCATTERING_STD_REGEX = Pattern.compile("BETA_BACKSCATTERING([0-9]+)_STD");
  private static final Pattern BETA_BACKSCATTERING_MED_REGEX = Pattern.compile("BETA_BACKSCATTERING([0-9]+)_MED");
  private static final Pattern TRANSMITTANCE_PARTICLE_BEAM_ATTENUATION_MED_REGEX = Pattern.compile(
      "TRANSMITTANCE_PARTICLE_BEAM_ATTENUATION([0-9]+)_MED");
  private static final Pattern TRANSMITTANCE_PARTICLE_BEAM_ATTENUATION_STD_REGEX = Pattern.compile(
      "TRANSMITTANCE_PARTICLE_BEAM_ATTENUATION([0-9]+)_STD");


  /*
  case 'PRES'
         o_attributeStruct = struct('name', 'PRES', ...
            'longName', 'Sea water pressure, equals 0 at sea-level', ...
            'standardName', 'sea_water_pressure', ...
            'fillValue', single(99999), ...
            'units', 'decibar', ...
            'validMin', single(0), ...
            'validMax', single(12000), ...
            'axis', 'Z', ...
            'cFormat', '%7.1f', ...
            'fortranFormat', 'F7.1', ...
            'resolution', single(0.1), ...
            'paramType', 'c', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 1);
   */

  // PRES	Sea water pressure, equals 0 at sea-level	The force per unit area exerted by the water column on a pressure sensor, corrected to read zero at sea level. Local_Attributes:{long_name:Sea water pressure, equals 0 at sea-level; standard_name:sea_water_pressure; units:decibar; valid_min:0.f; valid_max:12000.f; fill_value:99999.f}. Properties:{category:c; data_type:float}	2025-11-25
  private static final ParameterAttributes<Float> PRESS_PARAM_ATTR = new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Sea water pressure, equals 0 at sea-level";
    }

    @Override
    public String getStandardName() {
      return "sea_water_pressure";
    }

    @Override
    public String getUnits() {
      return "decibar";
    }

    @Override
    public Float getValidMin() {
      return 0f;
    }

    @Override
    public Float getValidMax() {
      return 12000f;
    }

    @Override
    public String getCFormat() {
      return "%7.1f";
    }

    @Override
    public String getFortranFormat() {
      return "F7.1";
    }

    @Override
    public Float getResolution() {
      return 0.1f;
    }

    @Override
    public String getAxis() {
      return "Z";
    }

  };

  /*
  case 'PRES_MED'
         o_attributeStruct = struct('name', 'PRES_MED', ...
            'longName', 'Median value of sea water pressure, equals 0 at sea-level', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'decibar', ...
            'validMin', single(0), ...
            'validMax', single(12000), ...
            'axis', '', ...
            'cFormat', '%7.1f', ...
            'fortranFormat', 'F7.1', ...
            'resolution', single(0.1), ...
            'paramType', 'i', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);
   */
  private static final ParameterAttributes<Float> PRESS_MED_PARAM_ATTR = new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Median value of sea water pressure, equals 0 at sea-level";
    }

    @Override
    public String getUnits() {
      return "decibar";
    }

    @Override
    public Float getValidMin() {
      return 0f;
    }

    @Override
    public Float getValidMax() {
      return 12000f;
    }

    @Override
    public String getCFormat() {
      return "%7.1f";
    }

    @Override
    public String getFortranFormat() {
      return "F7.1";
    }

    @Override
    public Float getResolution() {
      return 0.1f;
    }

  };

  /*
  case 'TEMP'
         o_attributeStruct = struct('name', 'TEMP', ...
            'longName', 'Sea temperature in-situ ITS-90 scale', ...
            'standardName', 'sea_water_temperature', ...
            'fillValue', single(99999), ...
            'units', 'degree_Celsius', ...
            'validMin', single(-2.5), ...
            'validMax', single(40), ...
            'axis', '', ...
            'cFormat', '%9.3f', ...
            'fortranFormat', 'F9.3', ...
            'resolution', single(0.001), ...
            'paramType', 'c', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 1);
   */

  // TEMP	Sea temperature in-situ ITS-90 scale	Temperature of the water body expressed against the International Temperature Scale 1990 (ITS-90) calibration standard. Local_Attributes:{long_name:Sea temperature in-situ ITS-90 scale; standard_name:sea_water_temperature; units:degree_Celsius; valid_min:-2.5f; valid_max:40.f; fill_value:99999.f}. Properties:{category:c; data_type:float}	2025-11-25
  private static final ParameterAttributes<Float> TEMP_PARAM_ATTR = new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Sea temperature in-situ ITS-90 scale";
    }

    @Override
    public String getStandardName() {
      return "sea_water_temperature";
    }

    @Override
    public String getUnits() {
      return "degree_Celsius";
    }

    @Override
    public Float getValidMin() {
      return -2.5f;
    }

    @Override
    public Float getValidMax() {
      return 40.0f;
    }

    @Override
    public String getCFormat() {
      return "%9.3f";
    }

    @Override
    public String getFortranFormat() {
      return "F9.3";
    }

    @Override
    public Float getResolution() {
      return 0.001f;
    }
  };

  /*
  case 'TEMP_STD'
         o_attributeStruct = struct('name', 'TEMP_STD', ...
            'longName', 'Standard deviation of sea temperature in-situ ITS-90 scale', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'degree_Celsius', ...
            'validMin', single(-2.5), ...
            'validMax', single(40), ...
            'axis', '', ...
            'cFormat', '%9.3f', ...
            'fortranFormat', 'F9.3', ...
            'resolution', single(0.001), ...
            'paramType', 'i', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);
   */
  private static final ParameterAttributes<Float> TEMP_STD_PARAM_ATTR = new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Standard deviation of sea temperature in-situ ITS-90 scale";
    }

    @Override
    public String getUnits() {
      return "degree_Celsius";
    }

    @Override
    public Float getValidMin() {
      return -2.5f;
    }

    @Override
    public Float getValidMax() {
      return 40.0f;
    }

    @Override
    public String getCFormat() {
      return "%9.3f";
    }

    @Override
    public String getFortranFormat() {
      return "F9.3";
    }

    @Override
    public Float getResolution() {
      return 0.001f;
    }
  };

  /*
  case 'TEMP_MED'
         o_attributeStruct = struct('name', 'TEMP_MED', ...
            'longName', 'Median value of sea temperature in-situ ITS-90 scale', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'degree_Celsius', ...
            'validMin', single(-2.5), ...
            'validMax', single(40), ...
            'axis', '', ...
            'cFormat', '%9.3f', ...
            'fortranFormat', 'F9.3', ...
            'resolution', single(0.001), ...
            'paramType', 'i', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);
   */
  private static final ParameterAttributes<Float> TEMP_MED_PARAM_ATTR = new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Median value of sea temperature in-situ ITS-90 scale";
    }

    @Override
    public String getUnits() {
      return "degree_Celsius";
    }

    @Override
    public Float getValidMin() {
      return -2.5f;
    }

    @Override
    public Float getValidMax() {
      return 40.0f;
    }

    @Override
    public String getCFormat() {
      return "%9.3f";
    }

    @Override
    public String getFortranFormat() {
      return "F9.3";
    }

    @Override
    public Float getResolution() {
      return 0.001f;
    }
  };

  /*
        case 'DOXY'
         o_attributeStruct = struct('name', 'DOXY', ...
            'longName', 'Dissolved oxygen', ...
            'standardName', 'moles_of_oxygen_per_unit_mass_in_sea_water', ...
            'fillValue', single(99999), ...
            'units', 'micromole/kg', ...
            'validMin', single(-5), ...
            'validMax', single(600), ...
            'axis', '', ...
            'cFormat', '%9.3f', ...
            'fortranFormat', 'F9.3', ...
            'resolution', single(0.001), ...
            'paramType', 'b', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 1);
   */

  // DOXY	Dissolved oxygen	Dissolved oxygen concentration. Local_Attributes:{long_name:Dissolved oxygen; standard_name:moles_of_oxygen_per_unit_mass_in_sea_water; units:micromole/kg; valid_min:-5.f; valid_max:600.f; fill_value:99999.f}. Properties:{category:b; data_type:float}	2025-11-25
  private static final ParameterAttributes<Float> DOXY_PARAM_ATTR = new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Dissolved oxygen";
    }

    @Override
    public String getStandardName() {
      return "moles_of_oxygen_per_unit_mass_in_sea_water";
    }

    @Override
    public String getUnits() {
      return "micromole/kg";
    }

    @Override
    public Float getValidMin() {
      return -5f;
    }

    @Override
    public Float getValidMax() {
      return 600f;
    }

    @Override
    public String getCFormat() {
      return "%9.3f";
    }

    @Override
    public String getFortranFormat() {
      return "F9.3";
    }

    @Override
    public Float getResolution() {
      return 0.001f;
    }

  };

  /*

  case 'PSAL'
         o_attributeStruct = struct('name', 'PSAL', ...
            'longName', 'Practical salinity', ...
            'standardName', 'sea_water_salinity', ...
            'fillValue', single(99999), ...
            'units', 'psu', ...
            'validMin', single(2), ...
            'validMax', single(41), ...
            'axis', '', ...
            'cFormat', '%9.3f', ...
            'fortranFormat', 'F9.3', ...
            'resolution', single(0.001), ...
            'paramType', 'c', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 1);
   */

  // PSAL	Practical salinity	Practical salinity of the water body by conductivity cell and computation using the UNESCO 1983 algorithm. Local_Attributes:{long_name:Practical salinity; standard_name:sea_water_salinity; units:psu; valid_min:2.f; valid_max:41.f; fill_value:99999.f}. Properties:{category:c; data_type:float}	2025-11-25
  private static final ParameterAttributes<Float> PSAL_PARAM_ATTR = new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Practical salinity";
    }

    @Override
    public String getStandardName() {
      return "sea_water_salinity";
    }

    @Override
    public String getUnits() {
      return "psu";
    }

    @Override
    public Float getValidMin() {
      return 2.0f;
    }

    @Override
    public Float getValidMax() {
      return 41.0f;
    }

    @Override
    public String getCFormat() {
      return "%9.3f";
    }

    @Override
    public String getFortranFormat() {
      return "F9.3";
    }

    @Override
    public Float getResolution() {
      return 0.001f;
    }
  };

  /*
   case 'PSAL_STD'
         o_attributeStruct = struct('name', 'PSAL_STD', ...
            'longName', 'Standard deviation of practical salinity', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'psu', ...
            'validMin', single(2), ...
            'validMax', single(41), ...
            'axis', '', ...
            'cFormat', '%9.3f', ...
            'fortranFormat', 'F9.3', ...
            'resolution', single(0.001), ...
            'paramType', 'i', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);

            */
  private static final ParameterAttributes<Float> PSAL_STD_PARAM_ATTR = new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Standard deviation of practical salinity";
    }

    @Override
    public String getUnits() {
      return "psu";
    }

    @Override
    public Float getValidMin() {
      return 2.0f;
    }

    @Override
    public Float getValidMax() {
      return 41.0f;
    }

    @Override
    public String getCFormat() {
      return "%9.3f";
    }

    @Override
    public String getFortranFormat() {
      return "F9.3";
    }

    @Override
    public Float getResolution() {
      return 0.001f;
    }
  };


  /*

      case 'PSAL_MED'
         o_attributeStruct = struct('name', 'PSAL_MED', ...
            'longName', 'Median value of practical salinity', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'psu', ...
            'validMin', single(2), ...
            'validMax', single(41), ...
            'axis', '', ...
            'cFormat', '%9.3f', ...
            'fortranFormat', 'F9.3', ...
            'resolution', single(0.001), ...
            'paramType', 'i', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);
   */
  private static final ParameterAttributes<Float> PSAL_MED_PARAM_ATTR = new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Median value of practical salinity";
    }

    @Override
    public String getUnits() {
      return "psu";
    }

    @Override
    public Float getValidMin() {
      return 2.0f;
    }

    @Override
    public Float getValidMax() {
      return 41.0f;
    }

    @Override
    public String getCFormat() {
      return "%9.3f";
    }

    @Override
    public String getFortranFormat() {
      return "F9.3";
    }

    @Override
    public Float getResolution() {
      return 0.001f;
    }
  };

  /*

    case 'DOWNWELLING_PAR'
         o_attributeStruct = struct('name', 'DOWNWELLING_PAR', ...
            'longName', 'Downwelling photosynthetic available radiation', ...
            'standardName', 'downwelling_photosynthetic_photon_flux_in_sea_water', ...
            'fillValue', single(99999), ...
            'units', 'microMoleQuanta/m^2/sec', ...
            'validMin', '', ...
            'validMax', '', ...
            'axis', '', ...
            'cFormat', '%.3f', ...
            'fortranFormat', 'F.3', ...
            'resolution', single(0.001), ...
            'paramType', 'b', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 1);

   */

  private static final ParameterAttributes<Float> DOWNWELLING_PAR_PARAM_ATTR = new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Downwelling photosynthetic available radiation";
    }

    @Override
    public String getStandardName() {
      return "downwelling_photosynthetic_photon_flux_in_sea_water";
    }

    @Override
    public String getUnits() {
      return "microMoleQuanta/m^2/sec";
    }

    @Override
    public String getCFormat() {
      return "%.3f";
    }

    @Override
    public String getFortranFormat() {
      return "F.3";
    }

    @Override
    public Float getResolution() {
      return 0.001f;
    }

  };


  /*
        case 'NITRATE'
         o_attributeStruct = struct('name', 'NITRATE', ...
            'longName', 'Nitrate', ...
            'standardName', 'moles_of_nitrate_per_unit_mass_in_sea_water', ...
            'fillValue', single(99999), ...
            'units', 'micromole/kg', ...
            'validMin', '', ...
            'validMax', '', ...
            'axis', '', ...
            'cFormat', '%.2f', ...
            'fortranFormat', 'F.2', ...
            'resolution', single(0.01), ...
            'paramType', 'b', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 1);
   */

  private static final ParameterAttributes<Float> NITRATE_PARAM_ATTR = new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Nitrate";
    }

    @Override
    public String getStandardName() {
      return "moles_of_nitrate_per_unit_mass_in_sea_water";
    }

    @Override
    public String getUnits() {
      return "micromole/kg";
    }

    @Override
    public String getCFormat() {
      return "%.2f";
    }

    @Override
    public String getFortranFormat() {
      return "F.2";
    }

    @Override
    public Float getResolution() {
      return 0.01f;
    }

  };

  /*
  case 'IB_PH'
         o_attributeStruct = struct('name', 'IB_PH', ...
            'longName', 'Base current of pH sensor', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'nanoampere', ...
            'validMin', '', ...
            'validMax', '', ...
            'axis', '', ...
            'cFormat', '%.4f', ...
            'fortranFormat', 'F.4', ...
            'resolution', single(0.0001), ...
            'paramType', 'i', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);
   */

  // IB_PH	Base current of pH sensor	Base current of pH sensor chip. Local_Attributes:{long_name:Base current of pH sensor; standard_name:-; units:nanoampere; valid_min:-; valid_max:-; fill_value:99999.f}. Properties:{category:ib; data_type:float}	2025-11-25
  private static final ParameterAttributes<Float> IB_PH_PARAM_ATTR = new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Base current of pH sensor";
    }

    @Override
    public String getUnits() {
      return "nanoampere";
    }

    @Override
    public String getCFormat() {
      return "%.4f";
    }

    @Override
    public String getFortranFormat() {
      return "F.4";
    }

    @Override
    public Float getResolution() {
      return 0.0001f;
    }

  };


  /*
      case 'TRANSMITTANCE_PARTICLE_BEAM_ATTENUATION660_STD'
         o_attributeStruct = struct('name', 'TRANSMITTANCE_PARTICLE_BEAM_ATTENUATION660_STD', ...
            'longName', 'Standard deviation of beam attenuation from transmissometer sensor at 660 nanometers', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'count', ...
            'validMin', '', ...
            'validMax', '', ...
            'axis', '', ...
            'cFormat', '%.1f', ...
            'fortranFormat', 'F.1', ...
            'resolution', single(0.1), ...
            'paramType', 'i', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);
     */

  private static final Function<String, ParameterAttributes<Float>> TRANSMITTANCE_PARTICLE_BEAM_ATTENUATION_STD_PARAM_ATTR_FACTORY = diNum -> new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Standard deviation of beam attenuation from transmissometer sensor at " + diNum + " nanometers";
    }

    @Override
    public String getUnits() {
      return "count";
    }

    @Override
    public String getCFormat() {
      return "%.1f";
    }

    @Override
    public String getFortranFormat() {
      return "F.1";
    }

    @Override
    public Float getResolution() {
      return 0.1f;
    }

  };


  /*

      case 'TRANSMITTANCE_PARTICLE_BEAM_ATTENUATION660_MED'
         o_attributeStruct = struct('name', 'TRANSMITTANCE_PARTICLE_BEAM_ATTENUATION660_MED', ...
            'longName', 'Median value of beam attenuation from transmissometer sensor at 660 nanometers', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'count', ...
            'validMin', '', ...
            'validMax', '', ...
            'axis', '', ...
            'cFormat', '%.1f', ...
            'fortranFormat', 'F.1', ...
            'resolution', single(0.1), ...
            'paramType', 'i', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);
   */

  private static final Function<String, ParameterAttributes<Float>> TRANSMITTANCE_PARTICLE_BEAM_ATTENUATION_MED_PARAM_ATTR_FACTORY = diNum -> new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Median value of beam attenuation from transmissometer sensor at " + diNum + " nanometers";
    }

    @Override
    public String getUnits() {
      return "count";
    }

    @Override
    public String getCFormat() {
      return "%.1f";
    }

    @Override
    public String getFortranFormat() {
      return "F.1";
    }

    @Override
    public Float getResolution() {
      return 0.1f;
    }

  };


  // TRANSMITTANCE_PARTICLE_BEAM_ATTENUATION	Beam attenuation from transmissometer sensor at x nanometers	Raw beam attenuation signal from transmissometer sensor at unspecified wavelength. Local_Attributes:{long_name:Beam attenuation from transmissometer sensor at x nanometers; standard_name:-; units:count; valid_min:-; valid_max:-; fill_value:99999.f}. Properties:{category:ib; data_type:float}	2025-11-25
  private static final Function<String, ParameterAttributes<Float>> TRANSMITTANCE_PARTICLE_BEAM_ATTENUATION_PARAM_ATTR_FACTORY = diNum -> new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Beam attenuation from transmissometer sensor at " + diNum + " nanometers";
    }

    @Override
    public String getUnits() {
      return "count";
    }

    @Override
    public String getCFormat() {
      return "%d";
    }

    @Override
    public String getFortranFormat() {
      return "I";
    }

    @Override
    public Float getResolution() {
      return 1f;
    }

  };

  /*
  case 'BISULFIDE'
         o_attributeStruct = struct('name', 'BISULFIDE', ...
            'longName', 'Bisulfide', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'micromole/kg', ...
            'validMin', '', ...
            'validMax', '', ...
            'axis', '', ...
            'cFormat', '%.3f', ...
            'fortranFormat', 'F.3', ...
            'resolution', single(0.001), ...
            'paramType', 'b', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 1);
   */

  // BISULFIDE	Bisulfide	Bisulfide concentration (umol/kg). Local_Attributes:{long_name:Bisulfide; standard_name:-; units:micromole/kg; valid_min:-; valid_max:-; fill_value:99999.f}. Properties:{category:b; data_type:float}	2025-11-25
  private static final ParameterAttributes<Float> BISULFIDE_PARAM_ATTR = new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Bisulfide";
    }

    @Override
    public String getUnits() {
      return "micromole/kg";
    }

    @Override
    public String getCFormat() {
      return "%.3f";
    }

    @Override
    public String getFortranFormat() {
      return "F.3";
    }

    @Override
    public Float getResolution() {
      return 0.001f;
    }

  };

  /*
  case 'DPHASE_DOXY'
         o_attributeStruct = struct('name', 'DPHASE_DOXY', ...
            'longName', 'Calibrated phase shift reported by oxygen sensor', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'degree', ...
            'validMin', single(10), ...
            'validMax', single(70), ...
            'axis', '', ...
            'cFormat', '%8.2f', ...
            'fortranFormat', 'F8.2', ...
            'resolution', single(0.01), ...
            'paramType', 'i', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);
   */

  // DPHASE_DOXY	Calibrated phase shift reported by oxygen sensor	Calibrated phase, reported by Aanderaa optode 3830 sensor. Local_Attributes:{long_name:Calibrated phase shift reported by oxygen sensor; standard_name:-; units:degree; valid_min:10.f; valid_max:70.f; fill_value:99999.f}. Properties:{category:ib; data_type:float}	2025-11-25
  private static final ParameterAttributes<Float> DPHASE_DOXY_PARAM_ATTR = new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Calibrated phase shift reported by oxygen sensor";
    }

    @Override
    public Float getValidMin() {
      return 10f;
    }

    @Override
    public Float getValidMax() {
      return 70f;
    }

    @Override
    public String getUnits() {
      return "degree";
    }

    @Override
    public String getCFormat() {
      return "%8.2f";
    }

    @Override
    public String getFortranFormat() {
      return "F8.2";
    }

    @Override
    public Float getResolution() {
      return 0.01f;
    }

  };

  // CHLA_FLUORESCENCE	Chlorophyll fluorescence with factory calibration	Chlorophyll fluorescence in relative unit with factory calibration Local_Attributes:{long_name:Chlorophyll fluorescence with factory calibration; standard_name:-; units:ru; valid_min:-0.2f; valid_max:100.f; fill_value:99999.f}. Properties:{category:b; data_type:float}	2025-11-25
  private static final ParameterAttributes<Float> CHLA_FLUORESCENCE_PARAM_ATTR = new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Chlorophyll fluorescence with factory calibration";
    }

    @Override
    public String getUnits() {
      return "ru";
    }

    @Override
    public String getCFormat() {
      return "%.3f";
    }

    @Override
    public String getFortranFormat() {
      return "F.3";
    }

    @Override
    public Float getResolution() {
      return 0.001f;
    }

  };

  /*
  case 'CHLA'
         o_attributeStruct = struct('name', 'CHLA', ...
            'longName', 'Chlorophyll-A', ...
            'standardName', 'mass_concentration_of_chlorophyll_a_in_sea_water', ...
            'fillValue', single(99999), ...
            'units', 'mg/m3', ...
            'validMin', '', ...
            'validMax', '', ...
            'axis', '', ...
            'cFormat', '%.3f', ...
            'fortranFormat', 'F.3', ...
            'resolution', single(0.025), ...
            'paramType', 'b', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 1);
   */

  // CHLA	Chlorophyll-A	Chlorophyll-a concentration (mg/m^3), reported by ECO3 sensor. Local_Attributes:{long_name:Chlorophyll-A; standard_name:mass_concentration_of_chlorophyll_a_in_sea_water; units:mg/m3; valid_min:-; valid_max:-; fill_value:99999.f}. Properties:{category:b; data_type:float}	2025-11-25
  private static final ParameterAttributes<Float> CHLA_PARAM_ATTR = new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Chlorophyll-A";
    }

    @Override
    public String getStandardName() {
      return "mass_concentration_of_chlorophyll_a_in_sea_water";
    }

    @Override
    public String getUnits() {
      return "mg/m3";
    }

    @Override
    public String getCFormat() {
      return "%.3f";
    }

    @Override
    public String getFortranFormat() {
      return "F.3";
    }

    @Override
    public Float getResolution() {
      return 0.025f;
    }

  };

  /*
  case 'FLUORESCENCE_CHLA'
         o_attributeStruct = struct('name', 'FLUORESCENCE_CHLA', ...
            'longName', 'Chlorophyll-A signal from fluorescence sensor', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'count', ...
            'validMin', '', ...
            'validMax', '', ...
            'axis', '', ...
            'cFormat', '%.1f', ...
            'fortranFormat', 'F.1', ...
            'resolution', single(0.1), ...
            'paramType', 'i', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);
   */

  // FLUORESCENCE_CHLA	Chlorophyll-A signal from fluorescence sensor	Raw fluorescence signal from chlorophyll-a fluorometer, reported by ECO3, FLNTU and FLBB sensors. Local_Attributes:{long_name:Chlorophyll-A signal from fluorescence sensor; standard_name:-; units:count; valid_min:-; valid_max:-; fill_value:99999.f}. Properties:{category:ib; data_type:float}	2025-11-25
  private static final ParameterAttributes<Float> FLUORESCENCE_CHLA_PARAM_ATTR = new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Chlorophyll-A signal from fluorescence sensor";
    }

    @Override
    public String getUnits() {
      return "count";
    }

    @Override
    public String getCFormat() {
      return "%.1f";
    }

    @Override
    public String getFortranFormat() {
      return "F.1";
    }

    @Override
    public Float getResolution() {
      return 0.1f;
    }

  };

  /*
  case 'FLUORESCENCE_CHLA_STD'
         o_attributeStruct = struct('name', 'FLUORESCENCE_CHLA_STD', ...
            'longName', 'Standard deviation of chlorophyll-A signal from fluorescence sensor', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'count', ...
            'validMin', '', ...
            'validMax', '', ...
            'axis', '', ...
            'cFormat', '%.1f', ...
            'fortranFormat', 'F.1', ...
            'resolution', single(0.1), ...
            'paramType', 'i', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);

            */

  private static final ParameterAttributes<Float> FLUORESCENCE_CHLA_STD_PARAM_ATTR = new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Standard deviation of chlorophyll-A signal from fluorescence sensor";
    }

    @Override
    public String getUnits() {
      return "count";
    }

    @Override
    public String getCFormat() {
      return "%.1f";
    }

    @Override
    public String getFortranFormat() {
      return "F.1";
    }

    @Override
    public Float getResolution() {
      return 0.1f;
    }

  };

  /*

      case 'FLUORESCENCE_CHLA_MED'
         o_attributeStruct = struct('name', 'FLUORESCENCE_CHLA_MED', ...
            'longName', 'Median value of chlorophyll-A signal from fluorescence sensor', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'count', ...
            'validMin', '', ...
            'validMax', '', ...
            'axis', '', ...
            'cFormat', '%.1f', ...
            'fortranFormat', 'F.1', ...
            'resolution', single(0.1), ...
            'paramType', 'i', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);
   */

  private static final ParameterAttributes<Float> FLUORESCENCE_CHLA_MED_PARAM_ATTR = new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Median value of chlorophyll-A signal from fluorescence sensor";
    }

    @Override
    public String getUnits() {
      return "count";
    }

    @Override
    public String getCFormat() {
      return "%.1f";
    }

    @Override
    public String getFortranFormat() {
      return "F.1";
    }

    @Override
    public Float getResolution() {
      return 0.1f;
    }

  };

  /*
  case 'CDOM'
         o_attributeStruct = struct('name', 'CDOM', ...
            'longName', 'Concentration of coloured dissolved organic matter in sea water', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'ppb', ...
            'validMin', '', ...
            'validMax', '', ...
            'axis', '', ...
            'cFormat', '%.3f', ...
            'fortranFormat', 'F.3', ...
            'resolution', single(0.001), ...
            'paramType', 'b', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 1);
   */

  // CDOM	Concentration of coloured dissolved organic matter in sea water	Coloured dissolved organic matter (CDOM) concentration. Local_Attributes:{long_name:Concentration of coloured dissolved organic matter in sea water; standard_name:-; units:ppb; valid_min:-; valid_max:-; fill_value:99999.f}. Properties:{category:b; data_type:float}	2025-11-25
  private static final ParameterAttributes<Float> CDOM_PARAM_ATTR = new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Concentration of coloured dissolved organic matter in sea water";
    }

    @Override
    public String getUnits() {
      return "ppb";
    }

    @Override
    public String getCFormat() {
      return "%.3f";
    }

    @Override
    public String getFortranFormat() {
      return "F.3";
    }

    @Override
    public Float getResolution() {
      return 0.001f;
    }

  };

  /*
  case 'COUNT_DOXY'
         o_attributeStruct = struct('name', 'COUNT_DOXY', ...
            'longName', 'Count reported by oxygen sensor', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'count', ...
            'validMin', '', ...
            'validMax', '', ...
            'axis', '', ...
            'cFormat', '%5.2f', ...
            'fortranFormat', 'F5.2', ...
            'resolution', single(0.01), ...
            'paramType', 'i', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);
   */

  // COUNT_DOXY	Count reported by oxygen sensor	Raw output signal from dissolved oxygen sensor. Local_Attributes:{long_name:Count reported by oxygen sensor; standard_name:-; units:count; valid_min:-; valid_max:-; fill_value:99999.f}. Properties:{category:ib; data_type:float}	2025-11-25
  private static final ParameterAttributes<Float> COUNT_DOXY_PARAM_ATTR = new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Count reported by oxygen sensor";
    }

    @Override
    public String getUnits() {
      return "count";
    }

    @Override
    public String getCFormat() {
      return "%5.2f";
    }

    @Override
    public String getFortranFormat() {
      return "F5.2";
    }

    @Override
    public Float getResolution() {
      return 0.01f;
    }

  };

  /*
  case 'TEMP_COUNT_DOXY' % for nc_create_synthetic_profile
         o_attributeStruct = struct('name', 'TEMP_COUNT_DOXY', ...
            'longName', 'Count which is expressive of uncalibrated temperature value reported by oxygen sensor', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'count', ...
            'validMin', '', ...
            'validMax', '', ...
            'axis', '', ...
            'cFormat', '%f', ...
            'fortranFormat', 'F', ...
            'resolution', single(1), ...
            'paramType', 'i', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);
   */

  // TEMP_COUNT_DOXY	Count which is expressive of uncalibrated temperature value reported by oxygen sensor	Raw temperature signal from thermistor on dissolved oxygen sensor. Local_Attributes:{long_name:Count which is expressive of uncalibrated temperature value reported by oxygen sensor; standard_name:-; units:count; valid_min:-; valid_max:-; fill_value:99999.f}. Properties:{category:ib; data_type:float}	2025-11-25
  private static final ParameterAttributes<Float> TEMP_COUNT_DOXY_PARAM_ATTR = new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Count which is expressive of uncalibrated temperature value reported by oxygen sensor";
    }

    @Override
    public String getUnits() {
      return "count";
    }

    @Override
    public String getCFormat() {
      return "%f";
    }

    @Override
    public String getFortranFormat() {
      return "F";
    }

    @Override
    public Float getResolution() {
      return 1f;
    }

  };

  /*
  case 'IK_PH'
         o_attributeStruct = struct('name', 'IK_PH', ...
            'longName', 'Counter electrode current of pH sensor', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'nanoampere', ...
            'validMin', '', ...
            'validMax', '', ...
            'axis', '', ...
            'cFormat', '%.4f', ...
            'fortranFormat', 'F.4', ...
            'resolution', single(0.0001), ...
            'paramType', 'i', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);
   */

  // IK_PH	Counter electrode current of pH sensor	Counter electrode current from pH sensor. Local_Attributes:{long_name:Counter electrode current of pH sensor; standard_name:-; units:nanoampere; valid_min:-; valid_max:-; fill_value:99999.f}. Properties:{category:ib; data_type:float}	2025-11-25
  private static final ParameterAttributes<Float> IK_PH_PARAM_ATTR = new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Counter electrode current of pH sensor";
    }

    @Override
    public String getUnits() {
      return "nanoampere";
    }

    @Override
    public String getCFormat() {
      return "%.4f";
    }

    @Override
    public String getFortranFormat() {
      return "F.4";
    }

    @Override
    public Float getResolution() {
      return 0.0001f;
    }

  };

  /*
  case 'VK_PH'
         o_attributeStruct = struct('name', 'VK_PH', ...
            'longName', 'Counter electrode voltage of pH sensor', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'volt', ...
            'validMin', '', ...
            'validMax', '', ...
            'axis', '', ...
            'cFormat', '%.5f', ...
            'fortranFormat', 'F.5', ...
            'resolution', single(0.00001), ...
            'paramType', 'i', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);
   */

  // VK_PH	Counter electrode voltage of pH sensor	Counter electrode signal from pH sensor. Local_Attributes:{long_name:Counter electrode voltage of pH sensor; standard_name:-; units:volt; valid_min:-; valid_max:-; fill_value:99999.f}. Properties:{category:ib; data_type:float}	2025-11-25
  private static final ParameterAttributes<Float> VK_PH_PARAM_ATTR = new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Counter electrode voltage of pH sensor";
    }

    @Override
    public String getUnits() {
      return "volt";
    }

    @Override
    public String getCFormat() {
      return "%.5f";
    }

    @Override
    public String getFortranFormat() {
      return "F.5";
    }

    @Override
    public Float getResolution() {
      return 0.00001f;
    }

  };

  /*

        case 'DOWN_IRRADIANCE380'
         o_attributeStruct = struct('name', 'DOWN_IRRADIANCE380', ...
            'longName', 'Downwelling irradiance at 380 nanometers', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'W/m^2/nm', ...
            'validMin', '', ...
            'validMax', '', ...
            'axis', '', ...
            'cFormat', '%.6f', ...
            'fortranFormat', 'F.6', ...
            'resolution', single(0.000001), ...
            'paramType', 'b', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 1);

      case 'DOWN_IRRADIANCE412'
         o_attributeStruct = struct('name', 'DOWN_IRRADIANCE412', ...
            'longName', 'Downwelling irradiance at 412 nanometers', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'W/m^2/nm', ...
            'validMin', '', ...
            'validMax', '', ...
            'axis', '', ...
            'cFormat', '%.6f', ...
            'fortranFormat', 'F.6', ...
            'resolution', single(0.000001), ...
            'paramType', 'b', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 1);

      case 'DOWN_IRRADIANCE443'
         o_attributeStruct = struct('name', 'DOWN_IRRADIANCE443', ...
            'longName', 'Downwelling irradiance at 443 nanometers', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'W/m^2/nm', ...
            'validMin', '', ...
            'validMax', '', ...
            'axis', '', ...
            'cFormat', '%.6f', ...
            'fortranFormat', 'F.6', ...
            'resolution', single(0.000001), ...
            'paramType', 'b', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 1);

      case 'DOWN_IRRADIANCE490'
         o_attributeStruct = struct('name', 'DOWN_IRRADIANCE490', ...
            'longName', 'Downwelling irradiance at 490 nanometers', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'W/m^2/nm', ...
            'validMin', '', ...
            'validMax', '', ...
            'axis', '', ...
            'cFormat', '%.6f', ...
            'fortranFormat', 'F.6', ...
            'resolution', single(0.000001), ...
            'paramType', 'b', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 1);

      case 'DOWN_IRRADIANCE555'
         o_attributeStruct = struct('name', 'DOWN_IRRADIANCE555', ...
            'longName', 'Downwelling irradiance at 555 nanometers', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'W/m^2/nm', ...
            'validMin', '', ...
            'validMax', '', ...
            'axis', '', ...
            'cFormat', '%.6f', ...
            'fortranFormat', 'F.6', ...
            'resolution', single(0.000001), ...
            'paramType', 'b', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 1);

      case 'DOWN_IRRADIANCE670'
         o_attributeStruct = struct('name', 'DOWN_IRRADIANCE670', ...
            'longName', 'Downwelling irradiance at 670 nanometers', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'W/m^2/nm', ...
            'validMin', '', ...
            'validMax', '', ...
            'axis', '', ...
            'cFormat', '%.6f', ...
            'fortranFormat', 'F.6', ...
            'resolution', single(0.000001), ...
            'paramType', 'b', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 1);

   */

  // DOWN_IRRADIANCE	Downwelling irradiance at x nanometers	Downwelling irradiance at unspecified wavelength, reported by OCR504 sensor (unit conversion). Local_Attributes:{long_name:Downwelling irradiance at x nanometers; standard_name:-; units:W/m^2/nm; valid_min:-; valid_max:-; fill_value:99999.f}. Properties:{category:b; data_type:float}	2025-11-25
  private static final Function<String, ParameterAttributes<Float>> DOWN_IRRADIANCE_PARAM_ATTR_FACTORY = diNum -> new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Downwelling irradiance at " + diNum + " nanometers";
    }

    @Override
    public String getUnits() {
      return "W/m^2/nm";
    }

    @Override
    public String getCFormat() {
      return "%.6f";
    }

    @Override
    public String getFortranFormat() {
      return "F.6";
    }

    @Override
    public Float getResolution() {
      return 0.000001f;
    }

  };

  /*
  case 'CNDC'
         o_attributeStruct = struct('name', 'CNDC', ...
            'longName', 'Electrical conductivity', ...
            'standardName', 'sea_water_electrical_conductivity', ...
            'fillValue', single(99999), ...
            'units', 'mhos/m', ...
            'validMin', single(0), ...
            'validMax', single(8.5), ...
            'axis', '', ...
            'cFormat', '%10.4f', ...
            'fortranFormat', 'F10.4', ...
            'resolution', single(0.0001), ...
            'paramType', 'c', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 1);
   */

  // CNDC	Electrical conductivity	Measure of seawater electrical conductivity. Local_Attributes:{long_name:Electrical conductivity; standard_name:sea_water_electrical_conductivity; units:mhos/m; valid_min:0.f; valid_max:8.5f; fill_value:99999.f}. Properties:{category:ic; data_type:float}	2025-11-25
  private static final ParameterAttributes<Float> CNDC_PARAM_ATTR = new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Electrical conductivity";
    }

    @Override
    public String getStandardName() {
      return "sea_water_electrical_conductivity";
    }

    @Override
    public Float getValidMin() {
      return 0f;
    }

    @Override
    public Float getValidMax() {
      return 8.5f;
    }

    @Override
    public String getUnits() {
      return "mhos/m";
    }

    @Override
    public String getCFormat() {
      return "%10.4f";
    }

    @Override
    public String getFortranFormat() {
      return "F10.4";
    }

    @Override
    public Float getResolution() {
      return 0.0001f;
    }

  };

  /*
  case 'MTIME'
         o_attributeStruct = struct('name', 'MTIME', ...
            'longName', 'Fractional day of the individual measurement relative to JULD of the station', ...
            'standardName', '', ...
            'fillValue', double(999999), ...
            'units', 'days', ...
            'validMin', double(-3), ...
            'validMax', double(3), ...
            'axis', '', ...
            'cFormat', '%.6f', ...
            'fortranFormat', 'F.6', ...
            'resolution', single(0.000001), ...
            'paramType', 'i', ...
            'paramNcType', 'NC_DOUBLE', ...
            'adjAllowed', 0);
   */

  //MTIME	Fractional day of the individual measurement relative to JULD of the station	Fractional day of the individual measurement relative to JULD of the station. Local_Attributes:{long_name:Fractional day of the individual measurement relative to JULD of the station; standard_name:-; units:days; valid_min:-3; valid_max:3; fill_value:999999}. Properties:{category:ic; data_type:double}	2025-11-25
  private static final ParameterAttributes<Float> MTIME_PARAM_ATTR = new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Fractional day of the individual measurement relative to JULD of the station";
    }

    @Override
    public String getUnits() {
      return "days";
    }

    @Override
    public String getCFormat() {
      return "%.6f";
    }

    @Override
    public String getFortranFormat() {
      return "F.6";
    }

    @Override
    public Float getResolution() {
      return 0.000001f;
    }

  };


  /*
  case 'FREQUENCY_DOXY'
         o_attributeStruct = struct('name', 'FREQUENCY_DOXY', ...
            'longName', 'Frequency reported by oxygen sensor', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'hertz', ...
            'validMin', single(0), ...
            'validMax', single(25000), ...
            'axis', '', ...
            'cFormat', '%7.1f', ...
            'fortranFormat', 'F7.1', ...
            'resolution', single(0.1), ...
            'paramType', 'i', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);
   */

  // FREQUENCY_DOXY	Frequency reported by oxygen sensor	Raw output signal from dissolved oxygen sensor. Local_Attributes:{long_name:Frequency reported by oxygen sensor; standard_name:-; units:hertz; valid_min:0.f; valid_max:25000.f; fill_value:99999.f}. Properties:{category:ib; data_type:float}	2025-11-25
  private static final ParameterAttributes<Float> FREQUENCY_DOXY_PARAM_ATTR = new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Frequency reported by oxygen sensor";
    }

    @Override
    public Float getValidMin() {
      return 0f;
    }

    @Override
    public Float getValidMax() {
      return 25000f;
    }

    @Override
    public String getUnits() {
      return "hertz";
    }

    @Override
    public String getCFormat() {
      return "%7.1f";
    }

    @Override
    public String getFortranFormat() {
      return "F7.1";
    }

    @Override
    public Float getResolution() {
      return 0.1f;
    }

  };


  /*
      case 'TILT' % for nc_create_synthetic_profile
         o_attributeStruct = struct('name', 'TILT', ...
            'longName', 'Inclination of the float axis in respect to the local vertical', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'degree', ...
            'validMin', '', ...
            'validMax', '', ...
            'axis', '', ...
            'cFormat', '%.1f', ...
            'fortranFormat', 'F.1', ...
            'resolution', single(0.1), ...
            'paramType', 'i', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);
   */

  // TILT	Inclination of the float axis in respect to the local vertical	Inclination of the float axis in respect to the local vertical. Local_Attributes:{long_name:Inclination of the float axis in respect to the local vertical; standard_name:-; units:degree; valid_min:-; valid_max:-; fill_value:99999.f}. Properties:{category:ib; data_type:float}	2025-11-25
  private static final ParameterAttributes<Float> TILT_PARAM_ATTR = new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Inclination of the float axis in respect to the local vertical";
    }

    @Override
    public String getUnits() {
      return "degree";
    }

    @Override
    public String getCFormat() {
      return "%.1f";
    }

    @Override
    public String getFortranFormat() {
      return "F.1";
    }

    @Override
    public Float getResolution() {
      return 0.1f;
    }

  };

  /*
  case 'UV_INTENSITY_DARK_NITRATE'
         o_attributeStruct = struct('name', 'UV_INTENSITY_DARK_NITRATE', ...
            'longName', 'Intensity of ultra violet flux dark measurement from nitrate sensor', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'count', ...
            'validMin', '', ...
            'validMax', '', ...
            'axis', '', ...
            'cFormat', '%d', ...
            'fortranFormat', 'I', ...
            'resolution', single(1), ...
            'paramType', 'i', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);
   */

  // UV_INTENSITY_DARK_NITRATE	Intensity of ultra violet flux dark measurement from nitrate sensor	Raw ultra-violet (UV) flux signal in darkness from nitrate sensor. Average of all 256 dark counts, reported by the SUNA sensor. Local_Attributes:{long_name:Intensity of ultra violet flux dark measurement from nitrate sensor; standard_name:-; units:count; valid_min:-; valid_max:-; fill_value:99999.f}. Properties:{category:ib; data_type:float}	2025-11-25
  private static final ParameterAttributes<Float> UV_INTENSITY_DARK_NITRATE_PARAM_ATTR = new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Intensity of ultra violet flux dark measurement from nitrate sensor";
    }

    @Override
    public String getUnits() {
      return "count";
    }

    @Override
    public String getCFormat() {
      return "%d";
    }

    @Override
    public String getFortranFormat() {
      return "I";
    }

    @Override
    public Float getResolution() {
      return 1f;
    }

  };

  /*
  case 'UV_INTENSITY_DARK_NITRATE_STD'
         o_attributeStruct = struct('name', 'UV_INTENSITY_DARK_NITRATE_STD', ...
            'longName', 'Standard deviation of intensity of ultra violet flux dark measurement from nitrate sensor', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'count', ...
            'validMin', '', ...
            'validMax', '', ...
            'axis', '', ...
            'cFormat', '%d', ...
            'fortranFormat', 'I', ...
            'resolution', single(1), ...
            'paramType', 'i', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);
   */
  private static final ParameterAttributes<Float> UV_INTENSITY_DARK_NITRATE_STD_PARAM_ATTR = new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Standard deviation of intensity of ultra violet flux dark measurement from nitrate sensor";
    }

    @Override
    public String getUnits() {
      return "count";
    }

    @Override
    public String getCFormat() {
      return "%d";
    }

    @Override
    public String getFortranFormat() {
      return "I";
    }

    @Override
    public Float getResolution() {
      return 1f;
    }

  };


  /*
        case 'UV_INTENSITY_NITRATE'
         o_attributeStruct = struct('name', 'UV_INTENSITY_NITRATE', ...
            'longName', 'Intensity of ultra violet flux from nitrate sensor', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'count', ...
            'validMin', '', ...
            'validMax', '', ...
            'axis', '', ...
            'cFormat', '%d', ...
            'fortranFormat', 'I', ...
            'resolution', single(1), ...
            'paramType', 'i', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);
   */

  // UV_INTENSITY_NITRATE	Intensity of ultra violet flux from nitrate sensor	Raw ultra-violet (UV) flux signal from nitrate sensor. Measurement in counts proportional to light intensity. Multi-spectral parameter requiring an extra N_VALUESxxx dimension in the NetCDF files; xxx is a number between 0 and 255 representing the number of wavelengths transmitted. Local_Attributes:{long_name:Intensity of ultra violet flux from nitrate sensor; standard_name:-; units:count; valid_min:-; valid_max:-; fill_value:99999.f}. Properties:{category:ib; data_type:float; extra_dim:[N_VALUES]}	2025-11-25
  private static final ParameterAttributes<Float> UV_INTENSITY_NITRATE_PARAM_ATTR = new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Intensity of ultra violet flux from nitrate sensor";
    }

    @Override
    public String getUnits() {
      return "count";
    }

    @Override
    public String getCFormat() {
      return "%d";
    }

    @Override
    public String getFortranFormat() {
      return "I";
    }

    @Override
    public Float getResolution() {
      return 1f;
    }

  };

  /*
  case 'UV_INTENSITY_DARK_SEAWATER_NITRATE'
         o_attributeStruct = struct('name', 'UV_INTENSITY_DARK_SEAWATER_NITRATE', ...
            'longName', 'Intensity of ultra-violet flux dark sea water from nitrate sensor', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'count', ...
            'validMin', '', ...
            'validMax', '', ...
            'axis', '', ...
            'cFormat', '%d', ...
            'fortranFormat', 'I', ...
            'resolution', single(1), ...
            'paramType', 'i', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);
   */

  // UV_INTENSITY_DARK_SEAWATER_NITRATE	Intensity of ultra-violet flux dark sea water from nitrate sensor	Raw ultra-violet (UV) flux signal in seawater and darkness from nitrate sensor. From light absorption on pixels at shortest wavelengths of array, reported by the SUNA sensor. Local_Attributes:{long_name:Intensity of ultra-violet flux dark sea water from nitrate sensor; standard_name:-; units:count; valid_min:-; valid_max:-; fill_value:99999.f}. Properties:{category:ib; data_type:float}	2025-11-25
  private static final ParameterAttributes<Float> UV_INTENSITY_DARK_SEAWATER_NITRATE_PARAM_ATTR = new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Intensity of ultra-violet flux dark sea water from nitrate sensor";
    }

    @Override
    public String getUnits() {
      return "count";
    }

    @Override
    public String getCFormat() {
      return "%d";
    }

    @Override
    public String getFortranFormat() {
      return "I";
    }

    @Override
    public Float getResolution() {
      return 1f;
    }

  };

  /*
  case 'TEMP_NITRATE'
         o_attributeStruct = struct('name', 'TEMP_NITRATE', ...
            'longName', 'Internal temperature of the SUNA sensor', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'degree_Celsius', ...
            'validMin', '', ...
            'validMax', '', ...
            'axis', '', ...
            'cFormat', '%.1f', ...
            'fortranFormat', 'F.1', ...
            'resolution', single(0.1), ...
            'paramType', 'i', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);
   */

  // TEMP_NITRATE	Internal temperature of the SUNA sensor	Internal temperature of the nitrate sensor, reported by the SUNA sensor from Remocean floats. Local_Attributes:{long_name:Internal temperature of the SUNA sensor; standard_name:-; units:degree_Celsius; valid_min:-; valid_max:-; fill_value:99999.f}. Properties:{category:ib; data_type:float}	2025-11-25
  private static final ParameterAttributes<Float> TEMP_NITRATE_PARAM_ATTR = new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Internal temperature of the SUNA sensor";
    }

    @Override
    public String getUnits() {
      return "degree_Celsius";
    }

    @Override
    public String getCFormat() {
      return "%.1f";
    }

    @Override
    public String getFortranFormat() {
      return "F.1";
    }

    @Override
    public Float getResolution() {
      return 0.1f;
    }

  };

  /*
  case 'TEMP_CNDC'
         o_attributeStruct = struct('name', 'TEMP_CNDC', ...
            'longName', 'Internal temperature of the conductivity cell', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'degree_Celsius', ...
            'validMin', single(-2), ...
            'validMax', single(40), ...
            'axis', '', ...
            'cFormat', '%9.3f', ...
            'fortranFormat', 'F9.3', ...
            'resolution', single(0.001), ...
            'paramType', 'j', ... % 'I' parameter stored in core files
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);
   */

  // TEMP_CNDC	Internal temperature of the conductivity cell	Internal temperature of the conductivity cell. Local_Attributes:{long_name:Internal temperature of the conductivity cell; standard_name:-; units:degree_Celsius; valid_min:-2.f; valid_max:40.f; fill_value:99999.f}. Properties:{category:ic; data_type:float}	2025-11-25
  private static final ParameterAttributes<Float> TEMP_CNDC_PARAM_ATTR = new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Internal temperature of the conductivity cell";
    }

    @Override
    public Float getValidMin() {
      return -2f;
    }

    @Override
    public Float getValidMax() {
      return 40f;
    }

    @Override
    public String getUnits() {
      return "degree_Celsius";
    }

    @Override
    public String getCFormat() {
      return "%9.3f";
    }

    @Override
    public String getFortranFormat() {
      return "F9.3";
    }

    @Override
    public Float getResolution() {
      return 0.001f;
    }

  };

  /*
        case 'MOLAR_NITRATE'
         o_attributeStruct = struct('name', 'MOLAR_NITRATE', ...
            'longName', 'Nitrate', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'micromole/l', ...
            'validMin', '', ...
            'validMax', '', ...
            'axis', '', ...
            'cFormat', '%.3f', ...
            'fortranFormat', 'F.3', ...
            'resolution', single(0.001), ...
            'paramType', 'i', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);
   */

  // MOLAR_NITRATE	Molar nitrate	Nitrate concentration, reported by the SUNA sensor. Local_Attributes:{long_name:Nitrate; standard_name:-; units:micromole/l; valid_min:-; valid_max:-; fill_value:99999.f}. Properties:{category:ib; data_type:float}	2025-11-25
  private static final ParameterAttributes<Float> MOLAR_NITRATE_PARAM_ATTR = new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Nitrate";
    }

    @Override
    public String getUnits() {
      return "micromole/l";
    }

    @Override
    public String getCFormat() {
      return "%.3f";
    }

    @Override
    public String getFortranFormat() {
      return "F.3";
    }

    @Override
    public Float getResolution() {
      return 0.001f;
    }

  };

  /*
        case 'FIT_ERROR_NITRATE'
         o_attributeStruct = struct('name', 'FIT_ERROR_NITRATE', ...
            'longName', 'Nitrate fit error', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'dimensionless', ...
            'validMin', '', ...
            'validMax', '', ...
            'axis', '', ...
            'cFormat', '%.3f', ...
            'fortranFormat', 'F.3', ...
            'resolution', single(0.001), ...
            'paramType', 'i', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);

   */

  // FIT_ERROR_NITRATE	Nitrate fit error	Calibration fit error of nitrate, SUNA sensro key variable in QC. Local_Attributes:{long_name:Nitrate fit error; standard_name:-; units:dimensionless; valid_min:-; valid_max:-; fill_value:99999.f}. Properties:{category:ib; data_type:float}	2025-11-25
  private static final ParameterAttributes<Float> FIT_ERROR_NITRATE_PARAM_ATTR = new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Nitrate fit error";
    }

    @Override
    public String getUnits() {
      return "dimensionless";
    }

    @Override
    public String getCFormat() {
      return "%.3f";
    }

    @Override
    public String getFortranFormat() {
      return "F.3";
    }

    @Override
    public Float getResolution() {
      return 0.001f;
    }

  };

  /*
      case 'NB_SAMPLE_CTD'
         o_attributeStruct = struct('name', 'NB_SAMPLE_CTD', ...
            'longName', 'Number of samples in each pressure bin for the CTD', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'dimensionless', ...
            'validMin', '', ...
            'validMax', '', ...
            'axis', '', ...
            'cFormat', '%d', ...
            'fortranFormat', 'I', ...
            'resolution', single(1), ...
            'paramType', 'j', ... % 'I' parameter stored in core files
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);
   */

  // NB_SAMPLE_CTD	Number of samples in each pressure bin for the CTD	Number of CTD samples in each pressure bin. Local_Attributes:{long_name:Number of samples in each pressure bin for the CTD; standard_name:-; units:count; valid_min:-; valid_max:-; fill_value:NC_FILL_SHORT}. Properties:{category:ic; data_type:short}	2025-11-25
  private static final ParameterAttributes<Float> NB_SAMPLE_CTD_PARAM_ATTR = new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Number of samples in each pressure bin for the CTD";
    }

    @Override
    public String getUnits() {
      return "dimensionless";
    }

    @Override
    public String getCFormat() {
      return "%d";
    }

    @Override
    public String getFortranFormat() {
      return "I";
    }

    @Override
    public Float getResolution() {
      return 1f;
    }

  };

  /*
        case 'NB_SAMPLE_SFET'
         o_attributeStruct = struct('name', 'NB_SAMPLE_SFET', ...
            'longName', 'Number of samples in each pressure bin for the SFET', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'dimensionless', ...
            'validMin', '', ...
            'validMax', '', ...
            'axis', '', ...
            'cFormat', '%d', ...
            'fortranFormat', 'I', ...
            'resolution', single(1), ...
            'paramType', 'i', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);
   */

  // NB_SAMPLE_SFET	Number of samples in each pressure bin for the SFET	Number of SFET samples in each pressure bin. Local_Attributes:{long_name:Number of samples in each pressure bin for the SFET; standard_name:-; units:count; valid_min:-; valid_max:-; fill_value:NC_FILL_SHORT}. Properties:{category:ib; data_type:short}	2025-11-25
  private static final ParameterAttributes<Float> NB_SAMPLE_SFET_PARAM_ATTR = new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Number of samples in each pressure bin for the SFET";
    }


    @Override
    public String getUnits() {
      return "dimensionless";
    }

    @Override
    public String getCFormat() {
      return "%d";
    }

    @Override
    public String getFortranFormat() {
      return "I";
    }

    @Override
    public Float getResolution() {
      return 1f;
    }

  };

  /*
     case 'LED_FLASHING_COUNT_DOXY' % for nc_create_synthetic_profile
         o_attributeStruct = struct('name', 'LED_FLASHING_COUNT_DOXY', ...
            'longName', 'Number of times oxygen sensor flashing to measure oxygen', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'count', ...
            'validMin', '', ...
            'validMax', '', ...
            'axis', '', ...
            'cFormat', '%f', ...
            'fortranFormat', 'F', ...
            'resolution', single(1), ...
            'paramType', 'i', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);
   */

  // LED_FLASHING_COUNT_DOXY	Number of times oxygen sensor flashing to measure oxygen	Light-Emitting Diode (LED) flashing frequency from dissolved oxygen sensor, reported by ARO-FT optode sensor. Local_Attributes:{long_name:Number of times oxygen sensor flashing to measure oxygen; standard_name:-; units:count; valid_min:-; valid_max:-; fill_value:NC_FILL_DOUBLE}. Properties:{category:ib; data_type:double}	2025-11-25
  private static final ParameterAttributes<Float> LED_FLASHING_COUNT_DOXY_PARAM_ATTR = new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Number of times oxygen sensor flashing to measure oxygen";
    }

    @Override
    public String getUnits() {
      return "count";
    }

    @Override
    public String getCFormat() {
      return "%f";
    }

    @Override
    public String getFortranFormat() {
      return "F";
    }

    @Override
    public Float getResolution() {
      return 1f;
    }

  };

  /*
        case 'MLPL_DOXY'
         o_attributeStruct = struct('name', 'MLPL_DOXY', ...
            'longName', 'Oxygen concentration reported by the oxygen sensor', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'ml/l', ...
            'validMin', single(0), ...
            'validMax', single(650), ...
            'axis', '', ...
            'cFormat', '%9.3f', ...
            'fortranFormat', 'F9.3', ...
            'resolution', single(0.001), ...
            'paramType', 'i', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);
   */

  // MLPL_DOXY	Oxygen concentration reported by the oxygen sensor	Dissolved oxygen concentration uncorrected for salinity and pressure effects. Local_Attributes:{long_name:Oxygen concentration reported by the oxygen sensor; standard_name:-; units:ml/l; valid_min:0.f; valid_max:650.f; fill_value:99999.f}. Properties:{category:ib; data_type:float}	2025-11-25
  private static final ParameterAttributes<Float> MLPL_DOXY_PARAM_ATTR = new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Oxygen concentration reported by the oxygen sensor";
    }

    @Override
    public Float getValidMin() {
      return 0f;
    }

    @Override
    public Float getValidMax() {
      return 650f;
    }

    @Override
    public String getUnits() {
      return "ml/l";
    }

    @Override
    public String getCFormat() {
      return "%9.3f";
    }

    @Override
    public String getFortranFormat() {
      return "F9.3";
    }

    @Override
    public Float getResolution() {
      return 0.001f;
    }

  };

  /*
  case 'PPOX_DOXY'
         o_attributeStruct = struct('name', 'PPOX_DOXY', ...
            'longName', 'Partial pressure of oxygen', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'millibar', ...
            'validMin', single(-5), ...
            'validMax', single(5000), ...
            'axis', '', ...
            'cFormat', '%8.2f', ...
            'fortranFormat', 'F8.2', ...
            'resolution', single(0.01), ...
            'paramType', 'i', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 1);
   */

  // PPOX_DOXY	Partial pressure of oxygen	Oxygen partial pressure, alternative oxygen quantity of oxygen optodes (for in-air measurements). Local_Attributes:{long_name:Partial pressure of oxygen; standard_name:-; units:millibar; valid_min:-5.f; valid_max:5000.f; fill_value:99999.f}. Properties:{category:ib; data_type:float}	2025-11-25
  private static final ParameterAttributes<Float> PPOX_DOXY_PARAM_ATTR = new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Partial pressure of oxygen";
    }

    @Override
    public Float getValidMin() {
      return -5f;
    }

    @Override
    public Float getValidMax() {
      return 5000f;
    }

    @Override
    public String getUnits() {
      return "millibar";
    }

    @Override
    public String getCFormat() {
      return "%8.2f";
    }

    @Override
    public String getFortranFormat() {
      return "F8.2";
    }

    @Override
    public Float getResolution() {
      return 0.01f;
    }

  };

  /*
       case 'BBP470' % for nc_create_synthetic_profile
         o_attributeStruct = struct('name', 'BBP470', ...
            'longName', 'Particle backscattering at 470 nanometers', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'm-1', ...
            'validMin', '', ...
            'validMax', '', ...
            'axis', '', ...
            'cFormat', '%.3f', ...
            'fortranFormat', 'F.3', ...
            'resolution', single(0.001), ...
            'paramType', 'b', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 1);

      case 'BBP532'
         o_attributeStruct = struct('name', 'BBP532', ...
            'longName', 'Particle backscattering at 532 nanometers', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'm-1', ...
            'validMin', '', ...
            'validMax', '', ...
            'axis', '', ...
            'cFormat', '%.3f', ...
            'fortranFormat', 'F.3', ...
            'resolution', single(0.001), ...
            'paramType', 'b', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 1);

      case 'BBP700'
         o_attributeStruct = struct('name', 'BBP700', ...
            'longName', 'Particle backscattering at 700 nanometers', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'm-1', ...
            'validMin', '', ...
            'validMax', '', ...
            'axis', '', ...
            'cFormat', '%.3f', ...
            'fortranFormat', 'F.3', ...
            'resolution', single(0.001), ...
            'paramType', 'b', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 1);
   */


  // BBP	Particle backscattering at x nanometers	Particle backscattering at unspecified wavelength, reported by ECO3 sensor. Local_Attributes:{long_name:Particle backscattering at x nanometers; standard_name:-; units:m-1; valid_min:-; valid_max:-; fill_value:99999.f}. Properties:{category:b; data_type:float}	2025-11-25
  private static final Function<String, ParameterAttributes<Float>> BBP_PARAM_ATTR_FACTORY = (diNum) -> new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Particle backscattering at " + diNum + " nanometers";
    }

    @Override
    public String getUnits() {
      return "m-1";
    }

    @Override
    public String getCFormat() {
      return "%.3f";
    }

    @Override
    public String getFortranFormat() {
      return "F.3";
    }

    @Override
    public Float getResolution() {
      return 0.001f;
    }

  };

  /*

        case 'CP660'
         o_attributeStruct = struct('name', 'CP660', ...
            'longName', 'Particle beam attenuation at 660 nanometers', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'm-1', ...
            'validMin', '', ...
            'validMax', '', ...
            'axis', '', ...
            'cFormat', '%.3f', ...
            'fortranFormat', 'F.3', ...
            'resolution', single(0.001), ...
            'paramType', 'b', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 1);

      case 'CP660_STD'
         o_attributeStruct = struct('name', 'CP660_STD', ...
            'longName', 'Standard deviation of particle beam attenuation at 660 nanometers', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'm-1', ...
            'validMin', '', ...
            'validMax', '', ...
            'axis', '', ...
            'cFormat', '%.3f', ...
            'fortranFormat', 'F.3', ...
            'resolution', single(0.001), ...
            'paramType', 'b', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);

      case 'CP660_MED'
         o_attributeStruct = struct('name', 'CP660_MED', ...
            'longName', 'Median value of particle beam attenuation at 660 nanometers', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'm-1', ...
            'validMin', '', ...
            'validMax', '', ...
            'axis', '', ...
            'cFormat', '%.3f', ...
            'fortranFormat', 'F.3', ...
            'resolution', single(0.001), ...
            'paramType', 'b', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);


   */

  // CP	Particle beam attenuation at x nanometers	Particle beam attenuation at unspecified wavelength, reported by C-Rover sensor. Local_Attributes:{long_name:Particle beam attenuation at x nanometers; standard_name:-; units:m-1; valid_min:-; valid_max:-; fill_value:99999.f}. Properties:{category:b; data_type:float}	2025-11-25
  private static final Function<String, ParameterAttributes<Float>> CP_PARAM_ATTR_FACTORY = diNum -> new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Particle beam attenuation at " + diNum + " nanometers";
    }

    @Override
    public String getUnits() {
      return "m-1";
    }

    @Override
    public String getCFormat() {
      return "%.3f";
    }

    @Override
    public String getFortranFormat() {
      return "F.3";
    }

    @Override
    public Float getResolution() {
      return 0.001f;
    }

  };

  /*
  case 'PHASE_DELAY_DOXY'
         o_attributeStruct = struct('name', 'PHASE_DELAY_DOXY', ...
            'longName', 'Phase delay reported by oxygen sensor', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'microsecond', ...
            'validMin', single(0), ...
            'validMax', single(99999), ...
            'axis', '', ...
            'cFormat', '%d', ...
            'fortranFormat', 'I', ...
            'resolution', single(1), ...
            'paramType', 'i', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);
   */

  // PHASE_DELAY_DOXY	Phase delay reported by oxygen sensor	Raw phase delay by optode, reported by SBE63 optode sensor. Local_Attributes:{long_name:Phase delay reported by oxygen sensor; standard_name:-; units:microsecond; valid_min:0.f; valid_max:99999.f; fill_value:99999.f}. Properties:{category:ib; data_type:float}	2025-11-25
  private static final ParameterAttributes<Float> PHASE_DELAY_DOXY_PARAM_ATTR = new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Phase delay reported by oxygen sensor";
    }

    @Override
    public Float getValidMin() {
      return 0f;
    }

    @Override
    public Float getValidMax() {
      return 99999f;
    }

    @Override
    public String getUnits() {
      return "microsecond";
    }

    @Override
    public String getCFormat() {
      return "%d";
    }

    @Override
    public String getFortranFormat() {
      return "I";
    }

    @Override
    public Float getResolution() {
      return 1f;
    }

  };

  /*
  case 'RAW_DOWNWELLING_IRRADIANCE412'
         o_attributeStruct = struct('name', 'RAW_DOWNWELLING_IRRADIANCE412', ...
            'longName', 'Raw downwelling irradiance at 412 nanometers', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'count', ...
            'validMin', '', ...
            'validMax', '', ...
            'axis', '', ...
            'cFormat', '%9.1f', ...
            'fortranFormat', 'F9.1', ...
            'resolution', single(1), ...
            'paramType', 'i', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);

    case 'RAW_DOWNWELLING_IRRADIANCE380'
         o_attributeStruct = struct('name', 'RAW_DOWNWELLING_IRRADIANCE380', ...
            'longName', 'Raw downwelling irradiance at 380 nanometers', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'count', ...
            'validMin', '', ...
            'validMax', '', ...
            'axis', '', ...
            'cFormat', '%9.1f', ...
            'fortranFormat', 'F9.1', ...
            'resolution', single(1), ...
            'paramType', 'i', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);

       case 'RAW_DOWNWELLING_IRRADIANCE443' % for nc_create_synthetic_profile
         o_attributeStruct = struct('name', 'RAW_DOWNWELLING_IRRADIANCE443', ...
            'longName', 'Raw downwelling irradiance at 443 nanometers', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'count', ...
            'validMin', '', ...
            'validMax', '', ...
            'axis', '', ...
            'cFormat', '%9.1f', ...
            'fortranFormat', 'F9.1', ...
            'resolution', single(1), ...
            'paramType', 'i', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);

      case 'RAW_DOWNWELLING_IRRADIANCE490'
         o_attributeStruct = struct('name', 'RAW_DOWNWELLING_IRRADIANCE490', ...
            'longName', 'Raw downwelling irradiance at 490 nanometers', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'count', ...
            'validMin', '', ...
            'validMax', '', ...
            'axis', '', ...
            'cFormat', '%9.1f', ...
            'fortranFormat', 'F9.1', ...
            'resolution', single(1), ...
            'paramType', 'i', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);

       case 'RAW_DOWNWELLING_IRRADIANCE555' % for nc_create_synthetic_profile
         o_attributeStruct = struct('name', 'RAW_DOWNWELLING_IRRADIANCE555', ...
            'longName', 'Raw downwelling irradiance at 555 nanometers', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'count', ...
            'validMin', '', ...
            'validMax', '', ...
            'axis', '', ...
            'cFormat', '%9.1f', ...
            'fortranFormat', 'F9.1', ...
            'resolution', single(1), ...
            'paramType', 'i', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);
   */

  // RAW_DOWNWELLING_IRRADIANCE	Raw downwelling irradiance at x nanometers	Raw downwelling irradiance signal at unspecified wavelength. Local_Attributes:{long_name:Raw downwelling irradiance at x nanometers; standard_name:-; units:count; valid_min:-; valid_max:-; fill_value:NC_FILL_DOUBLE}. Properties:{category:ib; data_type:double}	2025-11-25
  private static final Function<String, ParameterAttributes<Float>> RAW_DOWNWELLING_IRRADIANCE_PARAM_ATTR_FACTORY = diNum -> new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Raw downwelling irradiance at " + diNum + " nanometers";
    }

    @Override
    public String getUnits() {
      return "count";
    }

    @Override
    public String getCFormat() {
      return "%9.1f";
    }

    @Override
    public String getFortranFormat() {
      return "F9.1";
    }

    @Override
    public Float getResolution() {
      return 1f;
    }

  };

  /*

    case 'RAW_DOWNWELLING_IRRADIANCE490_STD'
         o_attributeStruct = struct('name', 'RAW_DOWNWELLING_IRRADIANCE490_STD', ...
            'longName', 'Standard deviation of raw downwelling irradiance at 490 nanometers', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'count', ...
            'validMin', '', ...
            'validMax', '', ...
            'axis', '', ...
            'cFormat', '%9.1f', ...
            'fortranFormat', 'F9.1', ...
            'resolution', single(1), ...
            'paramType', 'i', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);

  case 'RAW_DOWNWELLING_IRRADIANCE380_STD'
         o_attributeStruct = struct('name', 'RAW_DOWNWELLING_IRRADIANCE380_STD', ...
            'longName', 'Standard deviation of raw downwelling irradiance at 380 nanometers', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'count', ...
            'validMin', '', ...
            'validMax', '', ...
            'axis', '', ...
            'cFormat', '%9.1f', ...
            'fortranFormat', 'F9.1', ...
            'resolution', single(1), ...
            'paramType', 'i', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);

      case 'RAW_DOWNWELLING_IRRADIANCE412_STD'
         o_attributeStruct = struct('name', 'RAW_DOWNWELLING_IRRADIANCE412_STD', ...
            'longName', 'Standard deviation of raw downwelling irradiance at 412 nanometers', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'count', ...
            'validMin', '', ...
            'validMax', '', ...
            'axis', '', ...
            'cFormat', '%9.1f', ...
            'fortranFormat', 'F9.1', ...
            'resolution', single(1), ...
            'paramType', 'i', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);

      */
  private static final Function<String, ParameterAttributes<Float>> RAW_DOWNWELLING_IRRADIANCE_STD_PARAM_ATTR_FACTORY = diNum -> new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Standard deviation of raw downwelling irradiance at " + diNum + " nanometers";
    }

    @Override
    public String getUnits() {
      return "count";
    }

    @Override
    public String getCFormat() {
      return "%9.1f";
    }

    @Override
    public String getFortranFormat() {
      return "F9.1";
    }

    @Override
    public Float getResolution() {
      return 1f;
    }

  };


  /*

      case 'RAW_DOWNWELLING_IRRADIANCE380_MED'
         o_attributeStruct = struct('name', 'RAW_DOWNWELLING_IRRADIANCE380_MED', ...
            'longName', 'Median value of raw downwelling irradiance at 380 nanometers', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'count', ...
            'validMin', '', ...
            'validMax', '', ...
            'axis', '', ...
            'cFormat', '%9.1f', ...
            'fortranFormat', 'F9.1', ...
            'resolution', single(1), ...
            'paramType', 'i', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);





      case 'RAW_DOWNWELLING_IRRADIANCE412_MED'
         o_attributeStruct = struct('name', 'RAW_DOWNWELLING_IRRADIANCE412_MED', ...
            'longName', 'Median value of raw downwelling irradiance at 412 nanometers', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'count', ...
            'validMin', '', ...
            'validMax', '', ...
            'axis', '', ...
            'cFormat', '%9.1f', ...
            'fortranFormat', 'F9.1', ...
            'resolution', single(1), ...
            'paramType', 'i', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);

      case 'RAW_DOWNWELLING_IRRADIANCE490_MED'
         o_attributeStruct = struct('name', 'RAW_DOWNWELLING_IRRADIANCE490_MED', ...
            'longName', 'Median value of raw downwelling irradiance at 490 nanometers', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'count', ...
            'validMin', '', ...
            'validMax', '', ...
            'axis', '', ...
            'cFormat', '%9.1f', ...
            'fortranFormat', 'F9.1', ...
            'resolution', single(1), ...
            'paramType', 'i', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);
   */
  private static final Function<String, ParameterAttributes<Float>> RAW_DOWNWELLING_IRRADIANCE_MED_PARAM_ATTR_FACTORY = diNum -> new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Median value of raw downwelling irradiance at " + diNum + " nanometers";
    }

    @Override
    public String getUnits() {
      return "count";
    }

    @Override
    public String getCFormat() {
      return "%9.1f";
    }

    @Override
    public String getFortranFormat() {
      return "F9.1";
    }

    @Override
    public Float getResolution() {
      return 1f;
    }

  };

  /*
  case 'RAW_DOWNWELLING_PAR'
         o_attributeStruct = struct('name', 'RAW_DOWNWELLING_PAR', ...
            'longName', 'Raw downwelling photosynthetic available radiation', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'count', ...
            'validMin', '', ...
            'validMax', '', ...
            'axis', '', ...
            'cFormat', '%9.1f', ...
            'fortranFormat', 'F9.1', ...
            'resolution', single(1), ...
            'paramType', 'i', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);
   */

  // RAW_DOWNWELLING_PAR	Raw downwelling photosynthetic available radiation	Raw downwelling photosynthetically available radiation (PAR) signal. Local_Attributes:{long_name:Raw downwelling photosynthetic available radiation; standard_name:-; units:count; valid_min:-; valid_max:-; fill_value:NC_FILL_DOUBLE}. Properties:{category:ib; data_type:double}	2025-11-25
  private static final ParameterAttributes<Float> RAW_DOWNWELLING_PAR_PARAM_ATTR = new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Raw downwelling photosynthetic available radiation";
    }

    @Override
    public String getUnits() {
      return "count";
    }

    @Override
    public String getCFormat() {
      return "%9.1f";
    }

    @Override
    public String getFortranFormat() {
      return "F9.1";
    }

    @Override
    public Float getResolution() {
      return 1f;
    }

  };

  /*
     case 'RAW_DOWNWELLING_PAR_STD'
         o_attributeStruct = struct('name', 'RAW_DOWNWELLING_PAR_STD', ...
            'longName', 'Standard deviation of raw downwelling photosynthetic available radiation', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'count', ...
            'validMin', '', ...
            'validMax', '', ...
            'axis', '', ...
            'cFormat', '%9.1f', ...
            'fortranFormat', 'F9.1', ...
            'resolution', single(1), ...
            'paramType', 'i', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);

     */

  private static final ParameterAttributes<Float> RAW_DOWNWELLING_PAR_STD_PARAM_ATTR = new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Standard deviation of raw downwelling photosynthetic available radiation";
    }

    @Override
    public String getUnits() {
      return "count";
    }

    @Override
    public String getCFormat() {
      return "%9.1f";
    }

    @Override
    public String getFortranFormat() {
      return "F9.1";
    }

    @Override
    public Float getResolution() {
      return 1f;
    }

  };

  /*

      case 'RAW_DOWNWELLING_PAR_MED'
         o_attributeStruct = struct('name', 'RAW_DOWNWELLING_PAR_MED', ...
            'longName', 'Median value of raw downwelling photosynthetic available radiation', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'count', ...
            'validMin', '', ...
            'validMax', '', ...
            'axis', '', ...
            'cFormat', '%9.1f', ...
            'fortranFormat', 'F9.1', ...
            'resolution', single(1), ...
            'paramType', 'i', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);
   */

  private static final ParameterAttributes<Float> RAW_DOWNWELLING_PAR_MED_PARAM_ATTR = new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Median value of raw downwelling photosynthetic available radiation";
    }

    @Override
    public String getUnits() {
      return "count";
    }

    @Override
    public String getCFormat() {
      return "%9.1f";
    }

    @Override
    public String getFortranFormat() {
      return "F9.1";
    }

    @Override
    public Float getResolution() {
      return 1f;
    }

  };


  /*
  case 'FLUORESCENCE_CDOM'
         o_attributeStruct = struct('name', 'FLUORESCENCE_CDOM', ...
            'longName', 'Raw fluorescence from coloured dissolved organic matter sensor', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'count', ...
            'validMin', '', ...
            'validMax', '', ...
            'axis', '', ...
            'cFormat', '%.1f', ...
            'fortranFormat', 'F.1', ...
            'resolution', single(0.1), ...
            'paramType', 'i', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);
   */

  // FLUORESCENCE_CDOM	Raw fluorescence from coloured dissolved organic matter sensor	Raw fluorescence signal from coloured dissolved organic matter (CDOM) sensor, reported by ECO3 sensor. Local_Attributes:{long_name:Raw fluorescence from coloured dissolved organic matter sensor; standard_name:-; units:count; valid_min:-; valid_max:-; fill_value:99999.f}. Properties:{category:ib; data_type:float}	2025-11-25
  private static final ParameterAttributes<Float> FLUORESCENCE_CDOM_PARAM_ATTR = new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Raw fluorescence from coloured dissolved organic matter sensor";
    }


    @Override
    public String getUnits() {
      return "count";
    }

    @Override
    public String getCFormat() {
      return "%.1f";
    }

    @Override
    public String getFortranFormat() {
      return "F.1";
    }

    @Override
    public Float getResolution() {
      return 0.1f;
    }

  };

  /*
  case 'FLUORESCENCE_CDOM_STD'
         o_attributeStruct = struct('name', 'FLUORESCENCE_CDOM_STD', ...
            'longName', 'Standard deviation of raw fluorescence from coloured dissolved organic matter sensor', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'count', ...
            'validMin', '', ...
            'validMax', '', ...
            'axis', '', ...
            'cFormat', '%.1f', ...
            'fortranFormat', 'F.1', ...
            'resolution', single(0.1), ...
            'paramType', 'i', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);
            */
  private static final ParameterAttributes<Float> FLUORESCENCE_CDOM_STD_PARAM_ATTR = new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Standard deviation of raw fluorescence from coloured dissolved organic matter sensor";
    }

    @Override
    public String getUnits() {
      return "count";
    }

    @Override
    public String getCFormat() {
      return "%.1f";
    }

    @Override
    public String getFortranFormat() {
      return "F.1";
    }

    @Override
    public Float getResolution() {
      return 0.1f;
    }

  };
  /*

      case 'FLUORESCENCE_CDOM_MED'
         o_attributeStruct = struct('name', 'FLUORESCENCE_CDOM_MED', ...
            'longName', 'Median value of raw fluorescence from coloured dissolved organic matter sensor', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'count', ...
            'validMin', '', ...
            'validMax', '', ...
            'axis', '', ...
            'cFormat', '%.1f', ...
            'fortranFormat', 'F.1', ...
            'resolution', single(0.1), ...
            'paramType', 'i', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);
   */
  private static final ParameterAttributes<Float> FLUORESCENCE_CDOM_MED_PARAM_ATTR = new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Median value of raw fluorescence from coloured dissolved organic matter sensor";
    }

    @Override
    public String getUnits() {
      return "count";
    }

    @Override
    public String getCFormat() {
      return "%.1f";
    }

    @Override
    public String getFortranFormat() {
      return "F.1";
    }

    @Override
    public Float getResolution() {
      return 0.1f;
    }

  };


  /*

        case 'RAW_UPWELLING_RADIANCE412' % for nc_create_synthetic_profile
         o_attributeStruct = struct('name', 'RAW_UPWELLING_RADIANCE412', ...
            'longName', 'Raw upwelling radiance at 412 nanometers', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'count', ...
            'validMin', '', ...
            'validMax', '', ...
            'axis', '', ...
            'cFormat', '%9.1f', ...
            'fortranFormat', 'F9.1', ...
            'resolution', single(1), ...
            'paramType', 'i', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);

      case 'RAW_UPWELLING_RADIANCE443' % for nc_create_synthetic_profile
         o_attributeStruct = struct('name', 'RAW_UPWELLING_RADIANCE443', ...
            'longName', 'Raw upwelling radiance at 443 nanometers', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'count', ...
            'validMin', '', ...
            'validMax', '', ...
            'axis', '', ...
            'cFormat', '%9.1f', ...
            'fortranFormat', 'F9.1', ...
            'resolution', single(1), ...
            'paramType', 'i', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);

      case 'RAW_UPWELLING_RADIANCE490' % for nc_create_synthetic_profile
         o_attributeStruct = struct('name', 'RAW_UPWELLING_RADIANCE490', ...
            'longName', 'Raw upwelling radiance at 490 nanometers', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'count', ...
            'validMin', '', ...
            'validMax', '', ...
            'axis', '', ...
            'cFormat', '%9.1f', ...
            'fortranFormat', 'F9.1', ...
            'resolution', single(1), ...
            'paramType', 'i', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);

      case 'RAW_UPWELLING_RADIANCE555' % for nc_create_synthetic_profile
         o_attributeStruct = struct('name', 'RAW_UPWELLING_RADIANCE555', ...
            'longName', 'Raw upwelling radiance at 555 nanometers', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'count', ...
            'validMin', '', ...
            'validMax', '', ...
            'axis', '', ...
            'cFormat', '%9.1f', ...
            'fortranFormat', 'F9.1', ...
            'resolution', single(1), ...
            'paramType', 'i', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);


   */

  // RAW_UPWELLING_RADIANCE	Raw upwelling radiance at x nanometers	Raw upwelling radiance signal at unspecified wavelength, reported by OCR504 sensor. Local_Attributes:{long_name:Raw upwelling radiance at x nanometers; standard_name:-; units:count; valid_min:-; valid_max:-; fill_value:NC_FILL_DOUBLE}. Properties:{category:ib; data_type:double}	2025-11-25
  private static final Function<String, ParameterAttributes<Float>> RAW_UPWELLING_RADIANCE_PARAM_ATTR_FACTORY = diNum -> new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Raw upwelling radiance at " + diNum + " nanometers";
    }

    @Override
    public String getUnits() {
      return "count";
    }

    @Override
    public String getCFormat() {
      return "%9.1f";
    }

    @Override
    public String getFortranFormat() {
      return "F9.1";
    }

    @Override
    public Float getResolution() {
      return 1f;
    }

  };

  /*
    case 'HUMIDITY_NITRATE'
         o_attributeStruct = struct('name', 'HUMIDITY_NITRATE', ...
            'longName', 'Relative humidity inside the SUNA sensor (If > 50% There is a leak)', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'percent', ...
            'validMin', single(0), ...
            'validMax', single(100), ...
            'axis', '', ...
            'cFormat', '%.1f', ...
            'fortranFormat', 'F.1', ...
            'resolution', single(0.1), ...
            'paramType', 'i', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);
   */

  // HUMIDITY_NITRATE	Relative humidity inside the SUNA sensor (If > 50% There is a leak)	Relative humidity inside the nitrate sensor (If > 50% there is a leak), reported by the SUNA sensor from Remocean floats. Local_Attributes:{long_name:Relative humidity inside the SUNA sensor (If > 50% There is a leak); standard_name:-; units:percent; valid_min:0.f; valid_max:100.f; fill_value:99999.f}. Properties:{category:ib; data_type:float}	2025-11-25
  private static final ParameterAttributes<Float> HUMIDITY_NITRATE_PARAM_ATTR = new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Relative humidity inside the SUNA sensor (If > 50% There is a leak)'";
    }

    @Override
    public Float getValidMin() {
      return 0f;
    }

    @Override
    public Float getValidMax() {
      return 100f;
    }

    @Override
    public String getUnits() {
      return "percent";
    }

    @Override
    public String getCFormat() {
      return "%.1f";
    }

    @Override
    public String getFortranFormat() {
      return "F.1";
    }

    @Override
    public Float getResolution() {
      return 0.1f;
    }

  };

  /*
  case 'TEMP_DOXY'
         o_attributeStruct = struct('name', 'TEMP_DOXY', ...
            'longName', 'Sea temperature from oxygen sensor ITS-90 scale', ...
            'standardName', 'temperature_of_sensor_for_oxygen_in_sea_water', ...
            'fillValue', single(99999), ...
            'units', 'degree_Celsius', ...
            'validMin', single(-2.0), ...
            'validMax', single(40), ...
            'axis', '', ...
            'cFormat', '%9.3f', ...
            'fortranFormat', 'F9.3', ...
            'resolution', single(0.001), ...
            'paramType', 'i', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);
   */

  // TEMP_DOXY	Sea temperature from oxygen sensor ITS-90 scale	Temperature from thermistor on dissolved oxygen sensor, expressed against the International Temperature Scale 1990 (ITS-90) calibration standard. Local_Attributes:{long_name:Sea temperature from oxygen sensor ITS-90 scale; standard_name:temperature_of_sensor_for_oxygen_in_sea_water; units:degree_Celsius; valid_min:-2.f; valid_max:40.f; fill_value:99999.f}. Properties:{category:ib; data_type:float}	2025-11-25
  private static final ParameterAttributes<Float> TEMP_DOXY_PARAM_ATTR = new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Sea temperature from oxygen sensor ITS-90 scale";
    }

    @Override
    public String getStandardName() {
      return "temperature_of_sensor_for_oxygen_in_sea_water";
    }

    @Override
    public Float getValidMin() {
      return -2f;
    }

    @Override
    public Float getValidMax() {
      return 40f;
    }

    @Override
    public String getUnits() {
      return "degree_Celsius";
    }

    @Override
    public String getCFormat() {
      return "%9.3f";
    }

    @Override
    public String getFortranFormat() {
      return "F9.3";
    }

    @Override
    public Float getResolution() {
      return 0.001f;
    }

  };

  /*
  case 'TEMP_DOXY_STD'
         o_attributeStruct = struct('name', 'TEMP_DOXY_STD', ...
            'longName', 'Standard deviation of sea temperature from oxygen sensor ITS-90 scale', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'degree_Celsius', ...
            'validMin', single(-2.0), ...
            'validMax', single(40), ...
            'axis', '', ...
            'cFormat', '%9.3f', ...
            'fortranFormat', 'F9.3', ...
            'resolution', single(0.001), ...
            'paramType', 'i', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);

            */
  private static final ParameterAttributes<Float> TEMP_DOXY_STD_PARAM_ATTR = new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Standard deviation of sea temperature from oxygen sensor ITS-90 scale";
    }

    @Override
    public String getUnits() {
      return "degree_Celsius";
    }

    @Override
    public Float getValidMin() {
      return -2f;
    }

    @Override
    public Float getValidMax() {
      return 40f;
    }

    @Override
    public String getCFormat() {
      return "%9.3f";
    }

    @Override
    public String getFortranFormat() {
      return "F9.3";
    }

    @Override
    public Float getResolution() {
      return 0.001f;
    }

  };

  /*


      case 'TEMP_DOXY_MED'
         o_attributeStruct = struct('name', 'TEMP_DOXY_MED', ...
            'longName', 'Median value of sea temperature from oxygen sensor ITS-90 scale', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'degree_Celsius', ...
            'validMin', single(-2.0), ...
            'validMax', single(40), ...
            'axis', '', ...
            'cFormat', '%9.3f', ...
            'fortranFormat', 'F9.3', ...
            'resolution', single(0.001), ...
            'paramType', 'i', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);
   */

  private static final ParameterAttributes<Float> TEMP_DOXY_MED_PARAM_ATTR = new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Median value of sea temperature from oxygen sensor ITS-90 scale";
    }

    @Override
    public String getUnits() {
      return "degree_Celsius";
    }

    @Override
    public Float getValidMin() {
      return -2f;
    }

    @Override
    public Float getValidMax() {
      return 40f;
    }

    @Override
    public String getCFormat() {
      return "%9.3f";
    }

    @Override
    public String getFortranFormat() {
      return "F9.3";
    }

    @Override
    public Float getResolution() {
      return 0.001f;
    }

  };



  /*
  case 'TEMP_PH' % for nc_create_synthetic_profile
         o_attributeStruct = struct('name', 'TEMP_PH', ...
            'longName', 'Sea temperature from pH sensor', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'degree_Celsius', ...
            'validMin', single(-2.5), ...
            'validMax', single(40), ...
            'axis', '', ...
            'cFormat', '%9.3f', ...
            'fortranFormat', 'F9.3', ...
            'resolution', single(0.001), ...
            'paramType', 'i', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);
   */

  // TEMP_PH	Sea temperature from pH sensor	Temperature of seawater, reported by the pH sensor. Local_Attributes:{long_name:Sea temperature from pH sensor; standard_name:-; units:degree_Celsius; valid_min:-2.f; valid_max:40.f; fill_value:99999.f}. Properties:{category:ib; data_type:float}	2025-11-25
  private static final ParameterAttributes<Float> TEMP_PH_PARAM_ATTR = new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Sea temperature from pH sensor";
    }

    @Override
    public Float getValidMin() {
      return -2.5f;
    }

    @Override
    public Float getValidMax() {
      return 40f;
    }

    @Override
    public String getUnits() {
      return "degree_Celsius";
    }

    @Override
    public String getCFormat() {
      return "%9.3f";
    }

    @Override
    public String getFortranFormat() {
      return "F9.3";
    }

    @Override
    public Float getResolution() {
      return 0.001f;
    }

  };

  /*
      case 'TURBIDITY'
         o_attributeStruct = struct('name', 'TURBIDITY', ...
            'longName', 'Sea water turbidity', ...
            'standardName', 'sea_water_turbidity', ...
            'fillValue', single(99999), ...
            'units', 'ntu', ...
            'validMin', '', ...
            'validMax', '', ...
            'axis', '', ...
            'cFormat', '%.3f', ...
            'fortranFormat', 'F.3', ...
            'resolution', single(0.001), ...
            'paramType', 'b', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 1);
   */

  // TURBIDITY	Sea water turbidity	Turbidity of seawater. Local_Attributes:{long_name:Sea water turbidity; standard_name:sea_water_turbidity; units:ntu; valid_min:-; valid_max:-; fill_value:99999.f}. Properties:{category:b; data_type:float}	2025-11-25
  private static final ParameterAttributes<Float> TURBIDITY_PARAM_ATTR = new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Sea water turbidity";
    }

    @Override
    public String getStandardName() {
      return "sea_water_turbidity";
    }

    @Override
    public String getUnits() {
      return "ntu";
    }

    @Override
    public String getCFormat() {
      return "%.3f";
    }

    @Override
    public String getFortranFormat() {
      return "F.3";
    }

    @Override
    public Float getResolution() {
      return 0.001f;
    }

  };

  /*
        case 'TEMP_SPECTROPHOTOMETER_NITRATE'
         o_attributeStruct = struct('name', 'TEMP_SPECTROPHOTOMETER_NITRATE', ...
            'longName', 'Temperature of the spectrometer', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'degree_Celsius', ...
            'validMin', '', ...
            'validMax', '', ...
            'axis', '', ...
            'cFormat', '%.1f', ...
            'fortranFormat', 'F.1', ...
            'resolution', single(0.1), ...
            'paramType', 'i', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);
   */

  // TEMP_SPECTROPHOTOMETER_NITRATE	Temperature of the spectrometer	Spectrometer temperature of nitrate sensor, reported by the SUNA sensor from Remocean floats. Local_Attributes:{long_name:Temperature of the spectrometer; standard_name:-; units:degree_Celsius; valid_min:-; valid_max:-; fill_value:99999.f}. Properties:{category:ib; data_type:float}	2025-11-25
  private static final ParameterAttributes<Float> TEMP_SPECTROPHOTOMETER_NITRATE_PARAM_ATTR = new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Temperature of the spectrometer";
    }

    @Override
    public String getUnits() {
      return "degree_Celsius";
    }

    @Override
    public String getCFormat() {
      return "%.1f";
    }

    @Override
    public String getFortranFormat() {
      return "F.1";
    }

    @Override
    public Float getResolution() {
      return 0.1f;
    }

  };

  /*
  case 'TEMP_CPU_CHLA'
         o_attributeStruct = struct('name', 'TEMP_CPU_CHLA', ...
            'longName', 'Thermistor signal from backscattering sensor', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'count', ...
            'validMin', '', ...
            'validMax', '', ...
            'axis', '', ...
            'cFormat', '%d', ...
            'fortranFormat', 'I', ...
            'resolution', single(1), ...
            'paramType', 'i', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);
   */

  // TEMP_CPU_CHLA	Thermistor signal from backscattering sensor	Raw temperature signalfrom thermistor on backscattering meter and/or fluorometer, reported by FLBB sensor. Local_Attributes:{long_name:Thermistor signal from backscattering sensor; standard_name:-; units:count; valid_min:-; valid_max:-; fill_value:99999.f}. Properties:{category:ib; data_type:float}	2025-11-25
  private static final ParameterAttributes<Float> TEMP_CPU_CHLA_PARAM_ATTR = new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Thermistor signal from backscattering sensor";
    }

    @Override
    public String getUnits() {
      return "count";
    }

    @Override
    public String getCFormat() {
      return "%d";
    }

    @Override
    public String getFortranFormat() {
      return "I";
    }

    @Override
    public Float getResolution() {
      return 1f;
    }

  };

  /*
  case 'TEMP_VOLTAGE_DOXY' % for nc_create_synthetic_profile
         o_attributeStruct = struct('name', 'TEMP_VOLTAGE_DOXY', ...
            'longName', 'Thermistor voltage reported by oxygen sensor', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'volt', ...
            'validMin', '', ...
            'validMax', '', ...
            'axis', '', ...
            'cFormat', '%5.2f', ...
            'fortranFormat', 'F5.2', ...
            'resolution', single(0.01), ...
            'paramType', 'i', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);
   */

  // TEMP_VOLTAGE_DOXY	Thermistor voltage reported by oxygen sensor	Raw temperature signal (voltage) from the thermistor of dissolved oxygen sensor, reported by the SBE63 sensor. Local_Attributes:{long_name:Thermistor voltage reported by oxygen sensor; standard_name:-; units:volt; valid_min:-; valid_max:-; fill_value:99999.f}. Properties:{category:ib; data_type:float}	2025-11-25
  private static final ParameterAttributes<Float> TEMP_VOLTAGE_DOXY_PARAM_ATTR = new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Thermistor voltage reported by oxygen sensor";
    }

    @Override
    public String getUnits() {
      return "volt";
    }

    @Override
    public String getCFormat() {
      return "%5.2f";
    }

    @Override
    public String getFortranFormat() {
      return "F5.2";
    }

    @Override
    public Float getResolution() {
      return 0.01f;
    }

  };

  /*

        case 'BETA_BACKSCATTERING470' % for nc_create_synthetic_profile
         o_attributeStruct = struct('name', 'BETA_BACKSCATTERING470', ...
            'longName', 'Total angle specific volume from backscattering sensor at 470 nanometers', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'count', ...
            'validMin', '', ...
            'validMax', '', ...
            'axis', '', ...
            'cFormat', '%.1f', ...
            'fortranFormat', 'F.1', ...
            'resolution', single(0.1), ...
            'paramType', 'i', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);

      case 'BETA_BACKSCATTERING532'
         o_attributeStruct = struct('name', 'BETA_BACKSCATTERING532', ...
            'longName', 'Total angle specific volume from backscattering sensor at 532 nanometers', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'count', ...
            'validMin', '', ...
            'validMax', '', ...
            'axis', '', ...
            'cFormat', '%.1f', ...
            'fortranFormat', 'F.1', ...
            'resolution', single(0.1), ...
            'paramType', 'i', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);

      case 'BETA_BACKSCATTERING700'
         o_attributeStruct = struct('name', 'BETA_BACKSCATTERING700', ...
            'longName', 'Total angle specific volume from backscattering sensor at 700 nanometers', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'count', ...
            'validMin', '', ...
            'validMax', '', ...
            'axis', '', ...
            'cFormat', '%.1f', ...
            'fortranFormat', 'F.1', ...
            'resolution', single(0.1), ...
            'paramType', 'i', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);


   */

  // BETA_BACKSCATTERING	Total angle specific volume from backscattering sensor at x nanometers	Raw backscattering signal from backscattering meter at unspecified wavelength, reported by ECO3 or FLBB sensors. Local_Attributes:{long_name:Total angle specific volume from backscattering sensor at x nanometers; standard_name:-; units:count; valid_min:-; valid_max:-; fill_value:99999.f}. Properties:{category:ib; data_type:float}	2025-11-25
  private static final Function<String, ParameterAttributes<Float>> BETA_BACKSCATTERING_PARAM_ATTR_FACTORY = diNum -> new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Total angle specific volume from backscattering sensor at " + diNum + " nanometers";
    }

    @Override
    public String getUnits() {
      return "count";
    }

    @Override
    public String getCFormat() {
      return "%.1f";
    }

    @Override
    public String getFortranFormat() {
      return "F.1";
    }

    @Override
    public Float getResolution() {
      return 0.1f;
    }

  };

  /*
  case 'BETA_BACKSCATTERING532_STD'
         o_attributeStruct = struct('name', 'BETA_BACKSCATTERING532_STD', ...
            'longName', 'Standard deviation of total angle specific volume from backscattering sensor at 532 nanometers', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'count', ...
            'validMin', '', ...
            'validMax', '', ...
            'axis', '', ...
            'cFormat', '%.1f', ...
            'fortranFormat', 'F.1', ...
            'resolution', single(0.1), ...
            'paramType', 'i', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);

      case 'BETA_BACKSCATTERING700_STD'
         o_attributeStruct = struct('name', 'BETA_BACKSCATTERING700_STD', ...
            'longName', 'Standard deviation of total angle specific volume from backscattering sensor at 700 nanometers', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'count', ...
            'validMin', '', ...
            'validMax', '', ...
            'axis', '', ...
            'cFormat', '%.1f', ...
            'fortranFormat', 'F.1', ...
            'resolution', single(0.1), ...
            'paramType', 'i', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);

            */
  private static final Function<String, ParameterAttributes<Float>> BETA_BACKSCATTERING_STD_PARAM_ATTR_FACTORY = diNum -> new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Standard deviation of total angle specific volume from backscattering sensor at " + diNum + " nanometers";
    }

    @Override
    public String getUnits() {
      return "count";
    }

    @Override
    public String getCFormat() {
      return "%.1f";
    }

    @Override
    public String getFortranFormat() {
      return "F.1";
    }

    @Override
    public Float getResolution() {
      return 0.1f;
    }

  };


  /*

      case 'BETA_BACKSCATTERING532_MED'
         o_attributeStruct = struct('name', 'BETA_BACKSCATTERING532_MED', ...
            'longName', 'Median value of total angle specific volume from backscattering sensor at 532 nanometers', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'count', ...
            'validMin', '', ...
            'validMax', '', ...
            'axis', '', ...
            'cFormat', '%.1f', ...
            'fortranFormat', 'F.1', ...
            'resolution', single(0.1), ...
            'paramType', 'i', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);

      case 'BETA_BACKSCATTERING700_MED'
         o_attributeStruct = struct('name', 'BETA_BACKSCATTERING700_MED', ...
            'longName', 'Median value of total angle specific volume from backscattering sensor at 700 nanometers', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'count', ...
            'validMin', '', ...
            'validMax', '', ...
            'axis', '', ...
            'cFormat', '%.1f', ...
            'fortranFormat', 'F.1', ...
            'resolution', single(0.1), ...
            'paramType', 'i', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);
   */
  private static final Function<String, ParameterAttributes<Float>> BETA_BACKSCATTERING_MED_PARAM_ATTR_FACTORY = diNum -> new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Median value of total angle specific volume from backscattering sensor at " + diNum + " nanometers";
    }

    @Override
    public String getUnits() {
      return "count";
    }

    @Override
    public String getCFormat() {
      return "%.1f";
    }

    @Override
    public String getFortranFormat() {
      return "F.1";
    }

    @Override
    public Float getResolution() {
      return 0.1f;
    }

  };

  /*
  case 'SIDE_SCATTERING_TURBIDITY'
         o_attributeStruct = struct('name', 'SIDE_SCATTERING_TURBIDITY', ...
            'longName', 'Turbidity signal from side scattering sensor', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'count', ...
            'validMin', '', ...
            'validMax', '', ...
            'axis', '', ...
            'cFormat', '%.1f', ...
            'fortranFormat', 'F.1', ...
            'resolution', single(0.1), ...
            'paramType', 'i', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);
   */

  // SIDE_SCATTERING_TURBIDITY	Turbidity signal from side scattering sensor	Raw turbidity signal from side scattering sensor, reported by FLNTU sensor. Local_Attributes:{long_name:Turbidity signal from side scattering sensor; standard_name:-; units:count; valid_min:-; valid_max:-; fill_value:99999.f}. Properties:{category:ib; data_type:float}	2025-11-25
  private static final ParameterAttributes<Float> SIDE_SCATTERING_TURBIDITY_PARAM_ATTR = new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Turbidity signal from side scattering sensor";
    }

    @Override
    public String getUnits() {
      return "count";
    }

    @Override
    public String getCFormat() {
      return "%.1f";
    }

    @Override
    public String getFortranFormat() {
      return "F.1";
    }

    @Override
    public Float getResolution() {
      return 0.1f;
    }

  };

  /*
  case 'SIDE_SCATTERING_TURBIDITY_STD'
         o_attributeStruct = struct('name', 'SIDE_SCATTERING_TURBIDITY_STD', ...
            'longName', 'Standard deviation of turbidity signal from side scattering sensor', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'count', ...
            'validMin', '', ...
            'validMax', '', ...
            'axis', '', ...
            'cFormat', '%.1f', ...
            'fortranFormat', 'F.1', ...
            'resolution', single(0.1), ...
            'paramType', 'i', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);

            */
  private static final ParameterAttributes<Float> SIDE_SCATTERING_TURBIDITY_STD_PARAM_ATTR = new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Standard deviation of turbidity signal from side scattering sensor";
    }

    @Override
    public String getUnits() {
      return "count";
    }

    @Override
    public String getCFormat() {
      return "%.1f";
    }

    @Override
    public String getFortranFormat() {
      return "F.1";
    }

    @Override
    public Float getResolution() {
      return 0.1f;
    }

  };


  /*

      case 'SIDE_SCATTERING_TURBIDITY_MED'
         o_attributeStruct = struct('name', 'SIDE_SCATTERING_TURBIDITY_MED', ...
            'longName', 'Median value of turbidity signal from side scattering sensor', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'count', ...
            'validMin', '', ...
            'validMax', '', ...
            'axis', '', ...
            'cFormat', '%.1f', ...
            'fortranFormat', 'F.1', ...
            'resolution', single(0.1), ...
            'paramType', 'i', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);
   */

  private static final ParameterAttributes<Float> SIDE_SCATTERING_TURBIDITY_MED_PARAM_ATTR = new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Median value of turbidity signal from side scattering sensor";
    }

    @Override
    public String getUnits() {
      return "count";
    }

    @Override
    public String getCFormat() {
      return "%.1f";
    }

    @Override
    public String getFortranFormat() {
      return "F.1";
    }

    @Override
    public Float getResolution() {
      return 0.1f;
    }

  };


  /*
        case 'BPHASE_DOXY'
         o_attributeStruct = struct('name', 'BPHASE_DOXY', ...
            'longName', 'Uncalibrated phase shift reported by oxygen sensor', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'degree', ...
            'validMin', single(10), ...
            'validMax', single(70), ...
            'axis', '', ...
            'cFormat', '%8.2f', ...
            'fortranFormat', 'F8.2', ...
            'resolution', single(0.01), ...
            'paramType', 'i', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);
   */

  // BPHASE_DOXY	Uncalibrated blue excitation light phase shift reported by Aanderaa optode 3830	Uncalibrated blue excitation light phase shift, reported by Aanderaa optode 3830 sensor. Local_Attributes:{long_name:Uncalibrated phase shift reported by oxygen sensor; standard_name:-; units:degree; valid_min:10.f; valid_max:70.f; fill_value:99999.f}. Properties:{category:ib; data_type:float}	2025-11-25
  private static final ParameterAttributes<Float> BPHASE_DOXY_PARAM_ATTR = new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Uncalibrated phase shift reported by oxygen sensor";
    }

    @Override
    public Float getValidMin() {
      return 10f;
    }

    @Override
    public Float getValidMax() {
      return 70f;
    }

    @Override
    public String getUnits() {
      return "degree";
    }

    @Override
    public String getCFormat() {
      return "%8.2f";
    }

    @Override
    public String getFortranFormat() {
      return "F8.2";
    }

    @Override
    public Float getResolution() {
      return 0.01f;
    }

  };

  /*
   case 'C1PHASE_DOXY'
         o_attributeStruct = struct('name', 'C1PHASE_DOXY', ...
            'longName', 'Uncalibrated phase shift reported by oxygen sensor', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'degree', ...
            'validMin', single(10), ...
            'validMax', single(70), ...
            'axis', '', ...
            'cFormat', '%8.2f', ...
            'fortranFormat', 'F8.2', ...
            'resolution', single(0.01), ...
            'paramType', 'i', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);
   */

  // C1PHASE_DOXY	Uncalibrated blue excitation light phase shift reported by Aanderaa optode 4330/4330F	Uncalibrated blue excitation light phase shift, reported by Aanderaa optode 4330/4330F sensor. Local_Attributes:{long_name:Uncalibrated phase shift reported by oxygen sensor; standard_name:-; units:degree; valid_min:10.f; valid_max:70.f; fill_value:99999.f}. Properties:{category:ib; data_type:float}	2025-11-25
  private static final ParameterAttributes<Float> C1PHASE_DOXY_PARAM_ATTR = new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Uncalibrated phase shift reported by oxygen sensor";
    }

    @Override
    public String getUnits() {
      return "degree";
    }

    @Override
    public Float getValidMin() {
      return 10f;
    }

    @Override
    public Float getValidMax() {
      return 70f;
    }

    @Override
    public String getCFormat() {
      return "%8.2f";
    }

    @Override
    public String getFortranFormat() {
      return "F8.2";
    }

    @Override
    public Float getResolution() {
      return 0.01f;
    }

  };

  /*
  case 'C1PHASE_DOXY_STD'
         o_attributeStruct = struct('name', 'C1PHASE_DOXY_STD', ...
            'longName', 'Standard deviation of uncalibrated phase shift reported by oxygen sensor', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'degree', ...
            'validMin', single(10), ...
            'validMax', single(70), ...
            'axis', '', ...
            'cFormat', '%8.2f', ...
            'fortranFormat', 'F8.2', ...
            'resolution', single(0.01), ...
            'paramType', 'i', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);
            */

  private static final ParameterAttributes<Float> C1PHASE_DOXY_STD_PARAM_ATTR = new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Standard deviation of uncalibrated phase shift reported by oxygen sensor";
    }

    @Override
    public Float getValidMin() {
      return 10f;
    }

    @Override
    public Float getValidMax() {
      return 70f;
    }

    @Override
    public String getUnits() {
      return "degree";
    }

    @Override
    public String getCFormat() {
      return "%8.2f";
    }

    @Override
    public String getFortranFormat() {
      return "F8.2";
    }

    @Override
    public Float getResolution() {
      return 0.01f;
    }

  };

  /*

      case 'C1PHASE_DOXY_MED'
         o_attributeStruct = struct('name', 'C1PHASE_DOXY_MED', ...
            'longName', 'Median value of uncalibrated phase shift reported by oxygen sensor', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'degree', ...
            'validMin', single(10), ...
            'validMax', single(70), ...
            'axis', '', ...
            'cFormat', '%8.2f', ...
            'fortranFormat', 'F8.2', ...
            'resolution', single(0.01), ...
            'paramType', 'i', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);
   */

  private static final ParameterAttributes<Float> C1PHASE_DOXY_MED_PARAM_ATTR = new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Median value of uncalibrated phase shift reported by oxygen sensor";
    }

    @Override
    public Float getValidMin() {
      return 10f;
    }

    @Override
    public Float getValidMax() {
      return 70f;
    }

    @Override
    public String getUnits() {
      return "degree";
    }

    @Override
    public String getCFormat() {
      return "%8.2f";
    }

    @Override
    public String getFortranFormat() {
      return "F8.2";
    }

    @Override
    public Float getResolution() {
      return 0.01f;
    }

  };

  /*
   case 'TPHASE_DOXY'
         o_attributeStruct = struct('name', 'TPHASE_DOXY', ...
            'longName', 'Uncalibrated phase shift reported by oxygen sensor', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'degree', ...
            'validMin', single(10), ...
            'validMax', single(70), ...
            'axis', '', ...
            'cFormat', '%8.2f', ...
            'fortranFormat', 'F8.2', ...
            'resolution', single(0.01), ...
            'paramType', 'i', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);
   */

  // TPHASE_DOXY	Uncalibrated phase shift reported by oxygen sensor	Uncalibrated temperature compensated phase shift, reported by Aanderaa 4330 and 4330F optode sensors. Local_Attributes:{long_name:Uncalibrated phase shift reported by oxygen sensor; standard_name:-; units:degree; valid_min:10.f; valid_max:70.f; fill_value:99999.f}. Properties:{category:ib; data_type:float}	2025-11-25
  private static final ParameterAttributes<Float> TPHASE_DOXY_PARAM_ATTR = new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Uncalibrated phase shift reported by oxygen sensor";
    }

    @Override
    public Float getValidMin() {
      return 10f;
    }

    @Override
    public Float getValidMax() {
      return 70f;
    }

    @Override
    public String getUnits() {
      return "degree";
    }

    @Override
    public String getCFormat() {
      return "%8.2f";
    }

    @Override
    public String getFortranFormat() {
      return "F8.2";
    }

    @Override
    public Float getResolution() {
      return 0.01f;
    }

  };

  /*
  case 'C2PHASE_DOXY'
         o_attributeStruct = struct('name', 'C2PHASE_DOXY', ...
            'longName', 'Uncalibrated phase shift reported by oxygen sensor', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'degree', ...
            'validMin', single(0), ...
            'validMax', single(15), ...
            'axis', '', ...
            'cFormat', '%8.2f', ...
            'fortranFormat', 'F8.2', ...
            'resolution', single(0.01), ...
            'paramType', 'i', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);
   */

  // C2PHASE_DOXY	Uncalibrated red excitation light phase shift reported by Aanderaa optode 4330/4330F	Uncalibrated red excitation light phase shift, reported by Aanderaa optode 4330/4330F sensor. Local_Attributes:{long_name:Uncalibrated phase shift reported by oxygen sensor; standard_name:-; units:degree; valid_min:0.f; valid_max:15.f; fill_value:99999.f}. Properties:{category:ib; data_type:float}	2025-11-25
  private static final ParameterAttributes<Float> C2PHASE_DOXY_PARAM_ATTR = new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Uncalibrated phase shift reported by oxygen sensor";
    }

    @Override
    public Float getValidMin() {
      return 0f;
    }

    @Override
    public Float getValidMax() {
      return 15f;
    }

    @Override
    public String getUnits() {
      return "degree";
    }

    @Override
    public String getCFormat() {
      return "%8.2f";
    }

    @Override
    public String getFortranFormat() {
      return "F8.2";
    }

    @Override
    public Float getResolution() {
      return 0.01f;
    }

  };

  /*
  case 'C2PHASE_DOXY_STD'
         o_attributeStruct = struct('name', 'C2PHASE_DOXY_STD', ...
            'longName', 'Standard deviation of uncalibrated phase shift reported by oxygen sensor', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'degree', ...
            'validMin', single(0), ...
            'validMax', single(15), ...
            'axis', '', ...
            'cFormat', '%8.2f', ...
            'fortranFormat', 'F8.2', ...
            'resolution', single(0.01), ...
            'paramType', 'i', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);
            */

  private static final ParameterAttributes<Float> C2PHASE_DOXY_STD_PARAM_ATTR = new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Standard deviation of uncalibrated phase shift reported by oxygen sensor";
    }

    @Override
    public Float getValidMin() {
      return 0f;
    }

    @Override
    public Float getValidMax() {
      return 15f;
    }

    @Override
    public String getUnits() {
      return "degree";
    }

    @Override
    public String getCFormat() {
      return "%8.2f";
    }

    @Override
    public String getFortranFormat() {
      return "F8.2";
    }

    @Override
    public Float getResolution() {
      return 0.01f;
    }

  };

  /*

      case 'C2PHASE_DOXY_MED'
         o_attributeStruct = struct('name', 'C2PHASE_DOXY_MED', ...
            'longName', 'Median value of uncalibrated phase shift reported by oxygen sensor', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'degree', ...
            'validMin', single(0), ...
            'validMax', single(15), ...
            'axis', '', ...
            'cFormat', '%8.2f', ...
            'fortranFormat', 'F8.2', ...
            'resolution', single(0.01), ...
            'paramType', 'i', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);
   */

  private static final ParameterAttributes<Float> C2PHASE_DOXY_MED_PARAM_ATTR = new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Median value of uncalibrated phase shift reported by oxygen sensor";
    }

    @Override
    public Float getValidMin() {
      return 0f;
    }

    @Override
    public Float getValidMax() {
      return 15f;
    }

    @Override
    public String getUnits() {
      return "degree";
    }

    @Override
    public String getCFormat() {
      return "%8.2f";
    }

    @Override
    public String getFortranFormat() {
      return "F8.2";
    }

    @Override
    public Float getResolution() {
      return 0.01f;
    }

  };

  /*
       case 'RPHASE_DOXY'
         o_attributeStruct = struct('name', 'RPHASE_DOXY', ...
            'longName', 'Uncalibrated red phase shift reported by oxygen sensor', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'degree', ...
            'validMin', single(10), ...
            'validMax', single(70), ...
            'axis', '', ...
            'cFormat', '%8.2f', ...
            'fortranFormat', 'F8.2', ...
            'resolution', single(0.01), ...
            'paramType', 'i', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);
   */

  // RPHASE_DOXY	Uncalibrated red phase shift reported by oxygen sensor	Uncalibrated red excitation light phase shift, reported by Aanderaa optode 3830. Local_Attributes:{long_name:Uncalibrated red phase shift reported by oxygen sensor; standard_name:-; units:degree; valid_min:10.f; valid_max:70.f; fill_value:99999.f}. Properties:{category:ib; data_type:float}	2025-11-25
  private static final ParameterAttributes<Float> RPHASE_DOXY_PARAM_ATTR = new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Uncalibrated red phase shift reported by oxygen sensor";
    }

    @Override
    public Float getValidMin() {
      return 10f;
    }

    @Override
    public Float getValidMax() {
      return 70f;
    }

    @Override
    public String getUnits() {
      return "degree";
    }

    @Override
    public String getCFormat() {
      return "%8.2f";
    }

    @Override
    public String getFortranFormat() {
      return "F8.2";
    }

    @Override
    public Float getResolution() {
      return 0.01f;
    }

  };

  /*
  case 'MOLAR_DOXY'
         o_attributeStruct = struct('name', 'MOLAR_DOXY', ...
            'longName', 'Uncompensated (pressure and salinity) oxygen concentration reported by the oxygen sensor', ...
            'standardName', 'mole_concentration_of_dissolved_molecular_oxygen_in_sea_water', ...
            'fillValue', single(99999), ...
            'units', 'micromole/l', ...
            'validMin', single(0), ...
            'validMax', single(650), ...
            'axis', '', ...
            'cFormat', '%9.3f', ...
            'fortranFormat', 'F9.3', ...
            'resolution', single(0.001), ...
            'paramType', 'i', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);
   */

  // MOLAR_DOXY	Uncompensated (pressure and salinity) oxygen concentration reported by the oxygen sensor	Dissolved oxygen concentration uncorrected for salinity and pressure effects. Local_Attributes:{long_name:Uncompensated (pressure and salinity) oxygen concentration reported by the oxygen sensor; standard_name:mole_concentration_of_dissolved_molecular_oxygen_in_sea_water; units:micromole/l; valid_min:0.f; valid_max:650.f; fill_value:99999.f}. Properties:{category:ib; data_type:float}	2025-11-25
  private static final ParameterAttributes<Float> MOLAR_DOXY_PARAM_ATTR = new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Uncompensated (pressure and salinity) oxygen concentration reported by the oxygen sensor";
    }

    @Override
    public String getStandardName() {
      return "mole_concentration_of_dissolved_molecular_oxygen_in_sea_water";
    }

    @Override
    public Float getValidMin() {
      return 0f;
    }

    @Override
    public Float getValidMax() {
      return 650f;
    }

    @Override
    public String getUnits() {
      return "micromole/l";
    }

    @Override
    public String getCFormat() {
      return "%9.3f";
    }

    @Override
    public String getFortranFormat() {
      return "F9.3";
    }

    @Override
    public Float getResolution() {
      return 0.001f;
    }

  };

  /*

        case 'UP_RADIANCE412' % for nc_create_synthetic_profile
         o_attributeStruct = struct('name', 'UP_RADIANCE412', ...
            'longName', 'Upwelling radiance at 412 nanometers', ...
            'standardName', 'upwelling_radiance_in_sea_water', ...
            'fillValue', single(99999), ...
            'units', 'W/m^2/nm/sr', ...
            'validMin', '', ...
            'validMax', '', ...
            'axis', '', ...
            'cFormat', '%.3f', ...
            'fortranFormat', 'F.3', ...
            'resolution', single(0.001), ...
            'paramType', 'b', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 1);

      case 'UP_RADIANCE443' % for nc_create_synthetic_profile
         o_attributeStruct = struct('name', 'UP_RADIANCE443', ...
            'longName', 'Upwelling radiance at 443 nanometers', ...
            'standardName', 'upwelling_radiance_in_sea_water', ...
            'fillValue', single(99999), ...
            'units', 'W/m^2/nm/sr', ...
            'validMin', '', ...
            'validMax', '', ...
            'axis', '', ...
            'cFormat', '%.3f', ...
            'fortranFormat', 'F.3', ...
            'resolution', single(0.001), ...
            'paramType', 'b', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 1);

      case 'UP_RADIANCE490' % for nc_create_synthetic_profile
         o_attributeStruct = struct('name', 'UP_RADIANCE490', ...
            'longName', 'Upwelling radiance at 490 nanometers', ...
            'standardName', 'upwelling_radiance_in_sea_water', ...
            'fillValue', single(99999), ...
            'units', 'W/m^2/nm/sr', ...
            'validMin', '', ...
            'validMax', '', ...
            'axis', '', ...
            'cFormat', '%.3f', ...
            'fortranFormat', 'F.3', ...
            'resolution', single(0.001), ...
            'paramType', 'b', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 1);

      case 'UP_RADIANCE555' % for nc_create_synthetic_profile
         o_attributeStruct = struct('name', 'UP_RADIANCE555', ...
            'longName', 'Upwelling radiance at 555 nanometers', ...
            'standardName', 'upwelling_radiance_in_sea_water', ...
            'fillValue', single(99999), ...
            'units', 'W/m^2/nm/sr', ...
            'validMin', '', ...
            'validMax', '', ...
            'axis', '', ...
            'cFormat', '%.3f', ...
            'fortranFormat', 'F.3', ...
            'resolution', single(0.001), ...
            'paramType', 'b', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 1);


   */

  // UP_RADIANCE	Upwelling radiance at x nanometers	Upwelling radiance at unspecified wavelength, reported by OCR504 sensor (unit conversion). Local_Attributes:{long_name:Upwelling radiance at x nanometers; standard_name:upwelling_radiance_in_sea_water; units:W/m^2/nm/sr; valid_min:-; valid_max:-; fill_value:99999.f}. Properties:{category:b; data_type:float}	2025-11-25
  private static final Function<String, ParameterAttributes<Float>> UP_RADIANCE_PARAM_ATTR_FACTORY = (diNum) -> new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Upwelling radiance at " + diNum + " nanometers";
    }

    @Override
    public String getStandardName() {
      return "upwelling_radiance_in_sea_water";
    }

    @Override
    public String getUnits() {
      return "W/m^2/nm/sr";
    }

    @Override
    public String getCFormat() {
      return "%.3f";
    }

    @Override
    public String getFortranFormat() {
      return "F.3";
    }

    @Override
    public Float getResolution() {
      return 0.001f;
    }

  };

  /*
       case 'VRS_PH'
         o_attributeStruct = struct('name', 'VRS_PH', ...
            'longName', 'Voltage difference between reference and source from pH sensor', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'volt', ...
            'validMin', '', ...
            'validMax', '', ...
            'axis', '', ...
            'cFormat', '%.6f', ...
            'fortranFormat', 'F.6', ...
            'resolution', single(0.000001), ...
            'paramType', 'i', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);
   */

  // VRS_PH	Voltage difference between reference and source from pH sensor	Raw output signal from pH sensor, equating to the potential between the transistor source and the reference electrode. Local_Attributes:{long_name:Voltage difference between reference and source from pH sensor; standard_name:-; units:volt; valid_min:-; valid_max:-; fill_value:99999.f}. Properties:{category:ib; data_type:float}	2025-11-25
  private static final ParameterAttributes<Float> VRS_PH_PARAM_ATTR = new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Voltage difference between reference and source from pH sensor";
    }

    @Override
    public String getUnits() {
      return "volt";
    }

    @Override
    public String getCFormat() {
      return "%.6f";
    }

    @Override
    public String getFortranFormat() {
      return "F.6";
    }

    @Override
    public Float getResolution() {
      return 0.000001f;
    }

  };

  /*
  case 'VRS_PH_STD'
         o_attributeStruct = struct('name', 'VRS_PH_STD', ...
            'longName', 'Standard deviation of voltage difference between reference and source from pH sensor', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'volt', ...
            'validMin', '', ...
            'validMax', '', ...
            'axis', '', ...
            'cFormat', '%.6f', ...
            'fortranFormat', 'F.6', ...
            'resolution', single(0.000001), ...
            'paramType', 'i', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);

            */
  private static final ParameterAttributes<Float> VRS_PH_STD_PARAM_ATTR = new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Standard deviation of voltage difference between reference and source from pH sensor";
    }

    @Override
    public String getUnits() {
      return "volt";
    }

    @Override
    public String getCFormat() {
      return "%.6f";
    }

    @Override
    public String getFortranFormat() {
      return "F.6";
    }

    @Override
    public Float getResolution() {
      return 0.000001f;
    }

  };
  /*

      case 'VRS_PH_MED'
         o_attributeStruct = struct('name', 'VRS_PH_MED', ...
            'longName', 'Median value of voltage difference between reference and source from pH sensor', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'volt', ...
            'validMin', '', ...
            'validMax', '', ...
            'axis', '', ...
            'cFormat', '%.6f', ...
            'fortranFormat', 'F.6', ...
            'resolution', single(0.000001), ...
            'paramType', 'i', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);
   */
  private static final ParameterAttributes<Float> VRS_PH_MED_PARAM_ATTR = new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Median value of voltage difference between reference and source from pH sensor";
    }

    @Override
    public String getUnits() {
      return "volt";
    }

    @Override
    public String getCFormat() {
      return "%.6f";
    }

    @Override
    public String getFortranFormat() {
      return "F.6";
    }

    @Override
    public Float getResolution() {
      return 0.000001f;
    }

  };

  /*
  case 'VOLTAGE_DOXY'
         o_attributeStruct = struct('name', 'VOLTAGE_DOXY', ...
            'longName', 'Voltage reported by oxygen sensor', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'volt', ...
            'validMin', single(0), ...
            'validMax', single(100), ...
            'axis', '', ...
            'cFormat', '%5.2f', ...
            'fortranFormat', 'F5.2', ...
            'resolution', single(0.01), ...
            'paramType', 'i', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);
   */

  // VOLTAGE_DOXY	Voltage reported by oxygen sensor	Raw output signal from dissolved oxygen sensor. Local_Attributes:{long_name:Voltage reported by oxygen sensor; standard_name:-; units:volt; valid_min:0.f; valid_max:100.f; fill_value:99999.f}. Properties:{category:ib; data_type:float}	2025-11-25
  private static final ParameterAttributes<Float> VOLTAGE_DOXY_PARAM_ATTR = new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Voltage reported by oxygen sensor";
    }

    @Override
    public Float getValidMin() {
      return 0f;
    }

    @Override
    public Float getValidMax() {
      return 100f;
    }

    @Override
    public String getUnits() {
      return "volt";
    }

    @Override
    public String getCFormat() {
      return "%5.2f";
    }

    @Override
    public String getFortranFormat() {
      return "F5.2";
    }

    @Override
    public Float getResolution() {
      return 0.01f;
    }

  };


  /*
  case 'PH_IN_SITU_FREE'
         o_attributeStruct = struct('name', 'PH_IN_SITU_FREE', ...
            'longName', 'pH', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'dimensionless', ...
            'validMin', '', ...
            'validMax', '', ...
            'axis', '', ...
            'cFormat', '%.4f', ...
            'fortranFormat', 'F.4', ...
            'resolution', single(0.0001), ...
            'paramType', 'i', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);
   */

  // PH_IN_SITU_FREE	pH (free scale)	pH against the free scale (pH based on free proton concentration). Local_Attributes:{long_name:pH; standard_name:-; units:dimensionless; valid_min:-; valid_max:-; fill_value:99999.f}. Properties:{category:ib; data_type:float}	2025-11-25
  private static final ParameterAttributes<Float> PH_IN_SITU_FREE_PARAM_ATTR = new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "pH";
    }

    @Override
    public String getUnits() {
      return "dimensionless";
    }

    @Override
    public String getCFormat() {
      return "%.4f";
    }

    @Override
    public String getFortranFormat() {
      return "F.4";
    }

    @Override
    public Float getResolution() {
      return 0.0001f;
    }

  };

  /*
  case 'PH_IN_SITU_SEAWATER'
         o_attributeStruct = struct('name', 'PH_IN_SITU_SEAWATER', ...
            'longName', 'pH', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'dimensionless', ...
            'validMin', '', ...
            'validMax', '', ...
            'axis', '', ...
            'cFormat', '%.4f', ...
            'fortranFormat', 'F.4', ...
            'resolution', single(0.0001), ...
            'paramType', 'i', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);
   */

  // PH_IN_SITU_SEAWATER	pH (seawater scale)	pH against the seawater scale (pH based on free proton concentration and proton associated with SO4 2- and F- ions). Local_Attributes:{long_name:pH; standard_name:-; units:dimensionless; valid_min:-; valid_max:-; fill_value:99999.f}. Properties:{category:ib; data_type:float}	2025-11-25
  private static final ParameterAttributes<Float> PH_IN_SITU_SEAWATER_PARAM_ATTR = new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "pH";
    }

    @Override
    public String getUnits() {
      return "dimensionless";
    }

    @Override
    public String getCFormat() {
      return "%.4f";
    }

    @Override
    public String getFortranFormat() {
      return "F.4";
    }

    @Override
    public Float getResolution() {
      return 0.0001f;
    }

  };

  /*
  case 'PH_IN_SITU_TOTAL'
         o_attributeStruct = struct('name', 'PH_IN_SITU_TOTAL', ...
            'longName', 'pH', ...
            'standardName', 'sea_water_ph_reported_on_total_scale', ...
            'fillValue', single(99999), ...
            'units', 'dimensionless', ...
            'validMin', '', ...
            'validMax', '', ...
            'axis', '', ...
            'cFormat', '%.4f', ...
            'fortranFormat', 'F.4', ...
            'resolution', single(0.0001), ...
            'paramType', 'b', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 1);
   */

  // PH_IN_SITU_TOTAL	pH (total scale)	pH against the total scale (pH based on free proton concentration and proton associated with SO4 2- ion). Local_Attributes:{long_name:pH; standard_name:sea_water_ph_reported_on_total_scale; units:dimensionless; valid_min:-; valid_max:-; fill_value:99999.f}. Properties:{category:b; data_type:float}	2025-11-25
  private static final ParameterAttributes<Float> PH_IN_SITU_TOTAL_PARAM_ATTR = new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "pH";
    }

    @Override
    public String getStandardName() {
      return "sea_water_ph_reported_on_total_scale";
    }

    @Override
    public String getUnits() {
      return "dimensionless";
    }

    @Override
    public String getCFormat() {
      return "%.4f";
    }

    @Override
    public String getFortranFormat() {
      return "F.4";
    }

    @Override
    public Float getResolution() {
      return 0.0001f;
    }

  };

  /*
  case 'FLUORESCENCE_VOLTAGE_CHLA' % for CYCLOPS of ARVOR CM
         o_attributeStruct = struct('name', 'FLUORESCENCE_VOLTAGE_CHLA', ...
            'longName', 'Chlorophyll-A signal from analogic fluorescence sensor', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'volt', ...
            'validMin', '', ...
            'validMax', '', ...
            'axis', '', ...
            'cFormat', '%.3f', ...
            'fortranFormat', 'F.3', ...
            'resolution', single(0.001), ...
            'paramType', 'i', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);
       */
  private static final ParameterAttributes<Float> FLUORESCENCE_VOLTAGE_CHLA_PARAM_ATTR = new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Chlorophyll-A signal from analogic fluorescence sensor";
    }

    @Override
    public String getUnits() {
      return "volt";
    }

    @Override
    public String getCFormat() {
      return "%.3f";
    }

    @Override
    public String getFortranFormat() {
      return "F.3";
    }

    @Override
    public Float getResolution() {
      return 0.001f;
    }

  };

  /*

      case 'FLUORESCENCE_VOLTAGE_CHLA_STD' % for CYCLOPS of ARVOR CM
         o_attributeStruct = struct('name', 'FLUORESCENCE_VOLTAGE_CHLA_STD', ...
            'longName', 'Standard deviation of chlorophyll-A signal from analogic fluorescence sensor', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'volt', ...
            'validMin', '', ...
            'validMax', '', ...
            'axis', '', ...
            'cFormat', '%.3f', ...
            'fortranFormat', 'F.3', ...
            'resolution', single(0.001), ...
            'paramType', 'i', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);
                 */
  private static final ParameterAttributes<Float> FLUORESCENCE_VOLTAGE_CHLA_STD_PARAM_ATTR = new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Standard deviation of chlorophyll-A signal from analogic fluorescence sensor";
    }

    @Override
    public String getUnits() {
      return "volt";
    }

    @Override
    public String getCFormat() {
      return "%.3f";
    }

    @Override
    public String getFortranFormat() {
      return "F.3";
    }

    @Override
    public Float getResolution() {
      return 0.001f;
    }

  };


  /*

      case 'FLUORESCENCE_VOLTAGE_CHLA_MED' % for CYCLOPS of ARVOR CM
         o_attributeStruct = struct('name', 'FLUORESCENCE_VOLTAGE_CHLA_MED', ...
            'longName', 'Median value of chlorophyll-A signal from analogic fluorescence sensor', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'volt', ...
            'validMin', '', ...
            'validMax', '', ...
            'axis', '', ...
            'cFormat', '%.3f', ...
            'fortranFormat', 'F.3', ...
            'resolution', single(0.001), ...
            'paramType', 'i', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);
   */
  private static final ParameterAttributes<Float> FLUORESCENCE_VOLTAGE_CHLA_MED_PARAM_ATTR = new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Median value of chlorophyll-A signal from analogic fluorescence sensor";
    }

    @Override
    public String getUnits() {
      return "volt";
    }

    @Override
    public String getCFormat() {
      return "%.3f";
    }

    @Override
    public String getFortranFormat() {
      return "F.3";
    }

    @Override
    public Float getResolution() {
      return 0.001f;
    }

  };

  /*
  case 'VOLTAGE_TURBIDITY' % for SEAPOINT of ARVOR CM
         o_attributeStruct = struct('name', 'VOLTAGE_TURBIDITY', ...
            'longName', 'Turbidity signal from side scattering analogic sensor', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'volt', ...
            'validMin', '', ...
            'validMax', '', ...
            'axis', '', ...
            'cFormat', '%.3f', ...
            'fortranFormat', 'F.3', ...
            'resolution', single(0.001), ...
            'paramType', 'i', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);

       */

  private static final ParameterAttributes<Float> VOLTAGE_TURBIDITY_PARAM_ATTR = new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Turbidity signal from side scattering analogic sensor";
    }

    @Override
    public String getUnits() {
      return "volt";
    }

    @Override
    public String getCFormat() {
      return "%.3f";
    }

    @Override
    public String getFortranFormat() {
      return "F.3";
    }

    @Override
    public Float getResolution() {
      return 0.001f;
    }

  };

  /*

      case 'VOLTAGE_TURBIDITY_STD' % for SEAPOINT of ARVOR CM
         o_attributeStruct = struct('name', 'VOLTAGE_TURBIDITY_STD', ...
            'longName', 'Standard deviation of turbidity signal from side scattering analogic sensor', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'volt', ...
            'validMin', '', ...
            'validMax', '', ...
            'axis', '', ...
            'cFormat', '%.3f', ...
            'fortranFormat', 'F.3', ...
            'resolution', single(0.001), ...
            'paramType', 'i', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);

                 */
  private static final ParameterAttributes<Float> VOLTAGE_TURBIDITY_STD_PARAM_ATTR = new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Standard deviation of turbidity signal from side scattering analogic sensor";
    }

    @Override
    public String getUnits() {
      return "volt";
    }

    @Override
    public String getCFormat() {
      return "%.3f";
    }

    @Override
    public String getFortranFormat() {
      return "F.3";
    }

    @Override
    public Float getResolution() {
      return 0.001f;
    }

  };

  /*

      case 'VOLTAGE_TURBIDITY_MED' % for SEAPOINT of ARVOR CM
         o_attributeStruct = struct('name', 'VOLTAGE_TURBIDITY_MED', ...
            'longName', 'Median value of turbidity signal from side scattering analogic sensor', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'volt', ...
            'validMin', '', ...
            'validMax', '', ...
            'axis', '', ...
            'cFormat', '%.3f', ...
            'fortranFormat', 'F.3', ...
            'resolution', single(0.001), ...
            'paramType', 'i', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);
   */
  private static final ParameterAttributes<Float> VOLTAGE_TURBIDITY_MED_PARAM_ATTR = new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Median value of turbidity signal from side scattering analogic sensor";
    }

    @Override
    public String getUnits() {
      return "volt";
    }

    @Override
    public String getCFormat() {
      return "%.3f";
    }

    @Override
    public String getFortranFormat() {
      return "F.3";
    }

    @Override
    public Float getResolution() {
      return 0.001f;
    }

  };


  /*

      case 'NB_SAMPLE_ICE_DETECTION'  % for TRAJ_AUX
         o_attributeStruct = struct('name', 'NB_SAMPLE_ICE_DETECTION', ...
            'longName', 'Number of samples used to detect Ice', ...
            'standardName', '', ...
            'fillValue', single(99999), ...
            'units', 'dimensionless', ...
            'validMin', '', ...
            'validMax', '', ...
            'axis', '', ...
            'cFormat', '%d', ...
            'fortranFormat', 'I', ...
            'resolution', single(1), ...
            'paramType', 'i', ...
            'paramNcType', 'NC_FLOAT', ...
            'adjAllowed', 0);
   */
  private static final ParameterAttributes<Float> NB_SAMPLE_ICE_DETECTION_PARAM_ATTR = new ParameterAttributes<>() {
    @Override
    public String getLongName() {
      return "Number of samples used to detect Ice";
    }

    @Override
    public String getUnits() {
      return "dimensionless";
    }

    @Override
    public String getCFormat() {
      return "%d";
    }

    @Override
    public String getFortranFormat() {
      return "I";
    }

    @Override
    public Float getResolution() {
      return 1f;
    }

  };

  private static final Map<String, ParameterAttributes<Float>> namedParameterAttributes;

  static {
    Map<String, ParameterAttributes<Float>> map = new HashMap<>();

    map.put("PRES", PRESS_PARAM_ATTR);
    map.put("TEMP", TEMP_PARAM_ATTR);
    map.put("PSAL", PSAL_PARAM_ATTR);
    map.put("DOXY", DOXY_PARAM_ATTR);
    map.put("CDOM", CDOM_PARAM_ATTR);
    map.put("CHLA", CHLA_PARAM_ATTR);
    map.put("BISULFIDE", BISULFIDE_PARAM_ATTR);
    map.put("NITRATE", NITRATE_PARAM_ATTR);
    map.put("DOWNWELLING_PAR", DOWNWELLING_PAR_PARAM_ATTR);
    map.put("TURBIDITY", TURBIDITY_PARAM_ATTR);
    map.put("IB_PH", IB_PH_PARAM_ATTR);
    map.put("DPHASE_DOXY", DPHASE_DOXY_PARAM_ATTR);
    map.put("CHLA_FLUORESCENCE", CHLA_FLUORESCENCE_PARAM_ATTR);
    map.put("FLUORESCENCE_CHLA", FLUORESCENCE_CHLA_PARAM_ATTR);
    map.put("COUNT_DOXY", COUNT_DOXY_PARAM_ATTR);
    map.put("TEMP_COUNT_DOXY", TEMP_COUNT_DOXY_PARAM_ATTR);
    map.put("IK_PH", IK_PH_PARAM_ATTR);
    map.put("VK_PH", VK_PH_PARAM_ATTR);
    map.put("CNDC", CNDC_PARAM_ATTR);
    map.put("MTIME", MTIME_PARAM_ATTR);
    map.put("FREQUENCY_DOXY", FREQUENCY_DOXY_PARAM_ATTR);
    map.put("TILT", TILT_PARAM_ATTR);
    map.put("UV_INTENSITY_DARK_NITRATE", UV_INTENSITY_DARK_NITRATE_PARAM_ATTR);
    map.put("UV_INTENSITY_NITRATE", UV_INTENSITY_NITRATE_PARAM_ATTR);
    map.put("UV_INTENSITY_DARK_SEAWATER_NITRATE", UV_INTENSITY_DARK_SEAWATER_NITRATE_PARAM_ATTR);
    map.put("TEMP_NITRATE", TEMP_NITRATE_PARAM_ATTR);
    map.put("TEMP_CNDC", TEMP_CNDC_PARAM_ATTR);
    map.put("MOLAR_NITRATE", MOLAR_NITRATE_PARAM_ATTR);
    map.put("FIT_ERROR_NITRATE", FIT_ERROR_NITRATE_PARAM_ATTR);
    map.put("NB_SAMPLE_CTD", NB_SAMPLE_CTD_PARAM_ATTR);
    map.put("NB_SAMPLE_SFET", NB_SAMPLE_SFET_PARAM_ATTR);
    map.put("LED_FLASHING_COUNT_DOXY", LED_FLASHING_COUNT_DOXY_PARAM_ATTR);
    map.put("MLPL_DOXY", MLPL_DOXY_PARAM_ATTR);
    map.put("PPOX_DOXY", PPOX_DOXY_PARAM_ATTR);
    map.put("PHASE_DELAY_DOXY", PHASE_DELAY_DOXY_PARAM_ATTR);
    map.put("RAW_DOWNWELLING_PAR", RAW_DOWNWELLING_PAR_PARAM_ATTR);
    map.put("FLUORESCENCE_CDOM", FLUORESCENCE_CDOM_PARAM_ATTR);
    map.put("HUMIDITY_NITRATE", HUMIDITY_NITRATE_PARAM_ATTR);
    map.put("TEMP_DOXY", TEMP_DOXY_PARAM_ATTR);
    map.put("TEMP_PH", TEMP_PH_PARAM_ATTR);
    map.put("TEMP_SPECTROPHOTOMETER_NITRATE", TEMP_SPECTROPHOTOMETER_NITRATE_PARAM_ATTR);
    map.put("TEMP_CPU_CHLA", TEMP_CPU_CHLA_PARAM_ATTR);
    map.put("TEMP_VOLTAGE_DOXY", TEMP_VOLTAGE_DOXY_PARAM_ATTR);
    map.put("SIDE_SCATTERING_TURBIDITY", SIDE_SCATTERING_TURBIDITY_PARAM_ATTR);
    map.put("BPHASE_DOXY", BPHASE_DOXY_PARAM_ATTR);
    map.put("C1PHASE_DOXY", C1PHASE_DOXY_PARAM_ATTR);
    map.put("TPHASE_DOXY", TPHASE_DOXY_PARAM_ATTR);
    map.put("C2PHASE_DOXY", C2PHASE_DOXY_PARAM_ATTR);
    map.put("RPHASE_DOXY", RPHASE_DOXY_PARAM_ATTR);
    map.put("MOLAR_DOXY", MOLAR_DOXY_PARAM_ATTR);
    map.put("VRS_PH", VRS_PH_PARAM_ATTR);
    map.put("VOLTAGE_DOXY", VOLTAGE_DOXY_PARAM_ATTR);
    map.put("PH_IN_SITU_FREE", PH_IN_SITU_FREE_PARAM_ATTR);
    map.put("PH_IN_SITU_SEAWATER", PH_IN_SITU_SEAWATER_PARAM_ATTR);
    map.put("PH_IN_SITU_TOTAL", PH_IN_SITU_TOTAL_PARAM_ATTR);
    map.put("RAW_DOWNWELLING_PAR_MED", RAW_DOWNWELLING_PAR_MED_PARAM_ATTR);
    map.put("RAW_DOWNWELLING_PAR_STD", RAW_DOWNWELLING_PAR_STD_PARAM_ATTR);
    map.put("PRESS_MED", PRESS_MED_PARAM_ATTR);
    map.put("TEMP_STD", TEMP_STD_PARAM_ATTR);
    map.put("TEMP_MED", TEMP_MED_PARAM_ATTR);
    map.put("PSAL_MED", PSAL_MED_PARAM_ATTR);
    map.put("PSAL_STD", PSAL_STD_PARAM_ATTR);
    map.put("TEMP_DOXY_MED", TEMP_DOXY_MED_PARAM_ATTR);
    map.put("TEMP_DOXY_STD", TEMP_DOXY_STD_PARAM_ATTR);
    map.put("C1PHASE_DOXY_MED", C1PHASE_DOXY_MED_PARAM_ATTR);
    map.put("C1PHASE_DOXY_STD", C1PHASE_DOXY_STD_PARAM_ATTR);
    map.put("C2PHASE_DOXY_STD", C2PHASE_DOXY_STD_PARAM_ATTR);
    map.put("C2PHASE_DOXY_MED", C2PHASE_DOXY_MED_PARAM_ATTR);
    map.put("FLUORESCENCE_CHLA_MED", FLUORESCENCE_CHLA_MED_PARAM_ATTR);
    map.put("FLUORESCENCE_CHLA_STD", FLUORESCENCE_CHLA_STD_PARAM_ATTR);
    map.put("FLUORESCENCE_CDOM_MED", FLUORESCENCE_CDOM_MED_PARAM_ATTR);
    map.put("FLUORESCENCE_CDOM_STD", FLUORESCENCE_CDOM_STD_PARAM_ATTR);
    map.put("VRS_PH_MED", VRS_PH_MED_PARAM_ATTR);
    map.put("VRS_PH_STD", VRS_PH_STD_PARAM_ATTR);
    map.put("FLUORESCENCE_VOLTAGE_CHLA_MED", FLUORESCENCE_VOLTAGE_CHLA_MED_PARAM_ATTR);
    map.put("FLUORESCENCE_VOLTAGE_CHLA_STD", FLUORESCENCE_VOLTAGE_CHLA_STD_PARAM_ATTR);
    map.put("FLUORESCENCE_VOLTAGE_CHLA", FLUORESCENCE_VOLTAGE_CHLA_PARAM_ATTR);
    map.put("SIDE_SCATTERING_TURBIDITY_MED", SIDE_SCATTERING_TURBIDITY_MED_PARAM_ATTR);
    map.put("SIDE_SCATTERING_TURBIDITY_STD", SIDE_SCATTERING_TURBIDITY_STD_PARAM_ATTR);
    map.put("VOLTAGE_TURBIDITY_MED", VOLTAGE_TURBIDITY_MED_PARAM_ATTR);
    map.put("VOLTAGE_TURBIDITY_STD", VOLTAGE_TURBIDITY_STD_PARAM_ATTR);
    map.put("VOLTAGE_TURBIDITY", VOLTAGE_TURBIDITY_PARAM_ATTR);
    map.put("UV_INTENSITY_DARK_NITRATE_STD", UV_INTENSITY_DARK_NITRATE_STD_PARAM_ATTR);
    map.put("NB_SAMPLE_ICE_DETECTION", NB_SAMPLE_ICE_DETECTION_PARAM_ATTR);

    namedParameterAttributes = Collections.unmodifiableMap(map);
  }

  public static ParameterAttributes<Float> getParameterAttributes(String parameterName) {

    ParameterAttributes<Float> parameterAttributes = namedParameterAttributes.get(parameterName);
    if (parameterAttributes != null) {
      return parameterAttributes;
    }

    Matcher matcher = DOWN_IRRADIANCE_REGEX.matcher(parameterName);
    if (matcher.matches()) {
      return DOWN_IRRADIANCE_PARAM_ATTR_FACTORY.apply(matcher.group(1));
    }

    matcher = BBP_REGEX.matcher(parameterName);
    if (matcher.matches()) {
      return BBP_PARAM_ATTR_FACTORY.apply(matcher.group(1));
    }

    matcher = CP_REGEX.matcher(parameterName);
    if (matcher.matches()) {
      return CP_PARAM_ATTR_FACTORY.apply(matcher.group(1));
    }

    matcher = UP_RADIANCE_REGEX.matcher(parameterName);
    if (matcher.matches()) {
      return UP_RADIANCE_PARAM_ATTR_FACTORY.apply(matcher.group(1));
    }

    matcher = TRANSMITTANCE_PARTICLE_BEAM_ATTENUATION_REGEX.matcher(parameterName);
    if (matcher.matches()) {
      return TRANSMITTANCE_PARTICLE_BEAM_ATTENUATION_PARAM_ATTR_FACTORY.apply(matcher.group(1));
    }

    matcher = RAW_DOWNWELLING_IRRADIANCE_REGEX.matcher(parameterName);
    if (matcher.matches()) {
      return RAW_DOWNWELLING_IRRADIANCE_PARAM_ATTR_FACTORY.apply(matcher.group(1));
    }

    matcher = RAW_DOWNWELLING_IRRADIANCE_MED_REGEX.matcher(parameterName);
    if (matcher.matches()) {
      return RAW_DOWNWELLING_IRRADIANCE_MED_PARAM_ATTR_FACTORY.apply(matcher.group(1));
    }

    matcher = RAW_DOWNWELLING_IRRADIANCE_STD_REGEX.matcher(parameterName);
    if (matcher.matches()) {
      return RAW_DOWNWELLING_IRRADIANCE_STD_PARAM_ATTR_FACTORY.apply(matcher.group(1));
    }

    matcher = RAW_UPWELLING_RADIANCE_REGEX.matcher(parameterName);
    if (matcher.matches()) {
      return RAW_UPWELLING_RADIANCE_PARAM_ATTR_FACTORY.apply(matcher.group(1));
    }

    matcher = BETA_BACKSCATTERING_REGEX.matcher(parameterName);
    if (matcher.matches()) {
      return BETA_BACKSCATTERING_PARAM_ATTR_FACTORY.apply(matcher.group(1));
    }

    matcher = TRANSMITTANCE_PARTICLE_BEAM_ATTENUATION_MED_REGEX.matcher(parameterName);
    if (matcher.matches()) {
      return TRANSMITTANCE_PARTICLE_BEAM_ATTENUATION_MED_PARAM_ATTR_FACTORY.apply(matcher.group(1));
    }

    matcher = TRANSMITTANCE_PARTICLE_BEAM_ATTENUATION_STD_REGEX.matcher(parameterName);
    if (matcher.matches()) {
      return TRANSMITTANCE_PARTICLE_BEAM_ATTENUATION_STD_PARAM_ATTR_FACTORY.apply(matcher.group(1));
    }

    matcher = BETA_BACKSCATTERING_STD_REGEX.matcher(parameterName);
    if (matcher.matches()) {
      return BETA_BACKSCATTERING_STD_PARAM_ATTR_FACTORY.apply(matcher.group(1));
    }

    matcher = BETA_BACKSCATTERING_MED_REGEX.matcher(parameterName);
    if (matcher.matches()) {
      return BETA_BACKSCATTERING_MED_PARAM_ATTR_FACTORY.apply(matcher.group(1));
    }

    return new ParameterAttributes<>() {
      @Override
      public String getLongName() {
        return parameterName;
      }
    };
  }


  public static void addGlobalAttributes(NetcdfFormatWriter.Builder builder, String institution, String softwareVersion) {
    builder.addAttribute(new Attribute("title", "Argo float vertical profile"));
    if (institution != null) {
      builder.addAttribute(new Attribute("institution", institution));
    }
    builder.addAttribute(new Attribute("source", "Argo float"));
    builder.addAttribute(new Attribute("history", Instant.now().toString() + " creation (Argonaut " + softwareVersion + ")"));
    builder.addAttribute(new Attribute("references", "http://www.argodatamgt.org/Documentation, https://github.com/CI-CMG/argonaut"));
    builder.addAttribute(new Attribute("user_manual_version", "1.0"));
    builder.addAttribute(new Attribute("Conventions", "Argo-3.1 CF-1.6"));
    builder.addAttribute(new Attribute("featureType", "trajectoryProfile"));
    builder.addAttribute(new Attribute("software_version", softwareVersion + " (Argonaut " + softwareVersion + ")"));
    builder.addAttribute(new Attribute("id", "https://doi.org/10.17882/42182"));
  }

  public static ArgoNetCdfCreatedDimensions addCommonDimensions(NetcdfFormatWriter.Builder builder, ArgoNetCdfDimensions dimensions) {
    Dimension dateTimeDim = builder.addDimension("DATE_TIME", 14);
    Dimension string256Dim = builder.addDimension("STRING256", 256);
    Dimension string64Dim = builder.addDimension("STRING64", 64);
    Dimension string32Dim = builder.addDimension("STRING32", 32);
    Dimension string16Dim = builder.addDimension("STRING16", 16);
    Dimension string8Dim = builder.addDimension("STRING8", 8);
    Dimension string4Dim = builder.addDimension("STRING4", 4);
    Dimension string2Dim = builder.addDimension("STRING2", 2);
    Dimension nProfDim = builder.addDimension("N_PROF", dimensions.getProfiles());
    Dimension nParamDim = builder.addDimension("N_PARAM", dimensions.getParameters());
    Dimension nLevelsDim = builder.addDimension("N_LEVELS", dimensions.getLevels());
    Dimension nCalibDim = builder.addDimension("N_CALIB", dimensions.getCalibrations());
    Dimension nHistoryDim = dimensions.getHistories() > 0 ? builder.addDimension("N_HISTORY", dimensions.getHistories()) : builder.addUnlimitedDimension("N_HISTORY");
    return new ArgoNetCdfCreatedDimensions(
        dateTimeDim, string256Dim, string64Dim, string32Dim, string16Dim, string8Dim,
        string4Dim, string2Dim, nProfDim, nParamDim, nLevelsDim, nCalibDim,
        nHistoryDim
    );
  }

  public static void addCommonVariables(NetcdfFormatWriter.Builder builder, ArgoNetCdfCreatedDimensions dimensions, List<String> parameterNames) {

    Dimension dateTimeDim = dimensions.getDateTimeDim();
    Dimension string256Dim = dimensions.getString256Dim();
    Dimension string64Dim = dimensions.getString64Dim();
    Dimension string32Dim = dimensions.getString32Dim();
    Dimension string16Dim = dimensions.getString16Dim();
    Dimension string8Dim = dimensions.getString8Dim();
    Dimension string4Dim = dimensions.getString4Dim();
    Dimension string2Dim = dimensions.getString2Dim();
    Dimension nProfDim = dimensions.getnProfDim();
    Dimension nParamDim = dimensions.getnParamDim();
    Dimension nLevelsDim = dimensions.getnLevelsDim();
    Dimension nCalibDim = dimensions.getnCalibDim();
    Dimension nHistoryDim = dimensions.getnHistoryDim();

    builder.addVariable("DATA_TYPE", DataType.CHAR, Collections.singletonList(string32Dim))
        .addAttribute(new Attribute("_FillValue", " "))
        .addAttribute(new Attribute("long_name", "Data type"))
        .addAttribute(new Attribute("conventions", "Argo reference table 1"));

    builder.addVariable("FORMAT_VERSION", DataType.CHAR, Collections.singletonList(string4Dim))
        .addAttribute(new Attribute("_FillValue", " "))
        .addAttribute(new Attribute("long_name", "File format version"));

    builder.addVariable("HANDBOOK_VERSION", DataType.CHAR, Collections.singletonList(string4Dim))
        .addAttribute(new Attribute("_FillValue", " "))
        .addAttribute(new Attribute("long_name", "Data handbook version"));

    builder.addVariable("REFERENCE_DATE_TIME", DataType.CHAR, Collections.singletonList(dateTimeDim))
        .addAttribute(new Attribute("_FillValue", " "))
        .addAttribute(new Attribute("long_name", "Date of reference for Julian days"))
        .addAttribute(new Attribute("conventions", "YYYYMMDDHHMISS"));

    builder.addVariable("DATE_CREATION", DataType.CHAR, Collections.singletonList(dateTimeDim))
        .addAttribute(new Attribute("_FillValue", " "))
        .addAttribute(new Attribute("long_name", "Date of file creation"))
        .addAttribute(new Attribute("conventions", "YYYYMMDDHHMISS"));

    builder.addVariable("DATE_UPDATE", DataType.CHAR, Collections.singletonList(dateTimeDim))
        .addAttribute(new Attribute("_FillValue", " "))
        .addAttribute(new Attribute("long_name", "Date of update of this file"))
        .addAttribute(new Attribute("conventions", "YYYYMMDDHHMISS"));

    builder.addVariable("PLATFORM_NUMBER", DataType.CHAR, Arrays.asList(nProfDim, string8Dim))
        .addAttribute(new Attribute("_FillValue", " "))
        .addAttribute(new Attribute("long_name", "Float unique identifier"))
        .addAttribute(new Attribute("conventions", "WMO float identifier : A9IIIII"));

    builder.addVariable("PROJECT_NAME", DataType.CHAR, Arrays.asList(nProfDim, string64Dim))
        .addAttribute(new Attribute("_FillValue", " "))
        .addAttribute(new Attribute("long_name", "Name of the project"));

    builder.addVariable("PI_NAME", DataType.CHAR, Arrays.asList(nProfDim, string64Dim))
        .addAttribute(new Attribute("_FillValue", " "))
        .addAttribute(new Attribute("long_name", "Name of the principal investigator"));

    builder.addVariable("VERTICAL_SAMPLING_SCHEME", DataType.CHAR, Arrays.asList(nProfDim, string256Dim))
        .addAttribute(new Attribute("_FillValue", " "))
        .addAttribute(new Attribute("long_name", "Vertical sampling scheme"))
        .addAttribute(new Attribute("conventions", "Argo reference table 16"));

    builder.addVariable("DATA_STATE_INDICATOR", DataType.CHAR, Arrays.asList(nProfDim, string4Dim))
        .addAttribute(new Attribute("_FillValue", " "))
        .addAttribute(new Attribute("long_name", "Degree of processing the data have passed through"))
        .addAttribute(new Attribute("conventions", "Argo reference table 6"));

    builder.addVariable("STATION_PARAMETERS", DataType.CHAR, Arrays.asList(nProfDim, nParamDim, string64Dim))
        .addAttribute(new Attribute("_FillValue", " "))
        .addAttribute(new Attribute("long_name", "List of available parameters for the station"))
        .addAttribute(new Attribute("conventions", "Argo reference table 3"));

    builder.addVariable("CYCLE_NUMBER", DataType.INT, Collections.singletonList(nProfDim))
        .addAttribute(new Attribute("_FillValue", 99999))
        .addAttribute(new Attribute("long_name", "Float cycle number"))
        .addAttribute(new Attribute("conventions", "0...N, 0 : launch cycle (if exists), 1 : first complete cycle"));

    builder.addVariable("DIRECTION", DataType.CHAR, Collections.singletonList(nProfDim))
        .addAttribute(new Attribute("_FillValue", " "))
        .addAttribute(new Attribute("long_name", "Direction of the station profile"))
        .addAttribute(new Attribute("conventions", "A: ascending profiles, D: descending profiles"));

    builder.addVariable("DATA_CENTRE", DataType.CHAR, Arrays.asList(nProfDim, string2Dim))
        .addAttribute(new Attribute("_FillValue", " "))
        .addAttribute(new Attribute("long_name", "Data centre in charge of float data processing"))
        .addAttribute(new Attribute("conventions", "Argo reference table 4"));

    builder.addVariable("DC_REFERENCE", DataType.CHAR, Arrays.asList(nProfDim, string32Dim))
        .addAttribute(new Attribute("_FillValue", " "))
        .addAttribute(new Attribute("long_name", "Station unique identifier in data centre"))
        .addAttribute(new Attribute("conventions", "Data centre convention"));

    builder.addVariable("PARAMETER_DATA_MODE", DataType.CHAR, Arrays.asList(nProfDim, nParamDim))
        .addAttribute(new Attribute("_FillValue", " "))
        .addAttribute(new Attribute("long_name", "Delayed mode or real time data"))
        .addAttribute(new Attribute("conventions", "R : real time; D : delayed mode; A : real time with adjustment"));

    builder.addVariable("PLATFORM_TYPE", DataType.CHAR, Arrays.asList(nProfDim, string32Dim))
        .addAttribute(new Attribute("_FillValue", " "))
        .addAttribute(new Attribute("long_name", "Type of float"))
        .addAttribute(new Attribute("conventions", "Argo reference table 23"));

    builder.addVariable("FLOAT_SERIAL_NO", DataType.CHAR, Arrays.asList(nProfDim, string32Dim))
        .addAttribute(new Attribute("_FillValue", " "))
        .addAttribute(new Attribute("long_name", "Serial number of the float"));

    builder.addVariable("FIRMWARE_VERSION", DataType.CHAR, Arrays.asList(nProfDim, string32Dim))
        .addAttribute(new Attribute("_FillValue", " "))
        .addAttribute(new Attribute("long_name", "Instrument firmware version"));

    builder.addVariable("WMO_INST_TYPE", DataType.CHAR, Arrays.asList(nProfDim, string4Dim))
        .addAttribute(new Attribute("_FillValue", " "))
        .addAttribute(new Attribute("long_name", "Coded instrument type"))
        .addAttribute(new Attribute("conventions", "Argo reference table 8"));

    builder.addVariable("DATA_MODE", DataType.CHAR, Arrays.asList(nProfDim))
        .addAttribute(new Attribute("_FillValue", " "))
        .addAttribute(new Attribute("long_name", "Delayed mode or real time data"))
        .addAttribute(new Attribute("conventions", "R : real time; D : delayed mode; A : real time with adjustment"));

    builder.addVariable("JULD", DataType.DOUBLE, Arrays.asList(nProfDim))
        .addAttribute(new Attribute("_FillValue", 999999d))
        .addAttribute(new Attribute("long_name", "Julian day (UTC) of the station relative to REFERENCE_DATE_TIME"))
        .addAttribute(new Attribute("standard_name", "time"))
        .addAttribute(new Attribute("units", "days since 1950-01-01 00:00:00 UTC"))
        .addAttribute(new Attribute("conventions", "Relative julian days with decimal part (as parts of day)"))
        .addAttribute(new Attribute("axis", "T"))
        .addAttribute(new Attribute("resolution", 1.0E-5));

    builder.addVariable("JULD_QC", DataType.CHAR, Arrays.asList(nProfDim))
        .addAttribute(new Attribute("_FillValue", " "))
        .addAttribute(new Attribute("long_name", "Quality on date and time"))
        .addAttribute(new Attribute("conventions", "Argo reference table 2"));

    builder.addVariable("JULD_LOCATION", DataType.DOUBLE, Arrays.asList(nProfDim))
        .addAttribute(new Attribute("resolution", 1.0E-5))
        .addAttribute(new Attribute("_FillValue", 999999d))
        .addAttribute(new Attribute("long_name", "Julian day (UTC) of the location relative to REFERENCE_DATE_TIME"))
        .addAttribute(new Attribute("units", "days since 1950-01-01 00:00:00 UTC"))
        .addAttribute(new Attribute("conventions", "Relative julian days with decimal part (as parts of day)"));

    builder.addVariable("LATITUDE", DataType.DOUBLE, Arrays.asList(nProfDim))
        .addAttribute(new Attribute("_FillValue", 999999d))
        .addAttribute(new Attribute("long_name", "Latitude of the station, best estimate"))
        .addAttribute(new Attribute("standard_name", "latitude"))
        .addAttribute(new Attribute("units", "degree_north"))
        .addAttribute(new Attribute("valid_min", -90d))
        .addAttribute(new Attribute("valid_max", 90d))
        .addAttribute(new Attribute("axis", "Y"));

    builder.addVariable("LONGITUDE", DataType.DOUBLE, Arrays.asList(nProfDim))
        .addAttribute(new Attribute("_FillValue", 999999d))
        .addAttribute(new Attribute("long_name", "Longitude of the station, best estimate"))
        .addAttribute(new Attribute("standard_name", "longitude"))
        .addAttribute(new Attribute("units", "degree_north"))
        .addAttribute(new Attribute("valid_min", -180d))
        .addAttribute(new Attribute("valid_max", 180d))
        .addAttribute(new Attribute("axis", "X"));

    builder.addVariable("POSITION_QC", DataType.CHAR, Arrays.asList(nProfDim))
        .addAttribute(new Attribute("_FillValue", " "))
        .addAttribute(new Attribute("long_name", "Quality on position (latitude and longitude)"))
        .addAttribute(new Attribute("conventions", "Argo reference table 2"));

    builder.addVariable("POSITIONING_SYSTEM", DataType.CHAR, Arrays.asList(nProfDim, string8Dim))
        .addAttribute(new Attribute("_FillValue", " "))
        .addAttribute(new Attribute("long_name", "Positioning system"));

    builder.addVariable("CONFIG_MISSION_NUMBER", DataType.INT, Arrays.asList(nProfDim))
        .addAttribute(new Attribute("_FillValue", 99999))
        .addAttribute(new Attribute("long_name", "Unique number denoting the missions performed by the float"))
        .addAttribute(new Attribute("conventions", "1...N, 1 : first complete mission"));

    builder.addVariable("PARAMETER", DataType.CHAR, Arrays.asList(nProfDim, nCalibDim, nParamDim, string64Dim))
        .addAttribute(new Attribute("_FillValue", " "))
        .addAttribute(new Attribute("long_name", "List of parameters with calibration information"))
        .addAttribute(new Attribute("conventions", "Argo reference table 3"));

    builder.addVariable("SCIENTIFIC_CALIB_EQUATION", DataType.CHAR, Arrays.asList(nProfDim, nCalibDim, nParamDim, string256Dim))
        .addAttribute(new Attribute("_FillValue", " "))
        .addAttribute(new Attribute("long_name", "Calibration equation for this parameter"))
        .addAttribute(new Attribute("conventions", "Argo reference table 3"));

    builder.addVariable("SCIENTIFIC_CALIB_COEFFICIENT", DataType.CHAR, Arrays.asList(nProfDim, nCalibDim, nParamDim, string256Dim))
        .addAttribute(new Attribute("_FillValue", " "))
        .addAttribute(new Attribute("long_name", "Calibration coefficients for this equation"))
        .addAttribute(new Attribute("conventions", "Argo reference table 3"));

    builder.addVariable("SCIENTIFIC_CALIB_COMMENT", DataType.CHAR, Arrays.asList(nProfDim, nCalibDim, nParamDim, string256Dim))
        .addAttribute(new Attribute("_FillValue", " "))
        .addAttribute(new Attribute("long_name", "Comment applying to this parameter calibration"));

    builder.addVariable("SCIENTIFIC_CALIB_DATE", DataType.CHAR, Arrays.asList(nProfDim, nCalibDim, nParamDim, dateTimeDim))
        .addAttribute(new Attribute("_FillValue", " "))
        .addAttribute(new Attribute("long_name", "Date of calibration"))
        .addAttribute(new Attribute("conventions", "YYYYMMDDHHMISS"));

    builder.addVariable("HISTORY_REFERENCE", DataType.CHAR, Arrays.asList(nHistoryDim, nProfDim, string64Dim))
        .addAttribute(new Attribute("_FillValue", " "))
        .addAttribute(new Attribute("long_name", "Reference of database"))
        .addAttribute(new Attribute("conventions", "Institution dependent"));

    builder.addVariable("HISTORY_INSTITUTION", DataType.CHAR, Arrays.asList(nHistoryDim, nProfDim, string4Dim))
        .addAttribute(new Attribute("_FillValue", " "))
        .addAttribute(new Attribute("long_name", "Institution which performed action"))
        .addAttribute(new Attribute("conventions", "Argo reference table 4"));

    builder.addVariable("HISTORY_STEP", DataType.CHAR, Arrays.asList(nHistoryDim, nProfDim, string4Dim))
        .addAttribute(new Attribute("_FillValue", " "))
        .addAttribute(new Attribute("long_name", "Step in data processing"))
        .addAttribute(new Attribute("conventions", "Argo reference table 12"));

    builder.addVariable("HISTORY_SOFTWARE", DataType.CHAR, Arrays.asList(nHistoryDim, nProfDim, string4Dim))
        .addAttribute(new Attribute("_FillValue", " "))
        .addAttribute(new Attribute("long_name", "Name of software which performed action"))
        .addAttribute(new Attribute("conventions", "Institution dependent"));

    builder.addVariable("HISTORY_SOFTWARE_RELEASE", DataType.CHAR, Arrays.asList(nHistoryDim, nProfDim, string4Dim))
        .addAttribute(new Attribute("_FillValue", " "))
        .addAttribute(new Attribute("long_name", "Version/release of software which performed action"))
        .addAttribute(new Attribute("conventions", "Institution dependent"));

    builder.addVariable("HISTORY_ACTION", DataType.CHAR, Arrays.asList(nHistoryDim, nProfDim, string4Dim))
        .addAttribute(new Attribute("_FillValue", " "))
        .addAttribute(new Attribute("long_name", "Action performed on data"))
        .addAttribute(new Attribute("conventions", "Argo reference table 7"));

    builder.addVariable("HISTORY_PARAMETER", DataType.CHAR, Arrays.asList(nHistoryDim, nProfDim, string16Dim))
        .addAttribute(new Attribute("_FillValue", " "))
        .addAttribute(new Attribute("long_name", "Station parameter action is performed on"))
        .addAttribute(new Attribute("conventions", "Argo reference table 3"));

    builder.addVariable("HISTORY_QCTEST", DataType.CHAR, Arrays.asList(nHistoryDim, nProfDim, string16Dim))
        .addAttribute(new Attribute("_FillValue", " "))
        .addAttribute(new Attribute("long_name", "Documentation of tests performed, tests failed (in hex form)"))
        .addAttribute(new Attribute("conventions", "Write tests performed when ACTION=QCP$; tests failed when ACTION=QCF$"));

    builder.addVariable("HISTORY_DATE", DataType.CHAR, Arrays.asList(nHistoryDim, nProfDim, dateTimeDim))
        .addAttribute(new Attribute("_FillValue", " "))
        .addAttribute(new Attribute("long_name", "Date the history record was created"))
        .addAttribute(new Attribute("conventions", "YYYYMMDDHHMISS"));

    builder.addVariable("HISTORY_START_PRES", DataType.FLOAT, Arrays.asList(nHistoryDim, nProfDim))
        .addAttribute(new Attribute("_FillValue", 99999f))
        .addAttribute(new Attribute("long_name", "Start pressure action applied on"))
        .addAttribute(new Attribute("units", "decibar"));

    builder.addVariable("HISTORY_STOP_PRES", DataType.FLOAT, Arrays.asList(nHistoryDim, nProfDim))
        .addAttribute(new Attribute("_FillValue", 99999f))
        .addAttribute(new Attribute("long_name", "Stop pressure action applied on"))
        .addAttribute(new Attribute("units", "decibar"));

    builder.addVariable("HISTORY_PREVIOUS_VALUE", DataType.FLOAT, Arrays.asList(nHistoryDim, nProfDim))
        .addAttribute(new Attribute("_FillValue", 99999f))
        .addAttribute(new Attribute("long_name", "Parameter/Flag previous value before action"))
        .addAttribute(new Attribute("units", "decibar"));

    for (String parameterName : parameterNames) {
      builder.addVariable("PROFILE_" + parameterName + "_QC", DataType.CHAR, Arrays.asList(nProfDim))
          .addAttribute(new Attribute("long_name", "Global quality flag of " + parameterName + " profile"))
          .addAttribute(new Attribute("conventions", "Argo reference table 2a"))
          .addAttribute(new Attribute("_FillValue", " "));

      NetCdfWriteUtils.buildParameterVariable(builder, parameterName, nProfDim, nLevelsDim);

      builder.addVariable(parameterName + "_QC", DataType.CHAR, Arrays.asList(nProfDim, nLevelsDim))
          .addAttribute(new Attribute("long_name", "quality flag"))
          .addAttribute(new Attribute("conventions", "Argo reference table 2"))
          .addAttribute(new Attribute("_FillValue", " "));

      NetCdfWriteUtils.buildParameterVariable(builder, parameterName, "ADJUSTED", nProfDim, nLevelsDim);

      builder.addVariable(parameterName + "_ADJUSTED_QC", DataType.CHAR, Arrays.asList(nProfDim, nLevelsDim))
          .addAttribute(new Attribute("long_name", "quality flag"))
          .addAttribute(new Attribute("conventions", "Argo reference table 2"))
          .addAttribute(new Attribute("_FillValue", " "));

      builder.addVariable(parameterName + "_ADJUSTED_ERROR", DataType.FLOAT, Arrays.asList(nProfDim, nLevelsDim))
          .addAttribute(new Attribute("long_name", "Contains the error on the adjusted values as determined by the delayed mode QC process"))
          .addAttribute(new Attribute("_FillValue", 99999f))
          .addAttribute(new Attribute("units", "decibar"))
          .addAttribute(new Attribute("C_format", "%.3f"))
          .addAttribute(new Attribute("FORTRAN_format", "F.3"))
          .addAttribute(new Attribute("resolution", 0.001f));
    }

  }
}
