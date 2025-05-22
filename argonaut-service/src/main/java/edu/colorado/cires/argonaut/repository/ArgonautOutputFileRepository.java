package edu.colorado.cires.argonaut.repository;

import edu.colorado.cires.argonaut.jpa.ArgonautOutputFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArgonautOutputFileRepository extends JpaRepository<ArgonautOutputFileEntity, Long> {

}
