% ------------------------------------------------------------------------------
% Generate a synthetic profile from C and B mono-profile files.
%
% SYNTAX :
%  nc_create_synthetic_profile_rt(varargin)
%
% INPUT PARAMETERS :
%   varargin :
%      input parameters:
%         - should be provided as pairs ('param_name','param_value')
%         - 'param_name' value is not case sensitive
%   mandatory parameters:
%      createOnlyMultiProfFlag : should be set to '1' to create multi-profile
%                                file, to '0' otherwise
%      if createOnlyMultiProfFlag is set to '0' the mandatory parameters
%      are:
%         floatCProfFileName  : input c PROF file path name (mandatory if the file exists)
%         floatBProfFileName  : input b PROF file path name (mandatory if the file exists)
%         floatMetaFileName   : input META file path name
%         floatCTrajFileName  : input c TRAJ file path name
%         floatBTrajFileName  : input b TRAJ file path name
%         createMultiProfFlag : should be set to '1' to create multi-profile
%                               file, to '0' otherwise
%         outputDirName       : output directory name
%      if createOnlyMultiProfFlag is set to '1' the mandatory parameters
%      are:
%         floatWmo      : WMO number of concerned float
%         outputDirName : output directory name
%   optional parameters:
%      outputLogDirName     : LOG file directory name
%      outputCsvDirName     : CSV file directory name
%      xmlReportDirName     : XML file directory name
%      xmlReportFileName    : XML file name
%      monoProfRefFileName  : S mono-profile reference file path name
%      multiProfRefFileName : S multi-profile reference file path name
%      tmpDirName           : base name of the temporary directory
%      bgcFloatFlag         : should be set to '1' for a BGC float and '0'
%                             for a 'core' one (default value is '1'). It
%                             prevents generating S-PROF_ERROR and
%                             S-PROF_WARNING messages when no B-PROF file
%                             exists for a cycle.
%      addPresCore          : should be set to '1' if the PRES_CORE parameter
%                             should be added into the S-PROF file, '0' otherwise
%
% OUTPUT PARAMETERS :
%
% EXAMPLES :
%
% SEE ALSO :
% AUTHORS  : Jean-Philippe Rannou (Altran)(jean-philippe.rannou@altran.com)
% ------------------------------------------------------------------------------
% RELEASES :
%   01/11/2018 - RNU - V 0.1: creation
%   03/07/2018 - RNU - V 0.2: update from 20180306 version of the specifications
%   06/15/2018 - RNU - V 1.0: creation of PI and RT tool + generate NetCDF 4 output files
%   07/13/2018 - RNU - V 1.1: the temporary directory could be set by an input parameter
%   08/22/2018 - RNU - V 1.2: manage missing PARAMETER_DATA_MODE when DATA_MODE == 'R'
%   09/25/2018 - RNU - V 1.3: added input parameters 'createOnlyMultiProfFlag' and 'floatWmo'
%   27/11/2018 - RNU - V 1.4: use the provided XML file name to create log file name
%                             add information on concerned float when a mandatory parameter is missing
%   27/11/2018 - RNU - V 1.5: includes version 18.02.2019 for ARGO_simplified_profile
%   04/15/2019 - RNU - V 1.6: correction of previous version
%   07/08/2019 - RNU - V 1.7: for NetCDF-4 files, use 'defVarFill' function
%                             instead of 'putAtt' to define the fill Value of a
%                             variable
%   04/22/2020 - RNU - V 1.8: added a CSV output file that recall the
%                             INFO/WARNING/ERROR messages of the log file
%   07/06/2020 - RNU - V 1.9: includes version 30.06.2020 of ARGO_simplified_profile
%                             this new version generates S-PROF file (possibly
%                             empty) even when 'c' PROF or 'b' PROF file is
%                             missing
%   07/10/2020 - RNU - V 1.10: correction in processing of PROFILE_<PARAM>_QC
%                              (the input Qcs used depend on PARAMATER_DATA_MODE
%                              information)
%   04/27/2021 - RNU - V 1.11: ignore Bounce cycles (from Ice cycles of APEX
%                              APF1 floats) because they have distinct JULD and
%                              LOCATION
%   04/05/2022 - RNU - V 1.12: includes version 01.04.2022 of ARGO_simplified_profile
%   06/10/2022 - RNU - V 1.13: includes version 09.06.2022 of ARGO_simplified_profile
%   08/23/2022 - RNU - V 1.14: CP660_MED and _STD remained in 'b' type in
%                              get_netcdf_param_attributes_3_1, set to 'i'
%   09/13/2022 - RNU - V 1.15: report ERROR message and don't generate S-PROF
%                              file if PRES profiles are not consistent between
%                              core and B files
%   10/13/2023 - RNU - V 1.16: a PRES_CORE parameter can be added to the output
%                              S-PROF files (by using the 'addPresCore' input
%                              parameter).
%   12/04/2023 - RNU - V 1.17: includes version 01.12.2023 of ARGO_simplified_profile
%   01/19/2024 - RNU - V 1.18: includes version 11.01.2024 of ARGO_simplified_profile
% ------------------------------------------------------------------------------
function nc_create_synthetic_profile_rt(varargin)

