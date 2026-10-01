package edu.colorado.cires.argonaut.messaging.core.databind;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import tools.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = SubmissionReportSet.Builder.class)
public class SubmissionReportSet {

  public static Builder builder() {
    return new Builder();
  }

  public static Builder builder(SubmissionReportSet source) {
    return new Builder(source);
  }

  private final String dac;
  private final List<AuditMessage> events;
  private final String report;
  private final Map<String, Object> otherFields;

  private SubmissionReportSet(String dac, List<AuditMessage> events, String report, Map<String, Object> otherFields) {
    this.dac = dac;
    this.events = events;
    this.report = report;
    this.otherFields = otherFields;
  }

  public String getDac() {
    return dac;
  }

  public List<AuditMessage> getEvents() {
    return events;
  }

  public String getReport() {
    return report;
  }

  @Deprecated
  @JsonAnyGetter
  public Map<String, Object> getOtherFields() {
    return otherFields;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SubmissionReportSet that = (SubmissionReportSet) o;
    return Objects.equals(dac, that.dac) && Objects.equals(events, that.events) && Objects.equals(report, that.report)
        && Objects.equals(otherFields, that.otherFields);
  }

  @Override
  public int hashCode() {
    return Objects.hash(dac, events, report, otherFields);
  }

  @Override
  public String toString() {
    return "SubmissionReportSet{" +
        "dac='" + dac + '\'' +
        ", events=" + events +
        ", report='" + report + '\'' +
        ", otherFields=" + otherFields +
        '}';
  }

  public static class Builder {

    private String dac;
    private List<AuditMessage> events = Collections.emptyList();
    private String report;
    private final Map<String, Object> otherFields = new HashMap<>();

    private Builder() {
    }

    private Builder(SubmissionReportSet source) {
      dac = source.dac;
      events = source.events;
      report = source.report;
      otherFields.putAll(source.otherFields);
    }

    public Builder withDac(String dac) {
      this.dac = dac;
      return this;
    }

    public Builder withReport(String report) {
      this.report = report;
      return this;
    }

    public Builder withEvents(List<AuditMessage> events) {
      if (events == null) {
        this.events = Collections.emptyList();
      } else {
        this.events = Collections.unmodifiableList(new ArrayList<>(events));
      }
      return this;
    }

    @Deprecated
    @JsonAnySetter
    private Builder withOtherField(String name, Object value) {
      this.otherFields.put(name, value);
      return this;
    }

    public SubmissionReportSet build() {
      return new SubmissionReportSet(dac, events, report, otherFields);
    }
  }
}
