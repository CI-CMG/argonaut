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
Argonaut can be run as a Linux service.  For this, it is recommended to run Argonaut with a dedicated user.

To create a user run:
```bash
useradd argonaut
```
You may also want to increase the number of file descriptors for that user.

A service setup script is provided to simplify the creation process.
1. Navigate to the _svc_ directory in the installation location
2. Edit _install-service.properties_ and set the _USER_and _JAVA_HOME_ properties
3. Run _install-service.sh_ as root

## Configuration
### Application Properties
For a list of configuration properties, see [CONFIGURATION](argonaut-service/docs/CONFIGURATION.md)

### Setting Properties Via Environment Variables
All configuration properties can be set by environment variables.  These will take precedence over values specified 
in the configuration file.  Typically, all dots (periods) are replaced by underscores and all dashes are omitted. 
Upper case is preferred.  More details can be found in the [Spring Boot Externalized Configuration Documentation](https://docs.spring.io/spring-boot/reference/features/external-config.html#features.external-config.typesafe-configuration-properties.relaxed-binding.environment-variables).

### JVM Options
All JVM options passed to the application are located in _config/jvm.options_.  Lines starting with "#" are comments and will be ignored.
Sensible defaults have been selected.  The service will need to be restarted for changes to take effect.

### Logging
Logging is configured by the _config/log4j2.xml_ file.  Detailed configuration instructions can be found here: https://logging.apache.org/log4j/2.x/manual/configuration.html.

The most common changes would be adding loggers and changing levels though.

To add a logger add the following to the _<Loggers>_ section:
```xml
    <Logger name="com.foo.bar" level="info" additivity="false">
      <AppenderRef ref="File"/>
    </Logger>
```
The name will be a package name (full or partial) or a class name.  Level will be one of "fatal", "error", "warn", "info", "debug", or "trace".

To change the level of all loggers not explicitly set, update the level of the following entry in the file:
```xml
    <Root level="warn">
      <AppenderRef ref="File"/>
    </Root>
``` 

The _${sys:svc.home}_ placeholder can be used in the logging configuration and represents the absolute path to the service install location.

Changes made to this file do not require a restart.  They will be picked up within 30 seconds.

## Usage
To run the Argonaut (not as a Linux service), run _run.sh_ in the installation directory.  This will run Argonaut in the foreground.
To run Argonaut in the background, run _start-background.sh_.  To stop the background application, run _stop-background.sh_.


TODO

## Development Resources
TODO

### Development Environment Setup
TODO

### Components
TODO