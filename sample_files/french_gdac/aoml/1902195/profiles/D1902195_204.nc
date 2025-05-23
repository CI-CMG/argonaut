CDF      
      	DATE_TIME         	STRING256         STRING64   @   STRING32       STRING16      STRING8       STRING4       STRING2       N_PROF        N_PARAM       N_LEVELS  T   N_CALIB       	N_HISTORY             
   title         Argo float vertical profile    institution       AOML   source        
Argo float     history       92024-04-07T18:30:54Z creation; 2025-01-31T13:13:03Z DMQC;      
references        (http://www.argodatamgt.org/Documentation   comment       	free text      user_manual_version       3.4    Conventions       Argo-3.1 CF-1.6    featureType       trajectoryProfile      comment_dmqc_operator         PPRIMARY | https://orcid.org/0000-0001-9926-5735 | Dr. Hristina G. Hristova, PMEL      C   	DATA_TYPE                  	long_name         	Data type      conventions       Argo reference table 1     
_FillValue                    9�   FORMAT_VERSION                 	long_name         File format version    
_FillValue                    9�   HANDBOOK_VERSION               	long_name         Data handbook version      
_FillValue                    9�   REFERENCE_DATE_TIME                 	long_name         !Date of reference for Julian days      conventions       YYYYMMDDHHMISS     
_FillValue                    9�   DATE_CREATION                   	long_name         Date of file creation      conventions       YYYYMMDDHHMISS     
_FillValue                    9�   DATE_UPDATE                 	long_name         Date of update of this file    conventions       YYYYMMDDHHMISS     
_FillValue                    9�   PLATFORM_NUMBER                   	long_name         Float unique identifier    conventions       WMO float identifier : A9IIIII     
_FillValue                    9�   PROJECT_NAME                  	long_name         Name of the project    
_FillValue                  @  9�   PI_NAME                   	long_name         "Name of the principal investigator     
_FillValue                  @  :0   STATION_PARAMETERS           	            	long_name         ,List of available parameters for the station   conventions       Argo reference table 3     
_FillValue                  @  :p   CYCLE_NUMBER               	long_name         Float cycle number     conventions       =0...N, 0 : launch cycle (if exists), 1 : first complete cycle      
_FillValue         ��        :�   	DIRECTION                  	long_name         !Direction of the station profiles      conventions       -A: ascending profiles, D: descending profiles      
_FillValue                    :�   DATA_CENTRE                   	long_name         .Data centre in charge of float data processing     conventions       Argo reference table 4     
_FillValue                    :�   DC_REFERENCE                  	long_name         (Station unique identifier in data centre   conventions       Data centre convention     
_FillValue                     :�   DATA_STATE_INDICATOR                  	long_name         1Degree of processing the data have passed through      conventions       Argo reference table 6     
_FillValue                    :�   	DATA_MODE                  	long_name         Delayed mode or real time data     conventions       >R : real time; D : delayed mode; A : real time with adjustment     
_FillValue                    :�   PLATFORM_TYPE                     	long_name         Type of float      conventions       Argo reference table 23    
_FillValue                     :�   FLOAT_SERIAL_NO                   	long_name         Serial number of the float     
_FillValue                     ;   FIRMWARE_VERSION                  	long_name         Instrument firmware version    
_FillValue                     ;$   WMO_INST_TYPE                     	long_name         Coded instrument type      conventions       Argo reference table 8     
_FillValue                    ;D   JULD               	long_name         ?Julian day (UTC) of the station relative to REFERENCE_DATE_TIME    standard_name         time   units         "days since 1950-01-01 00:00:00 UTC     conventions       8Relative julian days with decimal part (as parts of day)   
resolution        >�E�vQ�   
_FillValue        A.�~       axis      T           ;H   JULD_QC                	long_name         Quality on date and time   conventions       Argo reference table 2     
_FillValue                    ;P   JULD_LOCATION                  	long_name         @Julian day (UTC) of the location relative to REFERENCE_DATE_TIME   units         "days since 1950-01-01 00:00:00 UTC     conventions       8Relative julian days with decimal part (as parts of day)   
resolution        >�E�vQ�   
_FillValue        A.�~            ;T   LATITUDE               	long_name         &Latitude of the station, best estimate     standard_name         latitude   units         degree_north   
_FillValue        @�i�       	valid_min         �V�        	valid_max         @V�        axis      Y           ;\   	LONGITUDE                  	long_name         'Longitude of the station, best estimate    standard_name         	longitude      units         degree_east    
_FillValue        @�i�       	valid_min         �f�        	valid_max         @f�        axis      X           ;d   POSITION_QC                	long_name         ,Quality on position (latitude and longitude)   conventions       Argo reference table 2     
_FillValue                    ;l   POSITIONING_SYSTEM                    	long_name         Positioning system     
_FillValue                    ;p   VERTICAL_SAMPLING_SCHEME                  	long_name         Vertical sampling scheme   conventions       Argo reference table 16    
_FillValue                    ;x   CONFIG_MISSION_NUMBER                  	long_name         :Unique number denoting the missions performed by the float     conventions       !1...N, 1 : first complete mission      
_FillValue         ��        <x   PROFILE_PRES_QC                	long_name         #Global quality flag of PRES profile    conventions       Argo reference table 2a    
_FillValue                    <|   PROFILE_TEMP_QC                	long_name         #Global quality flag of TEMP profile    conventions       Argo reference table 2a    
_FillValue                    <�   PROFILE_PSAL_QC                	long_name         #Global quality flag of PSAL profile    conventions       Argo reference table 2a    
_FillValue                    <�   PROFILE_NB_SAMPLE_CTD_QC               	long_name         ,Global quality flag of NB_SAMPLE_CTD profile   conventions       Argo reference table 2a    
_FillValue                    <�   PRES         
      
   	long_name         )Sea water pressure, equals 0 at sea-level      standard_name         sea_water_pressure     
_FillValue        G�O�   units         decibar    	valid_min                	valid_max         F;�    C_format      %7.1f      FORTRAN_format        F7.1   
resolution        =���   axis      Z        P  <�   PRES_QC          
         	long_name         quality flag   conventions       Argo reference table 2     
_FillValue                 T  I�   PRES_ADJUSTED            
      	   	long_name         )Sea water pressure, equals 0 at sea-level      standard_name         sea_water_pressure     
_FillValue        G�O�   units         decibar    	valid_min                	valid_max         F;�    C_format      %7.1f      FORTRAN_format        F7.1   
resolution        =���     P  M0   PRES_ADJUSTED_QC         
         	long_name         quality flag   conventions       Argo reference table 2     
_FillValue                 T  Z�   PRES_ADJUSTED_ERROR          
         	long_name         VContains the error on the adjusted values as determined by the delayed mode QC process     
_FillValue        G�O�   units         decibar    C_format      %7.1f      FORTRAN_format        F7.1   
resolution        =���     P  ]�   TEMP         
      	   	long_name         $Sea temperature in-situ ITS-90 scale   standard_name         sea_water_temperature      
