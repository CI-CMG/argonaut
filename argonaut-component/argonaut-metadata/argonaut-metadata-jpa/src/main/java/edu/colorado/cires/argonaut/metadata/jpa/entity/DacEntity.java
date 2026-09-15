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
@Table(name = "dac")
public class DacEntity {

  @Id
  @Column(name = "dac", nullable = false, length = 10)
  private String dac;

  @Version
  @Column(name = "version", nullable = false)
  private int version;

  // No accessors on purpose. For JPQL queries.
  @OneToMany(mappedBy = "dac", cascade = {CascadeType.DETACH, CascadeType.REFRESH})
  private List<FloatEntity> floats = new ArrayList<>();

  public String getDac() {
    return dac;
  }

  public int getVersion() {
    return version;
  }

  public void setDac(String dac) {
    this.dac = dac;
  }
}
