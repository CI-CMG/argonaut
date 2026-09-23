package edu.colorado.cires.argonaut.metadata.core;

import edu.colorado.cires.argonaut.messaging.core.databind.ProfileMode;
import java.time.Instant;
import java.util.Objects;

public class DefaultRecentProfileSearch implements RecentProfileSearch {

  public static Builder builder() {
    return new Builder();
  }

  public static Builder builder(RecentProfileSearch src) {
    return new Builder(src);
  }


  public static class Builder {

    private ProfileMode profileMode;
    private Instant lastUpdatedDateGe;
    private Instant lastUpdatedDateLt;
    private int limit = 10000;

    private Builder() {

    }

    private Builder(RecentProfileSearch src) {
      withProfileMode(src.getProfileMode());
      withLastUpdatedDateGe(src.getLastUpdatedDateGe());
      withLastUpdatedDateLt(src.getLastUpdatedDateLt());
      withLimit(src.getLimit());
    }

    public Builder withLimit(int limit) {
      this.limit = limit;
      return this;
    }

    public Builder withProfileMode(ProfileMode profileMode) {
      this.profileMode = profileMode;
      return this;
    }

    public Builder withLastUpdatedDateGe(Instant lastUpdatedDateGe) {
      this.lastUpdatedDateGe = lastUpdatedDateGe;
      return this;
    }

    public Builder withLastUpdatedDateLt(Instant lastUpdatedDateLt) {
      this.lastUpdatedDateLt = lastUpdatedDateLt;
      return this;
    }

    public DefaultRecentProfileSearch build() {
      return new DefaultRecentProfileSearch(profileMode, lastUpdatedDateGe, lastUpdatedDateLt, limit);
    }

  }

  private final ProfileMode profileMode;
  private final Instant lastUpdatedDateGe;
  private final Instant lastUpdatedDateLt;
  private final int limit;

  private DefaultRecentProfileSearch(ProfileMode profileMode, Instant lastUpdatedDateGe, Instant lastUpdatedDateLt, int limit) {
    this.profileMode = profileMode;
    this.lastUpdatedDateGe = lastUpdatedDateGe;
    this.lastUpdatedDateLt = lastUpdatedDateLt;
    this.limit = limit;
  }


  @Override
  public ProfileMode getProfileMode() {
    return profileMode;
  }

  @Override
  public Instant getLastUpdatedDateGe() {
    return lastUpdatedDateGe;
  }

  @Override
  public Instant getLastUpdatedDateLt() {
    return lastUpdatedDateLt;
  }

  @Override
  public int getLimit() {
    return limit;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DefaultRecentProfileSearch that = (DefaultRecentProfileSearch) o;
    return limit == that.limit && profileMode == that.profileMode && Objects.equals(lastUpdatedDateGe, that.lastUpdatedDateGe)
        && Objects.equals(lastUpdatedDateLt, that.lastUpdatedDateLt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(profileMode, lastUpdatedDateGe, lastUpdatedDateLt, limit);
  }

  @Override
  public String toString() {
    return "DefaultRecentProfileSearch{" +
        "profileMode=" + profileMode +
        ", lastUpdatedDateGe=" + lastUpdatedDateGe +
        ", lastUpdatedDateLt=" + lastUpdatedDateLt +
        ", limit=" + limit +
        '}';
  }

}