% generate NetCDF-4 flag for mono-profile file
global g_cocs_netCDF4FlagForMonoProf;
g_cocs_netCDF4FlagForMonoProf = 0;

% generate NetCDF-4 flag for multiple-profiles file
global g_cocs_netCDF4FlagForMultiProf;
g_cocs_netCDF4FlagForMultiProf = 1;

% default directory to store the LOG file
DIR_LOG_FILE = 'C:\Users\jprannou\_RNU\DecArgo_soft\work\log\';

% directory to store the CSV file (should be set to '' if output CSV file is not
% needed)
% DIR_CSV_FILE = ''; % if you don't need output CSV file
DIR_CSV_FILE = 'C:\Users\jprannou\_RNU\DecArgo_soft\work\csv\';

% default directory to store the XML file
DIR_XML_FILE = 'C:\Users\jprannou\_RNU\DecArgo_soft\work\xml\';

% default base name of the temporary directory 
DIR_TMP = 'C:\Users\jprannou\_RNU\DecArgo_soft\work\TMP\';

% merged profile reference file
if (g_cocs_netCDF4FlagForMonoProf)
   MONO_PROF_REF_PROFILE_FILE = 'C:\Users\jprannou\_RNU\DecArgo_soft\soft\util\misc/ArgoSProf_V1.0_netcdf4_classic.nc';
else
   MONO_PROF_REF_PROFILE_FILE = 'C:\Users\jprannou\_RNU\DecArgo_soft\soft\util\misc/ArgoSProf_V1.0_netcdf_classic.nc';
end
if (g_cocs_netCDF4FlagForMultiProf)
   MULTI_PROF_REF_PROFILE_FILE = 'C:\Users\jprannou\_RNU\DecArgo_soft\soft\util\misc/ArgoSProf_V1.0_netcdf4_classic.nc';
else
   MULTI_PROF_REF_PROFILE_FILE = 'C:\Users\jprannou\_RNU\DecArgo_soft\soft\util\misc/ArgoSProf_V1.0_netcdf_classic.nc';
end

% input parameters
global g_cocs_createOnlyMultiProfFlag;
global g_cocs_floatWmo;
global g_cocs_floatCProfFileName;
global g_cocs_floatBProfFileName;
global g_cocs_floatMetaFileName;
global g_cocs_createMultiProfFlag;
global g_cocs_monoProfRefFile;
global g_cocs_multiProfRefFile;
global g_cocs_outputDirName;
global g_cocs_outputLogDirName;
global g_cocs_outputCsvDirName;
global g_cocs_outputXmlReportDirName;
global g_cocs_outputXmlReportFileName;
global g_cocs_tmpDirName;
global g_cocs_bgcFloatFlag;

% DOM node of XML report
global g_cocs_xmlReportDOMNode;

% report information structure
global g_cocs_reportData;
g_cocs_reportData = [];
g_cocs_reportData.inputCProfFile = [];
g_cocs_reportData.inputBProfFile = [];
g_cocs_reportData.inputMetaFile = [];
g_cocs_reportData.outputSMonoProfFile = [];
g_cocs_reportData.outputSMultiProfFile = [];

% program version
global g_cocs_ncCreateSyntheticProfileVersion;
g_cocs_ncCreateSyntheticProfileVersion = '1.18 (version 11.01.2024 for ARGO_simplified_profile)';

% current float and cycle identification
global g_cocs_floatNum;
global g_cocs_cycleNum;
global g_cocs_cycleDir;

% output CSV file Id
global g_cocs_fidCsvFile;
g_cocs_fidCsvFile = -1;
global g_cocs_floatWmoStr;
g_cocs_floatWmoStr = '-';
global g_cocs_cycleNumStr;
g_cocs_cycleNumStr = '-';

% to add PRES_CORE variable in the generated S-PROF file
global g_cocs_addPresCoreFlag;
g_cocs_addPresCoreFlag = 0;

% startTime
ticStartTime = tic;

% store the start time of the run
currentTime = datestr(now, 'yyyymmddTHHMMSSZ');

% set default values
g_cocs_outputLogDirName = DIR_LOG_FILE;
g_cocs_outputCsvDirName = DIR_CSV_FILE;
g_cocs_outputXmlReportDirName = DIR_XML_FILE;
g_cocs_monoProfRefFile = MONO_PROF_REF_PROFILE_FILE;
g_cocs_multiProfRefFile = MULTI_PROF_REF_PROFILE_FILE;
g_cocs_outputXmlReportFileName = ['nc_create_synthetic_profile_rt_' currentTime '.xml'];
tmpXmlReportFileName = g_cocs_outputXmlReportFileName;
g_cocs_tmpDirName = DIR_TMP;

% default values initialization
init_default_values;

% measurement codes initialization
init_measurement_codes;

