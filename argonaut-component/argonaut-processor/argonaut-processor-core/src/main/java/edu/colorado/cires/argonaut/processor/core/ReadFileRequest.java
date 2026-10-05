package edu.colorado.cires.argonaut.processor.core;

import edu.colorado.cires.argonaut.messaging.core.databind.NcSubmissionMessage;
import java.nio.file.Path;

public record ReadFileRequest(String file, Path ncFile, NcSubmissionMessage message) {

}
