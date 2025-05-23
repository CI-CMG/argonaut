package edu.colorado.cires.argonaut.processor;

import edu.colorado.cires.argonaut.jpa.ArgonautOutputFileEntity;
import edu.colorado.cires.argonaut.message.NcSubmissionMessage;
import edu.colorado.cires.argonaut.repository.ArgonautOutputFileRepository;
import java.time.Instant;
import java.util.List;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class FileChangedPersistenceProcessor implements Processor {

  private final ArgonautOutputFileRepository argonautOutputFileRepository;

  @Autowired
  public FileChangedPersistenceProcessor(ArgonautOutputFileRepository argonautOutputFileRepository) {
    this.argonautOutputFileRepository = argonautOutputFileRepository;
  }

  //TODO this is broken, fix me
  private String resolveFileType(NcSubmissionMessage message) {
    return message.isProfile() ? "profile" : "other";
  }

  private void addFile(NcSubmissionMessage message) {
    ArgonautOutputFileEntity entity = new ArgonautOutputFileEntity();
    entity.setDac(message.getDac());
    entity.setFileName(message.getFileName());
    entity.setTimestamp(Instant.parse(message.getTimestamp()));
    entity.setFileType(resolveFileType(message));
    entity.setFloatId(message.getFloatId());
//    entity.setRegion();
    argonautOutputFileRepository.save(entity);
  }

  private void floatMerge(NcSubmissionMessage message) {
    String fileType = resolveFileType(message);
    ArgonautOutputFileEntity mergeEntity = argonautOutputFileRepository.findByDacAndFloatIdAndFileType(message.getDac(), message.getFloatId(), fileType)
        .orElse(new ArgonautOutputFileEntity());
    if (mergeEntity.getId() == null){
      mergeEntity.setDac(message.getDac());
      mergeEntity.setFloatId(message.getFloatId());
      mergeEntity.setFileType(fileType);
      mergeEntity.setFileName(message.getFileName());
    }
    argonautOutputFileRepository.save(mergeEntity);
    mergeEntity.setTimestamp(Instant.parse(message.getTimestamp()));
    List<String> associatedFiles = message.getAssociatedFiles();
    associatedFiles.stream().forEach((fileName) -> {
      ArgonautOutputFileEntity associateEntity = argonautOutputFileRepository.findByDacAndFloatIdAndFileName(message.getDac(), message.getFloatId(), fileName)
          .orElseThrow(() -> new RuntimeException("dac: " + message.getDac()+ "FloatId: "+ message.getFloatId() + "Associated File " + fileName + "entity not found"));
      associateEntity.setFloatMerged(true);
    });

  }

  @Override
  public void process(Exchange exchange) throws Exception {
    NcSubmissionMessage message = exchange.getIn().getBody(NcSubmissionMessage.class);
    switch (message.getOperation()) {
      case ADD:
        addFile(message);
        break;
      case REMOVE:
        break;
      case FLOAT_MERGE:
        break;
      default:
        throw new IllegalArgumentException("Operation not supported: " + message.getOperation());
    }



  }
}