logFileName = [];
status = 'nok';
try
      
   % init the XML report
   init_xml_report(currentTime);
   
   % get input parameters
   [inputError, logLines] = parse_input_param(varargin);

   % log file creation
   if (~strcmp(g_cocs_outputXmlReportFileName, tmpXmlReportFileName))
      % the XML report file name has been set through an input parameter
      if (~isempty(g_cocs_outputLogDirName))
         logFileName = [g_cocs_outputLogDirName '/' regexprep(g_cocs_outputXmlReportFileName, '.xml', '.log')];
      else
         logFileName = [tempdir '/' regexprep(g_cocs_outputXmlReportFileName, '.xml', '.log')];
      end
   else
      if (~isempty(g_cocs_outputLogDirName))
         logFileName = [g_cocs_outputLogDirName '/nc_create_synthetic_profile_rt_' currentTime '.log'];
      else
         logFileName = [tempdir '/nc_create_synthetic_profile_rt_' currentTime '.log'];
      end
   end
   
   diary(logFileName);
   
   if (~isempty(logLines))
      fprintf('%s', logLines{:});
   end
   
   if (~inputError)
      
      g_cocs_reportData.inputCProfFile = g_cocs_floatCProfFileName;
      g_cocs_reportData.inputBProfFile = g_cocs_floatBProfFileName;
      g_cocs_reportData.inputMetaFile = g_cocs_floatMetaFileName;
      
      % set float, cycle an direction
      if (g_cocs_createOnlyMultiProfFlag == '0')
         if (~isempty(g_cocs_floatCProfFileName))
            [~, cProfFileName, ~] = fileparts(g_cocs_floatCProfFileName);
            idF = strfind(cProfFileName, '_');
            g_cocs_floatNum = str2double(cProfFileName(2:idF-1));
            g_cocs_floatWmoStr = cProfFileName(2:idF-1);
            if (cProfFileName(end) == 'D')
               g_cocs_cycleDir = 'D';
               cProfFileName(end) = [];
            else
               g_cocs_cycleDir = '';
            end
            g_cocs_cycleNum = str2double(cProfFileName(idF+1:end));
            g_cocs_cycleNumStr = cProfFileName(idF+1:end);
         else
            [~, bProfFileName, ~] = fileparts(g_cocs_floatBProfFileName);
            idF = strfind(bProfFileName, '_');
            g_cocs_floatNum = str2double(bProfFileName(3:idF-1));
            g_cocs_floatWmoStr = bProfFileName(3:idF-1);
            if (bProfFileName(end) == 'D')
               g_cocs_cycleDir = 'D';
               bProfFileName(end) = [];
            else
               g_cocs_cycleDir = '';
            end
            g_cocs_cycleNum = str2double(bProfFileName(idF+1:end)); 
            g_cocs_cycleNumStr = bProfFileName(idF+1:end);
         end
      else
         g_cocs_floatNum = str2double(g_cocs_floatWmo);
         g_cocs_floatWmoStr = g_cocs_floatWmo;
      end
      
      % output CSV file name
      [~, logFile, ~] = fileparts(logFileName);
      csvFileName = [g_cocs_outputCsvDirName '/' logFile '.csv'];

      % create CSV file
      g_cocs_fidCsvFile = fopen(csvFileName, 'wt');
      if (g_cocs_fidCsvFile == -1)
         fprintf('ERROR: Unable to create output CSV file: %s\n', csvFileName);
         return
      end
      
      % put header
      %       header = 'dac, type, float code, cycle number, message, file';
      %       fprintf(g_cocs_fidCsvFile, '%s\n', header);

      % generate S-PROF file
      nc_create_synthetic_profile_(...
         str2num(g_cocs_createOnlyMultiProfFlag), ...
         g_cocs_floatCProfFileName, ...
         g_cocs_floatBProfFileName, ...
         g_cocs_floatMetaFileName, ...
         str2num(g_cocs_createMultiProfFlag), ...
         g_cocs_outputDirName, ...
         g_cocs_monoProfRefFile, g_cocs_multiProfRefFile, ...
         g_cocs_tmpDirName, ...
         str2num(g_cocs_bgcFloatFlag));

   end
   
   fclose(g_cocs_fidCsvFile);
   
   diary off;
   
   % finalize XML report
   [status] = finalize_xml_report(ticStartTime, logFileName, []);
   
catch
   
   if (g_cocs_fidCsvFile ~= -1)
      fclose(g_cocs_fidCsvFile);
   end
   
   diary off;
   
   % finalize XML report
   [status] = finalize_xml_report(ticStartTime, logFileName, lasterror);
   
end

% create the XML report path file name
if (~isempty(g_cocs_outputXmlReportDirName))
   xmlFileName = [g_cocs_outputXmlReportDirName '/' g_cocs_outputXmlReportFileName];
else
   xmlFileName = [tempdir '/' g_cocs_outputXmlReportFileName];
end

% save the XML report
xmlwrite(xmlFileName, g_cocs_xmlReportDOMNode);
% if (strcmp(status, 'nok') == 1)
%    edit(xmlFileName);
% end

return

% ------------------------------------------------------------------------------
% Initialize XML report.
%
% SYNTAX :
%  init_xml_report(a_time)
%
% INPUT PARAMETERS :
%   a_time : start date of the run ('yyyymmddTHHMMSS' format)
%
% OUTPUT PARAMETERS :
%
% EXAMPLES :
%
% SEE ALSO :
% AUTHORS  : Jean-Philippe Rannou (Altran)(jean-philippe.rannou@altran.com)
% ------------------------------------------------------------------------------
% RELEASES :
%   06/15/2018 - RNU - creation
% ------------------------------------------------------------------------------
function init_xml_report(a_time)

% DOM node of XML report
global g_cocs_xmlReportDOMNode;


