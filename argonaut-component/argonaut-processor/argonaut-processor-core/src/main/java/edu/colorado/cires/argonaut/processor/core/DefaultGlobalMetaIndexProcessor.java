package edu.colorado.cires.argonaut.processor.core;

import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord;
import edu.colorado.cires.argonaut.metadata.core.IndexPageRequest;
import edu.colorado.cires.argonaut.metadata.core.MetadataRecordPage;
import edu.colorado.cires.argonaut.metadata.core.MetadataStore;
import org.apache.commons.csv.CSVPrinter;

import java.io.IOException;

public class DefaultGlobalMetaIndexProcessor extends BaseIndexProcessor {

  public DefaultGlobalMetaIndexProcessor() {
    super(
        "Metadata directory file of the Argo Global Data Assembly Center",
        "The directory file describes all metadata files of the argo GDAC data store.",
        "ar_index_global_meta"
    );
  }

  @Override
  protected MetadataRecordPage queryPage(MetadataStore metadataStore, IndexPageRequest indexPageRequest) {
    return metadataStore.getGlobalMetaIndexPage(indexPageRequest);
  }

  @Override
  protected void writeColumnHeaders(CSVPrinter printer) throws IOException {
    printer.printRecord(
        "file",
        "profiler_type",
        "institution",
        "date_update"
    );
  }

  @Override
  protected void writeRecord(CSVPrinter printer, MetadataRecord record) throws IOException {
    printer.printRecord(
        record.getFile(),
        record.getProfilerType(),
        record.getInstitution(),
        formatDate(record.getDateUpdate()));
  }

}
