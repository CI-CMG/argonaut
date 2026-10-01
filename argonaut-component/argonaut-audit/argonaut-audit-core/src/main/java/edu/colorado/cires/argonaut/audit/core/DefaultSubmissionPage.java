package edu.colorado.cires.argonaut.audit.core;

import edu.colorado.cires.argonaut.messaging.core.databind.AuditMessage;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class DefaultSubmissionPage implements SubmissionPage {

  public static Builder builder() {
    return new Builder();
  }

  public static Builder builder(SubmissionPage source) {
    return new Builder(source);
  }

  private final int totalPages;
  private final long totalRecords;
  private final SubmissionReportSearch submissionReportSearch;
  private final List<AuditMessage> page;

  private DefaultSubmissionPage(long totalRecords, SubmissionReportSearch submissionReportSearch, List<AuditMessage> page) {
    this.totalRecords = totalRecords;
    this.submissionReportSearch = submissionReportSearch;
    this.page = page;
    totalPages = (int) Math.ceil((double) totalRecords / (double) submissionReportSearch.getPageSize());
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
  public List<AuditMessage> getPage() {
    return page;
  }

  @Override
  public Optional<SubmissionReportSearch> getNextPage() {
    int nextPageNumber = submissionReportSearch.getPageNumber() + 1;
    if (nextPageNumber > totalPages) {
      return Optional.empty();
    }
    return Optional.of(builder(this)
        .withPage(Collections.emptyList())
        .withSubmissionReportSearch(DefaultSubmissionReportSearch.builder(this).withPageNumber(nextPageNumber).build())
        .build());
  }

  @Override
  public String getDac() {
    return submissionReportSearch.getDac();
  }

  @Override
  public Instant getTimestampLt() {
    return submissionReportSearch.getTimestampLt();
  }

  @Override
  public int getPageNumber() {
    return submissionReportSearch.getPageNumber();
  }

  @Override
  public int getPageSize() {
    return submissionReportSearch.getPageSize();
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DefaultSubmissionPage that = (DefaultSubmissionPage) o;
    return totalPages == that.totalPages && totalRecords == that.totalRecords && Objects.equals(submissionReportSearch,
        that.submissionReportSearch) && Objects.equals(page, that.page);
  }

  @Override
  public int hashCode() {
    return Objects.hash(totalPages, totalRecords, submissionReportSearch, page);
  }

  @Override
  public String toString() {
    return "DefaultSubmissionPage{" +
        "totalPages=" + totalPages +
        ", totalRecords=" + totalRecords +
        ", submissionReportSearch=" + submissionReportSearch +
        ", page=" + page +
        '}';
  }

  public static class Builder {

    private long totalRecords;
    private SubmissionReportSearch submissionReportSearch;
    private List<AuditMessage> page = Collections.emptyList();

    private Builder() {

    }

    private Builder(SubmissionPage source) {
      totalRecords = source.getTotalRecords();
      page = source.getPage();
      submissionReportSearch = DefaultSubmissionReportSearch.builder(source).build();
    }

    public Builder withTotalRecords(long totalRecords) {
      this.totalRecords = totalRecords;
      return this;
    }

    public Builder withSubmissionReportSearch(SubmissionReportSearch submissionReportSearch) {
      this.submissionReportSearch = submissionReportSearch;
      return this;
    }

    public Builder withPage(List<AuditMessage> page) {
      if (page == null) {
        this.page = Collections.emptyList();
      } else {
        this.page = Collections.unmodifiableList(new ArrayList<>(page));
      }
      return this;
    }

    public DefaultSubmissionPage build() {
      return new DefaultSubmissionPage(totalRecords, submissionReportSearch, page);
    }
  }
}
