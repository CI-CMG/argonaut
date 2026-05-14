package edu.colorado.cires.argonaut.metadata.jpa.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "dac")
public class DacEntity {

  @Id
  @Column(name = "dac", nullable = false, length = 10)
  private String dac;

  // No accessors on purpose. For JPQL queries.
  @OneToMany(mappedBy = "dac", cascade = {CascadeType.DETACH, CascadeType.REFRESH})
  private List<FloatEntity> floats = new ArrayList<>();

  public String getDac() {
    return dac;
  }

  public void setDac(String dac) {
    this.dac = dac;
  }
}
