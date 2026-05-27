# Gazetteer

## Installation
### Basic Installation Overview
1. Extract distribution bundle
1. Configure application properties
1. Install as a systemd service

### Java Installation and Distribution Bundle Extraction

1. Install the Java 21 JRE
1. If running as a non-root user, set create that user ```useradd argonaut```. You may also want to increase the number of file descriptors for that user.
1. If not running as a service, it is recommended to set the JAVA_HOME environment variable
1. Download either the zip or the tar.gz archive from the repository and copy it to the install location
1. Run ```unzip argonaut-XXX.zip```
1. If extracted as the root user, file ownership will be incorrect. Update file permissions with chown.  Ex. ```chown -R user:user service_dir```


### Configure Application
The service can be configured from multiple sources.  The primary two are a properties file and environment variables.  Between these two, environment variables 
have a higher priority and can override any values set in the property file.  This could be useful if it is desired that secrets be kept out of the
properties file.  This guide will cover configuring the application via properties.  If environment variables are desired, this link provides guidance on the
format for the environment variables: https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html


The properties file is located at config/application.properties.  Most of the properties have sensible defaults.  However, some are blank and need to be 
configured:
```properties
# The password used to encrypt the keystore
server.ssl.key-store-password=
# The password used to encrypt the truststore
server.ssl.trust-store-password=
# The JDBC URL
spring.datasource.url=
# The username for the GAZ DB
spring.datasource.username=
# The password for the GAZ DB
spring.datasource.password=
```

The _${svc.home}_ placeholder can be used in properties files or environment variables and represents the absolute path to the service install location.


### Running the Service

To run the service manually, not as a systemd service, execute the _run.sh_ script in the directory where the application was extracted.


### Install As A Service

First, navigate to the svc directory.  Edit _install-service.properties_ and set the _USER_ and _JAVA_HOME_ properties.  Then run _install-service.sh_.

## Additional Configuration
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