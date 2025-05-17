# Argonaut
Argonaut implements an Argo Global Data Assembly Centre (GDAC) as outlined in the [Argo GDAC Cookbook](http://doi.org/10.13155/46202).

Argonaut is a self-contained application with the only system dependency being a Java Runtime Environment, JRE 21+.  Features of Argonaut include:
* File event driven - tar.gz files added to a DAC submit directory will trigger the ingest process.
* File event driven - DAC remove files added to the submit directory will trigger the profile / metadata removal process.
* Validation of data submissions with submission reports available to submitters. 
* Optional FTP servers for DAC submission and public data dissemination.
* Optional web server for public file dissemination.
* Automatic generation of float profile merge files.

This project is still in development and not all requirements in the GDAC Cookbook have been implemented.
The following features are on the short list of work to do:
* Automatic rejected file cleanup
* Automatic submission file cleanup
* Automatic deleted file cleanup
* Automatic index generation
* MD5 hash creation
* Submitter email notification
* Enhanced processing state persistence

The following are advanced features that could be implemented:
* Optional external FTP server data storage
* AWS S3 submission support
* AWS S3 data dissemination

## Running Argonaut

### Installation

Argonaut requires a Java Runtime Environment (JRE) of 21 or higher.  JRE downloads are available from Oracle https://www.oracle.com/java/technologies/downloads/.
There are also many alternative options for Java Development Kits (JDK), which include a JRE.  One alternative is Temurin, https://adoptium.net/temurin/releases/.
Follow the installation instructions for whichever option you choose.

Download the Argonaut installation for your desired version from https://github.com/CI-CMG/argonaut/releases.  Each release will include 
several files, pick one of these:
* argonaut-service-\<version>.zip (recommended) - A zip archive containing the Argonaut application, configuration templates, and start / stop scripts.
* argonaut-service-\<version>.tar.gz - A gzipped tarball containing the Argonaut application, configuration templates, and start / stop scripts.
* argonaut-service-\<version>-exe.jar - Only the Argonaut Java (Spring Boot) application without any supporting helper files.

If using the zip file, extract the archive to a location of your choosing:
```bash
unzip argonaut-service-<version>.zip
```
or
```bash
tar -xvf argonaut-service-<version>.tar.gz
```

It is recommended, but not required, to set the JAVA_HOME environment variable to
point to the location of your JRE installation.  If JAVA_HOME is not set, Argonaut
will use whichever version of the java executable is on your PATH.

That is it for the basic installation.  

#### Advanced Installation Notes
TODO


## Usage
TODO

## Development Resources
TODO

### Development Environment Setup
TODO

### Components
TODO