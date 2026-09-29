package edu.colorado.cires.argonaut.processor.core;

import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord;
import edu.colorado.cires.argonaut.metadata.core.IndexPageRequest;
import edu.colorado.cires.argonaut.metadata.core.MetadataRecordPage;
import edu.colorado.cires.argonaut.metadata.core.MetadataStore;
import java.io.IOException;
import org.apache.commons.csv.CSVPrinter;

public class DefaultBioProfileIndexProcessor extends BaseIndexProcessor {

  public DefaultBioProfileIndexProcessor() {
    super(
        "Bio-Profile directory file of the Argo Global Data Assembly Center",
        "The directory file describes all individual bio-profile files of the ARGO GDAC data store.",
        "argo_bio-profile_index");
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
        "parameters",
        "parameter_data_mode",
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
        String.join(" ", record.getParameters()),
        record.getParameterDataMode(),
        formatDate(record.getDateUpdate()));
  }

  @Override
  protected MetadataRecordPage queryPage(MetadataStore metadataStore, IndexPageRequest indexPageRequest) {
    return metadataStore.getBioProfileIndexPage(indexPageRequest);
  }

}
