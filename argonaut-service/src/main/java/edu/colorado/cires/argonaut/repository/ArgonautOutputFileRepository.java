package edu.colorado.cires.argonaut.repository;

import edu.colorado.cires.argonaut.jpa.ArgonautOutputFileEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArgonautOutputFileRepository extends JpaRepository<ArgonautOutputFileEntity, Long> {

  List<ArgonautOutputFileEntity> findAllByFloatMergedFalseAndFileType(String fileType);
  Optional<ArgonautOutputFileEntity> findByDacAndFloatIdAndFileType(String dac, String floatId, String fileType);
  Optional<ArgonautOutputFileEntity> findByDacAndFloatIdAndFileName(String dac, String floatId, String filename);
}
