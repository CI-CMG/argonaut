package edu.colorado.cires.argonaut;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.colorado.cires.argonaut.config.ServiceProperties;
import edu.colorado.cires.argonaut.message.ArgonautObjectMapperFactory;
import edu.colorado.cires.argonaut.submission.SubmissionProcessor;
import java.io.File;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@Configuration
public class Application {

  static {
    System.setProperty("camel.threads.virtual.enabled", "true");
    File svcHome = new ApplicationHome().getDir();
    String path = svcHome.getAbsolutePath();
    System.setProperty("svc.home", path);
  }

  public static void main(String[] args){
    SpringApplication.run(Application.class, args);
  }

  @Bean
  public SubmissionProcessor submissionProcessor(ServiceProperties serviceProperties) {
    return new SubmissionProcessor(serviceProperties);
  }

  @Bean
  public ObjectMapper objectMapper() {
    return ArgonautObjectMapperFactory.create();
  }

}
