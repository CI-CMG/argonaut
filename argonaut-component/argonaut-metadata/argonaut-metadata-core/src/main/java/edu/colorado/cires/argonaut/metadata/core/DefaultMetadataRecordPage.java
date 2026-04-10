package edu.colorado.cires.argonaut.metadata.core;

import edu.colorado.cires.argonaut.messaging.core.databind.MetadataRecord;
import edu.colorado.cires.argonaut.messaging.core.databind.NcSubmissionMessage.FileType;
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

    private IndexPageRequest pageRequest;
    private long totalRecords;
    private List<MetadataRecord> page = Collections.emptyList();

    private Builder() {

    }

    private Builder(MetadataRecordPage src) {
      withIndexPageRequest(DefaultIndexPageRequest.builder(src).build());
      withTotalRecords(src.getTotalRecords());
      withPage(src.getPage());
    }

    public Builder withIndexPageRequest(IndexPageRequest pageRequest) {
      this.pageRequest = pageRequest;
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
      return new DefaultMetadataRecordPage(pageRequest, totalRecords, page);
    }
  }

  private final IndexPageRequest pageRequest;
  private final int totalPages;
  private final long totalRecords;
  private final List<MetadataRecord> page;

  private DefaultMetadataRecordPage(IndexPageRequest pageRequest, long totalRecords, List<MetadataRecord> page) {
    this.pageRequest = pageRequest;
    this.totalRecords = totalRecords;
    this.page = page;
    totalPages = (int) Math.ceil((double) totalRecords / (double) pageRequest.getPageSize());
  }


  @Override
  public int getPageNumber() {
    return pageRequest.getPageNumber();
  }

  @Override
  public int getPageSize() {
    return pageRequest.getPageSize();
  }

  @Override
  public Optional<String> getSearchDacEquals() {
    return pageRequest.getSearchDacEquals();
  }

  @Override
  public Optional<String> getSearchFloatIdEquals() {
    return pageRequest.getSearchFloatIdEquals();
  }

  @Override
  public Optional<FileType> getSearchFileTypeEquals() {
    return pageRequest.getSearchFileTypeEquals();
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
    int nextPageNumber = pageRequest.getPageNumber() + 1;
    if (nextPageNumber > totalPages) {
      return Optional.empty();
    }
    return Optional.of(builder(this)
        .withPage(Collections.emptyList())
        .withIndexPageRequest(DefaultIndexPageRequest.builder(this).withPageNumber(nextPageNumber).build())
        .build());
  }

  @Override
  public String toString() {
    return "DefaultMetadataRecordPage{" +
        "pageRequest=" + pageRequest +
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
    return totalPages == that.totalPages && totalRecords == that.totalRecords && Objects.equals(pageRequest, that.pageRequest)
        && Objects.equals(page, that.page);
  }

  @Override
  public int hashCode() {
    return Objects.hash(pageRequest, totalPages, totalRecords, page);
  }
}
