package edu.colorado.cires.argonaut.service;

import edu.colorado.cires.argonaut.config.ServiceProperties;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.apache.ftpserver.ftplet.FtpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(value = "argonaut.local-output-ftp-server.enabled", havingValue = "true")
public final class OutputFtpService extends FtpService {

  @Autowired
  public OutputFtpService(ServiceProperties serviceProperties) {
    super(serviceProperties.getLocalOutputFtpServer());
  }

  @PostConstruct
  public void start() throws FtpException {
    super.start();
  }

  @PreDestroy
  public void stop() {
    super.stop();
  }


}
