package edu.colorado.cires.argonaut.processor;

import edu.colorado.cires.argonaut.jpa.ArgonautOutputFileEntity;
import edu.colorado.cires.argonaut.repository.ArgonautOutputFileRepository;
import java.util.stream.Stream;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

//@Component
//@Transactional(readOnly = true)
public class IndexProcessor implements Processor {

  private final ArgonautOutputFileRepository repository;

  @Autowired
  public IndexProcessor(ArgonautOutputFileRepository repository) {
    this.repository = repository;
  }

  private void writeProfileFile(Page<ArgonautOutputFileEntity> page){

  }

  @Override
  public void process(Exchange exchange) throws Exception {
//    Specification<ArgonautOutputFileEntity> spec; // = getSpecs(searchParameters).stream().reduce(null, ServiceUtils::and);
    Sort sort = Sort.by(Order.asc("dac"), Order.asc("floatId"), Order.asc("fileName"));
    try(Stream<ArgonautOutputFileEntity> entityStream = repository.findAllByFileType("profile", sort)) {

    }
//    int maxPerPage = 4000;
//    int pageIndex = 0;
//    Page<ArgonautOutputFileEntity> page = repository.findAll(spec, PageRequest.of(pageIndex, maxPerPage, sort));
//    writeToFile(page);
//    while (page.hasNext()){
//      repository.findAll(spec, PageRequest.of(pageIndex, maxPerPage, sort));
//    }
//    do {
//
//    } while (page.hasNext());
//    repository.findAll
  }
}
