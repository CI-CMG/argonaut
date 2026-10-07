package edu.colorado.cires.argonaut.processor.core;

import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord;
import edu.colorado.cires.argonaut.metadata.core.IndexPageRequest;
import edu.colorado.cires.argonaut.metadata.core.MetadataRecordPage;
import edu.colorado.cires.argonaut.metadata.core.MetadataStore;
import java.io.IOException;
import org.apache.commons.csv.CSVPrinter;

class TrajectoryIndexProcessor extends BaseIndexProcessor {

  protected TrajectoryIndexProcessor() {
    super(
      "Trajectory directory file of the Argo Global Data Assembly Center",
      "The directory file describes all trajectory files of the ARGO GDAC data store.",
      "ar_index_global_traj"
    );
  }

  @Override
  protected MetadataRecordPage queryPage(MetadataStore metadataStore,
    IndexPageRequest indexPageRequest) {
    return metadataStore.getTrajectoryIndexPage(indexPageRequest);
  }

  @Override
  protected void writeColumnHeaders(CSVPrinter printer) throws IOException {
    printer.printRecord(
      "file",
      "latitude_max",
      "latitude_min",
      "longitude_max",
      "longitude_min",
      "profiler_type",
      "institution",
      "date_update"
    );
  }

  @Override
  protected void writeRecord(CSVPrinter printer, MetadataRecord record) throws IOException {
    printer.printRecord(
      record.getFile(),
      formatLatLon(record.getLatitudeMax()),
      formatLatLon(record.getLatitudeMin()),
      formatLatLon(record.getLongitudeMax()),
      formatLatLon(record.getLongitudeMin()),
      record.getProfilerType(),
      record.getInstitution(),
      formatDate(record.getDateUpdate())
    );
  }
}
