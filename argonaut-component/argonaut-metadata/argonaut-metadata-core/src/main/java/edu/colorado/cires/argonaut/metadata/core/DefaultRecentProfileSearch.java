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

    private int pageNumber = 1;
    private int pageSize = 200;
    private ProfileMode profileMode;
    private Instant youngerOrEqual;

    private Builder() {

    }

    private Builder(RecentProfileSearch src) {
      withPageNumber(src.getPageNumber());
      withPageSize(src.getPageSize());
      withProfileMode(src.getProfileMode());
      withYoungerOrEqual(src.getYoungerOrEqual());
    }

    public Builder withPageNumber(int pageNumber) {
      this.pageNumber = pageNumber;
      return this;
    }

    public Builder withPageSize(int pageSize) {
      this.pageSize = pageSize;
      return this;
    }

    public Builder withProfileMode(ProfileMode profileMode) {
      this.profileMode = profileMode;
      return this;
    }

    public Builder withYoungerOrEqual(Instant youngerOrEqual) {
      this.youngerOrEqual = youngerOrEqual;
      return this;
    }

    public DefaultRecentProfileSearch build() {
      return new DefaultRecentProfileSearch(pageNumber, pageSize, profileMode, youngerOrEqual);
    }

  }

  private final int pageNumber;
  private final int pageSize;
  private final ProfileMode profileMode;
  private final Instant youngerOrEqual;

  private DefaultRecentProfileSearch(int pageNumber, int pageSize, ProfileMode profileMode, Instant youngerOrEqual) {
    this.pageNumber = pageNumber;
    this.pageSize = pageSize;
    this.profileMode = profileMode;
    this.youngerOrEqual = youngerOrEqual;
  }


  @Override
  public ProfileMode getProfileMode() {
    return profileMode;
  }

  @Override
  public Instant getYoungerOrEqual() {
    return youngerOrEqual;
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
    DefaultRecentProfileSearch that = (DefaultRecentProfileSearch) o;
    return pageNumber == that.pageNumber && pageSize == that.pageSize && profileMode == that.profileMode && Objects.equals(youngerOrEqual,
        that.youngerOrEqual);
  }

  @Override
  public int hashCode() {
    return Objects.hash(pageNumber, pageSize, profileMode, youngerOrEqual);
  }

  @Override
  public String toString() {
    return "DefaultRecentProfileSearch{" +
        "pageNumber=" + pageNumber +
        ", pageSize=" + pageSize +
        ", profileMode=" + profileMode +
        ", youngerOrEqual=" + youngerOrEqual +
        '}';
  }

}
