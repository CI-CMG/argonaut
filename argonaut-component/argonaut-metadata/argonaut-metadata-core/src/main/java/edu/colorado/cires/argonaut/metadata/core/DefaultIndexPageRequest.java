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

    public DefaultIndexPageRequest build() {
      return new DefaultIndexPageRequest(pageNumber, pageSize);
    }

  }

  private final int pageNumber;
  private final int pageSize;

  private DefaultIndexPageRequest(int pageNumber, int pageSize) {
    this.pageNumber = pageNumber;
    this.pageSize = pageSize;
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
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DefaultIndexPageRequest that = (DefaultIndexPageRequest) o;
    return pageNumber == that.pageNumber && pageSize == that.pageSize;
  }

  @Override
  public int hashCode() {
    return Objects.hash(pageNumber, pageSize);
  }

  @Override
  public String toString() {
    return "DefaultIndexPageRequest{" +
        "pageNumber=" + pageNumber +
        ", pageSize=" + pageSize +
        '}';
  }

}
