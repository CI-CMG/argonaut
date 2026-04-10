package edu.colorado.cires.argonaut.metadata.core;

import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class DefaultMetadataRecordPage implements MetadataRecordPage {

  public static Builder builder() {
    return new Builder();
  }

  public static Builder builder(MetadataRecordPage src) {
    return new Builder(src);
  }


  public static class Builder {

    private int pageNumber = 1;
    private int pageSize = 200;
    private long totalRecords;
    private List<MetadataRecord> page = Collections.emptyList();

    private Builder() {

    }

    private Builder(MetadataRecordPage src) {
      withPageNumber(src.getPageNumber());
      withPageSize(src.getPageSize());
      withTotalRecords(src.getTotalRecords());
      withPage(src.getPage());
    }

    public Builder withPageNumber(int pageNumber) {
      this.pageNumber = pageNumber;
      return this;
    }

    public Builder withPageSize(int pageSize) {
      this.pageSize = pageSize;
      return this;
    }

    public Builder withTotalRecords(long totalRecords) {
      this.totalRecords = totalRecords;
      return this;
    }

    public Builder withPage(List<MetadataRecord> page) {
      if (page == null) {
        this.page = Collections.emptyList();
      } else {
        this.page = Collections.unmodifiableList(new ArrayList<>(page));
      }
      return this;
    }

    public DefaultMetadataRecordPage build() {
      return new DefaultMetadataRecordPage(pageNumber, pageSize, totalRecords, page);
    }
  }

  private final int pageNumber;
  private final int pageSize;
  private final int totalPages;
  private final long totalRecords;
  private final List<MetadataRecord> page;

  private DefaultMetadataRecordPage(int pageNumber, int pageSize, long totalRecords, List<MetadataRecord> page) {
    this.pageNumber = pageNumber;
    this.pageSize = pageSize;
    this.totalRecords = totalRecords;
    this.page = page;
    totalPages = (int) Math.ceil((double) totalRecords / (double) pageSize);
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
  public int getTotalPages() {
    return totalPages;
  }

  @Override
  public long getTotalRecords() {
    return totalRecords;
  }

  @Override
  public List<MetadataRecord> getPage() {
    return page;
  }

  @Override
  public Optional<IndexPageRequest> getNextPage() {
    int nextPageNumber = pageNumber + 1;
    if (nextPageNumber > totalPages) {
      return Optional.empty();
    }
    return Optional.of(builder(this)
        .withPage(Collections.emptyList())
        .withPageNumber(nextPageNumber)
        .build());
  }

  @Override
  public String toString() {
    return "JpaMetadataRecordPage{" +
        "pageNumber=" + pageNumber +
        ", pageSize=" + pageSize +
        ", totalPages=" + totalPages +
        ", totalRecords=" + totalRecords +
        ", page=" + page +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DefaultMetadataRecordPage that = (DefaultMetadataRecordPage) o;
    return pageNumber == that.pageNumber && pageSize == that.pageSize && totalPages == that.totalPages && totalRecords == that.totalRecords
        && Objects.equals(page, that.page);
  }

  @Override
  public int hashCode() {
    return Objects.hash(pageNumber, pageSize, totalPages, totalRecords, page);
  }
}
