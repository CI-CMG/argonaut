package edu.colorado.cires.argonaut.metadata.core;

import edu.colorado.cires.argonaut.messaging.core.databind.ArgoFileType;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class DefaultRemovedFileSearch implements RemovedFileSearch {
  public static Builder builder() {
    return new Builder();
  }

  public static Builder builder(RemovedFileSearch src) {
    return new Builder(src);
  }


  public static class Builder {

    private int pageNumber = 1;
    private int pageSize = 200;
    private Instant olderThan;
    private List<ArgoFileType> forFileTypes = Collections.emptyList();

    private Builder() {

    }

    private Builder(RemovedFileSearch src) {
      withPageNumber(src.getPageNumber());
      withPageSize(src.getPageSize());
      withOlderThan(src.getOlderThan());
      withForFileTypes(src.getFileTypes());
    }

    public Builder withPageNumber(int pageNumber) {
      this.pageNumber = pageNumber;
      return this;
    }

    public Builder withPageSize(int pageSize) {
      this.pageSize = pageSize;
      return this;
    }

    public Builder withOlderThan(Instant olderThan) {
      this.olderThan = olderThan;
      return this;
    }

    public Builder withForFileTypes(List<ArgoFileType> forFileTypes) {
      if (forFileTypes == null) {
        this.forFileTypes = Collections.emptyList();
      } else {
        this.forFileTypes = Collections.unmodifiableList(new ArrayList<>(forFileTypes));
      }
      return this;
    }

    public DefaultRemovedFileSearch build() {
      return new DefaultRemovedFileSearch(pageNumber, pageSize, olderThan, forFileTypes);
    }

  }

  private final int pageNumber;
  private final int pageSize;
  private final Instant olderThan;
  private final List<ArgoFileType> forFileTypes;

  private DefaultRemovedFileSearch(int pageNumber, int pageSize, Instant olderThan, List<ArgoFileType> forFileTypes) {
    this.pageNumber = pageNumber;
    this.pageSize = pageSize;
    this.olderThan = olderThan;
    this.forFileTypes = forFileTypes;
  }

  @Override
  public Instant getOlderThan() {
    return olderThan;
  }

  @Override
  public List<ArgoFileType> getFileTypes() {
    return forFileTypes;
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
    DefaultRemovedFileSearch that = (DefaultRemovedFileSearch) o;
    return pageNumber == that.pageNumber && pageSize == that.pageSize && Objects.equals(olderThan, that.olderThan)
        && Objects.equals(forFileTypes, that.forFileTypes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(pageNumber, pageSize, olderThan, forFileTypes);
  }

  @Override
  public String toString() {
    return "DefaultRemovedFileSearch{" +
        "pageNumber=" + pageNumber +
        ", pageSize=" + pageSize +
        ", olderThan=" + olderThan +
        ", forFileTypes=" + forFileTypes +
        '}';
  }
}
