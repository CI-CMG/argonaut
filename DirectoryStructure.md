# Submission Directory Structure

submission
 - dac
 - - <dac_name>
 - - - submit
 - - - - xxx.tar.gz
 - - - - <dac_name>_greylist.csv
 - - - - <dac_name>_removal.txt
 - - - processing
 - - - - <submission_timestamp>
 - - - - - xxx.tar.gz
 - - - - - <dac_name>_greylist.csv
 - - - - - <dac_name>_removal.txt
 - - - processed
 - - - - <submission_timestamp>
 - - - - - xxx.tar.gz
 - - - - - <dac_name>_greylist.csv
 - - - - - <dac_name>_removal.txt
 - - - - - submission_report.csv
 - - - - - reject
 - - - - - - \<float\>
 - - - - - - - \<float\>_meta.nc
 - - - - - - - \<float\>_Rtraj.nc
 - - - - - - - \<float\>_tech.nc
 - - - - - - - profiles
 - - - - - - - - R\<float\>_xxx.nc
 - - - - - - - - D\<float\>_xxx.nc

# Internal Processing Structure

processing
 - dac
 - - <dac_name>
 - - - \<float\>
 - - - - \<float>_meta.nc
 - - - - \<float>_Rtraj.nc
 - - - - \<float>_Rtraj.nc.filecheck
 - - - - \<float>_tech.nc
 - - - - \<float>_tech.nc.filecheck
 - - - - profiles
 - - - - - R\<float>_xxx.nc
 - - - - - R\<float>_xxx.nc.filecheck
 - - - - - D\<float>_xxx.nc
 - - - - - D\<float>_xxx.nc.filecheck

# Final Directory Structure

output
  - etc
  - - removed
  - - - <dac_name>
  - - - - <submission_timestamp>
  - - - - - <dac_name>_removal.txt
  - - - - - \<float\>
  - - - - - - \<float>_meta.nc
  - - - - - - \<float>_meta.nc.md5
  - - - - - - \<float>_Rtraj.nc
  - - - - - - \<float>_Rtraj.nc.md5
  - - - - - - \<float>_tech.nc
  - - - - - - \<float>_tech.nc.md5
  - - - - - - profiles
  - - - - - - - R\<float>_xxx.nc
  - - - - - - - R\<float>_xxx.nc.md5
  - - - - - - - D\<float>_xxx.nc
  - - - - - - - D\<float>_xxx.nc.md5
  - dac
  - - <dac_name>
  - - - \<float\>
  - - - - \<float>_meta.nc
  - - - - \<float>_meta.nc.md5
  - - - - \<float>_Rtraj.nc
  - - - - \<float>_Rtraj.nc.md5
  - - - - \<float>_tech.nc
  - - - - \<float>_tech.nc.md5
  - - - - \<float>_prof.nc
  - - - - \<float>_prof.nc.md5
  - - - - profiles
  - - - - - R\<float>_xxx.nc
  - - - - - R\<float>_xxx.nc.md5
  - - - - - D\<float>_xxx.nc
  - - - - - D\<float>_xxx.nc.md5
  - geo
  - - atlantic_ocean
  - - - \<year>
  - - - - \<MM>
  - - - - \<year>\<MM>\<dd>\_prof.nc
  - - - - \<year>\<MM>\<dd>\_prof.nc.md5
  - - indian_ocean
  - - - \<year>
  - - - - \<MM>
  - - - - \<year>\<MM>\<dd>\_prof.nc
  - - - - \<year>\<MM>\<dd>\_prof.nc.md5
  - - pacific_ocean
  - - - \<year>
  - - - - \<MM>
  - - - - \<year>\<MM>\<dd>\_prof.nc
  - - - - \<year>\<MM>\<dd>\_prof.nc.md5
  - latest_data
  - - R\<year>\<MM>\<dd>\_prof\_\<num>.nc
  - - R\<year>\<MM>\<dd>\_prof\_\<num>.nc.md5

# Differences from French GDAC
1. Removal structure. French GDAC does not have a timestamp directory
2. Likely difference in reject structure
3. MD5 files are located in the same directory as the file itself, not etc/md5