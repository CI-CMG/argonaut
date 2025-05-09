package edu.colorado.cires.argonaut.service;

import edu.colorado.cires.argonaut.config.ServiceProperties.FtpServerConfig;
import org.apache.ftpserver.ConnectionConfigFactory;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.ssl.SslConfigurationFactory;
import org.apache.ftpserver.usermanager.ClearTextPasswordEncryptor;
import org.apache.ftpserver.usermanager.Md5PasswordEncryptor;
import org.apache.ftpserver.usermanager.PasswordEncryptor;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;
import org.apache.ftpserver.usermanager.Sha1PasswordEncryptor;
import org.apache.ftpserver.usermanager.Sha256PasswordEncryptor;
import org.apache.ftpserver.usermanager.Sha512PasswordEncryptor;

abstract class FtpService {

  private final FtpServer server;

  protected FtpService(FtpServerConfig config) {
    FtpServerFactory serverFactory = new FtpServerFactory();

    if (config.isAnonymousLoginEnabled()) {
      ConnectionConfigFactory connectionConfigFactory = new ConnectionConfigFactory();
      connectionConfigFactory.setAnonymousLoginEnabled(true);
      serverFactory.setConnectionConfig(connectionConfigFactory.createConnectionConfig());
    }

    ListenerFactory factory = new ListenerFactory();

    // set the port of the listener
    factory.setPort(config.getPort());

    if (config.isSslEnabled()) {
      // define SSL configuration
      SslConfigurationFactory ssl = new SslConfigurationFactory();
      ssl.setKeystoreFile(config.getKeystoreFile().toFile());
      ssl.setKeystorePassword(config.getKeystorePassword());

      // set the SSL configuration for the listener
      factory.setSslConfiguration(ssl.createSslConfiguration());
      factory.setImplicitSsl(true);
    }

    // replace the default listener
    serverFactory.addListener("default", factory.createListener());
    PropertiesUserManagerFactory userManagerFactory = new PropertiesUserManagerFactory();
    userManagerFactory.setFile(config.getUserFile().toFile());
    PasswordEncryptor passwordEncryptor;
    switch (config.getPasswordEncoding()) {
      case none:
        passwordEncryptor = new ClearTextPasswordEncryptor();
        break;
      case sha1:
        passwordEncryptor = new Sha1PasswordEncryptor();
        break;
      case sha256:
        passwordEncryptor = new Sha256PasswordEncryptor();
        break;
      case sha512:
        passwordEncryptor = new Sha512PasswordEncryptor();
        break;
      case md5:
        passwordEncryptor = new Md5PasswordEncryptor();
        break;
      default:
        throw new IllegalStateException("Unsupported password encoding " + config.getPasswordEncoding());
    }
    userManagerFactory.setPasswordEncryptor(passwordEncryptor);
    serverFactory.setUserManager(userManagerFactory.createUserManager());

    server = serverFactory.createServer();
  }

  public void start() throws FtpException {
    server.start();
  }

  public void stop() {
    server.stop();
  }

  @Override
  protected final void finalize() throws Throwable {

  }
}