% initialize XML report
docNode = com.mathworks.xml.XMLUtils.createDocument('coriolis_function_report');
docRootNode = docNode.getDocumentElement;

newChild = docNode.createElement('function');
newChild.appendChild(docNode.createTextNode('co041404'));
docRootNode.appendChild(newChild);

newChild = docNode.createElement('comment');
newChild.appendChild(docNode.createTextNode('Argo Coriolis synthetic profiles generating tool (nc_create_synthetic_profile_rt)'));
docRootNode.appendChild(newChild);

newChild = docNode.createElement('date');
newChild.appendChild(docNode.createTextNode(datestr(datenum(a_time, 'yyyymmddTHHMMSSZ'), 'dd/mm/yyyy HH:MM:SS')));
docRootNode.appendChild(newChild);

g_cocs_xmlReportDOMNode = docNode;

return

% ------------------------------------------------------------------------------
% Parse input parameters.
%
% SYNTAX :
%  [o_inputError, o_logLines] = parse_input_param(a_varargin)
%
% INPUT PARAMETERS :
%   a_varargin : input parameters
%
% OUTPUT PARAMETERS :
%   o_inputError : input error flag
%   o_logLines   : lines to write in log file
%
% EXAMPLES :
%
% SEE ALSO :
% AUTHORS  : Jean-Philippe Rannou (Altran)(jean-philippe.rannou@altran.com)
% ------------------------------------------------------------------------------
% RELEASES :
%   06/15/2018 - RNU - creation
% ------------------------------------------------------------------------------
function [o_inputError, o_logLines] = parse_input_param(a_varargin)

% output parameters initialization
o_inputError = 0;
o_logLines = [];

% input parameters
global g_cocs_createOnlyMultiProfFlag;
global g_cocs_floatWmo;
global g_cocs_floatCProfFileName;
global g_cocs_floatBProfFileName;
global g_cocs_floatMetaFileName;
global g_cocs_createMultiProfFlag;
global g_cocs_monoProfRefFile;
global g_cocs_multiProfRefFile;
global g_cocs_outputDirName;
global g_cocs_outputLogDirName;
global g_cocs_outputCsvDirName;
global g_cocs_outputXmlReportDirName;
global g_cocs_outputXmlReportFileName;
global g_cocs_tmpDirName;
global g_cocs_bgcFloatFlag;
global g_cocs_addPresCoreFlag;

g_cocs_floatWmo = '';
g_cocs_floatCProfFileName = '';
g_cocs_floatBProfFileName = '';
g_cocs_floatMetaFileName = '';
g_cocs_createMultiProfFlag = '';
g_cocs_outputDirName = '';
g_cocs_bgcFloatFlag = '';
addPresCore = '';

% ignore empty input parameters
idDel = [];
for id = 1:length(a_varargin)
   if (isempty(a_varargin{id}))
      idDel = [idDel id];
   end
end
a_varargin(idDel) = [];

% check input parameters
if (~isempty(a_varargin))
   if (rem(length(a_varargin), 2) ~= 0)
      o_logLines{end+1} = sprintf('ERROR: expecting an even number of input arguments (e.g. (''argument_name'', ''argument_value'') - exit\n');
      o_inputError = 1;
      return
   else
      for id = 1:2:length(a_varargin)
         if (strcmpi(a_varargin{id}, 'floatCProfFileName'))
            g_cocs_floatCProfFileName = a_varargin{id+1};
         elseif (strcmpi(a_varargin{id}, 'floatBProfFileName'))
            g_cocs_floatBProfFileName = a_varargin{id+1};
         elseif (strcmpi(a_varargin{id}, 'floatMetaFileName'))
            g_cocs_floatMetaFileName = a_varargin{id+1};
         elseif (strcmpi(a_varargin{id}, 'createMultiProfFlag'))
            g_cocs_createMultiProfFlag = a_varargin{id+1};
         elseif (strcmpi(a_varargin{id}, 'outputDirName'))
            g_cocs_outputDirName = a_varargin{id+1};
         elseif (strcmpi(a_varargin{id}, 'outputLogDirName'))
            g_cocs_outputLogDirName = a_varargin{id+1};
         elseif (strcmpi(a_varargin{id}, 'outputCsvDirName'))
            g_cocs_outputCsvDirName = a_varargin{id+1};
         elseif (strcmpi(a_varargin{id}, 'xmlReportDirName'))
            g_cocs_outputXmlReportDirName = a_varargin{id+1};
         elseif (strcmpi(a_varargin{id}, 'xmlReportFileName'))
            g_cocs_outputXmlReportFileName = a_varargin{id+1};
         elseif (strcmpi(a_varargin{id}, 'monoProfRefFileName'))
            g_cocs_monoProfRefFile = a_varargin{id+1};
         elseif (strcmpi(a_varargin{id}, 'multiProfRefFileName'))
            g_cocs_multiProfRefFile = a_varargin{id+1};
         elseif (strcmpi(a_varargin{id}, 'tmpDirName'))
            g_cocs_tmpDirName = a_varargin{id+1};
         elseif (strcmpi(a_varargin{id}, 'bgcFloatFlag'))
            g_cocs_bgcFloatFlag = a_varargin{id+1};
         elseif (strcmpi(a_varargin{id}, 'createOnlyMultiProfFlag'))
            g_cocs_createOnlyMultiProfFlag = a_varargin{id+1};
         elseif (strcmpi(a_varargin{id}, 'floatWmo'))
            g_cocs_floatWmo = a_varargin{id+1};
         elseif (strcmpi(a_varargin{id}, 'addPresCore'))
            addPresCore = a_varargin{id+1};
         else
            o_logLines{end+1} = sprintf('WARNING: unexpected input argument (''%s'') - ignored\n', a_varargin{id});
         end
      end
   end
