package edu.colorado.cires.argonaut.metadata.jpa.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "argo_float", indexes = {
    @Index(name = "argo_float_float_id_idx", columnList = "float_id"),
    @Index(name = "argo_float_dac_idx", columnList = "argo_dac")
})
public class ArgoFloatEntity {

  @Id
  @Column(name = "id", nullable = false, length = 20)
  private String id;

  @Version
  @Column(name = "version", nullable = false)
  private int version;

  @Column(name = "float_id", nullable = false, length = 10)
  private String floatId;

  @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.REFRESH})
  @JoinColumn(name = "argo_dac", nullable = false)
  private ArgoDacEntity argoDac;

  @OneToMany(mappedBy = "argoFloat", cascade = {CascadeType.DETACH, CascadeType.REFRESH})
  private List<ArgoFloatCycleEntity> cycles = new ArrayList<>();

  @OneToOne(mappedBy = "argoFloat")
  private ArgoFloatMetadataEntity metadata;

  @OneToOne(mappedBy = "argoFloat")
  private ArgonautProfileMergeFileEntity profileMerge;

  public String getId() {
    return id;
  }

  public int getVersion() {
    return version;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getFloatId() {
    return floatId;
  }

  public void setFloatId(String floatId) {
    this.floatId = floatId;
  }

  public ArgoDacEntity getArgoDac() {
    return argoDac;
  }

  public void setArgoDac(ArgoDacEntity dac) {
    this.argoDac = dac;
  }

  public ArgoFloatMetadataEntity getMetadata() {
    return metadata;
  }

  public void setMetadata(ArgoFloatMetadataEntity metadata) {
    this.metadata = metadata;
  }

  public List<ArgoFloatCycleEntity> getCycles() {
    return cycles;
  }

  public ArgonautProfileMergeFileEntity getProfileMerge() {
    return profileMerge;
  }

  public void setProfileMerge(ArgonautProfileMergeFileEntity profileMerge) {
    this.profileMerge = profileMerge;
  }
}
