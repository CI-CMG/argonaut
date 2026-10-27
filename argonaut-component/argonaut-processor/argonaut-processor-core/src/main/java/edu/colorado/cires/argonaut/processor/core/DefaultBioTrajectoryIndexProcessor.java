package edu.colorado.cires.argonaut.processor.core;

import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord;
import edu.colorado.cires.argonaut.metadata.core.IndexPageRequest;
import edu.colorado.cires.argonaut.metadata.core.MetadataRecordPage;
import edu.colorado.cires.argonaut.metadata.core.MetadataStore;
import org.apache.commons.csv.CSVPrinter;

import java.io.IOException;

public class DefaultBioTrajectoryIndexProcessor extends BaseIndexProcessor {

    public DefaultBioTrajectoryIndexProcessor() {
        super(
                "Bio-Trajectory directory file of the Argo Global Data Assembly Center",
                "The directory file describes all individual bio-trajectory files of the ARGO GDAC data store.",
                "argo_bio-traj_index");
    }

    @Override
    protected MetadataRecordPage queryPage(MetadataStore metadataStore, IndexPageRequest indexPageRequest) {
        return metadataStore.getBioTrajectoryIndexPage(indexPageRequest);
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
                "parameters",
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
                String.join(" ", record.getParameters()),
                formatDate(record.getDateUpdate()));
    }
}
