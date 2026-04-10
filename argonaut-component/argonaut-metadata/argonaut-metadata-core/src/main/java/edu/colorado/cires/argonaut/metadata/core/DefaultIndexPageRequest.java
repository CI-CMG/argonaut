package edu.colorado.cires.argonaut.metadata.core;

import edu.colorado.cires.argonaut.messaging.core.databind.NcSubmissionMessage.FileType;
import java.util.Objects;
import java.util.Optional;

public class DefaultIndexPageRequest implements IndexPageRequest {

  public static Builder builder() {
    return new Builder();
  }

  public static Builder builder(IndexPageRequest src) {
    return new Builder(src);
  }


  public static class Builder {

    private int pageNumber = 1;
    private int pageSize = 200;
    private String dac;
    private String floatId;
    private FileType fileType;

    private Builder() {

    }

    private Builder(IndexPageRequest src) {
      withPageNumber(src.getPageNumber());
      withPageSize(src.getPageSize());
    }

    public Builder withPageNumber(int pageNumber) {
      this.pageNumber = pageNumber;
      return this;
    }

    public Builder withPageSize(int pageSize) {
      this.pageSize = pageSize;
      return this;
    }

    public Builder withSearchDacEquals(String dac) {
      this.dac = dac;
      return this;
    }

    public Builder withSearchFloatIdEquals(String floatId) {
      this.floatId = floatId;
      return this;
    }

    public Builder withSearchFileTypeEquals(FileType fileType) {
      this.fileType = fileType;
      return this;
    }

    public DefaultIndexPageRequest build() {
      return new DefaultIndexPageRequest(pageNumber, pageSize, dac, floatId, fileType);
    }

  }

  private final int pageNumber;
  private final int pageSize;
  private final String dac;
  private final String floatId;
  private final FileType fileType;

  private DefaultIndexPageRequest(int pageNumber, int pageSize, String dac, String floatId, FileType fileType) {
    this.pageNumber = pageNumber;
    this.pageSize = pageSize;
    this.dac = dac;
    this.floatId = floatId;
    this.fileType = fileType;
  }


  @Override
  public int getPageNumber() {
    return pageNumber;
  }

  @Override
  public int getPageSize() {
    return pageSize;
  }

  @Override
  public Optional<String> getSearchDacEquals() {
    return Optional.ofNullable(dac);
  }

  @Override
  public Optional<String> getSearchFloatIdEquals() {
    return Optional.ofNullable(floatId);
  }

  @Override
  public Optional<FileType> getSearchFileTypeEquals() {
    return Optional.ofNullable(fileType);
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DefaultIndexPageRequest that = (DefaultIndexPageRequest) o;
    return pageNumber == that.pageNumber && pageSize == that.pageSize && Objects.equals(dac, that.dac) && Objects.equals(floatId,
        that.floatId) && Objects.equals(fileType, that.fileType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(pageNumber, pageSize, dac, floatId, fileType);
  }

  @Override
  public String toString() {
    return "DefaultIndexPageRequest{" +
        "pageNumber=" + pageNumber +
        ", pageSize=" + pageSize +
        ", dac='" + dac + '\'' +
        ", floatId='" + floatId + '\'' +
        ", fileType='" + fileType + '\'' +
        '}';
  }

}