end

% check that mandatory parameters are provided
if (isempty(g_cocs_createOnlyMultiProfFlag))
   o_logLines{end+1} = sprintf('ERROR: ''createOnlyMultiProfFlag'' input parameter is mandatory\n');
   o_inputError = 1;
   return
end
if ((length(g_cocs_createOnlyMultiProfFlag) ~= 1) || ...
      ((g_cocs_createOnlyMultiProfFlag ~= '0') && (g_cocs_createOnlyMultiProfFlag ~= '1')))
   o_logLines{end+1} = sprintf('ERROR: Inconsistent ''createOnlyMultiProfFlag'' value (%s) (expected ''0'' or ''1'')\n', g_cocs_createOnlyMultiProfFlag);
   o_inputError = 1;
   return
end

if (g_cocs_createOnlyMultiProfFlag == '0')
   
   if (isempty(g_cocs_floatCProfFileName) && isempty(g_cocs_floatBProfFileName))
      o_logLines{end+1} = sprintf('ERROR: ''floatCProfFileName'' and ''floatBProfFileName'' cannot be simultaneously empty\n');
      o_inputError = 1;
      return
   end
   %    if (isempty(g_cocs_floatCProfFileName))
   %       o_logLines{end+1} = sprintf('ERROR: ''floatCProfFileName'' input parameter is mandatory\n');
   %       o_inputError = 1;
   %       return
   %    end
   %    if (isempty(g_cocs_floatBProfFileName))
   %       o_logLines{end+1} = sprintf('ERROR: ''floatBProfFileName'' input parameter is mandatory (associated ''floatCProfFileName'': %s)\n', ...
   %          g_cocs_floatCProfFileName);
   %       o_inputError = 1;
   %       return
   %    end
   if (isempty(g_cocs_floatMetaFileName))
      o_logLines{end+1} = sprintf('ERROR: ''floatMetaFileName'' input parameter is mandatory (associated ''floatCProfFileName'': %s and ''floatBProfFileName'': %s)\n', ...
         g_cocs_floatCProfFileName, g_cocs_floatBProfFileName);
      o_inputError = 1;
      return
   end
   if (isempty(g_cocs_createMultiProfFlag))
      o_logLines{end+1} = sprintf('ERROR: ''createMultiProfFlag'' input parameter is mandatory (associated ''floatCProfFileName'': %s and ''floatBProfFileName'': %s)\n', ...
         g_cocs_floatCProfFileName, g_cocs_floatBProfFileName);
      o_inputError = 1;
      return
   end
   if (isempty(g_cocs_outputDirName))
      o_logLines{end+1} = sprintf('ERROR: ''outputDirName'' input parameter is mandatory (associated ''floatCProfFileName'': %s and ''floatBProfFileName'': %s)\n', ...
         g_cocs_floatCProfFileName, g_cocs_floatBProfFileName);
      o_inputError = 1;
      return
   end

   % check input parameters
   if (~isempty(g_cocs_floatCProfFileName) && ~(exist(g_cocs_floatCProfFileName, 'file') == 2))
      o_logLines{end+1} = sprintf('ERROR: Input file not found: %s\n', g_cocs_floatCProfFileName);
      o_inputError = 1;
      return
   end
   if (~isempty(g_cocs_floatBProfFileName) && ~(exist(g_cocs_floatBProfFileName, 'file') == 2))
      o_logLines{end+1} = sprintf('ERROR: Input file not found: %s\n', g_cocs_floatBProfFileName);
      o_inputError = 1;
      return
   end
   if ~(exist(g_cocs_floatMetaFileName, 'file') == 2)
      o_logLines{end+1} = sprintf('ERROR: Input file not found: %s\n', g_cocs_floatMetaFileName);
      o_inputError = 1;
      return
   end
   if ((length(g_cocs_createMultiProfFlag) ~= 1) || ...
         ((g_cocs_createMultiProfFlag ~= '0') && (g_cocs_createMultiProfFlag ~= '1')))
      o_logLines{end+1} = sprintf('ERROR: Inconsistent ''createMultiProfFlag'' value (%s) (expected ''0'' or ''1'')\n', g_cocs_createMultiProfFlag);
      o_inputError = 1;
      return
   end
   if ~(exist(g_cocs_outputDirName, 'dir') == 7)
      o_logLines{end+1} = sprintf('ERROR: Output directory not found: %s\n', g_cocs_outputDirName);
      o_inputError = 1;
      return
   end
   
   if (~isempty(g_cocs_floatWmo))
      o_logLines{end+1} = sprintf('WARNING: unexpected input argument (''floatWmo'') - ignored\n');
   end
   
