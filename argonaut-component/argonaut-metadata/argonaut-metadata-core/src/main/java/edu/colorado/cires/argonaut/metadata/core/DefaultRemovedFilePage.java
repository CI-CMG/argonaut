package edu.colorado.cires.argonaut.metadata.core;

import edu.colorado.cires.argonaut.messaging.core.databind.ArgoFileType;
import edu.colorado.cires.argonaut.messaging.core.databind.ProfileOperation;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class DefaultRemovedFilePage implements RemovedFilePage {

  private final RemovedFileSearch pageRequest;
  private final int totalPages;
  private final long totalRecords;
  private final List<ProfileOperation> page;

  private DefaultRemovedFilePage(RemovedFileSearch pageRequest, long totalRecords, List<ProfileOperation> page) {
    this.pageRequest = pageRequest;
    this.totalRecords = totalRecords;
    this.page = page;
    totalPages = (int) Math.ceil((double) totalRecords / (double) pageRequest.getPageSize());
  }


  @Override
  public Instant getOlderThan() {
    return pageRequest.getOlderThan();
  }

  @Override
  public List<ArgoFileType> getFileTypes() {
    return pageRequest.getFileTypes();
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
  public Optional<RemovedFileSearch> getNextPage() {
    int nextPageNumber = pageRequest.getPageNumber() + 1;
    if (nextPageNumber > totalPages) {
      return Optional.empty();
    }
    return Optional.of(builder(this)
        .withPage(Collections.emptyList())
        .withIndexPageRequest(DefaultRemovedFileSearch.builder(this).withPageNumber(nextPageNumber).build())
        .build());
  }


  public static Builder builder() {
    return new Builder();
  }

  public static Builder builder(DefaultRemovedFilePage src) {
    return new Builder(src);
  }

  public static class Builder {

    private RemovedFileSearch pageRequest;
    private long totalRecords;
    private List<ProfileOperation> page = Collections.emptyList();

    private Builder() {

    }

    private Builder(DefaultRemovedFilePage src) {
      withIndexPageRequest(DefaultRemovedFileSearch.builder(src).build());
      withTotalRecords(src.getTotalRecords());
      withPage(src.getPage());
    }

    public Builder withIndexPageRequest(RemovedFileSearch pageRequest) {
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

    public DefaultRemovedFilePage build() {
      return new DefaultRemovedFilePage(pageRequest, totalRecords, page);
    }
  }
}
