package edu.colorado.cires.argonaut.processor.core;

import edu.colorado.cires.argonaut.file.core.FileStore;
import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord;
import edu.colorado.cires.argonaut.metadata.core.DefaultIndexPageRequest;
import edu.colorado.cires.argonaut.metadata.core.IndexPageRequest;
import edu.colorado.cires.argonaut.metadata.core.MetadataRecordPage;
import edu.colorado.cires.argonaut.metadata.core.MetadataStore;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Supplier;
import java.util.zip.GZIPOutputStream;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseIndexProcessor implements IndexProcessor {

  private static final Logger LOGGER = LoggerFactory.getLogger(BaseIndexProcessor.class);
  private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");


  private final String title;
  private final String description;
  private final String fileNameBase;

  private String project = "ARGO";
  private String formatVersion = "2.2";
  private String gdacNode;
  private Map<String, String> accessPathDocumentation = new TreeMap<>();
  private MetadataStore metadataStore;
  private FileStore outputFileStore;
  private int pageSize = 2000;
  private Path localTempDir;
  private Supplier<Instant> nowGenerator = () -> Instant.now();
  private boolean enabled = true;

  protected BaseIndexProcessor(String title, String description, String fileNameBase) {
    this.title = title;
    this.description = description;
    this.fileNameBase = fileNameBase;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public void setProject(String project) {
    this.project = project;
  }

  public void setFormatVersion(String formatVersion) {
    this.formatVersion = formatVersion;
  }

  public void setGdacNode(String gdacNode) {
    this.gdacNode = gdacNode;
  }

  public void setAccessPathDocumentation(Map<String, String> accessPathDocumentation) {
    if (accessPathDocumentation == null) {
      this.accessPathDocumentation = new TreeMap<>();
    } else {
      this.accessPathDocumentation = accessPathDocumentation;
    }
  }

  public void setMetadataStore(MetadataStore metadataStore) {
    this.metadataStore = metadataStore;
  }

  public void setOutputFileStore(FileStore outputFileStore) {
    this.outputFileStore = outputFileStore;
  }

  public void setPageSize(int pageSize) {
    this.pageSize = pageSize;
  }

  public void setLocalTempDir(Path localTempDir) {
    this.localTempDir = localTempDir;
    try {
      Files.createDirectories(localTempDir);
    } catch (IOException e) {
      throw new RuntimeException("Unable to create temp directory: " + localTempDir, e);
    }
  }

  public void setNowGenerator(Supplier<Instant> nowGenerator) {
    this.nowGenerator = nowGenerator;
  }

  private Path createTempDirectory() {
    try {
      return Files.createTempDirectory(localTempDir, fileNameBase + "-");
    } catch (IOException e) {
      throw new RuntimeException("Unable to create temp directory", e);
    }
  }

  private void writeHeader(CSVPrinter printer) {
    try {
      printer.printComment("Title : " + title);
      printer.printComment("Description : " + description);
      printer.printComment("Project : " + project);
      printer.printComment("Format version : " + formatVersion);
      printer.printComment("Date of update : " + DATE_TIME_FORMATTER.format(nowGenerator.get().atZone(ZoneId.of("UTC")).toLocalDateTime()));
      for (Map.Entry<String, String> entry : accessPathDocumentation.entrySet()) {
        printer.printComment(entry.getKey() + " : " + entry.getValue());
      }
      printer.printComment("GDAC node : " + gdacNode);
    } catch (IOException e) {
      throw new RuntimeException("Unable to write header comments", e);
    }
  }

  private void writeColumnHeadersInternal(CSVPrinter printer) {
    try {
      writeColumnHeaders(printer);
    } catch (IOException e) {
      throw new RuntimeException("Unable to write column headers", e);
    }
  }

  protected void writeColumnHeaders(CSVPrinter printer) throws IOException {
    printer.printRecord(
        "file",
        "date",
        "latitude",
        "longitude",
        "ocean",
        "profiler_type",
        "institution",
        "parameters",
        "parameter_data_mode",
        "date_update");
  }

  protected static BigDecimal formatLatLon(Double value) {
    return value == null ? null : BigDecimal.valueOf(value).setScale(3, RoundingMode.DOWN);
  }

  protected static String formatDate(Instant value) {
    return value == null ? null : DATE_TIME_FORMATTER.format(value.atZone(ZoneId.of("UTC")).toLocalDateTime());
  }

  private void writePage(CSVPrinter printer, MetadataRecordPage page) {

    for (MetadataRecord record : page.getPage()) {
      try {
        writeRecord(printer, record);
      } catch (IOException e) {
        throw new RuntimeException("Unable to write index row", e);
      }
    }
  }

  protected void writeRecord(CSVPrinter printer, MetadataRecord record) throws IOException {
    printer.printRecord(
        record.getFile(),
        formatDate(record.getDate()),
        formatLatLon(record.getLatitude()),
        formatLatLon(record.getLongitude()),
        record.getOcean() == null ? null : record.getOcean().getCode(),
        record.getProfilerType(),
        record.getInstitution(),
        String.join(" ", record.getParameters()),
        record.getParameterDataMode(),
        formatDate(record.getDateUpdate()));
  }

  protected abstract MetadataRecordPage queryPage(MetadataStore metadataStore, IndexPageRequest indexPageRequest);

  @Override
  public void generateIndex() {
    if (enabled) {
      LOGGER.info("Updating " + fileNameBase);
      String textFileName = fileNameBase + ".txt";
      Path dir = createTempDirectory();
      try {
        Path textFile = dir.resolve(textFileName);
        try (
            Writer writer = Files.newBufferedWriter(textFile, StandardCharsets.UTF_8);
            CSVPrinter printer = new CSVPrinter(writer, CSVFormat.RFC4180.builder().setCommentMarker('#').setTrim(true).get())
        ) {
          writeHeader(printer);
          writeColumnHeadersInternal(printer);
          MetadataRecordPage page = queryPage(metadataStore, DefaultIndexPageRequest.builder().withPageNumber(1).withPageSize(pageSize).build());
          writePage(printer, page);
          Optional<IndexPageRequest> maybeNextPage = page.getNextPage();
          while (maybeNextPage.isPresent()) {
            page = queryPage(metadataStore, DefaultIndexPageRequest.builder(maybeNextPage.get()).build());
            writePage(printer, page);
            maybeNextPage = page.getNextPage();
          }
        } catch (IOException e) {
          throw new RuntimeException("Unable to open index file for writing", e);
        }

        try {
          String path = outputFileStore.appendToPath(outputFileStore.getRoot(), textFileName);
          outputFileStore.uploadLocalFile(textFile, path);
          try (InputStream in = Files.newInputStream(textFile);
              OutputStream out = new GZIPOutputStream(outputFileStore.getOutputStream(path + ".gz"))) {
            IOUtils.copy(in, out);
          }
        } catch (IOException e) {
          throw new RuntimeException("Unable to upload index file", e);
        }

      } finally {
        FileUtils.deleteQuietly(dir.toFile());
      }
    }
  }
}