_FillValue        G�O�   units         degree_Celsius     	valid_min         �      	valid_max         B      C_format      %10.3f     FORTRAN_format        F10.3      
resolution        :�o     P  k$   TEMP_QC          
         	long_name         quality flag   conventions       Argo reference table 2     
_FillValue                 T  xt   TEMP_ADJUSTED            
      	   	long_name         $Sea temperature in-situ ITS-90 scale   standard_name         sea_water_temperature      
_FillValue        G�O�   units         degree_Celsius     	valid_min         �      	valid_max         B      C_format      %10.3f     FORTRAN_format        F10.3      
resolution        :�o     P  {�   TEMP_ADJUSTED_QC         
         	long_name         quality flag   conventions       Argo reference table 2     
_FillValue                 T  �   TEMP_ADJUSTED_ERROR          
         	long_name         VContains the error on the adjusted values as determined by the delayed mode QC process     
_FillValue        G�O�   units         degree_Celsius     C_format      %10.3f     FORTRAN_format        F10.3      
resolution        :�o     P  �l   PSAL         
      	   	long_name         Practical salinity     standard_name         sea_water_salinity     
_FillValue        G�O�   units         psu    	valid_min         @      	valid_max         B$     C_format      %10.3f     FORTRAN_format        F10.3      
resolution        :�o     P  ��   PSAL_QC          
         	long_name         quality flag   conventions       Argo reference table 2     
_FillValue                 T  �   PSAL_ADJUSTED            
      	   	long_name         Practical salinity     standard_name         sea_water_salinity     
_FillValue        G�O�   units         psu    	valid_min         @      	valid_max         B$     C_format      %10.3f     FORTRAN_format        F10.3      
resolution        :�o     P  �`   PSAL_ADJUSTED_QC         
         	long_name         quality flag   conventions       Argo reference table 2     
_FillValue                 T  ��   PSAL_ADJUSTED_ERROR          
         	long_name         VContains the error on the adjusted values as determined by the delayed mode QC process     
_FillValue        G�O�   units         psu    C_format      %10.3f     FORTRAN_format        F10.3      
resolution        :�o     P  �   NB_SAMPLE_CTD            
         	long_name         2Number of samples in each pressure bin for the CTD     
_FillValue        �     units         count      C_format      %5d    FORTRAN_format        I5     
resolution                �  �T   NB_SAMPLE_CTD_QC         
         	long_name         quality flag   conventions       Argo reference table 2     
_FillValue                 T  ��   	PARAMETER               	            	long_name         /List of parameters with calibration information    conventions       Argo reference table 3     
_FillValue                  @  �P   SCIENTIFIC_CALIB_EQUATION               	            	long_name         'Calibration equation for this parameter    
_FillValue                    Ґ   SCIENTIFIC_CALIB_COEFFICIENT            	            	long_name         *Calibration coefficients for this equation     
_FillValue                    ֐   SCIENTIFIC_CALIB_COMMENT            	            	long_name         .Comment applying to this parameter calibration     
_FillValue                    ڐ   SCIENTIFIC_CALIB_DATE               	             	long_name         Date of calibration    conventions       YYYYMMDDHHMISS     
_FillValue                  8  ސ   HISTORY_INSTITUTION                      	long_name         "Institution which performed action     conventions       Argo reference table 4     
_FillValue                    ��   HISTORY_STEP                     	long_name         Step in data processing    conventions       Argo reference table 12    
_FillValue                    ��   HISTORY_SOFTWARE                     	long_name         'Name of software which performed action    conventions       Institution dependent      
_FillValue                    ��   HISTORY_SOFTWARE_RELEASE                     	long_name         2Version/release of software which performed action     conventions       Institution dependent      
_FillValue                    ��   HISTORY_REFERENCE                        	long_name         Reference of database      conventions       Institution dependent      
_FillValue                  @  ��   HISTORY_DATE                      	long_name         #Date the history record was created    conventions       YYYYMMDDHHMISS     
_FillValue                    �   HISTORY_ACTION                       	long_name         Action performed on data   conventions       Argo reference table 7     
_FillValue                    �(   HISTORY_PARAMETER                        	long_name         (Station parameter action is performed on   conventions       Argo reference table 3     
_FillValue                    �,   HISTORY_START_PRES                    	long_name          Start pressure action applied on   
_FillValue        G�O�   units         decibar         �<   HISTORY_STOP_PRES                     	long_name         Stop pressure action applied on    
_FillValue        G�O�   units         decibar         �@   HISTORY_PREVIOUS_VALUE                    	long_name         +Parameter/Flag previous value before action    
_FillValue        G�O�        �D   HISTORY_QCTEST                       	long_name         <Documentation of tests performed, tests failed (in hex form)   conventions       EWrite tests performed when ACTION=QCP$; tests failed when ACTION=QCF$      
_FillValue                    �HArgo profile    3.1 1.2 19500101000000  20240407183054  20250131131303  1902195 Argo PMEL                                                       GREGORY C. JOHNSON                                              PRES            TEMP            PSAL            NB_SAMPLE_CTD      �A   AO  7165                            2C  D   NAVIS_A                         0853                            170425                          863 @�}ooP�1   @�}p�� @ �s�PH�c.�(��1   GPS     Primary sampling: mixed [above 1700 dbar: averaged, 1Hz sampling by SBE-41CP averaged in 2-dbar bins; below 1700 dbar: discrete, pumped]                                                                                                                           �A   A   B       @�  @�  A   A   A@  A`  A�  A�  A�33A�  A�  A�  A�  A�  A�33B��B  B  B   B(  B0  B8  B@  BH  BP  BX  B`  Bh  Bp  Bx  B�  B�  B�  B�  B�  B�  B�  B�  B�  B�  B�  B�  B�  B�  B���B���B���B���B�33B�33B�33B�  B�  B�  B�  B�  B�  B�  B�  B�  B�  B�  C   C  C�C  C  C
  C  C  C  C  C  C  C  C  C  C  C   C"  C$  C&  C(  C*  C,  C.  C0  C2  C4  C6  C8  C:  C<  C>  C@  CB  CD  CF  CH  CJ  CL  CN  CP  CR  CT  CV  CX  CZ  C\  C^  C`  Cb  Cd  Cf  Ch  Cj  Cl  Cn  Cp  Cr  Ct  Cv  Cx  Cz  C|  C~  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  C�  D   D � D  D� D  D� D  D� D  D� D  D� D  D� D  D� D  D� D	  D	� D
  D
� D  D� D  D� D  D� D  D� D  D� D  D� D  D� D  D� D  D� D  D� D  D� D  D� D  D� D  D� D  D� D  D� D  D� D  D� D  D� D  D� D  D� D   D � D!  D!� D"  D"� D#  D#� D$  D$� D%  D%� D&  D&� D'  D'� D(  D(� D)  D)� D*  D*� D+  D+� D,  D,� D-  D-� D.  D.� D/  D/� D0  D0� D1  D1� D2  D2� D3  D3� D4  D4� D5  D5� D6  D6� D7  D7� D8  D8� D9  D9� D:  D:� D;  D;� D<  D<� D=  D=� D>  D>� D?  D?� D@  D@� DA  DA� DB  DB� DC  DC� DD  DD� DE  DE� DF  DF� DG  DG� DH  DH� DI  DI� DJ  DJ� DK  DK� DL  DL� DM  DM� DN  DN� DO  DO� DP  DP� DQ  DQ� DR  DR� DS  DS� DT  DT� DU  DU� DV  DV� DW  DW� DX  DX� DY  DY� DZ  DZ� D[  D[� D\  D\� D]  D]� D^  D^� D_  D_� D`  D`� Da  Da� Db  Db� Dc  Dc� Dd  Dd� De  De� Df  Df� Dg  Dg� Dh  Dh� Di  Di� Dj  Dj� Dk  Dk� Dl  Dl� Dm  Dm� Dn  Dn� Do  Do� Dp  Dp� Dq  Dq� Dr  Dr� Ds  Ds� Dt  Dt� Du  Du� Dv  Dv� Dw  Dw� Dx  Dx� Dy  Dy� Dz  Dz� D{  D{� D|  D|� D}  D}� D~  D~� D  D� D�  D�@ D�� D�� D�  D�@ D�� D�� D�  D�@ D�� D�� D�  D�@ D�� D�� D�  D�@ D�� D�� D�  D�@ D�� D�� D�  D�@ D�� D�� D�  D�@ D�� D�� D�  D�@ D�� D�� D�  D�@ D�� D�� D�  D�@ D�� D�� D�  D�@ D�� D�� D�  D�@ D�� D�� D�  D�@ D�� D�� D�  D�@ D�� D�� D�  D�@ D�� D�� D�  D�@ D�� D�� D�  D�@ D�� D�� D�  D�@ D�� D�� D�  D�@ D�� D�� D�  D�@ D�� D�� D�  D�@ D�� D�� D�  D�@ D�� D�� D�  D�@ D�� D�� D�  D�@ D�� D�� D�  D�@ D�� D�� D�  D�@ D�� D�� D�  D�<�D�� D�� D�  D�@ D�� D�� D�  D�@ D�� D�� D�  D�@ D�� D�� D�  D�@ D�� D�� D�  D�@ D�� D�� D�  D�@ D�� D�� D�  D�@ D�� D�� D�  D�@ D�� D�� D�  D�@ D�� D�� D�  D�@ D�� D�� D�  D�@ D�� D�� D�  D�@ D�� D�� D�  D�@ D�� D�� D�  D�@ D�� D�� D�  D�@ D�� D�� D�  D�@ D�� D�� D�  D�@ D�� D�� D�  D�@ D�� D�� D�  D�@ D�� D�� D�  D�@ D�� D�� D�  D�@ D�� D�� D�  D�@ D�� D�� D�  D�@ D�� D�� D�  D�@ D�� D�� D�  D�@ D�� D�� D�  D�@ D�� D�� D�  D�@ D�� D�� D�  D�@ D�� D�� D�  D�@ D�|�D�� D�  D�<�D�� D�� D�  D�@ D�� D�� D�  D�@ D�� D�� D�  D�@ D�� D�� D�  D�@ D�� D�� D�  D�@ D�� D�� D�  D�@ D�� D�� D�  D�@ D�� D�� D�  D�@ D�� D�� D�  D�@ D D�� D�  D�@ DÀ D�� D�  D�@ DĀ Dļ�D�  D�@ Dŀ D�� D�  D�@ Dƀ D�� D�  D�@ Dǀ D�� D�  D�@ DȀ D�� D�  D�@ Dɀ D�� D�  D�@ Dʀ D�� D�  D�@ Dˀ D�� D�  D�@ D̀ D�� D�  D�@ D̀ D�� D�  D�@ D΀ D�� D�  D�@ Dπ D�� D�  D�@ DЀ D�� D�  D�@ Dр D�� D�  D�@ DҀ D�� D�  D�@ D�p D�I�DږD���D���D�C�D�\D���111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111@��@��@��A�\A>�\A^�\A~�\A�G�A�z�A�G�A�G�A�G�A�G�A�G�A�z�B=qB��B��B��B'��B/��B7��B?��BG��BO��BW��B_��Bg��Bo��Bw��B��B���B���B���B���B���B���B���B���B���B���B���B���B���B���B�k�B���BĞ�B�B�B�B���B���B���B���B���B���B���B���B���B���B���B���C��C�C��C��C	��C��C��C��C��C��C��C��C��C��C��C��C!��C#��C%��C'��C)��C+��C-��C/��C1��C3��C5��C7��C9��C;��C=��C?��CA��CC��CE��CG��CI��CK��CM��CO��CQ��CS��CU��CW��CY��C[��C]��C_��Ca��Cc��Ce��Cg��Ci��Ck��Cm��Co��Cq��Cs��Cu��Cw��Cy��C{��C}��C��C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{C��{D z=D �=Dz=D�=Dz=D�=Dz=D�=Dz=D�=Dz=D�=Dz=D�=Dz=D�=Dz=D�=D	z=D	�=D
z=D
�=Dz=D�=Dz=D�=Dz=D�=Dz=D�=Dz=D�=Dz=D�=Dz=D�=Dz=D�=Dz=D�=Dz=D�=Dz=D�=Dz=D�=Dz=D�=Dz=D�=Dz=D�=Dz=D�=Dz=D�=Dz=D�=Dz=D�=Dz=D�=Dz=D�=D z=D �=D!z=D!�=D"z=D"�=D#z=D#�=D$z=D$�=D%z=D%�=D&z=D&�=D'z=D'�=D(z=D(�=D)z=D)�=D*z=D*�=D+z=D+�=D,z=D,�=D-z=D-�=D.z=D.�=D/z=D/�=D0z=D0�=D1z=D1�=D2z=D2�=D3z=D3�=D4z=D4�=D5z=D5�=D6z=D6�=D7z=D7�=D8z=D8�=D9z=D9�=D:z=D:�=D;z=D;�=D<z=D<�=D=z=D=�=D>z=D>�=D?z=D?�=D@z=D@�=DAz=DA�=DBz=DB�=DCz=DC�=DDz=DD�=DEz=DE�=DFz=DF�=DGz=DG�=DHz=DH�=DIz=DI�=DJz=DJ�=DKz=DK�=DLz=DL�=DMz=DM�=DNz=DN�=DOz=DO�=DPz=DP�=DQz=DQ�=DRz=DR�=DSz=DS�=DTz=DT�=DUz=DU�=DVz=DV�=DWz=DW�=DXz=DX�=DYz=DY�=DZz=DZ�=D[z=D[�=D\z=D\�=D]z=D]�=D^z=D^�=D_z=D_�=D`z=D`�=Daz=Da�=Dbz=Db�=Dcz=Dc�=Ddz=Dd�=Dez=De�=Dfz=Df�=Dgz=Dg�=Dhz=Dh�=Diz=Di�=Djz=Dj�=Dkz=Dk�=Dlz=Dl�=Dmz=Dm�=Dnz=Dn�=Doz=Do�=Dpz=Dp�=Dqz=Dq�=Drz=Dr�=Dsz=Ds�=Dtz=Dt�=Duz=Du�=Dvz=Dv�=Dwz=Dw�=Dxz=Dx�=Dyz=Dy�=Dzz=Dz�=D{z=D{�=D|z=D|�=D}z=D}�=D~z=D~�=Dz=D�=D�=D�}D��D��D�=D�}D��D��D�=D�}D��D��D�=D�}D��D��D�=D�}D��D��D�=D�}D��D��D�=D�}D��D��D�=D�}D��D��D�=D�}D��D��D�=D�}D��D��D�=D�}D��D��D�=D�}D��D��D�=D�}D��D��D�=D�}D��D��D�=D�}D��D��D�=D�}D��D��D�=D�}D��D��D�=D�}D��D��D�=D�}D��D��D�=D�}D��D��D�=D�}D��D��D�=D�}D��D��D�=D�}D��D��D�=D�}D��D��D�=D�}D��D��D�=D�}D��D��D�=D�}D��D��D�9�D�}D��D��D�=D�}D��D��D�=D�}D��D��D�=D�}D��D��D�=D�}D��D��D�=D�}D��D��D�=D�}D��D��D�=D�}D��D��D�=D�}D��D��D�=D�}D��D��D�=D�}D��D��D�=D�}D��D��D�=D�}D��D��D�=D�}D��D��D�=D�}D��D��D�=D�}D��D��D�=D�}D��D��D�=D�}D��D��D�=D�}D��D��D�=D�}D��D��D�=D�}D��D��D�=D�}D��D��D�=D�}D��D��D�=D�}D��D��D�=D�}D��D��D�=D�}D��D��D�=D�}D��D��D�=D�}D��D��D�=D�}D��D��D�=D�y�D��D��D�9�D�}D��D��D�=D�}D��D��D�=D�}D��D��D�=D�}D��D��D�=D�}D��D��D�=D�}D��D��D�=D�}D��D��D�=D�}D��D��D�=D�}D��D��D�=D�}D½D��D�=D�}DýD��D�=D�}DĹ�D��D�=D�}DŽD��D�=D�}DƽD��D�=D�}DǽD��D�=D�}DȽD��D�=D�}DɽD��D�=D�}DʽD��D�=D�}D˽D��D�=D�}D̽D��D�=D�}DͽD��D�=D�}DνD��D�=D�}DϽD��D�=D�}DнD��D�=D�}DѽD��D�=D�}DҽD��D�=D�mD�F�Dړ3D���D���D�@�D�{D���111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��@��A�5?A�5?A�7LA�7LA�7LA�5?A�;dA�7LA�9XA�=qA�=qA�A�A�A�A�A�A�C�A�C�A�A�A�C�A�E�A�E�A�E�A�E�A�G�A�E�A�E�A�E�A�G�A�G�A�I�A�G�A�I�A�K�A�I�A�K�A�K�A�M�A�M�A�M�A�I�A�I�A�O�A�I�A�A�A�$�Aޥ�AבhA�^5A�A©�A���A�?}A�=qA��A�$�A���A�oA��A��+A��AyK�AtbAsVAl�uA_��A]XAZ�yAV�AR�AQ7LAM�hAI`BAG�AE"�ACƨAB=qAAA@^5A>z�A>r�A>ȴA=�A<E�A;�#A:ĜA:^5A9��A8Q�A7|�A6��A6-A5dZA5VA4I�A3dZA3�A2��A1��A1;dA0ĜA0 �A/��A/VA.ZA-|�A-VA,M�A+��A+/A+A*�HA*�+A*  A)�;A)�
A)��A)�-A)x�A)oA(�9A(n�A(ffA(I�A(bA(  A'�A'XA'%A&��A&�A&�HA&ĜA&�A&�A&(�A%��A%�A%C�A%%A$ĜA$�+A$Q�A$  A#��A#�A#`BA#"�A"�yA"�jA"��A"I�A"1A!�FA!x�A!%A �9A ��A ZA bA��A��AS�A
=A�9AbNA�mAp�A"�A��AbNA�#A��A�Ap�AS�A�yA�uA5?A��At�AO�A&�A�A5?AƨAdZA;dA�9AffA(�A�AƨA�AVA�9Ar�AA�A=qA9XA5?A�Al�A�A��AVA=qA-AJA�
A��Al�AS�A�A�A�DAv�AjAA�A��A��A��AS�A%A�`A��A1AƨA��A\)A��A�DAQ�A�A�A�A
�A
ȴA
~�A
Q�A
1A	��A	G�A��Az�A1A��Al�A��A^5A-A�;A�A`BA��Az�A��AdZA��A��Av�A�A1A�TAx�A�A ��A ��A M�@���@�S�@�V@�@�O�@��9@�1'@�S�@�M�@�X@��`@�1@�K�@�;d@�o@�ff@�hs@���@�I�@�|�@�n�@�@�V@��@��@�@�n�@�5?@���@���@�p�@��@�bN@�+@�=q@��@�@���@�;d@�R@�5?@�-@��@�(�@�;d@�+@��@�^@�p�@���@߶F@�~�@�=q@�@���@���@���@ݑh@ܛ�@�K�@��y@�ff@ٺ^@ؼj@�A�@�\)@�V@��@���@� �@���@ӝ�@�@�{@��@ѡ�@�&�@�Z@ϥ�@��@�J@�O�@̃@˕�@�+@�~�@��@��@Ɂ@�/@�Ĝ@�bN@�  @�|�@�C�@�"�@���@���@�%@ļj@�z�@�Q�@��@�K�@�ȴ@�@�$�@���@���@�G�@�V@���@��@� �@��@�C�@�
=@��!@�^5@�=q@���@��-@�X@��j@�j@��m@�t�@���@�J@��^@�7L@��`@���@�Ĝ@��@�bN@��
@�+@��@��!@�M�@��@�@��@�X@��9@��D@�Q�@�Q�@�Q�@�1'@�1'@�b@��m@�dZ@���@���@��\@�$�@��@��-@�p�@��/@��@�z�@�A�@��m@���@��@��+@�ff@�{@���@���@��@�hs@�/@��j@�Q�@��
@���@�l�@��y@�^5@�@��^@�X@��/@�j@��@�33@�n�@�-@���@���@��@�p�@���@��9@�Q�@��@�b@��@���@��P@�
=@��!@�~�@�V@�$�@���@��h@�/@���@�9X@��
@��P@�+@�ȴ@��+@�5?@�@���@�?}@�Ĝ@���@�r�@�A�@��@�t�@�33@��@��R@�ff@���@��@�X@�G�@�&�@��`@��9@�j@���@�\)@�33@��@���@��!@��+@�ff@�-@�J@�@��h@�x�@�hs@�?}@��/@��j@�bN@�b@��@�ƨ@��@�+@���@��@���@��R@��+@�5?@�$�@�$�@��@���@��@��j@���@��D@�A�@��
@���@��@�dZ@��@���@��+@�E�@��@��#@��7@�/@�%@���@�r�@��@���@��@��@�+@��y@�~�@�E�@�{@���@��h@�`B@���@�r�@�I�@���@��
@�ƨ@���@�|�@�dZ@�dZ@�C�@��@�n�@�@��^@��7@�p�@�`B@�G�@�V@��`@�Ĝ@�(�@��F@��@�dZ@�C�@�
=@���@�ff@�=q@�5?@�5?@�@���@��^@���@�G�@�V@��/@�z�@�Q�@�1'@�1@K�@
=@~��@~ȴ@~�+@~{@}p�@|�j@{�
@{��@{dZ@z�@z�\@z�@y��@y�^@yhs@y&�@x��@x�@w�@w�@w�P@wK�@v�R@vV@v$�@u�h@up�@u?}@t��@t�D@s��@s�F@st�@r~�@q��@q%@pĜ@p�9@pr�@o�;@o+@n�@nff@n{@m�@lz�@kƨ@kS�@k"�@j^5@ix�@iX@i&�@h��@h  @gK�@g�@f�R@f��@f5?@f@e@e�-@e��@eO�@e/@eV@d��@d�/@d�@dj@ct�@cC�@c"�@c@b�H@b��@b^5@b-@bJ@a�#@a��@a7L@`Q�@`1'@_|�@_
=@^v�@^5?@^$�@^$�@^$�@]�T@]�h@]O�@]�@\��@\�j@\(�@[ƨ@[�@[t�@[33@[@Z�!@Z-@Y��@Y�7@Y�@X�@XQ�@X1'@X  @W��@WK�@V��@V{@U��@Up�@UO�@U/@U�@U�@T��@T�@Tz�@Tj@Tj@TI�@S��@R�@R�H@R�H@R�H@R=q@Q��@Q�7@Qhs@QX@P��@P�@O�;@N�@Nff@NE�@M�@M��@M�@Lj@LZ@L(�@Kƨ@K33@J�H@Jn�@JM�@I�@I�^@I7L@H�9@HbN@HbN@HbN@G�;@G�P@F�y@F��@FV@F@E�-@Ep�@EO�@EV@D�@DI�@C�
@C�@Ct�@CC�@B�H@Bn�@A��@A�7@A�@@��@@�9@@bN@@1'@?�@?�w@?�P@?\)@>�y@>��@>ff@>@=�h@=�@<j@;�m@;��@;dZ@:�H@:~�@:J@9��@9x�@97L@8��@8Ĝ@8��@8bN@8A�@81'@8 �@7�@7l�@6��@65?@5�@5@5�@5O�@4�@4��@3j�@-q@'�*@!�@{J@C@��111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111A�5?A�5?A�7LA�7LA�7LA�5?A�;dA�7LA�9XA�=qA�=qA�A�A�A�A�A�A�C�A�C�A�A�A�C�A�E�A�E�A�E�A�E�A�G�A�E�A�E�A�E�A�G�A�G�A�I�A�G�A�I�A�K�A�I�A�K�A�K�A�M�A�M�A�M�A�I�A�I�A�O�A�I�A�A�A�$�Aޥ�AבhA�^5A�A©�A���A�?}A�=qA��A�$�A���A�oA��A��+A��AyK�AtbAsVAl�uA_��A]XAZ�yAV�AR�AQ7LAM�hAI`BAG�AE"�ACƨAB=qAAA@^5A>z�A>r�A>ȴA=�A<E�A;�#A:ĜA:^5A9��A8Q�A7|�A6��A6-A5dZA5VA4I�A3dZA3�A2��A1��A1;dA0ĜA0 �A/��A/VA.ZA-|�A-VA,M�A+��A+/A+A*�HA*�+A*  A)�;A)�
A)��A)�-A)x�A)oA(�9A(n�A(ffA(I�A(bA(  A'�A'XA'%A&��A&�A&�HA&ĜA&�A&�A&(�A%��A%�A%C�A%%A$ĜA$�+A$Q�A$  A#��A#�A#`BA#"�A"�yA"�jA"��A"I�A"1A!�FA!x�A!%A �9A ��A ZA bA��A��AS�A
=A�9AbNA�mAp�A"�A��AbNA�#A��A�Ap�AS�A�yA�uA5?A��At�AO�A&�A�A5?AƨAdZA;dA�9AffA(�A�AƨA�AVA�9Ar�AA�A=qA9XA5?A�Al�A�A��AVA=qA-AJA�
A��Al�AS�A�A�A�DAv�AjAA�A��A��A��AS�A%A�`A��A1AƨA��A\)A��A�DAQ�A�A�A�A
�A
ȴA
~�A
Q�A
1A	��A	G�A��Az�A1A��Al�A��A^5A-A�;A�A`BA��Az�A��AdZA��A��Av�A�A1A�TAx�A�A ��A ��A M�@���@�S�@�V@�@�O�@��9@�1'@�S�@�M�@�X@��`@�1@�K�@�;d@�o@�ff@�hs@���@�I�@�|�@�n�@�@�V@��@��@�@�n�@�5?@���@���@�p�@��@�bN@�+@�=q@��@�@���@�;d@�R@�5?@�-@��@�(�@�;d@�+@��@�^@�p�@���@߶F@�~�@�=q@�@���@���@���@ݑh@ܛ�@�K�@��y@�ff@ٺ^@ؼj@�A�@�\)@�V@��@���@� �@���@ӝ�@�@�{@��@ѡ�@�&�@�Z@ϥ�@��@�J@�O�@̃@˕�@�+@�~�@��@��@Ɂ@�/@�Ĝ@�bN@�  @�|�@�C�@�"�@���@���@�%@ļj@�z�@�Q�@��@�K�@�ȴ@�@�$�@���@���@�G�@�V@���@��@� �@��@�C�@�
=@��!@�^5@�=q@���@��-@�X@��j@�j@��m@�t�@���@�J@��^@�7L@��`@���@�Ĝ@��@�bN@��
@�+@��@��!@�M�@��@�@��@�X@��9@��D@�Q�@�Q�@�Q�@�1'@�1'@�b@��m@�dZ@���@���@��\@�$�@��@��-@�p�@��/@��@�z�@�A�@��m@���@��@��+@�ff@�{@���@���@��@�hs@�/@��j@�Q�@��
@���@�l�@��y@�^5@�@��^@�X@��/@�j@��@�33@�n�@�-@���@���@��@�p�@���@��9@�Q�@��@�b@��@���@��P@�
=@��!@�~�@�V@�$�@���@��h@�/@���@�9X@��
@��P@�+@�ȴ@��+@�5?@�@���@�?}@�Ĝ@���@�r�@�A�@��@�t�@�33@��@��R@�ff@���@��@�X@�G�@�&�@��`@��9@�j@���@�\)@�33@��@���@��!@��+@�ff@�-@�J@�@��h@�x�@�hs@�?}@��/@��j@�bN@�b@��@�ƨ@��@�+@���@��@���@��R@��+@�5?@�$�@�$�@��@���@��@��j@���@��D@�A�@��
@���@��@�dZ@��@���@��+@�E�@��@��#@��7@�/@�%@���@�r�@��@���@��@��@�+@��y@�~�@�E�@�{@���@��h@�`B@���@�r�@�I�@���@��
@�ƨ@���@�|�@�dZ@�dZ@�C�@��@�n�@�@��^@��7@�p�@�`B@�G�@�V@��`@�Ĝ@�(�@��F@��@�dZ@�C�@�
=@���@�ff@�=q@�5?@�5?@�@���@��^@���@�G�@�V@��/@�z�@�Q�@�1'@�1@K�@
=@~��@~ȴ@~�+@~{@}p�@|�j@{�
@{��@{dZ@z�@z�\@z�@y��@y�^@yhs@y&�@x��@x�@w�@w�@w�P@wK�@v�R@vV@v$�@u�h@up�@u?}@t��@t�D@s��@s�F@st�@r~�@q��@q%@pĜ@p�9@pr�@o�;@o+@n�@nff@n{@m�@lz�@kƨ@kS�@k"�@j^5@ix�@iX@i&�@h��@h  @gK�@g�@f�R@f��@f5?@f@e@e�-@e��@eO�@e/@eV@d��@d�/@d�@dj@ct�@cC�@c"�@c@b�H@b��@b^5@b-@bJ@a�#@a��@a7L@`Q�@`1'@_|�@_
=@^v�@^5?@^$�@^$�@^$�@]�T@]�h@]O�@]�@\��@\�j@\(�@[ƨ@[�@[t�@[33@[@Z�!@Z-@Y��@Y�7@Y�@X�@XQ�@X1'@X  @W��@WK�@V��@V{@U��@Up�@UO�@U/@U�@U�@T��@T�@Tz�@Tj@Tj@TI�@S��@R�@R�H@R�H@R�H@R=q@Q��@Q�7@Qhs@QX@P��@P�@O�;@N�@Nff@NE�@M�@M��@M�@Lj@LZ@L(�@Kƨ@K33@J�H@Jn�@JM�@I�@I�^@I7L@H�9@HbN@HbN@HbN@G�;@G�P@F�y@F��@FV@F@E�-@Ep�@EO�@EV@D�@DI�@C�
@C�@Ct�@CC�@B�H@Bn�@A��@A�7@A�@@��@@�9@@bN@@1'@?�@?�w@?�P@?\)@>�y@>��@>ff@>@=�h@=�@<j@;�m@;��@;dZ@:�H@:~�@:J@9��@9x�@97L@8��@8Ĝ@8��@8bN@8A�@81'@8 �@7�@7l�@6��@65?@5�@5@5�@5O�@4�@4��@3j�@-q@'�*@!�@{J@C@��111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;o;oB
=B
=B
=B
=B
=B
=B
=B
=B
=B
=B
=B
=B
=B
=B
=B
=BDB
=BDBDBDBDB
=BDBDBDBDBDBDBDBDBDBDBDBDBDBDBDBDBDBDBDBDB+B
��B
�/B
��B
z�B
cTB
%�B
�B
�B
PB
  B	��B	��B	��B	��B
  B
�B
(�B
&�B
/B
�B
�B
&�B
Q�B
bNB
_;B
iyB
gmB
s�B
�B
�7B
�hB
�uB
�\B
�=B
�bB
�^B
ÖB
��B
�#B
�/B
�#B
�#B
�;B
�BB
�NB
�ZB
�ZB
�ZB
�`B
�TB
�NB
�TB
�NB
�HB
�BB
�5B
�#B
�B
�
B
�
B
��B
��B
��B
��B
��B
��B
��B
��B
��B
��B
��B
�
B
�
B
�B
�B
�
B
�
B
�
B
��B
��B
��B
��B
��B
��B
��B
��B
�B
��B
�B
��B
��B
��B
��B
��B
��B
��B
��B
��B
��B
��B
��B
��B
��B
��B
��B
��B
��B
��B
��B
��B
��B
��B
��B
��B
ɺB
ȴB
ȴB
ȴB
ǮB
ƨB
ŢB
ĜB
B
B
��B
�}B
�wB
�wB
�qB
�qB
�jB
�^B
�^B
�RB
�LB
�LB
�FB
�?B
�3B
�!B
�B
�B
�B
�B
�B
�B
��B
��B
��B
��B
��B
��B
��B
��B
��B
��B
��B
��B
��B
��B
��B
��B
��B
��B
��B
��B
��B
��B
��B
��B
��B
��B
��B
�{B
�uB
�oB
�oB
�hB
�hB
�hB
�\B
�VB
�PB
�PB
�JB
�=B
�7B
�1B
�%B
�B
�B
�B
�B
�B
�B
� B
}�B
|�B
z�B
z�B
x�B
w�B
v�B
t�B
s�B
s�B
q�B
q�B
p�B
n�B
m�B
k�B
jB
iyB
iyB
gmB
ffB
ffB
e`B
dZB
cTB
bNB
bNB
aHB
`BB
_;B
]/B
]/B
\)B
[#B
ZB
YB
XB
VB
VB
T�B
S�B
S�B
S�B
R�B
P�B
P�B
N�B
N�B
N�B
L�B
L�B
J�B
I�B
I�B
H�B
H�B
H�B
H�B
H�B
G�B
E�B
E�B
C�B
B�B
B�B
A�B
B�B
A�B
A�B
?}B
?}B
>wB
=qB
=qB
<jB
<jB
<jB
;dB
;dB
;dB
:^B
:^B
:^B
:^B
:^B
:^B
9XB
8RB
8RB
8RB
7LB
7LB
7LB
6FB
6FB
6FB
5?B
5?B
49B
5?B
5?B
49B
49B
49B
49B
33B
33B
33B
2-B
2-B
2-B
1'B
2-B
1'B
1'B
1'B
1'B
1'B
1'B
1'B
1'B
1'B
1'B
1'B
1'B
0!B
0!B
0!B
0!B
/B
0!B
0!B
0!B
0!B
0!B
0!B
0!B
/B
0!B
0!B
/B
0!B
0!B
/B
1'B
1'B
1'B
1'B
1'B
1'B
2-B
2-B
2-B
1'B
1'B
1'B
0!B
1'B
1'B
1'B
1'B
1'B
1'B
1'B
2-B
1'B
1'B
2-B
2-B
2-B
2-B
2-B
2-B
49B
49B
49B
49B
49B
49B
5?B
5?B
5?B
5?B
5?B
5?B
5?B
5?B
5?B
5?B
5?B
6FB
6FB
5?B
5?B
49B
49B
33B
33B
33B
33B
2-B
2-B
2-B
2-B
33B
33B
49B
49B
49B
49B
49B
5?B
5?B
6FB
7LB
7LB
7LB
8RB
9XB
9XB
9XB
9XB
9XB
9XB
9XB
9XB
:^B
:^B
:^B
:^B
:^B
:^B
:^B
:^B
:^B
:^B
:^B
:^B
;dB
;dB
;dB
;dB
;dB
<jB
<jB
<jB
<jB
<jB
<jB
<jB
=qB
<jB
<jB
<jB
<jB
=qB
=qB
>wB
>wB
>wB
>wB
>wB
>wB
>wB
>wB
>wB
>wB
?}B
?}B
?}B
?}B
?}B
?}B
?}B
?}B
?}B
?}B
?}B
>wB
?}B
?}B
?}B
>wB
?}B
?}B
?}B
?}B
@�B
@�B
@�B
@�B
@�B
@�B
@�B
@�B
@�B
@�B
A�B
A�B
A�B
A�B
A�B
B�B
C�B
C�B
C�B
D�B
D�B
D�B
D�B
D�B
D�B
E�B
E�B
E�B
E�B
E�B
E�B
E�B
E�B
E�B
E�B
E�B
E�B
E�B
E�B
E�B
F�B
F�B
F�B
F�B
G�B
G�B
G�B
H�B
H�B
H�B
I�B
I�B
I�B
J�B
J�B
J�B
J�B
J�B
J�B
J�B
K�B
K�B
K�B
K�B
K�B
K�B
K�B
K�B
K�B
L�B
L�B
L�B
L�B
L�B
L�B
M�B
M�B
M�B
M�B
M�B
M�B
M�B
M�B
M�B
M�B
M�B
M�B
N�B
N�B
N�B
N�B
N�B
N�B
N�B
N�B
N�B
N�B
N�B
O�B
O�B
O�B
O�B
O�B
O�B
O�B
O�B
O�B
O�B
O�B
O�B
P�B
P�B
P�B
P�B
P�B
P�B
P�B
P�B
Q�B
Q�B
Q�B
Q�B
Q�B
R�B
R�B
R�B
S�B
S�B
T�B
T�B
T�B
T�B
T�B
VB
VB
VB
VB
VB
VB
VB
VB
VB
W
B
W
B
W
B
W
B
XB
XB
YB
YB
YB
YB
ZB
ZB
ZB
[#B
\)B
\)B
\)B
\)B
\)B
\)B
\)B
\)B
\)B
]/B
]/B
]/B
]/B
]/B
^5B
^5B
^5B
^5B
_;B
_;B
`BB
_;B
`BB
`BB
aHB
aHB
aHB
aHB
aHB
aHB
aHB
aHB
aHB
aHB
aHB
bNB
bNB
bNB
bNB
bNB
bNB
cTB
cTB
cTB
cTB
dZB
dZB
dZB
dZB
dZB
dZB
dZB
e`B
e`B
e`B
e`B
e`B
e`B
ffB
e`B
ffB
ffB
ffB
ffB
ffB
ffB
gmB
gmB
gmB
gmB
gmB
gmB
gmB
hsB
gmB
gmB
hsB
hsB
hsB
iyB
iyB
iyB
jB
jB
jB
k�B
k�B
k�B
k�B
k�B
l�B
l�B
l�B
l�B
l�B
l�B
m�B
m�B
m�B
m�B
m�B
m�B
n�B
n�B
n�B
n�B
n�B
o�B
o�B
o�B
o�B
o�B
p�B
p�B
p�B
p�B
p�B
p�B
q�B
q�B
q�B
q�B
q�B
r�B
r�B
r�B
r�B
r�B
r�B
s�B
s�B
s�B
s�B
s�B
s�B
t�B
t�B
t�B
t�B
u�B
u�B
u�B
v�B
v�B
v�B
v�B
v�B
v�B
v�B
v�B
v�B
v�B
v�B
w�B
w�B
x�B
x�B
x�B
x�B
x�B
x�B
y�B
y�B
}�B
�oB
�9B
�RB
��B
��111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111141111111B
CB
=B
DB
DB
KB
0B
QB
<B
3B
CB
1B
DB
DB
<B
DB
LBAB
;BKBKBJBBB
MBKBJBBBJBCBTBABBBSBCBKBDBKBKBWBKB:B[BcB�BHB�B
��B
�B
}B
l�B
*B
(�B
*hB
�B

�B
B	�XB	��B
9B

�B
(�B
*�B
0�B
D�B
#B
.B
.�B
W�B
dVB
d�B
p�B
k�B
w�B
��B
��B
��B
��B
��B
�UB
��B
�gB
ŖB
�B
��B
�9B
ܼB
��B
�eB
�B
�B
�uB
�NB
�zB
��B
�6B
�B
�tB
��B
�B
�B
߹B
ܢB
�B
�aB
�LB
�*B
��B
�/B
�pB
�VB
��B
�dB
�NB
�B
�B
�ZB
��B
�9B
�%B
��B
�/B
�kB
״B
�<B
��B
��B
��B
�B
�"B
�.B
�cB
�XB
ՍB
�B
�B
��B
��B
ӷB
ҼB
џB
эB
��B
�B
�UB
�MB
ѠB
ыB
�^B
�*B
��B
ѝB
пB
ωB
�B
ΧB
�B
�wB
˅B
ˁB
�LB
�kB
�yB
ɗB
ȊB
��B
��B
�lB
�B
��B
��B
�0B
��B
��B
��B
��B
�YB
�nB
��B
�ZB
��B
��B
��B
��B
�#B
��B
��B
�=B
��B
��B
��B
�aB
��B
�B
��B
��B
�bB
��B
��B
��B
��B
�8B
�B
��B
��B
�B
��B
�B
�EB
�NB
�7B
��B
�EB
�TB
�pB
��B
��B
�B
�UB
��B
�B
�SB
�NB
��B
�SB
��B
�B
��B
�B
�|B
��B
��B
��B
��B
�RB
��B
��B
��B
��B
��B
�$B
�B
HB
}�B
|+B
{�B
y�B
y!B
x�B
uUB
t�B
t�B
r B
r�B
rB
p�B
n�B
l�B
kAB
jUB
jzB
g�B
f�B
g�B
f�B
e3B
c�B
c5B
cAB
b@B
a�B
`B
]�B
^B
\�B
\yB
[�B
Z�B
X�B
WUB
WB
U B
T:B
T�B
UB
S�B
Q�B
Q�B
PB
O�B
O�B
MB
M�B
K�B
JsB
J
B
IB
I$B
I	B
IcB
IkB
I;B
F�B
GYB
D/B
CzB
CZB
B?B
C>B
B@B
BcB
@�B
@�B
?kB
>B
=�B
<�B
=�B
=�B
=B
;�B
;�B
:�B
:tB
:lB
:�B
;�B
<)B
9�B
9B
9LB
9�B
7�B
8�B
8�B
6�B
7�B
7:B
5�B
5�B
5B
6{B
5�B
4�B
4�B
5QB
5>B
4XB
4WB
48B
3aB
3mB
2�B
2B
2�B
1vB
1�B
1�B
1�B
1�B
1�B
1�B
1�B
1eB
1�B
2�B
2<B
0�B
0�B
0lB
0�B
0B
0�B
0xB
0�B
0�B
0vB
0�B
0�B
/{B
0�B
0�B
/�B
0�B
0�B
/�B
1�B
1jB
1�B
1�B
1�B
2B
2�B
2�B
2�B
2"B
1�B
1�B
0�B
1�B
1FB
1GB
1TB
1�B
1�B
2B
2�B
1iB
1�B
2vB
2XB
2]B
3B
3B
2pB
4�B
4EB
4FB
4oB
4FB
4qB
5�B
5�B
5�B
5�B
5�B
5�B
5�B
5�B
5�B
6B
5�B
6�B
6�B
5�B
5�B
4�B
5B
3jB
3�B
3�B
3}B
2XB
2YB
2�B
2�B
3�B
3�B
4�B
4|B
4�B
4�B
4�B
5�B
5�B
6�B
7�B
7�B
8JB
9VB
9�B
9�B
9�B
9�B
9{B
9�B
9�B
9�B
:�B
:tB
:�B
:�B
:�B
;B
:�B
:�B
:�B
:�B
:�B
:�B
;�B
<"B
;�B
;�B
;�B
<�B
<�B
<�B
<�B
<�B
<�B
=B
>%B
<�B
<�B
<�B
<�B
>/B
=�B
?B
>�B
? B
?cB
>�B
>�B
>�B
>�B
>�B
>�B
@B
@�B
?�B
?�B
?�B
?�B
?�B
?�B
?�B
?�B
?�B
>�B
?�B
?�B
?�B
>�B
?�B
?�B
?�B
?�B
@�B
@�B
@�B
AB
@�B
@�B
@�B
@�B
@�B
@�B
A�B
A�B
A�B
BB
B:B
CB
C�B
C�B
DB
E6B
D�B
D�B
D�B
EB
E B
E�B
FB
E�B
FB
F"B
F&B
E�B
E�B
F-B
F'B
FB
E�B
E�B
F,B
F	B
GHB
GB
F�B
GB
HB
HB
H�B
I=B
H�B
I-B
I�B
I�B
J
B
J�B
J�B
J�B
J�B
KIB
K�B
K\B
L?B
LB
K�B
K�B
K�B
L'B
LB
LB
L�B
MyB
MB
M
B
M	B
M-B
MnB
N0B
NB
M�B
M�B
N%B
N&B
NB
NB
NRB
N0B
N'B
NhB
OB
OB
O%B
OhB
OB
N�B
OB
OB
O8B
OZB
OfB
P~B
PB
PB
P9B
P1B
P<B
P!B
O�B
P%B
PB
P%B
P$B
QXB
QB
Q	B
QB
QYB
Q6B
QB
Q[B
RB
RB
R*B
RKB
RcB
S-B
S4B
S�B
T�B
TpB
U9B
UB
U>B
UyB
U�B
VNB
ViB
VLB
VwB
V�B
VuB
VRB
V.B
V�B
W�B
W)B
W5B
WiB
X�B
X�B
YBB
Y`B
Y8B
YbB
ZHB
ZSB
Z3B
[;B
\jB
\JB
\JB
\@B
\KB
\WB
\cB
\�B
\VB
]QB
]QB
]QB
]SB
]�B
^bB
^XB
^dB
^fB
_�B
_�B
`hB
_�B
`�B
`�B
a�B
a_B
aTB
aUB
a�B
a�B
a�B
axB
akB
a�B
a�B
b�B
b�B
bhB
b�B
b~B
b�B
c�B
c�B
c�B
c�B
d�B
d�B
dB
d�B
d�B
d�B
d�B
e�B
e�B
e�B
e�B
e�B
ewB
fsB
e�B
f�B
f�B
f}B
fsB
f�B
f�B
g�B
g�B
gyB
g~B
g�B
g�B
g�B
h�B
g�B
g�B
h�B
iB
i@B
i�B
i�B
i�B
j�B
j�B
kB
k�B
k�B
k�B
k�B
k�B
l�B
l�B
l�B
l�B
l�B
l�B
m�B
m�B
m�B
nB
m�B
nB
n�B
n�B
n�B
n�B
n�B
o�B
o�B
o�B
o�B
o�B
p�B
p�B
p�B
p�B
p�B
p�B
q�B
q�B
q�B
q�B
q�B
r�B
r�B
r�B
r�B
r�B
sB
s�B
s�B
tB
tB
tB
t8B
uB
t�B
t�B
uB
vB
vB
vB
v�B
wB
w B
v�B
v�B
wB
v�B
v�B
v�B
v�B
w5B
xaB
x,B
yB
yB
yB
yB
y)B
y�G�O�B
y�B
}�B
�oB
�9B
�RB
��B
��111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111141111111<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<'=c<#�
<#�
<�n�<#�
<,�<#�
<Qp
<�8d<W} <A�#<6��<#�
<#�
<#�
<=m <(><#�
<.��<��1<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
<#�
G�O�<#�
<#�
<#�
<#�
<#�
<#�
<#�
                                          !    " ~ � O � � m � %  ! $ % # !    ! $      % $ $ !                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       
�������000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000009999999PRES            TEMP            PSAL            NB_SAMPLE_CTD   PRES_ADJ = PRES - (REPORTED_SURFACE_PRESSURE)                                                                                                                                                                                                                   none                                                                                                                                                                                                                                                            PSAL_ADJUSTED includes correction for the effects of pressure adj + Cnd. Thermal Mass (CTM), Johnson et al. JAOT (2007)                                                                                                                                         not applicable                                                                                                                                                                                                                                                  REPORTED_SURFACE_PRESSURE =0.09 dbar                                                                                                                                                                                                                            none                                                                                                                                                                                                                                                            CTM: alpha=0.141, tau=6.68s  with error equal to the correction.                                                                                                                                                                                                not applicable                                                                                                                                                                                                                                                  Pressure adjusted using reported surface pressure. The quoted error is manufacturer specified accuracy in dbar.                                                                                                                                                 The quoted error is manufacturer specified accuracy with respect to ITS-90.                                                                                                                                                                                     No significant salinity drift detected. The quoted error is max(0.01, CTM + sensor accuracy) in PSS-78.                                                                                                                                                         not applicable                                                                                                                                                                                                                                                  20241228215016202412282150162024122821501620241228215016AO  ARCAADJP                                                                    20240407183054    IP                G�O�G�O�G�O�                AO  ARGQQCPL                                                                    20240407183054  QCP$                G�O�G�O�G�O�1F83E           AO  ARGQQCPL                                                                    20240407183054  QCF$                G�O�G�O�G�O�0               PM  ARSQPADJV1.1                                                                20241228215016  QC                  G�O�G�O�G�O�                PM  ARSQCTM V1.1                                                                20241228215016  QC                  G�O�G�O�G�O�                PM  ARSQCOWGV1.1                                                                20250131131229  IP                  G�O�G�O�G�O�                