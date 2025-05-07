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