else
   
   % check that mandatory parameters are provided
   if (isempty(g_cocs_floatWmo))
      o_logLines{end+1} = sprintf('ERROR: ''floatWmo'' input parameter is mandatory\n');
      o_inputError = 1;
      return
   end
   if (isempty(g_cocs_outputDirName))
      o_logLines{end+1} = sprintf('ERROR: ''outputDirName'' input parameter is mandatory (for ''floatWmo'': %s)\n', g_cocs_floatWmo);
      o_inputError = 1;
      return
   end
   
   % check input parameters
   if ~(exist(g_cocs_outputDirName, 'dir') == 7)
      o_logLines{end+1} = sprintf('ERROR: Output directory not found: %s\n', g_cocs_outputDirName);
      o_inputError = 1;
      return
   end
   if ~(exist([g_cocs_outputDirName '/' g_cocs_floatWmo], 'dir') == 7)
      o_logLines{end+1} = sprintf('ERROR: Float output directory not found: %s\n', [g_cocs_outputDirName '/' g_cocs_floatWmo]);
      o_inputError = 1;
      return
   end
   
   if (~isempty(g_cocs_floatCProfFileName))
      o_logLines{end+1} = sprintf('WARNING: unexpected input argument (''floatCProfFileName'') - ignored\n');
   end
   if (~isempty(g_cocs_floatBProfFileName))
      o_logLines{end+1} = sprintf('WARNING: unexpected input argument (''floatBProfFileName'') - ignored\n');
   end
   if (~isempty(g_cocs_floatMetaFileName))
      o_logLines{end+1} = sprintf('WARNING: unexpected input argument (''floatMetaFileName'') - ignored\n');
   end
   if (~isempty(g_cocs_createMultiProfFlag))
      o_logLines{end+1} = sprintf('WARNING: unexpected input argument (''createMultiProfFlag'') - ignored\n');
   end
   
end

% check input not-mandatory parameters
if ~(exist(g_cocs_monoProfRefFile, 'file') == 2)
   o_logLines{end+1} = sprintf('ERROR: Input mono-profile reference file not found: %s\n', g_cocs_monoProfRefFile);
   o_inputError = 1;
   return
end
if ~(exist(g_cocs_multiProfRefFile, 'file') == 2)
   o_logLines{end+1} = sprintf('ERROR: Input multi-profile reference file not found: %s\n', g_cocs_multiProfRefFile);
   o_inputError = 1;
   return
end
if ~(exist(g_cocs_outputLogDirName, 'dir') == 7)
   o_logLines{end+1} = sprintf('ERROR: Output LOG directory not found: %s\n', g_cocs_outputLogDirName);
   o_inputError = 1;
   return
end
if (~isempty(g_cocs_outputCsvDirName))
   if ~(exist(g_cocs_outputCsvDirName, 'dir') == 7)
      o_logLines{end+1} = sprintf('ERROR: Output CSV directory not found: %s\n', g_cocs_outputCsvDirName);
      o_inputError = 1;
      return
   end
end
if ~(exist(g_cocs_outputXmlReportDirName, 'dir') == 7)
   o_logLines{end+1} = sprintf('ERROR: Output XML directory not found: %s\n', g_cocs_outputXmlReportDirName);
   o_inputError = 1;
   return
end
if ~(exist(g_cocs_tmpDirName, 'dir') == 7)
   o_logLines{end+1} = sprintf('ERROR: Temporary directory not found: %s\n', g_cocs_tmpDirName);
   o_inputError = 1;
   return
end

if (isempty(g_cocs_bgcFloatFlag))
   g_cocs_bgcFloatFlag = '1';
else
   if ((length(g_cocs_bgcFloatFlag) ~= 1) || ...
         ((g_cocs_bgcFloatFlag ~= '0') && (g_cocs_bgcFloatFlag ~= '1')))
      o_logLines{end+1} = sprintf('ERROR: Inconsistent ''bgcFloatFlag'' value (%s) (expected ''0'' or ''1'')\n', g_cocs_bgcFloatFlag);
      o_inputError = 1;
      return
   end
end

if (~isempty(addPresCore))
   if ((addPresCore ~= '0') && (addPresCore ~= '1'))
      o_logLines{end+1} = sprintf('ERROR: Inconsistent ''addPresCore'' value (%s) (expected ''0'' or ''1'')\n', addPresCore);
      o_inputError = 1;
      return
   else
      g_cocs_addPresCoreFlag = str2double(addPresCore);
   end
end

o_logLines{end+1} = sprintf('INPUT PARAMETERS\n');
o_logLines{end+1} = sprintf('createOnlyMultiProfFlag : %s\n', g_cocs_createOnlyMultiProfFlag);
if (g_cocs_createOnlyMultiProfFlag == '0')
   o_logLines{end+1} = sprintf('floatCProfFileName      : %s\n', g_cocs_floatCProfFileName);
   o_logLines{end+1} = sprintf('floatBProfFileName      : %s\n', g_cocs_floatBProfFileName);
   o_logLines{end+1} = sprintf('floatMetaFileName       : %s\n', g_cocs_floatMetaFileName);
   o_logLines{end+1} = sprintf('createMultiProfFlag     : %s\n', g_cocs_createMultiProfFlag);
else
   o_logLines{end+1} = sprintf('floatWmo                : %s\n', g_cocs_floatWmo);
