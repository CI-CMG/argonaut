package edu.colorado.cires.argonaut.processor;

import edu.colorado.cires.argonaut.jpa.ArgonautOutputFileEntity;
import edu.colorado.cires.argonaut.message.FloatMergeGroup;
import edu.colorado.cires.argonaut.repository.ArgonautOutputFileRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(readOnly = true)
public class FloatMergeAggregatorProcessor implements Processor {

  private final ArgonautOutputFileRepository repository;

  @Autowired
  public FloatMergeAggregatorProcessor(ArgonautOutputFileRepository repository) {
    this.repository = repository;
  }

  @Override
  public void process(Exchange exchange) throws Exception {
    List<ArgonautOutputFileEntity> entities = repository.findAllByFloatMergedFalseAndProfileTrue();
    Set<FloatMergeGroup> groups = entities.stream()
        .map(e -> FloatMergeGroup.builder().withDac(e.getDac()).withFloatId(e.getFloatId()).build())
        .collect(Collectors.toSet());
    exchange.getIn().setBody(new ArrayList<>(groups));
  }

}
