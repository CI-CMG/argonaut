package edu.colorado.cires.argonaut;

import edu.colorado.cires.cmg.shellexecutor.DefaultShellExecutor;
import edu.colorado.cires.cmg.shellexecutor.ShellExecutor;
import java.io.IOException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AomlProcessor implements Processor {

  private final ShellExecutor shellExecutor = new DefaultShellExecutor();
  private final ServiceProperties serviceProperties;
  private final Path exeJar;

  @Autowired
  public AomlProcessor(ServiceProperties serviceProperties) {
    this.serviceProperties = serviceProperties;
    Path workingDir = Paths.get(serviceProperties.getWorkDirectory());
    ClassLoader classLoader = getClass().getClassLoader();
    URL resource = classLoader.getResource("file_checker_exec.jar");

    if (resource == null) {
      throw new IllegalArgumentException("file is not found!");
    }

    try {
      Files.createDirectories(workingDir);
    } catch (IOException e) {
      throw new RuntimeException("Unable to create " + workingDir, e);
    }

    exeJar = workingDir.resolve("file_checker_exec.jar");

    try {
      IOUtils.copy(resource, exeJar.toFile());
    } catch (IOException e) {
      throw new RuntimeException("Unable to copy file checker", e);
    }
  }

  @Override
  public void process(Exchange exchange) throws Exception {
//    File file = exchange.getIn().getBody(File.class);

    String separator = FileSystems.getDefault().getSeparator();
    String classpath = System.getProperty("java.class.path");
    String path = System.getProperty("java.home") + separator + "bin" + separator + "java";
    try {
      long timeout = 120000L;

      Consumer<String> logger = System.out::println;

      int exitCode = shellExecutor.execute(Paths.get("/Users/cslater/projects/argo-pipeline"), logger, timeout,
          "java",
          "-jar",
          exeJar.toAbsolutePath().toString(),
          "aoml",
          "/Users/cslater/third-party-projects/ArgoFormatChecker/file_checker_spec",
          "/Users/cslater/projects/argo-pipeline/out",
          "/Users/cslater/projects/argo-pipeline/sample_files/aoml_dac/uncompressed");
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException(e);
    }
  }
}
