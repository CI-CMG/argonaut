package edu.colorado.cires.argonaut.metadata.jpa.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "argo_float_profile_parameter", indexes = {
    @Index(name = "argo_float_profile_parameter_profile_idx", columnList = "argo_float_profile")
})
public class ArgoFloatProfileParameterEntity {

  @Id
  @Column(name = "id", nullable = false, length = 36)
  private String id;

  @Column(name = "parameter_index", nullable = false)
  private int parameterIndex;

  @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.REFRESH})
  @JoinColumn(name = "argo_float_profile")
  private ArgoFloatProfileEntity argoFloatProfile;

  @Column(name = "parameter_name", nullable = false, length = 100)
  private String parameterName;


  public UUID getId() {
    return id == null ? null : UUID.fromString(id);
  }

  public void setId(UUID id) {
    this.id = id == null ? null : id.toString();
  }

  public ArgoFloatProfileEntity getArgoFloatProfile() {
    return argoFloatProfile;
  }

  public void setArgoFloatProfile(ArgoFloatProfileEntity profile) {
    this.argoFloatProfile = profile;
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
