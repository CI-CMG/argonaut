package edu.colorado.cires.argonaut.config;

import java.nio.file.Path;

/**
 * Subset of Argonaut configuration related to file paths.
 */
public interface ArgonautDirectoryConfig {

  /**
   * Returns the path to the directory used for temporary storage during processing.
   * @return the path to the directory used for temporary storage during processing
   */
  Path getWorkDirectory();

  /**
   * Returns the path to the directory used for data submission and submission reports.
   * @return the the path to the directory used for data submission and submission reports
   */
  Path getSubmissionDirectory();

  /**
   * Returns the path to the directory used for validated data output and related GDAC files.
   * @return the path to the directory used for validated data output and related GDAC files
   */
  Path getOutputDirectory();

}
