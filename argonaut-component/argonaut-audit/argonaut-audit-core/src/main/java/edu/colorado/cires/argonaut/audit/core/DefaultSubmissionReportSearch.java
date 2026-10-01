package edu.colorado.cires.argonaut.audit.core;

import java.time.Instant;
import java.util.Objects;

public class DefaultSubmissionReportSearch implements SubmissionReportSearch {

  public static Builder builder() {
    return new Builder();
  }

  public static Builder builder(SubmissionReportSearch search) {
    return new Builder(search);
  }

  private final String dac;
  private final Instant timestampLt;
  private final int pageNumber;
  private final int pageSize;

  private DefaultSubmissionReportSearch(String dac, Instant timestampLt, int pageNumber, int pageSize) {
    this.dac = dac;
    this.timestampLt = timestampLt;
    this.pageNumber = pageNumber;
    this.pageSize = pageSize;
  }

  @Override
  public String getDac() {
    return dac;
  }

  @Override
  public Instant getTimestampLt() {
    return timestampLt;
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
    DefaultSubmissionReportSearch that = (DefaultSubmissionReportSearch) o;
    return pageNumber == that.pageNumber && pageSize == that.pageSize && Objects.equals(dac, that.dac) && Objects.equals(
        timestampLt, that.timestampLt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(dac, timestampLt, pageNumber, pageSize);
  }

  @Override
  public String toString() {
    return "DefaultSubmissionReportSearch{" +
        "dac='" + dac + '\'' +
        ", timestampLt=" + timestampLt +
        ", pageNumber=" + pageNumber +
        ", pageSize=" + pageSize +
        '}';
  }

  public static class Builder {
    private String dac;
    private Instant timestampLt;
    private int pageNumber = 1;
    private int pageSize = 200;

    private Builder() {

    }

    private Builder(SubmissionReportSearch source) {
      dac = source.getDac();
      timestampLt = source.getTimestampLt();
      pageNumber = source.getPageNumber();
      pageSize = source.getPageSize();
    }

    public Builder withDac(String dac) {
      this.dac = dac;
      return this;
    }

    public Builder withTimestampLt(Instant timestampLt) {
      this.timestampLt = timestampLt;
      return this;
    }

    public Builder withPageNumber(int pageNumber) {
      this.pageNumber = pageNumber;
      return this;
    }

    public Builder withPageSize(int pageSize) {
      this.pageSize = pageSize;
      return this;
    }

    public DefaultSubmissionReportSearch build() {
      return new DefaultSubmissionReportSearch(dac, timestampLt, pageNumber, pageSize);
    }
  }
}
