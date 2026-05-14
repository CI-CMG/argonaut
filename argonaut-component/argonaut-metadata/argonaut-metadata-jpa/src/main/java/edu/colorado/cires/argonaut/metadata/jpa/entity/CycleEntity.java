package edu.colorado.cires.argonaut.metadata.jpa.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cycle")
public class CycleEntity {

  @Id
  @Column(name = "id", nullable = false, length = 26)
  private String id;

  @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.REFRESH})
  @JoinColumn(name = "float_id", nullable = false)
  private FloatEntity floatId;

  @Column(name = "data_mode", length = 1, nullable = false)
  private Character dataMode;

  @Column(name = "direction", length = 1, nullable = false)
  private Character direction;

  @Column(name = "cycle_number", length = 4, nullable = false)
  private String cycleNumber;

  @OneToMany(mappedBy = "cycle", cascade = {CascadeType.DETACH, CascadeType.REFRESH})
  private List<ProfileFileEntity> profiles = new ArrayList<>();

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public FloatEntity getFloatId() {
    return floatId;
  }

  public void setFloatId(FloatEntity floatId) {
    this.floatId = floatId;
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

  public List<ProfileFileEntity> getProfiles() {
    return profiles;
  }
}
