package edu.colorado.cires.argonaut.processor.core;

import edu.colorado.cires.argonaut.messaging.core.databind.NcSubmissionMessage;

import java.util.List;

// TODO implement me
public class DefaultValidationProcessor implements ValidationProcessor {

  private List<String> validateXml(String xml) {
    throw new UnsupportedOperationException("not implemented yet");
  }


  @Override
  public NcSubmissionMessage validate(NcSubmissionMessage ncSubmissionMessage) {
    String dac = ncSubmissionMessage.getDac();
    String floatId = ncSubmissionMessage.getFloatId();
    String fileName = ncSubmissionMessage.getFileName();
    boolean isProfile = ncSubmissionMessage.isProfile();
//    Path processingDacDir = ArgonautFileUtils.getProcessingProfileDir(serviceProperties, dac, floatId, isProfile);
//    checkFile(dac, processingDacDir, fileName);
//    Path fileCheckXmlFile = processingDacDir.resolve(fileName + ".filecheck");
    List<String> errors = validateXml("fileCheckXmlFile");
//    FileUtils.deleteQuietly(fileCheckXmlFile.toFile());
    return NcSubmissionMessage.builder(ncSubmissionMessage).withValidationErrors(errors).build();
  }
}
