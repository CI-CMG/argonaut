# Argonaut

TODO update documentation

French GDAC:  https://argo-gdac-sandbox.s3.eu-west-3.amazonaws.com/pub/index.html#pub/
AOML DAC: https://www.aoml.noaa.gov/ftp/pub/phod/ARGO_FTP/argo/nc/
Argo GADC cookbook: https://archimer.ifremer.fr/doc/00351/46202/

Goals
```
Basically what I would need is the set up of a prototype Argo Global Data Assembly center.  
What that means is monitoring a system directory for each of the 10 data assembly centers 
(the U. S. center is at NOAA Atlantic Ocean and Met Lab in MIami) running each submission through 
a file checker and then either processing into the GDAC collection or pushing back to the DAC 
specific system for inspection and reprocessing by the DAC.  There is also a mirror with the 
French GDAC to ensure both have the exact same data.  Finally there is public dissemination. 
```

Here are GDAC merged files :
latest_data files: https://data-argo.ifremer.fr/latest_data
geo files: https://data-argo.ifremer.fr/geo/atlantic_ocean/2025/04/
float file https://data-argo.ifremer.fr/dac/aoml/7902298/7902298_prof.nc

Architecture diagram live link: https://lucid.app/publicSegments/view/31ad2582-241f-49d8-88fc-b2910ae00ad4/image.png

Code to create bio merged profile:  https://github.com/euroargodev/Coriolis-data-processing-chain-for-Argo-floats/blob/main/decArgo_soft/soft/util/nc_create_synthetic_profile_rt.m
Maybe code to create multi float file: https://github.com/euroargodev/Coriolis-data-processing-chain-for-Argo-floats/blob/main/decArgo_soft/soft/util/nc_create_multi_prof_file.m





Properties file based UserManager implementation. We use user. properties file to store user data.
The file will use the following properties for storing users:
User data properties
Property
Documentation
ftpserver. user.{username}.homedirectory
Path to the home directory for the user, based on the file system implementation used
ftpserver. user.{username}.userpassword
The password for the user. Can be in clear text, MD5 hash or salted SHA hash based on the configuration on the user manager
ftpserver. user.{username}.enableflag
true if the user is enabled, false otherwise
ftpserver. user.{username}.writepermission
true if the user is allowed to upload files and create directories, false otherwise
ftpserver. user.{username}.idletime
The number of seconds the user is allowed to be idle before disconnected. 0 disables the idle timeout
ftpserver. user.{username}.maxloginnumber
The maximum number of concurrent logins by the user. 0 disables the check.
ftpserver. user.{username}.maxloginperip
The maximum number of concurrent logins from the same IP address by the user. 0 disables the check.
ftpserver. user.{username}.uploadrate
The maximum number of bytes per second the user is allowed to upload files. 0 disables the check.
ftpserver. user.{username}.downloadrate
The maximum number of bytes per second the user is allowed to download files. 0 disables the check.
Example:
ftpserver. user. admin. homedirectory=/ ftproot
ftpserver. user. admin. userpassword=admin
ftpserver. user. admin. enableflag=true
ftpserver. user. admin. writepermission=true
ftpserver. user. admin. idletime=0
ftpserver. user. admin. maxloginnumber=0
ftpserver. user. admin. maxloginperip=0
ftpserver. user. admin. uploadrate=0
ftpserver. user. admin. downloadrate=0
