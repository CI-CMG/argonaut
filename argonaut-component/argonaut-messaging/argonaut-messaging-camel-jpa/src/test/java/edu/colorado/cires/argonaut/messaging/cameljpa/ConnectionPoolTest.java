package edu.colorado.cires.argonaut.messaging.cameljpa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord.FileStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.TreeSet;
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
@ContextConfiguration({"ConnectionPoolTest.xml"})
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class ConnectionPoolTest {

  @EndpointInject("mock:mock")
  private MockEndpoint mockEndpoint;

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
    mockEndpoint.expectedMessageCount(10);
    Files.createFile(Paths.get("target/submit/file1.txt"));
    Files.createFile(Paths.get("target/submit/file2.txt"));
    Files.createFile(Paths.get("target/submit/file3.txt"));
    Files.createFile(Paths.get("target/submit/file4.txt"));
    Files.createFile(Paths.get("target/submit/file5.txt"));
    MockEndpoint.assertIsSatisfied(10, TimeUnit.SECONDS, mockEndpoint);
    assertEquals(
        new TreeSet<>(Arrays.asList("file1", "file2", "file3", "file4", "file5", "txt")),
        new TreeSet<>(mockEndpoint.getExchanges().stream().map(ex -> ex.getIn().getBody(String.class)).toList())
    );
  }

}
