package edu.colorado.cires.argonaut.processor.core;

import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord;
import edu.colorado.cires.argonaut.metadata.core.IndexPageRequest;
import edu.colorado.cires.argonaut.metadata.core.MetadataRecordPage;
import edu.colorado.cires.argonaut.metadata.core.MetadataStore;
import java.io.IOException;
import org.apache.commons.csv.CSVPrinter;

public class DefaultCoreProfileIndexProcessor extends BaseIndexProcessor {

  public DefaultCoreProfileIndexProcessor() {
    super(
        "Profile directory file of the Argo Global Data Assembly Center",
        "The directory file describes all individual profile files of the argo GDAC data store.",
        "ar_index_global_prof");
  }

  @Override
  protected MetadataRecordPage queryPage(MetadataStore metadataStore, IndexPageRequest indexPageRequest) {
    return metadataStore.getCoreProfileIndexPage(indexPageRequest);
  }

  @Override
  protected void writeColumnHeaders(CSVPrinter printer) throws IOException {
    printer.printRecord(
        "file",
        "date",
        "latitude",
        "longitude",
        "ocean",
        "profiler_type",
        "institution",
        "date_update");
  }

  @Override
  protected void writeRecord(CSVPrinter printer, MetadataRecord record) throws IOException {
    printer.printRecord(
        record.getFile(),
        formatDate(record.getDate()),
        formatLatLon(record.getLatitude()),
        formatLatLon(record.getLongitude()),
        record.getOcean() == null ? null : record.getOcean().getCode(),
        record.getProfilerType(),
        record.getInstitution(),
        formatDate(record.getDateUpdate()));
  }

}