end
o_logLines{end+1} = sprintf('outputDirName           : %s\n', g_cocs_outputDirName);
o_logLines{end+1} = sprintf('outputLogDirName        : %s\n', g_cocs_outputLogDirName);
o_logLines{end+1} = sprintf('outputCsvDirName        : %s\n', g_cocs_outputCsvDirName);
o_logLines{end+1} = sprintf('xmlReportDirName        : %s\n', g_cocs_outputXmlReportDirName);
o_logLines{end+1} = sprintf('xmlReportFileName       : %s\n', g_cocs_outputXmlReportFileName);
o_logLines{end+1} = sprintf('monoProfRefFileName     : %s\n', g_cocs_monoProfRefFile);
o_logLines{end+1} = sprintf('multiProfRefFileName    : %s\n', g_cocs_multiProfRefFile);
o_logLines{end+1} = sprintf('tmpDirName              : %s\n', g_cocs_tmpDirName);
o_logLines{end+1} = sprintf('bgcFloatFlag            : %s\n', g_cocs_bgcFloatFlag);
o_logLines{end+1} = sprintf('addPresCore             : %d\n', g_cocs_addPresCoreFlag);
o_logLines{end+1} = sprintf('\n');

return

% ------------------------------------------------------------------------------
% Finalize the XML report.
%
% SYNTAX :
%  [o_status] = finalize_xml_report(a_ticStartTime, a_logFileName, a_error)
%
% INPUT PARAMETERS :
%   a_ticStartTime : identifier for the "tic" command
%   a_logFileName  : log file path name of the run
%   a_error        : Matlab error
%
% OUTPUT PARAMETERS :
%   o_status : final status of the run
%
% EXAMPLES :
%
% SEE ALSO :
% AUTHORS  : Jean-Philippe Rannou (Altran)(jean-philippe.rannou@altran.com)
% ------------------------------------------------------------------------------
% RELEASES :
%   06/15/2018 - RNU - creation
% ------------------------------------------------------------------------------
function [o_status] = finalize_xml_report(a_ticStartTime, a_logFileName, a_error)

% DOM node of XML report
global g_cocs_xmlReportDOMNode;

% report information structure
global g_cocs_reportData;


% initalize final status
o_status = 'ok';

% finalize the report
docNode = g_cocs_xmlReportDOMNode;
docRootNode = docNode.getDocumentElement;


% list of input files
newChild = docNode.createElement('input_files');

if (~isempty(g_cocs_reportData.inputCProfFile))
   newChildBis = docNode.createElement('input_c_prof_file');
   newChildBis.appendChild(docNode.createTextNode(g_cocs_reportData.inputCProfFile));
   newChild.appendChild(newChildBis);
end

if (~isempty(g_cocs_reportData.inputBProfFile))
   newChildBis = docNode.createElement('input_b_prof_file');
   newChildBis.appendChild(docNode.createTextNode(g_cocs_reportData.inputBProfFile));
   newChild.appendChild(newChildBis);
end

if (~isempty(g_cocs_reportData.inputMetaFile))
   newChildBis = docNode.createElement('input_meta_file');
   newChildBis.appendChild(docNode.createTextNode(g_cocs_reportData.inputMetaFile));
   newChild.appendChild(newChildBis);
end

docRootNode.appendChild(newChild);

% list of output files
newChild = docNode.createElement('output_files');

if (~isempty(g_cocs_reportData.outputSMonoProfFile))
   newChildBis = docNode.createElement('output_s_mono_prof_file');
   newChildBis.appendChild(docNode.createTextNode(g_cocs_reportData.outputSMonoProfFile));
   newChild.appendChild(newChildBis);
end

if (~isempty(g_cocs_reportData.outputSMultiProfFile))
   newChildBis = docNode.createElement('output_s_multi_prof_file');
   newChildBis.appendChild(docNode.createTextNode(g_cocs_reportData.outputSMultiProfFile));
   newChild.appendChild(newChildBis);
end

docRootNode.appendChild(newChild);

% retrieve information from the log file
[infoMsg, warningMsg, errorMsg] = parse_log_file(a_logFileName);

if (~isempty(infoMsg))
   for idMsg = 1:length(infoMsg)
      newChild = docNode.createElement('info');
      textNode = infoMsg{idMsg};
      newChild.appendChild(docNode.createTextNode(textNode));
      docRootNode.appendChild(newChild);
   end
end

if (~isempty(warningMsg))
   for idMsg = 1:length(warningMsg)
      newChild = docNode.createElement('warning');
      textNode = warningMsg{idMsg};
      newChild.appendChild(docNode.createTextNode(textNode));
      docRootNode.appendChild(newChild);
   end
end

if (~isempty(errorMsg))
   for idMsg = 1:length(errorMsg)
      newChild = docNode.createElement('error');
      textNode = errorMsg{idMsg};
      newChild.appendChild(docNode.createTextNode(textNode));
      docRootNode.appendChild(newChild);
   end
   o_status = 'nok';
end

