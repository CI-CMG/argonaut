package edu.colorado.cires.argonaut.metadata.core;

import edu.colorado.cires.argonaut.messaging.core.databind.ProfileOperation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class DefaultProfilePage implements ProfilePage {

  private final IndexPageRequest pageRequest;
  private final int totalPages;
  private final long totalRecords;
  private final List<ProfileOperation> page;

  private DefaultProfilePage(IndexPageRequest pageRequest, long totalRecords, List<ProfileOperation> page) {
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
  public List<ProfileOperation> getPage() {
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


  public static Builder builder() {
    return new Builder();
  }

  public static Builder builder(DefaultProfilePage src) {
    return new Builder(src);
  }


  public static class Builder {

    private IndexPageRequest pageRequest;
    private long totalRecords;
    private List<ProfileOperation> page = Collections.emptyList();

    private Builder() {

    }

    private Builder(DefaultProfilePage src) {
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

    public Builder withPage(List<ProfileOperation> page) {
      if (page == null) {
        this.page = Collections.emptyList();
      } else {
        this.page = Collections.unmodifiableList(new ArrayList<>(page));
      }
      return this;
    }

    public DefaultProfilePage build() {
      return new DefaultProfilePage(pageRequest, totalRecords, page);
    }
  }
}
