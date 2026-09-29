package edu.colorado.cires.argonaut.metadata.jpa.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "profile_parameter")
public class ProfileParameterEntity {

  @Id
  @Column(name = "id")
  private UUID id;

  @Column(name = "parameter_index", nullable = false)
  private int parameterIndex;

  @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.REFRESH})
  @JoinColumn(name = "profile")
  private ProfileFileEntity profile;

  @Column(name = "parameter_name", nullable = false, length = 100)
  private String parameterName;


  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public ProfileFileEntity getProfile() {
    return profile;
  }

  public void setProfile(ProfileFileEntity profile) {
    this.profile = profile;
  }

  public String getParameterName() {
    return parameterName;
  }

  public void setParameterName(String parameterName) {
    this.parameterName = parameterName;
  }

  public int getParameterIndex() {
    return parameterIndex;
  }

  public void setParameterIndex(int parameterIndex) {
    this.parameterIndex = parameterIndex;
  }
}