% add matlab error
if (~isempty(a_error))
   o_status = 'nok';
   
   newChild = docNode.createElement('matlab_error');
   
   newChildBis = docNode.createElement('error_message');
   textNode = regexprep(a_error.message, char(10), ': ');
   newChildBis.appendChild(docNode.createTextNode(textNode));
   newChild.appendChild(newChildBis);
   
   for idS = 1:size(a_error.stack, 1)
      newChildBis = docNode.createElement('stack_line');
      textNode = sprintf('Line: %3d File: %s (func: %s)', ...
         a_error.stack(idS). line, ...
         a_error.stack(idS). file, ...
         a_error.stack(idS). name);
      newChildBis.appendChild(docNode.createTextNode(textNode));
      newChild.appendChild(newChildBis);
   end
   
   docRootNode.appendChild(newChild);
end

newChild = docNode.createElement('duration');
newChild.appendChild(docNode.createTextNode(format_time(toc(a_ticStartTime)/3600)));
docRootNode.appendChild(newChild);

newChild = docNode.createElement('status');
newChild.appendChild(docNode.createTextNode(o_status));
docRootNode.appendChild(newChild);

return

% ------------------------------------------------------------------------------
% Retrieve INFO, WARNING and ERROR messages from the log file.
%
% SYNTAX :
%  [o_infoMsg, o_warningMsg, o_errorMsg] = parse_log_file(a_logFileName)
%
% INPUT PARAMETERS :
%   a_logFileName  : log file path name of the run
%
% OUTPUT PARAMETERS :
%   o_infoMsg     : INFO messages
%   o_warningMsg  : WARNING messages
%   o_errorMsg    : ERROR messages
%
% EXAMPLES :
%
% SEE ALSO :
% AUTHORS  : Jean-Philippe Rannou (Altran)(jean-philippe.rannou@altran.com)
% ------------------------------------------------------------------------------
% RELEASES :
%   06/15/2018 - RNU - creation
% ------------------------------------------------------------------------------
function [o_infoMsg, o_warningMsg, o_errorMsg] = parse_log_file(a_logFileName)

% output parameters initialization
o_infoMsg = [];
o_warningMsg = [];
o_errorMsg = [];
o_infoMsg2 = [];
o_warningMsg2 = [];
o_errorMsg2 = [];

if (~isempty(a_logFileName))
   % read log file
   fId = fopen(a_logFileName, 'r');
   if (fId == -1)
      errorLine = sprintf('ERROR: Unable to open file: %s\n', a_logFileName);
      o_errorMsg = [o_errorMsg {errorLine}];
      return
   end
   fileContents = textscan(fId, '%s', 'delimiter', '\n');
   fclose(fId);
   
   if (~isempty(fileContents) && ~isempty(fileContents{:}))
      % retrieve wanted messages
      fileContents = fileContents{:};
      idLine = 1;
      while (1)
         line = fileContents{idLine};
         if (strncmp(line, 'INFO: ', length('INFO: ')))
            o_infoMsg = [o_infoMsg {line(length('INFO: ')+1:end)}];
         elseif (strncmp(line, 'WARNING: ', length('WARNING: ')))
            o_warningMsg = [o_warningMsg {line(length('WARNING: ')+1:end)}];
         elseif (strncmp(line, 'ERROR: ', length('ERROR: ')))
            o_errorMsg = [o_errorMsg {line(length('ERROR: ')+1:end)}];
         elseif (strncmp(line, 'S-PROF_INFO: ', length('S-PROF_INFO: ')))
            o_infoMsg = [o_infoMsg {['S-PROF: ' line(length('S-PROF_INFO: ')+1:end)]}];
         elseif (strncmp(line, 'S-PROF_WARNING: ', length('S-PROF_WARNING: ')))
            o_warningMsg = [o_warningMsg {['S-PROF: ' line(length('S-PROF_WARNING: ')+1:end)]}];
         elseif (strncmp(line, 'S-PROF_ERROR: ', length('S-PROF_ERROR: ')))
            o_errorMsg = [o_errorMsg {['S-PROF: ' line(length('S-PROF_ERROR: ')+1:end)]}];
         end
         idLine = idLine + 1;
         if (idLine > length(fileContents))
            break
         end
      end
   end
end

return

% ------------------------------------------------------------------------------
% Duration format.
%
% SYNTAX :
%   [o_time] = format_time(a_time)
%
% INPUT PARAMETERS :
%   a_time : hour (in float)
%
% OUTPUT PARAMETERS :
%   o_time : formated duration
%
% EXAMPLES :
%
% SEE ALSO :
% AUTHORS  : Jean-Philippe Rannou (Altran)(jean-philippe.rannou@altran.com)
% ------------------------------------------------------------------------------
% RELEASES :
%   06/15/2018 - RNU - creation
% ------------------------------------------------------------------------------
function [o_time] = format_time(a_time)

% output parameters initialization
o_time = [];

if (a_time >= 0)
   sign = '';
else
   sign = '-';
end
a_time = abs(a_time);
h = fix(a_time);
m = fix((a_time-h)*60);
s = round(((a_time-h)*60-m)*60);
if (s == 60)
   s = 0;
   m = m + 1;
   if (m == 60)
      m = 0;
      h = h + 1;
   end
end
if (isempty(sign))
   o_time = sprintf('%02d:%02d:%02d', h, m, s);
else
   o_time = sprintf('%c %02d:%02d:%02d', sign, h, m, s);
end

return
