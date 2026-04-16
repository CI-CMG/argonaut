package edu.colorado.cires.argonaut.metadata.core;

import edu.colorado.cires.argonaut.messaging.core.databind.FloatMergeGroup;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class DefaultFloatMergeGroupPage implements FloatMergeGroupPage {

  public static Builder builder() {
    return new Builder();
  }

  public static Builder builder(FloatMergeGroupPage src) {
    return new Builder(src);
  }


  public static class Builder {

    private IndexPageRequest pageRequest;
    private long totalRecords;
    private List<FloatMergeGroup> page = Collections.emptyList();

    private Builder() {

    }

    private Builder(FloatMergeGroupPage src) {
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

    public Builder withPage(List<FloatMergeGroup> page) {
      if (page == null) {
        this.page = Collections.emptyList();
      } else {
        this.page = Collections.unmodifiableList(new ArrayList<>(page));
      }
      return this;
    }

    public DefaultFloatMergeGroupPage build() {
      return new DefaultFloatMergeGroupPage(pageRequest, totalRecords, page);
    }
  }

  private final IndexPageRequest pageRequest;
  private final int totalPages;
  private final long totalRecords;
  private final List<FloatMergeGroup> page;

  private DefaultFloatMergeGroupPage(IndexPageRequest pageRequest, long totalRecords, List<FloatMergeGroup> page) {
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
  public int getTotalPages() {
    return totalPages;
  }

  @Override
  public long getTotalRecords() {
    return totalRecords;
  }

  @Override
  public List<FloatMergeGroup> getPage() {
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


}
