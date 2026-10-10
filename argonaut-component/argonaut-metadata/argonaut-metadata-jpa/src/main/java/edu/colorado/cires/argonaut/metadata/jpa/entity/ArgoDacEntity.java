package edu.colorado.cires.argonaut.metadata.jpa.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "argo_dac")
public class ArgoDacEntity {

  @Id
  @Column(name = "dac_name", nullable = false, length = 10)
  private String dacName;

  @Version
  @Column(name = "version", nullable = false)
  private int version;

  // No accessors on purpose. For JPQL queries.
  @OneToMany(mappedBy = "argoDac", cascade = {CascadeType.DETACH, CascadeType.REFRESH})
  private List<ArgoFloatEntity> argoFloats = new ArrayList<>();

  public String getDacName() {
    return dacName;
  }

  public int getVersion() {
    return version;
  }

  public void setDacName(String dac) {
    this.dacName = dac;
  }
}
