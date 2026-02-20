package edu.colorado.cires.argonaut.web;

import edu.colorado.cires.argonaut.config.ServiceProperties;
import edu.colorado.cires.argonaut.util.ArgonautFileUtils;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.HandlerMapping;

@Controller
@ConditionalOnWebApplication
public class FileServerController {

  private final ServiceProperties serviceProperties;
  private final String servletContextPath;

  @Autowired
  public FileServerController(ServiceProperties serviceProperties, @Value("#{servletContext.contextPath}") String servletContextPath) {
    this.serviceProperties = serviceProperties;
    this.servletContextPath = "/" + servletContextPath.replaceAll("/", "");
    ArgonautFileUtils.createDirectories(this.serviceProperties.getOutputDirectory());
  }

  private static boolean isHidden(Path path) {
    try {
      return Files.isHidden(path);
    } catch (IOException e) {
      throw new IllegalStateException("Unable to check hidden status: " + path);
    }
  }

  @GetMapping({"/", "", "/**/{path:^[^.]+$}", "/{path:^[^.]+$}", "/**/{path:^[^.]+$}/", "/{path:^[^.]+$}/"})
  public String index(Model model, HttpServletRequest request) {
    final String fullPath = ((String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE)).replaceAll("^/+", "")
        .replaceAll("/+$", "");
    boolean hasParent = !fullPath.isEmpty();
    Path path = hasParent ? serviceProperties.getOutputDirectory().resolve(fullPath) : serviceProperties.getOutputDirectory();
    if (!Files.exists(path) || isHidden(path)) {
      //TODO return 404
      throw new IllegalStateException("File not found: " + path);
    }

    if (!Files.isDirectory(path)) {
      throw new IllegalStateException("File is not a directory: " + path);
    }

    List<FileInfo> files = new ArrayList<>();
    if (hasParent) {
      String[] parts = fullPath.split("/");
      String parentPath = String.join("/", Arrays.asList(parts).subList(0, parts.length - 1));
      files.add(new FileInfo("Parent Directory", true, "", "-", true, parentPath));
    }
    try (Stream<Path> stream = Files.list(path)) {
      stream
          .filter(file -> !isHidden(file))
          .map(file -> {
            try {
              boolean isDirectory = Files.isDirectory(file);
              String name = file.getFileName().toString().replaceAll("^/+", "");
              if (isDirectory) {
                name = name + "/";
              }
              return new FileInfo(
                  name,
                  isDirectory,
                  Files.getLastModifiedTime(file).toInstant().toString(),
                  isDirectory ? "-" : FileUtils.byteCountToDisplaySize(Files.size(file)),
                  false,
                  hasParent ? fullPath + "/" + name : name
              );
            } catch (IOException e) {
              throw new RuntimeException("Unable to read file attributes: " + file, e);
            }
          }).sorted()
          .forEach(files::add);
    } catch (IOException e) {
      throw new RuntimeException("Unable to list files for: " + path, e);
    }
    model.addAttribute("path", fullPath);
    model.addAttribute("servletContextPath", servletContextPath.equals("/") ? "" : servletContextPath);
    model.addAttribute("files", files);
    return "index";
  }


}
