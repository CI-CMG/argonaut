package edu.colorado.cires.argonaut.standalone;

import static org.junit.jupiter.api.Assertions.*;

import org.apache.camel.test.spring.junit5.CamelSpringTest;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;

@CamelSpringTest
@ContextConfiguration("application-context.xml")
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class ApplicationTest {

  @Test
  public void firstTest() throws Exception {
    Thread.sleep(2000);
  }
}