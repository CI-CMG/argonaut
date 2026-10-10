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
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "argo_float_cycle", indexes = {
    @Index(name = "argo_float_cycle_float_id_idx", columnList = "argo_float")
})
public class ArgoFloatCycleEntity {

  @Id
  @Column(name = "id", nullable = false, length = 26)
  private String id;

  @Version
  @Column(name = "version", nullable = false)
  private int version;

  @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.REFRESH})
  @JoinColumn(name = "argo_float", nullable = false)
  private ArgoFloatEntity argoFloat;

  @Column(name = "data_mode", length = 1, nullable = false)
  private Character dataMode;

  @Column(name = "direction", length = 1, nullable = false)
  private Character direction;

  @Column(name = "cycle_number", length = 4, nullable = false)
  private String cycleNumber;

  @OneToMany(mappedBy = "argoFloatCycle", cascade = {CascadeType.DETACH, CascadeType.REFRESH})
  private List<ArgoFloatProfileEntity> profiles = new ArrayList<>();

  public String getId() {
    return id;
  }

  public int getVersion() {
    return version;
  }

  public void setId(String id) {
    this.id = id;
  }

  public ArgoFloatEntity getArgoFloat() {
    return argoFloat;
  }

  public void setArgoFloat(ArgoFloatEntity floatId) {
    this.argoFloat = floatId;
  }

  public Character getDataMode() {
    return dataMode;
  }

  public void setDataMode(Character dataMode) {
    this.dataMode = dataMode;
  }

  public Character getDirection() {
    return direction;
  }

  public void setDirection(Character direction) {
    this.direction = direction;
  }

  public String getCycleNumber() {
    return cycleNumber;
  }

  public void setCycleNumber(String cycleNumber) {
    this.cycleNumber = cycleNumber;
  }

  public List<ArgoFloatProfileEntity> getProfiles() {
    return profiles;
  }

}
