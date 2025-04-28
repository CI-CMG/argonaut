package edu.colorado.cires.argonaut;

import java.nio.file.Path;


public class FileCheckPair {

  private Path ncFile;
  private Path fileCheckFile;

  public Path getNcFile() {
    return ncFile;
  }

  public void setNcFile(Path ncFile) {
    this.ncFile = ncFile;
  }

  public Path getFileCheckFile() {
    return fileCheckFile;
  }

  public void setFileCheckFile(Path fileCheckFile) {
    this.fileCheckFile = fileCheckFile;
  }

  public boolean isComplete(){
    return ncFile != null && fileCheckFile != null;
  }

  public Path getExistingFile() {
    if (ncFile != null){
      return ncFile;
    }
    return fileCheckFile;
  }
}

//public class FileCheckTriplet {
//  public static class FileCheckPair{
//    private Path ncFile;
//    private Path fileCheckFile;
//    private String error;
//
//    public Path getNcFile() {
//      return ncFile;
//    }
//
//    public Path getFileCheckFile() {
//      return fileCheckFile;
//    }
//
//    public void setNcFile(Path ncFile) {
//      this.ncFile = ncFile;
//    }
//
//    public void setFileCheckFile(Path fileCheckFile) {
//      this.fileCheckFile = fileCheckFile;
//    }
//    public boolean isComplete(){
//      return ncFile != null && fileCheckFile != null;
//    }
//
//    public String getError() {
//      return error;
//    // TODO - need better error message
////      return "ErrorMessage{" +
////          "ncFile=" + ncFile +
////          ", fileCheckXmlFile=" + fileCheckFile +
////          '}';
//    }
//
//    public void setError(String error) {
//      this.error = error;
//    }
//  }
//  private FileCheckPair meta;
//  private FileCheckPair rTraj;
//  private FileCheckPair tech;
//  private FileCheckPair profile;
//
//  public boolean isComplete(){
//    return meta != null && meta.isComplete() && rTraj != null && rTraj.isComplete() && tech !=null && tech.isComplete();
//  }
//
//  public FileCheckPair getMeta() {
//    return meta;
//  }
//
//  public FileCheckPair getrTraj() {
//    return rTraj;
//  }
//
//  public FileCheckPair getTech() {
//    return tech;
//  }
//
//  public FileCheckPair getProfile() {
//    return profile;
//  }
//
//  public void setMeta(FileCheckPair meta) {
//    this.meta = meta;
//  }
//
//  public void setrTraj(FileCheckPair rTraj) {
//    this.rTraj = rTraj;
//  }
//
//  public void setTech(FileCheckPair tech) {
//    this.tech = tech;
//  }
//
//  public void setProfile(FileCheckPair profile) {
//    this.profile = profile;
//  }
//
//  public List<String> getErrors(){
//    List<String> errors = new ArrayList<>();
//    if (profile != null){
//      errors.add(profile.getError());
//    } else{
//      errors.add("metaError: " + meta.getError());
//      errors.add("techError: " + tech.getError());
//      errors.add("rTrajError: " + rTraj.getError());
//    }
//    return errors;
//  }
//}
