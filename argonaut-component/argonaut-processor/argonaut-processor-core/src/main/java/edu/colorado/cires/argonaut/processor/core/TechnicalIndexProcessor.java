package edu.colorado.cires.argonaut.processor.core;

import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord;
import edu.colorado.cires.argonaut.metadata.core.IndexPageRequest;
import edu.colorado.cires.argonaut.metadata.core.MetadataRecordPage;
import edu.colorado.cires.argonaut.metadata.core.MetadataStore;
import java.io.IOException;
import org.apache.commons.csv.CSVPrinter;

public class TechnicalIndexProcessor extends BaseIndexProcessor {

  public TechnicalIndexProcessor() {
    super(
      "Technical directory file of the Argo Global Data Assembly Center",
      "The directory file describes all technical files of the argo ARGO GDAC data store.",
      "ar_index_global_tech"
    );
  }

  @Override
  protected MetadataRecordPage queryPage(MetadataStore metadataStore,
    IndexPageRequest indexPageRequest) {
    return metadataStore.getTechnicalIndexPage(indexPageRequest);
  }

  @Override
  protected void writeColumnHeaders(CSVPrinter printer) throws IOException {
    printer.printRecord("file", "institution", "date_update");
  }

  @Override
  protected void writeRecord(CSVPrinter printer, MetadataRecord record) throws IOException {
    printer.printRecord(record.getFile(), record.getInstitution(), formatDate(record.getDateUpdate()));
  }
}
