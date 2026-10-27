package edu.colorado.cires.argonaut.core.merge.synthetic;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import ucar.nc2.NetcdfFile;
import ucar.nc2.NetcdfFiles;

class SyntheticProfileMergerImpl implements SyntheticProfileMerger {

  @Override
  public void mergeProfiles(Path cProfilePath, Path bProfilePath, Path metaPath, Path outputPath) throws IOException {
    ResultType resultType = getResultTypeFromFilePresence(cProfilePath, bProfilePath, metaPath)
      .orElse(getResultTypeFromFileContent(cProfilePath, bProfilePath));

    if (ResultType.MERGE.equals(resultType)) {
      MergeUtils.createMergedSProfile(cProfilePath, bProfilePath, metaPath, outputPath);
    } else {
      throw new IllegalStateException("Synthetic profile cannot be created from unsupported file state: %s".formatted(ResultType.BIO.equals(resultType) ? "metadata and BGC data exist" : "metadata and CTD data exist"));
    }
  }

  /**
   * Uses existence of metadata, CTD, and BGC files to determine computation result type
   *  BIO:
   *    1. metadata file exists
   *    2. BGC file exists
   *    3. CTD file does not exist
   *  CORE:
   *    1. metadata file exists
   *    2. CTD file exists
   *    3. BGC file does not exist
   * @param cProfilePath path to CTD file
   * @param bProfilePath path to BGC file
   * @param metaPath path to metadata file
   * @return computation result type
   *  empty if result type could not be determined from file existence
   * @throws IllegalStateException when metadata file does not exist, or metadata file exists but
   * CTD and BGC files do not exist
   */
  private Optional<ResultType> getResultTypeFromFilePresence(Path cProfilePath, Path bProfilePath, Path metaPath) {
    if (!Files.exists(metaPath)) {
      throw new IllegalStateException("%s does not exist, merge cannot be performed".formatted(metaPath));
    }

    boolean cProfileExists = Files.exists(cProfilePath);
    boolean bProfileExists = Files.exists(bProfilePath);
    if (!cProfileExists) {
      if (!bProfileExists) {
        throw new IllegalStateException("%s and %s do not exist, merge cannot be performed".formatted(cProfilePath, bProfilePath));
      }

      return Optional.of(ResultType.BIO);
    }

    if (!bProfileExists) {
      return Optional.of(ResultType.CORE);
    }

    return Optional.empty();
  }

  /**
   * Use CTD and BGC file contents to determine computation result type
   *  MERGE:
   *    1. dimensions of PRES (BGC) and PRES_QC (CTD) should match
   *    2. BGC parameters contain B-parameters (see {@link BioParameterFilter})
   *    3. All B-parameter values should not be fill value
   *    4. All PRES_QC (CTD) values should not be fill value or 4 (worst possible QC value)
   *  CORE:
   *    The above conditions have failed, CTD and GBC are incompatible for merge
   * @param cProfilePath path to CTD file
   * @param bProfilePath path to BGC file
   * @return computation result type
   * @throws IOException if NetCDF files cannot be opened
   */
  private ResultType getResultTypeFromFileContent(Path cProfilePath, Path bProfilePath) throws IOException {
    try (
      NetcdfFile cProfile = NetcdfFiles.open(cProfilePath.toString());
      NetcdfFile bProfile = NetcdfFiles.open(bProfilePath.toString())
    ) {
      return ReadUtils.getVariableSize(cProfile, "PRES_QC")
        .flatMap(qcPresSize -> ReadUtils.getVariableSize(bProfile, "PRES")
          .filter(qcPresSize::equals) // pressure and pressure qc must be of same size to merge
          .map(ignored -> ReadUtils.getVariables(bProfile, BioParameterFilter::isSupportedParameter))
          .filter(parameters -> !parameters.isEmpty()) // BGC data must contain at least one BGC parameter
          .filter(parameters -> {
            boolean containsGoodQcValue = ReadUtils.stream1DVariable(cProfile, "PRES_QC", Character.class) // 'good' QC values should be present (not equal to 4 and not a fill value)
              .anyMatch(character -> character != null && !character.equals('4'));

            boolean containsGoodBgcValue = parameters.stream()
              .flatMap(parameter -> ReadUtils.stream1DVariable(bProfile, parameter, Object.class)) // 'good' BGC parameter values should also be present (not a fill value)
              .anyMatch(Objects::nonNull);

            return containsGoodQcValue && containsGoodBgcValue;
          })
          )
          .map(ignored -> ResultType.MERGE) // BGC and CTD data are compatible for merge
          .orElse(ResultType.CORE); // Revert to CTD only result if incompatible with BGC
    }
  }

  /**
   *  BIO - Empty synthetic profile with locations and times only from metadata and BGC files
   *  CORE - Synthetic profile containing only information from CTD and metadata files
   *  MERGE - Synthetic profile containing information from CTD, BGC, and metadata files
   */
  private enum ResultType {
    BIO, CORE, MERGE
  }
}
