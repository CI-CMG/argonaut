package edu.colorado.cires.argonaut.messaging.cameljpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import org.apache.camel.EndpointInject;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringTest;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;

@CamelSpringTest
@ContextConfiguration({"DlqTest.xml"})
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class DlqTest {

  @EndpointInject("mock:success")
  private MockEndpoint success;

  @EndpointInject("mock:error")
  private MockEndpoint error;

  @Autowired
  private EntityManagerFactory entityManagerFactory;

  @BeforeEach
  @AfterEach
  public void setup() throws Exception {
    try (Stream<Path> stream = Files.list(Paths.get("target/submit"))) {
      stream.map(Path::toFile).forEach(FileUtils::deleteQuietly);
    }
    try (EntityManager em = entityManagerFactory.createEntityManager()) {
      EntityTransaction tx = em.getTransaction();
      tx.begin();
      try {
        em.createQuery("delete from ArgonautMessageEntity").executeUpdate();
        tx.commit();
      } catch (Exception e) {
        tx.rollback();
        throw e;
      }
    }
  }

  @Test
  public void test() throws Exception {
    success.expectedMessageCount(1);
    error.expectedMessageCount(2);
    Files.createFile(Paths.get("target/submit/file1.txt"));
    Files.createFile(Paths.get("target/submit/file2.txt"));
    Files.createFile(Paths.get("target/submit/file3.txt"));
    MockEndpoint.assertIsSatisfied(10, TimeUnit.SECONDS, success, error);
  }

}
